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

import Debrief.Wrappers.Extensions.Measurements.TimeSeriesTmpDouble2;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class TimeSeries2DoubleHandler extends
    MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  /**
   * handle the discrete measurements
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
      addAttributeHandler(new HandleAttribute(VALUE1)
      {
        @Override
        public void setValue(final String name, final String value)
        {
          _values1.add(Double.parseDouble(value));
        }
      });
      addAttributeHandler(new HandleAttribute(VALUE2)
      {
        @Override
        public void setValue(final String name, final String value)
        {
          _values2.add(Double.parseDouble(value));
        }
      });

    }
  }

  private static final String VALUE1 = "Value1";
  private static final String VALUE2 = "Value2";
  private static final String VALUE1_NAME = "Value1_Name";
  private static final String VALUE2_NAME = "Value2_Name";
  private static final String INDEX = "Index";
  private static final String MEASUREMENT = "Item";
  private static final String UNITS = "Units";
  private static final String NAME = "Name";
  private static final String MY_TYPE = "TimeSeries2Double";

  public static void exportThisDataset(final TimeSeriesTmpDouble2 dataset,
      final Element parent, final Document doc)
  {
    final Element ds = doc.createElement(MY_TYPE);

    ds.setAttribute(NAME, dataset.getName());
    ds.setAttribute(UNITS, dataset.getUnits());

    // ok, now work through the children
    final Iterator<Long> indices = dataset.getIndices();
    final Iterator<Double> values1 = dataset.getValues1();
    final Iterator<Double> values2 = dataset.getValues2();

    while (indices.hasNext())
    {
      final Element ele = doc.createElement(MEASUREMENT);
      ele.setAttribute(INDEX, "" + indices.next());
      ele.setAttribute(VALUE1, "" + values1.next());
      ele.setAttribute(VALUE2, "" + values2.next());
      ds.appendChild(ele);
    }

    parent.appendChild(ds);
  }

  private String _name;
  private String _units;
  private String _value1Name;
  private String _value2Name;

  private ArrayList<Long> _indices;

  private ArrayList<Double> _values1;
  private ArrayList<Double> _values2;

  /**
   * class which contains list of textual representations of label locations
   */
  static final MWC.GUI.Properties.LocationPropertyEditor lp =
      new MWC.GUI.Properties.LocationPropertyEditor();

  public TimeSeries2DoubleHandler()
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
    addAttributeHandler(new HandleAttribute(VALUE1_NAME)
    {
      @Override
      public void setValue(final String name, final String value)
      {
        _value1Name = value;
      }
    });
    addAttributeHandler(new HandleAttribute(VALUE2_NAME)
    {
      @Override
      public void setValue(final String name, final String value)
      {
        _value2Name = value;
      }
    });
    addHandler(new MeasurementHandler());
  }

  abstract public void addDataset(TimeSeriesTmpDouble2 dataset);

  @Override
  public final void elementClosed()
  {
    // create the dataset
    final TimeSeriesTmpDouble2 dataset =
        new TimeSeriesTmpDouble2(_name, _units, _value1Name, _value2Name);

    final Iterator<Long> iIter = _indices.iterator();
    final Iterator<Double> v1Iter = _values1.iterator();
    final Iterator<Double> v2Iter = _values2.iterator();
    while (iIter.hasNext())
    {
      dataset.add(iIter.next(), v1Iter.next(), v2Iter.next());
    }

    // and store it
    addDataset(dataset);

    // reset our variables
    _name = null;
    _units = null;
    _indices = null;
    _value1Name = null;
    _value2Name = null;
    _values1 = null;
    _values2 = null;
  }

  @Override
  public final void handleOurselves(final String name, final Attributes atts)
  {
    super.handleOurselves(name, atts);

    _indices = new ArrayList<Long>();
    _values1 = new ArrayList<Double>();
    _values2 = new ArrayList<Double>();
  }

}