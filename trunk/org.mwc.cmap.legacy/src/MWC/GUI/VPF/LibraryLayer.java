
/**
 * Layer which handles a particular VPF database - this may contain data
 * which has come from a number of different CD's or directories
 * but the LibrarySelectionTable object is able to manage retrieving
 * information across them.
 */package MWC.GUI.VPF;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: LibraryLayer.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: LibraryLayer.java,v $
// Revision 1.4  2006/05/17 08:43:40  Ian.Mayo
// Eclipse tidying
//
// Revision 1.3  2006/01/13 15:27:27  Ian.Mayo
// Eclipse tidying, minor mods to improve serialization
//
// Revision 1.2  2004/05/25 15:37:25  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:48  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:03+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:59+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:30+01  ian_mayo
// Initial revision
//
// Revision 1.5  2001-07-23 11:55:21+01  administrator
// Take out duff code, improve comments
//
// Revision 1.4  2001-07-20 09:28:00+01  administrator
// Corrections
//
// Revision 1.3  2001-07-19 12:41:02+01  administrator
// tidying up, and moving some code to VPFDatabase class
//
// Revision 1.2  2001-07-18 16:01:33+01  administrator
// still plodding along
//
// Revision 1.1  2001-07-17 12:57:06+01  administrator
// lots of tidying up
//
// Revision 1.0  2001-07-17 08:42:49+01  administrator
// Initial revision
//
// Revision 1.1  2001-07-16 14:59:15+01  novatech
// Initial revision
//


import MWC.GUI.CanvasType;

import com.bbn.openmap.layer.vpf.CoverageAttributeTable;
import com.bbn.openmap.layer.vpf.LibrarySelectionTable;

public class LibraryLayer extends MWC.GUI.BaseLayer
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//////////////////////////////////////
  // member variables
  /**
 * /////////////////////////////////////
 */
  transient LibrarySelectionTable _myLST = null;
  /**
   * DebriefFeatureWarehouse _myWarehouse
   */
  DebriefFeatureWarehouse _myWarehouse = null;
  /**
   * CoverageAttributeTable _myCat
   */
  CoverageAttributeTable _myCat = null;

  //////////////////////////////////////
  // constructor
  /**
 * /////////////////////////////////////
 *
 */

  public LibraryLayer(LibrarySelectionTable lst, String name, DebriefFeatureWarehouse warehouse, boolean autoPopulate)
  {

    _myWarehouse = warehouse;
    _myLST = lst;
    super.setName(name);
    setVisible(false);

    try
    {

      // create the tree of coverages
      CoverageAttributeTable _myCat1 = _myLST.getCAT(name);

      // did we get a cat?
      if(_myCat1 == null)
      {
        // oh, well, let's find the first non-reference library
        String[] libs = _myLST.getLibraryNames();
        for(int i=0; i<libs.length;i++)
        {
          String thisName = libs[i];
          if(thisName.toLowerCase().equals("rference"))
          {
            // just ignore it, man
          }
          else
          {
            _myCat1 = _myLST.getCAT(thisName);
            break;
          }
        }

      }

      // do we want to populate the library with all of the available data?
      if(autoPopulate)
      {
        // did we get a cat?
        if(_myCat1 == null)
        	return;

      	
        // get the list of coverages in this library
        String[] coverages = _myCat1.getCoverageNames();

        // step through the coverages
        for(int i=0;i<coverages.length;i++)
        {
          String thisCov = (String)coverages[i];
          CoverageLayer cl = new CoverageLayer(_myLST, _myWarehouse, thisCov, _myCat1);
          this.add(cl);
        }
      }
    }
    catch(com.bbn.openmap.io.FormatException fe)
    {
      fe.printStackTrace();
    }
  }

  //////////////////////////////////////
  // member methods
  /////////////////////////////////////

  /**
   * set the name of this library - this tells us which library to read from disk
   *
   */
  public void setName(String val)
  {
    // pass the name to the parent
    super.setName(val);

    // and initialise our data accordingly
  }

  /**
   * static method which returns an single painter which paints the libref feature set
   * @param path the location of the VMap data providing the reference coastline
   */
  public static CoverageLayer.ReferenceCoverageLayer createReferenceLayer(String path)
  {
    // return value
    CoverageLayer.ReferenceCoverageLayer rcl = null;
    try
    {
      LibrarySelectionTable LST = new LibrarySelectionTable(path);
      DebriefFeatureWarehouse myWarehouse = new DebriefFeatureWarehouse();
      FeaturePainter fp = new FeaturePainter("libref","Coastline");
      fp.setVisible(true);
      rcl = new CoverageLayer.ReferenceCoverageLayer(LST, myWarehouse, "libref", "libref", "Coastline", fp);
      rcl.setVisible(true);
    }
    catch(com.bbn.openmap.io.FormatException ex)
    {
      ex.printStackTrace();
    }
    return rcl;
  }

  /**
   * getWarehouse
   *
   * @return the returned DebriefFeatureWarehouse
   */
  public DebriefFeatureWarehouse getWarehouse()
  {
    return _myWarehouse;
  }

  /**
   * getLST
   *
   * @return the returned LibrarySelectionTable
   */
  public LibrarySelectionTable getLST()
  {
    return _myLST;
  }

  /**
   * getCAT
   *
   * @return the returned CoverageAttributeTable
   */
  public CoverageAttributeTable getCAT()
  {
    return _myCat;
  }

  /**
   * paint
   *
   * @param g parameter for paint
   */
  public void paint(CanvasType g)
  {
    if(!getVisible())
      return;
    
    float oldWid = g.getLineWidth();

    g.setLineWidth(this.getLineThickness());
    
    // store this canvas in the warehouse, so that it knows where it's plotting to
    _myWarehouse.setCanvas(g);

    DebriefFeatureWarehouse.counter = 0;

 //   long l = System.currentTimeMillis();

    // let the Plottables handle the plotting
    super.paint(g);

    // work out how long it took
 //   System.out.println("time:" + (System.currentTimeMillis() - l) + ", ct:" + DebriefFeatureWarehouse.counter);

    g.setLineWidth(oldWid);
    
  }

}



