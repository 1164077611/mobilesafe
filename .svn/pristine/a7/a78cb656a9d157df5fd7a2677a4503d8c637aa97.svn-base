package com.csx.mobilesafe.receiver;


import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.csx.mobilesafe.R;
import com.csx.mobilesafe.service.LocationService;

/**
 * 拦截短信
 * <p/>
 * <receiver android:name=".receiver.SmsReceiver" > <intent-filter
 * android:priority="2147483647" > <action
 * android:name="android.provider.Telephony.SMS_RECEIVED" /> </intent-filter>
 * </receiver>
 * <p/>
 * 需要权限:<uses-permission android:name="android.permission.RECEIVE_SMS" />
 *
 * @author csx
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"短信到来了");
        Object[] objs = (Object[]) intent.getExtras().get("pdus");

        //获取超级管理员
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        for(Object obj:objs){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
            String sender = smsMessage.getOriginatingAddress();
            String body = smsMessage.getMessageBody();
            if("#*location*#".equals(body)){
                Log.i(TAG,"返回位置信息.");
                //获取位置 放在服务里面去实现。
                Intent service = new Intent(context,LocationService.class);
                context.startService(service);
                abortBroadcast();
            }else if("#*alarm*#".equals(body)){
                Log.i(TAG,"播放报警音乐.");
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1.0f, 1.0f);
                player.start();
                abortBroadcast();
            }else if("#*wipedata*#".equals(body)){
                Log.i(TAG,"远程清除数据.");
                dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                abortBroadcast();
            }else if("#*lockscreen*#".equals(body)){
                Log.i(TAG,"远程锁屏.");
                dpm.resetPassword("123", 0);
                dpm.lockNow();
                abortBroadcast();
            }
        }
    }

}
