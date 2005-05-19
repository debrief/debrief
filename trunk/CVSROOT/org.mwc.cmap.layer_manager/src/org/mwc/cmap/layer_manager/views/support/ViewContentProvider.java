/**
 * 
 */
package org.mwc.cmap.layer_manager.views.support;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mwc.cmap.layer_manager.views.LayerManagerView;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;

/*
 * The content provider class is responsible for providing objects to the view.
 * It can wrap existing objects in adapters or simply return objects as-is.
 * These objects may be sensitive to the current input of the view, or ignore it
 * and always show the same content (like Task List, for example).
 */
public class ViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider
{
	/**
	 * the view provider
	 */
	private final LayerManagerView _myViewProvider;

	/**
	 * the parent of the layers
	 */
	private TreeParent invisibleRoot;

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
		} else
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
					PlottableWrapper wrapper = new PlottableWrapper(thisL, null);
					list.add(wrapper);
				}
				res = list.toArray();
			}
		}

		return res;
	}

	public Object getParent(Object child)
	{
		if (child instanceof TreeObject)
		{
			return ((TreeObject) child).getParent();
		}
		if(child instanceof PlottableWrapper)
		{
			return ((PlottableWrapper)child).getParent();
		}
		return null;
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
					PlottableWrapper pw = new PlottableWrapper(thisP, thisL);
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
		if (parent instanceof TreeParent)
			res = ((TreeParent) parent).hasChildren();
		else if (parent instanceof PlottableWrapper)
		{
			PlottableWrapper pw = (PlottableWrapper) parent;
			res = pw.hasChildren();
		}

		return res;
	}

	/**
	 * embedded class which wraps a plottable object alongside some useful other
	 * bits
	 */
	public static class PlottableWrapper
	{
		private Plottable _plottable;

		private Layer _parent;

		public PlottableWrapper(Plottable plottable, Layer parent)
		{
			_plottable = plottable;
			_parent = parent;
		}

		public Plottable getPlottable()
		{
			return _plottable;
		}

		public Layer getParent()
		{
			return _parent;
		}

		public boolean hasChildren()
		{
			return (_plottable instanceof Layer);
		}
	}
}