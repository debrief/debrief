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
  public String generateToolTip(final XYDataset data, final int series, final int item)
  {
    String result = "|" + data.getSeriesKey(series) + " | ";
    final Number x = data.getXValue(series, item);

    // put into Date value
    final Date newD = new Date(x.longValue());

    result = result + " DTG: " + newD.toString();

    final Number y = data.getYValue(series, item);
    if (y != null) {
      result = result + ", value: " + this.getYFormat().format(y);
    }
    else {
      result = result + ", y: null";
    }

    return result;
  }
}
