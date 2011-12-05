package org.mwc.debrief.core.wizards;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
	/**
	 * the type of the first sensor
	 * 
	 */
	protected String _sensorType1;

	/**
	 * the type of the second sensor
	 * 
	 */
	protected String _sensorType2;

	/** the protective marking on the data
	 * 
	 */
	private String _protMarking;
	
	/** the name of when data was recorded
	 * 
	 */
	private String _serialName;

	
	private DirectoryFieldEditor _fileFieldEditor;

	private RadioGroupFieldEditor _sensor1TypeEditor;
	private RadioGroupFieldEditor _sensor2TypeEditor;
	private StringFieldEditor _protMarkingEditor;
	private StringFieldEditor _serialNameEditor;

	/**
	 * how many sensors to support
	 * 
	 */
	private int _numSensors;


	public static final String FILE_SUFFIX = "txt";

	private static final String SINGLE_SENSOR = "This wizard allows you to indicate the type of sensor used, and the "
			+ "directory\nin which to place the output file. "
			+ "The output file will take the name of the primary \nfile with a "
			+ FILE_SUFFIX
			+ " suffix added. See online help for more details on the export format.";

	private static final String DOUBLE_SENSOR = "This wizard allows you to indicate the type of sensors used, " +
			"and the "
			+ "directory\nin which to place the output file. "
			+ "The output file will take the name of the primary \nfile with a "
			+ FILE_SUFFIX
			+ " suffix added. See online help for more details on the export format.";

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public FlatFilenameWizardPage(int numSensors)
	{
		super(PAGENAME);
		_numSensors = numSensors;
		setTitle("Export data to flat file");
		final String msgStr;
		if(numSensors == 1)
			msgStr = SINGLE_SENSOR;
		else
			msgStr = DOUBLE_SENSOR;
		setDescription(msgStr);
		
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
		String sensor1Key = "Debrief.FlatFileSensorType1";
		String sensor2Key = "Debrief.FlatFileSensorType2";
		String protMarkKey = "Debrief.FlatFileProtMarking";
		String serialKey = "Debrief.FlatFileSerialName";

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

			@Override
			protected boolean doCheckState()
			{
				return _filePath != null;
			}
		};
		_fileFieldEditor.fillIntoGrid(container, 3);
		_fileFieldEditor.setPreferenceStore(getPreferenceStore());
		_fileFieldEditor.setPage(this);
		_fileFieldEditor.setEmptyStringAllowed(false);
		_fileFieldEditor.load();

		// store the current editor value
		_filePath = _fileFieldEditor.getStringValue();

		// and the sensor type
		final String[][] sensorTypes = new String[][]
		{
		{ "Towed Array", "T" },
		{ "Hull mounted array", "H" } };

		// sort out the first sensor
		_sensor1TypeEditor = new RadioGroupFieldEditor(sensor1Key,
				"Sensor 1 type:", 2, sensorTypes, container)
		{
			protected void fireValueChanged(String property, Object oldValue,
					Object newValue)
			{
				super.fireValueChanged(property, oldValue, newValue);
				_sensorType1 = (String) newValue;
				dialogChanged();
			}
		};
		_sensor1TypeEditor.setPreferenceStore(getPreferenceStore());
		_sensor1TypeEditor.setPage(this);
		_sensor1TypeEditor.load();
		_sensorType1 = sensorTypes[0][1];
		
		@SuppressWarnings("unused")
		Label lbl = new Label(container,SWT.None);


		// and now the second sensor
		if (_numSensors > 1)
		{
			_sensor2TypeEditor = new RadioGroupFieldEditor(sensor2Key,
					"Sensor 2 type:", 2, sensorTypes, container)
			{
				protected void fireValueChanged(String property, Object oldValue,
						Object newValue)
				{
					super.fireValueChanged(property, oldValue, newValue);
					_sensorType2 = (String) newValue;
					dialogChanged();
				}
			};
			_sensor2TypeEditor.setPreferenceStore(getPreferenceStore());
			_sensor2TypeEditor.setPage(this);
			_sensor2TypeEditor.load();
			_sensorType2 = sensorTypes[0][1];
			
			@SuppressWarnings("unused")
			Label lbl2 = new Label(container,SWT.None);
			
			// we also want to specify the prot marking editor
			_protMarkingEditor = new StringFieldEditor(protMarkKey, "Protective Marking:",  container)
			{
				protected void fireValueChanged(String property, Object oldValue,
						Object newValue)
				{
					super.fireValueChanged(property, oldValue, newValue);
					_protMarking = (String) newValue;
					dialogChanged();
				}
				

				@Override
				protected boolean doCheckState()
				{
					return _protMarking != null;
				}
			};
			_protMarkingEditor.setEmptyStringAllowed(false);
			_protMarkingEditor.setPreferenceStore(getPreferenceStore());
			_protMarkingEditor.setPage(this);
			_protMarkingEditor.setErrorMessage("A value for protective marking must be supplied");
			_protMarkingEditor.load();

			_protMarking = "PENDING";

			@SuppressWarnings("unused")
			Label lbl3 = new Label(container,SWT.None);
			

		}

		// we also want to specify the serial nane (for single or double sensors)
		_serialNameEditor = new StringFieldEditor(serialKey, "Serial name:",  container)
		{
			protected void fireValueChanged(String property, Object oldValue,
					Object newValue)
			{
				super.fireValueChanged(property, oldValue, newValue);
				_serialName = (String) newValue;
				dialogChanged();
			}
			
			@Override
			protected boolean doCheckState()
			{
				return _serialName != null;
			}
			
		};
		_serialNameEditor.setPreferenceStore(getPreferenceStore());
		_serialNameEditor.setPage(this);
		_serialNameEditor.setEmptyStringAllowed(false);
		_serialNameEditor.setErrorMessage("A value for serial name must be supplied");
		_serialNameEditor.load();
		_serialName = "PENDING";
		

		
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
		if (!testFile.isDirectory())
		{
			updateStatus("Target must be a directory, not a file");
			return;
		}

		final String sensorType1 = getSensor1Type();
		if (sensorType1 == null)
		{
			updateStatus("Sensor 1 type must be selected");
			return;
		}

		final String sensorType2 = getSensor2Type();
		if (sensorType2 == null)
		{
			updateStatus("Sensor 2 type must be selected");
			return;
		}

		// so, we've got valid data. better store them
		_sensor1TypeEditor.store();
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
	public String getSensor1Type()
	{
		return _sensorType1;
	}

	/**
	 * retrieve the selected sensor type
	 * 
	 * @return
	 */
	public String getSensor2Type()
	{
		return _sensorType2;
	}

	/** get the protective marking on the data
	 * 
	 * @return
	 */
	public String getProtMarking()
	{
		return _protMarking;
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

	public String getSerialName()
	{
		return _serialName;
	}

}