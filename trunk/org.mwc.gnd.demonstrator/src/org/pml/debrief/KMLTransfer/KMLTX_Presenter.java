package org.pml.debrief.KMLTransfer;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class KMLTX_Presenter
{

	private final static String filePath = "/Users/ianmayo/Downloads/ais";
	private static final String DATABASE_ROOT = "jdbc:postgresql://127.0.0.1/ais";
	private static Connection _conn;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			// check we have data
			File sourceP = new File(filePath);
			if (!sourceP.exists())
			{
				throw new RuntimeException("data directory not found");
			}

			// check we have a database
			connectToDatabase();

			// sort out a helper to read the XML
			DefaultHandler saxer = new MySaxParser();
			XMLReader parser = XMLReaderFactory
					.createXMLReader("org.apache.xerces.parsers.SAXParser");
			parser.setContentHandler(saxer);

			// start looping through files
			File[] fList = sourceP.listFiles();
			for (int i = 0; i < 3; i++)
			{
				File thisF = fList[i];

				// unzip it to get the KML
				ZipFile zip = new ZipFile(thisF);
				ZipEntry contents = zip.entries().nextElement();
				InputStream is = zip.getInputStream(contents);

				places = 0;

				// right, go for it
				processThisFile(is, parser);
			}

		}
		catch (RuntimeException re)
		{
			re.printStackTrace();
		}
		catch (ZipException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{

			// close the databse
			if (_conn != null)
			{
				try
				{
					System.out.println("closing database");
					_conn.close();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}

		}
	}

	private static void connectToDatabase()
	{
		// driver first
		try
		{
			Class.forName("org.postgresql.Driver");
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException("Failed to load database driver");
		}

		try
		{
			String url = DATABASE_ROOT;
			final String password = System.getenv("pg_pwd");
			if (password == null)
				throw new RuntimeException("database password missing");
			_conn = DriverManager.getConnection(url, "postgres", password);

			// also tell the connection about our new custom data types
			((org.postgresql.PGConnection) _conn).addDataType("geometry",
					org.postgis.PGgeometry.class);
			((org.postgresql.PGConnection) _conn).addDataType("box3d",
					org.postgis.PGbox3d.class);
		}
		catch (SQLException e)
		{
			throw new RuntimeException("failed to create connection");
		}

	}

	public static int places = 0;
	public static int points = 0;
	public static int description = 0;

	protected static class MySaxParser extends DefaultHandler
	{
		private String name;
		private Point2D coords;
		private Double course;
		private Double speed;
		private Integer index;

		@Override
		public void endElement(String arg0, String arg1, String arg2)
				throws SAXException
		{
			super.endElement(arg0, arg1, arg2);

			// ok, check our datai
			if (name != null)
			{
				if (coords != null)
				{
					if (course != null)
					{
						System.out.println("create fix at " + coords + " for " + name
								+ " (" + index + ") on:" + course + " at:" + speed);
						name = null;
						coords = null;
						course = null;
						speed = null;
						index = null;
					}
				}
			}
		}

		@Override
		public void characters(final char[] ch, final int start, final int length)
				throws SAXException
		{
			if (isName)
			{
				String name = new String(ch, start, length);
				if (name.length() > 0)
				{
					if (name.equals("MarineTraffic"))
					{
						// just ignore it
					}
					else
					{
						this.name = name;
					}
				}
			}
			else if (isCoords)
			{
				String data = new String(ch, start, length);
				String[] split = data.split(",");
				double longVal = Double.valueOf(split[0]);
				double latVal = Double.valueOf(split[1]);
				coords = new Point2D.Double(longVal, latVal);
			}
			else if (isDesc)
			{
				final String details = new String(ch, start, length);

				// start off with course & speed
				int startStr = details.indexOf("&nbsp;") + 6;
				int endStr = details.indexOf("&deg;");
				if (endStr == -1)
					return;

				String subStr = details.substring(startStr, endStr);
				String[] components = subStr.split(" ");
				course = Double.valueOf(components[0]);
				speed = Double.valueOf(components[3]);

				// now the mmsi
				startStr = details.indexOf("mmsi=");
				if(startStr == -1)
					return;
				endStr = details.indexOf("\"", startStr);
				subStr = details.substring(startStr + "mmsi=".length(), endStr);
				index = Integer.valueOf(subStr);
			}
		}

		boolean isName = false;
		boolean isCoords = false;
		boolean isDesc = false;

		@Override
		public void startElement(String nsURI, String strippedName, String tagName,
				Attributes attributes) throws SAXException
		{
			isName = isCoords = isDesc = false;

			if (tagName.equals("name"))
			{
				isName = true;
				// ok - go for new placemark
				places++;
			}
			else if (tagName.equals("coordinates"))
			{
				isCoords = true;
				points++;
			}
			else if (tagName.equals("description"))
			{
				isDesc = true;
				description++;
			}

		}

	}

	private static void processThisFile(InputStream is, XMLReader parser)
			throws ZipException, IOException, SAXException
	{
		parser.parse(new InputSource(is));

		// process the KML

		// loop through the observations

		// create position for this obs

		// extract the other fields

		// add this record
	}

}
