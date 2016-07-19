package com.ui.my;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   // private Button bu1;
   public static final String MUSICBOXX_ACTION = "MUS_ACTION";
    //MusicService接收器所能响应的Action
    public static final String MUSICSERVIC_ACTION = "MUS1_ACTION";
    public static final String MUSICTIM_ACTION = "MUS2_ACTIO";
    public static final String MUSICSer_ACTION = "MUS3_ACTION";
    public static final String MUSICTIMM_ACTION = "MUS4_ACTION";
    public static final int STATE_PLAY = 0x123;
    //暂停的flag
    public static final int STATE_PAUSE = 0x124;
    //停止放的flag
    public static final int STATE_STOP = 0x125;
    //播放下一首的flag
    public static final int STATE_NEXT=0x125;
    private Button playBtn; // 播放（播放、暂停）
    private Button next;
    private boolean isFirstTime = true;
    private boolean isPlaying = false;
    private boolean isPause = false;
    private RelativeLayout rell;
    private TextView te1,te2;
    private ListView mMusiclist;
    private List<Mp3Info> mp3Infos = null;
    private SimpleAdapter mAdapter; // 简单适配器
    private int listPosition = 0;   //标识列表位置
    private int currentTime;
    private int duration;
    private int repeatState;        //循环标识
    private boolean isShuffle = false; // 随机播放
    Start3 s3;

    /**音乐播放器
     * 1 要在xml中给予写入权限
     * 2 音乐要加载完成才能播放 prepare();
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s3 = new Start3();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("Main_ACTION");
        registerReceiver(s3, filter1);
       // bu1 = (Button) findViewById(R.id.bu1);
        mMusiclist=(ListView)findViewById(R.id.music_list);
        te1=(TextView)findViewById(R.id.text1);
        te2=(TextView)findViewById(R.id.text2);
        playBtn=(Button)findViewById(R.id.play_muc);
        next=(Button)findViewById(R.id.next) ;
        rell=(RelativeLayout)findViewById(R.id.music_about_layout);
        rell.setOnClickListener(li);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        mMusiclist.setOnItemClickListener(new MusicListItemClickListener());
        mp3Infos = Audio.getMp3Infos(getApplicationContext());  //获取歌曲对象集合
        setListAdpter(Audio.getMusicMaps(mp3Infos));    //显示歌曲列表
        ViewOnClickListener viewOnClickListener = new ViewOnClickListener();
        playBtn.setOnClickListener(viewOnClickListener);
        next.setOnClickListener(viewOnClickListener);//
    }

    private View.OnClickListener li = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent in = new Intent();
            in.setClass(MainActivity.this, Musicc.class);
            MainActivity.this.startActivity(in);

        }
    };

    private class MusicListItemClickListener implements AdapterView.OnItemClickListener {
        @Override

        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if(mp3Infos != null) {
                Mp3Info mp3Info = mp3Infos.get(position);
                Log.d("mp3Info-->", mp3Info.toString());
                listPosition = position;
                String sbb=mp3Info.getTitle();
                Intent intent = new Intent();
                intent.putExtra("url", mp3Info.getUrl());
                intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
                intent.putExtra("listPosition",listPosition);
                intent.setClass(MainActivity.this, Ser.class);
                startService(intent);       //启动服务
                //((Mucc) getApplication()).setMyMu(sbb);//赋值操作
                te1.setText(sbb);
                hand.post(updateThread);
                isFirstTime=false;
                isPlaying = true;
                isPause = false;
                playBtn.setBackgroundResource(R.drawable.pause);
                Log.d("mmm","LLLLLLLLLLLLLLLLLLLlistPosition = "+listPosition);
            }
        }


        // playMusic(listPosition);

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
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
    public void onPause() {
        Log.d("uuu", "在MMMMMMMMMMMMMMMMMMonPPPPPPPPPPPPPPPPPause启动。");
        super.onPause();
    }
    @Override
    public void onRestart() {
        Log.d("uuu", "在MMMMMMMMMMMMMMMMMMonRRRRRRRRRRestart启动。");
        sendBroadcastToService(listPosition);
        super.onRestart();

    }
    @Override
    public void onResume() {
        Log.d("uuu", "在onMMMMMMMMMMMMMMMMMMRRRRRRRRRRRRRRRRResume启动。");
        super.onResume();
        Log.d("uuu", "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");


    }
    @Override
    public void onDestroy() {
        Log.d("uuu", "在MMMMMMMMMMMMMMMMMMonDDDDDDDDDDDDDDDDDDDDDDestroy启动。");
        hand.removeCallbacks(updateThread);
        unregisterReceiver(s3);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("uuu", "d调用onkey 不会杀死进程。。。。");
            moveTaskToBack(true);//true对任何Activity都适用
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 填充列表
     *
     * @param mp3list
     */
    public void setListAdpter(List<HashMap<String, String>> mp3list) {
        mAdapter = new SimpleAdapter(this, mp3list,
                R.layout.music_list_item_layout, new String[] { "title",
                "Artist", "duration" }, new int[] { R.id.music_title,
                R.id.music_Artist, R.id.music_duration });
        mMusiclist.setAdapter(mAdapter);
    }
    /**private class MusicListItemClickListener implements OnItemClickListener {

         * 点击列表播放音乐

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            listPosition = position;
            playMusic(listPosition);
        }

    }*/

    public Handler hand = new Handler();
    Runnable updateThread = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            if(Ser.me!=null && Ser.me.isPlaying()){
            String ti1=Audio.formatTime(Ser.me.getCurrentPosition());
            String sss=mp3Info.getTitle();
            te1.setText(sss);
            te2.setText(ti1);
            if(Ser.me.isPlaying()){
                playBtn.setBackgroundResource(R.drawable.pause);
                isPause=false;
                isPlaying=true;
            }else if(!Ser.me.isPlaying()){
                playBtn.setBackgroundResource(R.drawable.play);
                isPause=true;
                isPlaying=false;

            }
           // sendBroadcastToService2(sss);
            hand.postDelayed(updateThread, 1000);}
        }
    };

    private class ViewOnClickListener implements View.OnClickListener {
        Intent intent = new Intent();
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play_muc: // 播放音乐
                    if(isFirstTime) {
                        play();
                        isFirstTime = false;
                        isPlaying = true;
                        isPause = false;
                    } else {
                        if (isPlaying) {
                            playBtn.setBackgroundResource(R.drawable.play);
                            intent.setAction("com.wwj.media.MUSIC_SERVICE");
                            intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
                            Log.d("mmm","发送   。/////暂停指令。。。");
                            startService(intent);
                            isPlaying = false;
                            isPause = true;

                        } else if (isPause) {
                            playBtn.setBackgroundResource(R.drawable.pause);
                            intent.setAction("com.wwj.media.MUSIC_SERVICE");
                            intent.putExtra("MSG", AppConstant.PlayerMsg.CONTINUE_MSG);
                            Log.d("mmm","发送   。//BBBBBBBBBB播放指令。。。");
                            startService(intent);
                            isPause = false;
                            isPlaying = true;
                        }
                    }
                    break;
                case R.id.next: // 下一首
                    next.setBackgroundResource(R.drawable.next_glow);
                    isFirstTime = false;
                    isPlaying = true;
                    isPause = false;
                    next();
                    break;

                /*case R.id.playing:  //正在播放
                    Mp3Info mp3Info = mp3Infos.get(listPosition);
                    Intent intent = new Intent(HomeActivity.this, PlayerActivity.class);
                    intent.putExtra("title", mp3Info.getTitle());
                    intent.putExtra("url", mp3Info.getUrl());
                    intent.putExtra("artist", mp3Info.getArtist());
                    intent.putExtra("listPosition", listPosition);
                    intent.putExtra("currentTime", currentTime);
                    intent.putExtra("duration", duration);
                    intent.putExtra("MSG", AppConstant.PlayerMsg.PLAYING_MSG);
                    startActivity(intent);
                    break;*/
                 /* case R.id.repeat_music: // 重复播放
                    if (repeatState == isNoneRepeat) {
                        repeat_one();
                        shuffleBtn.setClickable(false);
                        repeatState = isCurrentRepeat;
                    } else if (repeatState == isCurrentRepeat) {
                        repeat_all();
                        shuffleBtn.setClickable(false);
                        repeatState = isAllRepeat;
                    } else if (repeatState == isAllRepeat) {
                        repeat_none();
                        shuffleBtn.setClickable(true);
                        repeatState = isNoneRepeat;
                    }
                    switch (repeatState) {
                        case isCurrentRepeat: // 单曲循环
                            repeatBtn
                                    .setBackgroundResource(R.drawable.repeat_current_selector);
                            Toast.makeText(HomeActivity.this, R.string.repeat_current,
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case isAllRepeat: // 全部循环
                            repeatBtn
                                    .setBackgroundResource(R.drawable.repeat_all_selector);
                            Toast.makeText(HomeActivity.this, R.string.repeat_all,
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case isNoneRepeat: // 无重复
                            repeatBtn
                                    .setBackgroundResource(R.drawable.repeat_none_selector);
                            Toast.makeText(HomeActivity.this, R.string.repeat_none,
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }

                    break;
                    case R.id.shuffle_music: // 随机播放
                    if (isNoneShuffle) {
                        shuffleBtn
                                .setBackgroundResource(R.drawable.shuffle_selector);
                        Toast.makeText(HomeActivity.this, R.string.shuffle,
                                Toast.LENGTH_SHORT).show();
                        isNoneShuffle = false;
                        isShuffle = true;
                        shuffleMusic();
                        repeatBtn.setClickable(false);
                    } else if (isShuffle) {
                        shuffleBtn
                                .setBackgroundResource(R.drawable.shuffle_none_selector);
                        Toast.makeText(HomeActivity.this, R.string.shuffle_none,
                                Toast.LENGTH_SHORT).show();
                        isShuffle = false;
                        isNoneShuffle = true;
                        repeatBtn.setClickable(true);
                    }
                    break;*/
            }
        }
    }

    /**
     * 下一首歌曲
     */
    public void next() {
        /*listPosition = listPosition + 1;
        if(listPosition <= mp3Infos.size() - 1) {
            Mp3Info mp3Info = mp3Infos.get(listPosition);
           // musicTitle.setText(mp3Info.getTitle());
            Intent intent = new Intent();
            intent.setAction("com.wwj.media.MUSIC_SERVICE");
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
            startService(intent);
            next.setBackgroundResource(R.drawable.next);
        } else {
            Toast.makeText(MainActivity.this, "没有下一首了", Toast.LENGTH_SHORT).show();
        }*/
        sendBroadcastToService1(STATE_NEXT);
    }

    /**
     * 上一首歌曲
     */

    public void previous() {
        listPosition = listPosition - 1;
        if(listPosition >= 0) {
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            //musicTitle.setText(mp3Info.getTitle());
            Intent intent = new Intent();
            intent.setAction("com.wwj.media.MUSIC_SERVICE");
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("MSG", AppConstant.PlayerMsg.PRIVIOUS_MSG);
            startService(intent);
        }else {
            Toast.makeText(MainActivity.this, "没有上一首了", Toast.LENGTH_SHORT).show();
        }
    }

    public void play() {
        Log.d("ttt","调用main play");
        playBtn.setBackgroundResource(R.drawable.pause);
        Mp3Info mp3Info = mp3Infos.get(listPosition);
        Log.d("mp3Info-->", mp3Info.toString());
        String sbb=mp3Info.getTitle();
        Intent intent = new Intent();
        intent.putExtra("url", mp3Info.getUrl());
        intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
        intent.putExtra("listPosition",listPosition);
        intent.setClass(MainActivity.this, Ser.class);
        startService(intent);       //启动服务
        //((Mucc) getApplication()).setMyMu(sbb);//赋值操作
        te1.setText(sbb);
        hand.post(updateThread);
        isFirstTime=false;
        isPlaying = true;
        isPause = false;
    }

    public void playMusic(int listPosition) {
        if (mp3Infos != null) {
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            //musicTitle.setText(mp3Info.getTitle());
            Intent intent = new Intent(MainActivity.this, Musicc.class);
            intent.putExtra("title", mp3Info.getTitle());
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("artist", mp3Info.getArtist());
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("currentTime", currentTime);
            intent.putExtra("repeatState", repeatState);
            intent.putExtra("shuffleState", isShuffle);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
            startActivity(intent);
        }
    }

    public class Start3 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
                    int ms1 = intent.getIntExtra("MS", 0);
                    Log.d("uuu", "s主活动设置收到歌曲位置==="+ms1);
                    listPosition=ms1;
                    Mp3Info mp3Info = mp3Infos.get(listPosition);
            if(Ser.me!=null && Ser.me.isPlaying()) {
                String ti1 = Audio.formatTime(Ser.me.getCurrentPosition());
                te1.setText(mp3Info.getTitle());
                te2.setText(ti1);
                if (Ser.me.isPlaying()) {
                    playBtn.setBackgroundResource(R.drawable.pause);
                    isPause = false;
                    isPlaying = true;
                } else if (!Ser.me.isPlaying()) {
                    playBtn.setBackgroundResource(R.drawable.play);
                    isPause = true;
                    isPlaying = false;

                }
            }
            /*if (action.equals("MS")) {
                //int ms = intent.getIntExtra("MS", 0);
                Log.d("uuu", "s设置SSSSSSSSSSSSSSSSS广播收到 偶哦");
               if(ms==1){
                   playBtn.setBackgroundResource(R.drawable.pause);
                   isPause=false;
                   isPlaying=true;
               }else {
                   playBtn.setBackgroundResource(R.drawable.play);
                   isPause=true;
                   isPlaying=false;
               }
            }else if (action.equals("cuu")){

                Log.d("uuu", "s设置收到歌曲位置==="+ms);
                listPosition=ms;
            }
            /*int ms = intent.getIntExtra("MS", 0);*/

        }
    }
    protected void sendBroadcastToService(int state) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(MUSICSer_ACTION);
        intent.putExtra("curr", state);
        //向后台Service发送播放控制的广播
        Log.d("ttt", "主活动广播已发送给Ser。。");
        sendBroadcast(intent);
    }
    protected void sendBroadcastToService1(int state) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(MUSICSERVIC_ACTION);
        intent.putExtra("control", state);
        //向后台Service发送播放控制的广播
        Log.d("ttt", "广播已发送。。");
        sendBroadcast(intent);
    }

    /*protected void sendBroadcastToService2(String state) {
        // TODO Auto-generated method stub
        Intent sendIntent = new Intent(MUSICTIMM_ACTION);
        sendIntent.putExtra("til",state );
        sendBroadcast(sendIntent);
        Log.d("ttt", "MAINHand222告诉musicc主题名字。。");

    }*/

}
