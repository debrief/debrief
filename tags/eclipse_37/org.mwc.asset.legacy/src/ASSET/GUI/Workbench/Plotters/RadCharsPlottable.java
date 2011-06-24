package ASSET.GUI.Workbench.Plotters;

import java.util.Enumeration;

import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import MWC.GUI.Editable;
import MWC.GUI.Layer;

public class RadCharsPlottable extends BasePlottable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RadCharsPlottable(RadiatedCharacteristics chars, Layer parentLayer)
	{
		super(chars, parentLayer);
	}

	public Enumeration<Editable> elements()
	{
		return null;
	}
	
}
