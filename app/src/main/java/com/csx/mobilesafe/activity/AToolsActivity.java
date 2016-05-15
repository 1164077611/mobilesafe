package com.csx.mobilesafe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.csx.mobilesafe.R;
import com.csx.mobilesafe.utils.SmsUtils;
import com.csx.mobilesafe.utils.ToastUtils;

import java.io.File;

/**
 * @author csx
 */
public class AToolsActivity extends AppCompatActivity {
	private ProgressBar pbProgress;
	private ProgressBar pbProgress_1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);

		pbProgress = (ProgressBar) findViewById(R.id.pb_progress);
		pbProgress_1= (ProgressBar) findViewById(R.id.pb_progress_1);
	}


	/**
	 * 程序锁
	 * @param view
	 */
	public void appLock(View view) {
		startActivity(new Intent(this, AppLockActivity.class));
	}

	/**
	 * 常用号码查询
	 *
	 * @param view
	 */
	public void commonNumberQuery(View view) {
		startActivity(new Intent(this, CommonNumberActivity.class));
	}

	/**
	 * 电话归属地
	 */
	public void addressQuery(View view) {
		startActivity(new Intent(this, AddressQueryActivity.class));
	}

	/**
	 * 短信备份
	 *
	 * @param view
	 */
	public void smsBackup(View view) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("正在备份短信...");
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 水平方向进度条,可以展示进度
			dialog.show();

			new Thread() {
				public void run() {
					File output = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/sms66.xml");
					SmsUtils.smsBackup(getApplicationContext(), output,
							new SmsUtils.SmsCallback() {

								@Override
								public void preSmsBackup(int count) {
									dialog.setMax(count);
									pbProgress_1.setMax(count);
								}

								@Override
								public void onSmsBackup(int progress) {
									dialog.setProgress(progress);
									pbProgress_1.setProgress(progress);
								}
							});

					dialog.dismiss();
				};
			}.start();
		} else {
			ToastUtils.showToast(this, "sdcard不存在!");
		}
	}

	/**
	 * 短信备份
	 *
	 * @param view
	 */
	public void smsRestore(View view) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			System.out.println("开始");
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("正在备份短信...");
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 水平方向进度条,可以展示进度
			dialog.show();

			new Thread() {
				public void run() {
					System.out.println("开始备份");
					SmsUtils.restoreSms(getApplicationContext(),
							new SmsUtils.RestoreSmsCallBack() {

								@Override
								public void beforeSmsRestore(int size) {
									dialog.setMax(size);
									pbProgress.setMax(size);
								}

								@Override
								public void onSmsRestore(int progress) {
									dialog.setProgress(progress);
									pbProgress.setProgress(progress);
								}
							});

					dialog.dismiss();
				};
			}.start();
		} else {
			ToastUtils.showToast(this, "sdcard不存在!");
		}
	}

}
