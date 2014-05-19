package org.mwc.debrief.core.gpx.mappers;

import java.awt.Color;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.gpx.LocationType;
import org.mwc.debrief.core.gpx.LocationType.ShortLocation;
import org.mwc.debrief.core.gpx.TextlabelType;
import org.mwc.debrief.core.loaders.DebriefJaxbContextAware;
import org.w3c.dom.Node;

import Debrief.Wrappers.LabelWrapper;
import MWC.GenericData.WorldLocation;

import com.topografix.gpx.v11.ExtensionsType;
import com.topografix.gpx.v11.GpxType;

/**
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date September 27, 2012
 * @category gpx
 * 
 */
public class TextLabelMapper implements DebriefJaxbContextAware
{
	private JAXBContext debriefContext;

	public LabelWrapper fromGpx(final GpxType gpx)
	{
		final ExtensionsType extensions = gpx.getExtensions();
		LabelWrapper lw = null;
		if (extensions != null)
		{
			final List<Object> any = extensions.getAny();
			Unmarshaller unmarshaller;
			try
			{
				unmarshaller = debriefContext.createUnmarshaller();
				final Object object = unmarshaller.unmarshal((Node) any.get(0));
				final TextlabelType textLabelExtension = (TextlabelType) JAXBIntrospector.getValue(object);

				final LocationType centreType = textLabelExtension.getCentre();

				WorldLocation center = null;
				if (centreType != null)
				{
					final ShortLocation shortLocation = centreType.getShortLocation();

					if (shortLocation != null)
					{
						center = new WorldLocation(shortLocation.getLat(), shortLocation.getLong(), shortLocation.getDepth());
					}
				}
				final Color color = GpxUtil.resolveColor(textLabelExtension.getColour());
				lw = new LabelWrapper(textLabelExtension.getLabel(), center, color);
				lw.setFont(GpxUtil.resolveFont(textLabelExtension.getFont()));
				lw.setLabelLocation(GpxUtil.resolveLabelLocation(textLabelExtension.getLabelLocation()));
				lw.setLabelVisible(textLabelExtension.isLabelVisible());
				lw.setSymbolVisible(textLabelExtension.isSymbolVisible());
				lw.setVisible(textLabelExtension.isVisible());
				lw.setSymbolSize(GpxUtil.resolveSymbolScale(textLabelExtension.getScale()));
				// lw.setSymbolType(val) TODO
			}
			catch (final JAXBException e)
			{
				CorePlugin.logError(Status.ERROR, "Error while mapping textLabel from GPX", e);
			}
		}
		return lw;
	}

	@Override
	public void setJaxbContext(final JAXBContext ctx)
	{
		debriefContext = ctx;
	}
}
