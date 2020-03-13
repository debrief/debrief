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

package org.mwc.debrief.pepys.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.mwc.debrief.pepys.model.tree.TreeBuilder;
import org.mwc.debrief.pepys.model.tree.TreeNode;
import org.mwc.debrief.pepys.model.tree.TreeStructurable;

import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;

public class ModelConfiguration implements AbstractConfiguration {

	public static String AREA_PROPERTY = "AREA";

	public static String PERIOD_PROPERTY = "PERIOD";

	public static String TREE_MODEL = "TREE_MODEL";

	private PropertyChangeSupport _pSupport = null;

	private final ArrayList<TypeDomain> currentDatatype = new ArrayList<TypeDomain>();

	private WorldArea currentArea = null;

	private TimePeriod currentPeriod = null;

	private final TreeNode treeModel = new TreeNode(TreeNode.NodeType.ROOT, "", null);

	@Override
	public void addDatafileTypeFilter(final TypeDomain newType) {
		currentDatatype.add(newType);
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener l) {
		if (_pSupport == null) {
			_pSupport = new PropertyChangeSupport(this);
		}
		_pSupport.addPropertyChangeListener(l);
	}

	@Override
	public void apply() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, PropertyVetoException, SQLException {

		TreeBuilder.buildStructure(this);

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, TREE_MODEL, null, treeModel);
			_pSupport.firePropertyChange(pce);
		}
	}

	@Override
	public void doImport() {
		doImportProcessMockup(treeModel);
	}

	private void doImportProcessMockup(final TreeNode treeModel) {
		if (treeModel.isChecked()) {
			for (final TreeStructurable item : treeModel.getItems()) {
				System.out.println("Importing " + treeModel.getName() + " -> " + item);
			}
		}
		for (final TreeNode child : treeModel.getChildren()) {
			doImportProcessMockup(child);
		}
	}

	@Override
	public WorldArea getCurrentArea() {
		return currentArea;
	}

	@Override
	public Collection<TypeDomain> getDatafileTypeFilters() {
		return currentDatatype;
	}

	@Override
	public TimePeriod getTimePeriod() {
		return currentPeriod;
	}

	@Override
	public TreeNode getTreeModel() {
		return treeModel;
	}

	@Override
	public void removeDatafileTypeFilter(final TypeDomain typeToRemove) {
		currentDatatype.remove(typeToRemove);
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener l) {
		if (_pSupport != null) {
			_pSupport.removePropertyChangeListener(l);
		}
	}

	@Override
	public void setArea(final WorldArea newArea) {
		final WorldArea oldArea = currentArea;
		currentArea = newArea;

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, AREA_PROPERTY, oldArea,
					currentArea);
			_pSupport.firePropertyChange(pce);
		}
	}

	@Override
	public void setTimePeriod(final TimePeriod newPeriod) {
		final TimePeriod oldPeriod = currentPeriod;
		currentPeriod = newPeriod;

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, PERIOD_PROPERTY, oldPeriod,
					currentPeriod);
			_pSupport.firePropertyChange(pce);
		}
	}

}
