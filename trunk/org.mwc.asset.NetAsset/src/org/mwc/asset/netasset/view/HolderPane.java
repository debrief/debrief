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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

public class HolderPane extends Composite
{
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

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public HolderPane(Composite parent, int style)
	{
		super(parent, SWT.BORDER);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.wrap = false;
		rowLayout.fill = true;
		setLayout(rowLayout);

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.VERTICAL));

		connectBtn = new Button(composite, SWT.NONE);
		connectBtn.setText("Connect");

		grpTime = new Group(composite, SWT.NONE);
		grpTime.setText("Time");
		grpTime.setSize(76, 83);
		grpTime.setLayout(new RowLayout(SWT.VERTICAL));
		grpTime.setEnabled(false);

		Label label_3 = new Label(grpTime, SWT.CENTER);
		label_3.setLayoutData(new RowData(140, SWT.DEFAULT));
		label_3.setFont(SWTResourceManager.getFont("Courier New", 14, SWT.BOLD));
		label_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		label_3.setForeground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		label_3.setText("00:00:00 ");

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

		Button fasterBtn = new Button(composite_2, SWT.NONE);
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
		label.setText("New Label");

		Label label_1 = new Label(grpState, SWT.NONE);
		label_1.setBounds(0, 0, 59, 14);
		label_1.setText("New Label");

		newState = new Button(grpState, SWT.NONE);
		newState.setBounds(0, 0, 94, 30);
		newState.setText("Submit");

		Composite composite_1 = new Composite(this, SWT.BORDER);
		composite_1.setLayoutData(new RowData(200, SWT.DEFAULT));
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));

		logList = new List(composite_1, SWT.BORDER | SWT.V_SCROLL);

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	public void addTimeListener(SelectionAdapter listener)
	{
		playBtn.addSelectionListener(listener);
	}

}
