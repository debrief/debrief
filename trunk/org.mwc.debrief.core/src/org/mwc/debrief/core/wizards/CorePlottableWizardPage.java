package org.mwc.debrief.core.wizards;

import java.beans.*;
import java.util.*;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.DebriefProperty;

import MWC.GUI.Plottable;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

abstract public class CorePlottableWizardPage extends WizardPage
{
	private ISelection selection;

	protected Plottable _plottable;

	private java.util.List<Control> _myEditors;

	// the controls containing the user data

	/**
	 * checkbox for whether to include a scale
	 */
	private Button _enabledBtn;

	private boolean _optional;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public CorePlottableWizardPage(ISelection selection, String pageName,
			String title, String description, String imageName, boolean optional)
	{
		super(pageName);
		_optional = optional;
		setTitle(title);
		setDescription(description);
		this.selection = selection;

		// ok, now try to set the image
		if (imageName != null)
		{
			ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(
					"org.mwc.debrief.core", imageName);
			if (id != null)
				super.setImageDescriptor(id);
			else
				CorePlugin.logError(Status.WARNING, "Wizard image file not found for:"
						+ imageName, null);
		}
	}

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public CorePlottableWizardPage(ISelection selection, String pageName,
			String title, String description, String imageName)
	{
		this(selection, pageName, title, description, imageName, true);
	}

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public CorePlottableWizardPage(ISelection selection, String pageName,
			String title, String description)
	{
		this(selection, pageName, title, description, null);
	}

	/**
	 * retrieve the edited item
	 * 
	 * @return
	 */
	final public Plottable getPlottable()
	{
		final Plottable res;

		// hmm, does the user want one?
		if (!_optional)
		{
			// yes, return it.
			res = _plottable;
		}
		else if (_enabledBtn.getSelection())
		{
			res = _plottable;
		}
		else
		{
			// no, return null instead
			res = null;
		}

		return res;
	}

	/**
	 * ok, create an instance of the thing we're editing
	 * 
	 * @return
	 */
	abstract protected Plottable createMe();

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	final public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		if (_optional)
		{
			// insert the "enabled" item first
			Label label = new Label(container, SWT.NONE);
			label.setText("Include");

			_enabledBtn = new Button(container, SWT.CHECK);

			// set enabled, so the controls are editable
			_enabledBtn.setSelection(true);

			// now start listening out for changes for the enabled btn
			_enabledBtn.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					enabledChanged();
					dialogChanged();
				}
			});

			label = new Label(container, SWT.NONE);
			label.setText("Whether to include this item");

		}

		// hey, let's try and automate it!!!
		_plottable = createMe();

		// ok, either generate by hand or automatically determine the editable bits
		// for this item
		PropertyDescriptor[] descriptors = getPropertyDescriptors();

		// ok, we've got our list of property descriptors, stick them into a grid
		populateEditors(container, _plottable, descriptors);

		initialize();
		dialogChanged();
		setControl(container);

		// finally, just clear the error message, we're not ready for it yet
		setErrorMessage(null);

	}

	/**
	 * @return
	 */
	abstract protected PropertyDescriptor[] getPropertyDescriptors();

	/**
	 * @param container
	 *          the think to stick our controls into
	 * @param myItem
	 *          the object we're editing
	 * @param descriptors
	 *          the set of editable properties for this object
	 */
	final protected void populateEditors(Composite container, Plottable myItem,
			PropertyDescriptor[] descriptors)
	{
		// build up the list of editors
		_myEditors = new Vector<Control>(0, 1);

		Label label;
		// right, walk through the properties
		for (int i = 0; i < descriptors.length; i++)
		{
			PropertyDescriptor thisD = descriptors[i];

			// ok, determine the editor & helper classes for this property
			DebriefProperty thisProp = new DebriefProperty(thisD, myItem, null);

			// ok, did we find a set of tags or a text-item?
			if (thisProp != null)
			{
				// cool, insert the items
				label = new Label(container, SWT.NONE);
				label.setText(thisProp.getDisplayName());

				// and now for the editor bit.
				Control newEditor = thisProp.createEditor(container);
				if (newEditor != null)
				{
					// ok, put the property name in the editor, so we can
					// easily get it when we're doing validation
					newEditor.setData(thisD.getName());

					// ok, remember the editor, so we can handle enable/disable later on
					_myEditors.add(newEditor);

					// listen out for changes on this control,
					newEditor.addListener(SWT.Selection, new Listener()
					{
						public void handleEvent(Event event)
						{
							dialogChanged();
						}
					});
				}
				else
				{
					// insert duff label as place-holder
					label = new Label(container, SWT.BORDER);
					label.setText("Suitable editor not found");
				}

				label = new Label(container, SWT.NONE);
				label.setText(thisProp.getDescription());
			}

		}
	}

	/**
	 * create a property descriptor to take the auto-determined editor class
	 * 
	 * @param name
	 *          of property
	 * @param description
	 *          help-text
	 * @param subject
	 *          object being edited
	 * @return new property descriptor
	 */
	final protected static PropertyDescriptor prop(String name,
			String description, Object subject)
	{
		PropertyDescriptor res = null;
		try
		{
			res = new PropertyDescriptor(name, subject.getClass());
			res.setShortDescription(description);
		}
		catch (IntrospectionException e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * create a property descriptor with supplied editor class
	 * 
	 * @param name
	 *          of property
	 * @param description
	 *          help-text
	 * @param subject
	 *          object being edited
	 * @param editorClass
	 *          the specified editor to use
	 * @return new property descriptor
	 * @return
	 */
	final protected static PropertyDescriptor longProp(String name,
			String description, Object subject, Class<?> editorClass)
	{
		PropertyDescriptor res = null;
		res = prop(name, description, subject);
		res.setPropertyEditorClass(editorClass);
		return res;
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	final protected void initialize()
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
	final void enabledChanged()
	{
		// cycle through our controls, enabling/disabling them as necessary
		for (Iterator<Control> iter = _myEditors.iterator(); iter.hasNext();)
		{
			Control thisC = (Control) iter.next();
			thisC.setEnabled(_enabledBtn.getSelection());
		}
	}

	/**
	 * Ensures that all fields are set.
	 */
	final void dialogChanged()
	{
		if (_optional)
		{
			if (_enabledBtn.getSelection())
			{
				// right, controls are enabled, check we've got something in all of them
				for (Iterator<Control> iterator = _myEditors.iterator(); iterator
						.hasNext();)
				{
					Control thisC = (Control) iterator.next();
					if (thisC instanceof Combo)
					{
						Combo combo = (Combo) thisC;
						int sel = combo.getSelectionIndex();
						if (sel == -1)
						{
							updateStatus("Please select a value for:" + combo.getData());
							return;
						}
					}
					else if (thisC instanceof Text)
					{
						Text txt = (Text) thisC;
						if (txt.getText().length() == 0)
						{
							updateStatus("Please enter a value for:" + txt.getData());
							return;
						}
					}
					else
					{
						// prob just boolean, ignore.
					}
				}
			}
		}

		// cool, everything must be ok.
		updateStatus(null);

	}

	final protected void updateStatus(String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}