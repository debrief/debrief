package org.mwc.debrief.core.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class PlotOutlinePage extends Page implements IContentOutlinePage
{
	private PlotEditor _editor;
	private Label label;

	public PlotOutlinePage(PlotEditor _editor)
	{
		super();
		this._editor = _editor;
	}

	@Override
	public void createControl(Composite parent)
	{
		label = new Label(parent, SWT.NONE);
		label.setText("Test");
	}

	@Override
	public Control getControl()
	{
		return label;
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ISelection getSelection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelection(ISelection selection)
	{
		// TODO Auto-generated method stub
		
	}

}
