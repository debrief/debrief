package org.mwc.debrief.satc_interface.data.wrappers;

import MWC.GUI.CanvasType;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

public class ContributionWrapper implements Plottable
{
	final BaseContribution _myCont;

	public ContributionWrapper(BaseContribution contribution)
	{
		_myCont = contribution;
	}

	public BaseContribution getContribution()
	{
		return _myCont;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public String getName()
	{
		return _myCont.getName();
	}

	@Override
	public boolean hasEditor()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EditorType getInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(Plottable arg0)
	{
		ContributionWrapper him = (ContributionWrapper) arg0;
		return this.getContribution().compareTo(him.getContribution());
	}

	@Override
	public void paint(CanvasType dest)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public WorldArea getBounds()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getVisible()
	{
		return _myCont.isActive();
	}

	@Override
	public void setVisible(boolean val)
	{
		_myCont.setActive(val);
	}

	@Override
	public double rangeFrom(WorldLocation other)
	{
		return Plottables.INVALID_RANGE;
	}
}