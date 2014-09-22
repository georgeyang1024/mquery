package com.minephone.volley;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

@SuppressLint("NewApi")
public class Mdb {
	private static String TAG = "mquerydb";
	private static ObjectCache dbchache;
	private static ObjectCache classchache;
	
	private SQLiteDatabase db;
	private String dbfile;
	

	public Mdb(Context context) {
		init(context, null, null);
	}

	public Mdb(Context context, String dbfilename) {
		init(context, null, dbfilename);
	}

	public Mdb(Context context, String sdcardPath, String dbfilename) {
		init(context, sdcardPath, dbfilename);
	}

	private void init(Context context, String sdcardPath, String dbfilename) {
		if (context == null) {
			throw new IllegalStateException("context is null,Dbl cannot be create");
		}
		 if (dbchache == null) {
		 dbchache = new ObjectCache();
		 }
		 if (classchache==null) {
		 classchache = new ObjectCache();
		 }
		if (sdcardPath != null && dbfilename != null) {
			(new File(sdcardPath)).mkdirs();
			File dbf = new File(sdcardPath, dbfilename);
			dbfile = dbf.getAbsolutePath();
		} else if (dbfilename != null) {
			dbfile = dbfilename;
		} else {
			dbfile = "mquery.db";
		}
		db =   dbchache.getCache(dbfile, SQLiteDatabase.class);
		if (db == null) {
			db = context.openOrCreateDatabase(dbfile, Application.MODE_PRIVATE,null);
			dbchache.putCache(dbfilename, db);
		}
	}

	public SQLiteDatabase getdb() {
		return this.db;
	}

	public <T> List<T> findAll(Class<T> clazz) {
		String strSQL = "select * from " + clazz.getSimpleName();
		return findAllBySql(clazz, strSQL);
	}

	public <T> List<T> findAllbyWhere(Class<T> clazz, String where) {
		String strSQL = "select * from " + clazz.getSimpleName() + " where " + where;
		return findAllBySql(clazz, strSQL);
	}

