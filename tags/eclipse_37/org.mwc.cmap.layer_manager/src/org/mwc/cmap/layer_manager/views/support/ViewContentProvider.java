/**
 * 
 */
package org.mwc.cmap.layer_manager.views.support;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mwc.cmap.core.property_support.*;
import org.mwc.cmap.layer_manager.views.LayerManagerView;

import MWC.GUI.*;

/*
 * The content provider class is responsible for providing objects to the view.
 * It can wrap existing objects in adapters or simply return objects as-is.
 * These objects may be sensitive to the current input of the view, or ignore it
 * and always show the same content (like Task List, for example).
 */
public class ViewContentProvider implements IStructuredContentProvider,	ITreeContentProvider
{
	/**
	 * the view provider
	 */
	private final LayerManagerView _myViewProvider;

	/**
	 * @param view
	 */
	public ViewContentProvider(LayerManagerView view)
	{
		_myViewProvider = view;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput)
	{
	}

	public void dispose()
	{
	}

	public Object[] getElements(Object parent)
	{
		Object[] res = null;
		if (parent.equals(_myViewProvider.getViewSite()))
		{
			res = new Object[0];
		}
		else
		{
			if (parent instanceof Layers)
			{
				// cool - run through the layers
				Vector<EditableWrapper> list = new Vector<EditableWrapper>(0, 1);
				Layers theLayers = (Layers) parent;
				Enumeration<Editable> numer = theLayers.elements();
				while (numer.hasMoreElements())
				{
					Layer thisL = (Layer) numer.nextElement();
					EditableWrapper wrapper = new EditableWrapper(thisL, null, theLayers);
					list.add(wrapper);
				}
				res = list.toArray();
			}
		}

		return res;
	}

	public Object getParent(Object child)
	{
		Object res = null;
		if (child instanceof EditableWrapper)
		{
			EditableWrapper thisP = (EditableWrapper) child;
			EditableWrapper parent = thisP.getParent();
			res = parent;
		}
		return res;
	}

	public Object[] getChildren(Object parent)
	{
		Object[] res = new Object[0];
		if (parent instanceof EditableWrapper)
		{
			EditableWrapper pl = (EditableWrapper) parent;
			if (pl.hasChildren())
			{
				Vector<EditableWrapper> list = new Vector<EditableWrapper>(0, 1);

				Layer thisL = (Layer) pl.getEditable();
				
				// right, do they have their own order?
				if(thisL.hasOrderedChildren())
				{
					int index = 0;
					Enumeration<Editable> numer = thisL.elements();
					while (numer.hasMoreElements())
					{
						Editable thisP = (Editable) numer.nextElement();
						EditableWrapper pw = new EditableWrapper.OrderedEditableWrapper(thisP, pl, pl.getLayers(), index);
						list.add(pw);
						index++;
					}
				}
				else
				{
					Enumeration<Editable> numer = thisL.elements();
					if(numer != null)
					{
						while (numer.hasMoreElements())
						{
							Editable thisP = (Editable) numer.nextElement();
							EditableWrapper pw = new EditableWrapper(thisP, pl, pl.getLayers());
							list.add(pw);
						}
					}
				}
				
				res = list.toArray();
			}
		}
		return res;
	}

	public boolean hasChildren(Object parent)
	{
		boolean res = false;
		if (parent instanceof EditableWrapper)
		{
			EditableWrapper pw = (EditableWrapper) parent;
			res = pw.hasChildren();
		}

		return res;
	}
}