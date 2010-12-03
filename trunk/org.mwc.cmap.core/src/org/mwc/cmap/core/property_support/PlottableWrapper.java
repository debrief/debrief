package org.mwc.cmap.core.property_support;

import MWC.GUI.*;

/**
 * embedded class which wraps a plottable object alongside some useful other
 * bits
 */
public class PlottableWrapper extends EditableWrapper
{

	/**
	 * the parent of this object
	 */
	private final PlottableWrapper _parent;

	/** constructor - note, we also store the prarent of the current object
	 * 
	 * @param plottable
	 * @param parent
	 * @param layers
	 */
	public PlottableWrapper(Plottable plottable, PlottableWrapper parent, Layers layers)
	{
		super(plottable, layers);
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
		return (Plottable) _editable;
	}

	public PlottableWrapper getParent()
	{
		return _parent;
	}

	public boolean equals(Object arg0)
	{
		Editable targetPlottable = null;
		boolean res = false;
		if (arg0 instanceof PlottableWrapper)
		{
			PlottableWrapper pw = (PlottableWrapper) arg0;
			targetPlottable = pw.getPlottable();
		}
		else
			targetPlottable = (Plottable) arg0;

		// right, have we found something to match?
		if (targetPlottable != null)
		{
			res = (targetPlottable == _editable);
		}

		return res;
	}

	@Override
	public int hashCode()
	{
		return _editable.hashCode();
	}
	
	
	

}