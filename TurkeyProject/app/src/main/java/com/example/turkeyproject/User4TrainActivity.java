package com.example.turkeyproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import pl.droidsonroids.gif.GifTextView;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class User4TrainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    YouTubePlayerView playerView;
    String API_KEY="htyhry";
    String[][] VIDEO_ID = {{"HOCPuGi6yLw", "rZmeCvfp3Qo", "akcm-CNrZ1o", "u2nQ_TDhYQw"}, {"HOCPuGi6yLw", "oy2hxmAVeFs", "X_Dvlabv7h0", "7A1uJ4dSfN0"}};
    private String PatientName,  DoctorName, strength;
    private int strength_index = 0;
    private int video_index = 0;
    private int video_size = 4;

    private GifTextView message1, message2;
    private TextView  title, message1_text, message2_text;
    private ObjectAnimator animator1,  animator2, animator3, animator4, animator5, animator6, animator7;
    private int time_l, time_r;
    private int wait;
    private String[] titleStr = {"該如何穿戴裝置呢？", "首先是...大腿運動!", "接著是...向上抬腳!", "最後是...側邊抬腿!"};
    private boolean ani_stop;

    /*bluetooth part*/
    private Intent intent;
    private int blemode;
    private final String path = "/storage/emulated/0/Android/data/acc_log.txt";
    private final String rtpath = "/storage/emulated/0/Android/data/accrt_log.txt";
    private FileOutputStream fos;
    private FileOutputStream fosrt;
    private Python py;
    private PyObject mod;
    private int timeCnt;
    private int rttime;
    private int[] results;
    private int resultIndex;
    private ArrayList<BluetoothGatt> gatts;
    //notify
    public final static UUID uartServiceUUIDString = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    //notify
    public final static UUID uartTXCharacteristicUUIDString = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    //send
    public final static UUID uartRXCharacteristicUUIDString = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final String TAG = "DataReceiverActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user4_train);

        PatientName = getIntent().getStringExtra("pName");
        DoctorName = getIntent().getStringExtra("dName");
        strength = getIntent().getStringExtra("strength");
        blemode = getIntent().getIntExtra("blemode", 0);

        findObject();
        ani_title();
        if(blemode == 1)
            bleInit();
        results = new int[3];
        resultIndex = 0;
        if(strength.equals("弱")){
            strength_index = 0;
        }
        else{
            strength_index = 1;
        }
        playerView.initialize(API_KEY,this);
    }

    public void findObject() {
        title = findViewById(R.id.textView_title);
        playerView = (YouTubePlayerView)findViewById(R.id.playerview);
        message1 = findViewById(R.id.message1);
        message2 = findViewById(R.id.message2);
        message1_text = findViewById(R.id.message1_text);
        message2_text = findViewById(R.id.message2_text);
    }

    public void bleInit() {
        gatts = new ArrayList<>();
        Intent intent = getIntent();
        ArrayList<BluetoothDevice> deviceToConnect = (ArrayList<BluetoothDevice>)intent.getSerializableExtra("devicesToConnect");
        for (int i = 0; i < deviceToConnect.size(); i++) {
            Log.d("TAG", "connect " + deviceToConnect.get(i).getName());
            BluetoothGatt gatt = deviceToConnect.get(i).connectGatt(this, false, bluetoothGattCallback);
            gatts.add(gatt);
            refreshDeviceCache(gatt);
        }

        timeCnt = 0;
        rttime = 0;
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
        py = Python.getInstance();
        mod = py.getModule("analysis");
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (youTubePlayer == null) {
            Log.d("CheckPoint", "CheckPoint youtubePlayer == null");
            return;
        }

        if (!wasRestored) {
            Log.d("CheckPoint", "CheckPoint !wasRestored");
            youTubePlayer.cueVideo(VIDEO_ID[strength_index][video_index]);
        }

        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {
                Log.d("CheckPoint", "CheckPoint onLoading");
            }

            @Override
            public void onLoaded(String s) {
                Log.d("CheckPoint", "CheckPoint onLoaded");
            }

            @Override
            public void onAdStarted() {
                Log.d("CheckPoint", "CheckPoint onAdStarted");
            }

            @Override
            public void onVideoStarted() {
                Log.d("CheckPoint", "CheckPoint onVideoStarted");
                if(blemode == 1){
                    rttime = 0;
                    if(video_index != 0)
                        startRecord();
                }
                if(video_index != 0) {
                    ani_stop = false;
                    wait = 0;
                    if(video_index == 1) {
                        time_l = -1;
                        time_r = -2;
                        ani_count(2100, 1500);
                    }
                    else if(video_index == 2) {
                        time_l = -2;
                        time_r = -1;
                        ani_count(250, 1200);
                    }
                    else if(video_index == 3) {
                        time_l = -1;
                        time_r = -2;
                        ani_count(1490, 1500);
                    }
                }
            }

            @Override
            public void onVideoEnded() {
                ani_stop = true;
                message1.setAlpha(0);
                message2.setAlpha(0);
                message1_text.setAlpha(0);
                message2_text.setAlpha(0);
                video_index++;
                if(video_index < video_size) {
                    ani_title();
                    youTubePlayer.cueVideo(VIDEO_ID[strength_index][video_index]);
                }
                if(blemode == 1 && video_index > 1)
                    stopRecord();
                Log.d("CheckPoint", "CheckPoint onVideoEnded");
                if(video_index == video_size) {
                    if(blemode == 0){
                        if(strength_index == 0){
                            results[0] = 16;
                            results[1] = 16;
                            results[2] = 12;
                        }
                        else{
                            results[0] = 32;
                            results[1] = 32;
                            results[2] = 20;
                        }
                    }
                    else{
                        results[0] *= 2;
                        results[2] *= 2;
                        if(strength_index == 0){
                            if(results[0] > 16)
                                results[0] = 16;
                            if(results[1] > 16)
                                results[1] = 16;
                            if(results[2] > 12)
                                results[2] = 12;
                        }
                        else{
                            if(results[0] > 32)
                                results[0] = 32;
                            if(results[1] > 32)
                                results[1] = 32;
                            if(results[2] > 20)
                                results[2] = 20;
                        }
                    }
                    Intent intent = new Intent(User4TrainActivity.this, User6ResultActivity.class);

                    intent.putExtra("pName", PatientName);
                    intent.putExtra("dName", DoctorName);
                    intent.putExtra("strength", strength);

                    intent.putExtra("resultArray", results);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {
                Log.d("CheckPoint", "CheckPoint onError = " + errorReason);
            }
        });
    }

    private void ani_title() {
        title.setText(titleStr[video_index]);

        if(playerView.getAlpha() == 1) {
            animator5 = ObjectAnimator.ofFloat(playerView, "alpha", 1, 0);
            animator5.setDuration(1000);
            animator5.start();
        }
        animator6 = ObjectAnimator.ofFloat(title, "alpha", 0, 1);
        animator6.setStartDelay(1000);
        animator6.setDuration(1000);
        animator6.start();

        animator7 = ObjectAnimator.ofFloat(title, "alpha", 1, 0);
        animator7.setStartDelay(2500);
        animator7.setDuration(1000);
        animator7.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animator5 = ObjectAnimator.ofFloat(playerView, "alpha", 0, 1);
                animator5.setDuration(1000);
                animator5.start();
            }
        });
        animator7.start();
    }

    public void ani_count(int delay, int duration) {
        if(ani_stop) {
            return;
        }
        message1.setAlpha(1);
        message2.setAlpha(1);
        message1_text.setAlpha(1);
        message2_text.setAlpha(1);

        if(blemode == 1){
            message1_text.setText("您做了 " + Integer.toString(rttime) + " 下 !");
            message2_text.setText("您做了 " + Integer.toString(rttime) + " 下 !");
        }
        else {
            if(wait != 2 && video_index == 1 && (time_l == 15 || time_r == 16)) {
                wait++;
                message1_text.setText("您做了 16 下 !");
                message2_text.setText("您做了 16 下 !");
            }
            else if(wait != 2 && video_index == 2 && (time_l == 15 || time_r == 16)) {
                wait++;
                message1_text.setText("您做了 16 下 !");
                message2_text.setText("您做了 16 下 !");
            }
            else if(wait != 1 && video_index == 3 && (time_l == 9 || time_r == 10)) {
                wait++;
                message1_text.setText("您做了 10 下 !");
                message2_text.setText("您做了 10 下 !");
            }
            else {
                time_l += 2;
                time_r += 2;
                message1_text.setText("您做了 " + Integer.toString(time_l) + " 下 !");
                message2_text.setText("您做了 " + Integer.toString(time_r) + " 下 !");
            }
        }

        animator1 = ObjectAnimator.ofFloat(message1, "translationY", 700, 750, 0);
        animator1.setStartDelay(delay);
        animator1.setDuration(duration);

        animator2 = ObjectAnimator.ofFloat(message2, "translationY", 700, 750, 0);
        animator2.setStartDelay(delay * 2);
        animator2.setDuration(duration);

        animator3 = ObjectAnimator.ofFloat(message1_text, "translationY", 750, 800, 0);
        animator3.setStartDelay(delay);
        animator3.setDuration(duration);

        animator4 = ObjectAnimator.ofFloat(message2_text, "translationY", 750, 800, 0);
        animator4.setStartDelay(delay * 2);
        animator4.setDuration(duration);

        animator4.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        animator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ani_count(delay, duration);
            }
        });

        animator1.start();
        animator2.start();
        animator3.start();
        animator4.start();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show();
    }

    // start
    public void startRecord() {

        try {
            fos = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < gatts.size(); i++) {
            BluetoothGatt gatt = gatts.get(i);
            BluetoothGattService service = gatt.getService(uartServiceUUIDString);
            Log.d(TAG, service.toString());
            BluetoothGattCharacteristic txCharacteristic= service.getCharacteristic(uartTXCharacteristicUUIDString);
            gatt.readCharacteristic(txCharacteristic);
            BluetoothGattCharacteristic rxCharacteristic= service.getCharacteristic(uartRXCharacteristicUUIDString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) +1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR);
            int min = cal.get(Calendar.MINUTE);
            int sec = cal.get(Calendar.SECOND);
            int ms = cal.get(Calendar.MILLISECOND);
            int msMinor = ms % 100;
            int msMajor = ms / 100;
            year -= 2000;

            byte [] data= { (byte)0x55, (byte)0x01, (byte)0x08, (byte)year, (byte)month, (byte)day, (byte)hour, (byte)min, (byte)sec, (byte)msMajor, (byte)msMinor, (byte)0xaa };
            rxCharacteristic.setValue(data);
            gatt.writeCharacteristic(rxCharacteristic);

            gatt.setCharacteristicNotification(txCharacteristic, true);

            BluetoothGattDescriptor descriptor = txCharacteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
    }

    // stop
    public void stopRecord() {

        for (int i = 0; i < gatts.size(); i++) {
            BluetoothGatt gatt = gatts.get(i);
            BluetoothGattService service = gatt.getService(uartServiceUUIDString);
            Log.d(TAG, service.toString());

            BluetoothGattCharacteristic txCharacteristic= service.getCharacteristic(uartTXCharacteristicUUIDString);
            gatt.readCharacteristic(txCharacteristic);
            BluetoothGattCharacteristic rxCharacteristic= service.getCharacteristic(uartRXCharacteristicUUIDString);

            byte [] data= { (byte)0x55, (byte)0x07, (byte)0x00, (byte)0xaa};
            rxCharacteristic.setValue(data);

            gatt.writeCharacteristic(rxCharacteristic);
            gatt.setCharacteristicNotification(txCharacteristic, true);
            BluetoothGattDescriptor descriptor = txCharacteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double period = 22.22;
        if(video_index == 2){
            period = 25;
        }
        PyObject py_ret = mod.callAttr("count_time", path, period);
        int cnt = py_ret.toInt();
        Log.d(TAG, "count_long " + Integer.toString(cnt));
        results[resultIndex] = cnt;
        if(resultIndex == 1)
            results[resultIndex] = cnt * 2;
        resultIndex++;
        // total counts
    }

    BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "onConnectionStateChange");
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "connect");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "disconnect");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.d(TAG, "onCharacteristicRead");
        }


        // per 0.4 sec
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            byte[] values = characteristic.getValue();

            short x1 = ByteBuffer.wrap(new byte[] {values[5], values[4]}).getShort();
            short y1 = ByteBuffer.wrap(new byte[] {values[25], values[24]}).getShort();
            short z1 = ByteBuffer.wrap(new byte[] {values[45], values[44]}).getShort();
            // Log.d(TAG, "x: " + x1 +", y: " + y1 + ", z: " + z1);
            String str_result = "\ntime: " + ByteBuffer.wrap(new byte[] {values[3], values[2], values[1], values[0]}).getInt();
            ArrayList<Double> arrAvg = new ArrayList<Double>(10);
            String formatX = "\nx[%d]: = %-4d";
            String formatY = ", y[%d]: = %-4d";
            String formatZ = ", z[%d]: = %-4d";
            String formatA = ", avg[%d]: = %-4f";
            for(int i=0; i<10; ++i){
                int a = i * 2;
                short x = ByteBuffer.wrap(new byte[] {values[5+a], values[4+a]}).getShort();
                short y = ByteBuffer.wrap(new byte[] {values[25+a], values[24+a]}).getShort();
                short z = ByteBuffer.wrap(new byte[] {values[45+a], values[44+a]}).getShort();
                double avg = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                arrAvg.add(avg);
                str_result += String.format(formatX, i, x);
                str_result += String.format(formatY, i, y);
                str_result += String.format(formatZ, i, z);
                str_result += String.format(formatA, i, avg);
            }

            timeCnt++;

            if(timeCnt == 1){
                try{
                    fosrt = new FileOutputStream(rtpath);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }

            try {
                fos.write(str_result.getBytes());
                fosrt.write(str_result.getBytes());

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }


            if(timeCnt == 7){
                timeCnt = 0;
                double period = 22.22;
                if(video_index == 1){
                    period = 25;
                }
                try{
                    fosrt.close();
                    PyObject py_ret = mod.callAttr("count_time", rtpath, period);
                    int cnt = py_ret.toInt();
                    rttime += cnt;

                    Log.d(TAG, "rt " + Integer.toString(cnt));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        }
        catch (Exception localException) {
            Log.e(TAG, "An exception occured while refreshing device");
        }
        return false;
    }
}
