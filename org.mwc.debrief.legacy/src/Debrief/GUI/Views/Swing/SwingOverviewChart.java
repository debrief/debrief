package Debrief.GUI.Views.Swing;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.*;
import MWC.GUI.Canvas.Swing.SwingCanvas;
import MWC.GUI.Chart.Swing.SwingChart;
import MWC.GUI.Tools.Chart.*;
import MWC.GenericData.*;

/** embedded class providing customized SwingChart used to provide an overview shot.
 *
 */
final class SwingOverviewChart extends SwingChart implements java.beans.PropertyChangeListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//////////////////////////////////////////////////
  // variables
  //////////////////////////////////////////////////
  /** the chart we update when double-clicking.
   *
   */
  final PlainChart _targetChart;

  /** our own pan tool, to put the pan events onto the buffer.
   *
   */
  private final Pan _myPan;

  /** our own zoom in tool, which allows us to view a zoomed in portion
   * of the plot.
   */
  private final ZoomIn _myZoom;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /** constructor passing info to parent class where applicable.
   *
   * @param theLayers the set of data we're plotting
   * @param theChart the chart we want to shift around on mouse-clicks
   * @param theParent the parent object providing cursor support
   */
  public SwingOverviewChart(final Layers theLayers, final PlainChart theChart,
                            final ToolParent theParent)
  {
    super(theLayers);

    _targetChart = theChart;

    this.getCanvas().getProjection().setDataBorder(1.1);

    this.getCanvas().getProjection().setName("Overview");

    // we also want to listen out for the projection of the target chart being changed
    _targetChart.getCanvas().getProjection().addListener(this);

    // create our own pan event thingy
    _myPan = new Pan(_targetChart, theParent, null)
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void areaSelected(final MWC.GenericData.WorldLocation theLocation, final Point thePoint){
        super.areaSelected(theLocation, thePoint);

        super.restoreCursor();

        // what's the current target area
        final WorldArea oldArea = _targetChart.getCanvas().getProjection().getDataArea();
        final WorldArea newArea = new WorldArea(oldArea);

        // shift to the new centre
        newArea.setCentre(theLocation);

        // we've got to restore the old area in order to calculate
        // the destination position in terms of old coordinates
        // instead of the current screen coordinates
        setNewArea(getChart().getCanvas().getProjection(), oldArea);

        super.doExecute(new PanAction(getChart().getCanvas().getProjection(), oldArea, newArea));

      }
    };

    // and the zoom
    _myZoom = new ZoomIn(_targetChart, theParent)
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void execute()
      {
        // add ourselves as the listener
        setChartDragListener(this);
      }
    };

    _myZoom.execute();

  }

  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  /** property to indicate if we are happy to perform double-buffering.
   * - override it to change the response
   * @return whether or not we allow double buffering
   */
  protected final boolean doubleBufferPlot()
  {
    return false;
  }

  /** over-rideable member function which allows us to over-ride the
   * canvas which gets used.
   * @return the Canvas to use
   */
  public final SwingCanvas createCanvas()
  {
    return new SwingCanvas()
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/** screen redraw, just repaint the buffer
       */
      public void update(final Graphics p1)
      {
        super.update(p1);

        // and paint the rect
        paintRect(p1);

      }

      public void drawText(final Font theFont, final String theStr, final int x, final int y)
      {
        // don't bother drawing text into the overview
      }

      public void drawText(final String theStr, final int x, final int y)
      {
        // as before, don't bother drawing text into the overview
      }
    };
  }

  /** over-ride the parent's version of paint, so that we can try to do it by layers.
   * @param dest - where we are going to paint to
   *
   */
  void paintRect(final Graphics dest)
  {
    // we want to draw a rectangle representing the current data area.  But,
    // sometimes the data area doesn't cover the whole area, when the user
    // zooms in on a selected area that area becomes the data area.
    // So, we'll convert the screen area to data coordinates

    // get the projection
    final PlainProjection proj = _targetChart.getCanvas().getProjection();

    // get the dimensions
    final Dimension scrArea = proj.getScreenArea();

    // did we find any data?
    if(scrArea == null)
      return;

    // now convert to data coordinates
    WorldLocation loc = proj.toWorld(new Point(0,0));

    // did it work?
    if(loc == null)
      return;

    // produce the screen coordinate in the overview
    final Point thePt = this.getCanvas().getProjection().toScreen(loc);

    // did it work?
    if(thePt == null)
     return;

    // and the other corner
    loc = proj.toWorld(new Point(scrArea.width, scrArea.height));

    // create the screen coordinates
    final Point tl = new Point(thePt);
    final Point br = new Point(this.getCanvas().getProjection().toScreen(loc));

    // draw the semi-transparent block
    dest.setColor(new Color(255, 255, 255, 60));
    dest.fillRect(tl.x, tl.y, br.x - tl.x, br.y - tl.y);

    // what's the old line thickness?
    // create the stroke
    final Graphics2D g2 = (Graphics2D) dest;
    final BasicStroke bs = (BasicStroke) g2.getStroke();

    // create the stroke
    final BasicStroke stk = new BasicStroke(1);
    g2.setStroke(stk);

    // and the solid border
    dest.setColor(new Color(255, 255, 255, 255));
    dest.drawRect(tl.x, tl.y, br.x - tl.x, br.y - tl.y);

    // and restore the thickness
    g2.setStroke(bs);
  }

  ////////////////////////////////////////////////////////////
  // mouse events
  ////////////////////////////////////////////////////////////
  public final void mouseClicked(final MouseEvent p1)
  {
    if (p1.getClickCount() == 2)
    {
      // get the location
      final Point clicked = p1.getPoint();

      // convert to real world
      final WorldLocation loc = this.getCanvas().getProjection().toWorld(clicked);


      // what's the current target area
      final WorldArea wa = _targetChart.getCanvas().getProjection().getDataArea();

      // shift to the new centre
      wa.setCentre(loc);

      // ok, now pass on the new data area
      _targetChart.getCanvas().getProjection().setDataArea(wa);

      // and trigger a repaint
      _targetChart.update();


      // use the pan tool to move the current view (and put the operation on the clipboard)
      _myPan.startDrag(loc, clicked);
      _myPan.areaSelected(loc, clicked);

      // and refresh ourselves (to redraw the viewing rectangle)
      this.update();
    }
    else
      super.mouseClicked(p1);
  }

  /** override the canvas resize event.
   *
   */

  public final void canvasResized()
  {
    // initialise the data area of the canvas
    this.getCanvas().getProjection().setDataArea(this.getDataArea());

    // do a fit to window
    this.getCanvas().getProjection().zoom(0.0d);

    this.update();
  }

  //////////////////////////////////////////////////
  // property change support
  //////////////////////////////////////////////////

  /**
   * This method gets called when a bound property is changed.
   * @param evt A PropertyChangeEvent object describing the event source
   *   	and the property that has changed.
   */

  public final void propertyChange(final PropertyChangeEvent evt)
  {

    final String propertyName = evt.getPropertyName();

    if (propertyName.equals(PlainProjection.REPLACED_EVENT))
    {
      // stop listening to the old one
      final PlainProjection oldProj = (PlainProjection) evt.getOldValue();
      oldProj.removeListener(this);

      // get the new projection opbject
      final PlainProjection newProj = (PlainProjection) evt.getNewValue();

      // and listen to it
      newProj.addListener(this);
    }
    else
    {
      // this.update();
      final SwingCanvas sc = (SwingCanvas) this.getCanvas();
      sc.repaint();
    }
  }

  /** provide method to clear stored data.
   */
  public final void close()
  {
    // stop listening to the projection
    _targetChart.getCanvas().getProjection().removeListener(this);

    // and pass to the parent
    super.close();
  }

}
