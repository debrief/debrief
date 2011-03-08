package org.mwc.debrief.multipath2.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Slider;

public class MultiPathUI extends Composite
{

	private Composite chartHolder;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MultiPathUI(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(this, SWT.NONE);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite.widthHint = 150;
		composite.setLayoutData(gd_composite);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setBounds(0, 0, 64, 43);
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setBounds(0, 0, 59, 14);
		lblNewLabel.setText("SVP:");
		
		Label lblNewLabel_1 = new Label(composite_1, SWT.NONE);
		lblNewLabel_1.setBounds(0, 21, 59, 14);
		lblNewLabel_1.setText("[pending]");
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setBounds(70, 0, 64, 43);
		
		Label lblIntervals = new Label(composite_2, SWT.NONE);
		lblIntervals.setText("Intervals");
		lblIntervals.setBounds(0, 0, 59, 14);
		
		Label label_1 = new Label(composite_2, SWT.NONE);
		label_1.setText("[pending]");
		label_1.setBounds(0, 21, 59, 14);
		
		Composite cmpSlider = new Composite(this, SWT.NONE);
		cmpSlider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		cmpSlider.setBounds(144, 0, 294, 43);
		
		Label lblNewLabel_2 = new Label(cmpSlider, SWT.NONE);
		lblNewLabel_2.setBounds(118, 0, 59, 14);
		lblNewLabel_2.setText("[pending]");
		
		Label lblNewLabel_3 = new Label(cmpSlider, SWT.NONE);
		lblNewLabel_3.setAlignment(SWT.RIGHT);
		lblNewLabel_3.setBounds(53, 0, 59, 14);
		lblNewLabel_3.setText("Depth:");
		
		Slider slider = new Slider(cmpSlider, SWT.NONE);
		slider.setBounds(10, 20, 274, 15);
		
		 chartHolder = new Composite(this, SWT.EMBEDDED);
		chartHolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

	}
	
	public Composite getChartHolder()
	{
		return chartHolder;
	}
	
	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

}
