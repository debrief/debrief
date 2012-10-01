package com.planetmayo.debrief.satc.ui.contributions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.ui.UIUtils;

public class LocationContributionPanel extends AnalystContributionPanel {

	private LocationForecastContribution contribution;
	
	private Scale limitSlider;
	private Text estimateText;
	
	public LocationContributionPanel(Composite parent, LocationForecastContribution contribution) {
		super(parent, "Location Forecast");
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
		
	}

	@Override
	protected void bindValues() {
		// TODO Auto-generated method stub
		
	}
}
