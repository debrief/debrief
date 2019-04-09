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
// $RCSfile: PlainPropertyEditor.java,v $
// @author $Author: ian $
// @version $Revision: 1.5 $
// $Log: PlainPropertyEditor.java,v $
// Revision 1.5  2004/10/16 13:51:09  ian
// GUI support for report window when applicable - together with listening for report events when applicable
//
// Revision 1.4  2004/10/07 14:23:12  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.3  2004/08/26 11:01:55  Ian.Mayo
// Implement core editable property testing
//
// Revision 1.2  2004/05/25 15:29:07  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:24  Ian.Mayo
// Initial import
//
// Revision 1.11  2003-05-08 13:48:48+01  ian_mayo
// Refactor property manager stuff down to child classes
//
// Revision 1.10  2003-03-25 15:50:23+00  ian_mayo
// Remove unused imports
//
// Revision 1.9  2003-01-14 09:09:00+00  ian_mayo
// Improve error trace messages
//
// Revision 1.8  2003-01-13 12:42:35+00  ian_mayo
// Better diagnostic messages when failing to update property
//
// Revision 1.7  2002-11-25 14:39:18+00  ian_mayo
// Add comments
//
// Revision 1.6  2002-07-08 11:52:48+01  ian_mayo
// <>
//
// Revision 1.5  2002-06-05 12:56:26+01  ian_mayo
// unnecessarily loaded
//
// Revision 1.4  2002-05-31 16:23:52+01  ian_mayo
// Provide and call doClose method for editors which use chart
//
// Revision 1.3  2002-05-28 09:25:42+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:42+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-04-12 16:12:01+01  ian_mayo
// hey, looking great!
//
// Revision 1.1  2002-04-11 14:01:44+01  ian_mayo
// Initial revision
//
// Revision 1.7  2002-01-24 14:22:29+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.6  2002-01-24 10:49:34+00  administrator
// Store the data object in the PropertyChangeAction (so that the PropertiesPanel knows which panel it needs to update following a name change)
//
// Revision 1.5  2001-11-14 19:52:15+00  administrator
// Remember the ToolParent and provide support for child classes which want to access it
//
// Revision 1.4  2001-10-22 11:27:15+01  administrator
// Handle instance where we have no properties to edit, but where a custom editor has been supplied (such as the layer manager)
//
// Revision 1.3  2001-10-03 16:01:23+01  administrator
// Add support for displaying message to indicate no editable properties found
//
// Revision 1.2  2001-08-29 19:36:10+01  administrator
// Hide public data
//
// Revision 1.1  2001-08-21 12:07:32+01  administrator
// Make property editor non-static, and improve setting object data with null value
//
// Revision 1.0  2001-07-17 08:46:16+01  administrator
// Initial revision
//
// Revision 1.10  2001-07-12 12:11:21+01  novatech
// store the toolparent, so that we can pass it's details to the child editors if they want it
//
// Revision 1.9  2001-07-09 14:09:27+01  novatech
// minor tidying
//
// Revision 1.8  2001-06-04 09:33:56+01  novatech
// provide interface for classes which want to know about the properties panel
//
// Revision 1.7  2001-01-18 13:20:39+00  novatech
// improve error reporting
//
// Revision 1.6  2001-01-17 13:26:58+00  novatech
// add debug lines
//
// Revision 1.5  2001-01-15 11:18:34+00  novatech
// do an update after Redo operation
//
// Revision 1.4  2001-01-05 10:38:55+00  novatech
// only perform PropertyChange action if there any changes to make
//
// Revision 1.3  2001-01-05 09:10:16+00  novatech
// implement "UpdatesCompleted" call
//
// Revision 1.2  2001-01-04 14:04:32+00  novatech
// tidying up
//
// Revision 1.1  2001-01-03 13:42:48+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:18  ianmayo
// initial version
//
// Revision 1.23  2000-10-09 13:35:48+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.22  2000-10-03 14:14:24+01  ian_mayo
// remove d-line
//
// Revision 1.21  2000-09-21 09:06:43+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.20  2000-08-18 13:35:46+01  ian_mayo
// fire property changes
//
// Revision 1.19  2000-08-18 10:06:51+01  ian_mayo
// <>
//
// Revision 1.18  2000-08-15 15:28:07+01  ian_mayo
// check that there are editable parameters for this object
//
// Revision 1.17  2000-08-09 16:02:40+01  ian_mayo
// correction which was stopping user setting value back to the original one
//
// Revision 1.16  2000-07-07 10:00:52+01  ian_mayo
// switch from "==" to ".equals" when testing to see if a value has been updated
//
// Revision 1.15  2000-06-05 14:18:44+01  ian_mayo
// only do an update if that specific parameter has been edited
//
// Revision 1.14  2000-04-19 11:40:20+01  ian_mayo
// remove Chart reference in updates
//
// Revision 1.13  2000-04-03 10:57:38+01  ian_mayo
// clear the list of modifications after we have applied them,
// re-insert "read" of additional beaninfo,
// don't show "Expert" properties in additional beaninfos
//
// Revision 1.12  2000-03-27 14:40:47+01  ian_mayo
// check-out preventing property editor from loading in properties from "additional" beans
//
// Revision 1.11  2000-03-14 09:57:05+00  ian_mayo
// Register LongEditor class
//
// Revision 1.10  2000-03-08 14:28:45+00  ian_mayo
// handle Editable not returning any properties (it may still return methods and additionals)
//
// Revision 1.9  2000-02-14 16:51:19+00  ian_mayo
// replaced code to show editable parameters for parent objects
//
// Revision 1.8  2000-02-04 16:06:15+00  ian_mayo
// Don't show list of "AdditionalBeaninfo" editables
//
// Revision 1.7  2000-02-03 15:08:05+00  ian_mayo
// First issue to Devron (modified files are mostly related to WMF)
//
// Revision 1.6  2000-01-14 11:58:42+00  ian_mayo
// Process and methods from the editable
//
// Revision 1.5  1999-11-26 15:45:05+00  ian_mayo
// adding toString method
//
// Revision 1.4  1999-11-18 11:10:03+00  ian_mayo
// move AWT/Swing specific behaviour into separate classes
//
// Revision 1.3  1999-11-11 18:16:32+00  ian_mayo
// register new editor (date)
//
// Revision 1.2  1999-10-13 17:18:08+01  ian_mayo
// Recognise additional bean descriptors, and create editor entries for these in addition to core properties.
//
// Revision 1.1  1999-10-12 15:36:51+01  ian_mayo
// Initial revision
//
// Revision 1.4  1999-08-17 08:15:01+01  administrator
// handle custom editors
//
// Revision 1.3  1999-08-04 09:43:06+01  administrator
// make tools serializable
//
// Revision 1.2  1999-07-27 12:08:50+01  administrator
// updating label after DIALOG panel updates
//
// Revision 1.1  1999-07-27 10:50:43+01  administrator
// Initial revision
//
// Revision 1.5  1999-07-27 09:26:13+01  administrator
// switching to bean-based editing
//
// Revision 1.4  1999-07-23 14:03:50+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.3  1999-07-19 12:39:42+01  administrator
// Added painting to a metafile
//
// Revision 1.2  1999-07-16 10:01:46+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-12 08:09:27+01  administrator
// Initial revision
//

