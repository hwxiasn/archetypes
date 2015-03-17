package com.qingbo.ginkgo.ygb.cms.service;

import java.util.List;

import com.qingbo.ginkgo.ygb.cms.entity.BgmEntity;
import com.qingbo.ginkgo.ygb.cms.entity.BgmMenu;
import com.qingbo.ginkgo.ygb.cms.entity.BgmPlatform;
import com.qingbo.ginkgo.ygb.common.result.Result;

public interface BackgroundManagementService {
	Result<BgmPlatform> addPlatform(BgmPlatform platform);
	Result<Boolean>	deletePlatform(Long id);
	Result<List<BgmPlatform>> listPlatforms();
	Result<BgmPlatform> getPlatform(Long id);
	
	
	Result<BgmMenu> addMenu(BgmMenu menu);
	Result<Boolean>	deleteMenu(Long id);
	Result<List<BgmMenu>> listMenus(Long platformId);
	Result<BgmMenu> getMenu(Long id);
	
	Result<BgmEntity> addEntity(BgmEntity entity);
	Result<Boolean>	deleteEntity(Long id);
	Result<BgmEntity> modifyEntity(BgmEntity entity);
	Result<List<BgmEntity>> listEntities();
	Result<BgmEntity> getEntity(Long id);
}
