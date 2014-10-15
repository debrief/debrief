/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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



abstract public class ScaleHandler extends MWCXMLReader  implements PlottableExporter
{

  java.awt.Color _theColor;
  boolean _isVisible;
  long _ScaleMax;
  long _ScaleStep;
  boolean _AutoMode;
  String _LabelLocation;
  String _displayUnits;

  MWC.GUI.Properties.DiagonalLocationPropertyEditor _dp =
    new MWC.GUI.Properties.DiagonalLocationPropertyEditor();

  MWC.GUI.Properties.UnitsPropertyEditor _up =
    new MWC.GUI.Properties.UnitsPropertyEditor();

  public ScaleHandler()
  {
    // inform our parent what type of class we are
    super("scale");

    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      public void setValue(final String name, final boolean value)
      {
        _isVisible = value;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("AutoMode")
    {
      public void setValue(final String name, final boolean value)
      {
        _AutoMode = value;
      }
    });
    addAttributeHandler(new HandleAttribute("ScaleMax")
    {
      public void setValue(final String name, final String value)
      {
        _ScaleMax = Long.valueOf(value).longValue();
      }
    });
    addAttributeHandler(new HandleAttribute("ScaleStep")
    {
      public void setValue(final String name, final String value)
      {
        _ScaleStep = Long.valueOf(value).longValue();
      }
    });
    addAttributeHandler(new HandleAttribute("Location")
    {
      public void setValue(final String name, final String value)
      {
        _LabelLocation = value;
      }
    });
    addAttributeHandler(new HandleAttribute("DisplayUnits")
    {
      public void setValue(final String name, final String value)
      {
        _displayUnits = value;
      }
    });
    addHandler(new ColourHandler()
    {
      public void setColour(final java.awt.Color color)
      {
        _theColor = color;
      }
    });



  }

  public void elementClosed()
  {
    // create a Scale from this data
    final MWC.GUI.Chart.Painters.ScalePainter csp = new MWC.GUI.Chart.Painters.ScalePainter();
    csp.setColor(_theColor);
    csp.setVisible(_isVisible);
    csp.setAutoMode(_AutoMode);
    csp.setScaleMax(new Long(_ScaleMax));
    csp.setScaleStep(new Long(_ScaleStep));
    if(_LabelLocation != null)
    {
      _dp.setAsText(_LabelLocation);
      csp.setLocation((Integer)_dp.getValue());
    }
    if(_displayUnits != null)
      csp.setDisplayUnits(_displayUnits);

    addPlottable(csp);

    // reset our variables
    _theColor = null;
    _isVisible = false;
    _LabelLocation = null;
    _displayUnits = null;

  }

  abstract public void addPlottable(MWC.GUI.Plottable plottable);


  public void exportThisPlottable(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {

    final MWC.GUI.Chart.Painters.ScalePainter csp = (MWC.GUI.Chart.Painters.ScalePainter) plottable;
    final Element scale = doc.createElement("scale");

    // do the visibility
    scale.setAttribute("Visible", writeThis(csp.getVisible()));
    scale.setAttribute("Name", "World Default");
    scale.setAttribute("ScaleMax", writeThis(csp.getScaleMax().longValue()));
    scale.setAttribute("ScaleStep", writeThis(csp.getScaleStep().longValue()));
    scale.setAttribute("AutoMode", writeThis(csp.getAutoMode()));

    // and the units
    scale.setAttribute("DisplayUnits", csp.getDisplayUnits());

    // and the scale location
    _dp.setValue(csp.getLocation());
    final String tmp = _dp.getAsAbbreviatedText();
    scale.setAttribute("Location", tmp);

    // do the colour
    ColourHandler.exportColour(csp.getColor(), scale, doc);


    parent.appendChild(scale);
  }



}