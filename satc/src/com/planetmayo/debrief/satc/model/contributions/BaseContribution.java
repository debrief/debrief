package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;

import com.planetmayo.debrief.satc.model.ModelObject;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public abstract class BaseContribution extends ModelObject
{

	protected String _name;
	protected boolean _active;
	protected int _weight;
	protected Date _startDate;
	protected Date _finishDate;

	/**
	 * apply this contribution to the supplied Problem Space
	 * 
	 * @param space
	 *          the object that we're going to bound
	 */
	public abstract void actUpon(ProblemSpace space);

	public Date getFinishDate()
	{
		return _finishDate;
	}

	/**
	 * provide a formatted string representing the hard constraints
	 * 
	 * @return summary of constraints
	 */
	public abstract String getHardConstraints();

	public String getName()
	{
		return _name;
	}

	public Date getStartDate()
	{
		return _startDate;
	}

	public int getWeight()
	{
		return _weight;
	}

	public boolean isActive()
	{
		return _active;
	}

	public void setActive(boolean active)
	{
		firePropertyChange("active", _active, active);
		this._active = active;
	}

	public void setFinishDate(Date finishDate)
	{
		firePropertyChange("finishDate", _finishDate, finishDate);
		this._finishDate = finishDate;
	}

	public void setName(String name)
	{
		firePropertyChange("name", _name, name);
		_name = name;
	}

	public void setStartDate(Date startDate)
	{
		firePropertyChange("startDate", _startDate, startDate);
		this._startDate = startDate;
	}

	public void setWeight(int weight)
	{
		firePropertyChange("weight", _weight, weight);
		this._weight = weight;
	}
}
