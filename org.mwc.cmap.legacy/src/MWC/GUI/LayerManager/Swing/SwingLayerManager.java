/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package MWC.GUI.LayerManager.Swing;

// Copyright MWC 1999
// $RCSfile: SwingLayerManager.java,v $
// $Author: Ian.Mayo $
// $Log: SwingLayerManager.java,v $
// Revision 1.6  2004/10/07 14:23:08  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.5  2004/09/07 07:35:07  Ian.Mayo
// Back down from showing editables as well as plottables
//
// Revision 1.3  2004/09/03 15:12:25  Ian.Mayo
// Experiment with allowing editables to be shown, not just plottables
//
// Revision 1.2  2004/05/25 15:28:13  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:22  Ian.Mayo
// Initial import
//
// Revision 1.8  2002-12-16 15:21:32+00  ian_mayo
// Correct finalise method (wasn't being called)
//
// Revision 1.7  2002-11-25 16:01:27+00  ian_mayo
// Handle close operation, ditch d-lines
//
// Revision 1.6  2002-11-25 14:38:54+00  ian_mayo
// Minor tidying, and add the ability to make item vis/not vis
//
// Revision 1.5  2002-07-08 11:48:09+01  ian_mayo
// <>
//
// Revision 1.4  2002-06-05 12:56:28+01  ian_mayo
// unnecessarily loaded
//
// Revision 1.3  2002-05-31 16:21:16+01  ian_mayo
// Provide ability to paste to top level layer
//
// Revision 1.2  2002-05-28 09:25:41+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:45+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:47+01  ian_mayo
// Initial revision
//
// Revision 1.13  2002-03-19 11:04:57+00  administrator
// Switch to Swing menus
//
// Revision 1.12  2002-03-12 09:21:30+00  administrator
// Parent layer renamed, plus we correctly support deleting sub-layer (such as a SensorTrack)
//
// Revision 1.11  2002-02-18 09:19:39+00  administrator
// Set the name of the GUI component (largely so that we can access it from JFCUnit)
//
// Revision 1.10  2002-02-01 16:17:24+00  administrator
// Correct bugs which were preventing us from Deleting a top level layer
//
// Revision 1.9  2002-01-25 13:31:37+00  administrator
// Pass around the "top-layer" objects so that we can update just the required layer following move
//
// Revision 1.8  2002-01-24 14:22:31+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.7  2001-08-29 19:19:48+01  administrator
// Tidy up buttons
//
// Revision 1.6  2001-08-24 12:39:09+01  administrator
// Extended support for plottable extras (cut/copy/paste)
//
// Revision 1.5  2001-08-24 09:56:05+01  administrator
// Force repaint of tree following reformatting (to make sure visible flag gets ticked correctly
//
// Revision 1.4  2001-08-17 07:56:41+01  administrator
// Reflect change in RightClickEditor, which no longer supplies the list of non-plottables.
//
// Revision 1.3  2001-08-13 12:48:59+01  administrator
// use Mutable tree nodes instead of Vectors (so that we can use sorted lists)
//
// Revision 1.1  2001-07-18 16:01:21+01  administrator
// add method which retrieves the current colour of the object, if applicable
//
// Revision 1.0  2001-07-17 08:46:16+01  administrator
// Initial revision
//
// Revision 1.5  2001-07-16 15:01:12+01  novatech
// 1. tidy up code which shows popup menu (now at correct location)
// 2. Add tickbox to show if label visible or not
//
// Revision 1.4  2001-07-12 12:13:33+01  novatech
// Put the Open Editor call into a Busy cursor block
//
// Revision 1.3  2001-07-09 13:59:58+01  novatech
// prevent double-click from expanding branch - so we can used double-click to only open editor
//
// Revision 1.2  2001-01-05 09:13:19+00  novatech
// Improve processing, and allow the duplication of layer names
//
// Revision 1.1  2001-01-03 13:42:51+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:06  ianmayo
// initial version
//
// Revision 1.11  2000-11-22 10:36:29+00  ian_mayo
// allow presentation of empty layers
//
// Revision 1.10  2000-11-08 11:50:58+00  ian_mayo
// tidying up
//
// Revision 1.9  2000-11-02 16:44:33+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer
//
// Revision 1.8  2000-04-05 08:35:17+01  ian_mayo
// Add handler for Reset button
//
// Revision 1.7  2000-03-14 14:48:34+00  ian_mayo
// re-arrange layout to that tree-control has to shrink to fit
//
// Revision 1.6  2000-02-15 16:38:10+00  ian_mayo
// corrected problem of automatically opening in properties window when right-clicking an item (it appears Java 1.2.1 was mis-interpreting a right-click as a double-click)
//
// Revision 1.5  2000-02-04 16:08:04+00  ian_mayo
// Experiment with different layouts
//
// Revision 1.4  2000-01-21 12:04:13+00  ian_mayo
// cutting & pasting layers
//
// Revision 1.3  2000-01-20 10:17:24+00  ian_mayo
// extensive implementation - add layer, right-click on nodes to edit
//
// Revision 1.2  1999-11-26 15:45:18+00  ian_mayo
// making great leaps, using Tree
//
// Revision 1.1  1999-11-26 10:29:22+00  ian_mayo
// Initial revision
//

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PlainPropertyEditor;
import MWC.GUI.Properties.Swing.SwingCustomEditor;
import MWC.GUI.Tools.Chart.RightClickEdit;
import MWC.GUI.Tools.Chart.RightClickEdit.PlottableMenuCreator;
import MWC.TacticalData.NarrativeEntry;

