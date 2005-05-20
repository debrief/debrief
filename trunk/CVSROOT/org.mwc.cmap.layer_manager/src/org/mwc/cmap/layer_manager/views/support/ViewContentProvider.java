/**
 * 
 */
package org.mwc.cmap.layer_manager.views.support;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mwc.cmap.core.property_support.PlottableWrapper;
import org.mwc.cmap.layer_manager.views.LayerManagerView;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;

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
				Vector list = new Vector(0, 1);
				Layers theLayers = (Layers) parent;
				Enumeration numer = theLayers.elements();
				while (numer.hasMoreElements())
				{
					Layer thisL = (Layer) numer.nextElement();
					PlottableWrapper wrapper = new PlottableWrapper(thisL, null, theLayers);
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
		if (child instanceof PlottableWrapper)
		{
			PlottableWrapper thisP = (PlottableWrapper) child;
			PlottableWrapper parent = thisP.getParent();
			res = parent;
		}
		return res;
	}

	public Object[] getChildren(Object parent)
	{
		Object[] res = new Object[0];
		if (parent instanceof PlottableWrapper)
		{
			PlottableWrapper pl = (PlottableWrapper) parent;
			if (pl.hasChildren())
			{

				Layer thisL = (Layer) pl.getPlottable();
				Vector list = new Vector(0, 1);
				Enumeration numer = thisL.elements();
				while (numer.hasMoreElements())
				{
					Plottable thisP = (Plottable) numer.nextElement();
					PlottableWrapper pw = new PlottableWrapper(thisP, pl, pl.getLayers());
					list.add(pw);
				}
				res = list.toArray();
			}
		}
		return res;
	}

	public boolean hasChildren(Object parent)
	{
		boolean res = false;
		if (parent instanceof PlottableWrapper)
		{
			PlottableWrapper pw = (PlottableWrapper) parent;
			res = pw.hasChildren();
		}

		return res;
	}
}