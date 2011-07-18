package org.mwc.asset.netasset2.part;

import java.text.DecimalFormat;

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

public class VPart extends Composite implements IVPartControl, IVPartMovement
{
	DecimalFormat df2 = new DecimalFormat("0.00");
	DecimalFormat df0 = new DecimalFormat("0");

	private static final String PENDING = "[Pending]";
	private Group grpState;
	private Text demCourse;
	private Label actCourse;
	private Text demSpeed;
	private Label actSpeed;
	private Text demDepth;
	private Label actDepth;
	private Button newState;
	private Label partName;
	private NewDemStatus _listener;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public VPart(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new FormLayout());

		KeyListener enterListener = new KeyListener()
		{

			@Override
			public void keyPressed(KeyEvent e)
			{
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.keyCode == 13)
				{
					fireDemStatus();
				}
			}
		};

		partName = new Label(this, SWT.NONE);
		FormData fd_partName = new FormData();
		fd_partName.bottom = new FormAttachment(0, 19);
		fd_partName.right = new FormAttachment(0, 222);
		fd_partName.top = new FormAttachment(0);
		fd_partName.left = new FormAttachment(0);
		partName.setLayoutData(fd_partName);
		partName.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.BOLD));
		partName.setText(PENDING);

		grpState = new Group(this, SWT.NONE);
		FormData fd_grpState = new FormData();
		fd_grpState.bottom = new FormAttachment(0, 169);
		fd_grpState.right = new FormAttachment(0, 210);
		fd_grpState.top = new FormAttachment(0, 25);
		fd_grpState.left = new FormAttachment(0, 10);
		grpState.setLayoutData(fd_grpState);
		grpState.setText("State");
		grpState.setLayout(new GridLayout(3, false));

		Label lblProperty = new Label(grpState, SWT.NONE);
		lblProperty.setText("   ");

		Label lblDemanded = new Label(grpState, SWT.NONE);
		lblDemanded.setText("Demanded");

		Label lblActual = new Label(grpState, SWT.NONE);
		lblActual.setText("Actual");

		Label lblCourse = new Label(grpState, SWT.NONE);
		lblCourse.setText("Course");

		demCourse = new Text(grpState, SWT.BORDER);
		demCourse.setText("000  ");
		demCourse.setEnabled(false);
		demCourse.addKeyListener(enterListener);

		actCourse = new Label(grpState, SWT.NONE);
		actCourse.setText("000 00");

		Label lblSpeed = new Label(grpState, SWT.NONE);
		lblSpeed.setText("Speed");

		demSpeed = new Text(grpState, SWT.BORDER);
		demSpeed.setText("000  ");
		demSpeed.setEnabled(false);
		demSpeed.addKeyListener(enterListener);

		actSpeed = new Label(grpState, SWT.NONE);
		actSpeed.setText("000 00");

		Label lblDepth = new Label(grpState, SWT.NONE);
		lblDepth.setText("Depth");

		demDepth = new Text(grpState, SWT.BORDER);
		demDepth.setText("000  ");
		demDepth.setEnabled(false);
		demDepth.addKeyListener(enterListener);

		actDepth = new Label(grpState, SWT.NONE);
		actDepth.setText("000 00");

		Label label = new Label(grpState, SWT.NONE);
		label.setText("  ");

		Label label_1 = new Label(grpState, SWT.NONE);
		label_1.setText("  ");

		newState = new Button(grpState, SWT.NONE);
		newState.setText("Submit");
		newState.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fireDemStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

		// super.setEnabled(false);
	}

	private void fireDemStatus()
	{
		if (_listener != null)
		{
			_listener.demanded(Double.valueOf(getDemCourse()),
					Double.valueOf(getDemSpeed()), Double.valueOf(getDemDepth()));
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
				demCourse.setEnabled(val);
				demSpeed.setEnabled(val);
				demDepth.setEnabled(val);
				newState.setEnabled(val);
				partName.setText(PENDING);
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
				partName.setText(name);
				demCourse.setText("");
				demSpeed.setText("");
				demDepth.setText("");
			}
		});

	}

	@Override
	public void setDemStatusListener(NewDemStatus newDemStatus)
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
