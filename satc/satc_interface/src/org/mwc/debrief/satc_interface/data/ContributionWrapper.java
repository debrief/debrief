package org.mwc.debrief.satc_interface.data;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

import MWC.GUI.Editable;

public class ContributionWrapper implements Editable
{

	private final BaseContribution _myCont;

	public ContributionWrapper(BaseContribution contribution)
	{
		_myCont = contribution;
	}
	
	@Override
	public String getName()
	{
		return _myCont.getName();
	}

	@Override
	public boolean hasEditor()
	{
		return false;
	}

	@Override
	public EditorType getInfo()
	{
		return null;
	}

}
