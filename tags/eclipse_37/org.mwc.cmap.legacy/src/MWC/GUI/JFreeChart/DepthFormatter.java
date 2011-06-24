/**
 * 
 */
package MWC.GUI.JFreeChart;

import java.io.Serializable;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;


public class DepthFormatter implements formattingOperation, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void format(final XYPlot thePlot)
	{
		NumberAxis theAxis = (NumberAxis) thePlot.getRangeAxis();
		theAxis.setInverted(true);
	}

}