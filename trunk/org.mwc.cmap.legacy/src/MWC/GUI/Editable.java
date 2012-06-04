// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Editable.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.18 $
// $Log: Editable.java,v $
// Revision 1.18  2006/05/02 13:26:18  Ian.Mayo
// Introduce deprecated properties
//
// Revision 1.17  2005/12/09 14:54:13  Ian.Mayo
// Minor tidying
//
// Revision 1.16  2005/05/19 14:46:51  Ian.Mayo
// Add more categories to editable bits
//
// Revision 1.15  2005/01/28 09:35:11  Ian.Mayo
// Add support for property categories, include Eclipse tidying
//
// Revision 1.14  2004/11/25 09:10:00  Ian.Mayo
// Don't worry about deprecating the test any more - we're on top of the ASSET testing bits now
//
// Revision 1.13  2004/10/19 08:27:44  Ian.Mayo
// Correct how we test the editable properties for an object (we weren't testing them before. bugger).
//
// Revision 1.12  2004/10/18 08:51:37  Ian.Mayo
// Cancel the final attribute previously applied to some Editor methods which are over-ridden in Debrief but not in ASSET
//
// Revision 1.11  2004/10/16 16:11:19  ian
// Minor to change to prevent compilation warning in NetBeans
//
// Revision 1.10  2004/10/16 14:10:23  ian
// Implement reporting from watched data items.  Implement Intellij code analysis.
//
// Revision 1.9  2004/10/07 14:23:20  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.8  2004/09/06 14:04:40  Ian.Mayo
// Switch to supporting editables in Layer Manager, and showing icon for any editables which have one
//
// Revision 1.7  2004/08/31 15:28:17  Ian.Mayo
// Polish off test refactoring, start Intercept behaviour
//
// Revision 1.6  2004/08/26 13:23:24  Ian.Mayo
// Re-instate old property tester, but mark as deprecated
//
// Revision 1.5  2004/08/26 12:24:08  Ian.Mayo
// Refactor editable testing back to editable
//
// Revision 1.4  2004/08/26 11:01:57  Ian.Mayo
// Implement core editable property testing
//
// Revision 1.3  2004/05/25 15:45:29  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.2  2003/08/22 15:02:00  Ian.Mayo
// Improve error reporting
//
// Revision 1.1.1.1  2003/07/17 10:07:03  Ian.Mayo
// Initial import
//
// Revision 1.8  2003-07-04 11:00:56+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.7  2003-03-24 11:08:35+00  ian_mayo
// Only check methods when they've been returned
//
// Revision 1.6  2003-03-21 15:44:36+00  ian_mayo
// Also check the correct methods are present
//
// Revision 1.5  2003-02-12 16:19:55+00  ian_mayo
// Part way through introduction of help support
//
// Revision 1.4  2002-11-01 14:43:58+00  ian_mayo
// minor tidying
//
// Revision 1.3  2002-10-30 15:35:53+00  ian_mayo
// better management of display name in tabs
//
// Revision 1.2  2002-05-28 09:25:36+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:26+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-03-19 11:04:42+00  administrator
// Add boolean method which indicates whether additionalBeanInfo should be combined with BeanInfo when shown in popup menu (default false)
//
// Revision 1.2  2002-01-24 10:49:57+00  administrator
// Don't use local name variable, use method (which can be over-ridden by child class)
//
// Revision 1.1  2001-08-01 20:09:32+01  administrator
// Don't fail test for bad setter, if we're not even passing in a good value.  Just place warning text on output
//
// Revision 1.0  2001-07-17 08:46:35+01  administrator
// Initial revision
//
// Revision 1.5  2001-06-04 09:31:49+01  novatech
// add function to tidy up return of properties
//
// Revision 1.4  2001-01-22 12:29:28+00  novatech
// added JUnit testing code
//
// Revision 1.3  2001-01-17 13:26:00+00  novatech
// provide convenience methods which set properties as Expert, so that they are not edited by child classes
//
// Revision 1.2  2001-01-05 09:09:49+00  novatech
// Provide callback for when all updates are complete (with default implementation)
//
// Revision 1.1  2001-01-03 13:43:06+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:47  ianmayo
// initial version
//
// Revision 1.8  2000-10-31 15:42:39+00  ian_mayo
// perform tidying up to keep JBuilder happy
//
// Revision 1.7  2000-10-09 13:35:53+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.6  2000-08-18 13:34:34+01  ian_mayo
// implement PropertyChangeListeners
//
// Revision 1.5  2000-08-18 10:06:45+01  ian_mayo
// <>
//
// Revision 1.4  2000-08-17 10:22:20+01  ian_mayo
// insert comments
//
// Revision 1.3  2000-08-11 08:42:02+01  ian_mayo
// tidy beaninfo
//
// Revision 1.2  1999-11-26 15:45:33+00  ian_mayo
// adding toString methods
//
// Revision 1.1  1999-10-12 15:37:07+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-04 09:45:29+01  administrator
// minor mods, tidying up
//
// Revision 1.1  1999-07-27 10:50:50+01  administrator
// Initial revision
//
// Revision 1.3  1999-07-19 12:40:33+01  administrator
// added storage of sub-second time data (Switched to storing as Long rather than java.utils.Date)
//

