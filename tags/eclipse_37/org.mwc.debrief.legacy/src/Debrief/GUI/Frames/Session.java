// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Session.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: Session.java,v $
// Revision 1.3  2005/09/08 08:57:23  Ian.Mayo
// Refactor name of chart features layer
//
// Revision 1.2  2004/08/09 09:36:30  Ian.Mayo
// Try to tidy up assigning filename
//
// Revision 1.1.1.2  2003/07/21 14:47:05  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:38:01+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:27:48+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:21+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:30:12+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-02-18 20:13:59+00  administrator
// Handle instance where things didn't work out, and session wasn't created
//
// Revision 1.3  2002-01-29 07:52:39+00  administrator
// Tell just the chart features layer to double-buffer itself
//
// Revision 1.2  2001-08-31 09:55:41+01  administrator
// Shift the order of closing, to overcome a bug
//
// Revision 1.1  2001-08-17 08:01:48+01  administrator
// Clear up memory leaks
//
// Revision 1.0  2001-07-17 08:41:43+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:56+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:44:59  ianmayo
// initial import of files
//
// Revision 1.12  2000-11-24 11:49:54+00  ian_mayo
// remove commented out lines
//
// Revision 1.11  2000-10-09 13:37:48+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.10  2000-09-27 15:39:05+01  ian_mayo
// listen to mods to UndoBuffer, and ask user if he want to do save if buffer is dirty
//
// Revision 1.9  2000-08-07 14:04:27+01  ian_mayo
// removed d-lines
//
// Revision 1.8  2000-05-23 13:39:46+01  ian_mayo
// set the name when we load a new file
//
// Revision 1.7  2000-04-19 11:30:08+01  ian_mayo
// implement Close method, clear local storage
//
// Revision 1.6  2000-04-03 14:06:17+01  ian_mayo
// add transient filename parameter, plus the SerialVersionID
//
// Revision 1.5  2000-02-15 15:52:28+00  ian_mayo
// Initialise clipboard when opening existing session
//
// Revision 1.4  2000-01-20 10:07:07+00  ian_mayo
// added application-wide clipboard
//
// Revision 1.3  2000-01-18 15:04:59+00  ian_mayo
// changed Decorations to Chart Features
//
// Revision 1.2  1999-12-03 14:39:53+00  ian_mayo
// removed default coast and grid painters
//
// Revision 1.1  1999-10-12 15:34:27+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-07-27 09:27:47+01  administrator
// general improvements
//
// Revision 1.2  1999-07-16 10:01:51+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:20+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:10+01  sm11td
// Initial revision
//
// Revision 1.4  1999-02-04 08:02:22+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.3  1999-02-01 16:08:45+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.2  1999-02-01 14:25:00+00  sm11td
// Skeleton there, opening new sessions, window management.
//
// Revision 1.1  1999-01-31 13:32:58+00  sm11td
// Initial revision
//

package Debrief.GUI.Frames;

import Debrief.GUI.Views.PlainView;
import Debrief.Tools.Operations.NewSession;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Undo.UndoBuffer;

import java.io.Serializable;
import java.util.Observer;

abstract public class Session implements Serializable, Observer
{

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /**
   * our version id
   */
  static final long serialVersionUID = 6064926140905655210L;

  /**
   * the name of this session
   */
  private String _theName;

  /**
   * the data contained in this session
   */
  private Layers _theData;

  /**
   * the list of views currently placed on this data
   */
  transient private java.util.Vector<PlainView> _theViews;

  /**
   * our undo buffer
   */
  private transient UndoBuffer _theBuffer;

  /**
   * whether this session has been modified since the last change
   */
  private transient boolean _modified = false;

  /**
   * the current clipboard
   */
  private transient java.awt.datatransfer.Clipboard _theClipboard;

