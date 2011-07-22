package org.mwc.debrief.core.wizards.core;

import java.util.Date;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.mwc.cmap.core.wizards.EnterDTGPage;
import org.mwc.cmap.core.wizards.EnterStringPage;
import org.mwc.cmap.core.wizards.MessageWizardPage;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;

public class NewNarrativeEntryWizard extends Wizard
{
	private static final String PAGE_TITLE = "New narrative entry";
	private EnterStringPage namePage;
	private EnterDTGPage dtgPage;
	private EnterStringPage typePage;
	private EnterStringPage entryPage;

	public NewNarrativeEntryWizard()
	{
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
				imagePath, helpContext);
		addPage(namePage);

		dtgPage = new EnterDTGPage(null, new HiResDate(new Date()), PAGE_TITLE,
				"Please specify the date-time for the entry",
				"Date-time the observation was recorded", imagePath, helpContext);
		addPage(dtgPage);

		typePage = new EnterStringPage(null, "EntryType", PAGE_TITLE,
				"Now specify what type of entry this is.",
				"a one-word title for the type of observation (e.g. Command_Comment)",
				imagePath, helpContext);
		addPage(typePage);

		entryPage = new EnterStringPage(null, "Content of the entry", PAGE_TITLE,
				"Now specify the observation itself",
				"the observation (e.g. Start of trial Optimara)", imagePath,
				helpContext);
		addPage(entryPage);

		String message = "The entry will now be added to the Narratives layer";
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

	public NarrativeEntry getEntry()
	{
		return new NarrativeEntry(namePage.getString(), typePage.getString(),
				dtgPage.getDate(), entryPage.getString());
	}

}
