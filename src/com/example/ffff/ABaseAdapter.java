package com.example.ffff;


import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

//写字楼里写字间，写字间里程序员。
//程序人员写程序，又拿程序换酒钱。
//酒醒只在网上坐，酒醉还来网下眠。
//酒醉酒醒日复日，网上网下年复年。
//但愿老死电脑间，不愿鞠躬老板前。
//奔驰宝马贵者趣，公交自行程序员。
//别人笑我忒疯癫，我笑自己命太贱。
//不见满街漂亮妹，哪个归得程序员。

/**
 * 动画适配器
 * @author ping
 * @create 2014-9-5 上午8:35:48
 */
public abstract class ABaseAdapter extends BaseAdapter {
	@Override
	public View getView(int i, View view, ViewGroup viewgroup) {
		View itemview = getAnimatorView(i, view, viewgroup);
		
		itemview.clearAnimation();
    	
    	Animation myAnimation_in= AnimationUtils.loadAnimation(itemview.getContext(),R.anim.infolayout_in);    		
		
    	itemview.setAnimation(myAnimation_in);
		myAnimation_in.startNow();
		
		return itemview;
	}
	
	public abstract  View getAnimatorView(int i, View view, ViewGroup viewgroup);
}
