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

class PaneNode {
	// public final static int Border = 3;

	// Contents: either one Component or two PaneNodes
	// Determined by the presence or absense of a value in
	// childComponent.
	Component childComponent;
	String name;
	// or
	PaneNode childNodeA;
	PaneNode childNodeB;

	// Location
	Rectangle location = new Rectangle();

	// When this object is a tree node, need to work out
	// how to split the space between the two children
	// either (x,1), or (1,y) where x, y may be one
	float widthDivide;
	float heightDivide;

	int xOffset;
	int yOffset;

	// Are children distributed horizontally or vertically?
	boolean horizontal;
	// Is NodeA on the top/left or bottom/right?
	boolean reverse = false;

	// Is the node selected?
	// boolean selected = false;

	public PaneNode(final PaneNode childA, final PaneNode childB, final String position, final float proportion) {
		childNodeA = childA;
		childNodeB = childB;
		if (position.equals(PaneConstraints.TOP) || position.equals(PaneConstraints.BOTTOM)) {
			widthDivide = 1.0f; // NORES
			heightDivide = proportion;
			horizontal = true;
		} else {
			widthDivide = proportion;
			heightDivide = 1.0f; // NORES
			horizontal = false;
		}
		if (position.equals(PaneConstraints.TOP) || position.equals(PaneConstraints.LEFT))
			reverse = true;
		else
			reverse = false;
	}

	public PaneNode(final String childName, final Component child, final String position) {
		childComponent = child;
		name = childName;
		widthDivide = 1.0f;
		heightDivide = 0.5f;
		horizontal = position.equals(PaneConstraints.TOP) || position.equals(PaneConstraints.BOTTOM); // NORES
		reverse = position.equals(PaneConstraints.TOP) || position.equals(PaneConstraints.LEFT); // NORES
	}

	void absorbChildNode(final PaneNode child) {
		// child is going away - immediate parent is adjusted so it looks just like
		// child
		if (child.childComponent != null) {
			childComponent = child.childComponent;
			name = child.name;
		} else {
			childComponent = null;
			childNodeA = child.childNodeA;
			childNodeB = child.childNodeB;
			widthDivide = child.widthDivide;
			heightDivide = child.heightDivide;
			reverse = child.reverse;
			horizontal = child.horizontal;
		}
	}

	public void addChild(final String childName, final Component child, final String position, final float proportion) {
		String thePosition = position;
		float theProportion = proportion;
		// Only two cases: either
		// a) node contains a single component => split it
		// b) node contains two sub nodes => pass the component on
		if (childComponent != null) {
			// Option a). Split the node
			// Steps:
			// i) Create a new node to contain the component from this node
			// System.err.println("Splitting " + name + " with " + childName);
			childNodeA = new PaneNode(name, childComponent, PaneConstraints.ROOT);
			childComponent = null;
			name = null;
			// ii) Put new node into the other child slot
			childNodeB = new PaneNode(childName, child, PaneConstraints.ROOT);
			// iii) Split as instructed
			theProportion = 1.0f - theProportion;
			if (thePosition.equals(PaneConstraints.RIGHT) || thePosition.equals(PaneConstraints.BOTTOM))
				reverse = false;
			else
				reverse = true;
			if (thePosition.equals(PaneConstraints.LEFT) || thePosition.equals(PaneConstraints.RIGHT)) {
				widthDivide = theProportion;
				heightDivide = 1.0f;
				horizontal = false;
			} else {
				widthDivide = 1.0f;
				heightDivide = theProportion;
				horizontal = true;
			}
			// Diagnostic.println("addChild "+splitHorizontal+" widthD:"+widthDivide+"
			// heightD:"+heightDivide);
		} else {
			// Option b). Pass the component in to the second node
			if (horizontal)
				thePosition = PaneConstraints.RIGHT;
			else
				thePosition = PaneConstraints.BOTTOM;
			childNodeB.addChild(childName, child, thePosition, theProportion);
		}
	}

