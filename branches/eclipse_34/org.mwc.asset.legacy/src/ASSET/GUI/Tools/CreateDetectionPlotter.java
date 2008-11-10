// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CreateDetectionPlotter.java,v $
// @author $Author$
// @version $Revision$
// $Log: CreateDetectionPlotter.java,v $
// Revision 1.1  2006/08/08 14:21:20  Ian.Mayo
// Second import
//
// Revision 1.1  2006/08/07 12:25:28  Ian.Mayo
// First versions
//
// Revision 1.2  2004/05/24 15:39:24  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:30:50  ian
// no message
//
// Revision 1.1.1.1  2003/07/25 09:58:06  Ian.Mayo
// Repository rebuild.
//
// Revision 1.2  2002/10/10 15:12:23  ian_mayo
// general mods, mostly because of IntelliJ Idea code inspections
//
// Revision 1.1  2002-09-20 11:42:29+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-09-17 11:24:33+01  ian_mayo
// Initial revision
//


package ASSET.GUI.Tools;

import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GUI.Tools.Palette.PlainCreate;
import MWC.GenericData.WorldLocation;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.GUI.Painters.Detections.ScenarioDetectionPainter;

/** create a point noise source
 *
 */
public class CreateDetectionPlotter extends PlainCreate
{


  /****************************************************
   * member objects
   ***************************************************/
  private ASSET.ScenarioType _theScenario = null;


  /****************************************************
   * constructor
   ***************************************************/
	public CreateDetectionPlotter(final MWC.GUI.ToolParent theParent,
										final MWC.GUI.Properties.PropertiesPanel thePanel,
                    final ASSET.ScenarioType theScenario,
										MWC.GUI.Layer theLayer,
										final MWC.GUI.Layers theData,
										final MWC.GUI.PlainChart theChart)
	{
		super(theParent, thePanel, null, theData, theChart, "Detection Painter", "images/noise_excess.gif");
    _theScenario = theScenario;
	}

	protected MWC.GUI.Plottable createItem(MWC.GUI.PlainChart theChart)
	{
    return new ScenarioDetectionPainter(_theScenario);
	}
}
