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
package Debrief.Tools.FilterOperations;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ReformatFixes.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.6 $
// $Log: ReformatFixes.java,v $
// Revision 1.6  2005/12/13 09:04:42  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.5  2004/11/25 10:24:28  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.4  2004/11/22 13:41:01  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.3  2004/06/10 15:10:41  Ian.Mayo
// Correctly implement TestCase pattern
//
// Revision 1.2  2003/07/28 07:53:59  Ian.Mayo
// Remove unnecessary import
//
// Revision 1.1.1.2  2003/07/21 14:48:24  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.14  2003-06-10 15:38:58+01  ian_mayo
// Only manipulate Property descriptor if we found one
//
// Revision 1.13  2003-05-12 12:01:56+01  ian_mayo
// Minor refactoring, lots of testing, allow entry of text fields
//
// Revision 1.12  2003-05-09 14:42:54+01  ian_mayo
// Much closer now, part way through refactoring & putting in more tests
//
// Revision 1.11  2003-05-09 12:38:13+01  ian_mayo
// First implementation complete - start stripping out old bits
//
// Revision 1.10  2003-05-08 13:53:35+01  ian_mayo
// Lots of tidying, together with getting 1/2 way towards identifying intersecting sets of properties
//
// Revision 1.9  2003-03-25 15:54:14+00  ian_mayo
// Implement "Reset me" buttons
//
// Revision 1.8  2003-03-19 15:37:06+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.7  2003-02-07 09:02:36+00  ian_mayo
// remove unnecessary toda comments
//
// Revision 1.6  2003-01-16 14:25:03+00  ian_mayo
// Improve text description, and correct label in choice box to use name of property
//
// Revision 1.5  2002-12-16 15:11:19+00  ian_mayo
// Allow user to select whether to change all, or all visible fixes
//
// Revision 1.4  2002-10-01 15:39:13+01  ian_mayo
// Remove un-used variable
//
// Revision 1.3  2002-09-24 10:54:54+01  ian_mayo
// Delete unnecessary variable
//
// Revision 1.2  2002-05-28 12:28:22+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:11:58+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-04-30 09:14:53+01  ian
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:08+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-02-01 15:09:10+00  administrator
// Initial revision
//

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JOptionPane;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Properties.ColorPropertyEditor;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

/**************************************************************
 * Class which lets user reformat a series of positions in the Track/Time toolbox.
 * Whilst the Toolbox may contain different types of object, this just edits
 * Fixes contained in Tracks
 **************************************************************/

public final class ReformatFixes implements FilterOperation
{

  /*********************************************************************************************
   * member objects
   **********************************************************************************************/

  /** string to represent what types of object are to be selected
   *
   */
  private static final String ALL_ITEMS = "All";

  private static final String ONLY_VISIBLE = "Only visible items";

  /** the specified start time
   *
   */
  HiResDate _start_time = null;

  /** the specified end time
   *
   */
  HiResDate _end_time = null;

  /** the selected tracks
   *
   */
  private Vector<WatchableList> _theTracks = null;

  /** the set of layers which we will update
   *
   */
  private final Layers _theLayers;

  /** the separator we use in the operation description
   *
   */
  private final String _theSeparator = System.getProperties().getProperty("line.separator");

  /** String describing what we do
   *
   */
  private static final String _myDescription = "Reformat objects";

  /*********************************************************************************************
   * constructor
   **********************************************************************************************/

  /** constructor - which receives the set of layers to be updated on completion
   * @param theLayers the layers object to be updated on our completion
   */
  public ReformatFixes(final Layers theLayers)
  {
    _theLayers = theLayers;
  }

  /*********************************************************************************************
   * member methods
   **********************************************************************************************/

  public final String getDescription()
  {
    String res = "2. Select objects to be reformatted";
    res += _theSeparator + "3. Select extent of data items to be changed (Start/Finish sliders)";
    res += _theSeparator + "4. Press 'Apply' button";
    res += _theSeparator + "5. Dialog boxes will pop up to alow specific property and new value to be set";
    res += _theSeparator + "====================";
    res += _theSeparator + "This operations allow the appearance of annotations and track positions within specified periods to be changed";
    return res;
  }

