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
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;



abstract public class VPFFeatureHandler extends MWCXMLReader
{

  private static final String _myType = "vpf_feature";

  java.awt.Color _theColor;
  String _theType;
  String _theDescription;
  boolean _isVisible;


  public VPFFeatureHandler()
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
    addAttributeHandler(new HandleAttribute("Type")
    {
      public void setValue(String name, String value)
      {
        _theType = value;
      }
    });
    addAttributeHandler(new HandleAttribute("Description")
    {
      public void setValue(String name, String value)
      {
        _theDescription = value;
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
    // pass the data to the parent
    addFeature(_theType, _theDescription, _theColor, _isVisible);

    // reset the parameters
    _theType = null;
    _theDescription = null;
    _theColor = null;
    _isVisible = false;
  }

  abstract public void addFeature(String type, String description, java.awt.Color color, boolean isVisible);


  public static void exportThisFeature(MWC.GUI.Plottable plottable, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {

    MWC.GUI.VPF.FeaturePainter fp = (MWC.GUI.VPF.FeaturePainter) plottable;
    Element feature = doc.createElement(_myType);

    // do the visibility
    feature.setAttribute("Visible", writeThis(fp.getVisible()));

    // do the feature type
    feature.setAttribute("Type", fp.getFeatureType());

    // do the name
    feature.setAttribute("Description", fp.getName());

    // do the colour
    ColourHandler.exportColour(fp.getColor(), feature, doc);

    // and store the data
    parent.appendChild(feature);
  }



}