package org.varonesoft.luke.countdown;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Locale;

public class Counter {

    // Public Statics
    public static final String FORMAT = "%02d";
    public static final long ONESECONDMILLIS = 1000L;
    public static final long ONEMINUTEMILLIS = 60 * 1000L;
    public static final long COUNT_MIN = 0L;
    public static final long COUNT_MAX = 60 * ONEMINUTEMILLIS;
    private static final Locale LOCALE = Locale.ITALY;
    private static final long DELAYMILLIS = 100L;

    // Object related
    private CountDownTimer mICT;                // first countdown
    private CountDownTimer mCT;                 // second
    private long mInterval = ONESECONDMILLIS;   // interval onPreCountTick()
    private long mPreCount = 5 * ONESECONDMILLIS;// millis before start
    private long mCount = ONEMINUTEMILLIS;      // counter 1min

    // Views
    private TextView mMinutesView;               // the view to adjourn
    private TextView mSecondsView;               // the view to adjourn

    // Listener
    private CounterListener mListener;
    private PreCounterListener mPreListener;

    // Force the use of the builder
    private Counter() {
    }

    // Pre-countdown
    public void start() {

        // Check PreCount
        if (mPreCount == 0) {
            _start();
            return;
        }

        // Adjourn th views
        adjournViews(mPreCount + DELAYMILLIS);

        mICT = new CountDownTimer(mPreCount, mInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                // Adjourn th view
                adjournViews(millisUntilFinished + DELAYMILLIS);
                // Emits a beep?
                if (mPreListener != null) mPreListener.onPreCountTick();
            }

            @Override
            public void onFinish() {
                // Adjourn th view
                adjournViews(mPreCount + DELAYMILLIS);
                // Inform thge listener
                if (mPreListener != null) mPreListener.onPreCountFinish();
                // Start the real countdown
                _start();
            }
        };
        mICT.start();
    }

    // The real thing
    private void _start() {
        // Adjourn th views
        adjournViews(mCount + DELAYMILLIS);

        mCT = new CountDownTimer(mCount, mInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                // Adjourn th view
                adjournViews(millisUntilFinished);
                // Emits a beep?
            }

            @Override
            public void onFinish() {
                // Adjourn th view
                adjournViews(0L);
                // Emits a beep?
                if (mListener != null) mListener.onCountFinish();
                // Free resources??
            }
        };
        mCT.start();
    }

    // Refresh Views
    private void adjournViews(long millisUntilFinished) {
        // Adjourn th view
        if (mMinutesView != null)
            mMinutesView.setText(String.format(LOCALE, FORMAT,
                    (millisUntilFinished + DELAYMILLIS) / (ONEMINUTEMILLIS) % 60));
        if (mSecondsView != null)
            mSecondsView.setText(String.format(LOCALE, FORMAT,
                    (millisUntilFinished + DELAYMILLIS) / (ONESECONDMILLIS) % 60));
    }

    // End of things
    public void end() {
        if (mICT != null) {
            mICT.cancel();
            mICT = null;
        }

        if (mCT != null) {
            mCT.cancel();
            mCT = null;
        }
    }

    public void addMinutes(long minutes) {
        this.mCount += minutes * ONEMINUTEMILLIS;
        if (this.mCount > COUNT_MAX)
            this.mCount = COUNT_MAX;
    }

    public void addSeconds(long seconds) {
        this.mCount += seconds * ONESECONDMILLIS;
        if (this.mCount > COUNT_MAX)
            this.mCount = COUNT_MAX;
    }

    // Interface Counter
    public interface CounterListener {
        // When the countdown has finished
        void onCountFinish();

        // When countdown is ticking
        void onCountTick();
    }

    // Interface to PreCounter
    public interface PreCounterListener {
        // When the countdown has finished
        void onPreCountFinish();

        // When pre-countdown is ticking
        void onPreCountTick();
    }

    // Builder
    public static class Builder {

        Counter counter = new Counter();

        public Builder setCounterListener(CounterListener listener) {
            counter.mListener = listener;
            return this;
        }

        public Builder setPreCounterListener(PreCounterListener listener) {
            counter.mPreListener = listener;
            return this;
        }

        public Builder setMinutesView(TextView view) {
            counter.mMinutesView = view;
            return this;
        }

        public Builder setSecondsView(TextView view) {
            counter.mSecondsView = view;
            return this;
        }

        public Builder setInitialTime(long time) {
            counter.mPreCount = time;
            return this;
        }

        public Builder setCount(long time) {
            counter.mCount = time;
            return this;
        }

        public Counter build() {
            if (counter.mCount >= COUNT_MIN && counter.mCount <= COUNT_MAX)
                return counter;

            return null;
        }
    }
}
