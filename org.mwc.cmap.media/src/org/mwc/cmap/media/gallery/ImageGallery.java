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
package org.mwc.cmap.media.gallery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.services.IDisposable;

public class ImageGallery<T, I> implements IDisposable {
	public static final int DEFAULT_WIDTH = 90;
	public static final int DEFAULT_HEIGHT = 90;
	
	private static final int IMAGE_BORDER_MARGIN = 10;
	private static final int LABELS_SPACING = 0;
	private static final int TABLE_HORIZONTAL_MARGIN = 5;
	private static final int TEXT_HEIGHT = 15;	
	
	private ImageGalleryElementsBuilder<T, I> elementsBuilder = new DefaultImageGalleryElementsBuilder<T, I>();
	
	private ScrolledComposite mainComposite;
	private Composite imagesTable;
	private ImageLabel highlitedLabel;
	private ImageLabel selectedLabel;
	private Image defaultImage;
	private int thumbnailWidth;
	private int thumbnailHeight;
	private Map<T, ImageLabel> labels;	
	
	private final BackgroundImages backgroundImages;
	private final Set<MouseListener> elementMouseListeners;
	private final Set<MouseMoveListener> elementMouseMoveListeners;
	
	public ImageGallery(Composite parent, int thumbnailWidth, int thumbnailHeight) {
		this.thumbnailWidth = thumbnailWidth;
		this.thumbnailHeight = thumbnailHeight;
		this.labels = new HashMap<T, ImageLabel>();
		backgroundImages = new BackgroundImages(this);
		elementMouseListeners = new HashSet<MouseListener>();
		elementMouseMoveListeners = new HashSet<MouseMoveListener>();
		initUI(parent);
	}
	
	public ImageGallery(Composite parent) {
		this(parent, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private void initUI(Composite parent) {
		mainComposite = new ScrolledComposite(parent, SWT.V_SCROLL) {

			@Override
			public void layout(boolean a) {
				super.layout(a);
				Control[] children = imagesTable.getChildren();
				int columns = ((GridLayout) imagesTable.getLayout()).numColumns;
				int rows = children.length / columns + (children.length % columns != 0 ? 1 : 0);
				int minHeight = 0;
				for (int row = 0; row < rows; row++) {
					int max = 0;
					for (int col = 0; col < columns; col++) {
						int index = row * columns + col;
						if (index >= children.length) {
							break;
						}
						max = Math.max(children[index].getSize().y, max);
					}
					minHeight += max + LABELS_SPACING;
				}
				mainComposite.setMinHeight(minHeight + 10);				
			}			
		};
		imagesTable = new Composite(mainComposite, SWT.NONE);
		final GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = LABELS_SPACING;
		layout.verticalSpacing = LABELS_SPACING;
		layout.marginLeft = TABLE_HORIZONTAL_MARGIN;
		layout.marginRight = TABLE_HORIZONTAL_MARGIN;
		imagesTable.setLayout(layout);
		imagesTable.setBackground(imagesTable.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		imagesTable.addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent event) {
				if (highlitedLabel != null) {
					highlitedLabel.setHighlight(false);
				}
				highlitedLabel = null;
			}
		});
		Listener resizeListener = new Listener() {
			
			@Override
			public void handleEvent(Event arg) {
				Point size = imagesTable.getSize();
				int width = size.x;
				int mod = (width - TABLE_HORIZONTAL_MARGIN * 2) % (thumbnailWidth + IMAGE_BORDER_MARGIN + LABELS_SPACING);
				int count = (width - TABLE_HORIZONTAL_MARGIN * 2) / (thumbnailWidth + IMAGE_BORDER_MARGIN + LABELS_SPACING) + (mod >= thumbnailWidth + IMAGE_BORDER_MARGIN + (LABELS_SPACING / 2) ? 1 : 0);
				mod = (width - count * (thumbnailWidth + IMAGE_BORDER_MARGIN) + TABLE_HORIZONTAL_MARGIN * 2);
				count = count > 0 ? count : 1;
				layout.numColumns = count;
				layout.horizontalSpacing = count > 1 ? mod / count - 3 : LABELS_SPACING;
				
				int labels = imagesTable.getChildren().length;
				labels = labels / count + (labels % count == 0 ? 0 : 1);
				mainComposite.layout();
			}
		};
		mainComposite.addListener(SWT.Resize, resizeListener);
		imagesTable.addListener(SWT.Resize, resizeListener);
		
		mainComposite.setContent(imagesTable);
		mainComposite.setMinWidth(thumbnailWidth + IMAGE_BORDER_MARGIN);

		mainComposite.setExpandHorizontal(true);
		mainComposite.setExpandVertical(true);
	}	
	
