package com.yunxunzh.mquery;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * ObjectCache，软引用的数据存储类，有效防止oom错误
 * @author ping
 * 2014-4-13 1:28:59
 */
public class ObjectCache {
	private HashMap<String, SoftReference> data;
	
	public ObjectCache() {
		data = new HashMap<String, SoftReference>();
	}
	
	public int getsum () {
		try {
			return data.size();
		} catch (Exception e) {}
		return 0;
	}
	
	public HashMap<String, SoftReference> getAllData() {
		return data;
	}
	
	
	public <T> boolean addCache(String key,T object) {
		try {
			if (!data.containsKey(key)) {
				return putCache(key,object);
			}
		} catch (Exception e) {		}
		return false;
	}
	
	  
	public <T> boolean putCache(String key,T object) {
		try {
			data.put(key, new SoftReference<T>(object));
			return true;
		} catch (Exception e) {		}
		return false;
	}
	
	
	public boolean delCache(String key) {
		try {
			SoftReference sf = data.get(key);
			if (sf !=null) {
				sf.clear();
				data.put(key, null);
				data.remove(key);
			}
			return true;
		} catch (Exception e) {		}
		return false;
	}
	
	public static void gc() {
		System.gc();
		System.runFinalization();
	}
	
	public <T> T getCache(String key,Class<T> cls) {
		try {
			SoftReference<T> sf = data.get(key);
			return sf.get();
		} catch (Exception e) {		}
		return null;
	}
	
	public Object getCache(String key) {
		try {
			SoftReference sf = data.get(key);
			return sf.get();
		} catch (Exception e) {		}
		return null;
	}
		
}
