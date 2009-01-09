// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingSession.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: SwingSession.java,v $
// Revision 1.3  2005/12/13 09:04:21  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/08/09 09:38:55  Ian.Mayo
// Lots of Idea tidying, tidy up assigning session filename
//
// Revision 1.1.1.2  2003/07/21 14:47:10  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.5  2003-07-01 16:31:20+01  ian_mayo
// Useful error on failing to load mouse libraries (JDK is prior to 1.4)
//
// Revision 1.4  2003-03-19 15:37:53+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.3  2003-01-24 11:56:05+00  ian_mayo
// Add jdk1.3 mouse wheel support (jmousewheel)
//
// Revision 1.2  2002-05-28 12:27:50+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:19+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:30:08+01  ian_mayo
// Initial revision
//
// Revision 1.5  2002-02-18 20:15:16+00  administrator
// try to do mouse support first, then drop back to old JDK1.3 processing
//
// Revision 1.4  2002-01-22 15:27:00+00  administrator
// Learn name earlier, and implement ToolBarOwner interface
//
// Revision 1.3  2001-11-26 14:26:15+00  administrator
// When users uses window decoration to close session, we want to stop the event completing, since it is handled separately by our window-close code.
//
// Revision 1.2  2001-08-17 08:02:07+01  administrator
// Clear up memory leaks
//
// Revision 1.1  2001-07-17 16:22:39+01  administrator
// We now listen to the window closing event, and ask the user if he is sure he wants to save
//
// Revision 1.0  2001-07-17 08:41:41+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:55+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:15  ianmayo
// initial import of files
//
// Revision 1.13  2000-11-23 14:52:38+00  ian_mayo
// set the name of the session when we create it
//
// Revision 1.12  2000-10-10 12:20:09+01  ian_mayo
// <>
//
// Revision 1.11  2000-10-09 13:37:43+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.10  2000-09-27 15:39:37+01  ian_mayo
// provide support for canClose()
//
// Revision 1.9  2000-08-07 14:04:12+01  ian_mayo
// removed d-lines
//
// Revision 1.8  2000-07-05 16:37:13+01  ian_mayo
// white space only
//
// Revision 1.7  2000-05-23 13:40:13+01  ian_mayo
// override "setFileName" method so that we can put the name in the titlebar of the frame
//
// Revision 1.6  2000-04-19 11:30:20+01  ian_mayo
// implement Close method, clear local storage
//
// Revision 1.5  2000-03-08 14:26:28+00  ian_mayo
// tidying up
//
// Revision 1.4  2000-02-22 13:45:52+00  ian_mayo
// white space
//
// Revision 1.3  2000-01-20 10:07:06+00  ian_mayo
// added application-wide clipboard
//
// Revision 1.2  1999-11-25 16:54:55+00  ian_mayo
// implementing Swing components
//
// Revision 1.1  1999-11-18 11:12:22+00  ian_mayo
// new Swing versions
//


package Debrief.GUI.Frames.Swing;

import Debrief.GUI.Frames.Session;
import Debrief.GUI.Views.AnalysisView;
import Debrief.GUI.Views.Swing.SwingAnalysisView;
import Debrief.GUI.Views.Swing.SwingMouseAnalysisView;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI;
import MWC.GenericData.WorldArea;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Swing
 */
public final class SwingSession extends Session implements Serializable, MyMetalToolBarUI.ToolbarOwner
{

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  transient ToolParent _theParent;

  transient private javax.swing.JInternalFrame _thePanel;
  transient private SwingAnalysisView _theView;
  transient private WorldArea _initialArea;

  static final long serialVersionUID = -6899302110582614129L;

  transient private java.beans.VetoableChangeListener _frameListener;

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////


