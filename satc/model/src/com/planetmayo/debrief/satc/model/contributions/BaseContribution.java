package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;

import com.planetmayo.debrief.satc.model.ModelObject;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public abstract class BaseContribution extends ModelObject implements
		Comparable<BaseContribution>
{
	private static final long serialVersionUID = 1L;

	public static final String WEIGHT = "weight";
	public static final String START_DATE = "startDate";
	public static final String NAME = "name";
	public static final String FINISH_DATE = "finishDate";
	public static final String ACTIVE = "active";
	public static final String ESTIMATE = "estimate";
	
	public static final String HARD_CONSTRAINTS = "hardConstraint";

	protected String _name;
	protected boolean _active = true;
	protected int _weight;
	protected Date _startDate;
	protected Date _finishDate;

	protected BaseContribution()
	{
		// give a default name
		setName("auto_id:" + new Date().getTime());
	}

	/**
	 * apply this contribution to the supplied Problem Space
	 * 
	 * @param space
	 *          the object that we're going to bound
	 */
	public abstract void actUpon(ProblemSpace space)
			throws IncompatibleStateException;
	
	/** generate the error for this route
	 * 
	 */
	public double calculateErrorScoreFor(CoreRoute route)
	{
		return 0;
	}

	/** are my constraints valid for the supplied period?
	 * 
	 * @param route
	 * @return
	 */
	protected boolean validFor(CoreRoute route)
	{
		return true;
	}
	
	/**
	 * check if this specified time is between our start/finish times, if we have
	 * them
	 * 
	 * @param thisDate
	 *          the date we're checking
	 * @return
	 */
	protected boolean checkInDatePeriod(final Date thisDate)
	{
		long millis = thisDate.getTime();
		if (getStartDate() != null && millis < getStartDate().getTime())
		{
			return false;
		}
		if (getFinishDate() != null && millis > getFinishDate().getTime())
		{
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(BaseContribution o)
	{
		// ok, what type am I?
		int myScore = getScore();
		int hisScore = o.getScore();
		if (myScore == hisScore)
		{
			// try the class names first, to group them
			String myClass = this.getClass().getName();
			String hisClass = o.getClass().getName();
			if (myClass.equals(hisClass))
			{
				// ha-they must be equal, compare the names
				return this.getName().compareTo(o.getName());
			}
			else
			{
				return myClass.compareTo(hisClass);
			}
		}
		return myScore - hisScore;
	}

	public abstract ContributionDataType getDataType();

	public Date getFinishDate()
	{
		return _finishDate;
	}

	public String getName()
	{
		return _name;
	}

	private int getScore()
	{
		switch (getDataType())
		{
		case MEASUREMENT:
			return 0;
		case FORECAST:
			return 1;
		default:
			return 2;
		}
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
		boolean oldActive = _active;
		this._active = active;
		firePropertyChange(ACTIVE, oldActive, active);
	}

	public void setFinishDate(Date finishDate)
	{
		Date oldFinishDate = _finishDate;
		this._finishDate = finishDate;
		firePropertyChange(FINISH_DATE, oldFinishDate, finishDate);
	}

	public void setName(String name)
	{
		String oldName = _name;
		_name = name;
		firePropertyChange(NAME, oldName, name);
	}

	public void setStartDate(Date startDate)
	{
		Date oldStartDate = _startDate;
		this._startDate = startDate;
		firePropertyChange(START_DATE, oldStartDate, startDate);
	}

	public void setWeight(int weight)
	{
		int oldWeight = _weight;
		this._weight = weight;
		firePropertyChange(WEIGHT, oldWeight, weight);
	}
}