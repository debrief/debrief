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
// $RCSfile: AWTPropertyEditor.java,v $
// @author $Author: ian $
// @version $Revision: 1.5 $
// $Log: AWTPropertyEditor.java,v $
// Revision 1.5  2004/10/16 13:49:42  ian
// Pretend to implement new GUI implementations
//
// Revision 1.4  2004/10/07 14:23:09  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.3  2004/08/26 11:01:53  Ian.Mayo
// Implement core editable property testing
//
// Revision 1.2  2004/05/25 15:29:25  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:26  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-05-08 13:48:12+01  ian_mayo
// Tidy javadoc comments & formatting
//
// Revision 1.3  2003-02-07 09:49:09+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:45+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:33+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-01-24 14:22:32+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.1  2001-10-03 16:01:38+01  administrator
// Add (duff) method for showing no editors found
//
// Revision 1.0  2001-07-17 08:43:46+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-12 12:11:35+01  novatech
// pass the ToolParent to the editors
//
// Revision 1.2  2001-01-05 09:09:04+00  novatech
// Create type of editor called "Constructor", which requires particular processing from the properties panel (renaming "Apply" to "Build").  Also provide button renaming method
//
// Revision 1.1  2001-01-03 13:42:44+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:30  ianmayo
// initial version
//
// Revision 1.7  2000-10-09 13:35:49+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.6  2000-08-09 16:03:14+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.5  2000-02-03 15:08:04+00  ian_mayo
// First issue to Devron (modified files are mostly related to WMF)
//
// Revision 1.4  2000-01-18 15:08:20+00  ian_mayo
// added dummy showMethods method to reflect Swing being more cool GUI
//
// Revision 1.3  1999-11-18 11:09:29+00  ian_mayo
// now with AWT-specific property editors
//
// Revision 1.2  1999-10-13 17:20:48+01  ian_mayo
// show editors for all elements, (including additional ones)
//
// Revision 1.1  1999-10-12 15:36:48+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-08-04 09:45:28+01  administrator
// minor mods, tidying up
//
// Revision 1.2  1999-07-27 12:09:31+01  administrator
// changed way editor updates panel (label is blanked first)
//
// Revision 1.1  1999-07-27 10:50:42+01  administrator
// Initial revision
//
// Revision 1.4  1999-07-27 09:26:13+01  administrator
// switching to bean-based editing
//
// Revision 1.3  1999-07-23 14:03:50+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.2  1999-07-16 10:01:45+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-12 08:09:27+01  administrator
// Initial revision
//

package MWC.GUI.Properties.AWT;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Enumeration;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.PlainPropertyEditor;
import MWC.GUI.Properties.PropertiesPanel;

