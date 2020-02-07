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

package ASSET.GUI.Workbench.Plotters;

import java.util.Enumeration;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class BasePlottable implements Layer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * the thing we're wrapping
	 */
	final private Editable _myModel;

	final private Layer _parentLayer;

	public BasePlottable(final Editable myModel, final Layer parentLayer) {
		_myModel = myModel;
		_parentLayer = parentLayer;
	}

	@Override
	public void add(final Editable point) {
	}

	@Override
	public void append(final Layer other) {
	}

	@Override
	public int compareTo(final Plottable arg0) {
		final BasePlottable other = (BasePlottable) arg0;
		final Editable otherM = other._myModel;
		return _myModel.getName().compareTo(otherM.getName());
	}

	@Override
	public Enumeration<Editable> elements() {
		return null;
	}

	@Override
	public void exportShape() {
	}

	@Override
	public WorldArea getBounds() {
		return null;
	}

	@Override
	public EditorType getInfo() {
		return _myModel.getInfo();
	}

	@Override
	public int getLineThickness() {
		return 1;
	}

	protected Editable getModel() {
		return _myModel;
	}

	@Override
	public String getName() {
		return _myModel.getName();
	}

	public Layer getTopLevelLayer() {
		return _parentLayer;
	}

	@Override
	public boolean getVisible() {
		return true;
	}

	@Override
	public boolean hasEditor() {
		return _myModel.hasEditor();
	}

	@Override
	public boolean hasOrderedChildren() {
		return false;
	}

	@Override
	public void paint(final CanvasType dest) {
	}

	@Override
	public double rangeFrom(final WorldLocation other) {
		return -1;
	}

	@Override
	public void removeElement(final Editable point) {
	}

	@Override
	public void setName(final String name) {
		// ignore...
	}

	@Override
	public void setVisible(final boolean val) {
	}

	@Override
	public String toString() {
		return getName();
	}

}
