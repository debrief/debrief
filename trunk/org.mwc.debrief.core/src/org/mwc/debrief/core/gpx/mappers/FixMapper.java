package org.mwc.debrief.core.gpx.mappers;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.gpx.FixExtensionType;
import org.mwc.debrief.core.loaders.DebriefJaxbContextAware;
import org.w3c.dom.Node;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

import com.topografix.gpx.v10.Gpx.Trk.Trkseg.Trkpt;
import com.topografix.gpx.v10.ObjectFactory;
import com.topografix.gpx.v11.ExtensionsType;
import com.topografix.gpx.v11.WptType;

/**
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date August 21, 2012
 * @category gpx
 * 
 */
public class FixMapper implements DebriefJaxbContextAware
{
	private JAXBContext debriefContext;
	private static final BigDecimal MINUS_ONE = new BigDecimal("-1");
	private static final ObjectFactory GPX_1_0_OBJ_FACTORY = new ObjectFactory();
	private static DatatypeFactory df;

	static
	{
		try
		{
			df = DatatypeFactory.newInstance();
		}
		catch (final DatatypeConfigurationException dce)
		{
			throw new IllegalStateException("Exception while obtaining DatatypeFactory instance. Can't marshall/unmarshall GPX documents.", dce);
		}
	}

	/**
	 * Extract a fix from the supplied data object
	 * 
	 * @param trackType
	 *          an object read in from GPX
	 * @param previousFix
	 *          the value of the previously read-in fix, used to calc course and
	 *          speed, when necessary
	 * 
	 * @category gpx11
	 */
	public FixWrapper fromGpx(final WptType trackType, final FixWrapper previousFix)
	{
		final Fix fix = new Fix();

		// we may not have a depth, better check
		BigDecimal ele = trackType.getEle();
		if (ele == null)
		{
			ele = new BigDecimal(0);
		}

		final WorldLocation val = new WorldLocation(trackType.getLat(), trackType.getLon(), convertElevationToDepth(ele));
		fix.setLocation(val);

		HiResDate theDate;
		final XMLGregorianCalendar time = trackType.getTime();
		if (time != null)
		{
			final GregorianCalendar calendar = time.toGregorianCalendar();
			theDate = new HiResDate(calendar.getTime());
		}
		else
		{
			CorePlugin.logError(Status.WARNING, "GPX Data is missing time data. current date to be used", null);
			theDate = new HiResDate();
		}
		fix.setTime(theDate);

		// also have a go at the heading

		final FixWrapper trackPoint = new FixWrapper(fix);

		final ExtensionsType extensions = trackType.getExtensions();
		if (extensions != null)
		{
			final List<Object> any = extensions.getAny();
			Unmarshaller unmarshaller;
			try
			{
				unmarshaller = debriefContext.createUnmarshaller();
				final Object object = unmarshaller.unmarshal((Node) any.get(0));
				final FixExtensionType fixExtension = (FixExtensionType) JAXBIntrospector.getValue(object);

				trackPoint.setCourse(MWCXMLReader.readThisDouble(fixExtension.getCourse()));
				trackPoint.setLabel(fixExtension.getLabel());
				trackPoint.setSpeed(MWCXMLReader.readThisDouble(fixExtension.getSpeed()));
				final LocationPropertyEditor locationConverter = new LocationPropertyEditor();
				locationConverter.setAsText(fixExtension.getLabelLocation().value());
				trackPoint.setLabelLocation((Integer) locationConverter.getValue());
				trackPoint.setArrowShowing(fixExtension.isArrowShowing());
				trackPoint.setLabelShowing(fixExtension.isLabelShowing());
				trackPoint.setLineShowing(fixExtension.isLineShowing());
				trackPoint.setSymbolShowing(fixExtension.isSymbolShowing());
				trackPoint.setVisible(fixExtension.isVisible());
			}
			catch (final JAXBException e)
			{
				CorePlugin.logError(Status.ERROR, "Error while mapping Track from GPX", e);
			}
			catch (final ParseException pe)
			{
				CorePlugin.logError(Status.ERROR, "Error while mapping Track from GPX", pe);
			}
		}

		// have we managed to sort out the course & speed?
		// have a go at the speed
		// if(previousFix != null)
		// {
		// WorldVector fromLast = null;
		// if(previousFix != null)
		// fromLast = val.subtract(previousFix.getLocation());
		//
		// long timeDiffMillis = theDate.getDate().getTime() -
		// previousFix.getTime().getDate().getTime();
		// WorldDistance dist = new WorldDistance( fromLast.getRange(),
		// WorldDistance.DEGS);
		// double speedYps = (dist.getValueIn(WorldDistance.YARDS)) /
		// (timeDiffMillis / 1000d);
		// fix.setSpeed(speedYps);
		// }

		return trackPoint;
	}

