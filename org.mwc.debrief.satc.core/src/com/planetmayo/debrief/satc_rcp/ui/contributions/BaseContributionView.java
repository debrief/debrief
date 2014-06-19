package com.planetmayo.debrief.satc_rcp.ui.contributions;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.DateAndTimeObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;
import com.planetmayo.debrief.satc_rcp.ui.converters.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.CompoundConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.IntegerConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.NullToBooleanConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.UnitConverter;
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

	protected Button estimateActiveCheckbox;

	protected Scale minSlider;
	protected Scale maxSlider;
	protected Scale estimateSlider;

	protected DataBindingContext context;
	protected PropertyChangeListener titleChangeListener;

	private final IContributions contributions;

	public BaseContributionView(final Composite parent, final T contribution,
			final IContributions contributions)
	{
		this.controlParent = parent;
		this.contribution = contribution;
		this.contributions = contributions;
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

	/**
	 * binds model value to specified slider and label with specified converters
	 * returns writable value which is used to store direct ui value
	 */
	protected WritableValue bindSliderLabel(DataBindingContext context,
			final IObservableValue modelValue, Scale slider, Label label,
			PrefixSuffixLabelConverter labelValueConverter,
			UnitConverter unitConverter)
	{
		return bindSliderLabelCheckbox(context, modelValue, slider, label, null,
				labelValueConverter, null, unitConverter);
	}

	/**
	 * binds model value to specified slider, label and checkBox with specified
	 * converters returns writable value which is used to store direct ui value
	 */
	protected WritableValue bindSliderLabelCheckbox(DataBindingContext context,
			final IObservableValue modelValue, Scale slider, Label label,
			Button checkBox, PrefixSuffixLabelConverter labelValueConverter,
			BooleanToNullConverter<?> checkBoxValueConverter,
			UnitConverter unitConverter)
	{
		final WritableValue uiProxy = new WritableValue(modelValue.getValue(),
				modelValue.getValueType());

		IObservableValue sliderValue = WidgetProperties.selection().observe(slider);
		IObservableValue labelValue = WidgetProperties.text().observe(label);

		if (unitConverter != null)
		{
			IConverter modelToUI = new CompoundConverter(
					unitConverter.getModelToUI(), new IntegerConverter());
			context.bindValue(sliderValue, uiProxy,
					UIUtils.converterStrategy(unitConverter.getUIToModel()),
					UIUtils.converterStrategy(modelToUI));
		}
		else
		{
			context.bindValue(sliderValue, uiProxy);
		}
		if (checkBox != null)
		{
			IObservableValue sliderEnabled = WidgetProperties.enabled().observe(
					slider);
			IObservableValue checkBoxValue = WidgetProperties.selection().observe(
					checkBox);
			context.bindValue(checkBoxValue, modelValue,
					UIUtils.converterStrategy(checkBoxValueConverter),
					UIUtils.converterStrategy(new NullToBooleanConverter()));
			context.bindValue(sliderEnabled, modelValue, null,
					UIUtils.converterStrategy(new NullToBooleanConverter()));
		}

		context.bindValue(labelValue, uiProxy, null,
				UIUtils.converterStrategy(labelValueConverter));
		context.bindValue(uiProxy, modelValue, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_NEVER), null);
		slider.addListener(SWT.MouseUp, new Listener()
		{
			@Override
			public void handleEvent(Event arg0)
			{
				modelValue.setValue(uiProxy.getValue());
			}
		});
		slider.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if ((e.stateMask & SWT.BUTTON1) == 0)
				{
					modelValue.setValue(uiProxy.getValue());
				}
			}
		});
		return uiProxy;
	}

	protected void bindMaxMinEstimate(final IObservableValue estimate,
			final IObservableValue min, final IObservableValue max)
	{
		estimate.addValueChangeListener(new IValueChangeListener()
		{

			@Override
			public void handleValueChange(ValueChangeEvent e)
			{
				final Number newValue = (Number) e.diff.getNewValue();
				final Number oldValue = (Number) e.diff.getOldValue();
				final Number minValue = (Number) min.getValue();
				final Number maxValue = (Number) max.getValue();

				if (newValue == null)
				{
					return;
				}
				double minT = minValue == null ? Double.MIN_VALUE : minValue
						.doubleValue();
				double maxT = maxValue == null ? Double.MAX_VALUE : maxValue
						.doubleValue();
				if (newValue.doubleValue() < minT || newValue.doubleValue() > maxT)
				{
					if (minT == maxT)
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
								if (!estimate.isDisposed())
								{
									if (oldValue == null)
									{
										estimate.setValue(minValue == null ? maxValue : minValue);
									}
									else
									{
										estimate.setValue(oldValue);
									}
								}
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
				if (newValue == null)
				{
					return;
				}
				if (newValue.doubleValue() > maxValue.doubleValue())
				{
					max.setValue(newValue);
				}
				// note: we may not have an estimate
				if (estimateValue != null)
					if (estimateValue.doubleValue() < newValue.doubleValue())
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
				if (newValue == null)
				{
					return;
				}
				if (newValue.doubleValue() < minValue.doubleValue())
				{
					min.setValue(newValue);
				}

				// note: we may not have an estimate
				if (estimateValue != null)
					if (estimateValue.doubleValue() > newValue.doubleValue())
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
	protected final void bindCommonHeaderWidgets(DataBindingContext context,
			IObservableValue hardContraints, IObservableValue estimateValue,
			IConverter labelConverter)
	{
		bindCommonHeaderWidgets(context, hardContraints, estimateValue,
				labelConverter, labelConverter);
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
			IObservableValue hardContraint, IObservableValue estimateValue,
			IConverter estimateConverter, IConverter hardConstraintsConverter)
	{
		IObservableValue activeValue = BeansObservables.observeValue(contribution,
				BaseContribution.ACTIVE);
		IObservableValue activeButton = WidgetProperties.selection().observe(
				activeCheckBox);
		context.bindValue(activeButton, activeValue);

		if (hardContraint != null)
		{
			IObservableValue hardContraintLabel = WidgetProperties.text().observe(
					hardConstraintLabel);
			context.bindValue(hardContraintLabel, hardContraint, null,
					UIUtils.converterStrategy(hardConstraintsConverter));
		}

		if (estimateValue != null)
		{
			IObservableValue estimateLabel = WidgetProperties.text().observe(
					this.estimateLabel);
			context.bindValue(estimateLabel, estimateValue, null,
					UIUtils.converterStrategy(estimateConverter));
		}

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
		UIUtils.createSpacer(bodyGroup, new GridData(45, SWT.DEFAULT));
		contributionNameText = new Text(bodyGroup, SWT.BORDER);
		contributionNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		UIUtils.createLabel(bodyGroup, "Dates:", new GridData());
		UIUtils.createSpacer(bodyGroup, new GridData());
		Composite datesGroup = UIUtils.createEmptyComposite(bodyGroup,
				new RowLayout(SWT.HORIZONTAL), new GridData());
		startDate = new DateTime(datesGroup, SWT.DROP_DOWN | SWT.DATE);
		startTime = new DateTime(datesGroup, SWT.DROP_DOWN | SWT.TIME);
		UIUtils.createLabel(datesGroup, "  -  ", new RowData());
		endDate = new DateTime(datesGroup, SWT.DROP_DOWN | SWT.DATE);
		endTime = new DateTime(datesGroup, SWT.DROP_DOWN | SWT.TIME);

		createLimitAndEstimateSliders();
	}
	

	/** set the color code for this contribution
	 * 
	 * @param newColor
	 */
	protected void setContributionColor(Color newColor)
	{
		if(newColor != null)
		{
			org.eclipse.swt.graphics.Color swtColpr = new org.eclipse.swt.graphics.Color(Display.getCurrent(), 
					newColor.getRed(), newColor.getGreen(), newColor.getBlue());
			
			// TODO: I presume we need to dispose of the previous generated color object
			
			// TODO: I hope we'll actually have a custom rounded rectangle to use
			// for the color coding, not the active check box
			activeCheckBox.setBackground(swtColpr);
		}
		else
		{
			// somehow - we need to set the control back to transparent
			// TODO:
		}
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
				if (controlParent.getParent() != null
						&& controlParent.getParent().getParent() != null)
				{
					controlParent.getParent().getParent().layout(true, true);
					controlParent.getParent().getParent().redraw();
					controlParent.getParent().getParent().update();
				}

			}
		});

		Composite nested =
				UIUtils.createEmptyComposite(header, UIUtils
						.createGridLayoutWithoutMargins(4, true), new GridData(
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

		weightSpinner = new Spinner(nested, SWT.BORDER);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		data.widthHint = 10;
		weightSpinner.setLayoutData(data);
		weightSpinner.setMinimum(1);
		weightSpinner.setMaximum(10);
		weightSpinner.setIncrement(1);
		weightSpinner.setPageIncrement(1);

		Button button = new Button(header, SWT.PUSH);
		button.setToolTipText("Delete this contribution");
		button.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		button.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				contributions.removeContribution(contribution);
			}
		});

	}

	protected void createLimitAndEstimateSliders()
	{
		Composite group;

		UIUtils.createLabel(bodyGroup, "Min:", new GridData(
				GridData.HORIZONTAL_ALIGN_FILL));
		group = new Composite(bodyGroup, SWT.NONE);
		group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		group.setLayout(UIUtils.createGridLayoutWithoutMargins(2, false));
		minLabel = UIUtils.createSpacer(group, new GridData(
				GridData.FILL_HORIZONTAL));
		minSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		minSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		minSlider.setPageIncrement(1);
		minSlider.setIncrement(1);

		UIUtils.createLabel(bodyGroup, "Max:", new GridData(
				GridData.HORIZONTAL_ALIGN_FILL));
		group = new Composite(bodyGroup, SWT.NONE);
		group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		group.setLayout(UIUtils.createGridLayoutWithoutMargins(2, false));
		maxLabel = UIUtils.createSpacer(group, new GridData(
				GridData.FILL_HORIZONTAL));
		maxSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		maxSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		maxSlider.setPageIncrement(1);
		maxSlider.setIncrement(1);

		UIUtils.createLabel(bodyGroup, "Estimate:", new GridData(
				GridData.HORIZONTAL_ALIGN_FILL));
		group = new Composite(bodyGroup, SWT.NONE);
		group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		group.setLayout(UIUtils.createGridLayoutWithoutMargins(2, false));
		estimateActiveCheckbox = new Button(group, SWT.CHECK);
		estimateDetailsLabel = UIUtils.createSpacer(group, new GridData(
				GridData.FILL_HORIZONTAL));
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
		mainGroup.dispose();
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

		titleChangeListener = attachTitleChangeListener(contribution,
				getTitlePrefix());
		initializeWidgets();

		context = new DataBindingContext();
		bindValues(context);
	}

	public void setLayoutData(Object data)
	{
		mainGroup.setLayoutData(data);
	}
}