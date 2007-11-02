package org.mwc.cmap.core.property_support;

import java.beans.*;
import java.lang.reflect.*;
import java.util.*;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.swt.dnd.Clipboard;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.*;

import MWC.GUI.*;
import MWC.GUI.Editable.EditorType;

public class RightClickSupport
{

	/**
	 * list of actions to be added to context-menu on right-click
	 */
	private static Vector _additionalRightClickItems = null;

	/**
	 * add a right-click generator item to the list we manage
	 * 
	 * @param generator
	 *          the generator to add...
	 */
	public static void addRightClickGenerator(RightClickContextItemGenerator generator)
	{
		if (_additionalRightClickItems == null)
			_additionalRightClickItems = new Vector(1, 1);

		_additionalRightClickItems.add(generator);
	}

	/**
	 * @param manager
	 * @param hideClipboardOperations
	 * @param pw
	 */
	static public void getDropdownListFor(IMenuManager manager, Editable[] editables,
			Layer[] topLevelLayers, Layer[] parentLayers, final Layers theLayers,
			boolean hideClipboardOperations)
	{

		// sort out the top level layer, if we have one
		Layer theTopLayer = null;
		if (topLevelLayers != null)
			if (topLevelLayers.length > 0)
				theTopLayer = topLevelLayers[0];

		// and now the editable bits
		Editable p = null;
		if (editables.length > 0)
		{
			p = editables[0];
			EditorType editor = p.getInfo();
			MenuManager subMenu = null;

			// hmm does it have anything editable?
			if (editor != null)
			{

				// and now the parameters
				PropertyDescriptor[] pd = editor.getPropertyDescriptors();
				for (int i = 0; i < pd.length; i++)
				{
					PropertyDescriptor thisP = pd[i];

					// start off with the booleans
					if (supportsBooleanEditor(thisP))
					{
						// generate boolean editors in the sub-menu
						subMenu = generateBooleanEditorFor(manager, subMenu, thisP, p, theLayers,
								theTopLayer);
					}
					else
					{
						// now the drop-down lists
						if (supportsListEditor(thisP))
						{
							// generate boolean editors in the sub-menu
							subMenu = generateListEditorFor(manager, subMenu, thisP, p, theLayers,
									theTopLayer);
						}
					}

				}

				// hmm, have a go at methods for this item
				// ok, try the methods
				MethodDescriptor[] meths = editor.getMethodDescriptors();
				if (meths != null)
				{
					for (int i = 0; i < meths.length; i++)
					{
						final Layer myTopLayer = theTopLayer;

						final MethodDescriptor thisMethD = meths[i];

						// create button for this method
						Action doThisAction = new SubjectAction(thisMethD.getDisplayName(), p,
								thisMethD.getMethod(), myTopLayer, theLayers);

						// ok - add to the list.
						manager.add(doThisAction);
					}
				}
			}
		}

		Clipboard theClipboard = CorePlugin.getDefault().getClipboard();

		// see if we're still looking at the parent element (we only show clipboard
		// operations for item clicked on)
		if (!hideClipboardOperations)
		{
			// hey, also see if we're going to do a cut/paste
			RightClickCutCopyAdaptor.getDropdownListFor(manager, editables, topLevelLayers,
					topLevelLayers, theLayers, theClipboard);

			// what about paste?
			Editable selectedItem = null;
			if (editables.length == 1)
			{
				selectedItem = editables[0];
			}
			RightClickPasteAdaptor.getDropdownListFor(manager, selectedItem, topLevelLayers,
					topLevelLayers, theLayers, theClipboard);

			manager.add(new Separator());
		}

		// hmm, do we have any right-click generators?
		if (_additionalRightClickItems != null)
		{
			for (Iterator thisItem = _additionalRightClickItems.iterator(); thisItem.hasNext();)
			{
				RightClickContextItemGenerator thisGen = (RightClickContextItemGenerator) thisItem
						.next();
				thisGen.generate(manager, theLayers, topLevelLayers, editables);
			}
		}
	}

	/**
	 * embedded class that encapsulates the information we need to fire an action.
	 * It was really only refactored to aid debugging.
	 * 
	 * @author ian.mayo
	 */
	private static class SubjectAction extends Action
	{
		private Object _subject;

		private Method _method;

		private Layer _topLayer;

		private Layers _theLayers;

		/**
		 * @param title
		 *          what to call the action
		 * @param subject
		 *          the thing we're operating upon
		 * @param method
		 *          what we're going to run
		 * @param topLayer
		 *          the layer to update after the action is complete
		 * @param theLayers
		 *          the host for the target layer
		 */
		public SubjectAction(String title, Object subject, Method method, Layer topLayer,
				Layers theLayers)
		{
			super(title);
			_subject = subject;
			_method = method;
			_topLayer = topLayer;
			_theLayers = theLayers;
		}

