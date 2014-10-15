/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

	private final LayerType getLayer(final PlotType plotType)
	{
		try
		{
			final SessionType sessionType = plotType.getSession();
			final LayersType layersType = sessionType.getLayers();

			return layersType.getLayer();
		}
		catch (final Exception e)
		{
			return null;
		}
	}

	public ShapeWrapper fromGpx(final GpxType gpx)
	{
		final ExtensionsType extensions = gpx.getExtensions();
		if (extensions != null)
		{
			final List<Object> any = extensions.getAny();
			Unmarshaller unmarshaller;
			try
			{
				unmarshaller = debriefContext.createUnmarshaller();
				final Object object = unmarshaller.unmarshal((Node) any.get(0));
				final PlotType plotExtension = (PlotType) JAXBIntrospector.getValue(object);
				final LayerType layerType = getLayer(plotExtension);
				if (layerType != null)
				{
					mapLine(layerType);
				}
				System.out.println();
			}
			catch (final JAXBException e)
			{
				CorePlugin.logError(Status.ERROR, "Error while mapping Line from GPX", e);
			}
		}
		return null;
	}

	private ShapeWrapper mapLine(final LayerType layerType)
	{
		final LineType lineType = layerType.getLine();
		if (lineType != null)
		{
			final ColourType colourType = lineType.getColour();
			final ShortLocation topLeft = lineType.getTl().getShortLocation();
			final WorldLocation tlWl = new WorldLocation(topLeft.getLat(), topLeft.getLong(), topLeft.getDepth());
			final ShortLocation bottomRight = lineType.getBr().getShortLocation();
			final WorldLocation btWl = new WorldLocation(bottomRight.getLat(), bottomRight.getLong(), bottomRight.getDepth());

			final MWC.GUI.Shapes.LineShape line = new MWC.GUI.Shapes.LineShape(tlWl, btWl);

			final BigInteger lineThickness = lineType.getLineThickness();
			if (lineThickness != null)
			{
				line.setLineWidth(lineThickness.intValue());
			}
			line.setVisible(lineType.isVisible());
			line.setArrowAtEnd(lineType.isArrowAtEnd());

			final ShapeWrapper sw = new ShapeWrapper(lineType.getLabel(), line, GpxUtil.resolveColor(colourType), null);
			sw.setLabel(lineType.getLabel());
			sw.setLabelVisible(lineType.isLabelVisible());

			sw.setLabelLocation(GpxUtil.resolveLabelLocation(lineType.getLabelLocation()));

			sw.setFont(GpxUtil.resolveFont(lineType.getFont()));
		}
		return null;
	}

	@Override
	public void setJaxbContext(final JAXBContext ctx)
	{
		debriefContext = ctx;
	}
}
