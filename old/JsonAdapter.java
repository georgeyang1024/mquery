package com.minephone.network;

import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.minephone.network.NetAccess.NetAccessListener;

/**
 *  json适配器，免去写adapter
 * @author ping
 * 2014-4-11 下午9:54:47

how to use ?

			HashMap<String, Integer> field = new HashMap<String, Integer>();
			field.put("googpic", R.id.image);
			field.put("goodname", R.id.name);
			field.put("goodspec", R.id.spec);
			field.put("goodprice", R.id.price);

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("page", 1);
			
			JsonAdapter adapter = new JsonAdapter(mactivity,R.layout.item_lbsstore, Urls.LBSSTORE, "data", params,field,null); 
			listview.setAdapter(adapter);
			//第一页
			adapter.refresh(true);
			//加载下一页
			HashMap<String, Object> param2 = new HashMap<String, Object>();
			param2.put("page", 2);
			adapter.setparams(param2);
			adapter.refresh(false);


 */
public class JsonAdapter extends BaseAdapter implements NetAccessListener  {
	private Context mcontext;
	private JSONArray data;
	private Map<String, Integer> field;
	private String datakey;
	private NetAccessListener callback;
	private int layoutid;
	private Map<String, String> params;
	private String url;
	
	public JSONArray getJsonArray (){
		return data;
	}
	
	public JsonAdapter(Context context,int layoutid,String url,String datakey,Map<String, String> params,Map<String,Integer> field,NetAccessListener callback) {
		mcontext = context;
		this.layoutid = layoutid;
		this.datakey  =datakey;
		this.field  = field;
		this.url  =url;
		this.callback = callback;
	}
	
	/**
	 * 重新设置参数，可用于追加下一页数据
	 * @author ping
	 * 2014-4-15 下午3:31:09
	 * @param params
	 */
	public void setparams (Map<String, String> params) {
		this.params = params;
	}
	
	/**
	 * 刷新数据
	 * @author ping
	 * 2014-4-15 下午3:46:54
	 * @param removeoldata是否移除之前数据
	 */
	public void refresh (boolean removeoldata) {
		if (removeoldata) {			
			NetAccess.request(mcontext).setFlag("JsonAdapter_get").byGet(url + NetAccess.getParamStr(params), this);
		} else {
			NetAccess.request(mcontext).setFlag("JsonAdapter_add").byGet(url + NetAccess.getParamStr(params), this);
		}
	}

	@Override
	public int getCount() {
		return data==null?0:data.size();
	}

	@Override
	public Object getItem(int index) {
		return data==null?null:data.get(index);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int index, View view, ViewGroup viewgroup) {
		ViewHolder holder = null;
		if(view == null){
			view = LayoutInflater.from(mcontext).inflate(layoutid, null);
			holder = new ViewHolder();
			holder.childviews = new View[field.size()];
			int count=0;
			for (String key : field.keySet()) {
				int id = field.get(key);
				holder.childviews[count] = view.findViewById(id) ;
				holder.childviews[count].setTag(key);
				count++;
			}
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		for (View childview:holder.childviews) {
			String key = (String) childview.getTag();
			String jsonvalue = data.getJSONObject(index).getString(key);
			if (childview instanceof TextView) {
				((TextView) childview).setText(jsonvalue);
			} else if (childview instanceof ImageView) {
				NetAccess.image((ImageView)childview, jsonvalue);
			} else {
				//key的id不属于TextView，也不属于ImageView
			}
		}
		return view;
	}

	@Override
	public void onAccessComplete(boolean success, String object,VolleyError error, String flag) {
		if (success) {
			JSONObject jo = JSONObject.parseObject(object);
			if (jo != null) {
				if (flag.equals("JsonAdapter_get")) {
					if (datakey==null) {
						data = JSONArray.parseArray(jo.toJSONString());
					} else {
						data = jo.getJSONArray(datakey);
					}
				} else if (flag.equals("JsonAdapter_add")) {
					if (datakey==null) {
						data.addAll(JSONArray.parseArray(jo.toJSONString()));
					} else {
						data.addAll(data = jo.getJSONArray(datakey));
					}
				} else {
					//unkonw flag
				}
				
				super.notifyDataSetChanged();
			}
		}
		
		if (callback!=null) {
			callback.onAccessComplete(success, object, error, flag);
		}
	}
	
	private static class ViewHolder  {
		View[] childviews;
	}
}
