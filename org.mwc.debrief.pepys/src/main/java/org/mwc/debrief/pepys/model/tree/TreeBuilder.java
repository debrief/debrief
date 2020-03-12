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
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.mwc.debrief.pepys.model.AbstractConfiguration;
import org.mwc.debrief.pepys.model.TypeDomain;
import org.mwc.debrief.pepys.model.bean.AbstractBean;
import org.mwc.debrief.pepys.model.bean.Datafile;
import org.mwc.debrief.pepys.model.bean.Sensor;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.annotation.AnnotationsUtils;
import org.mwc.debrief.pepys.model.tree.TreeNode.NodeType;

import junit.framework.TestCase;

public class TreeBuilder {

	/**
	 * Method that builds the tree structure from top to bottom.
	 * 
	 * @param items
	 * @param root
	 * @return
	 */
	public static TreeNode buildStructure(final AbstractBean[] items, final TreeNode root) {

		for (AbstractBean item : items) {
			if (item instanceof TreeStructurable) {
				final TreeStructurable currentItem = (TreeStructurable)item;
				final String platformName = currentItem.getPlatform().getName();

				TreeNode datafileNode = root.getChild(platformName);
				if (datafileNode == null) {
					datafileNode = new TreeNode(TreeNode.NodeType.PLATFORM, platformName, null, root);
					root.addChild(datafileNode);
				}

				final String measureName = AnnotationsUtils.getTableName(currentItem.getClass());
				TreeNode measureNode = datafileNode.getChild(measureName);
				if (measureNode == null) {
					measureNode = new TreeNode(TreeNode.NodeType.MEASURE, measureName, null, datafileNode);
					datafileNode.addChild(measureNode);
				}

				TreeNode leaf = new TreeNode(NodeType.DATAFILE, currentItem.getDatafile().getReference(), item);

				if (currentItem.getSensorType() == null) {
					// It has an exception in the structure, we simply add the leaf.
					measureNode.addChild(leaf);
				} else {
					final String sensorName = currentItem.getSensorType().getName();
					TreeNode sensorNode = measureNode.getChild(sensorName);
					if (sensorNode == null) {
						sensorNode = new TreeNode(TreeNode.NodeType.SENSOR, sensorName, null);
						measureNode.addChild(sensorNode);
					}

					sensorNode.addChild(leaf);
				}
			}
		}

		return root;
	}

	public static void buildStructure(final AbstractConfiguration configuration)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, PropertyVetoException, SQLException {
		configuration.getTreeModel().removeAllChildren();

		final ArrayList<AbstractBean> allItems = new ArrayList<AbstractBean>();
		for (TypeDomain domain : configuration.getDatafileTypeFilters()) {
			final Class<AbstractBean> currentBeanType = domain.getDatatype();

			if (AbstractBean.class.isAssignableFrom(currentBeanType) && domain.isChecked()) {

				// TODO FILTERING HERE
				String filter = null;
				List<? extends AbstractBean> currentItems = (List<? extends AbstractBean>) DatabaseConnection
						.getInstance().listAll(currentBeanType, null);
				allItems.addAll(currentItems);
			}
		}

		buildStructure(allItems.toArray(new AbstractBean[] {}), configuration.getTreeModel());
	}
	
	public class TreeBuilderTest extends TestCase{
		
		public void testTreeBuilder() {
			
		}
	}
}
