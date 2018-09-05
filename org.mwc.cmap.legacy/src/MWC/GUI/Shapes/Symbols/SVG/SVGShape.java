/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI.Shapes.Symbols.SVG;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public class SVGShape extends PlainSymbol
{

  /**
   * Version ID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Path of the SVG we are going to load.
   */
  private String _svgPath;

  /**
   * Elements of the shape (Cache)
   */
  private List<SVGElement> _elements;

  /**
   * True if we can rotate the SVG.
   */
  private boolean _canRotate;

  public SVGShape(final String svgPath)
  {
    _svgPath = svgPath;
  }

  public boolean is_canRotate()
  {
    return _canRotate;
  }

  public void set_canRotate(boolean _canRotate)
  {
    this._canRotate = _canRotate;
  }

  @Override
  public String getType()
  {
    return "SVG Shape";
  }

  @Override
  public Dimension getBounds()
  {
    return null;
  }

  @Override
  public void paint(final CanvasType dest, final WorldLocation center,
      final double direction)
  {
    if (_elements == null)
    {
      try
      {
        /**
         * We get the SVG as String
         */
        final File fXmlFile = new File(_svgPath);
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory
            .newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document doc = dBuilder.parse(fXmlFile);

        // read this -
        // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        final Element svgRoot = doc.getDocumentElement();

        final SVGElementFactory elementFactory = new SVGElementFactory();
        _elements = new ArrayList<>();
        for (int i = 0; i < svgRoot.getChildNodes().getLength(); i++)
        {
          Node element = svgRoot.getChildNodes().item(i);

          if (element.getNodeType() == Node.ELEMENT_NODE)
          {

            SVGElement newElement = elementFactory.getInstance((Element)element);

            // We are ignoring for now unknown elements.
            if (newElement != null)
            {
              _elements.add(newElement);
            }
          }
        }
      }
      catch (IOException e)
      {
        // TODO I coudln't read the given file.
        e.printStackTrace();
      }
      catch (SAXException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (ParserConfigurationException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    // At this point, we have our file loaded (pre-cached)

    double directionToUse = .0;
    if (_canRotate)
    {
      directionToUse = direction;
    }

    // create our centre point
    final java.awt.Point centre = dest.toScreen(center);
    for (SVGElement element : _elements)
    {
      element.render(dest, getScaleVal(), centre, directionToUse);
    }
  }

}
