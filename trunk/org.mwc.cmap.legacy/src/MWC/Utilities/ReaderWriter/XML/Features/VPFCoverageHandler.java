package MWC.Utilities.ReaderWriter.XML.Features;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.GUI.Editable;
import MWC.GUI.VPF.FeaturePainter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import org.w3c.dom.Element;

abstract public class VPFCoverageHandler extends MWCXMLReader
{

  private static final String _myType = "vpf_coverage";

  boolean _isVisible;
  String _description;
  String _type;

  private java.util.Vector<FeaturePainter> _currentFeatures = null;

  public VPFCoverageHandler()
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
    addAttributeHandler(new HandleAttribute("Description")
    {
      public void setValue(String name, String value)
      {
        _description = value;
      }
    });
    addAttributeHandler(new HandleAttribute("Type")
    {
      public void setValue(String name, String value)
      {
        _type = value;
      }
    });
    addHandler(new VPFFeatureHandler()
    {
      public void addFeature(String type, String description, java.awt.Color color, boolean isVisible)
      {
        addThisFeature(type, description, color, isVisible);
      }

    });

  }

  public void addThisFeature(String type, String description, java.awt.Color color, boolean isVisible)
  {
    // create the new feature painter
    FeaturePainter fp = new FeaturePainter(type, description);
    fp.setVisible(isVisible);
    fp.setColor(color);

    // has the list of features been initialised?
    if (_currentFeatures == null)
      _currentFeatures = new java.util.Vector<FeaturePainter>(0, 1);

    // and add to our list of features
    _currentFeatures.add(fp);

  }

  public void elementClosed()
  {
    java.util.Enumeration<FeaturePainter> enumer = null;
    if (_currentFeatures != null)
    {
      enumer = _currentFeatures.elements();
    }
    addCoverage(_type, _description, _isVisible, enumer);

    // reset the internal objects
    _currentFeatures = null;
    _description = null;
    _type = null;
  }

  abstract public void addCoverage(String type, String description, boolean visible, java.util.Enumeration<FeaturePainter> features);


  public static void exportThisCoverage(MWC.GUI.Plottable plottable, org.w3c.dom.Element parent,
                                        org.w3c.dom.Document doc)
  {

    MWC.GUI.VPF.CoverageLayer cl = (MWC.GUI.VPF.CoverageLayer) plottable;
    Element coverage = doc.createElement(_myType);

    // do the visibility
    coverage.setAttribute("Visible", writeThis(cl.getVisible()));

    // do the type
    coverage.setAttribute("Type", cl.getType());

    // do the name
    coverage.setAttribute("Description", cl.getName());

    // do the features
    java.util.Enumeration<Editable> enumer = cl.elements();
    while (enumer.hasMoreElements())
    {
      FeaturePainter fp = (FeaturePainter) enumer.nextElement();
      VPFFeatureHandler.exportThisFeature(fp, coverage, doc);
    }

    parent.appendChild(coverage);
  }


}