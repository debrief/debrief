package org.mwc.cmap.timebar.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.timebar.controls.TimeBarControl;

import MWC.GUI.Layers;

public class TimeBarView extends ViewPart {
	
	TimeBarControl _control;
	Layers _myLayers;
	
	@Override
	public void createPartControl(Composite parent) 
	{
		_control = new TimeBarControl(parent);
		
		getSite().setSelectionProvider(_control);
		
		ISelectionChangedListener listener = new ISelectionChangedListener() {
			
			@Override			
			public void selectionChanged(SelectionChangedEvent event)
			{
				// right, see what it is
				ISelection sel = event.getSelection();
				if (!(sel instanceof IStructuredSelection))
		               return;
		        IStructuredSelection ss = (IStructuredSelection) sel;
		        Object o = ss.getFirstElement();
		        if (o instanceof EditableWrapper) {
		        	//TODO: update control
		        }
			}		         
	      };
	      _control.addSelectionChangedListener(listener);
	}
	
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	public Object getAdapter(Class adapter)
	{
		Object res = null;

		if (adapter == ISelectionProvider.class)
		{
			res = _control;
		}
		else
		{
			res = super.getAdapter(adapter);
		}

		return res;
	}

}
