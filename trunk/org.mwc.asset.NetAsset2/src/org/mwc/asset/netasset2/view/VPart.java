package org.mwc.asset.netasset2.view;

import org.eclipse.swt.SWT;
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

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public VPart(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new FormLayout());
		
		 partName = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.bottom = new FormAttachment(0, 34);
		fd_lblNewLabel.right = new FormAttachment(0, 212);
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		partName.setLayoutData(fd_lblNewLabel);
		partName.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.BOLD));
		partName.setText("[Pending]");
		

		grpState = new Group(this, SWT.NONE);
		grpState.setText("State");
		grpState.setLayout(new GridLayout(3, false));
		grpState.setEnabled(false);

		Label lblProperty = new Label(grpState, SWT.NONE);
		lblProperty.setText("Property");

		Label lblDemanded = new Label(grpState, SWT.NONE);
		lblDemanded.setText("Demanded");

		Label lblActual = new Label(grpState, SWT.NONE);
		lblActual.setText("Actual");

		Label lblCourse = new Label(grpState, SWT.NONE);
		lblCourse.setBounds(0, 0, 59, 14);
		lblCourse.setText("Course");

		demCourse = new Text(grpState, SWT.BORDER);
		demCourse.setText("000");
		demCourse.setBounds(0, 0, 8, 19);
		demCourse.setEnabled(false);

		actCourse = new Label(grpState, SWT.NONE);
		actCourse.setBounds(0, 0, 59, 14);
		actCourse.setText("000");

		Label lblSpeed = new Label(grpState, SWT.NONE);
		lblSpeed.setBounds(0, 0, 59, 14);
		lblSpeed.setText("Speed");

		demSpeed = new Text(grpState, SWT.BORDER);
		demSpeed.setText("000");
		demSpeed.setBounds(0, 0, 8, 19);
		demSpeed.setEnabled(false);

		actSpeed = new Label(grpState, SWT.NONE);
		actSpeed.setSize(59, 14);
		actSpeed.setText("000");

		Label lblDepth = new Label(grpState, SWT.NONE);
		lblDepth.setBounds(0, 0, 59, 14);
		lblDepth.setText("Depth");

		demDepth = new Text(grpState, SWT.BORDER);
		demDepth.setText("000");
		demDepth.setSize(8, 19);
		demDepth.setEnabled(false);

		actDepth = new Label(grpState, SWT.NONE);
		actDepth.setBounds(0, 0, 59, 14);
		actDepth.setText("000");

		Label label = new Label(grpState, SWT.NONE);
		label.setBounds(0, 0, 59, 14);
		label.setText("  ");

		Label label_1 = new Label(grpState, SWT.NONE);
		label_1.setBounds(0, 0, 59, 14);
		label_1.setText("  ");

		newState = new Button(grpState, SWT.NONE);
		newState.setBounds(0, 0, 94, 30);
		newState.setText("Submit");

//		super.setEnabled(false);
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
	public void addSubmitListener(SelectionListener listener)
	{
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
