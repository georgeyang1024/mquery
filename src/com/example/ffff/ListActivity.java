package com.example.ffff;

import java.util.ArrayList;
import java.util.List;

import com.minephone.network.MQuery;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 
 * @author ping
 * @create 2014-10-10 下午7:47:14
 */
public class ListActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		ListView listview = (ListView) findViewById(R.id.listview);
		Adapter adapter = new Madapter(this);
		MQuery mq =new MQuery(this);
		mq.id(R.id.listview).adapter(adapter);		
	}
}
