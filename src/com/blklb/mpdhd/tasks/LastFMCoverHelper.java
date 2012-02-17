package com.blklb.mpdhd.tasks;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

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

	// Cache for the previous album and artists
	private String _album;
	private String _artist;
	private String _song;
	
	private Image _artwork;
	
	//additional information
	private String _releaseDate;
	private String _bio;

	public static LastFMCoverHelper getInstance() {
		if (_instance == null)
			_instance = new LastFMCoverHelper();
		return _instance;
	}

	private LastFMCoverHelper() {
		_album = "";
		_artist = "";
		_song = "";
		
		_artwork = null;
		
		//additional information
		_releaseDate = "";
		_bio = "";
	}
	
	/**
	 * 
	 * @param album
	 * @param artist
	 * @param song
	 */
	public void update(String album, String artist, String song) {
		if((album.equals(_album))&&(artist.equals(_artist))) { // same artwork 
			_song = song;
			return;
		} else {
			_album = album;
			_artist = artist;
			_song = song;
			lastFMUpdate();
			//need to update
		}		
		
	}
	
	
	private void lastFMUpdate() {
		//this needs to update the artwork and additional info
	}
	
	
	

	/**
	 * 
	 * @param artist
	 * @param album
	 * @return the URL of the image null, if something goes wrong
	 */
	private URL getArtImageURL(String artist, String album) {
		// http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=b25b959554ed76058ac220b7b2e0a026&artist=Drake&album=Take%20Care
		URL url = null;

		String baseURL = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo";
		String keyHeader = "&api_key=";
		String key = "b25b959554ed76058ac220b7b2e0a026";
		String myKey = "d002a2da3e88bea9b89983f63e368555";
		String artistHeader = "&artist=";
		String albumHeader = "&album=";

		String urlString = baseURL + keyHeader + key + artistHeader
				+ fixStringSpaces(artist) + albumHeader
				+ fixStringSpaces(album);

		try {

			url = new URL(urlString);
			URLConnection conn = url.openConnection();

			if (conn == null)
				System.out.println("NUll conn");

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(conn.getInputStream());

			if (doc == null)
				System.out.println("NUll doc");

			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer xform = tfactory.newTransformer();

			DOMSource ds = new DOMSource(doc);

			if (ds == null)
				System.out.println("Ds null");
			
			Node albumNode = ds.getNode().getChildNodes().item(0);
			
			System.out.println("L: " + albumNode.getTextContent().length());
			
		} catch (MalformedURLException e) {

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {

		}

		return url;
	}

	
	
	/**
	 * this was version one of getArtImageURL. goes on artist and track not
	 * artist and album
	 * 
	 * @param artist
	 * @param track
	 */
	private void getInfo(String artist, String track) {
		String baseURL = "http://ws.audioscrobbler.com/2.0/?method=track.getinfo";
		String keyHeader = "&api_key=";
		String key = "b25b959554ed76058ac220b7b2e0a026";
		String myKey = "d002a2da3e88bea9b89983f63e368555";
		String artistHeader = "&artist=";

		String trackHeader = "&track=";
		String urlString = baseURL + keyHeader + key + artistHeader + artist
				+ trackHeader + track;

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

			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer xform = tfactory.newTransformer();

			DOMSource ds = new DOMSource(doc);

			if (ds == null)
				System.out.println("Null ds");

			System.out.println("Node Name:" + ds.getNode().getNodeName());
			System.out.println("Has Attrib:" + ds.getNode().hasAttributes());
			System.out.println("Has Children:" + ds.getNode().hasChildNodes());
			System.out.println("Num Children:"
					+ ds.getNode().getChildNodes().getLength());

			System.out.println("");

			// System.out.println("	Children 1:");
			System.out.println("	Node Name:"
					+ ds.getNode().getChildNodes().item(0).getNodeName());
			System.out.println("	Has Attrib:"
					+ ds.getNode().getChildNodes().item(0).hasAttributes());
			System.out.println("	Node Name:"
					+ ds.getNode().getChildNodes().item(0).getNodeName());
			System.out.println("	Num Attrib:"
					+ ds.getNode().getChildNodes().item(0).getAttributes()
							.getLength());
			System.out.println("	Has Children:"
					+ ds.getNode().getChildNodes().item(0).hasChildNodes());
			System.out.println("	Num Children:"
					+ ds.getNode().getChildNodes().item(0).getChildNodes()
							.getLength());

			System.out.println("");

			System.out.println("		Node Name:"
					+ ds.getNode().getChildNodes().item(0).getChildNodes()
							.item(0).getNodeName());
			System.out.println("		Has Attrib:"
					+ ds.getNode().getChildNodes().item(0).getChildNodes()
							.item(0).hasAttributes());
			System.out.println("		Has Children:"
					+ ds.getNode().getChildNodes().item(0).getChildNodes()
							.item(0).hasChildNodes());
			System.out.println("		Num Children:"
					+ ds.getNode().getChildNodes().item(0).getChildNodes()
							.item(0).getChildNodes().getLength());

			System.out.println("");

			System.out.println("		Node Name:"
					+ ds.getNode().getChildNodes().item(0).getChildNodes()
							.item(1).getNodeName());
			System.out.println("		Has Attrib:"
					+ ds.getNode().getChildNodes().item(0).getChildNodes()
							.item(1).hasAttributes());
			System.out.println("		Has Children:"
					+ ds.getNode().getChildNodes().item(0).getChildNodes()
							.item(1).hasChildNodes());
			System.out.println("		Num Children:"
					+ ds.getNode().getChildNodes().item(0).getChildNodes()
							.item(1).getChildNodes().getLength());

			System.out.println("");

			/*
			 * for(int i = 0; i <
			 * ds.getNode().getChildNodes().item(0).getChildNodes
			 * ().item(1).getChildNodes().getLength(); ++i) {
			 * System.out.println("			i:" + i);
			 * System.out.println("			Node Name:" +
			 * ds.getNode().getChildNodes().
			 * item(0).getChildNodes().item(1).getChildNodes
			 * ().item(i).getNodeName()); System.out.println("			Has Attrib:" +
			 * ds.getNode().getChildNodes().item(0).getChildNodes().item(1).
			 * getChildNodes().item(i).hasAttributes());
			 * System.out.println("			Has Children:" +
			 * ds.getNode().getChildNodes
			 * ().item(0).getChildNodes().item(1).getChildNodes
			 * ().item(i).hasChildNodes());
			 * System.out.println("			Num Children:" +
			 * ds.getNode().getChildNodes
			 * ().item(0).getChildNodes().item(1).getChildNodes
			 * ().item(i).getChildNodes().getLength()); System.out.println(); }
			 */

			Node albumNode = ds.getNode().getChildNodes().item(0)
					.getChildNodes().item(1).getChildNodes().item(19);
			Node hqImageNode = albumNode.getChildNodes().item(15);
			String hqImageURL = hqImageNode.getTextContent();

			System.out.println("Image URL:" + hqImageURL);

			/*
			 * for(int i = 0; i < albumNode.getChildNodes().getLength(); ++i) {
			 * String nodeName =
			 * albumNode.getChildNodes().item(i).getNodeName();
			 * 
			 * 
			 * System.out.println("			i:" + i);
			 * 
			 * 
			 * 
			 * System.out.println("		Node Name:" +
			 * albumNode.getChildNodes().item(i).getNodeName());
			 * System.out.println("		Text Content:" +
			 * albumNode.getChildNodes().item(i).getTextContent());
			 * System.out.println("			Has Attrib:" +
			 * albumNode.getChildNodes().item(i).hasAttributes());
			 * System.out.println("			Has Children:" +
			 * albumNode.getChildNodes().item(i).hasChildNodes());
			 * System.out.println("			Num Children:" +
			 * albumNode.getChildNodes().item(i).getChildNodes().getLength());
			 * System.out.println(); }
			 */

			// Node hqImageNode =
			// albumNode.getChildNodes().item(15).getTextContent());

			// System.out.println("			Node Name:" +
			// ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(9).getNodeName());
			// System.out.println("			Has Attrib:" +
			// ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(9).hasAttributes());
			// System.out.println("			Has Children:" +
			// ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(9).hasChildNodes());
			// System.out.println("			Num Children:" +
			// ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(9).getChildNodes().getLength());

			// ds.getNode().getChildNodes().item(0).hasAttributes()
			// System.out.println(":"+ds.getNode().getLocalName().toString());

			// System.out.println("Length:" + doc.getAttributes().getLength());

			// that’s the default xform; use a stylesheet to get a real one
			// xform.transform(new DOMSource(doc), new
			// StreamResult(System.out));

		} catch (MalformedURLException e) {

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {

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
		String track = "Practice";
		String album = "Take Care";
		getInstance().getInfo(artist, track);
		// getInfo(artist, track);
	}

}
