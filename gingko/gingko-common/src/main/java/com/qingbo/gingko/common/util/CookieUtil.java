package com.qingbo.gingko.common.util;

import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 访问和设置cookie工具类
 * @author hongwei
 */
public class CookieUtil {
	/**
	 * 获取Cookie
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie cookies[] = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if(name.equals(cookie.getName())){
					return cookie;
				}
			}
		}
		return null;
	}
	
	/**
	 * 当前域名，根路径，14天
	 */
	public static void setCookie(HttpServletRequest request,HttpServletResponse response, String name, String value){
		setCookie(request, response, name, value, "/", 3600 * 24 * 14);
	}
	
	/**
	 * 当前域名，指定路径，14天
	 */
	public static void setCookie(HttpServletRequest request,HttpServletResponse response, String name, String value, String path){
		setCookie(request, response, name, value, path, 3600 * 24 * 14);
	}
	
	/**
	 * 当前域名，根路径，指定时间
	 */
	public static void setCookie(HttpServletRequest request,HttpServletResponse response, String name, String value, int maxAge){
		setCookie(request, response, name, value, "/", maxAge);
	}
	
	/**
	 * 当前域名，指定路径，指定时间
	 */
	public static void setCookie(HttpServletRequest request,HttpServletResponse response, String name, String value, String path, int maxAge) {
		String domain = getCookieDomain(request);
		setCookie(request, response, name, domain, value, path, maxAge);
	}
	
	/**
	 * 指定域名，指定路径，指定时间
	 */
	public static void setCookie(HttpServletRequest request,HttpServletResponse response, String name,String domain, String value, String path, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		if(domain != null) cookie.setDomain(domain);
		cookie.setMaxAge(maxAge);
		cookie.setPath(path);
		response.addCookie(cookie);
	}
	
	/**
	 * 获取请求设置Cookie时正确的域名值
	 */
	private static final Pattern ipDomain = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
	public static String getCookieDomain(HttpServletRequest request) {
		String domain = null;
		if(!"localhost".equals(request.getServerName())) {//localhost时不设domain反而能记上cookie
			String serverName = request.getServerName();
			if(ipDomain.matcher(serverName).matches()) {//域名为ip地址时保持不变
				domain = serverName;
			}else {
				boolean crossSubDomains = false;//默认关闭跨二级子域名
				if(crossSubDomains) {
					int dot1 = -1, dot2 = -1;
					dot1 = serverName.indexOf('.');
					if(dot1 > -1) dot2 = serverName.indexOf('.', dot1+1);
					domain = dot2 > -1 ? serverName.substring(dot1) : "."+serverName;
				}else {
					domain = serverName;
				}
			}
		}
		return domain;
	}
	
	/**
	 * 删除Cookie
	 */
	public static void removeCookie(HttpServletRequest request,HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, "");
		String domain = getCookieDomain(request);
		if(domain != null) cookie.setDomain(domain);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
}