public class AWTPropertyEditor extends PlainPropertyEditor implements
    KeyListener
{
  protected class paintLabel extends Panel
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected PropertyEditor _myEditor;
    protected Label _myLabel;

    public paintLabel(final PropertyEditor pe)
    {
      _myEditor = pe;
      _myLabel = new Label("  ")
      {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void paint(final Graphics p1)
        {
          final Rectangle area = _myLabel.getBounds();
          // we have to paint in the background first
          p1.setColor(SystemColor.control);
          p1.fillRect(area.x, area.y, area.width, area.height);
          // and now the updated font editor
          p1.setColor(SystemColor.controlText);
          _myEditor.paintValue(p1, area);
        }

      };
      final Button edit = new Button("Edit");
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
    }

    public void doClick()
    {
      // show the panel itself
      final Frame tmp = new Frame();
      final Dialog fr = new AWTEditFrame(tmp, _myEditor);
      fr.setModal(true);
      fr.setVisible(true);
      tmp.dispose();
      _myLabel.repaint();
    }

  }

  /////////////////////////////////////////////////////
  // store the information regarding an editable item
  ///////////////////////////////////////////////////
  protected class showInfo extends FocusAdapter
  {
    protected String _info1;
    protected Label _lbl;

    public showInfo(final Label theLbl, final String info)
    {
      _lbl = theLbl;
      _info1 = info;
    }

    @Override
    public void focusGained(final FocusEvent p1)
    {
      showInfo(_info1);
    }
  }

  protected static void checkPropertyEditors()
  {

    // no, create the core object
    createCorePropertyEditors();

    // now register the specific editors
    PropertyEditorManager.registerEditor(java.util.Date.class,
        AWTDatePropertyEditor.class);
    PropertyEditorManager.registerEditor(MWC.GenericData.WorldLocation.class,
        AWTWorldLocationPropertyEditor.class);
    PropertyEditorManager.registerEditor(boolean.class,
        AWTBooleanPropertyEditor.class);

  }

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /**
   * the panel we put ourselves into
   */
  Panel _main;
  /**
   * the panel to hold the editors
   */
  Panel host;

  /**
   * the assorted buttons we need
   */
  Button _close;

  Button _apply;

  Button _reset;

  /**
   * our parent panel, so that we can trigger an update
   */
  AWTPropertiesPanel _theParent;

  /**
   * the label to show the descriptions in
   */
  Label _info;
  
  /**
   * @param info the object we are going to edit
   * @param parent the panel we are contained in
   * @param theLayers where the data is
   * @param toolParent the parent we can report to
   * @param parentLayer the layer that contains this data
   */
  public AWTPropertyEditor(final MWC.GUI.Editable.EditorType info,
      final AWTPropertiesPanel parent, final Layers theLayers,
      final MWC.GUI.ToolParent theParent, final Layer parentLayer)
  {
    super(info, theLayers, parent, theParent, parentLayer, null);

    _theParent = parent;
  }

  private void addPanel(final String lbl, final Component c,
      final String details, final PropertyDescriptor p)
  {

    // take a copy of the component, so that we can edit
    // it later
    final PropertyEditorItem pei = _theEditors.get(p);
    pei.theEditorGUI = c;

    // prepare our handler for showing the details for this
    // parameter
    final FocusListener fl = new showInfo(_info, details);

    final Panel P = new Panel();
    final Label lb = new Label(lbl);

    P.setLayout(new BorderLayout());

    P.add(lb, BorderLayout.WEST);
    P.add(c, BorderLayout.CENTER);

    host.add(P);

    // add the focuses
    c.addFocusListener(fl);
    lb.addFocusListener(fl);
    P.addFocusListener(fl);

    // add the key listeners
    P.addKeyListener(this);
    lb.addKeyListener(this);
    c.addKeyListener(this);
  }

  void apply()
  {
    super.doUpdate();
    _theParent.doApply();
  }

  void close()
  {
    closing();
    _theParent.removeMe(getPanel());
  }

  @Override
  protected void declarePropertyEditors()
  {
    checkPropertyEditors();
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
    // don't bother firing this in our interface
  }

  protected void gainFocus(final Component c)
  {
    _info.setText(c.getName());
  }

  @Override
  protected MWC.GUI.Undo.UndoBuffer getBuffer()
  {
    return _theParent.getBuffer();
  }

  /**
   * get the AWT panel we have been drawn into
   *
   * @return AWT panel
   */
  public Panel getPanel()
  {
    return _main;
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  /**
   * layout the editors on the page
   */
  @Override
  protected void initForm(final PropertiesPanel thePanel)
  {

    // store the panel
    _theParent = (AWTPropertiesPanel) thePanel;

    // go through the editors, creating them
    host = new Panel();
    host.setLayout(new GridLayout(0, 1));

    _main = new Panel();
    _main.setLayout(new BorderLayout());
    _main.add("North", host);

    _main.addKeyListener(this);

    // try for the custom editor first
    if (super._theCustomEditor != null)
    {
      try
      {
        final AWTCustomEditor p = (AWTCustomEditor) super._theCustomEditor
            .newInstance();
        p.setObject(super._theData, _theParent);
        host.add(p);
        host.doLayout();
      }
      catch (final Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }

    }
    else
    {
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

    }

    final Panel infoPanel = new Panel();
    infoPanel.setLayout(new BorderLayout());
    _main.add("Center", infoPanel);
    _info = new Label("====");
    infoPanel.add("Center", _info);

    // create the button holder
    final Panel footer = new Panel();
    footer.setLayout(new GridLayout(1, 0));
    _main.add("South", footer);

    // create the buttons
    _close = new Button("Close");
    footer.add(_close);
    _close.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent e)
      {
        close();
      }
    });

    _apply = new Button("Apply");

    footer.add(_apply);
    _apply.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent e)
      {
        apply();
      }
    });

    _reset = new Button("Reset");
    footer.add(_reset);
    _reset.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent e)
      {
        reset();
      }
    });

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

  private void makeBox(final PropertyDescriptor p, final PropertyEditor pe)
  {
    final String res = pe.getAsText();
    final TextField tf = new TextField(res);
    final PropertyEditor pt = pe;
    tf.addTextListener(new TextListener()
    {
      @Override
      public void textValueChanged(final TextEvent t)
      {
        final TextField td = (TextField) t.getSource();
        final String val = td.getText();
        pt.setValue(val);
      }
    });

    addPanel(p.getDisplayName(), tf, p.getShortDescription(), p);
  }

  private void makeChoice(final PropertyDescriptor p, final PropertyEditor pe)
  {
    final String[] tags = pe.getTags();
    final PropertyEditor pf = pe;
    final Choice cl = new Choice();
    final int num = tags.length;
    for (int i = 0; i < num; i++)
    {
      cl.addItem(tags[i]);
    }

    // handler for new selection
    cl.addItemListener(new ItemListener()
    {
      @Override
      public void itemStateChanged(final ItemEvent e)
      {
        pf.setAsText((String) e.getItem());
      }
    });

    // and set the initial value
    final String sel = pe.getAsText();
    cl.select(sel);

    addPanel(p.getDisplayName(), cl, p.getShortDescription(), p);
  }

  private void makePanel(final PropertyDescriptor p, final PropertyEditor pe)
  {
    Component cp = null;
    if (pe.isPaintable())
    {
      cp = new paintLabel(pe);
    }
    else
    {
      cp = pe.getCustomEditor();
    }
    addPanel(p.getDisplayName(), cp, p.getShortDescription(), p);
  }

  /////////////////////////////////////////////////////
  // support classes - label containing edit button
  ///////////////////////////////////////////////////

  void reset()
  {
    // get all of the parameters from their parent, again
  }

  @Override
  public void setNames(final String apply, final String close,
      final String reset)
  {
    if (apply != null)
      _apply.setLabel(apply);
    if (close != null)
      _close.setLabel(close);
    if (reset != null)
      _reset.setLabel(reset);
  }

  protected void showInfo(final String val)
  {
    _info.setText(val);
  }

  /**
   */
  @Override
  protected void showMethods()
  {
    // duff implementation, properly
    // implemented in Swing
  }

  private void showThis(final PropertyDescriptor p, final PropertyEditor pe)
  {

    // see if there is a custom editor
    if (pe.supportsCustomEditor())
    {
      makePanel(p, pe);
      return;
    }

    // see if there is a list of values
    final String[] tags = pe.getTags();
    if (tags != null)
    {
      // create a choice item
      makeChoice(p, pe);
      return;
    }

    // and lastly see if there is a text editor
    final String res = pe.getAsText();
    if (res != null)
    {
      makeBox(p, pe);
    }
  }

  /**
   * method to indicate to user that no editors were found
   */
  @Override
  protected void showZeroEditorsFound(final String name)
  {
    // hey, lets do this another day@@@
  }

  @Override
  protected void updateThis(final Component c, final PropertyEditor pe)
  {
    // update the gui
    if (c instanceof TextField)
    {
      final TextField t = (TextField) c;
      t.setText(pe.getAsText());
    }
  }

}
