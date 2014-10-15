/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.wizards.s2r;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.mwc.cmap.core.wizards.RangeBearingPage;
import org.mwc.debrief.core.wizards.EnterSolutionPage;
import org.mwc.debrief.core.wizards.EnterSolutionPage.SolutionDataItem;

import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

public class TMAFromSensorWizard extends Wizard
{
	private static final String PAGE_TITLE = "Generate TMA segment";
	RangeBearingPage selectOffsetPage;
	EnterSolutionPage enterSolutionPage;
	private final double _brgDegs;
	private final WorldDistance _range;
	private final double _initalCourse;
	private final WorldSpeed _initialSpeed;

	public TMAFromSensorWizard(final double brgDegs, final WorldDistance range,
			final double initialCourse, final WorldSpeed initialSpeed)
	{
		_brgDegs = brgDegs;
		_range = range;
		_initalCourse = initialCourse;
		_initialSpeed = initialSpeed;
	}

	public void addPages()
	{
		final String imagePath = "images/grid_wizard.gif";

		final String helpContext = null;
		
		// now for the easy fields
		// ok, we need to let the user enter the solution wrapper name
		selectOffsetPage = new RangeBearingPage(null, PAGE_TITLE,
				"Now specify the offset to the track start",
				"range from ownship to start of track",
				"bearing from ownship to start of track", imagePath, helpContext );

		// give ourselves a default range, if we don't have one
		WorldDistance theRange = _range;
		if(theRange == null)
			theRange = new WorldDistance(5, WorldDistance.NM);
		
		selectOffsetPage.setData(theRange, _brgDegs);

		addPage(selectOffsetPage);

		enterSolutionPage = new EnterSolutionPage(null, PAGE_TITLE,
				"This page lets you enter an initial solution", imagePath, helpContext);
		final SolutionDataItem d2 = (SolutionDataItem) enterSolutionPage.createMe();
		d2._course = _initalCourse;
		d2._speed = _initialSpeed;

		addPage(enterSolutionPage);
	}

	public boolean performFinish()
	{
		return true;
	}

	@Override
	public IWizardPage getPage(final String name)
	{
		return super.getPage(name);
	}

}
