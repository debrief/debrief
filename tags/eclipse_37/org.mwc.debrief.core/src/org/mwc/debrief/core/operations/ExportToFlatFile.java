package org.mwc.debrief.core.operations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.TimeControllerOperation;
import org.mwc.cmap.core.ui_support.wizards.SimplePageListWizard;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.wizards.FlatFilenameWizardPage;

import Debrief.ReaderWriter.FlatFile.FlatFileExporter;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;

public class ExportToFlatFile extends TimeControllerOperation
{

	public ExportToFlatFile()
	{
		super("Export to flat file (SAM Format)", true, true, true);
	}

	@Override
	public ImageDescriptor getDescriptor()
	{
		return DebriefPlugin.getImageDescriptor("icons/NewFile.png");
	}

	@Override
	public void executeExport(WatchableList primaryTrack,
			WatchableList[] secondaryTracks, TimePeriod period)
	{
		// sort out the destination file name
		String filePath = null;

		// sort out what type of file it is
		String sensorType = null;

		// just check that at least one of the sensors has an offset
		TrackWrapper primary = (TrackWrapper) primaryTrack;
		BaseLayer sensors = primary.getSensors();
		Enumeration<Editable> sList = sensors.elements();
		boolean foundOne = false;
		while (sList.hasMoreElements())
		{
			SensorWrapper thisS = (SensorWrapper) sList.nextElement();
			if (thisS.getSensorOffset() != null)
				if (thisS.getSensorOffset().getValue() > 0)
					foundOne = true;
		}
		if (!foundOne)
		{
			Shell shell = Display.getCurrent().getActiveShell();
			String title = "Export flat file";
			Image image = null;
			String message = "None of the sensor data has a towed array offset applied.\nDo you wish to continue?";
			String[] labels = new String[]
			{ "Yes", "No" };
			int index = 1;
			int imageType = MessageDialog.QUESTION;
			// check the user knows what he's doing
			MessageDialog dl = new MessageDialog(shell, title, image, message,
					imageType, labels, index);
			int res = dl.open();
			if (res == MessageDialog.CANCEL)
				return;
		}

		SimplePageListWizard wizard = new SimplePageListWizard();
		wizard.addWizard(new FlatFilenameWizardPage());
		WizardDialog dialog = new WizardDialog(Display.getCurrent()
				.getActiveShell(), wizard);
		dialog.create();
		dialog.open();

		// did it work?
		if (dialog.getReturnCode() == WizardDialog.OK)
		{
			FlatFilenameWizardPage exportPage = (FlatFilenameWizardPage) wizard
					.getPage(FlatFilenameWizardPage.PAGENAME);
			if (exportPage != null)
			{
				if (exportPage.isPageComplete())
				{
					filePath = exportPage.getFileName();
					sensorType = exportPage.getSensorType();
				}
			}

			FlatFileExporter ff = new FlatFileExporter();
			String theData = ff.export(primaryTrack, secondaryTracks, period,
					sensorType);

			// now write the data to file

			final String HOST_NAME = primaryTrack.getName();
			final String HOST_DATE = MWC.Utilities.TextFormatting.FormatRNDateTime
					.toMediumString(period.getStartDTG().getDate().getTime());

			final String fileName = filePath + File.separator + HOST_NAME + "_"
					+ HOST_DATE + "." + FlatFilenameWizardPage.FILE_SUFFIX;

			BufferedWriter out = null;
			try
			{
				out = new BufferedWriter(new FileWriter(fileName));
				out.write(theData);
				CorePlugin.showMessage("Export to SAM",
						"Tracks successfullly exported to SAM format");
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
						out.close();
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
