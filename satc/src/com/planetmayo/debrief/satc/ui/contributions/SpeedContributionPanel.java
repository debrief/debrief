package com.planetmayo.debrief.satc.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.DateAndTimeObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;

public class SpeedContributionPanel extends AnalystContributionPanel {
	
	private SpeedForecastContribution contribution;
	
	public SpeedContributionPanel(Composite parent, SpeedForecastContribution contribution) {
		super(parent);
		this.contribution = contribution;
		initUI();
	}

	@Override
	protected void bindValues() {
		DataBindingContext context = new DataBindingContext();
		
		IObservableValue activeValue = BeansObservables.observeValue(contribution, "active");
		IObservableValue activeButton = WidgetProperties.selection().observe(activeCheckBox);
		context.bindValue(activeButton, activeValue);
		
		IObservableValue hardContraintValue = BeansObservables.observeValue(contribution, "hardConstraints");
		IObservableValue hardContraintLabel = WidgetProperties.text().observe(hardConstraintLabel);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new KnotsConverter(String.class));			
		context.bindValue(hardContraintLabel, hardContraintValue, null, strategy);
		
		IObservableValue estimateValue = BeansObservables.observeValue(contribution, "estimate");
		IObservableValue estimateLabel = WidgetProperties.text().observe(this.estimateLabel);
		strategy = new UpdateValueStrategy();
		strategy.setConverter(new KnotsConverter(double.class));		
		context.bindValue(estimateLabel, estimateValue, null, strategy);
		
		IObservableValue startDateValue = BeansObservables.observeValue(contribution, "startDate");
		IObservableValue startDateWidget = WidgetProperties.selection().observe(startDate);
		IObservableValue startTimeWidget = WidgetProperties.selection().observe(startTime);
		context.bindValue(new DateAndTimeObservableValue(startDateWidget, startTimeWidget), startDateValue);		

		IObservableValue endDateValue = BeansObservables.observeValue(contribution, "finishDate");
		IObservableValue endDateWidget = WidgetProperties.selection().observe(endDate);
		IObservableValue endTimeWidget = WidgetProperties.selection().observe(endTime);
		context.bindValue(new DateAndTimeObservableValue(endDateWidget, endTimeWidget), endDateValue);	
		
		IObservableValue weightValue = BeansObservables.observeValue(contribution, "weight");
		IObservableValue weightWidget = WidgetProperties.selection().observe(weightSpinner);
		context.bindValue(weightWidget, weightValue);
	}
	
	private static class KnotsConverter implements IConverter {
		private Object fromType;
		
		KnotsConverter(Object fromType) {
			this.fromType = fromType;
		}
		
		@Override
		public Object getToType() {			
			return String.class;
		}
		
		@Override
		public Object getFromType() {
			return fromType;
		}
		
		@Override
		public Object convert(Object model) {
			if (model instanceof Number) {
				model = ((Number) model).intValue();
			}
			return "" +  model + " knots";
		}		
	}
}
