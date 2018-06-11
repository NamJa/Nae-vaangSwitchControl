package com.example.jongwookim.naebang;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    FrameLayout noon, night;
    ImageView yellowonoff, whiteonoff;
    Button noonBtn, nightBtn;

    public float noonScaleXY = 1.0f, nightScaleXY = 1.0f, animationSpeed = 0.005f;
    public int i = 100;
    public boolean nooncnt = false, nightcnt = false;
    public int noonComplete = 0, nightComplete = 0;

    public Timer noonTimer, nightTimer, noonCompTimer, nightCompTimer;

    GradientDrawable drawable;
    RelativeLayout.LayoutParams lp;

    public void nightbringtofront()
    {
        night.bringToFront();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noon = (FrameLayout)findViewById(R.id.noon);
        night =(FrameLayout)findViewById(R.id.night);

        whiteonoff = (ImageView)findViewById(R.id.whiteonoff);
        yellowonoff = (ImageView)findViewById(R.id.yellowonoff);

        noonBtn = (Button)findViewById(R.id.noonBtn);
        nightBtn = (Button)findViewById(R.id.nightBtn);
//        drawable = (GradientDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.ripple_background);
//        drawable.setCornerRadius(10.0f);


        noonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // noon framelayout이 줄어들면서 nightframelayout을 앞으로 배치
                if(nooncnt == false)
                {
                    TimerTask timerTask = new TimerTask()
                    {
                        @Override
                        public void run() {
                            if(noonScaleXY > 0.2f)
                            {
                                noon.setScaleX(noonScaleXY);
                                noon.setScaleY(noonScaleXY);
                                noonScaleXY-=animationSpeed;
                            }else {
                                noonTimer.cancel();
                            }
                        }
                    };
                    noonTimer = new Timer();
                    noonTimer.schedule(timerTask, 0, 1);
                    nooncnt = true;

                    final Handler handler = new Handler()
                    {
                        public void handleMessage(Message msg)
                        {
                            night.bringToFront();
                            yellowonoff.bringToFront();
                        }
                    };
                    TimerTask nightbringTask = new TimerTask() {
                        @Override
                        public void run() {
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                        }
                        @Override
                        public boolean cancel()
                        {
                            noonCompTimer.cancel();
                            return super.cancel();
                        }
                    };
                    noonCompTimer = new Timer();
                    noonCompTimer.schedule(nightbringTask, 200);
                }
                else
                    {
                        TimerTask timerTask = new TimerTask()
                        {
                            @Override
                            public void run() {
                                if(noonScaleXY <= 1.0f)
                                {
                                    noon.setScaleX(noonScaleXY);
                                    noon.setScaleY(noonScaleXY);
                                    noonScaleXY+=animationSpeed;
                                }
                                else {
                                    noonTimer.cancel();
                                }
                            }
                        };
                        noonTimer = new Timer();
                        noonTimer.schedule(timerTask, 0, 1);
                        nooncnt = false;
                }
            }
        });





        nightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // night framelayout이 줄어들면서 noon framelayout을 앞으로 배치
                if(nightcnt == false)
                {
                    TimerTask timerTask = new TimerTask()
                    {
                        @Override
                        public void run() {
                            if(nightScaleXY > 0.2f)
                            {
                                night.setScaleX(nightScaleXY);
                                night.setScaleY(nightScaleXY);
                                nightScaleXY-=animationSpeed;
                            }else {

                                nightTimer.cancel();
                            }
                        }
                    };
                    nightTimer = new Timer();
                    nightTimer.schedule(timerTask, 0, 1);
                    nightcnt = true;
                    final Handler handler = new Handler()
                    {
                        public void handleMessage(Message msg)
                        {
                            noon.bringToFront();
                            whiteonoff.bringToFront();
                        }
                    };
                    TimerTask noonbringTask = new TimerTask() {
                        @Override
                        public void run() {
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                        }
                        @Override
                        public boolean cancel()
                        {
                            nightCompTimer.cancel();
                            return super.cancel();
                        }
                    };
                    nightCompTimer = new Timer();
                    nightCompTimer.schedule(noonbringTask, 200);
                }
                else
                {
                    TimerTask timerTask = new TimerTask()
                    {
                        @Override
                        public void run() {
                            if(nightScaleXY <= 1.0f)
                            {
                                night.setScaleX(nightScaleXY);
                                night.setScaleY(nightScaleXY);
                                nightScaleXY+=animationSpeed;
                            }
                            else {
                                nightTimer.cancel();
                            }
                        }
                    };
                    nightTimer = new Timer();
                    nightTimer.schedule(timerTask, 0, 1);

                    nightcnt = false;
                }
            }
        });


    }

}
