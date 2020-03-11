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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.mwc.debrief.pepys.model.AbstractConfiguration;
import org.mwc.debrief.pepys.nebula.PShelf;
import org.mwc.debrief.pepys.nebula.PShelfItem;
import org.mwc.debrief.pepys.nebula.RedmondShelfRenderer;

public class PepysImportView extends Dialog {

	private Label startLabel;
	private Label endLabel;
	private Label topLeftLabel;
	private Label bottomRightLabel;
	private Label titleLabel;

	private Button applyButton;
	private Button importButton;
	private Button useCurrentViewport;

	private CDateTime startDate;
	private CDateTime startTime;
	private CDateTime endDate;
	private CDateTime endTime;

	private PShelf shelf;
	private TreeViewer tree;
	
	private ArrayList<Button> dataTypesCheckBox = new ArrayList<Button>();
	
	private Composite dataTypesComposite;

	public PepysImportView(final AbstractConfiguration model, Shell parent) {
		super(parent);

		initGUI(model, parent);

	}

	public void initGUI(final AbstractConfiguration model, final Shell parent) {
		GridLayout mainLayout = new GridLayout();
		mainLayout.numColumns = 2;
		mainLayout.marginWidth = 20;
		mainLayout.marginHeight = 20;
		parent.setLayout(mainLayout);

		this.titleLabel = new Label(parent, SWT.NONE);
		this.titleLabel.setText("Pepys Import");
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		titleLabel.setLayoutData(gridData);

		this.shelf = new PShelf(parent, SWT.BORDER);
		this.shelf.setRenderer(new RedmondShelfRenderer());
		final GridData shelfGridData = new GridData();
		shelfGridData.verticalAlignment = GridData.FILL;
		shelfGridData.grabExcessVerticalSpace = true;
		this.shelf.setLayoutData(shelfGridData);

		// TIME PERIOD
		final PShelfItem timePeriodItem = new PShelfItem(shelf, SWT.NONE);
		timePeriodItem.setText("Time Period");
		GridLayout timePeriodLayout = new GridLayout();
		timePeriodLayout.numColumns = 2;
		timePeriodItem.getBody().setLayout(timePeriodLayout);

		this.startLabel = new Label(timePeriodItem.getBody(), SWT.PUSH);
		this.startLabel.setText("Start:");
		this.startLabel.setLayoutData(gridData);

		this.startDate = new CDateTime(timePeriodItem.getBody(), CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_SHORT);
		this.startDate.setPattern("dd/MM/yyyy");

		this.startTime = new CDateTime(timePeriodItem.getBody(), CDT.BORDER | CDT.SPINNER | CDT.TIME_MEDIUM);

		this.endLabel = new Label(timePeriodItem.getBody(), SWT.NONE);
		this.endLabel.setText("End:");
		this.endLabel.setLayoutData(gridData);

		this.endDate = new CDateTime(timePeriodItem.getBody(), CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_SHORT);
		this.endDate.setPattern("dd/MM/yyyy");
		this.endTime = new CDateTime(timePeriodItem.getBody(), CDT.BORDER | CDT.SPINNER | CDT.TIME_MEDIUM);
		
		// AREA
		final PShelfItem areaItem = new PShelfItem(shelf, SWT.NONE);
		areaItem.setText("Area");
		areaItem.getBody().setLayout(new FillLayout(SWT.VERTICAL));

		this.topLeftLabel = new Label(areaItem.getBody(), SWT.NONE);
		this.topLeftLabel.setText("Top Left:");

		this.bottomRightLabel = new Label(areaItem.getBody(), SWT.NONE);
		this.bottomRightLabel.setText("Bottom Right");

		this.useCurrentViewport = new Button(areaItem.getBody(), SWT.PUSH);
		this.useCurrentViewport.setText("Use current viewport");
		
		final PShelfItem dataTypeItem = new PShelfItem(shelf, SWT.NONE);
		dataTypeItem.setText("Data Type");
		dataTypeItem.getBody().setLayout(new FillLayout(SWT.VERTICAL));
		dataTypesComposite = dataTypeItem.getBody();
		
		final GridData treeGrid = new GridData(GridData.FILL_BOTH);
		treeGrid.horizontalAlignment = GridData.FILL;
		treeGrid.grabExcessHorizontalSpace = true;
		treeGrid.verticalAlignment = GridData.FILL;
		treeGrid.grabExcessVerticalSpace = true;
		this.tree = new TreeViewer(parent, SWT.BORDER);
		this.tree.setContentProvider(new PepysContentProvider());
		this.tree.setLabelProvider(new PepysLabelProvider());
		this.tree.getTree().setLayoutData(treeGrid);
		
		final GridData applyGridDataButton = new GridData();
		applyGridDataButton.horizontalAlignment = GridData.END;
		this.applyButton = new Button(parent, SWT.PUSH);
		this.applyButton.setText("Apply");
		this.applyButton.setLayoutData(applyGridDataButton);

		final GridData importGridDataButton = new GridData();
		importGridDataButton.horizontalAlignment = GridData.END;
		importGridDataButton.minimumWidth = 150;
		this.importButton = new Button(parent, SWT.PUSH);
		this.importButton.setText("Import");
		this.importButton.setLayoutData(importGridDataButton);
		

	}

	public CDateTime getStartDate() {
		return startDate;
	}

	public CDateTime getStartTime() {
		return startTime;
	}

	public CDateTime getEndDate() {
		return endDate;
	}

	public CDateTime getEndTime() {
		return endTime;
	}

	public TreeViewer getTree() {
		return tree;
	}

	public ArrayList<Button> getDataTypesCheckBox() {
		return dataTypesCheckBox;
	}

	public Composite getDataTypesComposite() {
		return dataTypesComposite;
	}
	
	
}
