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
import org.mwc.debrief.pepys.model.db.annotation.AnnotationsUtils;
import org.mwc.debrief.pepys.model.tree.TreeNode.NodeType;

import Debrief.GUI.Frames.Application;
import MWC.GUI.ToolParent;
import junit.framework.TestCase;

public class TreeUtils {

	public static class SearchTreeResult {
		private final TreeNode item;
		private final int ocurrence;

		public SearchTreeResult(final TreeNode item, final int ocurrence) {
			super();
			this.item = item;
			this.ocurrence = ocurrence;
		}

		public TreeNode getItem() {
			return item;
		}

		public int getOcurrence() {
			return ocurrence;
		}

	}

	public class TreeBuilderTest extends TestCase {

		public void testTreeBuilder() {

		}
	}

	public static Collection<TreeStructurable> buildStructure(final AbstractConfiguration configuration)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, PropertyVetoException, SQLException,
			ClassNotFoundException, IOException {
		Application.logError2(ToolParent.INFO, "Starting to Build Structure from Model", null);
		final ArrayList<TreeStructurable> allItems = new ArrayList<>();
		for (final TypeDomain domain : configuration.getDatafileTypeFilters()) {
			Application.logError2(ToolParent.INFO, "Starting to list for " + domain.getName(), null);
			final Class<TreeStructurable> currentBeanType = domain.getDatatype();

			if (AbstractBean.class.isAssignableFrom(currentBeanType) && domain.isChecked()) {
				configuration.getDatabaseConnection().cleanRenamingBuffer();
				final ArrayList<Condition> conditions = new ArrayList<Condition>();

				Application.logError2(ToolParent.INFO, "Starting to create conditions for time period", null);
				conditions.addAll(configuration.getDatabaseConnection()
						.createPeriodFilter(configuration.getTimePeriod(), currentBeanType));

				Application.logError2(ToolParent.INFO, "Starting to create conditions for area filtering", null);
				conditions.addAll(configuration.getDatabaseConnection().createAreaFilter(configuration.getCurrentArea(),
						currentBeanType));

				Application.logError2(ToolParent.INFO, "Starting to create text filter", null);
				conditions.addAll(configuration.getDatabaseConnection().createTextFilter(configuration.getFilter(),
						currentBeanType));

				final List<? extends TreeStructurable> currentItems = configuration.getDatabaseConnection()
						.listAll(currentBeanType, conditions);
				Application.logError2(ToolParent.INFO, "Adding results to the item lists", null);
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
		Application.logError2(ToolParent.INFO, "Starting to Build Tree Structure.", null);
		root.removeAllChildren();
		final TreeNode subRoot = new TreeNode(TreeNode.NodeType.ROOT, "Database");
		root.addChild(subRoot);
		
		Application.logError2(ToolParent.INFO, "Updating Tree from the model calculated", null);
		Application.logError2(ToolParent.INFO, "Total amount of items to add " + items.length, null);
		for (final TreeStructurable currentItem : items) {
			final String platformName = currentItem.getPlatform().getTrackName();
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
					leaf = new TreeNode(NodeType.DATAFILE, currentItem.getDatafile().getReference(), measureNode);

					measureNode.addChild(leaf);
				}
				leaf.addItem(currentItem);
			} else {
				final String sensorName = currentItem.getSensorType().getName();
				TreeNode sensorNode = measureNode.getChild(sensorName);

				if (sensorNode == null) {
					sensorNode = new TreeNode(TreeNode.NodeType.SENSOR, sensorName, measureNode);
					measureNode.addChild(sensorNode);
				}

				leaf = sensorNode.getChild(currentItem.getDatafile().getReference());
				if (leaf == null) {
					leaf = new TreeNode(NodeType.DATAFILE, datafileName, sensorNode);

					sensorNode.addChild(leaf);
				}

			}

			leaf.addItem(currentItem);
		}

		Application.logError2(ToolParent.INFO, "Finishing building tree structure", null);
		return root;
	}

	private static void buildTreeSearchMap(final List<SearchTreeResult> result, final String text,
			final TreeNode currentNode) {
		int currentOcurrence = 0;

		String currentName = currentNode.getName();
		while (currentName != null && !currentName.isBlank()) {
			final int nextOne = currentName.toLowerCase().indexOf(text.toLowerCase());
			if (nextOne >= 0) {
				result.add(new SearchTreeResult(currentNode, currentOcurrence++));
				currentName = currentName.substring(Math.min(currentName.length(), nextOne + 1));
			} else {
				break;
			}
		}
		for (final TreeNode child : currentNode.getChildren()) {
			buildTreeSearchMap(result, text, child);
		}
	}

	public static SearchTreeResult[] buildTreeSearchMap(final String text, final TreeNode root) {
		if (!text.isBlank()) {
			final List<SearchTreeResult> result = new ArrayList<SearchTreeResult>();
			buildTreeSearchMap(result, text, root);
			return result.toArray(new SearchTreeResult[] {});
		} else {
			return null;
		}
	}
}
