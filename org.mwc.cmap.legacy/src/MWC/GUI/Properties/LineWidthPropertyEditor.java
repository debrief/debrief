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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Properties;
public class LineWidthPropertyEditor extends AbstractPropertyEditor
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////
  private final String _stringTags[] =
  {
                     "Hairwidth",
                     "1 pixels",
                     "2 pixels",
                     "3 pixels",
                     "4 pixels",
                     "5 pixels",
  };

  ////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////
  public String[] getTags()
  {
    return _stringTags;
  }



}
