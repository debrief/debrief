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
import org.mwc.debrief.core.editors.painters.highlighters.SWTPlotHighlighter;

import Debrief.GUI.Tote.Painters.SnailPainter;
import Debrief.ReaderWriter.XML.GUIHandler;
import MWC.GenericData.*;
import MWC.Utilities.ReaderWriter.XML.*;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public final class StepperHandler implements SWTGUIHandler.ComponentCreator
{

	public final void makeThis(
			Debrief.ReaderWriter.XML.GUIHandler.ComponentDetails details,
			final TimeControlPreferences timePrefs,
			final LayerPainterManager painterMgr,
			final ControllableTime timeController)
	{
		String val = null;

		// ////////////////////////////////////////////////////////////
		String cursor = (String) details.properties.get("Cursor");
		if (cursor != null)
		{
			// set the cursor
		  painterMgr.setCurrentPainter(cursor);

			// ////////////////////////////////////////////////////////////
			// is this the snail cursor?
			if (cursor.equals(SnailPainter.SNAIL_NAME))
			{
				String vector_stretch = (String) details.properties.get("VectorStretch");
				if (vector_stretch != null)
				{
					// set the cursor
					SnailHighlighter sp = (SnailHighlighter) painterMgr.getCurrentPainter();
					sp.setVectorStretch(Double.valueOf(vector_stretch).doubleValue());
				}
			}

		}

		// ////////////////////////////////////////////////////////////
		String dateFormat = (String) details.properties.get("DateFormat");
		if (dateFormat != null)
		{
			// set the cursor
			timePrefs.setDTGFormat(dateFormat);
		}

		// ////////////////////////////////////////////////////////////
		String highlighter = (String) details.properties.get("Highlighter");
		if (highlighter != null)
		{
			// set the cursor
			
			painterMgr.setCurrentHighlighter(highlighter);
		}

		// ////////////////////////////////////////////////////////////
		String start_time = (String) details.properties.get("Toolbox_Start_Time");
		if (start_time != null)
		{
//			HiResDate startTime = null;
//			// get a date from this
//			startTime = DebriefFormatDateTime.parseThis(start_time);

			// set the cursor
			CorePlugin.logError(Status.WARNING, "Toolbox start not yet implemented", null);
//			_analysisView.getTote().getStepper().setToolboxStartTime(startTime);
		}

		// ////////////////////////////////////////////////////////////
		String end_time = (String) details.properties.get("Toolbox_End_Time");
		if (end_time != null)
		{
//			HiResDate endTime = null;
//
//			// get a date from this
//			endTime = DebriefFormatDateTime.parseThis(end_time);

			// set the cursor
			CorePlugin.logError(Status.WARNING, "Toolbox end not yet implemented", null);
		}

		// ////////////////////////////////////////////////////////////
		String tZero = (String) details.properties.get("TimeZero");
		if (tZero != null)
		{
			// get a date from this
//			HiResDate dt = DebriefFormatDateTime.parseThis(tZero);

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

		//////////////////////////////////////////////////////////////
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

	public final GUIHandler.ComponentDetails exportThis(TimeControlPreferences controller,
			LayerPainterManager painterMgr, TimeProvider timeProvider)
	{

		// collate the details for this component
		GUIHandler.ComponentDetails details = new GUIHandler.ComponentDetails();

		// start off with the painter-highlighter
		
		TemporalLayerPainter thePainter =  painterMgr.getCurrentPainter();

		// is this the snail painter?
		if (thePainter instanceof SnailHighlighter)
		{
			SnailHighlighter sp = (SnailHighlighter) thePainter;
			details.addProperty("VectorStretch", MWCXMLReader.writeThis(sp.getVectorStretch()));
		}

		details.addProperty("Cursor", thePainter.toString());

		// details.addProperty("StepLarge",
		// MWCXMLReader.writeThis(stepper.getStepLarge()));
		// details.addProperty("StepSmall",
		// MWCXMLReader.writeThis(stepper.getStepSmall()));

		// ok, we're switching to exporting the step size in microseconds
		// if we ever get the plain "StepLarge" parameter - we will assume it is
		// millis, else
		// we will always receive the units
		
		////////////////////////////
		// and now the stepper bits
		//////////////////////////////////

		details.addProperty("LargeStep", MWCXMLReader.writeThis(controller
				.getLargeStep()));
		details.addProperty("SmallStep", MWCXMLReader.writeThis(controller
				.getSmallStep()));

		details.addProperty("AutoStepInterval", MWCXMLReader.writeThis(controller.getAutoInterval()));
		
		details.addProperty("DateFormat", controller.getDTGFormat());

		// the current DTG
		
		HiResDate cTime = timeProvider.getTime();
		if (cTime != null)
			details.addProperty("CurrentTime", MWCXMLReader.writeThis(cTime));
		
		// the T-zero, if set
		// and let's not bother with time-zero for now either
//		if (controller.getTimeZero() != null)
//			details.addProperty("TimeZero", MWCXMLReader.writeThis(controller.getTimeZero()));
		

		 // aah, there's the highlighter as well
		SWTPlotHighlighter highlighter = painterMgr.getCurrentHighlighter();
		if(highlighter != null)
			details.addProperty("Highlighter", highlighter.getName());
		
		// what's the time?
		// Let's not bother with this for now...
//		HiResDate theStartTime = controller.getToolboxStartTime();
//		if (theStartTime != null)
//			details.addProperty("Toolbox_Start_Time", DebriefFormatDateTime
//					.toStringHiRes(theStartTime));
//		HiResDate theEndTime = controller.getToolboxEndTime();
//		if (theEndTime != null)
//			details.addProperty("Toolbox_End_Time", DebriefFormatDateTime
//					.toStringHiRes(theEndTime));

		return details;
	}

}