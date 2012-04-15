package com.blklb.mpdhd.tasks;

import java.net.UnknownHostException;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.blklb.mpdhd.tools.JMPDHelper2;
import com.blklb.mpdhd.tools.MPDHDInfo;
import com.blklb.mpdhd.tools.TimerHelper;
import com.blklb.mpdhd.ui.UIInfo;
import com.blklb.mpdhd.ui.UIUtilities;

public class NetworkAndUITask extends TimerTask {

	private Activity _activity;
	private String tag = "Network & UI Task";
	private long uiRefreshDelay;

	public NetworkAndUITask(Activity a) {
		_activity = a;
		loadPrefrences();
		uiRefreshDelay = 5000;
	}

	@Override
	public void run() {

		//Log.w(tag, "HIT");
		
		if (hasConnectionInfo() && hasNetworkAccess() && canHitHost()) {
			try {
				// TODO:Find a fix for changing server names once connected to a
				// server
				// force a prefrence reload load only when there has been a
				// change.
				// Figure out preference change listener
				// if(prefrencesHaveChanged)
				// loadPrefrences();
				// reestablishConnection();

				switch (MPDHDInfo.currentTab) {
				case NowPlaying:
					UIUtilities.updateNowPlayingUI(_activity);
					break;
				case Queue:
					UIUtilities.updateQueueUI(_activity);
					break;
				case Database:
					UIUtilities.updateDatabaseUI(_activity);
					break;
				case Search:
					UIUtilities.updateSearchUI(_activity);
					break;

				case Playlists:
					UIUtilities.updatePlaylistUI(_activity);
					break;
				}

				uiRefreshDelay = 300;

				TimerHelper.getInstance().scheduleTask(
						new NetworkAndUITask(_activity), uiRefreshDelay);

			} catch (NullPointerException e) {
				if (JMPDHelper2.getInstance().isConnected()) {
					uiRefreshDelay = 1000;
					TimerHelper.getInstance().scheduleTask(
							new NetworkAndUITask(_activity), uiRefreshDelay);
				} else {
					Log.w(tag,
							"Unable to connect to MPD. MPD may not be working properly. If this warn is hit it means we can find and hit the server but we are unable to establish a connection.");
					Log.w(tag, "Do you have the correct hostname entered ?");
					showDialogBox();
					// When we hit here we need to stop everything and propmt
					// the user with a connection error with a "retry" &
					// "edit settings" window
				}

			}

		} else {
			showDialogBox();
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// ////////////////////////UTILITY METHODS//////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////

	private void showDialogBox() {

		AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
		builder.setCancelable(true);
		//builder.setIcon(R.drawable.dialog_question);
		builder.setTitle("Title");
		builder.setInverseBackgroundForced(true);
		builder.setMessage("Unable to connect to the server. This may be due to an improper hostname or a timeout.");
		builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Retry the connection 
				TimerHelper.getInstance().cancelScheduledTasks(); // destroys all tasks in the queue
				TimerHelper.getInstance().scheduleTask( 
						new NetworkAndUITask(_activity), uiRefreshDelay); // makes a new one
				dialog.dismiss(); // removes the dialog
			}
		});

		builder.setNegativeButton("Settings", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO: Go to settings on return we must reload settings and retry the connection
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();

		alert.show();

	}

	private void loadPrefrences() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(_activity);
		MPDHDInfo.hostname = prefs.getString("hostname", "");
		MPDHDInfo.password = prefs.getString("password", "");
		MPDHDInfo.port = prefs.getString("port", "6600");
		MPDHDInfo.streamingPort = prefs.getString("streaming_port", "8000");
	}

	/**
	 * 
	 * @return true only if network access is available
	 */
	private boolean hasNetworkAccess() {

		final ConnectivityManager connMgr = (ConnectivityManager) _activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifi.isAvailable() || mobile.isAvailable())
			return true;

		Log.e(tag, "Problem on network access");
		return false;
	}

	/**
	 * 
	 * @return true if there is a valid hostname
	 */
	private boolean hasConnectionInfo() {
		String hostname = MPDHDInfo.hostname;
		if ((hostname.length() > 0))
			return true;
		Log.e(tag, "Problem on connection info");
		return false;
	}

	private boolean canHitHost() {
		try {
			java.net.InetAddress.getByName(MPDHDInfo.hostname);
		} catch (UnknownHostException e) {
			Log.e(tag, "Problem on host hit");
			return false;
		}
		return true;
	}
}