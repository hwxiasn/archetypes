package com.qingbo.ginkgo.ygb.base.util;

import net.sf.ehcache.Ehcache;

import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;

public class MyEhCacheCacheManager extends EhCacheCacheManager {
	/**
	 * add cache using default configs
	 */
	public Cache getCache(String name) {
		Cache cache = super.getCache(name);
		if (cache == null) {
			// Check the EhCache cache again (in case the cache was added at runtime)
			Ehcache ehcache = getCacheManager().addCacheIfAbsent(name);
			if (ehcache != null) {
				addCache(new EhCacheCache(ehcache));
				cache = super.getCache(name);  // potentially decorated
			}
		}
		return cache;
	}
}
