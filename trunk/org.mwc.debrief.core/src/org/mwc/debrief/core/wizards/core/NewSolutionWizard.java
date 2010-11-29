package org.mwc.debrief.core.wizards.core;

import java.awt.Color;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.mwc.cmap.core.wizards.EnterDTGPage;
import org.mwc.cmap.core.wizards.EnterRangePage;
import org.mwc.cmap.core.wizards.EnterStringPage;
import org.mwc.cmap.core.wizards.MessageWizardPage;
import org.mwc.cmap.core.wizards.RangeBearingPage;
import org.mwc.cmap.core.wizards.SelectColorPage;
import org.mwc.debrief.core.wizards.s2r.EnterSolutionPage;
import org.mwc.debrief.core.wizards.s2r.EnterSolutionPage.SolutionDataItem;

import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

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
	private RangeBearingPage rngBearingPage;
	private EnterSolutionPage solutionPage;
	private SelectColorPage colorPage;
	private EnterRangePage rangePage;

	public NewSolutionWizard(HiResDate tNow, TrackWrapper track, TMAWrapper tma)
	{
		_tNow = tNow;
		_track = track;
		_tma = tma;
	}

	public void addPages()
	{
		final String imagePath = "images/NewEllipse.png";
		final String helpContext = "org.mwc.debrief.help.TUA_Data";

		// do we know the solution wrapper?
		if (_track != null)
		{
			// ok, we need to let the user enter the solution wrapper name
			namePage = new EnterStringPage(null, "NameHere", PAGE_TITLE,
					"This wizard will lead you through creating a new TUA Ellipse.\n" +
					"The ellipse created to represent your solution must be placed\n"
							+ "inside a named block, please provide a name",
					"a one-word phrase for this block of ellipses", imagePath,
					helpContext);
			addPage(namePage);
		}

		// ok, now sort out the time
		datePage = new EnterDTGPage(null, _tNow, PAGE_TITLE,
				"Choose the time for the solution\n"
						+ "Note: this time is taken from the Time Slider",
				"Date-time for this ellipse", imagePath, helpContext);
		// ok, we need to let the user enter the solution wrapper name
		addPage(datePage);

		// now for the easy fields
		// ok, we need to let the user enter the solution wrapper name
		rngBearingPage = new RangeBearingPage(null, PAGE_TITLE,
				"Specify the range/bearing to the solution",
				"range from ownship to centre of ellipse",
				"bearing from ownship to centre of ellipse (degs)", imagePath,
				helpContext);
		addPage(rngBearingPage);

		solutionPage = new EnterSolutionPage(null, PAGE_TITLE,
				"Enter an initial solution\n"
						+ "This will be stored in the ellipse label", imagePath,
				helpContext);
		addPage(solutionPage);

		// ok, we need to let the user enter the solution wrapper name
		colorPage = new SelectColorPage(null, Color.RED, PAGE_TITLE,
				"Now format the new ellipse", "The color for this new ellipse",
				imagePath, helpContext);
		addPage(colorPage);

		WorldDistance defaultWidth = new WorldDistance(1, WorldDistance.NM);
		rangePage = new EnterRangePage(null, PAGE_TITLE,
				"Now specify the size of ellipse",
				"initial size (radius) for  ellipse", defaultWidth, imagePath,
				helpContext);
		addPage(rangePage);

		String message = "The solution will now be added to the specified track, \n"
				+ "and provided with the default ellipse size - you\n"
				+ "can customise the ellipse further in the Properties window";
		MessageWizardPage messagePage = new MessageWizardPage("finalMessage",
				PAGE_TITLE, "Steps complete", message, imagePath);
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
		WorldDistance radius = rangePage.getRange();
		tw.buildSetEllipse(0, radius, radius);
		double brgRads = MWC.Algorithms.Conversions.Degs2Rads(rngBearingPage
				.getBearingDegs());
		tw.buildSetVector(brgRads, rngBearingPage.getRange(),
						0);
		tw.setRange(rngBearingPage.getRange());

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