	public boolean addChildSplit(final String childName, final String splitComponent, final String position,
			final Component newComponent, final float proportion) {
		// Split this one?
		if (childComponent != null) {
			if (name != null)
				if (name.equals(splitComponent)) {
					// cheat by using the other code
					addChild(childName, newComponent, position, proportion);
					return true;
				} else
					return false;
		} else {
			// try the children
			if (!childNodeA.addChildSplit(childName, splitComponent, position, newComponent, proportion))
				return childNodeB.addChildSplit(childName, splitComponent, position, newComponent, proportion);
			return true;
		}
		return false;
	}

	void assertLocation(final Rectangle locationInit, final int gap) {
		location.x = locationInit.x;
		location.y = locationInit.y;
		location.width = locationInit.width;
		location.height = locationInit.height;
		// Diagnostic.println(" Node: " + location.toString() + " child:" +
		// childComponent);
		if (childComponent != null) {
			if (!childComponent.getBounds().equals(location)) {
				// System.err.println("reshape " + name + location);
				childComponent.setBounds(location.x, location.y, location.width, location.height);
				if (childComponent instanceof Container)
					((Container) childComponent).doLayout();
			}
		} else {
			final Rectangle childALocation = new Rectangle();
			final Rectangle childBLocation = new Rectangle();

			calculateLocations(location, childALocation, childBLocation, gap);

			childNodeA.assertLocation(childALocation, gap);
			childNodeB.assertLocation(childBLocation, gap);
		}
	}

	void calculateLocations(final Rectangle location1, final Rectangle childALocation, final Rectangle childBLocation,
			final int border) {
		// Diagnostic.println("CALC horizontal:"+horizontal+" widthD:"+widthDivide+"
		// heightD:"+heightDivide+" loc:"+location);
		// Diagnostic.println(" xOffset:"+xOffset+" yOffset:"+yOffset);
		final int theBorder = border + border;

		if (heightDivide == 1.0f) {
			float proportion = widthDivide;
			if (reverse)
				proportion = 1.0f - proportion;

			// Work out the location of the two children
			xOffset = (int) ((location1.width - theBorder) * proportion);

			childALocation.x = location1.x;
			childALocation.y = location1.y;
			childALocation.width = xOffset;
			childALocation.height = location1.height;

			childBLocation.x = location1.x + xOffset + theBorder;
			childBLocation.y = location1.y;
			childBLocation.width = location1.width - xOffset - theBorder;
			childBLocation.height = location1.height;

		} else {
			float proportion = heightDivide;
			if (reverse)
				proportion = 1.0f - proportion;
			// Work out the location of the two children
			yOffset = (int) ((location1.height - theBorder) * proportion);
			childALocation.x = location1.x;
			childALocation.y = location1.y;
			childALocation.width = location1.width;
			childALocation.height = yOffset;

			childBLocation.x = location1.x;
			childBLocation.y = location1.y + yOffset + theBorder;
			childBLocation.width = location1.width;
			childBLocation.height = location1.height - yOffset - theBorder;

		}
		if (reverse) {
			// System.err.println("reverse");
			final Rectangle temp = new Rectangle(childALocation.x, childALocation.y, childALocation.width,
					childALocation.height);
			childALocation.x = childBLocation.x;
			childALocation.y = childBLocation.y;
			childALocation.width = childBLocation.width;
			childALocation.height = childBLocation.height;

			childBLocation.x = temp.x;
			childBLocation.y = temp.y;
			childBLocation.width = temp.width;
			childBLocation.height = temp.height;
		}
		// System.err.println("CHILDA:"+childALocation+" CHILDB:"+childBLocation);
	}

	void drag(final int x, final int y) {
		if (childComponent == null) {
			// if (selected) {
			if (heightDivide == 1.0f) {
				// if (reverse)
				// widthDivide = 1.0f - widthDivide ;
				// widthDivide += (float)dx / (float)location.width;
				widthDivide = (float) (x - location.x) / (float) location.width;
				if (reverse)
					widthDivide = 1.0f - widthDivide;
				if (widthDivide < 0) {
					widthDivide = 0;
				} else if (widthDivide >= 1.0f) {
					widthDivide = 0.999f;
				}
			} else {
				if (reverse)
					heightDivide = 1.0f - heightDivide;
				// heightDivide += (float)dy / (float)location.height;
				heightDivide = (float) (y - location.y) / (float) location.height;
				if (reverse)
					heightDivide = 1.0f - heightDivide;
				if (heightDivide < 0) {
					heightDivide = 0;
				} else

				if (heightDivide >= 1.0f) {
					heightDivide = 0.999f;
				}
			}
			// }
		}
	}

