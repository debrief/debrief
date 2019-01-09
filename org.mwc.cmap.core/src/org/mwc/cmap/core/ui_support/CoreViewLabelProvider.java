/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.core.ui_support;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.ColorHelper;
import org.mwc.cmap.core.property_support.EditableWrapper;

import Debrief.GUI.CoreImageHelper;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.ColoredWatchable;
import MWC.GenericData.NonColoredWatchable;

public class CoreViewLabelProvider extends LabelProvider implements
    ITableLabelProvider
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

  // private static ImageRegistry _imageRegistry;

  private Map<String, Image> _imageMap = new HashMap<String, Image>();

  /**
	 * 
	 */

  /**
	 */
  public CoreViewLabelProvider()
  {
    // ok, retrieve the images from our own registry
    visImage = CorePlugin.getImageFromRegistry("show.png");
    hiddenImage = CorePlugin.getImageFromRegistry("hide.png");
    nonVisibleImage = CorePlugin.getImageFromRegistry("desktop.png");
  }

  public static void addImageHelper(final ViewLabelImageHelper helper)
  {
    if (imageHelpers == null)
      imageHelpers = new Vector<ViewLabelImageHelper>(1, 1);

    // put the new helper at the start. The "core" helpers tend to get added
    // first,
    // meaning they get selected before the more specific ones. So, insert the
    // helpers
    // at the start, so the specific ones get selected first.
    imageHelpers.insertElementAt(helper, 0);
  }

  public static void removeImageHelper(final ViewLabelImageHelper helper)
  {
    imageHelpers.remove(helper);
  }

  @Override
  public String getText(final Object obj)
  {
    final EditableWrapper pw = (EditableWrapper) obj;
    return pw.getEditable().toString();
  }

  protected Image getLocallyCachedImage(final String index)
  {
    return _imageMap.get(index);
  }

  /**
   * remember this image
   * 
   * @param index
   *          how to retrieve the image
   * @param image
   *          the image to store
   */
  protected void storeLocallyCachedImage(final String index, final Image image)
  {
    _imageMap.put(index, image);
  }

  public void disposeImages()
  {
    Collection<Image> images = _imageMap.values();
    for (Image image : images)
    {
      if (image != null && !image.isDisposed())
      {
        image.dispose();
      }
    }
    _imageMap.clear();
  }

  /**
   * ditch the cache for the specified items, so we generate new ones as required
   */
  public void resetCacheFor(TreeViewer tree)
  {
    if (tree != null && _imageMap != null && _imageMap.values().size() > 100)
    {
      for (String id : _imageMap.keySet())
      {
        Image image = _imageMap.get(id);
        if (image != null && !image.isDisposed())
        {
          image.dispose();
        }
      }
      _imageMap.clear();
      tree.refresh(true);
    }

  }

  @Override
  public Image getImage(final Object subject)
  {
    final EditableWrapper item = (EditableWrapper) subject;
    final Editable editable = item.getEditable();
    Image res = null;

    // try our helpers first
    ImageDescriptor thirdPartyImageDescriptor = null;
    if (imageHelpers != null)
    {

      // take a copy of the images listing, in case we receive a new
      // helper whilst we're looping through
      final Vector<ViewLabelImageHelper> spareHelpers =
          new Vector<ViewLabelImageHelper>(imageHelpers);

      // ok, now go for it.
      for (final Iterator<ViewLabelImageHelper> iter = spareHelpers.iterator(); iter
          .hasNext();)
      {
        final ViewLabelImageHelper helper = iter.next();
        thirdPartyImageDescriptor = helper.getImageFor(editable);
        if (thirdPartyImageDescriptor != null)
        {
          break;
        }
      }
    }

    if (thirdPartyImageDescriptor != null)
    {
      // is this a special case that doesn't want color?
      if (!(editable instanceof NonColoredWatchable))
      {
        // right, is this something that we apply color to?
        if (editable instanceof ColoredWatchable)
        {
          final ColoredWatchable thisW = (ColoredWatchable) editable;

          // sort out the color index. Note: we incude the image descriptor, since
          // some elements (tracks) can provide different icons.
          final Color color = thisW.getColor();
          final String thisId =
              idFor(thisW, color, thirdPartyImageDescriptor.toString());

          // do we have a cached image for this combination?
          res = getLocallyCachedImage(thisId);

          // have a look
          if (res == null || res.isDisposed())
          {
            // nope, better generate one
            res = CorePlugin.getImageFromRegistry(thirdPartyImageDescriptor);

            // now apply our decoration
            if (res != null)
            {
              // take a clone of the image
              res = new Image(Display.getCurrent(), res.getImageData());
              final int wid = res.getBounds().width;
              final int ht = res.getBounds().height;

              // create a graphics context for this new image
              final GC newGC = new GC(res);

              // set the color of our editable
              Color jColor = color;
              if (jColor == null)
              {
                // ok, declare a warning
                CorePlugin.logError(Status.WARNING, "Color not returned for:"
                    + thisW, null);

                // give it a color for now
                jColor = Color.gray;
              }
              final org.eclipse.swt.graphics.Color thisColor =
                  ColorHelper.getColor(jColor);

              
              
              boolean foundInfill = false;
        	  {// infill color 
        		newGC.setForeground(thisColor);  
              	ImageData data = CorePlugin.getImageFromRegistry(thirdPartyImageDescriptor).getImageData();
					PaletteData palette = data.palette;
              	if (palette.isDirect) {
              		for (int y = 0; y < data.height; y++) {
              			for (int x = 0; x < data.width; x++) {
              				int pixel = data.getPixel(x, y);
              				RGB rgb = palette.getRGB(pixel);
              				//match blue shades 
              				if((rgb.red<40 && rgb.red>25 ) && (rgb.green<65 && rgb.green>45 ) &&  (rgb.blue<105 && rgb.blue>85 ))
              				{
              					foundInfill = true;
              					newGC.drawPoint(x, y);
              				}
//              				else
//              				{
//              				System.out.println("X="+x+" : Y="+y+" :"+rgb);
//              				}
              			    
              			}
              		}
              	} 

              }
        	  //no infill found fill full color 
        	  if(!foundInfill)
        	  {
        		  newGC.setBackground(thisColor);

                  // apply a color wash
                  // Linux/Mac doesn't fill transparent color properly. The
                  // following is workaround.
                  if (Platform.OS_LINUX.equals(Platform.getOS())
                      || Platform.OS_MACOSX.equals(Platform.getOS()))
                  {
                    ImageData data = res.getImageData();
                    // we recognize two transparency types
                    if (data.getTransparencyType() == SWT.TRANSPARENCY_PIXEL
                        || data.getTransparencyType() == SWT.TRANSPARENCY_ALPHA)
                    {
                      for (int i = 0; i < wid; i++)
                      {
                        for (int j = 0; j < ht; j++)
                        {
                          if (data.getTransparencyType() == SWT.TRANSPARENCY_PIXEL)
                          {
                            if (data.getPixel(i, j) > 0)
                            {
                              newGC.fillRectangle(i, j, 1, 1);
                            }
                          }
                          else if (data.getTransparencyType() == SWT.TRANSPARENCY_ALPHA)
                          {
                            if (data.getAlpha(i, j) > 0)
                            {
                              newGC.fillRectangle(i, j, 1, 1);
                            }
                          }
                        }
                      }
                    }
                    else
                    {
                      // display solid color icon
                      newGC.fillRectangle(0, 0, wid, ht);
                    }
                  }
                  else
                  {
                	  // Windows
                		  newGC.fillRectangle(0, 0, wid, ht);
                  }
        	  }
              
              
              

              // and dispose the GC
              newGC.dispose();

              // and store the new image
              storeLocallyCachedImage(thisId, res);
            }
          }
        }
      }
      else
      {
        // nope, better generate one
        res = CorePlugin.getImageFromRegistry(thirdPartyImageDescriptor);
      }
    }
    else
    {
      String imageKey = CoreImageHelper.getImageKeyFor(editable);

      res = PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);

      if (res == null)
      {
        // ok, try to get the image from our own registry
        res = CorePlugin.getImageFromRegistry(imageKey);
      }
    }

    // are we still trying to find it?
    if (res == null && thirdPartyImageDescriptor != null)
    {
      res = CorePlugin.getImageFromRegistry(thirdPartyImageDescriptor);
    }

    return res;
  }

  /**
   * generate a unique id for this item - taking its color into account
   * 
   * @param thisW
   *          the item to hash
   * @param thirdPartyImageDescriptor
   * @return a unique string for this item type and color
   */
  private String idFor(final ColoredWatchable thisW, Color color,
      String thirdPartyImageDescriptor)
  {
    // optimise the performance of this string building
    StringBuffer buff = new StringBuffer();
    buff.append(thisW.getClass());
    buff.append(" ");
    buff.append(color.toString());
    buff.append(" ");
    buff.append(thirdPartyImageDescriptor);
    return buff.toString();
  }

  public Image getColumnImage(final Object element, final int columnIndex)
  {
    Image res = null;
    if (columnIndex == 0)
      res = getImage(element);
    else if (columnIndex == 1)
    {
      // hey - don't bother with this bit - just use the text-marker

      // sort out the visibility
      final EditableWrapper pw = (EditableWrapper) element;
      final Editable ed = pw.getEditable();
      if (ed instanceof Plottable)
      {
        final Plottable pl = (Plottable) ed;

        if (pl.getVisible())
          res = visImage;
        else
          res = hiddenImage;
      }
      else
      {
        // if it's not a plottable then we don't have the concept of visibility,
        // so don't show an image
        res = null;
      }
    }

    return res;
  }

  public String getColumnText(final Object element, final int columnIndex)
  {
    String res = null;
    if (columnIndex == 0)
      res = getText(element);

    return res;
  }

  @Override
  public boolean isLabelProperty(final Object element, final String property)
  {
    final boolean res = true;

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
    public ImageDescriptor getImageFor(Editable subject);
  }

}
