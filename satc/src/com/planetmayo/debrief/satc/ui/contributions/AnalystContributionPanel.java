package com.planetmayo.debrief.satc.ui.contributions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.DateAndTimeObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc.ui.UIUtils;
import com.planetmayo.debrief.satc.ui.widgets.ExpandButton;

public abstract class AnalystContributionPanel
{

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

	protected Label limitLabel;
	protected Label minLabel;
	protected Label maxLabel;
	protected Label estimateDetailsLabel;

	protected Scale minSlider;
	protected Scale maxSlider;
	protected Scale estimateSlider;

	public AnalystContributionPanel(final Composite parent)
	{
		this.controlParent = parent;
	}

	protected PropertyChangeListener attachTitleChangeListener(
			BaseContribution contribution, final String titlePrefix)
	{
		PropertyChangeListener listener = new PropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				mainGroup.setText(titlePrefix + evt.getNewValue());
			}
		};
		listener.propertyChange(new PropertyChangeEvent(contribution, BaseContribution.NAME, null,
				contribution.getName()));
		contribution.addPropertyChangeListener(BaseContribution.NAME, listener);
		return listener;
	}

	/**
	 * Utility base method which makes common binding for date fields Must be
	 * called if necessary from implementation of bindValues method in child class
	 * 
	 * @param context
	 * @param contribution
	 */
	protected final void bindCommonDates(DataBindingContext context,
			BaseContribution contribution)
	{
		IObservableValue startDateValue = BeansObservables.observeValue(
				contribution, BaseContribution.START_DATE);
		IObservableValue startDateWidget = WidgetProperties.selection().observe(
				startDate);
		IObservableValue startTimeWidget = WidgetProperties.selection().observe(
				startTime);
		context.bindValue(new DateAndTimeObservableValue(startDateWidget,
				startTimeWidget), startDateValue);

		IObservableValue endDateValue = BeansObservables.observeValue(contribution,
				BaseContribution.FINISH_DATE);
		IObservableValue endDateWidget = WidgetProperties.selection().observe(
				endDate);
		IObservableValue endTimeWidget = WidgetProperties.selection().observe(
				endTime);
		context.bindValue(new DateAndTimeObservableValue(endDateWidget,
				endTimeWidget), endDateValue);
	}

	/**
	 * Utility base method which binds common header widgets: "active" checkbox,
	 * hardconstrains label, estimate label, weight spinner. Must be called if
	 * necessary from implementation of bindValues method in child class
	 * 
	 * @param context
	 * @param contribution
	 * @param labelsConverter
	 */
	protected final void bindCommonHeaderWidgets(DataBindingContext context,
			BaseContribution contribution, PrefixSuffixLabelConverter labelsConverter)
	{
		IObservableValue activeValue = BeansObservables.observeValue(contribution,
				BaseContribution.ACTIVE);
		IObservableValue activeButton = WidgetProperties.selection().observe(
				activeCheckBox);
		context.bindValue(activeButton, activeValue);

		IObservableValue hardContraintValue = BeansObservables.observeValue(
				contribution, BaseContribution.HARD_CONSTRAINTS);
		IObservableValue hardContraintLabel = WidgetProperties.text().observe(
				hardConstraintLabel);
		context.bindValue(hardContraintLabel, hardContraintValue, null,
				UIUtils.converterStrategy(labelsConverter));

		IObservableValue estimateValue = BeansObservables.observeValue(
				contribution, BaseContribution.HARD_CONSTRAINTS);
		IObservableValue estimateLabel = WidgetProperties.text().observe(
				this.estimateLabel);
		context.bindValue(estimateLabel, estimateValue, null,
				UIUtils.converterStrategy(labelsConverter));

		IObservableValue weightValue = BeansObservables.observeValue(contribution,
				BaseContribution.WEIGHT);
		IObservableValue weightWidget = WidgetProperties.selection().observe(
				weightSpinner);
		context.bindValue(weightWidget, weightValue);
	}

	protected abstract void bindValues();

	protected void createBody(Composite parent)
	{
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

		UIUtils.createLabel(bodyGroup, "Start:", new GridData(120, SWT.DEFAULT));
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

	protected void createHeader(Composite parent)
	{
		Composite header = new Composite(parent, SWT.NONE);
		header.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL));
		header.setLayout(UIUtils.createGridLayoutWithoutMargins(3, false));

		expandButton = new ExpandButton(header);
		expandButton.getControl().setLayoutData(new GridData(40, SWT.DEFAULT));
		expandButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				GridData data = (GridData) bodyGroup.getLayoutData();
				data.exclude = !expandButton.getSelection();
				bodyGroup.setVisible(!data.exclude);
				controlParent.layout(new Control[]
				{ mainGroup });
			}
		});

		Composite nested = UIUtils.createEmptyComposite(header, UIUtils
				.createGridLayoutWithoutMargins(3, true), new GridData(
				GridData.FILL_HORIZONTAL));
		activeCheckBox = new Button(nested, SWT.CHECK);
		activeCheckBox.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_CENTER));

		hardConstraintLabel = new Label(nested, SWT.CENTER);
		hardConstraintLabel.setText("Hard constraints");
		hardConstraintLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		estimateLabel = new Label(nested, SWT.CENTER);
		estimateLabel.setText("Estimate");
		estimateLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		weightSpinner = new Spinner(header, SWT.BORDER);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		data.widthHint = 10;
		weightSpinner.setLayoutData(data);
		weightSpinner.setMinimum(0);
		weightSpinner.setMaximum(10);
		weightSpinner.setIncrement(1);
		weightSpinner.setPageIncrement(1);
	}

	protected void createLimitAndEstimateSliders()
	{
		GridLayout limitsLayout = new GridLayout(2, false);
		limitsLayout.horizontalSpacing = 15;
		Composite limitsComposite = UIUtils.createEmptyComposite(bodyGroup,
				limitsLayout, new GridData(GridData.VERTICAL_ALIGN_FILL
						| GridData.HORIZONTAL_ALIGN_FILL));
		GridData gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.grabExcessVerticalSpace = true;
		limitLabel = UIUtils.createLabel(limitsComposite, "Limit", gridData);
		minLabel = UIUtils.createLabel(limitsComposite, "min:", new GridData(
				GridData.GRAB_VERTICAL | GridData.FILL_HORIZONTAL));
		maxLabel = UIUtils.createLabel(limitsComposite, "max:", new GridData(
				GridData.GRAB_VERTICAL | GridData.FILL_HORIZONTAL));

		Composite limitGroup = UIUtils.createEmptyComposite(bodyGroup,
				new FillLayout(SWT.VERTICAL), new GridData(
						GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		minSlider = new Scale(limitGroup, SWT.HORIZONTAL);
		minSlider.setPageIncrement(1);
		minSlider.setIncrement(1);
		maxSlider = new Scale(limitGroup, SWT.HORIZONTAL);
		maxSlider.setPageIncrement(1);
		maxSlider.setIncrement(1);

		estimateDetailsLabel = UIUtils.createLabel(bodyGroup, "Estimate:",
				new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		estimateSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		estimateSlider.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL));
		estimateSlider.setPageIncrement(1);
		estimateSlider.setIncrement(1);
	}

	public void dispose()
	{

	}

	protected abstract void initializeWidgets();

	protected void initUI()
	{
		GridLayout layout = UIUtils.createGridLayoutWithoutMargins(1, false);
		layout.verticalSpacing = 0;
		mainGroup = new Group(controlParent, SWT.SHADOW_ETCHED_IN);
		mainGroup.setLayout(layout);

		createHeader(mainGroup);
		createBody(mainGroup);
		createLimitAndEstimateSliders();
		initializeWidgets();
		bindValues();
	}

	public void setLayoutData(Object data)
	{
		mainGroup.setLayoutData(data);
	}
}
