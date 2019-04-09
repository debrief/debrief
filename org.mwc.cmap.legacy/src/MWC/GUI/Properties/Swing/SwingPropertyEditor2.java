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
// $RCSfile: SwingPropertyEditor2.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.14 $
// $Log: SwingPropertyEditor2.java,v $
// Revision 1.14  2006/11/13 12:21:55  Ian.Mayo
// Eclipse tidying
//
// Revision 1.13  2004/11/25 10:22:59  Ian.Mayo
// Fixing more tests
//
// Revision 1.12  2004/11/01 10:45:40  Ian.Mayo
// Make the WorldPath editor on of the default editors
//
// Revision 1.11  2004/10/16 13:50:16  ian
// GUI support for report window when applicable
//
// Revision 1.10  2004/10/07 14:23:10  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.9  2004/09/29 14:19:48  Ian.Mayo
// Minor refactoring of creating editors, fixed significant bug where ComboBoxes not shown
//
// Revision 1.8  2004/09/03 11:09:51  Ian.Mayo
// Refactor adding individual editors so that one day we can handle optional attributes
//
// Revision 1.7  2004/08/26 16:47:32  Ian.Mayo
// Implement more editable properties, add Acceleration property editor
//
// Revision 1.6  2004/08/26 11:01:54  Ian.Mayo
// Implement core editable property testing
//
// Revision 1.5  2004/08/26 09:46:00  Ian.Mayo
// Getting fixes up to date
//
// Revision 1.3  2004/05/25 15:29:46  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.2  2003/10/17 14:51:39  Ian.Mayo
// Include new editor
//
// Revision 1.1.1.1  2003/07/17 10:07:27  Ian.Mayo
// Initial import
//
// Revision 1.10  2003-06-11 16:01:03+01  ian_mayo
// Tidy javadoc comments
//
// Revision 1.9  2003-05-30 11:14:49+01  ian_mayo
// Switch to multi-line text editor
//
// Revision 1.8  2003-05-08 13:49:07+01  ian_mayo
// Tidy & refactor property editor management
//
// Revision 1.7  2003-02-07 09:49:06+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.6  2002-11-27 11:44:10+00  ian_mayo
// Refactor to tidy up how we create custom editors
//
// Revision 1.5  2002-11-13 13:14:23+00  ian_mayo
// Remove unused methods/values
//
// Revision 1.4  2002-07-09 15:30:06+01  ian_mayo
// improve comments
//
// Revision 1.3  2002-05-28 09:25:46+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:35+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-04-19 08:35:58+01  ian_mayo
// Check we have a chart
//
// Revision 1.1  2002-04-11 14:01:28+01  ian_mayo
// Initial revision
//
// Revision 1.14  2002-02-20 10:39:59+00  administrator
// Switch our main JPanel to a CloseablePanel which ensures that the toolbar gets closed with it, and add more comments to special table object
//
// Revision 1.13  2002-02-19 20:26:13+00  administrator
// - Set GUI component names to assist JFCUnit testing
// - Create custom table class which stores the object we are editing, again to assist JFCUnit editing
//
// Revision 1.12  2002-02-18 20:16:43+00  administrator
// Name the buttons
//
// Revision 1.11  2002-01-24 14:22:33+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.10  2002-01-24 10:52:07+00  administrator
// When editor panel is closing, remove the propertiespanel from being a propertyChangeListener
//
// Revision 1.9  2002-01-17 20:40:57+00  administrator
// Register duration editor
//
// Revision 1.8  2002-01-17 14:49:18+00  administrator
// Reflect fact that distance now in WorldDistance units
//
// Revision 1.7  2001-11-14 19:52:31+00  administrator
// Inform child classes of ToolParent if they want it
//
// Revision 1.6  2001-10-03 16:02:00+01  administrator
// Add method to indicate to user that no editable properties found
//
// Revision 1.5  2001-10-02 10:08:09+01  administrator
// Tidy up error text, & correct bug where method can only be called once (since it gets set to null following execution)
//
// Revision 1.4  2001-10-01 12:48:24+01  administrator
// Create static method to construct combo-box, so that we can use this from elsewhere
//
// Revision 1.3  2001-08-29 19:37:16+01  administrator
// Some tidying up of the button layout, & closing panel
//
// Revision 1.2  2001-08-24 09:56:30+01  administrator
// Handle unexpected bug, where PropEditor gets closed twice
//
// Revision 1.1  2001-08-21 12:10:40+01  administrator
// Tidy up local variables, and Replace anonymous listeners with named class (to remove final objects)
//
// Revision 1.0  2001-07-17 08:43:41+01  administrator
// Initial revision
//
// Revision 1.12  2001-07-12 12:13:02+01  novatech
// Pass the ToolParent to any child classes
//
// Revision 1.11  2001-07-09 14:10:04+01  novatech
// Pass the Close event to any children
//
// Revision 1.10  2001-06-14 15:46:35+01  novatech
// when removing a panel, index it by the object, not the panel
//
// Revision 1.9  2001-06-04 09:33:33+01  novatech
// tidy up management of custom editors, and provide editors with details of property panel if required
//
// Revision 1.8  2001-01-26 11:20:46+00  novatech
// combo-boxes don't want to be editable
//
// Revision 1.7  2001-01-24 12:06:19+00  novatech
// put red highlight around item under cursor
//
// Revision 1.6  2001-01-24 11:38:48+00  novatech
// highlight current value in drop-down combo lists
//
// Revision 1.5  2001-01-21 21:39:14+00  novatech
// minor tidying whilst experimenting with setting StatusBar text on tool pass-over
//
// Revision 1.4  2001-01-17 13:27:12+00  novatech
// tidy up error reporting
//
// Revision 1.3  2001-01-05 09:08:54+00  novatech
// Create type of editor called "Constructor", which requires particular processing from the properties panel (renaming "Apply" to "Build").  Also provide button renaming method
//
// Revision 1.2  2001-01-04 14:04:13+00  novatech
// only pass JComboBox updates for new selection, not old one.  Also handle JComboBox returning a Double value instead of a String
//
// Revision 1.1  2001-01-03 13:42:40+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:44  ianmayo
// initial version
//
// Revision 1.23  2000-10-09 13:35:46+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.22  2000-10-03 14:13:54+01  ian_mayo
// Add definition of SteppingBoundedInteger editor
//
// Revision 1.21  2000-09-26 09:50:02+01  ian_mayo
// tidy up labelling of JDK1.3-specific code
//
// Revision 1.20  2000-09-21 09:06:18+01  ian_mayo
// insert comments indicating that setRowHeight(i, n) is jdk 1.3-specific
//
// Revision 1.19  2000-08-30 16:34:14+01  ian_mayo
// correct class hierarchy
//
// Revision 1.18  2000-08-29 10:56:39+01  ian_mayo
// inform the Editable that we've changed it after calling one of it's methods
//
// Revision 1.17  2000-08-21 16:29:01+01  ian_mayo
// 2 things:
// stop initialising customEditor to null, since the constructor does this after we have set a
// useful value to it
// update the plot after performing one of the class methods
//
// Revision 1.16  2000-08-21 09:49:09+01  ian_mayo
// correct type which was preventing Properties Panel from opening
//
// Revision 1.15  2000-08-18 13:35:32+01  ian_mayo
// enable refresh of table following data change
//
// Revision 1.14  2000-08-18 10:06:53+01  ian_mayo
// <>
//
// Revision 1.13  2000-08-16 14:13:51+01  ian_mayo
// tidy row height implementation
//
// Revision 1.12  2000-08-15 15:22:21+01  ian_mayo
// move ColorEditor definition to Swing initialisation routines
//
// Revision 1.11  2000-08-15 11:44:24+01  ian_mayo
// include support for color editor
//
// Revision 1.10  2000-08-09 16:03:10+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.9  2000-07-07 10:00:26+01  ian_mayo
// tidy up declarations & explicitly set control to enabled when it's clicked
//
// Revision 1.8  2000-06-05 14:18:25+01  ian_mayo
// correctly handle the RESET operation
//
// Revision 1.7  2000-04-19 11:40:53+01  ian_mayo
// tidying up
//
// Revision 1.6  2000-04-05 08:35:52+01  ian_mayo
// remember the custom editor, so that we can send it the Reset event if the button is pressed
//
// Revision 1.5  2000-03-14 14:48:12+00  ian_mayo
// correct placement of custom editors in panel
//
// Revision 1.4  2000-03-14 09:56:13+00  ian_mayo
// remove "Help" pane, and put methods at top of form (let table of properties accomodate changes in panel size)
//
// Revision 1.3  2000-03-08 16:25:43+00  ian_mayo
// register bounded integer editor
//
// Revision 1.2  2000-02-21 16:37:07+00  ian_mayo
// Minimised row heights
//
// Revision 1.1  2000-02-21 13:25:59+00  ian_mayo
// Initial revision
//
// Revision 1.3  1999-11-24 15:38:01+00  ian_mayo
// improved handling of updates to text fields (focus lost)
//
// Revision 1.2  1999-11-23 11:05:01+00  ian_mayo
// further introduction of SWING components
//

