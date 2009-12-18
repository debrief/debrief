package Debrief.ReaderWriter.XML.KML;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class ImportKML
{

	private static TrackWrapper lastLayer = null;
	
	private static void addFix(Layers target, String trackName,
			HiResDate theDate, WorldLocation theLoc)
	{
		// is this our current layer?
		if(lastLayer != null)
		{
			if(lastLayer.getName() == trackName)
			{
				// sorted
			}
			else
				lastLayer = null;
		}
		
		if(lastLayer == null)
		{
			lastLayer = (TrackWrapper) target.findLayer(trackName);
			if(lastLayer == null)
			{
				lastLayer = new TrackWrapper();
				lastLayer.setName(trackName);
				target.addThisLayer(lastLayer);
			}
		}
		
		FixWrapper newFix= new FixWrapper(new Fix(theDate, theLoc, 0, 0));
		lastLayer.addFix(newFix);
	}

	public static void doImport(Layers theLayers, InputStream inputStream,
			String fileName)
	{

		String thePath = "/Users/ianmayo/Downloads/test_file.kml";

		File theFile = new File(thePath);
		if (!theFile.exists())
			System.err.println("can't find it!");
		else
			System.out.println("found it!");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// then we have to create document-loader:
		DocumentBuilder loader;
		try
		{

			// read the file into a string
			String theStr = readFileAsString(thePath);
			String tidyStr = stripNonValidXMLCharacters(theStr);

			InputSource s = new InputSource(new StringReader(tidyStr));
			// Kml kml = Kml.unmarshal(is);

			loader = factory.newDocumentBuilder();
			Document doc = loader.parse(s);

			doc.getDocumentElement().normalize();
			System.out.println("Root element "
					+ doc.getDocumentElement().getNodeName());
			NodeList nodeList = doc.getElementsByTagName("Placemark");

			// right, work through them
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node thisNode = nodeList.item(i);
				Element thisP = (Element) thisNode;
				NodeList theGeom = thisP.getElementsByTagName("MultiGeometry");
				if (theGeom.getLength() > 0)
				{
					// yup, it's one of ours.
					// sort it out.
					String theName = thisP.getElementsByTagName("name").item(0)
							.getTextContent();
					String[] nameTokens = theName.split("-");
					String trackIdTxt = nameTokens[0].trim();
					int trackID = Integer.parseInt(trackIdTxt);
					String timeTxt = thisP.getElementsByTagName("when").item(0)
							.getTextContent();
					SimpleDateFormat parser = new SimpleDateFormat(
							"yyyy-MM-d'T'HH:mm:ss'Z'");
					Date theD = parser.parse(timeTxt);
					System.out.println("  time is:" + timeTxt + " to " + theD);
					String coordsTxt = thisP.getElementsByTagName("coordinates").item(0)
							.getTextContent();
					// and now parse the string
					String[] coords = coordsTxt.split(",");
					double longVal = Double.parseDouble(coords[0]);
					double latVal = Double.parseDouble(coords[1]);
					double depthVal = Double.parseDouble(coords[2]);
					addFix(theLayers, theFile.getName() + "-" + trackID, new HiResDate(theD.getTime()),
							new WorldLocation(latVal, longVal, depthVal));
				}
			}

		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * This method ensures that the output String has only valid XML unicode
	 * characters as specified by the XML 1.0 standard. For reference, please see
	 * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
	 * standard</a>. This method will return an empty String if the input is null
	 * or empty.
	 * 
	 * @param in
	 *          The String whose non-valid characters we want to remove.
	 * @return The in String, stripped of non-valid characters.
	 */
	public static String stripNonValidXMLCharacters(String in)
	{
		StringBuffer out = new StringBuffer(); // Used to hold the output.
		char current; // Used to reference the current character.

		if (in == null || ("".equals(in)))
			return ""; // vacancy test.
		for (int i = 0; i < in.length(); i++)
		{
			current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here;
															// it should not happen.
			if ((current == 0x9) || (current == 0xA) || (current == 0xD)
					|| ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD))
					|| ((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		return out.toString();
	}

	private static String readFileAsString(String filePath)
			throws java.io.IOException
	{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1)
		{
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	public static void main(String[] args)
	{
		doImport(null, null, null);
	}

}
