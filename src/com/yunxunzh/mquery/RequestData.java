package com.yunxunzh.mquery;

import java.util.Map;

import android.app.Dialog;
import android.content.Context;

public class RequestData{
	public static final String DEFAULT_ENCODING = "utf-8";// 默认编码
//	private static final String DEFAULT_CONTENT_TYPE = String.format("application/json; charset=%s", DEFAULT_ENCODING);//默认请求类型
	public static final String DEFAULT_CONTENT_TYPE = String.format("application/x-www-form-urlencoded; charset=%s", DEFAULT_ENCODING);//默认请求类型
	
	
	public String url;//请求的URL(用户保留，不做请求)
	public int method;//请求的方法
	public Context context;
	
//	public Listener listener;// 成功时的回调
//	public NetListener callBack;
	
	public Map<String, String> headers;// 提交头 
	public Map<String, String> params;// 提交的参数
	public String cookies;// 提交的cookies
	public String reCookies;// 返回来后重置成改cookies
	public Map<String, String> reHeader;//返回的数据头
	public String cacheKey;//缓存的key，可防止多出现
	public String flag;//请求标示
	public float timeout  =25;//超时时间,默认25秒
	public boolean saveCache;//是否缓存数据
	
	public Dialog dialog;// 加载窗口 int method;
	
	
	//用于存库
	public int id;
	public int type;//用于区分普通请求、草稿等
	public Object mydata;//自定义数据
	
	public class RequestType {
		public static final int NORMAL = 0;//普通
		public static final int UNTILSUCCESS=1;//直到成功的请求
		public static final int DRAFTS = 2;//草稿
	}
}
