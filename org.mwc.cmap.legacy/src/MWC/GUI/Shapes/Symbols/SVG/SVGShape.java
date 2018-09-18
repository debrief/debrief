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

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Errors.Trace;

public class SVGShape extends PlainSymbol
{

  /**
   * Version ID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * our symbol type
   * 
   */
  private final String _svgFileName;

  /**
   * Elements of the shape (Cache)
   */
  private List<SVGElement> _elements;

  /**
   * True if we can rotate the SVG.
   */
  private boolean _canRotate;

  /**
   * the origin, at which we centre the symbol
   * 
   */
  private Point _origin;

  /**
   * 
   * @param svgFileName
   *          SVG File name without extension or path.
   */
  public SVGShape(final String svgFileName)
  {
    _svgFileName = svgFileName;
  }

  public boolean canRotate()
  {
    return _canRotate;
  }

  public void setCanRotate(boolean _canRotate)
  {
    this._canRotate = _canRotate;
  }

  @Override
  public String getType()
  {
    return _svgFileName;
  }

  @Override
  public java.awt.Dimension getBounds()
  {
    final int sWid = (int) (getScaleVal());
    return new java.awt.Dimension(10 * sWid, 10 * sWid);
  }

  @Override
  public void paint(CanvasType dest, WorldLocation theCentre)
  {
    paint(dest, theCentre, .0);
  }

  @Override
  public void paint(final CanvasType dest, final WorldLocation center,
      final double direction)
  {
    if (_elements == null)
    {

      // get the XML document
      final Document doc = getDocument();

      if (doc != null)
      {
        // parse the XML document
        _elements = parseDocument(doc);
      }
    }

    // At this point, we have our file loaded (pre-cached)
    if (_elements == null)

    {
      Trace.trace("Failed to parse SVG file for: " + _svgFileName);
    }
    else
    {
      double directionToUse = .0;
      if (_canRotate)
      {
        directionToUse = direction;
      }

      // create our centre point
      final Point centre = dest.toScreen(center);
      for (SVGElement element : _elements)
      {
        element.render(dest, getScaleVal(), centre, directionToUse, _origin,
            getColor());
      }
    }
  }

  /**
   * Returns the Elements of the symbol
   * 
   * @return
   */
  public List<SVGElement> getElements()
  {
    return _elements;
  }

  /**
   * retrieve the nodes from the SVG document
   * 
   * @param doc
   * @return
   */
  private List<SVGElement> parseDocument(final Document doc)
  {

    final Element svgRoot = doc.getDocumentElement();

    final SVGElementFactory elementFactory = new SVGElementFactory();
    ArrayList<SVGElement> elements = new ArrayList<SVGElement>();
    for (int i = 0; i < svgRoot.getChildNodes().getLength(); i++)
    {
      Node element = svgRoot.getChildNodes().item(i);

      if (element.getNodeType() == Node.ELEMENT_NODE)
      {

        // check it's not the origin node
        Node id = element.getAttributes().getNamedItem("id");
        if (id != null && "origin".equals(id.getNodeValue()))
        {
          // ok, it's the origin marker. get the origin coords
          parseOrigin(element);

          // and determine if the shape should rotate
          parseRotation(element);
        }
        else
        {
          SVGElement newElement = elementFactory.getInstance((Element) element);

          // We are ignoring unknown elements for now.
          if (newElement != null)
          {
            elements.add(newElement);
          }
        }
      }
    }
    return elements;
  }

  /**
   * retrieve the SVG document
   * 
   * @return
   */
  private Document getDocument()
  {
    try
    {
      // remove the svg: prefix, if necesary
      final String fName = _svgFileName.contains("svg:") ? _svgFileName
          .substring(4) : _svgFileName;

      String svgPath = File.separator + fName + SymbolFactory.SVG_EXTENSION;

      InputStream inputStream = SVGShape.class.getResourceAsStream(svgPath);

      if (inputStream == null)
      {
        // Resource doesn't exist
        throw new FileNotFoundException();
      }
      final DocumentBuilderFactory dbFactory = DocumentBuilderFactory
          .newInstance();
      final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      final Document doc = dBuilder.parse(inputStream);

      // read this -
      // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();

      return doc;
    }
    catch (Exception e)
    {
      Trace.trace(e, "Failed to open SVG file " + _svgFileName);
    }

    // failed, drop out
    return null;
  }

  private void parseOrigin(Node element)
  {
    final int x = (int) Double.parseDouble(element.getAttributes().getNamedItem(
        "cx").getNodeValue());
    final int y = (int) Double.parseDouble(element.getAttributes().getNamedItem(
        "cy").getNodeValue());
    _origin = new Point(x, y);
  }

  private void parseRotation(Node element)
  {
    final String style = element.getAttributes().getNamedItem("style")
        .getNodeValue();
    if (style == null || style.length() == 0)
    {
      Trace.trace("Style parameter missing for " + _svgFileName);
    }
    else
    {
      final boolean rotates;
      if ("fill: rgb(255, 0, 0);".equals(style))
      {
        // red - doesn't rotate
        rotates = false;
      }
      else
      {
        // green = does rotate
        rotates = true;
      }

      _canRotate = rotates;
    }

  }

  @Override
  public PlainSymbol create()
  {
    return new SVGShape(_svgFileName);
  }

  public static class SVGShapeTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public SVGShapeTest(final String val)
    {
      super(val);
    }

    public void testLoadingFileDoesntExit()
    {
      final String fileDoesntExist = "fileDoesntExit";
      SVGShape svgShape = new SVGShape(fileDoesntExist);
      svgShape.paint(null, null, 0);

      assertTrue(svgShape.getElements() == null);
    }
  }
}
