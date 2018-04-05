package com.example.divindivakaran.myaichat;

import android.app.Application;
import android.content.Context;

/**
 * Created by divin.divakaran on 9/7/2017.
 */

public class GetTimeAgo extends Application {


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "last seen: just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "last seen: a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "last seen: "+ diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "last seen: an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "last seen: "+ diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "last seen: yesterday";
        } else {
            return "last seen: "+ diff / DAY_MILLIS + " days ago";
        }
    }




}
