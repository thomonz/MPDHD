package com.blklb.mpdhd.tasks;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.blklb.mpdhd.ui.UIInfo;

import android.graphics.drawable.Drawable;

/**
 * 
 * @author ablackbu
 * 
 */
public class LastFMCoverHelper {

	// Android debug tag
	final static String tag = "LastFMCoverHelper";

	// Singleton
	private static LastFMCoverHelper _instance;

	// Cache for the previous track info including artwork
	private String _album;
	private String _artist;
	private String _track;
	private Drawable _artwork;

	/**
	 * 
	 * @return the LastFMConverHelper singleton object
	 */
	public static LastFMCoverHelper getInstance() {
		if (_instance == null)
			_instance = new LastFMCoverHelper();
		return _instance;
	}

	/**
	 * Cstor.
	 */
	private LastFMCoverHelper() {
		_album = "";
		_artist = "";
		_track = "";

		_artwork = null;
	}

	/**
	 * 
	 * @param album
	 * @param artist
	 * @param song
	 */
	public void update(String album, String artist, String track) {
		if ((album.equals(_album)) && (artist.equals(_artist))) { // same
			_track = track;
			return;
		} else {
			_artwork = null;
			_album = album;
			_artist = artist;
			_track = track;
			updateArtworkPrimary(_artist, _album);
		}
	}
	

	public Drawable getArtwork() {
		return _artwork;
	}

	/**
	 * 
	 * @param artist
	 * @param track
	 */
	private void updateArtworkSecondary(String artist, String track) {
		String baseURL = "http://ws.audioscrobbler.com/2.0/?method=track.getinfo";
		String keyHeader = "&api_key=";
		String myKey = "d002a2da3e88bea9b89983f63e368555";
		String artistHeader = "&artist=";

		String trackHeader = "&track=";
		String urlString = baseURL + keyHeader + myKey + artistHeader
				+ fixStringSpaces(artist) + trackHeader
				+ fixStringSpaces(track);

		try {

			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();

			if (conn == null)
				System.out.println("Null conn");

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(conn.getInputStream());

			if (doc == null)
				System.out.println("Null doc");

			DOMSource ds = new DOMSource(doc);

			Node albumNode = ds.getNode().getChildNodes().item(0)
					.getChildNodes().item(1).getChildNodes().item(19);
			Node hqImageNode = albumNode.getChildNodes().item(15);
			String hqImageURL = hqImageNode.getTextContent();

			_artwork = Drawable.createFromStream((InputStream) new URL(
					hqImageURL).getContent(), null);

		} catch (Exception e) {
			// This means last fm didn't provide artwork. Jerks !
			//If this kicks back that means twice we couldn't find the data.
			
			_artwork = UIInfo.unkownDrawable;
		}

	}

	private void updateArtworkPrimary(String artist, String album) {
		String baseURL = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo";
		String keyHeader = "&api_key=";
		String myKey = "d002a2da3e88bea9b89983f63e368555";
		String artistHeader = "&artist=";
		String albumHeader = "&album=";

		String urlString = baseURL + keyHeader + myKey + artistHeader
				+ fixStringSpaces(artist) + albumHeader
				+ fixStringSpaces(album);

		try {

			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();

			if (conn == null)
				System.out.println("Null conn");

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(conn.getInputStream());

			if (doc == null)
				System.out.println("Null doc");

			DOMSource ds = new DOMSource(doc);
			
			int l0 = ds.getNode().getChildNodes().item(0).getChildNodes().item(0).getChildNodes().getLength();
			System.out.println("l0:"+ l0);
			
			
			int l1 = ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().getLength();
			System.out.println("l1:"+ l1);
			
			//Node 31 is the one with all the data, it has 31 child nodes and is named as such.
			Node thirtyOne = ds.getNode().getChildNodes().item(0).getChildNodes().item(1);
			
			Node hqImageNode = thirtyOne.getChildNodes().item(19);
			String hqImageURL = hqImageNode.getTextContent();
			
			_artwork = Drawable.createFromStream((InputStream) new URL(
					hqImageURL).getContent(), null);
			
		} catch (Exception e) {
			// This means last fm didn't provide artwork. Jerks
			//Attempt the backup pull
			updateArtworkSecondary(_artist, _track); 
		} 

	}

	/**
	 * Fixes strings so they use the %20 space command for URLs.
	 * 
	 * @param s
	 *            a string with standard spaces
	 * @return a fixed string with %20 replacing all of the spaces
	 */
	private String fixStringSpaces(String s) {
		String fixed = s;
		if (s.contains(" ")) {
			int breakPoint = fixed.indexOf(' ');
			String backCache = fixed.substring(breakPoint + 1);
			fixed = (fixed.substring(0, breakPoint)) + "%20" + backCache;
			;
			return fixStringSpaces(fixed);
		}
		return fixed;
	}


	/**
	 * For testing
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		String artist = "Drake";
		String track = "Make Me Proud (Feat. Nicki Minaj)";
		String album = "Take Care";

		getInstance().update(album, artist, track);
	}
}