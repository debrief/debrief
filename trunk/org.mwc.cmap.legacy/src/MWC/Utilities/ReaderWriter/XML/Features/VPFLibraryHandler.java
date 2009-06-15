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

import MWC.GUI.Editable;
import MWC.GUI.VPF.CoverageLayer;
import MWC.GUI.VPF.DebriefFeatureWarehouse;
import MWC.GUI.VPF.FeaturePainter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

import com.bbn.openmap.layer.vpf.LibrarySelectionTable;


abstract public class VPFLibraryHandler extends MWCXMLReader
{

  private static final String _myType = "vpf_library";


  boolean _isVisible;
  String _myName;

  private java.util.Vector<CoverageLayer> _myCoverages = null;

  public VPFLibraryHandler()
  {
    // inform our parent what type of class we are
    super(_myType);

    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, String value)
      {
        _myName = value;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      public void setValue(String name, boolean value)
      {
        _isVisible = value;
      }
    });
    addHandler(new VPFCoverageHandler()
    {
      public void addCoverage(String type, String description, boolean visible, java.util.Enumeration<FeaturePainter> features)
      {
        addThisCoverage(type, description, visible, features);
      }
    });

  }

  abstract public com.bbn.openmap.layer.vpf.LibrarySelectionTable getLST(String name);

  abstract public DebriefFeatureWarehouse getWarehouse();

  abstract public void addLibrary(String name, boolean visible, java.util.Vector<CoverageLayer> coverages);

  void addThisCoverage(String type, String description, boolean visible, java.util.Enumeration<FeaturePainter> features)
  {
    // do we have our list?
    if (_myCoverages == null)
      _myCoverages = new java.util.Vector<CoverageLayer>(0, 1);

    // create this coverage
    LibrarySelectionTable lsTable = getLST(_myName);
    if(lsTable != null)
    {
      CoverageLayer cl = new CoverageLayer(lsTable, getWarehouse(), type);

      // format the coverage
      cl.setVisible(visible);
      cl.setDescription(description);

      // are there any features?
      if (features != null)
      {
        // add the features
        while (features.hasMoreElements())
        {
          FeaturePainter fp = (FeaturePainter) features.nextElement();
          cl.add(fp);
        }
      }

      _myCoverages.add(cl);
    }
  }


  public void elementClosed()
  {

    // add ourselves to the parent layer
    addLibrary(_myName, _isVisible, _myCoverages);

    // reset the data
    _myCoverages = null;
    _myName = null;
  }


  public static void exportThisPlottable(MWC.GUI.Plottable plottable, org.w3c.dom.Element parent,
                                         org.w3c.dom.Document doc)
  {

    MWC.GUI.VPF.LibraryLayer ll = (MWC.GUI.VPF.LibraryLayer) plottable;
    Element coast = doc.createElement(_myType);

    // do the visibility
    coast.setAttribute("Visible", writeThis(ll.getVisible()));

    // do the name
    coast.setAttribute("Name", ll.getName());

    // now pass throuth the coverages, outputting each one
    java.util.Enumeration<Editable> enumer = ll.elements();
    while (enumer.hasMoreElements())
    {
      CoverageLayer cl = (CoverageLayer) enumer.nextElement();
      VPFCoverageHandler.exportThisCoverage(cl, coast, doc);
    }


    parent.appendChild(coast);
  }


}