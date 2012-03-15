package com.blklb.mpdhd.services;

import com.blklb.mpdhd.services.StreamPlaybackService.LocalBinder;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class MyServiceConnection implements ServiceConnection {

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		// We've bound to LocalService, cast the IBinder and get
		// LocalService instance
		LocalBinder binder = (LocalBinder) service;
		ServiceInfo.mService = binder.getService();
		ServiceInfo.mBound = true;
		Log.w(this.toString(), "onServiceConnected");
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		ServiceInfo.mBound = false;
	}

}
