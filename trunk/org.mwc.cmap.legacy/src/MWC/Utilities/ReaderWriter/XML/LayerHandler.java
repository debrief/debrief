package MWC.Utilities.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import MWC.Utilities.ReaderWriter.XML.Features.ChartBoundsHandler;
import MWC.Utilities.ReaderWriter.XML.Features.CoastlineHandler;
import MWC.Utilities.ReaderWriter.XML.Features.Grid4WHandler;
import MWC.Utilities.ReaderWriter.XML.Features.GridHandler;
import MWC.Utilities.ReaderWriter.XML.Features.LocalGridHandler;
import MWC.Utilities.ReaderWriter.XML.Features.ScaleHandler;
import MWC.Utilities.ReaderWriter.XML.Features.VPFCoastlineHandler;
import MWC.Utilities.ReaderWriter.XML.Features.VPFDatabaseHandler;

public class LayerHandler extends MWCXMLReader implements PlottableExporter
{

	private MWC.GUI.Layers _theLayers;

	protected MWC.GUI.BaseLayer _myLayer;

	protected static java.util.Hashtable<Class<?>, PlottableExporter> _myExporters;

	/**
	 * use the default layer name
	 * 
	 * @param theLayers
	 */
	public LayerHandler(MWC.GUI.Layers theLayers)
	{
		this(theLayers, "layer");
	}

	public LayerHandler(MWC.GUI.Layers theLayers, String layerName)
	{
		// inform our parent what type of class we are
		super(layerName);

		// store the layers object, so that we can add ourselves to it
		_theLayers = theLayers;

		addHandler(new CoastlineHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});

		addHandler(new ChartBoundsHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});

		addHandler(new VPFCoastlineHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});

		addHandler(new VPFDatabaseHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});

		addHandler(new ScaleHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});		

		addHandler(new LocalGridHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});
		addHandler(new GridHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});
		addHandler(new Grid4WHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});

		addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(String name, String val)
			{
				_myLayer.setName(val);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute("Visible")
		{
			public void setValue(String name, boolean val)
			{
				_myLayer.setVisible(val);
			}
		});

		addAttributeHandler(new HandleIntegerAttribute("LineThickness")
		{
			public void setValue(String name, int val)
			{
				_myLayer.setLineThickness(val);
			}
		});

	}

	// this is one of ours, so get on with it!
	protected void handleOurselves(String name, Attributes attributes)
	{
		// we are starting a new layer, so create it!
		_myLayer = getLayer();

		super.handleOurselves(name, attributes);
	}

	protected BaseLayer getLayer()
	{
		return new MWC.GUI.BaseLayer();
	}

	public void addThis(MWC.GUI.Plottable plottable)
	{
		_myLayer.add(plottable);
		
		// is this an item that wants to know about the layers object?
		if (plottable instanceof NeedsToKnowAboutLayers)
		{
			NeedsToKnowAboutLayers theL = (NeedsToKnowAboutLayers) plottable;
			theL.setLayers(_theLayers);
		}

	}

	public void elementClosed()
	{

		// our layer is complete, add it to the parent!
		// note, we no longer allow duplicates, we only want
		// one Chart Features, for example
		_theLayers.addThisLayer(_myLayer);
		
		// now, we may well have already had a layer of this name - particularly
		// the "Chart Features" one.
		// if this has happened, we need to update it's visibility with our one
		Layer theLayer = _theLayers.findLayer(_myLayer.getName());
		theLayer.setVisible(_myLayer.getVisible());

		_myLayer = null;

	}

	protected static void checkExporters()
	{
		if (_myExporters == null)
		{
			_myExporters = new java.util.Hashtable<Class<?>, PlottableExporter>();
			_myExporters.put(MWC.GUI.Chart.Painters.CoastPainter.class, new CoastlineHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
			_myExporters.put(MWC.GUI.Chart.Painters.LocalGridPainter.class,
					new LocalGridHandler()
					{
						public void addPlottable(MWC.GUI.Plottable plottable)
						{
						}
					});
			_myExporters.put(MWC.GUI.Chart.Painters.GridPainter.class, new GridHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
			_myExporters.put(MWC.GUI.Chart.Painters.Grid4WPainter.class, new Grid4WHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
			_myExporters.put(MWC.GUI.Chart.Painters.ScalePainter.class, new ScaleHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
			_myExporters.put(MWC.GUI.VPF.CoverageLayer.ReferenceCoverageLayer.class,
					new VPFCoastlineHandler()
					{
						public void addPlottable(MWC.GUI.Plottable plottable)
						{
						}
					});
			_myExporters.put(MWC.GUI.VPF.VPFDatabase.class, new VPFDatabaseHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
			_myExporters.put(ChartBoundsWrapper.class, new ChartBoundsHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});

		
			
		}
	}

	public static void exportThisItem(MWC.GUI.Plottable nextPlottable,
			org.w3c.dom.Element eLayer, org.w3c.dom.Document doc)
	{

		// definition of what parameter we are going to search for (since
		// shapeWrappers are indexed by shape not actual class)
		Object classId = null;

		// get the shape
		classId = nextPlottable.getClass();

		// try to get this one
		Object exporterType = _myExporters.get(classId);
		if (exporterType != null)
		{
			PlottableExporter cl = (PlottableExporter) exporterType;
			cl.exportThisPlottable(nextPlottable, eLayer, doc);
		}
		else
			MWC.Utilities.Errors.Trace.trace("Exporter not found for "
					+ nextPlottable.getName());

	}

	public static void exportLayer(MWC.GUI.BaseLayer layer, org.w3c.dom.Element parent,
			org.w3c.dom.Document doc)
	{
		exportLayer("layer", layer, parent, doc);
	}

	public static void exportLayer(String elementName, MWC.GUI.BaseLayer layer,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{
		// check our exporters
		checkExporters();

		org.w3c.dom.Element eLayer = doc.createElement(elementName);

		eLayer.setAttribute("Name", layer.getName());
		eLayer.setAttribute("Visible", writeThis(layer.getVisible()));
		eLayer.setAttribute("LineThickness", writeThis(layer.getLineThickness()));

		// step through the components of the layer
		java.util.Enumeration<Editable> enumer = layer.elements();
		while (enumer.hasMoreElements())
		{
			MWC.GUI.Plottable nextPlottable = (MWC.GUI.Plottable) enumer.nextElement();

			exportThisItem(nextPlottable, eLayer, doc);
		}

		parent.appendChild(eLayer);

	}

	public void exportThisPlottable(Plottable plottable, Element parent,
			Document doc)
	{
		exportLayer((BaseLayer)plottable, parent, doc);
	}

}