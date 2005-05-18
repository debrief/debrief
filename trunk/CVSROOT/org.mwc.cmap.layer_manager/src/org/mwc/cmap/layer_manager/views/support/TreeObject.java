/**
 * 
 */
package org.mwc.cmap.layer_manager.views.support;

import org.eclipse.core.runtime.IAdaptable;
import org.mwc.cmap.layer_manager.views.LayerManagerView;

public class TreeObject implements IAdaptable
{
	/**
	 * 
	 */
	private final LayerManagerView _myObject;

	String name;

	TreeParent parent;

	public TreeObject(LayerManagerView manager, String name)
	{
		_myObject = manager;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setParent(TreeParent parent)
	{
		this.parent = parent;
	}

	public TreeParent getParent()
	{
		return parent;
	}

	public String toString()
	{
		return getName();
	}

	public Object getAdapter(Class key)
	{
		return null;
	}
}