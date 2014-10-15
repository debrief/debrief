/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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

public class ImagePanel extends Canvas {
	
	private boolean stretch;
	private String currentImageFile;
	private String nextImageFile;
	private Image currentImage;
	private Image nextImage;
	private String loadedCurrentImage;
	private String loadedNextImage;
	
	public ImagePanel(Composite parent) {
		super(parent, SWT.NONE);
		addPaintListener(new PaintListener() {
			private Point scaled;
			
			@Override
			public void paintControl(PaintEvent e) {
				if (currentImage == null) {
					return;
				}
				ImageData data = currentImage.getImageData();
				Point size = getSize();
				e.gc.setAntialias(SWT.ON);				
				if (! stretch) {
					scaled = ImageUtils.getScaledSize(data.width, data.height, size.x, size.y, scaled);
					e.gc.drawImage(
							currentImage,
							0,
							0, 
							data.width,
							data.height,
							(size.x - scaled.x) / 2,
							(size.y - scaled.y) / 2,
							scaled.x,
							scaled.y							
					);
				} else {
					e.gc.drawImage(
							currentImage,
							0,
							0, 
							data.width,
							data.height,
							0,
							0,
							size.x,
							size.y
					);					
				}
				
			}
		});
	}
	
	public void setCurrentImage(String currentImageFile, Image image, boolean repaintIfNull) {
		if (StringUtils.safeEquals(currentImageFile, this.currentImageFile) && currentImage != null) {
			return;
		}
		if (currentImage != null) {
			currentImage.dispose();
		}		
		if (StringUtils.safeEquals(currentImageFile, this.nextImageFile) && image == null && nextImage != null) {
			this.currentImage = nextImage;
			loadedCurrentImage = loadedNextImage;
		} else {
			currentImage = image;
		}
		this.currentImageFile = currentImageFile;
		if (repaintIfNull || currentImage != null) {
			Point size = getSize();
			redraw(0, 0, size.x, size.y, true);
		}
	}
	
	public void setNextImage(String nextImageFile, Image image) {
		if (StringUtils.safeEquals(nextImageFile, this.nextImageFile) && nextImage != null) {
			return;
		}		
		if (this.nextImage != null && this.currentImage != this.nextImage) {
			this.nextImage.dispose();
		}
		this.nextImageFile = nextImageFile;		
		nextImage = image;
	}

	public boolean isStretchMode() {
		return stretch;
	}

	public void setStretchMode(boolean stretch) {
		this.stretch = stretch;
	}

	public String getCurrentImageFile() {
		return currentImageFile;
	}

	public String getNextImageFile() {
		return nextImageFile;
	}

	public Image getCurrentImage() {
		return currentImage;
	}

	public Image getNextImage() {
		return nextImage;
	}
	
	public boolean shouldLoadCurrentImage() {		
		return currentImageFile != null && ! (currentImageFile.equals(loadedCurrentImage) || currentImageFile.equals(loadedNextImage));
	}

	public boolean shouldLoadNextImage() {
		return nextImageFile != null && ! nextImageFile.equals(loadedNextImage);
	}
	
	public void currentImagePassedToLoad() {
		loadedCurrentImage = currentImageFile;
	}
	
	public void nextImagePassedToLoad() {
		loadedNextImage = nextImageFile;
	}	

	@Override
	public void dispose() {
		super.dispose();		
		if (nextImage != null) {
			nextImage.dispose();
		}
		if (currentImage != null) {
			currentImage.dispose();
		}		
	}	
}