package MWC.GUI;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;
import junit.framework.TestCase;
import MWC.GUI.Properties.Swing.SwingPropertyEditor2;
import MWC.GUI.Tools.SubjectAction;

/**
 * Interface defining behaviour for a screen item which should be editable by
 * the user (using Bean properties) <p/> The file also contains an abstract
 * which implements the required behaviour
 */
public interface Editable
{

	/**
   * the name of this object
   * 
   * @return the name of this editable object
   */
  String getName();

  /**
   * whether there is any edit information for this item this is a convenience
   * function to save creating the EditorType data first
   * 
   * @return yes/no
   */
  boolean hasEditor();

  /**
   * get the editor for this item
   * 
   * @return the BeanInfo data for this editable object
   */
  EditorType getInfo();

  /**
   * get the help-text id for this object
   * 
   */
  // public HelpContext getHelpInfo();

	/** marker interface for objects with changeable descriptors - so we re-calculate them everytime
	 * 
	 */
  public interface DynamicDescriptors
	{

	}

  
  /**
   * ******************************************************************* class
   * containing the help description for a particular
   * *******************************************************************
   */
  public class HelpContext
  {
    private final String _pageName;

    private final String _topicName;

    public HelpContext(final String pageName, final String topicName)
    {
      _pageName = pageName;
      _topicName = topicName;
    }

    public final String getHelpContext()
    {
      return _pageName + "\\" + _topicName;
    }
  }

