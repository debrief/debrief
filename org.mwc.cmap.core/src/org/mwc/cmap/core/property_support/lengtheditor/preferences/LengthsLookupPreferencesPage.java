/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.property_support.lengtheditor.preferences;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.lengtheditor.Messages;

/**
 * There are two ways to load data from a file on the preference page:
 * <ul>
 * <li>directly by "Update now" button
 * <li>on storing file name to preference store
 * </ul>
 * 
 */
public class LengthsLookupPreferencesPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage
{

	private static final String CONTEXT_ID = "org.mwc.debrief.help.LengthPrefs";

	/**
	 * extension filters for file selection dialog
	 */
	private static final String[] availableExtensions = new String[]
	{ "*.csv" };//$NON-NLS-1$

	private FileFieldEditor myFileEditor;

	public LengthsLookupPreferencesPage()
	{
		super(GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
	}

	public void init(final IWorkbench workbench)
	{
	}

	@Override
	protected void createFieldEditors()
	{
		addFileEditor();
		addOpenFileHyperlink();
		addReloadButton();
		addOpenHelpHyperlink();

		// and the context-sensitive help
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), CONTEXT_ID);
	}

	private void addFileEditor()
	{
		final Composite parent = getFieldEditorParent();
		myFileEditor = new FileFieldEditor(LengthsRegistry.FILE_NAME,
				Messages.LengthsLookupPreferencesPage_FileLabel, true,
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, parent)
		{

			@Override
			protected void doStore()
			{
				super.doStore();
				reloadDataFromFile();
			}
		};
		myFileEditor
				.setErrorMessage(Messages.LengthsLookupPreferencesPage_InvalidFileName);
		myFileEditor.setFileExtensions(availableExtensions);
		addField(myFileEditor);
	}

	private void addReloadButton()
	{
		final Composite parent = getFieldEditorParent();
		final Button reloadButton = new Button(parent, SWT.PUSH);
		reloadButton.setText(Messages.LengthsLookupPreferencesPage_UpdateNow);
		reloadButton.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(final SelectionEvent e)
			{
				reloadDataFromFile();
			}

			public void widgetDefaultSelected(final SelectionEvent e)
			{
				// nothing
			}
		});

		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		widthHint = Math.max(widthHint, reloadButton.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, true).x);

		GridDataFactory.fillDefaults().hint(widthHint, SWT.DEFAULT).align(SWT.FILL,
				SWT.CENTER).applyTo(reloadButton);
	}

	private void reloadDataFromFile()
	{
		LengthsRegistry.getRegistry().setFileName(getFileName());
		LengthsRegistry.getRegistry().reload();
	}

	private void addOpenFileHyperlink()
	{
		final Composite parent = getFieldEditorParent();

		final Link link = new Link(parent, SWT.NONE);
		link.setText(Messages.LengthsLookupPreferencesPage_OpenFileLabel);
		link.addListener(SWT.Selection, new Listener()
		{

			public void handleEvent(final Event event)
			{
				openSystemEditor();
			}
		});

		int numColumns = ((GridLayout) parent.getLayout()).numColumns;
		// skip last cell for 'reload' button
		if (numColumns > 1)
		{
			numColumns--;
		}
		GridDataFactory.fillDefaults().span(numColumns, 1).align(SWT.BEGINNING,
				SWT.CENTER).applyTo(link);
	}

	private void addOpenHelpHyperlink()
	{
		final Composite parent = getFieldEditorParent();

		final Button helpBtn = new Button(parent, SWT.NONE);
		helpBtn.setText("Find out more about sensor offsets");
		helpBtn.addListener(SWT.Selection, new Listener()
		{

			public void handleEvent(final Event event)
			{
				PlatformUI.getWorkbench().getHelpSystem().displayHelp(CONTEXT_ID);
			}
		});

		int numColumns = ((GridLayout) parent.getLayout()).numColumns;
		// skip last cell for 'reload' button
		if (numColumns > 1)
		{
			numColumns--;
		}
		GridDataFactory.fillDefaults().span(numColumns, 1).align(SWT.BEGINNING,
				SWT.CENTER).applyTo(helpBtn);
	}

	/** if we have a file specified, open it - else throw error
	 * 
	 */
	private void openSystemEditor()
	{
		final String fileName = getFileName();
		if ((fileName == null)||(fileName.length() == 0))
		{
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Edit Array Offsets file",
					"Array offsets file not assigned, \nplease create file as descried in Debrief help (link button below)," +
					"\nand select it using the above Browse button");
		}
		else
		{
			final File file = new File(fileName);
			try
			{
				Desktop.getDesktop().open(file);
			}
			catch (final IOException e)
			{
				CorePlugin.logError(Status.ERROR,
						Messages.LengthsLookupPreferencesPage_ErrorOnOpenFileEditor, e);
			}
		}
	}

	private String getFileName()
	{
		return myFileEditor.getStringValue();
	}
}
