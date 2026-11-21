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

//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996, 1996 Borland International, Inc. All Rights Reserved.
//
//--------------------------------------------------------------------------------------------------

package MWC.GUI.SplitPanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * A Pane Layout Manager
 *
 * Maintains a binary tree of child components. First split divides the whole
 * space horizontally or vertically, next split divides these splits further.
 *
 * Components can be added either in a default (and not very useful) way by
 * using add(). This method traverses the right hand nodes of the layout tree
 * until it encounters a leaf. This leaf is then split 50:50 in the opposite way
 * from its parent and the new node inserted.
 *
 * The other way to add components is to specifiy a PaneConstaints object which
 * specifies the component to split, whether to split horizontally and the
 * proportion of the space to take from the component that is being split
 *
 * When the window is resized, all the proportions are maintained as best as
 * possible.
 *
 * Programmatic modifictaion of the proportions at runtime are accomplished via
 * setConstraints(Component, PaneConstaint) .
 *
 * For a UI to modifify proportions, use borland.jbcl.control.SplitPanel.
 *
 * Further work: - implement docking of windows (at present they are just zero
 * width) - implement undocking of windows into different orders - improve the
 * message handling to get sharper performance - may ultimately need a more
 * complex alogrithm - current binary tree may look a little wacky under some
 * conditions
 *
 * @version 1.0, 25 Sept 1995
 * @author David Williams
 */
public class PaneLayout implements LayoutManager2 {
	PaneNode rootNode; // Root node of the layout tree
	// Rectangle dividerRect;
	// Container c;
	PaneNode lastSelected; // the last Selected node; used by Split
	private PaneNode lastDeletion; // the last removed component so we can repair
									// the tree if the user removes and then re-adds
									// instead of using setConstraints
	int gap = 0; // SplitPanel sets this to 2
	private String lastComponentAdded;
	private int addCount = 0; // number of components added without a constraint object

	/**
	 * Constructor
	 */
	public PaneLayout() {
	}

	/**
	 * (Internal) Adds a child to the layout into a default position
	 */
	void addChild(final Component c, final float proportion) {
		addCount++;
		final String name = "component" + addCount; // NORES
		// System.err.println("PanelLayout.addChild naming component " + name);
		if (rootNode == null)
			rootNode = new PaneNode(name, c, PaneConstraints.TOP);
		else
			rootNode.addChild(name, c, PaneConstraints.BOTTOM, proportion); // NORES
		lastComponentAdded = name;
	}

	/**
	 * (Internal) Adds a child into the layout by splitting the specified component
	 * (assumed to be already present) in the manner specified (ie horizontal or
	 * vertical)
	 */
	void addChild(final String name, final String splitComponentName, final String position,
			final Component newComponent, final float proportion) {
		if (rootNode == null) {
			// System.err.println("adding " + name + " as the root node");
			rootNode = new PaneNode(name, newComponent, PaneConstraints.TOP);
		} else {
			boolean foundSplitComponent = true;
			if (splitComponentName == null || splitComponentName.length() == 0) {
				// System.err.println("split with " + lastComponentAdded + " instead");
				foundSplitComponent = rootNode.addChildSplit(name, lastComponentAdded, position, newComponent,
						proportion);
			} else if (!rootNode.addChildSplit(name, splitComponentName, position, newComponent, proportion))
				foundSplitComponent = rootNode.addChildSplit(name, lastComponentAdded, position, newComponent,
						proportion);
			if (!foundSplitComponent) {
				// System.err.println("addChild never found " + splitComponentName);
				rootNode.addChild(name, newComponent, position, proportion);
			}
		}
	}