  /**
   * create the first Swing View, set to the view in the parent
   */
  public SwingSession(final ToolParent theParent,
                      final java.awt.datatransfer.Clipboard theClipboard,
                      final String newName)
  {
    super(theClipboard);

    super.setName(newName);

    _theParent = theParent;

    initialiseForm(theParent);

    _thePanel.setVisible(true);

    // take a copy of this session, which we can use in our frame close listener
    final SwingSession thisSession = this;

    // create the listener (we will remember this, so we can remove it later)
    _frameListener = new java.beans.VetoableChangeListener()
    {
      public void vetoableChange(final java.beans.PropertyChangeEvent evt)
        throws java.beans.PropertyVetoException
      {
        // find out which event we are receiving
        final String name = evt.getPropertyName();

        if (name.equals(javax.swing.JInternalFrame.IS_CLOSED_PROPERTY))
        {
          final Boolean oldValue = (Boolean) evt.getOldValue();
          final Boolean newValue = (Boolean) evt.getNewValue();

          if (oldValue == Boolean.FALSE && newValue == Boolean.TRUE)
          {

            // get the application to handle the close
            if (_theParent instanceof Debrief.GUI.Frames.Application)
            {
              final Debrief.GUI.Frames.Application parent = (Debrief.GUI.Frames.Application) _theParent;
              parent.closeSession(thisSession);

              // throw the exception anyway, we've handled it in the "closeSession" method
              throw new java.beans.PropertyVetoException("Handled elsewhere", evt);
            }
          }
        }

      }
    };

    // add the listener to the panel
    _thePanel.addVetoableChangeListener(_frameListener);


  }


  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public final void initialiseForm(final ToolParent theParent)
  {
    // SPECIAL PROCESSING, open up a mouse-less view if we are running jdk1.3
    try
    {
      _theView = new SwingMouseAnalysisView(_theParent, this);
    }
    catch (java.lang.NoClassDefFoundError er)
    {
      MWC.Utilities.Errors.Trace.trace(er, "Failed to load mouse-wheel libraries.  Will continue");
      _theView = new SwingAnalysisView(_theParent, this);
    }

    _theParent = theParent;
    _thePanel = new SessionJInternalFrame(super.getName(),
                                          true,
                                          true,
                                          true,
                                          true,
                                          _theView);
    _thePanel.getContentPane().setLayout(new BorderLayout());
    _thePanel.setSize(900, 400);
    _thePanel.setName(getName());
    _thePanel.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

    // set the view in the parent
    addView(_theView);

    _thePanel.getContentPane().add("Center", _theView.getPanel());

    _thePanel.doLayout();


    if (_initialArea != null)
    {
      // restore the data area
      _theView.getChart().getCanvas().getProjection().setDataArea(_initialArea);
      _theView.getChart().getCanvas().getProjection().zoom(0.0);
    }

    // and do an update
    _theView.getChart().update();

  }

  public final void closeGUI()
  {
    _theParent = null;
    _initialArea = null;

    // note that the Session bit clears all of the
    // views itself
    _theView = null;

    // stop listening for a frame to close
    _thePanel.removeVetoableChangeListener(_frameListener);
    _frameListener = null;

    // clear all of our references
    _thePanel.setVisible(false);
    _thePanel.removeAll();
    _thePanel.dispose();
    _thePanel = null;

  }


  /**
   * @return the Panel we are using for this session
   */
  public final JInternalFrame getPanel()
  {
    return _thePanel;
  }

  /**
   * repaint the current view
   */
  public final void repaint()
  {
    _thePanel.repaint();
    super.getCurrentView().update();
  }

  /**
   * @param theName is the string used to name
   *                this session
   */
  protected final void setName(final String theName)
  {
    super.setName(theName);

    // now give the panel the same name
    _thePanel.setName(getName());
    _thePanel.setTitle(getName());
  }

  protected final void finalize()
  {
    try
    {
      super.finalize();
    }
    catch (Throwable e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }
  }

  @SuppressWarnings("deprecation")
	protected final boolean wantsToClose()
  {
    // try to do a file save - ask the user
    final JPanel jp = new JPanel();
    final JOptionPane pane = new JOptionPane("Session has not been saved. Do you wish to close?",
                                             JOptionPane.QUESTION_MESSAGE,
                                             JOptionPane.YES_NO_OPTION);
    final JDialog dialog = pane.createDialog(jp, "Close Session");
    dialog.show();
    final Integer value = (Integer) pane.getValue();
    return (value.intValue() == JOptionPane.YES_OPTION);
  }

  /**
   * *******************************************************************
   * embedded class which adds a session pointer to a desktop frame.
   * - partially so we can get the session back when we're implementing
   * the third party mouse scroller for JDK1.3
   * *******************************************************************
   */
  public static final class SessionJInternalFrame extends JInternalFrame
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
     * the view we're looking at
     */
    final private AnalysisView theView;

    /**
     * Creates a <code>JInternalFrame</code> with the specified title and
     * with resizability, closability, maximizability, and iconifiability
     * specified.  All constructors defer to this one.
     *
     * @param title       the <code>String</code> to display in the title bar
     * @param resizable   if true, the frame can be resized
     * @param closable    if true, the frame can be closed
     * @param maximizable if true, the frame can be maximized
     * @param iconifiable if true, the frame can be iconified
     */
    public SessionJInternalFrame(final String title,
                                 final boolean resizable,
                                 final boolean closable,
                                 final boolean maximizable,
                                 final boolean iconifiable,
                                 final AnalysisView theView)
    {
      super(title, resizable, closable, maximizable, iconifiable);
      this.theView = theView;
    }

    /**
     * retrive the view which this frame is looking at
     *
     * @return the current view
     */

    public final AnalysisView getTheView()
    {
      return theView;
    }

  }

}
