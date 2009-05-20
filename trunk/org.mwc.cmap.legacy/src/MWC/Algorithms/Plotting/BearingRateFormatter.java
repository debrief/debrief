/**
 * 
 */
package MWC.Algorithms.Plotting;

import java.io.Serializable;

import MWC.GUI.ptplot.jfreeChart.Utils.ModifiedVerticalNumberAxis;

import com.jrefinery.legacy.chart.*;

public class BearingRateFormatter implements formattingOperation, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void format(final XYPlot thePlot)
	{
		VerticalNumberAxis theAxis = (ModifiedVerticalNumberAxis) thePlot.getVerticalAxis();
		theAxis.setRange(-180, +180);
		theAxis.setLabel("(Left)    " + theAxis.getLabel() + "     (Right)");
	}
}