  private String getTypeToSelect()
  {
    String res = null;

    final String[] types = {ALL_ITEMS, ONLY_VISIBLE};

    // find out which one the user wants to edit
    res = (String) JOptionPane.showInputDialog(null,
                                               "Which type of item do you wish to edit?",
                                               _myDescription,
                                               JOptionPane.QUESTION_MESSAGE,
                                               null,
                                               types,
                                               ALL_ITEMS);

    return res;
  }


  /** new method, which gets the property from a list supplied
   *
   */
  @SuppressWarnings("unchecked")
	private Vector<SetterHolder> getProperty(final HashMap<String, Vector<SetterHolder>> propertyList)
  {
    // find out which one the user wants to edit
  	final Vector<Vector<SetterHolder>> list = new Vector<Vector<SetterHolder>>();

    final Collection<String> theKeys = propertyList.keySet();

    for(final Iterator<String> iterator = theKeys.iterator(); iterator.hasNext();)
    {
    	final Vector<SetterHolder> thisVector = propertyList.get(iterator.next());
      list.add(thisVector);
    }

    final Vector<?>[] resList = new Vector[list.size()];
    int ctr = 0;
    for(final Iterator<Vector<SetterHolder>> iter = list.iterator();iter.hasNext();)
    {
    	resList[ctr++] = iter.next();
    }
    
    final Object val = JOptionPane.showInputDialog(null,
                                             "Which property do you wish to edit?",
                                             "Reformat objects",
                                             JOptionPane.QUESTION_MESSAGE,
                                             null,
                                             resList,
                                             null);

    final Vector<SetterHolder> res = (Vector<SetterHolder>) val;
    return res;
  }

  public final void setPeriod(final HiResDate startDTG, final HiResDate finishDTG)
  {
    _start_time = startDTG;
    _end_time = finishDTG;
  }

  public final void setTracks(final Vector<WatchableList> selectedTracks)
  {
    _theTracks = selectedTracks;
  }

  /** the user has pressed RESET whilst this button is pressed
   *
   * @param startTime the new start time
   * @param endTime the new end time
   */
  public void resetMe(final HiResDate startTime, final HiResDate endTime)
  {
  }

  public final void execute()
  {
  }

  public final Action getData()
  {


    // check we've got some tracks
    if(_theTracks == null)
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Reformat Tracks", "Please select one or more tracks");
      return null;
    }

    // find the list of properties to be edited
    final HashMap<String, Vector<SetterHolder>> commonProps = getCommonProperties(_theTracks);

    // find out what is to be edited
    final Vector<SetterHolder> theSetters = getProperty(commonProps);

    if(theSetters == null)
      return null;

    // find out what types of position to use
    final String position_type = getTypeToSelect();

    if(position_type == null)
      return null;


    // the value selected by the user
    Object newVal = null;

    final SetterHolder firstEditor = theSetters.firstElement();
    final PropertyEditor pe = firstEditor._propertyEditor;
    final String tags[] = pe.getTags();
    if(tags != null)
    {
      newVal = getSelectedNewValueFromList(firstEditor, tags, pe);
    }
    else
    {
      pe.setValue(firstEditor._existingValue);
      if(pe.getAsText() != null)
      {
        newVal =  this.getSelectedNewValueFromText(firstEditor, pe);
      }
      else
        return null;
    }


    // ok, the user data is collated, lets do it!
    final ReformatAction res = performTheOperation(firstEditor, newVal, theSetters, position_type);

