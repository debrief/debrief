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

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mwc.debrief.pepys.model.AbstractConfiguration;
import org.mwc.debrief.pepys.model.TypeDomain;
import org.mwc.debrief.pepys.model.bean.AbstractBean;
import org.mwc.debrief.pepys.model.db.Condition;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.annotation.AnnotationsUtils;
import org.mwc.debrief.pepys.model.tree.TreeNode.NodeType;

import junit.framework.TestCase;

public class TreeBuilder {

	public class TreeBuilderTest extends TestCase {

		public void testTreeBuilder() {

		}
	}

	public static Collection<TreeStructurable> buildStructure(final AbstractConfiguration configuration)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, PropertyVetoException, SQLException,
			ClassNotFoundException, IOException {

		final ArrayList<TreeStructurable> allItems = new ArrayList<>();
		for (final TypeDomain domain : configuration.getDatafileTypeFilters()) {
			final Class<TreeStructurable> currentBeanType = domain.getDatatype();

			if (AbstractBean.class.isAssignableFrom(currentBeanType) && domain.isChecked()) {
				DatabaseConnection.getInstance().cleanRenamingBuffer();
				final ArrayList<Condition> conditions = new ArrayList<Condition>();

				conditions.addAll(DatabaseConnection.getInstance().createPeriodFilter(configuration.getTimePeriod(),
						currentBeanType));

				conditions.addAll(DatabaseConnection.getInstance().createAreaFilter(configuration.getCurrentArea(),
						currentBeanType));

				conditions.addAll(
						DatabaseConnection.getInstance().createTextFilter(configuration.getFilter(), currentBeanType));

				final List<? extends TreeStructurable> currentItems = DatabaseConnection.getInstance()
						.listAll(currentBeanType, conditions);
				allItems.addAll(currentItems);
			}
		}

		return allItems;
	}

	/**
	 * Method that builds the tree structure from top to bottom.
	 *
	 * @param items
	 * @param root
	 * @return
	 */
	public static TreeNode buildStructure(final TreeStructurable[] items, final TreeNode root) {
		root.removeAllChildren();
		final TreeNode subRoot = new TreeNode(TreeNode.NodeType.ROOT, "Database");
		root.addChild(subRoot);

		for (final TreeStructurable currentItem : items) {
			final String platformName = currentItem.getPlatform().getName();
			final String datafileName = currentItem.getDatafile().getReference();
			TreeNode datafileNode = subRoot.getChild(platformName);
			if (datafileNode == null) {
				datafileNode = new TreeNode(TreeNode.NodeType.PLATFORM, platformName, subRoot);
				subRoot.addChild(datafileNode);
			}

			final String measureName = AnnotationsUtils.getTableName(currentItem.getClass());
			TreeNode measureNode = datafileNode.getChild(measureName);
			if (measureNode == null) {
				measureNode = new TreeNode(TreeNode.NodeType.MEASURE, measureName, datafileNode);
				datafileNode.addChild(measureNode);
			}

			TreeNode leaf;
			if (currentItem.getSensorType() == null) {
				// It has an exception in the structure, we simply add the leaf.

				leaf = measureNode.getChild(currentItem.getDatafile().getReference());
				if (leaf == null) {
					leaf = new TreeNode(NodeType.DATAFILE, currentItem.getDatafile().getReference());

					measureNode.addChild(leaf);
				}
				leaf.addItem(currentItem);
			} else {
				final String sensorName = currentItem.getSensorType().getName();
				TreeNode sensorNode = measureNode.getChild(sensorName);

				if (sensorNode == null) {
					sensorNode = new TreeNode(TreeNode.NodeType.SENSOR, sensorName, null);
					measureNode.addChild(sensorNode);
				}

				leaf = sensorNode.getChild(currentItem.getDatafile().getReference());
				if (leaf == null) {
					leaf = new TreeNode(NodeType.DATAFILE, datafileName);

					sensorNode.addChild(leaf);
				}

			}

			leaf.addItem(currentItem);
		}

		return root;
	}
}
