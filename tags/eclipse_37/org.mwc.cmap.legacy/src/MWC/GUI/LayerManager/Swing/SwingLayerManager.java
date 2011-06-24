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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.TreeCellEditor;
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

public class SwingLayerManager extends SwingCustomEditor implements
		Layers.DataListener, MWC.GUI.Properties.NoEditorButtons,
		PlainPropertyEditor.EditorUsesToolParent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////
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
	 * the scroll pane used to display the tree (we have to keep track of this
	 * since it is needed to help us translate a mouse-click screen location into
	 * a node in the tree, since the tree is whoosing around inside the pane).
	 */
	protected javax.swing.JScrollPane _myPane;

	protected Hashtable<?, ?> _myNodes = new Hashtable<String, String>();

	/**
	 * the name we give to the root layer
	 */
	static private final String ROOT_OBJECT = new String("Data");

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	/**
	 * return the updated data object - not really used.
	 */
	protected Layers getData()
	{
		return _myData;
	}

	/**
	 * construct the form
	 */
	protected void initForm()
	{
		// set the name
		super.setName("Layer Manager");

		_myTree = new JTree(_myNodes);
		_myTree.setName("Layer Tree");

		final PlottableRenderer renderer = new PlottableRenderer();
		_myTree.setCellRenderer(renderer);
		PlottableNodeEditor editor = new PlottableNodeEditor();
		_myTree.setCellEditor(new ImmediateEditor(_myTree, renderer, editor));
		_myTree.setEditable(true);

		// we use double-click to edit a node, so prevent the double-click from
		// opening
		// up a tree node. We do this by indicating that a triple-click is required
		// to open a tree node.
		_myTree.setToggleClickCount(3);

		_myTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		_myTree.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				TreeNode node = null;

				// get the node for this click
				int row = _myTree.getRowForLocation(e.getX(), e.getY());
				if (row != -1)
				{
					TreePath path = _myTree.getPathForRow(row);
					node = (TreeNode) path.getLastPathComponent();
				}

				// is this a right-click
				if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
				{
					// did we click on a node?
					if (node == null)
					{
						// do nothing
						showMenuFor(null, e.getPoint());
					}
					else
					{
						// try to get the plottable to represent this
						showMenuFor(node, e.getPoint());
					}
				}
				else
				// in that case it must be a left click
				{
					// is this a double-click?
					if (e.getClickCount() == 2)
					{
						if (node != null)
							editThis(node);
					}
				}
			}
		});

		this.setLayout(new BorderLayout());
		_myPane = new JScrollPane(_myTree);
		add(_myPane, java.awt.BorderLayout.CENTER);

		// do the 'add' button
		JButton addBtn = new JButton("Add layer");
		addBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addLayer();
			}
		});

		// do the 'refresh' button
		JButton refreshBtn = new JButton("Update view");
		refreshBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				doReset();
			}
		});

		JPanel btnHolder = new JPanel();
		btnHolder.setLayout(new java.awt.GridLayout(1, 0));
		btnHolder.add(addBtn);
		btnHolder.add(refreshBtn);
		add(btnHolder, java.awt.BorderLayout.NORTH);

	}

	/**
	 * get the top-level layer which contains this node
	 */
	protected Layer getTopLayerFor(TreeNode node)
	{
		Layer res = null;

		// we need to remember the "previous" layer before we get to the head, so
		// keep it in this object
		DefaultMutableTreeNode currentData = (DefaultMutableTreeNode) node;

		// we have to walk back upwards until the user data in the parent object is
		// the word "data"
		DefaultMutableTreeNode parentData = (DefaultMutableTreeNode) node
				.getParent();
		while (!parentData.getUserObject().equals(ROOT_OBJECT))
		{
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

	/**
	 * show a right-click menu for this node
	 */
	protected void showMenuFor(TreeNode node, Point thePoint)
	{

		Layer parentLayer = null;

		Layer topLayer = null;

		Object data = null;
		Plottable thePlottable = null;
		DefaultMutableTreeNode tn = null;

		// just see if we have a null node (ie the blank area of the layer
		// manager was selected
		if (node == null)
		{
			data = null;
			thePlottable = null;
		}
		else
		{
			// see if our right-click helper is created
			tn = (DefaultMutableTreeNode) node;
			data = tn.getUserObject();
		}

		if (tn != null)
			if (data instanceof MWC.GUI.Plottable)
			{
				// well, we've found the item to edit, let's sort out its parent
				thePlottable = (Plottable) data;

				topLayer = getTopLayerFor(tn);

				// has the right-click been on the background?
				if (data instanceof MWC.GUI.Layers)
				{
					parentLayer = null;
					topLayer = null;
				}
				// check if this item is a layer itself
				else if (data instanceof MWC.GUI.Layer)
				{
					parentLayer = (MWC.GUI.Layer) data;

					// and just check if it is a top-level layer, which must be deleted
					// from
					// the Layers object itself
					if (topLayer == data)
					{
						// ok, forget the top layer, since we delete it from the Layers
						// object
						topLayer = null;
					}
					else
					{
						// so, whilst this is a layer, it isn't a top level one, so we need
						// to
						// identify it's parent

						// find the layer parent for this node
						DefaultMutableTreeNode pr = (DefaultMutableTreeNode) tn.getParent();

						if (pr.getUserObject() instanceof MWC.GUI.Layer)
						{
							// the parent is clearly a layer
							parentLayer = (MWC.GUI.Layer) pr.getUserObject();
						}
						else
							MWC.Utilities.Errors.Trace
									.trace("Failed to find parent layer for:" + tn
											+ ". Please report to maintainer");
					}

				}
				else
				{

					// find the layer parent for this node
					DefaultMutableTreeNode pr = (DefaultMutableTreeNode) tn.getParent();

					if (pr.getUserObject() instanceof MWC.GUI.Layer)
					{
						// the parent is clearly a layer
						parentLayer = (MWC.GUI.Layer) pr.getUserObject();
					}
				}
			}

		JPopupMenu thePopup = null;

		// see if we have found one!
		if (thePlottable != null)
		{
			java.util.Vector<PlottableMenuCreator> extras = _myData.getEditor()
					.getExtraPlottableEditors(getPanel());
			thePopup = RightClickEdit.createMenuFor(thePlottable, thePoint,
					getChart().getCanvas(), parentLayer, _thePanel, _myData, extras,
					topLayer);

		}

		// just check if we are trying paste into layers
		if (node == null)
		{
			MWC.GUI.Tools.Operations.RightClickPasteAdaptor pr = new MWC.GUI.Tools.Operations.RightClickPasteAdaptor();

			thePopup = new JPopupMenu();

			pr.createMenu(thePopup, null, thePoint, getChart().getCanvas(),
					_thePanel, null, _myData, null);

		}

		if (thePopup != null)
			if (thePopup.getSubElements().length > 0)
			{
				this.add(thePopup);

				// move the origin of the mouse event (since we're in a scrolling
				// window)
				Point origin = _myPane.getViewport().getViewPosition();
				thePoint.translate(-origin.x, -origin.y);

				// and now show it
				thePopup.show(this, thePoint.x, thePoint.y);
			}

	}

	/**
	 * process a double-click for this tree node
	 */
	protected void editThis(TreeNode node)
	{
		if (node instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode tn = (DefaultMutableTreeNode) node;
			Object data = tn.getUserObject();
			if (data instanceof MWC.GUI.Editable)
			{
				Editable editable = (Editable) data;
				if (editable.hasEditor())
				{
					// get the toolparent object
					ToolParent tp = getToolParent();

					// did we get a valid toolparent?
					if (tp != null)
					{
						// set it to busy
						tp.setCursor(java.awt.Cursor.WAIT_CURSOR);
					}

					// open the editor
					_thePanel.addEditor(editable.getInfo(), null);

					// reset the cursor
					if (tp != null)
					{
						tp.restoreCursor();
					}
				} // whether this has an editor
			} // if this is editable

			else if (data instanceof String)
			{
				String layerName = (String) data;
				Layer thisL = _myData.findLayer(layerName);
				if (thisL.hasEditor())
					_thePanel.addEditor(thisL.getInfo(), null);
			}
		}
	}

	/**
	 * this is where we receive the data we are plotting, effectively the
	 * constructor
	 */
	public void setObject(Object data)
	{
		_myData = (Layers) data;

		// add us as a listener to the data
		_myData.addDataExtendedListener(this);
		_myData.addDataModifiedListener(this);
		_myData.addDataReformattedListener(this);

		initForm();

		updateData();
	}

	/**
	 * the user has changed the vis of an item - change the plottable itself, and
	 * inform the layers
	 * 
	 * @param pl
	 *          the plottable which has been changed
	 * @param isVisible
	 *          whether it is now visible or not
	 */
	void changeVisOfThisElement(Plottable pl, boolean isVisible, Layer parentLayer)
	{
		pl.setVisible(isVisible);

		// and make the update happen
		_myData.fireReformatted(parentLayer);

		// add it to the undo buffer
		_myParent.addActionToBuffer(new ChangeVis(pl, isVisible));
	}

	/**
	 * recursive method to pass through a layer, creating sub-layers for any
	 * layers we find
	 */
	protected DefaultMutableTreeNode makeLayer(Layer thisLayer, Layer theTopLayer)
	{
		// create the node
		DefaultMutableTreeNode thisL = new PlottableNode(thisLayer, theTopLayer);

		// and work through the elements of this layer
		Enumeration<Editable> enumer = thisLayer.elements();
		if (enumer != null)
		{
			while (enumer.hasMoreElements())
			{
				Plottable pl = (Plottable) enumer.nextElement();
				if (pl instanceof MWC.GUI.Layer)
				{
					// hey, let's get recursive!
					Layer otherLayer = (Layer) pl;
					thisL.add(makeLayer(otherLayer, theTopLayer));
				}
				else
				{
					// hey, it's a leaf - just add it
					thisL.add(new PlottableNode(pl, theTopLayer));
				}
			}
		}

		return thisL;
	}

	/**
	 * have a fresh pass through the data
	 */
	protected void updateData()
	{
		// find out which node is currently visible
		int[] selections = _myTree.getSelectionRows();
		int cur = 0;
		if (selections != null)
			cur = _myTree.getSelectionRows()[0];

		// create a new root element
		DefaultMutableTreeNode root = new PlottableNode(ROOT_OBJECT, null);

		// construct the data
		for (int i = 0; i < _myData.size(); i++)
		{
			Layer thisL = _myData.elementAt(i);

			root.add(makeLayer(thisL, thisL));
		}

		// create a new tree based on this data
		JTree tmp = new JTree(root);

		// and put the data into our existing tree
		_myTree.setModel(tmp.getModel());

		// highlight the existing selection again
		_myTree.setSelectionRow(cur);

		// trigger a repaint
		_myTree.invalidate();

	}

	/**
	 * the main data has changed - do a fresh pass
	 */
	public void dataModified(Layers theData, Layer changedLayer)
	{
		updateData();
	}

	/**
	 * the main data has changed - do a fresh pass
	 */
	public void dataExtended(Layers theData)
	{
		updateData();
	}

	/**
	 * the main data has changed - do a fresh pass
	 */
	public void dataReformatted(Layers theData, Layer changedLayer)
	{
		// we do a wierd update here, to force the tree to reset the visibility
		// flags of nodes in the tree
		_myTree.paintImmediately(_myTree.getBounds());
	}

	/**
	 * create a fresh (base) layer, for any old tat
	 */
	protected void addLayer()
	{
		// get the name from the user
		String s = javax.swing.JOptionPane.showInputDialog(_myTree,
				"Please enter name", "New Layer",
				javax.swing.JOptionPane.QUESTION_MESSAGE);

		if (s != null)
		{
			// create the layer
			Layer ly = new BaseLayer();
			ly.setName(s);

			// add to the data
			_myData.addThisLayer(ly);

			// the layers object should inform us of any update, anyway

		}

	}

	/**
	 * reset button has been pressed, process it
	 */
	public void doReset()
	{
		// rescan the tree, of course
		updateData();
	}

	public void doClose()
	{
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
	 * here's the data
	 * 
	 * @param theParent
	 *          the parent object
	 */
	public void setParent(ToolParent theParent)
	{
		_myParent = theParent;
	}

	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	/**
	 * embedded class used as a renderer - indicates if each layer is visible
	 */
	private static class PlottableRenderer extends
			javax.swing.tree.DefaultTreeCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected JCheckBox checkBox = new JCheckBox("");
		private Component strut = Box.createHorizontalStrut(5);
		private JPanel panel = new JPanel();
		private int _xOffset = 0;

		public void paint(java.awt.Graphics g)
		{
			super.paint(g);

			// get the location of the check box, to check our ticking
			if (g != null)
			{
				try
				{
					FontMetrics fm = g.getFontMetrics();
					_xOffset = fm.stringWidth(getText()) + strut.getPreferredSize().width;
				}
				finally
				{
					// g.dispose();
				}
			}
		}

		public PlottableRenderer()
		{
			super();
			panel.setBackground(UIManager.getColor("Tree.textBackground"));
			setOpaque(false);
			checkBox.setOpaque(false);
			panel.setOpaque(false);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			panel.add(this);
			panel.add(strut);
			panel.add(checkBox);

		}

		public Component getTreeCellRendererComponent(JTree tree, Object node,
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus1)
		{
			if (node instanceof DefaultMutableTreeNode)
			{
				DefaultMutableTreeNode tn = (DefaultMutableTreeNode) node;
				final Object data = tn.getUserObject();
				if (data instanceof MWC.GUI.Plottable)
				{
					final Plottable pl = (Plottable) tn.getUserObject();
					super.getTreeCellRendererComponent(tree, node, sel, expanded, leaf,
							row, hasFocus1);

					checkBox.setSelected(pl.getVisible());
				}
			}

			return panel;
		}

		public Dimension getCheckBoxOffset()
		{

			return new Dimension(_xOffset, 0);
		}

	} // end of renderer class

	// ////////////////////////////////////////////////
	//
	// ////////////////////////////////////////////////

	class ImmediateEditor extends DefaultTreeCellEditor
	{
		private PlottableRenderer renderer;

		public ImmediateEditor(JTree tree, PlottableRenderer renderer,
				PlottableNodeEditor editor)
		{
			super(tree, renderer, editor);
			this.renderer = renderer;
		}

		/**
		 * Configures the editor. Passed onto the <code>realEditor</code>.
		 */
		public Component getTreeCellEditorComponent(JTree tree1, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row)
		{
			return super.getTreeCellEditorComponent(tree1, value, isSelected,
					expanded, leaf, row);
		}

		protected boolean canEditImmediately(EventObject e)
		{
			boolean rv = false; // rv = return value

			if (e instanceof MouseEvent)
			{
				MouseEvent me = (MouseEvent) e;
				rv = inCheckBoxHitRegion(me);
			}
			return rv;
		}

		public boolean shouldSelectCell(EventObject e)
		{
			boolean rv = false; // only mouse events

			if (e instanceof MouseEvent)
			{
				MouseEvent me = (MouseEvent) e;
				TreePath path = tree.getPathForLocation(me.getX(), me.getY());

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();

				rv = node.isLeaf() || !inCheckBoxHitRegion(me);
				rv = false;
			}
			return rv;
		}

		public boolean inCheckBoxHitRegion(MouseEvent e)
		{
			boolean rv = false;

			// find the bounds

			// find the bounds for this row item
			Rectangle bounds = tree.getRowBounds(tree.getClosestRowForLocation(e
					.getX(), e.getY()));
			Dimension checkBoxOffset = renderer.getCheckBoxOffset();

			bounds.translate(offset + checkBoxOffset.width, checkBoxOffset.height);
			rv = bounds.contains(e.getPoint());

			return rv;
		}
	}

	class PlottableNodeEditor extends AbstractCellEditor implements
			TreeCellEditor
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		PlottableNodeEditorRenderer renderer;
		DefaultMutableTreeNode lastEditedNode;
		JCheckBox checkBox;

		public PlottableNodeEditor()
		{
			renderer = new PlottableNodeEditorRenderer();
			checkBox = renderer.getCheckBox();

			checkBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Plottable pl = (Plottable) lastEditedNode.getUserObject();
					PlottableNode pln = (PlottableNode) lastEditedNode;
					changeVisOfThisElement(pl, checkBox.isSelected(), pln
							.getParentLayer());
					stopCellEditing();
				}
			});
		}

		public Component getTreeCellEditorComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row)
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

	// class PlottableNodeEditorRenderer extends FileNodeRenderer {
	class PlottableNodeEditorRenderer extends PlottableRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected1, boolean expanded, boolean leaf, int row,
				boolean hasFocus1)
		{
			Component c = super.getTreeCellRendererComponent(tree, value, selected1,
					expanded, leaf, row, hasFocus1);
			setIcon(null);
			return c;
		}

		public JCheckBox getCheckBox()
		{
			return checkBox;
		}
	}

	/**
	 * class which combines an item and it's layer into a MutableNode thingy
	 */
	private class PlottableNode extends javax.swing.tree.DefaultMutableTreeNode
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Layer _theParentLayer = null;

		/**
		 * Creates a tree node with no parent, no children, but which allows
		 * children, and initializes it with the specified user object.
		 * 
		 * @param userObject
		 *          an Object provided by the user that constitutes the node's data
		 */
		public PlottableNode(Object userObject, Layer parentLayer)
		{
			super(userObject);
			_theParentLayer = parentLayer;
		}

		public Layer getParentLayer()
		{
			return _theParentLayer;
		}
	}

	/**
	 * class which handles the action of show/hide an item
	 */
	private class ChangeVis implements MWC.GUI.Tools.Action
	{
		// ////////////////////////////////////////////////
		// member objects
		// ////////////////////////////////////////////////

		/**
		 * the thing we're operating on
		 */
		private Plottable _myPlottable;

		/**
		 * the new viz state
		 */
		private boolean _isVis;

		// ////////////////////////////////////////////////
		// constructor
		// ////////////////////////////////////////////////
		public ChangeVis(Plottable myPlottable, boolean isVis)
		{
			_isVis = isVis;
			_myPlottable = myPlottable;
		}

		// ////////////////////////////////////////////////
		// member methods
		// ////////////////////////////////////////////////

		/**
		 * this method calls the 'do' event in the parent tool, passing the
		 * necessary data to it
		 */
		public void execute()
		{
			_myPlottable.setVisible(_isVis);
		}

		/**
		 * @return boolean flag to indicate whether this action may be redone
		 */
		public boolean isRedoable()
		{
			return true;
		}

		/**
		 * @return boolean flag to describe whether this operation may be undone
		 */
		public boolean isUndoable()
		{
			return true;
		}

		/**
		 * this method calls the 'undo' event in the parent tool, passing the
		 * necessary data to it
		 */
		public void undo()
		{
			_myPlottable.setVisible(!_isVis);
		}
	}

}
