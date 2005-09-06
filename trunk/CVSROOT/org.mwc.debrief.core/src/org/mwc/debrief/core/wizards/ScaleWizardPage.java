package org.mwc.debrief.core.wizards;

import java.beans.*;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.mwc.cmap.core.property_support.DebriefProperty;

import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.ScalePainter;
import MWC.GUI.Editable.EditorType;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class ScaleWizardPage extends WizardPage
{
	private Text containerText;

	private Text fileText;

	private ISelection selection;

	private ScalePainter _scale;

	//  the controls containing the user data

	/** checkbox for whether to include a scale
	 * 
	 */ 
	private Button _enabledBtn;	

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public ScaleWizardPage(ISelection selection)
	{
		super("wizardPage");
		setTitle("Add Scale to Plot");
		setDescription("This page adds a scale to your plot");
		this.selection = selection;
	}

	public ScalePainter getScale()
	{
		return _scale;
	}

	protected Plottable createMe()
	{
		if(_scale == null)
			 _scale = new ScalePainter();
		
		return _scale;
	}
	
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		// insert the "enabled" item first
		Label label = new Label(container, SWT.NONE);
		label.setText("Include");
		
		_enabledBtn = new Button(container, SWT.CHECK);
		_enabledBtn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e)
			{
				enabledChanged();
			}});
		
		label = new Label(container, SWT.NONE);
		label.setText("Whether to include a Scale");
		
		// hey, let's try and automate it!!!
		Plottable myItem = createMe();
		EditorType info = myItem.getInfo();
		PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
		for (int i = 0; i < descriptors.length; i++)
		{
			PropertyDescriptor thisD = descriptors[i];
			
			DebriefProperty thisProp = new DebriefProperty(thisD, myItem, null);
			

	    // ok, did we find a set of tags or a text-item?
	    if(thisProp != null)
	    {
	    	// cool, insert the items
	    	label = new Label(container, SWT.NONE);
	    	label.setText(thisProp.getDisplayName());

	    	// and now for the editor bit.
	    	thisProp.createEditor(container);
	    	
	    	label = new Label(container, SWT.NONE);
	    	label.setText(thisProp.getDescription());
	    }
			
		}
				
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize()
	{
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection)
		{
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;

			// IN HERE WE CAN USE WHATEVER IS CURRENTLY SELECTED FOR BACKGROUND INFO
		}
	}

	/**
	 * Ensures that all fields are set.
	 */
	private void enabledChanged()
	{
	}

	/**
	 * Ensures that all fields are set.
	 */
	private void dialogChanged()
	{
		updateStatus(null);
	}

	private void updateStatus(String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}