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
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
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

/**
 * @author Ayesha
 *
 */
public class SelectNarrativeTypesDialog extends Dialog {

	private Map<String,Integer> types;
	private Button[] typesCheck;
	private Button selectAllCheck;
	private List<String> selectedTypes;
	public SelectNarrativeTypesDialog(Shell parentShell,Map<String,Integer> narrativeTypes) {
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
		Composite control =  (Composite)super.createDialogArea(parent);
		control.setLayout(new GridLayout());
		Composite headingComposite = new Composite(control,SWT.NONE);
		Label lblHeading = new Label(headingComposite,SWT.NONE);
		headingComposite.setLayout(new GridLayout());
		lblHeading.setText("Select the narrative types to import:");
		lblHeading.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		ScrolledComposite scrolledComposite = new ScrolledComposite(control,SWT.V_SCROLL|SWT.BORDER);
		Composite component = createCheckboxes(scrolledComposite);
		scrolledComposite.setContent(component);
		scrolledComposite.setExpandVertical( true );
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setMinSize( 250, 250 );
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

	@Override
	protected void okPressed() {
		selectedTypes = new ArrayList<>();
		for(Button button:typesCheck)
		{
			if(button.getSelection()) {
				String text = button.getText();
				selectedTypes.add(text.substring(0,text.indexOf("(")));
			}
		}
		super.okPressed();
	}
	
	public List<String> getSelectedTypes() {
		return selectedTypes;
	}

}
