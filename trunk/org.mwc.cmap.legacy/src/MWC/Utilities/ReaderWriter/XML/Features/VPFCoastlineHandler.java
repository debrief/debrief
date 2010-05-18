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

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;



abstract public class VPFCoastlineHandler extends MWCXMLReader  implements PlottableExporter
{

  private static final String _myType = "vpf_coastline";

  private static MWC.GUI.ToolParent _myParent;

  java.awt.Color _theColor;
  boolean _isVisible;


  public VPFCoastlineHandler()
  {
    // inform our parent what type of class we are
    super(_myType);

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

  public void setParent(MWC.GUI.ToolParent parent)
  {
     _myParent = parent;
  }

  public void elementClosed()
  {
    if(_myParent == null)
      MWC.Utilities.Errors.Trace.trace("TOOLPARENT not set in VPF CoastlineHandler!!!");

    // create a coastline from this data
    String path =_myParent.getProperty(MWC.GUI.Tools.Palette.CreateVPFCoast.COAST_PROPERTY);

    if(path == null)
      path = MWC.GUI.Tools.Palette.CreateVPFCoast.COAST_PATH_DEFAULT;

    MWC.GUI.VPF.CoverageLayer.ReferenceCoverageLayer rcl = MWC.GUI.VPF.LibraryLayer.createReferenceLayer(path);

//    MWC.GUI.Chart.Painters.CoastPainter csp = new MWC.GUI.Chart.Painters.CoastPainter();
    rcl.setColor(_theColor);
    rcl.setVisible(_isVisible);

    addPlottable(rcl);

    // reset our variables
    _theColor = null;
    _isVisible = false;
  }

  abstract public void addPlottable(MWC.GUI.Plottable plottable);


  public void exportThisPlottable(MWC.GUI.Plottable plottable, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {

    MWC.GUI.VPF.CoverageLayer.ReferenceCoverageLayer cl = (MWC.GUI.VPF.CoverageLayer.ReferenceCoverageLayer) plottable;
    Element coast = doc.createElement(_myType);

    // do the visibility
    coast.setAttribute("Visible", writeThis(cl.getVisible()));

    // do the name
    coast.setAttribute("Name", "World Default");

    // do the colour
    ColourHandler.exportColour(cl.getColor(), coast, doc);


    parent.appendChild(coast);
  }



}