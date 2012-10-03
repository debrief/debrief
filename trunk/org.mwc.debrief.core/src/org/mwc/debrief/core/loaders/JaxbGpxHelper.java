package org.mwc.debrief.core.loaders;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.gpx.mappers.TrackMapper;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;

import com.topografix.gpx.v11.GpxType;

/**
 * JAXB based implementation for marhsalling and unmarshalling. EclipseLink is
 * used as the MOXy implementation. Refer to {@link TODO -link my blog post} on
 * how to install and configure JAXB for eclipse.
 * 
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date August 21, 2012
 * @category gpx
 * 
 */
public class JaxbGpxHelper implements GpxHelper
{
	private static JAXBContext GPX_JAXB_CTX;
	private static JAXBContext DEBRIEF_EXTENSIONS_JAXB_CTX;

	private final TrackMapper trackMapper = new TrackMapper();

	static
	{
		try
		{
			GPX_JAXB_CTX = JAXBContext.newInstance("com.topografix.gpx.v11");
			DEBRIEF_EXTENSIONS_JAXB_CTX = JAXBContext.newInstance("org.mwc.debrief.core.gpx");
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}

	public JaxbGpxHelper()
	{
		trackMapper.setJaxbContext(DEBRIEF_EXTENSIONS_JAXB_CTX);
	}

	@Override
	public Layers unmarshall(InputStream gpxStream, Layers theLayers)
	{
		if (theLayers == null)
		{
			theLayers = new Layers();
		}
		try
		{
			Unmarshaller unmarshaller = GPX_JAXB_CTX.createUnmarshaller();

			GpxType gpxType = (GpxType) JAXBIntrospector.getValue(unmarshaller.unmarshal(gpxStream));

			/*
			 * For our first trial I'm happy for the unmarshall method to return a
			 * Layers object. It is expect that in our trial this object will have a
			 * Track and a Layer containing a couple of shapes
			 */
			List<TrackWrapper> tracks = trackMapper.fromGpx(gpxType);

			for (TrackWrapper track : tracks)
			{
				theLayers.addThisLayer(track);
			}
		}
		catch (JAXBException e)
		{
			CorePlugin.logError(Status.ERROR, "Error while unmarshalling GPX", e);
			return null;
		}

		return theLayers;
	}

	@Override
	public void marshall(Layers from, File saveToGpx)
	{
		// TODO Auto-generated method stub

	}

	public boolean isValid(InputStream gpxStream)
	{
		return true;
	}

}
