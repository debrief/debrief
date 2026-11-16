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

package org.mwc.debrief.lite.headless;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.mwc.debrief.lite.LiteApplication;
import org.mwc.debrief.lite.LiteSession;
import org.mwc.debrief.lite.shapes.actions.ArcShapeCommandAction;
import org.mwc.debrief.lite.shapes.actions.CircleShapeCommandAction;
import org.mwc.debrief.lite.shapes.actions.EllipseShapeCommandAction;
import org.mwc.debrief.lite.shapes.actions.LineShapeCommandAction;
import org.mwc.debrief.lite.shapes.actions.RectangularShapeCommandAction;
import org.mwc.debrief.lite.undo.RedoAction;
import org.mwc.debrief.lite.undo.UndoAction;
import org.mwc.debrief.lite.util.PlotFiles;

import Debrief.Tools.Palette.CoreCreateShape;
import Debrief.Tools.Palette.CreateShape;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Shapes.ArcShape;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Tools.PlainTool.BoundsProvider;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Shapes made with Lite's Insert actions: where they go, their geometry and
 * defaults, undo and redo, and that they survive a save and reload.
 */
public class ShapeInsertionTest {

	/** the layer picked in the Insert ribbon's layer drop-down */
	private static final String SHAPES = "Shapes";

	private static final double TOLERANCE_DEGS = 1e-9;

	private static void assertLocation(final String what, final WorldLocation expected, final WorldLocation actual) {
		assertEquals(expected.getLat(), actual.getLat(), TOLERANCE_DEGS, what + " lat");
		assertEquals(expected.getLong(), actual.getLong(), TOLERANCE_DEGS, what + " long");
	}

	private LiteApplication _app;
	private LiteSession _session;
	private Layers _layers;
	private BoundsProvider _bounds;

	/** the visible area: centred on 50.5N 1.5W */
	private WorldLocation _centre;

	/**
	 * the Insert ribbon's layer drop-down, filled as the ribbon fills it, with
	 * the Shapes layer picked
	 */
	private JComboBox<String> ribbonLayerChoice() {
		final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		model.addElement(CoreCreateShape.USER_SELECTED_LAYER_COMMAND);
		for (final String name : _layers.trimmedLayers()) {
			model.addElement(name);
		}
		model.setSelectedItem(SHAPES);
		return new JComboBox<>(model);
	}

	/** run an Insert action, as the ribbon button does */
	private ShapeWrapper insert(final CreateShape action) {
		action.setSelectedLayerSource(ribbonLayerChoice());
		final int before = shapes().size();
		action.execute();
		final List<ShapeWrapper> after = shapes();
		assertEquals(before + 1, after.size(), "one shape added");
		return after.get(after.size() - 1);
	}

	/** the shapes in the Shapes layer, in the order they were added */
	private List<ShapeWrapper> shapes() {
		final List<ShapeWrapper> res = new ArrayList<>();
		final Layer misc = _layers.findLayer(SHAPES);
		if (misc != null) {
			final Enumeration<Editable> iter = misc.elements();
			while (iter.hasMoreElements()) {
				res.add((ShapeWrapper) iter.nextElement());
			}
		}
		return res;
	}

	@BeforeEach
	public void setUp() {
		_app = HeadlessFixture.newApp();
		_session = HeadlessFixture.newSession(_app);
		_layers = _session.getData();
		final BaseLayer shapes = new BaseLayer();
		shapes.setName(SHAPES);
		_layers.addThisLayer(shapes);
		final WorldArea visible = new WorldArea(new WorldLocation(51, -2, 0), new WorldLocation(50, -1, 0));
		_bounds = HeadlessFixture.boundsOf(visible);
		_centre = visible.getCentreAtSurface();
	}

	@Test
	public void testArc() {
		final ShapeWrapper wrapper = insert(new ArcShapeCommandAction(_app, null, _layers, "Arc", null, _bounds));
		assertEquals("new arc", wrapper.getName());
		final ArcShape arc = (ArcShape) wrapper.getShape();
		assertLocation("centre", _centre, arc.getCentre());
		assertEquals(4000, arc.getRadius().getValueIn(WorldDistance.YARDS), 1e-6, "radius (yds)");
		assertEquals(135, arc.getCentreBearing(), 1e-9, "centre bearing");
		assertEquals(90, arc.getArcWidth(), 1e-9, "arc width");
	}

