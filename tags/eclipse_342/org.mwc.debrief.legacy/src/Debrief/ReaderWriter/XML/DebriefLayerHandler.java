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
import Debrief.ReaderWriter.XML.Shapes.LabelHandler;
import Debrief.ReaderWriter.XML.Shapes.LineHandler;
import Debrief.ReaderWriter.XML.Shapes.PolygonHandler;
import Debrief.ReaderWriter.XML.Shapes.RectangleHandler;
import Debrief.ReaderWriter.XML.Shapes.WheelHandler;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Editable;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.ReaderWriter.XML.Features.CoastlineHandler;
import MWC.Utilities.ReaderWriter.XML.Features.Grid4WHandler;
import MWC.Utilities.ReaderWriter.XML.Features.GridHandler;
import MWC.Utilities.ReaderWriter.XML.Features.LocalGridHandler;
import MWC.Utilities.ReaderWriter.XML.Features.ScaleHandler;
import MWC.Utilities.ReaderWriter.XML.Features.VPFCoastlineHandler;
import MWC.Utilities.ReaderWriter.XML.Features.VPFDatabaseHandler;

public final class DebriefLayerHandler extends MWC.Utilities.ReaderWriter.XML.LayerHandler
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

    addHandler(new LabelHandler()
    {
      public void addPlottable(MWC.GUI.Plottable plottable)
      {
        addThis(plottable);
      }
    });

  }

  public static void exportThisPlottable(MWC.GUI.Plottable nextPlottable, org.w3c.dom.Element eLayer,
                                         org.w3c.dom.Document doc)
  {

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
      exporter cl = (exporter) exporterType;
      cl.exportThisPlottable(nextPlottable, eLayer, doc);
    }
    else
      MWC.Utilities.Errors.Trace.trace("Debrief Exporter not found for " + nextPlottable.getName());

  }


  private static void checkExporters()
  {
    if (_myExporters == null)
    {
      _myExporters = new java.util.Hashtable<Class<?>, LayerHandler.exporter>();
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
      _myExporters.put(MWC.GUI.Shapes.RectangleShape.class, new RectangleHandler()
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
      _myExporters.put(MWC.GUI.Chart.Painters.CoastPainter.class, new CoastlineHandler()
      {
        public void addPlottable(MWC.GUI.Plottable plottable)
        {
        }
      });
      _myExporters.put(MWC.GUI.Chart.Painters.LocalGridPainter.class, new LocalGridHandler()
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
      _myExporters.put(Debrief.Wrappers.LabelWrapper.class, new LabelHandler()
      {
        public void addPlottable(MWC.GUI.Plottable plottable)
        {
        }
      });
      _myExporters.put(MWC.GUI.VPF.CoverageLayer.ReferenceCoverageLayer.class, new VPFCoastlineHandler()
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
//			try
//			{
//				_myExporters.put(S57Layer.class, new S57Handler()
//				{
//					public void addPlottable(MWC.GUI.Plottable plottable)
//					{
//					}
//				});
//			}
//			catch (RuntimeException e)
//			{
//				System.err.println("Problem registering S57 exporter: class now found");
//				e.printStackTrace();
//			}
      
    }
  }

  public static void exportLayer(MWC.GUI.BaseLayer layer, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
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

      exportThisPlottable(nextPlottable, eLayer, doc);
    }


    parent.appendChild(eLayer);

  }


}