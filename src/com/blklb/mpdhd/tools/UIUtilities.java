package com.blklb.mpdhd.tools;

import android.app.Activity;
import android.content.ClipData.Item;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.blklb.mpdhd.R;
//import com.blklb.mpdhd.tasks.LastFMCover;

/**
 * This class houses UI Update utilities.
 * 
 * @author ablackbu
 * 
 */
public class UIUtilities {

	// private static String tag = "UIUtilities";

	/**
	 * 
	 * @param _activity
	 */
	public static void setupNowPlayingTabButtonListeners(Activity _activity) {
		final Activity a = _activity;

		ImageButton playPause = (ImageButton) a
				.findViewById(R.id.playPauseImageButton);
		ImageButton previous = (ImageButton) a
				.findViewById(R.id.previousImageButton);
		ImageButton next = (ImageButton) a.findViewById(R.id.nextImageButton);
		ImageButton random = (ImageButton) a
				.findViewById(R.id.randomImageButton);
		ImageButton repeat = (ImageButton) a
				.findViewById(R.id.repeatImageButton);

		SeekBar trackSeekBar = (SeekBar) a.findViewById(R.id.trackSeekBar);

		playPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						JMPDHelper2.getInstance().playPause();
					}
				}).start();
			}
		});

		previous.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						JMPDHelper2.getInstance().previousTrack();
					}
				}).start();
			}
		});

		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						JMPDHelper2.getInstance().nextTrack();
					}
				}).start();
			}
		});

		random.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						JMPDHelper2.getInstance().toggleRandom();
					}
				}).start();
			}
		});

		repeat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						JMPDHelper2.getInstance().toggleRepeat();
					}
				}).start();
			}
		});

		trackSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				final int _progress = progress;
				if (!fromUser) // Ignore is the update is not from the user
					return;
				new Thread(new Runnable() {
					@Override
					public void run() {
						JMPDHelper2.getInstance().seekTrack(_progress);
					}
				}).start();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		});

	}

	public static void setupDatabaseTabButtonListeners(Activity _activity) {
		setupNowPlayingSidebarListeners(_activity);
		// TODO:Finish
	}

	public static void setupQueueTabButtonListeners(Activity _activity) {
		setupNowPlayingSidebarListeners(_activity);
		// TODO:Finish
	}

	public static void setupSearchTabButtonListeners(Activity _activity) {
		setupNowPlayingSidebarListeners(_activity);
		// TODO:Finish
	}

	public static void setupPlaylistTabButtonListeners(Activity _activity) {
		setupNowPlayingSidebarListeners(_activity);
		// TODO:Finish
	}

	private static void setupNowPlayingSidebarListeners(Activity _activity) {
		final Activity activity = _activity;

		ImageButton playPauseSidebar = (ImageButton) activity
				.findViewById(R.id.playPauseSidebarImageButton);
		ImageButton previousSidebar = (ImageButton) activity
				.findViewById(R.id.previousSidebarImageButton);
		ImageButton nextSidebar = (ImageButton) activity
				.findViewById(R.id.nextSidebarImageButton);
		ImageButton randomSidebar = (ImageButton) activity
				.findViewById(R.id.randomSidebarImageButton);
		ImageButton repeatSidebar = (ImageButton) activity
				.findViewById(R.id.repeatSidebarImageButton);

		playPauseSidebar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						JMPDHelper2.getInstance().playPause();
					}
				}).start();
			}
		});

		previousSidebar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						JMPDHelper2.getInstance().previousTrack();
					}
				}).start();
			}
		});

		nextSidebar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						JMPDHelper2.getInstance().nextTrack();
					}
				}).start();
			}
		});

		randomSidebar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						JMPDHelper2.getInstance().toggleRandom();
					}
				}).start();
			}
		});

		repeatSidebar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						JMPDHelper2.getInstance().toggleRepeat();
					}
				}).start();
			}
		});
	}

	public static void updateNowPlayingSidebarUI(Activity _activity) {
		updateMPDHDInfo();

		JMPDHelper2 jmpd = JMPDHelper2.getInstance(); // establish connection

		final int trackLength = jmpd.getCurrentTrackLength();
		final int elapsedTime = jmpd.getElapsedTime();
		final String track = jmpd.getCurrentTrackTitle();
		final String artist = jmpd.getCurrentTrackArtist();
		final String album = jmpd.getCurrentTrackAlbum();

		// TODO:

		final Activity activity = _activity;

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				SeekBar sb = (SeekBar) activity
						.findViewById(R.id.trackSidebarSeekBar);

				try {
					sb.setMax(trackLength);
					sb.setProgress(elapsedTime);

					TextView songName = (TextView) activity
							.findViewById(R.id.songSidebarTextView);
					songName.setText(track);

					TextView artistName = (TextView) activity
							.findViewById(R.id.artistSidebarTextView);
					artistName.setText(artist);

					TextView albumName = (TextView) activity
							.findViewById(R.id.albumSidebarTextView);
					albumName.setText(album);

					TextView elapsedTotal = (TextView) activity
							.findViewById(R.id.elapsedTotalSidebarTextView);

					int elapsedSeconds = (elapsedTime % 60);
					int elapsedMinutes = (elapsedTime / 60);
					int totalSeconds = (trackLength % 60);
					int totalMinutes = (trackLength / 60);

					String elapsedTotalString = "";

					// This adds the 0 in front if it's under 10 seconds.
					// I like this UI better. Maybe because I'm used to it.

					if (elapsedSeconds < 10)
						elapsedTotalString += elapsedMinutes + ":0"
								+ elapsedSeconds + " - ";
					else
						elapsedTotalString += elapsedMinutes + ":"
								+ elapsedSeconds + " - ";

					// This adds the 0 in front if it's under 10 seconds.
					// I like this UI better. Maybe because I'm used to it.
					if (totalSeconds < 10)
						elapsedTotalString += totalMinutes + ":0"
								+ totalSeconds;
					else
						elapsedTotalString += totalMinutes + ":" + totalSeconds;

					elapsedTotal.setText(elapsedTotalString);

					updatePlayPauseSidebarButtonUI(activity);
					updateRepeatSidebarButtonUI(activity);
					updateRandomSidebarButtonUI(activity);

				} catch (NullPointerException e) {
					// Ignore this will be thrown if it's caught inside of the
					// method when the view is switched. There's no way I know
					// to fix this other then just catch the exception. Since
					// it can be hit anywhere in this chunk.
				}

			}
		});

	}

	public static void updateNowPlayingUI(Activity _activity) {
		updateMPDHDInfo();
		JMPDHelper2 jmpd = JMPDHelper2.getInstance(); // establish connection

		final int trackLength = jmpd.getCurrentTrackLength();
		final int elapsedTime = jmpd.getElapsedTime();
		final String track = jmpd.getCurrentTrackTitle();
		final String artist = jmpd.getCurrentTrackArtist();
		final String album = jmpd.getCurrentTrackAlbum();

		final Activity activity = _activity;

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				int seconds, minutes;

				SeekBar sb = (SeekBar) activity.findViewById(R.id.trackSeekBar);

				try {
					sb.setMax(trackLength);
					sb.setProgress(elapsedTime);

					TextView songName = (TextView) activity
							.findViewById(R.id.songTextView);
					songName.setText(track);

					TextView artistName = (TextView) activity
							.findViewById(R.id.artistTextView);
					artistName.setText(artist);

					TextView albumName = (TextView) activity
							.findViewById(R.id.albumTextView);
					albumName.setText(album);

					TextView elapsed = (TextView) activity
							.findViewById(R.id.elapsedTextView);
					seconds = (elapsedTime % 60);
					minutes = (elapsedTime / 60);

					// This adds the 0 in front if it's under 10 seconds.
					// I like this UI better. Maybe because I'm used to it.
					if (seconds < 10)
						elapsed.setText(minutes + ":0" + seconds);
					else
						elapsed.setText(minutes + ":" + seconds);

					TextView length = (TextView) activity
							.findViewById(R.id.lengthTextView);
					seconds = (trackLength % 60);
					minutes = (trackLength / 60);

					// This adds the 0 in front if it's under 10 seconds.
					// I like this UI better. Maybe because I'm used to it.
					if (seconds < 10)
						length.setText(minutes + ":0" + seconds);
					else
						length.setText(minutes + ":" + seconds);

					// Read Current state and update ui before buttons are
					// pressed
					updatePlayPauseButtonUI(activity);
					updateRepeatButtonUI(activity);
					updateRandomButtonUI(activity);
					
					//Attempts to fetch an album cover
					updateCoverArtUI(activity);
					
					
				} catch (NullPointerException e) {
					// Ignore this will be thrown if it's caught inside of the
					// method when the view is switched
				}
			}
		});

	}

	public static void updateQueueUI(Activity _activity) {
		updateNowPlayingSidebarUI(_activity);
		// TODO:Finish
	}

	public static void updateDatabaseUI(Activity _activity) {
		updateNowPlayingSidebarUI(_activity);
		// TODO:Finish
	}

	public static void updateSearchUI(Activity _activity) {
		updateNowPlayingSidebarUI(_activity);
		// TODO:Finish
	}

	public static void updatePlaylistUI(Activity _activity) {
		updateNowPlayingSidebarUI(_activity);
		// TODO:Finish
	}

	private static void updateCoverArtUI(Activity _activity) {
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				//String artist = JMPDHelper2.getInstance().getCurrentTrackArtist();
				//String album = JMPDHelper2.getInstance().getCurrentTrackAlbum();
				try {
					//LastFMCover.getCoverUrl(artist, album);
					//TODO:LastFMCover.getCoverUrl();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	private static void updateCoverArtSidebarUI(Activity _activity) {

	}

	/**
	 * Updates the PlayPauseButtonUI on the NowPlaying Tab. This must be run on
	 * the UI Thread.
	 * 
	 * @param _activity
	 */
	private static void updatePlayPauseButtonUI(Activity _activity) {
		ImageButton playPause = (ImageButton) _activity
				.findViewById(R.id.playPauseImageButton);
		Resources res = _activity.getResources();
		Drawable drawable;

		if (!MPDHDInfo.isPaused)
			drawable = res.getDrawable(R.drawable.btn_playback_ic_pause);
		else
			drawable = res.getDrawable(R.drawable.btn_playback_ic_play);
		playPause.setImageDrawable(drawable);
	}

	/**
	 * Updates the PlayPauseButtonUI on the NowPlaying Sidebar. This must be run
	 * on the UI Thread.
	 * 
	 * @param _activity
	 */
	private static void updatePlayPauseSidebarButtonUI(Activity _activity) {
		ImageButton playPause = (ImageButton) _activity
				.findViewById(R.id.playPauseSidebarImageButton);
		Resources res = _activity.getResources();
		Drawable drawable;

		if (!MPDHDInfo.isPaused)
			drawable = res.getDrawable(R.drawable.btn_playback_ic_pause);
		else
			drawable = res.getDrawable(R.drawable.btn_playback_ic_play);
		playPause.setImageDrawable(drawable);
	}

	/**
	 * Updates the RepeatButtonUI on the NowPlaying Tab. This must be run on the
	 * UI Thread.
	 * 
	 * @param _activity
	 */
	private static void updateRepeatButtonUI(Activity _activity) {
		ImageButton repeat = (ImageButton) _activity
				.findViewById(R.id.repeatImageButton);
		Resources res = _activity.getResources();
		Drawable drawable;
		if (MPDHDInfo.isRepeat)
			drawable = res.getDrawable(R.drawable.ic_mp_repeat_all_btn);
		else
			drawable = res.getDrawable(R.drawable.ic_mp_repeat_off_btn);
		repeat.setImageDrawable(drawable);
	}

	/**
	 * Updates the RepeatButtonUI on the NowPlaying Sidebar. This must be run on
	 * the UI Thread.
	 * 
	 * @param _activity
	 */
	private static void updateRepeatSidebarButtonUI(Activity _activity) {
		ImageButton repeat = (ImageButton) _activity
				.findViewById(R.id.repeatSidebarImageButton);
		Resources res = _activity.getResources();
		Drawable drawable;
		if (MPDHDInfo.isRepeat)
			drawable = res.getDrawable(R.drawable.ic_mp_repeat_all_btn);
		else
			drawable = res.getDrawable(R.drawable.ic_mp_repeat_off_btn);
		repeat.setImageDrawable(drawable);
	}

	/**
	 * Updates the RandomButtonUI on the NowPlaying Tab. This must be run on the
	 * UI Thread.
	 * 
	 * @param _activity
	 */
	private static void updateRandomButtonUI(Activity _activity) {
		ImageButton random = (ImageButton) _activity
				.findViewById(R.id.randomImageButton);
		Resources res = _activity.getResources();
		Drawable drawable;
		if (MPDHDInfo.isRandom)
			drawable = res.getDrawable(R.drawable.ic_mp_shuffle_on_btn);
		else
			drawable = res.getDrawable(R.drawable.ic_mp_shuffle_off_btn);
		random.setImageDrawable(drawable);
	}

	/**
	 * Updates the RandomButtonUI on the NowPlaying Sidebar. This must be run on
	 * the UI Thread.
	 * 
	 * @param _activity
	 */
	private static void updateRandomSidebarButtonUI(Activity _activity) {
		ImageButton random = (ImageButton) _activity
				.findViewById(R.id.randomSidebarImageButton);
		Resources res = _activity.getResources();
		Drawable drawable;
		if (MPDHDInfo.isRandom)
			drawable = res.getDrawable(R.drawable.ic_mp_shuffle_on_btn);
		else
			drawable = res.getDrawable(R.drawable.ic_mp_shuffle_off_btn);
		random.setImageDrawable(drawable);
	}

	private static void updateMPDHDInfo() {
		JMPDHelper2 jmpd = JMPDHelper2.getInstance(); // establish connection

		MPDHDInfo.isPaused = jmpd.isPaused();
		MPDHDInfo.isRandom = jmpd.isRandom();
		MPDHDInfo.isRepeat = jmpd.isRepeat();
	}

}