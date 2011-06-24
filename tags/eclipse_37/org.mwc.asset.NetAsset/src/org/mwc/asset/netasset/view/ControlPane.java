package org.mwc.asset.netasset.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.grouplayout.GroupLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.grouplayout.LayoutStyle;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import com.swtdesigner.SWTResourceManager;

public class ControlPane extends Composite
{
	private Button btnHosting;
	private List _theList;
	private Label lblScenario;
	private Label lblHostName;
	
	public void setHostName(String name)
	{
		lblHostName.setText(name);
	}
	
	public void setScenarioName(String name)
	{
		lblScenario.setText(name);
	}
	
	public void addHostingListener(SelectionListener listener)
	{
		btnHosting.addSelectionListener(listener);
	}
	
	public void removeHostingListener(SelectionListener listener)
	{
		btnHosting.removeSelectionListener(listener);
	}
	
	public List getList()
	{
		return _theList;
	}

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ControlPane(Composite parent, int style)
	{
		super(parent, style);
		
		Group grpControl = new Group(this, SWT.NONE);
		grpControl.setText("Control");
		
		Group grpListeners = new Group(this, SWT.NONE);
		grpListeners.setText("Participants");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(GroupLayout.LEADING)
				.add(groupLayout.createSequentialGroup()
					.addContainerGap()
					.add(grpControl, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.RELATED)
					.add(grpListeners, GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
					.add(21))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(GroupLayout.LEADING)
				.add(groupLayout.createSequentialGroup()
					.addContainerGap()
					.add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
						.add(grpControl, GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
						.add(grpListeners, GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
					.addContainerGap())
		);
		grpControl.setLayout(new RowLayout(SWT.VERTICAL));
		
		lblScenario = new Label(grpControl, SWT.NONE);
		lblScenario.setText("[Scenario]");
		
		btnHosting = new Button(grpControl, SWT.CHECK);
		btnHosting.setText("Hosting");
		
		 lblHostName = new Label(grpControl, SWT.RIGHT);
		 lblHostName.setFont(SWTResourceManager.getFont("Lucida Grande", 8, SWT.NORMAL));
		 lblHostName.setLayoutData(new RowData(112, SWT.DEFAULT));
		lblHostName.setText("[pending]");
		
		 _theList = new List(grpListeners, SWT.BORDER);
		GroupLayout gl_grpListeners = new GroupLayout(grpListeners);
		gl_grpListeners.setHorizontalGroup(
			gl_grpListeners.createParallelGroup(GroupLayout.LEADING)
				.add(gl_grpListeners.createSequentialGroup()
					.add(_theList, GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_grpListeners.setVerticalGroup(
			gl_grpListeners.createParallelGroup(GroupLayout.LEADING)
				.add(gl_grpListeners.createSequentialGroup()
					.addContainerGap()
					.add(_theList, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
					.addContainerGap())
		);
		grpListeners.setLayout(gl_grpListeners);
		setLayout(groupLayout);

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