	void dump() {
		// Diagnostic.println("childComponent:" + childComponent); //NORES
		// Diagnostic.println("childNodeA:" + childNodeA); //NORES
		// Diagnostic.println("childNodeB:" + childNodeB); //NORES
	}

	void getComponents(final Point subscript, final String[] componentArray, final boolean parentIsNodeB) {
		if (childComponent == null) {
			if (parentIsNodeB) {
				componentArray[subscript.x] = childNodeA.getNodeAComponent();
				// System.err.println("addA at " + subscript.x + " - " +
				// componentArray[subscript.x]);
				subscript.x++;
			}
			componentArray[subscript.x] = childNodeB.getNodeAComponent();

			// System.err.println("addB at " + subscript.x +parentIsNodeB + " - " +
			// componentArray[subscript.x]);
			subscript.x++;
			childNodeA.getComponents(subscript, componentArray, false);

			childNodeB.getComponents(subscript, componentArray, false);

		}

	}

	Rectangle getDividerRect(final int gap) {
		if (childComponent == null) {
			if (heightDivide == 1.0f) {
				if (reverse)
					return new Rectangle(childNodeB.location.x + childNodeB.location.width, childNodeB.location.y, gap,
							childNodeB.location.height);
				else
					return new Rectangle(childNodeA.location.x + childNodeA.location.width, childNodeA.location.y, gap,
							childNodeA.location.height);
			} else {
				if (reverse)
					return new Rectangle(childNodeB.location.x, childNodeB.location.y + childNodeB.location.height,
							childNodeB.location.width, gap);
				else
					return new Rectangle(childNodeA.location.x, childNodeA.location.y + childNodeA.location.height,
							childNodeA.location.width, gap);
			}
		}
		return new Rectangle(location.x - gap, yOffset + location.y - gap, location.width + gap, gap);
	}

	/**
	 * returns the node that points to the child leaf
	 */
	public PaneNode getImmediateParent(final Component child) {
		if (childComponent != null) {
			if (childComponent == child) {
				return this;
			} else
				return null;
		}
		if (childNodeA.childComponent == child)
			return this;
		else if (childNodeB.childComponent == child)
			return this;
		// Check ChildNodeA
		final PaneNode rc = childNodeA.getImmediateParent(child);
		if (rc != null)
			return rc;
		else
			return childNodeB.getImmediateParent(child);
	}

	/**
	 * return the first NodeA that is a childComponent
	 */
	public String getNodeAComponent() {
		// System.err.println("getNodeAcomponent");
		if (childComponent != null) {
			// System.err.println("found " + name);
			return name;
		}
		// if (childNodeA.childComponent != null)
		// return childNodeA.name;
		else
			return childNodeA.getNodeAComponent();
	}

	// void deselectAll() {
	// if (childComponent == null) {
	// selected = false;
	// childNodeA.deselectAll();
	// childNodeB.deselectAll();
	// }
	// }

	/*
	 * boolean select(boolean shift, int x, int y, int gap) { // Make sure this is a
	 * node, not a leaf if (childComponent == null) { int offset; if (heightDivide
	 * == 1.0f) offset = x - (location.x + xOffset); else offset = y - (location.y +
	 * yOffset);
	 *
	 * if (Math.abs(offset) <= gap) { if (shift) selected = !selected; else selected
	 * = true;
	 *
	 * return true; } else { if (offset < 0 ^ reverse) return
	 * childNodeA.select(shift, x, y, gap); else return childNodeB.select(shift, x,
	 * y, gap); } } return false; }
	 */

	/**
	 * returns the lowest node that points to both the component and the component
	 * that it is split from
	 */
	public PaneNode getParentNode(final Component child, final PaneNode lastBNode) {
		// End the recursion if we have a leaf (ie childComponent != null)
		if (childComponent != null) {
			if (childComponent == child) {
				// System.err.println("not split with anyone??");
				return this;
			}
			return null;
		} else if (childNodeA.childComponent == child)
			if (lastBNode != null)
				return lastBNode;
			else
				return childNodeA; // the root component
		else if (childNodeB.childComponent == child)
			return this;
		// Check ChildNodeA
		final PaneNode resultA = childNodeA.getParentNode(child, lastBNode);
		if (resultA != null)
			return resultA;
		else
			return childNodeB.getParentNode(child, this);
	}

