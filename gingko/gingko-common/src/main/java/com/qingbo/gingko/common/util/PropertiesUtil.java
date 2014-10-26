package com.qingbo.gingko.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 属性文件工具类
 * @author hongwei
 */
public class PropertiesUtil {
	static Map<String,PropertiesHolder> props = new HashMap<String, PropertiesHolder>();
	static List<PropertiesListener> listeners = new CopyOnWriteArrayList<PropertiesListener>();
	static Log log = LogFactory.getLog(PropertiesUtil.class);
	static boolean resourceMonitoring = false;
	
	/**
	 * 加载项目类路径下的属性文件，每分钟自动监视属性文件的更改
	 */
	public static Properties get(String resource) {
		PropertiesHolder prop = props.get(resource);
		if(prop == null) {
			try {
				URL url = getResource(resource);
				if(url == null) return null;
				
				prop = new PropertiesHolder();
				URLConnection connection = url.openConnection();
				prop.lastModified = connection.getLastModified();
				prop.resource = resource;
				InputStream is = connection.getInputStream();
				prop.properties.load(is);
				is.close();
				log.info("加载属性文件("+resource+")："+url);
				log.debug(prop.properties);
			} catch (IOException e) {
				log.warn("加载属性文件出错："+resource, e);
			}
			props.put(resource, prop);
			
			checkMonitoring();
		}
		return prop.properties;
	}
	
	/**
	 * 加载项目类路径下的属性文件，每分钟自动监视属性文件的更改，支持自定义字符集
	 */
	public static Properties get(String resource, String charset) {
		PropertiesHolder prop = props.get(resource);
		if(prop == null || (prop.charset == null ? charset != null : !prop.charset.equalsIgnoreCase(charset))) {
			try {
				URL url = getResource(resource);
				if(url == null) return null;
				
				boolean charsetChange = (prop != null) && (prop.charset == null ? charset != null : !prop.charset.equalsIgnoreCase(charset));
				if(prop == null) {
					prop = new PropertiesHolder();
					prop.charset = charset;
					prop.resource = resource;
				}else if(charsetChange) prop.charset = charset;
				URLConnection connection = url.openConnection();
				prop.lastModified = connection.getLastModified();
				InputStream is = connection.getInputStream();
				if(prop.charset != null) {
					prop.properties.load(new InputStreamReader(is, prop.charset));
				}else {
					prop.properties.load(is);
				}
				is.close();
				log.info("加载属性文件("+resource+")："+url);
				log.debug(prop.properties);
			} catch (IOException e) {
				log.warn("加载属性文件出错："+resource, e);
			}
			props.put(resource, prop);
			
			checkMonitoring();
		}
		return prop.properties;
	}
	
	/**
	 * 获取填充参数的消息内容，支持{index}类型参数如key=value{0}apend{1}end
	 */
	public static String getMessage(Properties prop, String key, Object... args) {
		String value = prop.getProperty(key);
		if(value == null) return null;
		return MessageFormat.format(value, args);
	}
	
	public static Map<String, String> getParams(String[] strings){
		Map<String, String> params = new HashMap<String, String>();
		if(strings != null) {
			for(int idx = 0; idx < strings.length; idx+=2) {
				params.put(strings[idx], strings[idx+1]);
			}
		}
		return params;
	}
	
	private static Pattern tagPattern = Pattern.compile("\\{(\\w+)\\}");
	/**
	 * 获取填充参数的消息内容，支持{tag}类型参数如key=value{tag}append{name}end
	 */
	public static String getTagMessage(Properties prop, String key, Map<String, String> params) {
		String value = prop.getProperty(key);
		if(value == null) return null;
		if(params == null || params.size() == 0) return value;
		StringBuilder b = new StringBuilder();
		Matcher matcher = tagPattern.matcher(value);
		int idx = 0;
		while(matcher.find()) {
			int start = matcher.start();
			b.append(value.substring(idx, start));
			idx = matcher.end();
			
			String k = matcher.group(1);
			String v = params.get(k);
			b.append(v!=null ? v : matcher.group(0));
		}
		b.append(value.substring(idx));
		return b.toString();
	}
	
	/**
	 * 双向属性映射支持key <=> property
	 * @author hongwei
	 */
	public static class BidiProperties extends Properties {
		private static final long serialVersionUID = 1671513358615520262L;
		private Properties reverseProps = new Properties();
		public BidiProperties(Properties props) {
			this.putAll(props);
			for(String key : this.stringPropertyNames()) {
				reverseProps.setProperty(this.getProperty(key), key);
			}
		}
		
		/**
		 * 根据右值快速查找左值
		 */
		public String getKey(String property) {
			return reverseProps.getProperty(property);
		}
		
		/**
		 * 与stringPropertyNames相对，用于快速获取右值集合
		 */
		public Set<String> stringPropertyValues(){
			return reverseProps.stringPropertyNames();
		}
	}
	
	/**
	 * 资源文件更新监听器
	 */
	public static interface PropertiesListener {
		String getResource();
		void afterReloaded(Properties props);
	}
	public static void addPropertiesListener(PropertiesListener listener) {
		listeners.add(listener);
	}
	private static void notifyListeners(PropertiesHolder props) {
		for(PropertiesListener listener : listeners) {
			if(listener.getResource().equals(props.resource)) {
				listener.afterReloaded(props.properties);
			}
		}
	}
	
	static class PropertiesHolder {
		long lastModified;
		String resource;
		String charset;
		Properties properties = new Properties();
	}
	
	private static URL getResource(String resource) {
		URL url = PropertiesUtil.class.getClassLoader().getResource(resource);
		return url;
	}
	
	private static void checkMonitoring() {
		if(resourceMonitoring) return;
		
		resourceMonitoring = true;
		TaskUtil.scheduleAtFixedRate(new Runnable() {//每分钟检查一下时间戳，需要时自动更新属性集
			@Override
			public void run() {
				for(String resource : props.keySet()) {
					PropertiesHolder prop = props.get(resource);
					try {
						URL url = getResource(resource);
						URLConnection connection = url.openConnection();
						long lastModified = connection.getLastModified();
						if(lastModified > prop.lastModified) {
							prop.properties.clear();
							InputStream is = connection.getInputStream();
							if(prop.charset != null) {
								prop.properties.load(new InputStreamReader(is, prop.charset));
							}else {
								prop.properties.load(is);
							}
							is.close();
							prop.lastModified = lastModified;
							notifyListeners(prop);
							log.info("更新属性文件("+resource+")："+url);
							log.debug(prop.properties);
						}
					} catch (IOException e) {
						log.warn("更新属性文件出错："+resource, e);
					}
				}
			}
		}, 1, 1, TimeUnit.MINUTES);
	}
}
