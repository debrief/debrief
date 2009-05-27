package MWC.GUI.JFreeChart;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import MWC.GUI.CanvasType;
import MWC.GUI.StepperListener;
import MWC.GenericData.HiResDate;


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
	
  

  /** Working storage for available panel area after deducting insets. */
  private Rectangle2D available = new Rectangle2D.Double();

  /** Working storage for the chart area. */
  private Rectangle2D chartArea = new Rectangle2D.Double();

  
  /**
   * Paints the component by drawing the chart to fill the entire component,
   * but allowing for the insets (which will be non-zero if a border has been
   * set for this component).  To increase performance (at the expense of
   * memory), an off-screen buffer image can be used.
   *
   * @param g  the graphics device for drawing on.
   */
  public void paintWMFComponent(Graphics g) {

      Graphics2D g2 = (Graphics2D) g;

      // first determine the size of the chart rendering area...
      Dimension size = getSize();
      Insets insets = getInsets();
      available.setRect(insets.left, insets.top,
                        size.getWidth() - insets.left - insets.right,
                        size.getHeight() - insets.top - insets.bottom);

      // work out if scaling is required...
      boolean scale = false;
      double drawWidth = available.getWidth();
      double drawHeight = available.getHeight();
      double scaleX = 1.0;
      double scaleY = 1.0;

      if (drawWidth < this.getMinimumDrawWidth()) {
          scaleX = drawWidth / getMinimumDrawWidth();
          drawWidth = getMinimumDrawWidth();
          scale = true;
      }
      else if (drawWidth > this.getMaximumDrawWidth()) {
          scaleX = drawWidth / getMaximumDrawWidth();
          drawWidth = getMaximumDrawWidth();
          scale = true;
      }

      if (drawHeight < this.getMinimumDrawHeight()) {
          scaleY = drawHeight / getMinimumDrawHeight();
          drawHeight = getMinimumDrawHeight();
          scale = true;
      }
      else if (drawHeight > this.getMaximumDrawHeight()) {
          scaleY = drawHeight / getMaximumDrawHeight();
          drawHeight = getMaximumDrawHeight();
          scale = true;
      }

      chartArea.setRect(0.0, 0.0, drawWidth, drawHeight);

      AffineTransform saved = g2.getTransform();
      g2.translate(insets.left, insets.right);
      if (scale) {
          AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
          g2.transform(st);
      }
      getChart().draw(g2, chartArea, this.getChartRenderingInfo());
      g2.setTransform(saved);

  }

  
}
