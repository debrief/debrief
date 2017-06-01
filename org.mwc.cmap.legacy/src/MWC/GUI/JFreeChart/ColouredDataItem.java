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
package MWC.GUI.JFreeChart;

import java.awt.Color;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Feb 5, 2003
 * Time: 10:59:00 AM
 * To change this template use Options | File Templates.
 */
//////////////////////////////////////////////////
// add a coloru to the data item pair
//////////////////////////////////////////////////
public class ColouredDataItem extends TimeSeriesDataItem implements AttractiveDataItem
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   *  the color for this item
   */
  private final Color _myColor;

  /** whether to connect this data item to the previous one
   *
   */
  private final boolean _connectToPrevious;

  /** the provider for the time offset
   *
   */
  private final OffsetProvider _provider;

  /** whether the symbol is visible in the parent object for
   * this data item
   */
  private final boolean _parentSymVisible;

  /**
   * Constructs a new data pair.
   *
   * @param period            the time period.
   * @param value             the value associated with the time period.
   * @param myColor           the color for this point
   * @param connectToPrevious whether to connect to the previous point (used when we're passing through zero)
   * @param provider          If we're plotting relative times, this is an object which can supply the zero time to use
   * @param parentSymVisible TODO
   * @see ColouredDataItem#ColouredDataItem(TimePeriod period,double value,Color myColor,boolean connectToPrevious)
   */
  public ColouredDataItem(final RegularTimePeriod period,
                          final double value,
                          final Color myColor,
                          final boolean connectToPrevious,
                          final OffsetProvider provider, boolean parentSymVisible) {
    super(period, value);
    _myColor = myColor;
    _connectToPrevious = connectToPrevious;
    _provider = provider;
    _parentSymVisible = parentSymVisible;
  }

  public boolean isParentSymVisible()
  {
    return _parentSymVisible;
  }


  /** get the color for this point
   *
   * @return the color
   */
  public final Color getColor() {
    return _myColor;
  }

  /** whether to connect this data point to the previous one
   *
   * @return yes/no to connect
   */
  public boolean connectToPrevious() {
    return _connectToPrevious;
  }

  /**
   * Returns the time period.
   *
   * @return the time period.
   */
  public RegularTimePeriod getPeriod()
  {
  	RegularTimePeriod res = super.getPeriod();
    if(_provider != null)
    {
      res = new FixedMillisecond(_provider.offsetTimeFor(res.getMiddleMillisecond()));
    }
    return res;
  }

  public static interface OffsetProvider
  {
    /** offset the provided time by the desired amount
     *
     * @param val the actual time value
     * @return the processed time value
     */
    public long offsetTimeFor(long val);
  }
}
