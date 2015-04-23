package org.mwc.debrief.core.handlers;

import org.xml.sax.Attributes;

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
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class DynamicShapeLayerHandler extends MWCXMLReader implements LayerHandlerExtension
{
	private static final String PLOT_ALL_SHAPES = "PlotAllShapes";
	private static final String VISIBLE = "Visible";
	private static final String NAME = "Name";
	public static final String TYPE = "dynamicShapeLayer";
	protected boolean _plotAllShapes;
	private Layers _theLayers;
	private DynamicShapeLayer _myLayer;
	protected boolean _visible;
	protected String _name;

	public DynamicShapeLayerHandler()
	{
		this(TYPE);
	}

	public DynamicShapeLayerHandler(String theType)
	{
		super(theType);
		addAttributeHandler(new HandleBooleanAttribute(PLOT_ALL_SHAPES)
		{
			public void setValue(final String name, final boolean value)
			{
				_plotAllShapes = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(VISIBLE)
		{
			public void setValue(final String name, final boolean value)
			{
				_visible = value;
			}
		});
		addAttributeHandler(new HandleAttribute(NAME)
		{
			public void setValue(final String name, final String value)
			{
				_name = value;
			}
		});
		addHandler(new CircleTrackHandler());
		addHandler(new RectangleTrackHandler());
		addHandler(new PolygonTrackHandler());
	}
	
	@Override
	public final void elementClosed()
	{
			// set our specific attributes
			final DynamicShapeLayer wrapper = (DynamicShapeLayer) _myLayer;
			wrapper.setVisible(_visible);
			wrapper.setPlotAllShapes(_plotAllShapes);
			wrapper.setName(_name);
			
			_theLayers.addThisLayer(wrapper);
	}

	@Override
	public boolean canExportThis(Layer subject)
	{
		return subject instanceof DynamicShapeLayer;
	}

	@Override
	public void exportThis(Layer theLayer, org.w3c.dom.Element parent,
			org.w3c.dom.Document doc)
	{
		
		DynamicShapeLayer dsl = (DynamicShapeLayer) theLayer;
		
		final org.w3c.dom.Element eLayer = doc.createElement(TYPE);

		eLayer.setAttribute(NAME, dsl.getName());
		eLayer.setAttribute(VISIBLE, writeThis(dsl.getVisible()));
		eLayer.setAttribute(PLOT_ALL_SHAPES, writeThis(dsl.isPlotAllShapes()));

		// step through the components of the layer
		final java.util.Enumeration<Editable> enumer = theLayer.elements();
		while (enumer.hasMoreElements())
		{
			final MWC.GUI.Plottable nextPlottable = (MWC.GUI.Plottable) enumer
					.nextElement();

			if (nextPlottable instanceof DynamicShapeWrapper)
			{
				DynamicShapeWrapper dsw = (DynamicShapeWrapper) nextPlottable;
				if (dsw.getShape() instanceof CircleShape)
				{
					CircleTrackHandler handler = new CircleTrackHandler();
					handler.exportThisPlottable(nextPlottable, eLayer, doc);
				}
				else if (dsw.getShape() instanceof RectangleShape)
				{
					RectangleTrackHandler handler = new RectangleTrackHandler();
					handler.exportThisPlottable(nextPlottable, eLayer, doc);
				}
				else if (dsw.getShape() instanceof PolygonShape)
				{
					PolygonTrackHandler handler = new PolygonTrackHandler();
					handler.exportThisPlottable(nextPlottable, eLayer, doc);
				}
			}
		}

		parent.appendChild(eLayer);
		
	}

	@Override
	public void setLayers(Layers theLayers)
	{
		_theLayers = theLayers;
	}

	@Override
  //this is one of ours, so get on with it!
	protected void handleOurselves(final String name, final Attributes attributes)
	{
		// we are starting a new layer, so create it!
		_myLayer = new DynamicShapeLayer();

		super.handleOurselves(name, attributes);
	}
	
	private class CircleTrackHandler extends CircleHandler {

		
		public CircleTrackHandler()
		{
			super("circleTrack");
		}

		@Override
		public void addPlottable(Plottable plottable)
		{
			addDsw(plottable);
		}
		
	}
	
	private class RectangleTrackHandler extends RectangleHandler {

		
		public RectangleTrackHandler()
		{
			super("rectangleTrack");
		}

		@Override
		public void addPlottable(Plottable plottable)
		{
			addDsw(plottable);
		}
		
	}
	
	private class PolygonTrackHandler extends PolygonHandler {

		
		public PolygonTrackHandler()
		{
			super("polyTrack");
		}

		@Override
		public void addPlottable(Plottable plottable)
		{
			addDsw(plottable);
		}
		
	}
	
	public void addThis(final MWC.GUI.Plottable plottable)
	{
		_myLayer.add(plottable);
		
		// is this an item that wants to know about the layers object?
		if (plottable instanceof NeedsToKnowAboutLayers)
		{
			final NeedsToKnowAboutLayers theL = (NeedsToKnowAboutLayers) plottable;
			theL.setLayers(_theLayers);
		}

	}

	private void addDsw(Plottable plottable)
	{
		if (plottable instanceof ShapeWrapper)
		{
			ShapeWrapper sw = (ShapeWrapper) plottable;
			DynamicShapeWrapper dsw = new DynamicShapeWrapper(sw.getLabel(),
					sw.getShape(), sw.getColor(), sw.getStartDTG(), _myLayer.getName());
			addThis(dsw);
		}
		else
		{
			addThis(plottable);
		}
	}

}
