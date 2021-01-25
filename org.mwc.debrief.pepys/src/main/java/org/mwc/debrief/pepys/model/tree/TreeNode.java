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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.mwc.debrief.pepys.model.bean.State;
import org.mwc.debrief.pepys.model.db.Condition;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;
import org.mwc.debrief.pepys.model.db.config.ConfigurationReader;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;
import org.mwc.debrief.pepys.model.db.config.LoaderOption;
import org.mwc.debrief.pepys.model.db.config.LoaderOption.LoaderType;

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import junit.framework.TestCase;

public class TreeNode {

	public static enum NodeType {
		ROOT, PLATFORM, MEASURE, SENSOR, DATAFILE
	}

	public static class TreeNodeTest extends TestCase {

		public void testTreeNode() {
			List<State> list = null;
			try {
				final DatabaseConfiguration _config = new DatabaseConfiguration();
				ConfigurationReader.loadDatabaseConfiguration(_config, new LoaderOption[] {
						new LoaderOption(LoaderType.DEFAULT_FILE, DatabaseConnection.DEFAULT_SQLITE_DATABASE_FILE) });
				final SqliteDatabaseConnection sqlite = new SqliteDatabaseConnection();
				sqlite.initializeInstance(_config);
				list = sqlite.listAll(State.class, (Collection<Condition>)null);
			} catch (final Exception e) {
				fail("Failed retrieving data from Database");
			}

			assertTrue("States - database entries", list.size() == 12239);

			final String rootName = "ROOT";
			final TreeNode root = new TreeNode(NodeType.ROOT, rootName, null);
			root.addItem(list.get(0));
			final String child1Name = "CHILD1";
			final TreeNode child1 = new TreeNode(NodeType.PLATFORM, child1Name, root);
			child1.addItem(list.get(1));
			final String child2Name = "CHILD2";
			final TreeNode child2 = new TreeNode(NodeType.MEASURE, child2Name, root);
			child2.addItem(list.get(2));
			final String child3Name = "CHILD3";
			final TreeNode child3 = new TreeNode(NodeType.ROOT, child3Name, root);
			child3.addItem(list.get(3));
			final String child1child1Name = "child1child1Name";
			final TreeNode child1child1 = new TreeNode(NodeType.ROOT, child1child1Name, child1);
			child1child1.addItem(list.get(4));
			final String child1child2Name = "child1child2Name";
			final TreeNode child1child2 = new TreeNode(NodeType.ROOT, child1child2Name, child1);
			child1child2.addItem(list.get(5));

			root.addChild(child1);
			root.addChild(child2);
			root.addChild(child3);

			child1.addChild(child1child1);
			child1.addChild(child1child2);

			assertTrue("Retrieving child1 correctly", child1.equals(root.getChild(child1Name)));
			assertTrue("Retrieving child2 correctly", child2.equals(root.getChild(child2Name)));
			assertTrue("Retrieving child3 correctly", child3.equals(root.getChild(child3Name)));

			assertTrue("Retrieving child1child1 correctly", child1child1.equals(child1.getChild(child1child1Name)));
			assertTrue("Retrieving child1child2 correctly", child1child2.equals(child1.getChild(child1child2Name)));
		}
	}

	public static final String STATE = "States";

	public static final String CONTACTS = "Contacts";

	public static final String COMMENT = "Comments";

	private static final String ADD_VALUE = "ADD_VALUE";

	private final NodeType type;
	private final String name;
	private TreeNode parent = null;
	private final TimePeriod currentPeriod = new BaseTimePeriod(TimePeriod.INVALID_DATE, TimePeriod.INVALID_DATE);

	private boolean checked = false;

	private final PropertyChangeSupport _pSupport = new PropertyChangeSupport(this);

	private final ArrayList<TreeStructurable> items = new ArrayList<TreeStructurable>();

	private final TreeMap<String, TreeNode> children = new TreeMap<String, TreeNode>();

	private final PropertyChangeListener addNewItemListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if (ADD_VALUE.equals(evt.getPropertyName()) && evt.getNewValue() != null
					&& evt.getNewValue() instanceof TreeStructurable) {
				final TreeStructurable newItem = (TreeStructurable) evt.getNewValue();
				currentPeriod.extend(new HiResDate(newItem.getTime()));
			}
		}
	};

	public TreeNode(final NodeType _type, final String _name) {
		this.type = _type;
		this.name = _name;

		_pSupport.addPropertyChangeListener(addNewItemListener);
	}

	public TreeNode(final NodeType _type, final String _name, final TreeNode _parent) {
		this.type = _type;
		this.name = _name;
		this.parent = _parent;

		_pSupport.addPropertyChangeListener(addNewItemListener);
	}

	public void addChild(final TreeNode node) {
		children.put(node.name, node);
	}

	public void addItem(final TreeStructurable item) {
		items.add(item);

		_pSupport.firePropertyChange(ADD_VALUE, null, item);
	}

	public int countCheckedItems() {
		int total = isChecked() && !items.isEmpty() ? 1 : 0;
		for (final TreeNode child : children.values()) {
			total += child.countCheckedItems();
		}
		return total;
	}

	@Override
	public boolean equals(final Object obj) {
		return this == obj;
	}

	public TreeNode getChild(final String childName) {
		return children.get(childName);
	}

	public TreeNode[] getChildren() {
		return children.values().toArray(new TreeNode[] {});
	}

	public TimePeriod getCurrentPeriod() {
		return currentPeriod;
	}

	public ArrayList<TreeStructurable> getItems() {
		return items;
	}

	public String getName() {
		return name;
	}

	public TreeNode getParent() {
		return parent;
	}

	public NodeType getType() {
		return type;
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	public boolean isChecked() {
		return checked;
	}

	public void print(final String currentTab, final String tabDelta, final PrintStream printStream) {
		printStream.println(currentTab + name);
		for (final TreeNode child : children.values()) {
			child.print(currentTab + tabDelta, tabDelta, printStream);
		}
	}

	public void removeAllChildren() {
		children.clear();
	}

	public void setChecked(final boolean checked) {
		this.checked = checked;
	}

	public void setCheckedRecursive(final boolean checked) {
		setChecked(checked);
		for (final TreeNode child : children.values()) {
			child.setCheckedRecursive(checked);
		}
	}

	public void setParent(final TreeNode parent) {
		this.parent = parent;
	}

}
