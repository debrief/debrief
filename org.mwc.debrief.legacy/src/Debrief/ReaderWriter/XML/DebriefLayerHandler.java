/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import Debrief.ReaderWriter.XML.Formatters.CoreFormatHandler;
import Debrief.ReaderWriter.XML.Shapes.ArcHandler;
import Debrief.ReaderWriter.XML.Shapes.CircleHandler;
import Debrief.ReaderWriter.XML.Shapes.EllipseHandler;
import Debrief.ReaderWriter.XML.Shapes.FurthestOnCircleHandler;
import Debrief.ReaderWriter.XML.Shapes.LabelHandler;
import Debrief.ReaderWriter.XML.Shapes.LineHandler;
import Debrief.ReaderWriter.XML.Shapes.PolygonHandler;
import Debrief.ReaderWriter.XML.Shapes.RangeRingsHandler;
import Debrief.ReaderWriter.XML.Shapes.RectangleHandler;
import Debrief.ReaderWriter.XML.Shapes.VectorHandler;
import Debrief.ReaderWriter.XML.Shapes.WheelHandler;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.Formatters.CoreFormatItemListener;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;

public class DebriefLayerHandler extends
    MWC.Utilities.ReaderWriter.XML.LayerHandler
{
  /**
   * we're having trouble ensuring that the Debrief exporters get declared. Introduce some extra
   * checking to double-check it's all ok.
   */
  private static boolean _exportersInitialised = false;

  public DebriefLayerHandler(final MWC.GUI.Layers theLayers)
  {
    // inform our parent what type of class we are
    super(theLayers);

    addHandler(new LineHandler()
    {
      public void addPlottable(final MWC.GUI.Plottable plottable)
      {
        addThis(plottable);
      }
    });

    addHandler(new VectorHandler()
    {

      @Override
      public void addPlottable(final Plottable plottable)
      {
        addThis(plottable);
      }
    });

    addHandler(new PolygonHandler()
    {
      public void addPlottable(final MWC.GUI.Plottable plottable)
      {
        addThis(plottable);
      }
    });

    addHandler(new EllipseHandler()
    {
      public void addPlottable(final MWC.GUI.Plottable plottable)
      {
        addThis(plottable);
      }
    });

    addHandler(new RectangleHandler()
    {
      public void addPlottable(final MWC.GUI.Plottable plottable)
      {
        addThis(plottable);
      }
    });

    addHandler(new ArcHandler()
    {
      public void addPlottable(final MWC.GUI.Plottable plottable)
      {
        addThis(plottable);
      }
    });

    addHandler(new CircleHandler()
    {
      public void addPlottable(final MWC.GUI.Plottable plottable)
      {
        addThis(plottable);
      }
    });

    addHandler(new WheelHandler()
    {
      public void addPlottable(final MWC.GUI.Plottable plottable)
      {
        addThis(plottable);
      }
    });
    addHandler(new RangeRingsHandler()
    {
      public void addPlottable(final MWC.GUI.Plottable plottable)
      {
        addThis(plottable);
      }
    });
    addHandler(new FurthestOnCircleHandler()
    {
      @Override
      public void addPlottable(final MWC.GUI.Plottable plottable)
      {
        addThis(plottable);
      }
    });
    addHandler(new LabelHandler()
    {
      public void addPlottable(final MWC.GUI.Plottable plottable)
      {
        addThis(plottable);
      }
    });
    addHandler(new CoreFormatHandler()
    {
      public void addFormatter(Editable editable)
      {
        addThis(editable);
      }
    });

  }

  public static void exportThisDebriefItem(
      final MWC.GUI.Plottable nextPlottable, final org.w3c.dom.Element eLayer,
      final org.w3c.dom.Document doc)
  {
    // get ready..
    checkExporters();

    // definition of what parameter we are going to search for (since
    // shapeWrappers are indexed by shape not actual class)
    Object classId = null;

    // if this is a shape then we've got to suck the shape out
    if (nextPlottable instanceof ShapeWrapper)
    {
      final ShapeWrapper sw = (ShapeWrapper) nextPlottable;
      classId = sw.getShape().getClass();
    }
    else
      classId = nextPlottable.getClass();

    // try to get this one
    final Object exporterType = _myExporters.get(classId);
    if (exporterType != null)
    {
      final PlottableExporter cl = (PlottableExporter) exporterType;
      cl.exportThisPlottable(nextPlottable, eLayer, doc);
    }
    else
    {
      // special handling, see if it's an item formatter
      if (nextPlottable instanceof CoreFormatItemListener)
      {
        CoreFormatHandler.exportThisPlottable(nextPlottable, eLayer, doc);
      }
      else
      {
        MWC.Utilities.Errors.Trace.trace("Debrief Exporter not found for "
            + nextPlottable.getName());
      }

    }

  }

  protected static void checkExporters()
  {
    // have the Debrief-specific exporters been declared?
    if (!_exportersInitialised)
    {
      _exportersInitialised = true;

      // hey, the parent ones haven't either. Go for it.
      LayerHandler.checkExporters();

      _myExporters.put(MWC.GUI.Shapes.ArcShape.class, new ArcHandler()
      {
        public void addPlottable(final MWC.GUI.Plottable plottable)
        {
        }
      });
      _myExporters.put(MWC.GUI.Shapes.CircleShape.class, new CircleHandler()
      {
        public void addPlottable(final MWC.GUI.Plottable plottable)
        {
        }
      });
      _myExporters.put(MWC.GUI.Shapes.EllipseShape.class, new EllipseHandler()
      {
        public void addPlottable(final MWC.GUI.Plottable plottable)
        {
        }
      });
      _myExporters.put(MWC.GUI.Shapes.LineShape.class, new LineHandler()
      {
        public void addPlottable(final MWC.GUI.Plottable plottable)
        {
        }
      });
      _myExporters.put(MWC.GUI.Shapes.VectorShape.class, new VectorHandler()
      {
        public void addPlottable(final MWC.GUI.Plottable plottable)
        {
        }
      });
      _myExporters.put(MWC.GUI.Shapes.PolygonShape.class, new PolygonHandler()
      {
        public void addPlottable(final MWC.GUI.Plottable plottable)
        {
        }
      });
      _myExporters.put(MWC.GUI.Shapes.RectangleShape.class,
          new RectangleHandler()
          {
            public void addPlottable(final MWC.GUI.Plottable plottable)
            {
            }
          });
      _myExporters.put(MWC.GUI.Shapes.WheelShape.class, new WheelHandler()
      {
        public void addPlottable(final MWC.GUI.Plottable plottable)
        {
        }
      });
      _myExporters.put(MWC.GUI.Shapes.FurthestOnCircleShape.class,
          new FurthestOnCircleHandler()
          {
            @Override
            public void addPlottable(final MWC.GUI.Plottable plottable)
            {
            }
          });
      _myExporters.put(MWC.GUI.Shapes.RangeRingShape.class,
          new RangeRingsHandler()
          {
            public void addPlottable(final MWC.GUI.Plottable plottable)
            {
            }
          });

      _myExporters.put(Debrief.Wrappers.LabelWrapper.class, new LabelHandler()
      {
        public void addPlottable(final MWC.GUI.Plottable plottable)
        {
        }
      });
    }
  }

  public static void exportLayer(final MWC.GUI.BaseLayer layer,
      final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    // check our exporters
    checkExporters();

    final org.w3c.dom.Element eLayer = doc.createElement("layer");

    eLayer.setAttribute("Name", layer.getName());
    eLayer.setAttribute("Visible", writeThis(layer.getVisible()));
    eLayer.setAttribute("LineThickness", writeThis(layer.getLineThickness()));

    // step through the components of the layer
    final java.util.Enumeration<Editable> iter = layer.elements();
    while (iter.hasMoreElements())
    {
      final MWC.GUI.Plottable nextPlottable =
          (MWC.GUI.Plottable) iter.nextElement();

      exportThisDebriefItem(nextPlottable, eLayer, doc);
    }

    parent.appendChild(eLayer);

  }

}