	/**
	 * add the specified component using the supplied PaneConstraints
	 */
	@Override
	public void addLayoutComponent(final Component newComponent, final Object constraints) {
		// try {
		// String s=null;
		// s.substring(0);
		// }
		// catch (Exception e) {MWC.Utilities.Errors.Trace.trace(e);}
		// System.err.println("adding Component" + constraints);
		if (constraints instanceof PaneConstraints) {
			if (justDeleted(newComponent, (PaneConstraints) constraints)) {
				// it was just deleted: justDeleted() has put it back
			} else if (setComponentConstraints(newComponent, (PaneConstraints) constraints)) { // make sure it has not
																								// already been added
				// System.err.println("this is ok; " + ((PaneConstraints)constraints).name + "
				// already in containter");
				lastDeletion = null;
				return;
			} else {
				final PaneConstraints paneConstraints = (PaneConstraints) constraints;
				// System.err.println("adding " + paneConstraints.name);
				addChild(paneConstraints.name, paneConstraints.splitComponentName, paneConstraints.position,
						newComponent, paneConstraints.proportion);
				lastComponentAdded = paneConstraints.name;
			}
		} else
			// if (!(newComponent instanceof PaneLayoutDivider))
			addChild(newComponent, 0.5f);
		lastDeletion = null;
		// System.err.println("added" + getConstraints(newComponent));
	}

	@Override
	public void addLayoutComponent(final String name, final Component comp) {
		final PaneConstraints pc = new PaneConstraints(name, "", "", 0.5f);
		addLayoutComponent(comp, pc);
	}

	/**
	 * (SplitPanel specific) Move the divider the amount specified One paramter will
	 * be zero
	 */
	public void dragDivider(final int x, final int y) {
		if (lastSelected != null) {
			// System.err.println("Drag" + lastSelected.childNodeA.location +
			// lastSelected.childNodeB.location + lastSelected.location);
			lastSelected.drag(x, y);
		}
	}

	/**
	 * Returns an array of the components in an order that will work called by the
	 * UI Designer's PaneLayoutAssistant after components have been moved or deleted
	 */
	public String[] getAddOrder(final Container parent) {
		final Component[] componentArray = parent.getComponents(); // just to get the correct size array
		final String[] componentNames = new String[componentArray.length];
		final Point subscript = new Point(0, 0); // wrap int in an object whose value the called methods can tweak
		if (rootNode != null) {
			if (rootNode.childComponent == null)
				rootNode.getComponents(subscript, componentNames, true);
			else
				componentNames[0] = rootNode.name;
		}
		return componentNames;
	}

	// ------------------------------------------------------------------------------------------------
	// LayoutManager Methods
	// ------------------------------------------------------------------------------------------------

	/**
	 * returns the PaneConstraints for the specified component
	 */
	public PaneConstraints getConstraints(final Component comp) {
		PaneConstraints constraints = null;
		if (rootNode != null) {
			final PaneNode node = rootNode.getParentNode(comp, null);
			if (node != null) {
				if (node.childComponent == comp)
					constraints = new PaneConstraints(node.name, node.name, "Root", 0.5f); // NORES
				else {
					final float proportion = node.heightDivide * node.widthDivide;
					final String splitComponentName = node.childNodeA.getNodeAComponent();
					String name = node.childNodeB.getNodeAComponent();
					if (node.childNodeA.childComponent == comp)
						name = node.childNodeB.name;
					if (node.horizontal)
						if (node.reverse)
							constraints = new PaneConstraints(name, splitComponentName, PaneConstraints.TOP,
									1.0f - proportion);
						else
							constraints = new PaneConstraints(name, splitComponentName, PaneConstraints.BOTTOM,
									1.0f - proportion);
					else if (node.reverse)
						constraints = new PaneConstraints(name, splitComponentName, PaneConstraints.LEFT,
								1.0f - proportion);
					else
						constraints = new PaneConstraints(name, splitComponentName, PaneConstraints.RIGHT,
								1.0f - proportion);
					if (name.equals(splitComponentName))
						constraints.position = "Root"; // NORES
				}
			}
		}
		// System.err.println("PaneLayout.getConstraints for " + constraints);
		return constraints;
	}

	/**
	 * (SplitPanel specific) returns the rectangle that the divider can be moved in
	 */
	public Rectangle getDividerBounds() {
		if (lastSelected != null) {
			// System.err.println("Bounds" + lastSelected.childNodeA.location +
			// lastSelected.childNodeB.location + lastSelected.location);
			return lastSelected.location;
		}
		return null;
	}

