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
package MWC.GUI.Properties.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingCustomEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SwingCustomEditor.java,v $
// Revision 1.2  2004/05/25 15:29:35  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:26  Ian.Mayo
// Initial import
//
// Revision 1.4  2002-11-13 13:14:34+00  ian_mayo
// minor tidying
//
// Revision 1.3  2002-10-11 08:32:33+01  ian_mayo
// Only remove the property editor if we have some
//
// Revision 1.2  2002-05-28 09:25:47+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:33+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:25+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-11-14 19:51:24+00  administrator
// tidy methods & insert comments
//
// Revision 1.0  2001-07-17 08:43:32+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-12 12:07:56+01  novatech
// Store the toolparent, to that any child editors can access it if they need to (particularly to set cursor busy)
//
// Revision 1.2  2001-07-09 13:58:49+01  novatech
// create doClose event, passed when editor is closed
//
// Revision 1.1  2001-01-03 13:42:38+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:35  ianmayo
// initial version
//
// Revision 1.4  2000-04-05 08:35:06+01  ian_mayo
// add Reset method
//
// Revision 1.3  2000-01-18 15:07:20+00  ian_mayo
// added accessors for Chart & Properties Panel
//
// Revision 1.2  1999-11-23 11:05:05+00  ian_mayo
// further introduction of SWING components
//

import java.beans.Customizer;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;

abstract public class SwingCustomEditor extends JPanel implements Customizer
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  protected PropertiesPanel _thePanel;
  protected MWC.GUI.ToolParent _theToolParent;
  protected Layers _theLayers;

  protected final java.beans.PropertyChangeSupport _pSupport;

  public SwingCustomEditor()
  {
    _pSupport = new java.beans.PropertyChangeSupport(this);
  }

  final public void setObject(final Object data,
                        final ToolParent theParent,
                        final Layers theLayers,
                        final PropertiesPanel thePanel)
  {
    _theLayers = theLayers;
    _thePanel = thePanel;
    _theToolParent = theParent;

    setObject(data);
  }

  /**  get the toolparent for this panel
   *
   */
  final public ToolParent getToolParent()
  {
    return _theToolParent;
  }

  /** get the chart we are painting to
   *
   */
	final public Layers getLayers()
	{
		return _theLayers;
	}

  /** get the properties panel we are displayed inside
   *
   */
	final public PropertiesPanel getPanel()
	{
		return _thePanel;
	}

  /** update the editor with the supplied object
   *
   */
  abstract public void setObject(Object data);


  /** add the indicated property event
   *
   */
  final public void addPropertyChangeListener(final PropertyChangeListener listener)
  {
    _pSupport.addPropertyChangeListener(listener);
  }

  /** remove the indicated property listener
   *
   */
  final public void removePropertyChangeListener(final PropertyChangeListener listener)
  {
    _pSupport.removePropertyChangeListener(listener);
  }

  /** fire the indicated event
   *
   */
  final protected void fireModified(final String name, final Object oldVal, final Object newVal)
  {
    _pSupport.firePropertyChange(name, oldVal, newVal);
  }

	public void doReset()
	{
		// don't do anything, but allow child class to process reset
	}

  public void doClose()
  {
		// remove the listeners
    PropertyChangeListener[] listeners = _pSupport.getPropertyChangeListeners();
    for(PropertyChangeListener l: listeners)
    {
      _pSupport.removePropertyChangeListener(l);
    }
  }

}
