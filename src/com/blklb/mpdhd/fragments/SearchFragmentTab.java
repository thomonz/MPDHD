package com.blklb.mpdhd.fragments;

import android.app.Fragment;
import android.os.Bundle;
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

public class SearchFragmentTab extends Fragment {

	private String tag = this.toString();

	@Override
	public void onStart() {
		super.onStart();
		this.registerForContextMenu(getView());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_search, container, false);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		try {
		
		menu.setHeaderTitle(""
				+ MyContextMenuInfo.searchSelectedSong.getTitle());
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.search_item_menu, menu);
		} catch (NullPointerException e) {
			//this is hit if we accidently long click on nothing
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// We would check which item we are grabbing but we know it's delete
		// since that's the only choice
		if (item.getItemId() == R.id.add) { // add to playlist
			new Thread(new Runnable() {
				@Override
				public void run() {
					JMPDHelper2.getInstance().addSong(
							MyContextMenuInfo.searchSelectedSong);
				}
			}).start();
			return true;
		} else if (item.getItemId() == R.id.replace) { // replace
			new Thread(new Runnable() {
				@Override
				public void run() {
					JMPDHelper2.getInstance().replace(
							MyContextMenuInfo.searchSelectedSong);
				}
			}).start();
			return true;
		} else { // add and play
			new Thread(new Runnable() {
				@Override
				public void run() {
					JMPDHelper2.getInstance().addAndPlay(
							MyContextMenuInfo.searchSelectedSong);
				}
			}).start();
			return true;
		}

	}

}
