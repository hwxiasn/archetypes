package com.hwxiasn.contentserver.impl;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import com.hwxiasn.contentserver.facade.TestService;

public class TestServiceImpl implements TestService {

	public String echo(String msg) {
		System.out.println(msg);
		return "TestServiceImpl.echo: "+msg;
	}

	@Override
	public String time() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.CHINESE).format(new Date());
	}

}
