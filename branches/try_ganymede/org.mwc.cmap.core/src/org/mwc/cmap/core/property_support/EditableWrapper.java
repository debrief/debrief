package org.mwc.cmap.core.property_support;

import java.beans.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.views.properties.*;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.*;
import MWC.GUI.Editable.DeprecatedPropertyDescriptor;

/**
 * embedded class which wraps a plottable object alongside some useful other
 * bits
 */
public class EditableWrapper implements IPropertySource
{
	/**
	 * the data object we are wrapping
	 */
	protected final Editable _editable;

	/**
	 * the editable properties of this object
	 */
	IPropertyDescriptor[] _myDescriptors;

	/**
	 * the layers we're looking at
	 */
	protected final Layers _theLayers;

	/**
	 * the tags we use for the boolean editor
	 */
	static String[] _booleanTags = new String[] { "Yes", "No" };

	/**
	 * the parent of this object
	 */
	private final EditableWrapper _parent;

	/**
	 * constructor - ok, lets get going
	 * 
	 * @param plottable
	 * @param theLayers
	 */
	public EditableWrapper(Editable plottable, EditableWrapper parent, Layers theLayers)
	{
		_editable = plottable;
		_theLayers = theLayers;
		_parent = parent;
	}

	/**
	 * constructor - ok, lets get going
	 * 
	 * @param plottable
	 * @param theLayers
	 */
	public EditableWrapper(Editable plottable, Layers theLayers)
	{
		_editable = plottable;
		_theLayers = theLayers;
		_parent = null;
	}

	/**
	 * constructor - ok, lets get going
	 * 
	 * @param plottable
	 * @param theLayers
	 */
	public EditableWrapper(Editable plottable)
	{
		_editable = plottable;
		_theLayers = null;
		_parent = null;
	}

	public Layer getTopLevelLayer()
	{
		Layer res = null;
		// ok. we may just be changing a single layer
		// head back up the tree to the base layer
		EditableWrapper parent = getParent();

		// just see if we are a top-level layer
		if (parent == null)
		{
			// get the parent then
			Editable parentE = getEditable();
			
			// right, is it an editable?		
			if(parentE instanceof Layer)
				res = (Layer) parentE;
		}
		else
		{
			EditableWrapper parentParent = parent;
			while (parent != null)
			{
				parent = parent.getParent();
				if (parent != null)
				{
					parentParent = parent;
				}
			}

			// sorted. previous parent should be the top-level layer
			res = (Layer) parentParent.getEditable();
		}
		return res;
	}

	/**
	 * hey, where's the thing we're dealing with?
	 * 
	 * @return
	 */
	public Editable getEditable()
	{
		return _editable;
	}

	public boolean equals(Object arg0)
	{
		Editable targetEditable = null;
		boolean res = false;
		if (arg0 instanceof EditableWrapper)
		{
			EditableWrapper pw = (EditableWrapper) arg0;
			targetEditable = pw.getEditable();
		}

		// right, have we found something to match?
		if (targetEditable != null)
		{
			res = (targetEditable == _editable);
		}

		return res;
	}