	/**
	 * (SplitPanel specific) determines which divider contains the supplied point
	 * and returns that dividers rectangle
	 */
	public Rectangle getDividerRect(final int x, final int y) {
		if (rootNode != null)
			lastSelected = rootNode.hitTest(x, y, gap * 2);
		if (lastSelected != null) {
			// System.err.println("Divides" + lastSelected.childNodeA.location +
			// lastSelected.childNodeB.location + lastSelected.location);
			return lastSelected.getDividerRect(gap * 2);
		}
		return null;
	}

	/**
	 * returns the gap size
	 */
	public int getGap() {
		return gap;
	}

	@Override
	public float getLayoutAlignmentX(final Container parm1) {
		return 0.5f;
	}

	@Override
	public float getLayoutAlignmentY(final Container parm1) {
		return 0.5f;
	}

	@Override
	public void invalidateLayout(final Container parm1) {
	}

	/**
	 * (Internal) helper function for addLayoutComponent checks to see if the
	 * newComponent was just removed from the layout and if it was, places it back
	 * were it once belonged. The UI designer does this (removeLayoutComponent
	 * immediately followed by AddLayoutComponent) because the preferred method,
	 * setConstraints, is not part of the LayoutManager2 interface
	 */
	boolean justDeleted(final Component newComponent, final PaneConstraints constraints) {
		if (lastDeletion == null)
			return false;
		if (lastDeletion.childComponent == newComponent) {
			if (lastDeletion.childNodeA == null) {
				final PaneNode node = lastDeletion.childNodeB;
				if (node == null) {
					// System.err.println("justDeleted: node is null");
					return false;
				}
				// System.err.println(constraints.splitComponentName +"," +
				// node.getNodeAComponent());
				if (constraints.splitComponentName.equals(node.getNodeAComponent())
						|| !lastDeletion.name.equals(constraints.name)) {
					// System.err.println("lastDeletion restored as nodeB " +constraints.name);
					final PaneNode newNode = new PaneNode(node.childNodeA, node.childNodeB, "", 0.5f);
					newNode.widthDivide = node.widthDivide;
					newNode.heightDivide = node.heightDivide;
					newNode.name = node.name;
					lastDeletion.name = constraints.name;
					newNode.childComponent = node.childComponent;
					newNode.reverse = node.reverse;
					newNode.horizontal = node.horizontal;
					node.childNodeA = newNode;
					node.childNodeB = lastDeletion;
					node.name = null;
					node.childComponent = null;
					lastDeletion = null;
					setConstraints(newComponent, constraints);
					return true;
				}
			} else {
				// System.err.println("lastDeletion restored as nodeA " +constraints.name);
				final PaneNode node = lastDeletion.childNodeA;
				// System.err.println(constraints.splitComponentName +"," +
				// node.getNodeAComponent());
				// verify that split component constraint did not change
				// if (constraints.splitComponentName.equals(node.getNodeAComponent())) {
				final PaneNode newNode = new PaneNode(node.childNodeA, node.childNodeB, "", 0.5f);
				newNode.widthDivide = node.widthDivide;
				newNode.heightDivide = node.heightDivide;
				newNode.name = node.name;
				lastDeletion.name = constraints.name;
				newNode.childComponent = node.childComponent;
				newNode.reverse = node.reverse;
				newNode.horizontal = node.horizontal;
				node.childNodeA = lastDeletion;
				node.childNodeB = newNode;
				node.name = null;
				node.childComponent = null;
				lastDeletion = null;
				setConstraints(newComponent, constraints);
				return true;
				// }
			}
			// System.err.println("justdeleted = false");
			lastDeletion = null;
		}
		return false;
	}

	/**
	 * Lays out the container in the specified Container. a LayoutManager Interface
	 * method
	 *
	 * @param parent the specified component being laid out
	 * @see Container
	 */
	@Override
	public void layoutContainer(final Container parent) {
		// System.err.println("PaneLayout.layoutContainer");
		if (rootNode != null) {
			final Dimension d = parent.getSize();
			final Rectangle location = new Rectangle(0, 0, d.width, d.height);
			rootNode.assertLocation(location, gap);
		}
	}