	/**
	 * @category gpx10
	 */
	public Trkpt toGpx10(final FixWrapper fixWrapper)
	{
		final Trkpt gpxPoint = GPX_1_0_OBJ_FACTORY.createGpxTrkTrksegTrkpt();

		gpxPoint.setLat(BigDecimal.valueOf(fixWrapper.getFix().getLocation().getLat()));
		gpxPoint.setLon(BigDecimal.valueOf(fixWrapper.getFix().getLocation().getLong()));
		gpxPoint.setEle(convertDepthToElevation(BigDecimal.valueOf(fixWrapper.getFix().getLocation().getDepth())));

		final HiResDate hiResDate = fixWrapper.getTime();
		final GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(hiResDate.getDate().getTime());
		final XMLGregorianCalendar gpxTime = df.newXMLGregorianCalendar(gc);
		// disable fractional second as this is never used
		gpxTime.setFractionalSecond(null);
		gpxPoint.setTime(gpxTime.normalize());
		gpxPoint.setCourse(BigDecimal.valueOf(fixWrapper.getFix().getCourse()).setScale(4, BigDecimal.ROUND_CEILING));
		gpxPoint.setSpeed(BigDecimal.valueOf(fixWrapper.getSpeed()).setScale(4, BigDecimal.ROUND_CEILING));
		//
		// ExtensionsType extensionsType = objectFactory.createExtensionsType();
		// List<Object> any = extensionsType.getAny();
		//
		// FixExtensionType fixExtensionType =
		// DEBRIEF_OBJ_FACTORY.createFixExtensionType();
		// any.add(DEBRIEF_OBJ_FACTORY.createFixExtension(fixExtensionType));
		return gpxPoint;
	}

	/**
	 * Debrief and its datasets currently represent the 'z' dimension as Depth.
	 * GPX has elevation, so clearly we'll need to invert this data - positive
	 * depths in Debrief will be stored as negative elevations.
	 */
	private BigDecimal convertElevationToDepth(final BigDecimal elevation)
	{
		return elevation.multiply(MINUS_ONE);
	}

	@Override
	public void setJaxbContext(final JAXBContext ctx)
	{
		debriefContext = ctx;
	}

	private BigDecimal convertDepthToElevation(final BigDecimal depth)
	{
		return depth.multiply(MINUS_ONE);
	}

	/**
	 * Extract a fix from the gpx 1.0
	 * 
	 * @category gpx10
	 */
	public FixWrapper fromGpx10(final Trkpt pointType, final FixWrapper previousFix)
	{
		final Fix fix = new Fix();

		// we may not have a depth, better check
		BigDecimal ele = pointType.getEle();
		if (ele == null)
		{
			ele = new BigDecimal(0);
		}

		final WorldLocation val = new WorldLocation(pointType.getLat(), pointType.getLon(), convertElevationToDepth(ele));
		fix.setLocation(val);

		HiResDate theDate;
		final XMLGregorianCalendar time = pointType.getTime();
		if (time != null)
		{
			final GregorianCalendar calendar = time.toGregorianCalendar();
			theDate = new HiResDate(calendar.getTime());
		}
		else
		{
			CorePlugin.logError(Status.WARNING, "GPX Data is missing time data. current date to be used", null);
			theDate = new HiResDate();
		}
		fix.setTime(theDate);

		final BigDecimal course = pointType.getCourse();
		if (course != null)
		{
			fix.setCourse(course.doubleValue());
		}

		final BigDecimal speed = pointType.getSpeed();
		if (speed != null)
		{
			fix.setSpeed(speed.doubleValue());
		}
		final FixWrapper trackPoint = new FixWrapper(fix);

		return trackPoint;
	}
}
