package org.mwc.debrief.multipath.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

import swing2swt.layout.BorderLayout;

public class MultiPathUI extends Composite
{

	private Label _lblSVP;
	private Label _lblTimes;
	private Slider _sliderDepth;
	private Composite _cmpSVP;
	private Composite _cmpDelays;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MultiPathUI(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);

		 _cmpSVP = new Composite(composite, SWT.NONE);
		_cmpSVP.setBounds(0, 0, 64, 64);

		Label lblNewLabel = new Label(_cmpSVP, SWT.NONE);
		lblNewLabel.setBounds(0, 10, 59, 14);
		lblNewLabel.setText("SVP:");

		_lblSVP = new Label(_cmpSVP, SWT.NONE);
		_lblSVP.setBounds(0, 30, 59, 14);
		_lblSVP.setText("[pending]");

		_cmpDelays = new Composite(composite, SWT.NONE);
		_cmpDelays.setBounds(70, 0, 64, 64);

		Label lblNewLabel_1 = new Label(_cmpDelays, SWT.NONE);
		lblNewLabel_1.setBounds(0, 10, 59, 14);
		lblNewLabel_1.setText("Delays:");

		_lblTimes = new Label(_cmpDelays, SWT.NONE);
		_lblTimes.setBounds(0, 30, 59, 14);
		_lblTimes.setText("[pending]");

		Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setBounds(140, 0, 300, 64);

		Label lblNewLabel_2 = new Label(composite_3, SWT.NONE);
		lblNewLabel_2.setBounds(123, 10, 59, 14);
		lblNewLabel_2.setText("[pending]");

		_sliderDepth = new Slider(composite_3, SWT.NONE);
		_sliderDepth.setMaximum(300);
		_sliderDepth.setBounds(10, 30, 280, 15);

		Label lblNewLabel_3 = new Label(composite_3, SWT.NONE);
		lblNewLabel_3.setBounds(59, 10, 59, 14);
		lblNewLabel_3.setText("Depth (m):");

	}
	

	public void setTimeLbl(String text)
	{
		_lblTimes.setText(text);
	}
	
	public void setSVPLbl(String text)
	{
		_lblSVP.setText(text);
	}
	
	public Slider getSlider()
	{
		return _sliderDepth;
	}
	
	public Composite getSVPHolder()
	{
		return _cmpSVP;
	}
	
	public Composite getDelaysHolder()
	{
		return _cmpDelays;
	}
	
	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
