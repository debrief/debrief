package org.mwc.debrief.multipath2.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.wb.swt.SWTResourceManager;

public class MultiPathUI extends Composite
{

	private Composite chartHolder;
	private Composite _cmpSVP;
	private Composite _cmpIntervals;
	private Label _lblSVP;
	private Label _lblIntervals;
	private Slider _slider;
	private Label _sliderVal;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MultiPathUI(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Composite composite = new Composite(this, SWT.NONE);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_composite.widthHint = 190;
		composite.setLayoutData(gd_composite);

		_cmpSVP = new Composite(composite, SWT.NONE);
		_cmpSVP.setBounds(0, 0, 92, 32);

		Label lblNewLabel = new Label(_cmpSVP, SWT.NONE);
		lblNewLabel.setBounds(0, 0, 59, 14);
		lblNewLabel.setText("SVP:");

		_lblSVP = new Label(_cmpSVP, SWT.NONE);
		_lblSVP.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		_lblSVP.setBounds(0, 15, 90, 14);
		_lblSVP.setText("[pending]");

		_cmpIntervals = new Composite(composite, SWT.NONE);
		_cmpIntervals.setBounds(94, 0, 90, 32);

		Label lblIntervals = new Label(_cmpIntervals, SWT.NONE);
		lblIntervals.setText("Intervals:");
		lblIntervals.setBounds(0, 0, 59, 14);

		_lblIntervals = new Label(_cmpIntervals, SWT.NONE);
		_lblIntervals.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		_lblIntervals.setText("[pending]");
		_lblIntervals.setBounds(0, 15, 90, 14);

		Composite cmpSlider = new Composite(this, SWT.NONE);
		cmpSlider.setLayout(new FillLayout(SWT.VERTICAL));
		GridData gd_cmpSlider = new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1);
		gd_cmpSlider.heightHint = 32;
		cmpSlider.setLayoutData(gd_cmpSlider);

		cmpSlider.setBounds(144, 0, 294, 43);

		_sliderVal = new Label(cmpSlider, SWT.CENTER);
		_sliderVal.setAlignment(SWT.CENTER);
		_sliderVal.setText("[pending]");

		_slider = new Slider(cmpSlider, SWT.NONE);
		_slider.setMaximum(300);

		chartHolder = new Composite(this, SWT.EMBEDDED);
		chartHolder
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

	}

	public Composite getSVPHolder()
	{
		return _cmpSVP;
	}

	public Composite getIntervalHolder()
	{
		return _cmpIntervals;
	}

	public void setSVPName(String text)
	{
		_lblSVP.setText(text);
	}

	public void setIntervalName(String text)
	{
		_lblIntervals.setText(text);
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

	public Slider getSlider()
	{
		return _slider;
	}

	public void setSliderValText(String text)
	{
		_sliderVal.setText(text);
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);

		if (!enabled)
		{
			setSliderValText("Disabled, pending data");
		}
	}

}
