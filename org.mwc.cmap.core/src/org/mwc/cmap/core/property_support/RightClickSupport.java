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
package org.mwc.cmap.core.property_support;

import java.awt.Color;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.dnd.Clipboard;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor;
import org.mwc.cmap.core.operations.RightClickPasteAdaptor;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Editable.CategorisedPropertyDescriptor;
import MWC.GUI.Editable.EditorType;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class RightClickSupport
{

  /**
   * embedded class to store a property change in an action
   * 
   * @author ian.mayo
   */
  private static class ListPropertyAction extends AbstractOperation
  {
    private Object _oldValue;

    private final Method _setter;

    private final Layers _layers;

    private final Layer _parentLayer;

    private final Editable[] _subjects;

    private final Object _newValue;

    private final String _propertyName;

    public ListPropertyAction(final String propertyName,
        final Editable[] editable, final Method getter, final Method setter,
        final Object newValue, final Layers layers, final Layer parentLayer)
    {
      super(propertyName + " for "
          + (editable.length > 1 ? "multiple items" : editable[0].getName()));
      _propertyName = propertyName;
      _setter = setter;
      _layers = layers;
      _parentLayer = parentLayer;
      _subjects = editable;
      _newValue = newValue;

      try
      {
        _oldValue = getter.invoke(editable[0], (Object[]) null);
      }
      catch (final Exception e)
      {
        CorePlugin.logError(IStatus.ERROR, "Failed to retrieve old value for:"
            + "Multiple items starting with:" + _subjects[0].getName(), e);
      }

      if (CorePlugin.getUndoContext() != null)
      {
        super.addContext(CorePlugin.getUndoContext());
      }
    }

    private IStatus doIt(final Object theValue)
    {
      IStatus res = Status.OK_STATUS;
      for (int cnt = 0; cnt < _subjects.length; cnt++)
      {
        final Editable thisSubject = _subjects[cnt];
        try
        {
          _setter.invoke(thisSubject, new Object[]
          {theValue});
          
          // and try to fire property change
          final EditorType info = thisSubject.getInfo();
          if(info != null)
          {
            info.fireChanged(this, _propertyName, null, theValue);
          }
        }
        catch (final InvocationTargetException e)
        {
          CorePlugin.logError(IStatus.ERROR, "Setter call failed:"
              + thisSubject.getName() + " Error was:"
              + e.getTargetException().getMessage(), e.getTargetException());
          res = Status.CANCEL_STATUS;
        }
        catch (final IllegalArgumentException e)
        {
          CorePlugin.logError(IStatus.ERROR, "Wrong parameters pass to:"
              + thisSubject.getName(), e);
          res = Status.CANCEL_STATUS;
        }
        catch (final IllegalAccessException e)
        {
          CorePlugin.logError(IStatus.ERROR, "Illegal access problem for:"
              + thisSubject.getName(), e);
          res = Status.CANCEL_STATUS;
        }
      }

      // and tell everybody (we only need to do this if the previous call
      // works,
      // if an exception is thrown we needn't worry about the update
      fireUpdate();

      return res;

    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      return doIt(_newValue);
    }

    private void fireUpdate()
    {
      // hmm, the method may have actually changed the data, we need to
      // find out if it
      // needs an extend
      if (_setter.isAnnotationPresent(FireExtended.class))
      {
        _layers.fireExtended(null, _parentLayer);
      }
      else if (_setter.isAnnotationPresent(FireReformatted.class))
      {
        _layers.fireReformatted(_parentLayer);
      }
      else
      {
        // hey, let's do a redraw aswell...
        _layers.fireModified(_parentLayer);
      }
    }

    @Override
    public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      return doIt(_newValue);
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      return doIt(_oldValue);
    }

  }

  /**
   * utility class that sorts property descriptors
   * 
   * @author Ian
   * 
   */
  private static class PropertyComparator implements
      Comparator<PropertyDescriptor>
  {

    @Override
    public int
        compare(final PropertyDescriptor o1, final PropertyDescriptor o2)
    {
      return o1.getName().compareTo(o2.getName());
    }
  }

  /**
   * template provide by support units that want to add items to the right-click menu when something
   * is selected
   * 
   * @author ian.mayo
   */
  public static interface RightClickContextItemGenerator
  {
    public void generate(IMenuManager parent, Layers theLayers,
        Layer[] parentLayers, Editable[] subjects);
  }

  /**
   * embedded class that encapsulates the information we need to fire an action. It was really only
   * refactored to aid debugging.
   * 
   * @author ian.mayo
   */
  private static class SubjectMethod extends Action
  {
    private final Editable[] _subjects;

    private final Method _method;

    private final Layer _topLayer;

    private final Layers _theLayers;

    /**
     * @param title
     *          what to call the action
     * @param subject
     *          the thing we're operating upon
     * @param method
     *          what we're going to run
     * @param topLayer
     *          the layer to update after the action is complete
     * @param theLayers
     *          the host for the target layer
     */
    public SubjectMethod(final String title, final Editable[] subject,
        final Method method, final Layer topLayer, final Layers theLayers)
    {
      super(title);
      _subjects = subject;
      _method = method;
      _topLayer = topLayer;
      _theLayers = theLayers;
    }

    @Override
    public void run()
    {
      final int len = _subjects.length;

      for (int cnt = 0; cnt < len; cnt++)
      {
        final Editable thisSubject = _subjects[cnt];
        try
        {
          _method.invoke(thisSubject, new Object[0]);

        }
        catch (final IllegalArgumentException e)
        {
          CorePlugin.logError(IStatus.ERROR,
              "whilst firing method from right-click", e);
        }
        catch (final IllegalAccessException e)
        {
          CorePlugin.logError(IStatus.ERROR,
              "whilst firing method from right-click", e);
        }
        catch (final InvocationTargetException e)
        {
          CorePlugin.logError(IStatus.ERROR,
              "whilst firing method from right-click", e);
        }
      }

      // hmm, the method may have actually changed the data, we need to
      // find out if it
      // needs an extend
      if (_method.isAnnotationPresent(FireExtended.class))
      {
        _theLayers.fireExtended(null, _topLayer);
      }
      else if (_method.isAnnotationPresent(FireReformatted.class))
      {
        _theLayers.fireReformatted(_topLayer);
      }
      else
      {
        // hey, let's do a redraw aswell...
        _theLayers.fireModified(_topLayer);
      }

    }
  };

  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testMe(final String val)
    {
      super(val);
    }

    public final void testAdditionalNonePresent()
    {
      final ShapeWrapper sw =
          new ShapeWrapper("rect", new RectangleShape(new WorldLocation(12.1,
              12.3, 12), new WorldLocation(1.1, 1.1, 12)), Color.red,
              new HiResDate(2222));
      final Editable[] editables = new Editable[]
      {sw};
      final MenuManager menu = new MenuManager("Holder");

      RightClickSupport.getDropdownListFor(menu, editables, null, null, null,
          true);

      boolean foundTransparent = false;

      // note: this next test may return 4 if run from within IDE,
      // some contributions provided by plugins
      assertEquals("Has items", 2, menu.getSize(), 2);

      final IContributionItem[] items = menu.getItems();
      for (int i = 0; i < items.length; i++)
      {
        final IContributionItem thisI = items[i];
        if (thisI instanceof MenuManager)
        {
          final MenuManager subMenu = (MenuManager) thisI;
          final IContributionItem[] subItems = subMenu.getItems();
          for (int j = 0; j < subItems.length; j++)
          {
            final IContributionItem subI = subItems[j];
            if (subI instanceof ActionContributionItem)
            {
              final ActionContributionItem ac = (ActionContributionItem) subI;
              final String theName = ac.getAction().getText();
              if (theName.equals("Semi transparent"))
              {
                foundTransparent = true;
              }
            }
          }
        }
      }

      assertTrue("The additional bean info got processed!", foundTransparent);
    }

    public final void testAdditionalSomePresent()
    {
      final LabelWrapper lw =
          new LabelWrapper("Some label", new WorldLocation(1.1, 1.1, 12),
              Color.red);
      final Editable[] editables = new Editable[]
      {lw};
      final MenuManager menu = new MenuManager("Holder");

      RightClickSupport.getDropdownListFor(menu, editables, null, null, null,
          true);

      // note: this next test may return 4 if run from within IDE,
      // some contributions provided by plugins
      assertEquals("Has items", 2, menu.getSize(), 2);

    }

    public final void testIntersection()
    {
      try
      {
        final PropertyDescriptor[] demo = new PropertyDescriptor[]
        {};
        final PropertyDescriptor[] pa =
            new PropertyDescriptor[]
            {new PropertyDescriptor("Color", FixWrapper.class),
                new PropertyDescriptor("Font", FixWrapper.class),
                new PropertyDescriptor("Label", FixWrapper.class),
                new PropertyDescriptor("LabelShowing", FixWrapper.class),
                new PropertyDescriptor("Visible", FixWrapper.class)};
        final PropertyDescriptor[] pb =
            new PropertyDescriptor[]
            {new PropertyDescriptor("Color", FixWrapper.class),
                new PropertyDescriptor("Font", FixWrapper.class),
                new PropertyDescriptor("Label", FixWrapper.class),
                new PropertyDescriptor("LabelShowing", FixWrapper.class),
                new PropertyDescriptor("SymbolShowing", FixWrapper.class),};
        final PropertyDescriptor[] pc =
            new PropertyDescriptor[]
            {new PropertyDescriptor("LabelShowing", FixWrapper.class),
                new PropertyDescriptor("SymbolShowing", FixWrapper.class),};
        final PropertyDescriptor[] pd = new PropertyDescriptor[]
        {};

        PropertyDescriptor[] res = getIntersectionFor(pa, pb, demo);
        assertNotNull("failed to find intersection", res);
        assertEquals("Failed to find correct num", 4, res.length);
        res = getIntersectionFor(res, pc, demo);
        assertNotNull("failed to find intersection", res);
        assertEquals("Failed to find correct num", 1, res.length);
        res = getIntersectionFor(pa, pd, demo);
        assertNotNull("failed to find intersection", res);
        assertEquals("Failed to find correct num", 0, res.length);
        res = getIntersectionFor(pd, pa, demo);
        assertNotNull("failed to find intersection", res);
        assertEquals("Failed to find correct num", 0, res.length);
      }
      catch (final IntrospectionException e)
      {
        CorePlugin.logError(IStatus.ERROR, "Whilst doing tests", e);
        assertTrue("threw some error", false);
      }
    }

    public final void testPropMgt()
    {
      final Editable itemOne =
          new FixWrapper(new Fix(new HiResDate(122333), new WorldLocation(1, 2,
              3), 12, 14));
      final Editable itemTwo =
          new FixWrapper(new Fix(new HiResDate(122334), new WorldLocation(1, 2,
              5), 13, 12));
      final Editable itemThree = new SensorWrapper("alpha");
      final Editable[] lst = new Editable[]
      {itemOne, itemTwo};
      final Editable[] lst2 = new Editable[]
      {itemOne, itemThree};
      final Editable[] lst3 = new Editable[]
      {itemThree, itemOne, itemThree};
      final Editable[] lst4 = new Editable[]
      {itemThree, itemThree};
      final Editable[] lst5 = new Editable[]
      {itemOne};
      assertEquals("no data", 2, lst.length);
      PropertyDescriptor[] props =
          RightClickSupport.getCommonPropertiesFor(lst);
      assertNotNull("found some data", props);
      assertEquals("found right matches", 14, props.length);
      props = RightClickSupport.getCommonPropertiesFor(lst2);
      assertNotNull("found some data", props);
      assertEquals("found right matches", 1, props.length);
      props = RightClickSupport.getCommonPropertiesFor(lst3);
      assertNotNull("found some data", props);
      assertEquals("found right matches", 1, props.length);
      props = RightClickSupport.getCommonPropertiesFor(lst4);
      assertNotNull("found some data", props);
      assertEquals("found right matches", 10, props.length);
      props = RightClickSupport.getCommonPropertiesFor(lst5);
      assertNotNull("found some data", props);
      assertEquals("found right matches", 14, props.length);
    }
  }

  /**
   * embedded class to store a property change in an action
   * 
   * @author ian.mayo
   */
  public static class UndoableAction extends AbstractOperation
  {
    private final SubjectAction _action;

    private final Layers _layers;

    private final Layer _parentLayer;

    private final Editable[] _subjects;

    public UndoableAction(final String propertyName, final Editable[] editable,
        final SubjectAction action, final Layers layers, final Layer parentLayer)
    {
      super(propertyName + " for "
          + (editable.length > 1 ? "multiple items" : editable[0].getName()));
      _layers = layers;
      _action = action;
      _parentLayer = parentLayer;
      _subjects = editable;
      if (CorePlugin.getUndoContext() != null)
      {
        super.addContext(CorePlugin.getUndoContext());
      }
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      IStatus res = Status.OK_STATUS;
      for (int cnt = 0; cnt < _subjects.length; cnt++)
      {
        final Editable thisSubject = _subjects[cnt];
        try
        {
          _action.execute(thisSubject);
          
        }
        catch (final IllegalArgumentException e)
        {
          CorePlugin.logError(IStatus.ERROR, "Wrong parameters pass to:"
              + thisSubject.getName(), e);
          res = Status.CANCEL_STATUS;
        }
      }

      // and tell everybody
      fireUpdate();
      return res;
    }

    private void fireUpdate()
    {
      _layers.fireExtended(null, _parentLayer);
    }

    @Override
    public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      IStatus res = Status.OK_STATUS;
      for (int cnt = 0; cnt < _subjects.length; cnt++)
      {
        final Editable thisSubject = _subjects[cnt];
        try
        {
          _action.execute(thisSubject);
        }
        catch (final Exception e)
        {
          CorePlugin.logError(IStatus.ERROR, "Failed to set new value for:"
              + thisSubject.getName(), e);
          res = Status.CANCEL_STATUS;
        }
      }

      // and tell everybody
      fireUpdate();

      return res;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      IStatus res = Status.OK_STATUS;
      for (int cnt = 0; cnt < _subjects.length; cnt++)
      {
        final Editable thisSubject = _subjects[cnt];
        try
        {
          _action.undo(thisSubject);
        }
        catch (final Exception e)
        {
          CorePlugin.logError(IStatus.ERROR, "Failed to set new value for:"
              + thisSubject.getName(), e);
          res = null;
        }
      }
      // and tell everybody
      fireUpdate();

      return res;
    }

  }

  /**
   * fixed strings for the right click support extension
   * 
   */
  private static final String EXTENSION_POINT_ID = "RightClickSupport";

  // Plug-in ID from <plugin> tag in plugin.xml
  private static final String PLUGIN_ID = "org.mwc.cmap.core";

  private static final String MULTIPLE_ITEMS_STR = "Multiple items";

  private static final int MAX_ITEMS_FOR_UNDO = 1000;

  /**
   * list of actions to be added to context-menu on right-click
   */
  private static Vector<RightClickContextItemGenerator> _additionalRightClickItems =
      null;;

  /**
   * whether we've checked for any one that extends teh right click support via plugin xml
   * 
   */
  private static boolean _rightClickExtensionsChecked = false;

  /**
   * add a right-click generator item to the list we manage
   * 
   * @param generator
   *          the generator to add...
   */
  public static void addRightClickGenerator(
      final RightClickContextItemGenerator generator)
  {
    if (_additionalRightClickItems == null)
    {
      _additionalRightClickItems =
          new Vector<RightClickContextItemGenerator>(1, 1);
    }

    _additionalRightClickItems.add(generator);
  }

  static private MenuManager generateBooleanEditorFor(
      final IMenuManager manager, final MenuManager subMenu,
      final PropertyDescriptor thisP, final Editable[] editables,
      final Layers theLayers, final Layer topLevelLayer)
  {

    boolean currentVal = false;
    final Method getter = thisP.getReadMethod();
    final Method setter = thisP.getWriteMethod();
    MenuManager result = subMenu;
    try
    {
      final Boolean valNow =
          (Boolean) getter.invoke(editables[0], (Object[]) null);
      currentVal = valNow.booleanValue();
    }
    catch (final Exception e)
    {
      CorePlugin.logError(IStatus.ERROR, "Failed to retrieve old value for:"
          + editables[0].getName(), e);
    }

    final IAction changeThis =
        new Action(thisP.getDisplayName(), IAction.AS_CHECK_BOX)
        {
          @Override
          public void run()
          {
            try
            {
              final ListPropertyAction la =
                  new ListPropertyAction(thisP.getDisplayName(), editables,
                      getter, setter, Boolean.valueOf(isChecked()), theLayers,
                      topLevelLayer);

              CorePlugin.run(la);
            }
            catch (final Exception e)
            {
              CorePlugin.logError(IStatus.INFO,
                  "While executing boolean editor for:" + thisP, e);
            }
          }
        };
    changeThis.setChecked(currentVal);
    changeThis.setToolTipText(thisP.getShortDescription());

    // is our sub-menu already created?
    if (result == null)
    {
      String nameStr;
      if (editables.length > 1)
      {
        nameStr = MULTIPLE_ITEMS_STR;
      }
      else
      {
        nameStr = editables[0].getName();
      }

      result = new MenuManager(nameStr);
      manager.add(result);
    }

    result.add(changeThis);

    return result;
  }

  @SuppressWarnings("rawtypes")
  static private MenuManager generateListEditorFor(final IMenuManager manager,
      final MenuManager subMenu, final PropertyDescriptor thisP,
      final Editable[] editables, final Layers theLayers,
      final Layer topLevelLayer)
  {

    // find out the type of the editor
    final Method m = thisP.getReadMethod();
    final Class cl = m.getReturnType();
    MenuManager result = subMenu;

    // is there a custom editor for this type?
    final Class c = thisP.getPropertyEditorClass();

    PropertyEditor pe = null;
    // try to create an editor for this class
    try
    {
      if (c != null)
      {
        pe = (PropertyEditor) c.newInstance();
      }
    }
    catch (final Exception e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }

    // did it work?
    if (pe == null)
    {
      // try to find an editor for this through our manager
      pe = PropertyEditorManager.findEditor(cl);
    }

    // retrieve the tags
    final String[] tags = pe.getTags();

    // are there any tags for this class?
    if (tags != null)
    {
      // create a drop-down list
      final MenuManager thisChoice = new MenuManager(thisP.getDisplayName());

      // sort out the setter details
      final Method getter = thisP.getReadMethod();

      // get the current value
      Object val = null;
      try
      {
        val = getter.invoke(editables[0], (Object[]) null);
      }
      catch (final Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }
      pe.setValue(val);

      // convert the current value to text
      final String currentValue = pe.getAsText();

      // and now a drop-down item for each options
      for (int j = 0; j < tags.length; j++)
      {
        final String thisTag = tags[j];
        pe.setAsText(thisTag);
        final Object thisValue = pe.getValue();

        // create the item
        final IAction thisA = new Action(thisTag, IAction.AS_RADIO_BUTTON)
        {
          @Override
          public void run()
          {
            try
            {
              // hey, since this is a radio button, we get two events when the
              // selection changes - one for the value being unset, and the
              // other
              // for the value being set. So just fire for the new value (the
              // one that's checked)
              if (isChecked())
              {
                final Method setter = thisP.getWriteMethod();

                // ok, place the change in the action
                final ListPropertyAction la =
                    new ListPropertyAction(thisP.getDisplayName(), editables,
                        getter, setter, thisValue, theLayers, topLevelLayer);

                // and add it to the history
                CorePlugin.run(la);
              }
            }
            catch (final Exception e)
            {
              CorePlugin.logError(IStatus.INFO,
                  "While executing select editor for:" + thisP, e);
            }
          }

        };

        // is this the current one?
        if (thisTag.equals(currentValue))
        {
          thisA.setChecked(true);
        }

        // add it to the menu
        thisChoice.add(thisA);

      }

      // is our sub-menu already created?
      if (result == null)
      {
        String nameStr;
        if (editables.length > 1)
        {
          nameStr = MULTIPLE_ITEMS_STR;
        }
        else
        {
          nameStr = editables[0].getName();
        }

        result = new MenuManager(nameStr);
        manager.add(result);
      }

      result.add(thisChoice);

    }

    return result;
  }

  static private IAction generateUndoableActionFor(
      final MWC.GUI.Tools.SubjectAction theAction, final Editable[] editables,
      final Layers theLayers, final Layer topLevelLayer)
  {

    final IAction changeThis =
        new Action(theAction.toString(), IAction.AS_PUSH_BUTTON)
        {
          @Override
          public void run()
          {
            try
            {
              final AbstractOperation la =
                  new UndoableAction(theAction.toString(), editables,
                      theAction, theLayers, topLevelLayer);

              CorePlugin.run(la);
            }
            catch (final Exception e)
            {
              CorePlugin.logError(IStatus.INFO,
                  "While executing undoable operations for for:"
                      + theAction.toString(), e);
            }
          }
        };
    return changeThis;
  }

  /** have a look at the supplied editors, find which properties are common */
  protected static MethodDescriptor[] getCommonMethodsFor(
      final Editable[] editables)
  {
    MethodDescriptor[] res = null;
    final MethodDescriptor[] demo = new MethodDescriptor[]
    {};

    final int len = editables.length;

    // right, get the first set of properties
    if (len > 0)
    {
      final Editable first = editables[0];
      final EditorType firstInfo = first.getInfo();
      if (firstInfo != null)
      {
        res = firstInfo.getMethodDescriptors();

        // only continue if there are any methods to compare against
        if (res != null)
        {
          // right, are there any more?
          if (len > 1)
          {
            // pass through the others, finding the common ground
            for (int cnt = 1; cnt < len; cnt++)
            {
              final Editable thisE = editables[cnt];

              // get its props
              final EditorType thisEditor = thisE.getInfo();

              // do we have an editor?
              if (thisEditor != null)
              {
                final MethodDescriptor[] newSet =
                    thisEditor.getMethodDescriptors();

                // check we're not already looking at an instance of this type
                if (newSet != res)
                {
                  // find the common ones
                  res = getIntersectionFor(res, newSet, demo);
                }
              }
              else
              {
                // handle instance where editable doesn't have anything editable
                res = null;
                break;
              }

            }
          }
        }
        else
        {
          // handle instance where editable doesn't have anything editable
          res = null;
        }

      }
    }

    return res;
  }

  /** have a look at the supplied editors, find which properties are common */
  protected static PropertyDescriptor[] getCommonPropertiesFor(
      final Editable[] editables)
  {
    PropertyDescriptor[] res = null;
    final PropertyDescriptor[] demo = new PropertyDescriptor[]
    {};

    final int len = editables.length;

    // right, get the first set of properties
    if (len > 0)
    {
      final Editable first = editables[0];
      final EditorType firstInfo = first.getInfo();
      if (firstInfo != null)
      {
        res = firstInfo.getPropertyDescriptors();

        // only continue if there are any property descriptors
        if (res != null)
        {
          // right, are there any more?
          if (len > 1)
          {
            // pass through the others, finding the common ground
            for (int cnt = 1; cnt < len; cnt++)
            {
              final Editable thisE = editables[cnt];

              // get its props
              final EditorType thisEditor = thisE.getInfo();

              // do we have an editor?
              if (thisEditor != null)
              {
                final PropertyDescriptor[] newSet =
                    thisEditor.getPropertyDescriptors();

                // just double-check that we aren't already looking at these props
                // (we do if it's lots of the same item selected
                if (res != newSet)
                {
                  // find the common ones
                  res = getIntersectionFor(res, newSet, demo);
                }
              }
              else
              {
                // handle instance where editable doesn't have anything editable
                res = null;
                break;
              }

            }
          }
        }
        else
        {
          // handle instance where editable doesn't have anything editable
          res = null;
        }
      }
    }

    return res;
  }

  /**
   * @param manager
   *          where we add our items to
   * @param editables
   *          the selected items
   * @param topLevelLayers
   *          the top-level layers that contain our elements (it's these that get updated)
   * @param parentLayers
   *          the immediate parents of our items
   * @param theLayers
   *          the overall layers object
   * @param hideClipboardOperations
   */
  static public void getDropdownListFor(final IMenuManager manager,
      final Editable[] editables, final Layer[] topLevelLayers,
      final HasEditables[] parentLayers, final Layers theLayers,
      final boolean hideClipboardOperations)
  {

    // sort out the top level layer, if we have a single one
    // Note: if we have more than one top level layer we don't populate the top
    // level layer - so the whole plot gets updated
    Layer theTopLayer = null;
    if (topLevelLayers != null)
    {
      if (topLevelLayers.length == 1)
      {
        theTopLayer = topLevelLayers[0];
      }
    }

    // and now the edit-able bits
    if (editables.length > 0)
    {
      // first the parameters
      MenuManager subMenu = null;
      final PropertyDescriptor[] commonProps =
          getCommonPropertiesFor(editables);
      if (commonProps != null)
      {

        // hey, can we group these descriptors?
        final Map<String, SortedSet<PropertyDescriptor>> map =
            mapThese(commonProps);

        for (final String thisCat : map.keySet())
        {
          final SortedSet<PropertyDescriptor> list = map.get(thisCat);
          for (final PropertyDescriptor thisP : list)
          {

            // start off with the booleans
            if (supportsBooleanEditor(thisP))
            {
              // generate boolean editors in the sub-menu
              subMenu =
                  generateBooleanEditorFor(manager, subMenu, thisP, editables,
                      theLayers, theTopLayer);
            }
            else
            {
              // now the drop-down lists
              if (supportsListEditor(thisP))
              {
                // generate boolean editors in the sub-menu
                subMenu =
                    generateListEditorFor(manager, subMenu, thisP, editables,
                        theLayers, theTopLayer);
              }
            }
          }
          
          if (subMenu != null)
          {
            // and a separator
            subMenu.add(new Separator(thisCat));
          }
        }
      }

      // special case: if only one item is selected, try adding any additional
      // methods
      if (editables.length == 1)
      {
        // any additional ones?
        final Editable theE = editables[0];

        // ok, get the editor
        final EditorType info = theE.getInfo();

        if (info != null)
        {
          final BeanInfo[] additional = info.getAdditionalBeanInfo();

          // any there?
          if (additional != null)
          {
            // ok, loop through the beans
            for (int i = 0; i < additional.length; i++)
            {
              final BeanInfo thisB = additional[i];
              if (thisB instanceof EditorType)
              {
                final EditorType editor = (EditorType) thisB;
                final Editable subject = (Editable) editor.getData();

                // and the properties
                final PropertyDescriptor[] theseProps =
                    thisB.getPropertyDescriptors();

                // hey, can we group these descriptors?
                final Map<String, SortedSet<PropertyDescriptor>> map =
                    mapThese(theseProps);

                for (final String thisCat : map.keySet())
                {
                  final SortedSet<PropertyDescriptor> list = map.get(thisCat);
                  for (final PropertyDescriptor thisP : list)
                  {
                    // and wrap the object
                    final Editable[] holder = new Editable[]
                    {subject};
                    if (supportsBooleanEditor(thisP))
                    {

                      // generate boolean editors in the sub-menu
                      subMenu =
                          generateBooleanEditorFor(manager, subMenu, thisP,
                              holder, theLayers, theTopLayer);
                    }
                    else
                    {
                      // now the drop-down lists
                      if (supportsListEditor(thisP))
                      {
                        // generate boolean editors in the sub-menu
                        subMenu =
                            generateListEditorFor(manager, subMenu, thisP,
                                holder, theLayers, theTopLayer);
                      }
                    }
                  }
                  // and a separator
                  subMenu.add(new Separator());

                }
              }
            }
          }
        }
      }

      // hmm, have a go at methods for this item
      // ok, try the methods
      final MethodDescriptor[] meths = getCommonMethodsFor(editables);
      if (meths != null)
      {
        for (int i = 0; i < meths.length; i++)
        {
          final Layer myTopLayer = theTopLayer;

          final MethodDescriptor thisMethD = meths[i];

          if (thisMethD == null)
          {
            CorePlugin.logError(IStatus.ERROR,
                "Failed to create method, props may be wrongly named", null);
          }
          else
          {
            // create button for this method
            final Action doThisAction =
                new SubjectMethod(thisMethD.getDisplayName(), editables,
                    thisMethD.getMethod(), myTopLayer, theLayers);

            // ok - add to the list.
            manager.add(doThisAction);
          }
        }
      }

      // hmm, now do the same for the undoable methods
      final SubjectAction[] actions =
          getUndoableActionsFor(editables);
      if (actions != null)
      {
        for (int i = 0; i < actions.length; i++)
        {
          final SubjectAction thisMethD = actions[i];

          // create button for this method
          final IAction doThisAction =
              generateUndoableActionFor(thisMethD, editables, theLayers,
                  theTopLayer);

          // ok - add to the list.
          manager.add(doThisAction);
        }
      }

    }

    // see if we're still looking at the parent element (we only show
    // clipboard
    // operations for item clicked on)
    if (!hideClipboardOperations)
    {
      final Clipboard theClipboard = CorePlugin.getDefault().getClipboard();

      // hey, also see if we're going to do a cut/paste
      RightClickCutCopyAdaptor.getDropdownListFor(manager, editables,
          topLevelLayers, parentLayers, theLayers, theClipboard);

      // what about paste?
      Editable selectedItem = null;
      if (editables.length == 1)
      {
        selectedItem = editables[0];
      }
      RightClickPasteAdaptor.getDropdownListFor(manager, selectedItem,
          topLevelLayers, parentLayers, theLayers, theClipboard);

      manager.add(new Separator());
    }

    if (!_rightClickExtensionsChecked)
    {
      loadLoaderExtensions();

      // ok, done
      _rightClickExtensionsChecked = true;
    }
    /* no params */
    // hmm, do we have any right-click generators?
    if (_additionalRightClickItems != null)
    {
      for (final Iterator<RightClickContextItemGenerator> thisItem =
          _additionalRightClickItems.iterator(); thisItem.hasNext();)
      {
        final RightClickContextItemGenerator thisGen = thisItem.next();

        try
        {
          thisGen.generate(manager, theLayers, topLevelLayers, editables);
        }
        catch (final Exception e)
        {
          // and log the error
          CorePlugin.logError(IStatus.ERROR,
              "failed whilst creating context menu", e);
        }
      }
    }
  }

  /**
   * have a look at the two arrays, and find the common elements (brute force)
   * 
   * @param a
   *          first array
   * @param b
   *          second array
   * @return the common elements
   */
  protected static MethodDescriptor[] getIntersectionFor(
      final MethodDescriptor[] a, final MethodDescriptor[] b,
      final MethodDescriptor[] demo)
  {
    final Vector<MethodDescriptor> res = new Vector<MethodDescriptor>();

    if (a != null && b != null)
    {
      final int aLen = a.length;
      final int bLen = b.length;

      for (int cnta = 0; cnta < aLen; cnta++)
      {
        final MethodDescriptor thisP = a[cnta];
        if (b != null)
        {
          for (int cntb = 0; cntb < bLen; cntb++)
          {
            final MethodDescriptor thatP = b[cntb];
            if (thisP.getDisplayName().equals(thatP.getDisplayName()))
            {
              res.add(thisP);
            }
          }
        }
      }
    }
    return res.toArray(demo);
  }

  private static SubjectAction[] getIntersectionFor(
      final SubjectAction[] a,
      final SubjectAction[] b,
      final SubjectAction[] demo)
  {
    final Vector<SubjectAction> res =
        new Vector<SubjectAction>();

    final int aLen = a.length;
    final int bLen = b.length;

    for (int cnta = 0; cnta < aLen; cnta++)
    {
      final SubjectAction thisP = a[cnta];
      for (int cntb = 0; cntb < bLen; cntb++)
      {
        final SubjectAction thatP = b[cntb];
        if (thisP.toString().equals(thatP.toString()))
        {
          res.add(thisP);
        }
      }
    }
    return res.toArray(demo);
  }

  /**
   * have a look at the two arrays, and find the common elements (brute force)
   * 
   * @param a
   *          first array
   * @param b
   *          second array
   * @return the common elements
   */
  protected static PropertyDescriptor[] getIntersectionFor(
      final PropertyDescriptor[] a, final PropertyDescriptor[] b,
      final PropertyDescriptor[] demo)
  {
    final Vector<PropertyDescriptor> res = new Vector<PropertyDescriptor>();

    final int aLen = a.length;
    final int bLen = b.length;

    for (int cnta = 0; cnta < aLen; cnta++)
    {
      final PropertyDescriptor thisP = a[cnta];
      for (int cntb = 0; cntb < bLen; cntb++)
      {
        final PropertyDescriptor thatP = b[cntb];
        if (thisP.equals(thatP))
        {
          res.add(thisP);
        }
      }
    }
    return res.toArray(demo);
  }

  /** have a look at the supplied editors, find which properties are common */
  protected static SubjectAction[] getUndoableActionsFor(
      final Editable[] editables)
  {
    SubjectAction[] res = null;
    final SubjectAction[] demo =
        new SubjectAction[]
        {};

    // right, get the first set of properties
    final int len = editables.length;

    // are there a reasonable number of them?
    if (len > 0 && len < MAX_ITEMS_FOR_UNDO)
    {
      final Editable first = editables[0];
      final EditorType firstInfo = first.getInfo();
      if (firstInfo != null)
      {
        res = firstInfo.getUndoableActions();

        // only continue if there are any methods to compare against
        if (res != null)
        {
          // right, are there any more?
          if (len > 1)
          {
            // pass through the others, finding the common ground
            for (int cnt = 1; cnt < len; cnt++)
            {
              final Editable thisE = editables[cnt];

              // get its props
              final EditorType thisEditor = thisE.getInfo();

              // do we have an editor?
              if (thisEditor != null)
              {
                final SubjectAction[] newSet =
                    thisEditor.getUndoableActions();

                // find the common ones
                res = getIntersectionFor(res, newSet, demo);
              }
            }
          }
        }
      }
    }
    return res;
  }

  /**
   * see if any extra right click handlers are defined
   * 
   */
  private static void loadLoaderExtensions()
  {
    final IExtensionRegistry registry = Platform.getExtensionRegistry();

    if (registry != null)
    {
      final IExtensionPoint point =
          registry.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);

      final IExtension[] extensions = point.getExtensions();
      for (int i = 0; i < extensions.length; i++)
      {
        final IExtension iExtension = extensions[i];
        final IConfigurationElement[] confE =
            iExtension.getConfigurationElements();
        for (int j = 0; j < confE.length; j++)
        {
          final IConfigurationElement iConfigurationElement = confE[j];
          RightClickContextItemGenerator newInstance;
          try
          {
            newInstance =
                (RightClickContextItemGenerator) iConfigurationElement
                    .createExecutableExtension("class");
            addRightClickGenerator(newInstance);
          }
          catch (final CoreException e)
          {
            CorePlugin.logError(IStatus.ERROR,
                "Trouble whilst loading right-click handler extensions", e);
          }
        }
      }
    }
  }

  private static Map<String, SortedSet<PropertyDescriptor>> mapThese(
      final PropertyDescriptor[] theseProps)
  {
    final Map<String, SortedSet<PropertyDescriptor>> res =
        new TreeMap<String, SortedSet<PropertyDescriptor>>();
    if (theseProps != null)
    {
      for (final PropertyDescriptor thisP : theseProps)
      {
        final String cat;
        if (thisP instanceof CategorisedPropertyDescriptor)
        {
          final CategorisedPropertyDescriptor catProp =
              (CategorisedPropertyDescriptor) thisP;
          cat = catProp.getCategory();
        }
        else
        {
          cat = "Unknown";
        }
        // do we have this list?
        SortedSet<PropertyDescriptor> thisL = res.get(cat);
        if (thisL == null)
        {
          final Comparator<PropertyDescriptor> comparator =
              new PropertyComparator();

          thisL = new TreeSet<PropertyDescriptor>(comparator);
          res.put(cat, thisL);
        }
        thisL.add(thisP);
      }
    }
    return res;
  }

  /**
   * can we edit this property with a tick-box?
   * 
   * @param thisP
   * @return yes/no
   */
  @SuppressWarnings("rawtypes")
  static private boolean supportsBooleanEditor(final PropertyDescriptor thisP)
  {
    final boolean res;

    // get the prop type
    final Class thisType = thisP.getPropertyType();
    final Class boolClass = Boolean.class;

    // is it boolean?
    if ((thisType == boolClass) || (thisType.equals(boolean.class)))
    {
      res = true;
    }
    else
    {
      res = false;
    }

    return res;
  }

  /**
   * can we edit this property with a drop-down list?
   * 
   * @param thisP
   * @return yes/no
   */
  @SuppressWarnings("rawtypes")
  static private boolean supportsListEditor(final PropertyDescriptor thisP)
  {
    boolean res = false;

    // find out the type of the editor
    final Method m = thisP.getReadMethod();
    final Class cl = m.getReturnType();

    // is there a custom editor for this type?
    final Class c = thisP.getPropertyEditorClass();

    PropertyEditor pe = null;
    // try to create an editor for this class
    try
    {
      if (c != null)
      {
        pe = (PropertyEditor) c.newInstance();
      }
    }
    catch (final Exception e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }

    // did it work?
    if (pe == null)
    {
      // try to find an editor for this through our manager
      pe = PropertyEditorManager.findEditor(cl);
    }

    // have we managed to create an editor?
    if (pe != null)
    {
      // retrieve the tags
      final String[] tags = pe.getTags();

      // are there any tags for this class?
      if (tags != null)
      {
        res = true;
      }
    }

    return res;
  }
}
