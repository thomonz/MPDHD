package com.blklb.mpdhd.services;

import java.io.IOException;

import com.blklb.mpdhd.tools.JMPDHelper2;
import com.blklb.mpdhd.tools.MPDHDInfo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


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
	
	/**
	 * 
	 * @param path
	 */
	public void playPauseStream() {
		String baseURL = "http://" + MPDHDInfo.hostname + ":" + MPDHDInfo.streamingPort + "/mpd";

		try {
			if(m.isPlaying()) {
				m.stop(); // This stops the playback so two streams arent being played concurrently
				ServiceInfo.isStreaming = false;
				Log.w(tag, "Stopping Streaming");
			} else {
				Log.w(tag, "Starting Streaming, please allow time for buffering");
				m.setDataSource(baseURL);
				m.prepare();
				m.start();
				ServiceInfo.isStreaming = true;
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			m.reset(); //reset the player and start it up again
			playPauseStream();
			//e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
}
