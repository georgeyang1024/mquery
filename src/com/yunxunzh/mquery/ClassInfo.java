package com.yunxunzh.mquery;

import java.lang.reflect.Field;


public class ClassInfo {
	private Class _class;
	private boolean tableisexist;
	private String Tablename;//simplename
	private String createsql;
	private Field id;
	private Field[] fields;
//	private int[] ColumnIndexs;//数据库中的索引
//	private String[] DBFiedName;//数据库中的名字
	
	
	public ClassInfo (Class cls) {
		this._class = cls;
		this.Tablename = _class.getSimpleName();
		this.fields = cls.getDeclaredFields();
		
		StringBuffer sb = new StringBuffer("create table if not exists " + cls.getSimpleName() + " (");
		for (Field field : fields) {//枚举字段
			field.setAccessible(true);//允许访问私有字段
			String dbfield = Mdb.getDBFiedName(field);// 字段名称 / field.getName();//字段名
			String dbtype = Mdb.getfiedtype(field);
			if (dbfield.equals("_id")) {
				this.id  = field;
				if (field.getType().getSimpleName().equals("int")) {
					sb.append("_id " + dbtype + " PRIMARY KEY,");//Autoincrement
				} else {
					sb.append("_id " + dbtype + ",");
				}
			} else {
				sb.append(dbfield + " " + dbtype + ",");
			}
		}
		String sql = sb.toString();
		this.createsql = sql.substring(0, sql.length() - 1) + ")";
	}

	

	public String getCreatesql() {
		return createsql;
	}



	public void setCreatesql(String createsql) {
		this.createsql = createsql;
	}



	public Class get_class() {
		return _class;
	}


	public void set_class(Class _class) {
		this._class = _class;
	}


	public boolean isTableisexist() {
		return tableisexist;
	}


	public void setTableisexist(boolean tableisexist) {
		this.tableisexist = tableisexist;
	}


	public String getTablename() {
		return Tablename;
	}


	public void setTablename(String tablename) {
		Tablename = tablename;
	}


	public Field getId() {
		return id;
	}


	public void setId(Field id) {
		this.id = id;
	}


	public Field[] getFields() {
		return fields;
	}


	public void setFields(Field[] fields) {
		this.fields = fields;
	}
	
}
