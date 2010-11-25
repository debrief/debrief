package org.mwc.cmap.core.wizards;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.DebriefProperty;
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsControl;

import MWC.GUI.Editable;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

abstract public class CoreEditableWizardPage extends WizardPage
{
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
		catch (final IntrospectionException e)
		{
			CorePlugin.logError(Status.ERROR, "Problem creating descriptor for:"
					+ name, e);
		}
		return res;
	}

	private final ISelection selection;

	// the controls containing the user data

	protected Editable _editable;

	private java.util.List<Control> _myEditors;

	/**
	 * checkbox for whether to include a scale
	 */
	private Button _enabledBtn;

	private final boolean _optional;

	private ModifyListener _txtModifiedListener;

	private String _helpContext;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 * @param helpContext
	 */
	public CoreEditableWizardPage(ISelection selection, String pageName,
			String title, String description, String helpContext)
	{
		this(selection, pageName, title, description, null, helpContext);
	}

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 * @param helpContext
	 */
	public CoreEditableWizardPage(ISelection selection, String pageName,
			String title, String description, String imageName, String helpContext)
	{
		this(selection, pageName, title, description, imageName, helpContext, true);
	}

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 * @param helpContext
	 */
	public CoreEditableWizardPage(ISelection selection, String pageName,
			String title, String description, String imageName, String helpContext,
			boolean optional)
	{
		super(pageName);
		_optional = optional;
		if (helpContext != null)
		{
			_helpContext = helpContext;
		}
		setTitle(title);
		setDescription(description);
		this.selection = selection;

		// ok, now try to set the image
		if (imageName != null)
		{
			final ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(
					"org.mwc.debrief.core", imageName);
			if (id != null)
				super.setImageDescriptor(id);
			else
				CorePlugin.logError(IStatus.WARNING, "Wizard image file not found for:"
						+ imageName, null);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void performHelp()
	{
				
		 if (_helpContext != null)
		 {
		 Action help = CorePlugin.createOpenHelpAction(_helpContext, null,
		 PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		 .getViews()[0]);
		 help.run();
		 }
		 super.performHelp();
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	final public void createControl(Composite parent)
	{
		final Composite container = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
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
				@Override
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
		_editable = createMe();

		// ok, either generate by hand or automatically determine the editable bits
		// for this item
		final PropertyDescriptor[] descriptors = getPropertyDescriptors();

		// ok, we've got our list of property descriptors, stick them into a grid
		populateEditors(container, _editable, descriptors);

		initialize();
		dialogChanged();
		setControl(container);

		// finally, just clear the error message, we're not ready for it yet
		setErrorMessage(null);

		// put the focus in our first control
		if (_myEditors.size() > 0)
			_myEditors.get(0).setFocus();

	}

	/**
	 * allocate a text modified listners
	 * 
	 * @param listener
	 * 
	 */
	public void addModifiedListener(ModifyListener listener)
	{
		_txtModifiedListener = listener;
	}

	/**
	 * ok, create an instance of the thing we're editing
	 * 
	 * @return
	 */
	abstract protected Editable createMe();

	/**
	 * Ensures that all fields are set.
	 */
	final void dialogChanged()
	{
		boolean doCheck = false;
		if (!_optional)
			doCheck = true;
		else if (_enabledBtn.getSelection())
			doCheck = true;

		if (doCheck)
		{
			// right, controls are enabled, check we've got something in all of them
			for (final Iterator<Control> iterator = _myEditors.iterator(); iterator
					.hasNext();)
			{
				final Control thisC = iterator.next();
				if (thisC instanceof Combo)
				{
					final Combo combo = (Combo) thisC;
					final int sel = combo.getSelectionIndex();
					if (sel == -1)
					{
						updateStatus("Please select a value for:" + combo.getToolTipText());
						return;
					}
				}
				else if (thisC instanceof Text)
				{
					final Text txt = (Text) thisC;
					if (txt.getText().length() == 0)
					{
						updateStatus("Please enter a value for:" + txt.getToolTipText());
						return;
					}
				}
				else if (thisC instanceof ValueWithUnitsControl)
				{
					final ValueWithUnitsControl value = (ValueWithUnitsControl) thisC;
					if (value.getData() == null)
					{
						updateStatus("Please enter a value for:" + value.getToolTipText());
						return;
					}
				}
				else
				{
					// prob just boolean, ignore.
				}
			}
		}

		// cool, everything must be ok.
		updateStatus(null);

	}

	/**
	 * Ensures that all fields are set.
	 */
	final void enabledChanged()
	{
		// cycle through our controls, enabling/disabling them as necessary
		for (final Iterator<Control> iter = _myEditors.iterator(); iter.hasNext();)
		{
			final Control thisC = iter.next();
			thisC.setEnabled(_enabledBtn.getSelection());
		}
	}

	/**
	 * retrieve the edited item
	 * 
	 * @return
	 */
	final public Editable getEditable()
	{
		final Editable res;

		// hmm, does the user want one?
		if (!_optional)
		{
			// yes, return it.
			res = _editable;
		}
		else if (_enabledBtn.getSelection())
		{
			res = _editable;
		}
		else
		{
			// no, return null instead
			res = null;
		}

		return res;
	}

	/**
	 * @return
	 */
	abstract protected PropertyDescriptor[] getPropertyDescriptors();

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	final protected void initialize()
	{
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection)
		{
			final IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;

			// IN HERE WE CAN USE WHATEVER IS CURRENTLY SELECTED FOR BACKGROUND INFO
		}
	}

	/**
	 * @param container
	 *          the think to stick our controls into
	 * @param myItem
	 *          the object we're editing
	 * @param descriptors
	 *          the set of editable properties for this object
	 */
	final protected void populateEditors(Composite container, Editable myItem,
			PropertyDescriptor[] descriptors)
	{
		// build up the list of editors
		_myEditors = new Vector<Control>(0, 1);

		Label label;
		// right, walk through the properties
		for (int i = 0; i < descriptors.length; i++)
		{
			final PropertyDescriptor thisD = descriptors[i];

			// ok, determine the editor & helper classes for this property
			final DebriefProperty thisProp = new DebriefProperty(thisD, myItem, null);

			// ok, did we find a set of tags or a text-item?
			if (thisProp != null)
			{
				// cool, insert the items
				label = new Label(container, SWT.NONE);
				label.setText(thisProp.getDisplayName() + ":");

				// and now for the editor bit.
				final Control newEditor = thisProp.createEditor(container);

				if (newEditor != null)
				{
					// get the current value, so we can initialise the editor
					try
					{
						Object currentVal = thisD.getReadMethod().invoke(myItem);
						// ok, put the property name in the editor, so we can
						// easily get it when we're doing validation

						// do we need to convert the value to text?
						if (newEditor instanceof Text)
						{
							Text txtEditor = (Text) newEditor;
							txtEditor.setText(currentVal.toString());

							// do we have a modified listener?
							if (_txtModifiedListener != null)
								txtEditor.addModifyListener(_txtModifiedListener);

						}
						else
						{
							// see if we can initialise the checkbox - we only do it if it's
							// boolean
							if (currentVal instanceof Boolean)
							{
								Button btn = (Button) newEditor;
								Boolean bVal = (Boolean) currentVal;
								btn.setSelection(bVal.booleanValue());
							}
							newEditor.setData(currentVal);
						}

						// store the data type name in the tooltip (for when we do error
						// handling)
						newEditor.setToolTipText(thisProp.getDisplayName());

						newEditor.redraw();

					}
					catch (Exception e)
					{
						CorePlugin.logError(Status.ERROR,
								"Whilst reading existing value of object:" + myItem, e);
					}

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

	final protected void updateStatus(String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}