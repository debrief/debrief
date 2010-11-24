package org.mwc.debrief.core.wizards.core;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;

public class NewSolutionWizard extends Wizard
{
	// SelectOffsetPage selectOffsetPage;
	// EnterSolutionPage enterSolutionPage;
	private HiResDate _tNow;
	private TrackWrapper _track;
	private TMAWrapper _tma;
	private EnterStringPage namePage;
	private EnterDTGPage datePage;
	private RangeBearingPage rangePage;

	public NewSolutionWizard(HiResDate tNow, TrackWrapper track, TMAWrapper tma)
	{
		_tNow = tNow;
		_track = track;
		_tma = tma;
	}

	public void addPages()
	{
		// do we know the solution wrapper?
		if (_track != null)
		{
			// ok, we need to let the user enter the solution wrapper name
			namePage = new EnterStringPage(
					null,
					"[new solution name]",
					"The solution must be placed inside a named block of solutions, please provide a name",
					"a one-word phrase for this block of ellipses");
			addPage(namePage);
		}

		// ok, now sort out the time
		datePage = new EnterDTGPage(null, _tNow,
				"We must now choose the time for the solution",
				"Date-time for this ellipse");
		// ok, we need to let the user enter the solution wrapper name
		addPage(datePage);

		// now for the easy fields
		// ok, we need to let the user enter the solution wrapper name
		rangePage = new RangeBearingPage(
				null,
				"The solution must be placed inside a named block of solutions, please provide a name",
				"a one-word phrase for this block of ellipses",
				"range from ownship to centre of ellipse",
				"bearing from ownship centre of ellipse (degs)");
		addPage(rangePage);

		// ok, also let the user choose a time

		// selectOffsetPage = new SelectOffsetPage(null);
		//
		// // initialise the sensor offset
		// DataItem di = (DataItem) selectOffsetPage.createMe();
		// di._bearing = _brgDegs;
		// if(_range != null)
		// di._range = _range;
		//        
		// addPage(selectOffsetPage);
		//                      
		// enterSolutionPage = new EnterSolutionPage(null);
		// SolutionDataItem d2 = (SolutionDataItem) enterSolutionPage.createMe();
		// d2._course = _initalCourse;
		// d2._speed = _initialSpeed;
		//           
		// addPage(enterSolutionPage);
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

	public TMAWrapper getSolutionWrapper()
	{
		if (_tma == null)
			if (namePage != null)
			{
				_tma = new TMAWrapper(namePage.getString());
			}
		return _tma;
	}

	public TMAContactWrapper getSolution()
	{
		TMAContactWrapper tw = new TMAContactWrapper();
		tw.setDTG(datePage.getDate());
		return tw;
	}

	public TrackWrapper getTrack()
	{
		return _track;
	}

}
