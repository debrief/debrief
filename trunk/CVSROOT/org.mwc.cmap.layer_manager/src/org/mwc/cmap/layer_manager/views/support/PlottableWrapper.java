package org.mwc.cmap.layer_manager.views.support;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import MWC.GUI.Layer;
import MWC.GUI.Plottable;

/**
 * embedded class which wraps a plottable object alongside some useful other
 * bits
 */
public class PlottableWrapper implements IPropertySource
{
	private Plottable _plottable;

	private PlottableWrapper _parent;

	public PlottableWrapper(Plottable plottable, PlottableWrapper parent)
	{
		if(plottable == null)
			System.err.println("null PLOTTABLE");
		_plottable = plottable;
		_parent = parent;
	}

	public Layer getTopLevelLayer()
	{
		Layer res = null;
		// ok. we may just be changing a single layer
		// head back up the tree to the base layer
		PlottableWrapper parent = getParent();

		// just see if we are a top-level layer
		if (parent == null)
		{
			res = (Layer) getPlottable();
		}
		else
		{
			PlottableWrapper parentParent = parent;
			while (parent != null)
			{
				parent = parent.getParent();
				if (parent != null)
				{
					parentParent = parent;
				}
			}

			// sorted. previous parent should be the top-level layer
			res = (Layer) parentParent.getPlottable();
		}			
		return res;
	}
	
	public Plottable getPlottable()
	{
		return _plottable;
	}

	public PlottableWrapper getParent()
	{
		return _parent;
	}

	public boolean hasChildren()
	{
		return (_plottable instanceof Layer);
	}

	public boolean equals(Object arg0)
	{
		Plottable targetPlottable = null;
		boolean res = false;
		if (arg0 instanceof PlottableWrapper)
		{
			PlottableWrapper pw = (PlottableWrapper) arg0;
			targetPlottable = pw.getPlottable();
		}
		else if (arg0 instanceof Plottable)
		{
			targetPlottable = (Plottable) arg0;
		}
		
		// right, have we found something to match?
		if (targetPlottable != null)
		{
			res = (targetPlottable == _plottable);
		}

		return res;
	}

	public Object getEditableValue()
	{
		// TODO Auto-generated method stub
		return _plottable;
	}

	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object getPropertyValue(Object id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isPropertySet(Object id)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void resetPropertyValue(Object id)
	{
		// TODO Auto-generated method stub
		
	}

	public void setPropertyValue(Object id, Object value)
	{
		// TODO Auto-generated method stub
		
	}
}