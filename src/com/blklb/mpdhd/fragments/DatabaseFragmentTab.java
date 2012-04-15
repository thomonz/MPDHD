package com.blklb.mpdhd.fragments;

import org.bff.javampd.objects.MPDItem;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.blklb.mpdhd.R;
import com.blklb.mpdhd.tools.JMPDHelper2;
import com.blklb.mpdhd.tools.MyContextMenuInfo;

public class DatabaseFragmentTab extends Fragment {

	private String tag = this.toString();
	private ListType currentList;

	private enum ListType {
		album, artist, song
	}

	@Override
	public void onStart() {
		super.onStart();
		this.registerForContextMenu(getView());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_database, container, false);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		// switch (v.getId()) {
		// case R.id.artistListView:
		// Log.e(tag, "HITTTT");
		// menu.setHeaderTitle(MyContextMenuInfo.dbArtistSelected.toString());
		// break;
		// case R.id.albumListView:
		// Log.e(tag, "HITTTT");
		// menu.setHeaderTitle(MyContextMenuInfo.dbAlbumSelected.getName());
		// break;
		// case R.id.songListView:
		// Log.e(tag, "HITTTT");
		// menu.setHeaderTitle(MyContextMenuInfo.dbSongSelected.getName());
		// break;
		// }

		// Ugly chunk of code because it wont give me the view id i want....

		if (MyContextMenuInfo.dbArtistSelected != null) {
			menu.setHeaderTitle(MyContextMenuInfo.dbArtistSelected.toString());
			currentList = ListType.artist;
		} else if (MyContextMenuInfo.dbAlbumSelected != null) {
			menu.setHeaderTitle(MyContextMenuInfo.dbAlbumSelected.getName());
			currentList = ListType.album;
		} else {
			menu.setHeaderTitle(MyContextMenuInfo.dbSongSelected.getName());
			currentList = ListType.song;
		}

		Log.w(tag, "menuInfo" + MyContextMenuInfo.dbAlbumSelected);

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.search_item_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// We would check which item we are grabbing but we know it's delete
		// since that's the only choice
		
		switch(item.getItemId()) {
		case R.id.add:
			new Thread(new Runnable() {
				@Override
				public void run() {
					addItem();
				}
			}).start();
			return true;
		case R.id.add_play:
			new Thread(new Runnable() {
				@Override
				public void run() {
					addAndPlayItem();
				}
			}).start();
			return true;
		case R.id.replace:
			new Thread(new Runnable() {
				@Override
				public void run() {
					replaceItem();
				}
			}).start();
			return true;
		}
		
		return false;
	}
	
	private void addItem() {
		switch (currentList) {
		case artist:
			JMPDHelper2.getInstance().add(MyContextMenuInfo.dbArtistSelected);
			break;
		case album:
			JMPDHelper2.getInstance().add(MyContextMenuInfo.dbAlbumSelected);
			break;
		case song:
			JMPDHelper2.getInstance().addSong(MyContextMenuInfo.dbSongSelected);
		}
	}
	
	private void addAndPlayItem() {
		switch (currentList) {
		case artist:
			JMPDHelper2.getInstance().addAndPlay(MyContextMenuInfo.dbArtistSelected);
			break;
		case album:
			JMPDHelper2.getInstance().addAndPlay(MyContextMenuInfo.dbAlbumSelected);
			break;
		case song:
			JMPDHelper2.getInstance().addAndPlay(MyContextMenuInfo.dbSongSelected);
		}
	}
	
	private void replaceItem() {
		switch (currentList) {
		case artist:
			JMPDHelper2.getInstance().replace(MyContextMenuInfo.dbArtistSelected);
			break;
		case album:
			JMPDHelper2.getInstance().replace(MyContextMenuInfo.dbAlbumSelected);
			break;
		case song:
			JMPDHelper2.getInstance().replace(MyContextMenuInfo.dbSongSelected);
		}
	}
	
}