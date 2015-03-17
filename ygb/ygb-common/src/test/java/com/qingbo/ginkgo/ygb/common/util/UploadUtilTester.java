package com.qingbo.ginkgo.ygb.common.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;

public class UploadUtilTester {
	public static void main(String[] args) throws IOException {
		
		File file = new File("src/test/resources/test-error.properties");
		String name = "file";
		String originalFileName = file.getName();
		String contentType = "text/plain";
		byte[] content = FileCopyUtils.copyToByteArray(file);
		
		String type="contract";//合同目录/uploads/contract
		MockMultipartFile uploadFile = new MockMultipartFile(name, originalFileName, contentType, content);
		String upload = UploadUtil.contract(type, uploadFile);
		System.out.println(upload);
		
		String[] split = upload.split("[,]");
		System.out.println(Arrays.toString(split));
		if(split.length==2) {
			String path = split[1];
//			boolean confirm = UploadUtil.confirm(path);
//			System.out.println("confirm: "+confirm);
//			System.out.println(GinkgoConfig.getProperty("uploads")+(confirm?"":"temp/")+split[1]);
			System.out.println(GinkgoConfig.getProperty("uploads")+path);
		}
	}
	
}
