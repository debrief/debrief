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
import MWC.GUI.Layers;
import MWC.GUI.Tools.Palette.CreateTOPO;
import MWC.GUI.ETOPO.ETOPO_2_Minute;

public class TOPOHandler extends MWCXMLReader
{

  java.awt.Color _theColor;
  boolean _isVisible;
  private Layers _theLayers;
  Integer _scaleLocation;
  Boolean _showLand = null;
  String _contourDepths = null;

  Boolean _showContours = null;
  Boolean _showBathy = null;


  /** class which contains list of textual representations of scale locations
   */
  static ETOPOPainter.KeyLocationPropertyEditor lp
          = new ETOPOPainter.KeyLocationPropertyEditor();


  public TOPOHandler(Layers theLayers)
  {
    // inform our parent what type of class we are
    super("topo");

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
        _showLand = new Boolean(value);
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("ShowBathy")
    {
      public void setValue(String name, boolean value)
      {
        _showBathy =  new Boolean(value);
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("ShowContours")
    {
      public void setValue(String name, boolean value)
      {
        _showContours = new Boolean(value);
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

    addAttributeHandler(new HandleAttribute("ContourDepths")
    {
      public void setValue(String name, String val)
      {
        _contourDepths = val;
      }
    });

  }

  public void elementClosed()
  {

    String topoPath = CreateTOPO.getETOPOPath();

    if(topoPath == null)
    {
      System.err.println("TOPO PATH MISSING");
      return;
    }

    // create a Grid from this data
    ETOPO_2_Minute painter = new ETOPO_2_Minute(topoPath);
    painter.setColor(_theColor);
    
    painter.setVisible(_isVisible);
    
    if(_showLand != null)
    	painter.setShowLand(_showLand.booleanValue());
    if(_showBathy != null)
    	painter.setBathyVisible(_showBathy.booleanValue());
    if(_showContours != null)
    	painter.setContoursVisible(_showContours.booleanValue());

    if(_scaleLocation != null)
      painter.setKeyLocation(_scaleLocation);

    if(_contourDepths != null)
      painter.setContourDepths(_contourDepths);

    _theLayers.addThisLayer(painter);

    // reset our variables
    _theColor = null;
    _isVisible = false;
    _showLand = null;
    _showContours = null;
    _showBathy = null;
  }

  public static void exportThisPlottable(MWC.GUI.Plottable plottable, Element parent, Document doc)
  {

    ETOPO_2_Minute csp = (ETOPO_2_Minute) plottable;
    Element etopo = doc.createElement("topo");

    // do the visibility
    etopo.setAttribute("Visible", writeThis(csp.getVisible()));
    etopo.setAttribute("ShowLand", writeThis(csp.getShowLand()));
    etopo.setAttribute("ShowBathy", writeThis(csp.isBathyVisible()));
    etopo.setAttribute("ShowContours", writeThis(csp.isContoursVisible()));

    etopo.setAttribute("ContourDepths", csp.getContourDepths());

    lp.setValue(csp.getKeyLocation());
    etopo.setAttribute("ScaleLocation", lp.getAsText());
//    etopo.setAttribute("LineThickness", writeThis(csp.getLineThickness()));

    // do the colour
    ColourHandler.exportColour(csp.getColor(), etopo, doc);

    parent.appendChild(etopo);
  }



}