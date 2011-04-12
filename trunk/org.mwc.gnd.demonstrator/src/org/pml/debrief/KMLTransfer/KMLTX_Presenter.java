package org.pml.debrief.KMLTransfer;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class KMLTX_Presenter
{

	private final static String filePath = "/Users/ianmayo/Downloads/ais";
	private static final String DATABASE_ROOT = "jdbc:postgresql://127.0.0.1/ais";
	private static Connection _conn;
	private static PreparedStatement sql;
	private static WorldArea limits;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{

			WorldLocation tl = new WorldLocation(59, -9.75, 0d);
			WorldLocation br = new WorldLocation(58, -5.5, 0d);
			limits = new WorldArea(tl, br);

			// check we have data
			File sourceP = new File(filePath);
			if (!sourceP.exists())
			{
				throw new RuntimeException("data directory not found");
			}

			// check we have a database
			connectToDatabase();

			// sort out a helper to read the XML
			MyHandler handler = new MyHandler()
			{
				public void writeThis(String name2, Date date2, Point2D coords2,
						Integer index2, Double course2, Double speed2)
				{
					try
					{
						writeThisToDb(name2, date2, coords2, index2, course2, speed2);
					}
					catch (SQLException e)
					{
						e.printStackTrace();
						System.exit(1);
					}
				}
			};

			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

			//
			// XMLReader parser = XMLReaderFactory
			// .createXMLReader("org.apache.xerces.parsers.SAXParser");
			// parser.setContentHandler(saxer);

			// see about format
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// String str =
			// "insert into tracks (latval, longval) values (32.3, 22.4);";
			// Statement st = _conn.createStatement();
			// st.execute(str);
			// System.exit(0);

			String query = "insert into tracks2 (dateval, nameval, latval, longval,"
					+ " courseval, speedval, mmsi) VALUES (?, ?, ?, ?, ?, ?, ?);";
			System.out.println("query will be:" + query);
			sql = _conn.prepareStatement(query);

			// start looping through files
			File[] fList = sourceP.listFiles();
			for (int i = 0; i < fList.length; i++)
			{
				File thisF = fList[i];

				// unzip it to get the KML
				ZipFile zip = new ZipFile(thisF);
				ZipEntry contents = zip.entries().nextElement();
				InputStream is = zip.getInputStream(contents);

				// sort out the filename snap_2011-04-11_08/32/00
				String[] legs = thisF.getName().split("_");
				String timeStr = legs[2].substring(0, 8);
				Date theDate = df.parse(legs[1] + " " + timeStr);
				handler.setDate(theDate);

				files++;

				System.err.println("==" + i + " of " + fList.length + " at:"
						+ new Date());

				// right, go for it
				processThisFile(is, parser, handler);
			}

			System.out.println("output " + places + " for " + files + " files");

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
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
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

	protected static void writeThisToDb(String name2, Date date2,
			Point2D coords2, Integer index2, Double course2, Double speed2)
			throws SQLException
	{
		// are we in zone?
//		WorldLocation loc = new WorldLocation(coords2.getY(), coords2.getX(), 0d);
//		if (!limits.contains(loc))
//			return;
		
		// String query =
		// "insert into AIS_tracks (daveVal, name, latVal, longVal, courseVal, speedVal) VALUES (";
		sql.setTimestamp(1, new java.sql.Timestamp(date2.getTime()));
		sql.setString(2, name2);
		sql.setDouble(3, coords2.getY());
		sql.setDouble(4, coords2.getX());
		sql.setDouble(5, course2);
		sql.setDouble(6, speed2);
		sql.setInt(7, index2);
		sql.executeUpdate();

		places++;
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
	public static int files = 0;

	protected static abstract class MyHandler extends DefaultHandler
	{
		private String name;
		private Point2D coords;
		private Double course;
		private Double speed;
		private Integer mmsi;
		private Date date;

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
						if (mmsi != null)
						{
							writeThis(name, date, coords, mmsi, course, speed);

							name = null;
							coords = null;
							course = null;
							speed = null;
							mmsi = null;
						}
					}
				}
			}
		}

		abstract public void writeThis(String name2, Date date2, Point2D coords2,
				Integer index2, Double course2, Double speed2);

		public void setDate(Date finalDate)
		{
			date = finalDate;
		}

		@Override
		public void characters(final char[] ch, final int start, final int length)
				throws SAXException
		{
			String data = "";
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
				try
				{
					if (name.equals("JACKIE"))
						System.out.println("here2");
					data = new String(ch, start, length);
					String[] split = data.split(",");
					double longVal = Double.valueOf(split[0]);
					double latVal = Double.valueOf(split[1]);
					coords = new Point2D.Double(longVal, latVal);
				}
				catch (NumberFormatException e)
				{
					System.out.println("number format prob reading pos for " + name);
				}
				catch (java.lang.ArrayIndexOutOfBoundsException aw)
				{
					System.out.println("array index prob reading pos for " + name
							+ " from " + data);
				}
			}
			else if (isDesc)
			{
				String details = "";
				try
				{
					if (name.equals("PEARL RIVER"))
						System.out.println("here");
					details = new String(ch, start, length);

					// start off with course & speed
					int startStr = details.indexOf("&nbsp;") + 6;
					int endStr = details.indexOf("&deg;");
					if (endStr == -1)
						return;

					String subStr = details.substring(startStr, endStr);
					String[] components = subStr.split(" ");
					course = Double.valueOf(components[3]);
					speed = Double.valueOf(components[0]);

					// now the mmsi
					startStr = details.indexOf("mmsi=");
					if (startStr == -1)
						return;
					endStr = details.indexOf("\"", startStr);
					subStr = details.substring(startStr + "mmsi=".length(), endStr);
					mmsi = Integer.valueOf(subStr);
				}
				catch (java.lang.StringIndexOutOfBoundsException aw)
				{
					System.out.println("prob reading desc for " + name);
				}

			}
		}

		boolean isName = false;
		boolean isCoords = false;
		boolean isDesc = false;
		boolean foundPlacemark = false;

		@Override
		public void startElement(String nsURI, String strippedName, String tagName,
				Attributes attributes) throws SAXException
		{
			isName = isCoords = isDesc = false;

			if (tagName.equals("Placemark"))
			{
				foundPlacemark = true;
				return;
			}
			if (foundPlacemark)
			{
				if (tagName.equals("name"))
				{
					isName = true;
					// ok - go for new placemark
					places++;
				}
				else if (tagName.equals("coordinates"))
				{
					isCoords = true;
				}
				else if (tagName.equals("description"))
				{
					isDesc = true;
				}
			}

		}

	}

	private static void processThisFile(InputStream is, SAXParser parser,
			DefaultHandler handler) throws ZipException, IOException, SAXException
	{
		parser.parse(is, handler);

		// process the KML

		// loop through the observations

		// create position for this obs

		// extract the other fields

		// add this record
	}

}
