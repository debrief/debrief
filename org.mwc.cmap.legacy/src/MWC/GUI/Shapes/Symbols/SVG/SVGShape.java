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
import java.io.IOException;
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
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Errors.Trace;

public class SVGShape extends PlainSymbol
{

  /**
   * Version ID.
   */
  private static final long serialVersionUID = 1L;

  /** our symbol type
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

  /** the origin, at which we centre the symbol
   * 
   */
  @SuppressWarnings("unused")
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
      try
      {
        /**
         * We get the SVG as String
         */

        // TODO. Change this. If we use org.mwc.cmap.core, it gives a cyclic dependency error.
        // final java.net.URL resource = ;
        // SymbolFactory.class.getReso.

        // String svgFilePath =
        // java.nio.file.Paths.get(resource.toURI()).toAbsolutePath().toString();
        //final File fXmlFile = new File(SVGShape.class.getResource("/" + _svgPath).getFile());
        // System.out.println(fXmlFile.getAbsolutePath());
        
        // remove the svg: prefix, if necesary
        final String fName = _svgFileName.contains("svg:") ? _svgFileName.substring(4) : _svgFileName;
        
        String svgPath = fName + SymbolFactory.SVG_EXTENSION;
        
        final File fXmlFile = new File(svgPath);
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

            // check it's not the origin node
            Node id = element.getAttributes().getNamedItem("id");
            if(id != null && "origin".equals(id.getNodeValue()))
            {
              parseOrigin(element);
              parseRotation(element);
            }
            else
            {
              SVGElement newElement = elementFactory.getInstance(
                  (Element) element);

              // We are ignoring unknown elements for now.
              if (newElement != null)
              {
                _elements.add(newElement);
              }
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
    if(_elements == null)
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
      final java.awt.Point centre = dest.toScreen(center);
      for (SVGElement element : _elements)
      {
        element.render(dest, getScaleVal(), centre, directionToUse);
      }
    }
  }

  private void parseOrigin(Node element)
  {
    final int x = Integer.parseInt(element.getAttributes().getNamedItem("cx").getNodeValue());
    final int y = Integer.parseInt(element.getAttributes().getNamedItem("cy").getNodeValue());
    _origin = new Point(x, y);
  }

  private void parseRotation(Node element)
  {
     final String style = element.getAttributes().getNamedItem("style").getNodeValue();
     if(style == null || style.length() == 0)
     {
       Trace.trace("Style parameter missing for " + _svgFileName);
     }
     else
     {
       final boolean rotates;
       if(style.equals("fill: rgb(255, 0, 0);"))
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

}
