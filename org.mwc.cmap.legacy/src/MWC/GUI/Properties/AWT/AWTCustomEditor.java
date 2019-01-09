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
package MWC.GUI.Properties.AWT;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTCustomEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTCustomEditor.java,v $
// Revision 1.2  2004/05/25 15:29:19  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:25  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:25+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:45+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:37+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:32+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:42+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:42+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:24  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:36:45+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:06:02+01  administrator
// Initial revision
//

import java.awt.Panel;
import java.beans.Customizer;
import java.beans.PropertyChangeListener;

import MWC.GUI.Properties.PropertiesPanel;

abstract public class AWTCustomEditor extends Panel implements Customizer
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  protected PropertiesPanel _thePanel;
  
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public void setObject(final Object data, 
                        final PropertiesPanel thePanel)
  {
    _thePanel = thePanel;
    setObject(data);    
  }
  

  abstract public void setObject(Object data);

  public void addPropertyChangeListener(final PropertyChangeListener p1)
  {
  }

  public void removePropertyChangeListener(final PropertyChangeListener p1)
  {
  }
}
