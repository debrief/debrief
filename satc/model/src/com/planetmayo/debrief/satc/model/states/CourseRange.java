package com.planetmayo.debrief.satc.model.states;

/**
 * class representing a set of Course bounds
 * 
 * @author ian
 * 
 */
public class CourseRange extends BaseRange<CourseRange>
{
	private double _min;
	private double _max;

	/**
	 * copy constructor
	 * 
	 * @param range
	 */
	public CourseRange(CourseRange range)
	{
		this(range.getMin(), range.getMax());
	}

	public CourseRange(double minCourse, double maxCourse)
	{
		if (minCourse > maxCourse) 
		{
			minCourse = minCourse - 2 * Math.PI;
		}
		this._min = minCourse;
		this._max = maxCourse;
	}

	@Override
	public void constrainTo(CourseRange sTwo) throws IncompatibleStateException
	{
		// note: we're using _min and _max because our getter mangles the value to
		// make it human readable
		_min = Math.max(_min, sTwo._min);
		_max = Math.min(_max, sTwo._max);

		// aah, but what if we're now impossible?
		if (_max < _min)
			throw new IncompatibleStateException("Incompatible states", this, sTwo);
	}

	@Override
	public String getConstraintSummary()
	{
		return "" + (int) Math.toDegrees(_min) + " - " + (int) Math.toDegrees(_max);
	}

	public double getMax()
	{
		return _max < 0 ? _max + 2 * Math.PI : _max;
	}

	public double getMin()
	{
		return _min < 0 ? _min + 2 * Math.PI : _min;
	}
}
