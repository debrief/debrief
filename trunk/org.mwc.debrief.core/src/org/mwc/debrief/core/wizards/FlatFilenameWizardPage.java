package org.mwc.debrief.core.wizards;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.debrief.core.DebriefPlugin;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class FlatFilenameWizardPage extends WizardPage
{

	public static final String PAGENAME = "FlatFileExport";

	protected String _filePath;
	protected String _sensorType;

	private DirectoryFieldEditor _fileFieldEditor;

	private RadioGroupFieldEditor _sensorTypeEditor;

	public static final String FILE_SUFFIX = "txt";

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public FlatFilenameWizardPage()
	{
		super(PAGENAME);
		setTitle("Export data to flat file");
		setDescription("This wizard allows you to indicate the type of sensor used, and the "
				+ "directory\nin which to place the output file. "
				+ "The output file will take the name of the primary \nfile with a "
				+ FILE_SUFFIX
				+ " suffix added. See online help for more details on the export format.");
		super.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				"org.mwc.debrief.core", "images/newplot_wizard.gif"));
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent)
	{

		final Composite container = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		String filenameKey = "Debrief.FlatFileOutput";
		String sensorKey = "Debrief.FlatFileSensorType";

		String title = "Output directory:";
		_fileFieldEditor = new DirectoryFieldEditor(filenameKey, title, container)
		{
			protected void fireValueChanged(String property, Object oldValue,
					Object newValue)
			{
				super.fireValueChanged(property, oldValue, newValue);

				if (property.equals("field_editor_value"))
				{
					// tell the ui to update itself
					_filePath = (String) newValue;
				}
				dialogChanged();

			}
		};
		_fileFieldEditor.fillIntoGrid(container, 3);
		_fileFieldEditor.setPreferenceStore(getPreferenceStore());
		_fileFieldEditor.load();

		// store the current editor value
		_filePath = _fileFieldEditor.getStringValue();

		// and the sensor type
		final String[][] sensorTypes = new String[][]
		{
		{ "Towed Array", "T" },
		{ "Hull mounted array", "H" } };

		_sensorTypeEditor = new RadioGroupFieldEditor(sensorKey, "Sensor type:", 2,
				sensorTypes, container)
		{
			protected void fireValueChanged(String property, Object oldValue,
					Object newValue)
			{
				super.fireValueChanged(property, oldValue, newValue);
				_sensorType = (String) newValue;
				dialogChanged();
			}
		};

		_sensorTypeEditor.setPreferenceStore(getPreferenceStore());
		_sensorTypeEditor.load();
		_sensorType = sensorTypes[0][1];

		GridLayout urlLayout = (GridLayout) container.getLayout();
		urlLayout.numColumns = 3;

		container.layout();
		setControl(container);
	}

	private IPreferenceStore getPreferenceStore()
	{
		return DebriefPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Ensures that both text fields are set.
	 */

	void dialogChanged()
	{

		final String targetDir = getFileName();

		if ((targetDir == null) || (targetDir.length() == 0))
		{
			updateStatus("Target directory must be specified");
			return;
		}
		
		// just check it's a directory, not a file
		File testFile = new File(targetDir);
		if(!testFile.isDirectory())
		{
			updateStatus("Target must be a directory, not a file");
			return;
		}

		final String sensorType = getSensorType();
		if (sensorType == null)
		{
			updateStatus("Sensor type must be selected");
			return;
		}

		// so, we've got valid data. better store them
		_sensorTypeEditor.store();
		_fileFieldEditor.store();

		updateStatus(null);
	}

	public String getFileName()
	{
		return _filePath;
	}

	/**
	 * retrieve the selected sensor type
	 * 
	 * @return
	 */
	public String getSensorType()
	{
		return _sensorType;
	}

	private void updateStatus(String message)
	{
		setErrorMessage(message);
		if (message == null)
		{
			this.setMessage("Press Finish to complete export",
					IMessageProvider.INFORMATION);
			setPageComplete(true);
		}
		else
			setPageComplete(false);
	}
}