package MWC.GUI.Properties;

import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.hasPropertyListeners;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GUI.Undo.UndoBuffer;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * Class defining GUI-independent part of property editing. AWT Portion is implemented in @see
 * MWC.GUI.Properties.AWT.AWTPropertyEditor Property editing works roughly like this:
 */

abstract public class PlainPropertyEditor implements PropertyChangeListener
{
  //////////////////////////////////////////////////
  // nested interface for custom editors which want
  // to know about the layers object
  //////////////////////////////////////////////////
  static public interface EditorUsesLayers
  {
    /**
     * this is the internal layers object
     *
     * @param theLayers
     *          the layers object
     */
    public void setLayers(Layers theLayers);
  }

  //////////////////////////////////////////////////
  // nested interface for custom editors which want
  // to know about the properties window
  //////////////////////////////////////////////////
  static public interface EditorUsesPropertyPanel
  {
    /**
     * this is the property panel we're using
     *
     * @param thePanel
     */
    public void setPanel(PropertiesPanel thePanel);
  }

  //////////////////////////////////////////////////
  // nested interface for custom editors which want
  // to know about the tool parent
  //////////////////////////////////////////////////
  static public interface EditorUsesToolParent
  {
    /**
     * here's the data
     *
     * @param theParent
     *          the parent object
     */
    public void setParent(ToolParent theParent);
  }

