package org.mwc.asset.netasset.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

public class HolderPane extends Composite
{
	private Text demCourse;

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
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		
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
		
		Composite composite_1 = new Composite(this, SWT.NONE);

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
