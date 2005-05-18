/**
 * 
 */
package org.mwc.cmap.layer_manager.views.support;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mwc.cmap.layer_manager.views.LayerManagerView;


/*
 * The content provider class is responsible for providing objects to the
 * view. It can wrap existing objects in adapters or simply return objects
 * as-is. These objects may be sensitive to the current input of the view, or
 * ignore it and always show the same content (like Task List, for example).
 */
public class ViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider
{
	/** the view provider
	 * 
	 */
	private final LayerManagerView _myViewProvider;

	/** the parent of the layers
	 * 
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
		if (parent.equals(_myViewProvider.getViewSite()))
		{
			if (invisibleRoot == null)
				initialize();
			
			System.out.println("returning children for invisible root");
			return getChildren(invisibleRoot);
		}
		return getChildren(parent);
	}

	public Object getParent(Object child)
	{
		if (child instanceof TreeObject)
		{
			return ((TreeObject) child).getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent)
	{
		if (parent instanceof TreeParent)
		{
			return ((TreeParent) parent).getChildren();
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent)
	{
		if (parent instanceof TreeParent)
			return ((TreeParent) parent).hasChildren();
		return false;
	}

	/*
	 * We will set up a dummy model to initialize tree heararchy. In a real
	 * code, you will connect to a real model and expose its hierarchy.
	 */
	private void initialize()
	{
		TreeObject to1 = new TreeObject(_myViewProvider, "Leaf 1");
		TreeObject to2 = new TreeObject(_myViewProvider, "Leaf 2");
		TreeObject to3 = new TreeObject(_myViewProvider, "Leaf 3");
		TreeParent p1 = new TreeParent(_myViewProvider, "Parent 1");
		p1.addChild(to1);
		p1.addChild(to2);
		p1.addChild(to3);

		TreeObject to4 = new TreeObject(_myViewProvider, "Leaf 4");
		TreeParent p2 = new TreeParent(_myViewProvider, "Parent 2");
		p2.addChild(to4);

		TreeParent root = new TreeParent(_myViewProvider, "Root");
		root.addChild(p1);
		root.addChild(p2);

		invisibleRoot = new TreeParent(_myViewProvider, "");
		invisibleRoot.addChild(root);
	}
}