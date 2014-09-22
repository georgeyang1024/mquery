package com.minephone.volley;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Copyright (C) 2010 ideasandroid 演示android多线程下载
 * 欢迎访问http://www.ideasandroid.com 让程序开发不再那么神秘
 * 
 * 单个下载线程
 * 
 * updata by ping
 */
public  class FileDownloadThread extends Thread {
	private static String TAG = "FileDownloadThread";
	private static final int BUFFER_SIZE = 1024 * 10;// 10k缓存
	private String urlString;
	private File file;
	private int startPosition;	//断点续传：startpoint = (int) file.length();
	private int curPosition;
	// 用于标识当前线程是否下载完成
	private boolean finished = false;
	private boolean isstop = false;

	private int downloadSize = 0;
	private int filesize = 0;
	private Handler handler;

	private long posttime;// 2秒更新一次ui

	public FileDownloadThread(String url, File file, int startPosition,
			Handler handler) {
		Log.i(TAG, "url:" + url + " file;" + file.getAbsolutePath()
				+ " startPosition;" + startPosition + " handler:" + handler);
		this.urlString = url;
		this.file = file;
		this.startPosition = startPosition;
		this.curPosition = startPosition;
		this.handler = handler;
	}

	@Override
	public void run() {
		while (true) {
			// 下载完成或设置停止了，就跳出循环
			if (finished || isstop)
				return;
			synchronized (control) {
				if (suspend) {
					try {
						control.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			down();
		}
	}

	private void down() {
		// 重置开始点
		startPosition = curPosition;

		BufferedInputStream bis = null;
		RandomAccessFile fos = null;
		byte[] buf = new byte[BUFFER_SIZE];
		URLConnection con = null;
		URLConnection testcon = null;
		URL url;
		try {
			url = new URL(urlString);

			if (!file.exists()) {
				file.createNewFile();
			}

			con = url.openConnection();

			testcon = url.openConnection();
			filesize = testcon.getContentLength();
			Log.i(TAG, "filesize=" + filesize);
			if (con.getReadTimeout() == 5 || filesize == -1) {
				isstop = true;

				// 网络错误(超时)
				Message msg = new Message();
				msg.what = 2;
				msg.arg1 = 0;
				msg.arg2 = 0;
				handler.sendMessage(msg);
				return;
			}

			con.setAllowUserInteraction(true);
			// 设置当前线程下载的起点，终点
			con.setRequestProperty("Range", "bytes=" + startPosition + "-");

			// filesize = con.getContentLength() +
			// startPosition;//剩余的加上开始的，就是文件总长度

			if (startPosition >= filesize) {
				// 已经下载完成了
				this.finished = true;

				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = 10000;
				msg.arg2 = filesize;
				handler.sendMessage(msg);
				return;
			}

			Log.i(TAG, "con.getContentLength() =" + con.getContentLength()
					+ " filesize;" + filesize + " startPosition;"
					+ startPosition);

			// 使用java中的RandomAccessFile 对文件进行随机读写操作
			fos = new RandomAccessFile(file, "rw");

			// 设置开始写文件的位置
			fos.seek(startPosition);
			bis = new BufferedInputStream(con.getInputStream());
			// 开始循环以流的形式读写文件
			while (curPosition < filesize) {
				if (isstop || suspend) {
					// 停止暂停了
					Log.i(TAG,"isstop:" + isstop);
					Log.i(TAG,"suspend:" + suspend);

					bis.close();
					fos.close();
					return;
				}

				int len = bis.read(buf, 0, BUFFER_SIZE);
				if (len == -1) {
					break;
				}
				fos.write(buf, 0, len);
				curPosition = curPosition + len;
				// 下载中更新界面
				long temptime = System.currentTimeMillis();
				if (temptime - posttime > 2000) {
					posttime = temptime;

					int x = (int) (curPosition * 1.0 / filesize * 10000);
					Log.i(TAG, "curPosition/filesize=" + curPosition + "/"
							+ filesize + "=" + x);
					Message msg = new Message();
					msg.what = 0;
					msg.arg1 = x;// 百分比
					msg.arg2 = filesize;
					handler.sendMessage(msg);
				}
			}
			bis.close();
			fos.close();

			// 下载完成设为true
			this.finished = true;

			// 下载完成
			Message msg = new Message();
			msg.what = 1;
			msg.arg1 = 10000;// 百分比
			msg.arg2 = filesize;
			handler.sendMessage(msg);
		} catch (IOException e) {
			if (!isstop) {
				isstop = true;
				Message msg = new Message();
				msg.what = 3;
				msg.arg1 = 0;
				msg.arg2 = filesize;
				handler.sendMessage(msg);
			}
			e.printStackTrace();
		}
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean isstop() {
		return isstop;
	}

	public void stop_download() {
		isstop = true;
	}

	private boolean suspend = false; // 暂停

	private String control = ""; // 只是需要一个对象而已，这个对象没有实际意义

	// 是否暂停
	public void setSuspend(boolean suspend) {
		if (!suspend) {
			synchronized (control) {
				control.notifyAll();
			}
		}
		this.suspend = suspend;
	}

	/**
	 * 暂停线程
	 * 
	 * @return
	 */
	public boolean isSuspend() {
		return this.suspend;
	}
}
