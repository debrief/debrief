package org.mwc.debrief.core.loaders;

import static org.mwc.debrief.core.loaders.GpxUtil.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.Status;
import org.jdom.Document;
import org.jdom.output.DOMOutputter;
import org.jdom.transform.JDOMSource;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.gpx.mappers.TrackMapper;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;

import com.topografix.gpx.v10.Gpx;
import com.topografix.gpx.v10.Gpx.Trk;
import com.topografix.gpx.v10.ObjectFactory;
import com.topografix.gpx.v11.GpxType;

/**
 * JAXB based implementation for marhsalling and unmarshalling. EclipseLink is
 * used as the MOXy implementation. <a href=
 * "http://stackoverflow.com/questions/7039493/jaxb-project-in-eclipse-indigo"
 * >Refer to</a> on how to install and configure JAXB for eclipse.
 * 
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date August 21, 2012
 * @category gpx
 * 
 */
public class JaxbGpxHelper implements GpxHelper
{
	private static JAXBContext GPX_1_1_JAXB_CTX;
	private static JAXBContext GPX_1_0_JAXB_CTX;
	private static JAXBContext DEBRIEF_EXTENSIONS_JAXB_CTX;

	private final TrackMapper trackMapper = new TrackMapper();
	private static final ObjectFactory GPX_1_0_OBJ_FACTORY = new ObjectFactory();

	static
	{
		try
		{
			GPX_1_1_JAXB_CTX = JAXBContext.newInstance("com.topografix.gpx.v11");
			GPX_1_0_JAXB_CTX = JAXBContext.newInstance("com.topografix.gpx.v10");
			DEBRIEF_EXTENSIONS_JAXB_CTX = JAXBContext.newInstance("org.mwc.debrief.core.gpx");
		}
		catch (JAXBException e)
		{
			throw new IllegalStateException("Exception while initialzing JAXB Context", e);
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
			JDOMSource source = (JDOMSource) getDocumentSource(gpxStream);

			boolean isGpx10 = isGpx10(source);
			boolean xmlValid = isValid(source, isGpx10);

			if (xmlValid)
			{
				Document document = source.getDocument();
				Unmarshaller unmarshaller;
				List<TrackWrapper> tracks = Collections.emptyList();

				if (isGpx10)
				{

					unmarshaller = GPX_1_0_JAXB_CTX.createUnmarshaller();
					com.topografix.gpx.v10.Gpx gpx10Type = (com.topografix.gpx.v10.Gpx) JAXBIntrospector.getValue(unmarshaller
							.unmarshal(new DOMOutputter().output(document)));
					tracks = trackMapper.fromGpx10(gpx10Type);
				}
				else
				{
					unmarshaller = GPX_1_1_JAXB_CTX.createUnmarshaller();
					GpxType gpxType = (GpxType) JAXBIntrospector.getValue(unmarshaller.unmarshal(new DOMOutputter().output(document)));
					tracks = trackMapper.fromGpx(gpxType);
				}
				for (TrackWrapper track : tracks)
				{
					theLayers.addThisLayer(track);
				}
			}
		}
		catch (Exception e)
		{
			CorePlugin.logError(Status.ERROR, "Problem reading GPX", e);
			errorDialog("Load GPS File", "Problem reading GPX");
			return null;
		}

		return theLayers;
	}

	@Override
	public void marshall(Layers from, File fileName)
	{
		String direcotryPath = collectDirecotryPath();

		if (direcotryPath == null)
		{
			errorDialog("Export to GPS", "You have to selected the directory to save the GPS file.");
			return;
		}

		try
		{
			File saveTo = new File(direcotryPath, fileName.getName());
			List<TrackWrapper> tracks = getTracksToMarshall(from);

			if (tracks.size() > 0)
			{
				CorePlugin.logError(Status.INFO, "Exporting " + tracks.size() + " tracks to gpx file " + saveTo.getAbsolutePath(), null);

				Gpx gpxType = GPX_1_0_OBJ_FACTORY.createGpx();
				gpxType.setVersion("1.0");
				gpxType.setName("Exported DebriefNG tracks");
				gpxType.setCreator("DebriefNG");

				List<Trk> gpxTracks = trackMapper.toGpx10(tracks);
				gpxType.getTrk().addAll(gpxTracks);

				Marshaller marshaller = GPX_1_0_JAXB_CTX.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(gpxType, saveTo);
				if (!isValid(saveTo))
				{
					errorDialog("Export to GPS", "Generated GPX failed validation");
				}
			}
			else
			{
				CorePlugin.logError(Status.INFO, "No tracks vailable to export", null);
				infoDialog("Export to GPS", "No tracks vailable to export");
			}
		}
		catch (Exception e)
		{
			CorePlugin.logError(Status.ERROR, "Error while marshalling to file GPX format: " + fileName.getAbsolutePath(), e);
			String dialogMsg = "";
			if (e.getMessage() != null)
			{
				dialogMsg = "Reason: " + e.getMessage();
			}
			errorDialog("Export to GPS", "Problem during the export." + dialogMsg);
		}
	}

	private List<TrackWrapper> getTracksToMarshall(Layers from)
	{
		Enumeration<Editable> allLayers = from.elements();
		List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();
		while (allLayers.hasMoreElements())
		{
			Editable element = allLayers.nextElement();
			if (element instanceof TrackWrapper)
			{
				tracks.add((TrackWrapper) element);
			}
		}
		return tracks;
	}
}
