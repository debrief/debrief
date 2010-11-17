package org.mwc.asset.netasset.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;

public class HolderPane extends Composite
{

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public HolderPane(Composite parent, int style)
	{
		super(parent, style);
		
		Button button = new Button(this, SWT.NONE);
		button.setBounds(24, 69, 94, 30);
		button.setText("New Button");

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
