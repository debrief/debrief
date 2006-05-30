package org.mwc.cmap.core.editor_views;

import java.beans.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
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
	private PolygonEditorControl _myEditor;

	public PolygonEditorView()
	{
		_pSupport = new PropertyChangeSupport(this);
	}
	
	public void createPartControl(Composite parent)
	{
		_myEditor = new PolygonEditorControl(parent, SWT.NONE){
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				btnPushed(e);
			}};
	}

	protected void btnPushed(SelectionEvent e)
	{
		// ok, the user has done something in the editor control. what was it?
		Object source = e.widget;
		if(source == _myEditor.newBtn)
			System.out.println("new pressed!");
		else if(source == _myEditor.delBtn)
			System.out.println("delete pressed!");
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
		_myEditor.editorPanel.setText(_myPath.getPoints().size() + " Points");
	}

}
