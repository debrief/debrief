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
package Debrief.ReaderWriter.XML.extensions;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.Wrappers.Extensions.Measurements.TimeSeriesTmpDouble;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class TimeSeriesDoubleHandler extends
    MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  /** handle the discrete measurements
   * 
   * @author ian
   *
   */
  private class MeasurementHandler extends MWCXMLReader
  {
    protected MeasurementHandler()
    {
      super(MEASUREMENT);

      addAttributeHandler(new HandleAttribute(INDEX)
      {
        @Override
        public void setValue(final String name, final String value)
        {
          _indices.add(Long.parseLong(value));
        }
      });
      addAttributeHandler(new HandleAttribute(VALUE)
      {
        @Override
        public void setValue(final String name, final String value)
        {
          _values.add(Double.parseDouble(value));
        }
      });

    }
  }

  private static final String VALUE = "Value";
  private static final String INDEX = "Index";
  private static final String MEASUREMENT = "Item";
  private static final String UNITS = "Units";
  private static final String NAME = "Name";
  private static final String MY_TYPE = "TimeSeriesDouble";

  public static void exportThisDataset(final TimeSeriesTmpDouble dataset,
      final Element parent, final Document doc)
  {
    final Element ds = doc.createElement(MY_TYPE);

    ds.setAttribute(NAME, dataset.getName());
    ds.setAttribute(UNITS, dataset.getUnits());

    // ok, now work through the children
    final Iterator<Long> indices = dataset.getIndices();
    final Iterator<Double> values = dataset.getValues();

    while (indices.hasNext())
    {
      final Element ele = doc.createElement(MEASUREMENT);
      ele.setAttribute(INDEX, "" + indices.next());
      ele.setAttribute(VALUE, "" + values.next());
      ds.appendChild(ele);
    }

    parent.appendChild(ds);
  }

  private String _name;
  private String _units;

  private ArrayList<Long> _indices;

  private ArrayList<Double> _values;

  /**
   * class which contains list of textual representations of label locations
   */
  static final MWC.GUI.Properties.LocationPropertyEditor lp =
      new MWC.GUI.Properties.LocationPropertyEditor();

  public TimeSeriesDoubleHandler()
  {
    // inform our parent what type of class we are
    super(MY_TYPE);

    addAttributeHandler(new HandleAttribute(NAME)
    {
      @Override
      public void setValue(final String name, final String value)
      {
        _name = value;
      }
    });
    addAttributeHandler(new HandleAttribute(UNITS)
    {
      @Override
      public void setValue(final String name, final String value)
      {
        _units = value;
      }
    });
    addHandler(new MeasurementHandler());
  }

  abstract public void addDataset(TimeSeriesTmpDouble dataset);

  @Override
  public final void elementClosed()
  {
    // create the dataset
    final TimeSeriesTmpDouble dataset = new TimeSeriesTmpDouble(_name, _units);

    final Iterator<Long> iIter = _indices.iterator();
    final Iterator<Double> vIter = _values.iterator();
    while (iIter.hasNext())
    {
      dataset.add(iIter.next(), vIter.next());
    }

    // and store it
    addDataset(dataset);

    // reset our variables
    _name = null;
    _units = null;
    _indices = null;
    _values = null;
  }

  @Override
  public final void handleOurselves(final String name, final Attributes atts)
  {
    super.handleOurselves(name, atts);

    _indices = new ArrayList<Long>();
    _values = new ArrayList<Double>();
  }

}