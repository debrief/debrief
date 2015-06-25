package org.mwc.debrief.core.handlers;

import org.xml.sax.Attributes;

import MWC.GUI.DynamicLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.TimeDisplayPainter;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Features.TimeDisplayPainterHandler;

public class DynamicLayerHandler extends MWCXMLReader implements LayerHandlerExtension
{
	private static final String VISIBLE = "Visible";
	private static final String NAME = "Name";
	public static final String TYPE = "dynamicLayer";
	private Layers _theLayers;
	private DynamicLayer _myLayer;
	protected boolean _visible;
	protected String _name;

	public DynamicLayerHandler()
	{
		this(TYPE);
	}

	public DynamicLayerHandler(String theType)
	{
		super(theType);
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
		addHandler(new TimeDisplayPainterHandler()
		{
			
			@Override
			public void addPlottable(Plottable plottable)
			{
				addThis(plottable);
			}
		});
	}
	
	@Override
	public final void elementClosed()
	{
			// set our specific attributes
			final DynamicLayer wrapper = _myLayer;
			wrapper.setVisible(_visible);
			wrapper.setName(_name);
			_theLayers.addThisLayer(wrapper);
	}

	@Override
	public boolean canExportThis(Layer subject)
	{
		return subject instanceof DynamicLayer;
	}

	@Override
	public void exportThis(final Layer theLayer, org.w3c.dom.Element parent,
			org.w3c.dom.Document doc)
	{
		
		DynamicLayer dl = (DynamicLayer) theLayer;
		
		final org.w3c.dom.Element eLayer = doc.createElement(TYPE);

		eLayer.setAttribute(NAME, dl.getName());
		eLayer.setAttribute(VISIBLE, writeThis(dl.getVisible()));
		
		// step through the components of the layer
		final java.util.Enumeration<Editable> enumer = theLayer.elements();
		while (enumer.hasMoreElements())
		{
			final MWC.GUI.Plottable nextPlottable = (MWC.GUI.Plottable) enumer
					.nextElement();
			if (nextPlottable instanceof TimeDisplayPainter)
			{
				TimeDisplayPainterHandler handler = new TimeDisplayPainterHandler()
				{

					@Override
					public void addPlottable(Plottable plottable)
					{
						theLayer.add(plottable);
					}
				};
				handler.exportThisPlottable(nextPlottable, eLayer, doc);
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
		_myLayer = new DynamicLayer();
		super.handleOurselves(name, attributes);
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

}
