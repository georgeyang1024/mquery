package com.example.ffff;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.volley.VolleyError;
import com.minephone.volley.FileDownloadThread;
import com.minephone.volley.FileUpload;
import com.minephone.volley.ImagePiece;
import com.minephone.volley.ImageSplitter;
import com.minephone.volley.MQuery;
import com.minephone.volley.NetAccess;
import com.minephone.volley.ObjectCache;
import com.minephone.volley.NetAccess.NetAccessListener;

import android.os.Bundle;
import android.os.Debug;
import android.R.anim;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity implements NetAccessListener {
	MMLoadingView loadview;
	MQuery mq;
	NetAccess net;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		loadview = (MMLoadingView) findViewById(R.id.mMLoadingView1);
		mq = new MQuery(this);
		mq.request().dealFailRequest(this);
		
		
		
		
		mq.request().byPost("baidu.com", this);
		mq.request().byCachePost("baidu.com", this);
		//直到成功的请求
		mq.request().byUntilSuccessPost("baidu.com", this);
		//处理上面未成功的请求(全部失败的)
		mq.request().dealFailRequest(this);
		
		AnimationDrawable ad = new AnimationDrawable();
		ad.addFrame(getResources().getDrawable(R.drawable.ic_launcher), 1500);// 显示的时间
		ad.addFrame(getResources().getDrawable(R.drawable.bg_error_image), 1500);// 显示的时间
		ad.setOneShot(false);// 循环播放
		loadview.setBackgroundDrawable(ad);// 设置动画
		ad.start();// 开始播放
		// loadview.setAnimation(null);
		// loadview.startAnimation(/)
		
		
		
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				net = mq.request();
				net.byUntilSuccessGet("http://192.168.191.1:8080/apperror/setcookie",
						MainActivity.this);
			}
		});

		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				net = mq.request();
				net.byGet("http://192.168.191.1:8080/apperror/delcookie",
						MainActivity.this);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		loadview.stop();
		super.onDestroy();
	}

	@Override
	public void onAccessComplete(boolean success, String object,
			VolleyError error, String flag) {
		if (net != null) {
			Log.i("test", "rewcookies:" + net.getRecookies());
			Log.i("test", "rewheaders:" + net.getReHeaders());
		} else {
			Log.i("test", "net var is null");
		}
	}
	
}
