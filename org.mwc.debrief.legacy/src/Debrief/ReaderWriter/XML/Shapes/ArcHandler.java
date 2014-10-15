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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package Debrief.ReaderWriter.XML.Shapes;

import MWC.GUI.Shapes.ArcShape;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.WorldDistance;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 19-Oct-2004
 * Time: 15:20:52
 * To change this template use File | Settings | File Templates.
 */
abstract public class ArcHandler extends CircleHandler
{
  private final static String myType = "Arc";
  private final static String PLOT_ORIGIN = "PlotOrigin";
  private final static String CENTRE_BEARING = "CentreBearing";
  private final static String ARC_WIDTH = "ArcWidth";
  private final static String PLOT_SPOKES = "PlotSpokes";

  boolean _plotOrigin;
  double _centreBearing;
  double _arcWidth;
  boolean _plotSpokes;

  public ArcHandler()
  {
    super(myType);

    super.addAttributeHandler(new HandleBooleanAttribute(PLOT_ORIGIN)
    {
      public void setValue(final String name, final boolean value)
      {
        _plotOrigin = value;
      }
    });

    super.addAttributeHandler(new HandleBooleanAttribute(PLOT_SPOKES)
    {
      public void setValue(final String name, final boolean value)
      {
        _plotSpokes = value;
      }
    });

    super.addAttributeHandler(new HandleDoubleAttribute(CENTRE_BEARING)
    {
      public void setValue(final String name, final double value)
      {
        _centreBearing = value;
      }
    });

    super.addAttributeHandler(new HandleDoubleAttribute(ARC_WIDTH)
    {
      public void setValue(final String name, final double value)
      {
        _arcWidth = value;
      }
    });

  }

  // this is one of ours, so get on with it!
  protected final void handleOurselves(final String name, final Attributes attributes)
  {
    _plotOrigin = true;
    _plotSpokes = false;
    _centreBearing = 0;
    _arcWidth = 90;
    super.handleOurselves(name, attributes);
  }

  public PlainShape getShape()
  {
  	PlainShape res = null;
  	if(_radiusDist != null)
  		res = new ArcShape(super._centre, _radiusDist, _centreBearing, _arcWidth, _plotOrigin, _plotSpokes);
  	else
  		res = new ArcShape(super._centre, new WorldDistance(super._radius * 1000, WorldDistance.YARDS),
  				_centreBearing, _arcWidth, _plotOrigin, _plotSpokes);
	
    return res;
    }

  // export the circle  specific components
  protected void exportCircleAttributes(final Element ePlottable, final CircleShape cs, final Document doc)
  {
    super.exportCircleAttributes(ePlottable, cs, doc);

    final ArcShape as = (ArcShape) cs;

    // and now our own arc-specific attributes
    ePlottable.setAttribute(PLOT_ORIGIN, writeThis(as.getPlotOrigin()));
    ePlottable.setAttribute(PLOT_SPOKES, writeThis(as.getPlotSpokes()));
    ePlottable.setAttribute(CENTRE_BEARING, writeThis(as.getCentreBearing()));
    ePlottable.setAttribute(ARC_WIDTH, writeThis(as.getArcWidth()));
  }


}
