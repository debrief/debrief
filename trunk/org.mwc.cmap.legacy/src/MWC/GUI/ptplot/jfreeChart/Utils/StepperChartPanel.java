package MWC.GUI.ptplot.jfreeChart.Utils;

import MWC.GUI.CanvasType;
import MWC.GUI.StepperListener;
import MWC.GenericData.HiResDate;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.JFreeChart;

/**
 * *******************************************************************
 * embedded class which extends free chart to give current DTG indication
 * *******************************************************************
 */
public final class StepperChartPanel extends ChartPanel implements StepperListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
   * the step control we monitor
   */
  private final StepperListener.StepperController _myStepper;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * Constructs a panel containing a chart.
   *
   * @param chart     the chart.
   * @param useBuffer a flag controlling whether or not an off-screen buffer is used.
   */
  public StepperChartPanel(final JFreeChart chart, final boolean useBuffer,
                           final StepperListener.StepperController stepper)
  {
    super(chart, useBuffer);
    this._myStepper = stepper;

    if (_myStepper != null)
    {
      _myStepper.addStepperListener(this);
    }
  }

  //////////////////////////////////////////////////
  // support for time stepper
  //////////////////////////////////////////////////

  /**
   * the current time has changed
   */
  public final void newTime(final HiResDate oldDTG, final HiResDate newDTG, final CanvasType canvas)
  {

    // trigger refresh
    this.setRefreshBuffer(true);

    // and invalidate
    this.invalidate();

    // and the redraw
    this.repaint();

  }

  /**
   * the mode for stepping has changed
   */
  public final void steppingModeChanged(final boolean on)
  {
  }

}
