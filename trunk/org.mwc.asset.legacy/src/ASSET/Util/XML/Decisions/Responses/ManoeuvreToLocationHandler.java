package ASSET.Util.XML.Decisions.Responses;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Responses.ManoeuvreToLocation;
import ASSET.Models.Decision.Responses.Response;
import ASSET.Util.XML.Utils.ASSETLocationHandler;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

public class ManoeuvreToLocationHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private final static String type = "ManoeuvreToLocation";

  private final static String SPEED = "Speed";
  private final static String LOCATION = "Location";

  WorldLocation _myLocation;
  String _name;
  WorldSpeed _mySpeed;


  public ManoeuvreToLocationHandler()
  {
    super("ManoeuvreToLocation");

    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, final String val)
      {
        _name = val;
      }
    });

    addHandler(new WorldSpeedHandler()
    {
      public void setSpeed(WorldSpeed res)
      {
        _mySpeed = res;
      }
    });

    addHandler(new ASSETLocationHandler(LOCATION)
    {
      public void setLocation(final WorldLocation res)
      {
        _myLocation = res;
      }
    });
  }


  public void elementClosed()
  {
    final Response ml = new ManoeuvreToLocation(_myLocation, _mySpeed);
    ml.setName(_name);

    // finally output it
    setResponse(ml);

    // and reset
    _myLocation = null;
    _mySpeed = null;
    _name = null;
  }

  public void setResponse(Response dec)
  {
  };


  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final ManoeuvreToLocation bb = (ManoeuvreToLocation) toExport;

    // output it's attributes
    thisPart.setAttribute("Name", bb.getName());

    // and the speed, if we have it
    if (bb.getSpeed() != null)
    {
      WorldSpeedHandler.exportSpeed(SPEED, bb.getSpeed(), thisPart, doc);
    }

    ASSETLocationHandler.exportLocation(bb.getLocation(), LOCATION, thisPart, doc);

    parent.appendChild(thisPart);

  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public class ManToLocationHandlerTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public ManToLocationHandlerTest(final String val)
    {
      super(val);
    }

    ManoeuvreToLocation resp = null;

    public void testTheTrail()
    {
      // ok, let's go for it
      final MyReaderWriter mr = new MyReaderWriter()
      {
        public void responseIs(final Response rec)
        {
          resp = (ManoeuvreToLocation) rec;
        }
      };

      ManoeuvreToLocation ml = new ManoeuvreToLocation(new WorldLocation(1, 1, 1), new WorldSpeed(12, WorldSpeed.M_sec));
      ml.setName("bingop");
      java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
      mr.exportThis(ml, os);

      // great, now try to read it in!
      java.io.InputStream is = new java.io.ByteArrayInputStream(os.toByteArray());
      mr.importThis("", is);

      // check we got it
      assertEquals("it has the correct name", resp.getName(), "bingop");
      assertEquals("it has the correct speed", resp.getSpeed().getValueIn(WorldSpeed.M_sec), 12, 0.001);
      assertEquals("correct location", resp.getLocation(), new WorldLocation(1, 1, 1));

      // second try, with missing speed
      ml = new ManoeuvreToLocation(new WorldLocation(1, 1, 1), null);
      ml.setName("bingop");
      os = new java.io.ByteArrayOutputStream();
      mr.exportThis(ml, os);

      // great, now try to read it in!
      is = new java.io.ByteArrayInputStream(os.toByteArray());
      mr.importThis("", is);

      // check we got it
      assertEquals("it has the correct name", resp.getName(), "bingop");
      assertEquals("it has the correct (null) speed", resp.getSpeed(), null);
      assertEquals("correct location", resp.getLocation(), new WorldLocation(1, 1, 1));

    }

    abstract protected class MyReaderWriter extends ASSET.Util.XML.ASSETReaderWriter
    {

      abstract public void responseIs(Response rec);

      /**
       * handle the import of XML data into an existing session
       */
      public void importThis(String fName,
                             final java.io.InputStream is)
      {
        final MWC.Utilities.ReaderWriter.XML.MWCXMLReader handler = new ManoeuvreToLocationHandler()
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
      public void exportThis(final ManoeuvreToLocation scenario, final java.io.OutputStream os)
      {
        // output the XML header stuff
        // output the plot
        final com.sun.xml.tree.XmlDocument doc = new com.sun.xml.tree.XmlDocument();
        final org.w3c.dom.Element scen = doc.createElement("Test");
        ManoeuvreToLocationHandler.exportThis(scenario, scen, doc);
        doc.appendChild(scen);
        doc.changeNodeOwner(scen);
        doc.setSystemId("ASSET XML Version 1.0");

        // ok, we should be done now

        try
        {

          doc.write(os);
        }
        catch (java.io.IOException e)
        {
          e.printStackTrace();
        }
      }
    }

  }


}