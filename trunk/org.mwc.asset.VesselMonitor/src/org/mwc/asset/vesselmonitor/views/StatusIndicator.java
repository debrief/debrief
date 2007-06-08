package org.mwc.asset.vesselmonitor.views;

import java.text.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.*;
import MWC.GenericData.WorldSpeed;

public class StatusIndicator extends org.eclipse.swt.widgets.Composite
{
	private Group MonitorGroup;

	private Label courseLbl;

	private Label speedLbl;

	private Label curSpeed;

	private Label curCourse;

	private Label curDepth;

	private Label spacer1;

	private Label statusLbl;

	private Label demDepthLbl;

	private Label actualLbl;

	private Label demSpeedLbl;

	private Label demCourseLbl;

	private Label demLbl;

	private Label nameLabel;

	private Label depthLbl;

	/**
	 * Auto-generated main method to display this
	 * org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void main(String[] args)
	{
		showGUI();
	}

	/**
	 * Auto-generated method to display this org.eclipse.swt.widgets.Composite
	 * inside a new Shell.
	 */
	public static void showGUI()
	{
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		StatusIndicator inst = new StatusIndicator(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if (size.x == 0 && size.y == 0)
		{
			inst.pack();
			shell.pack();
		}
		else
		{
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public StatusIndicator(org.eclipse.swt.widgets.Composite parent, int style)
	{
		super(parent, style);
		initGUI();
	}

	public void setName(String val)
	{
		if (!this.isDisposed())
		{
			nameLabel.setText(val);
		}
	}

	private void initGUI()
	{
		try
		{
			RowLayout thisLayout = new RowLayout(org.eclipse.swt.SWT.VERTICAL);
			thisLayout.type = SWT.VERTICAL;
			thisLayout.wrap = false;
			this.setLayout(thisLayout);
			this.setSize(254, 140);
			{
				nameLabel = new Label(this, SWT.NONE);
				RowData nameLabelLData = new RowData();
				nameLabelLData.width = 187;
				nameLabelLData.height = 13;
				nameLabel.setLayoutData(nameLabelLData);
				nameLabel.setText("====");
			}
			{
				statusLbl = new Label(this, SWT.NONE);
				RowData statusLblLData = new RowData();
				statusLblLData.width = 174;
				statusLblLData.height = 13;
				statusLbl.setLayoutData(statusLblLData);
				statusLbl.setText("======");
			}
			{
				MonitorGroup = new Group(this, SWT.NONE);
				GridLayout MonitorGroupLayout = new GridLayout();
				MonitorGroupLayout.numColumns = 3;
				MonitorGroupLayout.horizontalSpacing = 2;
				MonitorGroupLayout.marginHeight = 2;
				MonitorGroupLayout.marginWidth = 2;
				MonitorGroupLayout.verticalSpacing = 2;
				MonitorGroup.setLayout(MonitorGroupLayout);
				RowData MonitorGroupLData = new RowData();
				MonitorGroupLData.width = 183;
				MonitorGroupLData.height = 62;
				MonitorGroup.setLayoutData(MonitorGroupLData);
				MonitorGroup.setText("Status");
				{
					spacer1 = new Label(MonitorGroup, SWT.NONE);
					spacer1.setText("    ");
				}
				{
					actualLbl = new Label(MonitorGroup, SWT.NONE);
					actualLbl.setText("Actual");
				}
				{
					demLbl = new Label(MonitorGroup, SWT.NONE);
					demLbl.setText("Demanded");
				}
				{
					courseLbl = new Label(MonitorGroup, SWT.NONE);
					courseLbl.setText("Course:");
				}
				{
					curCourse = new Label(MonitorGroup, SWT.NONE);
					GridData curCourseLData = new GridData();
					curCourseLData.widthHint = 47;
					curCourseLData.heightHint = 13;
					curCourse.setLayoutData(curCourseLData);
					curCourse.setText("====");
				}
				{
					demCourseLbl = new Label(MonitorGroup, SWT.NONE);
					GridData demCourseLblLData = new GridData();
					demCourseLblLData.widthHint = 52;
					demCourseLblLData.heightHint = 13;
					demCourseLbl.setLayoutData(demCourseLblLData);
					demCourseLbl.setText("=====");
				}
				{
					speedLbl = new Label(MonitorGroup, SWT.NONE);
					speedLbl.setText("Speed:");
				}
				{
					curSpeed = new Label(MonitorGroup, SWT.NONE);
					GridData curSpeedLData = new GridData();
					curSpeedLData.widthHint = 54;
					curSpeedLData.heightHint = 13;
					curSpeed.setLayoutData(curSpeedLData);
					curSpeed.setText("=====");
				}
				{
					demSpeedLbl = new Label(MonitorGroup, SWT.NONE);
					GridData demSpeedLblLData = new GridData();
					demSpeedLblLData.widthHint = 55;
					demSpeedLblLData.heightHint = 13;
					demSpeedLbl.setLayoutData(demSpeedLblLData);
					demSpeedLbl.setText("=====");
				}
				{
					depthLbl = new Label(MonitorGroup, SWT.NONE);
					depthLbl.setText("Depth:");
				}
				{
					curDepth = new Label(MonitorGroup, SWT.NONE);
					GridData curDepthLData = new GridData();
					curDepthLData.widthHint = 53;
					curDepthLData.heightHint = 13;
					curDepth.setLayoutData(curDepthLData);
					curDepth.setText("=====");
				}
				{
					demDepthLbl = new Label(MonitorGroup, SWT.NONE);
					GridData demDepthLblLData = new GridData();
					demDepthLblLData.widthHint = 56;
					demDepthLblLData.heightHint = 13;
					demDepthLbl.setLayoutData(demDepthLblLData);
					demDepthLbl.setText("=====");
				}
			}
			this.layout();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Label getCurCourse()
	{
		return curCourse;
	}

	public Label getCurSpeed()
	{
		return curSpeed;
	}

	public Label getCurDepth()
	{
		return curDepth;
	}

	public void setStatus(Status newStatus)
	{
		curSpeed.setText(formatMe(newStatus.getSpeed().getValueIn(WorldSpeed.Kts)) + " kts");
		curCourse.setText("" + formatMe(newStatus.getCourse()) + "degs");
		curDepth.setText("" + formatMe(newStatus.getLocation().getDepth()) + "");
	}

	private NumberFormat _format = new DecimalFormat("0.0");

	private String formatMe(double val)
	{
		return _format.format(val);
	}

	public void setDecision(String description, DemandedStatus dem_status)
	{
		// sort out what we know
		if (dem_status instanceof SimpleDemandedStatus)
		{
			SimpleDemandedStatus sds = (SimpleDemandedStatus) dem_status;
			demSpeedLbl.setText("" + formatMe(sds.getSpeed()) + " kts");
			demCourseLbl.setText("" + formatMe(sds.getCourse()) + " degs");
			demDepthLbl.setText("" + formatMe(sds.getHeight()) + " m");
		}
		else
		{
			demSpeedLbl.setText("n/a");
			demCourseLbl.setText("n/a");
			demDepthLbl.setText("n/a");
		}
		statusLbl.setText(description);
	}

}
