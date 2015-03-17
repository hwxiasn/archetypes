package com.qingbo.ginkgo.ygb.cms.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.cms.entity.BgmEntity;
import com.qingbo.ginkgo.ygb.cms.entity.BgmField;
import com.qingbo.ginkgo.ygb.cms.entity.BgmFieldFilter;
import com.qingbo.ginkgo.ygb.cms.entity.BgmFieldView;
import com.qingbo.ginkgo.ygb.cms.entity.BgmMenu;
import com.qingbo.ginkgo.ygb.cms.entity.BgmPlatform;
import com.qingbo.ginkgo.ygb.cms.repository.BgmEntityRepository;
import com.qingbo.ginkgo.ygb.cms.repository.BgmFieldFilterRepository;
import com.qingbo.ginkgo.ygb.cms.repository.BgmFieldRepository;
import com.qingbo.ginkgo.ygb.cms.repository.BgmFieldViewRepository;
import com.qingbo.ginkgo.ygb.cms.repository.BgmMenuRepository;
import com.qingbo.ginkgo.ygb.cms.repository.BgmPlatformRepository;
import com.qingbo.ginkgo.ygb.cms.service.BackgroundManagementService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;

@Service("backgroundManagementService")
public class BackgroundManagementServiceImpl implements BackgroundManagementService {
	
	private static final String QUEUING_MAKE_SRC = "04";

	@Autowired private BgmPlatformRepository bgmPlatformRepository;
	@Autowired private BgmMenuRepository bgmMenuRepository;
	@Autowired private BgmEntityRepository bgmEntityRepository;
	@Autowired private BgmFieldRepository bgmFieldRepository;
	@Autowired private BgmFieldViewRepository bgmFieldViewRepository;
	@Autowired private BgmFieldFilterRepository bgmFieldFilterRepository;
	
	@Resource private QueuingService queuingService;
	
	@Override
	public Result<BgmEntity> addEntity(BgmEntity entity) {
		Result<Long> entityQueuing = queuingService.next(QUEUING_MAKE_SRC);
	    if(!entityQueuing.hasObject()){
	    	return Result.newFailure("BGM00001", "Id generation error.");
	    }
	    entity.setId(entityQueuing.getObject());
	    for(BgmField field : entity.getFields()){
			Result<Long> fieldQueuing = queuingService.next(QUEUING_MAKE_SRC);
		    if(!fieldQueuing.hasObject()){
		    	return Result.newFailure("BGM00001", "Id generation error.");
		    }
		    field.setId(fieldQueuing.getObject());
	    }
		BgmEntity saved = bgmEntityRepository.save(entity);
		return Result.newSuccess(saved);
	}

	@Override
	public Result<Boolean> deleteEntity(Long id) {
		bgmEntityRepository.delete(id);
		return Result.newSuccess(true);
	}

	@Override
	public Result<BgmEntity> modifyEntity(BgmEntity entity) {
		BgmEntity saved = bgmEntityRepository.save(entity);
		return Result.newSuccess(saved);
	}

	@Override
	public Result<List<BgmEntity>> listEntities() {
		List<BgmEntity> entities = bgmEntityRepository.findAll();
		return Result.newSuccess(entities);
	}

	@Override
	public Result<BgmEntity> getEntity(Long id) {
		BgmEntity entity = bgmEntityRepository.findOne(id);
		return Result.newSuccess(entity);
	}

	@Override
	public Result<BgmMenu> addMenu(BgmMenu menu) {
		Result<Long> queuing = queuingService.next(QUEUING_MAKE_SRC);
	    if(!queuing.hasObject()){
	    	return Result.newFailure("BGM00001", "Id generation error.");
	    }
	    menu.setId(queuing.getObject());
	    BgmMenu saved = bgmMenuRepository.save(menu);
		return Result.newSuccess(saved);
	}

	@Override
	public Result<Boolean> deleteMenu(Long id) {
		bgmMenuRepository.delete(id);
		return Result.newSuccess(true);
	}
	

