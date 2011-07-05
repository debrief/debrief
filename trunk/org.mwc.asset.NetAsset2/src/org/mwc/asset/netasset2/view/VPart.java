package org.mwc.asset.netasset2.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class VPart extends Composite implements IVPart
{
	private Group grpState;
	private Text demCourse;
	private Label actCourse;
	private Text demSpeed;
	private Label actSpeed;
	private Text demDepth;
	private Label actDepth;
	private Button newState;
	private Label partName;
	private SelectionListener _subListener;

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
					_subListener.widgetSelected(null);
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
		partName.setText("[Pending]");

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
		lblProperty.setText("Property");

		Label lblDemanded = new Label(grpState, SWT.NONE);
		lblDemanded.setText("Demanded");

		Label lblActual = new Label(grpState, SWT.NONE);
		lblActual.setText("Actual");

		Label lblCourse = new Label(grpState, SWT.NONE);
		lblCourse.setText("Course");

		demCourse = new Text(grpState, SWT.BORDER);
		demCourse.setText("000");
		demCourse.setEnabled(false);
		demCourse.addKeyListener(enterListener);



		actCourse = new Label(grpState, SWT.NONE);
		actCourse.setText("000");

		Label lblSpeed = new Label(grpState, SWT.NONE);
		lblSpeed.setText("Speed");

		demSpeed = new Text(grpState, SWT.BORDER);
		demSpeed.setText("000");
		demSpeed.setEnabled(false);
		demSpeed.addKeyListener(enterListener);

		actSpeed = new Label(grpState, SWT.NONE);
		actSpeed.setText("000");

		Label lblDepth = new Label(grpState, SWT.NONE);
		lblDepth.setText("Depth");

		demDepth = new Text(grpState, SWT.BORDER);
		demDepth.setText("000");
		demDepth.setEnabled(false);
		demDepth.addKeyListener(enterListener);


		actDepth = new Label(grpState, SWT.NONE);
		actDepth.setText("000");

		Label label = new Label(grpState, SWT.NONE);
		label.setText("  ");

		Label label_1 = new Label(grpState, SWT.NONE);
		label_1.setText("  ");

		newState = new Button(grpState, SWT.NONE);
		newState.setText("Submit");

		// super.setEnabled(false);
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setActSpeed(String val)
	{
		actSpeed.setText(val);
	}

	@Override
	public void setActCourse(String val)
	{
		actCourse.setText(val);
	}

	@Override
	public void setActDepth(String val)
	{
		actDepth.setText(val);
	}

	@Override
	public void setSubmitListener(SelectionListener listener)
	{
		_subListener = listener;
		newState.addSelectionListener(listener);
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

	public void setEnabled(boolean val)
	{
		super.setEnabled(val);
		demCourse.setEnabled(val);
		demSpeed.setEnabled(val);
		demDepth.setEnabled(val);
		newState.setEnabled(val);
	}

	@Override
	public void setParticipant(String name)
	{
		partName.setText(name);
	}

}
