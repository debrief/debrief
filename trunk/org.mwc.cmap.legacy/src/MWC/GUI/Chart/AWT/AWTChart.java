// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTChart.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: AWTChart.java,v $
// Revision 1.3  2005/09/23 14:54:03  Ian.Mayo
// Tidying
//
// Revision 1.2  2004/05/25 14:46:52  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:15  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:11  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-06-09 09:20:42+01  ian_mayo
// improved working rubber band, before method refactoring (to remove band parameter)
//
// Revision 1.2  2002-05-28 09:25:39+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:16+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-24 14:22:33+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.0  2001-07-17 08:46:30+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:01+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:44:10  ianmayo
// initial version
//
// Revision 1.4  2000-01-12 15:39:11+00  ian_mayo
// recognise renaming of AWTChart
//
// Revision 1.3  1999-12-13 11:28:19+00  ian_mayo
// added repaintNow function (although it's a noddy implementation)
//
// Revision 1.2  1999-11-23 10:37:55+00  ian_mayo
// moved directory to more sensible location
//
// Revision 1.1  1999-11-23 09:15:05+00  ian_mayo
// Initial revision
//
// Revision 1.2  1999-11-18 11:11:44+00  ian_mayo
// switched projection in use
//
// Revision 1.1  1999-10-12 15:37:02+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-08-04 09:45:33+01  administrator
// minor mods, tidying up
//
// Revision 1.2  1999-07-27 12:09:59+01  administrator
// changed update method of canvas to updateMe
//
// Revision 1.1  1999-07-27 10:50:47+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-16 10:01:46+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:06+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:59+01  sm11td
// Initial revision
//
// Revision 1.3  1999-02-04 08:02:28+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.2  1999-02-01 16:08:50+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:11+00  sm11td
// Initial revision
//


package MWC.GUI.Chart.AWT;
                
import java.awt.Component;
import java.awt.event.*;
import java.io.Serializable;

import MWC.GUI.*;
import MWC.GUI.Canvas.AWT.AWTCanvas;


/** The Chart is a canvas placed in a panel.
  * the majority of functionality is contained
  * in the PlainChart parent class, only the 
  * raw comms is in this class.
  * This is configured by setting the listeners to the 
  * chart/panel to be the listener functions defined in
  * the parent.
  *
  */
 final public class AWTChart extends PlainChart implements Serializable{
  
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  private AWTCanvas _theCanvas;
  
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  public AWTChart(Layers theLayers){
    super(theLayers);
    _theCanvas = new AWTCanvas();
    _theCanvas.setProjection(new MWC.Algorithms.Projections.FlatProjection());
//    _theCanvas.setProjection(new MWC.Algorithms.Projections.Mercator2());
//    _theCanvas.setProjection(new MWC.Algorithms.Projections.JMapTransformMercator());
//    _theCanvas.setProjection(new MWC.Algorithms.Projections.SysMercator());
//    _theCanvas.setProjection(new MWC.Algorithms.Projections.OpenMercator());
    
    // add us as a painter to the canvas
    _theCanvas.addPainter(this);
    
    // catch any resize events
    _theCanvas.addComponentListener(new ComponentAdapter(){
      public void componentResized(ComponentEvent e){
        canvasResized();   
      }
      });

    _theCanvas.getProjection().setScreenArea(_theCanvas.getSize());
    
    _theCanvas.addMouseMotionListener(this);
    _theCanvas.addMouseListener(this);
    
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /** repaint the indicated layer
   *
   */
  public void update(Layer changedLayer)
  {
    update();
  }

  public java.awt.Dimension getScreenSize(){
    // get the current size of the canvas
    return _theCanvas.getSize();
  }
  
  public Component getPanel(){
    return _theCanvas;
  }  

  public void update()
  {
    _theCanvas.updateMe();
  }

  public void rescale()
  {
    // do a rescale
    _theCanvas.rescale();
    
    // trigger a redraw of the canvas
    _theCanvas.updateMe();
  }
  
  public void repaint()
  {
    _theCanvas.repaint();
  }
  
	public void repaintNow(java.awt.Rectangle rect)
	{
		repaint();
	}
	
	
  public CanvasType getCanvas(){
    return _theCanvas;
  }

}












