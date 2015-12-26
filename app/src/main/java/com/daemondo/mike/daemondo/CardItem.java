package com.daemondo.mike.daemondo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Keep Cards data
 */
public class CardItem {

    private Daemon daemon;
    private String note;

    public CardItem(Daemon d, String n)
    {
        daemon = d;
        note = n;
    }

    public Daemon getDaemon() { return daemon; }
    public String getNote() { return note; }
    public void setNote(String s) { note = s; }
}
