/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI.Properties;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ColorPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: ColorPropertyEditor.java,v $
// Revision 1.3  2004/10/07 14:23:12  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.2  2004/05/25 15:28:44  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:23  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-05-08 13:49:41+01  ian_mayo
// Convert to standalone (non-abstract) class
//
// Revision 1.3  2003-03-03 14:05:55+00  ian_mayo
// Initial support for custom colour editors
//
// Revision 1.2  2002-05-28 09:25:44+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:39+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:36+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-29 19:36:47+01  administrator
// Make the list of vectors static
//
// Revision 1.0  2001-07-17 08:43:48+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-26 11:20:12+00  novatech
// retrieve the string value of the current value if we can
//
// Revision 1.1  2001-01-03 13:42:46+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:08  ianmayo
// initial version
//
// Revision 1.6  2000-10-16 11:12:09+01  ian_mayo
// added colours used in Replay files
//
// Revision 1.5  2000-08-18 10:06:49+01  ian_mayo
// <>
//
// Revision 1.4  2000-08-16 14:13:23+01  ian_mayo
// make NamedColour public, so the Swing child class can see it
//
// Revision 1.3  2000-08-15 15:22:32+01  ian_mayo
// add useful colours
//
// Revision 1.2  2000-08-15 11:43:59+01  ian_mayo
// tidied up & allowed GUI implementation
//
// Revision 1.1  1999-10-12 15:36:49+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:42+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-23 14:03:57+01  administrator
// Initial revision
//

import java.awt.Color;
import java.beans.PropertyEditorSupport;
import java.util.Enumeration;
import java.util.Vector;

/**
 * class providing GUI-independent editing of colours
 */
public class ColorPropertyEditor extends PropertyEditorSupport
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /**
   * current value
   */
  protected Color _theColor;

  /**
   * list of values to choose from
   */
  protected static Vector<NamedColor> _theColors;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  /**
   * constructor, which initialises the list of colours
   */
  public ColorPropertyEditor()
  {
    if (_theColors == null)
      _theColors = createColors();
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////


  /**
   * initialise the list of colours
   */
  protected Vector<NamedColor> createColors()
  {
    final Vector<NamedColor> theColors = new Vector<NamedColor>();
    theColors.addElement(new NamedColor("Red", Color.red));
    theColors.addElement(new NamedColor("Blue", Color.blue));
    theColors.addElement(new NamedColor("Green", Color.green));
    theColors.addElement(new NamedColor("Yellow", Color.yellow));
    theColors.addElement(new NamedColor("Magenta", Color.magenta));
    theColors.addElement(new NamedColor("Purple", new Color(169, 1, 132)));
    theColors.addElement(new NamedColor("Orange", Color.orange));
    theColors.addElement(new NamedColor("Brown", new Color(188, 93, 6)));
    theColors.addElement(new NamedColor("Cyan", Color.cyan));
    theColors.addElement(new NamedColor("Light Green", new Color(100, 240, 100)));
    theColors.addElement(new NamedColor("Gold", new Color(230, 200, 20)));
    theColors.addElement(new NamedColor("Pink", Color.pink));
    theColors.addElement(new NamedColor("Light Grey", Color.lightGray));
    theColors.addElement(new NamedColor("Grey", Color.gray));
    theColors.addElement(new NamedColor("Dark Grey", Color.darkGray));
    theColors.addElement(new NamedColor("White", Color.white));
    theColors.addElement(new NamedColor("Black", Color.black));

    return theColors;
  }


  /**
   * update the current value
   *
   * @param p1 colour to use
   */
  public void setValue(final Object p1)
  {
    if (p1 instanceof Color)
    {
      _theColor = (Color) p1;
    }
  }

  /**
   * find the current value as a named colour
   *
   * @return current NamedColor
   */
  protected NamedColor getNamedColor()
  {
    NamedColor res = null;
    final Enumeration<NamedColor> enumer = _theColors.elements();
    while (enumer.hasMoreElements())
    {
      final NamedColor cl = (NamedColor) enumer.nextElement();

      if (cl.color.equals(_theColor))
      {
      	// cool, found it
        res = cl;
        break;
      }
    }

    // are we still trying to find a color?
    if(res == null)
    {
    	res = new NamedColor("Custom", _theColor);
    }
    

    return res;
  }

  /**
   * whether we can provide a custom editor component
   *
   * @return yes, of course
   */
  public boolean supportsCustomEditor()
  {
    return false;
  }

  /**
   * get the current colour as a text string
   *
   * @return current colour as a string
   */
  public String getAsText()
  {
    String res = null;

    // try to get the name of the current colour
    final NamedColor val = getNamedColor();
    // did we find it
    if (val != null)
      res = val.name;

    return res;
  }

  /**
   * get the list of colours as a String array - used to populate a menu box
   *
   * @return String list of colours
   */
  public String[] getTags()
  {
    final int num = _theColors.size();
    final String res[] = new String[num];
    for (int i = 0; i < num; i++)
    {
      final NamedColor cl = (NamedColor) _theColors.elementAt(i);
      res[i] = cl.name;
    }

    return res;
  }

  /**
   * user has selected one of the String tags, update accordingly
   *
   * @param p1 new value as String
   */
  public void setAsText(final String p1)
  {
    final Enumeration<NamedColor> enumer = _theColors.elements();
    while (enumer.hasMoreElements())
    {
      final NamedColor cl = (NamedColor) enumer.nextElement();
      if (cl.name.equals(p1))
      {
        setValue(cl.color);
        break;
      }
    }
  }


  /**
   * return the current colour
   *
   * @return current (selected) colour
   */
  public Object getValue()
  {
    return _theColor;
  }

  ////////////////////////////////////////////////////////////////////
  // nested classes
  ////////////////////////////////////////////////////////////////////

  /**
   * inner class, which contains a colour together with its name
   */
  public class NamedColor
  {
    /**
     * name of this colour
     */
    final public String name;
    /**
     * colour of this colour
     */
    final Color color;

    /**
     * constructor, sets member values
     *
     * @param theName  name to use
     * @param theColor colour to use
     */
    public NamedColor(final String theName,
                      final Color theColor)
    {
      name = theName;
      color = theColor;
    }

    /**
     * return the current colour as a String
     *
     * @return name of this colour
     */
    public String toString()
    {
      return name;
    }

    /**
     * get the current colour
     *
     * @return the color
     */
    public Color getColor()
    {
      return color;
    }

  }

}