	public void addImage(T imageMeta, I image) {
		if (labels.containsKey(imageMeta)) {
			labels.get(imageMeta).setImage(image);
			return;
		}
		ImageLabel label = new ImageLabel(imagesTable);
		label.setImageMeta(imageMeta);
		label.setImage(image);
		labels.put(imageMeta, label);
		imagesTable.layout();
		mainComposite.layout();
	}
	
	public boolean containsImage(T imageMeta) {
		return labels.containsKey(imageMeta);
	}
	
	public void removeImage(T imageMeta) {
		if (labels.containsKey(imageMeta)) {
			ImageLabel label = labels.get(imageMeta);
			if (highlitedLabel == label) {
				highlitedLabel = null;
			}
			if (selectedLabel == label) {
				selectedLabel = null;
			}			
			label.dispose();
			labels.remove(imageMeta);
		}
	}
	
	public void removeAll() {
		highlitedLabel = null;
		selectedLabel = null;
		for (ImageLabel label : labels.values()) {
			label.dispose();
		}		
		labels.clear();
	}
	
	public int size() {
		return labels.size();
	}
	
	public void selectImage(T imageMeta, boolean makeSelectedVisible) {
		if (labels.containsKey(imageMeta)) {
			ImageLabel oldSelected = selectedLabel;
			selectedLabel = labels.get(imageMeta);
			if (selectedLabel != oldSelected) {
				selectedLabel.setSelected(true);
				if (oldSelected != null) {				
					if (highlitedLabel == oldSelected) {
						oldSelected.setHighlight(true);
					} else {
						oldSelected.setSelected(false);
					}
				}
				if (makeSelectedVisible) {
					mainComposite.setOrigin(0, selectedLabel.getComposite().getBounds().y);
				}
			}			
		}
	}
	
	public T getSelectedImage() {
		if (selectedLabel == null) {
			return null;
		}
		return selectedLabel.getImageMeta();
	}
	
	public void layout() {
		mainComposite.layout();
	}
	
	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	public void setThumbnailWidth(int thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
		mainComposite.layout();
	}

	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

	public void setThumbnailHeight(int thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
		mainComposite.layout();
	}
	
	public void setThumbnailSize(int thumbnailWidth, int thumbnailHeight) {
		this.thumbnailWidth = thumbnailWidth;
		this.thumbnailHeight = thumbnailHeight;
		mainComposite.layout();
	}
	
	public ScrolledComposite getMainComposite() {
		return mainComposite;
	}

	public Composite getImagesTable() {
		return imagesTable;
	}
	
	public ImageGalleryElementsBuilder<T, I> getLabelBuilder() {
		return elementsBuilder;
	}

	public void setLabelBuilder(ImageGalleryElementsBuilder<T, I> labelBuilder) {
		this.elementsBuilder = labelBuilder;
	}
	
	public Image getDefaultImage() {
		return defaultImage;
	}

	public void setDefaultImage(Image defaultImage) {
		if (this.defaultImage != null) {
			this.defaultImage.dispose();
		}
		this.defaultImage = defaultImage;
	}
	
	public void addElementMouseListener(MouseListener listener) {
		elementMouseListeners.add(listener);
	}
	
	public void removeElementMouseListener(MouseListener listener) {
		elementMouseListeners.remove(listener);
	}
	
	public void addElementMouseMoveListener(MouseMoveListener listener) {
		elementMouseMoveListeners.add(listener);
	}
	
