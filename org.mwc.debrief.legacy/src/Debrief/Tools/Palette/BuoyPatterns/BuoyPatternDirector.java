/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
// $RCSfile: BuoyPatternDirector.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.1.1.2 $
// $Log: BuoyPatternDirector.java,v $
// Revision 1.1.1.2  2003/07/21 14:48:48  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:10+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 09:25:10+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:47+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:42+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:16+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-18 13:19:43+00  novatech
// Initial revision
//
// Revision 1.1  2001-01-17 13:21:02+00  novatech
// Initial revision
//
// Revision 1.2  2001-01-08 11:39:42+00  novatech
// perform correct "Factory" processing
//
// Revision 1.1  2001-01-03 16:02:37+00  novatech
// Initial revision
//
package Debrief.Tools.Palette.BuoyPatterns;

import java.util.*;

final class BuoyPatternDirector
{

  //////////////////////////////////////////
  // Member variables
  //////////////////////////////////////////

  //////////////////////////////////////////
  // Constructor
  //////////////////////////////////////////

  //////////////////////////////////////////
  // Member functions
  //////////////////////////////////////////


  public final PatternBuilderType createBuilder(final MWC.GenericData.WorldLocation centre,
                                          final String type,
                                          final MWC.GUI.Properties.PropertiesPanel thePanel,
                                          final MWC.GUI.Layers theData)
  {
    PatternBuilderType res = null;
    if(type.equals("Field"))
    {
      res = new FieldBuilder(centre, thePanel, theData);
    }
    else if (type.equals("Circle"))
    {
      res = new CircleBuilder(centre, thePanel, theData);
    }
    else if (type.equals("Barrier"))
    {
      res = new BarrierBuilder(centre, thePanel, theData);
    }
    else if (type.equals("Wedge"))
    {
      res = new WedgeBuilder(centre, thePanel, theData);
    }
    else if (type.equals("Arc"))
    {
      res = new ArcBuilder(centre, thePanel, theData);
    }

    return res;
  }

  public final Object[] getPatterns()
  {
    final Vector<String> res = new Vector<String>(0,1);
    Object[] res2 = new String[]{"d1", "d2"};
    res.addElement("Field");
    res.addElement("Barrier");
    res.addElement("Wedge");
    res.addElement("Circle");
    res.addElement("Arc");
    res2 = res.toArray(res2);
    return res2;

  }
}