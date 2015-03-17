package com.qingbo.ginkgo.ygb.common.util;

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

import com.alibaba.fastjson.JSONObject;


public class HttpClientUtilCommon {
	
	private static final int SO_TIMEOUT = 30000;
	private static final int CONNECTION_TIMEOUT = 10000;
	
	private static RequestConfig requestConfig;
	private static CloseableHttpClient httpclient;
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtilCommon.class);
	
	static {
		try {
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(200);
			cm.setDefaultMaxPerRoute(20);
			
			httpclient = HttpClients.custom().setConnectionManager(cm).build();
			requestConfig = RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).setConnectTimeout(CONNECTION_TIMEOUT).build();
			
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					logger.info("HttpClientUtilCommon has shutdown");
					try {
						if (httpclient != null) {
							httpclient.close();
						}
					} catch (IOException e) {
						logger.info("HttpClientUtilCommon ioException by:", e.getMessage());
					}
				}
			}));
		} catch (Exception e) {
			logger.info("HttpClientUtilCommon initialize error by:", e.getMessage());
		}
	}
	
	
	public static JSONObject post(String url,Map<String, String> req){
		logger.info("HttpClientUtilCommon post Input Url:"+url+" Req is Null:"+(req!=null));
		HttpPost post = null;
		try {
			post = new HttpPost(url);
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
			HttpResponse response = httpclient.execute(post);
			String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
			String entity = EntityUtils.toString(response.getEntity());
			logger.info("HttpClientUtilCommon post Response StatusCode:"+statusCode+" Entity:"+entity);
			JSONObject json = new JSONObject();
			json.put("StatusCode", statusCode);
			json.put("Entity", entity);
			return json;
		} catch (IOException e) {
			logger.info("HttpClientUtilCommon IOException By:"+e.getMessage());
		} catch (RuntimeException e) {
			logger.info("HttpClientUtilCommon RuntimeException By:"+e.getMessage());
			if (post != null) {
				post.abort();
			}
		} finally {
			if (post != null) {
				post.releaseConnection();
			}
		}
		return new JSONObject();
	}
	
}
