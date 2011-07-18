package org.mwc.asset.netasset2.sensor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.mwc.asset.netasset2.time.IVTime;

import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.ParticipantDetectedListener;

public class VSensor extends Composite implements ParticipantDetectedListener, IVTime
{

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public VSensor(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new RowLayout(SWT.HORIZONTAL));
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}


	public void setEnabled(final boolean val)
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
			
			}
		});
	}

	@Override
	public void newDetections(DetectionList detections)
	{
		System.err.println("rx:" + detections.size() + " detections");
	}

	@Override
	public void restart(ScenarioType scenario)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newTime(long newTime)
	{
		System.out.println("stepping plot");
	}


}