	@Test
	public void testCircle() {
		final ShapeWrapper wrapper = insert(
				new CircleShapeCommandAction(_app, null, _layers, "Circle", null, _bounds));
		assertEquals("new circle", wrapper.getName());
		assertEquals(DebriefColors.RED, wrapper.getColor(), "default colour");
		final CircleShape circle = (CircleShape) wrapper.getShape();
		assertLocation("centre is the middle of the view", _centre, circle.getCentre());
		assertEquals(4000, circle.getRadius().getValueIn(WorldDistance.YARDS), 1e-6, "radius (yds)");
	}

	@Test
	public void testEllipse() {
		final ShapeWrapper wrapper = insert(
				new EllipseShapeCommandAction(_app, null, _layers, "Ellipse", null, _bounds));
		assertEquals("new ellipse", wrapper.getName());
		final EllipseShape ellipse = (EllipseShape) wrapper.getShape();
		assertLocation("centre", _centre, ellipse.getCentre());
		assertEquals(30, ellipse.getOrientation(), 1e-9, "orientation");
		assertEquals(5, ellipse.getMaxima().getValueIn(WorldDistance.KM), 1e-9, "maxima (km)");
		assertEquals(3, ellipse.getMinima().getValueIn(WorldDistance.KM), 1e-9, "minima (km)");
	}

	@Test
	public void testLineAndRectangle() {
		// both run from the centre to a point 0.05 degs away on a bearing of 045
		final WorldLocation northEast = _centre.add(new WorldVector(Math.toRadians(45), 0.05, 0));

		final LineShape line = (LineShape) insert(
				new LineShapeCommandAction(_app, null, _layers, "Line", null, _bounds)).getShape();
		assertLocation("line start", _centre, line.getLine_Start());
		assertLocation("line end", northEast, line.getLineEnd());

		final RectangleShape rect = (RectangleShape) insert(
				new RectangularShapeCommandAction(_app, null, _layers, "Rectangle", null, _bounds)).getShape();
		final WorldArea expected = new WorldArea(_centre, northEast);
		assertLocation("rectangle top left", expected.getTopLeft(), rect.getCorner_TopLeft());
		assertLocation("rectangle bottom right", expected.getBottomRight(), rect.getCornerBottomRight());
	}

	@Test
	public void testSavedAndReloaded() throws Exception {
		insert(new CircleShapeCommandAction(_app, null, _layers, "Circle", null, _bounds));
		insert(new EllipseShapeCommandAction(_app, null, _layers, "Ellipse", null, _bounds));

		final File saved = Files.createTempFile("lite-shapes", ".dpf").toFile();
		try {
			PlotFiles.writePlot(_session, saved);
			final LiteSession reloaded = HeadlessFixture.newSession(_app);
			PlotFiles.importPlot(saved, reloaded, _app);
			_layers = reloaded.getData();
		} finally {
			Files.deleteIfExists(saved.toPath());
		}

		final List<ShapeWrapper> shapes = shapes();
		assertEquals(2, shapes.size(), "both shapes reloaded");
		final CircleShape circle = (CircleShape) shapes.get(0).getShape();
		assertLocation("reloaded circle centre", _centre, circle.getCentre());
		assertEquals(4000, circle.getRadius().getValueIn(WorldDistance.YARDS), 1e-3, "reloaded circle radius (yds)");
		assertEquals("new ellipse", shapes.get(1).getName(), "reloaded ellipse");
	}

	@Test
	public void testUndoAndRedo() {
		final UndoAction undo = new UndoAction(_session.getUndoBuffer());
		final RedoAction redo = new RedoAction(_session.getUndoBuffer());

		final ShapeWrapper circle = insert(
				new CircleShapeCommandAction(_app, null, _layers, "Circle", null, _bounds));
		final ShapeWrapper ellipse = insert(
				new EllipseShapeCommandAction(_app, null, _layers, "Ellipse", null, _bounds));
		assertEquals(List.of(circle, ellipse), shapes());

		undo.execute();
		assertEquals(List.of(circle), shapes(), "undo removes the last shape");
		undo.execute();
		assertTrue(shapes().isEmpty(), "and then the one before");

		redo.execute();
		assertEquals(List.of(circle), shapes(), "redo puts the circle back");
		redo.execute();
		assertEquals(List.of(circle, ellipse), shapes(), "then the ellipse");

		assertFalse(_session.getUndoBuffer().canRedo(), "nothing left to redo");
	}
}