  /**
   * definition of data required for editing
   */
  public class EditorType extends SimpleBeanInfo implements
      hasPropertyListeners
  {

    /**
     * the standard categories to use
     * 
     */
    public final static String FORMAT = "Format";

    public final static String SPATIAL = "Spatial";

    public final static String VISIBILITY = "Visibility";
    
    public final static String TEMPORAL = "Time-Related";
    
    public final static String OPTIONAL = "Optional";

    /***************************************************************************
     * member values
     **************************************************************************/

    /**
     * the data item for this object
     */
    final Object _data;

    /**
     * the name of this instance
     */
    final String _name;

    /**
     * the class of this object
     */
    final Class<?> _class;

    /**
     * the name used for the display of this type of object
     */
    final String _displayName;

    /**
     * helper class for managing properties
     */
    PropertyChangeSupport _pSupport = null;

    /**
     * the path to the icon for this object. Icon is shown in Layer Manager
     */
    private final String _myIcon;

    /**
     * the property name for report updates
     */
    public static final String REPORT_NAME = "REPORT";

    /**
     * whether this item is able to fire reports
     */
    private final boolean _firesReports;

    /***************************************************************************
     * constructor
     **************************************************************************/

    /**
     * constructor for an editor type
     * 
     * @param data
     *          object we are editing
     * @param name
     *          name of this object
     * @param displayName
     *          name type of object we are editing
     */
    public EditorType(final Object data, final String name,
        final String displayName)
    {
      this(data, name, displayName, null, false);
    }

    /**
     * constructor for an editor type
     * 
     * @param data
     *          object we are editing
     * @param name
     *          name of this object
     * @param displayName
     *          name type of object we are editing
     */
    public EditorType(final Object data, final String name,
        final String displayName, final boolean firesReports)
    {
      this(data, name, displayName, null, firesReports);
    }

    /**
     * constructor which also takes a path to the icon for this editor
     * 
     * @param data
     * @param name
     * @param displayName
     * @param iconPath
     */
    public EditorType(final Object data, final String name,
        final String displayName, final String iconPath)
    {
      this(data, name, displayName, iconPath, false);
    }

    /**
     * constructor which also takes a path to the icon for this editor
     * 
     * @param data
     * @param name
     * @param displayName
     * @param iconPath
     */
    public EditorType(final Object data, final String name,
        final String displayName, final String iconPath,
        final boolean firesReports)
    {
      _data = data;
      _name = name;
      _class = data.getClass();
      _displayName = displayName;
      _myIcon = iconPath;
      _firesReports = firesReports;
    }

    /***************************************************************************
     * member variables
     **************************************************************************/

    /**
     * determine if this editable item provides status reports
     * 
     * @return yes/no for if it provides reports
     */
    public final boolean firesReports()
    {
      return _firesReports;
    }

    /**
     * determine if anybody is listening to us, to see if it's worth firing a
     * report
     */
    public final boolean hasReportListeners()
    {
      boolean res = false;
      // do we have a property change listener at all?
      if (_pSupport != null)
      {
        // yes, does it have any report listeners?
        if (_pSupport.getPropertyChangeListeners(REPORT_NAME).length > 0)
        {
          // yes. good. let's report it.
          res = true;
        }
      }
      return res;
    }

    /**
     * accessor to get the path to the icon (prob of the form
     * images/icons/xxx.gif)
     * 
     * @return Path to icon
     */
    public final String getIconPath()
    {
      return _myIcon;
    }

    /**
     * output as string
     * 
     * @return name of this object
     */
    public final String toString()
    {
      return getName();
    }

    /**
     * whether the normal editable properties should be combined with the
     * additional editable properties into a single list. This is typically used
     * for a composite object which has two lists of editable properties but
     * which is seen by the user as a single object To be overwritten to change
     * it
     */
    public boolean combinePropertyLists()
    {
      return false;
    }

    /**
     * Deny knowledge of properties. You can override this if you wish to
     * provide explicit property info.
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
			MWC.Utilities.Errors.Trace
			.trace("Possible problem collating properties for:" + getName() + " (" + getData().getClass() + ")", false);

      return super.getPropertyDescriptors();
    }

    /** get a series of undo-able operations
     * 
     * @return the list
     */
		public SubjectAction[] getUndoableActions()
		{
			return null;
		}

    
    /**
     * add the property listener which just listens out for a single property
     * type
     * 
     * @param propertyName
     * @param listener
     */
    public final void addPropertyChangeListener(final String propertyName,
        final PropertyChangeListener listener)
    {
      if (_pSupport == null)
        _pSupport = new PropertyChangeSupport(this);

      _pSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * remove the specific property change listener
     * 
     * @param propertyName
     * @param listener
     */
    public final void removePropertyChangeListener(final String propertyName,
        final PropertyChangeListener listener)
    {
      _pSupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * we have somebody who wants to listen out for events of this object
     * 
     * @param listener
     *          the new listener
     */
    public final void addPropertyChangeListener(
        final PropertyChangeListener listener)
    {
      if (_pSupport == null)
        _pSupport = new PropertyChangeSupport(this);

      _pSupport.addPropertyChangeListener(listener);
    }

    /**
     * somebody has now finished listening
     * 
     * @param listener
     *          the ex-listener
     */
    public final void removePropertyChangeListener(
        final PropertyChangeListener listener)
    {
    	if(_pSupport != null)
        _pSupport.removePropertyChangeListener(listener);
    }

    /**
     * fire an update event
     * 
     * @param source
     *          the object which is making the update
     * @param name
     *          the name of the parameter which has been updated
     * @param oldVal
     *          the old value
     * @param newVal
     *          the new value
     */
    public final void fireChanged(final Object source, final String name,
        final Object oldVal, final Object newVal)
    {
      if (_pSupport != null)
      {
        final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(
            source, name, oldVal, newVal);
        _pSupport.firePropertyChange(pce);
      }
    }

    /**
     * the 'type' of this object such as Ellipse, etc
     * 
     * @return the name
     */
    public String getDisplayName()
    {
      return _displayName + " " + getName();
    }

    /**
     * name of this object
     * 
     * @return the name
     */
    public String getName()
    {
      return _name;
    }

    /**
     * object we are editing
     * 
     * @return the object
     */
    public final Object getData()
    {
      return _data;
    }

    /**
     * build up description of this bean
     * 
     * @return description
     */
    public BeanDescriptor getBeanDescriptor()
    {
      final BeanDescriptor bp = new BeanDescriptor(_class);
      bp.setDisplayName(getDisplayName());
      return bp;
    }
    
    /**
     * convenience class to create an expert property
     * 
     * @param name
     *          name of this property
     * @param description
     *          description of this property
     * @return property description
     * @throws IntrospectionException
     *           if the methods can't be found
     */
    protected final PropertyDescriptor legacyProp(final String name,
        final String description) throws IntrospectionException
    {
      final PropertyDescriptor p = new DeprecatedPropertyDescriptor(name, _class);
      p.setShortDescription(description);
      p.setExpert(true);
      return p;
    }    

    /**
     * convenience class to create an expert property
     * 
     * @param name
     *          name of this property
     * @param description
     *          description of this property
     * @return property description
     * @throws IntrospectionException
     *           if the methods can't be found
     */
    protected final PropertyDescriptor expertProp(final String name,
        final String description) throws IntrospectionException
    {
      final PropertyDescriptor p = new PropertyDescriptor(name, _class);
      p.setShortDescription(description);
      p.setExpert(true);
      return p;
    }
    
    /**
     * convenience class to create an expert property
     * 
     * @param name
     *          name of this property
     * @param description
     *          description of this property
     * @return property description
     * @throws IntrospectionException
     *           if the methods can't be found
     */
    protected final PropertyDescriptor expertProp(final String name,
        final String description, final String category) throws IntrospectionException
    {
      final PropertyDescriptor p = new CategorisedPropertyDescriptor(category, name, _class);
      p.setShortDescription(description);
      p.setExpert(true);
      return p;
    }    

    /**
     * convenience class to create a property
     * 
     * @param name
     *          name of this property
     * @param description
     *          description of this property
     * @return property description
     * @throws IntrospectionException
     *           if the methods can't be found
     */
    protected final PropertyDescriptor prop(final String name,
        final String description) throws IntrospectionException
    {
      final PropertyDescriptor p = new PropertyDescriptor(name, _class);
      p.setShortDescription(description);
      return p;
    }

    /**
     * convenience class to create a property
     * 
     * @param name
     *          name of this property
     * @param description
     *          description of this property
     * @return property description
     * @throws IntrospectionException
     *           if the methods can't be found
     */
    protected final PropertyDescriptor prop(final String name,
        final String description, final String category)
        throws IntrospectionException
    {
      final PropertyDescriptor p = new CategorisedPropertyDescriptor(category,
          name, _class);
      p.setShortDescription(description);
      return p;
    }

    /**
     * convenience function for creating property descriptor for property which
     * has its own editor
     * 
     * @param name
     *          name of property
     * @param description
     *          description of property (for tooltip)
     * @param editor
     *          editor to use for this property
     * @return propertyDescriptor for this object
     * @throws IntrospectionException
     *           if we can't create property
     */
    protected final PropertyDescriptor expertLongProp(final String name,
        final String description, final Class<?> editor)
        throws IntrospectionException
    {
      final PropertyDescriptor p = new PropertyDescriptor(name, _class);
      p.setShortDescription(description);
      p.setPropertyEditorClass(editor);
      p.setExpert(true);
      return p;
    }

    /**
     * convenience function for creating property descriptor for property which
     * has its own editor
     * 
     * @param name
     *          name of property
     * @param description
     *          description of property (for tooltip)
     * @param editor
     *          editor to use for this property
     * @return propertyDescriptor for this object
     * @throws IntrospectionException
     *           if we can't create property
     */
    protected final PropertyDescriptor longProp(final String name,
        final String description, final Class<?> editor)
        throws IntrospectionException
    {
      final PropertyDescriptor p = new PropertyDescriptor(name, _class);
      p.setShortDescription(description);
      p.setPropertyEditorClass(editor);
      return p;
    }

    /**
     * convenience function for creating property descriptor for property which
     * has its own editor
     * 
     * @param name
     *          name of property
     * @param description
     *          description of property (for tooltip)
     * @param editor
     *          editor to use for this property
     * @param category
     *          the category for this property     
     * @return propertyDescriptor for this object
     * @throws IntrospectionException
     *           if we can't create property
     */
    protected final PropertyDescriptor longProp(final String name,
        final String description, final Class<?> editor, final String category)
        throws IntrospectionException
    {
      final PropertyDescriptor p = new CategorisedPropertyDescriptor(category,
          name, _class);
      p.setShortDescription(description);
      p.setPropertyEditorClass(editor);
      return p;
    }

    /**
     * convenience method to create methodDescriptor for this object
     * 
     * @param theClass
     *          class type for this object
     * @param name
     *          name of this object
     * @param params
     *          parameters to pass to this method
     * @param displayName
     *          name to display for this method (tooltop)
     * @return a method descriptor
     */
    public static final MethodDescriptor method(final Class<?> theClass,
        final String name, final Class<?>[] params, final String displayName)
    {
      MethodDescriptor res = null;
      java.lang.reflect.Method m;
      try
      {
        m = theClass.getMethod(name, params);
        res = new MethodDescriptor(m);
        res.setDisplayName(displayName);
      }
      catch (Exception e)
      {
      	String msg = "Failed to find method " + name + " for " + theClass;
        MWC.Utilities.Errors.Trace.trace(e, msg);
      }
      return res;
    }

    /**
     * callback to indicate that updates to this item are complete. typically an
     * editor will update a number of attributes, in an unknown sequence. This
     * callback is called at the end of this process
     */
    public void updatesComplete()
    {
      // duff implementation
    }

    /**
     * convenience class to wrap sending a report into a property change event
     * 
     * @param source
     *          the source object for the report
     * @param msg
     *          the report itself
     */
    public final void fireReport(final Object source, final String msg)
    {
      if (!_firesReports)
        throw new RuntimeException(
            "Tried to fire report via editable not declared as such:" + _data);

      fireChanged(source, REPORT_NAME, null, msg);
    }

  }


  /** convenience class for marking a property as deprecated (one that we won't load in NG)
   * 
   * @author ian.mayo
   *
   */
  public static class DeprecatedPropertyDescriptor extends PropertyDescriptor
  {
  	/** constructor - just pass the data on
  	 * 
  	 * @param name
  	 * @param _class
  	 * @throws IntrospectionException
  	 */
		public DeprecatedPropertyDescriptor(String name, Class<?> _class)  throws IntrospectionException
		{
			super(name, _class);
		}
  }
  
  /**
   * ******************************************************************* class
   * adding category information to a property (used in SWT)
   * *******************************************************************
   */
  public static class CategorisedPropertyDescriptor extends PropertyDescriptor
  {

    /**
     * our category
     * 
     */
    private final String _myCategory;

    /**
     * @param propertyName
     * @param beanClass
     * @throws IntrospectionException
     */
    public CategorisedPropertyDescriptor(final String category,
        final String propertyName, final Class<?> beanClass)
        throws IntrospectionException
    {
      super(propertyName, beanClass);

      _myCategory = category;
    }

    /**
     * the category to assign to this property
     * 
     * @return the category name
     */
    final public String getCategory()
    {
      return _myCategory;
    }
  }

  /**
   * internal class providing test support - tests that tests called provide all
   * required setter/getter methods
   */

  public class editableTesterSupport
  {

    /**
     * old version of calling tester
     * 
     * @param toBeTested
     *          the object to be tested
     * @param theCase
     *          the parent test case
     */
    public static final void testParams(final Editable toBeTested,
        final TestCase theCase)
    {
      testTheseParameters(toBeTested);
    }

    /**
     * test helper, to check that all of the object property getters/setters are
     * there
     * 
     * @param toBeTested
     */
    public static void testTheseParameters(final Editable toBeTested)
    {
      // check if we received an object
      if (toBeTested == null)
        return;

      Assert.assertNotNull("Found editable object", toBeTested);

      final Editable.EditorType et = toBeTested.getInfo();

      if (et == null)
      {
        Assert.fail("no editor type returned for");
        return;
      }

      // first see if we return a custom bean descriptor
      final BeanDescriptor desc = et.getBeanDescriptor();

      // did we get one?
      if (desc != null)
      {
        final Class<?> editorClass = desc.getCustomizerClass();
        if (editorClass != null)
        {
          Object newInstance = null;
          try
          {
            newInstance = editorClass.newInstance();
          }
          catch (InstantiationException e)
          {
            e.printStackTrace(); // To change body of catch statement use File
                                  // | Settings | File Templates.
          }
          catch (IllegalAccessException e)
          {
            e.printStackTrace(); // To change body of catch statement use File
                                  // | Settings | File Templates.
          }
          // check it worked
          Assert.assertNotNull("we didn't create the custom editor for:",
              newInstance);
        }

        else
        {

          // there isn't a dedicated editor, try the custom ones.

          // do the edits
          final PropertyDescriptor[] pd = et.getPropertyDescriptors();

          if (pd == null)
          {
            Assert.fail("problem fetching property editors for " + toBeTested);
            return;
          }

          final int len = pd.length;
          if (len == 0)
          {
            System.out.println("zero property editors found for " + toBeTested
                + ", " + toBeTested.getClass());
            return;
          }

          // get the data
          final Object data = et.getData();

          for (int i = 0; i < len; i++)
          {
            // get the methods
            final PropertyDescriptor p = pd[i];

            // find out the type of the editor
            final Method getter = p.getReadMethod();
            final Method setter = p.getWriteMethod();

            // is there a custom editor for this type?

            Object res = null;

            try
            {
              // sort out the getter
              Object[] dummyArgument = null;
              res = getter.invoke(data, dummyArgument);
            }
            catch (InvocationTargetException ie)
            {
              Assert.fail("missing getter for " + toBeTested + " called:" + getter.getName() + " property:"
                  + p.getDisplayName() + " (" + et.getClass() + ") because:" + ie.getCause());
            }
            catch (IllegalAccessException al)
            {
              Assert.fail("getter not visible for " + toBeTested);
            }

            try
            {
              final Object[] params = { res };
              // sort out the setter
              setter.invoke(data, params);
            }
            catch (InvocationTargetException ie)
            {
              // just check that we were using a valid value for the res
              if (res != null)
              {
                Assert.fail("missing setter for "
                    + p.getWriteMethod().getName() + ", "
                    + toBeTested.getClass());
              }
              else
                System.out
                    .println("######## null value returned form getter for "
                        + p.getReadMethod().getName());
            }
            catch (IllegalAccessException al)
            {
              Assert.fail("setter not visible for " + toBeTested);
            }

            // check if we can get a property editor GUI component for this
            SwingPropertyEditor2.checkPropertyEditors();
            final PropertyEditor editor = SwingPropertyEditor2.findEditor(p);
            Assert.assertNotNull("could not find GUI editor component for:" + data
                + " getter:" + p.getReadMethod().getName(), editor);

          }
        } // whether there was a customizer class
      } // whether there was a custom bean descriptor

      // now try out the methods
      final MethodDescriptor[] methods = et.getMethodDescriptors();
      if (methods != null)
      {
        for (int thisM = 0; thisM < methods.length; thisM++)
        {
          final MethodDescriptor method = methods[thisM];
          final Method thisOne = method.getMethod();
          final String theName = thisOne.getName();
          Assert.assertNotNull(theName);
        }
      }
    }
  }


	public static interface DoNoInspectChildren
	{
		
	}
}
