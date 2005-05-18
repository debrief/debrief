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
		Object[] res = null;
		if (parent.equals(_myViewProvider.getViewSite()))
		{
			res = null;
		}
		else
		{
			if(parent instanceof Layers)
			{
				// cool - run through the layers
				Vector list = new Vector(0,1);
			  Layers theLayers = (Layers) parent;
				Enumeration numer = theLayers.elements();
				while(numer.hasMoreElements())
				{
					Layer thisL = (Layer) numer.nextElement();
					list.add(thisL);
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
		return null;
	}

	public Object[] getChildren(Object parent)
	{
		Object [] res = new Object[0];
		if (parent instanceof TreeParent)
		{
			res=  ((TreeParent) parent).getChildren();
		}
		if(parent instanceof Layer)
		{
			Layer thisL = (Layer) parent;
			Vector list = new Vector(0,1);
			Enumeration numer = thisL.elements();
			while(numer.hasMoreElements())
			{
				Plottable thisP = (Plottable) numer.nextElement();
				list.add(thisP);
			}
			res = list.toArray();
		}
		return res;
	}

	public boolean hasChildren(Object parent)
	{
		boolean res =  false;
		if (parent instanceof TreeParent)
			res = ((TreeParent) parent).hasChildren();
		else if(parent instanceof Layer)
		{
			res = true;
		}
			
		return res;
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