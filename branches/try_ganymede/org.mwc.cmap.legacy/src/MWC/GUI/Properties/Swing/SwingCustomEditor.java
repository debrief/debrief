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

import javax.swing.*;
import java.beans.*;
import MWC.GUI.*;
import MWC.GUI.Properties.*;

abstract public class SwingCustomEditor extends JPanel implements Customizer
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  protected PlainChart _theChart;
  protected PropertiesPanel _thePanel;
  protected MWC.GUI.ToolParent _theToolParent;

  protected java.beans.PropertyChangeSupport _pSupport;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  final public void setObject(Object data,
                        PlainChart theChart,
                        ToolParent theParent,
                        PropertiesPanel thePanel)
  {
    _theChart = theChart;
    _thePanel = thePanel;
    _theToolParent = theParent;

    _pSupport = new java.beans.PropertyChangeSupport(this);

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
	final public PlainChart getChart()
	{
		return _theChart;
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
  final public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    _pSupport.addPropertyChangeListener(listener);
  }

  /** remove the indicated property listener
   *
   */
  final public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    if(_pSupport != null)
      _pSupport.removePropertyChangeListener(listener);
  }

  /** fire the indicated event
   *
   */
  final protected void fireModified(String name, Object oldVal, Object newVal)
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
    _pSupport = null;
  }

}
