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
package org.mwc.debrief.core.loaders;

import static org.mwc.debrief.core.loaders.GpxUtil.collectDirecotryPath;
import static org.mwc.debrief.core.loaders.GpxUtil.getDocumentSource;
import static org.mwc.debrief.core.loaders.GpxUtil.isGpx10;
import static org.mwc.debrief.core.loaders.GpxUtil.isValid;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
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
			DEBRIEF_EXTENSIONS_JAXB_CTX = JAXBContext
					.newInstance("org.mwc.debrief.core.gpx");
		}
		catch (final JAXBException e)
		{
			throw new IllegalStateException(
					"Exception while initialzing JAXB Context", e);
		}
	}

	public JaxbGpxHelper()
	{
		trackMapper.setJaxbContext(DEBRIEF_EXTENSIONS_JAXB_CTX);
	}

	@Override
	public Layers unmarshall(final InputStream gpxStream, final Layers theLayers)
	{
		Layers layers = theLayers;
		if (layers == null)
		{
			layers = new Layers();
		}
		try
		{
			final JDOMSource source = (JDOMSource) getDocumentSource(gpxStream);

			final boolean isGpx10 = isGpx10(source);
			final boolean xmlValid = isValid(source, isGpx10);

			if(!xmlValid)
			{
				CorePlugin.logError(Status.WARNING, "GPX Doc failed to validate. Trying to import anyway", null);
			}
			
			final Document document = source.getDocument();
			Unmarshaller unmarshaller;
			List<TrackWrapper> tracks = Collections.emptyList();

			if (isGpx10)
			{

				unmarshaller = GPX_1_0_JAXB_CTX.createUnmarshaller();
				final com.topografix.gpx.v10.Gpx gpx10Type = (com.topografix.gpx.v10.Gpx) JAXBIntrospector
						.getValue(unmarshaller.unmarshal(new DOMOutputter()
								.output(document)));
				tracks = trackMapper.fromGpx10(gpx10Type);
			}
			else
			{
				unmarshaller = GPX_1_1_JAXB_CTX.createUnmarshaller();
				final GpxType gpxType = (GpxType) JAXBIntrospector.getValue(unmarshaller
						.unmarshal(new DOMOutputter().output(document)));
				tracks = trackMapper.fromGpx(gpxType);
			}
			for (final TrackWrapper track : tracks)
			{
				layers.addThisLayer(track);
			}
		}
		catch (final Exception e)
		{
			CorePlugin.logError(Status.ERROR, "Problem reading GPX", e);
			CorePlugin.errorDialog("Load GPS File", "Problem reading GPX");
			return null;
		}

		return layers;
	}

	@Override
	public void marshall(final List<TrackWrapper> tracks, final File fileName)
	{
		final String direcotryPath = collectDirecotryPath();

		if (direcotryPath == null)
		{
			CorePlugin.errorDialog("Export to GPS",
					"You have to selected the directory to save the GPS file.");
			return;
		}

		try
		{
			final File saveTo = new File(direcotryPath, fileName.getName());

			if (tracks.size() > 0)
			{
				CorePlugin.logError(Status.INFO, "Exporting " + tracks.size()
						+ " tracks to gpx file " + saveTo.getAbsolutePath(), null);

				final Gpx gpxType = GPX_1_0_OBJ_FACTORY.createGpx();
				gpxType.setVersion("1.0");
				gpxType.setName("Exported DebriefNG tracks");
				gpxType.setCreator("DebriefNG");

				final List<Trk> gpxTracks = trackMapper.toGpx10(tracks);
				gpxType.getTrk().addAll(gpxTracks);

				final Marshaller marshaller = GPX_1_0_JAXB_CTX.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(gpxType, saveTo);
				if (!isValid(saveTo))
				{
					CorePlugin.errorDialog("Export to GPS", "Generated GPX failed validation");
				}
			}
			else
			{
				CorePlugin.logError(Status.INFO, "No tracks vailable to export", null);
				CorePlugin.infoDialog("Export to GPS", "No tracks vailable to export");
			}
		}
		catch (final Exception e)
		{
			CorePlugin.logError(
					Status.ERROR,
					"Error while marshalling to file GPX format: "
							+ fileName.getAbsolutePath(), e);
			String dialogMsg = "";
			if (e.getMessage() != null)
			{
				dialogMsg = "Reason: " + e.getMessage();
			}
			CorePlugin.errorDialog("Export to GPS", "Problem during the export." + dialogMsg);
		}
	}
}
