package com.qingbo.ginkgo.ygb.base.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.entity.UploadImage;

public interface UploadImageRepository extends BaseRepository<UploadImage> {
	/** 查询某个对象相关的所有上传图片，例如"project","1"可查找project.id=1的项目相关的图片资源，按imageName排序 */
	List<UploadImage> findByObjectTypeAndObjectIdOrderByImageNameAsc(String objectType, Long objectId);
}
