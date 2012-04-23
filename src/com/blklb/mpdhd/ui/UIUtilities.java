package com.blklb.mpdhd.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bff.javampd.objects.MPDAlbum;
import org.bff.javampd.objects.MPDArtist;
import org.bff.javampd.objects.MPDSavedPlaylist;
import org.bff.javampd.objects.MPDSong;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.blklb.mpdhd.R;
import com.blklb.mpdhd.services.ServiceInfo;
import com.blklb.mpdhd.tasks.LastFMCoverHelper;
import com.blklb.mpdhd.tools.JMPDHelper2;
import com.blklb.mpdhd.tools.MPDHDInfo;
import com.blklb.mpdhd.tools.MyContextMenuInfo;

/**
 * This class houses UI Update utilities.
 * 
 * @author ablackbu
 * 
 */
public class UIUtilities {

	private static ArrayList<HashMap<String, Object>> songlist;
	private static ArrayList<HashMap<String, Object>> playlistSonglist;
	

	private static String tag = "UIUtilities";
	private static Drawable art;
	private static boolean refreshQueue = true;

	private static MPDSong mpdSongCache;

	// For the
	private static EditText search;
	private static Collection<MPDSong> searchResults;

	// Used with the WebView
	private static WebView mWebView;
	private static WebView mLyricsWebView;

	// Caches
	private static String urlCache;
	private static String lyricsURLCache;
	private static String artistCache;
	private static String songCache;
	private static String artistCacheNotification;
	private static String songCacheNotification;