  //////////////////////////////////////////////////
  // the action itself - we take the chart to display the results on
  // and the list of property changes made in this action
  ///////////////////////////////////////////////////
  public class PropertyChangeAction implements Action
  {
    /**
     * the list of things to change
     */
    protected Vector<PropertyChangeItem> _theMods;

    /**
     * the item being edited
     */
    protected Object _theData1;

    public PropertyChangeAction(
        final Vector<PropertyChangeItem> theModifications, final Object theData)
    {
      // take a copy of the vector, not the vector itself
      _theMods = copyVector(theModifications);

      // store the object
      _theData1 = theData;
    }

    protected Vector<PropertyChangeItem> copyVector(
        final Vector<PropertyChangeItem> other)
    {
      final Vector<PropertyChangeItem> vector = new Vector<PropertyChangeItem>(
          other.size(), 1);
      final Vector<PropertyChangeItem> res = vector;
      final Enumeration<PropertyChangeItem> enumer = other.elements();
      while (enumer.hasMoreElements())
      {
        final PropertyChangeItem oldP = enumer.nextElement();
        final PropertyChangeItem newP = new PropertyChangeItem(oldP.data,
            oldP.setter, oldP.newValue, oldP.oldValue, oldP.editorInfo,
            oldP.propertyName);
        res.addElement(newP);
      }
      return res;
    }

    /**
     * utility function to perform the 'setting' for us
     */
    protected void doThis(final Method setter, final Object data,
        final Object val)
    {

      final Object args[] =
      {val};

      Class<?> p1 = null;

      try
      {
        // check that there is a valid setter
        if (setter == null)
          return;

        // see if the setter is expecting a double
        final Class<?>[] params = setter.getParameterTypes();
        // get the first parameter
        p1 = params[0];

        // do we have to do our special 'double' handling?
        if (p1.equals(double.class))
        {
          // is the value we are going to set currently
          // a string?
          if (val.getClass().equals(String.class))
          {
            final Double d = MWCXMLReader.readThisDouble((String) val);
            final Object args2[] =
            {d};
            setter.invoke(data, args2);
          }
          else if (val.getClass().equals(Double.class))
          {
            final Double d = (Double) val;
            final Object args2[] =
            {d};
            setter.invoke(data, args2);
          }

        }
        else
          setter.invoke(data, args);
      }
      catch (final java.lang.NumberFormatException e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }
      catch (final java.lang.reflect.InvocationTargetException ie)
      {
        final String tmpStr = null;
        if (p1 != null)
          p1.toString();
        final String msg = "Using:" + setter.getName() + " to set:" + data
            + " to " + val + " expecting type:" + tmpStr;
        MWC.Utilities.Errors.Trace.trace(ie, msg);
      }
      catch (final java.lang.IllegalArgumentException ell)
      {
        final String msg = "Using:" + setter.getName() + " to set:" + data
            + " to " + val;
        MWC.Utilities.Errors.Trace.trace(ell, msg);
      }
      catch (final Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }

    }

    @Override
    public void execute()
    {
      // go through the vector and make the changes
      final Enumeration<PropertyChangeItem> enumer = _theMods.elements();
      while (enumer.hasMoreElements())
      {
        final PropertyChangeItem it = enumer.nextElement();
        if ("Name".equals(it.propertyName))
        {
          _theName = it.newValue.toString();
          _theInfo.setName(it.newValue.toString());
          if (_propsPanel != null)
          {
            _propsPanel.setTitleAt(_propsPanel.getSelectedIndex(), _theName);
          }
        }

        doThis(it.setter, it.data, it.newValue);
        it.editorInfo.fireChanged(this, it.propertyName, it.oldValue,
            it.newValue);
        // TODO We are calling it after every change, and we are forcing to update UI again anda
        // again.
        // Maybe it could be done once at the end.
        // Saul Hidalgo
      }

      // try to update just the layer
      if (_theLayers != null)
        _theLayers.fireModified(_parentLayer);
    }