  /**
   * the filename we have been stored as
   * (null to start with)
   */
  transient private String _fileName = null;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  public Session(final java.awt.datatransfer.Clipboard theClipboard)
  {
    _theData = new Layers();


    // @@ IM HACK: ignore theClipboard parameter - we do not use unique clipboards,
    // we use clipboards of a specified name so that they are valid between
    // sessions (we don't want to have to store unique clipboards, so that the
    // data may be copied between sessions
    _theClipboard = theClipboard;

    // create decorations layer
    Layer dec = _theData.findLayer(Layers.CHART_FEATURES);
    if (dec == null)
    {
      dec = _theData.cleanLayer();
      dec.setName(Layers.CHART_FEATURES);
      _theData.addThisLayer(dec);
    }

    // what-ever happened, tell the Chart Features layer to double-buffer itself
    if (dec instanceof BaseLayer)
    {
      final BaseLayer bl = (BaseLayer) dec;
      bl.setBuffered(true);
    }


    initData();
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////



  /**
   * initialise the data which has to be initialised whether we
   * are a fresh session or not
   */
  private void initData()
  {
    _theViews = new java.util.Vector<PlainView>(0, 1);
    _theBuffer = new UndoBuffer();

    if (_theClipboard == null)
    {
      System.out.println("creating supplemental clipboard");
      _theClipboard = new java.awt.datatransfer.Clipboard("Debrief");
    }

    // set ourselves as a listener to the buffer, so that we can keep track
    // of how it runs
    _theBuffer.addObserver(this);
  }

  /**
   * get the current clipboard
   */
  public final java.awt.datatransfer.Clipboard getClipboard()
  {
    return _theClipboard;
  }

  /**
   * Set the name of this session
   *
   * @param theName is a String representing the name of the session
   */
  protected void setName(final String theName)
  {
    _theName = theName;
  }

  /**
   * set the filename of this session
   */
  public void setFileName(final String theName)
  {

    // this means we are currently in a save operation
    _modified = false;

    // store the filename
    _fileName = theName;

    // and use it as the name
    if (theName.equals(NewSession.DEFAULT_NAME))
      setName(theName);

  }

  /**
   * get the filename of this session (or null if it hasn't been saved yet)
   */
  public final String getFileName()
  {
    return _fileName;
  }


  /**
   * Get the current data for this session
   *
   * @return the data being used by the session
   */
  public final Layers getData()
  {
    return _theData;
  }

  /**
   * Add a view to this session
   *
   * @param theView is the view to add to the session
   */
  protected final void addView(final PlainView theView)
  {
    //
    _theViews.addElement(theView);
  }

  /**
   * @return the view currently being looked at.
   */
  public final PlainView getCurrentView()
  {
    PlainView res = null;

    if (_theViews.size() == 0)
      res = null;
    else
      res = (PlainView) _theViews.elementAt(0);

    // see which view is currently on top
    return res;
  }

  /**
   * @return the name of the current session
   */
  public final String getName()
  {
    return _theName;
  }

  abstract protected boolean wantsToClose();

  /**
   * close this session, inviting user to save as necessary.
   *
   * @return flag specifying whether close was successful
   *         (since user may have decided to cancel the file save)
   */
  public final boolean close()
  {

    // see if we need to save data
    if (isDirty())
    {
      // try to do a file save - ask the user
      if (wantsToClose())
      {
        // user still want to close
      }
      else
      {
        // user doesn't want to close anymore, drop out
        return false;
      }

    }

    // delete the local variables

    _theBuffer.close();
    _theBuffer = null;

    _theClipboard = null;
    _fileName = null;
    _theName = null;

    if (_theViews != null)
    {
      if (_theViews.size() == 1)
      {
        final PlainView theV = (PlainView) _theViews.elementAt(0);
        theV.close();
        _theViews.removeElement(theV);
      }
      _theViews = null;
    }

    // and the layers
    _theData.close();
    _theData = null;

    // now the GUI stuff
    //	closeGUI();

    // set the stuff we don;t want to null
    return true;
  }

  abstract public void closeGUI();

  /**
   * see if we are 'dirty'
   */
  private boolean isDirty()
  {
    //
    return _modified;
  }

  /**
   * cause redraw
   */
  abstract public void repaint();


  public final UndoBuffer getUndoBuffer()
  {
    return _theBuffer;
  }

  abstract public void initialiseForm(ToolParent theParent);

  protected void finalize()
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

  /**
   * listen out for changes to the undo buffer
   *
   * @param p1 the item which has been changed
   * @param p2 the parameter it has sent to us
   */
  public final void update(final java.util.Observable p1, final java.lang.Object p2)
  {
    // check it actually was the buffer which changed
    if (p1.equals(_theBuffer))
      _modified = true;
  }

}


