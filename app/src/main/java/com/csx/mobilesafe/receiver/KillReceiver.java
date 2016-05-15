package com.csx.mobilesafe.receiver;

import com.csx.mobilesafe.engine.ProcessInfoProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 一键清理广播
 * 
 * @author Kevin
 * 
 */
public class KillReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ProcessInfoProvider.killAll(context);
	}

}
