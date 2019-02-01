package org.varonesoft.luke.contatore;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.varonesoft.luke.countdown.Counter;

import java.util.Locale;

import static org.varonesoft.luke.countdown.Counter.ONEMINUTEMILLIS;
import static org.varonesoft.luke.countdown.Counter.ONESECONDMILLIS;

//import android.media.RingtoneManager;
//import android.net.Uri;


public class MainActivity extends AppCompatActivity implements Counter.CounterListener, Counter.PreCounterListener {

    // TAG
    private static final String TAG = "Contatore";

    // Static initial counter time
    private static final long COUNT = 60000L;

    // Counter
    private long mCount = 60000;

    // Preferences
    private boolean mPlaySound;

    // Binding widgets
    private Button btnStart;
    private ImageButton btnMinutesUp;
    private ImageButton btnMinutesDown;
    private ImageButton btnSecondsUp;
    private ImageButton btnSecondsDown;
    private TextView textMinutes;
    private TextView textSeconds;

    // Counter object
    private Counter mCounter;
    // Sounds related
    private AudioSTATE mAudioState;
    private MediaPlayer mMediaPlayerTick;
    // Listeners
    View.OnClickListener stopListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stop();
        }
    };
    View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            start();
        }
    };
    private MediaPlayer mMediaPlayerFinish;
    private AudioManager mAudioManager;
    private IntentFilter mAudioIntenteFilter;
    private BecomingNoisyReceiver mAudioBecomingNoisyReciver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.button);
        btnMinutesDown = findViewById(R.id.minutes_down);
        btnMinutesUp = findViewById(R.id.minutes_up);
        btnSecondsDown = findViewById(R.id.seconds_down);
        btnSecondsUp = findViewById(R.id.seconds_up);

        textMinutes = findViewById(R.id.minutes);
        textSeconds = findViewById(R.id.seconds);

        // Set preference
        mCount = Preferences.getLastCounterUsed(this);
        mPlaySound = Preferences.getPlaySoundPreference(this);

        // Init
        initWidgets();
    }

    private void adjournViews() {
        textMinutes.setText(
                (mCount > 0) ? String.format(Locale.ITALY, Counter.FORMAT, mCount /
                        (60 * ONESECONDMILLIS) % 60) : getString(R.string.str_00));
        textSeconds.setText(
                (mCount > 0) ? String.format(Locale.ITALY, Counter.FORMAT, mCount /
                        (ONESECONDMILLIS) % 60) : getString(R.string.str_00));
    }

    private void initWidgets() {
        if (mCount == 0) mCount = COUNT;
        btnStart.setOnClickListener(startListener);

        btnMinutesUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCount += ONEMINUTEMILLIS;
                if (mCount > Counter.COUNT_MAX) mCount = Counter.COUNT_MAX;
                adjournViews();
            }
        });
        btnMinutesDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCount -= ONEMINUTEMILLIS;
                if (mCount < Counter.COUNT_MIN) mCount = Counter.COUNT_MIN;
                adjournViews();
            }
        });
        btnSecondsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCount += ONESECONDMILLIS;
                if (mCount > Counter.COUNT_MAX) mCount = Counter.COUNT_MAX;
                adjournViews();
            }
        });
        btnSecondsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCount -= ONESECONDMILLIS;
                if (mCount < Counter.COUNT_MIN) mCount = Counter.COUNT_MIN;
                adjournViews();
            }
        });
        adjournViews();
    }

    private void initSound() {
        mAudioManager = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
        mAudioIntenteFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        mAudioBecomingNoisyReciver = new BecomingNoisyReceiver();
//        Uri mSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mMediaPlayerFinish = MediaPlayer.create(this,
                R.raw.zapsplat_bell_service_disk_ring_slightly_broken_resonate_multiple_18041);
        mMediaPlayerTick = MediaPlayer.create(MainActivity.this,
                R.raw.zapsplat_bell_service_disk_ring_slightly_broken_resonate_18042);
