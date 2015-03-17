package com.qingbo.ginkgo.ygb.customer;

import java.sql.PseudoColumnUsage;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;
import com.qingbo.ginkgo.ygb.customer.service.PasswordService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:consumer.xml" })
public class Consumer {
	@Autowired TongjiService tongjiService;
	@Autowired PasswordService passwordService;
	
	@Test
	public void password() {
		String[] dicts= {"123456","12345678","qweasd","QWEASD"};
		SqlBuilder sqlBuilder = new SqlBuilder("user_name,password,salt", "user");
		List list = tongjiService.list(sqlBuilder).getObject();
		for(Object obj:list) {
			Object[] arr=(Object[])obj;
			String userName = ObjectUtils.toString(arr[0]);
			String password = ObjectUtils.toString(arr[1]);
			String salt = ObjectUtils.toString(arr[2]);
			
			if(StringUtils.isBlank(userName) || StringUtils.isBlank(password) || StringUtils.isBlank(salt)) {
				if(StringUtils.isNotBlank(userName)) {
					String generateSalt = passwordService.generateSalt();
					String encryptPassword = passwordService.encryptPassword("123456", userName, generateSalt);
					System.err.println(userName+","+encryptPassword+","+generateSalt);
				}
				continue;
			}
			
			for(String dict:dicts) {
				String encryptPassword = passwordService.encryptPassword(dict, userName, salt);
				if(encryptPassword.equals(password)) {
					System.err.println(userName+","+dict);
					break;
				}
			}
//			System.out.println(userName+", not get");
		}
	}
}
