package com.planetmayo.debrief.satc_rcp.ui.contributions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.DateAndTimeObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc_rcp.ui.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.NullToBooleanConverter;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;
import com.planetmayo.debrief.satc_rcp.ui.widgets.ExpandButton;

public abstract class BaseContributionView<T extends BaseContribution>
{

	final protected T contribution;
	final protected Composite controlParent;

	protected Group mainGroup;
	protected Group bodyGroup;

	protected ExpandButton expandButton;
	protected Button activeCheckBox;
	protected Label hardConstraintLabel;
	protected Label estimateLabel;
	protected Spinner weightSpinner;

	protected Text contributionNameText;
	protected DateTime startDate;
	protected DateTime startTime;
	protected DateTime endDate;
	protected DateTime endTime;

	protected Label minLabel;
	protected Label maxLabel;
	protected Label estimateDetailsLabel;
	
	protected Button minActiveCheckbox;
	protected Button maxActiveCheckbox;
	protected Button estimateActiveCheckbox;

	protected Scale minSlider;
	protected Scale maxSlider;
	protected Scale estimateSlider;
	
	private DataBindingContext context;
	private PropertyChangeListener titleChangeListener;

	public BaseContributionView(final Composite parent, final T contribution)
	{
		this.controlParent = parent;
		this.contribution = contribution;
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
		listener.propertyChange(new PropertyChangeEvent(contribution,
				BaseContribution.NAME, null, contribution.getName()));
		contribution.addPropertyChangeListener(BaseContribution.NAME, listener);
		return listener;
	}
	
	protected void bindSliderLabelCheckbox(DataBindingContext context, IObservableValue modelValue,
			Scale slider, Label label, Button checkBox, PrefixSuffixLabelConverter labelValueConverter, 
			BooleanToNullConverter<?> checkBoxValueConverter) 
	{
		IObservableValue sliderValue = WidgetProperties.selection().observe(slider);
		IObservableValue sliderEnabled = WidgetProperties.enabled().observe(slider);
		IObservableValue checkBoxValue = WidgetProperties.selection().observe(checkBox);
		IObservableValue labelValue = WidgetProperties.text().observe(label);
		context.bindValue(sliderValue, modelValue);
		context.bindValue(sliderEnabled, modelValue, null, 
				UIUtils.converterStrategy(new NullToBooleanConverter()));
		context.bindValue(checkBoxValue, modelValue, 
				UIUtils.converterStrategy(checkBoxValueConverter),
				UIUtils.converterStrategy(new NullToBooleanConverter()));
		context.bindValue(labelValue, modelValue, null,
				UIUtils.converterStrategy(labelValueConverter));	
	}
	
	protected void bindMaxMinEstimate(final IObservableValue estimate, final IObservableValue min, final IObservableValue max) {
		estimate.addValueChangeListener(new IValueChangeListener()
		{
			
			@Override
			public void handleValueChange(ValueChangeEvent e)
			{
				final Number newValue = (Number) e.diff.getNewValue();
				final Number oldValue = (Number) e.diff.getOldValue();
				final Number minValue = (Number) min.getValue();
				final Number maxValue = (Number) max.getValue();
				if (newValue.longValue() < minValue.longValue() || newValue.longValue() > maxValue.longValue()) 
				{
					if (minValue.longValue() == maxValue.longValue()) 
					{
						max.setValue(newValue);
						min.setValue(newValue);
					} 
					else 
					{
						controlParent.getDisplay().asyncExec(new Runnable()
						{
							public void run()
							{
								estimate.setValue(oldValue);
							}
						});						
					}				
				}
			}
		});
		min.addValueChangeListener(new IValueChangeListener()
		{
			
			@Override
			public void handleValueChange(ValueChangeEvent e)
			{
				final Number newValue = (Number) e.diff.getNewValue();
				final Number estimateValue = (Number) estimate.getValue();
				final Number maxValue = (Number) max.getValue();
				if (newValue.longValue() > maxValue.longValue()) 
				{
					max.setValue(newValue);
				} 
				if (estimateValue.longValue() < newValue.longValue()) 
				{
					estimate.setValue(newValue);
				}
			}
		});
		max.addValueChangeListener(new IValueChangeListener()
		{
			
			@Override
			public void handleValueChange(ValueChangeEvent e)
			{
				final Number newValue = (Number) e.diff.getNewValue();
				final Number estimateValue = (Number) estimate.getValue();
				final Number minValue = (Number) min.getValue();
				if (newValue.longValue() < minValue.longValue()) 
				{
					min.setValue(newValue);										
				}
				if (estimateValue.longValue() > newValue.longValue()) 
				{
					estimate.setValue(newValue);
				}
			}
		});
	}

	/**
	 * Utility base method which makes common binding for date fields Must be
	 * called if necessary from implementation of bindValues method in child class
	 * 
	 * @param context
	 */
	protected final void bindCommonDates(DataBindingContext context)
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

