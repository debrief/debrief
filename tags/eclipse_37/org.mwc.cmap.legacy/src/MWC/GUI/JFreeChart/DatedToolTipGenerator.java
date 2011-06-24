package MWC.GUI.JFreeChart;

import java.util.Date;

import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

/** custom class which shows the date in a tooltip
 *
 */
public final class DatedToolTipGenerator extends StandardXYToolTipGenerator
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * Generates a tool tip text item for a particular item within a series.
   *
   * @param data  the dataset.
   * @param series  the series (zero-based index).
   * @param item  the item (zero-based index).
   *
   * @return the tool tip text.
   */
  public String generateToolTip(XYDataset data, int series, int item)
  {
    String result = "|" + data.getSeriesKey(series) + " | ";
    Number x = data.getXValue(series, item);

    // put into Date value
    Date newD = new Date(x.longValue());

    result = result + " DTG: " + newD.toString();

    Number y = data.getYValue(series, item);
    if (y != null) {
      result = result + ", value: " + this.getYFormat().format(y);
    }
    else {
      result = result + ", y: null";
    }

    return result;
  }
}
