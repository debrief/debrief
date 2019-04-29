/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
// $RCSfile: CreateVPFLayers.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: CreateVPFLayers.java,v $
// Revision 1.3  2005/06/08 15:16:36  Ian.Mayo
// Provide support for specify VPF directory paths from Eclipse-Debrief
//
// Revision 1.2  2004/05/25 15:44:26  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:46  Ian.Mayo
// Initial import
//
// Revision 1.3  2002-05-29 10:06:22+01  ian_mayo
// Tidy label
//
// Revision 1.2  2002-05-28 09:26:01+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:04+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:36+01  ian_mayo
// Initial revision
//
// Revision 1.6  2001-10-22 11:27:33+01  administrator
// Change name of default directory for VPF layers
//
// Revision 1.5  2001-09-25 16:33:55+01  administrator
// Provide convenience method
//
// Revision 1.4  2001-08-23 13:27:56+01  administrator
// Reflect new signature for PlainCreate class, to allow it to fireExtended()
//
// Revision 1.3  2001-07-19 12:41:23+01  administrator
// reflect fact that we now use VPFDatabase class
//
// Revision 1.2  2001-07-18 16:01:33+01  administrator
// still plodding along
//
// Revision 1.1  2001-07-17 12:56:47+01  administrator
// make the property name public, so that it can be re-used during the XML Import process
//
// Revision 1.0  2001-07-17 08:42:53+01  administrator
// Initial revision
//
// Revision 1.1  2001-07-16 15:38:33+01  novatech
// Initial revision
//

package MWC.GUI.Tools.Palette;

import MWC.GUI.ToolParent;
import MWC.GUI.VPF.VPFDatabase;

public class CreateVPFLayers extends PlainCreate
{

  private static MWC.GUI.ToolParent _myParent = null;

  /** the property name used to store the vpf coastline
   *
   */
  public static final String VPF_DATABASE_PROPERTY = "VPF_DATABASE_DIR";

  /** the default path we use for the data path
   *
   */
//  public static final String VPF_PATH_DEFAULT = "f://dnc13";
  public static final String VPF_PATH_DEFAULT = "f://vpf/vmap_af";

	public CreateVPFLayers(final MWC.GUI.ToolParent theParent,
										final MWC.GUI.Properties.PropertiesPanel thePanel,
										final MWC.GUI.Layers theData,
										final BoundsProvider bounds)
	{
		super(theParent, thePanel, theData, bounds, "VPF Layers", "images/vpf.png");
		
		// are we getting our toolparent initialised?
		if(theParent != null)
			_myParent = theParent;
	}
	
	/** initialise the tool, so that it knows where to get it's layers information
	 * 
	 * @param theParent
	 */
	public static void initialise(final ToolParent theParent)
	{
		_myParent = theParent;
	}

  protected MWC.GUI.Plottable createItem()
  {
    return createMyLibrary(true);
  }

  public static VPFDatabase createMyLibrary(final String[] thePaths, final boolean autoPopulate)
  {
    VPFDatabase database = null;

    // create the database itself
    database = new VPFDatabase(thePaths, autoPopulate);

    return database;
  }

	public static VPFDatabase createMyLibrary(final boolean autoPopulate)
	{
    // create the parent layer which will hold the VPF libraries which we know about
    VPFDatabase database = null;

    // do we know where our ToolParent is?
    if(_myParent == null)
    {
      MWC.Utilities.Errors.Trace.trace("Unable to create library, since 'Create VPF Layers' tool not initialised");
      return database;
    }

    // see if there are any data directories
    java.util.Map<String, String> paths = _myParent.getPropertiesLike(VPF_DATABASE_PROPERTY);

    // did we get anything
    if(paths == null)
    {
      paths = new java.util.HashMap<String, String>();
      paths.put("VPF_PATH_DEFAULT", VPF_PATH_DEFAULT);
  	}

    final String [] demo = new String[paths.size()];
    final String [] the_paths = paths.values().toArray(demo);

    database = createMyLibrary(the_paths, autoPopulate);

    return database;

	}

}