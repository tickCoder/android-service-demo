package com.example.tickcoder.yyservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBR extends BroadcastReceiver {

    static final String BOOT_BR_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TICK", getClass().getName() + "-onReceive");
        if (intent.getAction().equalsIgnoreCase(BOOT_BR_ACTION)) {
            Intent service = new Intent(context, YYService.class);
            context.startService(service);
        }
    }
}
