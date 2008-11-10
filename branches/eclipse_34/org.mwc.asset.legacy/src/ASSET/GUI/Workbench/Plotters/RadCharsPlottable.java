package ASSET.GUI.Workbench.Plotters;

import java.util.*;

import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
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

	public Enumeration elements()
	{
		Enumeration res = null;

		RadiatedCharacteristics chars = (RadiatedCharacteristics) super.getModel();
		Collection mediums = chars.getMediums();
		res = new MWC.GUI.Plottables.IteratorWrapper(mediums.iterator());

		return res;
	}
	
	
}