public class SwingLayerManager extends SwingCustomEditor
		implements Layers.DataListener, MWC.GUI.Properties.NoEditorButtons, PlainPropertyEditor.EditorUsesToolParent {
	/**
	 * class which handles the action of show/hide an item
	 */
	protected class ChangeVis implements MWC.GUI.Tools.Action {
		/**
		 * the thing we're operating on
		 */
		private final Plottable _myPlottable;

		/**
		 * the new viz state
		 */
		private final boolean _isVis;

		private final Layer _parent;

		public ChangeVis(final Plottable myPlottable, final boolean isVis, final Layer parentLayer) {
			_isVis = isVis;
			_myPlottable = myPlottable;
			_parent = parentLayer;
		}

		/**
		 * this method calls the 'do' event in the parent tool, passing the necessary
		 * data to it
		 */
		@Override
		public void execute() {
			_myPlottable.setVisible(_isVis);
			fireReformatted();
		}

		private void fireReformatted() {
			// tell the parent that something has been modified
			_myData.fireReformatted(_parent);
		}

		/**
		 * @return boolean flag to indicate whether this action may be redone
		 */
		@Override
		public boolean isRedoable() {
			return true;
		}

		/**
		 * @return boolean flag to describe whether this operation may be undone
		 */
		@Override
		public boolean isUndoable() {
			return true;
		}

		/**
		 * this method calls the 'undo' event in the parent tool, passing the necessary
		 * data to it
		 */
		@Override
		public void undo() {
			_myPlottable.setVisible(!_isVis);
			fireReformatted();
		}
		
		@Override
		public String toString() {
			final String name = _myPlottable.getName();
			return _isVis ? "show " + name : "hide " + name;
		}
	}

	protected class ImmediateEditor extends DefaultTreeCellEditor {
		private final PlottableRenderer renderer;

		public ImmediateEditor(final JTree tree, final PlottableRenderer renderer, final PlottableNodeEditor editor) {
			super(tree, renderer.proxy, editor);
			this.renderer = renderer;
		}

		@Override
		protected boolean canEditImmediately(final EventObject e) {
			boolean rv = false; // rv = return value

			if (e instanceof MouseEvent) {
				final MouseEvent me = (MouseEvent) e;
				rv = inCheckBoxHitRegion(me);
			}
			return rv;
		}

		/**
		 * Configures the editor. Passed onto the <code>realEditor</code>.
		 */
		@Override
		public Component getTreeCellEditorComponent(final JTree tree1, final Object value, final boolean isSelected,
				final boolean expanded, final boolean leaf, final int row) {
			return super.getTreeCellEditorComponent(tree1, value, isSelected, expanded, leaf, row);
		}

		public boolean inCheckBoxHitRegion(final MouseEvent e) {
			boolean rv = false;

			// find the bounds

			// find the bounds for this row item
			final Rectangle bounds = tree.getRowBounds(tree.getClosestRowForLocation(e.getX(), e.getY()));
			final Dimension checkBoxOffset = renderer.getCheckBoxOffset();

			bounds.translate(offset + checkBoxOffset.width, checkBoxOffset.height);
			rv = bounds.contains(e.getPoint());

			return rv;
		}

		@Override
		public boolean shouldSelectCell(final EventObject e) {
			boolean rv = false; // only mouse events

			if (e instanceof MouseEvent) {
				final MouseEvent me = (MouseEvent) e;
				final TreePath path = tree.getPathForLocation(me.getX(), me.getY());

				final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

				rv = node.isLeaf() || !inCheckBoxHitRegion(me);
				rv = false;
			}
			return rv;
		}
	}

	/**
	 * class which combines an item and it's layer into a MutableNode thingy
	 */
	protected class PlottableNode extends DefaultMutableTreeNode {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private Layer _theParentLayer = null;

		private boolean _selected;

		/**
		 * Creates a tree node with no parent, no children, but which allows children,
		 * and initializes it with the specified user object.
		 *
		 * @param userObject an Object provided by the user that constitutes the node's
		 *                   data
		 */
		public PlottableNode(final Object userObject, final Layer parentLayer) {
			super(userObject);
			_theParentLayer = parentLayer;
		}

		public Layer getParentLayer() {
			return _theParentLayer;
		}

		public boolean isSelected() {
			return _selected;
		}

		public void setSelected(final boolean selected) {
			this._selected = selected;
		}
	}

	class PlottableNodeEditor extends AbstractCellEditor implements TreeCellEditor {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		PlottableNodeEditorRenderer renderer;
		DefaultMutableTreeNode lastEditedNode;
		JCheckBox checkBox;

		public PlottableNodeEditor() {
			renderer = new PlottableNodeEditorRenderer();
			checkBox = renderer.getCheckBox();

			checkBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					final Plottable pl = (Plottable) lastEditedNode.getUserObject();
					final PlottableNode pln = (PlottableNode) lastEditedNode;
					changeVisOfThisElement(pl, checkBox.isSelected(), pln.getParentLayer());
					stopCellEditing();
				}
			});
		}

		@Override
		public Object getCellEditorValue() {
			return lastEditedNode.getUserObject();
		}

		@Override
		public Component getTreeCellEditorComponent(final JTree tree, final Object value, final boolean selected,
				final boolean expanded, final boolean leaf, final int row) {
			lastEditedNode = (DefaultMutableTreeNode) value;

			return renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, true); // hasFocus
																											// ignored
		}
	}

	// class PlottableNodeEditorRenderer extends FileNodeRenderer {
	class PlottableNodeEditorRenderer extends PlottableRenderer {
		/**
		 *
		 */
		@SuppressWarnings("unused")
		private static final long serialVersionUID = 1L;

		public JCheckBox getCheckBox() {
			return checkBox;
		}

		@Override
		public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected1,
				final boolean expanded, final boolean leaf, final int row, final boolean hasFocus1) {
			final Component c = super.getTreeCellRendererComponent(tree, value, selected1, expanded, leaf, row,
					hasFocus1);
			proxy.setIcon(null);
			return c;
		}
	}

	/**
	 * embedded class used as a renderer - indicates if each layer is visible
	 */
	private static class PlottableRenderer implements TreeCellRenderer

	{
		/**
		 *
		 */
		@SuppressWarnings("unused")
		private static final long serialVersionUID = 1L;
		protected JCheckBox checkBox = new JCheckBox("");
		private final Component strut = Box.createHorizontalStrut(5);
		private final JPanel panel = new JPanel();
		private int _xOffset = 0;

		javax.swing.tree.DefaultTreeCellRenderer proxy = new DefaultTreeCellRenderer();

		public PlottableRenderer() {
			super();
			panel.setBackground(UIManager.getColor("Tree.textBackground"));
			proxy.setOpaque(false);

			panel.setOpaque(false);
			panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			panel.add(proxy);

			panel.add(strut);
			checkBox.setOpaque(false);
			panel.add(checkBox);

		}

		public Dimension getCheckBoxOffset() {

			return new Dimension(_xOffset, 0);
		}

		@Override
		public Component getTreeCellRendererComponent(final JTree tree, final Object node, final boolean sel,
				final boolean expanded, final boolean leaf, final int row, final boolean hasFocus1) {
			if (node instanceof DefaultMutableTreeNode) {
				final DefaultMutableTreeNode tn = (DefaultMutableTreeNode) node;
				final Object data = tn.getUserObject();
				if (data instanceof MWC.GUI.Plottable) {
					final Plottable pl = (Plottable) tn.getUserObject();
					proxy.getTreeCellRendererComponent(tree, node, sel, expanded, leaf, row, hasFocus1);
					checkBox.setSelected(pl.getVisible());
				}
			}

			panel.doLayout();
			return panel;
		}

		public void paint(final java.awt.Graphics g) {
			proxy.paint(g);

			// get the location of the check box, to check our ticking
			if (g != null) {
				try {
					final FontMetrics fm = g.getFontMetrics();
					_xOffset = fm.stringWidth(proxy.getText()) + strut.getPreferredSize().width;
				} finally {
					// g.dispose();
				}
			}
		}

	} // end of renderer class

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the name we give to the root layer
	 */
	static private final String ROOT_OBJECT = new String("Data");

	/*
	 * thread-safe way of updating UI
	 *
	 */
	private static void updateInThread(final Runnable runner) {
		if (SwingUtilities.isEventDispatchThread()) {
			runner.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						runner.run();
					}
				});
			} catch (final InvocationTargetException e) {
				e.printStackTrace();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * the data we are plotting
	 */
	protected Layers _myData;

	/**
	 * the tree we display
	 */
	protected JTree _myTree;

	/**
	 * the ToolParent we use
	 */
	protected ToolParent _myParent;

	/**
	 * the scroll pane used to display the tree (we have to keep track of this since
	 * it is needed to help us translate a mouse-click screen location into a node
	 * in the tree, since the tree is whoosing around inside the pane).
	 */
	protected javax.swing.JScrollPane _myPane;

	protected Hashtable<?, ?> _myNodes = new Hashtable<String, String>();

	private JPanel btnHolder;

	final private HashMap<Object, Object> treeCache = new HashMap<Object, Object>();

	/**
	 * create a fresh (base) layer, for any old tat
	 */
	protected Layer addLayer() {
		final Layer ly;
		// get the name from the user
		final String s = javax.swing.JOptionPane.showInputDialog(_myTree, "Please enter name", "New Layer",
				javax.swing.JOptionPane.QUESTION_MESSAGE);

		if (s != null && !s.isEmpty()) {
			// check it's not the narratives layer
			if (NarrativeEntry.NARRATIVE_LAYER.equalsIgnoreCase(s)) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						JOptionPane.showMessageDialog(_myTree,
								"Sorry, the name `" + NarrativeEntry.NARRATIVE_LAYER
										+ "` is reserved for narratives.\nPlease choose another layer name",
								"Add layer", JOptionPane.WARNING_MESSAGE);
					}

				});
				ly = null;
			} else {
				// create the layer
				ly = new BaseLayer();
				ly.setName(s);

				// add to the data
				_myData.addThisLayer(ly);
			}
		} else {
			ly = null;
		}
		return ly;

	}

	/**
	 * the user has changed the vis of an item - change the plottable itself, and
	 * inform the layers
	 *
	 * @param pl        the plottable which has been changed
	 * @param isVisible whether it is now visible or not
	 */
	protected void changeVisOfThisElement(final Plottable pl, final boolean isVisible, final Layer parentLayer) {
		pl.setVisible(isVisible);

		// and make the update happen
		_myData.fireReformatted(parentLayer);

		// add it to the undo buffer
		_myParent.addActionToBuffer(new ChangeVis(pl, isVisible, parentLayer));
	}

	public void createAndInitializeTree() {
		// find out which node is currently visible
		final int[] selections = _myTree.getSelectionRows();
		int cur = 0;
		final TreePath selectionTreePath = _myTree.getSelectionPath();
		if (selections != null && selections.length > 0) {
			cur = _myTree.getSelectionRows()[0];
		}

		// create a new root element
		final DefaultMutableTreeNode root = new PlottableNode(ROOT_OBJECT, null);
		// construct the data
		for (int i = 0; i < _myData.size(); i++) {
			final Layer thisL = _myData.elementAt(i);

			root.add(makeLayer(thisL, thisL, treeCache));
		}

		// create a new tree based on this data
		final JTree tmp = new JTree(root);

		// and put the data into our existing tree
		_myTree.setModel(tmp.getModel());

		if (cur != 0) {
			// highlight the existing selection again
			_myTree.expandRow(cur);
			_myTree.setSelectionRow(cur);

		}
		if (selectionTreePath != null) {
			_myTree.expandPath(selectionTreePath);
			_myTree.scrollPathToVisible(selectionTreePath);
			_myTree.setSelectionPath(selectionTreePath);
		}

		// trigger a repaint
		_myTree.invalidate();
	}

	/**
	 * the main data has changed - do a fresh pass
	 */
	@Override
	public void dataExtended(final Layers theData) {
		final Runnable runner = new Runnable() {
			@Override
			public void run() {
				updateData();
			}
		};

		updateInThread(runner);
	}

	/**
	 * the main data has changed - do a fresh pass
	 */
	@Override
	public void dataModified(final Layers theData, final Layer changedLayer) {
		if (changedLayer != null) {
			myUpdateInThread(changedLayer);
		} else {
			final Runnable runner = new Runnable() {
				@Override
				public void run() {
					updateData();
				}
			};
			updateInThread(runner);
		}
	}

	/**
	 * the main data has changed - do a fresh pass
	 */
	@Override
	public void dataReformatted(final Layers theData, final Layer changedLayer) {
		// we do a wierd update here, to force the tree to reset the visibility
		// flags of nodes in the tree
		_myTree.paintImmediately(_myTree.getBounds());
	}

	@Override
	public void doClose() {
		super.doClose();

		// remove us from the data
		_myData.removeDataExtendedListener(this);
		_myData.removeDataModifiedListener(this);
		_myData.removeDataReformattedListener(this);

		// and reset everything else
		_myData = null;
		_myParent = null;
		_myTree.removeAll();
		_myTree = null;
		_myNodes = null;

	}

	/**
	 * reset button has been pressed, process it
	 */
	@Override
	public void doReset() {
		// rescan the tree, of course
		final Runnable runner = new Runnable() {
			@Override
			public void run() {
				updateData();
			}
		};
		updateInThread(runner);
	}

	/**
	 * process a double-click for this tree node
	 */
	protected void editThis(final TreeNode node) {
		if (node instanceof DefaultMutableTreeNode) {
			final DefaultMutableTreeNode tn = (DefaultMutableTreeNode) node;
			final Object data = tn.getUserObject();
			if (data instanceof MWC.GUI.Editable) {
				final Editable editable = (Editable) data;
				if (editable.hasEditor()) {
					// get the toolparent object
					final ToolParent tp = getToolParent();

					// did we get a valid toolparent?
					if (tp != null) {
						// set it to busy
						tp.setCursor(java.awt.Cursor.WAIT_CURSOR);
					}

					// open the editor
					_thePanel.addEditor(editable.getInfo(), null);

					// reset the cursor
					if (tp != null) {
						tp.restoreCursor();
					}
				} // whether this has an editor
			} // if this is editable

			else if (data instanceof String) {
				final String layerName = (String) data;
				final Layer thisL = _myData.findLayer(layerName);
				if (thisL.hasEditor())
					_thePanel.addEditor(thisL.getInfo(), null);
			}
		}
	}

	/**
	 * return the updated data object - not really used.
	 */
	protected Layers getData() {
		return _myData;
	}

	/**
	 * get the top-level layer which contains this node
	 */
	protected Layer getTopLayerFor(final TreeNode node) {
		Layer res = null;

		// we need to remember the "previous" layer before we get to the head, so
		// keep it in this object
		DefaultMutableTreeNode currentData = (DefaultMutableTreeNode) node;

		// we have to walk back upwards until the user data in the parent object is
		// the word "data"
		DefaultMutableTreeNode parentData = (DefaultMutableTreeNode) node.getParent();
		while (!parentData.getUserObject().equals(ROOT_OBJECT)) {
			// remember this (possibly valid) layer
			currentData = parentData;

			// retrieve this parent's layer
			parentData = (DefaultMutableTreeNode) parentData.getParent();
		}

		// so the parentData object was the root - the previous one must be the
		// top-level layer
		res = (Layer) currentData.getUserObject();

		return res;
	}

	protected DefaultMutableTreeNode getTreeNode(final DefaultMutableTreeNode parent, final String nodeText,
			final Object object) {
		final TreeModel model = _myTree.getModel();

		DefaultMutableTreeNode root = null;
		if (parent == null) {
			root = (DefaultMutableTreeNode) model.getRoot();
		} else {
			root = parent;
		}
		DefaultMutableTreeNode child;
		final int childrenCount = root.getChildCount();
		for (int i = 0; i < childrenCount; i++) {
			child = (DefaultMutableTreeNode) root.getChildAt(i);
			if (object == child.getUserObject()) {
				return child;
			}
		}
		return null;
	}

	private TreeNode getTreeNodeConstantTime(final HashMap<Object, Object> _treeCache, final Object node) {
		return (TreeNode) _treeCache.get(node);
	}

	/**
	 * construct the form
	 */

	protected void initForm() {
		initForm(false);
	}

	protected void initForm(final boolean hideLegacyButtons) {
		// set the name
		super.setName("Layer Manager");

		_myTree = new JTree(_myNodes);
		_myTree.setName("Layer Tree");

		final PlottableRenderer renderer = new PlottableRenderer();
		_myTree.setCellRenderer(renderer);
		final PlottableNodeEditor editor = new PlottableNodeEditor();
		_myTree.setCellEditor(new ImmediateEditor(_myTree, renderer, editor));
		_myTree.setEditable(true);
		_myTree.setRowHeight(0);
		// we use double-click to edit a node, so prevent the double-click from
		// opening
		// up a tree node. We do this by indicating that a triple-click is required
		// to open a tree node.
		_myTree.setToggleClickCount(3);

		_myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		_myTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				TreeNode node = null;

				// get the node for this click
				final int row = _myTree.getRowForLocation(e.getX(), e.getY());
				if (row != -1) {
					final TreePath path = _myTree.getPathForRow(row);
					node = (TreeNode) path.getLastPathComponent();
				} else {
					_myTree.clearSelection();
					return;
				}

				// is this a right-click
				if ((e.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0) {
					// did we click on a node?
					if (node == null) {
						// do nothing
						showMenuFor(null, e.getPoint());
					} else {
						// try to get the plottable to represent this
						showMenuFor(node, e.getPoint());
					}
				} else
				// in that case it must be a left click
				{
					// is this a double-click?
					if (e.getClickCount() == 2) {
						if (node != null)
							editThis(node);
					}
				}
			}
		});

		this.setLayout(new BorderLayout());
		_myPane = new JScrollPane(_myTree);
		add(_myPane, java.awt.BorderLayout.CENTER);

		if (!hideLegacyButtons) {
			// do the 'add' button
			final JButton addBtn = new JButton("Add layer");
			addBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					addLayer();
				}
			});

			// do the 'refresh' button
			final JButton refreshBtn = new JButton("Update view");
			refreshBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					doReset();
				}
			});

			btnHolder = new JPanel();
			btnHolder.setLayout(new java.awt.GridLayout(1, 0));
			btnHolder.add(addBtn);
			btnHolder.add(refreshBtn);
			add(btnHolder, java.awt.BorderLayout.NORTH);
		}

	}

	private void loadThisTreeCache(final HashMap<Object, Object> _treeCache, final DefaultMutableTreeNode layer) {
		final int childrenCount = layer.getChildCount();
		for (int i = 0; i < childrenCount; i++) {
			final DefaultMutableTreeNode child = (DefaultMutableTreeNode) layer.getChildAt(i);

			_treeCache.put(child.getUserObject(), child);
			loadThisTreeCache(_treeCache, child);
		}
	}

	private void loadTreeCache(final HashMap<Object, Object> _treeCache) {
		final TreeModel model = _myTree.getModel();

		final DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

		loadThisTreeCache(treeCache, root);
	}

	/**
	 * recursive method to pass through a layer, creating sub-layers for any layers
	 * we find
	 */
	protected DefaultMutableTreeNode makeLayer(final Layer thisLayer, final Layer theTopLayer,
			final HashMap<Object, Object> _treeCache) {
		// create the node
		final DefaultMutableTreeNode thisL = new PlottableNode(thisLayer, theTopLayer);

		// and work through the elements of this layer
		final Enumeration<Editable> enumer = thisLayer.elements();
		if (enumer != null) {
			while (enumer.hasMoreElements()) {
				final Plottable pl = (Plottable) enumer.nextElement();
				if (pl instanceof MWC.GUI.Layer) {
					// hey, let's get recursive!
					final Layer otherLayer = (Layer) pl;
					thisL.add(makeLayer(otherLayer, theTopLayer, _treeCache));
				} else {
					// hey, it's a leaf - just add it
					final PlottableNode newNode = new PlottableNode(pl, theTopLayer);
					thisL.add(newNode);
					_treeCache.put(pl, newNode);
				}
			}
		}
		((DefaultTreeModel) _myTree.getModel()).reload(thisL);
		_treeCache.put(thisLayer, thisL);
		return thisL;
	}

	private void myUpdateInThread(final Layer changedLayer) {
		// in case only the narratives have changed refresh only those.
		final Runnable runner;
		runner = new Runnable() {
			@Override
			public void run() {
				updateThisLayer(changedLayer);
			}
		};
		updateInThread(runner);
	}

	public void resetTree() {
		_myData.clear();
		treeCache.clear();
		createAndInitializeTree();

	}

	protected void setCellEditor(final TreeCellEditor cellEditor) {
		_myTree.setCellEditor(cellEditor);
	}

	protected void setCellRenderer(final TreeCellRenderer cellRenderer) {
		_myTree.setCellRenderer(cellRenderer);
	}

	/**
	 * this is where we receive the data we are plotting, effectively the
	 * constructor
	 */
	@Override
	public void setObject(final Object data) {
		_myData = (Layers) data;

		// add us as a listener to the data
		_myData.addDataExtendedListener(this);
		_myData.addDataModifiedListener(this);
		_myData.addDataReformattedListener(this);

		initForm();

		createAndInitializeTree();
	}

	/**
	 * here's the data
	 *
	 * @param theParent the parent object
	 */
	@Override
	public void setParent(final ToolParent theParent) {
		_myParent = theParent;
	}

	public void showButtonPanel(final boolean show) {
		if (btnHolder != null) {
			btnHolder.setVisible(show);
		}

	}

	/**
	 * show a right-click menu for this node
	 */
	protected void showMenuFor(final TreeNode node, final Point thePoint) {

		Layer parentLayer = null;

		Layer topLayer = null;

		Object data = null;
		Plottable thePlottable = null;
		DefaultMutableTreeNode tn = null;

		// just see if we have a null node (ie the blank area of the layer
		// manager was selected
		if (node == null) {
			data = null;
			thePlottable = null;
		} else {
			// see if our right-click helper is created
			tn = (DefaultMutableTreeNode) node;
			data = tn.getUserObject();
		}

		if (tn != null)
			if (data instanceof MWC.GUI.Plottable) {
				// well, we've found the item to edit, let's sort out its parent
				thePlottable = (Plottable) data;

				topLayer = getTopLayerFor(tn);

				// has the right-click been on the background?
				if (data instanceof MWC.GUI.Layers) {
					parentLayer = null;
					topLayer = null;
				}
				// check if this item is a layer itself
				else if (data instanceof MWC.GUI.Layer) {
					parentLayer = (MWC.GUI.Layer) data;

					// and just check if it is a top-level layer, which must be deleted
					// from
					// the Layers object itself
					if (topLayer == data) {
						// ok, forget the top layer, since we delete it from the Layers
						// object
						topLayer = null;
					} else {
						// so, whilst this is a layer, it isn't a top level one, so we need
						// to
						// identify it's parent

						// find the layer parent for this node
						final DefaultMutableTreeNode pr = (DefaultMutableTreeNode) tn.getParent();

						if (pr.getUserObject() instanceof MWC.GUI.Layer) {
							// the parent is clearly a layer
							parentLayer = (MWC.GUI.Layer) pr.getUserObject();
						} else
							MWC.Utilities.Errors.Trace
									.trace("Failed to find parent layer for:" + tn + ". Please report to maintainer");
					}

				} else {

					// find the layer parent for this node
					final DefaultMutableTreeNode pr = (DefaultMutableTreeNode) tn.getParent();

					if (pr.getUserObject() instanceof MWC.GUI.Layer) {
						// the parent is clearly a layer
						parentLayer = (MWC.GUI.Layer) pr.getUserObject();
					}
				}
			}

		JPopupMenu thePopup = null;

		// see if we have found one!
		if (thePlottable != null) {
			// check we can get a right-click-editor.
			// Note: we may not have one for Debrief-Lite
			final RightClickEdit editor = _myData.getEditor();
			if (editor != null) {
				final java.util.Vector<PlottableMenuCreator> extras = editor.getExtraPlottableEditors(getPanel());
				thePopup = RightClickEdit.createMenuFor(thePlottable, thePoint, parentLayer, _thePanel, _myData, extras,
						topLayer);

			}
		}

		// just check if we are trying paste into layers
		if (node == null) {
			final MWC.GUI.Tools.Operations.RightClickPasteAdaptor pr = new MWC.GUI.Tools.Operations.RightClickPasteAdaptor();

			thePopup = new JPopupMenu();

			pr.createMenu(thePopup, null, thePoint, _thePanel, null, _myData, null);

		}

		if (thePopup != null)
			if (thePopup.getSubElements().length > 0) {
				this.add(thePopup);

				// move the origin of the mouse event (since we're in a scrolling
				// window)
				final Point origin = _myPane.getViewport().getViewPosition();
				thePoint.translate(-origin.x, -origin.y);

				// and now show it
				thePopup.show(this, thePoint.x, thePoint.y);
			}

	}

	/**
	 * have a fresh pass through the data
	 */
	protected void updateData() {
		// find out which node is currently visible
		final int[] selections = _myTree.getSelectionRows();
		int cur = 0;
		if (selections != null && selections.length > 0) {
			cur = _myTree.getSelectionRows()[0];
		}
		// get the root element
		final DefaultMutableTreeNode root = (DefaultMutableTreeNode) _myTree.getModel().getRoot();

		// ok, capture the top level elements
		// capture the children of this layer, since we'll remove any that
		// don't get used
		final HashSet<MutableTreeNode> children = new HashSet<MutableTreeNode>();
		final int kids = root.getChildCount();
		for (int i = 0; i < kids; i++) {
			final TreeNode item = root.getChildAt(i);
			children.add((MutableTreeNode) item);
		}

		// construct the data
		for (int i = 0; i < _myData.size(); i++) {
			final Layer thisL = _myData.elementAt(i);
			final DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getTreeNodeConstantTime(treeCache, thisL);
			if (rootNode == null || rootNode.getParent() == null) {
				root.add(makeLayer(thisL, thisL, treeCache));
				((DefaultTreeModel) _myTree.getModel()).reload();
			} else {
				updateLayer(root, thisL, thisL);
				children.remove(rootNode);
			}
		}
		if (cur != 0) {
			_myTree.setSelectionRow(cur);
		}

		// did we leave any?
		if (!children.isEmpty()) {
			// ok, we've got some stagglers to get rid of
			for (final MutableTreeNode node : children) {
				root.remove(node);
			}
			// reload the tree
			((DefaultTreeModel) _myTree.getModel()).reload(root);
		}

		// trigger a repaint
		_myTree.invalidate();

	}

	protected DefaultMutableTreeNode updateLayer(final DefaultMutableTreeNode root, final Layer thisLayer,
			final Layer theTopLayer) {
		// create the node
		final DefaultMutableTreeNode thisL = (DefaultMutableTreeNode) getTreeNodeConstantTime(treeCache, thisLayer);

		// capture the children of this layer, since we'll remove any that
		// don't get used
		final ArrayList<MutableTreeNode> children = new ArrayList<MutableTreeNode>();
		final int kids = thisL.getChildCount();
		for (int i = 0; i < kids; i++) {
			final TreeNode item = thisL.getChildAt(i);
			children.add((MutableTreeNode) item);
		}

		// we're doing too many layer updates. Keep track of if we need to do it
		boolean needToReloadThisLayer = false;

		final HashSet<TreeNode> reloadedNodes = new HashSet<TreeNode>();

		// and work through the elements of this layer
		final Enumeration<Editable> enumer = thisLayer.elements();
		if (enumer != null) {
			while (enumer.hasMoreElements()) {
				final Plottable pl = (Plottable) enumer.nextElement();
				if (pl instanceof MWC.GUI.Layer) {
					// hey, let's get recursive!
					final Layer otherLayer = (Layer) pl;
					final DefaultMutableTreeNode otherL = (DefaultMutableTreeNode) getTreeNodeConstantTime(treeCache,
							otherLayer);
					if (otherL != null) {
						updateLayer(thisL, otherLayer, theTopLayer);
						children.remove(otherL);
					} else {
						thisL.add(makeLayer(otherLayer, theTopLayer, treeCache));
					}
				} else {
					// hey, it's a leaf - just add it
					final DefaultMutableTreeNode nodeL = (DefaultMutableTreeNode) getTreeNodeConstantTime(treeCache,
							pl);
					if (nodeL == null) {
						final PlottableNode node = new PlottableNode(pl, theTopLayer);
						treeCache.put(pl, node);
						thisL.add(node);
						needToReloadThisLayer = true;
					} else {

						if (nodeL.getParent() == null) {
							// node is not on the tree, so add to thisL, because we are iterating through
							// thisL
							thisL.add(nodeL);
						}
						// reload just that node that was modified
						final TreeNode parent = nodeL.getParent();
						// Lets reload it only once
						if (!reloadedNodes.contains(parent)) {
							reloadedNodes.add(parent);
						}

						// ok, we've used this one
						children.remove(nodeL);
					}
				}
			}
		}

		for (final TreeNode node : reloadedNodes) {
			((DefaultTreeModel) _myTree.getModel()).reload(node);
			// ((DefaultTreeModel) _myTree.getModel()).reload(node);
		}

		if (needToReloadThisLayer) {
			((DefaultTreeModel) _myTree.getModel()).reload(thisL);
		}

		if (!children.isEmpty()) {
			// ok, we've got some stagglers to get rid of
			for (final MutableTreeNode node : children) {
				thisL.remove(node);
			}
			// reload just that node that was modified
			((DefaultTreeModel) _myTree.getModel()).reload(thisL);
		}
		return thisL;
	}

	private void updateThisLayer(final Layer changedLayer) {
		loadTreeCache(treeCache);
		final TreeNode treeNode = getTreeNodeConstantTime(treeCache, changedLayer);
		if (treeNode != null) {
			updateLayer((DefaultMutableTreeNode) _myTree.getModel().getRoot(), changedLayer, changedLayer);
		}
	}

}
