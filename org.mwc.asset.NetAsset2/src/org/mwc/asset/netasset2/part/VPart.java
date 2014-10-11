/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.netasset2.part;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.mwc.asset.netasset2.Activator;
import org.mwc.cmap.gridharness.data.WorldSpeed;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class VPart extends Composite implements IVPartControl, IVPartMovement
{
	DecimalFormat df2 = new DecimalFormat("0.00");
	DecimalFormat df0 = new DecimalFormat("0");

	private static final String PENDING = "[Pending]";
	private final Group grpState;
	private final Text demCourse;
	private final Label actCourse;
	private final Text demSpeed;
	private final Label actSpeed;
	private final Text demDepth;
	private final Label actDepth;
	private final Button newState;
	private final Label partName;
	private NewDemStatus _listener;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public VPart(final Composite parent, final int style)
	{
		super(parent, style);
		setLayout(new FormLayout());

		final KeyListener enterListener = new KeyListener()
		{

			@Override
			public void keyPressed(final KeyEvent e)
			{
			}

			@Override
			public void keyReleased(final KeyEvent e)
			{
				if (e.keyCode == 13)
				{
					fireDemStatus();
				}
			}
		};

		partName = new Label(this, SWT.NONE);
		final FormData fd_partName = new FormData();
		fd_partName.bottom = new FormAttachment(0, 19);
		fd_partName.right = new FormAttachment(0, 222);
		fd_partName.top = new FormAttachment(0);
		fd_partName.left = new FormAttachment(0);
		partName.setLayoutData(fd_partName);
		partName.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.BOLD));
		partName.setText(PENDING);

		grpState = new Group(this, SWT.NONE);
		final FormData fd_grpState = new FormData();
		fd_grpState.bottom = new FormAttachment(0, 206);
		fd_grpState.right = new FormAttachment(0, 232);
		fd_grpState.top = new FormAttachment(0, 25);
		fd_grpState.left = new FormAttachment(0, 10);
		grpState.setLayoutData(fd_grpState);
		grpState.setText("State");
		grpState.setLayout(new GridLayout(3, false));

		final Label lblProperty = new Label(grpState, SWT.NONE);
		lblProperty.setText("   ");

		final Label lblDemanded = new Label(grpState, SWT.NONE);
		lblDemanded.setText("Demanded");

		final Label lblActual = new Label(grpState, SWT.NONE);
		lblActual.setText("Actual");

		final Label lblCourse = new Label(grpState, SWT.NONE);
		lblCourse.setText("Course");

		final Composite demC = new Composite(grpState, SWT.NONE);

		demCourse = new Text(demC, SWT.NONE);
		demCourse.setLocation(0, 5);
		demCourse.setSize(35, 20);
		demCourse.setText("000  ");
		demCourse.setEnabled(false);
		demCourse.addKeyListener(enterListener);

		final Button incCrse = new Button(demC, SWT.FLAT | SWT.CENTER);
		incCrse.setFont(SWTResourceManager.getFont("Lucida Grande", 6, SWT.NORMAL));
		incCrse.setBounds(40, 0, 15, 15);
		incCrse.setText("+");
		incCrse.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				crseChange(5);
			}
		});

		final Button decCrse = new Button(demC, SWT.FLAT | SWT.CENTER);
		decCrse.setText("-");
		decCrse.setFont(SWTResourceManager.getFont("Lucida Grande", 6, SWT.NORMAL));
		decCrse.setBounds(40, 16, 15, 15);
		decCrse.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				crseChange(-5);
			}
		});

		actCourse = new Label(grpState, SWT.NONE);
		actCourse.setText("000 00");

		final Label lblSpeed = new Label(grpState, SWT.NONE);
		lblSpeed.setText("Speed");

		final Composite demS = new Composite(grpState, SWT.NONE);

		demSpeed = new Text(demS, SWT.NONE);
		demSpeed.setLocation(0, 5);
		demSpeed.setSize(35, 20);
		demSpeed.setText("000  ");
		demSpeed.setEnabled(false);
		demSpeed.addKeyListener(enterListener);

		final Button incSpd = new Button(demS, SWT.FLAT | SWT.CENTER);
		incSpd.setFont(SWTResourceManager.getFont("Lucida Grande", 6, SWT.NORMAL));
		incSpd.setBounds(40, 0, 15, 15);
		incSpd.setText("+");
		incSpd.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				spdChange(2);
			}
		});

		final Button decSpd = new Button(demS, SWT.FLAT | SWT.CENTER);
		decSpd.setText("-");
		decSpd.setFont(SWTResourceManager.getFont("Lucida Grande", 6, SWT.NORMAL));
		decSpd.setBounds(40, 16, 15, 15);
		decSpd.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				spdChange(-2);
			}
		});

		actSpeed = new Label(grpState, SWT.NONE);
		actSpeed.setText("000 00");

		final Label lblDepth = new Label(grpState, SWT.NONE);
		lblDepth.setText("Depth");

		final Composite demD = new Composite(grpState, SWT.NONE);

		demDepth = new Text(demD, SWT.NONE);
		demDepth.setLocation(0, 5);
		demDepth.setSize(35, 20);
		demDepth.setText("000  ");
		demDepth.setEnabled(false);
		demDepth.addKeyListener(enterListener);

		final Button incDepth = new Button(demD, SWT.FLAT | SWT.CENTER);
		incDepth
				.setFont(SWTResourceManager.getFont("Lucida Grande", 6, SWT.NORMAL));
		incDepth.setBounds(40, 0, 15, 15);
		incDepth.setText("+");
		incDepth.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				depthChange(2);
			}
		});

		final Button decDepth = new Button(demD, SWT.FLAT | SWT.CENTER);
		decDepth.setText("-");
		decDepth
				.setFont(SWTResourceManager.getFont("Lucida Grande", 6, SWT.NORMAL));
		decDepth.setBounds(40, 16, 15, 15);
		decDepth.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				depthChange(-2);
			}
		});

		actDepth = new Label(grpState, SWT.NONE);
		actDepth.setText("000 00");

		final Label label = new Label(grpState, SWT.NONE);
		label.setText("  ");

		final Label label_1 = new Label(grpState, SWT.NONE);
		label_1.setText("  ");

		newState = new Button(grpState, SWT.FLAT);
		newState.setText("Submit");
		newState.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				fireDemStatus();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
			}
		});

		// super.setEnabled(false);
	}

	protected void crseChange(final float delta)
	{
		// get teh course
		Float cF = Float.parseFloat(demCourse.getText());
		cF += delta;

		// put back in degs
		if (cF < 0)
			cF += 360;
		if (cF >= 360)
			cF -= 360;

		demCourse.setText("" + cF);
		fireDemStatus();
	}

	protected void spdChange(final float delta)
	{
		// get teh course
		Float cF = Float.parseFloat(demSpeed.getText());
		cF += delta;

		// put back in +ve speeds
		if (cF < 0)
			cF = 0f;

		demSpeed.setText("" + cF);
		fireDemStatus();
	}

	protected void depthChange(final float delta)
	{
		// get teh course
		Float cF = Float.parseFloat(demDepth.getText());
		cF += delta;

		demDepth.setText("" + cF);
		fireDemStatus();
	}

	private void fireDemStatus()
	{
		if (_listener != null)
		{
			try
			{
			_listener.demanded( MWCXMLReader.readThisDouble(getDemCourse()),
					 MWCXMLReader.readThisDouble(getDemSpeed()), 
					 MWCXMLReader.readThisDouble(getDemDepth()));
			}
			catch(final ParseException pe)
			{
				Activator.logError(Status.ERROR, "Invalid Dem numeric format",
						null);
			}
		}
		else
		{
			Activator.logError(Status.WARNING, "No dem status listener declared",
					null);
		}
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	private void setActSpeed(final double speedKts)
	{
		if (!actSpeed.isDisposed())
		{
			final String spd = df2.format(speedKts);

			actSpeed.setText(spd + "kts");
			if (demSpeed.getText().equals(""))
			{
				demSpeed.setText(df0.format(speedKts));
			}
		}
	}

	private void setActCourse(final double course)
	{
		if (!actCourse.isDisposed())
		{
			final String crse = df0.format(course);

			actCourse.setText(crse + "\u00B0");
			if (demCourse.getText().equals(""))
			{
				demCourse.setText(crse);
			}
		}
	}

	private void setActDepth(final double depthM)
	{
		if (!actDepth.isDisposed())
		{
			final String dpth = df2.format(depthM);

			actDepth.setText(dpth + "m");
			if (demDepth.getText().equals(""))
			{
				demDepth.setText(df0.format(depthM));
			}
		}
	}

	@Override
	public String getDemSpeed()
	{
		return demSpeed.getText();
	}

	@Override
	public String getDemCourse()
	{
		return demCourse.getText();
	}

	@Override
	public String getDemDepth()
	{
		return demDepth.getText();
	}

	public void setEnabled(final boolean val)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				if (!demCourse.isDisposed())
				{
					demCourse.setEnabled(val);
					demSpeed.setEnabled(val);
					demDepth.setEnabled(val);
					newState.setEnabled(val);
					partName.setText(PENDING);
				}
			}
		});

	}

	@Override
	public void setParticipant(final String name)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				if (!partName.isDisposed())
				{
					partName.setText(name);
					demCourse.setText("");
					demSpeed.setText("");
					demDepth.setText("");
				}
			}
		});

	}

	@Override
	public void setDemStatusListener(final NewDemStatus newDemStatus)
	{
		_listener = newDemStatus;
	}

	@Override
	public void moved(final ASSET.Participants.Status status)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				setActCourse(status.getCourse());
				setActSpeed(status.getSpeed().getValueIn(WorldSpeed.Kts));
				setActDepth(status.getLocation().getDepth());
			}
		});
	}
}
