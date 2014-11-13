package com.minephone.network;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Wrapper for Volley requests to facilitate parsing of json responses. 
 * 
 * @param <T>
 */
public class JobjectRequest<T> extends Request<T>{
	
	/**
	 * Class type for the response
	 */
	private final Class<T> mClass;
	
	
	/**
	 * Callback for response delivery 
	 */
	private final Listener<T> mListener;
	
	/**
	 * @param method
	 * 		Request type.. Method.GET etc
	 * @param url
	 * 		path for the requests
	 * @param objectClass
	 * 		expected class type for the response. Used by gson for serialization.
	 * @param listener
	 * 		handler for the response
	 * @param errorListener
	 * 		handler for errors
	 */
	public JobjectRequest(int method
						, String url
						, Class<T> objectClass
						, Listener<T> listener
						, ErrorListener errorListener) {
		
		super(method, url, errorListener);
		this.mClass = objectClass;
		this.mListener = listener;
		
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(JSONObject.parseObject(json, mClass),HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			return Response.error(new ParseError(e));
		} 
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
		
	}
		
}
