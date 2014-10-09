package com.qingbo.project.upload;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

public class UploadTester {
	private HttpClient client = HttpClients.createDefault();
	private String testFile = "E:/系统默认形象1.0_161.png";

	@Test
	public void upload() throws ClientProtocolException, IOException {
		HttpEntity entity = MultipartEntityBuilder.create()
				.setMode(HttpMultipartMode.RFC6532)//BROWSER_COMPATIBLE自定义charset，RFC6532=utf-8，STRICT=iso-8859-1
				.addTextBody("type", "image")
				.addBinaryBody("file", new File(testFile))
				.build();

		HttpUriRequest post = RequestBuilder
				.post()
				.setUri("http://localhost:8080/upload")
				.setEntity(entity).build();

		HttpResponse response = client.execute(post);
		System.out.println(response.getStatusLine().getStatusCode() + " => " + IOUtils.toString(response.getEntity().getContent(), "utf-8"));
	}
}
