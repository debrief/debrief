package org.mwc.asset.netasset.view;

import org.eclipse.swt.SWT;
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
	private Button newState;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public HolderPane(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.VERTICAL));
		
		Group group = new Group(composite, SWT.NONE);
		group.setSize(76, 83);
		group.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label lblTime = new Label(group, SWT.CENTER);
		lblTime.setText("Time:");
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("Courier New", 14, SWT.BOLD));
		label_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		label_3.setForeground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		label_3.setText("00:00:00 ");
		
		Group state = new Group(composite, SWT.NONE);
		state.setLayout(new GridLayout(3, false));
		
		Label lblProperty = new Label(state, SWT.NONE);
		lblProperty.setText("Property");
		
		Label lblDemanded = new Label(state, SWT.NONE);
		lblDemanded.setText("Demanded");
		
		Label lblActual = new Label(state, SWT.NONE);
		lblActual.setText("Actual");
		
		Label lblCourse = new Label(state, SWT.NONE);
		lblCourse.setBounds(0, 0, 59, 14);
		lblCourse.setText("Course");
		
		demCourse = new Text(state, SWT.BORDER);
		demCourse.setText("000");
		demCourse.setBounds(0, 0, 8, 19);
		
		Label actCourse = new Label(state, SWT.NONE);
		actCourse.setBounds(0, 0, 59, 14);
		actCourse.setText("000");
		
		Label lblSpeed = new Label(state, SWT.NONE);
		lblSpeed.setBounds(0, 0, 59, 14);
		lblSpeed.setText("Speed");
		
		demSpeed = new Text(state, SWT.BORDER);
		demSpeed.setText("000");
		demSpeed.setBounds(0, 0, 8, 19);
		
		Label actSpeed = new Label(state, SWT.NONE);
		actSpeed.setSize(59, 14);
		actSpeed.setText("000");
		
		Label lblDepth = new Label(state, SWT.NONE);
		lblDepth.setBounds(0, 0, 59, 14);
		lblDepth.setText("Depth");
		
		demDepth = new Text(state, SWT.BORDER);
		demDepth.setText("000");
		demDepth.setSize(8, 19);
		
		Label actDepth = new Label(state, SWT.NONE);
		actDepth.setBounds(0, 0, 59, 14);
		actDepth.setText("000");
		
		Label label = new Label(state, SWT.NONE);
		label.setBounds(0, 0, 59, 14);
		label.setText("New Label");
		
		Label label_1 = new Label(state, SWT.NONE);
		label_1.setBounds(0, 0, 59, 14);
		label_1.setText("New Label");
		
		newState = new Button(state, SWT.NONE);
		newState.setBounds(0, 0, 94, 30);
		newState.setText("Submit");
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(new RowData(212, 296));
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		List logList = new List(composite_1, SWT.BORDER);

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
