package com.csx.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.csx.mobilesafe.R;
import com.csx.mobilesafe.utils.PrefUtils;


/**
 * 设置向导4
 * 
 * @author csx
 * 
 */
public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cbProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		cbProtect = (CheckBox) findViewById(R.id.cb_protect);
		boolean protect=PrefUtils.getBoolean("protect",false,getApplicationContext());
		if(protect){
			cbProtect.setChecked(true);
			cbProtect.setText("防盗启动已开启");
		}else {
			cbProtect.setChecked(false);
			cbProtect.setText("您没有开启防盗保护");
		}

		cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					cbProtect.setText("防盗启动已开启");
					PrefUtils.putBoolean("protect",true,getApplicationContext());
				}else {
					cbProtect.setText("您没有开启防盗保护");
					PrefUtils.putBoolean("protect",false,getApplicationContext());
				}
			}
		});
	}


	public void showPrevious() {
		startActivity(new Intent(this, Setup3Activity.class));
		finish();

		overridePendingTransition(R.anim.anim_previous_in,R.anim.anim_previous_out);
	}


	public void showNext() {
		PrefUtils.putBoolean("configed", true, this);// 表示已经展示向导页了,下次不再展示
		startActivity(new Intent(this, LostAndFindActivity.class));
		finish();

		overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
	}

}
