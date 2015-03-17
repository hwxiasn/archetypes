package com.qingbo.ginkgo.ygb.common.util;

import java.io.IOException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件工具类，将文件上传至远程服务器
 * @author hongwei
 * @date 2014-08-06
 */
public class UploadUtil {
	private static Logger logger = LoggerFactory.getLogger(UploadUtil.class);
	private static HttpClient httpClient = HttpClients.createDefault();
	private static ContentType textUtf8 = ContentType.create("text/plain", Consts.UTF_8);
	
	//上传相关
	private static String upload_direct = "upload_direct";
	private static String upload_contract = "upload_contract";
	private static String upload_temp = "upload_temp";
	private static String upload_confirm = "upload_confirm";
	private static String upload_trash = "upload_trash";
	
	//存储相关
//	private static String uploads_file = "uploads_file";
//	private static String uploads_temp_file = "uploads_temp_file";
//	private static String uploads_trash_file = "uploads_trash_file";
	
	//访问相关
//	private static String uploads = "uploads";
//	private static String uploads_temp = "uploads_temp";
	
	public static String upload(String type, MultipartFile file) {
		return uploadImpl(type, file, GinkgoConfig.getProperty(upload_direct));
	}
	
	public static String contract(String type, MultipartFile file) {
		return uploadImpl(type, file, GinkgoConfig.getProperty(upload_contract));
	}
	
	/**
	 * 上传图片到服务器（表单提交时可直接上传图片到服务器/uploads目录）
	 */
	public static String uploadImage(MultipartFile file) {
		return uploadImpl("image", file, GinkgoConfig.getProperty(upload_direct));
	}
	
	public static String temp(String type, MultipartFile file) {
		return uploadImpl(type, file, GinkgoConfig.getProperty(upload_temp));
	}
	
	/**
	 * 临时上传文件(通常不会在Controller里使用)
	 */
	public static String tempImage(MultipartFile file) {
		return uploadImpl("image", file, GinkgoConfig.getProperty(upload_temp));
	}

	/**
	 * 确定图片有效（对于uploadify上传的图片，需要确定其有效，并移动文件至/uploads目录，而uploadify默认上传至uploads/temp目录）
	 */
	public static boolean confirm(String path) {
		return pathImpl(path, GinkgoConfig.getProperty(upload_confirm));
	}
	
	/**
	 * 移动图片到垃圾箱
	 */
	public static boolean trash(String path) {
		return pathImpl(path, GinkgoConfig.getProperty(upload_trash));
	}
	
	private static String uploadImpl(String type, MultipartFile file, String uri) {
		if(file==null || file.isEmpty()) return null;
		
		try {
			HttpEntity entity = MultipartEntityBuilder.create()
					.setMode(HttpMultipartMode.RFC6532)
					.addTextBody("type", type, textUtf8)
					.addBinaryBody("file", file.getInputStream(), ContentType.DEFAULT_BINARY, file.getOriginalFilename())
					.build();
			
			HttpUriRequest request = RequestBuilder.post()
					.setUri(uri)
					.setEntity(entity)
					.build();
			
			HttpResponse response = httpClient.execute(request);
			String string = EntityUtils.toString(response.getEntity());
			logger.info("upload "+file.getOriginalFilename()+" ("+file.getSize()/1024+"K) to: " + uri);
			logger.info(string);
			return string;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static boolean pathImpl(String path, String uri) {
		if(path == null || path.length() == 0) return false;
		
		HttpEntity entity = MultipartEntityBuilder.create()
				.setMode(HttpMultipartMode.RFC6532)
				.addTextBody("path", path, textUtf8)
				.build();
		
		HttpUriRequest request = RequestBuilder.post()
				.setUri(uri)
				.setEntity(entity)
				.build();
		
		try {
			HttpResponse response = httpClient.execute(request);
			String string = EntityUtils.toString(response.getEntity());
			boolean result = "1".equals(string);
			logger.info(uri+"?path="+path+" result: "+result);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
