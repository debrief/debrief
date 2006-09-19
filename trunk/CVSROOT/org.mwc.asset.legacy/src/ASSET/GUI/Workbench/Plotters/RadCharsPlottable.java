package ASSET.GUI.Workbench.Plotters;

import java.util.*;

import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;

public class RadCharsPlottable extends BasePlottable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RadCharsPlottable(RadiatedCharacteristics chars)
	{
		super(chars);
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
