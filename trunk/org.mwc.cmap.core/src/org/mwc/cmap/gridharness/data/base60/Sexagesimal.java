package org.mwc.cmap.gridharness.data.base60;


public class Sexagesimal {

	private final double myDegrees;

	private final double myMinutes;

	private final double mySeconds;
	
	private final int myHemi;

	public Sexagesimal(final int degrees, final double minutes, final double seconds, final int hemi) {
		myDegrees = degrees;
		myMinutes = minutes;
		mySeconds = seconds;
		myHemi = hemi;
	}

	public int getHemi()
	{
		return myHemi;
	}
	
	public double getDegrees() {
		return myDegrees;
	}

	public double getSeconds() {
		return mySeconds;
	}

	public double getMinutes() {
		return myMinutes;
	}

	public double getCombinedDegrees() {
		return SexagesimalSupport.combineToDegrees(getDegrees(), getMinutes(), getSeconds(), getHemi());
	}

	public String format(final SexagesimalFormat format, final boolean forLongitudeNotLatitude) {
		return format.format(this, forLongitudeNotLatitude);
	}
}