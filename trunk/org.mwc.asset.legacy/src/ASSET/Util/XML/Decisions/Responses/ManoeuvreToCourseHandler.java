package ASSET.Util.XML.Decisions.Responses;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import ASSET.Models.Decision.Responses.ManoeuvreToCourse;
import ASSET.Models.Decision.Responses.Response;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

import org.apache.xml.serialize.XMLSerializer;

/**
 * class which reads in a man to course response
 * 
 * @see ASSET.Models.Decision.Responses.ManoeuvreToCourse
 */

public class ManoeuvreToCourseHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

	private final static String type = "ManoeuvreToCourse";

	private final static String SPEED = "Speed";
	private final static String RELATIVE_SPEED = "RelativeSpeed";
	private final static String COURSE = "Course";
	private final static String RELATIVE_COURSE = "RelativeCourse";
	private final static String Height = "Height";

	/**
	 * the speed to travel at (kts)
	 */
	WorldSpeed _mySpeed = null;

	/**
	 * whether the speed change is relative
	 */
	boolean _relativeSpeed;

	/**
	 * the course to steer to (degs)
	 */
	Float _myCourse = null;

	/**
	 * whether the course change is relative
	 */
	boolean _relativeCourse;

	/**
	 * the Height to change to (m), always absolute
	 */
	WorldDistance _myHeight = null;

	String _name;

	public ManoeuvreToCourseHandler()
	{
		super("ManoeuvreToCourse");

		addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(String name, final String val)
			{
				_name = val;
			}
		});

		addHandler(new WorldSpeedHandler(SPEED)
		{
			public void setSpeed(WorldSpeed res)
			{
				_mySpeed = res;
			}
		});
		addHandler(new WorldDistanceHandler(Height)
		{
			public void setWorldDistance(WorldDistance res)
			{
				_myHeight = res;
			}
		});

		addAttributeHandler(new HandleDoubleAttribute(COURSE)
		{
			public void setValue(String name, final double val)
			{
				_myCourse = new Float(val);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(RELATIVE_SPEED)
		{
			public void setValue(String name, final boolean val)
			{
				_relativeSpeed = val;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(RELATIVE_COURSE)
		{
			public void setValue(String name, final boolean val)
			{
				_relativeCourse = val;
			}
		});
	}

	public void elementClosed()
	{
		final Response ml = new ManoeuvreToCourse(_mySpeed, _relativeSpeed,
				_myCourse, _relativeCourse, _myHeight);
		ml.setName(_name);

		// finally output it
		setResponse(ml);

		// and reset
		_mySpeed = null;
		_myCourse = null;
		_myHeight = null;
		_name = null;
	}

	public void setResponse(Response dec)
	{
	}

	static public void exportThis(final Object toExport,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ManoeuvreToCourse bb = (ManoeuvreToCourse) toExport;

		// output it's attributes
		thisPart.setAttribute("Name", bb.getName());

		// and the speed, if we have it
		if (bb.getSpeed() != null)
		{
			WorldSpeedHandler.exportSpeed(SPEED, bb.getSpeed(), thisPart, doc);
			thisPart.setAttribute(RELATIVE_SPEED, writeThis(bb.isRelativeSpeed()));
		}

		// and the course, if we have it
		if (bb.getCourse() != null)
		{
			thisPart.setAttribute(COURSE, writeThis(bb.getCourse().floatValue()));
			thisPart.setAttribute(RELATIVE_COURSE, writeThis(bb.isRelativeCourse()));
		}

		// and the Height
		if (bb.getHeight() != null)
		{
			WorldDistanceHandler
					.exportDistance(Height, bb.getHeight(), thisPart, doc);
		}

		parent.appendChild(thisPart);

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class ManToCourseHandlerTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public ManToCourseHandlerTest(final String val)
		{
			super(val);
		}

		ManoeuvreToCourse resp = null;

		public void testTheTrail()
		{
			// ok, let's go for it
			final MyReaderWriter mr = new MyReaderWriter()
			{
				public void responseIs(final Response rec)
				{
					resp = (ManoeuvreToCourse) rec;
				}
			};

			ManoeuvreToCourse ml = new ManoeuvreToCourse(new WorldSpeed(12,
					WorldSpeed.M_sec), false, new Float(12), true, new WorldDistance(14,
					WorldDistance.METRES));
			ml.setName("bingop");
			java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
			mr.exportThis(ml, os);

			// great, now try to read it in!
			java.io.InputStream is = new java.io.ByteArrayInputStream(os
					.toByteArray());
			mr.importThis("", is);

			// check we got it
			assertEquals("it has the correct name", resp.getName(), "bingop");
			assertEquals("it has the correct speed", resp.getSpeed().getValueIn(
					WorldSpeed.M_sec), 12, 0.001);
			assertEquals("correct course", resp.getCourse().floatValue(), 12f, 0.001);

			// second try, with missing speed
			ml = new ManoeuvreToCourse(null, false, new Float(12), true, null);
			ml.setName("bingop");
			os = new java.io.ByteArrayOutputStream();
			mr.exportThis(ml, os);

			// great, now try to read it in!
			is = new java.io.ByteArrayInputStream(os.toByteArray());
			mr.importThis("", is);

			// check we got it
			assertEquals("it has the correct name", resp.getName(), "bingop");
			assertEquals("it has the correct (null) speed", resp.getSpeed(), null);
			assertEquals("correct course", resp.getCourse().floatValue(), 12f, 0.001);

		}

		abstract class MyReaderWriter extends ASSET.Util.XML.ASSETReaderWriter
		{

			abstract public void responseIs(Response rec);

			/**
			 * handle the import of XML data into an existing session
			 */
			public void importThis(String fName, final java.io.InputStream is)
			{
				final MWC.Utilities.ReaderWriter.XML.MWCXMLReader handler = new ManoeuvreToCourseHandler()
				{
					public void setResponse(final Response dec)
					{
						responseIs(dec);
					}
				};
				handler.reportNotHandledErrors(false);

				// import the datafile into this set of layers
				doImport(new org.xml.sax.InputSource(is), handler);
			}

			/**
			 * exporting the session
			 */
			public void exportThis(final ManoeuvreToCourse scenario,
					final java.io.OutputStream os)
			{
				try
				{
					// output the XML header stuff
					// output the plot
					final Document doc = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder().newDocument();
					final org.w3c.dom.Element scen = doc.createElement("Test");
					ManoeuvreToCourseHandler.exportThis(scenario, scen, doc);
					doc.appendChild(scen);

					// ok, we should be done now

					// Write the DOM document to the file
					XMLSerializer serializer = new XMLSerializer();
					serializer.setOutputByteStream(os);
					serializer.serialize(doc);

				}
				catch (java.io.IOException e)
				{
					e.printStackTrace();
				}
				catch (ParserConfigurationException e)
				{
					e.printStackTrace();
				}
			}
		}

	}

}