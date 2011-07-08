package org.mwc.asset.netasset2.time;

import java.util.Date;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class VTime extends Composite implements IVTime, IVTimeControl
{
	private Text _time;
	private Button btnStep;
	private Button btnPlay;
	private Button btnStop;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public VTime(Composite parent, int style)
	{
		super(parent, style);

		_time = new Text(this, SWT.BORDER);
		_time.setText("00/00/00 00:00:00");
		_time.setBounds(0, 0, 163, 19);

		btnStep = new Button(this, SWT.NONE);
		btnStep.setBounds(0, 19, 55, 28);
		btnStep.setEnabled(false);
		btnStep.setText("Step");

		btnPlay = new Button(this, SWT.NONE);
		btnPlay.setText(IVTimeControl.PLAY);
		btnPlay.setEnabled(false);
		btnPlay.setBounds(57, 19, 55, 28);

		btnStop = new Button(this, SWT.NONE);
		btnStop.setText("Stop");
		btnStop.setEnabled(false);
		btnStop.setBounds(118, 19, 55, 28);

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void newTime(final long newTime)
	{
		Date dt = new Date(newTime);
		final String date = dt.toString();


		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				_time.setText(date);
			}
		});
	}

	@Override
	public void addStepListener(SelectionListener listener)
	{
		btnStep.addSelectionListener(listener);
	}

	@Override
	public void addPlayListener(SelectionListener listener)
	{
		btnPlay.addSelectionListener(listener);
	}

	@Override
	public void addStopListener(SelectionListener listener)
	{
		btnStop.addSelectionListener(listener);
	}

	@Override
	public void setPlayLabel(final String text)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{

				btnPlay.setText(text);
			}
		});
	}

	public void setEnabled(final boolean val)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				_time.setEnabled(val);
				btnStep.setEnabled(val);
				btnPlay.setEnabled(val);
				btnStop.setEnabled(val);
			}
		});
	}

}