    /**
     * accessor for the object being edited
     */
    public Object getData()
    {
      return _theData1;
    }

    @Override
    public boolean isRedoable()
    {
      return true;
    }

    @Override
    public boolean isUndoable()
    {
      return true;
    }

    public int size()
    {
      return _theMods.size();
    }

    @Override
    public String toString()
    {
      return "Property change";
    }

    @Override
    public void undo()
    {
      // go through the vector and reset to the old values
      // go through the vector and make the changes
      final Enumeration<PropertyChangeItem> enumer = _theMods.elements();
      while (enumer.hasMoreElements())
      {
        final PropertyChangeItem it = enumer.nextElement();
        doThis(it.setter, it.data, it.oldValue);
        it.editorInfo.fireChanged(this, it.propertyName, it.newValue,
            it.oldValue);
      }

      _theLayers.fireModified(_parentLayer);
    }
  }

  protected class PropertyChangeItem
  {
    public Object data;
    transient public Method setter;
    public Object newValue;
    public Object oldValue;
    transient public Editable.EditorType editorInfo;
    public String propertyName;

    public PropertyChangeItem(final Object theData, final Method theSetter,
        final Object theNewValue, final Object theOldValue,
        final Editable.EditorType theEditorInfo, final String thePropertyName)
    {
      data = theData;
      setter = theSetter;
      newValue = theNewValue;
      oldValue = theOldValue;
      editorInfo = theEditorInfo;
      propertyName = thePropertyName;
    }
  }

  /////////////////////////////////////////////////////
  // class which represents a combination of a
  // property descriptor and the screen object that
  // is used to edit that property
  ////////////////////////////////////////////////////
  public static class PropertyEditorItem
  {
    public final PropertyDescriptor theDescriptor;
    public Component theEditorGUI;
    public final PropertyEditor theEditor;
    public final Object theData;
    public final Object originalValue;
    transient public Editable.EditorType theEditableInfo;

    public PropertyEditorItem(final PropertyDescriptor propVal,
        final PropertyEditor theEditorVal, final Object data,
        final Object theOriginalValue, final Editable.EditorType editableInfo)
    {
      theDescriptor = propVal;
      theEditor = theEditorVal;
      theData = data;
      originalValue = theOriginalValue;
      theEditableInfo = editableInfo;
    }

    public Object getCurrentVal()
    {
      final Object res = getValueFor(theData, theDescriptor);
      return res;
    }
  }

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  // the property editor manager
  protected static PropertyEditorManager _myPropertyManager;

  // method to create the property manager - called by the child classes to do the core stuff.
  protected static void createCorePropertyEditors()
  {
    // ok, create it first
    _myPropertyManager = new PropertyEditorManager();

    // and now stick in the stuff which is GUI independent
    PropertyEditorManager.registerEditor(Integer.class, IntegerEditor.class);
    PropertyEditorManager.registerEditor(Long.class, LongEditor.class);
  }

  /**
   * method to retrieve an editor for the supplied property from our internal list
   *
   * @param p
   *          the property to get an editor for
   * @return a GUI editor component (or null)
   */
  public static PropertyEditor findEditor(final PropertyDescriptor p)
  {
    PropertyEditor res = null;

    // find out the type of the editor
    final Method m = p.getReadMethod();
    final Class<?> cl = m.getReturnType();

    // is there a custom editor for this type?
    final Class<?> c = p.getPropertyEditorClass();

    if (c == null)
    {
      res = PropertyEditorManager.findEditor(cl);
    }
    else
    {
      try
      {
        res = (PropertyEditor) c.newInstance();
      }
      catch (final Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }
    }
    return res;
  }

