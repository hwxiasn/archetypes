package com.qingbo.ginkgo.ygb.base.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.entity.UploadImage;
import com.qingbo.ginkgo.ygb.base.enums.UploadObjectType;
import com.qingbo.ginkgo.ygb.base.repository.UploadImageRepository;
import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.base.service.UploadImageService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.UploadUtil;

@Service("uploadImageService")
public class UploadImageServiceImpl implements UploadImageService {
	@Autowired private UploadImageRepository uploadImageRepository;
	@Autowired private QueuingService queuingService;
	
	@Override
	public Result<Map<String, List<String>>> projectImages(Long projectId) {
		return objectImages(UploadObjectType.PROJECT.getCode(), projectId);
	}

	@Override
	public Result<Boolean> updateProjectImages(Long projectId, Map<String, List<String>> projectImages) {
		return updateObjectImages(UploadObjectType.PROJECT.getCode(), projectId, projectImages);
	}

	@Cacheable(value="objectImages", key="#objectType+'-'+#objectId")
	public Result<Map<String, List<String>>> objectImages(String objectType, Long objectId) {
		List<UploadImage> uploadImages = uploadImageRepository.findByObjectTypeAndObjectIdOrderByImageNameAsc(objectType, objectId);
		Map<String, List<String>> images = new HashMap<String, List<String>>();
		for(UploadImage uploadImage : uploadImages) {
			if(uploadImage.isDeleted()) continue;//忽略标记删除的图片
			
			String imageType = uploadImage.getImageType();
			List<String> list = images.get(imageType);
			if(list == null) {
				list = new ArrayList<String>();
				images.put(imageType, list);
			}
			list.add(uploadImage.getImagePath());
		}
		return Result.newSuccess(images);
	}

	@CacheEvict(value="objectImages", key="#objectType+'-'+#objectId")
	public Result<Boolean> updateObjectImages(String objectType, Long objectId, Map<String, List<String>> objectImages) {
		List<UploadImage> uploadImages = uploadImageRepository.findByObjectTypeAndObjectIdOrderByImageNameAsc(objectType, objectId);
		List<String> images = new ArrayList<String>();
		for(List<String> list : objectImages.values()) {
			images.addAll(list);
		}
		//delete some images
		for(UploadImage uploadImage : uploadImages) {
			String path = uploadImage.getImagePath();
			if(!images.contains(path)) {
				if(objectId != null) {//for null entry, just add, no delete
//					uploadImageRepository.delete(uploadImage);
					uploadImage.setDeleted(true);//标记删除
					uploadImageRepository.save(uploadImage);
					UploadUtil.trash(path);
				}
			}else {
				images.remove(path);
			}
		}
		//add some images
		for(String imageType : objectImages.keySet()) {
			List<String> typeImages = objectImages.get(imageType);
			for(String path : typeImages) {
				if(images.contains(path)) {
					UploadImage image = new UploadImage();
					image.setId(queuingService.next("03").getObject());
					image.setObjectType(objectType);
					image.setObjectId(objectId);
					image.setImageType(imageType);
					image.setImagePath(path);
					
					String name = FilenameUtils.getName(path);
					name = originalFileName(name);
					
					image.setImageName(name);
					uploadImageRepository.save(image);
					UploadUtil.confirm(path);
				}
			}
		}
		return Result.newSuccess(true);
	}

	//img19_1412845651263.jpg
	private static String originalFileName(String name) {
		int lastDot = name.lastIndexOf('.');
		if(lastDot > -1) {
			int lastDash = name.lastIndexOf('_', lastDot);
			if(lastDash > -1) name = name.substring(0, lastDash) + name.substring(lastDot);
		}
		return name;
	}
}
