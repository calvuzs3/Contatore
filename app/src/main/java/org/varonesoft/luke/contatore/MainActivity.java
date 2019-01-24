package org.varonesoft.luke.contatore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.varonesoft.luke.countdown.Counter;

public class MainActivity extends AppCompatActivity {

    static final long COUNT = 60000L;

    long mCount = 60000;
    Button btnStart;
    ImageButton btnMinutesUp;
    ImageButton btnMinutesDown;
    ImageButton btnSecondsUp;
    ImageButton btnSecondsDown;
    TextView textMinutes;
    TextView textSeconds;

    Counter mCounter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FullScreen activity..
//        Button btn2 = findViewById(R.id.button2);
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, Countdown.class);
//                startActivity(i);
//                finish();
//            }
//        });

        btnStart = findViewById(R.id.button);
        btnMinutesDown = findViewById(R.id.minutes_down);
        btnMinutesUp = findViewById(R.id.minutes_up);
        btnSecondsDown = findViewById(R.id.seconds_down);
        btnSecondsUp = findViewById(R.id.seconds_up);

        textMinutes = findViewById(R.id.minutes);
        textSeconds = findViewById(R.id.seconds);

        init();
    }

    private void adjournViews() {
        textMinutes.setText(
                (mCount > 0) ? String.format(Counter.FORMAT, mCount / (60 * 1000) % 60) : "00");
        textSeconds.setText(
                (mCount > 0) ? String.format(Counter.FORMAT, mCount / (1000) % 60) : "00");
    }

    private void init() {
        if (mCount == 0) mCount = COUNT;
        btnStart.setOnClickListener(startListener);

        btnMinutesUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCount += 1 * 60 * 1000;
                if (mCount > Counter.COUNT_MAX) mCount = Counter.COUNT_MAX;
                adjournViews();
            }
        });
        btnMinutesDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCount -= 1 * 60 * 1000;
                if (mCount < Counter.COUNT_MIN) mCount = Counter.COUNT_MIN;
                adjournViews();
            }
        });
        btnSecondsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCount += 1 * 1000;
                if (mCount > Counter.COUNT_MAX) mCount = Counter.COUNT_MAX;
                adjournViews();
            }
        });
        btnSecondsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCount -= 1 * 1000;
                if (mCount < Counter.COUNT_MIN) mCount = Counter.COUNT_MIN;
                adjournViews();
            }
        });
        adjournViews();
    }

    private void start() {
        mCounter = new Counter.Builder()
                .setCount(mCount)
                .setMinutesView(textMinutes)
                .setSecondsView(textSeconds)
                .setContext(this.getApplicationContext())
                .setPlaySound(true)
                .build();
        btnStart.setText("STOP");
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
//        mCounter = new Counter(10000, text);
        btnStart.setText("START");
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
}
