package MWC.Utilities.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.GUI.Layer;
import MWC.GUI.S57.S57Layer;
import MWC.Utilities.ReaderWriter.XML.Features.*;
import org.xml.sax.Attributes;

public class LayerHandler extends MWCXMLReader
{

	private MWC.GUI.Layers _theLayers;

	protected MWC.GUI.BaseLayer _myLayer;

	protected static java.util.Hashtable<Class, LayerHandler.exporter> _myExporters;

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
		

//		addHandler(new S57Handler()
//		{
//			public void addPlottable(MWC.GUI.Plottable plottable)
//			{
//				addThis(plottable);
//			}
//		});
		

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
		_myLayer = new MWC.GUI.BaseLayer();

		super.handleOurselves(name, attributes);
	}

	protected void addThis(MWC.GUI.Plottable plottable)
	{
		_myLayer.add(plottable);
	}

	public void elementClosed()
	{
		// check that there is more than one element in this layer
		int counter = 0;
		java.util.Enumeration enumer = _myLayer.elements();
		while (enumer.hasMoreElements())
		{
			Object val = enumer.nextElement();
			if (val != null)
			{
				counter++;
				continue;
			}
		}

		// our layer is complete, add it to the parent!
		// note, we no longer allow duplicates, we only want
		// one Chart Features, for example
		_theLayers.addThisLayer(_myLayer);

		// now, we may well have already had a layer of this name - particularly
		// the "Chart Features" one.
		// if this has happened, we need to update it's visibility with our one
		Layer theLayer = _theLayers.findLayer(_myLayer.getName());
		theLayer.setVisible(_myLayer.getVisible());
		
		// right, make sure we make the chart-feature layer unbuffered		
		// NO, don't bother making it buffered, we're having double-buffering trouble under VISTA		
//		if(_myLayer.getName().equals(MWC.GUI.Layers.CHART_FEATURES))
//		{
//			// yup, we've either just loaded the chart-features layer, or it's magicced itself
//			// from somewhere else.  Let's just double-check that it's buffered.
//			_myLayer.setBuffered(true);
//		}

		_myLayer = null;

	}

	private static void checkExporters()
	{
		if (_myExporters == null)
		{
			_myExporters = new java.util.Hashtable<Class, LayerHandler.exporter>();
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
			_myExporters.put(MWC.GUI.Chart.Painters.ScalePainter.class, new ScaleHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
//			_myExporters.put(S57Layer.class, new S57Handler()
//			{
//				public void addPlottable(MWC.GUI.Plottable plottable)
//				{
//				}
//			});
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
		}
	}

	public static void exportThisPlottable(MWC.GUI.Plottable nextPlottable,
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
			exporter cl = (exporter) exporterType;
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
		java.util.Enumeration enumer = layer.elements();
		while (enumer.hasMoreElements())
		{
			MWC.GUI.Plottable nextPlottable = (MWC.GUI.Plottable) enumer.nextElement();

			exportThisPlottable(nextPlottable, eLayer, doc);
		}

		parent.appendChild(eLayer);

	}

	public interface exporter
	{
		public void exportThisPlottable(MWC.GUI.Plottable plottable,
				org.w3c.dom.Element parent, org.w3c.dom.Document doc);
	}

}