package org.mwc.cmap.gridharness.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.EditorHelper;
import org.mwc.cmap.gridharness.data.base60.Sexagesimal;
import org.mwc.cmap.gridharness.data.base60.SexagesimalFormat;

import MWC.GenericData.WorldLocation;


public class WorldLocationHelper extends EditorHelper {

	public WorldLocationHelper()
	{
		super(WorldLocation.class);
	}

	@Override
	public CellEditor getCellEditorFor(Composite parent) {
		return new WorldLocationCellEditor(parent);
	}

	@Override
	public ILabelProvider getLabelFor(Object currentValue) {
		return new LabelProvider() {

			@Override
			public String getText(Object element) {
				SexagesimalFormat format = CorePlugin.getDefault().getLocationFormat();
				WorldLocation location = (WorldLocation) element;
				Sexagesimal latitude = format.parseDouble(location.getLat());
				Sexagesimal longitude = format.parseDouble(location.getLong());
				return format.format(latitude, false) + " " + format.format(longitude, true);
			}
		};
	}
}
