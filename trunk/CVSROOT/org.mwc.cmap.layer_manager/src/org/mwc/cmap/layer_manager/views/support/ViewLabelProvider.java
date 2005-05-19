/**
 * 
 */
package org.mwc.cmap.layer_manager.views.support;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.layer_manager.views.LayerManagerView;
import org.mwc.cmap.layer_manager.views.support.ViewContentProvider.PlottableWrapper;

import MWC.GUI.Plottable;

public class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
{

	/**
	 * 
	 */
	private final LayerManagerView _myLabelProvider;

	/**
	 * @param manager
	 */
	public ViewLabelProvider(LayerManagerView manager)
	{
		_myLabelProvider = manager;
	}

	public String getText(Object obj)
	{
		ViewContentProvider.PlottableWrapper pw = (PlottableWrapper) obj;
		return pw.getPlottable().toString();
	}

	public Image getImage(Object obj)
	{
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		if (obj instanceof TreeParent)
			imageKey = ISharedImages.IMG_OBJ_FOLDER;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	public Image getColumnImage(Object element, int columnIndex)
	{
		Image res;
		if (columnIndex == 0)
			res = getImage(element);
		else
			res = null;

		return res;
	}

	public String getColumnText(Object element, int columnIndex)
	{
		String res;
		if (columnIndex == 0)
			res = getText(element);
		else
		{
			// sort out the visibility
			ViewContentProvider.PlottableWrapper pw = (ViewContentProvider.PlottableWrapper) element;
			Plottable pl = pw.getPlottable();
			if(pl.getVisible())
			  res = "y";
			else
				res = "n";
		}

		return res;
	}
}