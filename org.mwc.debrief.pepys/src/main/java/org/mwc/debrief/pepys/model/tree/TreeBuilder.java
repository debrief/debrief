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
import org.mwc.debrief.pepys.model.bean.Datafiles;
import org.mwc.debrief.pepys.model.bean.FilterableBean;
import org.mwc.debrief.pepys.model.bean.Sensors;
import org.mwc.debrief.pepys.model.bean.TreeStructurable;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.tree.TreeNode.NodeType;

public class TreeBuilder {

	/**
	 * Method that builds the tree structure from top to bottom.
	 * @param items
	 * @param root
	 * @param sensors
	 * @param datafiles
	 * @return
	 */
	public static TreeNode buildStructure(final TreeStructurable[] items, final TreeNode root,
			final TreeMap<Integer, Sensors> sensors, final TreeMap<Integer, Datafiles> datafiles) {

		for (TreeStructurable item : items) {
			final String datafileName = datafiles.get(item.getSource()).getReference();
			
			TreeNode datafileNode = root.getChild(datafileName);
			if (datafileNode == null) {
				datafileNode = new TreeNode(TreeNode.NodeType.DATAFILES, datafileName, null);
				root.addChild(datafileNode);
			}
			
			TreeNode measureNode = datafileNode.getChild(item.getMeasureName());
			if (measureNode == null) {
				measureNode = new TreeNode(TreeNode.NodeType.MEASURE, item.getMeasureName(), null);
				datafileNode.addChild(measureNode);
			}

			TreeNode leaf = new TreeNode(NodeType.VALUE, item.getMyName(), item);
			
			if (item.getSensor() == -1) {
				// It has an exception in the structure, we simply add the leaf.
				measureNode.addChild(leaf);
			}else {
				final String sensorName = sensors.get(item.getSensor()).getName();
				TreeNode sensorNode = measureNode.getChild(sensorName);
				if (sensorNode == null) {
					sensorNode = new TreeNode(TreeNode.NodeType.SENSOR, sensorName, null);
					measureNode.addChild(sensorNode);
				}
				
				sensorNode.addChild(leaf);
			}
		}
		
		return root;
	}

	public static TreeNode buildStructure(final TreeStructurable[] items, final TreeNode root)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, PropertyVetoException, SQLException {
		final TreeMap<Integer, Sensors> sensors = new TreeMap<Integer, Sensors>();
		final TreeMap<Integer, Datafiles> datafiles = new TreeMap<Integer, Datafiles>();

		for (Datafiles datafile : DatabaseConnection.getInstance().listAll(Datafiles.class, null)) {
			datafiles.put(datafile.getDatafile_id(), datafile);
		}
		
		for (Sensors sensor : DatabaseConnection.getInstance().listAll(Sensors.class, null)) {
			sensors.put(sensor.getSensor_id(), sensor);
		}

		buildStructure(items, root, sensors, datafiles);

		return root;
	}

	public static void buildStructure(final AbstractConfiguration configuration)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, PropertyVetoException, SQLException {
		configuration.getTreeModel().removeAllChildren();

		final ArrayList<TreeStructurable> allItems = new ArrayList<TreeStructurable>();
		for (TypeDomain domain : configuration.getDatafileTypeFilters()) {
			final Class<FilterableBean> currentBeanType = domain.getDatatype();

			if (TreeStructurable.class.isAssignableFrom(currentBeanType)) {
				
				// TODO FILTERING HERE
				String filter = null;
				List<? extends TreeStructurable> currentItems = (List<? extends TreeStructurable>) DatabaseConnection
						.getInstance().listAll(currentBeanType, filter);
				allItems.addAll(currentItems);
			}
		}

		buildStructure(allItems.toArray(new TreeStructurable[] {}), configuration.getTreeModel());
	}
}