	@Override
	public Result<BgmMenu> getMenu(Long id) {
		BgmMenu menu = bgmMenuRepository.findOne(id);
		return Result.newSuccess(attachFields(menu));
	}

	@Override
	public Result<List<BgmMenu>> listMenus(Long platformId) {
		SpecParam<BgmMenu> specParam = new SpecParam<BgmMenu>();
		specParam.eq("platform", platformId);						// 指定平台
		specParam.eq("deleted", false);								// 未删除
		Sort sort = new Sort(Sort.Direction.ASC, "level").and(new Sort(Sort.Direction.ASC, "serial"));	// 排序
		List<BgmMenu> menus = bgmMenuRepository.findAll(SpecUtil.spec(specParam), sort);
//		for(BgmMenu menu : menus){
//
//		}
		return Result.newSuccess(menus);
	}

	@Override
	public Result<BgmPlatform> addPlatform(BgmPlatform platform) {
		Result<Long> queuing = queuingService.next(QUEUING_MAKE_SRC);
	    if(!queuing.hasObject()){
	    	return Result.newFailure("BGM00001", "Id generation error.");
	    }
	    platform.setId(queuing.getObject());
	    BgmPlatform saved = bgmPlatformRepository.save(platform);
		return Result.newSuccess(saved);
	}

	@Override
	public Result<Boolean> deletePlatform(Long id) {
		BgmPlatform platform = bgmPlatformRepository.findOne(id);
		if(platform == null){
			return Result.newFailure("BGM00001", "No such platform.");
		}
		SpecParam<BgmMenu> specParam = new SpecParam<BgmMenu>();
		specParam.eq("platform", id);						// 指定专题
		specParam.eq("deleted", false);						// 未删除
		List<BgmMenu> menus = bgmMenuRepository.findAll(SpecUtil.spec(specParam));
		if(menus != null){
			for(BgmMenu menu : menus){
				menu.setDeleted(true);
				bgmMenuRepository.save(menu);
			}
		}
		platform.setDeleted(true);
		bgmPlatformRepository.save(platform);
		return Result.newSuccess(true);
	}

	@Override
	public Result<List<BgmPlatform>> listPlatforms() {
		SpecParam<BgmPlatform> specParam = new SpecParam<BgmPlatform>();
		specParam.eq("deleted", false);						// 未删除
		List<BgmPlatform> platforms = bgmPlatformRepository.findAll(SpecUtil.spec(specParam));
		return Result.newSuccess(platforms);
	}

	@Override
	public Result<BgmPlatform> getPlatform(Long id) {
		BgmPlatform platform = bgmPlatformRepository.findOne(id);
		return Result.newSuccess(platform);
	}
	
	private BgmMenu attachFields(BgmMenu menu){
		if(menu.getLevel() > 1){
			// 加入显示字段
			List<BgmField> viewFields = new ArrayList<BgmField>();
			SpecParam<BgmFieldView> viewSpecParam = new SpecParam<BgmFieldView>();
			viewSpecParam.eq("menuId", menu.getId());
			List<BgmFieldView>  viewRelations = bgmFieldViewRepository.findAll(SpecUtil.spec(viewSpecParam));
			if(viewRelations != null){
				for(BgmFieldView relation : viewRelations){
					viewFields.add(bgmFieldRepository.findOne(relation.getFieldId()));
				}
			}
			menu.setViewFields(viewFields);
			
			// 加入筛选字段
			List<BgmField> filterFields = new ArrayList<BgmField>();
			SpecParam<BgmFieldFilter> filterSpecParam = new SpecParam<BgmFieldFilter>();
			filterSpecParam.eq("menuId", menu.getId());
			List<BgmFieldFilter>  filterRelations = bgmFieldFilterRepository.findAll(SpecUtil.spec(filterSpecParam));
			if(filterRelations != null){
				for(BgmFieldFilter relation : filterRelations){
					filterFields.add(bgmFieldRepository.findOne(relation.getFieldId()));
				}
			}
			menu.setFilterFields(filterFields);
		}
		
		return menu;
	}
}
