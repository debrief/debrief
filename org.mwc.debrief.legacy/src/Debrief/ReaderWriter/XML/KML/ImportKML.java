/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
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
import java.util.StringTokenizer;
import java.util.TimeZone;
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
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class ImportKML
{

	// cache the last layer - for speed
	private static TrackWrapper lastLayer = null;

	/**
	 * keep track of how many tracks we've created, so we can generate unique
	 * colors
	 */
	private static int colorCounter = 0;
	
	/** the time-stamp presumed for LineString data that may not contain time data
	 * 
	 */
	private static int DEFAULT_TIME_STEP = 1000;

	/**
	 * create a fix using the supplied data
	 * 
	 * @param target
	 * @param trackName
	 * @param theDate
	 * @param theLoc
	 */
	private static void addFix(final Layers target, final String trackName,
			final HiResDate theDate, final WorldLocation theLoc, final double courseDegs,
			final double speedKts)
	{
		// is this our current layer?
		if (lastLayer != null)
		{
			if (lastLayer.getName().equals(trackName))
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

		final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(courseDegs);
		final double speedYPS = new WorldSpeed(speedKts, WorldSpeed.Kts)
				.getValueIn(WorldSpeed.ft_sec) / 3d;

		final FixWrapper newFix = new FixWrapper(new Fix(theDate, theLoc, courseRads,
				speedYPS));
		
		// and reset the time value
		newFix.resetName();
		
		lastLayer.addFix(newFix);
	}

	/**
	 * create a track using the supplied name
	 * 
	 * @param target
	 * @param trackName
	 */
	private static void createTrack(final Layers target, final String trackName)
	{
		lastLayer = new TrackWrapper();
		lastLayer.setName(trackName);

		// sort out a color
		final Color theCol = ImportReplay.replayColorFor(colorCounter++);
		lastLayer.setColor(theCol);

		target.addThisLayer(lastLayer);
	}

	public static void doZipImport(final Layers theLayers, final InputStream inputStream,
			final String fileName)
	{
		final ZipInputStream zis = new ZipInputStream(inputStream);

		ZipEntry entry;
		try
		{
			while ((entry = zis.getNextEntry()) != null)
			{
				// is this one of ours?
				final String theName = entry.getName();

				if (theName.endsWith(".kml"))
				{
					// cool, here it is - process it

					// extract the data into a stream
					final ByteArrayOutputStream bos = new ByteArrayOutputStream();
					final BufferedOutputStream fout = new BufferedOutputStream(bos);
					for (int c = zis.read(); c != -1; c = zis.read())
					{
						fout.write(c);
					}
					zis.closeEntry();
					fout.close();
					bos.close();

					// now create a byte input stream from the byte output stream
					final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

					// and create it
					doImport(theLayers, bis, theName);
				}
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}

	}

	public static void doImport(final Layers theLayers, final InputStream inputStream,
			final String fileName)
	{
		try
		{

			// get the main part of the file - we use it for the track name
			final String prefix = tidyFileName(fileName);

			// get the bits ready to do the document parsing
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// then we have to create document-loader:
			DocumentBuilder loader;

			// read the file into a string
			final String theStr = inputStreamAsString(inputStream);// readFileAsString(thePath);

			// now ditch any non-compatible chars (such as the degree symbol)
			final String tidyStr = stripNonValidXMLCharacters(theStr);

			// wrap the string in a source
			final InputSource s = new InputSource(new StringReader(tidyStr));

			loader = factory.newDocumentBuilder();

			// get parsing
			final Document doc = loader.parse(s);

			// normalise the DOM - to make it a little more stable/predictable
			doc.getDocumentElement().normalize();

			// get our XML date parser ready
			final SimpleDateFormat parser = new SimpleDateFormat(
					"yyyy-MM-d'T'HH:mm:ss'Z'");
			parser.setTimeZone(TimeZone.getTimeZone("GMT"));

			// find the placemarks
			final NodeList nodeList = doc.getElementsByTagName("Placemark");

			// right, work through them
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				// ok - we have a placemark, see if it's got useful data
				final Node thisNode = nodeList.item(i);
				final Element thisP = (Element) thisNode;

				// look for the indicator for our child nodes
				final NodeList theGeom = thisP.getElementsByTagName("MultiGeometry");
				if (theGeom.getLength() > 0)
				{
					// yup, it's one of ours.
					// sort it out.
					final String theName = thisP.getElementsByTagName("name").item(0)
							.getTextContent();
					final String[] nameTokens = theName.split("-");

					// get the first part of the track id
					final String trackIdTxt = nameTokens[0].trim();
					final int trackID = Integer.parseInt(trackIdTxt);

					// now for the time
					final String timeTxt = thisP.getElementsByTagName("when").item(0)
							.getTextContent();
					final Date theD = parser.parse(timeTxt);

					// and the location
					final String coordsTxt = thisP.getElementsByTagName("coordinates").item(0)
							.getTextContent();
					// and now parse the string
					final String[] coords = coordsTxt.split(",");
					final double longVal = MWCXMLReader.readThisDouble(coords[0]);
					final double latVal = MWCXMLReader.readThisDouble(coords[1]);
					final double altitudeVal = MWCXMLReader.readThisDouble(coords[2]);

					// lastly, the course/speed
					double courseDegs;
					double speedKts;
					final String descriptionTxt = thisP.getElementsByTagName("description")
							.item(0).getTextContent();

					courseDegs = courseFrom(descriptionTxt);
					speedKts = speedFrom(descriptionTxt);

					addFix(theLayers, prefix + "-" + trackID, new HiResDate(theD
							.getTime()), new WorldLocation(latVal, longVal, -altitudeVal),
							courseDegs, speedKts);
				}
				else
				{
					// see if it's from a GPS tracker
					final NodeList lineString = thisP.getElementsByTagName("LineString");
					if (lineString != null)
					{
						// yup, suspect it's from a NokiaSportsTracker file
						String trimmedFile = fileName.substring(1, fileName.length() - 1);
						trimmedFile = trimmedFile.substring(0, trimmedFile.length() - 4);

						// get our XML date parser ready
						final SimpleDateFormat nokiaDateFormat = new SimpleDateFormat(
								"yyyyMMddHHmms");
						nokiaDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
						Date theDate = new Date();

						try
						{
							theDate = nokiaDateFormat.parse(trimmedFile);
						}
						catch (final Exception e)
						{
							e.printStackTrace();
						}

						final Element theString = (Element) lineString.item(0);
						if (theString != null)
						{
							final NodeList theCoords = theString
									.getElementsByTagName("coordinates");
							if (theCoords != null)
							{
								final Node theCoordStr = theCoords.item(0);
								final String contents = theCoordStr.getTextContent();
								parseTheseCoords(contents, theLayers, theDate);
							}
						}
					}

				}
			}

		}
		catch (final ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (final SAXException e)
		{
			e.printStackTrace();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		catch (final ParseException e)
		{
			e.printStackTrace();
		}

		// lastly, clear the 'last layer' object
		lastLayer = null;
		
	}

	/** utility to run through the contents of a LineString item - presuming the presence of altitude data
	 * 
	 * @param contents the inside of the linestring construct
	 * @param theLayers where we're going to stick the data
	 * @param startDate the start date for the track
	 * @throws ParseException 
	 */
	private static void parseTheseCoords(final String contents, final Layers theLayers,
			final Date startDate) throws ParseException
	{
		final StringTokenizer token = new StringTokenizer(contents, ",\n", false);

		Date newDate = new Date(startDate.getTime());

		while (token.hasMoreElements())
		{
			final String longV = token.nextToken();
			final String latV = token.nextToken();
			final String altitude = token.nextToken();
			
			// just check that we have altitude data
			double theAlt = 0;
			if(altitude.length() > 0)
				theAlt = MWCXMLReader.readThisDouble(altitude);

			addFix(theLayers, startDate.toString(), new HiResDate(newDate.getTime()),
					new WorldLocation(MWCXMLReader.readThisDouble(latV), 
							MWCXMLReader.readThisDouble(longV),
							-theAlt), 0, 0);

			// add a second incremenet to the date, to create the new date
			newDate = new Date(newDate.getTime() + DEFAULT_TIME_STEP);
		}

	}

	/**
	 * extract the course element from the supplied string
	 * 
	 * @param descriptionTxt
	 * @return
	 * @throws ParseException 
	 */
	private static double courseFrom(final String descriptionTxt) throws ParseException
	{
		double res = 0;
		// STRING LOOKS LIKE
		// <![CDATA[<b>RADAR PLOT 20:01:12 (GMT)</b><br><hr>Lat:
		// 05.9696<br>Lon: 07.9633<br>Course: 253.0<br>Speed: 7.1
		// knots<br>Date: September 12, 2009]]>
		final int startI = descriptionTxt.indexOf("Course");
		final int endI = descriptionTxt.indexOf("<br>Speed");
		if ((startI > 0) && (endI > 0))
		{
			final String subStr = descriptionTxt.substring(startI + 7, endI - 1);
			res = MWCXMLReader.readThisDouble(subStr.trim());
		}
		return res;
	}

	/**
	 * extract the course element from the supplied string
	 * 
	 * @param descriptionTxt
	 * @return
	 * @throws ParseException 
	 */
	private static double speedFrom(final String descriptionTxt) throws ParseException
	{
		double res = 0;
		// STRING LOOKS LIKE
		// <![CDATA[<b>RADAR PLOT 20:01:12 (GMT)</b><br><hr>Lat:
		// 05.9696<br>Lon: 07.9633<br>Course: 253.0<br>Speed: 7.1
		// knots<br>Date: September 12, 2009]]>
		final int startI = descriptionTxt.indexOf("Speed");
		final int endI = descriptionTxt.indexOf("knots");
		if ((startI > 0) && (endI > 0))
		{
			final String subStr = descriptionTxt.substring(startI + 6, endI - 1);
			res = MWCXMLReader.readThisDouble(subStr.trim());
		}
		return res;
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
	private static String stripNonValidXMLCharacters(final String in)
	{
		final StringBuffer out = new StringBuffer(); // Used to hold the output.
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

	private static String inputStreamAsString(final InputStream stream)
			throws IOException
	{
		final InputStreamReader isr = new InputStreamReader(stream);
		final BufferedReader br = new BufferedReader(isr);
		final StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = br.readLine()) != null)
		{
			sb.append(line + "\n");
		}

		br.close();
		return sb.toString();
	}

	/**
	 * Remove the suffix from the passed file name, together with any leading
	 * path.
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
		final File holder = new File(fileName);
		String res = holder.getName();

		// now ditch the file suffix
		final int pos = res.lastIndexOf('.');
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
