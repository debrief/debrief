/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.cmap.media.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.services.IDisposable;
import org.mwc.cmap.media.utility.ImageUtils;

public class ShowImageDialog implements IDisposable {

	private Shell dialog;
	private Canvas canvas;
	private Image image;
	private final boolean stretch;

	public ShowImageDialog(final Shell parent, final String imageName, final boolean stretch) {
		initUI(parent, imageName);
		initSizePosition();
		initPainter();
		this.stretch = stretch;
		dialog.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(final DisposeEvent e) {
				dispose();
			}
		});
	}

	@Override
	public void dispose() {
		dialog.dispose();
		image.dispose();
	}

	public Shell getDialog() {
		return dialog;
	}

	private void initPainter() {
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				final Point size = canvas.getSize();
				final ImageData data = image.getImageData();
				final int old = e.gc.getAntialias();
				e.gc.setAntialias(SWT.ON);
				if (!stretch) {
					final Point drawSize = ImageUtils.getScaledSize(data.width, data.height, size.x, size.y);
					final int drawX = (size.x - drawSize.x) / 2;
					final int drawY = (size.y - drawSize.y) / 2;
					e.gc.setBackground(e.gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
					if (drawX == 0) {
						e.gc.fillRectangle(0, 0, size.x, drawY);
						e.gc.fillRectangle(0, drawSize.y + drawY, size.x, size.y - drawSize.y - drawY);
					}
					if (drawY == 0) {
						e.gc.fillRectangle(0, 0, drawX, size.y);
						e.gc.fillRectangle(drawSize.x + drawX, 0, size.x - drawSize.x - drawX, size.y);
					}
					e.gc.drawImage(image, 0, 0, data.width, data.height, drawX, drawY, drawSize.x, drawSize.y);
				} else {
					e.gc.drawImage(image, 0, 0, data.width, data.height, 0, 0, size.x, size.y);
				}
				e.gc.setAntialias(old);
			}
		});
	}

	private void initSizePosition() {
		final Rectangle maximumArea = dialog.getMonitor().getClientArea();
		maximumArea.width -= 20;
		maximumArea.height -= 50;

		int initialWidth, initialHeight;
		final ImageData data = image.getImageData();
		if (data.width <= maximumArea.width && data.height <= maximumArea.height) {
			initialWidth = data.width;
			initialHeight = data.height;
		} else {
			final Point scaled = ImageUtils.getScaledSize(data.width, data.height, maximumArea.width,
					maximumArea.height);
			initialWidth = scaled.x;
			initialHeight = scaled.y;
		}
		dialog.setLocation(maximumArea.x + (maximumArea.width - initialWidth) / 2,
				maximumArea.y + (maximumArea.height - initialHeight) / 2);
		final GridData layoutData = (GridData) canvas.getLayoutData();
		layoutData.widthHint = initialWidth;
		layoutData.heightHint = initialHeight;
	}

	private void initUI(final Shell parent, final String imageName) {
		dialog = new Shell(parent, SWT.CENTER | SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.RESIZE);
		image = new Image(dialog.getDisplay(), imageName);
		final GridLayout layout = new GridLayout(1, false);
		layout.marginBottom = layout.marginHeight = layout.marginLeft = layout.marginRight = layout.marginTop = layout.marginWidth = layout.horizontalSpacing = layout.verticalSpacing = 0;
		dialog.setLayout(layout);
		dialog.setText(imageName);

		canvas = new Canvas(dialog, SWT.NO_BACKGROUND);
		final GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.verticalAlignment = SWT.FILL;
		canvas.setLayoutData(layoutData);
	}

	public void show() {
		dialog.pack();
		dialog.open();
	}
}
