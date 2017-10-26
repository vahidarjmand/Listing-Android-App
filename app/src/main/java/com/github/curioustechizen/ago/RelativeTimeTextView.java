
package com.github.curioustechizen.ago;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.tools.CONST;


/**
 * A {@code TextView} that, given a reference time, renders that time as a time period relative to the current time.
 * @author Kiran Rao
 * @see #setReferenceTime(long)
 *
 */
public class RelativeTimeTextView extends TextView {

    private static final long INITIAL_UPDATE_INTERVAL = DateUtils.MINUTE_IN_MILLIS;

    private long mReferenceTime;
    private String mText;
    private String mPrefix;
    private String mSuffix;
    private Handler mHandler = new Handler();
    private UpdateTimeRunnable mUpdateTimeTask;
    private boolean isUpdateTaskRunning = false;

    public RelativeTimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RelativeTimeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.RelativeTimeTextView, 0, 0);
        try {
            mText = a.getString(R.styleable.RelativeTimeTextView_reference_time);
            mPrefix = a.getString(R.styleable.RelativeTimeTextView_relative_time_prefix);
            mSuffix = a.getString(R.styleable.RelativeTimeTextView_relative_time_suffix);

            mPrefix = mPrefix == null ? "" : mPrefix;
            mSuffix = mSuffix == null ? "" : mSuffix;
        } finally {
            a.recycle();
        }

        try {
            mReferenceTime = Long.valueOf(mText);
        } catch (NumberFormatException nfe) {
        	/*
        	 * TODO: Better exception handling
        	 */
            mReferenceTime = -1L;
        }

        setReferenceTime(mReferenceTime);

    }

    /**
     * Returns prefix
     * @return
     */
    public String getPrefix() {
        return this.mPrefix;
    }

    /**
     * String to be attached before the reference time
     * @param prefix
     *
     * Example:
     * [prefix] in XX minutes
     */
    public void setPrefix(String prefix) {
        this.mPrefix = prefix;
        updateTextDisplay();
    }

    /**
     * Returns suffix
     * @return
     */
    public String getSuffix() {
        return this.mSuffix;
    }

    /**
     * String to be attached after the reference time
     * @param suffix
     *
     * Example:
     * in XX minutes [suffix]
     */
    public void setSuffix(String suffix) {
        this.mSuffix = suffix;
        updateTextDisplay();
    }

    /**
     * Sets the reference time for this view. At any moment, the view will render a relative time period relative to the time set here.
     * <p/>
     * This value can also be set with the XML attribute {@code reference_time}
     * @param referenceTime The timestamp (in milliseconds since epoch) that will be the reference point for this view.
     */
    public void setReferenceTime(long referenceTime) {
        this.mReferenceTime = referenceTime;
        
        /*
         * Note that this method could be called when a row in a ListView is recycled.
         * Hence, we need to first stop any currently running schedules (for example from the recycled view.
         */
        stopTaskForPeriodicallyUpdatingRelativeTime();
        
        /*
         * Instantiate a new runnable with the new reference time
         */
        initUpdateTimeTask();
        
        /*
         * Start a new schedule.
         */
        startTaskForPeriodicallyUpdatingRelativeTime();
        
        /*
         * Finally, update the text display.
         */
        updateTextDisplay();
    }

    private void updateTextDisplay() {
        /*
         * TODO: Validation, Better handling of negative cases
         */
        if (this.mReferenceTime == -1L)
            return;

        Log.d(CONST.APP_LOG,"time: "+ getRelativeTimeDisplayString());
        setText(mPrefix + getRelativeTimeDisplayString() + mSuffix);
    }

    private CharSequence getRelativeTimeDisplayString() {
        /*long now = System.currentTimeMillis();
        long difference = now - mReferenceTime; 
        return (difference >= 0 &&  difference<=DateUtils.MINUTE_IN_MILLIS) ? 
                getResources().getString(R.string.just_now): 
                DateUtils.getRelativeTimeSpanString(
                    mReferenceTime,
                    now,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE);*/

        Date fromdate = new Date(mReferenceTime);
        long then;
        then = fromdate.getTime();
        Date date = new Date(then);

        StringBuffer dateStr = new StringBuffer();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar now = Calendar.getInstance();

        int days = daysBetween(calendar.getTime(), now.getTime());
        int weeks = days / 7;
        int minutes = hoursBetween(calendar.getTime(), now.getTime());
        int hours = minutes / 60;
        if (days == 0) {

            int second = minuteBetween(calendar.getTime(), now.getTime());
            if (minutes > 60) {
                if (hours >= 1 && hours <= 24) {
                    dateStr.append(String.format("%s %s %s",
                            hours,
                            getContext().getString(R.string.hour)
                            , getContext().getString(R.string.ago)));
                }
            } else {
                if (second <= 10) {
                    dateStr.append(getContext().getString(R.string.now));
                } else if (second > 10 && second <= 30) {
                    dateStr.append(getContext().getString(R.string.few_seconds_ago));
                } else if (second > 30 && second <= 60) {
                    dateStr.append(String.format("%s %s %s",
                            second,
                            getContext().getString(R.string.seconds)
                            , getContext().getString(R.string.ago)));
                } else if (second >= 60 && minutes <= 60) {
                    dateStr.append(String.format("%s %s %s",
                            minutes,
                            getContext().getString(R.string.minutes)
                            , getContext().getString(R.string.ago)));
                }
            }
        } else if (hours > 24 && days < 7) {
            dateStr.append(String.format("%s %s %s",
                    days,
                    getContext().getString(R.string.days)
                    , getContext().getString(R.string.ago)));
        } else if (weeks == 1) {
            dateStr.append(String.format("%s %s %s",
                    weeks,
                    getContext().getString(R.string.weeks)
                    , getContext().getString(R.string.ago)));
        } else {
            /**
             * make formatted createTime by languageTag("fa")
             *
             * @return
             */
            return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG)
                    .format(date).toUpperCase();
        }

        return dateStr.toString();
    }

    public static int minuteBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / android.text.format.DateUtils.SECOND_IN_MILLIS);
    }

    public static int hoursBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / android.text.format.DateUtils.MINUTE_IN_MILLIS);
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / android.text.format.DateUtils.DAY_IN_MILLIS);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startTaskForPeriodicallyUpdatingRelativeTime();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTaskForPeriodicallyUpdatingRelativeTime();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == GONE || visibility == INVISIBLE) {
            stopTaskForPeriodicallyUpdatingRelativeTime();
        } else {
            startTaskForPeriodicallyUpdatingRelativeTime();
        }
    }

    private void startTaskForPeriodicallyUpdatingRelativeTime() {
        if(mUpdateTimeTask.isDetached()) initUpdateTimeTask();
        mHandler.post(mUpdateTimeTask);
        isUpdateTaskRunning = true;
    }

    private void initUpdateTimeTask() {
        mUpdateTimeTask = new UpdateTimeRunnable(this, mReferenceTime);
    }

    private void stopTaskForPeriodicallyUpdatingRelativeTime() {
        if(isUpdateTaskRunning) {
            mUpdateTimeTask.detach();
            mHandler.removeCallbacks(mUpdateTimeTask);
            isUpdateTaskRunning = false;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.referenceTime = mReferenceTime;
        return ss;
    }
    
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        mReferenceTime = ss.referenceTime;
        super.onRestoreInstanceState(ss.getSuperState());
    }

    public static class SavedState extends BaseSavedState {

        private long referenceTime;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeLong(referenceTime);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        
        private SavedState(Parcel in) {
            super(in);
            referenceTime = in.readLong();
        }
    }

    private static class UpdateTimeRunnable implements Runnable {

        private long mRefTime;
        private final WeakReference<RelativeTimeTextView> weakRefRttv;

        UpdateTimeRunnable(RelativeTimeTextView rttv, long refTime) {
            this.mRefTime = refTime;
            weakRefRttv = new WeakReference<>(rttv);
        }

        boolean isDetached() {
            return weakRefRttv.get() == null;
        }

        void detach() {
            weakRefRttv.clear();
        }

        @Override
        public void run() {
            RelativeTimeTextView rttv = weakRefRttv.get();
            if (rttv == null) return;
            long difference = Math.abs(System.currentTimeMillis() - mRefTime);
            long interval = INITIAL_UPDATE_INTERVAL;
            if (difference > DateUtils.WEEK_IN_MILLIS) {
                interval = DateUtils.WEEK_IN_MILLIS;
            } else if (difference > DateUtils.DAY_IN_MILLIS) {
                interval = DateUtils.DAY_IN_MILLIS;
            } else if (difference > DateUtils.HOUR_IN_MILLIS) {
                interval = DateUtils.HOUR_IN_MILLIS;
            }
            rttv.updateTextDisplay();
            rttv.mHandler.postDelayed(this, interval);

        }
    }
}
