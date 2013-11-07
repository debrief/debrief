package org.mwc.debrief.satc_interface.data.wrappers;

import MWC.GUI.CanvasType;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

public class ContributionWrapper implements Plottable
{
	final BaseContribution _myCont;
	
	protected EditorType _myEditor;

	
	HiResDate _start;
	HiResDate _end;

	public ContributionWrapper(BaseContribution contribution)
	{
		_myCont = contribution;
	}

	public BaseContribution getContribution()
	{
		return _myCont;
	}
	
	

	public HiResDate getStart()
	{
		return _start;
	}

	public void setStart(HiResDate start)
	{
		this._start = start;
	}

	public HiResDate getEnd()
	{
		return _end;
	}

	public void setEnd(HiResDate end)
	{
		this._end = end;
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
	
	public void setName(final String name)
	{
		_myCont.setName(name);
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

	@Override
	public int compareTo(Plottable arg0)
	{
		ContributionWrapper him = (ContributionWrapper) arg0;
		return this.getContribution().compareTo(him.getContribution());
	}

	@Override
	public void paint(CanvasType dest)
	{
	}

	@Override
	public WorldArea getBounds()
	{
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