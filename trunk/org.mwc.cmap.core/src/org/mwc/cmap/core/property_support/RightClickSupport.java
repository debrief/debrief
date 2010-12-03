package org.mwc.cmap.core.property_support;

import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.dnd.Clipboard;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor;
import org.mwc.cmap.core.operations.RightClickPasteAdaptor;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Editable.EditorType;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class RightClickSupport
{

	private static final String MULTIPLE_ITEMS_STR = "Multiple items";
	/**
	 * list of actions to be added to context-menu on right-click
	 */
	private static Vector<RightClickContextItemGenerator> _additionalRightClickItems = null;

	/**
	 * add a right-click generator item to the list we manage
	 * 
	 * @param generator
	 *          the generator to add...
	 */
	public static void addRightClickGenerator(
			RightClickContextItemGenerator generator)
	{
		if (_additionalRightClickItems == null)
			_additionalRightClickItems = new Vector<RightClickContextItemGenerator>(
					1, 1);

		_additionalRightClickItems.add(generator);
	}

	/**
	 * @param manager
	 *          where we add our items to
	 * @param editables
	 *          the selected items
	 * @param topLevelLayers
	 *          the top-level layers that contain our elements (it's these that
	 *          get updated)
	 * @param parentLayers
	 *          the immediate parents of our items
	 * @param theLayers
	 *          the overall layers object
	 * @param hideClipboardOperations
	 */
	static public void getDropdownListFor(IMenuManager manager,
			Editable[] editables, Layer[] topLevelLayers, Layer[] parentLayers,
			final Layers theLayers, boolean hideClipboardOperations)
	{

		// sort out the top level layer, if we have one
		Layer theTopLayer = null;
		if (topLevelLayers != null)
			if (topLevelLayers.length > 0)
				theTopLayer = topLevelLayers[0];

		// and now the edit-able bits
		if (editables.length > 0)
		{
			// first the parameters
			MenuManager subMenu = null;
			PropertyDescriptor[] commonProps = getCommonPropertiesFor(editables);
			for (int i = 0; i < commonProps.length; i++)
			{
				PropertyDescriptor thisP = commonProps[i];

				// start off with the booleans
				if (supportsBooleanEditor(thisP))
				{
					// generate boolean editors in the sub-menu
					subMenu = generateBooleanEditorFor(manager, subMenu, thisP,
							editables, theLayers, theTopLayer);
				}
				else
				{
					// now the drop-down lists
					if (supportsListEditor(thisP))
					{
						// generate boolean editors in the sub-menu
						subMenu = generateListEditorFor(manager, subMenu, thisP, editables,
								theLayers, theTopLayer);
					}
				}

			}

			// hmm, have a go at methods for this item
			// ok, try the methods
			MethodDescriptor[] meths = getCommonMethodsFor(editables);
			if (meths != null)
			{
				for (int i = 0; i < meths.length; i++)
				{
					final Layer myTopLayer = theTopLayer;

					final MethodDescriptor thisMethD = meths[i];

					// create button for this method
					Action doThisAction = new SubjectMethod(thisMethD.getDisplayName(),
							editables, thisMethD.getMethod(), myTopLayer, theLayers);

					// ok - add to the list.
					manager.add(doThisAction);
				}
			}

			// hmm, now do the same for the undoable methods
			MWC.GUI.Tools.SubjectAction[] actions = getUndoableActionsFor(editables);
			if (actions != null)
			{
				for (int i = 0; i < actions.length; i++)
				{
					final MWC.GUI.Tools.SubjectAction thisMethD = actions[i];

					// create button for this method
					IAction doThisAction = generateUndoableActionFor(thisMethD,
							editables, theLayers, theTopLayer);

					// ok - add to the list.
					manager.add(doThisAction);
				}
			}

		}

		Clipboard theClipboard = CorePlugin.getDefault().getClipboard();

		// see if we're still looking at the parent element (we only show
		// clipboard
		// operations for item clicked on)
		if (!hideClipboardOperations)
		{
			// hey, also see if we're going to do a cut/paste
			RightClickCutCopyAdaptor.getDropdownListFor(manager, editables,
					topLevelLayers, parentLayers, theLayers, theClipboard);

			// what about paste?
			Editable selectedItem = null;
			if (editables.length == 1)
			{
				selectedItem = editables[0];
			}
			RightClickPasteAdaptor.getDropdownListFor(manager, selectedItem,
					topLevelLayers, parentLayers, theLayers, theClipboard);

			manager.add(new Separator());
		}

		// hmm, do we have any right-click generators?
		if (_additionalRightClickItems != null)
		{
			for (Iterator<RightClickContextItemGenerator> thisItem = _additionalRightClickItems
					.iterator(); thisItem.hasNext();)
			{
				RightClickContextItemGenerator thisGen = (RightClickContextItemGenerator) thisItem
						.next();

				try
				{
					thisGen.generate(manager, theLayers, topLevelLayers, editables);
				}
				catch (Exception e)
				{
					// and log the error
					CorePlugin.logError(Status.ERROR,
							"failed whilst creating context menu", e);
				}
			}
		}
	}

	/** have a look at the supplied editors, find which properties are common */
	protected static MethodDescriptor[] getCommonMethodsFor(Editable[] editables)
	{
		MethodDescriptor[] res = null;
		MethodDescriptor[] demo = new MethodDescriptor[]
		{};

		// right, get the first set of properties
		if (editables.length > 0)
		{
			Editable first = editables[0];
			res = first.getInfo().getMethodDescriptors();

			// only continue if there are any methods to compare against
			if (res != null)
			{
				// right, are there any more?
				if (editables.length > 1)
				{
					// pass through the others, finding the common ground
					for (int cnt = 1; cnt < editables.length; cnt++)
					{
						Editable thisE = editables[cnt];

						// get its props
						EditorType thisEditor = thisE.getInfo();

						// do we have an editor?
						if (thisEditor != null)
						{
							MethodDescriptor[] newSet = thisEditor.getMethodDescriptors();

							// find the common ones
							res = (MethodDescriptor[]) getIntersectionFor(res, newSet, demo);
						}
					}
				}
			}
		}

		return res;
	}

	/** have a look at the supplied editors, find which properties are common */
	protected static MWC.GUI.Tools.SubjectAction[] getUndoableActionsFor(
			Editable[] editables)
	{
		MWC.GUI.Tools.SubjectAction[] res = null;
		MWC.GUI.Tools.SubjectAction[] demo = new MWC.GUI.Tools.SubjectAction[]
		{};

		// right, get the first set of properties
		if (editables.length > 0)
		{
			Editable first = editables[0];
			res = first.getInfo().getUndoableActions();

			// only continue if there are any methods to compare against
			if (res != null)
			{
				// right, are there any more?
				if (editables.length > 1)
				{
					// pass through the others, finding the common ground
					for (int cnt = 1; cnt < editables.length; cnt++)
					{
						Editable thisE = editables[cnt];

						// get its props
						EditorType thisEditor = thisE.getInfo();

						// do we have an editor?
						if (thisEditor != null)
						{
							MWC.GUI.Tools.SubjectAction[] newSet = thisEditor
									.getUndoableActions();

							// find the common ones
							res = (MWC.GUI.Tools.SubjectAction[]) getIntersectionFor(res,
									newSet, demo);
						}
					}
				}
			}
		}
		return res;
	}

	private static MWC.GUI.Tools.SubjectAction[] getIntersectionFor(
			MWC.GUI.Tools.SubjectAction[] a, MWC.GUI.Tools.SubjectAction[] b,
			MWC.GUI.Tools.SubjectAction[] demo)
	{
		Vector<MWC.GUI.Tools.SubjectAction> res = new Vector<MWC.GUI.Tools.SubjectAction>();

		for (int cnta = 0; cnta < a.length; cnta++)
		{
			MWC.GUI.Tools.SubjectAction thisP = a[cnta];
			for (int cntb = 0; cntb < b.length; cntb++)
			{
				MWC.GUI.Tools.SubjectAction thatP = b[cntb];
				if (thisP.toString().equals(thatP.toString()))
				{
					res.add(thisP);
				}
			}
		}
		return res.toArray(demo);
	}

	/** have a look at the supplied editors, find which properties are common */
	protected static PropertyDescriptor[] getCommonPropertiesFor(
			Editable[] editables)
	{
		PropertyDescriptor[] res = null;
		PropertyDescriptor[] demo = new PropertyDescriptor[]
		{};
		// right, get the first set of properties
		if (editables.length > 0)
		{
			Editable first = editables[0];
			res = first.getInfo().getPropertyDescriptors();

			// only continue if there are any property descriptors
			if (res != null)
			{
				// right, are there any more?
				if (editables.length > 1)
				{
					// pass through the others, finding the common ground
					for (int cnt = 1; cnt < editables.length; cnt++)
					{
						Editable thisE = editables[cnt];

						// get its props
						EditorType thisEditor = thisE.getInfo();

						// do we have an editor?
						if (thisEditor != null)
						{
							PropertyDescriptor[] newSet = thisEditor.getPropertyDescriptors();

							// find the common ones
							res = (PropertyDescriptor[]) getIntersectionFor(res, newSet, demo);
						}
					}
				}
			}
		}

		return res;
	}

	/**
	 * have a look at the two arrays, and find the common elements (brute force)
	 * 
	 * @param a
	 *          first array
	 * @param b
	 *          second array
	 * @return the common elements
	 */
	protected static MethodDescriptor[] getIntersectionFor(MethodDescriptor[] a,
			MethodDescriptor[] b, MethodDescriptor[] demo)
	{
		Vector<MethodDescriptor> res = new Vector<MethodDescriptor>();

		for (int cnta = 0; cnta < a.length; cnta++)
		{
			MethodDescriptor thisP = a[cnta];
			if (b != null)
			{
				for (int cntb = 0; cntb < b.length; cntb++)
				{
					MethodDescriptor thatP = b[cntb];
					if (thisP.getDisplayName().equals(thatP.getDisplayName()))
					{
						res.add(thisP);
					}
				}
			}
		}
		return res.toArray(demo);
	}

	/**
	 * have a look at the two arrays, and find the common elements (brute force)
	 * 
	 * @param a
	 *          first array
	 * @param b
	 *          second array
	 * @return the common elements
	 */
	protected static PropertyDescriptor[] getIntersectionFor(
			PropertyDescriptor[] a, PropertyDescriptor[] b, PropertyDescriptor[] demo)
	{
		Vector<PropertyDescriptor> res = new Vector<PropertyDescriptor>();

		for (int cnta = 0; cnta < a.length; cnta++)
		{
			PropertyDescriptor thisP = a[cnta];
			for (int cntb = 0; cntb < b.length; cntb++)
			{
				PropertyDescriptor thatP = b[cntb];
				if (thisP.equals(thatP))
				{
					res.add(thisP);
				}
			}
		}
		return res.toArray(demo);
	}

	/**
	 * embedded class that encapsulates the information we need to fire an action.
	 * It was really only refactored to aid debugging.
	 * 
	 * @author ian.mayo
	 */
	private static class SubjectMethod extends Action
	{
		private Editable[] _subjects;

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
		public SubjectMethod(String title, Editable[] subject, Method method,
				Layer topLayer, Layers theLayers)
		{
			super(title);
			_subjects = subject;
			_method = method;
			_topLayer = topLayer;
			_theLayers = theLayers;
		}

		public void run()
		{
			for (int cnt = 0; cnt < _subjects.length; cnt++)
			{
				Editable thisSubject = _subjects[cnt];
				try
				{
					_method.invoke(thisSubject, new Object[0]);

				}
				catch (IllegalArgumentException e)
				{
					CorePlugin.logError(Status.ERROR,
							"whilst firing method from right-click", e);
				}
				catch (IllegalAccessException e)
				{
					CorePlugin.logError(Status.ERROR,
							"whilst firing method from right-click", e);
				}
				catch (InvocationTargetException e)
				{
					CorePlugin.logError(Status.ERROR,
							"whilst firing method from right-click", e);
				}
			}

			// hmm, the method may have actually changed the data, we need to
			// find out if it
			// needs an extend
			if (_method.isAnnotationPresent(FireExtended.class))
			{
				_theLayers.fireExtended(null, _topLayer);
			}
			else if (_method.isAnnotationPresent(FireReformatted.class))
			{
				_theLayers.fireReformatted(_topLayer);
			}
			else
			{
				// hey, let's do a redraw aswell...
				_theLayers.fireModified(_topLayer);
			}

		}
	}

	/**
	 * can we edit this property with a tick-box?
	 * 
	 * @param thisP
	 * @return yes/no
	 */
	@SuppressWarnings("rawtypes")
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
	@SuppressWarnings("rawtypes")
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

	static private MenuManager generateBooleanEditorFor(
			final IMenuManager manager, MenuManager subMenu,
			final PropertyDescriptor thisP, final Editable[] editables,
			final Layers theLayers, final Layer topLevelLayer)
	{

		boolean currentVal = false;
		final Method getter = thisP.getReadMethod();
		final Method setter = thisP.getWriteMethod();
		try
		{
			final Boolean valNow = (Boolean) getter.invoke(editables[0],
					(Object[]) null);
			currentVal = valNow.booleanValue();
		}
		catch (Exception e)
		{
			CorePlugin.logError(Status.ERROR, "Failed to retrieve old value for:"
					+ editables[0].getName(), e);
		}

		IAction changeThis = new Action(thisP.getDisplayName(),
				IAction.AS_CHECK_BOX)
		{
			public void run()
			{
				try
				{
					ListPropertyAction la = new ListPropertyAction(
							thisP.getDisplayName(), editables, getter, setter, new Boolean(
									isChecked()), theLayers, topLevelLayer);

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
			String nameStr;
			if (editables.length > 1)
				nameStr = MULTIPLE_ITEMS_STR;
			else
				nameStr = editables[0].getName();

			subMenu = new MenuManager(nameStr);
			manager.add(subMenu);
		}

		subMenu.add(changeThis);

		return subMenu;
	}

	static private IAction generateUndoableActionFor(
			final MWC.GUI.Tools.SubjectAction theAction, final Editable[] editables,
			final Layers theLayers, final Layer topLevelLayer)
	{

		IAction changeThis = new Action(theAction.toString(),
				IAction.AS_PUSH_BUTTON)
		{
			public void run()
			{
				try
				{
					AbstractOperation la = new UndoableAction(theAction.toString(),
							editables, theAction, theLayers, topLevelLayer);

					CorePlugin.run(la);
				}
				catch (Exception e)
				{
					CorePlugin.logError(IStatus.INFO,
							"While executing undoable operations for for:"
									+ theAction.toString(), e);
				}
			}
		};
		return changeThis;
	}

	@SuppressWarnings("rawtypes")
	static private MenuManager generateListEditorFor(IMenuManager manager,
			MenuManager subMenu, final PropertyDescriptor thisP,
			final Editable[] editables, final Layers theLayers,
			final Layer topLevelLayer)
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
				val = getter.invoke(editables[0], (Object[]) null);
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
				final IAction thisA = new Action(thisTag, IAction.AS_RADIO_BUTTON)
				{
					public void run()
					{
						try
						{
							// hey, since this is a radio button, we get two events when the
							// selection changes - one for the value being unset, and the
							// other
							// for the value being set. So just fire for the new value (the
							// one that's checked)
							if (isChecked())
							{
								Method setter = thisP.getWriteMethod();

								// ok, place the change in the action
								ListPropertyAction la = new ListPropertyAction(thisP
										.getDisplayName(), editables, getter, setter, thisValue,
										theLayers, topLevelLayer);

								// and add it to the history
								CorePlugin.run(la);
							}
						}
						catch (Exception e)
						{
							CorePlugin.logError(IStatus.INFO,
									"While executing select editor for:" + thisP, e);
						}
					}

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
				String nameStr;
				if (editables.length > 1)
					nameStr = MULTIPLE_ITEMS_STR;
				else
					nameStr = editables[0].getName();

				subMenu = new MenuManager(nameStr);
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
		public void generate(IMenuManager parent, Layers theLayers,
				Layer[] parentLayers, Editable[] subjects);
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

		private final Editable[] _subjects;

		private final Object _newValue;

		public ListPropertyAction(final String propertyName,
				final Editable[] editable, final Method getter, final Method setter,
				final Object newValue, final Layers layers, final Layer parentLayer)
		{
			super(propertyName + " for multiple items");

			_setter = setter;
			_layers = layers;
			_parentLayer = parentLayer;
			_subjects = editable;
			_newValue = newValue;

			try
			{
				_oldValue = getter.invoke(editable[0], (Object[]) null);
			}
			catch (Exception e)
			{
				CorePlugin.logError(Status.ERROR, "Failed to retrieve old value for:"
						+ "Multiple items starting with:" + _subjects[0].getName(), e);
			}

			// put in the global context, for some reason
			super.addContext(CorePlugin.CMAP_CONTEXT);
		}

		private IStatus doIt(Object theValue)
		{
			IStatus res = Status.OK_STATUS;
			for (int cnt = 0; cnt < _subjects.length; cnt++)
			{
				Editable thisSubject = _subjects[cnt];
				try
				{
					_setter.invoke(thisSubject, new Object[]
					{ theValue });
				}
				catch (InvocationTargetException e)
				{
					CorePlugin.logError(Status.ERROR, "Setter call failed:"
							+ thisSubject.getName() + " Error was:"
							+ e.getTargetException().getMessage(), e.getTargetException());
					res = null;
				}
				catch (IllegalArgumentException e)
				{
					CorePlugin.logError(Status.ERROR, "Wrong parameters pass to:"
							+ thisSubject.getName(), e);
					res = null;
				}
				catch (IllegalAccessException e)
				{
					CorePlugin.logError(Status.ERROR, "Illegal access problem for:"
							+ thisSubject.getName(), e);
					res = null;
				}
			}

			// and tell everybody (we only need to do this if the previous call
			// works,
			// if an exception is thrown we needn't worry about the update
			fireUpdate();

			return res;

		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			return doIt(_newValue);
		}

		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			return doIt(_newValue);
		}

		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			return doIt(_oldValue);
		}

		private void fireUpdate()
		{
			// hmm, the method may have actually changed the data, we need to
			// find out if it
			// needs an extend
			if (_setter.isAnnotationPresent(FireExtended.class))
			{
				_layers.fireExtended(null, _parentLayer);
			}
			else if (_setter.isAnnotationPresent(FireReformatted.class))
			{
				_layers.fireReformatted(_parentLayer);
			}
			else
			{
				// hey, let's do a redraw aswell...
				_layers.fireModified(_parentLayer);
			}
		}

	}

	/**
	 * embedded class to store a property change in an action
	 * 
	 * @author ian.mayo
	 */
	public static class UndoableAction extends AbstractOperation
	{
		private final SubjectAction _action;

		private final Layers _layers;

		private final Layer _parentLayer;

		private final Editable[] _subjects;

		public UndoableAction(final String propertyName, final Editable[] editable,
				final SubjectAction action, final Layers layers, final Layer parentLayer)
		{
			super(propertyName + " for multiple items");
			_layers = layers;
			_action = action;
			_parentLayer = parentLayer;
			_subjects = editable;
			// put in the global context, for some reason
			super.addContext(CorePlugin.CMAP_CONTEXT);
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			IStatus res = Status.OK_STATUS;
			for (int cnt = 0; cnt < _subjects.length; cnt++)
			{
				Editable thisSubject = _subjects[cnt];
				try
				{
					_action.execute(thisSubject);

				}
				catch (IllegalArgumentException e)
				{
					CorePlugin.logError(Status.ERROR, "Wrong parameters pass to:"
							+ thisSubject.getName(), e);
					res = null;
				}
			}

			// and tell everybody
			fireUpdate();
			return res;
		}

		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			IStatus res = Status.OK_STATUS;
			for (int cnt = 0; cnt < _subjects.length; cnt++)
			{
				Editable thisSubject = _subjects[cnt];
				try
				{
					_action.execute(thisSubject);
				}
				catch (Exception e)
				{
					CorePlugin.logError(Status.ERROR, "Failed to set new value for:"
							+ thisSubject.getName(), e);
					res = null;
				}
			}

			// and tell everybody
			fireUpdate();

			return res;
		}

		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			IStatus res = Status.OK_STATUS;
			for (int cnt = 0; cnt < _subjects.length; cnt++)
			{
				Editable thisSubject = _subjects[cnt];
				try
				{
					_action.undo(thisSubject);
				}
				catch (Exception e)
				{
					CorePlugin.logError(Status.ERROR, "Failed to set new value for:"
							+ thisSubject.getName(), e);
					res = null;
				}
			}
			// and tell everybody
			fireUpdate();

			return res;
		}

		private void fireUpdate()
		{
			_layers.fireExtended(null, _parentLayer);
		}

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
		}

		public final void testIntersection()
		{
			try
			{
				PropertyDescriptor[] demo = new PropertyDescriptor[]
				{};
				PropertyDescriptor[] pa = new PropertyDescriptor[]
				{ new PropertyDescriptor("Color", FixWrapper.class),
						new PropertyDescriptor("Font", FixWrapper.class),
						new PropertyDescriptor("Label", FixWrapper.class),
						new PropertyDescriptor("LabelShowing", FixWrapper.class),
						new PropertyDescriptor("Visible", FixWrapper.class) };
				PropertyDescriptor[] pb = new PropertyDescriptor[]
				{ new PropertyDescriptor("Color", FixWrapper.class),
						new PropertyDescriptor("Font", FixWrapper.class),
						new PropertyDescriptor("Label", FixWrapper.class),
						new PropertyDescriptor("LabelShowing", FixWrapper.class),
						new PropertyDescriptor("SymbolShowing", FixWrapper.class), };
				PropertyDescriptor[] pc = new PropertyDescriptor[]
				{ new PropertyDescriptor("LabelShowing", FixWrapper.class),
						new PropertyDescriptor("SymbolShowing", FixWrapper.class), };
				PropertyDescriptor[] pd = new PropertyDescriptor[]
				{};

				PropertyDescriptor[] res = (PropertyDescriptor[]) getIntersectionFor(
						pa, pb, demo);
				assertNotNull("failed to find intersection", res);
				assertEquals("Failed to find correct num", 4, res.length);
				res = (PropertyDescriptor[]) getIntersectionFor(res, pc, demo);
				assertNotNull("failed to find intersection", res);
				assertEquals("Failed to find correct num", 1, res.length);
				res = (PropertyDescriptor[]) getIntersectionFor(pa, pd, demo);
				assertNotNull("failed to find intersection", res);
				assertEquals("Failed to find correct num", 0, res.length);
				res = (PropertyDescriptor[]) getIntersectionFor(pd, pa, demo);
				assertNotNull("failed to find intersection", res);
				assertEquals("Failed to find correct num", 0, res.length);
			}
			catch (IntrospectionException e)
			{
				CorePlugin.logError(Status.ERROR, "Whilst doing tests", e);
				assertTrue("threw some error", false);
			}
		}

		public final void testPropMgt()
		{
			Editable itemOne = new FixWrapper(new Fix(new HiResDate(122333),
					new WorldLocation(1, 2, 3), 12, 14));
			Editable itemTwo = new FixWrapper(new Fix(new HiResDate(122334),
					new WorldLocation(1, 2, 5), 13, 12));
			Editable itemThree = new SensorWrapper("alpha");
			Editable[] lst = new Editable[]
			{ itemOne, itemTwo };
			Editable[] lst2 = new Editable[]
			{ itemOne, itemThree };
			Editable[] lst3 = new Editable[]
			{ itemThree, itemOne, itemThree };
			Editable[] lst4 = new Editable[]
			{ itemThree, itemThree };
			Editable[] lst5 = new Editable[]
			{ itemOne };
			assertEquals("no data", 2, lst.length);
			PropertyDescriptor[] props = RightClickSupport
					.getCommonPropertiesFor(lst);
			assertNotNull("found some data", props);
			assertEquals("found right matches", 12, props.length);
			props = RightClickSupport.getCommonPropertiesFor(lst2);
			assertNotNull("found some data", props);
			assertEquals("found right matches", 1, props.length);
			props = RightClickSupport.getCommonPropertiesFor(lst3);
			assertNotNull("found some data", props);
			assertEquals("found right matches", 1, props.length);
			props = RightClickSupport.getCommonPropertiesFor(lst4);
			assertNotNull("found some data", props);
			assertEquals("found right matches", 7, props.length);
			props = RightClickSupport.getCommonPropertiesFor(lst5);
			assertNotNull("found some data", props);
			assertEquals("found right matches", 12, props.length);
		}
	}
}
