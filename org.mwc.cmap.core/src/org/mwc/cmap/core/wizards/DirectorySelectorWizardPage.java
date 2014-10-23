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

import java.io.File;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.CorePlugin;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class DirectorySelectorWizardPage extends WizardPage
{

	protected String _filePath;

	private DirectoryFieldEditor _fileFieldEditor;

	private final String _helpContext;

	public static final String FILE_SUFFIX = "txt";

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public DirectorySelectorWizardPage(final String PAGE_ID, final String title,
			final String description, final String pluginName, final String iconPath, final String helpContext)
	{
		super(PAGE_ID);
		setTitle(title);
		setDescription(description);
		_helpContext = helpContext;
		super.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				pluginName, iconPath));
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(final Composite parent)
	{

		if (_helpContext != null)
		{
			// declare our context sensitive help
			CorePlugin.declareContextHelp(parent, _helpContext);
		}

		final Composite container = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		final String filenameKey = super.getName();

		final String title = "Output directory:";
		_fileFieldEditor = new DirectoryFieldEditor(filenameKey, title, container)
		{
			protected void fireValueChanged(final String property, final Object oldValue,
					final Object newValue)
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

		final GridLayout urlLayout = (GridLayout) container.getLayout();
		urlLayout.numColumns = 3;

		container.layout();
		setControl(container);
	}

	private IPreferenceStore getPreferenceStore()
	{
		return CorePlugin.getDefault().getPreferenceStore();
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
		final File testFile = new File(targetDir);
		if (!testFile.isDirectory())
		{
			updateStatus("Target must be a directory, not a file");
			return;
		}

		// so, we've got valid data. better store them
		_fileFieldEditor.store();

		updateStatus(null);
	}

	public String getFileName()
	{
		return _filePath;
	}

	private void updateStatus(final String message)
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