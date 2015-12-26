package com.daemondo.mike.daemondo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;

import br.com.goncalves.pugnotification.notification.PugNotification;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<CardItem> mCards;

    // Daemons
    private Daemon daemon;
    private Daemon enemy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Custom Font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/upheavtt.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        setContentView(R.layout.activity_main);

        // Main Activity static
        mainActivity = this;

        // Try restoring from SharedPreferences
        restoreDaemon();
        restoreTasks();

        // List
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CustomAdapter(mCards);
        mRecyclerView.setAdapter(mAdapter);

        // Handler for swipe-to-dismiss
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter)mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        // Fab
        final FloatingActionButton buttonAdd = (FloatingActionButton)findViewById(R.id.fab);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "New Task Added", Toast.LENGTH_SHORT).show();
                newTask();
                mAdapter.notifyItemInserted(mCards.size() - 1);
            }
        });

        // Notification (every 2 hours)
        Intent notificationIntent = new Intent(this, NotificationService.class);
        PendingIntent contentIntent = PendingIntent.getService(this, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(contentIntent);
        am.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR*2, contentIntent);

    }

    // Called by the swipe listener when a card is removed
    public void cardRemoved(Daemon d)
    {
        enemy = new Daemon(Daemon.Type.ENEMY, d.extraID, d.bodyID, d.eyesID);
        enemy.enemyAnim();
        daemon.attack();
        daemon.enemyKilled();
        daemon.setHp(100);
    }

    public void newTask()
    {
        Daemon newEnemy = new Daemon(Daemon.Type.CARD, randRes(), randRes(), randRes());
        mCards.add(new CardItem(newEnemy, ""));
    }

    public int randRes()
    {
        Random r = new Random();
        return r.nextInt(Daemon.RES_COUNT+1 - 1) + 1;
    }

    // Custom font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    // Save state
    @Override
    public void onPause()
    {
        super.onPause();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPref.edit();

        // Push Daemon
        ed.putInt("level", daemon.level);
        ed.putInt("hp", daemon.hp);
        ed.putInt("xp", daemon.xp);
        ed.putInt("extraID", daemon.extraID);
        ed.putInt("bodyID", daemon.bodyID);
        ed.putInt("eyesID", daemon.eyesID);

        // Push Tasks
        ed.putInt("task_count", mCards.size());
        Iterator<CardItem> it = mCards.iterator();
        int count = 0;
        while(it.hasNext())
        {
            CardItem c = it.next();
            ed.putString("task_" + count, c.getNote());
            count++;
        }

        ed.commit();
    }

    // Restore state
    public void restoreDaemon()
    {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        // Check if daemon has died
        if (sp.getInt("hp",0) <= 0) {
            daemon = new Daemon(Daemon.Type.DAEMON, randRes(), randRes(), randRes());
            ed.putInt("hp", 100);
            ed.commit();
        }
        else
        {
            daemon = new Daemon(Daemon.Type.DAEMON, sp.getInt("extraID", randRes()),
                    sp.getInt("bodyID", randRes()),
                    sp.getInt("eyesID", randRes()));
        }

        daemon.setLevel(sp.getInt("level", 0));
        daemon.setHp(sp.getInt("hp", 100));
        daemon.setXp(sp.getInt("xp", 0));
        daemon.idle();
    }

    public void restoreTasks()
    {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        int taskCount = sp.getInt("task_count", 0);
        mCards = new ArrayList<CardItem>(0);

        for (int i = 0; i < taskCount; i++)
        {
            Daemon e = new Daemon(Daemon.Type.CARD, randRes(), randRes(), randRes());
            CardItem c = new CardItem(e, sp.getString("task_" + i, "Err"));
            mCards.add(c);
        }
    }
}
