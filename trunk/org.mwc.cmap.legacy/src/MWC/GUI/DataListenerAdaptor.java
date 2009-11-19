// Copyright MWC 1999
// $RCSfile: DataListenerAdaptor.java,v $
// $Author: Ian.Mayo $
// $Log: DataListenerAdaptor.java,v $
// Revision 1.4  2006/06/12 09:18:12  Ian.Mayo
// New data-extended message
//
// Revision 1.3  2004/10/21 15:30:11  Ian.Mayo
// Add support for overriding data updates
//
// Revision 1.2  2004/05/25 15:45:26  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:03  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:36+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:25+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-24 14:22:35+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.0  2001-07-17 08:46:34+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:05+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:45  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:37:06+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:49+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:08+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:00+01  sm11td
// Initial revision
//
// Revision 1.1  1999-02-04 08:02:33+00  sm11td
// Initial revision
//

package MWC.GUI;


/**
 * this class provides a no-op default instantiation of
 * a data listener, it is to be extended by any concrete
 * class expecting to handle data listener messages
 */

public abstract class DataListenerAdaptor implements  Layers.DataListener2
{


  /**
   * flag to indicate that the Layers object should suspend updates, just for a while
   */
  protected boolean _suspendUpdates = false;

  public void dataModified(Layers theData, Layer changedLayer)
  {
    // do nothing
  }

  public void dataExtended(Layers theData)
  {
    // do nothing
  }

  public void dataReformatted(Layers theData, Layer changedLayer)
  {
    // do nothing
  }

  public void setSuspendUpdates(boolean updates)
  {
    _suspendUpdates = updates;

    if(updates)
    {
      // we're just being switched back on, trigger a full repaint
      dataExtended(null);
    }
  }

	public void dataExtended(Layers theData, Plottable newItem, Layer parent)
	{
		// do nothing
	}
}
