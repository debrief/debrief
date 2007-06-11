package Debrief.ReaderWriter.XML.Shapes;

import MWC.GUI.Shapes.ArcShape;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.PlainShape;
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

  private boolean _plotOrigin;
  private double _centreBearing;
  private double _arcWidth;
  private boolean _plotSpokes;

  public ArcHandler()
  {
    super(myType);

    super.addAttributeHandler(new HandleBooleanAttribute(PLOT_ORIGIN)
    {
      public void setValue(String name, boolean value)
      {
        _plotOrigin = value;
      }
    });

    super.addAttributeHandler(new HandleBooleanAttribute(PLOT_SPOKES)
    {
      public void setValue(String name, boolean value)
      {
        _plotSpokes = value;
      }
    });

    super.addAttributeHandler(new HandleDoubleAttribute(CENTRE_BEARING)
    {
      public void setValue(String name, double value)
      {
        _centreBearing = value;
      }
    });

    super.addAttributeHandler(new HandleDoubleAttribute(ARC_WIDTH)
    {
      public void setValue(String name, double value)
      {
        _arcWidth = value;
      }
    });

  }

  // this is one of ours, so get on with it!
  protected final void handleOurselves(String name, Attributes attributes)
  {
    _plotOrigin = true;
    _plotSpokes = false;
    _centreBearing = 0;
    _arcWidth = 90;
    super.handleOurselves(name, attributes);
  }

  public PlainShape getShape()
  {
    return new ArcShape(super._centre, super._radius * 1000, _centreBearing, _arcWidth, _plotOrigin, _plotSpokes);
  }

  // export the circle  specific components
  protected void exportCircleAttributes(Element ePlottable, CircleShape cs, Document doc)
  {
    super.exportCircleAttributes(ePlottable, cs, doc);

    ArcShape as = (ArcShape) cs;

    // and now our own arc-specific attributes
    ePlottable.setAttribute(PLOT_ORIGIN, writeThis(as.getPlotOrigin()));
    ePlottable.setAttribute(PLOT_SPOKES, writeThis(as.getPlotSpokes()));
    ePlottable.setAttribute(CENTRE_BEARING, writeThis(as.getCentreBearing()));
    ePlottable.setAttribute(ARC_WIDTH, writeThis(as.getArcWidth()));
  }


}
