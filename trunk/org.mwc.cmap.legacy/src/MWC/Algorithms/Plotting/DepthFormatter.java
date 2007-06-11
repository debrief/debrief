/**
 * 
 */
package MWC.Algorithms.Plotting;

import java.io.Serializable;

import MWC.GUI.ptplot.jfreeChart.Utils.ModifiedVerticalNumberAxis;

import com.jrefinery.chart.*;

public class DepthFormatter implements formattingOperation, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void format(final XYPlot thePlot)
	{
		VerticalNumberAxis theAxis = (ModifiedVerticalNumberAxis) thePlot.getVerticalAxis();
		theAxis.setInverted(true);
	}

}