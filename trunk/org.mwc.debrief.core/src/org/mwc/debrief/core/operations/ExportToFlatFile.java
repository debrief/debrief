package org.mwc.debrief.core.operations;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.interfaces.TimeControllerOperation;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.wizards.FlatFile.ExportFlatFileDataPage;
import org.mwc.debrief.core.wizards.FlatFile.ExportFlatFileWizard;
import org.mwc.debrief.core.wizards.FlatFile.ExportFlatFileDataPage.ExportDataItem;

import Debrief.ReaderWriter.FlatFile.FlatFileExporter;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;

public class ExportToFlatFile extends TimeControllerOperation
{


	public ExportToFlatFile()
	{
		super("Export to flat file", true);
	}


	@Override
	public ImageDescriptor getDescriptor()
	{
		return DebriefPlugin.getImageDescriptor("icons/new.gif");
	}


	@Override
	public void executeExport(WatchableList primaryTrack,
			WatchableList[] secondaryTracks, TimePeriod period)
	{
		// sort out the destination file name
		String fileName = null;
		
		// sort out what type of file it is
		String sensorType = null;
		

		
		ExportFlatFileWizard wizard = new ExportFlatFileWizard();
		WizardDialog dialog = new WizardDialog(Display.getCurrent()
				.getActiveShell(), wizard);
		dialog.create();
		dialog.open();

		// did it work?
		if (dialog.getReturnCode() == WizardDialog.OK)
		{

			ExportFlatFileDataPage offsetPage = (ExportFlatFileDataPage) wizard
					.getPage(ExportFlatFileDataPage.NAME);
			if (offsetPage != null)
			{
				if (offsetPage.isPageComplete())
				{
					ExportDataItem res = (ExportDataItem) offsetPage.getEditable();
					fileName = res.getFilePath();
					sensorType = res.getSensorType();
				}
				}
			}
		
		FlatFileExporter ff = new FlatFileExporter();
		String theData = ff.export(primaryTrack, secondaryTracks, period, sensorType);
		
		// now write the data somewhere
		System.out.println(theData);
		System.out.println(fileName);
		
		// ask the user?

		// now loop through the OS track
		System.err.println("DOING EXPORT TO FLAT FILE!!");
	}

}
