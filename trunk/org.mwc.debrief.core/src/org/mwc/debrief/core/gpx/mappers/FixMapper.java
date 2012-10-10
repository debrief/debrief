package org.mwc.debrief.core.gpx.mappers;

import java.math.BigDecimal;
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

import com.topografix.gpx.v11.ExtensionsType;
import com.topografix.gpx.v11.ObjectFactory;
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
	private static final org.mwc.debrief.core.gpx.ObjectFactory DEBRIEF_OBJ_FACTORY = new org.mwc.debrief.core.gpx.ObjectFactory();
	private static DatatypeFactory df;

	static
	{
		try
		{
			df = DatatypeFactory.newInstance();
		}
		catch (DatatypeConfigurationException dce)
		{
			throw new IllegalStateException("Exception while obtaining DatatypeFactory instance", dce);
		}
	}

	public FixWrapper fromGpx(WptType trackType)
	{
		Fix fix = new Fix();

		WorldLocation val = new WorldLocation(trackType.getLat(), trackType.getLon(), convertElevationToDepth(trackType.getEle()));
		fix.setLocation(val);

		XMLGregorianCalendar time = trackType.getTime();
		GregorianCalendar calendar = time.toGregorianCalendar();
		fix.setTime(new HiResDate(calendar.getTime()));// TODO handle milli secs

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
				FixExtensionType fixExtension = (FixExtensionType) JAXBIntrospector.getValue(object);

				trackPoint.setCourse(Double.valueOf(fixExtension.getCourse()).doubleValue());
				trackPoint.setLabel(fixExtension.getLabel());
				trackPoint.setSpeed(Double.valueOf(fixExtension.getSpeed()).doubleValue());
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
				CorePlugin.logError(Status.ERROR, "Error while mapping Track from GPX", e);
			}
		}

		return trackPoint;
	}

	public WptType toGpx(FixWrapper fixWrapper)
	{
		ObjectFactory objectFactory = new ObjectFactory();
		WptType gpxPoint = objectFactory.createWptType();

		gpxPoint.setLat(BigDecimal.valueOf(fixWrapper.getFix().getLocation().getLat()));
		gpxPoint.setLon(BigDecimal.valueOf(fixWrapper.getFix().getLocation().getLong()));
		gpxPoint.setEle(convertDepthToElevation(BigDecimal.valueOf(fixWrapper.getFix().getLocation().getDepth())));

		HiResDate hiResDate = fixWrapper.getTime();
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(hiResDate.getDate().getTime());
		XMLGregorianCalendar gpxTime = df.newXMLGregorianCalendar(gc);
		// gpxTime.setFractionalSecond(BigDecimal.valueOf(hiResDate.getMicros()));
		// TODO map micro secs
		gpxPoint.setTime(gpxTime.normalize());
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
	private BigDecimal convertElevationToDepth(BigDecimal elevation)
	{
		return elevation.multiply(MINUS_ONE);
	}

	@Override
	public void setJaxbContext(JAXBContext ctx)
	{
		debriefContext = ctx;
	}

	private BigDecimal convertDepthToElevation(BigDecimal depth)
	{
		return depth.multiply(MINUS_ONE);
	}
}
