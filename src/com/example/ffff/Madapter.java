package com.example.ffff;

import java.util.List;

import com.minephone.network.MQuery;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 
 * @author ping
 * @create 2014-10-10 下午7:56:07
 */
public class Madapter extends ABaseAdapter {
	List<String> list;
	Activity mactivity;
	
	
	public Madapter (Activity act) {
		mactivity = act;
	}
	
	@Override
	public int getCount() {
		return 500;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getAnimatorView(int i, View convertView, ViewGroup viewgroup) {
		ViewHolder holder;
		if (convertView==null) {
			convertView = LayoutInflater.from(mactivity).inflate(R.layout.item_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		MQuery mq = new MQuery(convertView);
		mq.id(holder.tv).text("haha:" + i);
		
		return convertView;
	}

	
	private static class ViewHolder {
		TextView tv;
		public ViewHolder (View convertView) {
			tv = (TextView) convertView.findViewById(R.id.textView1);
		}
		
	}
}
