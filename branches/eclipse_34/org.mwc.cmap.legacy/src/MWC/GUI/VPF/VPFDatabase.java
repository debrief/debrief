package MWC.GUI.VPF;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: VPFDatabase.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: VPFDatabase.java,v $
// Revision 1.5  2006/05/17 08:44:08  Ian.Mayo
// Eclipse tidying
//
// Revision 1.4  2004/10/07 14:23:16  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.3  2004/09/03 15:13:29  Ian.Mayo
// Reflect refactored plottable getElements
//
// Revision 1.2  2004/05/25 15:37:27  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:49  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:03+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:59+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:56+01  ian
// end of 3d development
//
// Revision 1.1  2002-04-11 13:03:31+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-07-23 11:54:38+01  administrator
// handle problematic startups a bit more tidily
//
// Revision 1.0  2001-07-19 12:41:43+01  administrator
// Initial revision
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


import com.bbn.openmap.layer.vpf.LibrarySelectionTable;

import java.util.Enumeration;
import java.util.Iterator;


/**
 * Class to store and index a number of VPF databases,
 * such as the VMap Level 0 database and the DNC navigational
 * chart database.
 */

public class VPFDatabase extends MWC.GUI.BaseLayer implements MWC.GUI.Plottables.PlotMeFirst
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
   * the warehouse used to plot the data
   */
  DebriefFeatureWarehouse _myWarehouse = null;

  /**
   * Constructor, we look through the list of paths and
   * create a list of unique VPF databases, since a number of
   * paths may contain data from a single database.  We ensure
   * that only one unique Library Layer is created for each
   * database read in.
   *
   * @param paths        A list of paths which contain VPF data
   * @param autoPopulate Whether the layers we create should populate themselves by investigating their coverage information
   */
  public VPFDatabase(String[] paths, boolean autoPopulate)
  {
    // construct me
    super.setName("VPF");

    // create the warehouse
    _myWarehouse = new DebriefFeatureWarehouse();

    // create the LST
    //    _myLST = new LibrarySelectionTable();

    // a hashmap to store databases we know about
    java.util.HashMap _myDatabases = new java.util.HashMap();

    // add the paths
    for (int i = 0; i < paths.length; i++)
    {
      try
      {
        String thisPath = paths[i];

        // first let's check if this file exists
        java.io.File newFile = new java.io.File(thisPath);

        if (!newFile.exists())
        {
          // file not found, better report it!
          MWC.Utilities.Errors.Trace.trace("VPF Error: file not found:" + thisPath);
        }
        else
        {

          // we need to find out which database this is part of, so put it into an LST first
          LibrarySelectionTable tmpL = new LibrarySelectionTable(newFile);

          String thisName = tmpL.getDatabaseName().toUpperCase();

          LibrarySelectionTable thisLibrary = (LibrarySelectionTable) _myDatabases.get(thisName);

          // do we have it already?
          if (thisLibrary == null)
          {
            // no, we'll have to add it
            _myDatabases.put(thisName, tmpL);
          }
          else
          {
            // we have this database already, just add this path to it
            thisLibrary.addDataPath(new java.io.File(paths[i]));
          }
        }
      }
      catch (com.bbn.openmap.io.FormatException fe)
      {
        MWC.Utilities.Errors.Trace.trace(fe, "Add VPF data path:" + paths[i]);
      }
    }

    // so, we now have a list of library selection tables, one for each database we are handling

    // create a library for each library we know of
    Iterator enumer = _myDatabases.values().iterator();
    while (enumer.hasNext())
    {
      // get the table
      LibrarySelectionTable thisLib = (LibrarySelectionTable) enumer.next();

      // create the layer to manage this
      LibraryLayer layer = new LibraryLayer(thisLib, thisLib.getDatabaseName(), _myWarehouse, autoPopulate);


      // and remember it
      this.add(layer);

    }

  }

  /**
   * accessor to get the LST for the library in question
   */
  public LibrarySelectionTable getLST(String library)
  {
    LibrarySelectionTable res = null;
    LibraryLayer lib = this.getLibrary(library);
    if (lib != null)
      res = lib.getLST();
    return res;
  }

  /**
   * accessor to get the warehouse
   */
  public DebriefFeatureWarehouse getWarehouse()
  {
    return _myWarehouse;
  }

  /**
   * accessor to get a named library
   */
  public LibraryLayer getLibrary(String theName)
  {
    LibraryLayer res = null;
    Enumeration enumer = this.elements();
    while (enumer.hasMoreElements())
    {
      LibraryLayer ll = (LibraryLayer) enumer.nextElement();
      if (ll.getName().toUpperCase().equals(theName.toUpperCase()))
      {
        res = ll;
        break;
      }
    }

    return res;
  }

}
