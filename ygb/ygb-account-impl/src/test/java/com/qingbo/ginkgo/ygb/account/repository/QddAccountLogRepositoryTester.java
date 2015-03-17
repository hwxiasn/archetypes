package com.qingbo.ginkgo.ygb.account.repository;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.account.entity.QddAccountLog;

public class QddAccountLogRepositoryTester extends BaseRepositoryTester {
	@Autowired QddAccountLogRepository qddAccountLogRepository;
	@Test
	public void qddAccountLog() {
		List<QddAccountLog> findAll = qddAccountLogRepository.findAll();
		printList(findAll);
	}
}
