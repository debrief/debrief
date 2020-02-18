/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ayesha
 *
 */
public class SelectNarrativeTypesDialog extends Dialog {

	private Set<String> types;
	private Button[] typesCheck;
	private Button selectAllRadio;
	private Button deselectAllRadio;
	private List<String> selectedTypes;
	public SelectNarrativeTypesDialog(Shell parentShell,Set<String> narrativeTypes) {
		super(parentShell);
		this.types = narrativeTypes;
		
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Narrative Types");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Control control =  super.createDialogArea(parent);
		Composite component = new Composite((Composite)control,SWT.NONE);
		component.setLayout(new GridLayout());
		Label lblHeading = new Label(component,SWT.NONE);
		lblHeading.setText("Select the narrative types to import:");
		Composite radioGroupComposite = new Composite(component,SWT.NONE);
		radioGroupComposite.setLayout(new GridLayout(2,true));
		selectAllRadio = new Button(radioGroupComposite,SWT.RADIO);
		selectAllRadio.setText("Select All");
		deselectAllRadio = new Button(radioGroupComposite,SWT.RADIO);
		deselectAllRadio.setText("Deselect All");
		Composite typesComposite = new Composite(component,SWT.NONE);
		typesComposite.setLayout(new GridLayout(1,true));
		
		typesCheck = new Button[types.size()];
		int i=0;
		
		for(String type:types)
		{
			typesCheck[i]=new Button(component,SWT.CHECK);
			typesCheck[i].setText(type);
			i++;
		}
		selectAllRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Button button:typesCheck)
				{
					button.setSelection(true);
				}
			}
		});
		deselectAllRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Button button:typesCheck)
				{
					button.setSelection(false);
				}
			}
		});
		return component;
	}
	@Override
	protected void okPressed() {
		selectedTypes = new ArrayList<>();
		for(Button button:typesCheck)
		{
			if(button.getSelection()) {
				selectedTypes.add(button.getText());
			}
		}
		super.okPressed();
	}
	
	public List<String> getSelectedTypes() {
		return selectedTypes;
	}

}
