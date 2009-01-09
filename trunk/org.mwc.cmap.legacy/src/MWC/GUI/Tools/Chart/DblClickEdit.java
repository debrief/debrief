package MWC.GUI.Tools.Chart;


// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DblClickEdit.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.9 $
// $Log: DblClickEdit.java,v $
// Revision 1.9  2006/08/10 13:52:07  Ian.Mayo
// Tidying, don't store the layers - use it fresh each time.
//
// Revision 1.8  2005/05/25 08:38:51  Ian.Mayo
// Minor tidying from Eclipse
//
// Revision 1.7  2005/05/24 13:23:33  Ian.Mayo
// Make it more versatile all round...
//
// Revision 1.6  2005/05/24 13:16:12  Ian.Mayo
// Minor refactoring to make it easier to override
//
// Revision 1.5  2004/10/07 14:23:13  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.4  2004/09/03 15:13:24  Ian.Mayo
// Reflect refactored plottable getElements
//
// Revision 1.3  2004/08/31 08:05:06  Ian.Mayo
// Rename/remove old tests, so that we don't have non-testing classes whose named ends with Test (in support of Maven integration)
//
// Revision 1.2  2004/05/25 15:43:42  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:42  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:00+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:41+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-24 14:22:31+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.0  2001-07-17 08:42:55+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:47+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:14  ianmayo
// initial version
//
// Revision 1.4  2000-11-02 16:44:36+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer
//
// Revision 1.3  2000-02-03 15:08:18+00  ian_mayo
// First issue to Devron (modified files are mostly related to WMF)
//
// Revision 1.2  1999-11-11 18:18:23+00  ian_mayo
// minor tidying up
//
// Revision 1.1  1999-10-12 15:36:18+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-04 09:43:07+01  administrator
// make tools serializable
//
// Revision 1.1  1999-07-27 10:59:45+01  administrator
// Initial revision
//
// Revision 1.3  1999-07-27 09:27:02+01  administrator
// tidying up use of tools
//
// Revision 1.2  1999-07-16 10:01:48+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-12 08:09:28+01  administrator
// Initial revision
//

import java.awt.Point;
import java.io.Serializable;
import java.util.Enumeration;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Layer.ProvidesContiguousElements;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GenericData.WorldLocation;

public class DblClickEdit implements PlainChart.ChartDoubleClickListener, PlainChart.ChartClickListener, Serializable
{
  /**
	 * 
	 */
	private  static final long serialVersionUID = 1L;
	
	/////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  PropertiesPanel _thePanel;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public DblClickEdit(PropertiesPanel thePanel)
  {
    _thePanel = thePanel;
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public void cursorDblClicked(PlainChart theChart,
                               MWC.GenericData.WorldLocation theLocation,
                               java.awt.Point thePoint)
  {
  	this.CursorClicked(thePoint, theLocation, theChart.getCanvas(), theChart.getLayers());
  }
  
  /** open the provided item in the editor
   * 
   * @param res the item to show
   * @param e the editor details for the item
   * @param parentLayer the layer containing this item
   */
  protected void addEditor(Plottable res, Editable.EditorType e, Layer parentLayer)
  {
  	_thePanel.addEditor(e, parentLayer);
  }
  
  /** we haven't found anything. it's probably not worth bothering with
   * 
   * @param projection
   */
  protected void handleItemNotFound(PlainProjection projection)
  {
  	addEditor(null, projection.getInfo(), null);
  }

  /** process the cursor-click operation.
   * @param thePoint screen coordinates of click
   * @param thePos world coordinates of click
   * @param theCanvas what got clicked upon
   * @param theData the information we're storing
   */
	public void CursorClicked(Point thePoint, WorldLocation thePos, CanvasType theCanvas, Layers theData)
	{

    //
    Plottable res = null;
    double dist = 0;
    Layer closestLayer = null;

    // find the nearest editable item
    int num = theData.size();
    for (int i = 0; i < num; i++)
    {
      Layer thisL = theData.elementAt(i);
      if (thisL.getVisible())
      {
        // go through this layer
        Enumeration<Editable> enumer = null;
        if(thisL instanceof Layer.ProvidesContiguousElements)
        {
        	Layer.ProvidesContiguousElements contig = (ProvidesContiguousElements) thisL;
        	enumer = contig.contiguousElements();
        }
        else
        	enumer = thisL.elements();
        
        while (enumer.hasMoreElements())
        {
          Plottable p = (Plottable) enumer.nextElement();
          if (p.getVisible())
          {
            // how far away is it
            double rng = p.rangeFrom(thePos);

            // is it null though?
            if (rng != -1.0)
            {
              // is it closer?
              if (res == null)
              {
                res = p;
                dist = rng;
                closestLayer = thisL;
              }
              else
              {
                if (rng < dist)
                {
                  res = p;
                  dist = rng;
                  closestLayer = thisL;
                }
              }
            }
          }
        }
      }
    }

    // see if this is in our dbl-click range
    if (HitTester.doesHit(thePoint,
    		thePos,
                          dist,
                          theCanvas.getProjection()))
    {
      // do nothing, we're all happy
    }
    else
    {
      res = null;
    }

    // have we found something editable?
    if (res != null)
    {
      // so get the editor
      Editable.EditorType e = res.getInfo();
      if (e != null)
      {
        addEditor(res, e, closestLayer);
      }
    }
    else
    {
    	
      // not found anything useful,
      // so add
    	handleItemNotFound(theCanvas.getProjection());
    }
		
	}

}
