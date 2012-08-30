package org.mwc.debrief.core.gpx.mappers;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

import Debrief.Wrappers.FixWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

import com.topografix.gpx.v11.WptType;

/**
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date August 21, 2012
 * @category gpx
 * 
 */
public class FixMapper
{
	private static final BigDecimal MINUS_ONE = new BigDecimal("-1");

	public FixWrapper fromGpx(WptType trackType)
	{
		Fix fix = new Fix();

		WorldLocation val = new WorldLocation(trackType.getLat(), trackType.getLon(), convertElevationToDepth(trackType.getEle()));
		fix.setLocation(val);

		XMLGregorianCalendar time = trackType.getTime();
		GregorianCalendar calendar = time.toGregorianCalendar();
		fix.setTime(new HiResDate(calendar.getTime()));// TODO handle milli secs

		FixWrapper trackPoint = new FixWrapper(fix);

		return trackPoint;
	}

	/**
	 * Debrief and its datasets currently represent the 'z' dimension as Depth.
	 * GPX has elevation, so clearly we'll need to invert this data - positive
	 * depths in Debrief will be stored as negative elevations.
	 */
	private BigDecimal convertElevationToDepth(BigDecimal elevation)
	{
		return elevation.multiply(MINUS_ONE);
	}

	/*
	 * private BigDecimal convertDepthToElevation(BigDecimal depth) { return
	 * depth.multiply(MINUS_ONE); }
	 */
}
