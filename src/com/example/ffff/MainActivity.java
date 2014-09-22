package com.example.ffff;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.minephone.volley.MQuery;
import com.minephone.volley.NetAccess.NetAccessListener;

public class MainActivity extends Activity implements NetAccessListener, OnClickListener {
	MQuery mq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mq = new MQuery(this);
		mq.request().dealFailRequest(this);//byUntilSuccessGet
		
		mq.id(R.id.button1).clicked(this);
		mq.id(R.id.button2).clicked(this);
		mq.id(R.id.button3).clicked(this);
		mq.id(R.id.button4).clicked(this);
		mq.id(R.id.button5).clicked(this);
		mq.id(R.id.button6).clicked(this);
		mq.id(R.id.button7).clicked(this);
		mq.id(R.id.button8).clicked(this);
		mq.id(R.id.button9).clicked(this);
		mq.id(R.id.button10).longclicked(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				Toast.makeText(getApplicationContext(), "长按！", 1000).show();
				return false;
			}
		});
	}

	@Override
	public void onAccessComplete(boolean success, String object,
			VolleyError error, String flag) {
		Toast.makeText(getApplicationContext(), "请求回调:success=" + success + " object=" + object, 1000).show();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button1:
//			Toast.makeText(getApplicationContext(), "path:" + NetAccess.CachePath, 1000).show();
			mq.id(R.id.textView1).text("新内容!");
			break;
		case R.id.button2:
			mq.id(R.id.imageView1).image("http://www.eoeandroid.com/uc_server/avatar.php?uid=1178907&size=middle");
			break;
		case R.id.button3:
			mq.id(R.id.checkBox1).checked(true);
			break;
		case R.id.button4:
			mq.id(R.id.textView1).visibility(View.GONE);
			break;
		case R.id.button5:
			//不能取消(showDialog(false))
			mq.request().showDialog(false).byGet("http://gc.ditu.aliyun.com/geocoding?a=%E8%8B%8F%E5%B7%9E%E5%B8%82", this);
			break;
		case R.id.button6:
			//两次回调
//			HashMap<String, String> params = new HashMap<String, String>();
//			params.put("a", "%E8%8B%8F%E5%B7%9E%E5%B8%82");
//			mq.request().byCachePost("http://gc.ditu.aliyun.com/geocoding", this);
			mq.request().byCacheGet("http://gc.ditu.aliyun.com/geocoding?a=%E8%8B%8F%E5%B7%9E%E5%B8%82", this);
			break;
		case R.id.button7:
			//若请求失败，存入数据库，使用mq.request().dealFailRequest(this);方法再次发起请求(微信有这个功能)
			mq.request().byUntilSuccessGet("http://gc.ditu.aliyun.com/geocoding?a=%E8%8B%8F%E5%B7%9E%E5%B8%82", this);
			break;
		case R.id.button8:
			Test t = new Test();
			t.setId( (int)(Math.random()*1000));
			t.setContent(Math.random()+"#");
			if (mq.db().insert(t)){
				Toast.makeText(getApplicationContext(), "success", 1000).show();
			}
			break;
		case R.id.button9:
			ArrayList<Test> list = (ArrayList<Test>) mq.db().findAll(Test.class);
			Toast.makeText(getApplicationContext(), "数据:" + JSONObject.toJSONString(list), 1000).show();
			break;
		default:
			break;
		}
	}
}


