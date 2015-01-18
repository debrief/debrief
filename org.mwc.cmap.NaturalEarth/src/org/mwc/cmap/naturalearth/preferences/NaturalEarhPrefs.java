package org.mwc.cmap.naturalearth.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.naturalearth.Activator;

public class NaturalEarhPrefs extends PreferencePage implements
		IWorkbenchPreferencePage
{

	public static final String ID = "org.mwc.cmap.naturalearth.preferences.NaturalEarhPrefs";
	private Text dataFolderText;
	
	public NaturalEarhPrefs()
	{
		super("Natural Earth");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		composite.setLayoutData(gd);
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);

		Label dataFolderLabel = new Label(composite, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		dataFolderLabel.setLayoutData(gd);
		dataFolderLabel.setText("Data Folder:");

		dataFolderText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		dataFolderText.setLayoutData(gd);
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String dataFolderValue = store.getString(PreferenceConstants.DATA_FOLDER);
		dataFolderText.setText(dataFolderValue == null ? "" : dataFolderValue);

		final Button dataFolderBrowse = new Button(composite, SWT.PUSH);
		dataFolderBrowse.setText("Browse...");
		dataFolderBrowse.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SINGLE);
				String value = dataFolderText.getText();
				if (value.trim().length() == 0)
				{
					value = Platform.getLocation().toOSString();
				}
				dialog.setFilterPath(value);

				String result = dialog.open();
				if (result == null || result.trim().length() == 0)
				{
					return;
				}
				dataFolderText.setText(result);

			}

		});

		Group stylesGroup = new Group(composite, SWT.NONE);
		layout = new GridLayout(2, false);
		stylesGroup.setLayout(layout);
		stylesGroup.setText("Styles");
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 3;
		stylesGroup.setLayoutData(gd);

		return composite;
	}

	@Override
	protected void performDefaults()
	{
		dataFolderText.setText(""); //$NON-NLS-1$
		storePreferences();
		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		storePreferences();
		return super.performOk();
	}

	private void storePreferences()
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String value = dataFolderText.getText();
		if (value != null)
		{
			store.setValue(PreferenceConstants.DATA_FOLDER, value);
		}
	}

	class StylesLabelProvider extends LabelProvider
	{

		@Override
		public Image getImage(Object element)
		{
			return null;
		}


	}

}