//        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.setDataSource(MainActivity.this, mSoundUri);

        mAudioState = AudioSTATE.isStopped;
    }

    private void playFinishSound() {
        mAudioState = AudioSTATE.isPlaying;
        if (mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            if (mMediaPlayerFinish != null) {
                //            mMediaPlayerFinish.setAudioStreamType(AudioManager.STREAM_ALARM);
                // Do we want to play it repeatedly?
                //             mMediaPlayerFinish.setLooping(true);
                mMediaPlayerFinish.start();
            }
        }
    }

    private void playPreCountdownTickSound() {
        mAudioState = AudioSTATE.isPlaying;
        if (mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            if (mMediaPlayerTick != null) {
                //            mMediaPlayerTick.setAudioStreamType(AudioManager.STREAM_ALARM);
                // Do we want to play it repeatedly?
                //            mMediaPlayerTick.setLooping(true);
                mMediaPlayerTick.start();
            }
        }
    }

    private void stopPlaying() {
        mAudioState = AudioSTATE.isStopped;
        mMediaPlayerTick.stop();
        try {
             mMediaPlayerTick.prepare();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        mMediaPlayerFinish.stop();
        try {
            mMediaPlayerFinish.prepare();
        } catch (Exception e ){
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Init the sound system
        initSound();
        // Lets register our receiver
        if (mPlaySound) {
            registerReceiver(mAudioBecomingNoisyReciver, mAudioIntenteFilter);
        }
    }

    @Override
    protected void onPause() {
        // Unregister
        if (mPlaySound) {
            unregisterReceiver(mAudioBecomingNoisyReciver);
        }
        if (mAudioState.equals(AudioSTATE.isPlaying)) {
            // Stop the sound system
            stopPlaying();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        // Stop playing
//        stopPlaying();
        // Release resources
        mMediaPlayerFinish.release();
        mMediaPlayerFinish = null;
        mMediaPlayerTick.release();
        mMediaPlayerTick = null;

        // Set preference
        Preferences.setLastCounterUsed(this, mCount);
        Preferences.setPlaySoundPreference(this, mPlaySound);

        super.onStop();
    }


    private void start() {
        mCounter = new Counter.Builder()
                .setCount(mCount)
                .setMinutesView(textMinutes)
                .setSecondsView(textSeconds)
                .setCounterListener(this)
                .setPreCounterListener(this)
                .build();
        btnStart.setText(R.string.str_stop);
        btnStart.setOnClickListener(stopListener);

        btnSecondsDown.setEnabled(false);
        btnSecondsDown.setVisibility(View.INVISIBLE);
        btnSecondsUp.setEnabled(false);
        btnSecondsUp.setVisibility(View.INVISIBLE);
        btnMinutesDown.setEnabled(false);
        btnMinutesDown.setVisibility(View.INVISIBLE);
        btnMinutesUp.setEnabled(false);
        btnMinutesUp.setVisibility(View.INVISIBLE);
        mCounter.start();
    }

    private void stop() {
        // First stop playing if it is
        if (mAudioState.equals(AudioSTATE.isPlaying))
            stopPlaying();
        // Reset the counter
        btnStart.setText(R.string.str_start);
        btnStart.setOnClickListener(startListener);
        mCounter.end();

        btnSecondsDown.setEnabled(true);
        btnSecondsDown.setVisibility(View.VISIBLE);
        btnSecondsUp.setEnabled(true);
        btnSecondsUp.setVisibility(View.VISIBLE);
        btnMinutesDown.setEnabled(true);
        btnMinutesDown.setVisibility(View.VISIBLE);
        btnMinutesUp.setEnabled(true);
        btnMinutesUp.setVisibility(View.VISIBLE);
        adjournViews();
    }

    /**
     * Interfaces
     * Comunication with the Counter object
     */
    @Override
    public void onCountFinish() {

        // Playsound
        playFinishSound();
    }

    @Override
    public void onCountTick() {
        // nothing
    }

    @Override
    public void onPreCountFinish() {
        //nothing
    }

    @Override
    public void onPreCountTick() {
        // Playsound
        playPreCountdownTickSound();
    }

    // AudiSTATE
    private enum AudioSTATE {
        isPlaying,
        isPaused,
        isStopped
    }

    // TODO: implement the intent action filter, the reciver can comunicate with intents..
}
