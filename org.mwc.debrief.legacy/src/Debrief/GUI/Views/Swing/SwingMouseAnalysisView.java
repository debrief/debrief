/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package Debrief.GUI.Views.Swing;

import Debrief.GUI.Frames.Session;
import Debrief.GUI.Frames.Swing.SwingSession;

import java.awt.event.MouseEvent;

import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;

public final class SwingMouseAnalysisView extends SwingAnalysisView
{
  /**************************************************************
   * member methods
   **************************************************************/

  /** the instance of listener which listens to the mouse wheel,
   * and moves the tote backwards and forwards
   */
  private java.util.EventListener _wheelListener = null;


  /**************************************************************
   * constructor
   **************************************************************/
  public SwingMouseAnalysisView(final ToolParent theParent,
               final SwingSession theSession)
  {
    super(theParent, theSession);
  }

  /**************************************************************
   * member methods
   **************************************************************/



  protected final void initForm(final Session theSession)
  {
    // do the parent first
    super.initForm(theSession);

    // now the mouse stuff
    // handle instances where MouseWheel support not available (JDK before 1.4)
    try
    {
      /////////////////////////////////////////////////
      // mouse-wheel support
      /////////////////////////////////////////////////
      setupMouseWheel();

    }
    catch (final java.lang.NoClassDefFoundError e)
    {
      System.out.println("MOUSE SUPPORT NOT AVAILABLE");
    }

  }

  public final void close()
  {
    // first unload the mouse
    if(_wheelListener != null)
    {
      try
      {
        closeMouseWheel();
      }
      catch (final java.lang.NoClassDefFoundError e)
      {
        System.out.println("Mouse support not available");
      }
    }

    // now let the parent close
    super.close();
  }

  /** remove the mouse wheel
   *
   */
  private void closeMouseWheel()
  {
    getChart().getPanel().removeMouseWheelListener((java.awt.event.MouseWheelListener) _wheelListener);
    getTote().getPanel().removeMouseWheelListener((java.awt.event.MouseWheelListener) _wheelListener);
    _wheelListener = null;
  }


  /** setup the mouse wheel handler part of it
   *
   */
  private void setupMouseWheel()
  {
      final java.awt.event.MouseWheelListener theWheelListener = new java.awt.event.MouseWheelListener()
      {
        public void mouseWheelMoved(final java.awt.event.MouseWheelEvent e)
        {

          // find out if we are doing our "special" processing
          if(e.getModifiers() == MouseEvent.CTRL_MASK)
          {
            // set the scale factor

            double scale = 1.1;
            // are we zooming in our out?
            if(e.getWheelRotation() > 0)
              scale = 1 / scale;

            // set busy
            getParent().setCursor(java.awt.Cursor.WAIT_CURSOR);

            // create our action
            final Action action = new MWC.GUI.Tools.Chart.ZoomOut.ZoomOutAction(getChart(), getChart().getCanvas().getProjection().getDataArea(), scale);

            // do the zoom
            action.execute();

            // remember this action
            getParent().addActionToBuffer(action);

            // and restore the cursor
            getParent().restoreCursor();
          }
          else
          {
            // we are moving forward/backward in time
            final int rot = e.getWheelRotation();
            final boolean fwd = (rot > 0);

            // are we doing a large step?
            final boolean large_step = (e.getModifiers() == MouseEvent.SHIFT_MASK);

            // do the step
            getTote().getStepper().doStep(fwd, large_step);
          }

        }
      };

      // listen to the time stepper
      getTote().getPanel().addMouseWheelListener(theWheelListener);

      // and listen to the plt
      getChart().getPanel().addMouseWheelListener(theWheelListener);

      // and save it
      _wheelListener = theWheelListener;
  }

}
