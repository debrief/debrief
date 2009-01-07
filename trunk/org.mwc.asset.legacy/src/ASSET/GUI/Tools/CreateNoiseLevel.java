// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CreateNoiseLevel.java,v $
// @author $Author$
// @version $Revision$
// $Log: CreateNoiseLevel.java,v $
// Revision 1.1  2006/08/08 14:21:21  Ian.Mayo
// Second import
//
// Revision 1.1  2006/08/07 12:25:29  Ian.Mayo
// First versions
//
// Revision 1.2  2004/05/24 15:39:28  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:30:50  ian
// no message
//
// Revision 1.1.1.1  2003/07/25 09:58:06  Ian.Mayo
// Repository rebuild.
//
// Revision 1.2  2002/10/10 15:11:57  ian_mayo
// general mods, mostly because of IntelliJ Idea code inspections
//
// Revision 1.1  2002-09-17 11:24:33+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-28 09:26:02+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:04+01  ian_mayo
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
// Revision 1.1  2001-01-03 13:41:39+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:52  ianmayo
// initial version
//
// Revision 1.3  1999-11-26 15:51:40+00  ian_mayo
// tidying up
//
// Revision 1.2  1999-11-11 18:23:03+00  ian_mayo
// new classes, to allow creation of shapes from palette
//

package ASSET.GUI.Tools;

import ASSET.GUI.Painters.ScenarioNoiseLevelPainter;
import ASSET.Models.Environment.EnvironmentType;
import MWC.GUI.Tools.Palette.PlainCreate;

public class CreateNoiseLevel extends PlainCreate
{
  private EnvironmentType _theEnv = null;
  private ScenarioNoiseLevelPainter.StatusProvider _provider;
  private int _medium;

	public CreateNoiseLevel(final MWC.GUI.ToolParent theParent,
										final MWC.GUI.Properties.PropertiesPanel thePanel,
										MWC.GUI.Layer theLayer,
										final MWC.GUI.Layers theData,
										final MWC.GUI.PlainChart theChart,
                    final EnvironmentType theEnv,
                    final ScenarioNoiseLevelPainter.StatusProvider provider,
                    final int medium)
	{
		super(theParent, thePanel, null, theData, theChart, "Scenario Noise", "images/noise_level.gif");
    _theEnv = theEnv;
    _provider = provider;
    _medium = medium;
	}

	protected MWC.GUI.Plottable createItem(MWC.GUI.PlainChart theChart)
	{
		return new ScenarioNoiseLevelPainter(_theEnv, _provider, _medium, super.getLayers());
	}
}
