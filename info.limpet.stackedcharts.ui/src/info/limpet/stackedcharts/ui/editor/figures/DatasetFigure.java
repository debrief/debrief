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
package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;

import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

public class DatasetFigure extends DirectionalIconLabel {
	public DatasetFigure() {
		super(StackedchartsImages.getImage(StackedchartsImages.DESC_DATASET));

		// Indicate via Mouse Cursor that the Dataset can be moved (to another Axis).
		// Not the perfect solution, ideally there should be way to realize this in the
		// upper layer
		// (GEF)
		getLabel().addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(final MouseEvent me) {
			}

			@Override
			public void mouseEntered(final MouseEvent me) {
				setCursor(Cursors.SIZEALL);
			}

			@Override
			public void mouseExited(final MouseEvent me) {
				setCursor(Cursors.ARROW);
			}

			@Override
			public void mouseHover(final MouseEvent me) {
			}

			@Override
			public void mouseMoved(final MouseEvent me) {
			}
		});

	}

	public void setName(final String name) {
		getLabel().setText(name);
	}
}
