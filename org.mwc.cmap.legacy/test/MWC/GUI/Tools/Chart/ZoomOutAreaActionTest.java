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

package MWC.GUI.Tools.Chart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.awt.Component;
import java.awt.Dimension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.HasEditables;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Canvas.MockCanvasType;
import MWC.GUI.Tools.Chart.ZoomOut.ZoomOutAreaAction;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class ZoomOutAreaActionTest {
	class MockCanvas extends MockCanvasType {
		PlainProjection _theProjection;

		public MockCanvas() {
			_theProjection = new FlatProjection();
		}

		@Override
		public PlainProjection getProjection() {
			return _theProjection;
		}
	}

	class MockChart extends PlainChart {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		MockCanvasType _theCanvas;

		public MockChart(final Layers theLayers) {
			super(theLayers);
			_theCanvas = new MockCanvas();
		}

		@Override
		public CanvasType getCanvas() {
			return _theCanvas;
		}

		@Override
		public Component getPanel() {
			return null;
		}

		@Override
		public Dimension getScreenSize() {
			return null;
		}

		@Override
		public void repaint() {
		}

		@Override
		public void repaintNow(final java.awt.Rectangle rect) {
		}

		@Override
		public void rescale() {
		}

		@Override
		public void update() {
		}

		@Override
		public void update(final HasEditables changedLayer) {
		}

	}

	PlainChart _chart;

	@BeforeEach
	public void setUp() {
		_chart = new MockChart(null);
	}

	@Test
	public void testUndoRestoresArea() {
		final PlainProjection proj = _chart.getCanvas().getProjection();

		final WorldLocation topLeft = new WorldLocation(0, 0, 0);
		final WorldLocation bottomRight = new WorldLocation(10, 10, 0);
		final WorldArea oldArea = new WorldArea(topLeft, bottomRight);

		final WorldLocation selTopLeft = new WorldLocation(1, 2, 0);
		final WorldLocation selBottomRight = new WorldLocation(5, 5, 0);
		final WorldArea selectedArea = new WorldArea(selTopLeft, selBottomRight);

		proj.setDataArea(oldArea);
		proj.setScreenArea(new Dimension(100, 100));
		final ZoomOutAreaAction action = new ZoomOutAreaAction(_chart, oldArea, selectedArea, 2);

		assertEquals(oldArea, proj.getDataArea());
		action.execute();
		assertNotSame(oldArea, proj.getDataArea());
		action.undo();
		assertEquals(oldArea, proj.getDataArea());
	}

}
