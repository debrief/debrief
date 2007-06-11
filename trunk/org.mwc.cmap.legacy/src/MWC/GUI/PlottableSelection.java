// Copyright MWC 2000, Debrief 3 Project
// $RCSfile: PlottableSelection.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: PlottableSelection.java,v $
// Revision 1.3  2004/09/06 14:04:42  Ian.Mayo
// Switch to supporting editables in Layer Manager, and showing icon for any editables which have one
//
// Revision 1.2  2004/05/25 15:45:47  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:04  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:05+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:34+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:14+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:31+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:37+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:09+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:00  ianmayo
// initial version
//
// Revision 1.2  2000-10-09 13:35:51+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.1  2000-09-26 10:51:34+01  ian_mayo
// Initial revision
//
package MWC.GUI;

import java.awt.datatransfer.*;

/**
 * definition of class to handle copying via clipboard
 */
public class PlottableSelection implements Transferable, ClipboardOwner
{
  ////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  public static DataFlavor PlottableFlavor;
  private DataFlavor[] _flavors = {PlottableFlavor};
  private Editable _thePlottable = null;
  private boolean _isCopy;

  static
  {
    try
    {
      PlottableFlavor = new DataFlavor(Class.forName("MWC.GUI.Plottable"),
                                       "Plottable");
    }
    catch (Exception e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }
  }

  ////////////////////////////////////////////////////////////
  // constructor, receives a plottable
  ////////////////////////////////////////////////////////////
  public PlottableSelection(Editable val,
                            boolean isCopy)
  {
    _thePlottable = val;
    _isCopy = isCopy;
  }

  ////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public boolean isACopy()
  {
    return _isCopy;
  }

  // return the set of flavours supported by this selection
  public DataFlavor[] getTransferDataFlavors()
  {
    return _flavors;
  }

  // check if this is supported
  public boolean isDataFlavorSupported(DataFlavor p1)
  {
    return p1.equals(PlottableFlavor);
  }

  // retrieve the data
  public Object getTransferData(DataFlavor p1) throws UnsupportedFlavorException
  {
    if (p1.equals(PlottableFlavor))
    {
      return _thePlottable;
    }
    else
      throw new UnsupportedFlavorException(p1);
  }

  // don't bother!
  public void lostOwnership(Clipboard p1, Transferable p2)
  {
  }
}
	
 