package com.qingbo.project.bootstrap;

import com.qingbo.project.common.test.TomcatBootstrapHelper;

public class SaplingTomcatBootStrap {
	public static void main(final String[] args) {
		new TomcatBootstrapHelper(8080).start();
	}

}
