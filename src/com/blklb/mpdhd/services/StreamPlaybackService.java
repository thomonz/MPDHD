package com.blklb.mpdhd.services;

import java.io.IOException;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.blklb.mpdhd.tools.MPDHDInfo;
import com.blklb.mpdhd.ui.UIUtilities;

public class StreamPlaybackService extends Service {

	private String tag = this.toString();

	private MediaPlayer m = new MediaPlayer();
	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		public StreamPlaybackService getService() {
			return StreamPlaybackService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.w(tag, "onBind");
		return mBinder;
	}

	public void playStream(Activity _a) {

		String baseURL = "http://" + MPDHDInfo.hostname + ":"
				+ MPDHDInfo.streamingPort + "/mpd";
		
		final Activity a = _a;
		
		//Forced false to start to prevent unwanted updates to the notification icon
		ServiceInfo.isStreaming = false;

		try {
			UIUtilities.setupNotification(_a);
			
			m.setAudioStreamType(AudioManager.STREAM_MUSIC);
			m.setDataSource(baseURL);
			m.prepareAsync();
			

			m.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			    @Override
			    public void onPrepared(MediaPlayer mp) {
			        mp.start();
					ServiceInfo.isStreaming = true;
			        UIUtilities.updateNotification(a);
			    }
			});
			
			m.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) { 
					//if(what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
			        streamFix(a);
			        return true;
			        //}
					//return false;
				}
			});
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
			m.release();
			m = new MediaPlayer();
			playStream(_a);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * this method restarts the stream after we have tracked over or paused
	 * @param _a
	 */
	public void streamFix(Activity a) {
		//TODO destroy current
		m.release();
		m = new MediaPlayer();
		
		//Restart stream 
		playStream(a);
	}
	
	public void stopStreamStillStream(Activity _a) {
		m.release();
		m = new MediaPlayer();
		
		//TODO :Update notification icon to say playback is paused but streaming is active11
		UIUtilities.taredownNotification(_a);

	}

	public void stopStream(Activity _a) {
		m.release();
		UIUtilities.taredownNotification(_a);
		ServiceInfo.isStreaming = false;
	}
}
