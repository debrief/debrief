/**
 * 
 */
package org.mwc.cmap.layer_manager.views.support;

import java.util.ArrayList;

import org.mwc.cmap.layer_manager.views.LayerManagerView;

public class TreeParent extends TreeObject
{
	/**
	 * 
	 */
	private final LayerManagerView _myParent;
	ArrayList children;

	public TreeParent(LayerManagerView manager, String name)
	{
		super(manager, name);
		_myParent = manager;
		children = new ArrayList();
	}

	public void addChild(TreeObject child)
	{
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TreeObject child)
	{
		children.remove(child);
		child.setParent(null);
	}

	public TreeObject[] getChildren()
	{
		return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
	}

	public boolean hasChildren()
	{
		return children.size() > 0;
	}
}