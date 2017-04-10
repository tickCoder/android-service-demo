package com.example.tickcoder.xxservice;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

/*
* 独立的Service的调用都是其他应用调用，因为这个Service就是一个应用，没有界面。
* 所以都需要AIDL。
* */
public class XXService extends Service {

    private IXXService.Stub mIServiceBinder = new IXXService.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public String askXXForAnswer(String clientName) throws RemoteException {
            Log.e("TICK", "XXService-askXXForAnswer");
            return "Hello Mr. " + clientName + ", I'm XXService, what can I do for you?";
        }

        @Override
        public String bindXXTest(String clientName) throws RemoteException {
            return "Hello Mr. " + clientName + ", I'm XXService!";
        }
    };

    public XXService() {
        super();
        Log.e("TICK", "" + getClass().getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TICK",  getClass().getName() + "-onCreate");
        Log.e("TICK", getClass().getName() + ": Process " + Process.myPid() + ", Thread " + Thread.currentThread().getId());
        Notification.Builder notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setContentTitle("XXService");
        notificationBuilder.setContentText("完全独立的Service|apk");
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
        return mIServiceBinder;
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
}
