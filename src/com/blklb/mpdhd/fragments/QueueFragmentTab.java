package com.blklb.mpdhd.fragments;

import android.app.Fragment;
import android.os.Bundle;
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

public class QueueFragmentTab extends Fragment {

	private String tag = this.toString();

	@Override
	public void onStart() {
		super.onStart();
		this.registerForContextMenu(getView());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_queue, container, false);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle(""+MyContextMenuInfo.queueTrackName);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.queue_item_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// We would check which item we are grabbing but we know it's delete
		// since that's the only choice
		new Thread(new Runnable() {
			@Override
			public void run() {
				JMPDHelper2.getInstance().removeSong(MyContextMenuInfo.queuePosition);
			}
		}).start();
		return true;
	}

}