	public void removeElementMouseMoveListener(MouseMoveListener listener) {
		elementMouseMoveListeners.remove(listener);
	}	

	public void dispose() {
		backgroundImages.dispose();
		imagesTable.dispose();
		mainComposite.dispose();
		if (defaultImage != null) {
			defaultImage.dispose();
		}
		for (ImageLabel label : labels.values()) {
			label.dispose();
		}
	}
	
	public class ImageLabel implements MouseMoveListener, MouseListener, IDisposable {
		private Composite composite;
		private Canvas imageLabel;
		private CLabel textlabel; 
		
		private T imageMeta;
		private I loadedImage;

		public ImageLabel(Composite parent) {
			composite = new Composite(parent, SWT.NONE);
			GridData compositeData = new GridData(thumbnailWidth + IMAGE_BORDER_MARGIN, thumbnailHeight + IMAGE_BORDER_MARGIN);
			composite.setLayoutData(compositeData);
			composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
			composite.setBackgroundImage(backgroundImages.getTransparent());
			GridLayout layout = new GridLayout(1, false);
			layout.marginBottom = 0;
			layout.marginHeight = 0;
			layout.marginLeft = 0;
			layout.marginRight = 0;
			layout.marginTop = 0;
			layout.marginWidth = 0;
			layout.verticalSpacing = 0;
			composite.setLayout(layout);
			
			imageLabel = new Canvas(composite, SWT.CENTER);
			GridData data = new GridData(thumbnailWidth + IMAGE_BORDER_MARGIN, thumbnailHeight + IMAGE_BORDER_MARGIN);
			imageLabel.setLayoutData(data);
			imageLabel.addPaintListener(new PaintListener() {
				
				@Override
				public void paintControl(PaintEvent e) {
					Image drawImage = defaultImage;
					if (loadedImage != null) {
						drawImage = elementsBuilder.buildImage(loadedImage);
					}
					if (drawImage != null) {
						ImageData data = drawImage.getImageData();
						int widthPadding = (thumbnailWidth + IMAGE_BORDER_MARGIN - data.width) / 2;
						int heightPadding = (thumbnailHeight + IMAGE_BORDER_MARGIN - data.height) / 2;
						e.gc.drawImage(drawImage, widthPadding, heightPadding);
					}
				}
			});
			
			textlabel = new CLabel(composite, SWT.CENTER);
			textlabel.setAlignment(SWT.CENTER);
			data = new GridData(thumbnailWidth + IMAGE_BORDER_MARGIN, TEXT_HEIGHT);	
			data.horizontalIndent = 0;
			data.horizontalSpan = 0;
			textlabel.setLayoutData(data);
			textlabel.setMargins(0, 0, 0, 0);
			
			composite.addMouseMoveListener(this);
			imageLabel.addMouseMoveListener(this);
			textlabel.addMouseMoveListener(this);
			composite.addMouseListener(this);
			imageLabel.addMouseListener(this);
			textlabel.addMouseListener(this);			
		}
		
		@Override
		public void mouseMove(MouseEvent event) {
			if (highlitedLabel != this) {
				if (highlitedLabel != null) {
					highlitedLabel.setHighlight(false);
				}
				highlitedLabel = this;
				highlitedLabel.setHighlight(true);
			}
			event.data = this;
			for (MouseMoveListener listener : elementMouseMoveListeners) {
				event.data = this;
				listener.mouseMove(event);
			}
		}		
		
		@Override
		public void mouseDoubleClick(MouseEvent event) {
			event.data = this;
			for (MouseListener listener : elementMouseListeners) {
				listener.mouseDoubleClick(event);
			}
		}

		@Override
		public void mouseDown(MouseEvent event) {
			event.data = this;			
			for (MouseListener listener : elementMouseListeners) {
				listener.mouseDown(event);
			}
		}

		@Override
		public void mouseUp(MouseEvent event) {
			if (selectedLabel != this) {
				if (selectedLabel != null) {
					selectedLabel.setSelected(false);
				}
				selectedLabel = this;
				selectedLabel.setSelected(true);
			}
			event.data = this;			
			for (MouseListener listener : elementMouseListeners) {
				listener.mouseUp(event);
			}
		}

