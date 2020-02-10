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
package Debrief.ReaderWriter.XML.dynamic;

import org.xml.sax.Attributes;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.XML.Shapes.CircleHandler;
import Debrief.ReaderWriter.XML.Shapes.PolygonHandler;
import Debrief.ReaderWriter.XML.Shapes.RectangleHandler;
import Debrief.Wrappers.DynamicShapeLayer;
import Debrief.Wrappers.DynamicShapeWrapper;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class DynamicShapeLayerHandler extends MWCXMLReader implements LayerHandlerExtension {
	private class CircleTrackHandler extends CircleHandler {

		@Override
		public void addPlottable(final Plottable plottable) {
			addDsw(plottable);
		}

	}

	private class PolygonTrackHandler extends PolygonHandler {

		@Override
		public void addPlottable(final Plottable plottable) {
			addDsw(plottable);
		}

	}

	private class RectangleTrackHandler extends RectangleHandler {

		@Override
		public void addPlottable(final Plottable plottable) {
			addDsw(plottable);
		}

	}

	private static final String LINE_THICKNESS = "LineThickness";
	private static final String PLOT_ALL_SHAPES = "PlotAllShapes";
	private static final String VISIBLE = "Visible";
	private static final String NAME = "Name";
	public static final String TYPE = "dynamicShapeLayer";
	protected boolean _plotAllShapes;
	private Layers _theLayers;
	private DynamicShapeLayer _myLayer;

	protected boolean _visible;

	protected Integer _lineThickness;

	protected String _name;

	public DynamicShapeLayerHandler() {
		this(TYPE);
	}

	public DynamicShapeLayerHandler(final String theType) {
		super(theType);
		addAttributeHandler(new HandleBooleanAttribute(PLOT_ALL_SHAPES) {
			@Override
			public void setValue(final String name, final boolean value) {
				_plotAllShapes = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(VISIBLE) {
			@Override
			public void setValue(final String name, final boolean value) {
				_visible = value;
			}
		});
		addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String value) {
				_name = value;
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(LINE_THICKNESS) {
			@Override
			public void setValue(final String name, final int val) {
				_lineThickness = val;
			}
		});
		addHandler(new CircleTrackHandler());
		addHandler(new RectangleTrackHandler());
		addHandler(new PolygonTrackHandler());
	}

	private void addDsw(final Plottable plottable) {
		if (plottable instanceof ShapeWrapper) {
			final ShapeWrapper sw = (ShapeWrapper) plottable;
			final DynamicShapeWrapper dsw = new DynamicShapeWrapper(sw.getLabel(), sw.getShape(), sw.getColor(),
					sw.getStartDTG(), _myLayer.getName());
			addThis(dsw);
		} else {
			Application.logError2(ToolParent.WARNING,
					"Dynamic Shape Layer received unexpected type:" + plottable.getName(), null);
			addThis(plottable);
		}
	}

	public void addThis(final MWC.GUI.Plottable plottable) {
		_myLayer.add(plottable);

		// is this an item that wants to know about the layers object?
		if (plottable instanceof NeedsToKnowAboutLayers) {
			final NeedsToKnowAboutLayers theL = (NeedsToKnowAboutLayers) plottable;
			theL.setLayers(_theLayers);
		}

	}

	@Override
	public boolean canExportThis(final Layer subject) {
		return subject instanceof DynamicShapeLayer;
	}

	@Override
	public final void elementClosed() {
		// set our specific attributes
		final DynamicShapeLayer wrapper = _myLayer;
		wrapper.setVisible(_visible);
		wrapper.setPlotAllShapes(_plotAllShapes);
		wrapper.setName(_name);
		if (_lineThickness != null)
			wrapper.setLineThickness(_lineThickness);

		_theLayers.addThisLayer(wrapper);

		_lineThickness = null;
	}

	@Override
	public void exportThis(final Layer theLayer, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc) {

		final DynamicShapeLayer dsl = (DynamicShapeLayer) theLayer;

		final org.w3c.dom.Element eLayer = doc.createElement(TYPE);

		eLayer.setAttribute(NAME, dsl.getName());
		eLayer.setAttribute(VISIBLE, writeThis(dsl.getVisible()));
		eLayer.setAttribute(PLOT_ALL_SHAPES, writeThis(dsl.isPlotAllShapes()));
		eLayer.setAttribute(LINE_THICKNESS, writeThis(dsl.getLineThickness()));

		// step through the components of the layer
		final java.util.Enumeration<Editable> enumer = theLayer.elements();
		while (enumer.hasMoreElements()) {
			final MWC.GUI.Plottable nextPlottable = (MWC.GUI.Plottable) enumer.nextElement();

			if (nextPlottable instanceof DynamicShapeWrapper) {
				final DynamicShapeWrapper dsw = (DynamicShapeWrapper) nextPlottable;
				if (dsw.getShape() instanceof CircleShape) {
					final CircleTrackHandler handler = new CircleTrackHandler();
					handler.exportThisPlottable(nextPlottable, eLayer, doc);
				} else if (dsw.getShape() instanceof RectangleShape) {
					final RectangleTrackHandler handler = new RectangleTrackHandler();
					handler.exportThisPlottable(nextPlottable, eLayer, doc);
				} else if (dsw.getShape() instanceof PolygonShape) {
					final PolygonTrackHandler handler = new PolygonTrackHandler();
					handler.exportThisPlottable(nextPlottable, eLayer, doc);
				}
			}
		}

		parent.appendChild(eLayer);

	}

	@Override
	// this is one of ours, so get on with it!
	protected void handleOurselves(final String name, final Attributes attributes) {
		// we are starting a new layer, so create it!
		_myLayer = new DynamicShapeLayer();

		super.handleOurselves(name, attributes);
	}

	@Override
	public void setLayers(final Layers theLayers) {
		_theLayers = theLayers;
	}

}
