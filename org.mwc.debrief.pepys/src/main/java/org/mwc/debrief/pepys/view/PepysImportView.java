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

package org.mwc.debrief.pepys.view;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.mwc.debrief.pepys.model.AbstractConfiguration;

public class PepysImportView extends Dialog{
	
	Label startLabel;
	Label endLabel;
	Label topLeftLabel;
	Label bottomRightLabel;
	
	Button applyButton;
	Button importButton;
	Button useCurrentViewport;
	
	TreeViewer tree;
	
	
	
	public PepysImportView(final AbstractConfiguration model, Shell parent) {
		super(parent);
		
		

		initGUI(model, parent);
		
	}


	public void initGUI(final AbstractConfiguration model, final Shell parent) {
		this.applyButton = new Button(parent, SWT.PUSH);
		this.applyButton.setText("Apply");
		
		this.importButton = new Button(parent, SWT.PUSH);
		this.importButton.setText("Import");
		
		this.useCurrentViewport = new Button(parent, SWT.PUSH);
		this.useCurrentViewport.setText("Use current viewport");
		
		this.startLabel = new Label(parent, SWT.PUSH);
		this.startLabel.setText("Start:");
		
		this.endLabel = new Label(parent, SWT.BORDER);
		this.endLabel.setText("End:");
		
		this.topLeftLabel = new Label(parent, SWT.BORDER);
		this.topLeftLabel.setText("Top Left:");
		
		this.bottomRightLabel = new Label(parent, SWT.BORDER);
		this.bottomRightLabel.setText("Bottom Right");
		
		this.tree = new TreeViewer(parent, SWT.V_SCROLL);
		this.tree.setContentProvider(new PepysContentProvider());
		this.tree.setLabelProvider(new PepysLabelProvider());
		
		try {
			model.apply();
		} catch (Exception e) {
			// TODO DO SOME ALERT HERE FOR THE USER
			e.printStackTrace();
		}
		this.tree.setInput(model.getTreeModel());
		
	}
}
