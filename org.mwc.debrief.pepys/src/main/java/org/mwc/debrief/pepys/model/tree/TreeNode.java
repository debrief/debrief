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
import java.util.TreeMap;

import org.mwc.debrief.pepys.model.bean.TreeStructurable;

public class TreeNode {

	public static enum NodeType {
		ROOT, DATAFILES, MEASURE, SENSOR, VALUE
	}

	private NodeType type;
	private String name;
	private TreeStructurable value;

	private TreeMap<String, TreeNode> children = new TreeMap<String, TreeNode>();

	public TreeNode(final NodeType _type, final String _name, final TreeStructurable _value) {
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
	
	public void removeAllChildren() {
		children.clear();
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

	public TreeStructurable getValue() {
		return value;
	}
	
	public void print(final String currentTab, final String tabDelta, final PrintStream printStream) {
		printStream.println(currentTab + name);
		for (TreeNode child : children.values()) {
			child.print(currentTab + tabDelta, tabDelta, printStream);
		}
	}
}
