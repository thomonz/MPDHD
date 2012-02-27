package com.blklb.mpdhd;

import java.util.TimerTask;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;

import com.blklb.mpdhd.R;
import com.blklb.mpdhd.tools.MPDHDInfo;
import com.blklb.mpdhd.tools.TabType;
import com.blklb.mpdhd.tools.TimerHelper;
import com.blklb.mpdhd.ui.UIUtilities;

public class MyTabsListener implements ActionBar.TabListener {
	private Fragment fragment;
	private String tag = "MyTabsListener";

	public MyTabsListener(Fragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.setCustomAnimations(android.R.animator.fade_in,
				android.R.animator.fade_out);
		ft.replace(R.id.fragment_place, fragment, null);

		// This sets up stuff for the new tab UI.
		// New button listeners should go here
		String s = fragment.toString().substring(0,
				fragment.toString().lastIndexOf('{'));

		if (s.equals("DatabaseFragmentTab")) {
			MPDHDInfo.currentTab = TabType.Database;

			TimerHelper.getInstance().scheduleTask(new TimerTask() {
				@Override
				public void run() {
					try {
						UIUtilities.setupDatabaseTabButtonListeners(fragment
								.getActivity());
					} catch (NullPointerException e) {
						Log.w(tag,
								"Transitioned too quick between tabs. No worries though we caught you.");
					}
				}
			}, 100);

		} else if (s.equals("NowPlayingFragmentTab")) {
			MPDHDInfo.currentTab = TabType.NowPlaying;

			TimerHelper.getInstance().scheduleTask(new TimerTask() {
				@Override
				public void run() {
					try {
						UIUtilities.setupNowPlayingTabButtonListeners(fragment
								.getActivity());
					} catch (NullPointerException e) {
						Log.w(tag,
								"Transitioned too quick between tabs. No worries though we caught you.");
					}
				}
			}, 100);

		} else if (s.equals("PlaylistsFragmentTab")) {
			MPDHDInfo.currentTab = TabType.Playlists;

			TimerHelper.getInstance().scheduleTask(new TimerTask() {
				@Override
				public void run() {
					try {
						UIUtilities.setupPlaylistTabButtonListeners(fragment
								.getActivity());
					} catch (NullPointerException e) {
						Log.w(tag,
								"Transitioned too quick between tabs. No worries though we caught you.");
					}
				}
			}, 100);

		} else if (s.equals("QueueFragmentTab")) {
			MPDHDInfo.currentTab = TabType.Queue;

			TimerHelper.getInstance().scheduleTask(new TimerTask() {
				@Override
				public void run() {
					try {
						UIUtilities.setupQueueTabButtonListeners(fragment
								.getActivity());
					} catch (NullPointerException e) {
						Log.w(tag,
								"Transitioned too quick between tabs. No worries though we caught you.");
					}
				}
			}, 100);

		} else { // search
			MPDHDInfo.currentTab = TabType.Search;

			TimerHelper.getInstance().scheduleTask(new TimerTask() {
				@Override
				public void run() {
					try {
						UIUtilities.setupSearchTabButtonListeners(fragment
								.getActivity());
					} catch (NullPointerException e) {
						Log.w(tag,
								"Transitioned too quick between tabs. No worries though we caught you.");
					}
				}
			}, 100);
		}

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (fragment != null) {
			// Detach the fragment, because another one is being attached
			ft.remove(fragment);
		}
	}
}