  protected static Object getValueFor(final Object data,
      final PropertyDescriptor p)
  {
    Object res = null;

    try
    {
      // find out the type of the editor
      final Method m = p.getReadMethod();

      res = m.invoke(data, (Object[]) null);
    }
    catch (final Exception e)
    {
      System.err.println("Plain property editor: problem calling read method");
      MWC.Utilities.Errors.Trace.trace(e);
    }

    return res;

  }

  /**
   * the data object which we are editing
   */
  protected Object _theData;

  /**
   * the properties shown by this object
   */
  protected PropertyDescriptor[] _theProperties;

  /**
   * the name of this object
   */
  protected String _theName;

  /**
   * the property editors we are adding
   */
  protected Hashtable<PropertyDescriptor, PropertyEditorItem> _theEditors;

  /**
   * the list of modifications made (plus their old values and the setter methods)
   */
  protected Vector<PropertyChangeItem> _theModifications;

  /**
   * the custom editor, if there is one
   */
  protected Class<?> _theCustomEditor;

  /**
   * the panel which is holding us
   */
  protected PropertiesPanel _thePanel = null;

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /**
   * the additional editable items returned by this object
   */
  protected java.beans.BeanInfo[] _otherEditors;

  /**
   * and other methods supported by the object
   */
  protected MethodDescriptor[] _theMethods;

  /**
   * the editable behaviour for this item
   */
  transient protected MWC.GUI.Editable.EditorType _theInfo;

  /**
   * the toolparent for this interface, so that we can set the cursor busy when we want to
   */
  protected MWC.GUI.ToolParent _toolParent;

  /**
   * the layer which this editable object belongs to
   */
  protected Layer _parentLayer;

  /**
   * the listener object we declare for this item, so that we can remove it later on
   */
  private PropertyChangeListener _reportListener;

  /**
   * JTabbedPane
   */
  private final SwingPropertiesPanel _propsPanel;

  protected final Layers _theLayers;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public PlainPropertyEditor(final MWC.GUI.Editable.EditorType theInfo,
      final Layers theLayers, final PropertiesPanel thePanel,
      final ToolParent toolParent, final Layer parentLayer,
      final SwingPropertiesPanel propsPanel)
  {
    _propsPanel = propsPanel;

    // store the editor
    _theInfo = theInfo;

    // store the data
    _theData = theInfo.getData();

    // store the tool parent
    _toolParent = toolParent;

    // store the properties panel
    _thePanel = thePanel;

    // store the layer this object belongs to (so we can trigger a layer-specific repaint, where
    // supported)
    _parentLayer = parentLayer;

    // declare our custom property editors
    declarePropertyEditors();

    // do the editors
    _theEditors = new Hashtable<PropertyDescriptor, PropertyEditorItem>();

    // see if there is a custom editor
    _theCustomEditor = theInfo.getBeanDescriptor().getCustomizerClass();

    // store the properties
    _theProperties = theInfo.getPropertyDescriptors();

    // see if there are any additional editors
    // note we only edit additional items if they are not "Expert" parameters
    _otherEditors = theInfo.getAdditionalBeanInfo();

    // see if there are any other methods
    // note that even though we retrieve this full list,
    // we only show additional parameters which are not
    // "Expert" parameters
    _theMethods = theInfo.getMethodDescriptors();

    // store the name
    _theName = theInfo.getName();

    // store the chart
    _theLayers = theLayers;

    // assign the editors
    assignEditors();

    // layout the form
    initForm(thePanel);

    // show the method buttons
    showMethods();

    /**
     * get ready to store the list of changes, for our undoable action
     */
    _theModifications = new Vector<PropertyChangeItem>(0, 1);

    // see if we should be listening to this object
    theInfo.addPropertyChangeListener(this);

    // does this object fire any reports?
    if (theInfo.firesReports())
    {
      // ok, create the report listener for this item
      _reportListener = new PropertyChangeListener()
      {
        @Override
        public void propertyChange(final PropertyChangeEvent evt)
        {
          fireNewReport((String) evt.getNewValue());
        }
      };

      // ok, declare an interest in any reports from the object
      theInfo.addPropertyChangeListener(Editable.EditorType.REPORT_NAME,
          _reportListener);
    }

    // also try to listen in to the additional items
    if (_otherEditors != null)
    {
      for (int i = 0; i < _otherEditors.length; i++)
      {
        final Editable.EditorType et = (Editable.EditorType) _otherEditors[i];
        et.addPropertyChangeListener(this);
      }
    }

    // did we actually find any editors?
    if ((_theEditors.size() == 0) && (_theCustomEditor == null))
    {
      // inform the user that this object has no editable properties
      showZeroEditorsFound();
    }

  }

