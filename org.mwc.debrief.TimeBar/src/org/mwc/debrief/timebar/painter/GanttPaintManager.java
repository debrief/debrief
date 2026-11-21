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

package org.mwc.debrief.timebar.painter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.nebula.widgets.ganttchart.AbstractPaintManager;
import org.eclipse.nebula.widgets.ganttchart.AdvancedTooltip;
import org.eclipse.nebula.widgets.ganttchart.DefaultSettings;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttComposite;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttEventListenerAdapter;
import org.eclipse.nebula.widgets.ganttchart.GanttGroup;
import org.eclipse.nebula.widgets.ganttchart.GanttImage;
import org.eclipse.nebula.widgets.ganttchart.IColorManager;
import org.eclipse.nebula.widgets.ganttchart.ISettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.mwc.debrief.timebar.Activator;
import org.mwc.debrief.timebar.model.IEventEntry;
import org.mwc.debrief.timebar.model.TimeSpot;
import MWC.GUI.Editable;

class GanttPaintManager extends AbstractPaintManager {
	@Override
	public void drawImage(final GanttComposite ganttComposite, final ISettings settings,
			final IColorManager colorManager, final GanttEvent event, final GC gc, final Image image,
			final boolean threeDee, final int dayWidth, final int xLoc, final int yStart, final Rectangle fullBounds) {
		int y = yStart;
		int x = xLoc;

		// draw a cross in a box if image is null
		if (image == null) {
			gc.setForeground(colorManager.getBlack());
			gc.drawRectangle(x, y, dayWidth, settings.getEventHeight());
			gc.drawLine(x, y, x + dayWidth, y + settings.getEventHeight());
			gc.drawLine(x + dayWidth, y, x, y + settings.getEventHeight());
			return;
		}

		// can it fit?
		final Rectangle bounds = image.getBounds();
		if (settings.scaleImageToDayWidth() && bounds.width > dayWidth) {
			// shrink image
			final ImageData id = image.getImageData();
			final int diff = id.width - dayWidth;
			id.width -= diff;
			id.height -= diff;
			final Image temp = new Image(Display.getDefault(), id);

			final int negY = (bounds.height - settings.getEventHeight());
			if (negY > 0) {
				y += negY / 2;
			}

			gc.drawImage(temp, x, y);
			temp.dispose();
			return;
		} else {
			// center it x-wise
			x -= bounds.width / 2;
			// x -= Math.abs(bounds.width - dayWidth) / 2;
		}

		gc.drawImage(image, x, y);

	}
}
