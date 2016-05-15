package com.csx.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.csx.mobilesafe.R;
import com.csx.mobilesafe.utils.PrefUtils;
import com.csx.mobilesafe.utils.ToastUtils;
import com.csx.mobilesafe.view.SettingItemView;


/**
 * 设置向导2
 *
 * @author csx
 */
public class Setup2Activity extends BaseSetupActivity {

    private GestureDetector mDetector;
    private SettingItemView sivBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        sivBind = (SettingItemView) findViewById(R.id.siv_bind);
        String bindSim=PrefUtils.getString("bind_sim",null,getApplicationContext());
        if(TextUtils.isEmpty(bindSim)){
            sivBind.setChecked(false);
        }else{
            sivBind.setChecked(true);
        }
        sivBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sivBind.isChecked()){
                    sivBind.setChecked(false);
                    PrefUtils.remove("bind_sim",getApplicationContext());
                }else{
                    sivBind.setChecked(true);
                    TelephonyManager tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    //获取sim卡序列号
                    String simSerialNumber = tm.getSimSerialNumber();
                    PrefUtils.putString("bind_sim",simSerialNumber,getApplicationContext());
                }
            }
        });
    }



    public void showPrevious() {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();

        overridePendingTransition(R.anim.anim_previous_in, R.anim.anim_previous_out);
    }



    public void showNext() {
        // 判断是否已经绑定sim卡,只有绑定,才能进行下一步操作
        String bindSim = PrefUtils.getString("bind_sim", null, this);
        if (TextUtils.isEmpty(bindSim)) {
            ToastUtils.showToast(this, "必须绑定sim卡!");
            return;
        }

        startActivity(new Intent(this, Setup3Activity.class));
        finish();
        // 两个activity之间切换的动画, 应该放在finish之后运行
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }
}
