package com.kpinfotech.mediaplayer;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	
	Activity activity;
	
	TextView tvname;
	
	SeekBar sbmusic;
	Button btnplaypause, btnprevious, btnnext;
	Button btnrewind, btnforward;
	TextView tvctime, tvttime;
	int position = 0;
	
	MediaPlayer mediaPlayer;
	double timeElapsed = 0, finalTime = 0;
	int forwardTime = 2000, backwardTime = 2000;
	Handler durationHandler = new Handler();
	
	Boolean isPlayClicked = false;
	
	ArrayList<String> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btnplaypause:
			playPause();
			break;
			
		case R.id.btnprevious:
			previous();
			break;
			
		case R.id.btnnext:
			next();
			break;
			
		case R.id.btnrewind:
			rewind();
			break;
			
		case R.id.btnforward:
			forward();
			break;
			
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}
	
	private void init() {
		activity = (Activity) MainActivity.this;
		
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		tvname = (TextView) findViewById(R.id.tvname);
		
		sbmusic = (SeekBar) findViewById(R.id.sbmusic);
		btnplaypause = (Button) findViewById(R.id.btnplaypause);
		btnprevious = (Button) findViewById(R.id.btnprevious);
		btnnext = (Button) findViewById(R.id.btnnext);
		btnrewind = (Button) findViewById(R.id.btnrewind);
		btnforward = (Button) findViewById(R.id.btnforward);
		tvctime = (TextView) findViewById(R.id.tvctime);
		tvttime = (TextView) findViewById(R.id.tvttime);
		
		btnplaypause.setOnClickListener(this);
		btnprevious.setOnClickListener(this);
		btnnext.setOnClickListener(this);
		btnrewind.setOnClickListener(this);
		btnforward.setOnClickListener(this);
		
		sbmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {	}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {	}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (mediaPlayer != null && fromUser) {
					mediaPlayer.seekTo((int) progress);
				}
			}
		});
		
		fillList();
	}
	
	private void fillList() {
		
		list = new ArrayList<String>();
		for(int i=0; i<5; i++) {
			String url = "SET YOUR ONLINE OR LOCAL URL FOR AUDIO";
			list.add(url);
		}
		
		tvname.setText(list.get(position));
	}
	
	private Runnable updateSeekBarTime = new Runnable() {
		public void run() {
			
			//get current position
			timeElapsed = mediaPlayer.getCurrentPosition();
			
			//set seekbar progress
			sbmusic.setProgress((int) timeElapsed);
			
			//set time remaining
			/*double timeRemaining = finalTime - timeElapsed;
			tvctime.setText(String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));*/
			
			tvctime.setText(String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed),
					TimeUnit.MILLISECONDS.toSeconds((long) timeElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed))));
			
			//repeat yourself that again in 100 miliseconds
			durationHandler.postDelayed(this, 100);
		}
	};
	
	private void playPause() {
		
		if(isPlayClicked) {
			isPlayClicked = false;
			btnplaypause.setBackgroundResource(R.drawable.play);
			
			mediaPlayer.pause();
			
		} else {
			isPlayClicked = true;
			btnplaypause.setBackgroundResource(R.drawable.pause);
			
			try {
				mediaPlayer.setDataSource(list.get(position));
				mediaPlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			finalTime = mediaPlayer.getDuration();
			sbmusic.setMax((int) finalTime);
			
			tvttime.setText(String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
					TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
			
			mediaPlayer.start();
			timeElapsed = mediaPlayer.getCurrentPosition();
			sbmusic.setProgress((int) timeElapsed);
			durationHandler.postDelayed(updateSeekBarTime, 100);
		}
	}
	
	public void rewind() {
		if ((timeElapsed - backwardTime) > 0) {
			timeElapsed = timeElapsed - backwardTime;
			
			mediaPlayer.seekTo((int) timeElapsed);
		}
	}
	
	public void forward() {
		if ((timeElapsed + forwardTime) <= finalTime) {
			timeElapsed = timeElapsed + forwardTime;

			mediaPlayer.seekTo((int) timeElapsed);
		}
	}

	private void previous() {
		mediaPlayer.stop();
		position -= 1;
		
		if(position < 0) {
			position = 0;
			
			finalTime = 0;
			sbmusic.setMax((int) finalTime);
			
			timeElapsed = 0;
			sbmusic.setProgress((int) timeElapsed);
			durationHandler.postDelayed(updateSeekBarTime, 100);
			
		} else {
			
			// update view's values
			tvname.setText(list.get(position));
			
			isPlayClicked = true;
			btnplaypause.setBackgroundResource(R.drawable.pause);
			
			try {
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setDataSource(list.get(position));
				mediaPlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			finalTime = mediaPlayer.getDuration();
			sbmusic.setMax((int) finalTime);
			
			tvttime.setText(String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
					TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
			
			mediaPlayer.start();
			timeElapsed = mediaPlayer.getCurrentPosition();
			sbmusic.setProgress((int) timeElapsed);
			durationHandler.postDelayed(updateSeekBarTime, 100);
		}
	}

	private void next() {
		mediaPlayer.stop();
		position += 1;
		
		if(position >= list.size()) {
			position = (list.size() - 1);
			
			finalTime = 0;
			sbmusic.setMax((int) finalTime);
			
			timeElapsed = 0;
			sbmusic.setProgress((int) timeElapsed);
			durationHandler.postDelayed(updateSeekBarTime, 100);
			
		} else {
			
			// update view's values
			tvname.setText(list.get(position));
			
			isPlayClicked = true;
			btnplaypause.setBackgroundResource(R.drawable.pause);
			
			try {
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setDataSource(list.get(position));
				mediaPlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			finalTime = mediaPlayer.getDuration();
			sbmusic.setMax((int) finalTime);
			
			tvttime.setText(String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
					TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
			
			mediaPlayer.start();
			timeElapsed = mediaPlayer.getCurrentPosition();
			sbmusic.setProgress((int) timeElapsed);
			durationHandler.postDelayed(updateSeekBarTime, 100);
		}
	}

}