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
// $RCSfile: CreateNoiseSource.java,v $
// @author $Author$
// @version $Revision$
// $Log: CreateNoiseSource.java,v $
// Revision 1.1  2006/08/08 14:21:21  Ian.Mayo
// Second import
//
// Revision 1.1  2006/08/07 12:25:29  Ian.Mayo
// First versions
//
// Revision 1.2  2004/05/24 15:39:29  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:30:50  ian
// no message
//
// Revision 1.1.1.1  2003/07/25 09:58:07  Ian.Mayo
// Repository rebuild.
//
// Revision 1.3  2002/10/29 16:19:46  ian_mayo
// reflect name change from PASSIVE to BB_PASSIVE
//
// Revision 1.2  2002-10-10 16:12:36+01  ian_mayo
// general mods, mostly because of IntelliJ Idea code inspections
//
// Revision 1.1  2002-09-17 11:24:34+01  ian_mayo
// Initial revision
//


package ASSET.GUI.Tools;

import ASSET.GUI.Painters.NoiseSourcePainter;
import ASSET.Models.Environment.EnvironmentType;
import MWC.GUI.Tools.Palette.PlainCreate;
import MWC.GenericData.WorldLocation;

/** create a point noise source
 *
 */
public class CreateNoiseSource extends PlainCreate
{
  private EnvironmentType _theEnv = null;
  protected int _medium;

	public CreateNoiseSource(final MWC.GUI.ToolParent theParent,
										final MWC.GUI.Properties.PropertiesPanel thePanel,
										final MWC.GUI.Layers theData,
										final BoundsProvider theChart,
                    final EnvironmentType theEnv,
                    final int medium)
	{
		super(theParent, thePanel, theData, theChart, "Scenario Noise Source", "images/noise_source.gif");
    _theEnv = theEnv;
    _medium = medium;
	}

	protected MWC.GUI.Plottable createItem()
	{
    final WorldLocation origin = getBounds().getViewport().getCentreAtSurface();
    return new NoiseSourcePainter(origin, 180, _theEnv, EnvironmentType.BROADBAND_PASSIVE);
	}
}
