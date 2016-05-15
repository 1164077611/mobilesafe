package com.csx.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.csx.mobilesafe.R;
import com.csx.mobilesafe.utils.PrefUtils;


/**
 * 手机防盗页面
 * 
 * @author Kevin
 * 
 */
public class LostAndFindActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 判断是否第一次进入
		boolean configed = PrefUtils.getBoolean("configed", false, this);
		if (!configed) {
			// 进入设置向导页面
			startActivity(new Intent(this, Setup1Activity.class));
			finish();
		} else {
			setContentView(R.layout.activity_lost_and_find);
			TextView tvPhone= (TextView) findViewById(R.id.tv_safe_phone);
			ImageView ivLock= (ImageView) findViewById(R.id.iv_lock);

			String phone=PrefUtils.getString("safe_phone","",this);
			tvPhone.setText(phone);

			boolean protect=PrefUtils.getBoolean("protect",false,this);
			if(protect){
				ivLock.setImageResource(R.drawable.lock);
			}else {
				ivLock.setImageResource(R.drawable.unlock);
			}

		}
	}

	/**
	 * 重新进入设置向导
	 * @param view
     */
	public void reSetup(View view){
		startActivity(new Intent(this,Setup1Activity.class));
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.lost_find_menu, menu);
		return true;
	}

	// 当菜单被用户打开的时候调用的方法。
//	@Override
//	public boolean onMenuOpened(int featureId, Menu menu) {
//		if (rl_menu.getVisibility() == View.VISIBLE) {
//			rl_menu.setVisibility(View.INVISIBLE);
//		} else if (rl_menu.getVisibility() == View.INVISIBLE) {
//			rl_menu.setVisibility(View.VISIBLE);
//		}
//		return super.onMenuOpened(featureId, menu);
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (R.id.item_change_name == menuItem.getItemId()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("请输入新的手机防盗名称");
			final EditText et = new EditText(this);
			builder.setView(et);
			builder.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					String newname = et.getText().toString().trim();
					PrefUtils.putString("newname",newname,getApplicationContext());
				}
			});
			builder.show();
		}
		return super.onOptionsItemSelected(menuItem);
	}
}
