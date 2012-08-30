package org.mwc.debrief.core.gpx.mappers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.gpx.TrackType;
import org.mwc.debrief.core.loaders.DebriefJaxbContextAware;
import org.w3c.dom.Node;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Properties.LocationPropertyEditor;

import com.topografix.gpx.v11.GpxType;
import com.topografix.gpx.v11.TrkType;
import com.topografix.gpx.v11.TrksegType;
import com.topografix.gpx.v11.WptType;

/**
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date August 21, 2012
 * @category gpx
 * 
 *           <pre>
 * /plot/session/layers/track										/gpx/trk								Debrief.Wrappers.TrackWrapper
 * /plot/session/layers/track/TrackSegment 			/gpx/trk/trkseg 				Debrief.Wrappers.Track.TrackSegment	
 * /plot/session/layers/track/TrackSegment/fix 	/gpx/trk/trkseg/trkpt 	Debrief.Wrappers.FixWrapper
 * </pre>
 */
public class TrackMapper implements DebriefJaxbContextAware
{
	private final TrackSegmentMapper segmentMapper = new TrackSegmentMapper();
	private final FixMapper fixMapper = new FixMapper();
	private JAXBContext debriefContext;

	public List<TrackWrapper> fromGpx(GpxType gpx)
	{
		List<TrackWrapper> tracks = new ArrayList<TrackWrapper>(gpx.getTrk().size());

		for (TrkType gpxTrack : gpx.getTrk())
		{
			TrackWrapper track = new TrackWrapper();

			mapGpxTrack(gpxTrack, track);

			for (TrksegType gpxSegment : gpxTrack.getTrkseg())
			{
				TrackSegment segment = segmentMapper.fromGpx(gpxSegment);
				track.add(segment);

				for (WptType waypointType : gpxSegment.getTrkpt())
				{
					FixWrapper fix = fixMapper.fromGpx(waypointType);
					segment.add(fix);
				}
			}
			tracks.add(track);
		}

		return tracks;
	}

	private void mapGpxTrack(TrkType gpxTrack, TrackWrapper track)
	{
		track.setName(gpxTrack.getName());

		try
		{
			List<Object> any = gpxTrack.getExtensions().getAny();

			Unmarshaller unmarshaller = debriefContext.createUnmarshaller();
			Object object = unmarshaller.unmarshal((Node) any.get(0));
			TrackType trackExtension = (TrackType) JAXBIntrospector.getValue(object);

			track.setNameAtStart(trackExtension.isNameAtStart());
			track.setLineThickness(trackExtension.getLineThickness().intValue());
			track.setInterpolatePoints(trackExtension.isInterpolatePoints());
			track.setLinkPositions(trackExtension.isLinkPositions());
			track.setLineStyle(trackExtension.getLineStyle().intValue());
			LocationPropertyEditor nameLocationConverter = new LocationPropertyEditor();
			nameLocationConverter.setAsText(trackExtension.getNameLocation());
			track.setNameLocation(((Integer) nameLocationConverter.getValue()).intValue());
			track.getSensors().setVisible(trackExtension.isSensorsVisible());
			track.getSolutions().setVisible(trackExtension.isSolutionsVisible());
			track.setNameVisible(trackExtension.isNameVisible());
			track.setPlotArrayCentre(trackExtension.isPlotArrayCentre());
			track.setPositionsVisible(trackExtension.isPositionsVisible());
			track.setLinkPositions(trackExtension.isLinkPositions());
			track.setVisible(trackExtension.isVisible());
			track.setSymbolType(trackExtension.getSymbol());
		}
		catch (JAXBException e)
		{
			CorePlugin.logError(Status.ERROR, "Error while mapping Track from GPX", e);
		}

	}

	@Override
	public void setJaxbContext(JAXBContext ctx)
	{
		debriefContext = ctx;
	}
}
