package com.onyx.jdread.setting.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;

import com.onyx.jdread.util.TimeUtils;

import java.util.Calendar;

/**
 * Created by hehai on 18-1-12.
 */

public class OnyxDigitalClock extends android.support.v7.widget.AppCompatTextView {
    Calendar mCalendar;
    private final static String m12 = "h:mm";
    private final static String m24 = "k:mm";
    private FormatChangeObserver mFormatChangeObserver;

    String format = m24;

    public OnyxDigitalClock(Context context) {
        super(context);
        initClock(context);

    }

    public OnyxDigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    private void initClock(Context context) {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);

        setText(DateFormat.format(format, mCalendar));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerReceiver(getContext());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unRegisterReceiver(getContext());
    }

    /**
     * Pulls 12/24 mode from system settings
     */
    private boolean get24HourMode() {
        return android.text.format.DateFormat.is24HourFormat(getContext());
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {

        }
    }

    private BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                setText(DateFormat.format(format, mCalendar));
                invalidate();
            }
        }
    };

    public void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        context.registerReceiver(alarmReceiver, filter);
    }

    public void unRegisterReceiver(Context context) {
        context.unregisterReceiver(alarmReceiver);
    }

    public void setFormat() {
        format = TimeUtils.is24Hour() ? m24 : m12;
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        setText(DateFormat.format(format, mCalendar));
        invalidate();
    }
}
