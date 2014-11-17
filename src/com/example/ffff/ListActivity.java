package com.example.ffff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.example.view.XListView;
import com.example.view.XListView.IXListViewListener;
import com.minephone.network.MQuery;
import com.minephone.network.NetAccess.NetAccessListener;

import entity.MPic;

/**
 * list
 * @author ping
 * @create 2014-10-10 下午7:47:14
 */
public class ListActivity extends Activity implements NetAccessListener, IXListViewListener {
	private Madapter adapter;
	private MQuery mq ;
	private int page;
	private List<MPic> list;
	private XListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
        mq = new MQuery(this);
        listView = (XListView) mq.id(R.id.listview).getView();
        listView.setXListViewListener(this);
        adapter = new Madapter(this);
        
		mq.id(R.id.listview).adapter(adapter);
		
		//获取数据
		onRefresh();
	}

	@Override
	public void onAccessComplete(boolean success, String object,
			VolleyError error, String flag) {
		listView.stopRefresh();
		listView.stopLoadMore();
		if (object != null) {
			JSONObject json=JSONObject.parseObject(object);
			
			if (flag.equals("get")) {
				list = JSONArray.parseArray(json.getString("data"), MPic.class);
			} else if (flag.equals("add")){
				list.addAll(JSONArray.parseArray(json.getString("data"), MPic.class));
			}
			adapter.setData(list);
		} else {
			Toast.makeText(this, "网络连接失败，请重试!", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onRefresh() {
		page = 1;
		Map<String, String> params= new HashMap<String,String>();
		params.put("page", page+"");
		params.put("onepagecount", "200");//一页200个数据
		mq.request().setFlag("get").setParams(params).byGet("http://ptool.aliapp.com/listimages", this);
	}

	@Override
	public void onLoadMore() {
		page++;
		Map<String, String> params= new HashMap<String,String>();
		params.put("page", page+"");
		params.put("onepagecount", "200");
		mq.request().setFlag("add").setParams(params).byGet("http://ptool.aliapp.com/listimages", this);
	}
}



