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

package ASSET.Scenario.Observers;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Scenario.Observers.TrackPlotObserver.*;
import ASSET.Scenario.Observers.TrackPlotObserver.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.imageio.ImageIO;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.Editable;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GUI.Chart.Painters.ScalePainter;
import MWC.GUI.Properties.UnitsPropertyEditor;
import MWC.GUI.Shapes.Symbols.Geog.SquareSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.TacticalData.Track;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class TrackPlotObserverTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final TrackPlotObserver tpo = new TrackPlotObserver("a", 400, 400, "b",
				new WorldDistance(10, WorldDistance.KM), true, true, false, "test observer", true);
		return tpo;
	}

	// TODO FIX-TEST
	public void NtestWrite() {

		final String directoryName = "./test_reports/";
		final String fileName = "res.png";

		final TrackPlotObserver tpo = new TrackPlotObserver(directoryName, 400, 400, fileName,
				new WorldDistance(10, WorldDistance.KM), true, true, false, "test observer", true);
		final CoreScenario cs = new CoreScenario();
		tpo.setup(cs);

		WorldLocation loc = new WorldLocation(1, 2, 3);
		final Status stat = new Status(12, 0);
		stat.setCourse(21);
		stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		ParticipantType ssn = new CoreParticipant(12);
		ssn.setName("Bingo");
		ssn.setCategory(
				new Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));

		tpo.processTheseDetails(loc, stat, ssn);

		// move location
		loc = new WorldLocation(loc.add(new WorldVector(0.101, 0.01, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		loc = new WorldLocation(loc.add(new WorldVector(0.201, 0.05, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		loc = new WorldLocation(loc.add(new WorldVector(0.101, 0.03, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		loc = new WorldLocation(loc.add(new WorldVector(0.011, 0.05, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		ssn = new CoreParticipant(14);
		ssn.setName("Spooner");
		ssn.setCategory(new Category(Category.Force.RED, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));

		loc = new WorldLocation(loc.add(new WorldVector(2.701, 0.12, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		// move location
		loc = new WorldLocation(loc.add(new WorldVector(2.201, 0.12, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		loc = new WorldLocation(loc.add(new WorldVector(2.101, 0.05, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		loc = new WorldLocation(loc.add(new WorldVector(1.301, 0.02, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		loc = new WorldLocation(loc.add(new WorldVector(2.031, 0.04, 1)));
		tpo.processTheseDetails(loc, stat, ssn);

		tpo.tearDown(cs);

		// check file exists
		final File file = new File(directoryName + fileName);
		assertTrue(file.exists(), "file got created");
		System.out.println("file size is:" + file.length());
		assertEquals(7540, file.length(), 400, "file is of correct size");
	}
}
