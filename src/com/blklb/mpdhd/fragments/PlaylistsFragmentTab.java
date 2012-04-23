package com.blklb.mpdhd.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;

import com.blklb.mpdhd.R;
import com.blklb.mpdhd.tools.JMPDHelper2;
import com.blklb.mpdhd.tools.MyContextMenuInfo;


public class PlaylistsFragmentTab extends Fragment {
	
	
	@Override
	public void onStart() {
		super.onStart();
		this.registerForContextMenu(getView());
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_playlists, container, false);
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		//menu.setHeaderTitle(MyContextMenuInfo.playlist.getName());
		
		
		
		
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
					JMPDHelper2.getInstance().addPlaylist(MyContextMenuInfo.savedPlaylistIndex);
				}
			}).start();
			return true;
		case R.id.add_play:
			new Thread(new Runnable() {
				@Override
				public void run() {
					JMPDHelper2.getInstance().addAndPlayPlaylist(MyContextMenuInfo.savedPlaylistIndex);
				}
			}).start();
			return true;
		case R.id.replace:
			new Thread(new Runnable() {
				@Override
				public void run() {
					JMPDHelper2.getInstance().replacePlaylist(MyContextMenuInfo.savedPlaylistIndex);
				}
			}).start();
			return true;
		}
		
		return false;
	}

	
	
	
}
