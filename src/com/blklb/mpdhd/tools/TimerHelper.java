package com.blklb.mpdhd.tools;

import java.util.Timer;
import java.util.TimerTask;

public class TimerHelper {
	
	private Timer timer;
	
	private static TimerHelper instance = null;
	
	private TimerHelper() {
		timer = new Timer(true);
	}
	
	public static TimerHelper getInstance() {
		if(instance == null) {
			instance = new TimerHelper();
		}
		return instance;
	}
	
	public void scheduleTask(TimerTask task, long delay) {
		try {
			timer.schedule(task, delay);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This cancels all scheduled tasks and makes a new timer instance
	 * since the old timer object is useless after a cancel() call  
	 */
	public void cancelScheduledTasks () {
		timer.cancel();
		timer = new Timer(true);
	}
}
