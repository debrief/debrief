package org.mwc.debrief.core.operations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.TimeControllerOperation;
import org.mwc.cmap.core.ui_support.wizards.SimplePageListWizard;
import org.mwc.cmap.core.wizards.DirectorySelectorWizardPage;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.ReaderWriter.FlatFile.DopplerShift.DopplerShiftExporter;
import Debrief.ReaderWriter.FlatFile.DopplerShift.DopplerShiftExporter.ExportException;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;

/**
 * embedded class to generate menu-items for creating a new sensor
 */
public class ExportDopplerShift extends TimeControllerOperation
{
	private static final String ACTION_NAME = "Export Doppler Shift data";
	public static final String FILE_SUFFIX = "csv";
	private static final String HELP_CONTEXT = "org.mwc.debrief.help.ExportDoppler";

	public ExportDopplerShift()
	{
		super(ACTION_NAME, true, false, true);
	}

	@Override
	public ImageDescriptor getDescriptor()
	{
		return DebriefPlugin.getImageDescriptor("icons/export_doppler.png");
	}

	@Override
	public void executeExport(WatchableList primaryTrack,
			WatchableList[] secondaryTracks, TimePeriod period)
	{

		DopplerShiftExporter ff = new DopplerShiftExporter();
		String theData = null;
		try
		{
			theData = ff.export(primaryTrack, secondaryTracks, period);
		}
		catch (ExportException e1)
		{
			CorePlugin.logError(Status.ERROR, "Whilst exporting doppler shift data",
					e1);
			CorePlugin.showMessage("Export to doppler", e1.getMessage());
			return;
		}

		// did it work?
		if (theData != null)
		{

			// sort out the destination file name
			String filePath = null;

			SimplePageListWizard wizard = new SimplePageListWizard();
			DirectorySelectorWizardPage exportPage = new DirectorySelectorWizardPage(
					"ExportDoppler",
					ACTION_NAME,
					"Please select the directory where Debrief will \nplace the exported Doppler shift file.",
					"org.mwc.debrief.core", "images/DopplerEffect.png",HELP_CONTEXT);
			wizard.addWizard(exportPage);
			wizard.setHelpAvailable(true);
			WizardDialog dialog = new WizardDialog(Display.getCurrent()
					.getActiveShell(), wizard);
			dialog.create();
			dialog.open();

			// did it work?
			if (dialog.getReturnCode() == WizardDialog.OK)
			{
				if (exportPage.isPageComplete())
				{
					filePath = exportPage.getFileName();
				}

				// now write the data to file
				final String HOST_NAME = primaryTrack.getName();
				final String HOST_DATE = MWC.Utilities.TextFormatting.FormatRNDateTime
						.toMediumString(period.getStartDTG().getDate().getTime());

				final String fileName = filePath + File.separator + HOST_NAME + "_"
						+ HOST_DATE + "." + FILE_SUFFIX;

				BufferedWriter out = null;
				try
				{
					FileWriter fOut = new FileWriter(fileName);
					out = new BufferedWriter(fOut);
					out.write(theData);
					out.flush();
				}
				catch (FileNotFoundException e)
				{
					DebriefPlugin.logError(Status.ERROR, "Unable to find output file:"
							+ fileName, e);
				}
				catch (IOException e)
				{
					DebriefPlugin.logError(Status.ERROR, "Whilst writing to output file:"
							+ fileName, e);
				}
				finally
				{
					try
					{
						if (out != null)
						{
							out.close();
						}
					}
					catch (IOException e)
					{
						DebriefPlugin.logError(Status.ERROR, "Whilst closing output file:"
								+ fileName, e);
					}
				}
			}
		}

	}

}