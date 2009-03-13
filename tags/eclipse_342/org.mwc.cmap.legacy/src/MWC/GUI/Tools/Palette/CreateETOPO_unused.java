// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CreateETOPO_unused.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: CreateETOPO_unused.java,v $
// Revision 1.2  2004/05/25 15:44:18  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1  2003/08/08 11:03:50  Ian.Mayo
// first version after renaming
//
// Revision 1.1.1.1  2003/07/17 10:07:45  Ian.Mayo
// Initial import
//
// Revision 1.3  2002-07-09 15:29:38+01  ian_mayo
// improve comments
//
// Revision 1.2  2002-05-29 10:06:32+01  ian_mayo
// Tidy toolbar label
//
// Revision 1.1  2002-05-28 09:14:03+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-05-23 13:13:22+01  ian
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:35+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-23 13:27:55+01  administrator
// Reflect new signature for PlainCreate class, to allow it to fireExtended()
//
// Revision 1.0  2001-07-17 08:42:52+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:40+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:58  ianmayo
// initial version
//

package MWC.GUI.Tools.Palette;

import MWC.GUI.Layer;
import MWC.GUI.Layers;

/** class to create an ETOPO object,
 * @see MWC.GUI.Chart.Painters.ETOPOPainter
 */
public class CreateETOPO_unused extends PlainCreateLayer
{

  /** static copy of parent, so we can retrieve the path
   *
   */
  private static MWC.GUI.ToolParent _myParent = null;

  /** the name of the property which indicates where the etopo data is
   *
   */
  private static String ETOPO_PATH = "ETOPO_Directory";

  /** the default path to find the ETOPO data
   *
   */
  private static String DEFAULT_PATH = "etopo";


  /** constructor, taking normal PlainCreate parameters
   *
   */
	public CreateETOPO_unused(MWC.GUI.ToolParent theParent,
										MWC.GUI.Properties.PropertiesPanel thePanel,
										MWC.GUI.Layer theLayer,
										MWC.GUI.Layers theData,
										MWC.GUI.PlainChart theChart)
	{
		super(theParent, thePanel, theData, theChart, "ETOPO Gridded Bathy", "images/etopo.gif");

    _myParent = theParent;
	}

  /** get the current ETOPO path
   *
   */
  public static String getETOPOPath()
  {
    // retrieve the path to the ETOPO layers
    //
    String newPath =  _myParent.getProperty(ETOPO_PATH);

    if(newPath == null)
      newPath = DEFAULT_PATH;

    return newPath;

  }

  /** create this item. The Layers object will know to put this layer at the back because
   * of it's BackgroundLayer marker interface
   *
   */
	protected Layer createItem(MWC.GUI.PlainChart theChart)
	{
		return loadBathyData(super.getLayers());
	}

  /** static class which allows other layers to load data
   *
   */
  public static Layer loadBathyData(Layers theLayers)
  {
    String newPath = getETOPOPath();

    return new MWC.GUI.Chart.Painters.ETOPOPainter(newPath, theLayers);
  }
}
