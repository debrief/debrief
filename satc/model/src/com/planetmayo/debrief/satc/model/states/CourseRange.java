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
			maxCourse += 2 * Math.PI;
		}
		this._min = minCourse;
		this._max = maxCourse;
	}

	@Override
	public void constrainTo(CourseRange sTwo) throws IncompatibleStateException
	{
		// right, there's a chance that the two ranges are in different cycles (one
		// is -5 to +5, the other is 355 to 365) , so put them in the same domain
		final double newUpper, newLower;
		if (sTwo._max < _min)
		{
			newLower = sTwo._min + Math.PI * 2;
			newUpper = sTwo._max + Math.PI * 2;
		}
		else
		{
			newLower = sTwo._min;
			newUpper = sTwo._max;
		}

		// note: we're using _min and _max because our getter mangles the value to
		// make it human readable
		_min = Math.max(_min, newLower);
		_max = Math.min(_max, newUpper);

		// aah, but are are we now in the wrong domain?
		if (_min > Math.PI * 2)
		{
			// yes, put us back into 0..360
			_min -= Math.PI * 2;
			_max -= Math.PI * 2;
		}

		// aah, but what if we're now impossible?
		if (_max < _min)
			throw new IncompatibleStateException("Incompatible states", this, sTwo);
	}

	public double getMax()
	{
		return _max > 2 * Math.PI ? _max - 2 * Math.PI : _max;
	}

	public double getMin()
	{
		return _min < 0 ? _min + 2 * Math.PI : _min;
	}
}
