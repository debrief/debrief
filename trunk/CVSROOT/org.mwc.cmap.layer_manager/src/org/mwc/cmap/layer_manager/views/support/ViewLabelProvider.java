/**
 * 
 */
package org.mwc.cmap.layer_manager.views.support;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.*;

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
	
	/** image to indicate that item is hidden (would you believe...)
	 * 
	 */
	Image hiddenImage = null;

	/** image to indicate that item isn't plottable
	 * 
	 */
	Image nonVisibleImage = null;
	
	/**
	 * 
	 */

	/**
	 */
	public ViewLabelProvider()
	{
			// ok, retrieve the images from our own registry
			visImage = CorePlugin.getImageFromRegistry("check2.png");
			hiddenImage = CorePlugin.getImageFromRegistry("blank_check.png");
			nonVisibleImage = CorePlugin.getImageFromRegistry("desktop.png");
	}

	public String getText(Object obj)
	{
		EditableWrapper pw = (EditableWrapper) obj;
		return pw.getEditable().toString();
	}


	public Image getImage(Object item)
	{
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;

		EditableWrapper pw = (EditableWrapper) item;
		Editable obj = pw.getEditable();

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
			EditableWrapper pw = (EditableWrapper) element;
			Editable ed = pw.getEditable();
			if (ed instanceof Plottable)
			{
				Plottable pl = (Plottable) ed;
				
				if (pl.getVisible())
					res = visImage;
				else
					res = hiddenImage;
			}
			else
			{
				res = nonVisibleImage;
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
//			EditableWrapper pw = (EditableWrapper) element;
//			Editable ed = pw.getEditable();
//			if (ed instanceof Editable)
//			{
//				Editable pl = (Editable) ed;
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