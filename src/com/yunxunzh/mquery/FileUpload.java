package com.yunxunzh.mquery;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import android.os.AsyncTask;
import android.util.Log;

/**
 * 文件上传
 * 
 * 
 				HashMap<String, String> params = new HashMap<String, String>();
				params.put("page", "1");
				params.put("name", "2");

				HashMap<String, File> files = new HashMap<String, File>();
				files.put("1.apk","/mnt/sdcard/1.apk"));
				files.put("name", new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"6.jpg"));
				
				new fileupload("http://192.168.191.1:8081/uploadtest/servlet/upload2",params, files,MainActivity.this).execute("");
 
 * @author ping
 *
 */


public class FileUpload extends AsyncTask<String, Float, String> {
	public  static final String TAG = "FileUpload";
	String actionUrl;
	Map<String, String> params;
	Map<String, File> files;
	UploadListener listener;
	
	public interface UploadListener {
		public void onProgressUpdate(float progress);
		public void onUploadEnd(boolean success,String object);
	}
	
	public FileUpload(String actionUrl, Map<String, String> params,Map<String, File> files,UploadListener listener) {
		this.actionUrl = actionUrl;
		this.params = params;
		this.files = files;
		this.listener = listener;
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		String reslut = null;
		try {
			String BOUNDARY = java.util.UUID.randomUUID().toString();  
		     String PREFIX = "--", LINEND = "\r\n";  
		     String MULTIPART_FROM_DATA = "multipart/form-data";  
		     String CHARSET = "UTF-8";  
		     URL uri = new URL(actionUrl);  
		     HttpURLConnection conn = (HttpURLConnection) uri.openConnection();  
		     conn.setReadTimeout(5 * 1000);  
		     conn.setDoInput(true);// 允许输入  
		     conn.setDoOutput(true);// 允许输出  
		     conn.setUseCaches(false);  
		     conn.setRequestMethod("POST"); // Post方式  
		     conn.setRequestProperty("connection", "keep-alive");  
		     conn.setRequestProperty("Charsert", "UTF-8");  
		     conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA  + ";boundary=" + BOUNDARY);  
		     // 首先组拼文本类型的参数  
		     StringBuilder sb = new StringBuilder();  
		     for (Map.Entry<String, String> entry : params.entrySet()) {  
		         sb.append(PREFIX);  
		         sb.append(BOUNDARY);  
		         sb.append(LINEND);  
		         sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);  
		         sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);  
		         sb.append("Content-Transfer-Encoding: 8bit" + LINEND);  
		         sb.append(LINEND);  
		         sb.append(entry.getValue());  
		         sb.append(LINEND);  
		     }  
		     DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());  
		     outStream.write(sb.toString().getBytes());  
		     
		     float donecount = 0;//out完成的数量
		     // 发送文件数据  
		     if (files != null)  
		    	 for (String key : files.keySet()) {
		             StringBuilder sb1 = new StringBuilder();  
		             sb1.append(PREFIX);  
		             sb1.append(BOUNDARY);  
		             sb1.append(LINEND);  
		             sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""   + key + "\"" + LINEND);  
		             sb1.append("Content-Type: multipart/form-data; charset="  + CHARSET + LINEND);  
		             sb1.append(LINEND);
		             outStream.write(sb1.toString().getBytes());  
		             File valuefile = files.get(key);
		             InputStream is = new FileInputStream(valuefile);  
		             byte[] buffer = new byte[1024];  
		             int len = 0;
		             while ((len = is.read(buffer)) != -1) {
		                 outStream.write(buffer, 0, len);
		             }
		             donecount++;
		             publishProgress(donecount/files.size()*80f);//上传占80%
		             is.close();  
		             outStream.write(LINEND.getBytes());  
		         }
		     // 请求结束标志  
		     byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();  
		     outStream.write(end_data);  
		     outStream.flush();  
		     // 得到响应码  
//		     success = conn.getResponseCode()==200;  
		     
             publishProgress(90f);//flush,90%

		     InputStream in = conn.getInputStream();  
		     InputStreamReader isReader = new InputStreamReader(in);  
		     BufferedReader bufReader = new BufferedReader(isReader);  
		     String line = null;  
		     reslut = "";  
             publishProgress(95f);//读取95%
		     while ((line = bufReader.readLine()) != null)  
		    	 reslut += line;  
		     outStream.close();

             publishProgress(100f);//完成100%
		     
		     bufReader.close();
		     isReader.close();
		     in.close();
		     conn.disconnect();  
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return reslut;
	}
	
	@Override
	protected void onPreExecute() {
		//onPreExecute方法用于在执行后台任务前做一些UI操作  
	}
	
	@Override
	protected void onCancelled() {
		//取消操作
		if (listener!=null) {
				listener.onUploadEnd(false, null);
		}
	}

	@Override
	protected void onPostExecute(String result) {
		//onPostExecute方法用于在执行完后台任务后更新UI,显示结果  
		if (listener!=null) {
			if (result==null) {
				listener.onUploadEnd(false, null);
			} else {
				listener.onUploadEnd(true,  result);
			}
 		}
	}
	

	@Override
	protected void onProgressUpdate(Float... values) {
		 //onProgressUpdate方法用于更新进度信息  
		if (listener!=null) {
			listener.onProgressUpdate(values[0]);
		}
	}

 
	
}
