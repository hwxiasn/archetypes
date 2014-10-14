package com.hwxiasn.contentserver.impl;

import com.hwxiasn.contentserver.facade.TestService;

public class TestServiceImpl implements TestService {

	public String echo(String msg) {
		return "TestServiceImpl.echo: "+msg;
	}

}
