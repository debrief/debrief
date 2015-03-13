/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.core.property_support;

import java.awt.Color;
import java.beans.BeanInfo;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.dnd.Clipboard;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor;
import org.mwc.cmap.core.operations.RightClickPasteAdaptor;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Editable.EditorType;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class RightClickSupport
{

	/**
	 * fixed strings for the right click support extension
	 * 
	 */
	private static final String EXTENSION_POINT_ID = "RightClickSupport";

	// Plug-in ID from <plugin> tag in plugin.xml
	private static final String PLUGIN_ID = "org.mwc.cmap.core";

	private static final String MULTIPLE_ITEMS_STR = "Multiple items";
	/**
	 * list of actions to be added to context-menu on right-click
	 */
	private static Vector<RightClickContextItemGenerator> _additionalRightClickItems = null;

	/**
	 * whether we've checked for any one that extends teh right click support via
	 * plugin xml
	 * 
	 */
	private static boolean _rightClickExtensionsChecked = false;

	/**
	 * add a right-click generator item to the list we manage
	 * 
	 * @param generator
	 *          the generator to add...
	 */
	public static void addRightClickGenerator(
			final RightClickContextItemGenerator generator)
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
	static public void getDropdownListFor(final IMenuManager manager,
			final Editable[] editables, final Layer[] topLevelLayers,
			final Layer[] parentLayers, final Layers theLayers,
			final boolean hideClipboardOperations)
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
			final PropertyDescriptor[] commonProps = getCommonPropertiesFor(editables);
			if (commonProps != null)
			{
				for (int i = 0; i < commonProps.length; i++)
				{
					final PropertyDescriptor thisP = commonProps[i];

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
							subMenu = generateListEditorFor(manager, subMenu, thisP,
									editables, theLayers, theTopLayer);
						}
					}

				}
			}

			// special case: if only one item is selected, try adding any additional
			// methods
			if (editables.length == 1)
			{
				// any additional ones?
				Editable theE = editables[0];

				// ok, get the editor
				EditorType info = theE.getInfo();

				if (info != null)
				{
					BeanInfo[] additional = info.getAdditionalBeanInfo();

					// any there?
					if (additional != null)
					{
						// ok, loop through the beans
						for (int i = 0; i < additional.length; i++)
						{
							BeanInfo thisB = additional[i];
							if (thisB instanceof EditorType)
							{
								EditorType editor = (EditorType) thisB;
								Editable subject = (Editable) editor.getData();

								// and the properties
								PropertyDescriptor[] theseProps = thisB
										.getPropertyDescriptors();

								for (int j = 0; j < theseProps.length; j++)
								{
									
									PropertyDescriptor thisP = theseProps[j];
									
									// and wrap the object
									Editable[] holder = new Editable[]
									{ subject };
									if (supportsBooleanEditor(thisP))
									{

										// generate boolean editors in the sub-menu
										subMenu = generateBooleanEditorFor(manager, subMenu, thisP,
												holder, theLayers, theTopLayer);
									}					else
									{
										// now the drop-down lists
										if (supportsListEditor(thisP))
										{
											// generate boolean editors in the sub-menu
											subMenu = generateListEditorFor(manager, subMenu, thisP,
													holder, theLayers, theTopLayer);
										}
									}

								}
							}
						}
					}
				}
			}

			// hmm, have a go at methods for this item
			// ok, try the methods
			final MethodDescriptor[] meths = getCommonMethodsFor(editables);
			if (meths != null)
			{
				for (int i = 0; i < meths.length; i++)
				{
					final Layer myTopLayer = theTopLayer;

					final MethodDescriptor thisMethD = meths[i];

					if (thisMethD == null)
					{
						CorePlugin.logError(Status.ERROR,
								"Failed to create method, props may be wrongly named", null);
					}
					else
					{
						// create button for this method
						final Action doThisAction = new SubjectMethod(
								thisMethD.getDisplayName(), editables, thisMethD.getMethod(),
								myTopLayer, theLayers);

						// ok - add to the list.
						manager.add(doThisAction);
					}
				}
			}

			// hmm, now do the same for the undoable methods
			final MWC.GUI.Tools.SubjectAction[] actions = getUndoableActionsFor(editables);
			if (actions != null)
			{
				for (int i = 0; i < actions.length; i++)
				{
					final MWC.GUI.Tools.SubjectAction thisMethD = actions[i];

					// create button for this method
					final IAction doThisAction = generateUndoableActionFor(thisMethD,
							editables, theLayers, theTopLayer);

					// ok - add to the list.
					manager.add(doThisAction);
				}
			}

		}

		// see if we're still looking at the parent element (we only show
		// clipboard
		// operations for item clicked on)
		if (!hideClipboardOperations)
		{
			final Clipboard theClipboard = CorePlugin.getDefault().getClipboard();

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

		if (!_rightClickExtensionsChecked)
		{
			loadLoaderExtensions();

			// ok, done
			_rightClickExtensionsChecked = true;
		}

		// hmm, do we have any right-click generators?
		if (_additionalRightClickItems != null)
		{
			for (final Iterator<RightClickContextItemGenerator> thisItem = _additionalRightClickItems
					.iterator(); thisItem.hasNext();)
			{
				final RightClickContextItemGenerator thisGen = (RightClickContextItemGenerator) thisItem
						.next();

				try
				{
					thisGen.generate(manager, theLayers, topLevelLayers, editables);
				}
				catch (final Exception e)
				{
					// and log the error
					CorePlugin.logError(Status.ERROR,
							"failed whilst creating context menu", e);
				}
			}
		}
	}

	/**
	 * see if any extra right click handlers are defined
	 * 
	 */
	private static void loadLoaderExtensions()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		if (registry != null)
		{
			final IExtensionPoint point = registry.getExtensionPoint(PLUGIN_ID,
					EXTENSION_POINT_ID);

			final IExtension[] extensions = point.getExtensions();
			for (int i = 0; i < extensions.length; i++)
			{
				final IExtension iExtension = extensions[i];
				final IConfigurationElement[] confE = iExtension
						.getConfigurationElements();
				for (int j = 0; j < confE.length; j++)
				{
					final IConfigurationElement iConfigurationElement = confE[j];
					RightClickContextItemGenerator newInstance;
					try
					{
						newInstance = (RightClickContextItemGenerator) iConfigurationElement
								.createExecutableExtension("class");
						addRightClickGenerator(newInstance);
					}
					catch (final CoreException e)
					{
						CorePlugin.logError(Status.ERROR,
								"Trouble whilst loading right-click handler extensions", e);
					}
				}
			}
		}
	}

	/** have a look at the supplied editors, find which properties are common */
	protected static MethodDescriptor[] getCommonMethodsFor(
			final Editable[] editables)
	{
		MethodDescriptor[] res = null;
		final MethodDescriptor[] demo = new MethodDescriptor[]
		{};

		// right, get the first set of properties
		if (editables.length > 0)
		{
			final Editable first = editables[0];
			final EditorType firstInfo = first.getInfo();
			if (firstInfo != null)
			{
				res = firstInfo.getMethodDescriptors();

				// only continue if there are any methods to compare against
				if (res != null)
				{
					// right, are there any more?
					if (editables.length > 1)
					{
						// pass through the others, finding the common ground
						for (int cnt = 1; cnt < editables.length; cnt++)
						{
							final Editable thisE = editables[cnt];

							// get its props
							final EditorType thisEditor = thisE.getInfo();

							// do we have an editor?
							if (thisEditor != null)
							{
								final MethodDescriptor[] newSet = thisEditor
										.getMethodDescriptors();

								// find the common ones
								res = (MethodDescriptor[]) getIntersectionFor(res, newSet, demo);
							}
							else
							{
								// this type doesn't even have an editor, so it can't have any 
								// matching items!
								res = null;
							}
						}
					}
				}
			}
		}

		return res;
	}

	/** have a look at the supplied editors, find which properties are common */
	protected static MWC.GUI.Tools.SubjectAction[] getUndoableActionsFor(
			final Editable[] editables)
	{
		MWC.GUI.Tools.SubjectAction[] res = null;
		final MWC.GUI.Tools.SubjectAction[] demo = new MWC.GUI.Tools.SubjectAction[]
		{};

		// right, get the first set of properties
		if (editables.length > 0)
		{
			final Editable first = editables[0];
			final EditorType firstInfo = first.getInfo();
			if (firstInfo != null)
			{
				res = firstInfo.getUndoableActions();

				// only continue if there are any methods to compare against
				if (res != null)
				{
					// right, are there any more?
					if (editables.length > 1)
					{
						// pass through the others, finding the common ground
						for (int cnt = 1; cnt < editables.length; cnt++)
						{
							final Editable thisE = editables[cnt];

							// get its props
							final EditorType thisEditor = thisE.getInfo();

							// do we have an editor?
							if (thisEditor != null)
							{
								final MWC.GUI.Tools.SubjectAction[] newSet = thisEditor
										.getUndoableActions();

								// find the common ones
								res = (MWC.GUI.Tools.SubjectAction[]) getIntersectionFor(res,
										newSet, demo);
							}
						}
					}
				}
			}
		}
		return res;
	}

	private static MWC.GUI.Tools.SubjectAction[] getIntersectionFor(
			final MWC.GUI.Tools.SubjectAction[] a,
			final MWC.GUI.Tools.SubjectAction[] b,
			final MWC.GUI.Tools.SubjectAction[] demo)
	{
		final Vector<MWC.GUI.Tools.SubjectAction> res = new Vector<MWC.GUI.Tools.SubjectAction>();

		for (int cnta = 0; cnta < a.length; cnta++)
		{
			final MWC.GUI.Tools.SubjectAction thisP = a[cnta];
			for (int cntb = 0; cntb < b.length; cntb++)
			{
				final MWC.GUI.Tools.SubjectAction thatP = b[cntb];
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
			final Editable[] editables)
	{
		PropertyDescriptor[] res = null;
		final PropertyDescriptor[] demo = new PropertyDescriptor[]
		{};
		// right, get the first set of properties
		if (editables.length > 0)
		{
			final Editable first = editables[0];
			final EditorType firstInfo = first.getInfo();
			if (firstInfo != null)
			{
				res = firstInfo.getPropertyDescriptors();

				// only continue if there are any property descriptors
				if (res != null)
				{
					// right, are there any more?
					if (editables.length > 1)
					{
						// pass through the others, finding the common ground
						for (int cnt = 1; cnt < editables.length; cnt++)
						{
							final Editable thisE = editables[cnt];

							// get its props
							final EditorType thisEditor = thisE.getInfo();

							// do we have an editor?
							if (thisEditor != null)
							{
								final PropertyDescriptor[] newSet = thisEditor
										.getPropertyDescriptors();

								// find the common ones
								res = (PropertyDescriptor[]) getIntersectionFor(res, newSet,
										demo);
							}
							else
							{
								// this type doesn't even have an editor, so it can't have any 
								// matching items!
								res = null;
							}
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
	protected static MethodDescriptor[] getIntersectionFor(
			final MethodDescriptor[] a, final MethodDescriptor[] b,
			final MethodDescriptor[] demo)
	{
		final Vector<MethodDescriptor> res = new Vector<MethodDescriptor>();

		for (int cnta = 0; cnta < a.length; cnta++)
		{
			final MethodDescriptor thisP = a[cnta];
			if (b != null)
			{
				for (int cntb = 0; cntb < b.length; cntb++)
				{
					final MethodDescriptor thatP = b[cntb];
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
			final PropertyDescriptor[] a, final PropertyDescriptor[] b,
			final PropertyDescriptor[] demo)
	{
		final Vector<PropertyDescriptor> res = new Vector<PropertyDescriptor>();

		for (int cnta = 0; cnta < a.length; cnta++)
		{
			final PropertyDescriptor thisP = a[cnta];
			for (int cntb = 0; cntb < b.length; cntb++)
			{
				final PropertyDescriptor thatP = b[cntb];
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
		private final Editable[] _subjects;

		private final Method _method;

		private final Layer _topLayer;

		private final Layers _theLayers;

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
		public SubjectMethod(final String title, final Editable[] subject,
				final Method method, final Layer topLayer, final Layers theLayers)
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
				final Editable thisSubject = _subjects[cnt];
				try
				{
					_method.invoke(thisSubject, new Object[0]);

				}
				catch (final IllegalArgumentException e)
				{
					CorePlugin.logError(Status.ERROR,
							"whilst firing method from right-click", e);
				}
				catch (final IllegalAccessException e)
				{
					CorePlugin.logError(Status.ERROR,
							"whilst firing method from right-click", e);
				}
				catch (final InvocationTargetException e)
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
	static private boolean supportsBooleanEditor(final PropertyDescriptor thisP)
	{
		final boolean res;

		// get the prop type
		final Class thisType = thisP.getPropertyType();
		final Class boolClass = Boolean.class;

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
	static private boolean supportsListEditor(final PropertyDescriptor thisP)
	{
		boolean res = false;

		// find out the type of the editor
		final Method m = thisP.getReadMethod();
		final Class cl = m.getReturnType();

		// is there a custom editor for this type?
		final Class c = thisP.getPropertyEditorClass();

		PropertyEditor pe = null;
		// try to create an editor for this class
		try
		{
			if (c != null)
				pe = (PropertyEditor) c.newInstance();
		}
		catch (final Exception e)
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
			final String[] tags = pe.getTags();

			// are there any tags for this class?
			if (tags != null)
			{
				res = true;
			}
		}

		return res;
	}

	static private MenuManager generateBooleanEditorFor(
			final IMenuManager manager, final MenuManager subMenu,
			final PropertyDescriptor thisP, final Editable[] editables,
			final Layers theLayers, final Layer topLevelLayer)
	{

		boolean currentVal = false;
		final Method getter = thisP.getReadMethod();
		final Method setter = thisP.getWriteMethod();
		MenuManager result = subMenu;
		try
		{
			final Boolean valNow = (Boolean) getter.invoke(editables[0],
					(Object[]) null);
			currentVal = valNow.booleanValue();
		}
		catch (final Exception e)
		{
			CorePlugin.logError(Status.ERROR, "Failed to retrieve old value for:"
					+ editables[0].getName(), e);
		}

		final IAction changeThis = new Action(thisP.getDisplayName(),
				IAction.AS_CHECK_BOX)
		{
			public void run()
			{
				try
				{
					final ListPropertyAction la = new ListPropertyAction(
							thisP.getDisplayName(), editables, getter, setter, new Boolean(
									isChecked()), theLayers, topLevelLayer);

					CorePlugin.run(la);
				}
				catch (final Exception e)
				{
					CorePlugin.logError(IStatus.INFO,
							"While executing boolean editor for:" + thisP, e);
				}
			}
		};
		changeThis.setChecked(currentVal);
		changeThis.setToolTipText(thisP.getShortDescription());

		// is our sub-menu already created?
		if (result == null)
		{
			String nameStr;
			if (editables.length > 1)
				nameStr = MULTIPLE_ITEMS_STR;
			else
				nameStr = editables[0].getName();

			result = new MenuManager(nameStr);
			manager.add(result);
		}

		result.add(changeThis);

		return result;
	}

	static private IAction generateUndoableActionFor(
			final MWC.GUI.Tools.SubjectAction theAction, final Editable[] editables,
			final Layers theLayers, final Layer topLevelLayer)
	{

		final IAction changeThis = new Action(theAction.toString(),
				IAction.AS_PUSH_BUTTON)
		{
			public void run()
			{
				try
				{
					final AbstractOperation la = new UndoableAction(theAction.toString(),
							editables, theAction, theLayers, topLevelLayer);

					CorePlugin.run(la);
				}
				catch (final Exception e)
				{
					CorePlugin.logError(
							IStatus.INFO,
							"While executing undoable operations for for:"
									+ theAction.toString(), e);
				}
			}
		};
		return changeThis;
	}

	@SuppressWarnings("rawtypes")
	static private MenuManager generateListEditorFor(final IMenuManager manager,
			final MenuManager subMenu, final PropertyDescriptor thisP,
			final Editable[] editables, final Layers theLayers,
			final Layer topLevelLayer)
	{

		// find out the type of the editor
		final Method m = thisP.getReadMethod();
		final Class cl = m.getReturnType();
		MenuManager result = subMenu;

		// is there a custom editor for this type?
		final Class c = thisP.getPropertyEditorClass();

		PropertyEditor pe = null;
		// try to create an editor for this class
		try
		{
			if (c != null)
				pe = (PropertyEditor) c.newInstance();
		}
		catch (final Exception e)
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
		final String[] tags = pe.getTags();

		// are there any tags for this class?
		if (tags != null)
		{
			// create a drop-down list
			final MenuManager thisChoice = new MenuManager(thisP.getDisplayName());

			// sort out the setter details
			final Method getter = thisP.getReadMethod();

			// get the current value
			Object val = null;
			try
			{
				val = getter.invoke(editables[0], (Object[]) null);
			}
			catch (final Exception e)
			{
				MWC.Utilities.Errors.Trace.trace(e);
			}
			pe.setValue(val);

			// convert the current value to text
			final String currentValue = pe.getAsText();

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
								final Method setter = thisP.getWriteMethod();

								// ok, place the change in the action
								final ListPropertyAction la = new ListPropertyAction(
										thisP.getDisplayName(), editables, getter, setter,
										thisValue, theLayers, topLevelLayer);

								// and add it to the history
								CorePlugin.run(la);
							}
						}
						catch (final Exception e)
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
			if (result == null)
			{
				String nameStr;
				if (editables.length > 1)
					nameStr = MULTIPLE_ITEMS_STR;
				else
					nameStr = editables[0].getName();

				result = new MenuManager(nameStr);
				manager.add(result);
			}

			result.add(thisChoice);

		}

		return result;
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
			catch (final Exception e)
			{
				CorePlugin.logError(Status.ERROR, "Failed to retrieve old value for:"
						+ "Multiple items starting with:" + _subjects[0].getName(), e);
			}

			// put in the global context, for some reason
			super.addContext(CorePlugin.CMAP_CONTEXT);
		}

		private IStatus doIt(final Object theValue)
		{
			IStatus res = Status.OK_STATUS;
			for (int cnt = 0; cnt < _subjects.length; cnt++)
			{
				final Editable thisSubject = _subjects[cnt];
				try
				{
					_setter.invoke(thisSubject, new Object[]
					{ theValue });
				}
				catch (final InvocationTargetException e)
				{
					CorePlugin
							.logError(Status.ERROR,
									"Setter call failed:" + thisSubject.getName() + " Error was:"
											+ e.getTargetException().getMessage(),
									e.getTargetException());
					res = Status.CANCEL_STATUS;
				}
				catch (final IllegalArgumentException e)
				{
					CorePlugin.logError(Status.ERROR, "Wrong parameters pass to:"
							+ thisSubject.getName(), e);
					res = Status.CANCEL_STATUS;
				}
				catch (final IllegalAccessException e)
				{
					CorePlugin.logError(Status.ERROR, "Illegal access problem for:"
							+ thisSubject.getName(), e);
					res = Status.CANCEL_STATUS;
				}
			}

			// and tell everybody (we only need to do this if the previous call
			// works,
			// if an exception is thrown we needn't worry about the update
			fireUpdate();

			return res;

		}

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			return doIt(_newValue);
		}

		public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			return doIt(_newValue);
		}

		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
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

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			IStatus res = Status.OK_STATUS;
			for (int cnt = 0; cnt < _subjects.length; cnt++)
			{
				final Editable thisSubject = _subjects[cnt];
				try
				{
					_action.execute(thisSubject);

				}
				catch (final IllegalArgumentException e)
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

		public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			IStatus res = Status.OK_STATUS;
			for (int cnt = 0; cnt < _subjects.length; cnt++)
			{
				final Editable thisSubject = _subjects[cnt];
				try
				{
					_action.execute(thisSubject);
				}
				catch (final Exception e)
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

		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
				throws ExecutionException
		{
			IStatus res = Status.OK_STATUS;
			for (int cnt = 0; cnt < _subjects.length; cnt++)
			{
				final Editable thisSubject = _subjects[cnt];
				try
				{
					_action.undo(thisSubject);
				}
				catch (final Exception e)
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
				final PropertyDescriptor[] demo = new PropertyDescriptor[]
				{};
				final PropertyDescriptor[] pa = new PropertyDescriptor[]
				{ new PropertyDescriptor("Color", FixWrapper.class),
						new PropertyDescriptor("Font", FixWrapper.class),
						new PropertyDescriptor("Label", FixWrapper.class),
						new PropertyDescriptor("LabelShowing", FixWrapper.class),
						new PropertyDescriptor("Visible", FixWrapper.class) };
				final PropertyDescriptor[] pb = new PropertyDescriptor[]
				{ new PropertyDescriptor("Color", FixWrapper.class),
						new PropertyDescriptor("Font", FixWrapper.class),
						new PropertyDescriptor("Label", FixWrapper.class),
						new PropertyDescriptor("LabelShowing", FixWrapper.class),
						new PropertyDescriptor("SymbolShowing", FixWrapper.class), };
				final PropertyDescriptor[] pc = new PropertyDescriptor[]
				{ new PropertyDescriptor("LabelShowing", FixWrapper.class),
						new PropertyDescriptor("SymbolShowing", FixWrapper.class), };
				final PropertyDescriptor[] pd = new PropertyDescriptor[]
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
			catch (final IntrospectionException e)
			{
				CorePlugin.logError(Status.ERROR, "Whilst doing tests", e);
				assertTrue("threw some error", false);
			}
		}

		public final void testAdditionalSomePresent()
		{
			LabelWrapper lw = new LabelWrapper("Some label", new WorldLocation(1.1,
					1.1, 12), Color.red);
			Editable[] editables = new Editable[]
			{ lw };
			MenuManager menu = new MenuManager("Holder");

			RightClickSupport.getDropdownListFor(menu, editables, null, null, null,
					true);

			// note: this next test may return 4 if run from within IDE, 
			// some contributions provided by plugins
			assertEquals("Has items", 2, menu.getSize(),2);

		}

		public final void testAdditionalNonePresent()
		{
			ShapeWrapper sw = new ShapeWrapper("rect", new RectangleShape(
					new WorldLocation(12.1, 12.3, 12), new WorldLocation(1.1, 1.1, 12)),
					Color.red, new HiResDate(2222));
			Editable[] editables = new Editable[]
			{ sw };
			MenuManager menu = new MenuManager("Holder");

			RightClickSupport.getDropdownListFor(menu, editables, null, null, null,
					true);

			boolean foundTransparent = false;

			// note: this next test may return 4 if run from within IDE, 
			// some contributions provided by plugins
			assertEquals("Has items", 2, menu.getSize(),2);
			
			IContributionItem[] items = menu.getItems();
			for (int i = 0; i < items.length; i++)
			{
				IContributionItem thisI = items[i];
				if (thisI instanceof MenuManager)
				{
					MenuManager subMenu = (MenuManager) thisI;
					IContributionItem[] subItems = subMenu.getItems();
					for (int j = 0; j < subItems.length; j++)
					{
						IContributionItem subI = subItems[j];
						if (subI instanceof ActionContributionItem)
						{
							ActionContributionItem ac = (ActionContributionItem) subI;
							String theName = ac.getAction().getText();
							if (theName.equals("SemiTransparent"))
								foundTransparent = true;
						}
					}
				}
			}

			assertTrue("The additional bean info got processed!", foundTransparent);
		}

		public final void testPropMgt()
		{
			final Editable itemOne = new FixWrapper(new Fix(new HiResDate(122333),
					new WorldLocation(1, 2, 3), 12, 14));
			final Editable itemTwo = new FixWrapper(new Fix(new HiResDate(122334),
					new WorldLocation(1, 2, 5), 13, 12));
			final Editable itemThree = new SensorWrapper("alpha");
			final Editable[] lst = new Editable[]
			{ itemOne, itemTwo };
			final Editable[] lst2 = new Editable[]
			{ itemOne, itemThree };
			final Editable[] lst3 = new Editable[]
			{ itemThree, itemOne, itemThree };
			final Editable[] lst4 = new Editable[]
			{ itemThree, itemThree };
			final Editable[] lst5 = new Editable[]
			{ itemOne };
			assertEquals("no data", 2, lst.length);
			PropertyDescriptor[] props = RightClickSupport
					.getCommonPropertiesFor(lst);
			assertNotNull("found some data", props);
			assertEquals("found right matches", 13, props.length);
			props = RightClickSupport.getCommonPropertiesFor(lst2);
			assertNotNull("found some data", props);
			assertEquals("found right matches", 1, props.length);
			props = RightClickSupport.getCommonPropertiesFor(lst3);
			assertNotNull("found some data", props);
			assertEquals("found right matches", 1, props.length);
			props = RightClickSupport.getCommonPropertiesFor(lst4);
			assertNotNull("found some data", props);
			assertEquals("found right matches", 9, props.length);
			props = RightClickSupport.getCommonPropertiesFor(lst5);
			assertNotNull("found some data", props);
			assertEquals("found right matches", 13, props.length);
		}
	}
}
