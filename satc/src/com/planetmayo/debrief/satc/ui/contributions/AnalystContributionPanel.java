package com.planetmayo.debrief.satc.ui.contributions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

import com.planetmayo.debrief.satc.ui.UIUtils;
import com.planetmayo.debrief.satc.ui.widgets.ExpandButton;

public abstract class AnalystContributionPanel {
	
	protected Composite controlParent;
	
	protected Group mainGroup;
	protected Group bodyGroup;
	
	protected ExpandButton expandButton;
	protected Button activeCheckBox;
	protected Label hardConstraintLabel;
	protected Label estimateLabel;
	protected Spinner weightSpinner;
	
	protected DateTime startDate;
	protected DateTime startTime;
	protected DateTime endDate;
	protected DateTime endTime;	

	public AnalystContributionPanel(Composite parent) {
		this.controlParent = parent;
	}
	
	protected void initUI() {
		GridLayout layout = UIUtils.createGridLayoutWithoutMargins(1, false);
		layout.verticalSpacing = 0;
		mainGroup = new Group(controlParent, SWT.SHADOW_ETCHED_IN);
		mainGroup.setText("sssss");
		mainGroup.setLayout(layout);
		
		createHeader(mainGroup);		
		createBody(mainGroup);
		createLimitAndEstimateSliders();
		bindValues();
	}
	
	protected void createHeader(Composite parent) {
		Composite header = new Composite(parent, SWT.NONE);
		header.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		header.setLayout(UIUtils.createGridLayoutWithoutMargins(5, false));
		
		expandButton = new ExpandButton(header);
		expandButton.getControl().setLayoutData(new GridData());
		expandButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				GridData data = (GridData) bodyGroup.getLayoutData();
		        data.exclude = !expandButton.getSelection();
		        bodyGroup.setVisible(!data.exclude);
		        controlParent.layout(true, true);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		activeCheckBox = new Button(header, SWT.CHECK);
		activeCheckBox.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER));
		
		hardConstraintLabel = new Label(header, SWT.NONE);
		hardConstraintLabel.setText("Hard constraints");
		hardConstraintLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER));
		
		estimateLabel = new Label(header, SWT.NONE);
		estimateLabel.setText("Estimate");
		estimateLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER));
		
		weightSpinner = new Spinner (header, SWT.BORDER);
		weightSpinner.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		weightSpinner.setMinimum(0);
		weightSpinner.setMaximum(10);
		weightSpinner.setIncrement(1);
		weightSpinner.setPageIncrement(1);
	}
	
	protected void createBody(Composite parent) {
		GridData layoutData = new GridData();
		layoutData.horizontalIndent = 15;
		layoutData.exclude = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.verticalAlignment = SWT.FILL;
		
		bodyGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		bodyGroup.setLayoutData(layoutData);
		bodyGroup.setText("Adjust");
		bodyGroup.setLayout(new GridLayout(2, false));

		UIUtils.createLabel(bodyGroup, "Start:", new GridData(100, SWT.DEFAULT));		
		Composite startDateGroup = UIUtils.createEmptyComposite(bodyGroup, 
				new RowLayout(SWT.HORIZONTAL), new GridData());		
		startDate = new DateTime(startDateGroup, SWT.DROP_DOWN | SWT.DATE);
		startTime = new DateTime(startDateGroup, SWT.DROP_DOWN | SWT.TIME);
		
		UIUtils.createLabel(bodyGroup, "Finish:", new GridData());
		Composite endDateGroup = UIUtils.createEmptyComposite(bodyGroup, 
				new RowLayout(SWT.HORIZONTAL), new GridData());		
		endDate = new DateTime(endDateGroup, SWT.DROP_DOWN | SWT.DATE);
		endTime = new DateTime(endDateGroup, SWT.DROP_DOWN | SWT.TIME);
	}	
	
	protected void createLimitAndEstimateSliders() {
		UIUtils.createLabel(bodyGroup, "Limit:", new GridData());
		Composite limitGroup = UIUtils.createEmptyComposite(bodyGroup, 
				new FillLayout(SWT.VERTICAL), new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		Scale slider = new Scale(limitGroup, SWT.HORIZONTAL);
		slider.setMaximum(30);
		slider.setMinimum(0);
		slider.setPageIncrement(1);
		slider.setIncrement(1);
		slider = new Scale(limitGroup, SWT.HORIZONTAL);
		slider.setMaximum(30);
		slider.setMinimum(0);
		slider.setPageIncrement(1);
		slider.setIncrement(1);		
		
		UIUtils.createLabel(bodyGroup, "Estimate:", new GridData());
		slider = new Scale(bodyGroup, SWT.HORIZONTAL);
		slider.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		slider.setMaximum(30);
		slider.setMinimum(0);
		slider.setPageIncrement(1);
		slider.setIncrement(1);		
	}
	
	protected abstract void bindValues();
	
	public void setLayoutData(Object data) {
		mainGroup.setLayoutData(data);
	}
}
