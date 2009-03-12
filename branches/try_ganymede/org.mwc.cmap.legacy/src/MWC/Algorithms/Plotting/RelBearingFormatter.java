/**
 * 
 */
package MWC.Algorithms.Plotting;

import java.io.Serializable;

import MWC.GUI.ptplot.jfreeChart.Utils.ModifiedVerticalNumberAxis;

import com.jrefinery.chart.*;

public class RelBearingFormatter implements formattingOperation, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void format(final XYPlot thePlot)
	{
		VerticalNumberAxis theAxis = (ModifiedVerticalNumberAxis) thePlot.getVerticalAxis();
		theAxis.setRange(-180, +180);
		theAxis.setLabel("(Red)    " + theAxis.getLabel() + "     (Green)");
		
		// create some tick units suitable for degrees
    theAxis.setStandardTickUnits(CourseFormatter.getDegreeTickUnits());
    theAxis.setAutoTickUnitSelection(true);		
	}
}