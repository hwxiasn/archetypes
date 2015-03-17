package com.qingbo.ginkgo.ygb.project.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;
import com.qingbo.ginkgo.ygb.common.util.PropertiesUtil;
import com.qingbo.ginkgo.ygb.common.util.UploadUtil;


/**
 * 通过HTTP接口提交WORD操作及WORD转PDF的操作，并通过HTTP接口下载PDF文档到本地，并删除远端文档
 * 文件名为数字与字母的组合，无需编码
 * @author Administrator
 *
 */
public class GenerateFile {
	
	public static final String VOUCHER = "VOUCHER";
	public static final String CONTRACT = "CONTRACT";
	public static final String LETTER = "LETTER";
	
	private static final int SO_TIMEOUT = 30000;
	private static final int CONNECTION_TIMEOUT = 10000;
	
	private static RequestConfig requestConfig;
	private static CloseableHttpClient httpclient;
	private static Logger logger = LoggerFactory.getLogger(GenerateFile.class);
	private static final String key= PropertiesUtil.get(ContractNoBuilder.KEY_FILE).getProperty("signinfo");
	private static String FINAL_FILE_PATH = PropertiesUtil.get(ContractNoBuilder.KEY_FILE).getProperty("final_file_path");
	private static final String URL_ROOT = "http://121.42.10.117:8080/hetong/";
	
	
	static {
		try {
			PoolingHttpClientConnectionManager phccm = new PoolingHttpClientConnectionManager();
			phccm.setMaxTotal(50);
			phccm.setDefaultMaxPerRoute(5);
			
			httpclient = HttpClients.custom().setConnectionManager(phccm).build();
			requestConfig = RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).setConnectTimeout(CONNECTION_TIMEOUT).build();
			
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					logger.info("合同文本生成通道关闭");
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
			logger.error("合同文本生成通道初始化失败", e);
		}
	}
	
	public static String generateProjectFile(String type ,String contractName,List<Object> contract,String letterName,List<Object> letter){
		if(type == null || contractName == null || contract == null || letterName == null || letter  == null){
			return null;
		}
		try {
			if (sendByHttp(type,contractName, contract, letterName, letter)) {
				downloadByHttp(contractName);
				downloadByHttp(letterName);
				deleteByHttp(contractName);
				deleteByHttp(letterName);
				return "+OK";
			}
		}catch(Exception e){
			logger.error("生成合同文本失败",e);
		}
		return "-ERR";
	}
	
	
	/**
	 * 根据担保类型以及模板类型名称生成单个DOC文档
	 * @param type
	 * 担保类型
	 * @param tempTypeName
	 * 模板类型名称
	 * @param contractName
	 * 文件名
	 * @param contract
	 * 文件信息
	 * @return
	 */
	public static String singleDoc(String type,String tempTypeName,String contractName,List<Object> contract){
		if(type == null || contractName == null || contract == null || tempTypeName == null ){
			return null;
		}
		logger.info("GenerateFile SingleDoc Input Param: type:"+type+" TempTypeName:"+tempTypeName+" ContractName:"+contractName+" ListSize:"+(contract==null?0:contract.size()));
		
		try{
			File directory = new File("");//设定为当前文件夹
		    String path = directory.getAbsolutePath();//获取绝对路径
		    path = path + "/final/";
		    File newDir = new File(path);
		    if(!newDir.exists()){
		    	boolean flag = newDir.mkdir();
		    	logger.info("SingleDoc Build Final File Path:"+path+" Result:"+flag);
		    }
		    FINAL_FILE_PATH = newDir.getAbsolutePath()+"/"; 
		}catch(Exception e){
			logger.info("Get Current Abs Path Error.By:"+e.getMessage());
		}

		try {
			if (sendByHttp(type,tempTypeName,contractName, contract)) {
				downloadByHttp(contractName);
				deleteByHttp(contractName);
				uploadByHttp(contractName);
				return "+OK";
			}
		}catch(Exception e){
			logger.info("GenerateFile SingleDoc Error.By:"+e.getMessage());
//			logger.error("生成合同文本失败",e);
		}
		return "-ERR";
	}
	
	/**
	 * 发送HTTP请求生成单个文档
	 * @param type
	 * 担保类型
	 * @param tempTypeName
	 * 合同模板类型CONTRACT LETTER VOUCHER
	 * @param contractName
	 * 合同文件名
	 * @param contract
	 * 合同内容
	 * @return
	 */
	private static final String SINGLE_URL_HTTP = URL_ROOT+"single.jsp?signinfo="+key;
	private static boolean sendByHttp(String type,String tempTypeName,String contractName,List<Object> contract){
		String contractListEn = null;
		String contractList =JSON.toJSONString(contract);
		try{
			contractListEn = java.net.URLEncoder.encode(contractList, "UTF-8");
		}catch(Exception e){
			return false;
		}
		StringBuffer buf = new StringBuffer(SINGLE_URL_HTTP);
		buf.append("&type=").append(type);
		buf.append("&temp_type=").append(tempTypeName);
		buf.append("&file_name=").append(contractName);
		buf.append("&file_info=").append(contractListEn);

		System.out.println("GenerateFile sendByHttp info：\t"+buf.toString());
		
		HttpPost post = null;
		try {
	        post = new HttpPost(SINGLE_URL_HTTP);
			post.setConfig(requestConfig);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("type", type));
			nvps.add(new BasicNameValuePair("temp_type", tempTypeName));
			nvps.add(new BasicNameValuePair("file_name", contractName));
			nvps.add(new BasicNameValuePair("file_info", contractListEn));
			post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

			HttpResponse response = httpclient.execute(post);
			String status = String.valueOf(response.getStatusLine().getStatusCode());
			String entity = EntityUtils.toString(response.getEntity());
			System.out.println("GenerateFile sendByHttp result：\t"+status);
			return true;
		} catch (IOException e) {
			logger.trace("Post File Build Request", e);
		} catch (RuntimeException e) {
			try{
			if (post != null) {
				post.abort();
			}
			}catch(Exception ee){}
		} finally {
			try{
			if (post != null) {
				post.releaseConnection();
			}}catch(Exception ee){}
		}
		return false;
	}
	
	private static final String URL_HTTP = URL_ROOT+"file.jsp?signinfo="+key;
	private static boolean sendByHttp(String type,String contractName,List<Object> contract,String letterName,List<Object> letter){
		String contractListEn = null;
		String letterListEn = null;
		
		String contractList =JSON.toJSONString(contract);
		String letterList =JSON.toJSONString(letter);
		
		try{
			contractListEn = java.net.URLEncoder.encode(contractList, "UTF-8");
			letterListEn = java.net.URLEncoder.encode(letterList, "UTF-8");
		}catch(Exception e){
			return false;
		}
		StringBuffer buf = new StringBuffer(URL_HTTP);
		buf.append("&type=").append(type);
		buf.append("&contract=").append(contractName);
		buf.append("&contract_info=").append(contractListEn);
		buf.append("&letter=").append(letterName);
		buf.append("&letter_info=").append(letterListEn);
		
		System.out.println("合同信息：\t"+buf.toString());
		
		HttpPost post = null;
		try {
	         URL realUrl = new URL(buf.toString());
	            // 打开和URL之间的连接
	         URLConnection conn = realUrl.openConnection();
	            // 设置通用的请求属性
	            conn.setRequestProperty("accept", "*/*");
	            conn.setRequestProperty("connection", "Keep-Alive");
	            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            // 发送POST请求必须设置如下两行
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            // 获取URLConnection对象对应的输出流
	            PrintWriter out = new PrintWriter(conn.getOutputStream());
	            // 发送请求参数
	            out.print("");
	            // flush输出流的缓冲
	            out.flush();
	            // 定义BufferedReader输入流来读取URL的响应
	            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	            };
//	        post = new HttpPost(buf.toString());
//			post.setConfig(requestConfig);
//			HttpResponse response = httpclient.execute(post);
//			String status = String.valueOf(response.getStatusLine().getStatusCode());
//			String entity = EntityUtils.toString(response.getEntity());
			return true;
		} catch (IOException e) {
			logger.trace("Post File Build Request", e);
		} catch (RuntimeException e) {
			try{
			if (post != null) {
				post.abort();
			}
			}catch(Exception ee){}
		} finally {
			try{
			if (post != null) {
				post.releaseConnection();
			}}catch(Exception ee){}
		}
		return false;
	}
	
	private static final String DELETE_URL_HTTP = URL_ROOT+"del.jsp?signinfo="+key;
	public static boolean deleteByHttp(String fileName) {
		StringBuffer buf = new StringBuffer(DELETE_URL_HTTP);
		buf.append("&file=").append(fileName);
		System.out.println("GenerateFile deleteByHttp info：\t"+buf.toString());
		HttpPost post = null;
		try {
			post = new HttpPost(buf.toString());
			post.setConfig(requestConfig);
			HttpResponse response = httpclient.execute(post);
			String status = String.valueOf(response.getStatusLine().getStatusCode());
			String entity = EntityUtils.toString(response.getEntity());
			System.out.println("GenerateFile deleteByHttp result：\t"+status);
			return true;
		} catch (IOException e) {
			logger.trace("DELETE File Request", e);
		} catch (RuntimeException e) {
			try {
				if (post != null) {
					post.abort();
				}
			} catch (Exception ee) {
			}
		} finally {
			try {
				if (post != null) {
					post.releaseConnection();
				}
			} catch (Exception e) {
			}
		}
		return false;
	}
	
	private static final String DOWNLOAD_URL_HTTP = URL_ROOT+"download.jsp?signinfo="+key;
	private static boolean downloadByHttp(String fileName) {
		URL url = null;
		HttpURLConnection httpConn = null;
		InputStream in = null;
		BufferedInputStream bio = null;
		FileOutputStream fos = null;
		try {
			StringBuffer buf = new StringBuffer(DOWNLOAD_URL_HTTP);
			buf.append("&file=").append(fileName);
			System.out.println("GenerateFile downloadByHttp info：\t"+buf.toString());

			// 创建URL连接
			url = new URL(buf.toString());
			httpConn = (HttpURLConnection) url.openConnection();
//			httpConn.setConnectTimeout(300000);
//			httpConn.setReadTimeout(300000);
			HttpURLConnection.setFollowRedirects(true);
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setUseCaches(false);
			
			in = httpConn.getInputStream();
			bio = new BufferedInputStream(in);
			
			File outFile = new File(FINAL_FILE_PATH+fileName);
			System.out.println("GenerateFile downloadByHttp FilePath：\t"+outFile.getAbsolutePath());
			if(outFile.exists()){
				outFile.delete();
			}
			outFile.setWritable(false);
			fos = new FileOutputStream(outFile);
			byte[] bytes = new byte[2048];
			int size = 0;
			while((size = bio.read(bytes))!=-1){
				fos.write(bytes,0,size);
			}

			fos.flush();
			fos.close();
			System.out.println("GenerateFile downloadByHttp result：\t true" );
			return true;
		} catch (Exception e) {
			logger.trace("Download File Request", e);
			System.out.println("GenerateFile downloadByHttp error：\t "+e.getMessage() );
		} finally {
			try {
				if(bio != null){
					bio.close();
				}
				if(in != null){
					in.close();
				}
				httpConn.disconnect();
			} catch (Exception e) {
			}
			try {
				if(fos != null){
					fos.close();
				}
			} catch (Exception e) {
			}
		}
		return false;
	}
	
	private static boolean uploadByHttp(String fileName){
		try{
			File file = new File(FINAL_FILE_PATH+fileName);
			logger.info("GenerateFile for uploadByHttp file Path:"+file.getAbsolutePath()+" FileName:"+fileName);
			String name = "file";
			String originalFileName = file.getName();
			String contentType = "text/plain";
			byte[] content = FileCopyUtils.copyToByteArray(file);
			
			String type="contract";//合同目录/uploads/contract
			MockMultipartFile uploadFile = new MockMultipartFile(name, originalFileName, contentType, content);
			String upload = UploadUtil.contract(type, uploadFile);
			logger.info("GenerateFile for uploadByHttp Upload Return:"+upload+" FileName:"+fileName);
			if(upload == null || "".equals(upload)){
				logger.info("GenerateFile for uploadByHttp Upload Failed. FileName:"+fileName);
				return false;
			}
			
			String[] split = upload.split("[,]");
			logger.info("GenerateFile for uploadByHttp Split Return:"+Arrays.toString(split));
			if(split.length==2) {
				String path = split[1];
				logger.info("GenerateFile for uploadByHttp URL:"+GinkgoConfig.getProperty("uploads")+path);
			}
			return true;
		}catch(Exception e){
			logger.info("GenerateFile for uploadByHttp Error.By:"+e.getMessage());
		}
		return false;
	}
	
	public static void main(String[] args){
		try{
			File directory = new File("");//设定为当前文件夹
//		    System.out.println(directory.getCanonicalPath());//获取标准的路径
		    String path = directory.getAbsolutePath();//获取绝对路径
		    path = path + "/final/";
		    File newDir = new File(path);
	    	System.out.println("SingleDoc Build Final File Path:"+path);
		    if(!newDir.exists()){
		    	boolean flag = newDir.mkdir();
		    	System.out.println("SingleDoc Build Final File Path:"+path);
		    }
		    FINAL_FILE_PATH = newDir.getAbsolutePath(); 
		    System.out.println(FINAL_FILE_PATH);
		}catch(Exception e){
			System.out.println("Get Current Abs Path Error.By:"+e.getMessage());
		}

	}
}
