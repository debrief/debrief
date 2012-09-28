package org.mwc.debrief.core.gpx.mappers;

import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.gpx.ColourType;
import org.mwc.debrief.core.gpx.LayerType;
import org.mwc.debrief.core.gpx.LayersType;
import org.mwc.debrief.core.gpx.LineType;
import org.mwc.debrief.core.gpx.LocationType.ShortLocation;
import org.mwc.debrief.core.gpx.PlotType;
import org.mwc.debrief.core.gpx.SessionType;
import org.mwc.debrief.core.loaders.DebriefJaxbContextAware;
import org.w3c.dom.Node;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GenericData.WorldLocation;

import com.topografix.gpx.v11.ExtensionsType;
import com.topografix.gpx.v11.GpxType;

/**
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date September 12.9, 2012
 * @category gpx
 * 
 */
public class LineMapper implements DebriefJaxbContextAware
{
	private JAXBContext debriefContext;

	private final LayerType getLayer(PlotType plotType)
	{
		try
		{
			SessionType sessionType = plotType.getSession();
			LayersType layersType = sessionType.getLayers();

			return layersType.getLayer();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public ShapeWrapper fromGpx(GpxType gpx)
	{
		ExtensionsType extensions = gpx.getExtensions();
		if (extensions != null)
		{
			List<Object> any = extensions.getAny();
			Unmarshaller unmarshaller;
			try
			{
				unmarshaller = debriefContext.createUnmarshaller();
				Object object = unmarshaller.unmarshal((Node) any.get(0));
				PlotType plotExtension = (PlotType) JAXBIntrospector.getValue(object);
				LayerType layerType = getLayer(plotExtension);
				if (layerType != null)
				{
					mapLine(layerType);
				}
				System.out.println();
			}
			catch (JAXBException e)
			{
				CorePlugin.logError(Status.ERROR, "Error while mapping Line from GPX", e);
			}
		}
		return null;
	}

	private ShapeWrapper mapLine(LayerType layerType)
	{
		LineType lineType = layerType.getLine();
		if (lineType != null)
		{
			ColourType colourType = lineType.getColour();
			ShortLocation topLeft = lineType.getTl().getShortLocation();
			WorldLocation tlWl = new WorldLocation(topLeft.getLat(), topLeft.getLong(), topLeft.getDepth());
			ShortLocation bottomRight = lineType.getBr().getShortLocation();
			WorldLocation btWl = new WorldLocation(bottomRight.getLat(), bottomRight.getLong(), bottomRight.getDepth());

			MWC.GUI.Shapes.LineShape line = new MWC.GUI.Shapes.LineShape(tlWl, btWl);

			BigInteger lineThickness = lineType.getLineThickness();
			if (lineThickness != null)
			{
				line.setLineWidth(lineThickness.intValue());
			}
			line.setVisible(lineType.isVisible());
			line.setArrowAtEnd(lineType.isArrowAtEnd());

			ShapeWrapper sw = new ShapeWrapper(lineType.getLabel(), line, GpxUtil.resolveColor(colourType), null);
			sw.setLabel(lineType.getLabel());
			sw.setLabelVisible(lineType.isLabelVisible());

			sw.setLabelLocation(GpxUtil.resolveLabelLocation(lineType.getLabelLocation()));

			sw.setFont(GpxUtil.resolveFont(lineType.getFont()));
		}
		return null;
	}

	@Override
	public void setJaxbContext(JAXBContext ctx)
	{
		debriefContext = ctx;
	}
}
