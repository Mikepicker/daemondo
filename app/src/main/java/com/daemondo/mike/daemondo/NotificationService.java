package com.daemondo.mike.daemondo;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import br.com.goncalves.pugnotification.notification.PugNotification;

/**
 * Created by mike on 26/12/15.
 */
public class NotificationService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Service destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onCreate();
        Toast.makeText(this,"Service started", Toast.LENGTH_SHORT).show();

        // Decrease daemon hp
        SharedPreferences sharedPref = MainActivity.mainActivity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPref.edit();
        int hp = sharedPref.getInt("hp", 0) - 25;
        if (hp <= 0)
            ed.putInt("hp", 0);
        else
            ed.putInt("hp", hp);
        ed.commit();

        PugNotification.with(MainActivity.mainActivity)
                .load()
                .title("DaemonDo")
                .message("Your daemon is suffering!")
                .bigTextStyle("bigtext")
                .smallIcon(R.mipmap.app_icon)
                .largeIcon(R.mipmap.app_icon)
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();

        return START_NOT_STICKY;
    }
}
