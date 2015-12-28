package com.daemondo.mike.daemondo;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Daemon Class
 */
public class Daemon {

    public enum Type {DAEMON, ENEMY, CARD}

    // Number of png's
    final static int RES_COUNT = 5;

    // Views
    RelativeLayout rl; // Relative Layout
    ImageView extra;
    ImageView body;
    ImageView eyes;

    // Res ID's
    int extraID, bodyID, eyesID;
    int extraRes, bodyRes, eyesRes;

    // Daemon type
    Type type;

    // HP, XP
    int level, hp, xp = 0;

    public Daemon(Type type, int extraID, int bodyID, int eyesID) {

        init(type, extraID, bodyID, eyesID);
    }

    private void init(Type type, int extraID, int bodyID, int eyesID)
    {
        this.type = type;
        this.extraID = extraID;
        this.bodyID = bodyID;
        this.eyesID = eyesID;

        switch (type) {
            case DAEMON:
                rl = (RelativeLayout)  MainActivity.mainActivity.findViewById(R.id.daemon);
                extra = (ImageView) MainActivity.mainActivity.findViewById(R.id.daemon_extra);
                body = (ImageView) MainActivity.mainActivity.findViewById(R.id.daemon_body);
                eyes = (ImageView) MainActivity.mainActivity.findViewById(R.id.daemon_eyes);

                // Set resources
                this.extraRes = MainActivity.mainActivity.getResources().getIdentifier("extra_" + extraID, "mipmap", MainActivity.mainActivity.getPackageName());
                this.bodyRes = MainActivity.mainActivity.getResources().getIdentifier("body_" + bodyID, "mipmap", MainActivity.mainActivity.getPackageName());
                this.eyesRes = MainActivity.mainActivity.getResources().getIdentifier("eyes_" + eyesID, "mipmap", MainActivity.mainActivity.getPackageName());
                extra.setImageResource(this.extraRes);
                body.setImageResource(this.bodyRes);
                eyes.setImageResource(this.eyesRes);

                refreshLevelTextView();

                break;

            case ENEMY:
                rl = (RelativeLayout)  MainActivity.mainActivity.findViewById(R.id.enemy);
                extra = (ImageView) MainActivity.mainActivity.findViewById(R.id.enemy_extra);
                body = (ImageView) MainActivity.mainActivity.findViewById(R.id.enemy_body);
                eyes = (ImageView) MainActivity.mainActivity.findViewById(R.id.enemy_eyes);

                this.extraRes = MainActivity.mainActivity.getResources().getIdentifier("extra_" + extraID, "mipmap", MainActivity.mainActivity.getPackageName());
                this.bodyRes = MainActivity.mainActivity.getResources().getIdentifier("body_" + bodyID, "mipmap", MainActivity.mainActivity.getPackageName());
                this.eyesRes = MainActivity.mainActivity.getResources().getIdentifier("eyes_" + eyesID, "mipmap", MainActivity.mainActivity.getPackageName());
                extra.setImageResource(this.extraRes);
                body.setImageResource(this.bodyRes);
                eyes.setImageResource(this.eyesRes);
                rl.setVisibility(View.INVISIBLE);

                break;

            case CARD: // Otherwise the view is being set by the Card Adapter
                // Set resources
                this.extraRes = MainActivity.mainActivity.getResources().getIdentifier("extra_" + extraID, "mipmap", MainActivity.mainActivity.getPackageName());
                this.bodyRes = MainActivity.mainActivity.getResources().getIdentifier("body_" + bodyID, "mipmap", MainActivity.mainActivity.getPackageName());
                this.eyesRes = MainActivity.mainActivity.getResources().getIdentifier("eyes_" + eyesID, "mipmap", MainActivity.mainActivity.getPackageName());

                break;
        }
    }

    // Callback for Card Adapter
    public void setViews(RelativeLayout rl, ImageView extraView, ImageView bodyView, ImageView eyesView) {
        this.rl = rl;
        this.extra = extraView;
        this.body = bodyView;
        this.eyes = eyesView;
    }

    public void setLevel(int level)
    {
        this.level = level;
        String s = MainActivity.mainActivity.getString(R.string.level_text, level);
        ((TextView)MainActivity.mainActivity.findViewById(R.id.level)).setText(s);
    }

    public void setHp(int hp)
    {
        this.hp = hp;
        MaterialProgressBar hpBar = (MaterialProgressBar)MainActivity.mainActivity.findViewById(R.id.hp);
        hpBar.setProgress(hp);
    }

    public void setXp(int xp)
    {
        this.xp = xp;
        MaterialProgressBar xpBar = (MaterialProgressBar)MainActivity.mainActivity.findViewById(R.id.xp);
        xpBar.setProgress(xp);
    }

    public void attack() {
        Log.d("DEBUG", "Attacking");

        Animator attack = AnimatorInflater.loadAnimator(MainActivity.mainActivity,
                R.animator.anim_attack);
        Animator idle = AnimatorInflater.loadAnimator(MainActivity.mainActivity,
                R.animator.anim_idle);
        AnimatorSet s = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.mainActivity,
                R.animator.anim_idle);
        s.play(attack).before(idle);
        s.setTarget(rl);
        s.start();
    }

    public void idle() {
        Log.d("DEBUG", "Idle");
        AnimatorSet s = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.mainActivity,
                R.animator.anim_idle);
        s.setTarget(rl);
        s.start();
    }

    public void enemyAnim() {
        Log.d("DEBUG", "Enemy Anim");
        rl.setVisibility(View.VISIBLE);

        AnimatorSet s = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.mainActivity,
                R.animator.anim_enemy);
        s.setTarget(rl);
        s.start();
    }

    public void enemyKilled()
    {
        xp += 10;
        if (xp >= 100)
        {
            level++;
            xp = 0;
        }

        refreshLevelTextView();
        MaterialProgressBar xpBar = (MaterialProgressBar)MainActivity.mainActivity.findViewById(R.id.xp);
        xpBar.setProgress(xp);
    }

    public void refreshLevelTextView()
    {
        String s = MainActivity.mainActivity.getString(R.string.level_text, level);
        ((TextView)MainActivity.mainActivity.findViewById(R.id.level)).setText(s);
    }
}
