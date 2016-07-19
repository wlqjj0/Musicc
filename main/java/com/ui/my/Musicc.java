package com.ui.my;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;

/**
 * Created by wl on 2016/7/3.
 */
public class Musicc extends Activity {

    public static final String MUSICBOX_ACTION = "MUS_ACTION";
    //MusicService接收器所能响应的Action
    public static final String MUSICSERVIC_ACTION = "MUS1_ACTION";
    public static final String MUSICTIM_ACTION = "MUS2_ACTION";
    public static final String MUSICTIMM_ACTION = "MUS4_ACTION";
    public static final int STATE_NON = 0x122;
    //播放的flag
    public static final int STATE_PLAY = 0x123;
    //暂停的flag
    public static final int STATE_PAUSE = 0x124;
    //停止放的flag
    public static final int STATE_STOP = 0x125;
    //播放下一首的flag
    public static final int STATE_NEXT=0x125;
    //播放的flag
    public static final int STATE_CQ=0x126;
    //播放下一首的flag
    public static final int STATE_PRE=0x127;

    private Button bu1;
    private Button next,previous;
    private SeekBar seek;
    private TextView te1, te2, te3=null,te4,te5;
    boolean isplaying = false;
    private RelativeLayout rell;
    private ImageView image;

    //Ser ser1 = new Ser();
    Start2 s2 ;
    int i=0;
    //boolean frist=true;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        bu1 = (Button) findViewById(R.id.bu1);
        next = (Button) findViewById(R.id.next);
        previous = (Button) findViewById(R.id.previous);
        seek = (SeekBar) findViewById(R.id.seek);
        te1 = (TextView) findViewById(R.id.te1);
        te2 = (TextView) findViewById(R.id.te2);
        te3 = (TextView) findViewById(R.id.te3);
        te4 = (TextView) findViewById(R.id.te4);
        te5 = (TextView) findViewById(R.id.te5);
        rell=(RelativeLayout)findViewById(R.id.Re1);
        image = (ImageView) findViewById(R.id.im1);

