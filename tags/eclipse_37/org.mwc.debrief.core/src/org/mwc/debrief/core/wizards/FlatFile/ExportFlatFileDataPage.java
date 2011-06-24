package org.mwc.debrief.core.wizards.FlatFile;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Text;
import org.mwc.cmap.core.wizards.CoreEditableWizardPage;

import MWC.GUI.Editable;

public class ExportFlatFileDataPage extends CoreEditableWizardPage
{
	public static String NAME = "Export details";

	public static class ExportDataItem implements Editable
	{
		private String _filePath;
		private String _sensorType;

		public String getFilePath()
		{
			return _filePath;
		}

		public void setFilePath(String filePath)
		{
			_filePath = filePath;
		}

		public String getSensorType()
		{
			return _sensorType;
		}

		public void setSensorType(String sensorType)
		{
			_sensorType = sensorType;
		}

		public EditorType getInfo()
		{
			return null;
		}

		public String getName()
		{
			return "File export details";
		}

		public boolean hasEditor()
		{
			return false;
		}

	}

	ExportDataItem _myWrapper;

	Text secondNameText;

	protected ExportFlatFileDataPage(ISelection selection, String helpContext)
	{
		super(selection, NAME, "Export to flat file data",
				"This page lets you enter further details to support the flat file export",
				"images/grid_wizard.gif", helpContext, false);

		_myWrapper = new ExportDataItem();
	}

	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors =
		{ prop("FilePath", "where to place the exported file", getEditable()),
				prop("SensorType", "the type of sensor", getEditable()) };
		return descriptors;
	}

	protected Editable createMe()
	{
		return _myWrapper;
	}

}
