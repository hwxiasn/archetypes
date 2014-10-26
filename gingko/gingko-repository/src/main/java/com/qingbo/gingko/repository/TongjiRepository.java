package com.qingbo.gingko.repository;

import java.util.List;

public interface TongjiRepository {
	int count(String sql);
	List<?> list(String sql);
	Object[] sums(String sql);
}
