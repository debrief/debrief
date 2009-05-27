/**
 * 
 */
package MWC.GUI.JFreeChart;

import java.io.Serializable;
import java.text.DecimalFormat;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.XYPlot;

public final class CourseFormatter implements formattingOperation, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final void format(final XYPlot thePlot)
	{
		NumberAxis theAxis = (NumberAxis) thePlot.getRangeAxis();
		theAxis.setRange(0, 360);
		
		// create some tick units suitable for degrees
    theAxis.setStandardTickUnits(getDegreeTickUnits());
    theAxis.setAutoTickUnitSelection(true);
		
	}
	
	public final static TickUnits getDegreeTickUnits()
	{
		TickUnits units = new TickUnits();
		DecimalFormat fmt = new DecimalFormat("0");
		DecimalFormat fmt2 = new DecimalFormat("0.0");
		DecimalFormat fmt3 = new DecimalFormat("0.00");
		
		units.add(new NumberTickUnit(0.05d,fmt3));
		units.add(new NumberTickUnit(0.1d, fmt2));
		units.add(new NumberTickUnit(0.5d, fmt2));
		units.add(new NumberTickUnit(1d,   fmt));
    units.add(new NumberTickUnit(2d,   fmt));
    units.add(new NumberTickUnit(5d,   fmt));
    units.add(new NumberTickUnit(10d,  fmt));
    units.add(new NumberTickUnit(30d,  fmt));
    units.add(new NumberTickUnit(45d,  fmt));
    units.add(new NumberTickUnit(90d,  fmt));    
    units.add(new NumberTickUnit(180d, fmt));
    units.add(new NumberTickUnit(360d, fmt));
    return units;
	}
	
}