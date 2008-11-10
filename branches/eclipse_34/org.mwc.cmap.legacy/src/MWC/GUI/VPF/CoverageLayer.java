package MWC.GUI.VPF;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CoverageLayer.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.6 $
// $Log: CoverageLayer.java,v $
// Revision 1.6  2006/05/17 08:43:07  Ian.Mayo
// Minor tidying
//
// Revision 1.5  2006/01/13 15:26:39  Ian.Mayo
// Eclipse tidying
//
// Revision 1.4  2004/10/07 14:23:15  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.3  2004/09/03 15:13:28  Ian.Mayo
// Reflect refactored plottable getElements
//
// Revision 1.2  2004/05/25 15:37:20  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:48  Ian.Mayo
// Initial import
//
// Revision 1.6  2003-03-17 09:30:23+00  ian_mayo
// Decide whether to plot feature depending on if it's in the visible data area, not just the selected data area
//
// Revision 1.5  2002-11-01 14:44:00+00  ian_mayo
// minor tidying
//
// Revision 1.4  2002-10-30 16:27:00+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.3  2002-07-12 15:46:04+01  ian_mayo
// Insert minor error trapping
//
// Revision 1.2  2002-05-28 09:26:04+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:59+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:29+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-07-18 16:01:33+01  administrator
// still plodding along
//
// Revision 1.1  2001-07-17 12:57:34+01  administrator
// simplify, remove need to store Canvas object
//
// Revision 1.0  2001-07-17 08:42:50+01  administrator
// Initial revision
//
// Revision 1.1  2001-07-16 14:59:19+01  novatech
// Initial revision
//


import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import com.bbn.openmap.layer.vpf.*;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;


public class CoverageLayer extends MWC.GUI.BaseLayer
{

  ///////////////////////////////////////////////////
  // member variables
  ///////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the library selection table we view
   */
  transient LibrarySelectionTable _myLST = null;

  /**
   * the graphic warehouse, which does the plotting for us
   */
  DebriefFeatureWarehouse _myWarehouse;

  /**
   * the type of coverage we are plotting
   */
  private String _myType = null;

  /**
   * the list of features which we pass to the painter
   */
  private Hashtable _myFeatureHash;



  ///////////////////////////////////////////////////
  // constructor
  ///////////////////////////////////////////////////

  public CoverageLayer(LibrarySelectionTable LST,
                       VPFGraphicWarehouse warehouse,
                       String coverageType)
  {
    _myType = coverageType;

    setVisible(false);

    // store the other data
    _myWarehouse = (DebriefFeatureWarehouse) warehouse;
    _myLST = LST;

    try
    {
      if (_myLST != null)
        setName(_myLST.getDescription(_myType));
    }
    catch (Exception fe)
    {
      fe.printStackTrace();
    }
  }


