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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.loaders.xml_handlers;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;
import java.text.ParseException;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.editors.painters.LayerPainterManager;
import org.mwc.debrief.core.editors.painters.SnailHighlighter;
import org.mwc.debrief.core.editors.painters.TemporalLayerPainter;
import org.mwc.debrief.core.editors.painters.highlighters.SWTPlotHighlighter;
import org.mwc.debrief.core.editors.painters.highlighters.SWTPlotHighlighter.RectangleHighlight;
import org.mwc.debrief.core.editors.painters.highlighters.SWTRangeHighlighter;
import org.w3c.dom.Document;

import Debrief.GUI.Tote.Painters.SnailPainter;
import Debrief.ReaderWriter.XML.GUIHandler;
import Debrief.ReaderWriter.XML.GUIHandler.ComponentDetails;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.temporal.ControllableTime;
import MWC.TacticalData.temporal.TimeControlPreferences;
import MWC.TacticalData.temporal.TimeProvider;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReaderWriter;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public final class StepperHandler implements SWTGUIHandler.ComponentCreator
{

  private static final String TOOLBOX_END_TIME = "Toolbox_End_Time";

  private static final String TOOLBOX_START_TIME = "Toolbox_Start_Time";

  private static final String RECT_HIGHLIGHT_SIZE = "RectHighlight_Size";

	private static final String RECT_HIGHLIGHT_COLOR = "RectHighlight_Color";

	/**
	 * Range ring highlighter
	 * 
	 */
	private static final String NUM_RINGS = "NumRings";

	private static final String SPOKE_SEPARATION = "SpokeSeparation";

	private static final String ARCS = "Arcs";
	private static final String ARCS_FINISH = "Arc_Finish";

	private static final String SHADE_ARCS = "ShadeArcs";
	private static final String JUST_PRIMARY = "JustPrimary";

	private static final String RADIUS = "Radius";

	/**
	 * snail trail properties
	 * 
	 */
	private static final String TRAIL_LENGTH = "TrailLength";

	private static final String USE_TRACK_COLOR = "UseTrackColor";

	private static final String POINT_SIZE = "PointSize";

	private static final String FADE_POINTS = "FadePoints";

	private static final String PLOT_TRACK_NAME = "PlotTrackName";

	private static final String LINK_POSITIONS = "LinkPositions";

	private static final String VECTOR_STRETCH = "VectorStretch";

	private static final String COL_RED = "RED";

	private static final String COL_GREEN = "GREEN";

	private static final String COL_BLUE = "BLUE";

	public final void makeThis(
			final Debrief.ReaderWriter.XML.GUIHandler.ComponentDetails details,
			final TimeControlPreferences timePrefs,
			final LayerPainterManager painterMgr,
			final ControllableTime timeController)
	{
		String val = null;

		// ////////////////////////////////////////////////////////////
		final String cursor = (String) details.properties.get("Cursor");
		if (cursor != null)
		{
			// set the cursor
			painterMgr.setCurrentPainter(cursor);
		}

		// configure any other painters that the user's edited
		final TemporalLayerPainter[] painters = painterMgr.getPainterList();
		for (int i = 0; i < painters.length; i++)
		{
			final TemporalLayerPainter thisPainter = painters[i];
			configureThisPainter(thisPainter, details);
		}

		// ////////////////////////////////////////////////////////////
		final String highlighter = (String) details.properties.get("Highlighter");
		if (highlighter != null)
			painterMgr.setCurrentHighlighter(highlighter);

		final SWTPlotHighlighter[] highlighters = painterMgr.getHighlighterList();
		for (int i = 0; i < highlighters.length; i++)
		{
			final SWTPlotHighlighter thisHighlighter = highlighters[i];
			configureThisHighlighter(thisHighlighter, details);
		}

		// ////////////////////////////////////////////////////////////
		final String dateFormat = (String) details.properties.get("DateFormat");
		if (dateFormat != null)
		{
			// set the cursor
			timePrefs.setDTGFormat(dateFormat);
		}

		// ////////////////////////////////////////////////////////////
		final String start_time = (String) details.properties.get(TOOLBOX_START_TIME);
		if (start_time != null)
		{
			HiResDate startTime = null;
			// get a date from this
			try
      {
        startTime = DebriefFormatDateTime.parseThis(start_time);
        // set the cursor
        timePrefs.setSliderStartTime(startTime);
      }
      catch (ParseException e)
      {
        Trace.trace(e, "While parsing date");
      }

		}

		// ////////////////////////////////////////////////////////////
		final String end_time = (String) details.properties.get(TOOLBOX_END_TIME);
		if (end_time != null)
		{
			HiResDate endTime = null;

			// get a date from this
			try
      {
        endTime = DebriefFormatDateTime.parseThis(end_time);
      }
      catch (ParseException e)
      {
        Trace.trace(e, "While parsing date");
      }

			// set the cursor
			timePrefs.setSliderEndTime(endTime);
		}

		// ////////////////////////////////////////////////////////////
		final String tZero = (String) details.properties.get("TimeZero");
		if (tZero != null)
		{
			// get a date from this
			// HiResDate dt = DebriefFormatDateTime.parseThis(tZero);

			// set the cursor
			// set the cursor
			CorePlugin.logError(Status.WARNING, "T-Zero not yet implemented", null);
		}
		// ////////////////////////////////////////////////////////////
		final String currentTime = (String) details.properties.get("CurrentTime");
		if (currentTime != null)
		{
			// and set the time
			HiResDate dtg;
      try
      {
        dtg = DebriefFormatDateTime.parseThis(currentTime);
        
        // did we find a valid dtg?
        if (dtg != null)
        {
          timeController.setTime(this, dtg, false);
        }
      }
      catch (ParseException e)
      {
        Trace.trace(e, "While parsing date");
      }

		}

		// ////////////////////////////////////////////////////////////
		val = (String) details.properties.get("AutoStep");
		if (val != null)
		{
			// set the auto step to this number of millis
			final int len = Integer.valueOf(val).intValue();
			timePrefs.setAutoInterval(new Duration(len, Duration.MILLISECONDS));
		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("StepLarge");
		if (val != null)
		{
			// set the large step to this number of millis
			try
			{
				final double len = MWCXMLReaderWriter.readThisDouble(val);
				timePrefs.setLargeStep(new Duration(len, Duration.MILLISECONDS));
			}
			catch (final java.text.ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe,
						"Failed reading large step size value is:" + val);
			}

		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("StepSmall");
		if (val != null)
		{
			try
			{
				// set the small step to this number of millis
				final double len = MWCXMLReaderWriter.readThisDouble(val);
				timePrefs.setSmallStep(new Duration(len, Duration.MILLISECONDS));
			}
			catch (final java.text.ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe,
						"Failed reading small step size value is:" + val);
			}
		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("SmallStep");
		if (val != null)
		{
			try
			{
				final Duration dur = Duration.fromString(val);
				timePrefs.setSmallStep(dur);
			}
			catch (final java.text.ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe,
						"Failed reading duration value is:" + val);
			}
		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("LargeStep");
		if (val != null)
		{
			try
			{
				final Duration dur = Duration.fromString(val);
				timePrefs.setLargeStep(dur);
			}
			catch (final java.text.ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe,
						"Failed reading duration value is:" + val);
			}
		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("AutoStepInterval");
		if (val != null)
		{
			try
			{
				final Duration dur = Duration.fromString(val);
				timePrefs.setAutoInterval(dur);
			}
			catch (final java.text.ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe,
						"Failed reading duration value is:" + val);
			}
		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("StepLargeMicros");
		if (val != null)
		{
			// set the large step to this number of millis
			try
			{
				final double len = MWCXMLReaderWriter.readThisDouble(val);
				timePrefs.setLargeStep(new Duration(len, Duration.MICROSECONDS));
			}
			catch (final java.text.ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe,
						"Failed reading large step size value is:" + val);
			}

		}

		// /////////////////////////////////////////////////////////////
		val = (String) details.properties.get("StepSmallMicros");
		if (val != null)
		{
			try
			{
				// set the small step to this number of millis
				final double len = MWCXMLReaderWriter.readThisDouble(val);
				timePrefs.setSmallStep(new Duration(len, Duration.MICROSECONDS));
			}
			catch (final java.text.ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe,
						"Failed reading small step size value is:" + val);
			}
		}
	}

	private void configureThisHighlighter(final SWTPlotHighlighter thisHighlighter,
			final ComponentDetails details)
	{
		if (thisHighlighter.getName().equals(
				SWTRangeHighlighter.RANGE_RING_HIGHLIGHT))
		{
			final SWTRangeHighlighter rr = (SWTRangeHighlighter) thisHighlighter;

			final String radius = (String) details.properties.get(RADIUS);
			if (radius != null)
				try {
					rr.setRadius(MWCXMLReader.readThisDouble(radius));
				} catch (final ParseException pe) {
					MWC.Utilities.Errors.Trace.trace(pe,
							"Reader: Whilst reading in " + RADIUS + " value of :"
									+ radius);
				}

			final String arcs = (String) details.properties.get(ARCS);
			if (arcs != null)
				rr.setArcStart(Integer.parseInt(arcs));
			
			final String arcs_end = (String) details.properties.get(ARCS_FINISH);
			if (arcs_end != null)
				rr.setArcEnd(Integer.parseInt(arcs_end));

			final String spokeSep = (String) details.properties.get(SPOKE_SEPARATION);
			if (spokeSep != null)
				rr.setSpokeSeparation(Integer.parseInt(spokeSep));

			final String numRings = (String) details.properties.get(NUM_RINGS);
			if (numRings != null)
				rr.setNumRings(Integer.parseInt(numRings));

			final String fillRings = (String) details.properties.get(SHADE_ARCS);
			if (fillRings != null)
				rr.setFillArcs(Boolean.valueOf(fillRings));

			final String justPrimary = (String) details.properties.get(JUST_PRIMARY);
			if (justPrimary != null)
				rr.setJustPlotPrimary(Boolean.valueOf(justPrimary));

			final String useTrackColor = (String) details.properties.get(USE_TRACK_COLOR);
			if (useTrackColor != null)
				rr.setUseCurrentTrackColor(Boolean.valueOf(useTrackColor));

			final String colRed = (String) details.properties.get(COL_RED);
			final String colGreen = (String) details.properties.get(COL_GREEN);
			final String colBlue = (String) details.properties.get(COL_BLUE);
			if ((colRed != null) && (colGreen != null) && (colBlue != null))
			{
				final Color newCol = new Color(Integer.valueOf(colRed),
						Integer.valueOf(colGreen), Integer.valueOf(colBlue));
				rr.setColor(newCol);
			}

		}
		else if (thisHighlighter.getName().equals(
				SWTPlotHighlighter.RectangleHighlight.DEFAULT_HIGHLIGHT))
		{
			final SWTPlotHighlighter.RectangleHighlight rect = (RectangleHighlight) thisHighlighter;
			final String theCol = (String) details.properties.get(RECT_HIGHLIGHT_COLOR);
			if (theCol != null)
				rect.setColor(ColourHandler.fromString(theCol));
			final String theSize = (String) details.properties.get(RECT_HIGHLIGHT_SIZE);
			if (theSize != null)
				rect.setRawSize(Integer.parseInt(theSize));
		}
	}

	private void configureThisPainter(final TemporalLayerPainter thisPainter,
			final Debrief.ReaderWriter.XML.GUIHandler.ComponentDetails details)
	{
		// ////////////////////////////////////////////////////////////
		// is this the snail cursor?
		if (thisPainter.getName().equals(SnailPainter.SNAIL_NAME))
		{
			// ok, get the snail properties
			final SnailHighlighter sp = (SnailHighlighter) thisPainter;
			final String vector_stretch = (String) details.properties.get(VECTOR_STRETCH);
			if (vector_stretch != null)
			{
				try {
					final double value = MWCXMLReader.readThisDouble(vector_stretch); 
					sp.setVectorStretch(value);
					sp.getSnailProperties().setVectorStretch(value);
				} catch (final ParseException pe) {
					MWC.Utilities.Errors.Trace.trace(pe,
							"Reader: Whilst reading in " + VECTOR_STRETCH + " value of :"
									+ vector_stretch);
				}
			}				

			final String linkPos = (String) details.properties.get(LINK_POSITIONS);
			if (linkPos != null)
				sp.getSnailProperties().setLinkPositions(Boolean.parseBoolean(linkPos));

			final String plotTrkName = (String) details.properties.get(PLOT_TRACK_NAME);
			if (plotTrkName != null)
				sp.getSnailProperties().setPlotTrackName(
						Boolean.parseBoolean(plotTrkName));

			final String fadePoints = (String) details.properties.get(FADE_POINTS);
			if (fadePoints != null)
				sp.getSnailProperties().setFadePoints(Boolean.parseBoolean(fadePoints));

			final String pointSize = (String) details.properties.get(POINT_SIZE);
			if (pointSize != null)
				sp.getSnailProperties().setPointSize(Integer.parseInt(pointSize));

			final String trailLength = (String) details.properties.get(TRAIL_LENGTH);
			if (trailLength != null)
			{
				try
				{
					final Duration theLen = Duration.fromString(trailLength);
					sp.getSnailProperties().setTrailLength(theLen);
				}
				catch (final java.text.ParseException pe)
				{
					MWC.Utilities.Errors.Trace.trace(pe,
							"Failed reading duration value is:" + trailLength);
				}
			}
		}
	}

	public final GUIHandler.ComponentDetails exportThis(
			final TimeControlPreferences controller, final LayerPainterManager painterMgr,
			final TimeProvider timeProvider, final Document doc)
	{

		// collate the details for this component
		final GUIHandler.ComponentDetails details = new GUIHandler.ComponentDetails();

		// start off with the painter-highlighter
		final TemporalLayerPainter currentPainter = painterMgr.getCurrentPainter();
		details.addProperty("Cursor", currentPainter.toString());

		// store the current settings for the painters
		final TemporalLayerPainter[] painterList = painterMgr.getPainterList();
		for (int i = 0; i < painterList.length; i++)
		{
			final TemporalLayerPainter thisPainter = painterList[i];
			storeThisPainter(thisPainter, thisPainter.getName(), details);
		}

		// aah, there's the highlighter as well
		final SWTPlotHighlighter highlighter = painterMgr.getCurrentHighlighter();
		if (highlighter != null)
			details.addProperty("Highlighter", highlighter.getName());

		// and export the other highlighters
		final SWTPlotHighlighter[] highlighterList = painterMgr.getHighlighterList();
		for (int i = 0; i < highlighterList.length; i++)
		{
			final SWTPlotHighlighter thisHighlighter = highlighterList[i];
			storeThisHighlighter(thisHighlighter, thisHighlighter.getName(), details,
					doc);
		}

		// ok, we're switching to exporting the step size in microseconds
		// if we ever get the plain "StepLarge" parameter - we will assume it is
		// millis, else
		// we will always receive the units

		// //////////////////////////
		// and now the stepper bits
		// ////////////////////////////////

		if (controller.getLargeStep() != null)
			details.addProperty("LargeStep",
					MWCXMLReader.writeThis(controller.getLargeStep()));
		if (controller.getSmallStep() != null)
			details.addProperty("SmallStep",
					MWCXMLReader.writeThis(controller.getSmallStep()));

		if (controller.getAutoInterval() != null)
			details.addProperty("AutoStepInterval",
					MWCXMLReader.writeThis(controller.getAutoInterval()));

		details.addProperty("DateFormat", controller.getDTGFormat());

		// the current DTG

		final HiResDate cTime = timeProvider.getTime();
		if (cTime != null)
			details.addProperty("CurrentTime", MWCXMLReader.writeThis(cTime));

		// the T-zero, if set
		// and let's not bother with time-zero for now either
		// if (controller.getTimeZero() != null)
		// details.addProperty("TimeZero",
		// MWCXMLReader.writeThis(controller.getTimeZero()));

		// what's the time?
		// Let's not bother with this for now...
		final HiResDate theStartTime = controller.getSliderStartTime();
		if (theStartTime != null)
			details.addProperty(TOOLBOX_START_TIME,
					DebriefFormatDateTime.toStringHiRes(theStartTime));
		final HiResDate theEndTime = controller.getSliderEndTime();
		if (theEndTime != null)
			details.addProperty(TOOLBOX_END_TIME,
					DebriefFormatDateTime.toStringHiRes(theEndTime));

		return details;
	}

	private void storeThisHighlighter(final SWTPlotHighlighter thisHighlighter,
			final String name, final ComponentDetails details, final Document doc)
	{
		if (name.equals(SWTRangeHighlighter.RANGE_RING_HIGHLIGHT))
		{
			final SWTRangeHighlighter hi = (SWTRangeHighlighter) thisHighlighter;

			details.addProperty(RADIUS, MWCXMLReader.writeThis(hi.getRadius()));
			details.addProperty(ARCS,
					MWCXMLReader.writeThis(hi.getArcStart()));
			details.addProperty(ARCS_FINISH,
					MWCXMLReader.writeThis(hi.getArcEnd()));
			details.addProperty(SPOKE_SEPARATION,
					MWCXMLReader.writeThis(hi.getSpokeSeparation()));
			details.addProperty(NUM_RINGS,
					MWCXMLReader.writeThis(hi.getNumRings().getCurrent()));
			details.addProperty(SHADE_ARCS, MWCXMLReader.writeThis(hi.getFillArcs()));
			details.addProperty(JUST_PRIMARY, MWCXMLReader.writeThis(hi.isJustPlotPrimary()));
			details.addProperty(USE_TRACK_COLOR,
					MWCXMLReader.writeThis(hi.getUseCurrentTrackColor()));

			details.addProperty(COL_RED,
					MWCXMLReader.writeThis(hi.getColor().getRed()));
			details.addProperty(COL_GREEN,
					MWCXMLReader.writeThis(hi.getColor().getGreen()));
			details.addProperty(COL_BLUE,
					MWCXMLReader.writeThis(hi.getColor().getBlue()));

		}
		if(name.equals(SWTPlotHighlighter.RectangleHighlight.DEFAULT_HIGHLIGHT))
		{
				final RectangleHighlight rec = (RectangleHighlight) thisHighlighter;
				details.addProperty(RECT_HIGHLIGHT_COLOR,
						ColourHandler.toString(rec.getColor()));
				details.addProperty(RECT_HIGHLIGHT_SIZE,"" + rec.getSize().getCurrent());
		}
	}

	private void storeThisPainter(final TemporalLayerPainter thisPainter, final String name,
			final ComponentDetails details)
	{
		// is this the snail painter?
		if (name.equals(SnailPainter.SNAIL_NAME))
		{
			final SnailHighlighter sp = (SnailHighlighter) thisPainter;
			details.addProperty(LINK_POSITIONS,
					MWCXMLReader.writeThis(sp.getSnailProperties().getLinkPositions()));
			details.addProperty(PLOT_TRACK_NAME,
					MWCXMLReader.writeThis(sp.getSnailProperties().getPlotTrackName()));
			details.addProperty(FADE_POINTS,
					MWCXMLReader.writeThis(sp.getSnailProperties().getFadePoints()));
			details.addProperty(
					POINT_SIZE,
					MWCXMLReader.writeThis(sp.getSnailProperties().getPointSize()
							.getCurrent()));
			details.addProperty(TRAIL_LENGTH,
					MWCXMLReader.writeThis(sp.getSnailProperties().getTrailLength()));
			details.addProperty(VECTOR_STRETCH,
					MWCXMLReader.writeThis(sp.getSnailProperties().getVectorStretch()));
		}
	}

}