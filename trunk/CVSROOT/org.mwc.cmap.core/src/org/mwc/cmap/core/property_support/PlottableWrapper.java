package org.mwc.cmap.core.property_support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Vector;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;

/**
 * embedded class which wraps a plottable object alongside some useful other
 * bits
 */
public class PlottableWrapper implements IPropertySource
{
	/** the data object we are wrapping
	 * 
	 */
	private final Plottable _plottable;

	/** the parent of this object
	 * 
	 */
	private final PlottableWrapper _parent;

	/** the parent layers object containing us
	 * 
	 */
	private final Layers _layers;

	
	/**
	 * the editable properties of this object
	 */
	IPropertyDescriptor[] _myDescriptors;

	/**
	 * the tags we use for the boolean editor
	 */
	static String[] _booleanTags = new String[] { "Yes", "No" };

	public PlottableWrapper(Plottable plottable, 
			PlottableWrapper parent,
			Layers layers)
	{
		if (plottable == null)
			System.err.println("null PLOTTABLE");
		_plottable = plottable;
		_parent = parent;
		_layers = layers;
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
	
	public Layers getLayers()
	{
		return _layers;
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
		return _plottable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{

		if (_myDescriptors == null)
		{
			IPropertyDescriptor[] res = null;
			Editable.EditorType editor = _plottable.getInfo();
			if (editor != null)
			{
				Vector list = new Vector(0, 1);
				PropertyDescriptor[] properties = editor.getPropertyDescriptors();
				_myDescriptors = new IPropertyDescriptor[properties.length];

				for (int i = 0; i < properties.length; i++)
				{
					final PropertyDescriptor thisProp = properties[i];
					IPropertyDescriptor newProp = new DebriefProperty(thisProp,
							_plottable, null);
					_myDescriptors[i] = newProp;
				}

			}
		}
		return _myDescriptors;
	}

	/**
	 * using the supplied display name value, find our matching property
	 * descriptor
	 * 
	 * @param id
	 *          the string to look for
	 * @return the matching property descriptor
	 */
	private DebriefProperty getDescriptorFor(String id)
	{

		DebriefProperty res = null;
		// right, the id we're getting is the string display name.
		// pass through our descriptors to find the matching one
		for (int i = 0; i < _myDescriptors.length; i++)
		{
			IPropertyDescriptor thisDescriptor = _myDescriptors[i];
			if (thisDescriptor.getDisplayName().equals(id))
			{
				res = (DebriefProperty) thisDescriptor;
				break;
			}
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		Object res = null;

		// convert the id back to a string
		String thisName = (String) id;

		// ok. now find the matching descriptor
		DebriefProperty thisProp = getDescriptorFor(thisName);

		// get the value, if it worked
		res = thisProp.getValue();

		// done. for better or for worse..
		return res;
	}

	public boolean isPropertySet(Object id)
	{
		return true;
	}

	public void resetPropertyValue(Object id)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object,
	 *      java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value)
	{

		// convert the id back to a string
		String thisName = (String) id;

		// ok. now find the matching descriptor
		DebriefProperty thisProp = getDescriptorFor(thisName);

		// get the value, if it worked
		thisProp.setValue(value);

		// find the parent layer
		Layer parent = getTopLevelLayer();
		
		// fire the reformatted event for the parent layer
		getLayers().fireReformatted(parent);
	}

	protected static Class getPropertyClass(PropertyDescriptor thisProp)
	{

		Class res = null;
		try
		{
			// find out the type of the editor
			Method m = thisProp.getReadMethod();
			res = m.getReturnType();
		} catch (Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

		return res;
	}

}