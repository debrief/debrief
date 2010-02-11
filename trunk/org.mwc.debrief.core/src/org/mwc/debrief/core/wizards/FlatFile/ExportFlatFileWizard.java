package org.mwc.debrief.core.wizards.FlatFile;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.mwc.debrief.core.wizards.FlatFile.ExportFlatFileDataPage.ExportDataItem;

public class ExportFlatFileWizard extends Wizard
{
	ExportFlatFileDataPage enterSolutionPage;
	private String _filePath;
	private String _sensorType;

	public ExportFlatFileWizard()
	{
		// see if we can remember the last file path (preferences)
		
		_filePath = "output.csv";
		_sensorType = "C";
	}

	public void addPages()
	{
		// initialise the sensor offset
		enterSolutionPage = new ExportFlatFileDataPage(null);
		ExportDataItem d2 = (ExportDataItem) enterSolutionPage.createMe();
		d2.setFilePath(_filePath);
		d2.setSensorType(_sensorType);

		addPage(enterSolutionPage);
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

}
