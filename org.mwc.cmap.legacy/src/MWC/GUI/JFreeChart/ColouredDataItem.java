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

import MWC.GUI.Editable;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: Feb 5, 2003 Time: 10:59:00 AM To change this
 * template use Options | File Templates.
 */
// ////////////////////////////////////////////////
// add a coloru to the data item pair
// ////////////////////////////////////////////////
public class ColouredDataItem extends TimeSeriesDataItem implements
    AttractiveDataItem
{
  public static interface OffsetProvider
  {
    /**
     * offset the provided time by the desired amount
     *
     * @param val
     *          the actual time value
     * @return the processed time value
     */
    public long offsetTimeFor(long val);
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * the color for this item
   */
  private final Color _myColor;

  /**
   * whether to connect this data item to the previous one
   *
   */
  private final boolean _connectToPrevious;

  /**
   * the provider for the time offset
   *
   */
  private final OffsetProvider _provider;

  /**
   * whether the symbol is visible in the parent object for this data item
   */
  private final boolean _parentSymVisible;

  private final boolean _isFilled;

  /**
   * (optionally) store the Debrief item that is being represented by this chart point
   */
  private final Editable _payload;

  /**
   * Constructs a new data pair.
   *
   * @param period
   *          the time period.
   * @param value
   *          the value associated with the time period.
   * @param myColor
   *          the color for this point
   * @param connectToPrevious
   *          whether to connect to the previous point (used when we're passing through zero)
   * @param provider
   *          If we're plotting relative times, this is an object which can supply the zero time to
   *          use
   * @param parentSymVisible
   *          whether the parent object this relates to is visible
   * @param isFilled
   *          whether we want this shape to be filled
   * @see ColouredDataItem#ColouredDataItem(TimePeriod period,double value,Color myColor,boolean
   *      connectToPrevious)
   */
  public ColouredDataItem(final RegularTimePeriod period, final double value,
      final Color myColor, final boolean connectToPrevious,
      final OffsetProvider provider, final boolean parentSymVisible,
      final boolean isFilled)
  {
    this(period, value, myColor, connectToPrevious, provider, parentSymVisible,
        isFilled, null);
  }

  /**
   * Constructs a new data pair.
   *
   * @param period
   *          the time period.
   * @param value
   *          the value associated with the time period.
   * @param myColor
   *          the color for this point
   * @param connectToPrevious
   *          whether to connect to the previous point (used when we're passing through zero)
   * @param provider
   *          If we're plotting relative times, this is an object which can supply the zero time to
   *          use
   * @param parentSymVisible
   *          whether the parent object this relates to is visible
   * @param isFilled
   *          whether we want this shape to be filled
   * @see ColouredDataItem#ColouredDataItem(TimePeriod period,double value,Color myColor,boolean
   *      connectToPrevious)
   */
  public ColouredDataItem(final RegularTimePeriod period, final double value,
      final Color myColor, final boolean connectToPrevious,
      final OffsetProvider provider, final boolean parentSymVisible,
      final boolean isFilled, final Editable payload)
  {
    super(period, value);
    _myColor = myColor;
    _connectToPrevious = connectToPrevious;
    _provider = provider;
    _parentSymVisible = parentSymVisible;
    _isFilled = isFilled;
    _payload = payload;
  }

  /**
   * whether to connect this data point to the previous one
   *
   * @return yes/no to connect
   */
  @Override
  public boolean connectToPrevious()
  {
    return _connectToPrevious;
  }

  /**
   * get the color for this point
   *
   * @return the color
   */
  @Override
  public final Color getColor()
  {
    return _myColor;
  }

  /**
   * the data item that we're rendering
   *
   * @return
   */
  public Editable getPayload()
  {
    return _payload;
  }

  /**
   * Returns the time period.
   *
   * @return the time period.
   */
  @Override
  public RegularTimePeriod getPeriod()
  {
    RegularTimePeriod res = super.getPeriod();
    if (_provider != null)
    {
      res = new FixedMillisecond(_provider.offsetTimeFor(res
          .getMiddleMillisecond()));
    }
    return res;
  }

  /**
   * whether the parent object this item refers to is visible
   *
   * @return
   */
  public boolean isParentSymVisible()
  {
    return _parentSymVisible;
  }

  /**
   * whether we wish this shape to be filled
   *
   * @return
   */
  public boolean isShapeFilled()
  {
    return _isFilled;
  }
}
