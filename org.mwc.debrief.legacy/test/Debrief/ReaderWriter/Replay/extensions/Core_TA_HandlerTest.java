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

package Debrief.ReaderWriter.Replay.extensions;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.ReaderWriter.Replay.extensions.Core_TA_Handler.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.ReaderWriter.Replay.extensions.Core_TA_Handler.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataItem;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble2;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.ExtensibleLineImporter;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import org.junit.jupiter.api.Test;

public class Core_TA_HandlerTest {
	@Test
	public void testFolders() {
		final Layers layers = new Layers();
		final TrackWrapper track = new TrackWrapper();
		track.setName("Platform");
		final SensorWrapper sensor = new SensorWrapper("Sensor");
		track.add(sensor);
		layers.addThisLayer(track);

		final TA_Modules_DataHandler handler = new TA_Modules_DataHandler();
		handler.setLayers(layers);
		handler.storeMeasurement("Platform", "Sensor", "Modules", "Fore", "Some units", new HiResDate(1200000),
				12.33);
		handler.storeMeasurement("Platform", "Sensor", "Modules", "Fore", "Some units", new HiResDate(1300000),
				15.33);
		handler.storeMeasurement("Platform", "Sensor", "Modules", "Fore", "Some units", new HiResDate(1400000),
				11.33);
		handler.finalise();

		// check it worked
		// find the measurements
		DataFolder topF = (DataFolder) sensor.getAdditionalData().getThisType(DataFolder.class);
		if (topF == null) {
			topF = new DataFolder();
			sensor.getAdditionalData().add(topF);
		}

		topF.printAll();

		final DataFolder subF = (DataFolder) topF.get("Modules");
		final TimeSeriesDatasetDouble dataset = (TimeSeriesDatasetDouble) subF.get("Fore");
		assertEquals(3, dataset.size(), "has items");

	}
}
