package com.qingbo.ginkgo.ygb.account.payment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class HttpClientUtil
{
	private static final int SO_TIMEOUT = 30000;
	private static final int CONNECTION_TIMEOUT = 10000;
	
	private static RequestConfig requestConfig;
	private static CloseableHttpClient httpclient;
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	
	static {
		try {
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(200);
			cm.setDefaultMaxPerRoute(20);
			
			httpclient = HttpClients.custom().setConnectionManager(cm).build();
			requestConfig = RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).setConnectTimeout(CONNECTION_TIMEOUT).build();
			
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					logger.info("HttpClientUtil shutdown");
					try {
						if (httpclient != null) {
							httpclient.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}));
		} catch (Exception e) {
			logger.error("HttpClientUtil initialize error", e);
		}
	}
	
	/**
	 * 乾多多跳转接口，自动附加SignInfo参数
	 */
	public static String redirect(String strURL, Map<String, String> req) {
		StringBuilder sb = new StringBuilder();
		sb.append(strURL);
		if (req != null && req.size()>0) {
			sb.append('?');
			for (String key : req.keySet()) {
				String value = req.get(key);
				if(value!=null) sb.append(key).append("=").append(value).append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		logger.info(sb.toString());
		return sb.toString();
	}
	
	/**
	 * 提交数据，返回状态码和响应字符串
	 */
	public static String[] postData(String strURL, Map<String, String> req)
	{
		String[] resultarr = new String[2];
		HttpPost post = null;
		
		try {
			post = new HttpPost(strURL);
			post.setConfig(requestConfig);
			
			if (req != null) {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (Entry<String, String> entry : req.entrySet()) {
					if (entry.getValue() == null) {
						nvps.add(new BasicNameValuePair(entry.getKey(), null));
					} else {
						nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
					}
				}
				post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			}
			logger.info("post: "+strURL);
			logger.info("params: "+JSON.toJSONString(req));
			
			HttpResponse response = httpclient.execute(post);
			
			resultarr[0] = String.valueOf(response.getStatusLine().getStatusCode());
			resultarr[1] = EntityUtils.toString(response.getEntity());
			
			logger.info(resultarr[0]);
			logger.info(resultarr[1]);
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
		} catch (RuntimeException e) {
			if (post != null) {
				post.abort();
			}
			logger.warn(e.getMessage(), e);
		} finally {
			if (post != null) {
				post.releaseConnection();
			}
		}
		
		return resultarr;
	}
}