	// never figured out what this was for, cause I don't use it anymore
	// public void deselectAll() {
	// if (rootNode != null)
	// rootNode.deselectAll();
	// }

	@Override
	public Dimension maximumLayoutSize(final Container parm1) {
		return new Dimension(500, 500);
	}

	// mark the node that this divider divides as needing re-layout
	// (the layout remembers the lastSelected PaneNode instead)
	// public void select(boolean shift, int x, int y) {
	// if (rootNode != null)
	// rootNode.select(shift,x,y,gap);
	// }

	/**
	 * Returns the minimum dimensions needed to layout the components contained in
	 * the specified panel. a LayoutManager Interface method
	 *
	 * @param parent the component which needs to be laid out
	 * @see #getPreferredSize
	 */
	@Override
	public Dimension minimumLayoutSize(final Container parent) {
		return preferredLayoutSize(parent);
	}

	// ------------------------------------------------------------------------------------------------
	// LayoutManager2 methods
	// ------------------------------------------------------------------------------------------------

	/**
	 * Returns the preferred dimensions for this layout given the components in the
	 * specified Container. a LayoutManager Interface method
	 *
	 * @param parent the component which needs to be laid out
	 * @see #getMinimumSize
	 */
	@Override
	public Dimension preferredLayoutSize(final Container parent) {
		if (rootNode != null) {
			final Dimension d = rootNode.getPreferredSize(gap);
			// System.err.println("PaneLayout.preferredLayoutSize returns:" + d);
			return d;
		}
		// System.err.println("PaneLayout.preferredLayoutSize- no components");
		return new Dimension(10, 10);
	}

	/**
	 * Removes the specified node from the layout - although not from the container
	 * itself a LayoutManager Interface method
	 */
	@Override
	public void removeLayoutComponent(final Component comp) {
		if (comp == null) {
			lastDeletion = null;
			return;
		}
		if (rootNode != null) {
			// System.err.println("removing " + getConstraints(comp));
			if (rootNode.childComponent == comp)
				rootNode = null;
			else {
				final PaneNode parent = rootNode.getImmediateParent(comp);
				if (parent != null) {
					// System.err.println(parent.childNodeA.name + "-" +parent.childNodeB.name);
					lastDeletion = parent.removeChild(comp);
				}
			}
		}
	}

	/**
	 * (Internal) implementation of setConstraints returns false if component is not
	 * found
	 */
	private boolean setComponentConstraints(final Component child, final PaneConstraints constraints) {
		if (rootNode == null) {
			// System.err.println("PaneLayout.setComponentConstraints: setting root node to
			// " + constraints.name);
			rootNode = new PaneNode(constraints.name, child, PaneConstraints.ROOT);
		} else {
			final PaneNode node = rootNode.getParentNode(child, null);
			if (node != null) {
				node.setConstraints(child, constraints);
				// System.err.println("PaneLayout.setComponentConstraints:");
				return true;
			}
			// System.err.println("PaneLayout.setComponentConstraints: did not find the
			// component " + constraints.name);
			return false;
		}
		return true;
	}

	/**
	 * set the PaneConstraints for the specified component
	 * paneConstraints.splitComponent is ignored the components are not redrawn
	 * until layoutContainer is called
	 */
	public void setConstraints(final Component child, final PaneConstraints constraints) {
		// System.err.println("PaneLayout.setConstraints " + constraints);
		setComponentConstraints(child, constraints);
		lastDeletion = null;
	}

	/**
	 * The number of pixels that will surround each component the actual size of a
	 * divider will be twice this number SplitPanel uses this area to allow the user
	 * to change the relative sizes of the components by clicking and dragging on
	 * this area. Default is 0.
	 */
	public void setGap(final int gap) {
		this.gap = gap;
	}

	/**
	 * Returns the String representation of this PaneLayout's values.
	 */
	@Override
	public String toString() {
		return "PaneLayout"; // NORES
	}
}
