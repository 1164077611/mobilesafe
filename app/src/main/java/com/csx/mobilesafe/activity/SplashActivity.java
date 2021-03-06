package com.csx.mobilesafe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csx.mobilesafe.R;
import com.csx.mobilesafe.utils.PrefUtils;
import com.csx.mobilesafe.utils.StreamUtils;
import com.csx.mobilesafe.utils.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 闪屏页面
 *
 * - 展示logo, 公司品牌 - 检查版本更新 - 项目初始化 - 校验合法性(检查是否有网络, 检查是否登录)
 * 开发流程:
 * 1. 布局文件
 * 2. 获取版本名,显示给TextView
 * 3. 访问服务器,获取json数据
 * 4. 解析json, 判断是否有更新
 * 5. 有更新,弹窗提示
 * 6. 无更新,跳主页面
 * 7. 网络异常等情况,也跳主页面
 * 8. 闪屏页显示2秒逻辑
 * 9. 打包2.0版本
 * 10. 使用xutils下载apk
 * 11. 更新下载进度
 * 12. 安装apk
 * 13. 解决签名冲突问题
 * 14. 修改bug(返回弹窗,取消安装,style样式修改)
 * 15. 闪屏页渐变动画
 * @author csx
 *
 */

public class SplashActivity extends AppCompatActivity {

    private static final int CODE_UPDATE_DIALOG = 1;
    private static final int CODE_ENTER_HOME = 2;
    private static final int CODE_URL_ERROR = 3;
    private static final int CODE_NETWORK_ERROR = 4;
    private static final int CODE_JSON_ERROR = 5;

