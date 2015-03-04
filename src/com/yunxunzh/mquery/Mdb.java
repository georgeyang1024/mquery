package com.yunxunzh.mquery;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

@SuppressLint("NewApi")
public class Mdb {
	private static String TAG = "mquerydb";
	private static ObjectCache dbchache;
	private static HashMap<String,ClassInfo> classchache;
	
	private SQLiteDatabase db;
	private String dbfile;
	private static String defaulePath;//数据库默认路径

    public static void setDefaultPath (String path) {
        defaulePath = path;
    }

	public Mdb(Context context) {
        if (defaulePath!=null) {
            init(context, defaulePath, "mquery.db");
        } else {
            init(context, null, null);

        }
	}

	public Mdb(Context context, String dbfilename) {
		init(context, null, dbfilename);
	}

	public Mdb(Context context, String sdcardPath, String dbfilename) {
		init(context, sdcardPath, dbfilename);
	}

	private void init(Context context, String Path, String dbfilename) {
		if (context == null) {
			throw new IllegalStateException("context is null,Dbl cannot be create");
		}
		 if (dbchache == null) {
		 dbchache = new ObjectCache();
		 }
		 if (classchache==null) {
		 classchache = new HashMap<String, ClassInfo>();
		 }
		if (Path != null && dbfilename != null) {
			(new File(Path)).mkdirs();
			File dbf = new File(Path, dbfilename);
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

	/**
	 * 字段修改需调用该方法,耗时操作 (class加字段时，更新表)
	 * @param clazz
	 */
	public void updataTable (Class clazz) {
		if (!checktable(clazz)) {
		try {
			boolean isLastTable = isLastTable(clazz);
			MQLog.e(TAG, "needUpdateTabe?" + !isLastTable);
			if (isLastTable){
				//是最新de表
				return;
			}
			//准备临时表创建语句
			Field[] fields = clazz.getDeclaredFields();
			String tableTName = clazz.getSimpleName() + "_temp";
			
			StringBuffer sb = new StringBuffer("create table if not exists " +tableTName+ " (");
			for (Field field : fields) {//枚举字段
				field.setAccessible(true);//允许访问私有字段
				String dbfield = Mdb.getDBFiedName(field);// 字段名称 / field.getName();//字段名
				String dbtype = Mdb.getfiedtype(field);
				if (dbfield.equals("_id")) {
					if (field.getType().getSimpleName().equals("int")) {
						sb.append("_id " + dbtype + " PRIMARY KEY,");
					} else {
						sb.append("_id " + dbtype + ",");
					}
				} else {
					sb.append(dbfield + " " + dbtype + ",");
				}
			}
			String sql = sb.toString();
			sql = sql.substring(0, sql.length() - 1) + ")";
			//创建临时表
			db.execSQL(sql);
			//数据转移
			int page=1;
			List list=  findAllbyPage(clazz, page, 100);
			while (!(list==null || list.size()==0)) {
				for (Object ob:list) {
					db.insert(tableTName, null,object2contentvalue(ob));
				}
				page++;
				list =  findAllbyPage(clazz, page, 100);
			}
			//删除原表//DROP TABLE Teachers;
			sql = "drop table " + clazz.getSimpleName();
			db.execSQL(sql);
			//临时表更改名//ALTER TABLE Students RENAME TO Teachers;
			sql = "ALTER TABLE " + tableTName + " RENAME TO " + clazz.getSimpleName();
			db.execSQL(sql);
		} catch (Exception e) {e.printStackTrace();}
		
		}
	}
	
	/**
	 * 表的字段 是否包含 class的全部字段
	 * @param clazz
	 * @return
	 */
	public boolean isLastTable(Class clazz) {
		try {
			Field[] fieds = clazz.getDeclaredFields();
			String strSQL ="PRAGMA table_info(["+clazz.getSimpleName() + "])";
			Cursor cursor = null;
			try {
				cursor = db.rawQuery(strSQL, null);
				for (Field field : fieds) {
					boolean fieldExistInTable = false;
					String dbfied = getDBFiedName(field);
					if (cursor.moveToFirst()){
						do {
							String name = cursor.getString(cursor.getColumnIndex("name"));
							if (dbfied.equals(name)) {
								fieldExistInTable=true;
								break;
							}
						} while (cursor.moveToNext());
						

						if (!fieldExistInTable) {
							return false;
						}
						
					} else {
						return false;
					}
					
				}
				
				
			
			} catch (Exception e) {
				MQLog.e(TAG, "error:" + e.getMessage());
				e.printStackTrace();
			} finally{
				if (cursor != null)
					cursor.close();
				cursor = null;
			}
		} catch (Exception e) {
			MQLog.e(TAG, "error:" + e.getMessage());
			e.printStackTrace();
		}
		return true;
	}
	
	public <T> List<T> findAll(Class<T> clazz) {
		String strSQL = "select * from " + clazz.getSimpleName();
		return findAllBySql(clazz, strSQL);
	}

    public int getCount(Class clazz) {
        return getCount(clazz.getSimpleName());
    }

    public int getCount(String tabname) {
        String strSQL = "select count(_Id) from " + tabname;
        Cursor cursor = null;
         try {
            cursor = db.rawQuery(strSQL, null);
            if (cursor.moveToNext()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
             e.printStackTrace();
         }
        return 0;
    }

    public <T> T getMax(Class clazz,String fiedname,Class<T> back) {
        String strSQL = "select "+ fiedname +" from " + clazz.getSimpleName() + " order by " + fiedname +" desc limit 1";
        MQLog.i(TAG,"getmaxsql:" + strSQL);
        Cursor cursor = null;
        Object result = null;
        try {
            cursor = db.rawQuery(strSQL, null);
            if (cursor.moveToNext()) {
                String cname = back.getSimpleName();
                int index = cursor.getColumnIndex(fiedname);
                if (cname.equals("int") || cname.equals("Integer")) {
                    result = cursor.getInt(index);
                } else if (cname.equals("float") || cname.equals("Float")) {
                    result = cursor.getFloat(index);
                } else if (cname.equals("long") || cname.equals("Long")) {
                    result = cursor.getLong(index);
                } else if (cname.equals("double") || cname.equals("Double")) {
                    String str = cursor.getString(index);
                    double res = 0;
                    try {
                        res = Double.parseDouble(str);
                    } catch (Exception e) {}
                    result = res;
                } else if (cname.equals("String")) {
                    result = cursor.getString(index);
                } else {
                    result =ObjectUtil.toObject(cursor.getBlob(index));
                }
            }
        } catch (Exception e) {
            //Failed to read row 0, column -1 from a CursorWindow which has 1 rows, 1 columns.
            e.printStackTrace();
        } finally {
            if (cursor!=null) {
                cursor.close();
            }
            cursor = null;
        }
        return (T)result;
    }
	
	public <T> List<T> findAllbyDesc(Class<T> clazz,String order) {
		String strSQL = "select * from " + clazz.getSimpleName() + " order by " + order + " desc";
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyAsc(Class<T> clazz,String order) {
		String strSQL = "select * from " + clazz.getSimpleName() + " order by " + order + " asc";
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyPageDesc(Class<T> clazz,String order, int page,int onepagecount) {
		String strSQL = "select * from " + clazz.getSimpleName() + " order by " + order + " desc" + " limit " + (page-1)*onepagecount + "," + onepagecount;
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyPageAsc(Class<T> clazz,String order, int page,int onepagecount) {
		String strSQL = "select * from " + clazz.getSimpleName() + " order by " + order + " asc" + " limit " + (page-1)*onepagecount + "," + onepagecount;
		return findAllBySql(clazz, strSQL);
	}

	public <T> List<T> findAllbyPageDesc(Class<T> clazz, int page,int onepagecount) {
		String strSQL = "select * from " + clazz.getSimpleName() + " order by _id desc" + " limit " + (page-1)*onepagecount + "," + onepagecount;
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyPageAsc(Class<T> clazz, int page,int onepagecount) {
		String strSQL = "select * from " + clazz.getSimpleName() + " order by _id asc" + " limit " + (page-1)*onepagecount + "," + onepagecount;
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyWhere(Class<T> clazz, String where) {
		String strSQL = "select * from " + clazz.getSimpleName() + " where " + where;
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyWhereDesc(Class<T> clazz, String where,String order) {
		String strSQL = "select * from " + clazz.getSimpleName() + " where " + where +" order by " + order + " desc";
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyWhereAsc(Class<T> clazz, String where,String order) {
		String strSQL = "select * from " + clazz.getSimpleName() + " where " + where +" order by " + order + " asc";
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyPage(Class<T> clazz, int page,int onepagecount) {
		String strSQL = "select * from " + clazz.getSimpleName() + " limit " + (page-1)*onepagecount + "," + onepagecount;
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyWherePage(Class<T> clazz,String where, int page,int onepagecount) {
		String strSQL = "select * from " + clazz.getSimpleName() + " where " + where +" limit " + (page-1)*onepagecount + "," + onepagecount;
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyWherePageAsc(Class<T> clazz,String where, int page,int onepagecount) {
		String strSQL = "select * from " + clazz.getSimpleName() + " where " + where + " order by _id asc " + " limit " + (page-1)*onepagecount + "," + onepagecount;
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyWherePageDesc(Class<T> clazz,String where, int page,int onepagecount) {
		String strSQL = "select * from " + clazz.getSimpleName() + " where " + where + " order by _id desc " + " limit " + (page-1)*onepagecount + "," + onepagecount;
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyWhereOrderPageDesc(Class<T> clazz,String where,String order, int page,int onepagecount) {
		String strSQL = "select * from " + clazz.getSimpleName() + " where " + where + " order by " + order + " desc " + " limit " + (page-1)*onepagecount + "," + onepagecount;
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAllbyWhereOrderPageAsc(Class<T> clazz,String where,String order, int page,int onepagecount) {
		String strSQL = "select * from " + clazz.getSimpleName() + " where " + where + " order by " + order + " asc " + " limit " + (page-1)*onepagecount + "," + onepagecount;
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAll2byWherePageAsc(Class<T> clazz,String where, int page,int onepagecount) {
		String strSQL = "select * from (select * from " + clazz.getSimpleName() + " where " + where + " order by _id asc " + " limit " + (page-1)*onepagecount + "," + onepagecount +") order by _id desc";
		return findAllBySql(clazz, strSQL);
	}
	
	public <T> List<T> findAll2byWherePageDesc(Class<T> clazz,String where, int page,int onepagecount) {
		String strSQL = "select * from (select * from " + clazz.getSimpleName() + " where " + where + " order by _id desc " + " limit " + (page-1)*onepagecount + "," + onepagecount +") order by _id asc";
		return findAllBySql(clazz, strSQL);
	}
	
	//结果集再倒序
	public <T> List<T> findAll2byWhereOrderPageDesc(Class<T> clazz,String where,String order, int page,int onepagecount) {
		String strSQL = "select * from (select * from " + clazz.getSimpleName() + " where " + where + " order by " + order + " desc " + " limit " + (page-1)*onepagecount + "," + onepagecount + ") order by " + order + " asc";
		return findAllBySql(clazz, strSQL);
	}
	
	//结果集再倒序
	public <T> List<T> findAll2byWhereOrderPageAsc(Class<T> clazz,String where,String order, int page,int onepagecount) {
		String strSQL = "select * from (select * from " + clazz.getSimpleName() + " where " + where + " order by " + order + " asc " + " limit " + (page-1)*onepagecount + "," + onepagecount + ") order by " + order + " desc";
		return findAllBySql(clazz, strSQL);
	}
	
	
	public <T> List<T> findAllBySql(Class<T> clazz, String strSQL) {
		MQLog.i(TAG, strSQL);
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(strSQL, null);
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
		String strSQL = "select * from " + clazz.getSimpleName() + " order by _id desc";
		return findOnelBySql(clazz, strSQL);
	}

	public <T> T findOnebyWhere(Class<T> clazz, String where) {
		String strSQL = "select * from " + clazz.getSimpleName() + " where " + where;
		return findOnelBySql(clazz, strSQL);
	}

    public <T> T findOnebyWhereDesc(Class<T> clazz,String order, String where) {
        String strSQL = "select * from " + clazz.getSimpleName() + " where " + where + " order by " + order + " desc ";
        return findOnelBySql(clazz, strSQL);
    }

	public <T> T findOnelBySql(Class<T> clazz, String strSQL) {
		MQLog.i(TAG, strSQL);
		Cursor cursor = db.rawQuery(strSQL, null);
		try {
			T data = null;
			if (cursor.moveToFirst()) {
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
        Log.i(TAG,"insert result:" + re);
        return re == -1 ? false : true;
	}

    public boolean insertOrUpdate (Object object) {
        checktable(object.getClass());
        if (findOnebyWhere(object.getClass(),"_id=" + getObjectId(object))==null) {
            return insert(object);
        } else {
            return updata(object);
        }
    }

    public int getObjectId(Object object) {
        checktable(object.getClass());
        ClassInfo cinfo = classchache.get(object.getClass().getSimpleName());
        Field cid  = cinfo.getId();
        try {
            if (cid==null) {
                throw new IllegalStateException(object.getClass().getName() +  " has no 'id' or '_id',can not updata");
            } else {
                return cid.getInt(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;


//        try {
//            Field[] fieds = object.getClass().getDeclaredFields();
//            for (Field field:fieds) {
//                field.setAccessible(true);//允许访问私有字段
//                if (field.getName().equals("_id") || field.getName().equals("id")) {
//                    return field.getInt(object);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return -1;
    }

	public boolean updata(Object object) {
		checktable(object.getClass());
		ClassInfo cinfo = classchache.get(object.getClass().getSimpleName());
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


    /**
     * 把新的obj的数据放到oldObject(原有数据)上，(null或0不存放)
     * @param newObject
     * @param dbObject
     */
    public static void updataObject(Object newObject,Object dbObject) {
        try {
            Field[] fieds = newObject.getClass().getDeclaredFields();
            Field[] fieds2 = dbObject.getClass().getDeclaredFields();
            for (Field field:fieds) {
                field.setAccessible(true);//允许访问私有字段
                for (Field field2:fieds2) {
                    field2.setAccessible(true);
                    try {
                        if (field.getName().equals(field2.getName())) {
                            Object newobj = field.get(newObject);
                            if (newobj==null) {
                                field.set(newObject,field2.get(dbObject));
                                continue;
                            }

                            boolean isNeedupdata = false;
                            if (newobj instanceof Long) {
                                if (((Long)newobj).longValue()==0) {
                                    isNeedupdata=true;
                                }
                            } else if (newobj instanceof Integer) {
                                if (((Integer)newobj).intValue()==0) {
                                    isNeedupdata=true;
                                }
                            } else if (newobj instanceof Double) {
                                if (((Double)newobj).doubleValue()==0) {
                                    isNeedupdata=true;
                                }
                            }


                            if (isNeedupdata) {
                                field.set(newObject, field2.get(dbObject));
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public boolean updata(Object object,String whereClause,String[] whereArgs) {
		checktable(object.getClass());
		int re = this.db.update(object.getClass().getSimpleName(), object2contentvalue(object), whereClause, whereArgs);
		return re==0?false:true;
	}
	
	
	public boolean delete(Object object) {
		checktable(object.getClass());
		ClassInfo cinfo = classchache.get(object.getClass().getSimpleName());
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
		ClassInfo cinfo = classchache.get(cls.getSimpleName());
		try {
			String delsql = "delete from " + cinfo.getTablename() + " where "+ where;MQLog.i(TAG, delsql);
			this.db.execSQL(delsql);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	private boolean checktable(Class cls) {
		if (!tableIsExist(cls)) {
			ClassInfo cinfo = classchache.get(cls.getSimpleName());
			db.execSQL(cinfo.getCreatesql());
			MQLog.i(TAG,cinfo.getCreatesql());
			cinfo.setTableisexist(true);
			return true;
		} 
		return false;
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
		ClassInfo info = classchache.get(cls.getSimpleName());
		if (info ==null) {
			classchache.put(cls.getSimpleName(), new ClassInfo(cls));
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

    public static void printCursor (Cursor cursor) {
        MQLog.i(TAG,"prineCursor:" + cursor);
        if (cursor==null) {
            MQLog.d(TAG,"cursor = null");
        } else {
            StringBuffer sb = new StringBuffer();

            int count = cursor.getColumnCount();
            MQLog.i(TAG,"count:" + count);
//            for (int i = 0; i < count; i++) {
//                //标题
//                sb.append(cursor.getColumnName(i) + "(" + cursor.getType(i) + ")\t");
//                MQLog.i(TAG,"cname:" + cursor.getColumnName(i));
//            }
            while (cursor.moveToNext()) {
                MQLog.i(TAG,"moveNet");
                for (int i = 0; i < count; i++) {
                    sb.append(cursor.getString(i) +"\t");
                }
            }
            String resu = sb.toString();
            if (resu==null || resu.equals("")) {
                MQLog.i(TAG,"cursor have no result!");
            } else {
                MQLog.i(TAG,sb.toString());
            }
        }
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
						} else if (subname.equals("long") || subname.equals("Long")) {
							field.set(result, cursor.getLong(cindex));
						} else if (subname.equals("double") || subname.equals("Double")) {
                            String str = cursor.getString(cindex);
                            double res = 0;
                            try {
                                res = Double.parseDouble(str);
                            } catch (Exception e) {}
                            field.set(result,res);
                        }
					} else if (ctype == Cursor.FIELD_TYPE_INTEGER) {
						if (subname.equals("int")|| subname.equals("Integer")) {
							field.set(result, cursor.getInt(cindex));
						} else if (subname.equals("boolean")|| subname.equals("Boolean")) {
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
			if (keyname.equals("_id")) {
				continue;
			}
			try {
				if (subname.equals("String")) {
					result.put(keyname, (String)field.get(object));
				} else if (subname.equals("int")  || subname.equals("Integer")) {
					result.put(keyname, field.getInt(object));
				} else if (subname.equals("boolean")|| subname.equals("Boolean")) {
					result.put(keyname, field.getBoolean(object));
				} else if (subname.equals("long")|| subname.equals("Long")) {
					if (keyname.equals("_updateTime")){
						result.put(keyname, System.currentTimeMillis()+"");
                        continue;
					}
					if (keyname.equals("_addTime")) {
                        long addtime = System.currentTimeMillis();
                        try{
                            long addtime2 = field.getLong(object);
                            if (addtime2>0) {
                                addtime = addtime2<addtime?addtime2:addtime;
                            }
                        }catch (Exception e){}
						result.put(keyname, addtime+"");
						continue;
					}
					result.put(keyname, ""+field.get(object));
				} else if (subname.equals("float")|| subname.equals("Float")) {
					result.put(keyname, field.getFloat(object));
                } else if (subname.equals("double")|| subname.equals("Double")) {
                    result.put(keyname, field.get(object)+"");
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
		} else if (classname.equals("long") || classname.equals("Long")) {
			type =  "text";
		} else if (classname.equals("int") || classname.equals("Integer")) {
			type = "integer";
		} else if (classname.equals("boolean") || classname.equals("Boolean")) {
			type = "integer";
		} else if (classname.equals("float") || classname.equals("Float")) {
			type = "Float";
        } else if (classname.equals("double") || classname.equals("Double")) {
            type = "text";
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
