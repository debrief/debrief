/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.outline;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.mwc.debrief.lite.menu.OutlineViewSelection;
import org.mwc.debrief.lite.outline.LogicHelpers.*;
import org.mwc.debrief.lite.properties.PropertiesDialog;

import Debrief.GUI.CoreImageHelper;
import Debrief.GUI.DebriefImageHelper;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanEnumerate;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.PlottableSelection;
import MWC.GUI.Plottables;
import MWC.GUI.Renamable;
import MWC.GUI.ToolParent;
import MWC.GUI.LayerManager.Swing.SwingLayerManager;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI.ToolbarOwner;
import MWC.GUI.Undo.UndoBuffer;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class OutlinePanelView extends SwingLayerManager implements
    ClipboardOwner, LogicHelpers.Helper
{

  private static final String DUPLICATE_PREFIX = "Copy of ";
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private UndoBuffer _undoBuffer;
  private Clipboard _clipboard;
  private ArrayList<ButtonEnabler> _enablers = new ArrayList<ButtonEnabler>();

  private static class ButtonEnabler
  {
    private final JButton _button;
    private final EnabledTest _test;
    private String _title;

    private ButtonEnabler(final JButton button, final EnabledTest test)
    {
      _title = button.getToolTipText();
      _button = button;
      _test = test;
    }
    
    @Override
    public String toString()
    {
      return _title;
    }
    
    private void refresh(final Helper helper)
    {
      _button.setEnabled(_test.isEnabled(helper));
    }
  }
  
  public OutlinePanelView(UndoBuffer undoBuffer, Clipboard clipboard)
  {
    _undoBuffer = undoBuffer;
    _clipboard = clipboard;
  }

  public static void main(String[] args)
  {
    final EnabledTest hasData = new EnabledTest("Has data")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        return !helper.getSelection().isEmpty();
      }
    };

    final EnabledTest isEmpty = new EnabledTest("Is empty")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        return helper.getSelection().isEmpty();
      }
    };
    final EnabledTest notEmpty = new EnabledTest("Not empty")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        return !helper.getSelection().isEmpty();
      }
    };
    final EnabledTest onlyOne = new EnabledTest("Only one")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        return helper.getSelection().size() == 1;
      }
    };
    Helper helper1 = new Helper() {

      @Override
      public ArrayList<Plottable> getSelection()
      {
        ArrayList<Plottable> res = new ArrayList<Plottable>();
        res.add(new BaseLayer());
        return res;
      }

      @Override
      public ArrayList<Plottable> getClipboardContents()
      {
        return new ArrayList<Plottable>();
      }};

      Helper helper2 = new Helper() {

        @Override
        public ArrayList<Plottable> getSelection()
        {
          ArrayList<Plottable> res = new ArrayList<Plottable>();
          res.add(new BaseLayer());
          res.add(new BaseLayer());
          return res;
        }

        @Override
        public ArrayList<Plottable> getClipboardContents()
        {
          return new ArrayList<Plottable>();
        }};

    System.out.println(new And(hasData, isEmpty).isEnabled(helper1));
    System.out.println(new Or(hasData, isEmpty).isEnabled(helper1));
    System.out.println(new And(notEmpty, onlyOne).isEnabled(helper1));
    System.out.println(new And(notEmpty, onlyOne).isEnabled(helper2));
  }

  @Override
  protected void initForm()
  {
    super.initForm(true);
    JPanel commandBar = new JPanel();
    commandBar.setBackground(Color.LIGHT_GRAY);
    commandBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
    
    // sort out the logical tests
    final EnabledTest notEmpty = getNotEmptyTest();
    final EnabledTest onlyOne = getOnlyOneTest();
    final EnabledTest clipboardNotEmpty = getClipboardNotEmptyTest();
    final EnabledTest selectionIsTrack = getIsTrackTest();
    final EnabledTest selectionIsLayer = getIsLayerTest();
    final EnabledTest clipboardIsFixes = getIsFixesTest();
    final EnabledTest clipboardIsShapes = getIsShapesTest();
    final EnabledTest isEmpty = getEmptyTest();
    final EnabledTest notNarrative = getNotNarrativeTest();
        
    final JButton editButton = createCommandButton("Edit",
        "images/16/edit.png");
    _enablers.add(new ButtonEnabler(editButton, new LogicHelpers.And(notEmpty, onlyOne)));
    commandBar.add(editButton);
    
    final JButton copyButton = createCommandButton("Copy",
        "images/16/copy_to_clipboard.png");
    _enablers.add(new ButtonEnabler(copyButton, new LogicHelpers.And(notEmpty, notNarrative)));
    commandBar.add(copyButton);

    final JButton pasteButton = createCommandButton("Paste",
        "images/16/paste.png");
    _enablers.add(new ButtonEnabler(pasteButton, new LogicHelpers.And(
        clipboardNotEmpty, new LogicHelpers.Or(new LogicHelpers.And(
            selectionIsTrack, clipboardIsFixes), new LogicHelpers.And(
                selectionIsLayer, clipboardIsShapes)))));
    commandBar.add(pasteButton);    

    final JButton addLayerButton = createCommandButton("Add Layer",
        "images/16/add_layer.png");
    _enablers.add(new ButtonEnabler(addLayerButton, isEmpty));
    commandBar.add(addLayerButton);

    final JButton deleteButton = createCommandButton("Delete",
        "images/16/remove.png");
    deleteButton.setToolTipText("Delete");
    _enablers.add(new ButtonEnabler(deleteButton, notEmpty));
    commandBar.add(deleteButton);

    final JButton refreshViewButton = createCommandButton("Update View",
        "images/16/repaint.png");
    refreshViewButton.setToolTipText("Update View");
    refreshViewButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        doReset();

      }
    });
    commandBar.add(refreshViewButton);
    final Helper me = this;
    _myTree.addTreeSelectionListener(new TreeSelectionListener()
    {
      @Override
      public void valueChanged(TreeSelectionEvent e)
      {
        final ArrayList<Plottable> sel = me.getSelection();
        final ArrayList<Plottable> clip = me.getClipboardContents();
        Helper helper = new Helper()
        {
          @Override
          public ArrayList<Plottable> getSelection()
          {
            return sel;
          }

          @Override
          public ArrayList<Plottable> getClipboardContents()
          {
            return clip;
          }
        };
        
        for(final ButtonEnabler t: _enablers)
        {
          t.refresh(helper);
        }
      }
    });
    pasteButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        doPaste();

      }
    });
    editButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        int selectionCount = _myTree.getSelectionCount();
        if (selectionCount == 1)
        {
          TreePath selectionPath = _myTree.getSelectionPath();
          if (selectionPath != null)
          {
            Object node = selectionPath.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode)
            {
              editThis((DefaultMutableTreeNode) node);
            }
          }
        }

      }
    });
    addLayerButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        addLayer();

      }
    });
    copyButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        _clipboard.getContents(this);
        int selectionCount = _myTree.getSelectionCount();
        if (selectionCount > 0)
        {
          TreePath selectionPath[] = _myTree.getSelectionPaths();
          doCopy(selectionPath);
        }
      }
    });

    add(commandBar, BorderLayout.NORTH);
    setCellRenderer(new OutlineRenderer());
    setCellEditor(new OutlineCellEditor());
  }

  private EnabledTest getNotNarrativeTest()
  {
    return new EnabledTest("Selection not narrative")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        ArrayList<Plottable> sel = helper.getSelection();
        for (Plottable t : sel)
        {
          if (t instanceof NarrativeEntry || t instanceof NarrativeWrapper)
          {
            return false;
          }
        }
        return true;
      }
    };
  }

  public EnabledTest getNotEmptyTest()
  {
    return new EnabledTest("Not empty")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        return !helper.getSelection().isEmpty();
      }
    };
  }

  public EnabledTest getOnlyOneTest()
  {
    return new EnabledTest("Only one")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        return helper.getSelection().size() == 1;
      }
    };
  }

  public EnabledTest getClipboardNotEmptyTest()
  {
    return new EnabledTest("Clipboard not empty")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        return !helper.getClipboardContents().isEmpty();
      }
    };
  }

  public EnabledTest getIsTrackTest()
  {
    return new EnabledTest("Selection is track")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        ArrayList<Plottable> sel = helper.getSelection();
        if(sel.size() == 1)
        {
          Plottable first = sel.get(0);
          if(first instanceof TrackWrapper)
          {
            return true;
          }
        }
        return false;
      }
    };
  }

  public EnabledTest getIsLayerTest()
  {
    return new EnabledTest("Selection is layer")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        ArrayList<Plottable> sel = helper.getSelection();
        if(sel.size() == 1)
        {
          Plottable first = sel.get(0);
          if(first instanceof BaseLayer)
          {
            return true;
          }
        }
        return false;
      }
    };
  }

  public EnabledTest getIsFixesTest()
  {
    return new EnabledTest("Clipboard is fixes")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        ArrayList<Plottable> sel = helper.getClipboardContents();
        for (Plottable t : sel)
        {
          if (!(t instanceof FixWrapper))
          {
            return false;
          }
        }
        return true;
      }
    };
  }

  public EnabledTest getIsShapesTest()
  {
    return new EnabledTest("Clipboard is shapes or labels")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        ArrayList<Plottable> sel = helper.getClipboardContents();
        for (Plottable t : sel)
        {
          if (!(t instanceof ShapeWrapper) && !(t instanceof LabelWrapper))
          {
            return false;
          }
        }
        return true;
      }
    };
  }

  public EnabledTest getEmptyTest()
  {
    return new EnabledTest("Is empty")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        return helper.getSelection().isEmpty();
      }
    };
  }

  private JButton createCommandButton(String command, String image)
  {
    URL imageIcon = getClass().getClassLoader().getResource(image);
    final JButton button = new JButton(new ImageIcon(imageIcon));
    button.setToolTipText(command);
    return button;
  }

  protected void doCopy(TreePath[] selectionPaths)
  {
    Plottable[] plottables = new Plottable[selectionPaths.length];
    int i = 0;
    for (TreePath path : selectionPaths)
    {
      plottables[i++] = (Plottable) ((DefaultMutableTreeNode) path
          .getLastPathComponent()).getUserObject();
    }
    OutlineViewSelection selection = new OutlineViewSelection(plottables, true);
    _clipboard.setContents(selection, this);

  }

  private boolean isEnableCopy()
  {
    final TreePath[] selectionPaths = _myTree.getSelectionPaths();
    boolean retVal = true;
    if (selectionPaths != null)
    {
      @SuppressWarnings("rawtypes")
      Class theClass = null;
      if (selectionPaths.length == 1)
      {
        DefaultMutableTreeNode treenode =
            (DefaultMutableTreeNode) selectionPaths[0].getLastPathComponent();
        if (!(treenode.getUserObject() instanceof Plottable))
        {
          retVal = false;
        }
        if (treenode.getUserObject() instanceof NarrativeWrapper || treenode
            .getUserObject() instanceof NarrativeEntry)
        {
          retVal = false;
        }
      }
      else
      {
        for (TreePath path : selectionPaths)
        {
          DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) path
              .getLastPathComponent();
          if (treenode.getUserObject() instanceof Plottable && !((treenode
              .getUserObject() instanceof NarrativeWrapper) || (treenode
                  .getUserObject() instanceof NarrativeEntry)))
          {
            if (theClass == null)
            {
              theClass = treenode.getUserObject().getClass();
            }
            if (theClass != null && theClass != treenode.getUserObject()
                .getClass())
            {
              retVal = false;
            }
          }
        }
      }
    }
    else
    {
      retVal = false;
    }
    return retVal;
  }

  protected void doDelete()
  {
    int pathCount = _myTree.getSelectionPath().getPathCount();
    DefaultMutableTreeNode selectedNode = getSelectedNode();
    Editable editable = (Editable) selectedNode.getUserObject();
    if (pathCount > 1)
    {
      DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) _myTree
          .getSelectionPath().getPathComponent(pathCount - 1);
      Editable obj = (Editable) parentNode.getUserObject();
      // TODO implement delete.

    }

  }

  protected void doCut()
  {
    // TODO implement this
  }

  private DefaultMutableTreeNode getSelectedNode()
  {
    return (DefaultMutableTreeNode) _myTree.getSelectionPath()
        .getLastPathComponent();
  }

  protected void doPaste()
  {
    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) _myTree
        .getSelectionPath().getLastPathComponent();
    final Editable editable = (Editable) node.getUserObject();
    final CanEnumerate destination;
    if (editable instanceof BaseLayer)
    {
      destination = (BaseLayer) editable;
    }
    else if (editable instanceof TrackWrapper)
    {
      destination = (TrackWrapper) editable;
    }
    else
    {
      destination = null;
    }

    final Transferable tr = _clipboard.getContents(this);
    final OutlineViewSelection os = (OutlineViewSelection) tr;
    final boolean _isCopy = os.isACopy();
    // see if there is currently a plottable on the clipboard
    if (tr != null && tr.isDataFlavorSupported(
        PlottableSelection.PlottableFlavor))
    {
      // we're off!

      try
      {

        // extract the plottable
        final Object objectToPaste = tr.getTransferData(
            PlottableSelection.PlottableFlavor);
        final Plottable[] plottables;

        if (objectToPaste instanceof Plottable[])
        {
          plottables = (Plottable[]) objectToPaste;
        }
        else
        {
          plottables = new Plottables[1];
          plottables[0] = (Plottable) objectToPaste;
        }
        // see if it is a layer or not
        for (Plottable theData : plottables)
        {
          // do the checks that are opposite
          if (theData instanceof Layer)
          {
            pasteLayer(destination, (Layer) theData);
          }
          else
          {
            renameIfNecessary(editable, destination);
            if (destination instanceof Layer)
            {
              ((Layer) destination).add(theData);
            }
            else
            {
              ((TrackWrapper) destination).add(theData);
            }
          }
        }
        if (!_isCopy)
        {
          // clear the clipboard
          _clipboard.setContents(null, null);
        }
        _myData.fireModified(null);
      }
      catch (Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }
    }

  }

  /**
   * see if this layer contains an item with the specified name
   * 
   * @param name
   *          name we're checking against.
   * @param destination
   *          layer we're looking at
   * 
   * @return
   */
  private static boolean containsThis(final String name,
      final CanEnumerate destination)
  {
    final Enumeration<Editable> enumeration = destination.elements();
    while (enumeration.hasMoreElements())
    {
      final Editable next = enumeration.nextElement();
      if (next.getName() != null && next.getName().equals(name))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * helper method, to find if an item with this name already exists. If it does, we'll prepend the
   * duplicate phrase
   * 
   * @param editable
   *          the item we're going to add
   * @param enumeration
   *          the destination for the add operation
   */
  private static void renameIfNecessary(final Editable editable,
      final CanEnumerate destination)
  {
    if (editable instanceof Renamable)
    {
      String hisName = editable.getName();
      while (containsThis(hisName, destination))
      {
        hisName = DUPLICATE_PREFIX + hisName;
      }

      // did it change?
      if (!hisName.equals(editable.getName()))
      {
        ((Renamable) editable).setName(hisName);
      }
    }
  }

  public void pasteLayer(final CanEnumerate destination, final Layer theData)
  {
    if (destination instanceof BaseLayer)
    {
      ((Layer) destination).add(theData);
    }
    else
    {
      if (_myData.findLayer(theData.getName()) == null)
      {
        // just add it
        if (theData instanceof FixWrapper)
          _myData.addThisLayerDoNotResize((Layer) theData);
      }
      else
      {
        // adjust the name
        final Layer newLayer = (Layer) theData;

        final String theName = newLayer.getName();

        // does the layer end in a digit?
        final char id = theName.charAt(theName.length() - 1);
        final String idStr = new String("" + id);
        int val = 1;

        String newName = null;
        try
        {
          val = Integer.parseInt(idStr);
          newName = theName.substring(0, theName.length() - 2) + " " + val;

          while (_myData.findLayer(newName) != null)
          {
            val++;
            newName = theName.substring(0, theName.length() - 2) + " " + val;
          }
        }
        catch (final java.lang.NumberFormatException f)
        {
          newName = theName + " " + val;
          while (_myData.findLayer(newName) != null)
          {
            val++;
            newName = theName + " " + val;
          }
        }

        // ignore, there isn't a number, just add a 1
        newLayer.setName(newName);

        // just drop it in at the top level
        _myData.addThisLayerDoNotResize((Layer) theData);
      }
    }

  }

  protected boolean isEnablePaste()
  {
    boolean retVal = true;
    if (_myTree.getSelectionCount() == 1)
    {
      TreePath path = _myTree.getSelectionPath();
      DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path
          .getLastPathComponent();
      Object objectToPasteInto = selectedNode.getUserObject();
      Transferable tr = _clipboard.getContents(this);
      // see if there is currently a plottable on the clipboard
      if (tr != null && tr.isDataFlavorSupported(
          PlottableSelection.PlottableFlavor))
      {
        // we're off!
        try
        {
          // extract the plottable
          Object objectToPaste = tr.getTransferData(
              PlottableSelection.PlottableFlavor);
          if (tr instanceof OutlineViewSelection)
          {
            final Plottable[] plottables;
            if (objectToPaste instanceof Plottable[])
            {
              plottables = (Plottable[]) objectToPaste;
            }
            else
            {
              plottables = new Plottables[1];
              plottables[0] = (Plottable) objectToPaste;
            }
            // see if it is a layer or not
            for (Plottable theData : plottables)
            {
              // do the checks that are opposite
              if (objectToPasteInto instanceof BaseLayer)
              {
                if (!(theData instanceof BaseLayer)
                    && !(theData instanceof ShapeWrapper)
                    && !(theData instanceof LabelWrapper))
                {
                  retVal = false;
                }
              }
              if (objectToPasteInto instanceof TrackWrapper)
              {
                if (!(theData instanceof FixWrapper)
                    && !(theData instanceof SensorContactWrapper))
                {
                  retVal = false;
                }
                if (!retVal)
                {
                  break;
                }
              }
            }
          }
        }
        catch (Exception e)
        {
          MWC.Utilities.Errors.Trace.trace(e);
        }
      }
      else if (tr == null)
      {
        retVal = false;
      }
    }
    else
    {
      retVal = false;
    }
    return retVal;
  }

  private class OutlineRenderer extends DefaultTreeCellRenderer
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Icon visibilityIconEnabled;
    private Icon visibilityIconDisabled;
    @SuppressWarnings("unused")
    private int _xOffset = 0;
    private final Component strut = Box.createHorizontalStrut(5);
    private final JPanel panel = new JPanel();
    private JLabel visibility = new JLabel();
    private Border border = BorderFactory.createEmptyBorder(4, 2, 2, 4);
    private Map<String, ImageIcon> iconMap = new HashMap<String, ImageIcon>();

    public OutlineRenderer()
    {
      panel.setBackground(UIManager.getColor("Tree.textBackground"));
      setOpaque(false);
      panel.setOpaque(false);
      panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

      visibility.setOpaque(false);
      visibilityIconEnabled = new ImageIcon(getClass().getClassLoader()
          .getResource("icons/16/visible-eye.png"));
      visibilityIconDisabled = new ImageIcon(getClass().getClassLoader()
          .getResource("icons/16/invisible-eye.png"));
      panel.add(visibility);
      panel.add(strut);
      panel.add(this);
      panel.setBorder(border);
    }

    public JLabel getVisibilityLabel()
    {
      return visibility;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object node,
        boolean selected, boolean expanded, boolean leaf, int row,
        boolean hasFocus)
    {
      super.getTreeCellRendererComponent(tree, node, selected, expanded, leaf,
          row, hasFocus);
      if (node instanceof DefaultMutableTreeNode)
      {
        final DefaultMutableTreeNode tn = (DefaultMutableTreeNode) node;
        final Object data = tn.getUserObject();
        if (data instanceof Plottable)
        {
          final Plottable pl = (Plottable) tn.getUserObject();
          DebriefImageHelper helper = new DebriefImageHelper();
          String icon = helper.getImageFor(pl);
          if (icon == null)
          {
            String imageKey = CoreImageHelper.getImageKeyFor(pl);
            icon = "icons/16/" + imageKey;
          }
          if (icon != null)
          {
            // do we have this image in the cache?
            ImageIcon match = iconMap.get(icon);
            if (match == null)
            {
              // ok, we'll have to create it
              URL iconURL = DebriefImageHelper.class.getClassLoader()
                  .getResource(icon);
              if (iconURL == null)
              {
                System.err.println("Can't find icon:" + icon);
              }
              else
              {
                match = new ImageIcon(iconURL);
                iconMap.put(icon, match);
              }
            }

            // have we generated one?
            if (match != null)
            {
              // ok, use it
              setIcon(match);
            }

          }
          setVisibility(pl.getVisible());
        }
      }

      panel.doLayout();
      return panel;
    }

    public void paint(final java.awt.Graphics g)
    {
      super.paint(g);

      // get the location of the check box, to check our ticking
      if (g != null)
      {
        try
        {
          final FontMetrics fm = g.getFontMetrics();
          _xOffset = fm.stringWidth(getText()) + strut.getPreferredSize().width;
        }
        finally
        {
          // g.dispose();
        }
      }
    }

    private void setVisibility(boolean visible)
    {
      if (visible)
      {
        visibility.setIcon(visibilityIconEnabled);
      }
      else
      {
        visibility.setIcon(visibilityIconDisabled);
      }
    }

  }

  private class OutlineCellEditor extends AbstractCellEditor implements
      TreeCellEditor
  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JLabel visibilityLabel;
    private OutlineRenderer renderer;
    private DefaultMutableTreeNode lastEditedNode;

    public OutlineCellEditor()
    {
      renderer = new OutlineRenderer();
      visibilityLabel = renderer.getVisibilityLabel();

      visibilityLabel.addMouseListener(new MouseAdapter()
      {
        public void mousePressed(final MouseEvent e)
        {
          final Plottable pl = (Plottable) lastEditedNode.getUserObject();
          final PlottableNode pln = (PlottableNode) lastEditedNode;
          boolean newVisibility = !pl.getVisible();
          changeVisOfThisElement(pl, newVisibility, pln.getParentLayer());
          pln.setSelected(newVisibility);
          renderer.setVisibility(newVisibility);
          stopCellEditing();
        }
      });
    }

    public Component getTreeCellEditorComponent(final JTree tree,
        final Object value, final boolean selected, final boolean expanded,
        final boolean leaf, final int row)
    {
      lastEditedNode = (DefaultMutableTreeNode) value;

      return renderer.getTreeCellRendererComponent(tree, value, selected,
          expanded, leaf, row, true); // hasFocus ignored
    }

    public Object getCellEditorValue()
    {
      return lastEditedNode.getUserObject();
    }

  }

  protected void editThis(final TreeNode node)
  {
    System.out.println("Editing...");
    if (node instanceof DefaultMutableTreeNode)
    {
      final DefaultMutableTreeNode tn = (DefaultMutableTreeNode) node;
      final Object data = tn.getUserObject();
      if (data instanceof MWC.GUI.Editable)
      {
        final Editable editable = (Editable) data;
        if (editable.hasEditor())
        {
          // get the toolparent object
          final ToolParent tp = getToolParent();

          // did we get a valid toolparent?
          if (tp != null)
          {
            // set it to busy
            tp.setCursor(java.awt.Cursor.WAIT_CURSOR);
          }
          DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tn
              .getParent();
          final Object parentData = parent.getUserObject();
          ToolbarOwner owner = null;
          if (parentData instanceof ToolbarOwner)
          {
            owner = (ToolbarOwner) parentData;
          }
          PropertiesDialog dialog = new PropertiesDialog(editable, _myData,
              _undoBuffer, tp, owner);
          dialog.setSize(400, 500);
          dialog.setLocationRelativeTo(null);
          dialog.setVisible(true);
          if (tp != null)
          {
            tp.restoreCursor();
          }
        }
      }
    }

  }

  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents)
  {
    // do nothing

  }

  @Override
  public ArrayList<Plottable> getSelection()
  {
    final ArrayList<Plottable> res = new ArrayList<Plottable>();
    final TreePath[] selectionPaths = _myTree.getSelectionPaths();
    if (selectionPaths != null)
    {
      for (final TreePath item : selectionPaths)
      {
        Object component = item.getLastPathComponent();
        if (component instanceof PlottableNode)
        {
          PlottableNode node = (PlottableNode) component;
          Object object = node.getUserObject();
          if(object instanceof Plottable)
          {
            res.add((Plottable) object);
          }
        }
      }
    }
    return res;
  }

  @Override
  public ArrayList<Plottable> getClipboardContents()
  {
    final ArrayList<Plottable> res = new ArrayList<Plottable>();
    Transferable tr = _clipboard.getContents(this);
    // see if there is currently a plottable on the clipboard
    if (tr != null && tr.isDataFlavorSupported(
        PlottableSelection.PlottableFlavor))
    {
      if (tr instanceof OutlineViewSelection)
      {
        // extract the plottable
        Object objectToPaste = null;
        try
        {
          objectToPaste = tr.getTransferData(
              PlottableSelection.PlottableFlavor);
        }
        catch (UnsupportedFlavorException e)
        {
          e.printStackTrace();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }

        final Plottable[] plottables;
        if (objectToPaste != null)
        {
          if (objectToPaste instanceof Plottable[])
          {
            plottables = (Plottable[]) objectToPaste;
          }
          else
          {
            plottables = new Plottables[1];
            plottables[0] = (Plottable) objectToPaste;
          }
          // get the contents
          for (Plottable theData : plottables)
          {
            res.add(theData);
          }
        }
      }
    }
    return res;
  }
}
