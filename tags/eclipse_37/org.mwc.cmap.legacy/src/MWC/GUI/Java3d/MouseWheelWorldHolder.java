/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 16, 2002
 * Time: 11:04:52 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Java3d;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;

import MWC.GUI.StepperListener;
import MWC.GUI.ETOPO.BathyProvider;
import MWC.GUI.Properties.PropertiesPanel;

abstract public class MouseWheelWorldHolder extends WorldHolder
{

  ///////////////////////////
  // constructor
  ///////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/** constructor - just pass it all back to parent
   * @param liteVersion 
   *
   */
  public MouseWheelWorldHolder(PropertiesPanel thePanel,
                               StepperListener.StepperController stepper,
                               MWC.GUI.Layers theLayers,
                               BathyProvider bathyProvider, boolean liteVersion) {
    super(thePanel, stepper, theLayers, bathyProvider, liteVersion);
  }

  protected void initForm(boolean liteVersion)
  {
    // do the parent first
    super.initForm(liteVersion);

    // now the mouse stuff
    // handle instances where MouseWheel support not available (JDK before 1.4)
    try
    {
      /////////////////////////////////////////////////
      // mouse-wheel support
      /////////////////////////////////////////////////
      setupMouseWheel();
    }
    catch (NoClassDefFoundError e)
    {
      System.out.println("MOUSE SUPPORT NOT AVAILABLE");
    }
  }

  /** setup the mouse wheel handler part of it
   *
   */
  private void setupMouseWheel()
  {
    MouseWheelListener theWheelListener = new MyMouseWheelListener();

    // listen to the canvas
    _myCanvas.addMouseWheelListener(theWheelListener);
  }

  public void doClose() {
    // remove the mouse listeners
    closeMouseWheel();

    // and pass back to the parent
    super.doClose();
  }

  /** remove the mouse wheel
   *
   */
  private void closeMouseWheel()
  {
  }

  /**********************************************************************
   * embedded class which handles the MouseWheel activity
   *********************************************************************/
  protected class MyMouseWheelListener implements MouseWheelListener {
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent e)
    {

      // find out if we are doing our "special" processing
      if(e.getModifiers() == MouseEvent.CTRL_MASK)
      {
        // set the scale factor
        double scale = 1.1;

        // are we zooming in our out?
        if(e.getWheelRotation() > 0)
          scale = 1 / scale;

        int amountRotated = e.getWheelRotation();

        doZoom(amountRotated);

      }
      else
      {
        // we are moving forward/backward in time
        int rot = e.getWheelRotation();
        boolean fwd = (rot > 0);

        // are we doing a large step?
        boolean large_step = (e.getModifiers() == MouseEvent.ALT_MASK);

        if(_stepper != null)
          _stepper.doStep(fwd, large_step);
      }
    }
  }
}
