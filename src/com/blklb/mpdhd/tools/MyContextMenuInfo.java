package com.blklb.mpdhd.tools;

import org.bff.javampd.objects.MPDAlbum;
import org.bff.javampd.objects.MPDArtist;
import org.bff.javampd.objects.MPDItem;
import org.bff.javampd.objects.MPDSong;

/**
 * This is an ugly hack to work around the broken context menu.
 * Bug report for the prblem is located at
 * http://code.google.com/p/android/issues/detail?id=7139
 * 
 * @author ablackbu
 *
 */
public class MyContextMenuInfo {
	
	public static String queueTrackName = "";
	public static int queuePosition = 0;
	
	public static MPDSong searchSelectedSong = null;	
	
	public static MPDArtist dbArtistSelected = null;
	public static MPDAlbum dbAlbumSelected = null;
	public static MPDSong dbSongSelected = null;
	

}
