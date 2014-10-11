/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.media.gallery;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.services.IDisposable;
import org.mwc.cmap.media.PlanetmayoImages;

public class BackgroundImages implements IDisposable {
	
	private ImageGallery<?, ?> gallery;
	private Image transparent;
	private Image highlightedSource;
	private Image selectedSource;	
	private Map<Point, Image> highlightedImages = new HashMap<Point, Image>();
	private Map<Point, Image> selectedImages = new HashMap<Point, Image>();
	
	public BackgroundImages(ImageGallery<?, ?> gallery) {
		this.gallery = gallery;
		transparent = PlanetmayoImages.TRANSPARENT.getImage().createImage();
		highlightedSource = PlanetmayoImages.HIGHLIGHTED_IMAGE.getImage().createImage();
		selectedSource = PlanetmayoImages.SELECTED_IMAGE.getImage().createImage();
	}

	public Image getTransparent() {
		return transparent;
	}
	
	private Image createFromSource(Point size, Image source) {
		Image newImage = new Image(gallery.getMainComposite().getDisplay(), size.x, size.y);
		GC gc = new GC(newImage);
		gc.drawImage(source, 0, 0, 3, 3, 0, 0, 3, 3);
		gc.drawImage(source, 0, 112, 3, 3, 0, size.y - 3, 3, 3);
		gc.drawImage(source, 97, 0, 3, 3, size.x - 3, 0, 3, 3);
		gc.drawImage(source, 97, 112, 3, 3, size.x - 3, size.y - 3, 3, 3);
		gc.drawImage(source, 0, 3, 3, 109, 0, 3, 3, size.y - 6);
		gc.drawImage(source, 97, 3, 3, 109, size.x - 3, 3, 3, size.y - 6);
		gc.drawImage(source, 3, 0, 94, 3, 3, 0, size.x - 6, 3);
		gc.drawImage(source, 3, 112, 94, 3, 3, size.y - 3, size.x - 6, 3);
		gc.drawImage(source, 3, 3, 94, 109, 3, 3, size.x - 6, size.y - 6);
		gc.dispose();
		return newImage;
	}
	
	public Image getHighlightedImage(Point size) {
		if (highlightedImages.containsKey(size)) {
			return highlightedImages.get(size);
		}
		Image newImage = createFromSource(size, highlightedSource);
		highlightedImages.put(size, newImage);
		return newImage;
	}
	
	public Image getSelectedImage(Point size) {
		if (selectedImages.containsKey(size)) {
			return selectedImages.get(size);
		}
		Image newImage = createFromSource(size, selectedSource);
		selectedImages.put(size, newImage);
		return newImage;
	}	

	@Override
	public void dispose() {
		transparent.dispose();
		highlightedSource.dispose();
		selectedSource.dispose();
		for (Image image : highlightedImages.values()) {
			image.dispose();
		}
		for (Image image : selectedImages.values()) {
			image.dispose();
		}
	}	
}
