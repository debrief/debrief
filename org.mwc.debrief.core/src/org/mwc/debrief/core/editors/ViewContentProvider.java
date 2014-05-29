/**
 * 
 */
package org.mwc.debrief.core.editors;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mwc.cmap.core.property_support.*;

import MWC.GUI.*;

/*
 * The content provider class is responsible for providing objects to the view.
 * It can wrap existing objects in adapters or simply return objects as-is.
 * These objects may be sensitive to the current input of the view, or ignore it
 * and always show the same content (like Task List, for example).
 */
public class ViewContentProvider implements IStructuredContentProvider,	ITreeContentProvider
{
	/** set a limit on the limit for which we allow a layer
	 * to be expanded  
	 */
	private static final int MAX_ITEMS = 10000;
	
	/**
	 * @param view
	 */
	public ViewContentProvider()
	{
		
	}

	public void inputChanged(final Viewer v, final Object oldInput, final Object newInput)
	{
	}

	public void dispose()
	{
	}

	public Object[] getElements(final Object parent)
	{
		Object[] res = null;
		if (parent instanceof Layers)
		{
			// cool - run through the layers
			final Vector<EditableWrapper> list = new Vector<EditableWrapper>(0, 1);
			final Layers theLayers = (Layers) parent;
			final Enumeration<Editable> numer = theLayers.elements();
			while (numer.hasMoreElements())
			{
				final Layer thisL = (Layer) numer.nextElement();
				final EditableWrapper wrapper = new EditableWrapper(thisL, null,
						theLayers);
				list.add(wrapper);
			}
			res = list.toArray();
		}
		return res;
	}

	public Object getParent(final Object child)
	{
		Object res = null;
		if (child instanceof EditableWrapper)
		{
			final EditableWrapper thisP = (EditableWrapper) child;
			final EditableWrapper parent = thisP.getParent();
			res = parent;
		}
		return res;
	}

	public Object[] getChildren(final Object parent)
	{
		Object[] res = new Object[0];
		if (parent instanceof EditableWrapper)
		{
			final EditableWrapper pl = (EditableWrapper) parent;
			if (pl.hasChildren())
			{
				final Vector<EditableWrapper> list = new Vector<EditableWrapper>(0, 1);

				final Layer thisL = (Layer) pl.getEditable();
				
				// right, do they have their own order?
				if(thisL.hasOrderedChildren())
				{
					int index = 0;
					final Enumeration<Editable> numer = thisL.elements();
					while (numer.hasMoreElements())
					{
						final Editable thisP = (Editable) numer.nextElement();
						final EditableWrapper pw = new EditableWrapper.OrderedEditableWrapper(thisP, pl, pl.getLayers(), index);
						list.add(pw);
						index++;
					}
				}
				else
				{
					final Enumeration<Editable> numer = thisL.elements();
					if(numer != null)
					{
						while (numer.hasMoreElements())
						{
							final Editable thisP = (Editable) numer.nextElement();
							final EditableWrapper pw = new EditableWrapper(thisP, pl, pl.getLayers());
							list.add(pw);
						}
					}
				}
				
				res = list.toArray();
			}
		}
		return res;
	}

	public boolean hasChildren(final Object parent)
	{
		boolean res = false;
		if (parent instanceof EditableWrapper)
		{
			final EditableWrapper pw = (EditableWrapper) parent;
			
			// special case - only allow the layer to open if it has less than max-items
			Editable ed = pw.getEditable();
			if(ed instanceof Plottables)
			{
				// get the object as a list
				Plottables pl = (Plottables) ed;
				
				// check if it's a reasonable size
				res = pl.size() < MAX_ITEMS;
			}
			else
				res = pw.hasChildren();
		}

		return res;
	}
}