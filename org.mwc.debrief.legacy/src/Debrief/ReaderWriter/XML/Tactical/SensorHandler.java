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

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.ReaderWriter.XML.extensions.AdditionalDataHandler;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.Extensions.AdditionalData;
import Debrief.Wrappers.Track.ArrayOffsetHelper;
import Debrief.Wrappers.Track.ArrayOffsetHelper.ArrayCentreMode;
import Debrief.Wrappers.Track.ArrayOffsetHelper.LegacyArrayOffsetModes;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class SensorHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	Debrief.Wrappers.SensorWrapper _mySensor;
	private static final String WORM_IN_HOLE = "WormInHole";
  private static final String ARRAY_MODE = "ArrayMode";

	private static final String OFFSET = "Offset";
	private static final String BASE_FREQUENCY = "BaseFrequency";

	private double _baseFrequency = 0;
			
	public SensorHandler()
	{
		// inform our parent what type of class we are
		super("sensor");

		addHandler(new ColourHandler()
		{
			public void setColour(final java.awt.Color res)
			{
				_mySensor.setColor(res);
			}
		});

		addHandler(new SensorContactHandler()
		{
			public void addContact(final MWC.GUI.Plottable contact)
			{
				addThisContact(contact);

				// and set the sensor for that contact
				final SensorContactWrapper sc = (SensorContactWrapper) contact;
				sc.setSensor(_mySensor);

			}
		});
		addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(final String name, final String val)
			{
				_mySensor.setName(fromXML(val));
			}
		});
		addAttributeHandler(new HandleAttribute("TrackName")
		{
			public void setValue(final String name, final String val)
			{
				_mySensor.setTrackName(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("Visible")
		{
			public void setValue(final String name, final boolean val)
			{
				_mySensor.setVisible(val);
			}
		});
		addAttributeHandler(new HandleIntegerAttribute("LineThickness")
		{
			public void setValue(final String name, final int val)
			{
				_mySensor.setLineThickness(val);
			}
		});

		addHandler(new WorldDistanceHandler(OFFSET)
		{
			public void setWorldDistance(final WorldDistance value)
			{
				// just check it contains a credible value
				if (value.getValue() != 0)
					_mySensor.setSensorOffset(new WorldDistance.ArrayLength(value));
			}
		});
    addAttributeHandler(new HandleDoubleAttribute(BASE_FREQUENCY)
    {
      public void setValue(final String name, final double val)
      {
        _baseFrequency = val;
      }
    });

		addAttributeHandler(new HandleBooleanAttribute(WORM_IN_HOLE)
		{
			@Override
			public void setValue(final String name, final boolean value)
			{
			  // decide which legacy mode to use
			  final LegacyArrayOffsetModes mode = value ? LegacyArrayOffsetModes.WORM : LegacyArrayOffsetModes.PLAIN;
				_mySensor.setArrayCentreMode(mode);
			}
		});
    addAttributeHandler(new HandleAttribute(ARRAY_MODE)
    {
      @Override
      public void setValue(final String name, final String value)
      {
        final ArrayCentreMode mode;
        // ok, handle the string
        if(LegacyArrayOffsetModes.PLAIN.asString().equals(value))
        {
          mode = LegacyArrayOffsetModes.PLAIN;
        }
        else if(LegacyArrayOffsetModes.WORM.asString().equals(value))
        {
          mode = LegacyArrayOffsetModes.WORM;
        }
        else
        {
          // ok, it must be a datset name
          mode = new 
              ArrayOffsetHelper.DeferredDatasetArrayMode(value);
        }
        _mySensor.setArrayCentreMode(mode);
      }
    });
		
		// and one for any additional data
		addHandler(new AdditionalDataHandler()
		{
		  @Override
		  public void storeData(final AdditionalData data)
		  {
		    _mySensor.getAdditionalData().addAll(data);
		  }
		});
	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(final String name, final Attributes attributes)
	{
		_mySensor = new Debrief.Wrappers.SensorWrapper("");

		super.handleOurselves(name, attributes);
	}

	void addThisContact(final MWC.GUI.Plottable val)
	{
		// store in our list
		_mySensor.add(val);
	}

	public final void elementClosed()
	{
		// our layer is complete, add it to the parent!
		addSensor(_mySensor);

		if(_baseFrequency != 0)
		{
			_mySensor.setBaseFrequency(_baseFrequency);
		}

		_baseFrequency = 0;		
		_mySensor = null;
	}

	abstract public void addSensor(Debrief.Wrappers.SensorWrapper data);

	public static void exportSensor(final Debrief.Wrappers.SensorWrapper sensor,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{

		/*
		 * <!ELEMENT sensor (colour,((fix|contact)*))> <!ATTLIST track name CDATA
		 * #REQUIRED visible (TRUE|FALSE) "TRUE" PositionsLinked (TRUE|FALSE) "TRUE"
		 * NameVisible (TRUE|FALSE) "TRUE" PositionsVisible (TRUE|FALSE) "TRUE"
		 * NameAtStart (TRUE|FALSE) "TRUE" NameLocation
		 * (Top|Left|Bottom|Centre|Right) "Right" Symbol CDATA "SQUARE" >
		 */
		final Element trk = doc.createElement("sensor");
		trk.setAttribute("Name", toXML(sensor.getName()));
		trk.setAttribute("Visible", writeThis(sensor.getVisible()));
		trk.setAttribute("TrackName", sensor.getTrackName());
		trk.setAttribute("LineThickness", writeThis(sensor.getLineThickness()));
		ColourHandler.exportColour(sensor.getColor(), trk, doc);

		// do we have an offset?  It's only used for legacy modes,
		// but we may as well store it anyway.
		final WorldDistance theOffset = sensor.getSensorOffset();
		if (theOffset != null)
		{
			// check it has a legitimate value
			if (theOffset.getValue() != 0)
				MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler
						.exportDistance(OFFSET, theOffset, trk, doc);
		}

		// and the worm in the hole indicator?
		ArrayCentreMode mode = sensor.getArrayCentreMode();
		trk.setAttribute(ARRAY_MODE, mode.asString());

		// do we have a base frequency?
		final double baseF = sensor.getBaseFrequency();
		if(baseF != 0)
		{
			trk.setAttribute(BASE_FREQUENCY, writeThis(baseF));
		}
		
		// now the points
		final java.util.Enumeration<Editable> iter = sensor.elements();
		while (iter.hasMoreElements())
		{
			final MWC.GUI.Plottable pl = (MWC.GUI.Plottable) iter.nextElement();
			if (pl instanceof Debrief.Wrappers.SensorContactWrapper)
			{
				final Debrief.Wrappers.SensorContactWrapper fw = (Debrief.Wrappers.SensorContactWrapper) pl;
				SensorContactHandler.exportFix(fw, trk, doc);
			}

		}

		// chuck in some extended data support
		AdditionalDataHandler.appendChild(sensor, trk, doc);
		
		parent.appendChild(trk);
	}

}