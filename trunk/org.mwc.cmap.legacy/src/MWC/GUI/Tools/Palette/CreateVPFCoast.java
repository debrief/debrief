// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CreateVPFCoast.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: CreateVPFCoast.java,v $
// Revision 1.2  2004/05/25 15:44:24  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:46  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:02+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:04+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:36+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-08-23 13:27:56+01  administrator
// Reflect new signature for PlainCreate class, to allow it to fireExtended()
//
// Revision 1.1  2001-07-17 12:56:35+01  administrator
// make the property name public, so that it can be re-used during the XML Import process
//
// Revision 1.0  2001-07-17 08:42:53+01  administrator
// Initial revision
//
// Revision 1.1  2001-07-16 15:38:33+01  novatech
// Initial revision
//

package MWC.GUI.Tools.Palette;

import MWC.GUI.VPF.LibraryLayer;

public class CreateVPFCoast extends PlainCreate
{
  /** the property name used to store the vpf coastline
   *
   */
  public static final String COAST_PROPERTY = "VPF_COAST_DIR";

  /** the default path we use for the data path
   *
   */
  public static final String COAST_PATH_DEFAULT = "c://vp//vmaplv0";

  public CreateVPFCoast(MWC.GUI.ToolParent theParent,
                        MWC.GUI.Properties.PropertiesPanel thePanel,
                        MWC.GUI.Layer theLayer,
                        MWC.GUI.Layers theData,
                        MWC.GUI.PlainChart theChart,
                        String theName,
                        String thePath)
  {
    super(theParent, thePanel, theLayer, theData, theChart, theName, thePath);
  }

	public CreateVPFCoast(MWC.GUI.ToolParent theParent,
                        MWC.GUI.Properties.PropertiesPanel thePanel,
                        MWC.GUI.Layer theLayer,
                        MWC.GUI.Layers theData,
                        MWC.GUI.PlainChart theChart)
	{
		this(theParent, thePanel, theLayer, theData, theChart, "Coast", "images/coast.gif");
	}

  protected String getMyPath()
  {
    String res =  getParent().getProperty(COAST_PROPERTY);
    if(res == null)
      res = COAST_PATH_DEFAULT;
    return res;
  }

  protected MWC.GUI.Plottable getMyLayer(String path)
  {
    MWC.GUI.Plottable res = null;
    try
    {
      // create the coverage layer for the coastline
      res = LibraryLayer.createReferenceLayer(path);
    }
    catch(Exception e)
    {
      MWC.Utilities.Errors.Trace.trace(e, "Unable to create VPF coastline");
    }
    return res;
  }

	protected MWC.GUI.Plottable createItem(MWC.GUI.PlainChart theChart)
	{
    MWC.GUI.Plottable cl = null;

    // try and retrieve the path to the coast line directory
    String path = getMyPath();

    // try to build the layer
    cl = getMyLayer(path);

    // and return the data
    return cl;
	}
}
