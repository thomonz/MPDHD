package com.blklb.mpdhd.tasks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 
 * @author ablackbu
 *
 */
public class LastFMCoverHelper {
	
	//Android debug tag
	final static String tag = "LastFMCoverHelper";
	
	//Singleton
	private LastFMCoverHelper _instance;
	
	//Cache for the previous album and artists
	private String previousAlbum;
	private String previousArtist;
	
	public LastFMCoverHelper getInstance() {
		if(_instance == null)
			_instance = new LastFMCoverHelper();
		return _instance;
	}
	
	private LastFMCoverHelper() {
		previousAlbum = "";
		previousArtist = "";
	}
	
	
	/**
	 * 
	 * @param artist
	 * @param album
	 * @return
	 */
	private static URL getArtImageURL(String artist, String album) {
		URL url = null;
		//http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=b25b959554ed76058ac220b7b2e0a026&artist=Drake&album=Take%20Care
				
				
		return url;
	}
	
	
	/**
	 * this was version one of getArtImageURL. goes on artist and track not artist and album
	 * @param artist
	 * @param track
	 */
	private static void getInfo(String artist, String track) {
		String baseURL = "http://ws.audioscrobbler.com/2.0/?method=track.getinfo";
		String keyHeader = "&api_key=";
		String key = "b25b959554ed76058ac220b7b2e0a026";
		String myKey = "d002a2da3e88bea9b89983f63e368555";
		String artistHeader = "&artist=";
		
		String trackHeader = "&track=";
		String urlString = baseURL + keyHeader + key + artistHeader + artist + trackHeader + track;
		
		try {
		
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		
		if(conn == null) 
			System.out.println("NUll conn");
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(conn.getInputStream());
		
		
		if(doc == null) 
			System.out.println("NUll doc");
		
		

		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer xform = tfactory.newTransformer();
		
		
		
		
		DOMSource ds = new DOMSource(doc);
		
		if(ds == null)
			System.out.println("Ds null");
		
		System.out.println("Node Name:" + ds.getNode().getNodeName());
		System.out.println("Has Attrib:" + ds.getNode().hasAttributes());
		System.out.println("Has Children:" + ds.getNode().hasChildNodes());
		System.out.println("Num Children:" + ds.getNode().getChildNodes().getLength());
		
		System.out.println("");
		
		//System.out.println("	Children 1:");
		System.out.println("	Node Name:" + ds.getNode().getChildNodes().item(0).getNodeName());
		System.out.println("	Has Attrib:" + ds.getNode().getChildNodes().item(0).hasAttributes());
		System.out.println("	Node Name:" + ds.getNode().getChildNodes().item(0).getNodeName());
		System.out.println("	Num Attrib:" + ds.getNode().getChildNodes().item(0).getAttributes().getLength());
		System.out.println("	Has Children:" + ds.getNode().getChildNodes().item(0).hasChildNodes());
		System.out.println("	Num Children:" + ds.getNode().getChildNodes().item(0).getChildNodes().getLength());
		
		System.out.println("");

		System.out.println("		Node Name:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(0).getNodeName());
		System.out.println("		Has Attrib:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(0).hasAttributes());
		System.out.println("		Has Children:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(0).hasChildNodes());
		System.out.println("		Num Children:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(0).getChildNodes().getLength());
		
		System.out.println("");

		System.out.println("		Node Name:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getNodeName());
		System.out.println("		Has Attrib:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(1).hasAttributes());
		System.out.println("		Has Children:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(1).hasChildNodes());
		System.out.println("		Num Children:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().getLength());
		
		System.out.println("");
		
		
		
		/*for(int i = 0; i < ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().getLength(); ++i) {
			System.out.println("			i:" + i);
			System.out.println("			Node Name:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(i).getNodeName());
			System.out.println("			Has Attrib:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(i).hasAttributes());
			System.out.println("			Has Children:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(i).hasChildNodes());
			System.out.println("			Num Children:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(i).getChildNodes().getLength());
			System.out.println();
		}*/
		
		Node albumNode = ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(19);
		Node hqImageNode = albumNode.getChildNodes().item(15);
		String hqImageURL = hqImageNode.getTextContent();
		
		System.out.println("Image URL:" + hqImageURL);
		
		/*for(int i = 0; i < albumNode.getChildNodes().getLength(); ++i) {
			String nodeName = albumNode.getChildNodes().item(i).getNodeName();
			
			
			System.out.println("			i:" + i);
			
			
			
			System.out.println("		Node Name:" + albumNode.getChildNodes().item(i).getNodeName());
			System.out.println("		Text Content:" + albumNode.getChildNodes().item(i).getTextContent());
			System.out.println("			Has Attrib:" + albumNode.getChildNodes().item(i).hasAttributes());
			System.out.println("			Has Children:" + albumNode.getChildNodes().item(i).hasChildNodes());
			System.out.println("			Num Children:" + albumNode.getChildNodes().item(i).getChildNodes().getLength());
			System.out.println();
		}*/
		
		
		//Node hqImageNode = albumNode.getChildNodes().item(15).getTextContent());
		
		//System.out.println("			Node Name:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(9).getNodeName());
		//System.out.println("			Has Attrib:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(9).hasAttributes());
		//System.out.println("			Has Children:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(9).hasChildNodes());
		//System.out.println("			Num Children:" + ds.getNode().getChildNodes().item(0).getChildNodes().item(1).getChildNodes().item(9).getChildNodes().getLength());
		
		
		//ds.getNode().getChildNodes().item(0).hasAttributes()
		//System.out.println(":"+ds.getNode().getLocalName().toString());
		
		
		
		//System.out.println("Length:" + doc.getAttributes().getLength());

		// that’s the default xform; use a stylesheet to get a real one
		//xform.transform(new DOMSource(doc), new StreamResult(System.out));
		
		
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
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			
		}
	}
	
	
	
	/**
	 * For testing
	 * @param args
	 */
	public static void main(String args[]) {
		String artist = "Drake";
		String track = "Practice";
		getInfo(artist, track); 
	}
	
}
