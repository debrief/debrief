package MWC.GUI.ptplot.jfreeChart.Utils;

import com.jrefinery.data.TimeSeriesDataPair;
import com.jrefinery.data.TimePeriod;
import com.jrefinery.data.FixedMillisecond;

import java.awt.*;
import java.io.Serializable;

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
public class ColouredDataItem extends TimeSeriesDataPair implements AttractiveDataItem, Serializable
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
  private boolean _connectToPrevious;

  /** the provider for the time offset
   *
   */
  private OffsetProvider _provider = null;

  /**
   * Constructs a new data pair.
   *
   * @param period  the time period.
   * @param value  the value associated with the time period.
   */
  private ColouredDataItem(final TimePeriod period,
                          final double value,
                          final Color myColor,
                          boolean connectToPrevious) {
    super(period, value);
    _myColor = myColor;
    _connectToPrevious = connectToPrevious;
  }

  /**
   * Constructs a new data pair.
   *
   * @param period            the time period.
   * @param value             the value associated with the time period.
   * @param myColor           the color for this point
   * @param connectToPrevious whether to connect to the previous point (used when we're passing through zero)
   * @param provider          If we're plotting relative times, this is an object which can supply the zero time to use
   * @see ColouredDataItem#ColouredDataItem(TimePeriod period,double value,Color myColor,boolean connectToPrevious)
   */
  public ColouredDataItem(final TimePeriod period,
                          final double value,
                          final Color myColor,
                          boolean connectToPrevious,
                          OffsetProvider provider) {
    this(period, value, myColor, connectToPrevious);
    _provider = provider;
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
  public TimePeriod getPeriod()
  {
    TimePeriod res = super.getPeriod();
    if(_provider != null)
    {
      res = new FixedMillisecond(_provider.offsetTimeFor(res.getMiddle()));
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
