package org.mwc.cmap.core.property_support.lengtheditor.preferences;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
		implements IWorkbenchPreferencePage {

	private static final String CONTEXT_ID = "org.mwc.debrief.help.LengthPrefs";

	/**
	 * extension filters for file selection dialog
	 */
	private static final String[] availableExtensions = new String[] { "*.csv" };//$NON-NLS-1$

	private FileFieldEditor myFileEditor;

	public LengthsLookupPreferencesPage() {
		super(GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		addFileEditor();
		addOpenFileHyperlink();
		addReloadButton();
		addOpenHelpHyperlink();

		// and the context-sensitive help
		PlatformUI.getWorkbench().getHelpSystem().setHelp(
				getControl(), CONTEXT_ID);
	}

	private void addFileEditor() {
		Composite parent = getFieldEditorParent();
		myFileEditor = new FileFieldEditor(LengthsRegistry.FILE_NAME,
				Messages.LengthsLookupPreferencesPage_FileLabel, true,
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, parent) {

			@Override
			protected void doStore() {
				super.doStore();
				reloadDataFromFile();
			}
		};
		myFileEditor
				.setErrorMessage(Messages.LengthsLookupPreferencesPage_InvalidFileName);
		myFileEditor.setFileExtensions(availableExtensions);
		addField(myFileEditor);
	}

	private void addReloadButton() {
		Composite parent = getFieldEditorParent();
		Button reloadButton = new Button(parent, SWT.PUSH);
		reloadButton.setText(Messages.LengthsLookupPreferencesPage_UpdateNow);
		reloadButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				reloadDataFromFile();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing
			}
		});

		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		widthHint = Math.max(widthHint, reloadButton.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, true).x);

		GridDataFactory.fillDefaults().hint(widthHint, SWT.DEFAULT).align(
				SWT.FILL, SWT.CENTER).applyTo(reloadButton);
	}

	private void reloadDataFromFile() {
		LengthsRegistry.getRegistry().setFileName(getFileName());
		LengthsRegistry.getRegistry().reload();
	}

	private void addOpenFileHyperlink() {
		Composite parent = getFieldEditorParent();

		Link link = new Link(parent, SWT.NONE);
		link.setText(Messages.LengthsLookupPreferencesPage_OpenFileLabel);
		link.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				openSystemEditor();
			}
		});

		int numColumns = ((GridLayout) parent.getLayout()).numColumns;
		// skip last cell for 'reload' button
		if (numColumns > 1) {
			numColumns--;
		}
		GridDataFactory.fillDefaults().span(numColumns, 1).align(SWT.BEGINNING,
				SWT.CENTER).applyTo(link);
	}

	private void addOpenHelpHyperlink() {
		Composite parent = getFieldEditorParent();

		Button helpBtn = new Button(parent, SWT.NONE);
		helpBtn.setText("Find out more about sensor offsets");
		helpBtn.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				PlatformUI.getWorkbench().getHelpSystem().displayHelp(
						CONTEXT_ID);
			}
		});

		int numColumns = ((GridLayout) parent.getLayout()).numColumns;
		// skip last cell for 'reload' button
		if (numColumns > 1) {
			numColumns--;
		}
		GridDataFactory.fillDefaults().span(numColumns, 1).align(SWT.BEGINNING,
				SWT.CENTER).applyTo(helpBtn);
	}

	private void openSystemEditor() {
		String fileName = getFileName();
		if (fileName == null) {
			fileName = ""; //$NON-NLS-1$
		}
		File file = new File(fileName);
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			CorePlugin
					.logError(
							Status.ERROR,
							Messages.LengthsLookupPreferencesPage_ErrorOnOpenFileEditor,
							e);
		}
	}

	private String getFileName() {
		return myFileEditor.getStringValue();
	}
}
