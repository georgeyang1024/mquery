package com.example.ffff;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Environment;
import android.util.Base64;
import android.view.Display;
import android.view.View;

public class PhotoEditor {

	/**
	 * 从路径获取压缩的bitmap
	 * 
	 * @param srcPath
	 * @return
	 */
	public static Bitmap compressImage(String srcPath) {

		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		int hh = 800;// 这里设置高度为800f
		int ww = 480;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据高度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// System.out.println(srcPath);

		return CompressionQuality(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 质量压缩
	 * 
	 * @author ping 2014-3-31 上午9:38:58
	 * @param bitmap
	 * @return
	 */
	public static Bitmap CompressionQuality(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 90, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于50kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片

		return bitmap;
	}

	/**
	 * 从路径获取原始的Bitmap
	 * 
	 * @param path
	 * @return
	 * @throws
	 */
	public static Bitmap getbitmap(String path) {
		// BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		// newOpts.inJustDecodeBounds = true;
		// Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);
		Bitmap bitmap = null;
		FileInputStream fis;
		try {
			fis = new FileInputStream(path);
			bitmap = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

//	/**
//	 * 从bitmap获取压缩的bitmap
//	 * 
//	 * @author ping 2014-3-31 上午9:41:38
//	 * @param image
//	 * @return
//	 */
//	public static Bitmap compressImage(Bitmap image) {
//		L.showlog("压缩图片：bitmap" + image.toString());
//
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
//			baos.reset();// 重置baos即清空baos
//			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
//		}
//		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//		BitmapFactory.Options newOpts = new BitmapFactory.Options();
//		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
//		newOpts.inJustDecodeBounds = true;
//		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//		newOpts.inJustDecodeBounds = false;
//		int w = newOpts.outWidth;
//		int h = newOpts.outHeight;
//		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//		float hh = 800f;// 这里设置高度为800f
//		float ww = 480f;// 这里设置宽度为480f
//		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//		int be = 1;// be=1表示不缩放
//		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
//			be = (int) (newOpts.outWidth / ww);
//		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
//			be = (int) (newOpts.outHeight / hh);
//		}
//		if (be <= 0)
//			be = 1;
//		newOpts.inSampleSize = be;// 设置缩放比例
//		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//		isBm = new ByteArrayInputStream(baos.toByteArray());
//		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//
//		return CompressionQuality(bitmap);// 压缩好比例大小后再进行质量压缩
//	}

	/**
	 * 将Bitmap转换成Base64字符串
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String bitmaptoString(Bitmap bitmap) {
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;
	}

	/**
	 * 保存图片到根目录+文件名
	 * 
	 * @param bmp
	 * @param filename
	 * @return
	 */
	public static boolean saveBitmap2file(Bitmap bmp, String filename) {
		return saveBitmap2file(bmp,
				new File(Environment.getExternalStorageDirectory(), filename));
	}

	/**
	 * 保存图片到文件
	 * 
	 * @param bmp
	 * @param filename
	 * @return
	 */
	public static boolean saveBitmap2file(Bitmap bmp, File file) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			CompressFormat format = Bitmap.CompressFormat.PNG;// 格式化的格式
			int quality = 100;// 质量
			OutputStream stream = null;
			try {
				stream = new FileOutputStream(file);
				if (bmp.compress(format, quality, stream)) {
					stream.flush();
					stream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
	/**
	 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
	* 官网：获取压缩后的图片
	* @param res
	* @param resId
	* @param reqWidth            所需图片压缩尺寸最小宽度
	* @param reqHeight           所需图片压缩尺寸最小高度
	* @return
	*/
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {
	   
	    // 首先不加载图片,仅获取图片尺寸
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    // 当inJustDecodeBounds设为true时,不会加载图片仅获取图片尺寸信息
	    options.inJustDecodeBounds = true;
	    // 此时仅会将图片信息会保存至options对象内,decode方法不会返回bitmap对象
	    BitmapFactory.decodeResource(res, resId, options);

	    // 计算压缩比例,如inSampleSize=4时,图片会压缩成原图的1/4
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // 当inJustDecodeBounds设为false时,BitmapFactory.decode...就会返回图片对象了
	    options. inJustDecodeBounds = false;
	    // 利用计算的比例值获取压缩后的图片对象
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	/**
	* 官网：获取压缩后的图片
	* @param res
	* @param resId
	* @param reqWidth            所需图片压缩尺寸最小宽度
	* @param reqHeight           所需图片压缩尺寸最小高度
	* @return
	*/
	public static Bitmap decodeSampledBitmapFromFile(String filepath,int reqWidth, int reqHeight) {
	   
	    // 首先不加载图片,仅获取图片尺寸
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    // 当inJustDecodeBounds设为true时,不会加载图片仅获取图片尺寸信息
	    options.inJustDecodeBounds = true;
	    // 此时仅会将图片信息会保存至options对象内,decode方法不会返回bitmap对象
	    BitmapFactory.decodeFile(filepath, options);

	    // 计算压缩比例,如inSampleSize=4时,图片会压缩成原图的1/4
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // 当inJustDecodeBounds设为false时,BitmapFactory.decode...就会返回图片对象了
	    options. inJustDecodeBounds = false;
	    // 利用计算的比例值获取压缩后的图片对象
	    return BitmapFactory.decodeFile(filepath, options);
	}
	
	
	public static Bitmap decodeSampledBitmapFromBitmap(Bitmap bitmap,int reqWidth, int reqHeight) {
		
		  ByteArrayOutputStream baos = new ByteArrayOutputStream();
		  bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		  byte[] data = baos.toByteArray();
		   
	    // 首先不加载图片,仅获取图片尺寸
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    // 当inJustDecodeBounds设为true时,不会加载图片仅获取图片尺寸信息
	    options.inJustDecodeBounds = true;
	    // 此时仅会将图片信息会保存至options对象内,decode方法不会返回bitmap对象
	    BitmapFactory.decodeByteArray(data, 0, data.length,options);

	    // 计算压缩比例,如inSampleSize=4时,图片会压缩成原图的1/4
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//	    options.inSampleSize = 10;
	    
	    // 当inJustDecodeBounds设为false时,BitmapFactory.decode...就会返回图片对象了
	    options. inJustDecodeBounds = false;
	    // 利用计算的比例值获取压缩后的图片对象
	    return BitmapFactory.decodeByteArray(data, 0, data.length,options);
	}
	
	
	/**
	* 计算压缩比例值
	* @param options       解析图片的配置信息
	* @param reqWidth            所需图片压缩尺寸最小宽度
	* @param reqHeight           所需图片压缩尺寸最小高度
	* @return
	*/
	public static int calculateInSampleSize(BitmapFactory.Options options,
	             int reqWidth, int reqHeight) {
	       // 保存图片原宽高值
	       final int height = options. outHeight;
	       final int width = options. outWidth;
	       // 初始化压缩比例为1
	       int inSampleSize = 1;

	       // 当图片宽高值任何一个大于所需压缩图片宽高值时,进入循环计算系统
	       if (height > reqHeight || width > reqWidth) {

	             final int halfHeight = height / 2;
	             final int halfWidth = width / 2;

	             // 压缩比例值每次循环两倍增加,
	             // 直到原图宽高值的一半除以压缩值后都~大于所需宽高值为止
	             while ((halfHeight / inSampleSize) >= reqHeight
	                        && (halfWidth / inSampleSize) >= reqWidth) {
	                  inSampleSize *= 2;
	            }
	      }

	       return inSampleSize;
	}
}
