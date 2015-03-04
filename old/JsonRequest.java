package com.minephone.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

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
//	private static final String DEFAULT_CONTENT_TYPE = String.format("application/json; charset=%s", DEFAULT_ENCODING);//默认请求类型
	private static final String DEFAULT_CONTENT_TYPE = String.format("application/x-www-form-urlencoded; charset=%s", DEFAULT_ENCODING);//默认请求类型
	
	private Listener<String> mlistener;// 成功时的回调
	
	private String murl;//请求的URL(用户保留，不做请求)
	
	private Map<String, String> headers;// 提交头 
	private Map<String, String> params;// 提交的参数
	private String cookies;// 提交的cookies
	
	private String re_cookies;// 返回来后重置成改cookies
	private Map<String, String> re_header;//返回的数据头
	
	private String cachekey;
	
	public void setCachekey(String key) {
		cachekey = key;
	}
	@Override
	public String getCacheKey() {
		return cachekey ==null ? super.getCacheKey() :cachekey;
	}
	
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
		}
		headers.put("Content-Type",DEFAULT_CONTENT_TYPE);
		headers.put("accept-language", "zh-Hans-CN,zh-Hans;q=0.5");
		headers.put("accept-encoding", "gzip, deflate");
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
		//获取返回的cookie(服务器要求写入的cookie)
		if (re_header!=null) {
			re_cookies = re_header.get("Set-Cookie");
		}
		
		String parsed = decode(response.headers,response.data);
		
		return Response.success(parsed,HttpHeaderParser.parseCacheHeaders(response));
	}
	
	
	
	
	
	
	
	
	
	/**
	 * data原始数据转换编码
	 * 
	 * @author ping 2014-4-10 下午2:13:35
	 * @param data
	 * @return
	 */
	public static String decode(Map<String, String> header,byte[] data) {
		//gzip解码数据
		if (header != null) {
			if (header.containsKey("Content-Encoding")) {//Content-Type
				String valu = header.get("Content-Encoding");
				if ("gzip".equalsIgnoreCase(valu)) {
					data = decompress(data);
				}
			}
		}
		
		String parsed;
		try {
			parsed = new String(data, parseCharset(header));
		} catch (Exception e) {
			parsed = new String(data);
		}
		return parsed;
	}
	
	
    private static final  int BUFFER = 1024;
	/** 
     * gzip数据解压缩 
     *  
     * @param data 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decompress(byte[] data) {  
    	try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);  
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            // 解压缩
            GZIPInputStream gis = new GZIPInputStream(bais);  
            int count;  
            byte temp[] = new byte[BUFFER];  
            while ((count = gis.read(temp, 0, BUFFER)) != -1) {
                baos.write(temp, 0, count);
            }  
            gis.close();  
            
            data = baos.toByteArray();  
            baos.flush();  
            baos.close();  
            bais.close();  
		} catch (Exception e) {
			e.printStackTrace();
		}
        return data;  
    }
    
    /**
     * 从HttpHeaderParser抽出
     * @author ping
     * @create 2014-11-13 上午10:06:21
     * @param headers
     * @return
     */
	public static String parseCharset(Map<String, String> headers) {
		String contentType = (String) headers.get("Content-Type");
		if (contentType != null) {
			String params[] = contentType.split(";");
			for (int i = 1; i < params.length; i++) {
				String pair[] = params[i].trim().split("=");
				if (pair.length == 2 && pair[0].equals("charset"))
					return pair[1];
			}

		}
		return DEFAULT_ENCODING;
	}
}
