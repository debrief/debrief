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
package org.mwc.debrief.core.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider.ViewLabelImageHelper;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.DynamicLayer;
import MWC.GUI.Editable;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.Chart.Painters.TimeDisplayPainter;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import MWC.GUI.Shapes.ChartFolio;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.TacticalData.NarrativeEntry;

public class DebriefImageHelper implements ViewLabelImageHelper
{

	public ImageDescriptor getImageFor(final Editable editable)
	{
		ImageDescriptor res = null;
		
		if (editable instanceof SensorWrapper)
			res = DebriefPlugin.getImageDescriptor("icons/16/sensor.png");
		else if (editable instanceof ChartFolio)
			res = DebriefPlugin.getImageDescriptor("icons/16/library.png");
		else if (editable instanceof ExternallyManagedDataLayer)
			res = DebriefPlugin.getImageDescriptor("icons/16/map.png");
		else if (editable instanceof ChartBoundsWrapper)
			res = DebriefPlugin.getImageDescriptor("icons/16/map.png");
		else if (editable instanceof SensorContactWrapper)
			res = DebriefPlugin.getImageDescriptor("icons/16/sensor_contact.png");
		else if (editable instanceof NarrativeWrapper)
			res = DebriefPlugin.getImageDescriptor("icons/16/narrative.png");
		else if (editable instanceof NarrativeEntry)
			res = DebriefPlugin.getImageDescriptor("icons/16/narrative_entry.png");
		else if (editable instanceof RelativeTMASegment)
			res = DebriefPlugin.getImageDescriptor("icons/16/tma_segment.png");
		else if (editable instanceof AbsoluteTMASegment)
			res = DebriefPlugin.getImageDescriptor("icons/16/abs_tma_segment.png");
		else if (editable instanceof CoreTMASegment)
			res = DebriefPlugin.getImageDescriptor("icons/16/tma_segment.png");
		else if (editable instanceof TrackSegment)
			res = DebriefPlugin.getImageDescriptor("icons/16/track_segment.png");
		else if (editable instanceof FixWrapper)
			res = DebriefPlugin.getImageDescriptor("icons/16/fix.png");
		else if (editable instanceof ShapeWrapper)
			res = DebriefPlugin.getImageDescriptor("icons/16/shape.png");
		else if (editable instanceof PolygonNode)
			res = DebriefPlugin.getImageDescriptor("icons/16/polygon.png");
		else if (editable instanceof LabelWrapper)
			res = DebriefPlugin.getImageDescriptor("icons/16/shape.png");
		else if (editable instanceof DynamicLayer)
			res = DebriefPlugin.getImageDescriptor("icons/16/clock.png");
		else if (editable instanceof TimeDisplayPainter)
		{	
			TimeDisplayPainter tdp = (TimeDisplayPainter) editable;
			if(tdp.isAbsolute())
				res = DebriefPlugin.getImageDescriptor("icons/16/clock.png");
			else
				res = DebriefPlugin.getImageDescriptor("icons/16/stopwatch.png");
		}
		else if (editable instanceof TrackWrapper)
		{
			// we're doing fancy testing here, so put it last in the list
			
			// see if it's a relative track
			TrackWrapper tw=  (TrackWrapper) editable;
			if(tw.isTMATrack())
				res = DebriefPlugin.getImageDescriptor("icons/16/track_relative.png");
			else
				res = DebriefPlugin.getImageDescriptor("icons/16/track.png");
		}
		 
		return res;
	}

}
