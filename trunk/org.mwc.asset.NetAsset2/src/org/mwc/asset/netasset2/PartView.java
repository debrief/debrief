package org.mwc.asset.netasset2;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.netasset2.view.IVTime;
import org.mwc.asset.netasset2.view.VTime;

public class PartView extends ViewPart
{
	public static final String ID = "org.mwc.asset.NetAsset2.PartView";

	private IVTime _view;

	private ConnectView _connect;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		_view = new VTime(parent, SWT.NONE);

		_view.addStepListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				getConnect().step();
			}
		});
		_view.addStopListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				getConnect().stop();
			}
		});
		_view.addPlayListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// right, what's the current value?
				Button src = (Button) e.getSource();
				String label = src.getText();
				if (label.equals("Play"))
				{
					getConnect().play();
					src.setText(IVTime.PAUSE);
				}
				else
				{
					getConnect().pause();
					src.setText("Play");
				}
			}
		});

	}

	private ConnectView getConnect()
	{
		if (_connect == null)
		{
			IViewPart vp = getSite().getPage().findView(ConnectView.ID);
			_connect = (ConnectView) vp;
		}
		return _connect;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
	}

	public void setTime(final long newTime)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				Date dt = new Date(newTime);
				String date = dt.toString();
				System.out.println("writing:" + date);
				_view.setTime(date);

			}
		});
	}
	
	public void setEnabled(boolean val)
	{
		_view.setEnabled(val);
	}

}