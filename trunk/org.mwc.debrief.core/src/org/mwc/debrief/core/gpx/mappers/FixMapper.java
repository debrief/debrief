package org.mwc.debrief.core.gpx.mappers;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
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

	/** extract a fix from the supplied data object
	 * 
	 * @param trackType an object read in from GPX
	 * @param previousFix the value of the previously read-in fix, used to calc course and speed, when necessary
	 * @return
	 */
	public FixWrapper fromGpx(WptType trackType, FixWrapper previousFix)
	{
		Fix fix = new Fix();

		// we may not have a depth, better check
		BigDecimal ele = trackType.getEle();
		if (ele == null)
			ele = new BigDecimal(0);

		WorldLocation val = new WorldLocation(trackType.getLat(),
				trackType.getLon(), convertElevationToDepth(ele));
		fix.setLocation(val);

		HiResDate theDate;
		XMLGregorianCalendar time = trackType.getTime();
		if (time != null)
		{
			GregorianCalendar calendar = time.toGregorianCalendar();
			theDate = new HiResDate(calendar.getTime());
		}
		else
		{
			CorePlugin.logError(Status.WARNING,
					"GPX Data is missing time data. current date to be used", null);
			theDate = new HiResDate();
		}
		fix.setTime(theDate);

		// also have a go at the heaidng

		FixWrapper trackPoint = new FixWrapper(fix);

		ExtensionsType extensions = trackType.getExtensions();
		if (extensions != null)
		{
			List<Object> any = extensions.getAny();
			Unmarshaller unmarshaller;
			try
			{
				unmarshaller = debriefContext.createUnmarshaller();
				Object object = unmarshaller.unmarshal((Node) any.get(0));
				FixExtensionType fixExtension = (FixExtensionType) JAXBIntrospector
						.getValue(object);

				trackPoint.setCourse(Double.valueOf(fixExtension.getCourse())
						.doubleValue());
				trackPoint.setLabel(fixExtension.getLabel());
				trackPoint.setSpeed(Double.valueOf(fixExtension.getSpeed())
						.doubleValue());
				LocationPropertyEditor locationConverter = new LocationPropertyEditor();
				locationConverter.setAsText(fixExtension.getLabelLocation().value());
				trackPoint.setLabelLocation((Integer) locationConverter.getValue());
				trackPoint.setArrowShowing(fixExtension.isArrowShowing());
				trackPoint.setLabelShowing(fixExtension.isLabelShowing());
				trackPoint.setLineShowing(fixExtension.isLineShowing());
				trackPoint.setSymbolShowing(fixExtension.isSymbolShowing());
				trackPoint.setVisible(fixExtension.isVisible());
			}
			catch (JAXBException e)
			{
				CorePlugin.logError(Status.ERROR, "Error while mapping Track from GPX",
						e);
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
	 * Debrief and its datasets currently represent the 'z' dimension as Depth.
	 * GPX has elevation, so clearly we'll need to invert this data - positive
	 * depths in Debrief will be stored as negative elevations.
	 */
	private BigDecimal convertElevationToDepth(BigDecimal elevation)
	{
		return elevation.multiply(MINUS_ONE);
	}

	@Override
	public void setJaxbContext(JAXBContext ctx)
	{
		debriefContext = ctx;
	}
	/*
	 * private BigDecimal convertDepthToElevation(BigDecimal depth) { return
	 * depth.multiply(MINUS_ONE); }
	 */
}