    private TextView tvName;
    private TextView tvProgress;
    private RelativeLayout rlRoot;
    //服务器返回的版本信息
    private String mversionName;
    private int mversionCode;
    private String mdesc;
    private String murl;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;
                case CODE_URL_ERROR:
                    ToastUtils.showToast(getApplicationContext(), "网络链接错误");
                    enterHome();
                    break;
                case CODE_NETWORK_ERROR:
                    ToastUtils.showToast(getApplicationContext(), "网络异常");
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    ToastUtils.showToast(getApplicationContext(), "数据解析异常");
                    enterHome();
                    break;

                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tvName=   (TextView) findViewById(R.id.tv_name);
        tvName.setText("版本名:"+ getVersionName());
        tvProgress= (TextView)findViewById(R.id.tv_progress);
        rlRoot= (RelativeLayout) findViewById(R.id.rl_root);
//        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
//        boolean autoUpdate=sp.getBoolean("auto_update",true);
        boolean autoUpdate = PrefUtils.getBoolean("auto_update", true, this);
        if(autoUpdate){
            //需要检查更新
            checkVersion();
        }else {
            //不需要检查更新
            mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME,2000);//发送延时2s的消息
        }

        //渐变动画
        AlphaAnimation animation=new AlphaAnimation(0.2f,1);
        animation.setDuration(2000);
        rlRoot.startAnimation(animation);

        copyDb("address.db");// 拷贝归属地数据库
        copyDb("commonnum.db");// 拷贝常用号码数据库
        installShortcut();// 创建快捷方式
    }

    /**
     * 继承版本
     */
    private void checkVersion() {
        new Thread(){
            @Override
            public void run() {
                Message msg=Message.obtain();
                long startTime=System.currentTimeMillis();
                try {
                    System.out.println("线程中");
                    //10.0.2.2是预留的ip地址访问本机地址
                    HttpURLConnection conn= (HttpURLConnection) new URL("http://192.168.1.110:8080/update.json").openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(2000);
                    conn.setReadTimeout(2000);

                    conn.connect();
                    int responseCode=conn.getResponseCode();

                    if(responseCode==200){
                        InputStream inputStream = conn.getInputStream();
                        String result=StreamUtils.stream2String(inputStream);
                        JSONObject jo=new JSONObject(result);
                        mversionName = jo.getString("versionName");
                        mversionCode = jo.getInt("versionCode");
                        mdesc = jo.getString("des");
                        murl = jo.getString("url");
                        if(getVersionCode()<mversionCode){
                            msg.what=CODE_UPDATE_DIALOG;
                        }else{
                            msg.what=CODE_ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    // url错误
                    e.printStackTrace();
                    msg.what = CODE_URL_ERROR;
                } catch (IOException e) {
                    // 网络异常
                    e.printStackTrace();
                    msg.what = CODE_NETWORK_ERROR;
                } catch (JSONException e) {
                    // json解析异常
                    e.printStackTrace();
                    msg.what = CODE_JSON_ERROR;
                } finally {
                    long endTime = System.currentTimeMillis();
                    long timeUsed = endTime - startTime;// 访问网络使用的时间

                    try {
                        if (timeUsed < 2000) {
                            Thread.sleep(2000 - timeUsed);// 强制等待一段时间, 凑够两秒钟
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 升级弹窗
     */
    protected void showUpdateDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("发现新版本:"+mversionName);
        builder.setMessage(mdesc);
//        builder.setCancelable(false);//点返回,弹窗不消失,尽量不要使用
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApk();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("以后再说");
                enterHome();
            }
        });
        // 用户取消弹窗的监听,比如点返回键
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }

    /**
     * 下载安装包
     */
    public void downloadApk(){
        //判断sdk是否存在
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            tvProgress.setVisibility(View.VISIBLE);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobilesafe.apk";

            HttpUtils utils = new HttpUtils();
            utils.download(murl, path, new RequestCallBack<File>() {
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    //下载进度
                    int percent= (int) (100*current/total);
                    System.out.println("下载进度："+percent+"%");
                    tvProgress.setText("下载进度："+percent+"%");
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功
                    String p = responseInfo.result.getAbsolutePath();
                    System.out.println("下载成功:" + p);
                    //跳转系统安装页面
                    Intent intent=new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(responseInfo.result),
                            "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    //下载失败
                    e.printStackTrace();
                   ToastUtils.showToast(getApplicationContext(), s);

                }
            });
        }
    }

    /**
     * 跳到主页面
     */
    protected void enterHome(){
        startActivity(new Intent(this,HomeActivity.class));
    }

    /**
     * 用户取消调用此方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterHome();
    }

    /**
     * 获取版本名称
     * @return
     */
    private String getVersionName(){
        PackageManager pm=getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            String versionName=packageInfo.versionName;
//            int mversionCode=packageInfo.mversionCode;
//            System.out.println(mversionName+"--"+mversionCode);
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getVersionCode(){
        PackageManager pm=getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            int versionCode=packageInfo.versionCode;
//            int mversionCode=packageInfo.mversionCode;
//            System.out.println(mversionName+"--"+mversionCode);
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 拷贝数据库
     */
    private void copyDb(String dbName) {
        // data/data/包名
        // /data/data/com.csx.mobilesafe/files
        File filesDir = getFilesDir();
        File targetFile = new File(filesDir, dbName);

        // 先判断文件是否存在,如果存在,无需拷贝
        if (targetFile.exists()) {
            System.out.println("数据库" + dbName + "已经存在,无需拷贝!");
            return;
        }

        InputStream in = null;
        FileOutputStream out = null;
        try {
            AssetManager assets = getAssets();
            in = assets.open(dbName);

            out = new FileOutputStream(targetFile);

            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("拷贝数据库" + dbName + "成功!");
    }


    // 创建快捷方式
    private void installShortcut() {
        boolean isCreated = PrefUtils.getBoolean("is_shortcut_created", false,
                this);

        if (!isCreated) {
            Intent intent = new Intent();
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "小卫士");// 快解方式名称
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
                    .decodeResource(getResources(), R.drawable.home_apps));// 快解方式图标

            // 跳到主页面
            Intent actionIntent = new Intent();
            actionIntent.setAction("com.csx.mobilesafe.HOME");
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);

            sendBroadcast(intent);

            PrefUtils.putBoolean("is_shortcut_created", true, this);
        }

    }

}
