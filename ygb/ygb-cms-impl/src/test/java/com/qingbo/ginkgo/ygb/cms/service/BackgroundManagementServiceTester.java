package com.qingbo.ginkgo.ygb.cms.service;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.cms.entity.BgmEntity;
import com.qingbo.ginkgo.ygb.cms.entity.BgmField;
import com.qingbo.ginkgo.ygb.cms.entity.BgmMenu;
import com.qingbo.ginkgo.ygb.cms.entity.BgmPlatform;
import com.qingbo.ginkgo.ygb.common.result.Result;

public class BackgroundManagementServiceTester extends BaseServiceTester {

	@Autowired private BackgroundManagementService backgroundManagementService;
	
	public void testAddEntity(){
		BgmField field1 = new BgmField();
		BgmField field2 = new BgmField();
		field1.setField("code");
		field1.setName("编码");
		field2.setField("name");
		field2.setName("名称");
		BgmEntity entity = new BgmEntity();
		entity.setName("专题");
		entity.setClassName("Subject");
		entity.addField(field1);
		entity.addField(field2);
		Result<BgmEntity> result = backgroundManagementService.addEntity(entity);
		if(result.hasObject()){
			System.out.println("OK");
			for(BgmField field : result.getObject().getFields()){
				System.out.println(field.getField() + " -- " + field.getName() );
			}
		}else{
			System.out.println(result.getError() + " -- " + result.getMessage());
		}
	}
	
	
	public void testDeleteEntity(){
		Result<Boolean> result = backgroundManagementService.deleteEntity(150210170409040001L);
		if(result.success()){
			System.out.println(result.getObject());
		}else{
			System.out.println(result.getError() + " -- " + result.getMessage());
		}
	}
	
	public void testModifyEntity(){
		
	}
	
	@Test
	public void testGetEntity(){
		Result<BgmEntity> result = backgroundManagementService.getEntity(150210171434040001L);
		if(result.hasObject()){
			for(BgmField field : result.getObject().getFields()){
				System.out.println(field.getField() + " -- " + field.getName() );
			}
		}else{
			System.out.println(result.getError() + " -- " + result.getMessage());
		}
	}
	
	public void testAddPlatform(){
		BgmPlatform platform = new BgmPlatform();
		platform.setTag("BY");
		platform.setShortName("倍赢财富箱");
		platform.setFullName("倍赢金融财富箱");
		Result<BgmPlatform> result = backgroundManagementService.addPlatform(platform);
		if(result.hasObject()){
			System.out.println(JSON.toJSON(result.getObject()));
		}else{
			System.out.println(result.getError() + " -- " + result.getMessage());
		}
	}
	
	public void testAddMenu(){
		BgmMenu menu1 = new BgmMenu();
		menu1.setPlatform(150211151807040001L);
		menu1.setLevel(1);
		menu1.setSerial(1);
		menu1.setName("产品管理");
		Result<BgmMenu> result1 = backgroundManagementService.addMenu(menu1);
		if(result1.hasObject()){
			System.out.println(JSON.toJSON(result1.getObject()));
			BgmMenu menu2 = new BgmMenu();
			menu2.setPlatform(150211151807040001L);
			menu2.setParent(result1.getObject());
			menu2.setLevel(2);
			menu2.setSerial(1);
			menu2.setName("项目管理");
			Result<BgmMenu> result2 = backgroundManagementService.addMenu(menu2);
			if(result2.hasObject()){
				System.out.println(JSON.toJSON(result2.getObject()));
			}else{
				System.out.println(result2.getError() + " -- " + result2.getMessage());
			}
		}else{
			System.out.println(result1.getError() + " -- " + result1.getMessage());
		}
	}
	
	public void testAddChildMenu(){
		BgmMenu menu1 = new BgmMenu();
		menu1.setPlatform(150211151807040001L);
		menu1.setLevel(2);
		menu1.setSerial(2);
		menu1.setName("合同管理");
		Result<BgmMenu> result1 = backgroundManagementService.getMenu(150212113511040001L);
		if(result1.hasObject()){
			menu1.setParent(result1.getObject());
		}
		Result<BgmMenu> result2 = backgroundManagementService.addMenu(menu1);
		if(result2.hasObject()){
			Set<BgmMenu> children = result2.getObject().getChildren();
			if(children != null){
				System.out.println(result2.getObject().getName() + "'s children:");
				for(BgmMenu m : children){
					System.out.println(m.getName());
				}
			}
		}else{
			System.out.println(result2.getError() + " -- " + result2.getMessage());
		}
	}
	
	public void testDeleteMenu(){
		Result<Boolean> result = backgroundManagementService.deleteMenu(150212113342040001L);
		if(result.hasObject()){
			System.out.println(JSON.toJSON(result.getObject()));
		}else{
			System.out.println(result.getError() + " -- " + result.getMessage());
		}
	}
	
	public void testListMenus(){
		Result<List<BgmMenu>> result = backgroundManagementService.listMenus(150211151807040001L);
		if(result.hasObject()){
			System.out.println(result.getObject());
			for(BgmMenu menu : result.getObject()){
				Set<BgmMenu> children = menu.getChildren();
				if(children != null){
					System.out.println(menu.getName() + "'s children:");
					for(BgmMenu m : children){
						System.out.println(m.getName());
					}
				}
			}
		}else{
			System.out.println(result.getError() + " -- " + result.getMessage());
		}
	}
	
	public void testDeletePlatform(){
		Result<Boolean> result = backgroundManagementService.deletePlatform(150211151807040001L);
		if(result.hasObject()){
			System.out.println(JSON.toJSON(result.getObject()));
		}else{
			System.out.println(result.getError() + " -- " + result.getMessage());
		}
	}
}
