package MWC.Utilities.ReaderWriter.XML.Features;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.*;
import MWC.Utilities.ReaderWriter.XML.*;
import MWC.Utilities.ReaderWriter.XML.Util.*;
import MWC.GUI.Chart.Painters.ETOPOPainter;
import MWC.GUI.Tools.Palette.CreateTOPO;
import MWC.GUI.Layers;

public class ETOPOHandler extends MWCXMLReader
{

  java.awt.Color _theColor;
  boolean _isVisible;
  private Layers _theLayers;
  Integer _scaleLocation;
  boolean _showLand;
  int _lineThickness = 1;


  /** class which contains list of textual representations of scale locations
   */
  static MWC.GUI.Chart.Painters.ETOPOPainter.KeyLocationPropertyEditor lp
          = new MWC.GUI.Chart.Painters.ETOPOPainter.KeyLocationPropertyEditor();


  public ETOPOHandler(Layers theLayers)
  {
    // inform our parent what type of class we are
    super("etopo");

    _theLayers = theLayers;

    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      public void setValue(String name, boolean value)
      {
        _isVisible = value;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("ShowLand")
    {
      public void setValue(String name, boolean value)
      {
        _showLand = value;
      }
    });
    addHandler(new ColourHandler()
    {
      public void setColour(java.awt.Color color)
      {
        _theColor = color;
      }
    });
    addAttributeHandler(new HandleAttribute("ScaleLocation")
    {
      public void setValue(String name, String val)
      {
        lp.setAsText(val);
        _scaleLocation = (Integer)lp.getValue();
      }
    });
    addAttributeHandler(new HandleIntegerAttribute("LineThickness")
    {
      public void setValue(String name, int val)
      {
        _lineThickness = val;
      }
    });

  }

  public void elementClosed()
  {
    // create a Grid from this data
    ETOPOPainter painter = new ETOPOPainter(CreateTOPO.getETOPOPath(), _theLayers);
    painter.setColor(_theColor);
    painter.setVisible(_isVisible);
    painter.setShowLand(_showLand);
    painter.setLineThickness(_lineThickness);

    if(_scaleLocation != null)
      painter.setKeyLocation(_scaleLocation);

    _theLayers.addThisLayer(painter);

    // reset our variables
    _theColor = null;
    _isVisible = false;
    _showLand = true;
    _lineThickness = 1;
  }

  public static void exportThisPlottable(MWC.GUI.Plottable plottable, Element parent, Document doc)
  {

    MWC.GUI.Chart.Painters.ETOPOPainter csp = (MWC.GUI.Chart.Painters.ETOPOPainter) plottable;
    Element etopo = doc.createElement("etopo");

    // do the visibility
    etopo.setAttribute("Visible", writeThis(csp.getVisible()));
    etopo.setAttribute("ShowLand", writeThis(csp.getShowLand()));

    lp.setValue(csp.getKeyLocation());
    etopo.setAttribute("ScaleLocation", lp.getAsText());
    etopo.setAttribute("LineThickness", writeThis(csp.getLineThickness()));

    // do the colour
    ColourHandler.exportColour(csp.getColor(), etopo, doc);

    parent.appendChild(etopo);
  }



}