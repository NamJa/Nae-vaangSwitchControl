package com.example.jongwookim.naebang;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import me.aflak.bluetooth.Bluetooth;

public class MainActivity extends AppCompatActivity {


    FrameLayout noon, night;
    ImageView yellowonoff, whiteonoff;
    Button noonBtn, nightBtn, onehundreddegree, tendegree;

    public float noonScaleXY = 1.0f, nightScaleXY = 1.0f, animationSpeed = 0.005f;
    public int i = 100;
    public boolean nooncnt = false, nightcnt = false;
    public int noonComplete = 0, nightComplete = 0;

    public Timer noonTimer, nightTimer, noonCompTimer, nightCompTimer;

    GradientDrawable drawable;
    RelativeLayout.LayoutParams lp;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "00:21:13:00:1D:37";
    private static final String TAG = "bluetooth1";






    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }
    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
        sendData("c");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            errorExit("Fatal Error", msg);
        }
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
        onehundreddegree = (Button)findViewById(R.id.onehundreddegree);
        tendegree = (Button)findViewById(R.id.tendegree);
//        drawable = (GradientDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.ripple_background);
//        drawable.setCornerRadius(10.0f);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        onehundreddegree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("a");
                Toast.makeText(getApplicationContext(), "send 'a'", Toast.LENGTH_SHORT).show();
            }
        });

        tendegree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("b");
                Toast.makeText(getApplicationContext(), "send 'b'", Toast.LENGTH_SHORT).show();
            }
        });

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