        this.seek.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());
        s2 = new Start2();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(MUSICBOX_ACTION);
        filter1.addAction(MUSICTIM_ACTION);
        filter1.addAction(MUSICTIMM_ACTION);
        registerReceiver(s2, filter1);
        dainji();
        //Intent in = new Intent(Musicc.this, Ser.class);
       // startService(in);
        Log.d("iii","MUSICCHHHHHHHHHHAND启动。。。。。。。。。。。");
        hand.post(updateThread);
        hand.post(updateThread1);
        //seek.setProgress(100);
       // rell.getBackground().setAlpha(100);//0~255透明度值
      //  image.getBackground().setAlpha(100);
    }

    private void dainji() {
        View.OnClickListener mi = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent in = new Intent(Musicc.this, Ser.class);
                switch (v.getId()) {
                    case R.id.bu1:
                        if (!isplaying) {
                            sendBroadcastToService(STATE_PLAY);
                            bu1.setBackgroundResource(R.drawable.pause);
                           // sendBroadcastToService1(STATE_PLAY);
                            hand.post(updateThread);
                            isplaying = true;
                        }else if (isplaying) {
                            sendBroadcastToService(STATE_PAUSE);
                            bu1.setBackgroundResource(R.drawable.play);
                            //sendBroadcastToService1(0);
                            //hand.removeCallbacks(updateThread);
                            isplaying = false;
                        }
                        // startService(in);
                        break;
                    case R.id.previous: // 上一首
                        previous.setBackgroundResource(R.drawable.previous_glow);
                        bu1.setBackgroundResource(R.drawable.play);
                        bu1.setBackgroundResource(R.drawable.pause);
                        isplaying=true;
                        sendBroadcastToService(STATE_PRE);
                    break;
                    case R.id.next: // 下一首
                        next.setBackgroundResource(R.drawable.next_glow);
                        bu1.setBackgroundResource(R.drawable.play);
                        bu1.setBackgroundResource(R.drawable.pause);
                        isplaying=true;
                        sendBroadcastToService(STATE_NEXT);
                        break;
                }
            }
        };
        bu1.setOnClickListener(mi);
        previous.setOnClickListener(mi);
        next.setOnClickListener(mi);
    }

    protected void sendBroadcastToService(int state) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(MUSICSERVIC_ACTION);
        intent.putExtra("control", state);
        //向后台Service发送播放控制的广播
        Log.d("ttt", "广播已发送。。");
        sendBroadcast(intent);
    }

    protected void sendBroadcastToService1(int state) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Main_ACTION");
        intent.putExtra("MS", state);
        Log.d("ttt", "告诉主活动。。");
        sendBroadcast(intent);
    }

    public class Start2 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MUSICTIM_ACTION)) {
                int mus = intent.getIntExtra("mus", 0);
               // Log.d("uuu", "位置位置位置位置位置位置位置位置广播收到 偶我");
                seek.setProgress(mus);

            }
            if (action.equals(MUSICBOX_ACTION)) {
              //  Log.d("uuu", "位置广播已已已已已已已已收到 偶我");
                int control = intent.getIntExtra("control", -1);
                seek.setMax(control);
                seek.setProgress(Ser.me.getCurrentPosition());
                musicti(control);
                hand.post(updateThread);
                hand.post(updateThread1);
            }
            if (action.equals(MUSICTIMM_ACTION)) {
                String stringValue=intent.getStringExtra("til");
                Log.d("uuu", "收到歌曲主题");
                te4.setText(stringValue);
            }

        }
    }

    @Override
    public void onStart() {
        sendBroadcastToService(STATE_CQ);
        if(Ser.me!=null && Ser.me.isPlaying()){
            bu1.setBackgroundResource(R.drawable.pause);
            isplaying=true;
        }
        super.onStart();
        Log.d("uuu", "在onstart 启动service。。。");

       // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Musicc Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.ui.my/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        hand.removeCallbacks(updateThread);
        hand.removeCallbacks(updateThread1);
        super.onStop();
        Log.d("uuu", "在onstooooooooooooooopremoveCallbacks。");
       // unregisterReceiver(s2);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Musicc Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.ui.my/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onPause() {
        Log.d("uuu", "在onPPPPPPPPPPPPPPPPPause启动。");
        super.onPause();
    }
    @Override
    public void onRestart() {
        Log.d("uuu", "在onRRRRRRRRRRestart启动。");
        super.onRestart();

    }
    @Override
    public void onResume() {
        Log.d("uuu", "在onRRRRRRRRRRRRRRRRResume启动。");
        super.onResume();
        Log.d("uuu", "STATE_CQ777777777777777");
        //sendBroadcastToService(STATE_CQ);


    }
    @Override
    public void onDestroy() {
        Log.d("uuu", "在onDDDDDDDDDDDDDDDDDDDDDDestroy启动。");
        unregisterReceiver(s2);
        super.onDestroy();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("uuu", "在onSSSSSSSSSSSSSSSSSSSSSSaveInstanceState启动。");
        outState.putInt("seed", seek.getMax());
        Log.v("tag", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onRestoreInstanceState(Bundle outState) {
        Log.d("uuu", "在onREEEEEEEEEEEEEEEEestoreInstanceState启动。");
        int in=outState.getInt("seed");
        seek.setMax(in);
        super.onRestoreInstanceState(outState);
    }

           //拖动进度条最终只能用stop。。。。。
    class OnSeekBarChangeListenerImp implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

           // Ser.me.seekTo(seek.getProgress());  //
           // te3.append(seek.getProgress()+""+"\n");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            Ser.me.seekTo(seek.getProgress());
            Log.d("mmm","拖动进度条 "+seek.getProgress());
        }
    }

    protected void musicti(int ss) {
        // int ss = Ser.me.getDuration();

        if(Ser.me.isPlaying()){isplaying=true;}
        //seek.setMax(ss);
        //seek.setProgress(Ser.me.getCurrentPosition());
        Log.d("uuu","调用musicti函数。。。");
            /*如果有数字，使用String#format方法来格式化
            * 别硬编码，而是使用Android的String资源文件
            * 别使用+进行字符串拼接*/
        String ti1=Audio.formatTime(ss);
        te2.setText(ti1);

    }

    public Handler hand = new Handler();
    Runnable updateThread = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(Ser.me!=null && Ser.me.isPlaying()){
            int ti1=Ser.me.getCurrentPosition();
            //int chd=Ser.me.getDuration();
                //seek.setMax(chd);
            seek.setProgress(ti1);
            hand.postDelayed(updateThread, 100);
            }
        }
    };
    Runnable updateThread1 = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(isplaying) {
                String ti1 = Audio.formatTime(Ser.me.getCurrentPosition());
                te1.setText(ti1);
                String sb=((Mucc) getApplication()).getMyMu();
                te4.setText(sb);
                String sb1=((Mucc) getApplication()).getNam();
                te5.setText(sb1);
                int sb2=((Mucc) getApplication()).getDuc();

                hand.postDelayed(updateThread1, 1000);
            }
        }
    };
}