	// holds the song list
	private static Collection<MPDSong> filteredSongList;
	private static Collection<MPDAlbum> filteredAlbumList;
	
	
	private static MPDSavedPlaylist playlist;
	

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
						if (ServiceInfo.isStreaming)
							if (MPDHDInfo.isPaused)
								fixStream(a);
							else
								destroyStream(a);

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
						if (ServiceInfo.isStreaming)
							fixStream(a);
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
						if (ServiceInfo.isStreaming)
							fixStream(a);
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
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		setupWebViewUI(a);
		setupLyricsWebViewUI(a);
	}

	public static void setupDatabaseTabButtonListeners(Activity _activity) {
		setupNowPlayingSidebarListeners(_activity);

		final Activity a = _activity;

		// Populate the artist view
		Collection<MPDArtist> artistDb = JMPDHelper2.getInstance()
				.getAllArtists();

		final ArrayList<String> artTemp = new ArrayList<String>();
		for (MPDArtist _artist : artistDb) {
			artTemp.add(_artist.getName());
		}
		Collections.sort(artTemp);
		artTemp.remove(0);
		artTemp.add(0, "*ALL*");

		final ArrayAdapter<String> artists = new ArrayAdapter<String>(a,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				artTemp);

		// Populate the album view
		Collection<MPDAlbum> albumDb = JMPDHelper2.getInstance().getAllAlbums();

		final ArrayList<String> albumTemp = new ArrayList<String>();
		for (MPDAlbum _album : albumDb) {
			albumTemp.add(_album.getName());
		}
		Collections.sort(albumTemp);
		albumTemp.remove(0);
		albumTemp.add(0, "*ALL*");

		// Collections.sort((MPDAlbum[]) albumDb.toArray());

		filteredAlbumList = albumDb;

		final ArrayAdapter<String> albums = new ArrayAdapter<String>(a,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				albumTemp);

		// Populate the songs view
		ArrayList<String> songTemp = new ArrayList<String>();

		songTemp.add("Displaying all songs is not a good idea. Please select an artist or album.");

		final ArrayAdapter<String> songs = new ArrayAdapter<String>(a,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				songTemp);

		a.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ListView artistListView = (ListView) a
						.findViewById(R.id.artistListView);
				ListView albumListView = (ListView) a
						.findViewById(R.id.albumListView);
				ListView songListView = (ListView) a
						.findViewById(R.id.songListView);

				artistListView.setAdapter(artists);
				albumListView.setAdapter(albums);
				songListView.setAdapter(songs);

				artistListView
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO reflow the songs & album view

								MPDHDInfo.lastSelectedArtist = artTemp
										.get(position);
								new Thread(new Runnable() {
									@Override
									public void run() {
										reflowSongs(a,
												MPDHDInfo.lastSelectedArtist,
												MPDHDInfo.lastSelectedAlbum);
										reflowAlbums(a,
												MPDHDInfo.lastSelectedArtist);
									}
								}).start();

							}
						});

				artistListView
						.setOnItemLongClickListener(new OnItemLongClickListener() {

							@Override
							public boolean onItemLongClick(
									AdapterView<?> parent, View v,
									int position, long id) {
								
								Log.w(tag, "kjeblwie: " + artTemp.get(position));

								
								if (position == 0)
									return true;

								MyContextMenuInfo.dbArtistSelected = new MPDArtist(artTemp.get(position));
								MyContextMenuInfo.dbAlbumSelected = null;
								MyContextMenuInfo.dbSongSelected = null;

								return false; // This forces the context menu to
												// be hit

							}
						});
				artistListView.setLongClickable(true);

				albumListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {

						int positionOffset;
						
						if (MPDHDInfo.lastSelectedArtist.equals("*ALL*")) {
							//if(MPDHDInfo.lastSelectedAlbum.equals("*ALL*"))
								positionOffset = 0;
							//else
								//positionOffset = 0;
						}
							
						else {
							//if(!MPDHDInfo.lastSelectedAlbum.equals("*ALL*"))
							positionOffset = 1;
						}
						if (position == 0) { // this is the all
							MPDHDInfo.lastSelectedAlbum = "*ALL*";
						} else {
							ArrayList<String> temp = new ArrayList<String>();
							for (MPDAlbum alb : filteredAlbumList) {
								temp.add(alb.getName());
							}
							Collections.sort(temp);
							try{
							MPDHDInfo.lastSelectedAlbum = temp.get(position
									- positionOffset); //TODO: FIgure out out of bounds exception
							} catch (IndexOutOfBoundsException e) {
								Log.w(tag, "out of bounds");
								MPDHDInfo.lastSelectedAlbum = temp.get(position
										- positionOffset -1);
							}
						}

						new Thread(new Runnable() {
							@Override
							public void run() {
								reflowSongs(a, MPDHDInfo.lastSelectedArtist,
										MPDHDInfo.lastSelectedAlbum);
							}
						}).start();

					}
				});

				albumListView
						.setOnItemLongClickListener(new OnItemLongClickListener() {

							@Override
							public boolean onItemLongClick(AdapterView<?> arg0,
									View arg1, int position, long arg3) {
								
								int positionOffset;
								
								if (MPDHDInfo.lastSelectedArtist.equals("*ALL*")) {
									//if(MPDHDInfo.lastSelectedAlbum.equals("*ALL*"))
										positionOffset = 0;
									//else
									//	positionOffset = 0;
								}
									
								else {
									//if(!MPDHDInfo.lastSelectedAlbum.equals("*ALL*"))
									positionOffset = 1;
								}
									
								
								
								
								
								ArrayList<String> temp = new ArrayList<String>();
								for (MPDAlbum alb : filteredAlbumList) {
									temp.add(alb.getName());
								}
								Collections.sort(temp);
								
								MyContextMenuInfo.dbAlbumSelected = new MPDAlbum(temp.get(position-positionOffset));
								//MyContextMenuInfo.dbAlbumSelected = new MPDAlbum(temp.get(position));
								MyContextMenuInfo.dbArtistSelected = null;
								MyContextMenuInfo.dbSongSelected = null;
								return false;
							}

						});
				albumListView.setLongClickable(true);

				songListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View v,
							int position, long arg3) {

						// ListView lv = (ListView) v;
						// String songClicked = (String)
						// lv.getAdapter().getItem(position);

						final int pos = position;

						// Add position to the queue
						new Thread(new Runnable() {

							@Override
							public void run() {
								JMPDHelper2.getInstance()
										.addSong(
												(MPDSong) filteredSongList
														.toArray()[pos]);
							}

						}).start();
						
						
						
						MPDSong s = (MPDSong) filteredSongList.toArray()[pos];

						int duration = Toast.LENGTH_SHORT;
						String text = "Added Song:	" + s.getTitle()
								+ "	by	" + s.getArtist();

						Toast toast = Toast.makeText(a,
								text, duration);
						toast.show();
						

						
					}

				});

				songListView
						.setOnItemLongClickListener(new OnItemLongClickListener() {

							@Override
							public boolean onItemLongClick(AdapterView<?> arg0,
									View arg1, int position, long arg3) {
								MyContextMenuInfo.dbAlbumSelected = null;
								MyContextMenuInfo.dbArtistSelected = null;
								MyContextMenuInfo.dbSongSelected = (MPDSong) filteredSongList.toArray()[position];
								return false;
							}

						});
				songListView.setLongClickable(true);

			}
		});

		// This is a little hack since for some reason before the initial reflow
		// it's off by one
		// therefore when i start messing with the offset its alwasy messed up
		// reflowAlbums(a, MPDHDInfo.lastSelectedArtist);

	}

	public static void setupQueueTabButtonListeners(Activity _activity) {
		setupNowPlayingSidebarListeners(_activity);
		refreshQueue = true;
	}

	public static void setupSearchTabButtonListeners(Activity _activity) {
		setupNowPlayingSidebarListeners(_activity);

		final Activity a = _activity;

		ImageButton searchBtn = (ImageButton) a
				.findViewById(R.id.searchImageButton);
		search = (EditText) a.findViewById(R.id.searchEditText);

		InputMethodManager mgr = (InputMethodManager) a
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// only will trigger it if no physical keyboard is open
		mgr.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);

		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { // Grabs the string from the edit text
											// run a query
				final String tmp = search.getText().toString();

				new Thread(new Runnable() {
					@Override
					public void run() {
						searchResults = JMPDHelper2.getInstance().search(tmp);
						updateSearchResults(a);
					}
				}).start();
			}
		});

		search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					final String tmp = search.getText().toString();

					new Thread(new Runnable() {
						@Override
						public void run() {
							searchResults = JMPDHelper2.getInstance().search(
									tmp);
							updateSearchResults(a);
						}
					}).start();
					return true;
				}
				return false;
			}
		});

		if (!search.getText().toString().isEmpty() && searchResults != null)
			updateSearchResults(_activity);

	}

	public static void setupPlaylistTabButtonListeners(Activity _activity) {
		setupNowPlayingSidebarListeners(_activity);
		// TODO:Finish
		
		//TODO: FIX
		final ArrayList<String> saved = JMPDHelper2.getInstance().getSavedPlaylistsNames(); /// Incredibly slow operation

		
		
		Log.w(tag, "Saved: " + saved.size());
		final Activity a = _activity;

		final ArrayAdapter<String> savedAdapter = new ArrayAdapter<String>(_activity,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				saved);
		
		_activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ListView v = (ListView) a.findViewById(R.id.savedPlaylistListView);
				try {
				v.setAdapter(savedAdapter);
				} catch (NullPointerException e) {
					//This happens when you switch to quickly because of the dumb slow operations
				}
				
				v.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						Log.w(tag, "hit: " +saved.get(position));
						final int pos = position;
						
						new Thread(new Runnable() {

							@Override
							public void run() {
								
								Log.e(tag, "Before call 2");
								//TODO: Redo this because it's a very costly command
								playlist = JMPDHelper2.getInstance().getSavedPlaylist(pos); //Slow operation
								
								Log.e(tag, "After call 2");
								
								playlistSonglist = new ArrayList<HashMap<String, Object>>();

								for (MPDSong song : playlist.getSongs()) {
									HashMap<String, Object> item = new HashMap<String, Object>();
									item.put("songid", song.getId());
									item.put("artist", song.getArtist());
									item.put("title", song.getTitle());
									item.put("album", song.getAlbum());
									item.put("play", 0);
									playlistSonglist.add(item);
								}

								
								final SimpleAdapter songs = new SimpleAdapter(a, playlistSonglist,
										R.layout.queue_list_item, new String[] { "play", "title",
												"artist", "album" }, new int[] { R.id.picture,
												android.R.id.text1, android.R.id.text2,
												R.id.album_list_item });
								
								
								a.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										ListView v = (ListView) a.findViewById(R.id.savedPlaylistSongListView);
										v.setAdapter(songs);
										Log.e(tag, "Size:"+ playlistSonglist.size());
										
										v.setOnItemClickListener(new OnItemClickListener () {

											@Override
											public void onItemClick(
													AdapterView<?> arg0,
													View arg1, int i,
													long arg3) {
													Log.w(tag, "Clicked: " + playlistSonglist.get(i));														
											}
											
										});
									}
									
								});
								
								
							}
						
						}).start();
						
						
					}
				});
			
				
				v.setOnItemLongClickListener(new OnItemLongClickListener () {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						MyContextMenuInfo.savedPlaylistIndex = arg2;
						return false;
					}
					
				});
				
				
				
			}
			
		});
		

		
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
						if (ServiceInfo.isStreaming)
							fixStream(activity);
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
						if (ServiceInfo.isStreaming)
							fixStream(activity);
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
						if (ServiceInfo.isStreaming)
							fixStream(activity);
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
		updateMPDHDInfo(_activity);

		JMPDHelper2 jmpd = JMPDHelper2.getInstance(); // establish connection

		final int trackLength = jmpd.getCurrentTrackLength();
		final int elapsedTime = jmpd.getElapsedTime();
		final String track = jmpd.getCurrentTrackTitle();
		final String artist = jmpd.getCurrentTrackArtist();
		final String album = jmpd.getCurrentTrackAlbum();
		final int volume = jmpd.getVolume();

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

					ProgressBar volBar = (ProgressBar) activity
							.findViewById(R.id.volumeSidebarProgressBar);
					volBar.setProgress(volume);

					updatePlayPauseSidebarButtonUI(activity);
					updateRepeatSidebarButtonUI(activity);
					updateRandomSidebarButtonUI(activity);

					updateCoverArtSidebarUI(activity);

					updateNotification(activity);

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

		updateMPDHDInfo(_activity);
		JMPDHelper2 jmpd = JMPDHelper2.getInstance(); // establish connection

		final int trackLength;
		final int elapsedTime;
		final String track;
		final String artist;
		final String album;
		final int volume;

		final Activity activity = _activity;

		//final boolean notPlaying = jmpd.isStopped();

		trackLength = jmpd.getCurrentTrackLength();
		elapsedTime = jmpd.getElapsedTime();
		track = jmpd.getCurrentTrackTitle();
		artist = jmpd.getCurrentTrackArtist();
		album = jmpd.getCurrentTrackAlbum();
		volume = jmpd.getVolume();

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

					ProgressBar volBar = (ProgressBar) activity
							.findViewById(R.id.volumeProgressBar);
					volBar.setProgress(volume);

					// Read Current state and update ui before buttons are
					// pressed
					updatePlayPauseButtonUI(activity);
					updateRepeatButtonUI(activity);
					updateRandomButtonUI(activity);

					// Attempts to fetch an album cover
					updateCoverArtUI(activity);

					updateWebViewUI(activity, artist);
					updateLyricsWebViewUI(activity, artist, track);

					updateNotification(activity);

				} catch (NullPointerException e) {
					// Ignore this will be thrown if it's caught inside of the
					// method when the view is switched
				}
			}
		});

	}

	public static void updateQueueUI(Activity _activity) {
		updateNowPlayingSidebarUI(_activity);
		JMPDHelper2 jmpd = JMPDHelper2.getInstance(); // establish connection

		final Activity activity = _activity;

		if (!jmpd.getNowPlayingSong().equals(mpdSongCache)) {
			refreshQueue = true;
			mpdSongCache = jmpd.getNowPlayingSong();
		}

		if (refreshQueue || jmpd.playlistChanged()) {

			final List<MPDSong> q = jmpd.getMPDPlaylist();

			songlist = new ArrayList<HashMap<String, Object>>();

			for (MPDSong song : q) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("songid", song.getId());
				item.put("artist", song.getArtist());
				item.put("title", song.getTitle());
				item.put("album", song.getAlbum());
				if (song.equals(JMPDHelper2.getInstance().getNowPlayingSong())) {
					item.put("play", android.R.drawable.ic_media_play);
				} else {
					item.put("play", 0);
				}
				songlist.add(item);
			}

			final SimpleAdapter songs = new SimpleAdapter(_activity, songlist,
					R.layout.queue_list_item, new String[] { "play", "title",
							"artist", "album" }, new int[] { R.id.picture,
							android.R.id.text1, android.R.id.text2,
							R.id.album_list_item });

			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					try {
						ListView queueListView = (ListView) activity
								.findViewById(R.id.queueListView);

						queueListView.setAdapter(songs);

						queueListView
								.setOnItemClickListener(new OnItemClickListener() {
									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										refreshQueue = true;
										final int pos = position;
										new Thread(new Runnable() {
											@Override
											public void run() {
												JMPDHelper2.getInstance()
														.playSong(pos);
											}
										}).start();

										MyContextMenuInfo.queuePosition = position;

									}

								});

						queueListView
								.setOnItemLongClickListener(new OnItemLongClickListener() {

									@Override
									public boolean onItemLongClick(
											AdapterView<?> parent, View v,
											int position, long id) {
										MyContextMenuInfo.queuePosition = position;
										MyContextMenuInfo.queueTrackName = q
												.get(position).getTitle();
										return false;
									}

								});

						queueListView.setLongClickable(true);

						// queueListView.requestFocusFromTouch();
						if (MyContextMenuInfo.queuePosition != 0)
							queueListView
									.setSelection(MyContextMenuInfo.queuePosition - 1);
						else
							queueListView
									.setSelection(MyContextMenuInfo.queuePosition);

					} catch (NullPointerException e) {
						// Ignore this will be thrown if it's caught inside
						// ofthe
						// method when the view is switched. There's no way I
						// know to fix this other then just catch the exception.
						// Since it can be hit anywhere in this chunk.
						e.printStackTrace();
					}
				}

			});
			refreshQueue = false;
		}

	}

	public static void updateDatabaseUI(Activity _activity) {
		updateNowPlayingSidebarUI(_activity);
		// TODO:Finish
	}

	/**
	 * May need to changet this to private since it shouldn't need to be
	 * accessed by anything else.
	 * 
	 * @param _activity
	 */
	public static void updateSearchUI(Activity _activity) {
		updateNowPlayingSidebarUI(_activity);
	}

	private static void updateSearchResults(Activity _activity) {
		// TODO: GRAB THE collection
		if (searchResults == null) // else populate the view
			return;

		//JMPDHelper2 jmpd = JMPDHelper2.getInstance(); // establish connection

		final Activity activity = _activity;

		songlist = new ArrayList<HashMap<String, Object>>();

		for (MPDSong song : searchResults) {
			if(song.getTitle() != null) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("songid", song.getId());
				item.put("artist", song.getArtist());
				item.put("title", song.getTitle());
				item.put("album", song.getAlbum());
				songlist.add(item);
				}
		}

		final SimpleAdapter songs = new SimpleAdapter(_activity, songlist,
				R.layout.queue_list_item, new String[] { "play", "title",
						"artist", "album" }, new int[] { R.id.picture,
						android.R.id.text1, android.R.id.text2,
						R.id.album_list_item });

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				try {
					ListView searchListView = (ListView) activity
							.findViewById(R.id.searchListView);

					searchListView.setAdapter(songs);

					searchListView
							.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									final int pos = position;

									new Thread(new Runnable() {
										@Override
										public void run() {
											MPDSong s = (MPDSong) searchResults
													.toArray()[pos];
											JMPDHelper2.getInstance()
													.addSong(s);
										}
									}).start();

									// Toast to let the user know that they
									// added the track
									MPDSong s = (MPDSong) searchResults
											.toArray()[pos];

									int duration = Toast.LENGTH_SHORT;
									String text = "Added Song:	" + s.getTitle()
											+ "	by	" + s.getArtist();

									Toast toast = Toast.makeText(activity,
											text, duration);
									toast.show();

								}
							});

					searchListView
							.setOnItemLongClickListener(new OnItemLongClickListener() {

								@Override
								public boolean onItemLongClick(
										AdapterView<?> parent, View v,
										int position, long id) {

									MyContextMenuInfo.searchSelectedSong = (MPDSong) searchResults
											.toArray()[position];
									return false;
								}

							});

					searchListView.setLongClickable(true);

				} catch (NullPointerException e) {
					Log.w(tag,
							"Woah nelly! You're switching tabs quite quickly.");
				}
			}
		});

	}

	public static void updatePlaylistUI(Activity _activity) {
		updateNowPlayingSidebarUI(_activity);
		// TODO:Finish
	}

	private static void updateCoverArtUI(Activity _activity) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				String artist = JMPDHelper2.getInstance()
						.getCurrentTrackArtist();
				String album = JMPDHelper2.getInstance().getCurrentTrackAlbum();
				String track = JMPDHelper2.getInstance().getCurrentTrackTitle();

				LastFMCoverHelper.getInstance().update(album, artist, track);
				art = LastFMCoverHelper.getInstance().getArtwork();
			}
		}).start();

		if (!art.equals(null)) {
			// update picture
			ImageView albumArt = (ImageView) _activity
					.findViewById(R.id.albumArtImageView);
			albumArt.setAnimation(AnimationUtils.loadAnimation(
					albumArt.getContext(), android.R.anim.fade_in));
			if (!albumArt.getDrawable().equals(art)) {
				albumArt.setImageDrawable(art);
			} else {
				albumArt.setAnimation(null);
			}
		}
	}

	private static void updateCoverArtSidebarUI(Activity _activity) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
				
				String artist = JMPDHelper2.getInstance()
						.getCurrentTrackArtist();
				String album = JMPDHelper2.getInstance().getCurrentTrackAlbum();
				String track = JMPDHelper2.getInstance().getCurrentTrackTitle();

				LastFMCoverHelper.getInstance().update(album, artist, track);
				art = LastFMCoverHelper.getInstance().getArtwork();
				} catch (NullPointerException e) {
					e.printStackTrace();
					return;
				}
				
			}
		}).start();

		if (!art.equals(null)) {
			// update picture
			ImageView albumArt = (ImageView) _activity
					.findViewById(R.id.albumArtSidebarImageView);
			albumArt.setAnimation(AnimationUtils.loadAnimation(
					albumArt.getContext(), android.R.anim.fade_in));
			if (!albumArt.getDrawable().equals(art)) {
				albumArt.setImageDrawable(art);
			} else {
				albumArt.setAnimation(null);
			}
		}
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

	/**
	 * This updates the web view ui. It checks for changes in the artist to
	 * prevent excessive page requests. This should be used in the UI updater.
	 * 
	 * @param _activity
	 */
	private static void updateWebViewUI(Activity _activity, String artist) {

		final String url;
		String wikiBaseURL = "http://en.m.wikipedia.org/wiki/";

		artist.replace(' ', '_');

		if (artist.equals("Not Connected to MPD Server")
				|| artist.equals(artistCache)) { // if it's the same artist we
													// want the
			// cache to load incase they
			// navigated to a new page
			return;
		} else {
			url = wikiBaseURL + artist;
			artistCache = artist;
		}

		final Activity activity = _activity;

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mWebView = (WebView) activity.findViewById(R.id.wikiWebView);
				mWebView.getSettings().setJavaScriptEnabled(true);
				mWebView.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view,
							String url) {
						view.loadUrl(url);
						return true;
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						urlCache = url;
					}
				});
				mWebView.loadUrl(url);
			}
		});

	}

	/**
	 * This updates the web view ui. It checks for changes in the artist to
	 * prevent excessive page requests. This should be used in the UI updater.
	 * 
	 * @param _activity
	 */
	private static void updateLyricsWebViewUI(Activity _activity,
			String artist, String song) {
		final String url;
		String wikiBaseURL = "http://lyrics.wikia.com/";

		artist.replace(' ', '_');
		song.replace(' ', '_');

		if (artist.equals("Not Connected to MPD Server")
				|| (artist.equals(artistCache) && song.equals(songCache))) { // if
																				// it's
																				// the
																				// same
																				// artist
																				// we
			// want the
			// cache to load incase they
			// navigated to a new page
			return;
		} else {
			url = wikiBaseURL + artist + ":" + song;
			artistCache = artist;
			songCache = song;

			// If we need to update the lyrics then its a new song, if its a new
			// song we need to update our notification icon
			updateNotification(_activity);
		}

		final Activity activity = _activity;

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mLyricsWebView = (WebView) activity
						.findViewById(R.id.lyricsWebView);
				mLyricsWebView.getSettings().setJavaScriptEnabled(true);
				mLyricsWebView.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view,
							String url) {
						view.loadUrl(url);
						return true;
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						lyricsURLCache = url;
					}
				});
				mLyricsWebView.loadUrl(url);
			}
		});
	}

	/**
	 * This forces a new request of the page. It does support caching so if the
	 * artist hasn't changed and the user has moved from the initial search it
	 * will return to the previous. This should only be used for setup when the
	 * tab is initially selected or reselected to prevent excessive page
	 * requests
	 * 
	 * @param _activity
	 */
	private static void setupWebViewUI(Activity _activity) {

		Log.w(tag, ("Build #" + Build.VERSION.SDK_INT));

		final String url;
		String wikiBaseURL = "http://en.wikipedia.org/wiki/";
		String artist = JMPDHelper2.getInstance().getCurrentTrackArtist();

		artist.replace(' ', '_');

		if (artist.equals(artistCache)) { // if it's the same artist we want the
											// cache to load incase they
											// navigated to a new page
			url = urlCache;
		} else {
			url = wikiBaseURL + artist;
			artistCache = artist;
		}

		final Activity activity = _activity;

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mWebView = (WebView) activity.findViewById(R.id.wikiWebView);
				mWebView.getSettings().setJavaScriptEnabled(true);
				mWebView.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view,
							String url) {
						view.loadUrl(url);
						return true;
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						urlCache = url;
					}
				});
				mWebView.loadUrl(url);
			}
		});

	}

	private static void setupLyricsWebViewUI(Activity _activity) {

		final String url;
		String wikiBaseURL = "http://lyrics.wikia.com/";
		String artist = JMPDHelper2.getInstance().getCurrentTrackArtist();
		artist.replace(' ', '_');
		String song = JMPDHelper2.getInstance().getCurrentTrackTitle();
		song.replace(' ', '_');

		if (artist.equals(artistCache) && song.equals(songCache)) { // if it's
																	// the same
																	// song we
																	// want the
			// cache to load incase they
			// navigated to a new page
			url = lyricsURLCache;
		} else {
			url = wikiBaseURL + artist + ":" + song;
			artistCache = artist;
		}

		final Activity activity = _activity;

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mLyricsWebView = (WebView) activity
						.findViewById(R.id.lyricsWebView);
				mLyricsWebView.getSettings().setJavaScriptEnabled(true);
				mLyricsWebView.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view,
							String url) {
						view.loadUrl(url);
						return true;
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						lyricsURLCache = url;
					}
				});
				mLyricsWebView.loadUrl(url);
			}
		});
	}

	private static void updateMPDHDInfo(Activity _activity) {
		JMPDHelper2 jmpd = JMPDHelper2.getInstance(); // establish connection
		//
		// final Activity _a = _activity;
		//
		// if ((MPDHDInfo.isPaused && !jmpd.isPaused() &&
		// ServiceInfo.isStreaming)) {
		// // restart the stream because its been interrupted by a pause
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// if (JMPDHelper2.getInstance().isConnected()) {
		// Log.e(tag, "Restart Streaming Thread");
		// if (ServiceInfo.mBound) {
		// ServiceInfo.mService.playStream(_a);
		// } else {
		// Log.e(tag, "mBound False");
		// }
		// }
		// }
		// }).start();
		// }

		MPDHDInfo.isPaused = jmpd.isPaused();
		MPDHDInfo.isRandom = jmpd.isRandom();
		MPDHDInfo.isRepeat = jmpd.isRepeat();
	}

	@SuppressWarnings("static-access")
	public static void setupNotification(Activity _a) {

		// Setup the notification manager and clear out any notifications with
		// the previous notification
		NotificationManager mNotificationManager = (NotificationManager) _a
				.getSystemService(_a.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(MPDHDInfo.NOTIFICATION_ID);

		// Create Notification
		int icon = R.drawable.ic_hardware_headphones;
		CharSequence tickerText = "Buffering"; // ticker-text
		long when = System.currentTimeMillis(); // notification time
		Notification notification = new Notification(icon, tickerText, when);

		// Setup extra flags on the notification - this one is for "ongoing"
		// status
		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		// Create the content view
		RemoteViews contentView = new RemoteViews(_a.getPackageName(),
				R.layout.streaming_notification);
		contentView.setImageViewResource(R.id.image,
				R.drawable.ic_hardware_headphones);
		contentView.setTextViewText(R.id.title, "Buffering");
		contentView.setTextViewText(R.id.text,
				"Please allow time for buffering");

		// Set the content view
		notification.contentView = contentView;

		// Post notification
		mNotificationManager.notify(MPDHDInfo.NOTIFICATION_ID, notification);
	}

	public static void taredownNotification(Activity _a) {
		// Setup the notification manager and clear out any notifications with
		// the previous notification
		NotificationManager mNotificationManager = (NotificationManager) _a
				.getSystemService(_a.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(MPDHDInfo.NOTIFICATION_ID);
	}

	public static void updateNotification(Activity _activity) {

		if (!ServiceInfo.isStreaming)
			return;

		final Activity _a = _activity;

		new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				String artist = JMPDHelper2.getInstance()
						.getCurrentTrackArtist();
				String track = JMPDHelper2.getInstance().getCurrentTrackTitle();

				if (artist.equals("Not Connected to MPD Server")
						|| (artist.equals(artistCacheNotification) && track
								.equals(songCacheNotification))) {
					return;
				}

				artistCacheNotification = artist;
				songCacheNotification = track;

				// Setup the notification manager and clear out any
				// notifications with
				// the previous notification
				NotificationManager mNotificationManager = (NotificationManager) _a
						.getSystemService(_a.NOTIFICATION_SERVICE);
				// mNotificationManager.cancel(MPDHDInfo.NOTIFICATION_ID);

				// Create Notification
				int icon = R.drawable.ic_hardware_headphones;
				CharSequence tickerText = "Streaming Activated"; // ticker-text
				long when = System.currentTimeMillis(); // notification time
				Notification notification = new Notification(icon, tickerText,
						when);

				// Setup extra flags on the notification - this one is for
				// "ongoing"
				// status
				notification.flags |= Notification.FLAG_ONGOING_EVENT;

				// Create the content view
				RemoteViews contentView = new RemoteViews(_a.getPackageName(),
						R.layout.streaming_notification);
				contentView.setImageViewResource(R.id.image,
						R.drawable.ic_hardware_headphones);

				String song = JMPDHelper2.getInstance().getCurrentTrackTitle();
				String artistAlbum = JMPDHelper2.getInstance()
						.getCurrentTrackArtist()
						+ " - "
						+ JMPDHelper2.getInstance().getCurrentTrackAlbum();

				if (song.length() > 50)
					song = song.substring(0, 50) + "...";
				if (artistAlbum.length() > 50)
					artistAlbum = artistAlbum.substring(0, 50) + "...";

				contentView.setTextViewText(R.id.title, song);
				contentView.setTextViewText(R.id.text, artistAlbum);

				// Set the content view
				notification.contentView = contentView;

				// Post notification
				mNotificationManager.notify(MPDHDInfo.NOTIFICATION_ID,
						notification);
			}
		}).start();
	}

	private static void fixStream(Activity _activity) {
		final Activity _a = _activity;

		// restart the stream because its been interrupted by a pause forward or
		// back
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (JMPDHelper2.getInstance().isConnected()) {
					Log.e(tag, "Restart Streaming Thread");

					if (ServiceInfo.mBound) {
						ServiceInfo.mService.streamFix(_a);

					} else {
						Log.e(tag, "mBound False");
					}
				}
			}
		}).start();

	}

	private static void destroyStream(Activity _activity) {
		final Activity _a = _activity;

		// restart the stream because its been interrupted by a pause forward or
		// back
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (JMPDHelper2.getInstance().isConnected()) {
					Log.e(tag, "Destroy Streaming Thread");

					if (ServiceInfo.mBound) {
						ServiceInfo.mService.stopStreamStillStream(_a);

					} else {
						Log.e(tag, "mBound False");
					}
				}
			}
		}).start();

	}

	private static void reflowSongs(Activity _activity, String selectedArtist,
			String selectedAlbum) {

		final Activity a = _activity;

		Collection<MPDSong> songList;

		final ArrayList<String> songTemp = new ArrayList<String>();

		Log.w(tag, "Artist:" + selectedArtist);
		Log.w(tag, "Album:" + selectedAlbum);

		if (selectedArtist.equals("*ALL*") && selectedAlbum.equals("*ALL*")) {
			// TODO: Post message saying we wont display without a filter
			// selected
			songTemp.add("Displaying all songs is not a good idea. Please select an artist or album.");
			songList = null;
			Log.e(tag, "You dont have a filter wtf");
		} else if (selectedArtist.equals("*ALL*")) {
			Log.e(tag,
					"You clicked on an album filter but not an artist filter");
			songList = JMPDHelper2.getInstance().getAlbumFilteredSongs(
					selectedAlbum);
		} else if (selectedAlbum.equals("*ALL*")) {
			Log.e(tag,
					"You clicked on an artist filter but not an album filter");
			songList = JMPDHelper2.getInstance().getArtistFilteredSongs(
					selectedArtist);
		} else {
			Log.e(tag, "You clicked on both filters");
			songList = JMPDHelper2.getInstance().getFilteredSongs(
					selectedArtist, selectedAlbum);
		}

		if (songList != null) {
			for (MPDSong _song : songList) {
				songTemp.add(_song.getName());
			}
			// Collections.sort(songTemp);
		}

		// do UI stuff

		final ArrayAdapter<String> songs = new ArrayAdapter<String>(_activity,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				songTemp);

		_activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ListView songListView = (ListView) a
						.findViewById(R.id.songListView);
				songListView.setAdapter(songs);
			}
		});

		filteredSongList = songList;

	}

	private static void reflowAlbums(Activity _activity, String selectedArtist) {

		final Activity a = _activity;
		Collection<MPDAlbum> albumDb;

		if (selectedArtist.equals("*ALL*"))
			albumDb = JMPDHelper2.getInstance().getAllAlbums();
		else
			albumDb = JMPDHelper2.getInstance().getArtistFilteredAlbums(
					selectedArtist);

		final ArrayList<String> albumTemp = new ArrayList<String>();
		for (MPDAlbum _album : albumDb) {
			if (!_album.toString().equals(""))
				albumTemp.add(_album.getName());
		}

		Collections.sort(albumTemp);
		albumTemp.add(0, "*ALL*");

		final ArrayAdapter<String> albums = new ArrayAdapter<String>(_activity,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				albumTemp);

		_activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ListView albumListView = (ListView) a
						.findViewById(R.id.albumListView);
				albumListView.setAdapter(albums);
			}
		});

		filteredAlbumList = albumDb;
		// filteredSongList = songList;

	}

	private static void reflowArtists(Activity _Activity, String selectedAlbum) {

	}

}
