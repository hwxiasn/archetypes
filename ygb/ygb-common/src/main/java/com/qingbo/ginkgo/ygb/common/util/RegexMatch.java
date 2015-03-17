package com.qingbo.ginkgo.ygb.common.util;

import java.util.ArrayList;
import java.util.List;
public class RegexMatch {

	//用户名是否匹配 (只能是6-20位的数字或者字母、数字和字母的组合)
	public static boolean isMatchUserName(String userName) {
		if(userName==null || ("").equalsIgnoreCase(userName)) {
			return false;
		}
		String regx = "^[a-zA-Z0-9]{6,20}$";
		return userName.matches(regx);

	}
	
	//手机是否匹配(11位数字以13、15、18、14、17开头)
	public static boolean isMacthMobile(String mobile) {
		if(mobile==null || ("").equalsIgnoreCase(mobile)) {
			return false;
		}
		String regx = "^0?(13[0-9]|15[012356789]|18[012356789]|14[57]|17[0-9])[0-9]{8}$";
		return mobile.matches(regx);
	}
	
	//邮箱是否匹配(匹配list中的邮箱)
	public static boolean isMatchEmail(String email) {
		if(email==null || ("").equalsIgnoreCase(email)) {
			return false;
		}
		String regx = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		boolean reg1 = email.matches(regx);
//		List<String> list = new ArrayList<String>();
//		list.add("qq.com");
//		list.add("126.com");
//		list.add("yahoo.com");
//		list.add("yahoo.com.cn");
//		list.add("gmail.com");
//		list.add("hotmail.com");
//		list.add("sina.com");
//		list.add("21.com");
//		list.add("msn.com");
//		list.add("163.com");
//		list.add("yeah.net");
//		list.add("yahoo.cn");
//		list.add("foxmail.com");
//		list.add("sohu.com");
//		list.add("sogou.com");
//		list.add("tom.com");
//		list.add("21cn.com");
//		list.add("live.cn");
//		list.add("live.com");
//		list.add("hexun.com");
//		list.add("139.com");
//		list.add("189.cn");
//		list.add("91.com");
//		list.add("56.com");
//		list.add("eyou.com");
//		list.add("people.com.cn");
//		list.add("sh.com");	
//		String eam = email.split("@")[1];
//		boolean reg2 = list.contains(eam);
		if(reg1) {
			return true;
		}else {
			return false;
		}
	}
	
	//真实姓名(2-4为的中文汉字)
	public static boolean isMatchRealName(String realName) {
		if(realName == null || ("").equalsIgnoreCase(realName)) {
			return false;
		}
		String regx = "^[\u4e00-\u9fa5]{2,4}$";
		return realName.matches(regx);
	}
	
	
//	public static void main(String args[] ) {
//		String userName = "hudongdfsgsg123652362";
//		String mobile = "15337469383";
//		String email = "agjkahga@qq.com";
//		String realName = "测测试试";
//		System.out.println("--------真实姓名----"+isMatchRealName(realName));
//		System.out.println("--------用户名----"+isMatchUserName(userName));
//		System.out.println("--------邮箱----"+isMatchEmail(email));
//		System.out.println("--------手机号码----"+isMacthMobile(mobile));
//	}
	
}
