package com.minephone.network;

import java.util.Date;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * 请求的cookie数据库工具
 * @author ping
 * @create 2014-9-21 上午10:19:47
 */
public class CookieUtil {
	private static final String DB_NAME = "mqrequest.db";
	private static final String TABLE_NAME = "cookie";
	
	private static final String TABLE_ID= "_id";
	private static final String TABLE_URL = "url";
	private static final String TABLE_KEY = "key";
	private static final String TABLE_VALUE = "value";
	private static final String TABLE_TIMEOUT = "timeout";
	
	private static SQLiteDatabase mdb;
	
	public static void checkDB(Context context) {
		if (mdb==null) {
			if (context ==null) throw new IllegalStateException("application is null,DbUtil cannot create");
			try {
				//创建数据库
				mdb = context.openOrCreateDatabase(DB_NAME, Application.MODE_PRIVATE, null);			
				//创建据库表
				mdb.execSQL("create table if not exists "+TABLE_NAME + "  (" + TABLE_ID + " integer PRIMARY KEY," +TABLE_URL + " text," + TABLE_KEY + " text," + TABLE_VALUE + " text," +TABLE_TIMEOUT + " bigint)");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 设置cookie
	 * @author ping
	 * @create 2014-9-22 上午9:59:34
	 * @param context
	 * @param url
	 * @param setcookieStr
	 * @return
	 */
	public static boolean setcookie(Context context,String url,String setcookieStr) {
		//Set-Cookie = addtime=null; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/
		//cookie = LoginCookie=d512310ecf8bc7f152945872bfcd18d1; JSESSIONID=73148C205CEEFE2BB34E9619FC3C7D38
		if (setcookieStr!=null) {
			String[] strs = setcookieStr.replace("; ", ";").split(";");
			String key = null;
			String value = null;
			long timeout = Long.MAX_VALUE;
			for (int i = 0; i < strs.length; i++) {
				String[] strs2 = strs[i].split("=");
				if (strs2.length>1) {
					if ("Expires".equalsIgnoreCase(strs2[0])) {
						try {
							timeout = Date.parse(strs2[1]);
						} catch (Exception e) {
							timeout=0;
							e.printStackTrace();
						}
					} else if ("Path".equalsIgnoreCase(strs2[0])) {
						//do nothing
					} else {
						key = strs2[0];
						value = strs2[1];
					}
				}
			}
			

			setValue(context, url, key, value, timeout);
			return true;
		}
		return false;
	}
	
	/**
	 * 设置数据
	 */
	public static boolean setValue(Context context,String url,String key, String value,long timeout) {
		delKey(context,url,key);// 删除已存在的
		try {
			ContentValues cv = new ContentValues();
			cv.put(TABLE_URL, url);
			cv.put(TABLE_KEY, key);
			cv.put(TABLE_VALUE, value);
			cv.put(TABLE_TIMEOUT, timeout);
			mdb.insert(TABLE_NAME, null, cv);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 读取cookie
	 * @author ping
	 * @create 2014-9-22 上午10:03:28
	 * @param context
	 * @param url
	 * @return
	 */
	public static String getCookie(Context context,String url) {
		checkDB(context);
		StringBuffer var = new StringBuffer();
		try {
			Cursor sur = mdb.rawQuery("select * from " + TABLE_NAME + " where " + TABLE_URL+ "='" + url +  "' and " + TABLE_TIMEOUT + ">" +  new Date().getTime(), null);
			if (sur != null) {
				while(sur.moveToNext()) {
					var.append(sur.getString(sur.getColumnIndex(TABLE_KEY)) + "=" + sur.getString(sur.getColumnIndex(TABLE_VALUE)) + "; ");
				}
				sur.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (var.length()>2) {
			return var.substring(0, var.length()-2);
		}
		return null;
	}

	/**
	 * 删除数据
	 */
	public static boolean delKey(Context context,String url,String key) {
		checkDB(context);
		try {
			mdb.execSQL("delete from " + TABLE_NAME + " where " + TABLE_KEY + "='" + key + "'");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
