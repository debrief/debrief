
package MWC.GUI.Properties;

import MWC.GUI.Layers;

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

import MWC.GUI.ToolParent;
import MWC.GenericData.WorldPath;

abstract public class WorldPathPropertyEditor extends java.beans.PropertyEditorSupport
		implements PlainPropertyEditor.EditorUsesPropertyPanel, PlainPropertyEditor.EditorUsesToolParent,
		PlainPropertyEditor.EditorUsesLayers {

	protected WorldPath _myPath;
	protected PropertiesPanel _thePanel;
	protected ToolParent _theParent;
	protected Layers _theLayers;

	@Override
	abstract public java.awt.Component getCustomEditor();

	@Override
	public Object getValue() {
		return _myPath;
	}

	abstract protected void resetData();

	@Override
	public void setLayers(final Layers theLayers) {
		_theLayers = theLayers;
	}

	@Override
	public void setPanel(final PropertiesPanel thePanel) {
		_thePanel = thePanel;
	}

	@Override
	public void setParent(final ToolParent theParent) {
		_theParent = theParent;
	}

	@Override
	public void setValue(final Object p1) {
		if (p1 instanceof WorldPath) {
			_myPath = (WorldPath) p1;
			resetData();
		} else
			return;
	}

	@Override
	public boolean supportsCustomEditor() {
		return true;
	}
}