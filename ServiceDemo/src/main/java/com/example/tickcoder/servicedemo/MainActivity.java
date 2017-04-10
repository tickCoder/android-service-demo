package com.example.tickcoder.servicedemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tickcoder.xxservice.IXXService;
import com.example.tickcoder.yyservice.YYService;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // XXService
    private final static String XXSERVICE_PKGNAME = "com.example.tickcoder.xxservice";
    private final static String XXSERVICE_NAME = "com.example.tickcoder.xxservice.XXService";
    private final static String XXSERVICE_AIDLACTION = "com.example.tickcoder.xxservice.action";
    private boolean isXXServiceConnected = false;
    private Button mXXServiceStartBtn;
    private Button mXXServiceBindBtn;
    private Button mXXServiceSendBtn;
    private Button mXXServiceUnbindBtn;
    private Button mXXServiceStopBtn;
    private IXXService mIXXService;
    private ServiceConnection mXXServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("TICK", "XXService-onServiceConnected");
            String pkgName = name.getPackageName();
            if (pkgName.equalsIgnoreCase(XXSERVICE_PKGNAME)) {
                isXXServiceConnected = true;
                try {
                    mIXXService = IXXService.Stub.asInterface(service);
                    String response = mIXXService.bindXXTest("Outer");
                    Toast.makeText(MainActivity.this, response+"", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("TICK", "XXService-onServiceDisconnected");
            isXXServiceConnected = false;
        }
    };

    // YYService
    private final static String YYSERVICE_PKGNAME = "com.example.tickcoder.yyservice";
    private final static String YYSERVICE_AIDLACTION = "com.example.tickcoder.yyservice.action";
    private final static String YYSERVICE_DEPEN_PKGNAME = "com.example.tickcoder.servicedemo";
    private boolean isYYServiceConnected = false;
    private Button mYYServiceStartBtn;
    private Button mYYServiceBindBtn;
    private Button mYYServiceSendBtn;
    private Button mYYServiceUnbindBtn;
    private Button mYYServiceStopBtn;
    private YYService.YYServiceBinder mYYServiceBinder;
    private ServiceConnection mYYServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("TICK", "YYService-onServiceConnected");
            String pkgName = name.getPackageName();
            if (pkgName.equalsIgnoreCase(YYSERVICE_DEPEN_PKGNAME)) {
                isYYServiceConnected = true;
                try {
                    // 错误: 需要去掉:remote
                    // java.lang.ClassCastException: android.os.BinderProxy cannot be cast to com.example.tickcoder.yyservice.YYService$YYServiceBinder
                    mYYServiceBinder = (YYService.YYServiceBinder) service;
                    String response = mYYServiceBinder.bindYYTest("Inner");
                    Toast.makeText(MainActivity.this, response+"", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("TICK", "YYService-onServiceDisconnected");
            isYYServiceConnected = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("TICK", getClass().getName() + ": Process " + Process.myPid() + ", Thread " + Thread.currentThread().getId());

        mXXServiceStartBtn = (Button)findViewById(R.id.btn_xxservice_start);
        mXXServiceBindBtn = (Button)findViewById(R.id.btn_xxservice_bind);
        mXXServiceSendBtn = (Button)findViewById(R.id.btn_xxservice_send);
        mXXServiceUnbindBtn = (Button)findViewById(R.id.btn_xxservice_unbind);
        mXXServiceStopBtn = (Button)findViewById(R.id.btn_xxservice_stop);

        mXXServiceStartBtn.setOnClickListener(this);
        mXXServiceBindBtn.setOnClickListener(this);
        mXXServiceSendBtn.setOnClickListener(this);
        mXXServiceUnbindBtn.setOnClickListener(this);
        mXXServiceStopBtn.setOnClickListener(this);

        mYYServiceStartBtn = (Button)findViewById(R.id.btn_yyservice_start);
        mYYServiceBindBtn = (Button)findViewById(R.id.btn_yyservice_bind);
        mYYServiceSendBtn = (Button)findViewById(R.id.btn_yyservice_send);
        mYYServiceUnbindBtn = (Button)findViewById(R.id.btn_yyservice_unbind);
        mYYServiceStopBtn = (Button)findViewById(R.id.btn_yyservice_stop);

        mYYServiceStartBtn.setOnClickListener(this);
        mYYServiceBindBtn.setOnClickListener(this);
        mYYServiceSendBtn.setOnClickListener(this);
        mYYServiceUnbindBtn.setOnClickListener(this);
        mYYServiceStopBtn.setOnClickListener(this);
    }

    /*
    * 关于跨应用调用Service有三种方式：
    *
    * 第一种方法， 6.0可以，7.0不可以
    * 5.0开始要求明确的调用，如XXService.class，所以需要转换一下
    * Intent service = new Intent(XXSERVICE_AIDLACTION);
    * Intent finalService = createExplicitFromImplicitIntent(MainActivity.this, service);
    *
    * 第二种方法，6.0可以，7.0可以
    * 所依赖的apk包名。若为独立Service，则为其报名；若不是独立的Service，则为其所依赖的包名。
    * 所属的Service的Name，不是action
    * Intent service = new Intent();
    * service.setComponent(new ComponentName(XXSERVICE_PKGNAME, XXSERVICE_NAME));
    *
    *第三种方法，6.0可以，7.0可以
    * 其action
    * 所依赖的apk包名。若为独立Service，则为其报名；若不是独立的Service，则为其所依赖的包名。
    * Intent service = new Intent();
    * service.setAction(XXSERVICE_AIDLACTION);
    * service.setPackage(XXSERVICE_PKGNAME);
    * */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_xxservice_start: {
                Intent service = new Intent();
                service.setAction(XXSERVICE_AIDLACTION);
                service.setPackage(XXSERVICE_PKGNAME);
                startService(service);
                break;
            }
            case R.id.btn_xxservice_bind: {
                Intent service = new Intent();
                service.setAction(XXSERVICE_AIDLACTION);
                service.setPackage(XXSERVICE_PKGNAME);
                bindService(service, mXXServiceConnection, BIND_AUTO_CREATE);
                break;
            }
            case R.id.btn_xxservice_send: {
                if (mIXXService != null) {
                    try {
                        String response = mIXXService.askXXForAnswer("Outer");
                        Toast.makeText(MainActivity.this, response+"", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case R.id.btn_xxservice_unbind: {
                if (mIXXService != null && isXXServiceConnected) {
                    unbindService(mXXServiceConnection);
                    isXXServiceConnected = false;
                }
                break;
            }
            case R.id.btn_xxservice_stop: {
                Intent service = new Intent();
                service.setAction(XXSERVICE_AIDLACTION);
                service.setPackage(XXSERVICE_PKGNAME);
                stopService(service);
                isXXServiceConnected = false;
                break;
            }
            case R.id.btn_yyservice_start: {
                Intent service = new Intent(this, YYService.class);
                startService(service);
                break;
            }
            case R.id.btn_yyservice_bind: {
                Intent service = new Intent(this, YYService.class);
                bindService(service, mYYServiceConnection, BIND_AUTO_CREATE);
                break;
            }
            case R.id.btn_yyservice_send: {
                if (mYYServiceBinder != null) {
                    try {
                        String response = mYYServiceBinder.askYYForAnswer("Inner");
                        Toast.makeText(MainActivity.this, response+"", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case R.id.btn_yyservice_unbind: {
                if (mYYServiceBinder != null && isYYServiceConnected){
                    unbindService(mYYServiceConnection);
                    isYYServiceConnected = false;
                }
                break;
            }
            case R.id.btn_yyservice_stop: {
                Intent service = new Intent(this, YYService.class);
                stopService(service);
                isYYServiceConnected = false;
                break;
            }
            default:break;
        }
    }

    private static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}
