package org.mwc.debrief.core.wizards.core;

import java.awt.Color;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.mwc.debrief.core.wizards.MessageWizardPage;
import org.mwc.debrief.core.wizards.s2r.EnterSolutionPage;
import org.mwc.debrief.core.wizards.s2r.EnterSolutionPage.SolutionDataItem;

import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

public class NewSolutionWizard extends Wizard
{
	private static final String PAGE_TITLE = "New solution";
	// SelectOffsetPage selectOffsetPage;
	// EnterSolutionPage enterSolutionPage;
	private HiResDate _tNow;
	private TrackWrapper _track;
	private TMAWrapper _tma;
	private EnterStringPage namePage;
	private EnterDTGPage datePage;
	private RangeBearingPage rangePage;
	private EnterSolutionPage solutionPage;
	private SelectColorPage colorPage;

	public NewSolutionWizard(HiResDate tNow, TrackWrapper track, TMAWrapper tma)
	{
		_tNow = tNow;
		_track = track;
		_tma = tma;
	}

	public void addPages()
	{
		final String imagePath = "images/grid_wizard.gif";

		// do we know the solution wrapper?
		if (_track != null)
		{
			// ok, we need to let the user enter the solution wrapper name
			namePage = new EnterStringPage(
					null,
					"[new block name]",
					PAGE_TITLE,
					"The solution must be placed inside a named block of solutions, please provide a name",
					"a one-word phrase for this block of ellipses",
					imagePath);
			addPage(namePage);
		}

		// ok, now sort out the time
		datePage = new EnterDTGPage(null, _tNow, PAGE_TITLE,
				"Choose the time for the solution", "Date-time for this ellipse", imagePath);
		// ok, we need to let the user enter the solution wrapper name
		addPage(datePage);

		// now for the easy fields
		// ok, we need to let the user enter the solution wrapper name
		rangePage = new RangeBearingPage(null, PAGE_TITLE,
				"Specify the range/bearing to the target",
				"range from ownship to centre of ellipse",
				"bearing from ownship centre of ellipse (degs)", imagePath);
		addPage(rangePage);

		solutionPage = new EnterSolutionPage(null, PAGE_TITLE,
				"Enter an initial solution\n"
						+ "This will be stored in the ellipse label", imagePath);
		addPage(solutionPage);

		// ok, we need to let the user enter the solution wrapper name
		colorPage = new SelectColorPage(null, Color.RED, PAGE_TITLE,
				"Now format the new ellipse", "The color for this new ellipse", imagePath);
		addPage(colorPage);

		String message = "The solution will now be added to the specified track, \n"
				+ "and provided with a default ellipse size - which you \n"
				+ "may wish to customise";
		MessageWizardPage messagePage = new MessageWizardPage("finalMessage",
				PAGE_TITLE, "Steps complete", message,  imagePath);
		addPage(messagePage);

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

	/**
	 * get either the new solution wrapper, or the one passed in
	 * 
	 * @return
	 */
	public TMAWrapper getSolutionWrapper()
	{
		if (_tma == null)
			if (namePage != null)
			{
				_tma = new TMAWrapper(namePage.getString());
			}

		// make the wrapper visible, so the new data can be seen
		_tma.setVisible(true);

		return _tma;
	}

	/**
	 * provide the solution created here
	 * 
	 * @return a new solution
	 */
	public TMAContactWrapper getSolution()
	{
		TMAContactWrapper tw = new TMAContactWrapper();

		SolutionDataItem sol = (SolutionDataItem) solutionPage.getEditable();
		tw.buildSetTargetState(sol.getCourse(), sol.getSpeed().getValueIn(
				WorldSpeed.Kts), 0);
		tw.buildSetEllipse(0, new WorldDistance(1, WorldDistance.NM),
				new WorldDistance(1, WorldDistance.NM));
		double brgRads = MWC.Algorithms.Conversions.Degs2Rads(rangePage
				.getBearingDegs());
		tw.buildSetVector(new WorldVector(brgRads, rangePage.getRange(), null));
		tw.setRange(rangePage.getRange());

		tw.setTMATrack(_tma);
		tw.setDTG(datePage.getDate());
		tw.setColor(colorPage.getColor());

		String lblStr = sol.getSpeed().toString() + sol.getCourse() + "Degs";
		tw.setLabel(lblStr);
		return tw;
	}

	public TrackWrapper getTrack()
	{
		return _track;
	}

}
