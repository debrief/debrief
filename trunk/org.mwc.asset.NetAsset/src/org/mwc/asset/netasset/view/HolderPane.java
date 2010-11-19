package org.mwc.asset.netasset.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.grouplayout.GroupLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

public class HolderPane extends Composite
{
	private static final String CONTROL = "Control";
	private static final String CONNECT = " Connect ";
	private Text demCourse;
	private Text demSpeed;
	private Text demDepth;
	private Button slowerBtn;
	private Button playBtn;
	private Label actSpeed;
	private Label actCourse;
	private Label actDepth;
	private Button newState;
	private List logList;
	private Button connectBtn;
	private Group grpState;
	private Group grpTime;
	private Label lblTime;
	private Button fasterBtn;
	private Composite composite_3;
	private Button controlBtn;
	private Group grpDetections;
	private Composite plotContainer;

	static private String toStringLikeThis(long theVal, String thePattern)
	{
		java.util.Date theTime = new java.util.Date(theVal);
		String res;

		DateFormat df = new SimpleDateFormat(thePattern);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		res = df.format(theTime);

		return res;
	}

	// ////////////////////////////
	// my bits
	// ////////////////////////////
	public void setActSpeed(String val)
	{
		actSpeed.setText(val);
	}

	public void setActCourse(String val)
	{
		actCourse.setText(val);
	}

	public void setActDepth(String val)
	{
		actDepth.setText(val);
	}

	public void addSubmitListener(SelectionListener listener)
	{
		newState.addSelectionListener(listener);
	}

	public void removeSubmitListener(SelectionListener listener)
	{
		newState.removeSelectionListener(listener);
	}

	public String getDemCourse()
	{
		return demCourse.getText();
	}

	public String getDemSpeed()
	{
		return demSpeed.getText();
	}

	public String getDemDepth()
	{
		return demDepth.getText();
	}

	public void logEvent(long time, String type, String desc)
	{
		String item = toStringLikeThis(time, "HHmm:ss") + " " + type + " " + desc;
		logList.add(item, 0);
	}

	public void addTakeControlListener(SelectionListener listener)
	{
		controlBtn.addSelectionListener(listener);
	}

	public void removeControlListener(SelectionListener listener)
	{
		controlBtn.removeSelectionListener(listener);
	}
	public void addConnectListener(SelectionListener listener)
	{
		connectBtn.addSelectionListener(listener);
	}

	public void removeConnectListener(SelectionListener listener)
	{
		connectBtn.removeSelectionListener(listener);
	}


	public void setTimeEnabled(boolean val)
	{
		grpTime.setEnabled(val);
	}

	public void setStateEnabled(boolean val)
	{
		grpState.setEnabled(val);
	}
	

	public void setTime(String string)
	{
		lblTime.setText(string);
	}

	public Composite getPlotContainer()
	{
		return plotContainer;
	}
	
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public HolderPane(Composite parent, int style)
	{
		super(parent, SWT.BORDER);

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.VERTICAL));
		
		composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));

		connectBtn = new Button(composite_3, SWT.TOGGLE);
		connectBtn.setText(CONNECT);
		connectBtn.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(connectBtn.getText().equals(CONNECT))
					connectBtn.setText("Disconect");
				else
					connectBtn.setText(CONNECT);
			}
		});
		
		 controlBtn = new Button(composite_3, SWT.TOGGLE);
		controlBtn.setText(CONTROL);
		controlBtn.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(controlBtn.getText().equals(CONTROL))
					controlBtn.setText("Release");
				else
					controlBtn.setText(CONTROL);
			}
		});

		grpTime = new Group(composite, SWT.NONE);
		grpTime.setText("Time");
		grpTime.setSize(90, 83);
		grpTime.setLayout(new RowLayout(SWT.VERTICAL));
		grpTime.setEnabled(false);

		lblTime = new Label(grpTime, SWT.CENTER);
		lblTime.setLayoutData(new RowData(166, SWT.DEFAULT));
		lblTime.setFont(SWTResourceManager.getFont("Courier", 10, SWT.BOLD));
		lblTime.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		lblTime.setForeground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		lblTime.setText("00/0000/00 00:00:00");

		Composite composite_2 = new Composite(grpTime, SWT.NONE);
		composite_2.setLayout(new RowLayout(SWT.HORIZONTAL));

		playBtn = new Button(composite_2, SWT.TOGGLE);
		playBtn.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (playBtn.getText().equals(">"))
					playBtn.setText("||");
				else
					playBtn.setText(">");
			}
		});
		playBtn.setText(">");

		 fasterBtn = new Button(composite_2, SWT.NONE);
		fasterBtn.setText("++");

		slowerBtn = new Button(composite_2, SWT.CENTER);
		slowerBtn.setText("--");

		grpState = new Group(composite, SWT.NONE);
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

		actCourse = new Label(grpState, SWT.NONE);
		actCourse.setBounds(0, 0, 59, 14);
		actCourse.setText("000");

		Label lblSpeed = new Label(grpState, SWT.NONE);
		lblSpeed.setBounds(0, 0, 59, 14);
		lblSpeed.setText("Speed");

		demSpeed = new Text(grpState, SWT.BORDER);
		demSpeed.setText("000");
		demSpeed.setBounds(0, 0, 8, 19);

		actSpeed = new Label(grpState, SWT.NONE);
		actSpeed.setSize(59, 14);
		actSpeed.setText("000");

		Label lblDepth = new Label(grpState, SWT.NONE);
		lblDepth.setBounds(0, 0, 59, 14);
		lblDepth.setText("Depth");

		demDepth = new Text(grpState, SWT.BORDER);
		demDepth.setText("000");
		demDepth.setSize(8, 19);

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
		
		grpDetections = new Group(composite, SWT.NONE);
		grpDetections.setLayoutData(new RowData(210, 153));
		grpDetections.setText("Detections");
		
				logList = new List(grpDetections, SWT.BORDER | SWT.V_SCROLL);
				GroupLayout gl_grpDetections = new GroupLayout(grpDetections);
				gl_grpDetections.setHorizontalGroup(
					gl_grpDetections.createParallelGroup(GroupLayout.LEADING)
						.add(gl_grpDetections.createSequentialGroup()
							.add(logList, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
				gl_grpDetections.setVerticalGroup(
					gl_grpDetections.createParallelGroup(GroupLayout.LEADING)
						.add(gl_grpDetections.createSequentialGroup()
							.add(logList, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(29, Short.MAX_VALUE))
				);
				grpDetections.setLayout(gl_grpDetections);

		plotContainer = new Composite(this, SWT.BORDER);
		plotContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(GroupLayout.LEADING)
				.add(groupLayout.createSequentialGroup()
					.add(3)
					.add(composite, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.add(3)
					.add(plotContainer, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(GroupLayout.LEADING)
				.add(groupLayout.createSequentialGroup()
					.add(3)
					.add(groupLayout.createParallelGroup(GroupLayout.LEADING)
						.add(groupLayout.createSequentialGroup()
							.add(plotContainer, GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
							.add(16))
						.add(composite, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
		);
		setLayout(groupLayout);

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	public void addTimeListener(SelectionListener listener)
	{
		playBtn.addSelectionListener(listener);
	}

	public void addTimeSpeedListener(SelectionListener listener)
	{
		slowerBtn.addSelectionListener(listener);
		fasterBtn.addSelectionListener(listener);
	}
}
