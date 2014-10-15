/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.Util.XMLFactory;

import java.text.ParseException;

import org.w3c.dom.Element;

import ASSET.Util.RandomGenerator;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class XMLRange implements XMLOperation
{
  /** the min value
   *
   */
  private double _min;

  /** the max value
   *
   */
  private double _max;

  /** the step to use
   *
   */
  private double _step;

  /** the text format to use
   *
   */
  private java.text.DecimalFormat _format;

  /** the current value of this operation
   *
   */
  private double _currentValue;

  /***************************************************************
   *  constructor
   ***************************************************************/
  public XMLRange(final Element element)
  {
    try
    {
      // get the data items
      _min = MWCXMLReader.readThisDouble(element.getAttribute("min"));
      _max =  MWCXMLReader.readThisDouble(element.getAttribute("max"));
      _step =  MWCXMLReader.readThisDouble(element.getAttribute("step"));
      final String fr = element.getAttribute("format");
      if(fr != null)
        _format = new java.text.DecimalFormat(fr);

      // stick in a random variable to start us off
      newPermutation();

    }
    catch(final ParseException de)
    {
      de.printStackTrace();
      throw new java.lang.RuntimeException("Poor XMLRange data");
    }
  }

  /** default constructor, used in cloning operation
   *
   */
  private XMLRange(final XMLRange other)
  {
    _currentValue = other._currentValue;
    _format = other._format;
    _max = other._max;
    _min = other._min;
    _step = other._step;

    // stick in a random variable to start us off
    newPermutation();

  }

  /***************************************************************
   *  member methods
   ***************************************************************/
  /** clone this object
   *
   */
  public Object clone()
  {
    final XMLRange res = new XMLRange(this);

    // but put the current value back in
    res._currentValue = this._currentValue;

    return res;
  }


  /** produce a new value for this operation
   *
   */
  public void newPermutation()
  {
    final int num_steps = (int)( (_max - _min) / _step);
    final int rnd = (int)(RandomGenerator.nextRandom() * num_steps);
    final double newVal = _min + rnd * _step;
    _currentValue = newVal;
  }

  /** return the current value of this permutation
   *
   */
  public String getValue()
  {
    final String res;
    if(_format == null)
    {
      res = "" + _currentValue;
    }
    else
    {
      res = _format.format(_currentValue);
    }

    return res;
  }


  /** return the human legible current value of this permutation
   *
   */
  public String getSimpleValue()
  {
    return getValue();
  }

  /** merge ourselves with the supplied operation
   *
   */
  public void merge(final XMLOperation other)
  {
    final XMLRange xr = (XMLRange)other;
    _currentValue = xr._currentValue;

    // add a little random variance
    final double variance = 0.05 * (_max - _min);
    final double var_2 = 2 * variance;
    final double rand = RandomGenerator.nextRandom();
    _currentValue += (var_2) * rand - (var_2);

    // check we're still in range
    _currentValue = Math.max(_currentValue, _min);
    _currentValue = Math.min(_currentValue, _max);

//    double sep = xr._currentValue - _currentValue;
//    double newVal = _currentValue + sep / 2;
//    _currentValue = newVal;
  }


}