	public <T> List<T> findAllBySql(Class<T> clazz, String strSQL) {
		MQLog.i(TAG, strSQL);
		Cursor cursor = db.rawQuery(strSQL, null);
		try {
			List<T> list = new ArrayList<T>();
			while (cursor.moveToNext()) {
				T data = cursor2object(cursor, clazz);
				list.add(data);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			cursor = null;
		}
		return null;
	}

	
	public <T> T findOne(Class<T> clazz) {
		String strSQL = "select * from " + clazz.getSimpleName();
		return findOnebyWhere(clazz, strSQL);
	}

	public <T> T findOnebyWhere(Class<T> clazz, String where) {
		String strSQL = "select * from " + clazz.getSimpleName() + " where " + where;
		return findOnebyWhere(clazz, strSQL);
	}

	public <T> T findOnelBySql(Class<T> clazz, String strSQL) {
		MQLog.i(TAG, strSQL);
		Cursor cursor = db.rawQuery(strSQL, null);
		try {
			T data = null;
			while (cursor.moveToFirst()) {
				data = cursor2object(cursor, clazz);
			}
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			cursor = null;
		}
		return null;
	}
	
	
	public boolean insert(Object object) {
		checktable(object.getClass());
		long re = db.insert(object.getClass().getSimpleName(), null,object2contentvalue(object));
		return re == -1 ? false : true;
	}

	public boolean updata(Object object) {
		checktable(object.getClass());
		ClassInfo cinfo = classchache.getCache(object.getClass().getSimpleName(),ClassInfo.class);
		Field cid  = cinfo.getId();
		if (cid==null) {
			throw new IllegalStateException(object.getClass().getName() +  " has no 'id' or '_id',can not updata");
		} else {
			try {
				String where[]={cid.getInt(object)+""};
				return updata(object, "_id=?",where);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean updata(Object object,String whereClause,String[] whereArgs) {
		checktable(object.getClass());
		int re = this.db.update(object.getClass().getSimpleName(), object2contentvalue(object), whereClause, whereArgs);
		return re==0?false:true;
	}
	
	
	public boolean delete(Object object) {
		checktable(object.getClass());
		ClassInfo cinfo = classchache.getCache(object.getClass().getSimpleName(),ClassInfo.class);
		Field cid  = cinfo.getId();
		if (cid==null) {
			throw new IllegalStateException(object.getClass().getName() +  " has no 'id' or '_id',can not updata");
		} else {
			try {
				return delete(cinfo.get_class(),"_id='" + cid.get(object) + "'");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean delete(Class cls,String where) {
		ClassInfo cinfo = classchache.getCache(cls.getSimpleName(),ClassInfo.class);
		try {
			String delsql = "delete from " + cinfo.getTablename() + " where "+ where;MQLog.i(TAG, delsql);
			this.db.execSQL(delsql);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	private void checktable(Class cls) {
		if (!tableIsExist(cls)) {
			ClassInfo cinfo = classchache.getCache(cls.getSimpleName(),ClassInfo.class);
			db.execSQL(cinfo.getCreatesql());
			MQLog.i(TAG,cinfo.getCreatesql());
			cinfo.setTableisexist(true);
		}
	}

	public boolean dropTable(Class cls) {
		return dropTable(cls.getSimpleName());
	}

	public boolean dropTable(String name) {
		String sql = "DROP TABLE " + name;
		MQLog.i(TAG, sql);
		try {
			this.db.execSQL(sql);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	public boolean tableIsExist(Class cls) {
		ClassInfo info = classchache.getCache(cls.getSimpleName(),ClassInfo.class);
		if (info ==null) {
			classchache.addCache(cls.getSimpleName(), new ClassInfo(cls));
			return tableIsExist(cls.getSimpleName());
		} else {
			return info.isTableisexist();			
		}
	}

	public boolean tableIsExist(String tablename) {
		Cursor cursor = null;
		try {
			String sql = "SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='" + tablename + "' ";
			MQLog.i(TAG, sql);
			cursor = this.db.rawQuery(sql, null);
			if ((cursor != null) && (cursor.moveToNext())) {
				int count = cursor.getInt(0);
				if (count > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			cursor = null;
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * cursor转换到object
	 * @param cursor
	 * @param cls
	 * @return
	 */
	public static <T> T cursor2object (Cursor cursor,Class<T> cls) {
		Field[] fieds = cls.getDeclaredFields();
		T result = null;
		try {
			result = cls.newInstance();
			for (Field field : fieds) {
				field.setAccessible(true);//允许访问私有字段
				Class<?> subcls =field.getType();
				String subname = subcls.getSimpleName();
				int cindex = cursor.getColumnIndex(getDBFiedName(field));//字段名称);
				int ctype = cursor.getType(cindex);
				
				if (cindex !=-1) {
					if (ctype == Cursor.FIELD_TYPE_STRING) {
						if (subname.equals("String")) {							
							field.set(result, cursor.getString(cindex));
						} else if (subname.equals("long")) {
							field.set(result, cursor.getLong(cindex));
						}
					} else if (ctype == Cursor.FIELD_TYPE_INTEGER) {
						if (subname.equals("int")) {
							field.set(result, cursor.getInt(cindex));
						} else if (subname.equals("boolean")) {
							int var  = cursor.getInt(cindex);
							if (var==1) {
								field.set(result, true);
							} else {
								field.set(result, false);
							}
						}
					} else if (subname.equals("float") && ctype == Cursor.FIELD_TYPE_FLOAT) {
						field.set(result, cursor.getFloat(cindex));
					} else if  (ctype == Cursor.FIELD_TYPE_BLOB){
						//other object
						field.set(result, ObjectUtil.toObject(cursor.getBlob(cindex)));
					} else {
						//type is null,do nothing
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * object转换到ContentValues
	 * @param cursor
	 * @param cls
	 * @return
	 */
	public static ContentValues object2contentvalue (Object object) {
		ContentValues result = new ContentValues();
		Field[] fieds = object.getClass().getDeclaredFields();
		for (Field field : fieds) {//枚举字段
			field.setAccessible(true);//允许访问私有字段
			Class<?> subcls =field.getType();//字段所属class
			String subname = subcls.getSimpleName();//字段所属class名称
			String keyname = getDBFiedName(field);//字段名称
			try {
				if (subname.equals("String")) {
					result.put(keyname, (String)field.get(object));
				} else if (subname.equals("int")) {
					result.put(keyname, field.getInt(object));
				} else if (subname.equals("boolean")) {
					result.put(keyname, field.getBoolean(object));
				} else if (subname.equals("float")) {
					result.put(keyname, field.getFloat(object));
				} else {
					byte[] data =ObjectUtil.toByteArray(field.get(object));
					result.put(keyname, data);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	/**
	 * 获取fied在数据库中的类型
	 * @param field
	 * @return
	 */
	public static String getfiedtype (Field field) {
		String classname = field.getType().getSimpleName();
		String type = null;
		if (classname.equals("String")) {
			type =  "text";
		} else if (classname.equals("long")) {
			type =  "text";
		} else if (classname.equals("int")) {
			type = "integer";
		} else if (classname.equals("boolean")) {
			type = "integer";
		} else if (classname.equals("float")) {
			type = "Float";
		} else {
			type = "BLOB";
		}
		return type;
	}
	
	/**
	 * 获取fied在数据库中的字段名,id转换为_id,values转化为_values,table转化为_table
	 * @param field
	 * @return
	 */
	public static String getDBFiedName (Field field) {
		String name = field.getName();
		if (name.equals("id")) {
			return "_id";
		} else if (name.equals("values")) {
			return "_values";
		} else if (name.equals("table")) {
			return "_table";
		} else {
			return name;
		}
	}
}
