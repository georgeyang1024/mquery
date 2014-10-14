package com.minephone.network;

import java.util.ArrayList;
import java.util.Map;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 请求工具
 * @author ping
 * @create 2014-8-29 下午2:00:44
 */
public class USRequestUtil {
	private static final String DB_NANME = "mqrequest.db";
	private static final String TABLE_NAME = "usrequest";
	private static final String TABLE_ID = "_id";
	private static final String TABLE_METHOD = "method";
	private static final String TABLE_URL = "url";
	private static final String TABLE_POSTDATA = "postdata";
	private static final String TABLE_HEADDATA = "headdata";
	
	private static SQLiteDatabase mdb;
	
	private static  void checkDB(Context context) {
		if (mdb==null) {
			if (context ==null) throw new IllegalStateException("context is null,Db cannot create");
			//创建数据库
			mdb = context.openOrCreateDatabase(DB_NANME, Application.MODE_PRIVATE, null);
			try {
				//创建缓存数据库表
				mdb.execSQL("create table if not exists " + TABLE_NAME + "  (" + TABLE_ID +  " integer PRIMARY KEY," + TABLE_METHOD + " integer," +TABLE_URL + " text," + TABLE_POSTDATA + " BLOB," + TABLE_HEADDATA + " BLOB)");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}
	
	
	public static boolean add(Context context,int method,String url,Map head,Map data) {
		checkDB(context);
		try {
			ContentValues cv = new ContentValues();
			cv.put(TABLE_METHOD, method);
			cv.put(TABLE_URL, url);
			cv.put(TABLE_HEADDATA, ObjectUtil.toByteArray(head));
			cv.put(TABLE_POSTDATA, ObjectUtil.toByteArray(data));
			mdb.insert(TABLE_NAME, null, cv);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static boolean del(Context context,int id) {
		checkDB(context);
		try {
			mdb.delete(TABLE_NAME, TABLE_ID + "=?",new String[]{id+""} );
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static ArrayList<RqData> listall (Context context) {
		checkDB(context);
		ArrayList<RqData> list = new ArrayList<RqData>();
		try {
			Cursor sur = mdb.rawQuery("select * from " + TABLE_NAME +" ORDER BY '" + TABLE_ID +"' asc",null);//上到下开始请求
			if (sur != null) {
				while(sur.moveToNext()){
					RqData info = new RqData();
					info.id = sur.getInt(sur.getColumnIndex(TABLE_ID));
					info.method = sur.getInt(sur.getColumnIndex(TABLE_METHOD));
					info.url =  sur.getString(sur.getColumnIndex(TABLE_URL));
					info.postdata = (Map) ObjectUtil.toObject(sur.getBlob(sur.getColumnIndex(TABLE_POSTDATA)));
					info.headdata = (Map) ObjectUtil.toObject(sur.getBlob(sur.getColumnIndex(TABLE_HEADDATA)));
					list.add(info);
				}
				sur.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}

class RqData {
	public int id;
	public int method;
	public String url;
	public Map headdata;
	public Map postdata;
}

