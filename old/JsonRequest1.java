package com.minephone.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.alibaba.fastjson.JSONObject;
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
public class JsonRequestBak0 extends Request<String> {
	
	
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
	public JsonRequestBak0(int method, String url, Listener<String> listener,ErrorListener errorListener) {
		super(method, url, errorListener);
		mlistener = listener;
		murl = url;
	}

	/**
	 * 发送数据时 获请求头header
	 */
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		 MQLog.i("test","getHeaders");
		if (headers == null) {
			headers = new HashMap<String, String>();
			//app验证
			headers.put("Content-Type","application/x-www-form-urlencoded; charset="+ DEFAULT_ENCODING);
//			headers.put("Content-Type","application/json; charset="+ DEFAULT_ENCODING);
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
		MQLog.i("test","getParams");
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
		 MQLog.i("test","decode data");
		String parsed;
		try {
//			if (re_header != null) {
//				if (re_header.containsKey("Content-Encoding")) {//Content-Type
//					String valu = re_header.get("");
//					if ("gzip".equalsIgnoreCase(valu)) {
//						data = decompress(data);
//					}
//				}
//			}
//			data = decompress(data);
			parsed = new String(data, DEFAULT_ENCODING);
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
            decompress(bais, baos);
            
            data = baos.toByteArray();  
            baos.flush();  
            baos.close();  
            bais.close();  
		} catch (Exception e) {
			
		}
        return data;  
    }  

  
    

    /** 
     * 数据解压缩 
     *  
     * @param is 
     * @param os 
     * @throws Exception 
     */  
    public static void decompress(InputStream is, OutputStream os)  
            throws Exception {  
  
        GZIPInputStream gis = new GZIPInputStream(is);  
  
        int count;  
        byte data[] = new byte[BUFFER];  
        while ((count = gis.read(data, 0, BUFFER)) != -1) {  
            os.write(data, 0, count);  
        }  
  
        gis.close();  
    }  

	 /**
	 * 获取BodyContentType
	 */
	 @Override
	 public String getBodyContentType() {
		 MQLog.i("test","getBodyContentType");
		 return "application/json; charset=" + getParamsEncoding();
//		 return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
	 }

	// /**
	// * 获取参数编码
	// */
	// @Override
	// protected String getParamsEncoding() {
	// return super.getParamsEncoding();
	// }


//		@Override
//		protected Map<String, String> getPostParams() throws AuthFailureError {
//			 MQLog.i("test","getPostParams()");
////			return null;
//			 return super.
//		}
	 
	 /**
	 * 获取请求内容，使用该方法会屏蔽getParams()
	 */
	 @Override
	 public byte[] getPostBody() throws AuthFailureError {
		 MQLog.i("test","getPostBody");
		 return getBody();
	 }

	 
	 
	 


		@Override
		public byte[] getBody() throws AuthFailureError {
			 MQLog.i("test","getBody");
//			try {
				return  JSONObject.toJSONString(params).getBytes();
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return "{}".getBytes();
		}
}

