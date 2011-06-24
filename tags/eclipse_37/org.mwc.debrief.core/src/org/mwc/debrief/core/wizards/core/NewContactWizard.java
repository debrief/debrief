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
	private static final String PAGE_TITLE = "New sensor cut";
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
		final String imagePath = "images/NewSensor.png";
		final String helpContext = "org.mwc.debrief.help.NewSensorContact";

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
				"Specify the range/bearing to the contact",
				"range to contact (0.0 if not known)", "bearing to contact (degs)",
				imagePath, helpContext);
		addPage(rngBearingPage);

		// set some default data - like zero range
		rngBearingPage.setData(new WorldDistance(0, WorldDistance.NM), 270);

		// ok, we need to let the user enter the solution wrapper name
		colorPage = new SelectColorPage(null, Color.RED, PAGE_TITLE,
				"Now format the new sensor cut", "The color for this new sensur cut",
				imagePath, helpContext);
		addPage(colorPage);

		String message = "The sensor cut will now be added to the specified sensor, \n"
				+ " you " + "can customise the cut further in the Properties window";
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
		if (rng != null)
		{
			// ok, we have a range, check it's not zero
			if (rng.getValue() != 0d)
			{
				// we have valid data - use it
				tw.setRange(rng);
			}
		}
		// ok, work through the other parameters
		tw.setSensor(_sensor);
		tw.setDTG(datePage.getDate());
		tw.setColor(colorPage.getColor());
		String label = MWC.Utilities.TextFormatting.FormatRNDateTime.toString(_tNow
				.getDate().getTime());
		tw.setLabel(label);
		return tw;
	}

	public TrackWrapper getTrack()
	{
		return _track;
	}

}
