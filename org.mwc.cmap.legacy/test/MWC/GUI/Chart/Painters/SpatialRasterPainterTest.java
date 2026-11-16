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

package MWC.GUI.Chart.Painters;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GUI.Chart.Painters.SpatialRasterPainter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.GUI.Chart.Painters.SpatialRasterPainter.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Dimension2D;
import java.awt.image.MemoryImageSource;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JButton;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Defaults;
import MWC.GUI.Layer;
import MWC.GUI.ETOPO.BathyProvider;
import MWC.GUI.ETOPO.Conrec;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GUI.Tools.Palette.CreateTOPO;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class SpatialRasterPainterTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	// TODO FIX-TEST
	public void NtestDepthAllocation() {
		final SpatialRasterPainter srp = new SpatialRasterPainter("scrap") {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * provide the delta for the data
			 */
			@Override
			public double getGridDelta() {
				return 1;
			}

			@Override
			public int getValueAt(final WorldLocation location) {
				return 0;
			}

			/**
			 * whether the data has been loaded yet
			 */
			@Override
			public boolean isDataLoaded() {
				return true;
			}
		};

		String listOfDepths = "-100,-20,-10,0,5";
		srp.setContourDepths(listOfDepths);
		assertEquals(srp._contourDepths[0], -100, 1, "min depth set");
		assertEquals(srp._contourDepths[4], 5, 1, "max depth set");
		String outputDepths = srp.getContourDepths();
		assertEquals(outputDepths, "-100, -20, -10, 0, 5", "depths output correctly");

		// check we can depths out of sequence
		listOfDepths = "-60, -100,-10,0,5";
		srp.setContourDepths(listOfDepths);
		assertEquals(srp._contourDepths[0], -100, 1, "min depth set");
		assertEquals(srp._contourDepths[4], 5, 1, "max depth set");
		outputDepths = srp.getContourDepths();
		assertEquals(outputDepths, "-100, -60, -10, 0, 5", "depths output correctly");

		// check we can handle mangled depths
		listOfDepths = "-60, -100,-10,0-5";
		srp.setContourDepths(listOfDepths);
		assertEquals(srp._contourDepths[0], -100, 1, "min depth set");
		assertEquals(srp._contourDepths[2], -10, 1, "max depth set");
		outputDepths = srp.getContourDepths();
		assertEquals(outputDepths, "-100, -60, -10", "depths output correctly");

	}

	@Test
	public void testRasterDrawing() {
		// what are we trying to do?
		/**
		 * represent a raster as a series of painted rectangles. Using the memory image
		 * (as we originally did, the time to plot the image is directly proportional to
		 * the resolution (num pixels) of the image. Using the series of rectangles,
		 * however, will be related to the resolution of the data to be plotted into the
		 * image - and is therefore independent of the screen resolution
		 */

		// find how many pixels wide the screen is

		// find the data units wide the screen is

		// are the data units more than 2 pixels wide?

		// yes, draw rectangles

		// else

		// no, draw the image buffer thingy

	}
}
