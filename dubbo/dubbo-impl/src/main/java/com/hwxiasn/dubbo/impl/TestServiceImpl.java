package com.hwxiasn.dubbo.impl;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import com.hwxiasn.dubbo.facade.TestService;

public class TestServiceImpl implements TestService {

	public String echo(String msg) {
		return "dubbo server echo: "+msg;
	}

	@Override
	public String time() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.CHINESE).format(new Date());
	}

}
