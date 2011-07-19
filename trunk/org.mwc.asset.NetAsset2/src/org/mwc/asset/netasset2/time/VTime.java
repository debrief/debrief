package org.mwc.asset.netasset2.time;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class VTime extends Composite implements IVTime, IVTimeControl
{
	private Text _time;
	private Button btnStep;
	private Button btnPlay;
	private Button btnStop;
	private Button btnFaster;
	private Button btnSlower;
	private Composite composite;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public VTime(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new RowLayout(SWT.HORIZONTAL));

		_time = new Text(this, SWT.BORDER);
		_time.setLayoutData(new RowData(137, SWT.DEFAULT));
		_time.setText("00/00/00 00:00:00");

		composite = new Composite(this, SWT.NONE);
		RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
		rl_composite.justify = true;
		composite.setLayout(rl_composite);
		composite.setLayoutData(new RowData(116, 55));

		btnStep = new Button(composite, SWT.FLAT);
		btnStep.setEnabled(false);
		btnStep.setText("Step");

		btnStop = new Button(composite, SWT.FLAT);
		btnStop.setText("Stop");
		btnStop.setEnabled(false);

		btnPlay = new Button(composite, SWT.FLAT);
		btnPlay.setText(IVTimeControl.PLAY);
		btnPlay.setEnabled(false);

		btnFaster = new Button(composite, SWT.FLAT);
		btnFaster.setText("Faster");
		btnFaster.setEnabled(false);

		btnSlower = new Button(composite, SWT.FLAT);
		btnSlower.setText("Slower");
		btnSlower.setEnabled(false);

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
				if (!_time.isDisposed())
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
				if (btnPlay.isDisposed())
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
				if (!_time.isDisposed())
				{
					_time.setEnabled(val);
					btnStep.setEnabled(val);
					btnPlay.setEnabled(val);
					btnStop.setEnabled(val);
					btnFaster.setEnabled(val);
					btnSlower.setEnabled(val);
				}
			}
		});
	}

	@Override
	public void addFasterListener(SelectionListener listener)
	{
		btnFaster.addSelectionListener(listener);
	}

	@Override
	public void addSlowerListener(SelectionListener listener)
	{
		btnSlower.addSelectionListener(listener);
	}

}
