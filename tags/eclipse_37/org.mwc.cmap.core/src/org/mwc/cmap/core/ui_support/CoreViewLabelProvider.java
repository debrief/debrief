/**
 * 
 */
package org.mwc.cmap.core.ui_support;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.ColorHelper;
import org.mwc.cmap.core.property_support.EditableWrapper;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.CoastPainter;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GUI.Chart.Painters.ScalePainter;
import MWC.GUI.VPF.FeaturePainter;
import MWC.GenericData.Watchable;

public class CoreViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {

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

	private ImageRegistry _imageRegistry;

	/**
	 * 
	 */

	/**
	 */
	public CoreViewLabelProvider() {
		// ok, retrieve the images from our own registry
		visImage = CorePlugin.getImageFromRegistry("check2.png");
		hiddenImage = CorePlugin.getImageFromRegistry("blank_check.png");
		nonVisibleImage = CorePlugin.getImageFromRegistry("desktop.png");
	}

	public static void addImageHelper(final ViewLabelImageHelper helper) {
		if (imageHelpers == null)
			imageHelpers = new Vector<ViewLabelImageHelper>(1, 1);
		imageHelpers.add(helper);
	}

	public static void removeImageHelper(final ViewLabelImageHelper helper) {
		imageHelpers.remove(helper);
	}

	@Override
	public String getText(final Object obj) {
		final EditableWrapper pw = (EditableWrapper) obj;
		return pw.getEditable().toString();
	}

	protected Image getLocallyCachedImage(final String index) {
		if (_imageRegistry == null)
			_imageRegistry = new ImageRegistry();

		return _imageRegistry.get(index);
	}

	/**
	 * remember this image
	 * 
	 * @param index
	 *            how to retrieve the image
	 * @param image
	 *            the image to store
	 */
	protected void storeLocallyCachedImage(final String index, final Image image) {
		if (_imageRegistry == null)
			_imageRegistry = new ImageRegistry();

		_imageRegistry.put(index, image);
	}

	/**
	 * ditch the cache for the specified items, so we generate new ones as
	 * required
	 * 
	 * @param newList
	 *            the list of objects for whom we want to delete an image
	 * 
	 */
	public void resetCacheFor(final Vector<Object> newList) {
		// ok, cycle through them
		for (final Iterator<Object> iterator = newList.iterator(); iterator
				.hasNext();) {
			final Object object = iterator.next();
			if (object instanceof EditableWrapper) {
				final EditableWrapper ew = (EditableWrapper) object;
				final Editable ed = ew.getEditable();

				// aah does this have color?
				if (ed instanceof Watchable) {
					// ok - cast it
					final Watchable was = (Watchable) ed;

					// generate the id
					final String theId = idFor(was);

					// and ditch it
					_imageRegistry.remove(theId);
				}
			}
		}
	}

	@Override
	public Image getImage(final Object subject) {
		final EditableWrapper item = (EditableWrapper) subject;
		final Editable editable = item.getEditable();
		Image res = null;

		// try our helpers first
		ImageDescriptor thirdPartyImageDescriptor = null;
		if (imageHelpers != null) {
			
			// take a copy of the images listing, in case we receive a new
			// helper whilst we're looping through
			Vector<ViewLabelImageHelper> spareHelpers = new Vector<ViewLabelImageHelper>(imageHelpers);
			
			// ok, now go for it.
			for (final Iterator<ViewLabelImageHelper> iter = spareHelpers
					.iterator(); iter.hasNext();) {
				final ViewLabelImageHelper helper = iter.next();
				thirdPartyImageDescriptor = helper.getImageFor(editable);
				if (thirdPartyImageDescriptor != null) {
					break;
				}
			}
		}

		if (thirdPartyImageDescriptor != null) {
			// right, is this something that we apply color to?
			if (editable instanceof Watchable) {
				final Watchable thisW = (Watchable) editable;

				// sort out the color index
				final String thisId = idFor(thisW);

				// do we have a cached image for this combination?
				res = getLocallyCachedImage(thisId);

				// have a look
				if (res == null) {
					// nope, better generate one
					res = CorePlugin
							.getImageFromRegistry(thirdPartyImageDescriptor);

					// now apply our decoration
					if (res != null) {
						// take a clone of the image
						res = new Image(Display.getCurrent(), res
								.getImageData());
						final int wid = res.getBounds().width;
						final int ht = res.getBounds().height;

						// create a graphics context for this new image
						final GC newGC = new GC(res);

						// set the color of our editable
						final org.eclipse.swt.graphics.Color thisColor = ColorHelper
								.getColor(thisW.getColor());
						newGC.setBackground(thisColor);

						// apply a color wash
						newGC.fillRectangle(0, 0, wid, ht);

						// and dispose the GC
						newGC.dispose();

						// and store the new image
						storeLocallyCachedImage(thisId, res);
					}
				}
			} else {
				// nope, better generate one
				res = CorePlugin
						.getImageFromRegistry(thirdPartyImageDescriptor);
			}
		} else {

			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;

			if (editable instanceof Layer)
				imageKey = "layer.gif";
			else if (editable instanceof GridPainter)
				imageKey = "grid.gif";
			else if (editable instanceof ScalePainter)
				imageKey = "scale.gif";
			else if (editable instanceof CoastPainter)
				imageKey = "coast.gif";
			else if (editable instanceof FeaturePainter)
				imageKey = "vpf.gif";

			res = PlatformUI.getWorkbench().getSharedImages()
					.getImage(imageKey);

			if (res == null) {
				// ok, try to get the image from our own registry
				res = CorePlugin.getImageFromRegistry(imageKey);
			}
		}

		return res;
	}

	/** generate a unique id for this item - taking its color into account
	 * 
	 * @param thisW the item to hash
	 * @return a unique string for this item type and color
	 */
	private String idFor(final Watchable thisW) {
		return thisW.getClass() + " " + thisW.getColor();
	}

	public Image getColumnImage(final Object element, final int columnIndex) {
		Image res = null;
		if (columnIndex == 0)
			res = getImage(element);
		else if (columnIndex == 1) {
			// hey - don't bother with this bit - just use the text-marker

			// sort out the visibility
			final EditableWrapper pw = (EditableWrapper) element;
			final Editable ed = pw.getEditable();
			if (ed instanceof Plottable) {
				final Plottable pl = (Plottable) ed;

				if (pl.getVisible())
					res = visImage;
				else
					res = hiddenImage;
			} else {
				res = nonVisibleImage;
			}
		}

		return res;
	}

	public String getColumnText(final Object element, final int columnIndex) {
		String res = null;
		if (columnIndex == 0)
			res = getText(element);


		return res;
	}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		final boolean res = true;

		return res;
	}

	public static interface ViewLabelImageHelper {
		/**
		 * produce an image icon for this object
		 * 
		 * @param subject
		 * @return
		 */
		public ImageDescriptor getImageFor(Editable subject);
	}

}