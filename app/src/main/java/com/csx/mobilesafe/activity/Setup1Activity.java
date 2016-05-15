package com.csx.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.csx.mobilesafe.R;


/**
 * 设置向导1
 * 
 * @author csx
 * 
 */
public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);


	}



	@Override
	public void showPrevious() {

	}

	@Override
	public void showNext() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		// 两个activity之间切换的动画, 应该放在finish之后运行
		overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
	}

}
