package org.mwc.cmap.gridharness.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.property_support.EditorHelper;
import org.mwc.cmap.gridharness.Activator;
import org.mwc.cmap.gridharness.data.WorldLocation;
import org.mwc.cmap.gridharness.data.base60.Sexagesimal;
import org.mwc.cmap.gridharness.data.base60.SexagesimalFormat;


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
				SexagesimalFormat format = Activator.getDefault().getLocationFormat();
				WorldLocation location = (WorldLocation) element;
				Sexagesimal latitude = format.parseDouble(location.getLatitude());
				Sexagesimal longitude = format.parseDouble(location.getLongitude());
				return format.format(latitude, false) + " " + format.format(longitude, true);
			}
		};
	}
}
