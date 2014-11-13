package org.mwc.debrief.satc_interface.data.wrappers;

import org.eclipse.core.runtime.Status;

import MWC.GUI.Editable;
import MWC.GUI.Layer;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.CoreMeasurementContribution;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

abstract public class CoreLayer_Wrapper<Contribution extends CoreMeasurementContribution> extends ContributionWrapper implements Layer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CoreLayer_Wrapper(BaseContribution contribution)
	{
		super(contribution);
	}


	@Override
	final public void exportShape()
	{
	}

	@Override
	final public void append(Layer other)
	{
	}

	@Override
	final public void setName(String val)
	{
		super.getContribution().setName(val);
	}
	

	@Override
	final public boolean hasEditor()
	{
		return true;
	}

	@Override
	final public boolean hasOrderedChildren()
	{
		return true;
	}

	@Override
	final public int getLineThickness()
	{
		return 0;
	}

	@Override
	final public void add(Editable point)
	{
		SATC_Activator.log(Status.ERROR,
				"Should not be adding items to this layer", null);
	}

	@Override
	final public void removeElement(Editable point)
	{

	}
}
