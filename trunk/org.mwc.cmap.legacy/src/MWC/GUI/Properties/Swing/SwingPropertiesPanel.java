package MWC.GUI.Properties.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingPropertiesPanel.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: SwingPropertiesPanel.java,v $
// Revision 1.3  2004/10/07 14:23:10  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.2  2004/05/25 15:29:45  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:27  Ian.Mayo
// Initial import
//
// Revision 1.4  2002-10-30 15:36:07+00  ian_mayo
// Better management of tabs
//
// Revision 1.3  2002-05-28 09:25:46+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:35+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-04-12 16:12:01+01  ian_mayo
// hey, looking great!
//
// Revision 1.1  2002-04-11 14:01:28+01  ian_mayo
// Initial revision
//
// Revision 1.9  2002-02-20 10:39:09+00  administrator
// Add new embedded class and interface to ensure that floating toolbars are closed when the tab is closed, and so that the tab is closed with the floating toolbar
//
// Revision 1.8  2002-01-29 07:56:50+00  administrator
// Use MWC.Trace instead of System.out
//
// Revision 1.7  2002-01-24 14:22:32+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.6  2002-01-24 10:51:37+00  administrator
// When a property editor is added, listen for NAME property changes, so that we can update the tab title
//
// Revision 1.5  2002-01-22 15:31:18+00  administrator
// Remember who the toolbar owner is
//
// Revision 1.4  2001-08-31 10:00:48+01  administrator
// If we don't already hold this panel, then add it
//
// Revision 1.3  2001-08-29 19:34:25+01  administrator
// Extend closer
//
// Revision 1.2  2001-08-17 07:58:30+01  administrator
// Provide method to clear up memory leaks
//
// Revision 1.1  2001-08-06 14:39:42+01  administrator
// set the UI of the Toolbar to our SPECIAL ui
//
// Revision 1.0  2001-07-17 08:43:32+01  administrator
// Initial revision
//
// Revision 1.5  2001-07-12 12:12:20+01  novatech
// Pass the ToolParent to any child classes
//
// Revision 1.4  2001-07-05 11:56:17+01  novatech
// Add new method to allow us to remember the object's index before adding panel to toolbar.
// Include an extra "remove" method to try to remove the embedded panel.
// Make addThisInToolbar protected, since it is accessed by higher level functions which remember which panel this new data object is viewed with (to help with Close operation)
//
// Revision 1.3  2001-06-14 15:47:13+01  novatech
// provide better support for floating toolbars - in particular by putting all editor panes into a floating toolbar
//
// Revision 1.2  2001-01-05 09:10:45+00  novatech
// Create type of editor called "Constructor", which requires particular processing from the properties panel (renaming "Apply" to "Build").
//
// Revision 1.1  2001-01-03 13:42:40+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:39  ianmayo
// initial version
//
// Revision 1.8  2000-08-23 09:36:34+01  ian_mayo
// tidied up
//
// Revision 1.7  2000-08-17 10:22:06+01  ian_mayo
// implement comments AND correct signature of 'remove' method
//
// Revision 1.6  2000-04-03 10:56:07+01  ian_mayo
// include Add(component) method
//
// Revision 1.5  2000-01-14 11:57:58+00  ian_mayo
// added method to return the UndoBuffer
//
// Revision 1.4  1999-11-26 15:45:47+00  ian_mayo
// implementing layer management
//
// Revision 1.3  1999-11-25 16:54:03+00  ian_mayo
// tidied up locations
//
// Revision 1.2  1999-11-23 11:05:05+00  ian_mayo
// further introduction of SWING components
//


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JToolBar;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PlainPropertyEditor;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.TabPanel.SwingTabPanel;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI;
import MWC.GUI.Undo.UndoBuffer;

/**
 * Class which implements Swing version of a properties panel
 */