  /**
   * find a editor for the supplied component, and add it to our list
   *
   * @param p
   * @param data
   * @param editor
   * @return
   */
  public PropertyEditor addEditor(final PropertyDescriptor p, final Object data,
      final Editable.EditorType editor)
  {
    PropertyEditor res = null;

    if (p != null)
    {

      res = findEditor(p);

      if (res != null)
      {
        final Object val = getValueFor(data, p);
        res.setValue(val);
        _theEditors.put(p, new PropertyEditorItem(p, res, data, val, editor));
      }
      else
      {
        MWC.Utilities.Errors.Trace.trace("Failed to find " + p + " editor for "
            + data + ", method:" + p.getReadMethod());
      }
    }

    return res;
  }

  protected void addUs()
  {
    final hasPropertyListeners l = (hasPropertyListeners) _theData;
    l.addPropertyChangeListener(this);
  }

  private void assignEditors()
  {
    // try for a custom editor first
    if (_theCustomEditor != null)
    {
    }
    else
    {
      if (_theProperties != null)
      {

        // introspection here we come
        final int cnt = _theProperties.length;
        for (int i = 0; i < cnt; i++)
        {
          final PropertyDescriptor p = _theProperties[i];
          addEditor(p, _theData, _theInfo);
        }
      }
    }

    // also have a go at creating supplemental editors
    if (_otherEditors != null)
    {
      // adding more editors
      for (int i = 0; i < _otherEditors.length; i++)
      {
        final BeanInfo bn = _otherEditors[i];
        if (bn instanceof MWC.GUI.Editable.EditorType)
        {
          final Editable.EditorType et = (Editable.EditorType) bn;
          final Object obj = et.getData();
          final PropertyDescriptor[] pds = et.getPropertyDescriptors();
          if (pds != null)
          {
            for (int j = 0; j < pds.length; j++)
            {
              final PropertyDescriptor pd = pds[j];

              // is this an 'expert' property which
              // should not appear in here as an additional?
              if (pd.isExpert())
              {
                // do nothing, we don't want to show this
              }
              else
              {
                addEditor(pd, obj, et);
              }
            }
          }
        }
      }
    }
  }

  protected void closing()
  {
    // remove the normal property change listener
    _theInfo.removePropertyChangeListener(this);

    // do we have a reporting listener?
    if (_reportListener != null)
      _theInfo.removePropertyChangeListener(Editable.EditorType.REPORT_NAME,
          _reportListener);

    // also try to listen in to the additional items
    if (_otherEditors != null)
    {
      for (int i = 0; i < _otherEditors.length; i++)
      {
        final Editable.EditorType et = (Editable.EditorType) _otherEditors[i];
        et.removePropertyChangeListener(this);
      }
    }
  }

  abstract protected void declarePropertyEditors();

  //////////////////////////////////////////////////
  // the data for the action
  ///////////////////////////////////////////////////

  public void doRefresh()
  {
    final Enumeration<PropertyEditorItem> enumer = _theEditors.elements();
    while (enumer.hasMoreElements())
    {
      final PropertyEditorItem pi = enumer.nextElement();
      final Component c = pi.theEditorGUI;
      final PropertyEditor pe = pi.theEditor;

      // now get the new value
      final Object current = getValueFor(pi.theData, pi.theDescriptor);

      if (current != null)
      {
        pe.setValue(current);
        updateThis(c, pe);
      }

    }
  }

