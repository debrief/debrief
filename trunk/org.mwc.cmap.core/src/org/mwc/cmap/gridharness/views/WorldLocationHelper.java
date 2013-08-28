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
	public CellEditor getCellEditorFor(final Composite parent) {
		return new WorldLocationCellEditor(parent);
	}

	@Override
	public ILabelProvider getLabelFor(final Object currentValue) {
		return new LabelProvider() {

			@Override
			public String getText(final Object element) {
				final SexagesimalFormat format = CorePlugin.getDefault().getLocationFormat();
				final WorldLocation location = (WorldLocation) element;
				final Sexagesimal latitude = format.parseDouble(location.getLat());
				final Sexagesimal longitude = format.parseDouble(location.getLong());
				return format.format(latitude, false) + " " + format.format(longitude, true);
			}
		};
	}
}
