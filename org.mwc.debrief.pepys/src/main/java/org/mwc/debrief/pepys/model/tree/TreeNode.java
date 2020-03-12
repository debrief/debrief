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

package org.mwc.debrief.pepys.model.tree;

import java.io.PrintStream;
import java.util.List;
import java.util.TreeMap;

import org.mwc.debrief.pepys.model.bean.AbstractBean;
import org.mwc.debrief.pepys.model.bean.State;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;

import junit.framework.TestCase;

public class TreeNode {

	public static enum NodeType {
		ROOT, PLATFORM, MEASURE, SENSOR, DATAFILE
	}

	private NodeType type;
	private String name;
	private AbstractBean value;
	private TreeNode parent = null;

	private TreeMap<String, TreeNode> children = new TreeMap<String, TreeNode>();

	public TreeNode(final NodeType _type, final String _name, final AbstractBean _value, final TreeNode _parent) {
		this.type = _type;
		this.name = _name;
		this.value = _value;
		this.parent = _parent;
	}
	
	public TreeNode(final NodeType _type, final String _name, final AbstractBean _value) {
		this.type = _type;
		this.name = _name;
		this.value = _value;
	}

	public TreeNode getChild(final String childName) {
		return children.get(childName);
	}
	
	public void addChild(final TreeNode node) {
		children.put(node.name, node);
	}
	
	public TreeNode[] getChildren() {
		return children.values().toArray(new TreeNode[] {});
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	public void removeAllChildren() {
		children.clear();
	}
	
	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeNode other = (TreeNode) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public AbstractBean getValue() {
		return value;
	}
	
	public void print(final String currentTab, final String tabDelta, final PrintStream printStream) {
		printStream.println(currentTab + name);
		for (TreeNode child : children.values()) {
			child.print(currentTab + tabDelta, tabDelta, printStream);
		}
	}
	
	public String getName() {
		return name;
	}

	public static class TreeNodeTest extends TestCase{
		
		public void testTreeNode() {
			List list = null;
			try {
				list = DatabaseConnection.getInstance().listAll(State.class, null);
			} catch (Exception e) {
				fail("Failed retrieving data from Database");
			}

			assertTrue("States - database entries", list.size() == 543);
			
			final String rootName = "ROOT";
			final TreeNode root = new TreeNode(NodeType.ROOT, rootName, (State)list.get(0), null);
			final TreeNode child1 = new TreeNode(NodeType.ROOT, rootName, (State)list.get(1), root);
			final TreeNode child2 = new TreeNode(NodeType.ROOT, rootName, (State)list.get(2), root);
			final TreeNode child3 = new TreeNode(NodeType.ROOT, rootName, (State)list.get(3), root);
			final TreeNode child1child1 = new TreeNode(NodeType.ROOT, rootName, (State)list.get(4), child1);
			final TreeNode child1child2 = new TreeNode(NodeType.ROOT, rootName, (State)list.get(5), child1);
			
			root.addChild(child1);
			root.addChild(child2);
			root.addChild(child3);
			
			child1.addChild(child1child1);
			child1.addChild(child1child2);
			
			assertTrue("Retrieving child1 correctly", child1.equals(root.getChild(child1.name)));
			assertTrue("Retrieving child2 correctly", child2.equals(root.getChild(child2.name)));
			assertTrue("Retrieving child3 correctly", child3.equals(root.getChild(child3.name)));

			assertTrue("Retrieving child1child1 correctly", child1child1.equals(child1.getChild(child1child1.name)));
			assertTrue("Retrieving child1child2 correctly", child1child2.equals(child1.getChild(child1child2.name)));
		}
	}
}