package MWC.GUI.Properties.Swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GUI.Properties.PlainPropertyEditor;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Properties.SteppingBoundedInteger;
import MWC.GUI.Swing.MultiLineLabel;
import MWC.GenericData.WorldDistance;

/**
 * Swing implementation of a property editor. note that setRowHeight in initForm is jdk1.3 specific!
 * ===============================
 */
public class SwingPropertyEditor2 extends PlainPropertyEditor implements
    KeyListener
{
  // ///////////////////////////////////////////////////////////
  // member variables
  // //////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////
  // produce the correct component to edit this item
  // /////////////////////////////////////////////////
  protected class dataCellEditor implements TableCellEditor
  {

    @Override
    public void addCellEditorListener(final CellEditorListener p1)
    {
    }

    @Override
    public void cancelCellEditing()
    {
    }

    @Override
    public Object getCellEditorValue()
    {
      return null;
    }

    @Override
    public Component getTableCellEditorComponent(final JTable p1,
        final Object p2, final boolean p3, final int p4, final int p5)
    {
      return _theEditorList.get(p2);
    }

    @Override
    public boolean isCellEditable(final EventObject p1)
    {
      return true;
    }

    @Override
    public void removeCellEditorListener(final CellEditorListener p1)
    {
    }

    @Override
    public boolean shouldSelectCell(final EventObject p1)
    {
      return true;
    }

    @Override
    public boolean stopCellEditing()
    {
      return true;
    }
  }

  // ///////////////////////////////////////////////////
  // produce the correct component to view this item
  // /////////////////////////////////////////////////
  protected class dataCellRenderer implements TableCellRenderer
  {

    @Override
    public Component getTableCellRendererComponent(final JTable p1,
        final Object p2, final boolean isSelected, final boolean hasFocus,
        final int p5, final int p6)
    {
      final Component res = _theEditorList.get(p2);
      res.setEnabled(true);
      return res;
    }
  }

  /**
   * embedded class which will invode the "setter" method (as provided in constructor)
   */
  private class DoActionPerformed implements java.awt.event.ActionListener
  {

    private final MethodDescriptor _myMethod;

    public DoActionPerformed(final MethodDescriptor method)
    {
      _myMethod = method;
    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      try
      {
        _myMethod.getMethod().invoke(_theData, (Object[]) null);

        // inform the object that we've updated it.
        _theInfo.fireChanged(this, _myMethod.getMethod().toString(), null,
            null);

        // assume we also want to update the plot
        if (_theLayers != null)
          _theLayers.fireModified(null);
      }
      catch (final Exception b)
      {
        MWC.Utilities.Errors.Trace.trace(b, "Assigning data value in editor");
      }
    }
  }

  /**
   * embedded helper class to handle updating the text control when the users edits it
   */
  private static class HandleTextChange extends java.awt.event.FocusAdapter
      implements ActionListener
  {
    PropertyEditor _myEditor;

    public HandleTextChange(final PropertyEditor editor)
    {
      _myEditor = editor;
    }

    @Override
    public void actionPerformed(final java.awt.event.ActionEvent t)
    {
      final JTextField td = (JTextField) t.getSource();
      docChanged(_myEditor, td);
    }

    /**
     * private handler to look after changes to the text box
     */
    private void docChanged(final PropertyEditor pt, final JTextComponent text)
    {
      _myEditor.setValue(text.getText());
    }

    @Override
    public void focusLost(final java.awt.event.FocusEvent f)
    {
      final JTextComponent td = (JTextComponent) f.getSource();
      docChanged(_myEditor, td);
    }
  }

  // ///////////////////////////////////////////////////
  // produce a JLabel component, so that we can see labels
  // /////////////////////////////////////////////////
  protected class myLabelRenderer implements TableCellRenderer
  {

    @Override
    public Component getTableCellRendererComponent(final JTable p1,
        final Object p2, final boolean p3, final boolean p4, final int p5,
        final int p6)
    {
      final PropertyDescriptor pd = (PropertyDescriptor) p2;
      final String displayName = pd.getDisplayName();
      final String name = displayName != null ? displayName : pd.getName();
      final JLabel res = new JLabel(name);
      res.setPreferredSize(res.getMinimumSize());
      final String myStr = pd.getShortDescription();
      res.setToolTipText(myStr);
      return res;
    }
  }

  /**
   * special extension of table class which stores the object being edited this was initially
   * created to support JFCUnit editing
   */
  public static class MyTable extends JTable
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * the object being edited by the table
     */
    private Object _subject;

    /**
     * constructor, receives the object being edited (of course)
     */
    public MyTable(final Object object_being_edited)
    {
      super();
      _subject = object_being_edited;
    }

    /**
     * table is being closed, forget about the subject
     */
    protected void closeMe()
    {
      _subject = null;
    }

    /**
     * retrieve the object being edited
     */
    public Object getSubject()
    {
      return _subject;
    }
  }

  // /////////////////////////////////////////////////////////////////
  // embedded implementation of table cell editor
  // /////////////////////////////////////////////////////////////////
  protected class myTableEditor implements javax.swing.table.TableCellEditor
  {
    protected Component _myC;

    public myTableEditor(final Component c)
    {
      _myC = c;
    }

    @Override
    public void addCellEditorListener(final CellEditorListener p1)
    {
      // do nothing
    }

    @Override
    public void cancelCellEditing()
    {
      // do nothing
    }

    @Override
    public Object getCellEditorValue()
    {
      return null;
    }

    @Override
    public Component getTableCellEditorComponent(final JTable p1,
        final Object p2, final boolean p3, final int p4, final int p5)
    {
      return _myC;
    }

    @Override
    public boolean isCellEditable(final EventObject p1)
    {
      return true;
    }

    @Override
    public void removeCellEditorListener(final CellEditorListener p1)
    {
      // do nothing
    }

    @Override
    public boolean shouldSelectCell(final EventObject p1)
    {
      return false;
    }

    @Override
    public boolean stopCellEditing()
    {
      return false;
    }
  }

  protected class paintLabel extends JPanel
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected PropertyEditor _myEditor;

    protected JLabel _myLabel;

    public paintLabel(final PropertyEditor pe)
    {
      _myEditor = pe;
      _myLabel = new JLabel("  ")
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void paint(final Graphics p1)
        {
          final Rectangle area = _myLabel.getBounds();

          // and now the updated font editor
          p1.setColor(SystemColor.controlText);
          _myEditor.paintValue(p1, area);
        }
      };

      final JButton edit = new JButton("Edit");

      setLayout(new BorderLayout());
      edit.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(final ActionEvent e)
        {
          doClick();
        }
      });

      add("Center", _myLabel);
      add("East", edit);

      // and minimize the size
      setPreferredSize(getMinimumSize());
    }

    public void doClick()
    {
      // show the panel itself
      final JFrame tmp = new JFrame();
      final Dialog fr = new SwingEditFrame(tmp, _myEditor);
      fr.setModal(true);
      fr.setVisible(true);
      tmp.dispose();
      _myLabel.repaint();
    }
  }

  /**
   * embedded class to handle setting the value of the indicated editor
   */
  static private class SetThisItem implements java.awt.event.ItemListener
  {
    PropertyEditor _myEditor;

    public SetThisItem(final PropertyEditor editor)
    {
      _myEditor = editor;
    }

    @Override
    public void itemStateChanged(final ItemEvent e)
    {
      // see if we are being told about a new selection
      if (e.getStateChange() == ItemEvent.SELECTED)
      {
        final Object val = e.getItem();
        if (val instanceof String)
          _myEditor.setAsText((String) e.getItem());
        else
          _myEditor.setValue(e.getItem());
      }
    }
  }

  // custom class to remember the current value, and paint the current item in
  // RED
  @SuppressWarnings("rawtypes")
  public static class TickableComboBox extends JComboBox
  {

    protected class TickableComboBoxRenderer extends JLabel implements
        javax.swing.ListCellRenderer
    {
      /**
       *
       */
      private static final long serialVersionUID = 1L;

      /**
       * red border, to show the currently selected item
       */

      private final javax.swing.border.Border redBorder = BorderFactory
          .createLineBorder(Color.red, 1);

      private final javax.swing.border.Border emptyBorder = BorderFactory
          .createEmptyBorder(1, 1, 1, 1);

      @Override
      public Component getListCellRendererComponent(final JList list,
          final Object value, final int index, final boolean isSelected,
          final boolean cellHasFocus)
      {
        // set the text
        setText(value.toString());
        // is this the current value?
        if (isCurrentValue(index))
        {
          setForeground(Color.red);
        }
        else
        {
          setForeground(Color.black);
        }

        // draw a button around the object we are currently editing
        if (isSelected)
        {
          setBorder(redBorder);
        }
        else
        {
          setBorder(emptyBorder);
        }

        return this;
      }
      // //////////////////////////////////////////////////////////
      // end of renderer
      // //////////////////////////////////////////////////////////
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public TickableComboBox()
    {
      this.setRenderer(new TickableComboBoxRenderer());
    }

    /**
     * event handler so that we are informed when the user sets a new value
     */
    @Override
    public void actionPerformed(final java.awt.event.ActionEvent event)
    {
    }

    /**
     * accessor method to determine if this index is the current value
     */
    public boolean isCurrentValue(final int index)
    {
      return (this.getSelectedIndex() == index);
    }

    // //////////////////////////////////////////////////////////
    // custom renderer which can call the parent object to
    // determine the current value of the list, and which does
    // not depend on whether the value is selected
    // //////////////////////////////////////////////////////////

    /**
     * over-ride this method so that we can update our index of the current value
     */
    @Override
    public void setSelectedItem(final Object oj)
    {
      super.setSelectedItem(oj);
    }

    // //////////////////////////////////////////////////////////
    // end of custom component
    // //////////////////////////////////////////////////////////

  }

  /**
   * declare a list of Swing-specific property editors
   */
  public static void checkPropertyEditors()
  {
    // has our property manager been defined?
    if (_myPropertyManager == null)
    {
      // no, create the core object
      createCorePropertyEditors();

      // now register the specific editors
      PropertyEditorManager.registerEditor(MWC.GenericData.HiResDate.class,
          SwingDatePropertyEditor.class);
      PropertyEditorManager.registerEditor(MWC.GenericData.WorldLocation.class,
          SwingWorldLocationPropertyEditor.class);
      PropertyEditorManager.registerEditor(boolean.class,
          SwingBooleanPropertyEditor.class);
      PropertyEditorManager.registerEditor(SteppingBoundedInteger.class,
          SwingSteppingBoundedIntegerEditor.class);
      PropertyEditorManager.registerEditor(BoundedInteger.class,
          SwingBoundedIntegerEditor.class);
      PropertyEditorManager.registerEditor(MWC.GenericData.WorldDistance.class,
          SwingDistanceWithUnitsPropertyEditor.class);
      PropertyEditorManager.registerEditor(MWC.GenericData.Duration.class,
          SwingDurationPropertyEditor.class);
      PropertyEditorManager.registerEditor(MWC.GenericData.WorldSpeed.class,
          SwingWorldSpeedPropertyEditor.class);
      PropertyEditorManager.registerEditor(
          MWC.GenericData.WorldAcceleration.class,
          SwingWorldAccelerationPropertyEditor.class);
      PropertyEditorManager.registerEditor(WorldDistance.ArrayLength.class,
          SwingDistanceWithUnitsPropertyEditor.class);
      // we were adding the Color editor in this method - but instead
      // we've added it in the Swing application initialisation classes
      // so that it is also available to the right-click editing algorithms,
      // since it implements the getTags method
    }
  }

  @SuppressWarnings("unchecked")
  public static TickableComboBox createChoiceEditor(final PropertyEditor pe,
      final String[] tags)
  {
    final TickableComboBox cl = new TickableComboBox();

    final int num = tags.length;

    // and set the initial value
    final String sel = pe.getAsText();

    for (int i = 0; i < num; i++)
    {
      final String st = tags[i];
      cl.addItem(st);

      if (st.equals(sel))
      {
        cl.setSelectedIndex(i);
      }
    }

    return cl;
  }

  /**
   * accessor to let other classes get to our property manager
   *
   * @return the manager containing a list of gui-independent and gui-dependent editors
   */
  public static PropertyEditorManager getPropertyManager()
  {
    checkPropertyEditors();
    return _myPropertyManager;
  }

  // ///////////////////////////////////////////////////////////
  // member functions
  // //////////////////////////////////////////////////////////

  /**
   * the panel we put ourselves into
   */
  private SwingPropertiesPanel.CloseableJPanel _main;

  /**
   * the table we place the properties into
   */
  MyTable _table;

  /**
   * the model which stores the data for the table
   */
  private javax.swing.table.DefaultTableModel dm;

  /**
   * the list of editor components (indexed by the component itself)
   */
  Hashtable<Component, Component> _theEditorList;

  /**
   * our parent panel, so that we can trigger an update
   */
  private SwingPropertiesPanel _theParent;

  /**
   * the panel to contain the method buttons in
   */
  private JPanel _methodsPanel;

  /**
   * the code to keep the table sorted
   */
  TableSortDecorator _tableSorter;

  /**
   * the custom editor currently in use, if there is one
   */
  private SwingCustomEditor _theSwingCustomEditor;

  /**
   * the apply, close and reset buttons
   */
  private JButton _apply;

  private JButton _reset;

  private JButton _close;

  /**
   * the panel in which we show reports from the item being edited
   */
  private MultiLineLabel _reportWindow;

  // ///////////////////////////////////////////////////////////
  // constructor
  // //////////////////////////////////////////////////////////
  /**
   * the constructor for our Swing property editor
   *
   * @param info
   *          the object we are going to edit
   * @param parent
   *          the panel we are contained in
   * @param propsPanel
   *          JTabbedPane
   * @param theChart
   *          the chart we will send updates to
   */
  public SwingPropertyEditor2(final MWC.GUI.Editable.EditorType info,
      final SwingPropertiesPanel parent, final Layers theLayers,
      final MWC.GUI.ToolParent toolParent, final Layer parentLayer,
      final SwingPropertiesPanel propsPanel)
  {
    super(info, theLayers, parent, toolParent, parentLayer, propsPanel);

    // store the parent
    _theParent = parent;

  }

  protected void addButton(final JButton btn)
  {
    _methodsPanel.add(btn);
  }

  /**
   * stick the panel into the interface
   */
  private void addPanel(final Component c, final PropertyDescriptor p)
  {

    // take a copy of the component, so that we can edit
    // it later
    final PropertyEditorItem pei = _theEditors.get(p);
    pei.theEditorGUI = c;

    final Vector<Object> v = new Vector<Object>();
    v.addElement(p);
    v.addElement(c);

    if (_theEditorList == null)
      _theEditorList = new Hashtable<Component, Component>();

    _theEditorList.put(c, c);

    dm.addRow(v);

    // and do a quick sort
    _tableSorter.sort(0);

    // add the key listeners
    c.addKeyListener(this);

  }

  void apply()
  {
    super.doUpdate();
    _theParent.doApply();
  }

  /**
   * close the editor, clearing all external links
   */
  public void close()
  {
    // process the Close event
    if (_theSwingCustomEditor != null)
    {
      // so, we have a custom editor, tell it the Close button has been pressed
      _theSwingCustomEditor.doClose();
    }

    closing();

    // do we have a parent?
    if (_theParent != null)
    {

      // remove the panel using the object as the index (we used to use the
      // panel but
      // we can't be sure if the panel has been inserted into a toolbar or not)
      _theParent.remove(_theData);

      // stop the parent from listening to the data item (the parent listens to
      // it in the
      // addEditor method of SwingPropertiesPanel)
      _theInfo.removePropertyChangeListener(_theParent);
    }

    // tell our special panel that it can now close
    _main.doClose();

    // ok, now ditch all of our local data
    _main.removeKeyListener(this);
    _main.removeAll();
    _main = null;
    dm = null;

    if (_theEditorList != null)
    {
      _theEditorList.clear();
    }

    _theParent = null;
    _methodsPanel = null;
    _tableSorter = null;
    _theSwingCustomEditor = null;
    _apply = null;
    _reset = null;
    _close = null;

    // check the table
    if (_table != null)
    {
      _table.closeMe();
      _table = null;
    }

    // and in the parent
    _myPropertyManager = null;
    _theData = null;
    _theProperties = null;
    _theName = null;
    _theEditors.clear();
    _theModifications.removeAllElements();
    _theCustomEditor = null;
    _thePanel = null;
    _otherEditors = null;
    _theMethods = null;
    _theInfo = null;
    _toolParent = null;
  }

  /**
   * convenience function to find a dedicated editor and put it into it's own panel
   *
   * @param op
   *          what we're editing
   * @param thePanel
   *          the panel to put ourselves into
   * @param ourCustomEditorInstance
   *          any custom editor specified by the data object
   * @return a panel containing our editor
   */
  private Object createCustomEditor(final Object op,
      final PropertiesPanel thePanel, final Object ourCustomEditorInstance)
  {
    SwingCustomEditor p = null;
    JPanel jp = null;
    Object ourCustomEditor = ourCustomEditorInstance;

    if (op instanceof SwingCustomEditor)
    {
      p = (SwingCustomEditor) op;
      _theSwingCustomEditor = p;
      _main.add("Center", p);

      p.setObject(super._theData, _toolParent, _theLayers, thePanel);

      ourCustomEditor = op;
    }
    else
    {
      if (op instanceof MWC.GUI.Properties.AWT.AWTCustomEditor)
      {
        System.err.println("found AWT Editor");
        final MWC.GUI.Properties.AWT.AWTCustomEditor ap =
            (MWC.GUI.Properties.AWT.AWTCustomEditor) op;
        jp = new JPanel();
        jp.setLayout(new BorderLayout());
        jp.add("Center", ap);
        _main.add("North", jp);
        ap.setObject(super._theData, _theParent);

        ourCustomEditor = op;
      }
    }

    // see if this custom editor wants to know about the property panel
    if (ourCustomEditor instanceof PlainPropertyEditor.EditorUsesPropertyPanel)
    {
      final PlainPropertyEditor.EditorUsesPropertyPanel eu =
          (PlainPropertyEditor.EditorUsesPropertyPanel) ourCustomEditor;
      eu.setPanel(super._thePanel);
    }

    // see if this custom editor wants to know about the property panel
    if (ourCustomEditor instanceof PlainPropertyEditor.EditorUsesLayers)
    {
      final PlainPropertyEditor.EditorUsesLayers eu =
          (PlainPropertyEditor.EditorUsesLayers) ourCustomEditor;
      eu.setLayers(super._theLayers);
    }

    // see if this custom editor wants to know about the tool parnet
    if (ourCustomEditor instanceof PlainPropertyEditor.EditorUsesToolParent)
    {
      final PlainPropertyEditor.EditorUsesToolParent eu =
          (PlainPropertyEditor.EditorUsesToolParent) ourCustomEditor;
      eu.setParent(super._toolParent);
    }
    return ourCustomEditor;
  }

  /**
   * declare a list of Swing-specific property editors
   */
  @Override
  protected void declarePropertyEditors()
  {
    // check we have created them
    checkPropertyEditors();
  }

  @Override
  public void doRefresh()
  {
    super.doRefresh();
    if (_table != null)
    {
      _table.revalidate();
      _table.repaint();
    }

  }

  /**
   * the object we are listening to has fired a new report. Display it in our GUI if we want to
   *
   * @param report
   *          the text to show
   */
  @Override
  protected void fireNewReport(final String report)
  {
    _reportWindow.setText(report);
  }

  @Override
  protected MWC.GUI.Undo.UndoBuffer getBuffer()
  {
    return _theParent.getBuffer();
  }

  /**
   * get the Swing panel we have been drawn into
   *
   * @return Swing panel
   */
  public Component getPanel()
  {
    return _main;
  }

  /**
   * layout the editors on the page
   *
   * @param thePanel
   *          our parent properties panel
   */
  @Override
  protected void initForm(final PropertiesPanel thePanel)
  {

    // go through the editors, creating them
    _main = new SwingPropertiesPanel.CloseableJPanel()
    {
      /**
       *
       */
      private static final long serialVersionUID = 1L;

      // somebody is telling us to close - to be added by the implementing class
      @Override
      public void triggerClose()
      {
        close();
      }
    };

    _main.setLayout(new BorderLayout());
    _main.addKeyListener(this);

    // create the buttons early, so we can re-format them if we want to
    _close = new JButton("Close");
    _close.setName("Close");
    _reset = new JButton("Reset");
    _reset.setName("Reset");
    _apply = new JButton("Apply");
    _apply.setName("Apply");

    Object ourCustomEditorInstance = null;

    // try for the custom editor first
    if (super._theCustomEditor != null)
    {
      try
      {
        final Object op = super._theCustomEditor.newInstance();

        // let our support class produce the item
        ourCustomEditorInstance = createCustomEditor(op, thePanel,
            ourCustomEditorInstance);
      }
      catch (final Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }

    } // we have a custom editor
    else
    { // a custom editor was not provided, let's resort to introspection

      _table = new MyTable(super._theData);
      _table.setName("Editor");
      dm = new javax.swing.table.DefaultTableModel();
      // create a sorted list around the model
      _tableSorter = new TableSortDecorator(dm);
      _table.setModel(_tableSorter);

      // insert a handler to allow user to select which column to sort by
      final JTableHeader hdr = _table.getTableHeader();
      hdr.addMouseListener(new MouseAdapter()
      {
        @Override
        public void mouseClicked(final MouseEvent e)
        {
          final TableColumnModel tcm = _table.getColumnModel();
          final int vc = tcm.getColumnIndexAtX(e.getX());
          final int mc = _table.convertColumnIndexToModel(vc);
          _tableSorter.sort(mc);
        }
      });

      // now put in the columns
      dm.addColumn("Name");
      dm.addColumn("Data");

      // put the table in a scroll pane, so it can scroll
      // if necessary
      final JScrollPane jsp = new JScrollPane(_table);
      _main.add("Center", jsp);

      final javax.swing.table.TableColumn lbls = _table.getColumn("Name");
      lbls.setCellRenderer(new myLabelRenderer());

      final javax.swing.table.TableColumn data = _table.getColumn("Data");
      data.setCellRenderer(new dataCellRenderer());
      data.setCellEditor(new dataCellEditor());

      // now do the column width for the name column
      final int nameWid = lbls.getMinWidth();
      lbls.setPreferredWidth(nameWid);
      _table.sizeColumnsToFit(-1);

      // show all of the editor entities
      final Enumeration<PropertyEditorItem> enumer = _theEditors.elements();
      while (enumer.hasMoreElements())
      {
        final PropertyEditorItem pei = enumer.nextElement();
        final PropertyDescriptor p = pei.theDescriptor;
        final PropertyEditor pe = pei.theEditor;
        if (pe != null)
        {
          showThis(p, pe);
        }
      }

      // we have created our editors, now set the best height
      int maxHt = 0;
      for (int i = 0; i < _table.getRowCount(); i++)
      {
        final Component c = data.getCellRenderer()
            .getTableCellRendererComponent(_table, _table.getValueAt(i, 1),
                false, false, i, 1);
        final int h = c.getMinimumSize().height;

        /**
         * note that setRowHeight in initForm is jdk1.3 specific!
         */
        _table.setRowHeight(i, h);
        maxHt = Math.max(maxHt, h);
      }

      // JDK1.2 - set height for all rows
      // _table.setRowHeight(maxHt);

      // provide the panel for the method buttons to go in
      _methodsPanel = new JPanel();
      _main.add("North", _methodsPanel);

    }

    // create the button holder
    final JPanel buttonHolder = new JPanel();
    buttonHolder.setLayout(new GridLayout(1, 0));
    final JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BorderLayout());
    bottomPanel.add("South", buttonHolder);
    _main.add("South", bottomPanel);

    // see if we need to display a reporting window
    if (super._theInfo.firesReports())
    {
      // ok, add the reporting window
      _reportWindow = new MultiLineLabel();
      _reportWindow.setRows(2);
      _reportWindow.setBorder(BorderFactory.createLoweredBevelBorder());
      bottomPanel.add("Center", _reportWindow);
    }

    // format the buttons
    buttonHolder.add(_close);
    _close.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent e)
      {
        close();
      }
    });

    /**
     * check that this isn't one of our special editors which doesn't apply/reset buttons at it's
     * foot
     */
    if (ourCustomEditorInstance != null)
    {
      if (ourCustomEditorInstance instanceof MWC.GUI.Properties.NoEditorButtons)
      {
        // let's just drop out now
        return;
      }
    }

    if (!_theEditors.isEmpty())
    {
      buttonHolder.add(_apply);
      _apply.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(final ActionEvent e)
        {
          apply();
        }
      });

      buttonHolder.add(_reset);
      _reset.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(final ActionEvent e)
        {
          reset();
        }
      });
    }

  }

  @Override
  public void keyPressed(final KeyEvent p1)
  {
  }

  @Override
  public void keyReleased(final KeyEvent p1)
  {
  }

  @Override
  public void keyTyped(final KeyEvent p1)
  {
    final int mods = p1.getModifiers();
    if ((mods & InputEvent.ALT_MASK) != 0)
    {
      // so an alt-key has been pressed
      final char k = p1.getKeyChar();
      if (k == 'a')
      {
        apply();
      }
    }
  }

  /**
   * make a text box editor to edit this parameter
   */
  private Component makeBox(final PropertyDescriptor p, final PropertyEditor pe)
  {
    final String res = pe.getAsText();

    final JTextArea tf = new JTextArea(res);
    tf.setRows(3);

    final JScrollPane scroller = new JScrollPane(tf);
    scroller.setMinimumSize(new Dimension(30, 36));

    /**
     * add an action listener, to make the update when the user presses return inside the control
     */
    // tf.addActionListener(new HandleTextChange(pe));
    /**
     * add a focus listener, to update the text field as the user leaves the box
     */
    tf.addFocusListener(new HandleTextChange(pe));

    // and put it in the panel
    return scroller;
  }

  /**
   * create a drop down list to edit this item
   */
  private Component makeChoice(final PropertyEditor pe, final String[] tags)
  {
    final TickableComboBox cl = createChoiceEditor(pe, tags);

    // handler for new selection
    cl.addItemListener(new SetThisItem(pe));
    cl.setPreferredSize(cl.getMinimumSize());

    return cl;
  }

  // ///////////////////////////////////////////////////
  // support classes - label containing edit button
  // /////////////////////////////////////////////////

  /**
   * make a panel to edit this item inside
   */
  private Component makePanel(final PropertyDescriptor p,
      final PropertyEditor pe)
  {
    Component cp = null;
    if (pe.isPaintable())
    {
      cp = new paintLabel(pe);

    }
    else
    {
      cp = pe.getCustomEditor();

      // set the name
      cp.setName(p.getDisplayName());

      // see if this custom editor wants to know about the property panel
      if (pe instanceof PlainPropertyEditor.EditorUsesPropertyPanel)
      {
        final PlainPropertyEditor.EditorUsesPropertyPanel eu =
            (PlainPropertyEditor.EditorUsesPropertyPanel) pe;
        eu.setPanel(super._thePanel);
      }
      // see if this custom editor wants to know about the property panel
      if (pe instanceof PlainPropertyEditor.EditorUsesLayers)
      {
        final PlainPropertyEditor.EditorUsesLayers eu =
            (PlainPropertyEditor.EditorUsesLayers) pe;
        eu.setLayers(super._theLayers);
      }
      // see if this custom editor wants to know about the tool parnet
      if (pe instanceof PlainPropertyEditor.EditorUsesToolParent)
      {
        final PlainPropertyEditor.EditorUsesToolParent eu =
            (PlainPropertyEditor.EditorUsesToolParent) pe;
        eu.setParent(super._toolParent);
      }

    }

    return cp;
  }

  void reset()
  {
    // process the RESET event
    if (_theSwingCustomEditor != null)
    {
      // so, we have a custom editor, tell it the RESET button has been pressed
      _theSwingCustomEditor.doReset();
    }
    else
    {
      // do a reset for each parameter
      doReset();
    }
  }

  @Override
  public void setNames(final String apply, final String close,
      final String reset)
  {
    if (apply != null)
      _apply.setText(apply);
    if (reset != null)
      _reset.setText(reset);
    if (close != null)
      _close.setText(close);
  }

  @Override
  protected void showMethods()
  {
    // just check that we've got a methods panel
    if (_theMethods != null)
    {
      for (int i = 0; i < _theMethods.length; i++)
      {
        final MethodDescriptor md = _theMethods[i];
        if (_methodsPanel != null)
        {

          final JButton btn = new JButton(md.getDisplayName());
          btn.addActionListener(new DoActionPerformed(md));
          addButton(btn);
        }
        else
        {
          MWC.Utilities.Errors.Trace.trace(
              "Trying to show methods when we don't have a 'methods' panel to host:"
                  + md.getDisplayName(), false);
        }
      }
    }

  }

  /**
   * sort out what kind of GUI component to edit this, then stick it into our panel
   */
  private void showThis(final PropertyDescriptor p, final PropertyEditor pe)
  {
    Component theComp = null;

    // see if there is a custom editor
    if (pe.supportsCustomEditor())
    {
      theComp = makePanel(p, pe);
    }
    else
    {

      // see if there is a list of values
      final String[] tags = pe.getTags();
      if (tags != null)
      {
        // create a choice item
        theComp = makeChoice(pe, tags);
      }
      else
      {

        // note, we used to check that there was a string present before
        // creating this text
        // editor - but we want to slowly switch to allowing the user to supply
        // null values
        // so, temporarily still
        theComp = makeBox(p, pe);
      }
    }

    // did we find one?
    if (theComp != null)
    {
      // todo: if this attribute is optional, wrap it in a holder containing an
      // "ignore" checkbox
      addPanel(theComp, p);
    }
    else
    {
      MWC.Utilities.Errors.Trace.trace("Failed to find editor for:" + p
          .getShortDescription());
    }

  }

  /**
   * method to indicate to user that no editors were found
   */
  @Override
  protected void showZeroEditorsFound()
  {
    _main.add("Center", new JLabel("This object has no editable properties"));
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected void updateThis(final Component c, final PropertyEditor pe)
  {
    // update the gui
    if (c instanceof JTextField)
    {
      final JTextField t = (JTextField) c;
      t.setText(pe.getAsText());
      t.invalidate();
    }
    if (c instanceof JCheckBox)
    {
      final JCheckBox t = (JCheckBox) c;
      final Boolean val = (Boolean) pe.getValue();
      t.setSelected(val.booleanValue());
    }
    if (c instanceof JComboBox)
    {
      final JComboBox t = (JComboBox) c;
      // optimistically try for the text value first
      Object current = pe.getAsText();

      if (current == null)
      {
        // oh well, use the raw value instead
        current = pe.getValue();
      }

      // update the current value
      t.setSelectedItem(current);
    }
  }
}
