package com.planetmayo.debrief.satc.model.states;

/**
 * class representing a set of Course bounds
 * 
 * @author ian
 * 
 */
public class CourseRange extends BaseRange<CourseRange>
{
	private volatile double _min;
	private volatile double _max;

	public String toDebugString()
	{
		return (int) Math.toDegrees(_min) + " - " + (int) Math.toDegrees(_max);
	}

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

	@Override
	public int hashCode()
	{
		int result = (int) _min;
		result = 31 * result + (int) _max;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}
		CourseRange other = (CourseRange) obj;

		if (_max != other._max)
			return false;
		if (_min != other._min)
			return false;

		return true;
	}

	/**
	 * generate a course object that is the reverse of this one
	 * 
	 * @return
	 */
	public CourseRange generateInverse()
	{
		// special case. if it covers the full circle keep a full circle
		final CourseRange res;
		if (_max - _min == 2 * Math.PI)
			res = new CourseRange(this._min, this._max);
		else
		{
			// generate the inverse angles
			double newMin = this._min + Math.PI;
			double newMax = this._max + Math.PI;
			
			res = new CourseRange(newMin, newMax);
		}
		return res;
	}

	/** does the supplied course fit in my range?
	 * 
	 * @param speed the value to test
	 * @return  yes/no
	 */
	public boolean allows(double course)
	{
		// put the coursre into my domain
		while(course < _min)
			course += 2 * Math.PI;
		
		// and just check we're not too high
		while(course > _max)
			course -= 2 * Math.PI;
		
		// and test
		return (course >= _min) && (course <= _max);
	}	
}
