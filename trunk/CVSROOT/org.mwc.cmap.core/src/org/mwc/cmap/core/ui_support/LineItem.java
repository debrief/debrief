package org.mwc.cmap.core.ui_support;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


/** class representing line of text which may be plased on the status bar
 * 
 * @author ian.mayo
 *
 */
public class LineItem extends ControlContribution
{
	Label label;
	
	String _lastText = " 00°00\'00.00\"N 000°00\'00.00\"W ";

	public LineItem(String id)
	{
		super(id);
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#isDynamic()
	 */
	public boolean isDynamic()
	{
		return true;
	}
	
	public void setText(String val)
	{
		label.setText(val);
		
		_lastText = val;
	}

	public boolean isDisposed()
	{
		return label.isDisposed();
	}
	
	/**
	 * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createControl(Composite parent)
	{
		label = new Label(parent, SWT.RIGHT | SWT.BORDER);
		label.setText(_lastText);
		label.setToolTipText("");
		label.setSize(550, 20);
		return label;
	}

}
