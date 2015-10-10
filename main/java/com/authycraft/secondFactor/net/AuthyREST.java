package com.authycraft.secondFactor.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class AuthyREST {
	HttpClient client = new DefaultHttpClient();

	public String postNotify(String AuthyID, String ApiKey, String message, String playerName, String playerUUID, String serverIP, String strMinecraftVersion) throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost("https://api.authy.com/onetouch/xml/users/" + AuthyID + "/approval_requests?api_key=" + ApiKey);
		
		// Build the list of data we want to send to the Authy app notification.
		List nameValuePairs = new ArrayList(1);
		nameValuePairs.add(new BasicNameValuePair("message", message)); 
		nameValuePairs.add(new BasicNameValuePair("details[Player Name]", playerName)); 
		nameValuePairs.add(new BasicNameValuePair("details[Player UUID]", playerUUID)); 
		nameValuePairs.add(new BasicNameValuePair("details[Server Name]", serverIP)); 
		nameValuePairs.add(new BasicNameValuePair("details[Server Version]", strMinecraftVersion)); 
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
		// Make the HTTP request
		HttpResponse response = client.execute(post);
		return parseHTTPResponseForXMlTag(response, "uuid");
	}
	/**
	 * 
	 * Call the Authy REST API to get status on a users push notification.
	 * 
	 * @param strUUID - The UUID received from a successful push notifiation call.
	 * @param ApiKey
	 * @returns
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getNotify(String strUUID, String ApiKey) throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet("https://api.authy.com/onetouch/xml/approval_requests/" + strUUID + "?api_key=" + ApiKey);
 
		// Make the HTTP request
		HttpResponse response = client.execute(get);
		return parseHTTPResponseForXMlTag(response, "status");
 }

	private String parseHTTPResponseForXMlTag (HttpResponse response, String XMLTag) {
		// Get the response into XML and parse out the UUID.
		String XMLFoundTag = "";
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer();
			int ch;
			// Read the buffer
			while((ch = rd.read()) != -1) {
				sb.append((char)ch);
			}
			// Dump buffer into string, API returns xml.
			String xml = sb.toString();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			InputSource is;
			builder = factory.newDocumentBuilder();
			is = new InputSource(new StringReader(xml));
			Document doc = builder.parse(is);
			NodeList list = doc.getElementsByTagName(XMLTag);
			System.out.println(list.item(0).getTextContent());
			
			XMLFoundTag = list.item(0).getTextContent().toString();
		} catch (ParserConfigurationException e) {
		} catch (SAXException e) {
		} catch (IOException e) {

		}
	return XMLFoundTag;
	}
}