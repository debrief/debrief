/**
 * 
 */
package MWC.GUI.JFreeChart;

import java.io.Serializable;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;


public class RelBearingFormatter implements formattingOperation, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void format(final XYPlot thePlot)
	{
		NumberAxis theAxis = (NumberAxis) thePlot.getRangeAxis();
		theAxis.setRange(-180, +180);
		theAxis.setLabel("(Red)    " + theAxis.getLabel() + "     (Green)");
		
		// create some tick units suitable for degrees
    theAxis.setStandardTickUnits(CourseFormatter.getDegreeTickUnits());
    theAxis.setAutoTickUnitSelection(true);		
	}
}