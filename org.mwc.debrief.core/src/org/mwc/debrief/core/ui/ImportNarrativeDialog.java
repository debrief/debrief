/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
/**
 *
 */
package org.mwc.debrief.core.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.preferences.PrefsPage.PreferenceConstants;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument.ImportNarrativeEnum;

/**
 * Dialog popped up from {@link ImportNarrativeHelper}
 *
 * @author Ayesha
 *
 */
public class ImportNarrativeDialog extends Dialog {
	private Button _btnLoadedTracks;
	private boolean preference;
	private Map<String,Integer> types;
	private Button[] typesCheck;
	private Button selectAllCheck;
	private List<String> selectedTypes = new ArrayList<>();;

	@Override
	protected boolean isResizable() {
	    return true;
	}
	
	public ImportNarrativeDialog(final Shell parentShell,Map<String,Integer> narrativeTypes) {
		super(parentShell);
		this.types = narrativeTypes;
	}

	@Override
	protected void configureShell(final Shell newShell) {
		newShell.setText("Import Narrative Entries");

		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite control = (Composite) super.createDialogArea(parent);
		final Composite composite = new Composite(control, SWT.BORDER);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Label title = new Label(composite, SWT.BOLD);
		FontDescriptor descriptor = FontDescriptor.createFrom(title.getFont());
		// setStyle method returns a new font descriptor for the given style
		descriptor = descriptor.setStyle(SWT.BOLD);
		title.setFont(descriptor.createFont(title.getDisplay()));
		title.setText("Loading narrative data.");

		_btnLoadedTracks = new Button(composite, SWT.CHECK);
		_btnLoadedTracks.setText("Trim Narrative Entries to the  period of currently loaded tracks");
		_btnLoadedTracks.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		

		new Label(composite, SWT.NONE).setLayoutData(new GridData(GridData.FILL));
		_btnLoadedTracks.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				preference = _btnLoadedTracks.getSelection();
				CorePlugin.getDefault().getPreferenceStore()
						.setValue(PreferenceConstants.REUSE_TRIM_NARRATIVES_DIALOG_CHOICE, preference);
			}
		});
		preference = CorePlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.REUSE_TRIM_NARRATIVES_DIALOG_CHOICE);
		_btnLoadedTracks.setSelection(preference);
		Composite headingComposite = new Composite(control,SWT.NONE);
		Label lblHeading = new Label(headingComposite,SWT.NONE);
		headingComposite.setLayout(new GridLayout());
		lblHeading.setText("Select the narrative types to import:");
		lblHeading.setFont(descriptor.createFont(title.getDisplay()));
		lblHeading.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		ScrolledComposite scrolledComposite = new ScrolledComposite(control,SWT.V_SCROLL|SWT.BORDER);
		Composite component = createCheckboxes(scrolledComposite);
		scrolledComposite.setContent(component);
		scrolledComposite.setExpandVertical( true );
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setMinSize( 250, 75 );
		scrolledComposite.addListener( SWT.Resize, event -> {
		      int width = scrolledComposite.getClientArea().width;
		      scrolledComposite.setMinSize( parent.computeSize( width, SWT.DEFAULT ) );
		    } );
		GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
	    scrolledComposite.setLayoutData( gridData );
		selectAllCheck = new Button(control,SWT.CHECK);
		selectAllCheck.setText("Select All/None");
		selectAllCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Button button:typesCheck)
				{
					button.setSelection(selectAllCheck.getSelection());
				}
			}
		});
		return control;
	}
	
	private Composite createCheckboxes(Composite scrolledComposite) {
		Composite component = new Composite(scrolledComposite,SWT.NONE);
		component.setLayout(new GridLayout());
		
		Composite typesComposite = new Composite(component,SWT.NONE);
		typesComposite.setLayout(new GridLayout(2,true));
		typesCheck = new Button[types.size()];
		int i=0;
		
		for(String type:types.keySet())
		{
			typesCheck[i]=new Button(typesComposite,SWT.CHECK);
			typesCheck[i].setText(type+"("+types.get(type)+")");
			typesCheck[i].setLayoutData(new GridData(SWT.FILL));
			i++;
		}
		return component;
		
	}

	public boolean getPreference() {
		return preference;
	}

	public ImportNarrativeEnum getUserChoice() {
		if(preference) {
			return ImportNarrativeEnum.TRIMMED_DATA;
		}
		return ImportNarrativeEnum.ALL_DATA;
	}

	public List<String> getSelectedNarrativeTypes() {
		return selectedTypes;
	}
	
	@Override
	protected void okPressed() {
		for(Button button:typesCheck)
		{
			if(button.getSelection()) {
				String text = button.getText();
				selectedTypes.add(text.substring(0,text.indexOf("(")));
			}
		}
		super.okPressed();
	}

}