	public Object getEditableValue()
	{
		return _editable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	final public IPropertyDescriptor[] getPropertyDescriptors()
	{
		
		if (_myDescriptors == null)
		{
			Vector<IPropertyDescriptor> list = new Vector<IPropertyDescriptor>(0, 1);
			IPropertyDescriptor[] res = new IPropertyDescriptor[] { null };
			Editable.EditorType editor = _editable.getInfo();
			if (editor != null)
			{
				PropertyDescriptor[] properties = editor.getPropertyDescriptors();
				// _myDescriptors = new IPropertyDescriptor[properties.length];

				if (properties != null)
				{
					for (int i = 0; i < properties.length; i++)
					{
						final PropertyDescriptor thisProp = properties[i];

						// hmm, is it a legacy property?
						if (thisProp instanceof DeprecatedPropertyDescriptor)
						{
							// right, just give it a stiff ignoring, it's deprecated
						}
						else
						{
							// ok, wrap it, and add it to our list.
							IPropertyDescriptor newProp = new DebriefProperty(thisProp,
									(Editable) editor.getData(), null);
							list.add(newProp);
						}
					}
				}

				// hmm, are there any "supplemental" editors?
				BeanInfo[] others = editor.getAdditionalBeanInfo();
				if (others != null)
				{
					// adding more editors
					for (int i = 0; i < others.length; i++)
					{
						BeanInfo bn = others[i];
						if (bn instanceof MWC.GUI.Editable.EditorType)
						{
							Editable.EditorType et = (Editable.EditorType) bn;
							Editable obj = (Editable) et.getData();
							PropertyDescriptor[] pds = et.getPropertyDescriptors();
							if (pds != null)
							{
								for (int j = 0; j < pds.length; j++)
								{
									PropertyDescriptor pd = pds[j];

									// is this an 'expert' property which
									// should not appear in here as an additional?
									if (pd.isExpert())
									{
										// do nothing, we don't want to show this
									}
									else
									{
										// ok, add this editor
										IPropertyDescriptor newProp = new DebriefProperty(pd, obj, null);

										list.add(newProp);
									}
								}
							}
						}
					}
				}
			}

			// hmm, did we find any
			if (list.size() > 0)
			{
				_myDescriptors = (IPropertyDescriptor[]) list.toArray(res);
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
	final private DebriefProperty getDescriptorFor(String id)
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
	final public Object getPropertyValue(Object id)
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

	final public boolean isPropertySet(Object id)
	{
		return true;
	}

	final public void resetPropertyValue(Object id)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object,
	 *      java.lang.Object)
	 */
	final public void setPropertyValue(Object id, Object value)
	{

		// convert the id back to a string
		String thisName = (String) id;

		// ok. now find the matching descriptor
		DebriefProperty thisProp = getDescriptorFor(thisName);

		// and find the existing value
		Object oldVal = thisProp.getValue();

		// ok, create the action
		PropertyChangeAction pca = new PropertyChangeAction(oldVal, value, thisProp,
				getEditable().getName(), getLayers(), getTopLevelLayer());

		// and sort it out with the history
		CorePlugin.run(pca);
	}

	final public Layers getLayers()
	{
		return _theLayers;
	}

	final public boolean hasChildren()
	{
		return (_editable instanceof Layer);
	}

	@SuppressWarnings("unchecked")
	final protected static Class getPropertyClass(PropertyDescriptor thisProp)
	{

		Class res = null;
		try
		{
			// find out the type of the editor
			Method m = thisProp.getReadMethod();
			res = m.getReturnType();
		}
		catch (Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

		return res;
	}

	public EditableWrapper getParent()
	{
		return _parent;
	}

	/**
	 * embedded class which stores a property change in an undoable operation
	 * 
	 * @author ian.mayo
	 */
	final protected static class PropertyChangeAction extends AbstractOperation
	{
		private final Object _oldValue;

		private final Object _newValue;

		private final DebriefProperty _property;

		private final Layers _wholeLayers;

		/**
		 * the parent plottable for this item - the layer that we fire an update for
		 * after a change
		 */
		private Layer _topLevelLayer;

		/**
		 * setup the change details
		 * 
		 * @param oldValue
		 *          old value (to undo to)
		 * @param newValue
		 *          new value
		 * @param subject
		 *          the item being edited
		 * @param wholeLayers
		 *          the complete set of layers to redraw
		 * @param parentLayer
		 *          the parent layer - the one to be updated following a change
		 * @param wholeLayers
		 *          the layers object we inform about the change
		 */
		public PropertyChangeAction(Object oldValue, Object newValue,
				DebriefProperty subject, String name, Layers wholeLayers, Layer topLevelLayer)
		{
			super(name + " " + subject.getDisplayName());

			this.addContext(CorePlugin.CMAP_CONTEXT);

			_oldValue = oldValue;
			_newValue = newValue;
			_property = subject;
			_wholeLayers = wholeLayers;
			_topLevelLayer = topLevelLayer;
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// get the value, if it worked
			_property.setValue(_newValue);

			// ok, and tell any listeners that want to know
			// - the only listener I can think of is the Java3d properties viewer
			_property._subject.getInfo().fireChanged(_property.getValue(),
					_property.getDisplayName(), _oldValue, _newValue);

			// fire the reformatted event for the parent layer
			// - note, we may not have the layers object if this editable isn't a plot
			// object
			// (it could be an xy plot)
			if (_wholeLayers != null)
				_wholeLayers.fireReformatted(_topLevelLayer);

			return Status.OK_STATUS;
		}

		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			return execute(monitor, info);
		}

		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// get the value, if it worked
			_property.setValue(_oldValue);

			// ok, and tell any listeners that want to know...
			_property._subject.getInfo().fireChanged(_property.getValue(),
					_property.getDisplayName(), _newValue, _oldValue);

			// fire the reformatted event for the parent layer
			_wholeLayers.fireReformatted(_topLevelLayer);

			// and return the status
			return Status.OK_STATUS;
		}

	}
	
	public static class OrderedEditableWrapper extends EditableWrapper implements Comparable<OrderedEditableWrapper>
	{
		private final int _myIndex;
		
		public OrderedEditableWrapper(Editable plottable, EditableWrapper parent, Layers theLayers, int myIndex)
		{
			super(plottable, parent, theLayers);
			_myIndex  = myIndex;
		}

		public int compareTo(OrderedEditableWrapper o)
		{
			OrderedEditableWrapper other = (OrderedEditableWrapper) o;
			int hisIndex = other._myIndex;
			int res;
			
			if(_myIndex < hisIndex)
				res = -1;
			else if(_myIndex > hisIndex)
				res = 1;
			else
				res = 0;
			
			return res;
		}
		
		
		
	}
	
}