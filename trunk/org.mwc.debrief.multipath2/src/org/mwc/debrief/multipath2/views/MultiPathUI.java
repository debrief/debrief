package org.mwc.debrief.multipath2.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.ResourceManager;

public class MultiPathUI extends Composite
{

	private Composite chartHolder;
	private Composite _cmpSVP;
	private Composite _cmpIntervals;
	private Label _lblSVP;
	private Label _lblIntervals;
	private Slider _slider;
	private Label _sliderVal;
	private Button _btnMagic;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MultiPathUI(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new GridLayout(3, false));

		Composite cmpFiles = new Composite(this, SWT.NONE);
		GridData gd_cmpFiles = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_cmpFiles.widthHint = 190;
		cmpFiles.setLayoutData(gd_cmpFiles);

		_cmpSVP = new Composite(cmpFiles, SWT.NONE);
		_cmpSVP.setBounds(0, 0, 92, 32);

		Label lblNewLabel = new Label(_cmpSVP, SWT.NONE);
		lblNewLabel.setBounds(0, 0, 59, 14);
		lblNewLabel.setText("SVP:");

		_lblSVP = new Label(_cmpSVP, SWT.NONE);
		_lblSVP.setFont(SWTResourceManager.getFont("Lucida Grande", 9, SWT.BOLD));
		_lblSVP.setBounds(0, 15, 90, 14);
		_lblSVP.setText("[pending]");

		_cmpIntervals = new Composite(cmpFiles, SWT.NONE);
		_cmpIntervals.setBounds(94, 0, 90, 32);

		Label lblIntervals = new Label(_cmpIntervals, SWT.NONE);
		lblIntervals.setText("Intervals:");
		lblIntervals.setBounds(0, 0, 59, 14);

		_lblIntervals = new Label(_cmpIntervals, SWT.NONE);
		_lblIntervals.setFont(SWTResourceManager.getFont("Lucida Grande", 9,
				SWT.BOLD));
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
		_slider.setMaximum(800);

		Composite cmpButton = new Composite(this, SWT.NONE);
		GridData gd_cmpButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_cmpButton.widthHint = 60;
		cmpButton.setLayoutData(gd_cmpButton);

		_btnMagic = new Button(cmpButton, SWT.FLAT);
		_btnMagic.setToolTipText("Calculate optimal target depth");
		_btnMagic.setImage(ResourceManager.getPluginImage("org.mwc.debrief.multipath2", "icons/magic_hat.png"));
		_btnMagic.setBounds(0, 0, 40, 40);

		chartHolder = new Composite(this, SWT.EMBEDDED);
		chartHolder
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		new Label(this, SWT.NONE);
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
	

	public Control getRangeHolder()
	{
		return _sliderVal;
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

	public void addMagicHandler(SelectionListener handler)
	{
		_btnMagic.addSelectionListener(handler);
	}

	public void setSliderValText(String text)
	{
		_sliderVal.setText(text);
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		_slider.setEnabled(enabled);

		if (!enabled)
		{
			setSliderValText("Disabled, pending data");
		}
	}

}
