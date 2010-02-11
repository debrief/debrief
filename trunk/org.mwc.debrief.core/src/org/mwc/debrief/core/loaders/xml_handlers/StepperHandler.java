package org.mwc.debrief.core.loaders.xml_handlers;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.*;
import org.mwc.debrief.core.editors.painters.*;
import org.mwc.debrief.core.editors.painters.highlighters.*;

import Debrief.GUI.Tote.Painters.SnailPainter;
import Debrief.ReaderWriter.XML.GUIHandler;
import Debrief.ReaderWriter.XML.GUIHandler.ComponentDetails;
import MWC.GenericData.*;
import MWC.Utilities.ReaderWriter.XML.*;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public final class StepperHandler implements SWTGUIHandler.ComponentCreator
{

	/** Range ring highlighter
	 * 
	 */
	private static final String NUM_RINGS = "NumRings";

	private static final String SPOKE_SEPARATION = "SpokeSeparation";

	private static final String ARCS = "Arcs";

	private static final String SHADE_ARCS = "ShadeArcs";

	private static final String RADIUS = "Radius";

	/** snail trail properties
	 * 
	 */
	private static final String TRAIL_LENGTH = "TrailLength";

	private static final String POINT_SIZE = "PointSize";

	private static final String FADE_POINTS = "FadePoints";

	private static final String PLOT_TRACK_NAME = "PlotTrackName";

	private static final String LINK_POSITIONS = "LinkPositions";

	private static final String VECTOR_STRETCH = "VectorStretch";

	public final void makeThis(
			Debrief.ReaderWriter.XML.GUIHandler.ComponentDetails details,
			final TimeControlPreferences timePrefs, final LayerPainterManager painterMgr,
			final ControllableTime timeController)
	{
		String val = null;

		// ////////////////////////////////////////////////////////////
		String cursor = (String) details.properties.get("Cursor");
		if (cursor != null)
		{
			// set the cursor
			painterMgr.setCurrentPainter(cursor);
		}

		// configure any other painters that the user's edited
		TemporalLayerPainter[] painters = painterMgr.getPainterList();
		for (int i = 0; i < painters.length; i++)
		{
			TemporalLayerPainter thisPainter = painters[i];
			configureThisPainter(thisPainter, details);
		}

		//////////////////////////////////////////////////////////////
		String highlighter = (String) details.properties.get("Highlighter");
		if (highlighter != null)
			painterMgr.setCurrentHighlighter(highlighter);

		SWTPlotHighlighter[] highlighters = painterMgr.getHighlighterList();
		for (int i = 0; i < highlighters.length; i++)
		{
			SWTPlotHighlighter thisHighlighter = highlighters[i];
			configureThisHighlighter(thisHighlighter, details);
		}
		
		// ////////////////////////////////////////////////////////////
		String dateFormat = (String) details.properties.get("DateFormat");
		if (dateFormat != null)
		{
			// set the cursor
			timePrefs.setDTGFormat(dateFormat);
		}

		// ////////////////////////////////////////////////////////////
		String start_time = (String) details.properties.get("Toolbox_Start_Time");
		if (start_time != null)
		{
			HiResDate startTime = null;
			// get a date from this
			startTime = DebriefFormatDateTime.parseThis(start_time);

			// set the cursor
			timePrefs.setSliderStartTime(startTime);
		}

		// ////////////////////////////////////////////////////////////
		String end_time = (String) details.properties.get("Toolbox_End_Time");
		if (end_time != null)
		{
			HiResDate endTime = null;

			// get a date from this
			endTime = DebriefFormatDateTime.parseThis(end_time);

			// set the cursor
			timePrefs.setSliderEndTime(endTime);
		}

		// ////////////////////////////////////////////////////////////
		String tZero = (String) details.properties.get("TimeZero");
		if (tZero != null)
		{
			// get a date from this
			// HiResDate dt = DebriefFormatDateTime.parseThis(tZero);

			// set the cursor
			// set the cursor
			CorePlugin.logError(Status.WARNING, "T-Zero not yet implemented", null);
		}
		// ////////////////////////////////////////////////////////////
		String currentTime = (String) details.properties.get("CurrentTime");
		if (currentTime != null)
		{
			// and set the time
			HiResDate dtg = DebriefFormatDateTime.parseThis(currentTime);

			// did we find a valid dtg?
			if (dtg != null)
			{
				timeController.setTime(this, dtg, false);
			}
		}

		// ////////////////////////////////////////////////////////////
		val = (String) details.properties.get("AutoStep");
		if (val != null)
		{
			// set the auto step to this number of millis
			int len = Integer.valueOf(val).intValue();
			timePrefs.setAutoInterval(new Duration(len, Duration.MILLISECONDS));
		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("StepLarge");
		if (val != null)
		{
			// set the large step to this number of millis
			try
			{
				double len = MWCXMLReaderWriter.readThisDouble(val);
				timePrefs.setLargeStep(new Duration(len, Duration.MILLISECONDS));
			}
			catch (java.text.ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe, "Failed reading large step size value is:"
						+ val);
			}

		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("StepSmall");
		if (val != null)
		{
			try
			{
				// set the small step to this number of millis
				double len = MWCXMLReaderWriter.readThisDouble(val);
				timePrefs.setSmallStep(new Duration(len, Duration.MILLISECONDS));
			}
			catch (java.text.ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe, "Failed reading small step size value is:"
						+ val);
			}
		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("SmallStep");
		if (val != null)
		{
			Duration dur = Duration.fromString(val);
			timePrefs.setSmallStep(dur);
		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("LargeStep");
		if (val != null)
		{
			Duration dur = Duration.fromString(val);
			timePrefs.setLargeStep(dur);
		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("AutoStepInterval");
		if (val != null)
		{
			Duration dur = Duration.fromString(val);
			timePrefs.setAutoInterval(dur);
		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("StepLargeMicros");
		if (val != null)
		{
			// set the large step to this number of millis
			try
			{
				double len = MWCXMLReaderWriter.readThisDouble(val);
				timePrefs.setLargeStep(new Duration(len, Duration.MICROSECONDS));
			}
			catch (java.text.ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe, "Failed reading large step size value is:"
						+ val);
			}

		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("StepSmallMicros");
		if (val != null)
		{
			try
			{
				// set the small step to this number of millis
				double len = MWCXMLReaderWriter.readThisDouble(val);
				timePrefs.setSmallStep(new Duration(len, Duration.MICROSECONDS));
			}
			catch (java.text.ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe, "Failed reading small step size value is:"
						+ val);
			}
		}
	}

	private void configureThisHighlighter(SWTPlotHighlighter thisHighlighter, ComponentDetails details)
	{
		if(thisHighlighter.getName().equals(SWTRangeHighlighter.RANGE_RING_HIGHLIGHT))
		{
		  SWTRangeHighlighter rr = (SWTRangeHighlighter) thisHighlighter;

		  String radius = (String) details.properties.get(RADIUS);
			if (radius != null)
				rr.setRadius(Double.parseDouble(radius));

		  String arcs = (String) details.properties.get(ARCS);
			if (arcs != null)
				rr.setArcs(Integer.parseInt(arcs));

		  String spokeSep = (String) details.properties.get(SPOKE_SEPARATION);
			if (spokeSep != null)
				rr.setSpokeSeparation(Integer.parseInt(spokeSep));

		  String numRings = (String) details.properties.get(NUM_RINGS);
			if (numRings != null)
				rr.setNumRings(Integer.parseInt(numRings));

		  String fillRings = (String) details.properties.get(SHADE_ARCS);
			if (fillRings != null)
				rr.setFillArcs(Boolean.valueOf(fillRings));

		}
	}

	private void configureThisPainter(TemporalLayerPainter thisPainter,
			Debrief.ReaderWriter.XML.GUIHandler.ComponentDetails details)
	{
		// ////////////////////////////////////////////////////////////
		// is this the snail cursor?
		if (thisPainter.getName().equals(SnailPainter.SNAIL_NAME))
		{
			// ok, get the snail properties
			SnailHighlighter sp = (SnailHighlighter) thisPainter;
			String vector_stretch = (String) details.properties.get(VECTOR_STRETCH);
			if (vector_stretch != null)
				sp.setVectorStretch(Double.valueOf(vector_stretch).doubleValue());

			String linkPos = (String) details.properties.get(LINK_POSITIONS);
			if (linkPos != null)
				sp.getSnailProperties().setLinkPositions(Boolean.parseBoolean(linkPos));

			String plotTrkName = (String) details.properties.get(PLOT_TRACK_NAME);
			if (plotTrkName != null)
				sp.getSnailProperties().setPlotTrackName(Boolean.parseBoolean(plotTrkName));

			String fadePoints = (String) details.properties.get(FADE_POINTS);
			if (fadePoints != null)
				sp.getSnailProperties().setFadePoints(Boolean.parseBoolean(fadePoints));

			String pointSize = (String) details.properties.get(POINT_SIZE);
			if (pointSize != null)
				sp.getSnailProperties().setPointSize(Integer.parseInt(pointSize));

			String trailLength = (String) details.properties.get(TRAIL_LENGTH);
			if (trailLength != null)
			{
				Duration theLen = Duration.fromString(trailLength);
				sp.getSnailProperties().setTrailLength(theLen);
			}

			String vectorStretch = (String) details.properties.get(VECTOR_STRETCH);
			if (vectorStretch != null)
				sp.getSnailProperties().setVectorStretch(Double.parseDouble(vectorStretch));
		}
	}

	public final GUIHandler.ComponentDetails exportThis(TimeControlPreferences controller,
			LayerPainterManager painterMgr, TimeProvider timeProvider)
	{

		// collate the details for this component
		GUIHandler.ComponentDetails details = new GUIHandler.ComponentDetails();

		// start off with the painter-highlighter
		TemporalLayerPainter currentPainter = painterMgr.getCurrentPainter();
		details.addProperty("Cursor", currentPainter.toString());

		// store the current settings for the painters
		TemporalLayerPainter[] painterList = painterMgr.getPainterList();
		for (int i = 0; i < painterList.length; i++)
		{
			TemporalLayerPainter thisPainter = painterList[i];
			storeThisPainter(thisPainter, thisPainter.getName(), details);
		}

		// aah, there's the highlighter as well
		SWTPlotHighlighter highlighter = painterMgr.getCurrentHighlighter();
		if (highlighter != null)
			details.addProperty("Highlighter", highlighter.getName());

		// and export the other highlighters
		SWTPlotHighlighter[] highlighterList = painterMgr.getHighlighterList();
		for (int i = 0; i < highlighterList.length; i++)
		{
			SWTPlotHighlighter thisHighlighter = highlighterList[i];
			storeThisHighlighter(thisHighlighter, thisHighlighter.getName(), details);
		}

		// ok, we're switching to exporting the step size in microseconds
		// if we ever get the plain "StepLarge" parameter - we will assume it is
		// millis, else
		// we will always receive the units

		// //////////////////////////
		// and now the stepper bits
		// ////////////////////////////////

		details.addProperty("LargeStep", MWCXMLReader.writeThis(controller.getLargeStep()));
		details.addProperty("SmallStep", MWCXMLReader.writeThis(controller.getSmallStep()));

		details.addProperty("AutoStepInterval", MWCXMLReader.writeThis(controller
				.getAutoInterval()));

		details.addProperty("DateFormat", controller.getDTGFormat());

		// the current DTG

		HiResDate cTime = timeProvider.getTime();
		if (cTime != null)
			details.addProperty("CurrentTime", MWCXMLReader.writeThis(cTime));

		// the T-zero, if set
		// and let's not bother with time-zero for now either
		// if (controller.getTimeZero() != null)
		// details.addProperty("TimeZero",
		// MWCXMLReader.writeThis(controller.getTimeZero()));

		// what's the time?
		// Let's not bother with this for now...
		HiResDate theStartTime = controller.getSliderStartTime();
		if (theStartTime != null)
			details.addProperty("Toolbox_Start_Time", DebriefFormatDateTime
					.toStringHiRes(theStartTime));
		HiResDate theEndTime = controller.getSliderEndTime();
		if (theEndTime != null)
			details.addProperty("Toolbox_End_Time", DebriefFormatDateTime
					.toStringHiRes(theEndTime));

		return details;
	}

	private void storeThisHighlighter(SWTPlotHighlighter thisHighlighter, String name, ComponentDetails details)
	{
		if(name.equals(SWTRangeHighlighter.RANGE_RING_HIGHLIGHT))
		{
			SWTRangeHighlighter hi = (SWTRangeHighlighter) thisHighlighter;

//			details.addProperty(COLOR, MWCXMLReader.writeThis(hi.getColor()));
			details.addProperty(RADIUS, MWCXMLReader.writeThis(hi.getRadius()));
			details.addProperty(ARCS, MWCXMLReader.writeThis(hi.getArcs().getCurrent()));
			details.addProperty(SPOKE_SEPARATION, MWCXMLReader.writeThis(hi.getSpokeSeparation()));
			details.addProperty(NUM_RINGS,MWCXMLReader.writeThis(hi.getNumRings().getCurrent()));
			details.addProperty(SHADE_ARCS,MWCXMLReader.writeThis(hi.getFillArcs()));
		}
	}

	private void storeThisPainter(TemporalLayerPainter thisPainter, String name,
			ComponentDetails details)
	{
		// is this the snail painter?
		if (name.equals(SnailPainter.SNAIL_NAME))
		{
			SnailHighlighter sp = (SnailHighlighter) thisPainter;
			details.addProperty(LINK_POSITIONS, MWCXMLReader.writeThis(sp.getSnailProperties()
					.getLinkPositions()));
			details.addProperty(PLOT_TRACK_NAME, MWCXMLReader.writeThis(sp.getSnailProperties()
					.getPlotTrackName()));
			details.addProperty(FADE_POINTS, MWCXMLReader.writeThis(sp.getSnailProperties()
					.getFadePoints()));
			details.addProperty(POINT_SIZE, MWCXMLReader.writeThis(sp.getSnailProperties()
					.getPointSize().getCurrent()));
			details.addProperty(TRAIL_LENGTH, MWCXMLReader.writeThis(sp.getSnailProperties()
					.getTrailLength()));
			details.addProperty(VECTOR_STRETCH, MWCXMLReader.writeThis(sp.getSnailProperties()
					.getVectorStretch()));
		}
	}

}