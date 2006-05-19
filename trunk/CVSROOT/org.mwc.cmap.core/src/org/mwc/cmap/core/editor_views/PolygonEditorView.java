package org.mwc.cmap.core.editor_views;

import java.beans.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.part.ViewPart;

import MWC.GenericData.WorldPath;

public class PolygonEditorView extends ViewPart
{

	private FormToolkit _toolkit;
	private Form _form;
	private WorldPath _myPath;
	private PropertyChangeSupport _pSupport;
	private Label _tstLabel;

	public PolygonEditorView()
	{
		_pSupport = new PropertyChangeSupport(this);
	}
	
	public void createPartControl(Composite parent)
	{
		_toolkit = new FormToolkit(parent.getDisplay());
		_form = _toolkit.createForm(parent);
		_form.setText("Polygon gets edited here (to be implemented)");
		
		GridLayout grid = new GridLayout();
		_form.getBody().setLayout(grid);
		_tstLabel = new Label(_form.getBody(), SWT.NONE);
		_tstLabel.setText("====    pending     =====");
		
	}

	public void setFocus()
	{
	}
	
	public void dispose()
	{
		_toolkit.dispose();
		_form.dispose();
	}
	
	public void setPolygon(WorldPath thePath)
	{
		_myPath = thePath;
		updateUI();
	}
	
	public void addListener(PropertyChangeListener listener)
	{
	  _pSupport.addPropertyChangeListener(listener);
	}
	
	public void removeListener(PropertyChangeListener listener)
	{
		_pSupport.removePropertyChangeListener(listener);
	}

	/** ok, we've received some data. store it.
	 * 
	 *
	 */
	private void updateUI()
	{
		_tstLabel.setText(_myPath.toString() + " id:" + _myPath.hashCode());
	}

}