		public void run()
		{
			try
			{
				_method.invoke(_subject, new Object[0]);

				// hey, let's do a redraw aswell...
				_theLayers.fireModified(_topLayer);
			}
			catch (IllegalArgumentException e)
			{
				CorePlugin.logError(Status.ERROR, "whilst firing method from right-click", e);
			}
			catch (IllegalAccessException e)
			{
				CorePlugin.logError(Status.ERROR, "whilst firing method from right-click", e);
			}
			catch (InvocationTargetException e)
			{
				CorePlugin.logError(Status.ERROR, "whilst firing method from right-click", e);
			}
		}
	}

	/**
	 * can we edit this property with a tick-box?
	 * 
	 * @param thisP
	 * @return yes/no
	 */
	static private boolean supportsBooleanEditor(PropertyDescriptor thisP)
	{
		final boolean res;

		// get the prop type
		Class thisType = thisP.getPropertyType();
		Class boolClass = Boolean.class;

		// is it boolean?
		if ((thisType == boolClass) || (thisType.equals(boolean.class)))
		{
			res = true;
		}
		else
		{
			res = false;
		}

		return res;
	}

	/**
	 * can we edit this property with a drop-down list?
	 * 
	 * @param thisP
	 * @return yes/no
	 */
	static private boolean supportsListEditor(PropertyDescriptor thisP)
	{
		boolean res = false;

		// find out the type of the editor
		Method m = thisP.getReadMethod();
		Class cl = m.getReturnType();

		// is there a custom editor for this type?
		Class c = thisP.getPropertyEditorClass();

		PropertyEditor pe = null;
		// try to create an editor for this class
		try
		{
			if (c != null)
				pe = (PropertyEditor) c.newInstance();
		}
		catch (Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

		// did it work?
		if (pe == null)
		{
			// try to find an editor for this through our manager
			pe = PropertyEditorManager.findEditor(cl);
		}

		// have we managed to create an editor?
		if (pe != null)
		{
			// retrieve the tags
			String[] tags = pe.getTags();

			// are there any tags for this class?
			if (tags != null)
			{
				res = true;
			}
		}

		return res;
	}

	static private MenuManager generateBooleanEditorFor(final IMenuManager manager,
			MenuManager subMenu, final PropertyDescriptor thisP, final Editable p,
			final Layers theLayers, final Layer topLevelLayer)
	{

		boolean currentVal = false;
		final Method getter = thisP.getReadMethod();
		final Method setter = thisP.getWriteMethod();
		try
		{
			final Boolean valNow = (Boolean) getter.invoke(p, null);
			currentVal = valNow.booleanValue();
		}
		catch (Exception e)
		{
			CorePlugin.logError(Status.ERROR,
					"Failed to retrieve old value for:" + p.getName(), e);
		}

		IAction changeThis = new Action(thisP.getDisplayName(), IAction.AS_CHECK_BOX)
		{
			public void run()
			{
				try
				{
					ListPropertyAction la = new ListPropertyAction(thisP.getDisplayName(), p,
							getter, setter, new Boolean(isChecked()), theLayers, topLevelLayer);

					CorePlugin.run(la);
				}
				catch (Exception e)
				{
					CorePlugin.logError(IStatus.INFO,
							"While executing boolean editor for:" + thisP, e);
				}
			}
		};
		changeThis.setChecked(currentVal);
		changeThis.setToolTipText(thisP.getShortDescription());

		// is our sub-menu already created?
		if (subMenu == null)
		{
			subMenu = new MenuManager(p.getName());
			manager.add(subMenu);
		}

		subMenu.add(changeThis);

		return subMenu;
	}

	static private MenuManager generateListEditorFor(IMenuManager manager,
			MenuManager subMenu, final PropertyDescriptor thisP, final Editable p,
			final Layers theLayers, final Layer topLevelLayer)
	{

		// find out the type of the editor
		Method m = thisP.getReadMethod();
		Class cl = m.getReturnType();

		// is there a custom editor for this type?
		Class c = thisP.getPropertyEditorClass();

		PropertyEditor pe = null;
		// try to create an editor for this class
		try
		{
			if (c != null)
				pe = (PropertyEditor) c.newInstance();
		}
		catch (Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

		// did it work?
		if (pe == null)
		{
			// try to find an editor for this through our manager
			pe = PropertyEditorManager.findEditor(cl);
		}

		// retrieve the tags
		String[] tags = pe.getTags();

		// are there any tags for this class?
		if (tags != null)
		{
			// create a drop-down list
			MenuManager thisChoice = new MenuManager(thisP.getDisplayName());

			// sort out the setter details
			final Method getter = thisP.getReadMethod();

			// get the current value
			Object val = null;
			try
			{
				val = getter.invoke(p, null);
			}
			catch (Exception e)
			{
				MWC.Utilities.Errors.Trace.trace(e);
			}
			pe.setValue(val);

			// convert the current value to text
			String currentValue = pe.getAsText();

			// and now a drop-down item for each options
			for (int j = 0; j < tags.length; j++)
			{
				final String thisTag = tags[j];
				pe.setAsText(thisTag);
				final Object thisValue = pe.getValue();

				// create the item
				IAction thisA = new Action(thisTag, IAction.AS_RADIO_BUTTON)
				{
					public void run()
					{
						try
						{
							Method setter = thisP.getWriteMethod();

							// ok, place the change in the action
							ListPropertyAction la = new ListPropertyAction(thisP.getDisplayName(), p,
									getter, setter, thisValue, theLayers, topLevelLayer);

							// and add it to the history
							CorePlugin.run(la);
						}
						catch (Exception e)
						{
							CorePlugin.logError(IStatus.INFO, "While executing select editor for:"
									+ thisP, e);
						}
					};

				};

				// is this the current one?
				if (thisTag.equals(currentValue))
				{
					thisA.setChecked(true);
				}

				// add it to the menu
				thisChoice.add(thisA);

			}

			// is our sub-menu already created?
			if (subMenu == null)
			{
				subMenu = new MenuManager(p.getName());
				manager.add(subMenu);
			}
			
			subMenu.add(thisChoice);

		}

		return subMenu;
	}

	/**
	 * template provide by support units that want to add items to the right-click
	 * menu when something is selected
	 * 
	 * @author ian.mayo
	 */
	public static interface RightClickContextItemGenerator
	{
		public void generate(IMenuManager parent, Layers theLayers, Layer[] parentLayers,
				Editable[] subjects);
	}

	/**
	 * embedded class to store a property change in an action
	 * 
	 * @author ian.mayo
	 */
	private static class ListPropertyAction extends AbstractOperation
	{
		private Object _oldValue;

		private final Method _setter;

		private final Layers _layers;

		private final Layer _parentLayer;

		private final Editable _subject;

		private final Object _newValue;

		public ListPropertyAction(final String propertyName, final Editable subject,
				final Method getter, final Method setter, final Object newValue,
				final Layers layers, final Layer parentLayer)
		{
			super(propertyName + " for " + subject.getName());

			_setter = setter;
			_layers = layers;
			_parentLayer = parentLayer;
			_subject = subject;
			_newValue = newValue;

			try
			{
				_oldValue = getter.invoke(subject, null);
			}
			catch (Exception e)
			{
				CorePlugin.logError(Status.ERROR, "Failed to retrieve old value for:"
						+ _subject.getName(), e);
			}

			// put in the global context, for some reason
			super.addContext(CorePlugin.CMAP_CONTEXT);
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			IStatus res = Status.OK_STATUS;
			try
			{
				_setter.invoke(_subject, new Object[] { _newValue });
			}
			catch (InvocationTargetException e)
			{
				CorePlugin
						.logError(Status.ERROR, "Setter call failed:" + _subject.getName()
								+ " Error was:" + e.getTargetException().getMessage(), e
								.getTargetException());
				res = null;
			}
			catch (IllegalArgumentException e)
			{
				CorePlugin.logError(Status.ERROR, "Wrong parameters pass to:"
						+ _subject.getName(), e);
				res = null;
			}
			catch (IllegalAccessException e)
			{
				CorePlugin.logError(Status.ERROR, "Illegal access problem for:"
						+ _subject.getName(), e);
				res = null;
			}

			// and tell everybody
			fireUpdate();
			return res;
		}

		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			IStatus res = Status.OK_STATUS;
			try
			{
				_setter.invoke(_subject, new Object[] { _newValue });
			}
			catch (Exception e)
			{
				CorePlugin.logError(Status.ERROR, "Failed to set new value for:"
						+ _subject.getName(), e);
				res = null;
			}

			// and tell everybody
			fireUpdate();

			return res;
		}

		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			IStatus res = Status.OK_STATUS;
			try
			{
				_setter.invoke(_subject, new Object[] { _oldValue });
			}
			catch (Exception e)
			{
				CorePlugin.logError(Status.ERROR, "Failed to set new value for:"
						+ _subject.getName(), e);
				res = null;
			}

			// and tell everybody
			fireUpdate();

			return res;
		}

		private void fireUpdate()
		{
			_layers.fireModified(_parentLayer);
		}

	}
}