public class SwingPropertiesPanel extends SwingTabPanel implements PropertiesPanel, PropertyChangeListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /**
   * chart to update
   */
  PlainChart _myChart;

  /**
   * the buffer to store the undo operations
   */
  UndoBuffer _theBuffer;

  /**
   * list of objects we are currently editing, so that
   * we can highlight their page instead of opening a new one
   */
  java.util.Hashtable<Object, JPanel> _myPanels = new java.util.Hashtable<Object, JPanel>();

  /**
   * the toolparent we supply to any new panels
   */
  MWC.GUI.ToolParent _theToolParent;

  /**
   * the name of the session we read from, for when the toolbar floats
   */
  private MyMetalToolBarUI.ToolbarOwner _owner;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  /**
   * Constructor for SwingPropertiesPanel
   *
   * @param theChart      the chart we are editing
   * @param theUndoBuffer buffer to store the undo commands
   */
  public SwingPropertiesPanel(PlainChart theChart,
                              UndoBuffer theUndoBuffer,
                              MWC.GUI.ToolParent theToolParent,
                              MyMetalToolBarUI.ToolbarOwner owner)
  {
    super();
    _myChart = theChart;
    _theBuffer = theUndoBuffer;
    _theToolParent = theToolParent;
    _owner = owner;
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public void closeMe()
  {
    // clear our local data
    _myPanels.clear();
    _theBuffer.close();

    // and the objects
    _theBuffer = null;
    _myChart = null;
    _theToolParent = null;
    _myPanels = null;

    // and the parent
    super.closeMe();
  }

  public void remove(Object thisItem)
  {
    java.awt.Component comp = _myPanels.get(thisItem);
    remove(comp);
  }


  /**
   * remove the indicated page from this tabbed panel.
   * Also remove it from the list we are keeping
   *
   * @param thePage the page to be removed
   */
  public void remove(java.awt.Component thePage)
  {
    // remove this panel
    super.remove(thePage);

    // also remove it from our list
    java.util.Enumeration<Object> enumer = _myPanels.keys();
    while (enumer.hasMoreElements())
    {
      Object oj = enumer.nextElement();
      JPanel jp = _myPanels.get(oj);

      if (jp.equals(thePage))
      {
        // now, it seems that since we started to allow floating properties pages, the first
        // remove operation doesn't always work.  Accordingly we use another remove operation
        // which uses the component which got returned from the "Add" operation
        super.remove(jp);

        _myPanels.remove(oj);

        break;
      }
    }
  }

  /**
   * put the specified panel to the top of the stack
   *
   * @param thePanel the panel to put to the top
   */
  public void show(JPanel thePanel)
  {
    // do we hold this?
    int index = super.indexOfComponent(thePanel);

    if (index == -1)
    {
      this.add(thePanel);
    }
    else
      this.setSelectedComponent(thePanel);
  }

  /**
   * We have been asked to show this editor.  First see if we have it open already
   * (by examining the data object), then either show the existing one, or open
   * a fresh one
   *
   * @param theInfo the EditableInfo for this object
   */
  public void addConstructor(Editable.EditorType theInfo, Layer parentLayer)
  {
    // see if we already have this editor open

    JPanel thePanel = _myPanels.get(theInfo.getData());
    if (thePanel == null)
    {
      SwingPropertyEditor2 ap = new SwingPropertyEditor2(theInfo,
                                                         this,
                                                         _myChart,
                                                         _theToolParent,
                                                         parentLayer);

      // set the name of the "build" button
      ap.setNames("Build", null, null);


      thePanel = (JPanel) ap.getPanel();
      this.addTab(theInfo.getName(), thePanel);

      // also remember this object
      _myPanels.put(theInfo.getData(), thePanel);

    }

    // just show it anyway
    this.setSelectedComponent(thePanel);

  }

  /**
   * We have been asked to show this editor.  First see if we have it open already
   * (by examining the data object), then either show the existing one, or open
   * a fresh one
   *
   * @param theInfo the EditableInfo for this object
   */
  public void addEditor(Editable.EditorType theInfo, Layer parentLayer)
  {
    // see if we already have this editor open

    JPanel thePanel = _myPanels.get(theInfo.getData());
    if (thePanel == null)
    {
      SwingPropertyEditor2 ap = new SwingPropertyEditor2(theInfo,
                                                         this,
                                                         _myChart,
                                                         _theToolParent,
                                                         parentLayer);

      thePanel = (JPanel) ap.getPanel();
      thePanel.setName(theInfo.getDisplayName());

      JPanel destination = addThisInToolbar(thePanel);

      // also remember this object
      _myPanels.put(theInfo.getData(), destination);

      // store the holder in the panel, so that we can correctly select it
      thePanel = destination;

      // now, listen out for the name of the panel changing - we are removed as listener by the SwingPropertyEditor
      // in it's close operation
      theInfo.addPropertyChangeListener(this);

    }

    // just show it anyway
    this.setSelectedComponent(thePanel);

  }

  /**
   * method to add the indicated panel inside a floatable toolbar, and adding the initial
   * component itself to our list of panels (so that they can be deleted)
   */
  public JPanel addThisPanel(java.awt.Component thePanel)
  {
    JPanel inserted = addThisInToolbar(thePanel);
    _myPanels.put(thePanel, inserted);
    return inserted;
  }

  protected JPanel addThisInToolbar(java.awt.Component thePanel)
  {
    JToolBar jt = new JToolBar();
    jt.setUI(new MWC.GUI.Tools.Swing.MyMetalToolBarUI(_owner));
    jt.setLayout(new java.awt.GridLayout(1, 0));
    jt.setFloatable(true);
    jt.add(thePanel);
    jt.setName(thePanel.getName());

    // create a panel to contain the toolbar
    JPanel theHolder = new JPanel();
    theHolder.setLayout(new java.awt.BorderLayout());
    theHolder.add("Center", jt);

    this.addTabPanel(thePanel.getName(), true, theHolder);

    this.setSelectedComponent(theHolder);

    return theHolder;
  }


  /**
   * Update the chart
   */
  public void doApply()
  {
    if (_myChart != null)
      _myChart.update();
  }

  /**
   * provide the calling class with our undo buffer
   *
   * @return the undo buffer for this Session
   */
  public UndoBuffer getBuffer()
  {
    return _theBuffer;
  }

  public ToolParent getToolParent()
  {
    return _theToolParent;
  }

  public MyMetalToolBarUI.ToolbarOwner getOwner()
  {
    return _owner;
  }


  /**
   * a property has changed - if it's the name, then change the name of that panel
   */
  public void propertyChange(PropertyChangeEvent evt)
  {
    if (evt.getPropertyName().equals("Name"))
    {
      PlainPropertyEditor.PropertyChangeAction pa = (PlainPropertyEditor.PropertyChangeAction) evt.getSource();
      Object source = pa.getData();
      JPanel panel = _myPanels.get(source);
      String newName = (String) evt.getNewValue();
      panel.setName(newName);

      int index = this.indexOfComponent(panel);
      if (index != -1)
      {
        this.setTitleAt(index, newName);
      }
    }
    // find the panel with the old name
  }


  /** add a new page to us - one which is not an editor (such as the tote)
   * @param p1 the panel to add
   * @return the container for the panel
   */
  /*	public java.awt.Component add(java.awt.Component p1)
    {
      java.awt.Component res = super.add(p1);
      this.setSelectedComponent(res);
      return res;
    }*/


  /**
   * **************************************************************
   * event to indicate that this panel is about to close
   * **************************************************************
   */
  public static interface ClosingEventListener
  {
    public void isClosing();
  }

  /**
   * **************************************************************
   * support for the closeable window interface
   * **************************************************************
   */
  abstract static public class CloseableJPanel extends JPanel
  {

    /**
     * list of objects which want to know if/when we close (in particular toolbars when we are already closing)
     */
    private Vector<ClosingEventListener> _closeListeners;

    /**
     * let somebody listen to us
     */
    public void addClosingListener(SwingPropertiesPanel.ClosingEventListener listener)
    {
      if (_closeListeners == null)
        _closeListeners = new Vector<ClosingEventListener>(1, 1);

      _closeListeners.add(listener);
    }

    /**
     * let somebody stop listening to us
     */
    public void removeClosingListener(SwingPropertiesPanel.ClosingEventListener listener)
    {
      _closeListeners.remove(listener);
    }

    // somebody is telling us to close - to be added by the implementing class
    abstract public void triggerClose();

    // closing - inform the listeners
    public void doClose()
    {
      // if we have any other closing listeners, tell them we are closing
      if (_closeListeners != null)
      {
        Iterator<ClosingEventListener> it = _closeListeners.iterator();
        while (it.hasNext())
        {
          SwingPropertiesPanel.ClosingEventListener listener = it.next();
          listener.isClosing();
        }

        // and empty ourselves out
        _closeListeners.removeAllElements();
        _closeListeners = null;

      }
    }
  }

}
