package MWC.GUI.ptplot.jfreeChart.Utils;

import com.jrefinery.legacy.data.XYDataPair;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian.mayo
 * Date: 30-Nov-2004
 * Time: 09:29:16
 * To change this template use File | Settings | File Templates.
 */
public class ColouredXYDataItem extends XYDataPair implements AttractiveDataItem
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  /**
   *  the color for this item
   */
  private final Color _myColor;

  /** whether to connect this data item to the previous one
   *
   */
  private boolean _connectToPrevious;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * Constructs a new data pair.
   *
   * @param x the x-value.
   * @param y the y-value.
   */
  public ColouredXYDataItem(double x, double y, Color myColor, boolean connectToPrevious)
  {
    super(x, y);
    _myColor = myColor;
    _connectToPrevious = connectToPrevious;
  }

  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////


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
}