	public Dimension getPreferredSize(final int gap) {
		// is This one split?
		Dimension d = null;
		try {
			if (childComponent != null) {
				d = childComponent.getPreferredSize();
				// System.err.println("preferred size of:" + name + "="+ d);
				return d;
			}

			final Dimension a = childNodeA.getPreferredSize(gap);
			final Dimension b = childNodeB.getPreferredSize(gap);

			float divisor = widthDivide;
			if (!horizontal) {
				if (divisor >= 0.999)
					d = a;
				if (divisor == 0.0)
					d = b;
				else
					d = new Dimension((int) Math.max((a.width) / divisor, (b.width) / (1. - divisor)),
							Math.max(a.height, b.height));
				d.width += gap + gap + 1;
			} else {
				divisor = heightDivide;

				if (divisor >= 0.999) // NORES
					d = a;
				else if (divisor == 0.0) // NORES
					d = b;
				else
					d = new Dimension(Math.max(a.width, b.width),
							(int) Math.max((a.height) / divisor, (b.height) / (1. - divisor)));
				d.height += gap + gap + 1;
			}
			// System.err.println("PaneNode has preferred size of:" + d + " divisor=" +
			// divisor + " " +heightDivide +" " + widthDivide);
		} catch (final Exception e) {
			MWC.Utilities.Errors.Trace.trace(e);
		}
		return d;
	}

	PaneNode hitTest(final int x, final int y, final int gap) {
		// Make sure this is a node, not a leaf
		if (location != null && childComponent == null) {
			int offset;
			if (heightDivide == 1.0f)
				offset = x - (location.x + xOffset);
			else
				offset = y - (location.y + yOffset);

			if (Math.abs(offset) <= gap) {
				return this;
			} else {
				// if (heightDivide == 1.0f)
				// System.err.println("not on X " + (location.x + xOffset));
				// else
				// System.err.println("not on Y " + (location.y + yOffset));
				if (offset < 0 ^ reverse)
					return childNodeA.hitTest(x, y, gap);
				else
					return childNodeB.hitTest(x, y, gap);
			}
		}
		return null;
	}

	/**
	 * This method removes a given node. must already be positioned on the parent of
	 * the node to be removed
	 */
	public PaneNode removeChild(final Component child) {
		PaneNode rc = null;
		// Check ChildNodeA
		if (childNodeA.childComponent == child) {
			// System.err.println("removing A"+childNodeA.name);
			rc = childNodeA;
			absorbChildNode(childNodeB);
			// remember enough stuff so we can reconstruct the removed node
			rc.childNodeA = this;
			rc.childNodeB = null;
		} else if (childNodeB.childComponent == child) {
			// System.err.println("removing B"+childNodeB.name);
			rc = childNodeB;
			absorbChildNode(childNodeA);
			rc.childNodeA = null;
			rc.childNodeB = this;
		}
		// else
		// System.err.println("did not find the component ");
		return rc;
	}

	public void setConstraints(final Component child, final PaneConstraints constraints) {
		if (childComponent != null)
			return;
		if (constraints.position.equals(PaneConstraints.TOP) || constraints.position.equals(PaneConstraints.LEFT))
			reverse = true;
		else
			reverse = false;
		if (constraints.position.equals(PaneConstraints.BOTTOM)) {
			heightDivide = 1.0f - constraints.proportion;
			widthDivide = 1.0f;
			horizontal = true;
		} else if (constraints.position.equals(PaneConstraints.TOP)) {
			heightDivide = 1.0f - constraints.proportion;
			widthDivide = 1.0f;
			horizontal = true;
		} else if (constraints.position.equals(PaneConstraints.RIGHT)) {
			widthDivide = 1.0f - constraints.proportion;
			heightDivide = 1.0f;
			horizontal = false;
		} else if (constraints.position.equals(PaneConstraints.LEFT)) {
			widthDivide = 1.0f - constraints.proportion;
			heightDivide = 1.0f;
			horizontal = false;
		}
	}
}
