package com.example.tickcoder.yyservice;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.util.Log;

/*
* 依赖应用的Service有两种调用方法：
* 1。其依赖的应用调用（可以不使用AIDL）
* 2。其他应用调用（需要使用AIDL）
* */
public class YYService extends Service {

    private boolean mUseAIDL = false; // 是否使用AIDL
    private YYServiceBinder mServiceBinder = new YYServiceBinder(); // 不使用AIDL

    private IYYService.Stub mIServiceBinder = new IYYService.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public String askYYForAnswer(String clientName) throws RemoteException {
            Log.e("TICK", "YYService-askYYForAnswer");
            return "Hello Mr. " + clientName + ", I'm YYService, what can I do for you?";
        }

        @Override
        public String bindYYTest(String clientName) throws RemoteException {
            return "Hello Mr. " + clientName + ", I'm YYService!";
        }
    };

    public YYService() {
        super();
        Log.e("TICK", "" + getClass().getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TICK", getClass().getName() + "-onCreate");
        Log.e("TICK", getClass().getName() + ": Process " + Process.myPid() + ", Thread " + Thread.currentThread().getId());
        Notification.Builder notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setContentTitle("YYService");
        notificationBuilder.setContentText("不独立的Service|aar");
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        //notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setWhen(System.currentTimeMillis());
        Notification notification = notificationBuilder.build();
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TICK", getClass().getName() + "-onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TICK", getClass().getName() + "-onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e("TICK", getClass().getName() + "-onStart");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("TICK", getClass().getName() + "-onBind");
        return mUseAIDL?mIServiceBinder:mServiceBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.e("TICK", getClass().getName() + "-onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("TICK", getClass().getName() + "-onUnbind");
        return super.onUnbind(intent);
    }

    // ServiceBinder，当不增加AIDL时使用此类
    public class YYServiceBinder extends Binder {
        public String bindYYTest(String clientName) {
            return "Hello Mr. " + clientName + ", I'm YYService!";
        }
        public String askYYForAnswer(String clientName) throws RemoteException {
            return "Hello Mr. " + clientName + ", I'm YYService, what can I do for you?";
        }
    }
}
