package com.csx.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * 进程信息封装
 * 
 * @author csx
 * 
 */
public class ProcessInfo {

	public String name;
	public String packageName;
	public Drawable icon;
	public long memory;

	public boolean isUser;// true表示用户进程
	public boolean isChecked;//表示当前item是否被勾选

}
