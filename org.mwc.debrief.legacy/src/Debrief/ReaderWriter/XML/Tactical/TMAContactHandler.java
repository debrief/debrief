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
package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Enumeration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

abstract public class TMAContactHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	private static final String RANGE = "Range";
	private static final String MINIMA = "Minima";
	private static final String MAXIMA = "Maxima";
	private static final String MY_NAME = "tma_solution";

	Debrief.Wrappers.TMAContactWrapper _thisSolution = null;

	/**
	 * class which contains list of textual representations of label locations
	 */
	static final MWC.GUI.Properties.LocationPropertyEditor lp = new MWC.GUI.Properties.LocationPropertyEditor();

	/**
	 * the parameters to build up
	 */
	double _theBearingDegs = 0d;
	WorldDistance _theRange = null;
	WorldLocation _theOrigin = null;
	double _course = 0d;
	double _speed = 0d;
	double _depth = 0d;

	double _orientationDegs = 0d;
	WorldDistance _maxima = null;
	WorldDistance _minima = null;

	public TMAContactHandler()
  {
    // inform our parent what type of class we are
    super(MY_NAME);

    addHandler(new ColourHandler()
    {
      public void setColour(final java.awt.Color theVal)
      {
        _thisSolution.setColor(theVal);
      }
    });

    addHandler(new LocationHandler("centre")
    {
      public void setLocation(final MWC.GenericData.WorldLocation res)
      {
        _theOrigin = res;
      }
    });


    addAttributeHandler(new HandleAttribute("Dtg")
    {
      public void setValue(final String name, final String value)
      {
          try
          {
            _thisSolution.setDTG(DebriefFormatDateTime.parseThis(value));
          }
          catch (ParseException e)
          {
            Trace.trace(e, "While parsing date");
          }
      }
    });

    addAttributeHandler(new HandleDoubleAttribute("Bearing")
    {
      public void setValue(final String name, final double value)
      {
        _theBearingDegs = value;
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      public void setValue(final String name, final boolean value)
      {
        _thisSolution.setVisible(value);
      }
    });

    addAttributeHandler(new HandleAttribute("Label")
    {
      public void setValue(final String name, final String value)
      {
        _thisSolution.setLabel(fromXML(value));
      }
    });

    addAttributeHandler(new HandleAttribute("Symbol")
    {
      public void setValue(final String name, final String value)
      {
        _thisSolution.setSymbol(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("LabelShowing")
    {
      public void setValue(final String name, final boolean value)
      {
        _thisSolution.setLabelVisible(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("LineShowing")
    {
      public void setValue(final String name, final boolean value)
      {
        _thisSolution.setLineVisible(value);
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("EllipseShowing")
    {
      public void setValue(final String name, final boolean value)
      {
        _thisSolution.setEllipseVisible(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("SymbolShowing")
    {
      public void setValue(final String name, final boolean value)
      {
        _thisSolution.setSymbolVisible(value);
      }
    });


    addAttributeHandler(new HandleAttribute("LabelLocation")
    {
      public void setValue(final String name, final String val)
      {
        lp.setAsText(val);
        final Integer res = (Integer) lp.getValue();
        if (res != null)
          _thisSolution.setLabelLocation(res);
      }
    });

    addAttributeHandler(new HandleDoubleAttribute("Course")
    {
      public void setValue(final String name, final double value)
      {
        _course = value;
      }
    });

    addAttributeHandler(new HandleDoubleAttribute("Speed")
    {
      public void setValue(final String name, final double value)
      {
        _speed = value;
      }
    });

    addAttributeHandler(new HandleDoubleAttribute("Depth")
    {
      public void setValue(final String name, final double value)
      {
        _depth = value;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute("Orientation")
    {
      public void setValue(final String name, final double value)
      {
        _orientationDegs = value;
      }
    });

    addAttributeHandler(new HandleDoubleAttribute(MAXIMA)
    {
      public void setValue(final String name, final double value)
      {
        _maxima = new WorldDistance(value, WorldDistance.YARDS);
      }
    });

    addAttributeHandler(new HandleDoubleAttribute(MINIMA)
    {
      public void setValue(final String name, final double value)
      {
        _minima = new WorldDistance(value, WorldDistance.YARDS);
      }
    });
    
    addAttributeHandler(new HandleDoubleAttribute(RANGE)
    {
      public void setValue(final String name, final double value)
      {
        _theRange = new WorldDistance(value, WorldDistance.YARDS);
      }
    });

    addHandler(new WorldDistanceHandler(RANGE){
			@Override
			public void setWorldDistance(final WorldDistance res)
			{
				_theRange = res;
			}});
    addHandler(new WorldDistanceHandler(MAXIMA){
			@Override
			public void setWorldDistance(final WorldDistance res)
			{
				_maxima = res;
			}});
    addHandler(new WorldDistanceHandler(MINIMA){
			@Override
			public void setWorldDistance(final WorldDistance res)
			{
				_minima = res;
			}});
  }

	public final void handleOurselves(final String name, final Attributes atts)
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
			_thisSolution.buildSetVector(0d,null,0d);
		}
		else
		{
			// aah, it's relative - build up the vector
			final double theDepth = 0;
			_thisSolution.buildSetVector(_theBearingDegs, _theRange, theDepth);

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

	public static void exportSolution(final Debrief.Wrappers.TMAContactWrapper contact,
			final Element parent, final Document doc)
	{
		/*
		 * 
		 * <!ELEMENT tma_solution (colour?, centre?)> <!ATTLIST tma_solution Dtg
		 * CDATA #REQUIRED Bearing CDATA #IMPLIED Range CDATA #IMPLIED Visible (TRUE
		 * | FALSE) "TRUE" Label CDATA #REQUIRED LabelShowing (TRUE | FALSE) "TRUE"
		 * LineShowing (TRUE | FALSE) "TRUE" EllipseShowing (TRUE | FALSE) "TRUE"
		 * SymbolShowing (TRUE | FALSE) "TRUE" LabelLocation (Top | Left | Bottom |
		 * Centre | Right) "Left" Course CDATA #REQUIRED Speed CDATA #REQUIRED Depth
		 * CDATA #REQUIRED Orientation CDATA #REQUIRED Maxima CDATA #REQUIRED Minima
		 * CDATA #REQUIRED >
		 */
		final Element eFix = doc.createElement(MY_NAME);

		// note, we are accessing the "actual" colour for this fix, we are not using
		// the
		// normal getColor method which may return the track colour
		final java.awt.Color fCol = contact.getActualColor();
		if (fCol != null)
			ColourHandler.exportColour(fCol, eFix, doc);

		// are we absolute or relative?
		final WorldLocation origin = contact.buildGetOrigin();
		if (origin != null)
		{
			// so, absolute - output it
			LocationHandler.exportLocation(origin, "centre", eFix, doc);
		}
		else
		{
			// so, relative - output it
			eFix.setAttribute("Bearing", writeThis(contact.getBearing()));

			// don't do range by hand, automate it
			WorldDistanceHandler.exportDistance(RANGE, contact.getRange(), eFix, doc);
		}

		// carry on with the common parameters
		eFix.setAttribute("Dtg", writeThis(contact.getDTG()));
		eFix.setAttribute("Visible", writeThis(contact.getVisible()));

		// now the label/visibility
		eFix.setAttribute("Symbol", contact.getSymbol());
		eFix.setAttribute("Label", toXML(contact.getLabel()));
		eFix.setAttribute("LabelShowing", writeThis(contact.getLabelVisible()));

		final Boolean lineVis = contact.getRawLineVisible();
		// is this the same as the parent?
		if (lineVis != null)
		{
			// only output the line visibility if it is different to the parent.
			eFix.setAttribute("LineShowing", writeThis(lineVis.booleanValue()));
		}

		eFix.setAttribute("EllipseShowing", writeThis(contact.getEllipseVisible()));
		eFix.setAttribute("SymbolShowing", writeThis(contact.getSymbolVisible()));

		// where is the label?
		lp.setValue(contact.getLabelLocation());
		final String val = lp.getAsText();
		if (val != null)
			eFix.setAttribute("LabelLocation", lp.getAsText());
		else
			System.out.println("WRONG LABEL VALUE!!!");

		// and the target vector
		eFix.setAttribute("Course", writeThis(contact.getTargetCourse()));
		eFix.setAttribute("Speed", writeThis(contact.getSpeed()));
		eFix.setAttribute("Depth", writeThis(contact.getDepth()));

		// and ellipse shape
		final EllipseShape ellipse = contact.buildGetEllipse();
		final double maxima = ellipse.getMaxima().getValueIn(WorldDistance.YARDS);
		final double minima = ellipse.getMinima().getValueIn(WorldDistance.YARDS);

		// did we find ellipse data?
		if ((maxima > 0.0001d) && (minima > 0.0001d))
		{
			// yes, write it out
			eFix.setAttribute("Orientation", writeThis(ellipse.getOrientation()));

			WorldDistanceHandler.exportDistance(MAXIMA, ellipse.getMaxima(), eFix,
					doc);
			WorldDistanceHandler.exportDistance(MINIMA, ellipse.getMinima(), eFix,
					doc);
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

		public void testDummy() {
			
		}
		
		// TODO FIX-TEST
		public final void NtestRead()
		{
			final DebriefXMLReaderWriter reader = new DebriefXMLReaderWriter(null);
			final Layers res = new Layers();
			final String fName = "../org.mwc.debrief.legacy/src/test_tma_read_write.xml";

			final java.io.File fileTest = new File(fName);
			assertTrue("Test file not found:" + fName, fileTest.exists());

			try
			{
				final java.io.FileInputStream fis = new java.io.FileInputStream(fName);
				reader.importThis(fName, fis, res);

				// right, now check it contains our data
				final Layer layer = res.findLayer("TOMATO");
				assertNotNull("found tomato track");

				final TrackWrapper tw = (TrackWrapper) layer;
				final Enumeration<Editable> solutions = tw.getSolutions().elements();
				assertNotNull("found solutions", solutions);

				// find our solution track
				while (solutions.hasMoreElements())
				{
					final TMAWrapper wrapper = (TMAWrapper) solutions.nextElement();
					assertEquals("found our solution", "TRACK_060", wrapper.getName());

					final Enumeration<Editable> contacts = wrapper.elements();
					while (contacts.hasMoreElements())
					{
						final TMAContactWrapper contactWrapper = (TMAContactWrapper) contacts
								.nextElement();
						assertEquals("found first contact", "Trial label", contactWrapper
								.getLabel());
						assertEquals("correct symbol set", "Submarine", contactWrapper
								.getSymbol());
						assertEquals("correct vis set", true, contactWrapper.getVisible());
						assertEquals("correct label vis", true, contactWrapper
								.getLabelVisible());
						assertEquals("correct colour set", new Color(230, 200, 20),
								contactWrapper.getColor());
						assertEquals("correct ellipse vis", true, contactWrapper
								.getEllipseVisible());
						assertEquals("correct symbol vis", true, contactWrapper
								.getSymbolVisible());
						assertEquals("correct line vis", true, contactWrapper
								.getLineVisible());
						assertEquals("correct label loc", new Integer(
								MWC.GUI.Properties.LocationPropertyEditor.RIGHT),
								contactWrapper.getLabelLocation());
						assertEquals("correct line course", 50, contactWrapper
								.getTargetCourse(), 0d);
						assertEquals("correct line speed", 12.4, contactWrapper.getSpeed(),
								0d);
						assertEquals("correct line depth", 100, contactWrapper.getDepth(),
								0d);
						final EllipseShape es = contactWrapper.buildGetEllipse();
						assertEquals("correct orientation", 45, es.getOrientation(), 0d);
						assertEquals("correct maxima", 4000, es.getMaxima().getValueIn(
								WorldDistance.YARDS), 0.00001d);
						assertEquals("correct minima", 2000, es.getMinima().getValueIn(
								WorldDistance.YARDS), 0.0001d);
					}

				}
			}
			catch (final FileNotFoundException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
															// File Templates.
			}
		}
	}

	public static void main(final String[] args)
	{
//		final testIt ti = new testIt("scrap");
		// TODo FIX-TEST
		//ti.testRead();
	}

}