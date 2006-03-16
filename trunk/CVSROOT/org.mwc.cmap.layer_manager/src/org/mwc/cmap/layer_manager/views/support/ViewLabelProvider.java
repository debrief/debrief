/**
 * 
 */
package org.mwc.cmap.layer_manager.views.support;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.PlottableWrapper;

import Debrief.Wrappers.*;
import MWC.GUI.*;
import MWC.GUI.Chart.Painters.*;
import MWC.GUI.VPF.FeaturePainter;

public class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
{

	/** image to indicate that item is visible
	 * 
	 */
	Image visImage = null;
	
	/** image to indicate that item isn't visible (would you believe...)
	 * 
	 */
	Image nonVisImage = null;
	
	/**
	 * 
	 */

	/**
	 */
	public ViewLabelProvider()
	{
			// ok, retrieve the images from our own registry
			visImage = CorePlugin.getImageFromRegistry("check2.png");
			nonVisImage = CorePlugin.getImageFromRegistry("blank_check.png");
	}

	public String getText(Object obj)
	{
		PlottableWrapper pw = (PlottableWrapper) obj;
		return pw.getPlottable().toString();
	}


	public Image getImage(Object item)
	{
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;

		PlottableWrapper pw = (PlottableWrapper) item;
		Editable obj = pw.getPlottable();

		if (obj instanceof TrackWrapper)
			imageKey = "track.gif";
		else if (obj instanceof Layer)
			imageKey = "layer.gif";
		else if (obj instanceof FixWrapper)
			imageKey = "fix.gif";
		else if (obj instanceof ShapeWrapper)
			imageKey = "shape.gif";
		else if (obj instanceof GridPainter)
			imageKey = "grid.gif";
		else if (obj instanceof ScalePainter)
			imageKey = "scale.gif";
		else if (obj instanceof CoastPainter)
			imageKey = "coast.gif";
		else if (obj instanceof FeaturePainter)
			imageKey = "vpf.gif";

		Image theImage = PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);

		if (theImage == null)
		{
			// ok, try to get the image from our own registry
			theImage = CorePlugin.getImageFromRegistry(imageKey);
		}

		// if (obj instanceof TreeParent)
		// imageKey = ISharedImages.IMG_OBJ_FOLDER;
		return theImage;
	}

	public Image getColumnImage(Object element, int columnIndex)
	{
		Image res = null;
		if (columnIndex == 0)
			res = getImage(element);
		else if(columnIndex == 1)
		{
			// hey - don't bother with this bit - just use the text-marker
			
			// sort out the visibility
			PlottableWrapper pw = (PlottableWrapper) element;
			Editable ed = pw.getPlottable();
			if (ed instanceof Plottable)
			{
				Plottable pl = (Plottable) ed;
				if (pl.getVisible())
					res = visImage;
				else
					res = nonVisImage;
			}
		}

		return res;
	}

	public String getColumnText(Object element, int columnIndex)
	{
		String res = null;
		if (columnIndex == 0)
			res = getText(element);
//		else
//		{
//			// sort out the visibility
//			PlottableWrapper pw = (PlottableWrapper) element;
//			Editable ed = pw.getPlottable();
//			if (ed instanceof Plottable)
//			{
//				Plottable pl = (Plottable) ed;
//				if (pl.getVisible())
//					res = "Y";
//				else
//					res = "N";
//			}
//		}

		return res;
	}

	public boolean isLabelProperty(Object element, String property)
	{
		boolean res = true;

		return res;
	}

}