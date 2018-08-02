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
package org.mwc.cmap.core.wizards;

import java.util.Date;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;

public class NewNarrativeEntryWizard extends Wizard
{
	private static final String PAGE_TITLE = "New narrative entry";
	private EnterStringPage namePage;
	private EnterDTGPage dtgPage;
	private EnterStringPage typePage;
	private EnterStringPage entryPage;
	private final HiResDate defaultDate;
	
	public NewNarrativeEntryWizard(final HiResDate date)
	{
	  defaultDate = date;
	}

	public void addPages()
	{
		final String imagePath = "images/NewNarrative.png";
		final String helpContext = "org.mwc.debrief.help.NewNarrativeEntry";

		// when was it?
		// ok, we need to let the user enter the track name
		namePage = new EnterStringPage(null, "TrackNameHere", PAGE_TITLE,
				"This wizard will lead you through creating a new narrative entry.\n"
						+ "Please provide the name of the subject track",
				"a one-word title for the track the entry relates to (e.g. NELSON)",
				imagePath, helpContext, true, null);
		addPage(namePage);

		final HiResDate date = defaultDate != null ? defaultDate : new HiResDate(new Date());
		
		dtgPage = new EnterDTGPage(null, date, PAGE_TITLE,
				"Please specify the date-time for the entry",
				"Date-time the observation was recorded", imagePath, helpContext);
		addPage(dtgPage);

		typePage = new EnterStringPage(null, "EntryType", PAGE_TITLE,
				"Now specify what type of entry this is.",
				"a one-word title for the type of observation (e.g. Command_Comment)",
				imagePath, helpContext, true, null);
		addPage(typePage);

		entryPage = new EnterStringPage(null, "Content of the entry", PAGE_TITLE,
				"Now specify the observation itself",
				"the observation (e.g. Start of trial Optimara)", imagePath,
				helpContext, true, null);
		addPage(entryPage);

		final String message = "The entry will now be added to the Narratives layer";
		final MessageWizardPage messagePage = new MessageWizardPage("finalMessage",
				PAGE_TITLE, "Steps complete", message, imagePath);
		addPage(messagePage);

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

	public NarrativeEntry getEntry()
	{
		return new NarrativeEntry(namePage.getString(), typePage.getString(),
				dtgPage.getDate(), entryPage.getString());
	}

}
