package org.mwc.cmap.core.property_support;

import java.beans.*;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.*;
import org.eclipse.swt.dnd.Clipboard;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.*;

import MWC.GUI.*;
import MWC.GUI.Editable.EditorType;

public class RightClickSupport
{

	/**
	 * @param manager
	 * @param hideClipboardOperations
	 *          TODO
	 * @param pw
	 */
	static public void getDropdownListFor(IMenuManager manager, Editable[] editables,
			Layer[] topLevelLayers, Layer[] parentLayers, Layers theLayers,
			boolean hideClipboardOperations)
	{

		if (editables.length == 0)
			return;

		// and now the editable bits
		Editable p = editables[0];
		EditorType editor = p.getInfo();

		MenuManager subMenu = null;

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
						topLevelLayers[0]);
			}
			else
			{
				// now the drop-down lists
				if (supportsListEditor(thisP))
				{
					// generate boolean editors in the sub-menu
					subMenu = generateListEditorFor(manager, subMenu, thisP, p, theLayers,
							topLevelLayers[0]);
				}
			}

		}

		// that's this item done. Now see if there are any child elements
		// if ((p instanceof Layer)&& (!hideClipboardOperations))
		// {
		// Layer thisL = (Layer) p;
		// Enumeration enumer = thisL.elements();
		// while (enumer.hasMoreElements())
		// {
		// Plottable pl = (Plottable) enumer.nextElement();
		// getDropdownListFor(subMenu, pl.getInfo(), topLevelLayer, theLayers,
		// true);
		// }
		// }

		Clipboard theClipboard = CorePlugin.getDefault().getClipboard();

		// see if we're still looking at the parent element (we only show clipboard
		// operations for item clicked on)
		if (!hideClipboardOperations)
		{
			// hey, also see if we're going to do a cut/paste
			RightClickCutCopyAdaptor.getDropdownListFor(manager, editables, topLevelLayers,
					topLevelLayers, theLayers, theClipboard);

			// what about paste?
			// - we can only paste into a single destination, so only allow this if
			// there's just one selected
			if (editables.length == 1)
			{
				RightClickPasteAdaptor.getDropdownListFor(manager, editor, topLevelLayers[0],
						topLevelLayers[0], theLayers, theClipboard);
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

		// get the current value
		try
		{
			Method getter = thisP.getReadMethod();
			Object val = getter.invoke(p, null);
			currentVal = ((Boolean) val).booleanValue();
		}
		catch (Exception e)
		{
			CorePlugin
					.logError(IStatus.INFO, "While generating boolean editor for:" + thisP, e);
		}

		IAction changeThis = new Action(thisP.getDisplayName(), IAction.AS_CHECK_BOX)
		{
			public void run()
			{
				try
				{
					Method setter = thisP.getWriteMethod();
					Object args[] = { new Boolean(isChecked()) };
					setter.invoke(p, args);

					// and update the update
					theLayers.fireReformatted(topLevelLayer);
				}
				catch (Exception e)
				{
					CorePlugin.logError(IStatus.INFO,
							"While executing boolean editor for:" + thisP, e);
				}
			}
		};
		changeThis.setChecked(currentVal);

		// is our sub-menu already created?
		if (subMenu == null)
		{
			subMenu = new MenuManager("Edit " + p.getName());
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
			Method getter = thisP.getReadMethod();

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
							Object args[] = { thisValue };
							setter.invoke(p, args);

							// and update the update
							theLayers.fireReformatted(topLevelLayer);
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
				subMenu = new MenuManager("Edit " + p.getName());
				manager.add(subMenu);
			}

			subMenu.add(thisChoice);

		}

		return subMenu;
	}

}
