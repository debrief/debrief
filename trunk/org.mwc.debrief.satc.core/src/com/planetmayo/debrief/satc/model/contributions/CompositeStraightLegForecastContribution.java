package com.planetmayo.debrief.satc.model.contributions;

import java.beans.PropertyChangeListener;
import java.util.Date;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;

public class CompositeStraightLegForecastContribution extends
		StraightLegForecastContribution
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** the speed forecast
	 * 
	 */
	private final SpeedForecastContribution _speed = new SpeedForecastContribution();
	
	/** the course forecast
	 * 
	 */
	private final CourseForecastContribution _course = new CourseForecastContribution();

	
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		super.addPropertyChangeListener(listener);
		_course.addPropertyChangeListener(listener);
		_speed.addPropertyChangeListener(listener);

	}

	@Override
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener)
	{
		super.addPropertyChangeListener(propertyName, listener);
		_course.addPropertyChangeListener(propertyName, listener);
		_speed.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		super.removePropertyChangeListener(listener);
		_course.removePropertyChangeListener(listener);
		_speed.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener)
	{
		super.removePropertyChangeListener(propertyName, listener);
		_course.removePropertyChangeListener(propertyName, listener);
		_speed.removePropertyChangeListener(propertyName, listener);
	}

	@Override
	public void setFinishDate(final Date newFinishDate)
	{
		// set the parent value
		super.setFinishDate(newFinishDate);
		
		// now set the child values
		_course.setFinishDate(newFinishDate);
		_speed.setFinishDate(newFinishDate);
	}

	@Override
	public void setStartDate(final Date newStartDate)
	{
		// set the parent value
		super.setStartDate(newStartDate);

		// now set the child values
		_course.setStartDate(newStartDate);
		_speed.setStartDate(newStartDate);
	}

	/** get the course constraint
	 * 
	 * @return
	 */
	public CourseForecastContribution getCourse()
	{
		return _course;
	}
	
	/** get the speed constraint
	 * 
	 * @return
	 */
	public SpeedForecastContribution getSpeed()
	{
		return _speed;
	}

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// handle the straight leg component first
		super.actUpon(space);
		
		// now apply the constraints from the speed forecast
		_speed.actUpon(space);
		
		// and the course forecast
		_course.actUpon(space);
	}

	@Override
	protected double calcError(State thisState)
	{
		// combine the three errors
		return super.calcError(thisState) + _course.calcError(thisState) + _speed.calcError(thisState);
	}
	
	

}
