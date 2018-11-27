package com.dl.spring.cache.service;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.dl.spring.cache.po.User;

@Component
public class CheckUtils implements ApplicationContextAware {
	private static CacheManager cacheManager;
	
	public void setCacheManager( CacheManager cacheManager){
		CheckUtils.cacheManager = cacheManager;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		setCacheManager(ctx.getBean(CacheManager.class));
	}
	
	@SuppressWarnings("unchecked")
	public static boolean checkPut(String relateCache, String relateCacheKey, String objectKey, Object object)
			throws Exception {

		Cache cache = cacheManager.getCache(relateCache);
		if (cache == null) {
			return true;
		}
		Set<Object> objs = cache.get(relateCacheKey, Set.class);
		if (objs == null) {
			// 还未加载过该关联缓存，不更新
			return true;
		}
		Object finded = null;
		for (Object o : objs) {
			if (invokeGetterMethod(o, objectKey) .equals( invokeGetterMethod(object, objectKey) )) {
				finded = o;
			}
		}
		if (finded != null) {
			objs.remove(finded);
		}
		objs.add(object);
		cache.put(relateCacheKey, objs);
		return true;
	}

	@SuppressWarnings("unchecked")
	public static boolean checkEvict(String relateCache, String relateCacheKey, String objectKey, User user) {

		Cache cache = cacheManager.getCache(relateCache);
		if (cache == null) {
			return true;
		}
		Set<User> users = cache.get(relateCacheKey, Set.class);
		if (users == null) {
			users = new HashSet<>();
		}
		users.remove(user);
		// User finded = null;
		// for(User u : users){
		// if(u.getId() == user.getId()){
		// finded = u;
		// }
		// }
		// if(finded != null){
		// users.remove(finded);
		// }
		// users.add(user);
		cache.put(relateCacheKey, users);
		return true;
	}


	private static Object invokeGetterMethod(Object o, String field) throws Exception {
		String methodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);

		Method m = o.getClass().getDeclaredMethod(methodName);
		return m.invoke(o);
	}
}
