package org.mwc.debrief.core.wizards.core;

import java.awt.Color;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.mwc.cmap.core.wizards.EnterDTGPage;
import org.mwc.cmap.core.wizards.MessageWizardPage;
import org.mwc.cmap.core.wizards.RangeBearingPage;
import org.mwc.cmap.core.wizards.SelectColorPage;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;

public class NewContactWizard extends Wizard
{
	private static final String PAGE_TITLE = "New solution";
	// SelectOffsetPage selectOffsetPage;
	// EnterSolutionPage enterSolutionPage;
	private HiResDate _tNow;
	final private TrackWrapper _track;
	final private SensorWrapper _sensor;
	private EnterDTGPage datePage;
	private RangeBearingPage rngBearingPage;
	private SelectColorPage colorPage;

	public NewContactWizard(HiResDate tNow, TrackWrapper track,
			SensorWrapper sensor)
	{
		_tNow = tNow;
		_track = track;
		_sensor = sensor;
	}

	public void addPages()
	{
		final String imagePath = "images/NewEllipse.png";
		final String helpContext = "org.mwc.debrief.help.TUA_Data";

		// ok, provide an intro
		String introMessage = "This wizard will lead you through creating a new cut\n"
			+ "for the selected sensor.";
		MessageWizardPage introPage = new MessageWizardPage("introMessage",
				PAGE_TITLE, "Introduction", introMessage, imagePath);
		addPage(introPage);

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

		// ok, we need to let the user enter the solution wrapper name
		colorPage = new SelectColorPage(null, Color.RED, PAGE_TITLE,
				"Now format the new ellipse", "The color for this new ellipse",
				imagePath, helpContext);
		addPage(colorPage);

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
	public SensorWrapper getSensorWrapper()
	{
		return _sensor;
	}

	/**
	 * provide the solution created here
	 * 
	 * @return a new solution
	 */
	public SensorContactWrapper getContact()
	{
		SensorContactWrapper tw = new SensorContactWrapper();

		double brgDegs = rngBearingPage.getBearingDegs();
		WorldDistance rng = rngBearingPage.getRange();
		
		tw.setBearing(brgDegs);
		if(rng != null)
			tw.setRange(rng);
		tw.setSensor(_sensor);
		tw.setDTG(datePage.getDate());
		tw.setColor(colorPage.getColor());
		String label = MWC.Utilities.TextFormatting.FormatRNDateTime.toString(_tNow.getDate().getTime());
		tw.setLabel(label);
		return tw;
	}

	public TrackWrapper getTrack()
	{
		return _track;
	}

}
