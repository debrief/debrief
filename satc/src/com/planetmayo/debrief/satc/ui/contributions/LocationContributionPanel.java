package com.planetmayo.debrief.satc.ui.contributions;

import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.ui.UIUtils;

public class LocationContributionPanel extends AnalystContributionPanel {

	private LocationForecastContribution contribution;
	private DataBindingContext context;
	private PropertyChangeListener titleChangeListener;		
	
	private Scale limitSlider;
	private Text estimateText;
	
	public LocationContributionPanel(Composite parent, LocationForecastContribution contribution) {
		super(parent);
		this.contribution = contribution;
		initUI();
	}
	
	@Override
	protected void createLimitAndEstimateSliders() {
		limitLabel = UIUtils.createLabel(bodyGroup, "Limit:", new GridData());
		limitSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		limitSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		
		estimateDetailsLabel = UIUtils.createLabel(bodyGroup, "Estimate:", new GridData());
		estimateText = new Text(bodyGroup, SWT.BORDER);
		estimateText.setLayoutData(new GridData());
	}

	@Override
	protected void initializeWidgets() {
		titleChangeListener = attachTitleChangeListener(contribution, "Location Forecast - ");		
	}
	
	@Override
	public void dispose() {
		super.dispose();
		contribution.removePropertyChangeListener("name", titleChangeListener);
		context.dispose();		
	}

	@Override
	protected void bindValues() {
		context = new DataBindingContext();
		
	}
}
