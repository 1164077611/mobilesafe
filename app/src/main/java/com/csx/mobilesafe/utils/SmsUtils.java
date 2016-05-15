package com.csx.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.BitSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import com.csx.mobilesafe.domain.Sms;

/**
 * 短信备份的工具类
 * 
 * 权限: <uses-permission android:name="android.permission.READ_SMS" />
 * <uses-permission android:name="android.permission.WRITE_SMS" />
 * 
 * @author csx
 * 
 */
public class SmsUtils {

	public static void smsBackup(Context ctx, File output, SmsCallback callback) {
		// 从系统短信数据库读取短信内容
		// 将短信序列化到xml文件中
		// ProgressDialog和进度相关的方法可以在子线程运行
		try {
			// address:来电号码
			// date:短信时间
			// read:是否已读, 1表示已读, 0表示未读
			// type:标记短信类型, 1表示收到, 2表示发出
			// body:短信内容
			Cursor cursor = ctx.getContentResolver().query(
					Uri.parse("content://sms"),
					new String[] { "address", "date", "type", "body" }, null,
					null, null);

			//dialog.setMax(cursor.getCount());// 将短信数量设置为进度最大值
			Integer count=cursor.getCount();
			callback.preSmsBackup(count);//回调短信总数

			XmlSerializer xml = Xml.newSerializer();
			xml.setOutput(new FileOutputStream(output), "utf-8");
			xml.startDocument("utf-8", false);
			xml.startTag(null, "smss");
			xml.startTag(null, "count");
			xml.text(count.toString());
			xml.endTag(null,"count");
			int progress = 0;
			while (cursor.moveToNext()) {
				xml.startTag(null, "sms");

				xml.startTag(null, "address");
				String address = cursor.getString(cursor
						.getColumnIndex("address"));
				xml.text(address);
				xml.endTag(null, "address");

				xml.startTag(null, "date");
				String date = cursor.getString(cursor.getColumnIndex("date"));
				xml.text(date);
				xml.endTag(null, "date");

				xml.startTag(null, "type");
				String type = cursor.getString(cursor.getColumnIndex("type"));
				xml.text(type);
				xml.endTag(null, "type");

				xml.startTag(null, "body");
				//可能会有乱码问题需要处理，如果出现乱码会导致备份师表。
				try {
					String body = cursor.getString(1);
					xml.text(body);
					System.out.println(body);
				} catch (Exception e1) {
					e1.printStackTrace();
					xml.text("短信读取失败");
				}
				xml.endTag(null, "body");

				xml.endTag(null, "sms");

				progress++;
				//dialog.setProgress(progress);// 更新进度
				callback.onSmsBackup(progress);//回调更新进度
				
				Thread.sleep(500);//模拟耗时操作
			}

			xml.endTag(null, "smss");
			xml.endDocument();

			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//短信备份的回调接口
	public interface SmsCallback {
		//备份前, count表示短信总数
		public void preSmsBackup(int count);
		//正在备份, progress备份进度
		public void onSmsBackup(int progress);
	}

	public interface RestoreSmsCallBack{
		public void beforeSmsRestore(int size);
		public void onSmsRestore(int progress);
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static boolean restoreSms(Context context,RestoreSmsCallBack callback){
		//判断 是否备份文件存在 读取sd卡的 文件
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				System.out.println("开始解析");
				XmlPullParser xp = Xml.newPullParser();
				File file = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/sms66.xml");
				//初始化
				xp.setInput(new FileInputStream(file), "utf-8");

				//获取当前节点的事件类型
				int type = xp.getEventType();
				Sms sms=null;
				ArrayList<Sms> smses=null;
				while(type != XmlPullParser.END_DOCUMENT){
					switch (type) {
						case XmlPullParser.START_TAG:
							if("smss".equals(xp.getName())){
								smses=new ArrayList<Sms>();
							}else if("count".equals(xp.getName())){
								String count=xp.nextText();
								int countInt=Integer.parseInt(count);
								callback.beforeSmsRestore(countInt);
								System.out.println("count:+"+count);
							}else if("sms".equals(xp.getName())){
								sms=new Sms();
							}
							if("address".equals(xp.getName())){
								String address=xp.nextText();
								sms.setAddress(address);
								System.out.println("address:+"+address);
							}
							else if("date".equals(xp.getName())){
								String date=xp.nextText();
								sms.setDate(Long.parseLong(date));
								System.out.println("date:"+date);
							}
							else if("type".equals(xp.getName())){
								String type1=xp.nextText();
								sms.setType(Integer.parseInt(type1));
								System.out.println("type:"+type);
							}
							else if("body".equals(xp.getName())){
								String body=xp.nextText();
								sms.setBody(body);
								System.out.println("信息:"+body);
							}
							break;
						case XmlPullParser.END_TAG:
							if("sms".equals(xp.getName())){
								smses.add(sms);
							}
							break;

					}
					//把指针移动至下一个节点，并返回该节点的事件类型
					type = xp.next();
				}
				ContentResolver resolver = context.getContentResolver();
				int progress=0;
				for(Sms s:smses){
					ContentValues values = new ContentValues();
					values.put(Sms.ADDRESS,s.getAddress());
					values.put(Sms.DATE,s.getDate());
					values.put(Sms.TYPE,s.getType());
					values.put(Sms.BODY,sms.getBody());
					resolver.insert(Uri.parse("content://sms"), values);
					callback.onSmsRestore(progress++);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//解析xml文件。
		//1. 创建pull解析器
		//2.初始化pull解析器，设置编码 inputstream
		//3.解析xml文件 while(文档末尾）
		//{
		//读取属性 size 总个数据. 调用接口的方法 beforeSmsRestore
		//每读取到一条短信 就把这个短信 body（解密） address date type获取出来
		//利用内容提供者  resolver.insert(Uri.parse("content://sms/"),contentValue);
		//每还原条 count++ 调用onSmsRestore(count);
		//}
		return false;
	}
}
