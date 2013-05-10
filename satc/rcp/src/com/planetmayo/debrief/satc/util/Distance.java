package com.planetmayo.debrief.satc.util;

public class Distance
{

	private double shortestDistance;
	private double longestDistance;

	public Distance()
	{
	}

	public Distance(double shortestDistance, double longestDistance)
	{
		this.shortestDistance = shortestDistance;
		this.longestDistance = longestDistance;
	}

	public double getShortestDistance()
	{
		return shortestDistance;
	}

	public void setShortestDistance(double shortestDistance)
	{
		this.shortestDistance = shortestDistance;
	}

	public double getLongestDistance()
	{
		return longestDistance;
	}

	public void setLongestDistance(double longestDistance)
	{
		this.longestDistance = longestDistance;
	}
	
	public void apply(Distance distance)
	{
		shortestDistance = Math.min(shortestDistance, distance.getShortestDistance());
		longestDistance = Math.max(longestDistance, distance.getLongestDistance());
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Distance distance = (Distance) o;

		if (Double.compare(distance.longestDistance, longestDistance) != 0)	return false;
		if (Double.compare(distance.shortestDistance, shortestDistance) != 0)	return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result;
		long temp;
		temp = shortestDistance != +0.0d ? Double.doubleToLongBits(shortestDistance) : 0L;
		result = (int) (temp ^ (temp >>> 32));
		temp = longestDistance != +0.0d ? Double.doubleToLongBits(longestDistance) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public String toString()
	{
		return "Distance [shortestDistance=" + shortestDistance
				+ ", longestDistance=" + longestDistance + "]";
	}
}
