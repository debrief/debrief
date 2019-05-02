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

import static Debrief.GUI.Views.LogicHelpers.getClipboardNotEmptyTest;
import static Debrief.GUI.Views.LogicHelpers.getIsFixesTest;
import static Debrief.GUI.Views.LogicHelpers.getIsLayerTest;
import static Debrief.GUI.Views.LogicHelpers.getIsShapesTest;
import static Debrief.GUI.Views.LogicHelpers.getIsTrackTest;
import static Debrief.GUI.Views.LogicHelpers.getNotEmptyTest;
import static Debrief.GUI.Views.LogicHelpers.getNotLayerTest;
import static Debrief.GUI.Views.LogicHelpers.getNotNarrativeTest;
import static Debrief.GUI.Views.LogicHelpers.getOnlyOneTest;
import static Debrief.GUI.Views.LogicHelpers.getSelectionEmptyTest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
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
import org.mwc.debrief.lite.properties.PropertiesDialog;

import Debrief.GUI.CoreImageHelper;
import Debrief.GUI.DebriefImageHelper;
import Debrief.GUI.Views.LogicHelpers.And;
import Debrief.GUI.Views.LogicHelpers.EnabledTest;
import Debrief.GUI.Views.LogicHelpers.Helper;
import Debrief.GUI.Views.LogicHelpers.Or;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanEnumerate;
import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.PlottableSelection;
import MWC.GUI.Plottables;
import MWC.GUI.ToolParent;
import MWC.GUI.LayerManager.Swing.SwingLayerManager;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI.ToolbarOwner;
import MWC.GUI.Undo.UndoBuffer;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class OutlinePanelView extends SwingLayerManager implements
    ClipboardOwner, Helper
{

  private static class ButtonEnabler
  {
    private final Component _button;
    private final EnabledTest _test;
    private final String _title;

    private ButtonEnabler(final JButton button, final EnabledTest test)
    {
      this(button.getToolTipText(), button, test);
    }

    private ButtonEnabler(final String title, final Component button,
        final EnabledTest test)
    {
      _title = title;
      _button = button;
      _test = test;
    }

    private void refresh(final Helper helper)
    {
      _button.setEnabled(_test.isEnabled(helper));
    }

    @Override
    public String toString()
    {
      return _title;
    }
  }

  private class OutlineCellEditor extends AbstractCellEditor implements
      TreeCellEditor
  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final JLabel visibilityLabel;
    private final OutlineRenderer renderer;
    private DefaultMutableTreeNode lastEditedNode;

    public OutlineCellEditor()
    {
      renderer = new OutlineRenderer();
      visibilityLabel = renderer.getVisibilityLabel();

      visibilityLabel.addMouseListener(new MouseAdapter()
      {
        @Override
        public void mousePressed(final MouseEvent e)
        {
          final Plottable pl = (Plottable) lastEditedNode.getUserObject();
          final PlottableNode pln = (PlottableNode) lastEditedNode;
          final boolean newVisibility = !pl.getVisible();
          changeVisOfThisElement(pl, newVisibility, pln.getParentLayer());
          pln.setSelected(newVisibility);
          renderer.setVisibility(newVisibility);
          stopCellEditing();
        }
      });
    }

    @Override
    public Object getCellEditorValue()
    {
      return lastEditedNode.getUserObject();
    }

    @Override
    public Component getTreeCellEditorComponent(final JTree tree,
        final Object value, final boolean selected, final boolean expanded,
        final boolean leaf, final int row)
    {
      lastEditedNode = (DefaultMutableTreeNode) value;

      return renderer.getTreeCellRendererComponent(tree, value, selected,
          expanded, leaf, row, true); // hasFocus ignored
    }

  }

  private class OutlineRenderer extends DefaultTreeCellRenderer
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final Icon visibilityIconEnabled;
    private final Icon visibilityIconDisabled;
    @SuppressWarnings("unused")
    private int _xOffset = 0;
    private final Component strut = Box.createHorizontalStrut(5);
    private final JPanel panel = new JPanel();
    private final JLabel visibility = new JLabel();
    private final Border border = BorderFactory.createEmptyBorder(4, 2, 2, 4);
    private final Map<String, ImageIcon> iconMap =
        new HashMap<String, ImageIcon>();

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

    @Override
    public Component getTreeCellRendererComponent(final JTree tree,
        final Object node, final boolean selected, final boolean expanded,
        final boolean leaf, final int row, final boolean hasFocus)
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
          final DebriefImageHelper helper = new DebriefImageHelper();
          String icon = helper.getImageFor(pl);
          if (icon == null)
          {
            final String imageKey = CoreImageHelper.getImageKeyFor(pl);
            icon = "icons/16/" + imageKey;
          }
          if (icon != null)
          {
            // do we have this image in the cache?
            ImageIcon match = iconMap.get(icon);
            if (match == null)
            {
              // ok, we'll have to create it
              final URL iconURL = DebriefImageHelper.class.getClassLoader()
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

    public JLabel getVisibilityLabel()
    {
      return visibility;
    }

    @Override
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

    private void setVisibility(final boolean visible)
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

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public static void main(final String[] args)
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
    final Helper helper1 = new Helper()
    {

      @Override
      public ArrayList<Plottable> getClipboardContents()
      {
        return new ArrayList<Plottable>();
      }

      @Override
      public ArrayList<Plottable> getSelection()
      {
        final ArrayList<Plottable> res = new ArrayList<Plottable>();
        res.add(new BaseLayer());
        return res;
      }
    };

    final Helper helper2 = new Helper()
    {

      @Override
      public ArrayList<Plottable> getClipboardContents()
      {
        return new ArrayList<Plottable>();
      }

      @Override
      public ArrayList<Plottable> getSelection()
      {
        final ArrayList<Plottable> res = new ArrayList<Plottable>();
        res.add(new BaseLayer());
        res.add(new BaseLayer());
        return res;
      }
    };

    System.out.println(new And(hasData, isEmpty).isEnabled(helper1));
    System.out.println(new Or(hasData, isEmpty).isEnabled(helper1));
    System.out.println(new And(notEmpty, onlyOne).isEnabled(helper1));
    System.out.println(new And(notEmpty, onlyOne).isEnabled(helper2));
  }

  private final UndoBuffer _undoBuffer;

  private final Clipboard _clipboard;

  private final ArrayList<ButtonEnabler> _enablers =
      new ArrayList<ButtonEnabler>();

  private Transferable _cutContents;

  private TreePath _theCutParent;

  public OutlinePanelView(final UndoBuffer undoBuffer,
      final Clipboard clipboard)
  {
    _undoBuffer = undoBuffer;
    _clipboard = clipboard;
  }

  private void addBackData(final Plottable theData,
      final CanEnumerate destination)
  {
    if (theData instanceof Layer)
    {
      pasteLayer(destination, (Layer) theData);
    }
    else
    {
      try {
        if (destination instanceof Layer)
        {
          ((Layer) destination).add(theData);
        }
        else
        {
          if (_cutContents != null)
          {
            restoreCutContents();
          }
          doDelete();
          _myTree.setSelectionPath(null);
        }
      }
      catch (RuntimeException re)
      {
        JOptionPane.showMessageDialog(null, re.getMessage(),
            "Error while pasting", JOptionPane.ERROR_MESSAGE);
      }
      catch (Exception e)
      {
        System.err.println("Error occured while pasting:" + e.getMessage());
      }
    }

  }

  private JButton createCommandButton(final String command, final String image)
  {
    final URL imageIcon = getClass().getClassLoader().getResource(image);
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon(imageIcon);
    }
    catch (final Exception e)
    {
      System.err.println("Failed to find icon:" + image);
      e.printStackTrace();
    }
    final JButton button = new JButton(icon);
    button.setToolTipText(command);
    return button;
  }

  protected void doCopy(final TreePath[] selectionPaths)
  {
    final Plottable[] plottables = new Plottable[selectionPaths.length];
    int i = 0;
    for (final TreePath path : selectionPaths)
    {
      plottables[i++] = (Plottable) ((DefaultMutableTreeNode) path
          .getLastPathComponent()).getUserObject();
    }
    final OutlineViewSelection selection = new OutlineViewSelection(plottables,
        true);
    _clipboard.setContents(selection, this);

  }

  protected void doDelete()
  {
    final TreePath[] selectionPaths = _myTree.getSelectionPaths();
    if (selectionPaths != null)
    {
      for (final TreePath item : selectionPaths)
      {
        final Object component = item.getLastPathComponent();
        if (component instanceof PlottableNode)
        {
          final PlottableNode node = (PlottableNode) component;
          final Object object = node.getUserObject();
          if (object instanceof Plottable)
          {
            final int pathCount = item.getPathCount();
            // ok, delete it, get the parent
            final DefaultMutableTreeNode parentNode =
                (DefaultMutableTreeNode) _myTree.getSelectionPath()
                    .getPathComponent(pathCount - 2);
            final Object parent = parentNode.getUserObject();
            if (parent instanceof Layer)
            {
              final Layer layer = (Layer) parent;
              layer.removeElement((Editable) object);
            }
            else
            {
              // ok, the parent isn't a layer. In that case this must
              // be a top level layer
              getData().removeThisLayer((Layer) object);
            }
          }
        }
      }
      _myTree.setSelectionPath(null);
    }   
    if (modified)
    {
      doReset();
    }
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
    final ArrayList<Plottable> plottables = getClipboardContents();
    // see if there is currently a plottable on the clipboard
    // see if it is a layer or not
    if (!plottables.isEmpty())
    {
      for (final Plottable theData : plottables)
      {
        addBackData(theData, destination);
      }
      _myData.fireExtended(plottables.get(0), (HasEditables) destination);
    }
    if (!_isCopy)
    {
      // clear the clipboard
      _clipboard.setContents(new Transferable()
      {
        public DataFlavor[] getTransferDataFlavors()
        {
          return new DataFlavor[0];
        }

        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
          return false;
        }

        public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException
        {
          throw new UnsupportedFlavorException(flavor);
        }
      }, this);
    }
  }
  
  @Override
  protected void editThis(final TreeNode node)
  {
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
          final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tn
              .getParent();
          final Object parentData = parent.getUserObject();
          ToolbarOwner owner = null;
          if (parentData instanceof ToolbarOwner)
          {
            owner = (ToolbarOwner) parentData;
          }
          final PropertiesDialog dialog = new PropertiesDialog(editable
              .getInfo(), _myData, _undoBuffer, tp, owner);
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
  public ArrayList<Plottable> getClipboardContents()
  {
    final Transferable tr = _clipboard.getContents(this);
    // see if there is currently a plottable on the clipboard
    return getContentsFromTransferable(tr);
  }

  public ArrayList<Plottable> getContentsFromTransferable(final Transferable tr)
  {
    final ArrayList<Plottable> res = new ArrayList<Plottable>();
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
        catch (final UnsupportedFlavorException e)
        {
          e.printStackTrace();
        }
        catch (final IOException e)
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
          for (final Plottable theData : plottables)
          {
            res.add(theData);
          }
        }
      }
    }
    return res;

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
        final Object component = item.getLastPathComponent();
        if (component instanceof PlottableNode)
        {
          final PlottableNode node = (PlottableNode) component;
          final Object object = node.getUserObject();
          if (object instanceof Plottable)
          {
            res.add((Plottable) object);
          }
        }
      }
    }
    return res;
  }

  @Override
  protected void initForm()
  {
    super.initForm(true);
    final JPanel commandBar = new JPanel();
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
    final EnabledTest isEmpty = getSelectionEmptyTest();
    final EnabledTest notNarrative = getNotNarrativeTest();
    final EnabledTest notIsLayer = getNotLayerTest();

    final JButton editButton = createCommandButton("Edit", "icons/24/edit.png");
    _enablers.add(new ButtonEnabler(editButton, new And(notEmpty, onlyOne)));
    editButton.setEnabled(false);
    editButton.setMnemonic(KeyEvent.VK_ENTER);
    commandBar.add(editButton);

    final JButton cutButton = createCommandButton("Cut", "icons/24/cut.png");
    _enablers.add(new ButtonEnabler(cutButton, new And(notEmpty, notNarrative,
        notIsLayer)));
    cutButton.setEnabled(false);
    cutButton.setMnemonic(KeyEvent.VK_X);
    commandBar.add(cutButton);

    final JButton copyButton = createCommandButton("Copy",
        "icons/24/copy_to_clipboard.png");
    _enablers.add(new ButtonEnabler(copyButton, new And(notEmpty, notNarrative,
        notIsLayer)));
    copyButton.setEnabled(false);
    copyButton.setMnemonic(KeyEvent.VK_C);
    commandBar.add(copyButton);

    final JButton pasteButton = createCommandButton("Paste",
        "icons/24/paste.png");
    _enablers.add(new ButtonEnabler(pasteButton, new And(clipboardNotEmpty,
        new Or(new And(selectionIsTrack, clipboardIsFixes), new And(
            selectionIsLayer, clipboardIsShapes)))));
    pasteButton.setEnabled(false);
    pasteButton.setMnemonic(KeyEvent.VK_V);
    commandBar.add(pasteButton);

    final JButton addLayerButton = createCommandButton("Add Layer",
        "icons/24/add.png");
    //_enablers.add(new ButtonEnabler(addLayerButton, new Or(isEmpty,notEmpty)));
    commandBar.add(addLayerButton);

    final JButton deleteButton = createCommandButton("Delete",
        "icons/24/remove.png");
    deleteButton.setToolTipText("Delete");
    deleteButton.setMnemonic(KeyEvent.VK_DELETE);
    _enablers.add(new ButtonEnabler(deleteButton, notEmpty));
    deleteButton.setEnabled(false);
    commandBar.add(deleteButton);

    final JButton refreshViewButton = createCommandButton("Update View",
        "icons/24/repaint.png");
    refreshViewButton.setToolTipText("Update View");
    refreshViewButton.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        doReset();
      }
    });
    commandBar.add(refreshViewButton);
    final Helper me = this;
    _myTree.addTreeSelectionListener(new TreeSelectionListener()
    {
      @Override
      public void valueChanged(final TreeSelectionEvent e)
      {
        final ArrayList<Plottable> sel = me.getSelection();
        final ArrayList<Plottable> clip = me.getClipboardContents();
        final Helper helper = new Helper()
        {
          @Override
          public ArrayList<Plottable> getClipboardContents()
          {
            return clip;
          }

          @Override
          public ArrayList<Plottable> getSelection()
          {
            return sel;
          }
        };

        for (final ButtonEnabler t : _enablers)
        {
          t.refresh(helper);
        }
      }
    });
    final Action pasteAction = new PasteAction();
    pasteButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke
        .getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit()
            .getMenuShortcutKeyMask()), "paste");
    pasteButton.getActionMap().put("paste", pasteAction);
    pasteButton.addActionListener(pasteAction);

    final Action editAction = new AbstractAction()
    {

      /**
       *
       */
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        final int selectionCount = _myTree.getSelectionCount();
        if (selectionCount == 1)
        {
          final TreePath selectionPath = _myTree.getSelectionPath();
          if (selectionPath != null)
          {
            final Object node = selectionPath.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode)
            {
              editThis((DefaultMutableTreeNode) node);
            }
          }
        }

      }
    };
    editButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke
        .getKeyStroke(KeyEvent.VK_ENTER, Toolkit.getDefaultToolkit()
            .getMenuShortcutKeyMask()), "edit");
    editButton.getActionMap().put("edit", editAction);
    editButton.addActionListener(editAction);
    final MWC.GUI.Tools.Action addLayerAction = new AddLayerAction();
    addLayerButton.addActionListener((ActionListener)addLayerAction);
    final Action cutAction = new DeleteAction(true);
    cutButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke
        .getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit()
            .getMenuShortcutKeyMask()), "cut");
    cutButton.getActionMap().put("cut", cutAction);
    cutButton.addActionListener(cutAction);
    final Action copyAction = new AbstractAction()
    {

      /**
       *
       */
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        final int selectionCount = _myTree.getSelectionCount();
        if (selectionCount > 0)
        {
          final TreePath selectionPath[] = _myTree.getSelectionPaths();
          doCopy(selectionPath);
          _myTree.setSelectionPath(null);
        }
      }
    };
    copyButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke
        .getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit()
            .getMenuShortcutKeyMask()), "copy");
    copyButton.getActionMap().put("copy", copyAction);
    copyButton.addActionListener(copyAction);
    Action deleteAction = new DeleteAction(false);
    copyButton.setEnabled(false);
    deleteButton.setEnabled(false);
    pasteButton.setEnabled(false);
    editButton.setEnabled(false);
    cutButton.setEnabled(false);
    deleteButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke
        .getKeyStroke("DELETE"), "delete");
    deleteButton.getActionMap().put("delete", deleteAction);
    deleteButton.addActionListener(deleteAction);
    add(commandBar, BorderLayout.NORTH);
    setCellRenderer(new OutlineRenderer());
    setCellEditor(new OutlineCellEditor());
  }

  @Override
  public void lostOwnership(final Clipboard clipboard,
      final Transferable contents)
  {
    // do nothing

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
          _myData.addThisLayerDoNotResize(theData);
      }
      else
      {
        // adjust the name
        final Layer newLayer = theData;

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
        _myData.addThisLayerDoNotResize(theData);
      }
    }

  }

  private void restoreCutContents()
  {
    final Transferable tr = _cutContents;
    final ArrayList<Plottable> plottables = getContentsFromTransferable(tr);
    final CanEnumerate destination;
    if (_theCutParent != null && plottables != null && !plottables.isEmpty())
    {
      final Object obj = ((DefaultMutableTreeNode) _theCutParent
          .getLastPathComponent()).getUserObject();
      if (obj instanceof String)
      {
        destination = null;
      }
      else
      {
        destination = (CanEnumerate) obj;
      }
    }
    else
    {
      destination = null;
    }
    for (final Plottable theData : plottables)
    {
      addBackData(theData, destination);
    }
    _cutContents = null;
    _theCutParent = null;
    _myData.fireModified(null);
  }

  /**
   * have a fresh pass through the data
   */
  public void updateData(final Layer changedLayer, final Plottable newItem)
  {
    _myTree.setExpandsSelectedPaths(true);
    // find out which node is currently visible
    if (changedLayer != null && newItem != null)
    {
      DefaultMutableTreeNode rootNode = getTreeNode(null, changedLayer
          .getName(), changedLayer);
      if (rootNode != null)
      {
        final DefaultMutableTreeNode itemNode;
        if (rootNode.getUserObject() instanceof Layer)
        {
          // used class name instead of instanceof as otherwise
          // there will be cyclic dependency in eclipse manifest.

          if (newItem instanceof FixWrapper)
          {
            rootNode = (DefaultMutableTreeNode) rootNode.getFirstChild();
          }
          itemNode = getTreeNode(rootNode, newItem.getName(),
              newItem);
        }
        else
        {
          // if not a layer but a trackwrapper
          final DefaultMutableTreeNode firstChild =
              (DefaultMutableTreeNode) rootNode.getFirstChild();
          if (firstChild != null)
          {
            itemNode = getTreeNode((DefaultMutableTreeNode) rootNode
                .getFirstChild(), newItem.getName(),

                newItem);
          }
          else
          {
            itemNode = null;
          }
        }
        if (itemNode != null)
        {
          final TreePath _treePath = new TreePath(itemNode.getPath());
          SwingUtilities.invokeLater(new Runnable()
          {
            @Override
            public void run()
            {
              _myTree.expandPath(_treePath);
              _myTree.scrollPathToVisible(_treePath);
              _myTree.makeVisible(_treePath);
              _myTree.setSelectionPath(_treePath);
            }
          });
        }
      }
    }
    else if (changedLayer != null)
    {
      final DefaultMutableTreeNode rootNode = getTreeNode(null, changedLayer
          .getName(), changedLayer);
      if (rootNode != null)
      {
        final TreePath _treePath = new TreePath(rootNode.getPath());
        SwingUtilities.invokeLater(new Runnable()
        {
          @Override
          public void run()
          {
            _myTree.expandPath(_treePath);
            _myTree.scrollPathToVisible(_treePath);
            _myTree.makeVisible(_treePath);
            _myTree.setSelectionPath(_treePath);
          }
        });
      }
    }
  }
  @SuppressWarnings("serial")
  final class AddLayerAction extends AbstractAction implements MWC.GUI.Tools.Action
  {
    private Layer layerToAdd;
    @Override
    public void actionPerformed(ActionEvent e)
    {
      layerToAdd = addLayer();
      _undoBuffer.add(this);
    }
    @Override
    public void undo()
    {
      _myData.removeThisLayer(layerToAdd);
    }
    
    @Override
    public boolean isUndoable()
    {
      return true;
    }
    
    @Override
    public boolean isRedoable()
    {
      return true;
    }
    
    @Override
    public void execute()
    {
      _myData.addThisLayer(layerToAdd);
    }
  };
  @SuppressWarnings("serial")
  final class DeleteAction extends AbstractAction implements MWC.GUI.Tools.Action,ClipboardOwner
  {
    private Plottable itemToDelete;
    private Layer parentItem;
    private Plottable[] data;
    private Transferable _oldData;
    boolean _isCut;
    DeleteAction(boolean isCut){
      _isCut = isCut;
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
      final TreePath[] selectionPaths = _myTree.getSelectionPaths();
      if(_isCut)
      {
        final Plottable[] plottables = new Plottable[selectionPaths.length];
        int i = 0;
        for (final TreePath path : selectionPaths)
        {
          plottables[i++] = (Plottable) ((DefaultMutableTreeNode) path
              .getLastPathComponent()).getUserObject();
        }
        data = plottables;
      }
      
      if (selectionPaths != null)
      {
        
        for (final TreePath item : selectionPaths)
        {
          final Object component = item.getLastPathComponent();
          if (component instanceof PlottableNode)
          {
            final PlottableNode node = (PlottableNode) component;
            final Object object = node.getUserObject();
            if (object instanceof Plottable)
            {
              final int pathCount = item.getPathCount();
              // ok, delete it, get the parent
              final DefaultMutableTreeNode parentNode =
                  (DefaultMutableTreeNode) _myTree.getSelectionPath()
                  .getPathComponent(pathCount - 2);
              final Object parent = parentNode.getUserObject();

              if (parent instanceof Layer)
              {
                final Layer layer = (Layer) parent;
                parentItem = layer;
                itemToDelete = (Plottable)object;
              }
              else
              {
                // ok, the parent isn't a layer. In that case this must
                // be a top level layer
                parentItem = null;
                itemToDelete = (Plottable)object;
              }
              execute();
              _undoBuffer.add(this);
            }
          }
        }
      }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents)
    {
      //do nothing
      
    }

    @Override
    public boolean isUndoable()
    {
      return true;
    }

    @Override
    public boolean isRedoable()
    {
      return true;
    }

    @Override
    public void undo()
    {
      if(parentItem!=null) {
        parentItem.add(itemToDelete);
        _myData.fireExtended(itemToDelete,parentItem);
      }
      else {
        if(itemToDelete instanceof Layer) {
          _myData.addThisLayer((Layer)itemToDelete);
          _myData.fireExtended();
        }
      }
      if(_isCut) {
        restoreOld();
      }
      
    }

    @Override
    public void execute()
    {
      if(_isCut) {
       storeOld();
        _clipboard.setContents(new OutlineViewSelection(data,true), this);
      }
      if(parentItem!=null) {
        parentItem.removeElement((Editable) itemToDelete);
        _myData.fireModified(parentItem);
      }
      else {
        getData().removeThisLayer((Layer) itemToDelete);
        _myData.fireExtended();
      }
      
      
    }
    private void restoreOld() {
      _clipboard.setContents(_oldData, this);
    }
    private void storeOld()
    {
      _oldData = _clipboard.getContents(this);
    }
  }
  @SuppressWarnings("serial")
  final class PasteAction extends AbstractAction implements MWC.GUI.Tools.Action,ClipboardOwner
  {
    private ArrayList<Plottable> lastPastedItems = new ArrayList<>();
    private Layer destination;
    private boolean _isCopy;

    @Override
    public void actionPerformed(ActionEvent e)
    {
      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) _myTree
          .getSelectionPath().getLastPathComponent();
      final Editable editable = (Editable) node.getUserObject();
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
      _isCopy = os.isACopy();
      final ArrayList<Plottable> plottables = getClipboardContents();
      lastPastedItems = plottables;
      execute();
      _undoBuffer.add(this);
    }
    @Override
    public void undo()
    {
      for(Plottable item:lastPastedItems) {
        destination.removeElement(item);
      }
      _myData.fireExtended(lastPastedItems.get(0), (HasEditables) destination);
    }
    
    @Override
    public boolean isUndoable()
    {
      return true;
    }
    
    @Override
    public boolean isRedoable()
    {
      return true;
    }
    @Override
    public void execute()
    {
      doPaste();
    }
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents)
    {
     //do nothing 
    }
    protected void doPaste()
    {
      // see if there is currently a plottable on the clipboard
      // see if it is a layer or not
      if (!lastPastedItems.isEmpty())
      {
        for (final Plottable theData : lastPastedItems)
        {
          addBackData(theData, (CanEnumerate)destination);
        }
        
        _myData.fireExtended(lastPastedItems.get(0), (HasEditables) destination);
      }
      if (!_isCopy)
      {
        // clear the clipboard
        _clipboard.setContents(new Transferable()
        {
          public DataFlavor[] getTransferDataFlavors()
          {
            return new DataFlavor[0];
          }

          public boolean isDataFlavorSupported(DataFlavor flavor)
          {
            return false;
          }

          public Object getTransferData(DataFlavor flavor)
              throws UnsupportedFlavorException
          {
            throw new UnsupportedFlavorException(flavor);
          }
        }, this);
      }
    }
  };
  

    
  
}