  public CoverageLayer(LibrarySelectionTable LST,
                       VPFGraphicWarehouse warehouse,
                       String coverageType,
                       CoverageAttributeTable cat)
  {

    this(LST, warehouse, coverageType);

    // we now pass through the coverages, adding a feature painter for each layer
    // now get the features inside this coverage
    CoverageTable ct = cat.getCoverageTable(_myType);

    // get the list of features in this coverage
    Hashtable hash = ct.getFeatureTypeInfo();

    try
    {
      Enumeration enumer = hash.keys();
      while (enumer.hasMoreElements())
      {
        String thisFeature = (String) enumer.nextElement();

        String description = _myLST.getDescription(thisFeature);
        FeaturePainter fp = new FeaturePainter(thisFeature, description);
        this.add(fp);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * get the coverage type
   */
  public String getType()
  {
    return _myType;
  }

  /**
   * set the library selection table, to allow deferred creation
   */
  public void setLST(LibrarySelectionTable lst)
  {
    _myLST = lst;
  }

  /**
   * set the description of this coverage
   */
  public void setDescription(String desc)
  {
    setName(desc);
  }

  /**
   * accessor to get the list of features we want to plot
   */
  protected Hashtable getFeatureHash()
  {
    if (_myFeatureHash == null)
    {
      _myFeatureHash = new Hashtable();
      Enumeration enumer = this.elements();
      while (enumer.hasMoreElements())
      {
        FeaturePainter fp = (FeaturePainter) enumer.nextElement();
        _myFeatureHash.put(fp.getFeatureType(), fp);
      }
    }

    return _myFeatureHash;
  }

  public void paint(CanvasType g)
  {

    // check we are visible
    if (!getVisible())
      return;

    if (_myLST == null)    	
    {
   		System.err.println("No LST for:" + _myType);
    }
    else
    {
      MWC.GenericData.WorldArea area = g.getProjection().getVisibleDataArea();

      // check that an area has been created
      if (area == null)
        return;

      // get the feature list
      Hashtable _myFeatures = getFeatureHash();

      // put our data into the warehouse
      _myWarehouse.setCurrentFeatures(_myFeatures);

      _myWarehouse.setCanvas(g);

      MWC.GenericData.WorldLocation tl = area.getTopLeft();
      MWC.GenericData.WorldLocation br = area.getBottomRight();

      com.bbn.openmap.LatLonPoint tlp = new com.bbn.openmap.LatLonPoint(tl.getLat(), tl.getLong());
      com.bbn.openmap.LatLonPoint brp = new com.bbn.openmap.LatLonPoint(br.getLat(), br.getLong());

      int data_wid = (int) MWC.Algorithms.Conversions.Degs2Yds(area.getWidth());

      java.awt.Dimension dim = g.getProjection().getScreenArea();

      // catch the occasional error we get when first paining a new layer
      try
      {
        // and start the repaint
        doDraw(_myLST, data_wid, dim.width, dim.height, _myType, _myWarehouse, tlp, brp);
      }
      catch (java.lang.NullPointerException ne)
      {
        MWC.Utilities.Errors.Trace.trace("Error painting VPF for first time", false);
      }
    }

  }


  /**
   * paint operation, used to hide whether this class does a drawTile, or a drawFeature
   */
  protected void doDraw(LibrarySelectionTable lst,
                        int scale,
                        int screenwidth,
                        int screenheight,
                        String covname,
                        VPFFeatureGraphicWarehouse warehouse,
                        com.bbn.openmap.LatLonPoint ll1, com.bbn.openmap.LatLonPoint ll2)
  {
    lst.drawFeatures(scale, screenwidth, screenheight, covname, warehouse, ll1, ll2);
  }


  /////////////////////////////////
  // a specific instance of coverage layer which plots data which has
  // been read from a reference directory
  /////////////////////////////////
  public static class ReferenceCoverageLayer extends CoverageLayer
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
     * the feature list we want to plot
     */
    FeaturePainter _myFeature;

    transient private Editable.EditorType _myEditor;

    /**
     * the name of this feature
     */
    private String _featureType;

    /**
     * whether to draw lines for this feature
     */
    private boolean _drawLine = true;

    /**
     * whether to draw text for this feature
     */
    private boolean _drawText = true;

    public ReferenceCoverageLayer(LibrarySelectionTable LST,
                                  VPFGraphicWarehouse warehouse,
                                  String coverageType,
                                  String featureType,
                                  String description,
                                  FeaturePainter theFeature)
    {
      super(LST, warehouse, coverageType);

      _myFeature = theFeature;
      _featureType = featureType;

      setName(description);
    }

    /**
     * accessor to get the list of features we want to plot
     */
    protected Hashtable getFeatureHash()
    {
      Hashtable res = new Hashtable();
      res.put(_featureType, _myFeature);
      return res;
    }

    public Editable.EditorType getInfo()
    {
      if (_myEditor == null)
        _myEditor = new FeaturePainterInfo(this);

      return _myEditor;
    }

    public java.awt.Color getColor()
    {
      return _myFeature.getColor();
    }

    public void setColor(java.awt.Color color)
    {
      _myFeature.setColor(color);
    }

    public boolean getDrawText()
    {
      return _drawText;
    }

    public void setDrawText(boolean val)
    {
      _drawText = val;
    }

    public boolean getDrawLine()
    {
      return _drawLine;
    }

    public void setDrawLine(boolean val)
    {
      _drawLine = val;
    }

    /**
     * paint operation, used to hide whether this class does a drawTile, or a drawFeature
     */
    protected void doDraw(LibrarySelectionTable lst,
                          int scale,
                          int screenwidth,
                          int screenheight,
                          String covname,
                          VPFFeatureGraphicWarehouse warehouse,
                          com.bbn.openmap.LatLonPoint ll1, com.bbn.openmap.LatLonPoint ll2)
    {
      // set the painting characteristics in the painting warehouse
      super._myWarehouse.setCoastlinePainting(new Boolean(_drawText), new Boolean(_drawLine));

      // draw tile by tile
      lst.drawTile(scale, screenwidth, screenheight, covname, warehouse, ll1, ll2);

      // clear the painting characteristics in the painting warehouse
      super._myWarehouse.setCoastlinePainting(null, null);

    }

    //
    public class FeaturePainterInfo extends Editable.EditorType implements Serializable
    {

      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public FeaturePainterInfo(ReferenceCoverageLayer data)
      {
        super(data, data.getName(), "");
      }

      public PropertyDescriptor[] getPropertyDescriptors()
      {
        try
        {
          PropertyDescriptor[] res = {
            prop("Color", "the Color to draw this Feature"),
            prop("Visible", "whether this feature is visible"),
            prop("DrawText", "whether to draw text"),
            prop("DrawLine", "whether to paint lines"),
          };

          return res;
        }
        catch (IntrospectionException e)
        {
          return super.getPropertyDescriptors();
        }
      }
    }

  }

}




