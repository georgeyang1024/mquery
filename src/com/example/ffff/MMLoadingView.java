package com.example.ffff;

import java.util.ArrayList;
import com.minephone.volley.ImagePiece;
import com.minephone.volley.ImageSplitter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 加载图
 * @author ping
 * @create 2014-9-1 下午3:06:42
 */
public class MMLoadingView extends ImageView {
	private boolean canloop;
	private ArrayList<ImagePiece> mlist;
	private Bitmap currbitmap;
	
	public MMLoadingView(Context paramContext) {
		super(paramContext);
	}

	public MMLoadingView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public MMLoadingView(Context paramContext, AttributeSet paramAttributeSet,int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}
	
	@SuppressLint("NewApi")
	public void init(Bitmap bitmap,int  xcount,int ycount) {
		mlist =(ArrayList<ImagePiece>) ImageSplitter.split(bitmap, xcount, ycount);
		
		
	    // 计算缩放比例
	    float scaleWidth = ((float) MMLoadingView.this.getMeasuredWidth()) / mlist.get(0).bitmap.getWidth() ;
	    float scaleHeight =  ((float) MMLoadingView.this.getMeasuredHeight())/mlist.get(0).bitmap.getHeight() ;
	    // 取得想要缩放的matrix参数
	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleWidth, scaleHeight);
	    
		for (int i = 0; i < mlist.size(); i++) {//重设大小
			mlist.get(i).bitmap =	Bitmap.createBitmap(mlist.get(i).bitmap, 0, 0, mlist.get(i).bitmap.getWidth(), mlist.get(i).bitmap.getHeight(), matrix, true);
		}
	}
	
	public void start () throws IllegalAccessException {
		if (mlist==null) throw new IllegalAccessException("MMLoadingView had not init");
		canloop = true;
		try {
			mloopthread.start();
		} catch (Exception e) {}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (currbitmap!=null) {
			canvas.drawBitmap(currbitmap, 0, 0, null);			
		} else {
			super.onDraw(canvas);
		}
	}

	public void stop() {
		canloop = false;
	}
	
	Thread  mloopthread = new Thread() {
		@Override
		public void run() {
			while(canloop) {
				for (ImagePiece ip : mlist) {
					currbitmap = ip.bitmap;
					MMLoadingView.this.postInvalidate();
					try {
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			super.run();
		}
	};
}
