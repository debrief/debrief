/**
 * 
 */
package org.mwc.cmap.layer_manager.views.support;

import java.util.*;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.*;

import Debrief.Wrappers.*;
import Debrief.Wrappers.Track.TMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.*;
import MWC.GUI.Chart.Painters.*;
import MWC.GUI.VPF.FeaturePainter;

public class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
{

	static Vector<ViewLabelImageHelper> imageHelpers = null;

	/**
	 * image to indicate that item is visible
	 */
	Image visImage = null;

	/**
	 * image to indicate that item is hidden (would you believe...)
	 */
	Image hiddenImage = null;

	/**
	 * image to indicate that item isn't plottable
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

	public static void addImageHelper(ViewLabelImageHelper helper)
	{
		if (imageHelpers == null)
			imageHelpers = new Vector<ViewLabelImageHelper>(1, 1);
		imageHelpers.add(helper);
	}

	public static void removeImageHelper(ViewLabelImageHelper helper)
	{
		imageHelpers.remove(helper);
	}

	
	public String getText(Object obj)
	{
		EditableWrapper pw = (EditableWrapper) obj;
		return pw.getEditable().toString();
	}

	public Image getImage(Object subject)
	{
		
		EditableWrapper item = (EditableWrapper) subject;
		Editable editable = item.getEditable();

		Image res = null;

		// try our helpers first
		ImageDescriptor thirdPartyImageDescriptor = null;
		if (imageHelpers != null)
		{
			for (Iterator<ViewLabelImageHelper> iter = imageHelpers.iterator(); iter.hasNext();)
			{
				ViewLabelImageHelper helper = (ViewLabelImageHelper) iter.next();
				thirdPartyImageDescriptor = helper.getImageFor(editable);
				if (thirdPartyImageDescriptor != null)
				{
					break;
				}
			}
		}

		if (thirdPartyImageDescriptor != null)
		{
			// cool go for it
			res = thirdPartyImageDescriptor.createImage();
		}
		else
		{

			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;

			if (editable instanceof TrackWrapper)
				imageKey = "track.gif";
			else if (editable instanceof SensorWrapper)
				imageKey = "SensorFit.png";
			else if (editable instanceof SensorContactWrapper)
				imageKey = "active16.png";
			else if (editable instanceof TMASegment)
				imageKey = "tmasegment.png";
			else if (editable instanceof TrackSegment)
				imageKey = "tracksegment.gif";
			else if (editable instanceof Layer)
				imageKey = "layer.gif";
			else if (editable instanceof FixWrapper)
				imageKey = "Location.png";
			else if (editable instanceof ShapeWrapper)
				imageKey = "shape.gif";
			else if (editable instanceof GridPainter)
				imageKey = "grid.gif";
			else if (editable instanceof ScalePainter)
				imageKey = "scale.gif";
			else if (editable instanceof CoastPainter)
				imageKey = "coast.gif";
			else if (editable instanceof FeaturePainter)
				imageKey = "vpf.gif";

			res = PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);

			if (res == null)
			{
				// ok, try to get the image from our own registry
				res = CorePlugin.getImageFromRegistry(imageKey);
			}
		}
		return res;
	}

	public Image getColumnImage(Object element, int columnIndex)
	{
		Image res = null;
		if (columnIndex == 0)
			res = getImage(element);
		else if (columnIndex == 1)
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
		// else
		// {
		// // sort out the visibility
		// EditableWrapper pw = (EditableWrapper) element;
		// Editable ed = pw.getEditable();
		// if (ed instanceof Editable)
		// {
		// Editable pl = (Editable) ed;
		// if (pl.getVisible())
		// res = "Y";
		// else
		// res = "N";
		// }
		// }

		return res;
	}

	public boolean isLabelProperty(Object element, String property)
	{
		boolean res = true;

		return res;
	}

	public static interface ViewLabelImageHelper
	{
		/**
		 * produce an image icon for this object
		 * 
		 * @param subject
		 * @return
		 */
		public ImageDescriptor getImageFor(Object subject);
	}

}