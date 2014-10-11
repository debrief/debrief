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
package org.mwc.cmap.media.views.images;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.io.IOUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.media.gallery.ImageGallery;
import org.mwc.cmap.media.utility.ImageUtils;
import org.mwc.cmap.media.utility.InterruptableInputStream;

@SuppressWarnings(
{ "rawtypes", "unchecked" })
public class ImageLoader implements Runnable
{
	private static ImageLoader instance;

	public static ImageLoader getInstance()
	{
		if (instance == null)
		{
			instance = new ImageLoader();
			Thread thread = new Thread(instance);
			thread.setDaemon(true);
			thread.setPriority(Thread.NORM_PRIORITY - 2);
			thread.start();
		}
		return instance;
	}

	private final Object loaderMutex = new Object();
	private LinkedList<LoaderEntry> labelsToLoad = new LinkedList<LoaderEntry>();

	private ImageLoader()
	{
	}

	public void load(String filename, Object imageMeta, ImageGallery gallery)
	{
		synchronized (loaderMutex)
		{
			labelsToLoad.add(new GalleryLoaderEntry(filename, imageMeta, gallery));
			loaderMutex.notify();
		}
	}

	public void load(ImagePanel panel)
	{
		synchronized (loaderMutex)
		{
			boolean load = false;
			if (panel.shouldLoadCurrentImage())
			{
				load = true;
				labelsToLoad.add(new ImagePanelLoader(panel.getCurrentImageFile(),
						panel));
				panel.currentImagePassedToLoad();
			}
			if (panel.shouldLoadNextImage())
			{
				load = true;
				labelsToLoad.add(new ImagePanelLoader(panel.getNextImageFile(), panel));
				panel.nextImagePassedToLoad();
			}
			if (load)
			{
				loaderMutex.notify();
				Thread.yield();
			}
		}
	}

	public void run()
	{
		while (true)
		{
			LoaderEntry toLoad;
			synchronized (loaderMutex)
			{
				if (labelsToLoad.isEmpty())
				{
					try
					{
						loaderMutex.wait();
					}
					catch (InterruptedException ex)
					{
						// ignore
					}
					continue;
				}
				toLoad = null;
				Iterator<LoaderEntry> iterator = labelsToLoad.iterator();
				while (iterator.hasNext())
				{
					LoaderEntry loadEntry = iterator.next();
					if (loadEntry.isVisible())
					{
						toLoad = loadEntry;
						iterator.remove();
						break;
					}
				}
				if (toLoad == null)
				{
					toLoad = labelsToLoad.poll();
				}
			}
			toLoad.load();
		}
	}

	private static interface LoaderEntry
	{
		void load();

		boolean isVisible();
	}

	private static class ImagePanelLoader implements LoaderEntry
	{
		ImagePanel panel;
		String name;
		boolean visible;

		public ImagePanelLoader(String name, ImagePanel panel)
		{
			this.name = name;
			this.panel = panel;
			this.visible = panel.isVisible();
		}

		@Override
		public boolean isVisible()
		{
			return visible;
		}

		@Override
		public void load() {
			if (name.equals(panel.getCurrentImageFile()) || name.equals(panel.getNextImageFile())) {
				InterruptableInputStream imageInput = null;
				BufferedInputStream iStream = null;
				try {
					iStream = new BufferedInputStream(new FileInputStream(name));
					imageInput = new InterruptableInputStream(iStream) {
						
						@Override
						protected void checkInterrupted() throws IOException {
							if (! name.equals(panel.getCurrentImageFile()) &&  
									! name.equals(panel.getNextImageFile())) {
								throw new IOException("interrupted");
							}							
						}
					};	
					final Image image = new Image(panel.getDisplay(), imageInput);
					Display.getDefault().asyncExec(new Runnable() {
					
						@Override
						public void run() {
							if (name.equals(panel.getCurrentImageFile())) {
								panel.setCurrentImage(name, image, false);
							} else if (name.equals(panel.getNextImageFile())) {
								panel.setNextImage(name, image);
							} else {
								image.dispose();
							}
						}
					});
				} catch (Exception ex) {
					if (imageInput == null || ! imageInput.wasInterrupted()) {
						ex.printStackTrace();
					}
				} finally {
					IOUtils.closeQuietly(imageInput);
					IOUtils.closeQuietly(iStream);
				}
			}
		}
	}

	private static class GalleryLoaderEntry implements LoaderEntry
	{
		public String name;
		public Object imageMeta;
		public ImageGallery gallery;
		public boolean visible;

		public GalleryLoaderEntry(String name, Object imageMeta,
				ImageGallery gallery)
		{
			this.name = name;
			this.imageMeta = imageMeta;
			this.gallery = gallery;
			visible = gallery.getMainComposite().isVisible();
		}

		@Override
		public boolean isVisible()
		{
			return visible;
		}

		@Override
		public void load()
		{
			try
			{
				if (!gallery.containsImage(imageMeta))
				{
					return;
				}
				if (gallery.getMainComposite().isDisposed())
				{
					return;
				}
				Image image = new Image(gallery.getMainComposite().getDisplay(), name);
				ImageData imageData = image.getImageData();
				Point scaledSize = ImageUtils.getScaledSize(imageData.width,
						imageData.height, gallery.getThumbnailWidth(),
						gallery.getThumbnailHeight());
				if (gallery.getMainComposite().isDisposed())
				{
					return;
				}
				final Image rescaled = new Image(gallery.getMainComposite()
						.getDisplay(), scaledSize.x, scaledSize.y);
				final Image stretched = new Image(gallery.getMainComposite()
						.getDisplay(), gallery.getThumbnailWidth(),
						gallery.getThumbnailHeight());
				GC gc = new GC(rescaled);
				gc.setAntialias(SWT.ON);
				gc.drawImage(image, 0, 0, imageData.width, imageData.height, 0, 0,
						scaledSize.x, scaledSize.y);
				gc.dispose();
				gc = new GC(stretched);
				gc.setAntialias(SWT.ON);
				gc.drawImage(image, 0, 0, imageData.width, imageData.height, 0, 0,
						gallery.getThumbnailWidth(), gallery.getThumbnailHeight());
				gc.dispose();

				image.dispose();
				Display.getDefault().asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						if (gallery.containsImage(imageMeta))
						{
							gallery.addImage(imageMeta, new ThumbnailPackage(rescaled,
									stretched));
						}
					}
				});
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

}