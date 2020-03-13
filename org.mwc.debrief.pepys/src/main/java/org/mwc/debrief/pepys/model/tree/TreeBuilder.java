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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.mwc.debrief.pepys.model.AbstractConfiguration;
import org.mwc.debrief.pepys.model.TypeDomain;
import org.mwc.debrief.pepys.model.bean.AbstractBean;
import org.mwc.debrief.pepys.model.db.Condition;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;
import org.mwc.debrief.pepys.model.db.annotation.AnnotationsUtils;
import org.mwc.debrief.pepys.model.db.annotation.Time;
import org.mwc.debrief.pepys.model.tree.TreeNode.NodeType;

import junit.framework.TestCase;

public class TreeBuilder {

	public class TreeBuilderTest extends TestCase {

		public void testTreeBuilder() {

		}
	}

	/**
	 * Method that builds the tree structure from top to bottom.
	 *
	 * @param items
	 * @param root
	 * @return
	 */
	public static TreeNode buildStructure(final AbstractBean[] items, final TreeNode root) {

		for (final AbstractBean item : items) {
			if (item instanceof TreeStructurable) {
				final TreeStructurable currentItem = (TreeStructurable) item;
				final String platformName = currentItem.getPlatform().getName();

				TreeNode datafileNode = root.getChild(platformName);
				if (datafileNode == null) {
					datafileNode = new TreeNode(TreeNode.NodeType.PLATFORM, platformName, root);
					root.addChild(datafileNode);
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
						leaf = new TreeNode(NodeType.DATAFILE, currentItem.getDatafile().getReference());

						sensorNode.addChild(leaf);
					}

				}

				leaf.addItem(currentItem);
			}
		}

		return root;
	}

	public static void buildStructure(final AbstractConfiguration configuration)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, PropertyVetoException, SQLException {
		configuration.getTreeModel().removeAllChildren();

		final ArrayList<AbstractBean> allItems = new ArrayList<AbstractBean>();
		for (final TypeDomain domain : configuration.getDatafileTypeFilters()) {
			final Class<AbstractBean> currentBeanType = domain.getDatatype();

			if (AbstractBean.class.isAssignableFrom(currentBeanType) && domain.isChecked()) {

				final ArrayList<Condition> conditions = new ArrayList<Condition>();

				// Let's filter by Period.
				final Field timeField = AnnotationsUtils.getField(currentBeanType, Time.class);
				if (timeField != null) {
					final SimpleDateFormat sqlDateFormat = new SimpleDateFormat(SqliteDatabaseConnection.SQLITE_DATE_FORMAT);
					final String initDate = sqlDateFormat.format(configuration.getTimePeriod().getStartDTG().getDate());
					final String endDate = sqlDateFormat.format(configuration.getTimePeriod().getEndDTG().getDate());

					final String fieldName = AnnotationsUtils.getTableName(currentBeanType)
							+ AnnotationsUtils.getColumnName(timeField);

					conditions.add(new Condition(DatabaseConnection.ESCAPE_CHARACTER + initDate
							+ DatabaseConnection.ESCAPE_CHARACTER + " <= " + fieldName));
					conditions.add(new Condition(fieldName + " <= " + DatabaseConnection.ESCAPE_CHARACTER + endDate
							+ DatabaseConnection.ESCAPE_CHARACTER));
				}

				// TODO FILTERING HERE
				final String filter = null;
				final List<? extends AbstractBean> currentItems = DatabaseConnection.getInstance()
						.listAll(currentBeanType, conditions);
				allItems.addAll(currentItems);
			}
		}

		buildStructure(allItems.toArray(new AbstractBean[] {}), configuration.getTreeModel());
	}
}
