package com.qingbo.ginkgo.ygb.base.repository;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.base.entity.UploadImage;

public class UploadImageRepositoryTester extends BaseRepositoryTester {
	@Autowired private UploadImageRepository uploadImageRepository;
	
	@Test
	public void uploadImage() {
		List<UploadImage> findAll = uploadImageRepository.findAll();
		printList(findAll);
	}
}
