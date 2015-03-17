package com.qingbo.ginkgo.ygb.base.service;

import java.util.List;
import java.util.Map;

import com.qingbo.ginkgo.ygb.common.result.Result;

public interface UploadImageService {
	/**
	 * 获得项目相关图片资源，键值参考枚举{@link com.qingbo.ginkgo.UploadImageType.constants.SaplingConstants.UploadImageImageType UploadImageImageType}
	 */
	Result<Map<String, List<String>>> projectImages(Long projectId);
	/**
	 * 更新项目相关图片资源
	 */
	Result<Boolean> updateProjectImages(Long projectId, Map<String, List<String>> projectImages);
	/**
	 * 获得对象相关图片资源，对象参考枚举{@link com.qingbo.ginkgo.sapling.constants.SaplingConstants.UploadImageObjectType UploadImageObjectType}
	 * @param objectType，项目相关project，担保函相关letter，等
	 */
	Result<Map<String, List<String>>> objectImages(String objectType, Long objectId);
	/**
	 * 更新对象相关图片资源
	 */
	Result<Boolean> updateObjectImages(String objectType, Long objectId, Map<String, List<String>> objectImages);
}