		IObservableValue nameValue = BeansObservables.observeValue(contribution,
				BaseContribution.NAME);
		IObservableValue nameText = WidgetProperties.text(SWT.Modify).observe(
				contributionNameText);
		context.bindValue(nameText, nameValue);
	}

	/**
	 * Utility base method which binds common header widgets: "active" checkbox,
	 * hardconstrains label, estimate label, weight spinner. Must be called if
	 * necessary from implementation of bindValues method in child class
	 * 
	 * @param context
	 * @param labelsConverter
	 */
	protected final void bindCommonHeaderWidgets(DataBindingContext context, IConverter labelConverter)
	{
		bindCommonHeaderWidgets(context, labelConverter,
				labelConverter);
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
	protected final void bindCommonHeaderWidgets(DataBindingContext context, IConverter estimateConverter,
			IConverter hardConstraintsConverter)
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
				UIUtils.converterStrategy(hardConstraintsConverter));

		IObservableValue estimateValue = BeansObservables.observeValue(
				contribution, BaseContribution.ESTIMATE);
		IObservableValue estimateLabel = WidgetProperties.text().observe(
				this.estimateLabel);
		context.bindValue(estimateLabel, estimateValue, null,
				UIUtils.converterStrategy(estimateConverter));

		IObservableValue weightValue = BeansObservables.observeValue(contribution,
				BaseContribution.WEIGHT);
		IObservableValue weightWidget = WidgetProperties.selection().observe(
				weightSpinner);
		context.bindValue(weightWidget, weightValue);
	}

	protected abstract void bindValues(DataBindingContext context);

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
		bodyGroup.setLayout(new GridLayout(3, false));

		UIUtils.createLabel(bodyGroup, "Name:", new GridData(70, SWT.DEFAULT));
		UIUtils.createSpacer(bodyGroup, new GridData(95, SWT.DEFAULT));
		contributionNameText = new Text(bodyGroup, SWT.BORDER);
		contributionNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		UIUtils.createLabel(bodyGroup, "Start:", new GridData());
		UIUtils.createSpacer(bodyGroup, new GridData());
		Composite startDateGroup = UIUtils.createEmptyComposite(bodyGroup,
				new RowLayout(SWT.HORIZONTAL), new GridData());
		startDate = new DateTime(startDateGroup, SWT.DROP_DOWN | SWT.DATE);
		startTime = new DateTime(startDateGroup, SWT.DROP_DOWN | SWT.TIME);

		UIUtils.createLabel(bodyGroup, "Finish:", new GridData());
		UIUtils.createSpacer(bodyGroup, new GridData());
		Composite endDateGroup = UIUtils.createEmptyComposite(bodyGroup,
				new RowLayout(SWT.HORIZONTAL), new GridData());
		endDate = new DateTime(endDateGroup, SWT.DROP_DOWN | SWT.DATE);
		endTime = new DateTime(endDateGroup, SWT.DROP_DOWN | SWT.TIME);
		
		createLimitAndEstimateSliders();
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
		Composite group;
		
		UIUtils.createLabel(bodyGroup, "Min:", new GridData(GridData.HORIZONTAL_ALIGN_FILL));		
		group = new Composite(bodyGroup, SWT.NONE);
		group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		group.setLayout(UIUtils.createGridLayoutWithoutMargins(2, false));		
		minActiveCheckbox = new Button(group, SWT.CHECK);
		minLabel = UIUtils.createSpacer(group, new GridData(GridData.FILL_HORIZONTAL));
		minSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		minSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		minSlider.setPageIncrement(1);
		minSlider.setIncrement(1);
		
		UIUtils.createLabel(bodyGroup, "Max:", new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		group = new Composite(bodyGroup, SWT.NONE);
		group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		group.setLayout(UIUtils.createGridLayoutWithoutMargins(2, false));			
		maxActiveCheckbox = new Button(group, SWT.CHECK);
		maxLabel = UIUtils.createSpacer(group, new GridData(GridData.FILL_HORIZONTAL));
		maxSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		maxSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		maxSlider.setPageIncrement(1);
		maxSlider.setIncrement(1);

		UIUtils.createLabel(bodyGroup, "Estimate:",	new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		group = new Composite(bodyGroup, SWT.NONE);
		group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		group.setLayout(UIUtils.createGridLayoutWithoutMargins(2, false));			
		estimateActiveCheckbox = new Button(group, SWT.CHECK);
		estimateDetailsLabel = UIUtils.createSpacer(group, new GridData(GridData.FILL_HORIZONTAL));
		estimateSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		estimateSlider.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL));
		estimateSlider.setPageIncrement(1);
		estimateSlider.setIncrement(1);
	}

	public void dispose()
	{
		if (titleChangeListener != null) 
		{
			contribution.removePropertyChangeListener(BaseContribution.NAME,
					titleChangeListener);
		}
		if (context != null) 
		{
			context.dispose();
		}
	}

	/**
	 * get the top level control that this panel uses
	 * 
	 * @return
	 */
	public Composite getControl()
	{
		return mainGroup;
	}

	protected void initializeWidgets() 
	{
		
	}
	
	protected String getTitlePrefix() 
	{
		return "";
	}

	protected void initUI()
	{
		GridLayout layout = UIUtils.createGridLayoutWithoutMargins(1, false);
		layout.verticalSpacing = 0;
		mainGroup = new Group(controlParent, SWT.SHADOW_ETCHED_IN);
		mainGroup.setLayout(layout);

		createHeader(mainGroup);
		createBody(mainGroup);
		
		titleChangeListener = attachTitleChangeListener(contribution, getTitlePrefix());
		initializeWidgets();
		
		context = new DataBindingContext();
		bindValues(context);
	}

	public void setLayoutData(Object data)
	{
		mainGroup.setLayoutData(data);
	}
}