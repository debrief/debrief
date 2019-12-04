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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
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
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.mwc.debrief.lite.DebriefLiteApp;
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
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.PlottableSelection;
import MWC.GUI.Plottables;
import MWC.GUI.ToolParent;
import MWC.GUI.LayerManager.Swing.SwingLayerManager;
import MWC.GUI.Tools.PlainTool;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI.ToolbarOwner;
import MWC.GUI.Undo.UndoBuffer;
import MWC.GenericData.TimePeriod;

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

  @SuppressWarnings("serial")
  final class DoAddLayer extends PlainTool implements ActionListener
  {
    final class AddLayerAction implements MWC.GUI.Tools.Action
    {
      /**
       *
       */
      private final Layer _layer;

      public AddLayerAction(final Layer layer)
      {
        _layer = layer;
      }

      @Override
      public void execute()
      {
        if (_layer != null)
        {
          _myData.addThisLayer(_layer);
        }
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

      @Override
      public void undo()
      {
        if (_layer != null)
        {
          _myData.removeThisLayer(_layer);
        }
      }
    }

    private Layer layerToAdd;

    DoAddLayer()
    {
      super(DebriefLiteApp.getDefault(), "Add Layer", null);
    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      layerToAdd = addLayer();
      super.execute();
    }

    @Override
    public MWC.GUI.Tools.Action getData()
    {
      return new AddLayerAction(layerToAdd);
    }
  }

  @SuppressWarnings("serial")
  final class DoDelete extends PlainTool
  {
    final class DeleteAction implements MWC.GUI.Tools.Action, ClipboardOwner
    {
      private final Plottable plottable;
      private final Layer parent;
      private final Plottable[] _data;
      private Transferable _oldData;
      private final boolean cutFlag;

      DeleteAction(final boolean isCut, final Plottable itemToDelete,
          final Layer parentItem, final Plottable[] data,
          final Transferable oldData)
      {
        cutFlag = isCut;
        plottable = itemToDelete;
        parent = parentItem;
        _data = data;
        _oldData = oldData;
      }

      @Override
      public void execute()
      {
        if (cutFlag)
        {
          storeOld();
          _clipboard.setContents(new OutlineViewSelection(_data, true), this);
        }
        if (parent != null)
        {
          parent.removeElement(plottable);
          _myData.fireModified(parent);
        }
        else
        {
          getDataLayers().removeThisLayer((Layer) plottable);
          _myData.fireExtended();

        }
        updateTime();
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

      @Override
      public void lostOwnership(final Clipboard clipboard,
          final Transferable contents)
      {
        // do nothing

      }

      private void restoreOld()
      {
        _clipboard.setContents(_oldData, this);
      }

      private void storeOld()
      {
        _oldData = _clipboard.getContents(this);
      }

      @Override
      public void undo()
      {
        if (parent != null)
        {
          parent.add(plottable);
          _myData.fireExtended(plottable, parent);
        }
        else
        {
          if (plottable instanceof Layer)
          {
            _myData.addThisLayer((Layer) plottable);
          }
        }
        if (_isCut)
        {
          restoreOld();
        }
        updateTime();
      }

      private void updateTime()
      {
        final TimePeriod period = _myData.getTimePeriod();
        DebriefLiteApp.getInstance().getTimeManager().setPeriod(this, period);
        if (period != null)
        {
          DebriefLiteApp.getInstance().getTimeManager().setTime(this, period
              .getStartDTG(), true);
        }
        System.out.println("Updated time");
      }
    }

    private Plottable itemToDelete;
    private Layer parentItem;
    private Plottable[] data;
    private Transferable _oldData;

    private final boolean _isCut;

    DoDelete(final boolean isCut)
    {
      super(DebriefLiteApp.getDefault(), "Delete", null);
      _isCut = isCut;
    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      final TreePath[] selectionPaths = _myTree.getSelectionPaths();
      if (_isCut)
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
                itemToDelete = (Plottable) object;
              }
              else
              {
                // ok, the parent isn't a layer. In that case this must
                // be a top level layer
                parentItem = null;
                itemToDelete = (Plottable) object;
              }
              super.execute();
              // _undoBuffer.add(getData());
            }
          }
        }
      }
    }

    @Override
    public final MWC.GUI.Tools.Action getData()
    {
      return new DeleteAction(_isCut, itemToDelete, parentItem, data, _oldData);
    }
  }

  @SuppressWarnings("serial")
  final class DoPaste extends PlainTool
  {

    final class PasteAction implements MWC.GUI.Tools.Action, ClipboardOwner
    {

      /**
       *
       */
      private ArrayList<Plottable> _lastPastedItems = new ArrayList<>();
      private final Layer _destination;
      private final boolean _isCopy;

      PasteAction(final Layer destination, final boolean iscopy,
          final ArrayList<Plottable> lastPastedItems)
      {
        _destination = destination;
        _isCopy = iscopy;
        _lastPastedItems = lastPastedItems;

      }

      protected void doPaste()
      {
        // see if there is currently a plottable on the clipboard
        // see if it is a layer or not
        if (!lastPastedItems.isEmpty())
        {
          for (final Plottable theData : _lastPastedItems)
          {
            addBackData(theData, _destination);
          }

          _myData.fireExtended(_lastPastedItems.get(0), _destination);
        }
        if (!_isCopy)
        {
          // clear the clipboard
          _clipboard.setContents(new Transferable()
          {
            @Override
            public Object getTransferData(final DataFlavor flavor)
                throws UnsupportedFlavorException
            {
              throw new UnsupportedFlavorException(flavor);
            }

            @Override
            public DataFlavor[] getTransferDataFlavors()
            {
              return new DataFlavor[0];
            }

            @Override
            public boolean isDataFlavorSupported(final DataFlavor flavor)
            {
              return false;
            }
          }, this);
        }
      }

      @Override
      public void execute()
      {
        doPaste();
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

      @Override
      public void lostOwnership(final Clipboard clipboard,
          final Transferable contents)
      {
        // do nothing
      }

      @Override
      public void undo()
      {
        for (final Plottable item : _lastPastedItems)
        {
          _destination.removeElement(item);
        }
        _myData.fireExtended(_lastPastedItems.get(0), _destination);
      }
    }

    private ArrayList<Plottable> lastPastedItems = new ArrayList<>();
    private Layer destination;

    private boolean _isCopy;

    public DoPaste()
    {
      super(DebriefLiteApp.getDefault(), "Paste", null);
    }

    @Override
    public void actionPerformed(final ActionEvent e)
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
      super.execute();
    }

    @Override
    public MWC.GUI.Tools.Action getData()
    {

      return new PasteAction(destination, _isCopy, lastPastedItems);
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
      try
      {
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
      catch (final RuntimeException re)
      {
        JOptionPane.showMessageDialog(null, re.getMessage(),
            "Error while pasting", JOptionPane.ERROR_MESSAGE);
      }
      catch (final Exception e)
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
    button.setName(command);
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
    boolean modified = false;
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
            modified = true;
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
        @Override
        public Object getTransferData(final DataFlavor flavor)
            throws UnsupportedFlavorException
        {
          throw new UnsupportedFlavorException(flavor);
        }

        @Override
        public DataFlavor[] getTransferDataFlavors()
        {
          return new DataFlavor[0];
        }

        @Override
        public boolean isDataFlavorSupported(final DataFlavor flavor)
        {
          return false;
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
          final Layer parentLayer;
          if (parentData instanceof Layer)
          {
            parentLayer = (Layer) parentData;
          }
          else
          {
            parentLayer = null;
          }
          final PropertiesDialog dialog = new PropertiesDialog(editable
              .getInfo(), _myData, _undoBuffer, tp, owner, parentLayer);
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

  protected Layers getDataLayers()
  {
    return getData();
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
    // final EnabledTest isEmpty = getSelectionEmptyTest();
    final EnabledTest notNarrative = getNotNarrativeTest();
    final EnabledTest notIsLayer = getNotLayerTest();

    final JButton collapseAllButton = createCommandButton("Collapse All",
        "icons/24/collapse_all.png");
    collapseAllButton.setEnabled(true);
    collapseAllButton.setMnemonic(KeyEvent.VK_MINUS);
    commandBar.add(collapseAllButton);

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
    // _enablers.add(new ButtonEnabler(addLayerButton, new Or(isEmpty,notEmpty)));
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
    final DoPaste pasteAction = new DoPaste();
    pasteButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke
        .getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "paste");
    pasteButton.getActionMap().put("paste", pasteAction);
    pasteButton.addActionListener(pasteAction);

    final Action collapseAction = new AbstractAction()
    {

      /**
       *
       */
      private static final long serialVersionUID = 1856754284317991555L;

      @Override
      public void actionPerformed(final ActionEvent e)
      {
        final Object root = _myTree.getModel().getRoot();
        if (root instanceof DefaultMutableTreeNode)
        {
          final DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) root;

          final int count = _myTree.getModel().getChildCount(rootNode);
          for (int i = 0; i < count; i++)
          {
            final Object child = _myTree.getModel().getChild(rootNode, i);
            if (child instanceof DefaultMutableTreeNode)
            {
              final DefaultMutableTreeNode childNode =
                  (DefaultMutableTreeNode) child;
              collapseAll(new TreePath(childNode.getPath()));
            }
          }
        }
      }

      private void collapseAll(final TreePath selectionPath)
      {
        final int count = _myTree.getModel().getChildCount(selectionPath
            .getLastPathComponent());
        for (int i = 0; i < count; i++)
        {
          final Object child = _myTree.getModel().getChild(selectionPath
              .getLastPathComponent(), i);
          if (child instanceof DefaultMutableTreeNode)
          {
            final DefaultMutableTreeNode childNode =
                (DefaultMutableTreeNode) child;
            collapseAll(new TreePath(childNode.getPath()));
          }
        }
        _myTree.collapsePath(selectionPath);
      }

    };

    collapseAllButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK), "collapseall");
    collapseAllButton.getActionMap().put("collapseall", collapseAction);
    collapseAllButton.addActionListener(collapseAction);

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
        .getKeyStroke(KeyEvent.VK_ENTER, 0), "edit");
    editButton.getActionMap().put("edit", editAction);
    editButton.addActionListener(editAction);
    final ActionListener addLayerAction = new DoAddLayer();
    addLayerButton.addActionListener(addLayerAction);
    final DoDelete cutAction = new DoDelete(true);
    cutButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke
        .getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK), "cut");
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
        .getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "copy");
    copyButton.getActionMap().put("copy", copyAction);
    copyButton.addActionListener(copyAction);
    final Action deleteAction = new DoDelete(false);
    copyButton.setEnabled(false);
    deleteButton.setEnabled(false);
    pasteButton.setEnabled(false);
    editButton.setEnabled(false);
    cutButton.setEnabled(false);
    deleteButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke
        .getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
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

  };

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
    if (changedLayer != null)
    {
      if (newItem != null)
      {
        DefaultMutableTreeNode rootNode = getTreeNode(null, changedLayer
            .getName(), changedLayer);
        if (rootNode != null)
        {
          DefaultMutableTreeNode itemNode = null;
          if (rootNode.getUserObject() instanceof Layer)
          {
            if (newItem instanceof FixWrapper)
            {
              rootNode = (DefaultMutableTreeNode) rootNode.getFirstChild();
            }
            itemNode = getTreeNode(rootNode, newItem.getName(), newItem);
          }
          if (itemNode != null)
          {
            final TreePath _treePath = new TreePath(itemNode.getPath());

            ((DefaultTreeModel) _myTree.getModel()).reload(rootNode);
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
          else
          {
            System.out.println("Found null itemnode");
          }
        }
      }
      else
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
  };

}
