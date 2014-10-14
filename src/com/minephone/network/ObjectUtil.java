package com.minephone.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.util.Base64;

public class ObjectUtil {
	/**
	 * 对象转字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		String str = null;
		try {
			str = Base64.encodeToString(toByteArray(obj), Base64.DEFAULT);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 对象转字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static Object toObject(String base64str) {
		Object obj = null;
		try {			
			obj = toObject(Base64.decode(base64str, Base64.DEFAULT));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 对象转数组
	 * 
	 * @param obj
	 * @return
	 */
	public static byte[] toByteArray(Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			bos.close();
		 } catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 数组转对象
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object toObject(byte[] bytes) {
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = ois.readObject();
			ois.close();
			bis.close();
		 } catch (Exception ex) {        
	            ex.printStackTrace();   
		 }
		return obj;
	}
}
