package com.ui.my;


import android.app.Application;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class Ser extends Service {

	public static final String MUSICBOX_ACTION = "MUS_ACTION";
	//MusicService接收器所能响应的Action
	public static final String MUSICSERVIC_ACTION = "MUS1_ACTION";
	public static final String MUSICTIM_ACTION = "MUS2_ACTION";
	public static final String MUSICSer_ACTION = "MUS3_ACTION";
	public static final String MUSICTIMM_ACTION = "MUS4_ACTION";
	public static final int STATE_NON=0x122;
	//播放的flag
	public static final int STATE_PLAY=0x123;
	//暂停的flag
	public static final int STATE_PAUSE=0x124;
	//停止放的flag
	public static final int STATE_NEXT=0x125;
	//播放上一首的flag
	public static final int STATE_CQ=0x126;
	//播放下一首的flag
	public static final int STATE_PRE=0x127;

	private Mybinder mybinder=new Mybinder();
	static MediaPlayer me=null;
	public String path=null;
			//="/mnt/sdcard/stand.mp3";
	double st,st1;
	String til=null;
	Start1 s1;
	boolean isPause;
	int status=2;
	private int current = 0;        // 记录当前正在播放的音乐
	private List<Mp3Info> mp3Infos;   //存放Mp3Info对象的集合
	Mp3Info mp3Info;
	private static final String TAG="Service";

	@Override
	public IBinder onBind(Intent intent) {///�󶨴���
		// TODO Auto-generated method stub
		//mp.stop();
		//mp.start();
		
		return mybinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.d(TAG,"oncreate");
		//mp=MediaPlayer.create(getApplicationContext(),R.raw.volar);
		//mp.setLooping(true);
		//广播动态注册
		s1=new Start1();
		IntentFilter filter=new IntentFilter();
		filter.addAction(MUSICSERVIC_ACTION);
		filter.addAction(MUSICSer_ACTION);
		registerReceiver(s1,filter);
		me=new MediaPlayer();
		mp3Infos = Audio.getMp3Infos(Ser.this);
		//prepare();
		//sendBroadcastToService(me.getDuration());


		/**
		 * 设置音乐播放完成时的监听器
		 */
		me.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.d("mmm","ge歌曲播放完成，已被完成监听函数收到");
				if (status == 1) { // 单曲循环
					mp.start();
				} else if (status == 2) { // 全部循环
					Log.d("mmm","ge歌曲播放完成，设置为全部循环");
					current++;
					Log.d("mmm","current++ = "+current);
					if(current > mp3Infos.size() - 1) {  //变为第一首的位置继续播放
						current = 0;
						///
					}
					//Intent sendIntent = new Intent(UPDATE_ACTION);
					//sendIntent.putExtra("current", current);
					// 发送广播，将被Activity组件中的BroadcastReceiver接收到
					//sendBroadcast(sendIntent);
					mp3Info = mp3Infos.get(current);
					path = mp3Info.getUrl();
					String sbb=mp3Info.getTitle();
					((Mucc) getApplication()).setMyMu(sbb);//赋值操作
					play(0);
				} else if (status == 3) { // 顺序播放
					current++;  //下一首位置
					if (current <= mp3Infos.size() - 1) {
						//Intent sendIntent = new Intent(UPDATE_ACTION);
						//sendIntent.putExtra("current", current);
						// 发送广播，将被Activity组件中的BroadcastReceiver接收到
						//sendBroadcast(sendIntent);
						path = mp3Infos.get(current).getUrl();
						String sbb=mp3Info.getTitle();
						((Mucc) getApplication()).setMyMu(sbb);//赋值操作
						play(0);
					}else {
						me.seekTo(0);
						current = 0;
						//Intent sendIntent = new Intent(UPDATE_ACTION);
						//sendIntent.putExtra("current", current);
						// 发送广播，将被Activity组件中的BroadcastReceiver接收到
						//sendBroadcast(sendIntent);
					}
				} else if(status == 4) {    //随机播放
					//current = getRandomIndex(mp3Infos.size() - 1);
					System.out.println("currentIndex ->" + current);
					path = mp3Infos.get(current).getUrl();
					play(0);
				}
				//sendBroadcastToService1(current);
				Log.d("mmm","g给main下一首歌曲位置");
			}

		});
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onsartcommand");
		Log.d("mmm","FFFFFFFFFFFFFFFFFFfuwu服务已启动");
		//if(me.isPlaying()) {
		//	Log.d("mmm","r如果音乐在播放就停止播放。。。");
		//	stop();}

		path = intent.getStringExtra("url");
		int msg = intent.getIntExtra("MSG", 0);
		current=intent.getIntExtra("listPosition",0);
		//current=intent.getIntExtra("wei",0);
		Log.d("mmm","Ser Ccurrent = "+current);
		if(msg == AppConstant.PlayerMsg.PLAY_MSG) {
			play(0);
		} else if(msg == AppConstant.PlayerMsg.PAUSE_MSG) {
			Log.d("mmm","接收到暂停指令。。。");
			pause();
		} else if(msg == AppConstant.PlayerMsg.STOP_MSG) {
			stop();
		}else if(msg == AppConstant.PlayerMsg.CONTINUE_MSG) {
			Log.d("mmm","接收ji继续播放指令lll");
			resume();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(TAG,"ondestroy");
		//me.stop();
		unregisterReceiver(s1);
		super.onDestroy();
	}



	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onunbind");
		//mp.stop();
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onrebind");
		super.onRebind(intent);
	}
	
	public class Mybinder extends Binder{
		Ser getService(){
			return Ser.this;
		}
	}
	
	void prepare(){
		        //me.setDataSource(path);
		        Log.d(TAG, "h获得歌曲路径，正在加载。。");

		        me.setOnPreparedListener(opl);
			 	me.prepareAsync();

	}///mee
	
	private OnPreparedListener opl=new OnPreparedListener(){

		@Override
		public void onPrepared(MediaPlayer mp) {
			// TODO Auto-generated method stub
			Log.d("onPrepared", "音乐加载完成。。 ");
			//sendBroadcastToService(me.getDuration());
			//mp.start();

		}};

	protected void sendBroadcastToService(int state) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();
		intent.setAction(MUSICBOX_ACTION);
		intent.putExtra("control", state);
		Log.d("ttt","000Ser广播已发送。。");
		sendBroadcast(intent);
	}
	protected void sendBroadcastToService1(int state) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setAction("Main_ACTION");
		intent.putExtra("MS", state);
		Log.d("ttt", "1111告诉主活动位置。。");
		sendBroadcast(intent);
	}
	protected void sendBroadcastToService2(String state) {
		// TODO Auto-generated method stub
		Intent sendIntent = new Intent(MUSICTIMM_ACTION);
		sendIntent.putExtra("til",state );
		sendBroadcast(sendIntent);
		Log.d("ttt", "Hand222告诉musicc主题名字。。");

	}
	public class Start1 extends BroadcastReceiver{
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if (action.equals(MUSICSERVIC_ACTION)) {
				int control=intent.getIntExtra("control",-1);

				switch (control){
					case STATE_PLAY:
						if(!me.isPlaying()){
							me.start();
							//hand.post(updateThread);
							Log.d("yyy","开始播放音乐");
							isPause = false;
						}
						 break;
					case STATE_PAUSE:
						if(me.isPlaying()){
							me.pause();
							//hand.removeCallbacks(updateThread);
							Log.d("yyy","音乐暂停。。");
							isPause = true;
						}
						break;
					case STATE_CQ:
						//if(me.isPlaying()){
							Log.d("yyy","回复歌曲长度广播成功。。");
							sendBroadcastToService(me.getDuration());
						break;
					case STATE_NEXT:
						Log.d("yyy","播放下一首哦");
						next();
						break;
					case STATE_PRE:
						Log.d("yyy","播放上一首哦");
						previous();
					}
				}
				if (action.equals(MUSICSer_ACTION)) {
					int control1=intent.getIntExtra("curr",0);
						current=control1;
					sendBroadcastToService1(current);
				}
			}
			
		}

	/*Handler hand = new Handler();

	Runnable updateThread = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(me.isPlaying()) {
				Log.d("mmm","hand函数");
			int ti=me.getCurrentPosition();
			//me.getDuration()
			Intent intent=new Intent();
			intent.setAction(MUSICTIM_ACTION);
			intent.putExtra("mus", ti);
			//Log.d("ttt","播放位置广播已发送。。"+ti);
			sendBroadcast(intent);
			}
			hand.postDelayed(updateThread, 100);
		}
	};
	Runnable updateThread1 = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			hand.postDelayed(updateThread1, 1000);
		}
	};*/

	private void play(int position) {
		if(me.isPlaying()) {
				Log.d("mmm","r如果音乐在播放就停止播放。。。");
				stop();}
		try {
			Log.d("mmm","音乐重新播放");
			me.reset();//把各项参数恢复到初始状态
			me.setDataSource(path);
			prepare();  //进行缓冲
			me.setOnPreparedListener(new PreparedListener(position));//注册一个监听器
			me.setLooping(false);
			//hand.post(updateThread);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 暂停音乐
	 */
	private void pause() {
		Log.d("ttt","z调用ppppppause  me != null && ");
		if (me.isPlaying()) {
			Log.d("ttt","音乐zZZZZZZZZZZZZ停。me != null && ");
			me.pause();
			isPause = true;
		}

	}

	/**
	 * 下一首歌曲
	 */
	public void next() {
		//hand.removeCallbacks(updateThread);
		current= current + 1;
		if(current <= mp3Infos.size() - 1) {
			Mp3Info mp3Info = mp3Infos.get(current);
			path = mp3Info.getUrl();
			Log.d("yyy","播放下一首哦");
			play(0);
			// musicTitle.setText(mp3Info.getTitle());
		} else {
			Toast.makeText(this, "没有下一首了", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 上一首歌曲
	 */

	public void previous() {
		current= current - 1;
		if(current <= mp3Infos.size() - 1) {
			Mp3Info mp3Info = mp3Infos.get(current);
			path = mp3Info.getUrl();
			play(0);
			// musicTitle.setText(mp3Info.getTitle());
		} else {
			Toast.makeText(this, "没有上一首了", Toast.LENGTH_SHORT).show();
		}
	}


	/**
	 * 停止音乐
	 */
	private void stop(){
		if(me != null) {
			me.stop();
			//hand.removeCallbacks(updateThread);
			try {
				me.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void resume() {
		Log.d("mmm","接收resumeeeeeee");
		if (isPause) {
			Log.d("mmm","接收resumeeeeeeekkaishi开始播放");
			me.start();
			isPause = false;
		}
	}
	private final class PreparedListener implements OnPreparedListener {
		private int positon;
		public PreparedListener(int positon) {
			this.positon = positon;
		}
		@Override
		public void onPrepared(MediaPlayer mp) {
			Log.d("mmm","r监听播放开始。");
			Log.d("mmm","CCb播放时的current = "+current);


			if(positon > 0) {    //如果音乐不是从头播放
				me.seekTo(positon);
				//String t=mp3Info.getTitle();
			}
			me.start();    //开始播放
			//Log.d("ee","z主题="+mp3Info.getTitle());
			Mp3Info mp3Info = mp3Infos.get(current);
			String sbb=mp3Info.getTitle();
			String sn=mp3Info.getArtist();
			((Mucc) getApplication()).setMyMu(sbb);//赋值操作
			((Mucc) getApplication()).setDuc(me.getDuration());
			((Mucc) getApplication()).setNam(sn);
			sendBroadcastToService(me.getDuration());
			sendBroadcastToService1(current);
			Log.d("ee","z主题="+sbb+sn);
		}

	}

}
