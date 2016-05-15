package com.csx.mobilesafe.domain;

import android.graphics.drawable.Drawable;
/**
 * 应用信息封装
 * @author csx
 *
 */
public class AppInfo {

	public String name;
	public String packageName;
	public Drawable icon;
	
	public boolean isRom;//true表示安装在手机内存
	public boolean isUser;//true表示用户应用

}
