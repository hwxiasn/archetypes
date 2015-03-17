package com.qingbo.ginkgo.ygb.base.repository;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;

public class RepositorysTester extends BaseRepositoryTester {
	@Autowired ApplicationContext applicationContext;
	
	@SuppressWarnings("rawtypes")
	@Test
	public void repository() {
		Map<String, BaseRepository> repositorys = applicationContext.getBeansOfType(BaseRepository.class);
		for(String name : repositorys.keySet()) {
			try {
				BaseRepository repository = repositorys.get(name);
				long count = repository.count();
				System.out.println(name+" has "+count+" records");
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
