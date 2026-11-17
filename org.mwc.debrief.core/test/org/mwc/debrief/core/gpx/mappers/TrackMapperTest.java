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

package org.mwc.debrief.core.gpx.mappers;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mwc.debrief.core.gpx.mappers.TrackMapper.*;
import org.mwc.debrief.core.gpx.mappers.TrackMapper.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import org.eclipse.core.runtime.IStatus;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.gpx.TrackExtensionType;
import org.mwc.debrief.core.loaders.DebriefJaxbContextAware;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import com.topografix.gpx.v10.Gpx;
import com.topografix.gpx.v10.Gpx.Trk;
import com.topografix.gpx.v10.Gpx.Trk.Trkseg;
import com.topografix.gpx.v10.Gpx.Trk.Trkseg.Trkpt;
import com.topografix.gpx.v10.ObjectFactory;
import com.topografix.gpx.v11.ExtensionsType;
import com.topografix.gpx.v11.GpxType;
import com.topografix.gpx.v11.TrkType;
import com.topografix.gpx.v11.TrksegType;
import com.topografix.gpx.v11.WptType;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.Utilities.ReaderWriter.PlainImporter.MonitorProvider;

public class TrackMapperTest {
	private static TrackWrapper getData(final String name) throws FileNotFoundException {
		// get our sample data-file
		final ImportReplay importer = new ImportReplay();
		final Layers theLayers = new Layers();
		final String fName = "../org.mwc.cmap.combined.feature/root_installs/sample_data/" + name;
		final File inFile = new File(fName);
		assertTrue(inFile.exists(), "input file exists");
		final FileInputStream is = new FileInputStream(fName);
		importer.importThis(fName, is, theLayers, new MonitorProvider() {

			@Override
			public void done() {
				// not implemented
			}

			@Override
			public void init(final String fileName, final int length) {
				System.out.println("Loading " + fileName);
			}

			@Override
			public void progress(final int _progress) {
				// System.out.println(_progress);
			}
		});

		// sort out the sensors
		importer.storePendingSensors();

		// get the sensor track
		final TrackWrapper track = (TrackWrapper) theLayers.elements().nextElement();

		return track;
	}

	@Test
	public void testTrackExport() throws FileNotFoundException {
		final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();

		final TrackMapper mapper = new TrackMapper();

		List<Trk> res = mapper.toGpx10(tracks);

		assertEquals(0, res.size(), "empty list");

		final TrackWrapper trk1 = getData("boat1.rep");

		tracks.add(trk1);

		res = mapper.toGpx10(tracks);

		assertEquals(1, res.size(), "has track");

		final TrackWrapper trk2 = getData("boat2.rep");

		tracks.add(trk2);

		res = mapper.toGpx10(tracks);

		assertEquals(2, res.size(), "has track");

		// check the data
		final Trk firstTrack = res.get(0);
		final Trkseg seg = firstTrack.getTrkseg().iterator().next();
		final Iterator<Trkpt> tIter = seg.getTrkpt().iterator();
		final Trkpt fix1 = tIter.next();

		assertEquals(22.186286d, fix1.getLat().doubleValue(), 0.000001, "correct lat");
		assertEquals(-21.697880, fix1.getLon().doubleValue(), 0.000001, "correct lon");
		assertEquals(269.7d, fix1.getCourse().doubleValue(), 0.1, "correct course");
		assertEquals(MWC.Algorithms.Conversions.Kts2Mps(2d), fix1.getSpeed().doubleValue(), 0.001, "correct speed");
		assertEquals("1995-12-12T05:00:00Z", fix1.getTime().toString(), "correct time");

		final Trkpt fix2 = tIter.next();

		assertEquals(22.186272d, fix2.getLat().doubleValue(), 0.000001, "correct lat");
		assertEquals(-21.700827, fix2.getLon().doubleValue(), 0.000001, "correct lon");
		assertEquals(269.7d, fix2.getCourse().doubleValue(), 0.1, "correct course");
		assertEquals(MWC.Algorithms.Conversions.Kts2Mps(2d), fix2.getSpeed().doubleValue(), 0.001, "correct speed");
		assertEquals("1995-12-12T05:01:00Z", fix2.getTime().toString(), "correct time");

	}
}
