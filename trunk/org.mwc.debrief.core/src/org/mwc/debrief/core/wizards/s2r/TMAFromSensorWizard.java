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
	private double _brgDegs;
	private WorldDistance _range;
	private double _initalCourse;
	private WorldSpeed _initialSpeed;

	public TMAFromSensorWizard(double brgDegs, WorldDistance range,
			double initialCourse, WorldSpeed initialSpeed)
	{
		_brgDegs = brgDegs;
		_range = range;
		_initalCourse = initialCourse;
		_initialSpeed = initialSpeed;
	}

	public void addPages()
	{
		final String imagePath = "images/grid_wizard.gif";

		String helpContext = null;
		
		// now for the easy fields
		// ok, we need to let the user enter the solution wrapper name
		selectOffsetPage = new RangeBearingPage(null, PAGE_TITLE,
				"Now specify the offset to the track start",
				"range from ownship to start of track",
				"bearing from ownship to start of track", imagePath, helpContext );

		selectOffsetPage.setData(_range, _brgDegs);

		addPage(selectOffsetPage);

		enterSolutionPage = new EnterSolutionPage(null, PAGE_TITLE,
				"This page lets you enter an initial solution", imagePath, helpContext);
		SolutionDataItem d2 = (SolutionDataItem) enterSolutionPage.createMe();
		d2._course = _initalCourse;
		d2._speed = _initialSpeed;

		addPage(enterSolutionPage);
	}

	public boolean performFinish()
	{
		return true;
	}

	@Override
	public IWizardPage getPage(String name)
	{
		return super.getPage(name);
	}

}
