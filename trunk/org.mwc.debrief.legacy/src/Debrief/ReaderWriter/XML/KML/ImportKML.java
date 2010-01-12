package Debrief.ReaderWriter.XML.KML;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class ImportKML
{

	// cache the last layer - for speed
	private static TrackWrapper lastLayer = null;

	/**
	 * keep track of how many tracks we've created, so we can generate unique
	 * colors
	 */
	private static int colorCounter = 0;

	/**
	 * create a fix using the supplied data
	 * 
	 * @param target
	 * @param trackName
	 * @param theDate
	 * @param theLoc
	 */
	private static void addFix(Layers target, String trackName,
			HiResDate theDate, WorldLocation theLoc)
	{
		// is this our current layer?
		if (lastLayer != null)
		{
			if (lastLayer.getName() == trackName)
			{
				// sorted
			}
			else
				lastLayer = null;
		}

		if (lastLayer == null)
		{
			lastLayer = (TrackWrapper) target.findLayer(trackName);
			if (lastLayer == null)
			{
				createTrack(target, trackName);
			}
		}

		FixWrapper newFix = new FixWrapper(new Fix(theDate, theLoc, 0, 0));
		lastLayer.addFix(newFix);
	}

	/**
	 * create a track using the supplied name
	 * 
	 * @param target
	 * @param trackName
	 */
	private static void createTrack(Layers target, String trackName)
	{
		lastLayer = new TrackWrapper();
		lastLayer.setName(trackName);

		// sort out a color
		Color theCol = ImportReplay.replayColorFor(colorCounter++);
		lastLayer.setColor(theCol);

		target.addThisLayer(lastLayer);
	}

	public static void doZipImport(Layers theLayers, InputStream inputStream,
			String fileName)
	{
		ZipInputStream zis = new ZipInputStream(inputStream);

		ZipEntry entry;
		try
		{
			while ((entry = zis.getNextEntry()) != null)
			{
				// is this one of ours?
				String theName = entry.getName();

				if (theName.endsWith(".kml"))
				{
					// cool, here it is - process it

					// extract the data into a stream
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					BufferedOutputStream fout = new BufferedOutputStream(bos);
	        for (int c = zis.read(); c != -1; c = zis.read()) {
	          fout.write(c);
	        }
	        zis.closeEntry();
	        fout.close();
	        bos.close();
	        
	        // now create a byte input stream from the byte output stream
	        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
					
					// and create it					
					doImport(theLayers, bis, theName);
				}
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void doImport(Layers theLayers, InputStream inputStream,
			String fileName)
	{
		try
		{

			// get the main part of the file - we use it for the track name
			String prefix = tidyFileName(fileName);

			// get the bits ready to do the document parsing
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// then we have to create document-loader:
			DocumentBuilder loader;

			// read the file into a string
			String theStr = inputStreamAsString(inputStream);// readFileAsString(thePath);

			// now ditch any non-compatible chars (such as the degree symbol)
			String tidyStr = stripNonValidXMLCharacters(theStr);

			// wrap the string in a source
			InputSource s = new InputSource(new StringReader(tidyStr));

			loader = factory.newDocumentBuilder();

			// get parsing
			Document doc = loader.parse(s);

			// normalise the DOM - to make it a little more stable/predictable
			doc.getDocumentElement().normalize();

			// get our XML date parser ready
			final SimpleDateFormat parser = new SimpleDateFormat(
					"yyyy-MM-d'T'HH:mm:ss'Z'");

			// find the placemarks
			NodeList nodeList = doc.getElementsByTagName("Placemark");

			// right, work through them
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				// ok - we have a placemark, see if it's got useful data
				Node thisNode = nodeList.item(i);
				Element thisP = (Element) thisNode;

				// look for the indicator for our child nodes
				NodeList theGeom = thisP.getElementsByTagName("MultiGeometry");
				if (theGeom.getLength() > 0)
				{
					// yup, it's one of ours.
					// sort it out.
					String theName = thisP.getElementsByTagName("name").item(0)
							.getTextContent();
					String[] nameTokens = theName.split("-");

					// get the first part of the track id
					String trackIdTxt = nameTokens[0].trim();
					int trackID = Integer.parseInt(trackIdTxt);

					// now for the time
					String timeTxt = thisP.getElementsByTagName("when").item(0)
							.getTextContent();
					Date theD = parser.parse(timeTxt);

					// and the location
					String coordsTxt = thisP.getElementsByTagName("coordinates").item(0)
							.getTextContent();
					// and now parse the string
					String[] coords = coordsTxt.split(",");
					double longVal = Double.parseDouble(coords[0]);
					double latVal = Double.parseDouble(coords[1]);
					double depthVal = Double.parseDouble(coords[2]);
					addFix(theLayers, prefix + "-" + trackID, new HiResDate(theD
							.getTime()), new WorldLocation(latVal, longVal, depthVal));
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
	private static String stripNonValidXMLCharacters(String in)
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

	private static String inputStreamAsString(InputStream stream)
			throws IOException
	{
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = br.readLine()) != null)
		{
			sb.append(line + "\n");
		}

		br.close();
		return sb.toString();
	}

	/**
	 * Remove the suffix from the passed file name, together with any leading path.
	 * 
	 * @param fileName
	 *          File name to remove suffix from.
	 * 
	 * @return <TT>fileName</TT> without a suffix.
	 * 
	 * @throws IllegalArgumentException
	 *           if <TT>null</TT> file name passed.
	 */
	private static String tidyFileName(final String fileName)
	{
		if (fileName == null)
		{
			throw new IllegalArgumentException("file name == null");
		}
		
		// start off by ditching the path
		File holder = new File(fileName);
		String res = holder.getName();
		
		// now ditch the file suffix
		int pos = res.lastIndexOf('.');
		if (pos > 0 && pos < res.length() - 1)
		{
			res = res.substring(0, pos);
		}
		return res;
	}

	// private static String readFileAsString(String filePath)
	// throws java.io.IOException
	// {
	// StringBuffer fileData = new StringBuffer(1000);
	// BufferedReader reader = new BufferedReader(new FileReader(filePath));
	// char[] buf = new char[1024];
	// int numRead = 0;
	// while ((numRead = reader.read(buf)) != -1)
	// {
	// String readData = String.valueOf(buf, 0, numRead);
	// fileData.append(readData);
	// buf = new char[1024];
	// }
	// reader.close();
	// return fileData.toString();
	// }
	//
	// public static void main(String[] args)
	// {
	// doImport(null, null, null);
	// }

}