    // return the new action
    return res;
  }

 /** method to let the user select the new value to use for this property
   *
   * @param firstEditor supplier of the property name
   * @param pe editor capable of translating selected tag to a real value
   * @return the new value to use
   */
  private Object getSelectedNewValueFromText(final SetterHolder firstEditor, final PropertyEditor pe)
  {
    Object newVal;
    // get the new value
    final String col = (String) JOptionPane.showInputDialog(null,
                                                      "What value do you want to use for " + firstEditor._propertyName + " ?",
                                                      "Reformat data",
                                                      JOptionPane.QUESTION_MESSAGE
                                                      );

    pe.setAsText(col);
    newVal = pe.getValue();
    return newVal;
  }

  /** method to let the user select the new value to use for this property
   *
   * @param firstEditor supplier of the property name
   * @param tags list of valid values
   * @param pe editor capable of translating selected tag to a real value
   * @return the new value to use
   */
  private Object getSelectedNewValueFromList(final SetterHolder firstEditor, final String[] tags, final PropertyEditor pe)
  {
    Object newVal;
    // get the new value
    final String col = (String) JOptionPane.showInputDialog(null,
                                                      "Which value do you want to use for " + firstEditor._propertyName + " ?",
                                                      "Reformat fixes",
                                                      JOptionPane.QUESTION_MESSAGE,
                                                      null,
                                                      tags,
                                                      null);

    pe.setAsText(col);
    newVal = pe.getValue();
    return newVal;
  }

  /**
   *
   * @param sampleEditor an example of one of the edits
   * @param newValue the new value to use
   * @param theSetters the list of objects to edit
   * @param position_type whether to do few or all points
   * @return
   */
  ReformatAction performTheOperation(final SetterHolder sampleEditor, final Object newValue, final Vector<SetterHolder> theSetters, final String position_type)
  {
    // create the results object
    final ReformatAction res = new ReformatAction(sampleEditor._propertyName, newValue, _theLayers);

    // make our symbols and labels visible
    final Enumeration<SetterHolder> iter = theSetters.elements();
    while(iter.hasMoreElements())
    {
      final SetterHolder wl = iter.nextElement();

      // pass through our items and check if editing them is applicable.  If so, create some
      // reformat actions to make/undo the change
      final WatchableList thisList = (WatchableList) wl._object;

      Collection<Editable> validItems = null;

      // find if it is, or has any valid points
      // get the correct type of data
      if(position_type == ReformatFixes.ALL_ITEMS)
      {
        validItems = thisList.getItemsBetween(_start_time, _end_time);
      }
      else if(position_type.equals(ReformatFixes.ONLY_VISIBLE))
      {
        // ah-ha!  SPECIAL PROCESSING, if this item is a TrackWrapper
        if(thisList instanceof TrackWrapper)
        {
          final TrackWrapper thisTrack = (TrackWrapper) thisList;
          validItems = thisTrack.getUnfilteredItems(_start_time, _end_time);
        }
        else
        {
          // just do a quick check whether this item is visible
          if(thisList.getVisible())
          {
            // yup, remember it
            validItems = thisList.getItemsBetween(_start_time, _end_time);
          }
        }
      }

      // did we find any?
      if(validItems != null)
      {
        // so, we now have a list of items to edit
        for(final Iterator<Editable> iterator = validItems.iterator(); iterator.hasNext();)
        {
          final Editable editable = iterator.next();

          // ok, now add it to our list of edits
          res.submitNewEdit(new SetterHolder(editable, wl._setter, wl._getter, wl._propertyName, wl._propertyEditor));
        } // step through the editable items
      }
    }
    return res;
  }

  /** get the common properties from the list provided
   *
   * @param theObjects the currently selected list of objects
   * @return the properties common to all items in the list
   */
  static HashMap<String, Vector<SetterHolder>> getCommonProperties(final Vector<WatchableList> theObjects)
  {
    HashMap<String, Vector<SetterHolder>> theProperties = null;

    // create our core property editor
    final java.beans.PropertyEditorManager propMan = getPropertyManager();

    // iterate through the list of objects
    for(int i = 0; i < theObjects.size(); i++)
    {
      HashMap<String, SetterHolder> propertiesForThisObject;

      final WatchableList thisObject = theObjects.elementAt(i);

      // find the editable properties for this object
      propertiesForThisObject = getSuitablePropertiesFor(thisObject, propMan);

      // did we find any?
      if(!propertiesForThisObject.isEmpty())
      {
        // so, is this our first list?
        if(theProperties == null)
        {
          theProperties = new HashMap<String, Vector<SetterHolder>>();

          // yup, create and insert ours
          for(final Iterator<String> iterator = propertiesForThisObject.keySet().iterator(); iterator.hasNext();)
          {
            final String thisProperty = iterator.next();

            // get the property editor
            final SetterHolder pe = propertiesForThisObject.get(thisProperty);

            // create a list of these setters
            final Vector<SetterHolder> thisList = new Vector<SetterHolder>(0, 1)
            {
              /**
							 * 
							 */
							private static final long serialVersionUID = 1L;

							/**
               * Returns a string representation of this Vector, containing
               * the String representation of each element.
               */
              public synchronized String toString()
              {
                return thisProperty;
              }
            };

            thisList.add(pe);

            // and insert into the properties object
            theProperties.put(thisProperty, thisList);
          } // step through our properties

        }
        else
        {
          // so, we've got our list already.  We've got to produce the intersection of the
          // two lists

          // get the list of names
          final Set<String> existingKeys = theProperties.keySet();
          final Set<String> newKeys = propertiesForThisObject.keySet();

          // find the intersection
          newKeys.retainAll(existingKeys);

          // and now do the reverse
          existingKeys.retainAll(newKeys);

          // we should now have the combined list - collate the items into a new list
          final HashMap<String, Vector<SetterHolder>> newProps = new HashMap<String, Vector<SetterHolder>>();
          for(final Iterator<String> iterator = existingKeys.iterator(); iterator.hasNext();)
          {
            final String s = iterator.next();
            final Vector<SetterHolder> thisList = theProperties.get(s);
            newProps.put(s, thisList);
          }

          // ditch the old properties
          theProperties = null;

          // and remember the new one
          theProperties = newProps;

          // lastly, add our new property editors to this new list
          for(final Iterator<String> iterator = existingKeys.iterator(); iterator.hasNext();)
          {
            final String s = iterator.next();
            // get the new property
            final SetterHolder sh = propertiesForThisObject.get(s);

            // find this vector in our big properties list
            final Vector<SetterHolder> thisV =  theProperties.get(s);

            // append our setter to this list
            thisV.add(sh);
          }
        }

      }
    }

    return theProperties;
  }

  /** pass through the properties for this object, and if any of them can be edited
   * by tags or a text box, add them to our hashmap
   *
   * @param thisList the object we're looking at
   * @return the list of props for this object
   */
  static HashMap<String, SetterHolder> getSuitablePropertiesFor(final WatchableList thisList,
                                                  final PropertyEditorManager propMan)
  {
    final HashMap<String, SetterHolder> propertiesForThisObject = new HashMap<String, SetterHolder>();

    // check it's an editable object
    if(!(thisList instanceof Editable))
    {
      return propertiesForThisObject;
    }

    final WatchableList thisObject = (WatchableList) thisList;

    Editable.EditorType tt = null;


    // it works strangely here.  If we were passed a TrackWrapper we convert
    // it to a FixWrapper - since it is the series of Fixes which actually get
    // reformatted - thus we want the list of properties for a Fix
    if(thisList instanceof TrackWrapper)
    {
      // give the fix a date, so it can initialise its label corerctly
      final Fix datedFix = new Fix();
      datedFix.setTime(new HiResDate(120000));


      final FixWrapper fw = new FixWrapper(datedFix);
      tt = fw.getInfo();
    }
    else
    {
      // no, it's not a track - get the properties as normal
      tt = ((Editable) thisObject).getInfo();
    }

    // get it's list of editable properties
    final PropertyDescriptor[] pd = tt.getPropertyDescriptors();

    if(pd != null)
      {

      // step through the properties
      for(int j = 0; j < pd.length; j++)
      {
        final PropertyDescriptor thisProperty = pd[j];

        // get the property editor for this method
        final PropertyEditor pe = getPropertyEditorFor(thisProperty, propMan);

        // ok, do we now have an editor?
        if(pe != null)
        {
          // we've got an editor - now see if it's suitable
          if(thisEditorIsSuitable(pe))
          {
            final SetterHolder sh = new SetterHolder((Editable) thisList,
                                               thisProperty.getWriteMethod(),
                                               thisProperty.getReadMethod(),
                                               thisProperty.getName(),
                                               pe);

            // get the name of this method
            final String thePropName = thisProperty.getName();

            // yup, add it to our list
            propertiesForThisObject.put(thePropName, sh);
          } // whether the editor was suitable
        } // whether we found an editor
      } // stepping through the properties for this object
    }

    return propertiesForThisObject;
  }

  public static PropertyEditorManager getPropertyManager()
  {
    final PropertyEditorManager pm = new PropertyEditorManager();
    PropertyEditorManager.registerEditor(java.awt.Color.class, ColorPropertyEditor.class);
    return pm;
  }

  /** examine this object, and find a property editor for it (self-defined or auto-defined)
   *
   * @param thisProperty
   * @return
   */
  private static PropertyEditor getPropertyEditorFor(final PropertyDescriptor thisProperty,
                                                     final PropertyEditorManager propMan)
  {

    final Method theReadMethod = thisProperty.getReadMethod();

    // see if we can create an editor
    PropertyEditor pe = null;

    final Class<?> returnClass = theReadMethod.getReturnType();

    // is there a custom editor for this type?
    final Class<?> c = thisProperty.getPropertyEditorClass();

    // did we find a custom instance
    if(c != null)
    {
      // yes, have a go at creating it
      try
      {
        pe = (PropertyEditor) c.newInstance();
      }
      catch(final Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }
    }
    else
    {
      if(returnClass == String.class)
      {
        pe = PropertyEditorManager.findEditor(returnClass);
      }
      else if(returnClass == boolean.class)
      {
        pe = PropertyEditorManager.findEditor(returnClass);
      }
      else if(returnClass == Boolean.class)
      {
        pe = PropertyEditorManager.findEditor(returnClass);
      }
      else if(returnClass == java.awt.Color.class)
      {
        pe = PropertyEditorManager.findEditor(returnClass);
      }
    }

    // and return it
    return pe;
  }

  /** have a look at this editor, and see if it's suitable for our type of editors
   *
   * @param editor the editor to have a look at
   * @return whether or not we can edit it using tags or a text box
   */
  private static boolean thisEditorIsSuitable(final PropertyEditor editor)
  {
    boolean suitable = false;

    // will it get tags?
    final String[] res = editor.getTags();
    if(res != null)
    {
      suitable = true;
    }
    else
    {
      // ok, see if it will accept a string value
      final String res2 = editor.getAsText();

      // did it work?
      if(res2 != null)
        suitable = true;
    }

    return suitable;
  }


  public final String getLabel()
  {
    return _myDescription;
  }

  public final String getImage()
  {
    return null;
  }

  public final void actionPerformed(final java.awt.event.ActionEvent p1)
  {

  }

  public final void close()
  {

  }

  /** embedded class acting a structure to contain an object, and it's setter method
   *
   *
   */
  private final static class SetterHolder
  {
    /** the object we are editing
     *
     */
    final Editable _object;
    /** the setter method in question
     *
     */
    final Method _setter;

    /** the getter method for this object
     *
     */
    final Method _getter;

    /** the name of the property being edited
     *
     */
    final String _propertyName;

    /** the property editor to use
     *
     */
    final PropertyEditor _propertyEditor;

    /** the existing value for this value
     *
     */
    Object _existingValue = null;

    /** the getter method for this object

     /** constructor, to store the relevant data
     *
     * @param object the object to edit
     * @param setter the setter method
     */
    public SetterHolder(final Editable object,
                        final Method setter,
                        final Method getter,
                        final String propertyName,
                        final PropertyEditor propertyEditor)
    {
      _object = object;
      _setter = setter;
      _getter = getter;
      _propertyName = propertyName;
      _propertyEditor = propertyEditor;
    }

    /** set the existing value for this object
     *
     */
    public void setExistingValue(final Object val)
    {
      _existingValue = val;
    }

    /**
     *
     * @return
     */
    public String toString()
    {
      return _propertyName;
    }
  }

  /** embedded class to store the details of a set of reformatting changes
   *
   */
  private static final class ReformatAction implements Action
  {

    /** the list of single edit operations
     *
     */
    final List<SetterHolder> _myEdits;

    /** the name of this edit operation
     *
     */
    final String _myName;

    /** the value to update the data to
     *
     */
    final Object _newValue;

    /** the set of layers which gets updated
     *
     */
    final Layers _theLayers;

    /**
     * Returns a string representation of this operation.
     * @return  a string representation of the object.
     */
    public String toString()
    {
      return "Edit " + _myName;
    }

    /** constructor
     *
     */
    public ReformatAction(final String theName,
                          final Object newValue,
                          final Layers theLayers)
    {
      _myName = theName;
      _myEdits = new Vector<SetterHolder>(0, 1);
      _newValue = newValue;
      _theLayers = theLayers;
    }

    /** submit a new edit operation to our list
     *
     */
    public void submitNewEdit(final SetterHolder newEdit)
    {
      _myEdits.add(newEdit);
    }

    /** trigger an update
     *
     */
    private void triggerUpdate()
    {
      _theLayers.fireReformatted(null);
    }

    /** this method calls the 'do' event in the parent tool,
     *  passing the necessary data to it
     */
    public void execute()
    {

      final Object[] newVals = new Object[]{_newValue};

      for(int i = 0; i < _myEdits.size(); i++)
      {
        final SetterHolder setterHolder = _myEdits.get(i);

        try
        {
          // get the old (existing) value for this object
          final Object existingValue = setterHolder._getter.invoke(setterHolder._object, (Object[])null);

          // and store it
          setterHolder.setExistingValue(existingValue);

          // now set the new value
          setterHolder._setter.invoke(setterHolder._object, newVals);
        }
        catch(final Exception e)
        {
          MWC.Utilities.Errors.Trace.trace(e, "Failed whilst updating " + setterHolder._object + " to " + _newValue);
        }
      }

      // and finished
      triggerUpdate();
    }

    /** this method calls the 'undo' event in the parent tool,
     *  passing the necessary data to it
     */
    public void undo()
    {
      // pass through our edits, undoing the change

      for(int i = 0; i < _myEdits.size(); i++)
      {
        final SetterHolder setterHolder = _myEdits.get(i);

        try
        {
          // ok, we're ondoing, get the old value for this item
          final Object[] newVals = new Object[]{setterHolder._existingValue};

          // and restore it to the old value
          setterHolder._setter.invoke(setterHolder._object, newVals);
        }
        catch(final Exception e)
        {
          MWC.Utilities.Errors.Trace.trace(e, "Failed whilst undoing " + setterHolder._object + " to " + _newValue);
        }
      }

      // and an update
      triggerUpdate();
    }

    /** @return boolean flag to indicate whether
     * this action may be redone */
    public boolean isRedoable()
    {
      return true;
    }

    /** @return boolean flag to describe whether this
     * operation may be undone*/
    public boolean isUndoable()
    {
      return true;
    }

  }


  /////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testListOfProperties extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

      public testListOfProperties(final String s) {
          super(s);
      }

    public void testCollateProperties()
    {

      System.out.println("test 1");
      // produce the objects to test
      final Vector<WatchableList> theObjects2 = getTestObjects(5);

      // now find the common list of properties
      final HashMap<String, Vector<SetterHolder>> res = ReformatFixes.getCommonProperties(theObjects2);

      // check we have our two items
      assertEquals("found right number of properties", 3, res.keySet().size());

      // get the first list
      final Vector<SetterHolder> firstList =  res.get(res.keySet().iterator().next());
      assertEquals("have editors for correct number of properties", 4, firstList.size());
    }

    // TODO FIX-TEST
    public void NtestGetProperties()
    {
      System.out.println("test 2");
      final Debrief.Wrappers.TrackWrapper tw = new Debrief.Wrappers.TrackWrapper();
      tw.setColor(java.awt.Color.red);
      tw.setName("scrap track");

      final Editable.EditorType et = tw.getInfo();
      // check we got it
      assertNotNull("found editable data", et);

      // now check we can get the suitable properties
      final HashMap<String, SetterHolder> props = getSuitablePropertiesFor(tw, ReformatFixes.getPropertyManager());

      // so, how many are there?
      final Collection<String> keys = props.keySet();

      // did we get the correct number
//      for(Iterator iterator = keys.iterator(); iterator.hasNext();)
//      {
//        String s = (String) iterator.next();
//        System.out.println("this key:" + s);
//      }

      assertEquals("found correct number of properties for track", 9, keys.size());

      // see if we've got a property we expect
      assertTrue("found get color", keys.contains("Color"));

      // check we haven't got one we don't want
      assertTrue("not found get track font", !keys.contains("TrackFont"));

    }

    /**
     * Get a list of objects used for testing
     * @param num the number of objects to return
     * @return a list of sample objects
     */
    private Vector<WatchableList> getTestObjects(final int num)
    {
      final Vector<WatchableList> res = new Vector<WatchableList>();

      // ok, start with a track
      final Debrief.Wrappers.TrackWrapper tw = new Debrief.Wrappers.TrackWrapper();
      tw.setColor(java.awt.Color.red);
      res.add(tw);

      if(num < 2)
        return res;

      // and another...
      final Debrief.Wrappers.TrackWrapper tw2 = new Debrief.Wrappers.TrackWrapper();
      tw2.setColor(java.awt.Color.red);
      tw2.setTrackColor(java.awt.Color.green);
      res.add(tw2);

      if(num < 3)
        return res;

      // and a shape
      final MWC.GenericData.WorldLocation theLocation = new MWC.GenericData.WorldLocation(0, 1, 1d);
      final MWC.GUI.Shapes.PlainShape theShape = new MWC.GUI.Shapes.CircleShape(theLocation, 0.03d);
      final Debrief.Wrappers.ShapeWrapper sw = new Debrief.Wrappers.ShapeWrapper("bingo", theShape, java.awt.Color.red, null);
      res.add(sw);

      if(num < 4)
        return res;

      // and a label
      final Debrief.Wrappers.LabelWrapper lw = new Debrief.Wrappers.LabelWrapper("here", theLocation, java.awt.Color.red);
      res.add(lw);

      if(num < 5)
        return res;

      // and collate the list

      return res;
    }

    public void testIntersect()
    {
      System.out.println("test 3");
      final TreeSet<String> t1 = new TreeSet<String>();
      t1.add("a");
      t1.add("b");
      t1.add("c");
      t1.add("d");
      t1.add("e");

      final TreeSet<String> t2 = new TreeSet<String>();
      t2.add("c");
      t2.add("d");
      t2.add("e");
      t2.add("f");

      t1.retainAll(t2);
      t2.retainAll(t1);

      // check we've got the ones we're after
      assertTrue("found it", t2.contains("c"));
      assertTrue("found it", t2.contains("d"));
      assertTrue("found it", t2.contains("e"));
    }

    public void testMatchingAnnotations()
    {
      final LabelWrapper lw = new LabelWrapper("first label", null, java.awt.Color.blue);

      final WorldLocation loc = new WorldLocation(0d,0d,0d);
      final PlainShape ps = new CircleShape(loc, 12d);
      final ShapeWrapper sw = new ShapeWrapper("first shape", ps, java.awt.Color.blue, null);

      final TrackWrapper tw = new TrackWrapper();
      tw.setColor(java.awt.Color.blue);
      final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(5,0), loc, 0d, 0d));
      final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(7,0), loc, 0d, 0d));
      final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(9,0), loc, 0d, 0d));
      fw1.setLabelShowing(true);
      fw1.setVisible(true);
      fw2.setVisible(true);
      fw3.setVisible(true);
      tw.addFix(fw1);
      tw.addFix(fw2);
      tw.addFix(fw3);
      fw1.setTrackWrapper(tw);
      fw2.setTrackWrapper(tw);
      fw3.setTrackWrapper(tw);
      fw1.setColor(java.awt.Color.blue);
      fw2.setColor(java.awt.Color.blue);
      fw3.setColor(java.awt.Color.blue);

      // check they're visible
      assertTrue("label is visible", lw.getVisible());
      assertTrue("shape is visible", sw.getVisible());
      assertTrue("track wrapper is visible", tw.getVisible());

      assertEquals("track is blue", java.awt.Color.blue, tw.getColor());
      assertEquals("label is blue", java.awt.Color.blue, lw.getColor());
      assertEquals("shape is blue", java.awt.Color.blue, sw.getColor());

      final Vector<WatchableList> theTracks = new Vector<WatchableList>(0,1);
      theTracks.add(lw);
      theTracks.add(sw);
      theTracks.add(tw);

      final Layers ly = new Layers();
      final ReformatFixes rf = new ReformatFixes(ly);

      // find the list of properties to be edited
       final HashMap<String, Vector<SetterHolder>> commonProps = ReformatFixes.getCommonProperties(theTracks);

       // get the color setter
       final Vector<SetterHolder> theSetters = (Vector<SetterHolder>) commonProps.get("Color");

      // find out what types of position to use
      String position_type = ALL_ITEMS;

      // the value selected by the user
      Object newVal = null;

      final SetterHolder firstEditor = theSetters.firstElement();
      final PropertyEditor pe = firstEditor._propertyEditor;
      final String tags[] = pe.getTags();
      if(tags != null)
      {
          Object res;
          pe.setAsText(tags[0]);
          res = pe.getValue();
          newVal = res;
      }

      // set the start/end times
      rf._start_time = new HiResDate(5);
      rf._end_time = new HiResDate(6);

      // ok, the user data is collated, lets do it!
      ReformatAction res = rf.performTheOperation(firstEditor, newVal, theSetters, position_type);

      assertNotNull("created action item", res);

      // execute it
      res.execute();

      // check if colours updates
      assertEquals("track changed to red", java.awt.Color.red, fw1.getColor());
      assertEquals("shape changed to red", java.awt.Color.red, sw.getColor());
      assertEquals("label changed to red", java.awt.Color.red, lw.getColor());

      // and check the undo
      res.undo();

      // check if colours updates
      assertEquals("track changed to red", java.awt.Color.blue, fw1.getColor());
      assertEquals("shape changed to red", java.awt.Color.blue, sw.getColor());
      assertEquals("label changed to red", java.awt.Color.blue, lw.getColor());

      newVal = java.awt.Color.yellow;
      position_type = ReformatFixes.ONLY_VISIBLE;

      // ok, the user data is collated, lets do it!
      res = rf.performTheOperation(firstEditor, newVal, theSetters, position_type);

      assertNotNull("created action item", res);

      // execute it
      res.execute();

      // check if colours updates
      assertEquals("fix 1 changed to yellow", java.awt.Color.yellow, fw1.getColor());
      assertEquals("fix 2 not changed to yellow", java.awt.Color.blue, fw2.getColor());
      assertEquals("shape changed to yellow", java.awt.Color.yellow, sw.getColor());
      assertEquals("label changed to yellow", java.awt.Color.yellow, lw.getColor());



    }
  }

}

