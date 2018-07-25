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
package org.mwc.cmap.media.views.images;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.media.utility.ImageUtils;
import org.mwc.cmap.media.utility.StringUtils;

public class ImagePanel extends Canvas
{

  private boolean stretch;
  private String currentImageFile;
  private String nextImageFile;
  private Image currentImage;
  private Image nextImage;
  private String loadedCurrentImage;
  private String loadedNextImage;

  public ImagePanel(final Composite parent)
  {
    super(parent, SWT.NONE);
    addPaintListener(new PaintListener()
    {
      private Point scaled;

      @Override
      public void paintControl(final PaintEvent e)
      {
        if (currentImage == null)
        {
          return;
        }
        final ImageData data = currentImage.getImageData();
        final Point size = getSize();
        e.gc.setAntialias(SWT.ON);
        if (!stretch)
        {
          scaled = ImageUtils.getScaledSize(data.width, data.height, size.x,
              size.y, scaled);
          e.gc.drawImage(currentImage, 0, 0, data.width, data.height, (size.x
              - scaled.x) / 2, (size.y - scaled.y) / 2, scaled.x, scaled.y);
        }
        else
        {
          e.gc.drawImage(currentImage, 0, 0, data.width, data.height, 0, 0,
              size.x, size.y);
        }

      }
    });
  }

  public void currentImagePassedToLoad()
  {
    loadedCurrentImage = currentImageFile;
  }

  @Override
  public void dispose()
  {
    super.dispose();
    if (nextImage != null)
    {
      nextImage.dispose();
    }
    if (currentImage != null)
    {
      currentImage.dispose();
    }
  }

  public Image getCurrentImage()
  {
    return currentImage;
  }

  public String getCurrentImageFile()
  {
    return currentImageFile;
  }

  public Image getNextImage()
  {
    return nextImage;
  }

  public String getNextImageFile()
  {
    return nextImageFile;
  }

  public boolean isStretchMode()
  {
    return stretch;
  }

  public void nextImagePassedToLoad()
  {
    loadedNextImage = nextImageFile;
  }

  public void setCurrentImage(final String currentImageFile, final Image image,
      final boolean repaintIfNull)
  {
    if (StringUtils.safeEquals(currentImageFile, this.currentImageFile)
        && currentImage != null)
    {
      return;
    }
    if (currentImage != null)
    {
      currentImage.dispose();
    }
    if (StringUtils.safeEquals(currentImageFile, this.nextImageFile)
        && image == null && nextImage != null)
    {
      this.currentImage = nextImage;
      loadedCurrentImage = loadedNextImage;
    }
    else
    {
      currentImage = image;
    }
    this.currentImageFile = currentImageFile;
    if (repaintIfNull || currentImage != null)
    {
      final Point size = getSize();
      redraw(0, 0, size.x, size.y, true);
    }
  }

  public void setNextImage(final String nextImageFile, final Image image)
  {
    if (StringUtils.safeEquals(nextImageFile, this.nextImageFile)
        && nextImage != null)
    {
      return;
    }
    if (this.nextImage != null && this.currentImage != this.nextImage)
    {
      this.nextImage.dispose();
    }
    this.nextImageFile = nextImageFile;
    nextImage = image;
  }

  public void setStretchMode(final boolean stretch)
  {
    this.stretch = stretch;
  }

  public boolean shouldLoadCurrentImage()
  {
    return currentImageFile != null && !(currentImageFile.equals(
        loadedCurrentImage) || currentImageFile.equals(loadedNextImage));
  }

  public boolean shouldLoadNextImage()
  {
    return nextImageFile != null && !nextImageFile.equals(loadedNextImage);
  }
}
