package com.minephone.network;

import com.android.volley.VolleyLog;

import android.util.Log;

class MQLog {
	public static boolean isDebug = true;
	
	static {
		//赋值
		VolleyLog.DEBUG = isDebug;
	}
	
	public static void i(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}
}
