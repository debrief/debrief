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
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

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
	private static JAXBContext GPX_1_1_JAXB_CTX;
	private static JAXBContext GPX_1_0_JAXB_CTX;
	private static JAXBContext DEBRIEF_EXTENSIONS_JAXB_CTX;

	private final TrackMapper trackMapper = new TrackMapper();
	// private final XMLInputFactory xif = XMLInputFactory.newInstance();
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
				document.getRootElement();

				/*
				 * rootElement.removeAttribute("version");
				 * rootElement.setAttribute("version", "1.0");
				 */
				// XMLStreamReader xsr = xif.createXMLStreamReader(new DOMSource(new
				// DOMOutputter().output(document)));
				// xsr = new GpxNamespaceTransposingStreamReaderDelegate(xsr);
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
		catch (JAXBException e)
		{
			CorePlugin.logError(Status.ERROR, "Error while unmarshalling GPX", e);
			// MessageDialog.openWarning(shell, "Load GPS File",
			// "[description of problem in user speak, as below");
			return null;
		}
		catch (Exception e)
		{
			CorePlugin.logError(Status.ERROR, "Error while unmarshalling GPX. The issue happened while creating the stax classes", e);
			return null;
		}

		return theLayers;
	}

	@Override
	public void marshall(Layers from, File saveToGpx)
	{
		try
		{
			List<TrackWrapper> tracks = getTracksToMarshall(from);

			if (tracks.size() > 0)
			{
				CorePlugin.logError(Status.INFO, "Exporting " + tracks.size() + " tracks to gpx file " + saveToGpx.getAbsolutePath(), null);

				Gpx gpxType = GPX_1_0_OBJ_FACTORY.createGpx();
				gpxType.setVersion("1.0");
				gpxType.setName(System.getProperty("user.name"));
				gpxType.setCreator("DebriefNG");

				List<Trk> gpxTracks = trackMapper.toGpx10(tracks);
				gpxType.getTrk().addAll(gpxTracks);

				Marshaller marshaller = GPX_1_0_JAXB_CTX.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(gpxType, saveToGpx);
				if (!isValid(saveToGpx))
				{
					// TODO display dialog box
				}
			}
			else
			{
				CorePlugin.logError(Status.INFO, "No tracks vailable to export", null);
			}
		}
		catch (Exception e)
		{
			CorePlugin.logError(Status.ERROR, "Error while marshalling to file GPX format: " + saveToGpx.getAbsolutePath(), e);
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

	/**
	 * JAXB requires binding code to be generated for every version of the GPX
	 * schema. This is because of the way versioning is implemented by the GPX;
	 * GPX changes the namespaces with every new version becuase the version is
	 * part of the namespace. For example,
	 * <ol>
	 * <li>GPX 1.0: http://www.topografix.com/GPX/1/0</li>
	 * <li>GPX 1.1: http://www.topografix.com/GPX/1/1</li>
	 * </ol>
	 * 
	 * <p>
	 * Hoping that new versions are backward compatible with older versions
	 * (except the <code>version</code> attribute and the namespace change), we
	 * just treat all of them as 1.1 versions thus reusing the binding code
	 * generated for 1.1 version.
	 * </p>
	 * 
	 * <b>NOTE: The <code>version</code> attribute of <code>gpx</code> element is
	 * considered insignificant for the unmarshalling process and hence being
	 * ignored. </b>
	 * 
	 * <p>
	 * The implementation tip is from <a href=
	 * "http://blog.bdoughan.com/2010/12/case-insensitive-unmarshalling.html"
	 * >here</a>
	 * </p>
	 */
	public static class GpxNamespaceTransposingStreamReaderDelegate extends StreamReaderDelegate
	{
		public GpxNamespaceTransposingStreamReaderDelegate(XMLStreamReader xsr)
		{
			super(xsr);
		}

		@Override
		public String getNamespaceURI()
		{
			if (super.getNamespaceURI().toLowerCase().contains("gpx"))
			{
				return "http://www.topografix.com/GPX/1/0";
			}
			else
			{
				return super.getNamespaceURI();
			}
		}
	}
}
