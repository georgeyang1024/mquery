package com.example.ffff;

import java.util.List;

import com.minephone.network.MQuery;

import entity.MPic;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author ping
 * @create 2014-10-10 下午7:56:07
 */
public class Madapter extends ABaseAdapter {
	List<MPic> list;
	Activity mactivity;
	
	
	public Madapter (Activity act) {
		mactivity = act;
	}
	
	@Override
	public int getCount() {
		return list==null?0:list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void setData(List<MPic> newlist) {
		list = newlist;
		notifyDataSetChanged();
	}
	
	@Override
	public View getAnimatorView(final int i, View convertView, ViewGroup viewgroup) {
		ViewHolder holder;
		if (convertView==null) {
			convertView = LayoutInflater.from(mactivity).inflate(R.layout.item_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		MQuery mq = new MQuery(convertView,true);
		
		MPic poic = list.get(i);
		mq.id(R.id.imageView1).image(list.get(i).getThumbnail_url());
		mq.id(holder.tv).text("标题:" +poic.getAbs());
		mq.id(R.id.button1).clicked(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(mactivity,"内容:" + list.get(i).getDesc(),Toast.LENGTH_LONG).show();
			}
		});
		
		return convertView;
	}

	
	private static class ViewHolder {
		TextView tv;
		public ViewHolder (View convertView) {
			tv = (TextView) convertView.findViewById(R.id.textView1);
		}
		
	}
}
