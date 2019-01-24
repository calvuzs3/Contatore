package org.varonesoft.luke.countdown;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;

public class Counter {

    public static final String FORMAT = "%02d";
    public static final Locale LOCALE = Locale.ITALY;

    public static final int COUNT_MIN = 3000; // three secs
    public static final int COUNT_MAX = 60 * 60 * 1000; // one hour

    // Audio
    private static final int VOLUME = 100;  // between 0-100
    private static final int TONE = AudioManager.STREAM_ALARM;  //

    private CountDownTimer mICT;                // first countdown
    private CountDownTimer mCT;                 // second
    private long mInterval = 1000L;       // interval onTick()
    private long mDelay = 100L;           // Delay
    private long mInitialCount = 5000L;   // millis before start
    private long mCount = 60000L;             // counter 1min

    private TextView mMinutesView;               // the view to adjourn
    private TextView mSecondsView;               // the view to adjourn

    private boolean mInited = false;
    private boolean mTone = false;
    private boolean mPlaySound = false;

    private Context mContext;
    private ToneGenerator toneGenerator;

    private OnFinishListener mListener;

    private Uri mSoundUri;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    // private constructor
    private Counter() {    }

    // Sets the context, init some..
    private void setContext(Context context) throws IllegalArgumentException,
            SecurityException,
            IllegalStateException,
            IOException {

        this.mContext = context;

        mSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(mContext, mSoundUri);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

    }

    // Pre-countdown
    public void start() {
        if (!mInited) return;
        if (mInitialCount == 0) {
            _start();
            return;
        }
//        mView.setText(String.format(LOCALE, FORMAT, mInitialCount / 1000));
        mICT = new CountDownTimer(mInitialCount, mInterval) {
            @Override
            public void onFinish() {
                // Adjourn th view
                mMinutesView.setText("00");
                mSecondsView.setText(String.format(LOCALE, FORMAT,
                        (mInitialCount + mDelay) / (1000) %60));
                // Start the real countdown
                _start();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                // Adjourn th view
                mMinutesView.setText("");
                mSecondsView.setText(String.format(LOCALE, FORMAT,
                        (millisUntilFinished + mDelay) / (1000) %60));
                // Emits a beep
            }
        };
        mICT.start();
    }

    // The real thing
    private void _start() {
        if (mCount < 1000) return;
        // Adjourn th view
        mMinutesView.setText(String.format(LOCALE, FORMAT,
                (mCount + mDelay) / (60*1000) %60));
        mSecondsView.setText(String.format(LOCALE, FORMAT,
                (mCount + mDelay) / (1000) %60));

        mCT = new CountDownTimer(mCount, mInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Emits a beep
                // Adjourn th view
                mMinutesView.setText(String.format(LOCALE, FORMAT,
                        (millisUntilFinished + mDelay) / (60*1000) %60));
                mSecondsView.setText(String.format(LOCALE, FORMAT,
                        (millisUntilFinished + mDelay) / (1000) %60));
            }

            @Override
            public void onFinish() {
                // Callback
                if (mListener != null) mListener.onFinish();
                // Emits beeps or play sound
                if (mContext == null) return;
                if (mPlaySound) try {
                    playSound(mContext);
                } catch (IOException e) {
                    Log.e("Counter", e.getMessage());
                    e.printStackTrace();
                }
                // Free resources??
            }
        };
        mCT.start();
    }

    public void end() {
        if (mICT != null) mICT.cancel();
        if (mCT != null) mCT.cancel();
    }

    public void playSound(Context context) throws IllegalArgumentException,
            SecurityException,
            IllegalStateException,
            IOException {
//
//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        MediaPlayer mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.setDataSource(context, soundUri);
//        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            // Uncomment to play it repeatedly
            // mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }
    }

    public void addMinutes(long minutes) {
        this.mCount += minutes * 60 * 1000;
        if (this.mCount > COUNT_MAX)
            this.mCount = COUNT_MAX - 1000;
    }

    public void addSeconds(long seconds) {
        this.mCount += seconds * 1000;
        if (this.mCount > COUNT_MAX)
            this.mCount = COUNT_MAX - 1000;
    }

    // Interface
    public interface OnFinishListener {
        void onFinish();
    }

    // Builder
    public static class Builder {

        Counter counter = new Counter();

        public Builder setContext(Context context) {
            try {
                counter.setContext(context);
            } catch (IOException e) {
                Log.e(Counter.class.getSimpleName(), "setContext Error");
                e.printStackTrace();
            }
            return this;
        }
        public Builder setListener(OnFinishListener listener) {
            counter.mListener = listener;
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
        public Builder setPlaySound(boolean sound) {
            counter.mPlaySound = sound;
            return this;
        }
        public Builder setInitialTime(long time) {
            counter.mInitialCount = time;
            return this;
        }
        public Builder setCount(long time) {
            counter.mCount = time;
            return this;
        }
        public Counter build() throws InternalError {
            if (counter.mCount > COUNT_MIN && counter.mCount < COUNT_MAX &&
                    counter.mMinutesView != null &&
                    counter.mSecondsView != null &&
                    counter.mInitialCount >= 0) {
                counter.mInited = true;
                return counter;
            }

            throw new InternalError("Invalid parameters to Class Counter");
        }
    }
}