  /**
   * pass through all of our editors and reset them to their original values
   */
  protected void doReset()
  {
    // remember if we have made a change
    boolean _someChanged = false;

    // update the editors in turn
    final Enumeration<PropertyEditorItem> enumer = _theEditors.elements();
    while (enumer.hasMoreElements())
    {
      final PropertyEditorItem pei = enumer.nextElement();
      final PropertyDescriptor pd = pei.theDescriptor;

      final Object res = pei.theEditor.getValue();

      // find out if the value for this parameter is different to the
      // original one (has it been modified?)
      final Object orig = pei.originalValue;

      if (!res.equals(orig))
      {
        _someChanged = true;

        // get the "writer" method
        final Method write = pd.getWriteMethod();

        try
        {
          final Object[] params =
          {orig};
          write.invoke(pei.theData, params);
        }
        catch (final Exception e)
        {
          MWC.Utilities.Errors.Trace.trace(e);
        }

        // so reset the value of this property editor
        final PropertyEditor pe = pei.theEditor;
        pe.setValue(pei.originalValue);
        updateThis(pei.theEditorGUI, pe);
      }
    }

    // and trigger an update
    if (_someChanged)
      _theLayers.fireModified(null);

  }

  public void doUpdate()
  {

    // see if there is a custom editor
    if (_theCustomEditor != null)
    {
      // then let's leave it to edit itself
    }
    else
    {
      // update the editors in turn
      final Enumeration<PropertyEditorItem> enumer = _theEditors.elements();
      while (enumer.hasMoreElements())
      {
        final PropertyEditorItem pei = enumer.nextElement();
        final PropertyDescriptor pd = pei.theDescriptor;
        update(pd, pei.theData);
      }

      // check if there are any modifications to be made
      if (_theModifications.size() > 0)
      {

        /*
         * we now have a list of properties to be changed (and their old values) in our vector we
         * now have to do them
         */
        final PropertyChangeAction act = new PropertyChangeAction(
            _theModifications, _theData);

        // check if the list actually contains any modifications

        // and do it
        act.execute();

        // and put it on the undo buffer
        final MWC.GUI.Undo.UndoBuffer buff = getBuffer();
        if (buff != null)
          buff.add(act);

        // and now clear the list of modifications (we don't want to repeat them)
        _theModifications.removeAllElements();

      }

      // finally inform the item being edited that we are finished
      _theInfo.updatesComplete();

    }
  }

  /**
   * the object we are listening to has fired a new report. Display it in our GUI if we want to
   *
   * @param report
   *          the text to show
   */
  abstract protected void fireNewReport(String report);

  abstract protected UndoBuffer getBuffer();

  public String getName()
  {
    return _theName;
  }

  /////////////////////////////////////////////////////
  // abstract methods
  ////////////////////////////////////////////////////
  abstract protected void initForm(PropertiesPanel thePanel);

  @Override
  public void propertyChange(final PropertyChangeEvent pce)
  {
    // check that the change didn't come from us
    if (pce.getSource() != this)
    {
      // trigger a refresh
      doRefresh();
    }
  }

  abstract public void setNames(String apply, String close, String reset);

  abstract protected void showMethods();

  abstract protected void showZeroEditorsFound();

  /**
   * return this item as a string
   */
  @Override
  public String toString()
  {
    return getName();
  }

  private void update(final PropertyDescriptor pd, final Object data)
  {
    final PropertyEditorItem pei = _theEditors.get(pd);
    final PropertyEditor pe = pei.theEditor;

    if (pe == null)
    {
      return;
    }

    final Object newVal = pe.getValue();
    final Object oldVal = pei.getCurrentVal();

    // find out if the value for this parameter is different to the
    // current one for this object - or if there isn't a current value
    if ((newVal == null) || (!newVal.equals(oldVal)))
    {

      // find out the type of the editor
      final Method write = pd.getWriteMethod();

      // and store all of this data, ready to apply it
      _theModifications.addElement(new PropertyChangeItem(data, write, newVal,
          oldVal, pei.theEditableInfo, pei.theDescriptor.getName()));
    }
  }

  abstract protected void updateThis(Component c, PropertyEditor pe);

}
