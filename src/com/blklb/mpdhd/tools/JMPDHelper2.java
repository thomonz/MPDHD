package com.blklb.mpdhd.tools;

import java.net.UnknownHostException;
import java.util.List;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDPlayer;
import org.bff.javampd.MPDPlayer.PlayerStatus;
import org.bff.javampd.MPDPlaylist;
import org.bff.javampd.events.PlaylistChangeEvent;
import org.bff.javampd.events.PlaylistChangeListener;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDPlaylistException;
import org.bff.javampd.exception.MPDResponseException;
import org.bff.javampd.objects.MPDSong;

import android.util.Log;

public class JMPDHelper2 {

	// singleton
	private static JMPDHelper2 instance;

	private MPD mpd;
	private MPDPlayer mpdPlayer;
	private MPDPlaylist mpdPlaylist;
	
	private int port;
	private int streamingPort;
	private String hostname;
	private String password;
	
	private int oldSize = 0;

	private String tag = "JMPDHelper";

	/**
	 * Default Cstor.
	 */
	private JMPDHelper2() {
		updateMPDInfo();
		establishConnection();
	}

	/**
	 * Public access for the singleton.
	 * 
	 * @return the singleton object
	 */
	public static JMPDHelper2 getInstance() {
		if (instance == null) {
			instance = new JMPDHelper2();
		}
		return instance;
	}

	public void reestablishConnection() {
		updateMPDInfo();
		establishConnection();
	}

	private void updateMPDInfo() {
		port = Integer.parseInt(MPDHDInfo.port);
		streamingPort = Integer.parseInt(MPDHDInfo.streamingPort);
		hostname = MPDHDInfo.hostname;
		password = MPDHDInfo.password;
		mpd = null;
		mpdPlayer = null;
	}

	private void establishConnection() {
		try {
			if (password.length() > 0) {
				mpd = new MPD(hostname, port, password);
			} else {
				mpd = new MPD(hostname, port);
			}
			mpdPlayer = mpd.getMPDPlayer();
			mpdPlaylist = mpd.getMPDPlaylist();

			Log.w(tag, "MPD isNULL:" + mpd);
			Log.w(tag, "MPDP isNULL:" + mpdPlayer);

		} catch (MPDException e) {
			Log.e(tag, "MPDServerException");
			Log.e(tag, "Hostname:" + hostname);
			Log.e(tag, "Port:" + port);
			e.printStackTrace();
			mpd = null;
		} catch (UnknownHostException e) {
			Log.e(tag, "UnknownHostException");
			e.printStackTrace();
			mpd = null;
		} catch (ExceptionInInitializerError e) {
			e.printStackTrace();
		}
	}

	public String getCurrentTrackTitle() {
		String name = "Not Connected to MPD Server";
		MPDSong song;
		try {
			song = mpdPlayer.getCurrentSong();
			name = song.getName();
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			// e.printStackTrace();
		}

		if (name == null) {
			name = "Not Playing";
		}
		return name;
	}

	public String getCurrentTrackArtist() {
		String name = "Not Connected to MPD Server";
		MPDSong song;
		try {
			song = mpdPlayer.getCurrentSong();
			name = song.getArtist().toString();
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// e.printStackTrace();
			Log.e(tag, "Null in getCurrentTrackArtist Hit");
		}

		if (name == null) {
			name = "Not Playing";
		}

		return name;
	}

	public String getCurrentTrackAlbum() {
		String name = "Not Connected to MPD Server";
		MPDSong song;
		try {
			song = mpdPlayer.getCurrentSong();
			name = song.getAlbum().toString();
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// e.printStackTrace();
		}
		return name;
	}

	public int getElapsedTime() {
		int amtPlayed = 0;
		try {
			amtPlayed = (int) mpdPlayer.getElapsedTime();
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// e.printStackTrace();
		}
		return amtPlayed;
	}

	public int getCurrentTrackLength() {
		int trackLength = 1;
		try {
			trackLength = mpdPlayer.getCurrentSong().getLength();
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// e.printStackTrace();
		}
		return trackLength;
	}

	public void nextTrack() {
		try {
			mpdPlayer.playNext();
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		}
	}

	public void previousTrack() {
		try {
			mpdPlayer.playPrev();
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		}
	}

	public void playPause() {
		try {
			if (mpdPlayer.getStatus() == PlayerStatus.STATUS_PLAYING) {
				mpdPlayer.pause();
			} else {
				mpdPlayer.play();
			}
		} catch (MPDException e) {
			e.printStackTrace();
		}
	}

	public boolean isPaused() {

		try {
			if (mpdPlayer.getStatus() == PlayerStatus.STATUS_PLAYING) {
				return false;
			}
		} catch (MPDException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean isRepeat() {
		try {
			return mpdPlayer.isRepeat();
		} catch (MPDException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isRandom() {
		try {
			return mpdPlayer.isRandom();
		} catch (MPDException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void toggleRepeat() {
		try {
			mpdPlayer.setRepeat(!mpdPlayer.isRepeat());
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		}
	}

	public void toggleRandom() {
		try {
			mpdPlayer.setRandom(!mpdPlayer.isRandom());
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		}
	}

	public void getQueue() {
		String s = "";
		try {
			s = mpdPlaylist.getSongList().get(0).getArtist()
					.toString();
		} catch (MPDPlaylistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.w(tag, "Artist: " + s);
	}

	public void volumeUp() {
		try {
			int volume = mpdPlayer.getVolume();
			mpdPlayer.setVolume(volume + 3);
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		}
	}

	public void volumeDown() {
		try {
			int volume = mpdPlayer.getVolume();
			mpdPlayer.setVolume(volume - 3);
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		}
	}

	public void seekTrack(int time) {
		try {
			mpdPlayer.seek(time);
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return mpd.isConnected();
	}

	public void disconnect() {
		try {
			mpd.close();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		} catch (MPDResponseException e) {
			e.printStackTrace();
		}
	}

	public int getVolume() {
		int volume = 0;
		try {
			volume = mpdPlayer.getVolume();
		} catch (MPDPlayerException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		}
		return volume;
	}

	public boolean isStopped() {
		try {
			if (mpdPlayer.getStatus().equals(PlayerStatus.STATUS_STOPPED))
				return true;
		} catch (MPDException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String[] getPlayQueue() {
		String[] values;
		List<MPDSong> mpdQueue = null;
		
		try {
			mpdQueue = mpdPlaylist.getSongList();
		} catch (MPDPlaylistException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		} 
		values = new String[mpdQueue.size()];
		
		for(int i = 0; i < mpdQueue.size(); ++i) {
			MPDSong s = mpdQueue.get(i);
			values[i] = s.getTitle() + "	" + s.getArtist()  + "	" + s.getAlbum();
		}
		
		return values;
	}
	
	public void addPlaylistChangeListener(PlaylistChangeListener pcl) {
		mpdPlaylist.addPlaylistChangeListener(pcl);
	}
	
	
	/**
	 * Hacked together playlist change poller since the build in playlist change
	 * listener doesn't work properly
	 * @return
	 */
	public boolean playlistChanged() {
		int newSize = 0;
		try {
			newSize = mpdPlaylist.getSongList().size();
		} catch (MPDPlaylistException e) {
			e.printStackTrace();
		} catch (MPDConnectionException e) {
			e.printStackTrace();
		}
		if(newSize != oldSize) {
			Log.w(tag, "PLAYLISTCHANGED");
			oldSize = newSize;
			return true;
		}
		return false;
	}
	

}
