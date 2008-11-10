package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;


abstract public class TMAContactHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  private static final String MY_NAME = "tma_solution";


  private Debrief.Wrappers.TMAContactWrapper _thisSolution = null;

  /**
   * class which contains list of textual representations of label locations
   */
  static private final MWC.GUI.Properties.LocationPropertyEditor lp
    = new MWC.GUI.Properties.LocationPropertyEditor();


  /**
   * the parameters to build up
   */
  private double _theBearing = 0d;
  private WorldDistance _theRange = null;
  private WorldLocation _theOrigin = null;
  private double _course = 0d;
  private double _speed = 0d;
  private double _depth = 0d;

  private double _orientationDegs = 0d;
  private WorldDistance _maxima = null;
  private WorldDistance _minima = null;


  public TMAContactHandler()
  {
    // <!ELEMENT tma_solution (colour?, centre?)>
    //	Dtg CDATA #REQUIRED
    //	Bearing CDATA #REQUIRED
    //	Range CDATA #REQUIRED
    //	Visible (TRUE | FALSE) "TRUE"
    //	Label CDATA #REQUIRED
    //	LabelShowing (TRUE | FALSE) "TRUE"
    //	LineShowing (TRUE | FALSE) "TRUE"
    //	EllipseShowing (TRUE | FALSE) "TRUE"
    //	SymbolShowing (TRUE | FALSE) "TRUE"
    //	LabelLocation (Top | Left | Bottom | Centre | Right) "Left"
    //	Course CDATA #REQUIRED
    //	Speed CDATA #REQUIRED
    //	Depth CDATA #REQUIRED
    //	Orientation CDATA #REQUIRED
    //	Maxima CDATA #REQUIRED
    //	Minima CDATA #REQUIRED

    // inform our parent what type of class we are
    super(MY_NAME);

    addHandler(new ColourHandler()
    {
      public void setColour(java.awt.Color theVal)
      {
        _thisSolution.setColor(theVal);
      }
    });

    addHandler(new LocationHandler("centre")
    {
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _theOrigin = res;
      }
    });


    addAttributeHandler(new HandleAttribute("Dtg")
    {
      public void setValue(String name, String value)
      {
          _thisSolution.setDTG(DebriefFormatDateTime.parseThis(value));
      }
    });

    addAttributeHandler(new HandleDoubleAttribute("Bearing")
    {
      public void setValue(String name, double value)
      {
        _theBearing = value;
      }
    });

    addAttributeHandler(new HandleDoubleAttribute("Range")
    {
      public void setValue(String name, double value)
      {
        _theRange = new WorldDistance(value, WorldDistance.YARDS);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      public void setValue(String name, boolean value)
      {
        _thisSolution.setVisible(value);
      }
    });

    addAttributeHandler(new HandleAttribute("Label")
    {
      public void setValue(String name, String value)
      {
        _thisSolution.setLabel(fromXML(value));
      }
    });

    addAttributeHandler(new HandleAttribute("Symbol")
    {
      public void setValue(String name, String value)
      {
        _thisSolution.setSymbol(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("LabelShowing")
    {
      public void setValue(String name, boolean value)
      {
        _thisSolution.setLabelVisible(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("LineShowing")
    {
      public void setValue(String name, boolean value)
      {
        _thisSolution.setLineVisible(value);
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("EllipseShowing")
    {
      public void setValue(String name, boolean value)
      {
        _thisSolution.setEllipseVisible(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("SymbolShowing")
    {
      public void setValue(String name, boolean value)
      {
        _thisSolution.setSymbolVisible(value);
      }
    });


    addAttributeHandler(new HandleAttribute("LabelLocation")
    {
      public void setValue(String name, String val)
      {
        lp.setAsText(val);
        Integer res = (Integer) lp.getValue();
        if (res != null)
          _thisSolution.setLabelLocation(res);
      }
    });

    addAttributeHandler(new HandleDoubleAttribute("Course")
    {
      public void setValue(String name, double value)
      {
        _course = value;
      }
    });

    addAttributeHandler(new HandleDoubleAttribute("Speed")
    {
      public void setValue(String name, double value)
      {
        _speed = value;
      }
    });

    addAttributeHandler(new HandleDoubleAttribute("Depth")
    {
      public void setValue(String name, double value)
      {
        _depth = value;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute("Orientation")
    {
      public void setValue(String name, double value)
      {
        _orientationDegs = value;
      }
    });

    addAttributeHandler(new HandleDoubleAttribute("Maxima")
    {
      public void setValue(String name, double value)
      {
        _maxima = new WorldDistance(value, WorldDistance.YARDS);
      }
    });

    addAttributeHandler(new HandleDoubleAttribute("Minima")
    {
      public void setValue(String name, double value)
      {
        _minima = new WorldDistance(value, WorldDistance.YARDS);
      }
    });


  }

  public final void handleOurselves(String name, Attributes atts)
  {
    // create the new items
    _thisSolution = new Debrief.Wrappers.TMAContactWrapper();

    // reset the label location property editor
    lp.setValue(null);

    // and handle the parameters...
    super.handleOurselves(name, atts);
  }

  public final void elementClosed()
  {
    // ok, find out how the ellipse is defined

    // do we have a centre?
    if (_theOrigin != null)
    {
      // so, this is an absolute position solution
      _thisSolution.buildSetOrigin(_theOrigin);

      // and reset the vector
      _thisSolution.buildSetVector(null);
    }
    else
    {
      // aah, it's relative - build up the vector
      WorldVector theVector = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(_theBearing),
                                              _theRange.getValueIn(WorldDistance.DEGS),
                                              0);
      _thisSolution.buildSetVector(theVector);

      // and reset the position
      _thisSolution.buildSetOrigin(null);
    }

    // and carry on with the other parameters
    _thisSolution.buildSetTargetState(_course, _speed, _depth);

    // and the ellipse
    _thisSolution.buildSetEllipse(_orientationDegs, _maxima, _minima);

    addSolution(_thisSolution);

    // reset our variables
    _thisSolution = null;
    _theRange = null;
    _theOrigin = null;
    _orientationDegs = 0d;
    _maxima = null;
    _minima = null;
  }

  abstract public void addSolution(MWC.GUI.Plottable plottable);

  public static void exportSolution(Debrief.Wrappers.TMAContactWrapper contact, Element parent, Document doc)
  {
    /*

 <!ELEMENT tma_solution (colour?, centre?)>
<!ATTLIST tma_solution
	Dtg CDATA #REQUIRED
	Bearing CDATA #IMPLIED
	Range CDATA #IMPLIED
	Visible (TRUE | FALSE) "TRUE"
	Label CDATA #REQUIRED
	LabelShowing (TRUE | FALSE) "TRUE"
	LineShowing (TRUE | FALSE) "TRUE"
	EllipseShowing (TRUE | FALSE) "TRUE"
	SymbolShowing (TRUE | FALSE) "TRUE"
	LabelLocation (Top | Left | Bottom | Centre | Right) "Left"
	Course CDATA #REQUIRED
	Speed CDATA #REQUIRED
	Depth CDATA #REQUIRED
	Orientation CDATA #REQUIRED
	Maxima CDATA #REQUIRED
	Minima CDATA #REQUIRED
>

    */
    Element eFix = doc.createElement(MY_NAME);

    // note, we are accessing the "actual" colour for this fix, we are not using the
    // normal getColor method which may return the track colour
    java.awt.Color fCol = contact.getActualColor();
    if (fCol != null)
      ColourHandler.exportColour(fCol, eFix, doc);

    // are we absolute or relative?
    WorldLocation origin = contact.buildGetOrigin();
    if (origin != null)
    {
      // so, absolute - output it
      LocationHandler.exportLocation(origin, "centre", eFix, doc);
    }
    else
    {
      // so, relative - output it
      WorldVector vector = contact.buildGetVector();
      eFix.setAttribute("Bearing", writeThis(MWC.Algorithms.Conversions.Rads2Degs(vector.getBearing())));
      eFix.setAttribute("Range", writeThis(MWC.Algorithms.Conversions.Degs2Yds(vector.getRange())));
    }

    // carry on with the common parameters
    eFix.setAttribute("Dtg", writeThis(contact.getDTG()));
    eFix.setAttribute("Visible", writeThis(contact.getVisible()));

    // now the label/visibility
    eFix.setAttribute("Symbol", contact.getSymbol());
    eFix.setAttribute("Label", toXML(contact.getLabel()));
    eFix.setAttribute("LabelShowing", writeThis(contact.getLabelVisible()));

    Boolean lineVis = contact.getRawLineVisible();
    // is this the same as the parent?
    if(lineVis != null)
    {
      // only output the line visibility if it is different to the parent.
      eFix.setAttribute("LineShowing", writeThis(lineVis.booleanValue()));
    }

    eFix.setAttribute("EllipseShowing", writeThis(contact.getEllipseVisible()));
    eFix.setAttribute("SymbolShowing", writeThis(contact.getSymbolVisible()));

    // where is the label?
    lp.setValue(contact.getLabelLocation());
    String val = lp.getAsText();
    if (val != null)
      eFix.setAttribute("LabelLocation", lp.getAsText());
    else
      System.out.println("WRONG LABEL VALUE!!!");

    // and the target vector
    eFix.setAttribute("Course", writeThis(contact.getTargetCourse()));
    eFix.setAttribute("Speed", writeThis(contact.getSpeed()));
    eFix.setAttribute("Depth", writeThis(contact.getDepth()));

    // and ellipse shape
    EllipseShape ellipse = contact.buildGetEllipse();
    double maxima = ellipse.getMaxima().getValueIn(WorldDistance.YARDS);
    double minima = ellipse.getMinima().getValueIn(WorldDistance.YARDS);

    // did we find ellipse data?
    if ((maxima > 0.0001d) && (minima > 0.0001d))
    {
      // yes, write it out
      eFix.setAttribute("Orientation", writeThis(ellipse.getOrientation()));
      eFix.setAttribute("Maxima", writeThis(maxima));
      eFix.setAttribute("Minima", writeThis(minima));
    }

    // done
    parent.appendChild(eFix);

  }


  static public final class testIt extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";


    public testIt(final String val)
    {
      super(val);
    }

    public final void testRead()
    {
      DebriefXMLReaderWriter reader = new DebriefXMLReaderWriter(null);
      Layers res = new Layers();
      //       final String fName = "D:\\dev\\Debrief\\Source\\src\\Debrief\\test_tma_read_write.xml";
      String fName = System.getProperty("dataDir");
      assertNotNull("dataDir system variable not set - should be in debrief_legacy\\src", fName);
      fName += "/test_tma_read_write.xml";
      
      java.io.File fileTest = new File(fName);
      assertTrue("Test file not found:" + fName, fileTest.exists());
      
      try
      {
        java.io.FileInputStream fis = new java.io.FileInputStream(fName);
        reader.importThis(fName, fis, res);

        // right, now check it contains our data
        Layer layer = res.findLayer("TOMATO");
        assertNotNull("found tomato track");

        TrackWrapper tw = (TrackWrapper) layer;
        Enumeration solutions = tw.getSolutions();
        assertNotNull("found solutions", solutions);

        // find our solution track
        while (solutions.hasMoreElements())
        {
          TMAWrapper wrapper = (TMAWrapper) solutions.nextElement();
          assertEquals("found our solution", "TRACK_060", wrapper.getName());

          Enumeration contacts = wrapper.elements();
          while (contacts.hasMoreElements())
          {
            TMAContactWrapper contactWrapper = (TMAContactWrapper) contacts.nextElement();
            assertEquals("found first contact", "Trial label", contactWrapper.getLabel());
            assertEquals("correct symbol set", "Submarine", contactWrapper.getSymbol());
            assertEquals("correct vis set", true, contactWrapper.getVisible());
            assertEquals("correct label vis", true, contactWrapper.getLabelVisible());
            assertEquals("correct colour set", new Color(230, 200, 20), contactWrapper.getColor());
            assertEquals("correct ellipse vis", true, contactWrapper.getEllipseVisible());
            assertEquals("correct symbol vis", true, contactWrapper.getSymbolVisible());
            assertEquals("correct line vis", true, contactWrapper.getLineVisible());
            assertEquals("correct label loc", new Integer(MWC.GUI.Properties.LocationPropertyEditor.RIGHT), contactWrapper.getLabelLocation());
            assertEquals("correct line course", 50, contactWrapper.getTargetCourse(), 0d);
            assertEquals("correct line speed", 12.4, contactWrapper.getSpeed(), 0d);
            assertEquals("correct line depth", 100, contactWrapper.getDepth(), 0d);
            EllipseShape es = contactWrapper.buildGetEllipse();
            assertEquals("correct orientation", 45, es.getOrientation(), 0d);
            assertEquals("correct maxima", 4000, es.getMaxima().getValueIn(WorldDistance.YARDS), 0.00001d);
            assertEquals("correct minima", 2000, es.getMinima().getValueIn(WorldDistance.YARDS), 0.0001d);
          }

        }
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }
    }
  }

  public static void main(String[] args)
  {
    testIt ti = new testIt("scrap");
    ti.testRead();
  }


}