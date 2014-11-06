package com.qingbo.gingko.common.result;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class PageObject<T> implements Serializable {
	private int total;
	private List<T> list;
	public PageObject(int total, List<T> list) {
		this.total = total;
		this.list = list;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
}
