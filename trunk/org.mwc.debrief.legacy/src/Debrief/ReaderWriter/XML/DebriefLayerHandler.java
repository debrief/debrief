package Debrief.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import Debrief.ReaderWriter.XML.Shapes.ArcHandler;
import Debrief.ReaderWriter.XML.Shapes.CircleHandler;
import Debrief.ReaderWriter.XML.Shapes.EllipseHandler;
import Debrief.ReaderWriter.XML.Shapes.FurthestOnCircleHandler;
import Debrief.ReaderWriter.XML.Shapes.LabelHandler;
import Debrief.ReaderWriter.XML.Shapes.LineHandler;
import Debrief.ReaderWriter.XML.Shapes.PolygonHandler;
import Debrief.ReaderWriter.XML.Shapes.RangeRingsHandler;
import Debrief.ReaderWriter.XML.Shapes.RectangleHandler;
import Debrief.ReaderWriter.XML.Shapes.WheelHandler;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;

public class DebriefLayerHandler extends
		MWC.Utilities.ReaderWriter.XML.LayerHandler
{
	public DebriefLayerHandler(MWC.GUI.Layers theLayers)
	{
		// inform our parent what type of class we are
		super(theLayers);

		addHandler(new LineHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});

		addHandler(new PolygonHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});

		addHandler(new EllipseHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});

		addHandler(new RectangleHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});

		addHandler(new ArcHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});

		addHandler(new CircleHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});

		addHandler(new WheelHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});
		addHandler(new RangeRingsHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});
		addHandler(new FurthestOnCircleHandler()
		{
			@Override
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});
		addHandler(new LabelHandler()
		{
			public void addPlottable(MWC.GUI.Plottable plottable)
			{
				addThis(plottable);
			}
		});

	}

	public static void exportThisDebriefItem(MWC.GUI.Plottable nextPlottable,
			org.w3c.dom.Element eLayer, org.w3c.dom.Document doc)
	{
		// get ready..
		checkExporters();

		// definition of what parameter we are going to search for (since
		// shapeWrappers are indexed by shape not actual class)
		Object classId = null;

		// if this is a shape then we've got to suck the shape out
		if (nextPlottable instanceof ShapeWrapper)
		{
			ShapeWrapper sw = (ShapeWrapper) nextPlottable;
			classId = sw.getShape().getClass();
		}
		else
			classId = nextPlottable.getClass();

		// try to get this one
		Object exporterType = _myExporters.get(classId);
		if (exporterType != null)
		{
			PlottableExporter cl = (PlottableExporter) exporterType;
			cl.exportThisPlottable(nextPlottable, eLayer, doc);
		}
		else
			MWC.Utilities.Errors.Trace.trace("Debrief Exporter not found for "
					+ nextPlottable.getName());

	}

	protected static void checkExporters()
	{
		if (_myExporters == null)
		{
			LayerHandler.checkExporters();

			_myExporters.put(MWC.GUI.Shapes.ArcShape.class, new ArcHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
			_myExporters.put(MWC.GUI.Shapes.CircleShape.class, new CircleHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
			_myExporters.put(MWC.GUI.Shapes.EllipseShape.class, new EllipseHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
			_myExporters.put(MWC.GUI.Shapes.LineShape.class, new LineHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
			_myExporters.put(MWC.GUI.Shapes.PolygonShape.class, new PolygonHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
			_myExporters.put(MWC.GUI.Shapes.RectangleShape.class,
					new RectangleHandler()
					{
						public void addPlottable(MWC.GUI.Plottable plottable)
						{
						}
					});
			_myExporters.put(MWC.GUI.Shapes.WheelShape.class, new WheelHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
			_myExporters.put(MWC.GUI.Shapes.FurthestOnCircleShape.class,
					new FurthestOnCircleHandler()
					{
						@Override
						public void addPlottable(MWC.GUI.Plottable plottable)
						{
						}
					});
			_myExporters.put(MWC.GUI.Shapes.RangeRingShape.class,
					new RangeRingsHandler()
					{
						public void addPlottable(MWC.GUI.Plottable plottable)
						{
						}
					});

			_myExporters.put(Debrief.Wrappers.LabelWrapper.class, new LabelHandler()
			{
				public void addPlottable(MWC.GUI.Plottable plottable)
				{
				}
			});
		}
	}

	public static void exportLayer(MWC.GUI.BaseLayer layer,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{
		// check our exporters
		checkExporters();

		org.w3c.dom.Element eLayer = doc.createElement("layer");

		eLayer.setAttribute("Name", layer.getName());
		eLayer.setAttribute("Visible", writeThis(layer.getVisible()));
		eLayer.setAttribute("LineThickness", writeThis(layer.getLineThickness()));

		// step through the components of the layer
		java.util.Enumeration<Editable> iter = layer.elements();
		while (iter.hasMoreElements())
		{
			MWC.GUI.Plottable nextPlottable = (MWC.GUI.Plottable) iter.nextElement();

			exportThisDebriefItem(nextPlottable, eLayer, doc);
		}

		parent.appendChild(eLayer);

	}

}