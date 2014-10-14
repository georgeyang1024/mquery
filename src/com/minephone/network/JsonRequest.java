package com.minephone.network;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * 自定义请求Request类(保存请求的所有参数)
 * 
 * @author ping 2014-4-9 下午8:03:12
 */
public class JsonRequest extends Request<String> {
	private static final String DEFAULT_ENCODING = "utf-8";// 默认编码

	private Listener<String> mlistener;// 成功时的回调
	
	private String murl;//请求的URL(用户保留，不做请求)
	
	private Map<String, String> headers;// 提交头
	private Map<String, String> params;// 提交的参数
	private String cookies;// 提交的cookies
	
	private String re_cookies;// 返回来后重置成改cookies
	private Map<String, String> re_header;//返回的数据头
	

	
	public String getUrl() {
		return murl;
	}


	public void setUrl(String url) {
		this.murl = url;
	}


	public String getRe_cookies() {
		return re_cookies;
	}


	public Map<String, String> getRe_header() {
		return re_header;
	}


	public void setParams(Map<String, String> params) {
		this.params = params;
	}


	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	
	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

	public void setTimeout(int time) {
		if (time == 25000)
			return;// volley默认25秒
		RetryPolicy retryPolicy = new DefaultRetryPolicy(time,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		super.setRetryPolicy(retryPolicy);
	}
	
	
	/**
	 * 构建方法
	 * 
	 * @param method
	 * @param url
	 * @param listener
	 *            正确时的回调
	 * @param errorListener
	 *            错误时的回调
	 */
	public JsonRequest(int method, String url, Listener<String> listener,ErrorListener errorListener) {
		super(method, url, errorListener);
		mlistener = listener;
		murl = url;
	}

	/**
	 * 发送数据时 获请求头header
	 */
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		if (headers == null) {
			headers = new HashMap<String, String>();
			//app验证
//			headers.put("App_Key", "LBS1256");
//			headers.put("App_Secret", "a0cc07054f97e8e5f33102dbe42c1ecd");
			headers.put("Content-Type","application/x-www-form-urlencoded; charset="+ DEFAULT_ENCODING);
			headers.put("accept-language", "zh-Hans-CN,zh-Hans;q=0.5");
			headers.put("accept-encoding", "gzip, deflate");
			
//			if-modified-since = Mon, 22 Sep 2014 02:20:30 GMT
//			accept-language = zh-Hans-CN,zh-Hans;q=0.5
//			accept-encoding = gzip, deflate
//			cookie = addtime6=1233; addtime82=1233; addtime43=1233; addtime90=1233
//			content-type = application/x-www-form-urlencoded; charset=utf-8
//			user-agent = Dalvik/1.6.0 (Linux; U; Android 4.1.2; LG-F160K Build/JZO54K)
//			host = 192.168.191.1:8080
//			connection = Keep-Alive
		}
		
		//（cookie提交方式）cookie = LoginCookie=f5e2ff194d3f6bfad0ffeca33c90b81b; JSESSIONID=E2157841B595D0AE93D1C6F0DE6DD1F8
		if (!(cookies == null || cookies.length() == 0)) {
			headers.put("Cookie", cookies);
		}
		return headers;
	}

	/**
	 * 发送(post)数据时 vollery获取请求的参数
	 */
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		return params;
	}

	/**
	 * 触发mlistener成功事件(必须)
	 */
	@Override
	protected void deliverResponse(String response) {
		if (mlistener != null) {
			mlistener.onResponse(response);
		}
	}

	/**
	 * 处理服务器返回数据(获取成功后才调用)
	 */
	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		if (response == null) {
			return null;
		}
		if (response.statusCode != 200) {
			return null;
		}
		
		
		re_header =  response.headers;
		String parsed = decode(response.data);
		
		//获取返回的cookie(服务器要求写入的cookie)
		if (re_header!=null) {
			re_cookies = re_header.get("Set-Cookie");
		}

		return Response.success(parsed,HttpHeaderParser.parseCacheHeaders(response));
	}

	/**
	 * data原始数据转换编码
	 * 
	 * @author ping 2014-4-10 下午2:13:35
	 * @param data
	 * @return
	 */
	public static String decode(byte[] data) {
		String parsed;
		try {
			parsed = new String(data, DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			parsed = new String(data);
		}
		return parsed;
	}

	// /**
	// * 获取BodyContentType
	// */
	// @Override
	// public String getBodyContentType() {
	// return "application/x-www-form-urlencoded; charset=" +
	// getParamsEncoding();
	// }

	// /**
	// * 获取参数编码
	// */
	// @Override
	// protected String getParamsEncoding() {
	// return super.getParamsEncoding();
	// }

	// /**
	// * 获取请求内容，使用该方法会屏蔽getParams()
	// */
	// @Override
	// public byte[] getPostBody() throws AuthFailureError {
	// return super.getPostBody();
	// }

}