		public CLabel getTextLabel() {
			return textlabel;
		}
		
		public Canvas getImageLabel() {
			return imageLabel;
		}	
		
		public Composite getComposite() {
			return composite;
		}		
		
		public void setHighlight(boolean highlight) {
			if (selectedLabel == this) {
				return;
			}
			if (highlight) {
				composite.setBackgroundImage(backgroundImages.getHighlightedImage(composite.getSize()));
			} else {
				composite.setBackgroundImage(backgroundImages.getTransparent());
			}
			composite.redraw();
		}
		
		public void setSelected(boolean select) {
			if (select) {
				composite.setBackgroundImage(backgroundImages.getSelectedImage(composite.getSize()));
			} else {
				composite.setBackgroundImage(backgroundImages.getTransparent());
			}
			composite.redraw();
		}		
		
		public T getImageMeta() {
			return imageMeta;
		}
		
		public void setImageMeta(T imageMeta) {
			this.imageMeta = imageMeta;
			String imageText;
			imageText = elementsBuilder.buildLabel(imageMeta);
			
			String[] splitted = imageText.split("\\s+");
			int[] width = new int[splitted.length];
			
			GC gc = new GC(textlabel);
			int spaceWidth = gc.getCharWidth(' ');
			for (int i = 0; i < splitted.length; i++) {
				String word = splitted[i];
				for (int j = 0; j < word.length(); j++) {
					width[i] += gc.getCharWidth(word.charAt(j));
				}				
			}
			final int lineWidth = thumbnailWidth;
			int remainingWidth = lineWidth;
			StringBuilder labelText = new StringBuilder();
			for (int i = 0; i < splitted.length; i++) {
				int checkWidth = width[i] + (remainingWidth == lineWidth ? 0 : spaceWidth);
				if (remainingWidth >= checkWidth) {
					if (remainingWidth != lineWidth) {
						labelText.append(' ');
					}
					labelText.append(splitted[i]);
					remainingWidth -= checkWidth;
					continue;
				}
				if (width[i] <= lineWidth) {
					labelText.append('\n');
					labelText.append(splitted[i]);
					remainingWidth = lineWidth - width[i];
					continue;
				}
				if (remainingWidth >= spaceWidth) {
					labelText.append(' ');
					remainingWidth -= spaceWidth;
				} else {
					labelText.append('\n');
					remainingWidth = lineWidth;
				}
				String word = splitted[i];
				for (int j = 0; j < word.length(); j++) {
					int charWidth = gc.getCharWidth(word.charAt(j));
					if (remainingWidth < charWidth) {
						labelText.append('\n');
						remainingWidth = lineWidth;
					}
					labelText.append(word.charAt(j));
					remainingWidth -= charWidth;
				}
			}			
			gc.dispose();
			textlabel.setText(labelText.toString());
			Point size = textlabel.computeSize(thumbnailWidth + IMAGE_BORDER_MARGIN, SWT.DEFAULT);
			((GridData) textlabel.getLayoutData()).heightHint = size.y;
			((GridData)composite.getLayoutData()).heightHint = thumbnailHeight + IMAGE_BORDER_MARGIN + size.y + 2;
			composite.layout();
		}
		
		public void setImage(I loadedImage) {
			if (this.loadedImage == loadedImage) {
				return;
			}
			if (this.loadedImage != null) {
				elementsBuilder.disposeImage(this.loadedImage);
			}
			this.loadedImage = loadedImage;
			if (! imageLabel.isDisposed() && imageLabel.isVisible()) {
				imageLabel.redraw();
			}
		}
		
		public I getImage() {
			return loadedImage;
		}
		
		public void dispose() {
			composite.dispose();
			if (loadedImage != null) {
				elementsBuilder.disposeImage(this.loadedImage);
			}
			elementsBuilder.disposeMeta(this.imageMeta);
			imageLabel.dispose();
			textlabel.dispose();
		}
	}
}
