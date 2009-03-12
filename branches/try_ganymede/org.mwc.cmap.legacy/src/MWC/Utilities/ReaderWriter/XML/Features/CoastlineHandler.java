package MWC.Utilities.ReaderWriter.XML.Features;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Element;

import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;



abstract public class CoastlineHandler extends MWCXMLReader  implements LayerHandler.exporter
{

  java.awt.Color _theColor;
  boolean _isVisible;


  public CoastlineHandler()
  {
    // inform our parent what type of class we are
    super("coastline");

    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      public void setValue(String name, boolean value)
      {
        _isVisible = value;
      }
    });
    addHandler(new ColourHandler()
    {
      public void setColour(java.awt.Color color)
      {
        _theColor = color;
      }
    });

  }

  public void elementClosed()
  {
    // create a coastline from this data
    MWC.GUI.Chart.Painters.CoastPainter csp = new MWC.GUI.Chart.Painters.CoastPainter();
    csp.setColor(_theColor);
    csp.setVisible(_isVisible);

    addPlottable(csp);

    // reset our variables
    _theColor = null;
    _isVisible = false;
  }

  abstract public void addPlottable(MWC.GUI.Plottable plottable);


  public void exportThisPlottable(MWC.GUI.Plottable plottable, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {


    MWC.GUI.Chart.Painters.CoastPainter csp = (MWC.GUI.Chart.Painters.CoastPainter) plottable;
    Element coast = doc.createElement("coastline");

    // do the visibility
    coast.setAttribute("Visible", writeThis(csp.getVisible()));

    // do the name
    coast.setAttribute("Name", "World Default");

    // do the colour
    ColourHandler.exportColour(csp.getColor(), coast, doc);


    parent.appendChild(coast);
  }



}