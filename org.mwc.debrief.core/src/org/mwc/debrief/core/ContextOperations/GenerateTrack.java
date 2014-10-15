/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.ContextOperations;

import java.awt.Color;
import java.util.*;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.*;
import MWC.GUI.*;
import MWC.GUI.Shapes.*;
import MWC.GenericData.*;
import MWC.TacticalData.*;

/**
 * @author ian.mayo
 *
 */
public class GenerateTrack implements RightClickContextItemGenerator
{


	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	public void generate(final IMenuManager parent, final Layers theLayers, final Layer[] parentLayers,
			final Editable[] subjects)
	{
		int layersValidForConvertToTrack = 0;
		
		// right, work through the subjects
		for (int i = 0; i < subjects.length; i++)
		{
			final Editable thisE = subjects[i];
			if(thisE instanceof BaseLayer)
			{
				// ok, we're started...
				final BaseLayer layer = (BaseLayer) thisE;
				
				// does it have any children?
				if(layer.size() > 0)
				{
					// hey, looking good, why not loop through them?
					final Enumeration<Editable> items = layer.elements();
					while(items.hasMoreElements())
					{
						final Plottable thisP = (Plottable) items.nextElement();
						
						// is this one suitable?
						if(isSuitableAsTrackPoint(thisP))
						{
							// cool, go for it!
							layersValidForConvertToTrack++;
							break;
						}
						
					}
				}				
			}						
		}
		
		// ok, is it worth going for?
		if(layersValidForConvertToTrack > 0)
		{
			final String title;
			if(layersValidForConvertToTrack > 1)
				 title = "Convert layers to tracks";
			else
				 title = "Convert layer to track";
			
			// yes, create the action
			final Action convertToTrack = new Action(title)
			{
				public void run()
				{
					// ok, go for it.
					// sort it out as an operation
					final IUndoableOperation convertToTrack1 = new ConvertTrack(title, theLayers, 
							subjects);

					// ok, stick it on the buffer
					runIt(convertToTrack1);
				}				
			};
			
			// right,stick in a separator
			parent.add(new Separator());
			
			// ok - flash up the menu item
			parent.add(convertToTrack);
		}
		
	}
	
	/** put the operation firer onto the undo history.  We've refactored this
	 * into a separate method so testing classes don't have to simulate the CorePlugin
	 * @param operation
	 */
	protected void runIt(final IUndoableOperation operation)
	{
		CorePlugin.run(operation);
	}

	private static class ConvertTrack extends CMAPOperation
	{

		private final Layers _layers;
		private final Editable[] _subjects;
		
		private Vector<TrackWrapper> _newTracks;

		public ConvertTrack(final String title, final Layers layers, final Editable[] subjects)
		{
			super(title);
			_layers = layers;
			_subjects = subjects;
		}

		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException
		{
			// right, get going through the track
			for (int i = 0; i < _subjects.length; i++)
			{
				final Editable thisE = _subjects[i];
				if(thisE instanceof BaseLayer)
				{
					final BaseLayer layer = (BaseLayer) thisE;
					final Enumeration<Editable> numer = layer.elements();
					while (numer.hasMoreElements())
					{
						final Plottable pl = (Plottable) numer.nextElement();
						
						// ok, is it suitable?
						if(isSuitableAsTrackPoint(pl))
						{
							// cool, pass through it, generating the track points
							final TrackWrapper tw = generateTrackFor(layer);
							
							if(tw != null)
							{
								_layers.addThisLayer(tw);
								
								// and remember it, for the undo operation
								if(_newTracks == null)
									_newTracks = new Vector<TrackWrapper>(0,1);
								
								_newTracks.add(tw);
							}
							
							break;
						}
					}
				}
			}
			
			// sorted, do the update
			_layers.fireExtended();
			
			return Status.OK_STATUS;
		}

		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException
		{
			// forget about the new tracks
			for (final Iterator<TrackWrapper> iter = _newTracks.iterator(); iter.hasNext();)
			{
				final TrackWrapper trk = (TrackWrapper) iter.next();
				_layers.removeThisLayer(trk);
			}
			
			// and clear the new tracks item
			_newTracks.removeAllElements();
			_newTracks = null;
			
			return Status.OK_STATUS;
		}
		
	}
	
	/** find out if this item is suitable for use as a track item
	 * 
	 * @param thisP
	 * @return
	 */
	static boolean isSuitableAsTrackPoint(final Plottable thisP)
	{
		boolean res = false;
		
		// ok - is it a label?  Converting that to a track point is quite easy
		if(thisP instanceof LabelWrapper)
		{
			res = true;
		}
		
		// next, see if it's a line, because the pretend track could have been
		// drawn up as a series of lines
		if(thisP instanceof ShapeWrapper)
		{
			final ShapeWrapper sw = (ShapeWrapper) thisP;
			final PlainShape shp = sw.getShape();
			if(shp instanceof LineShape)
				res = true;
		}
		return res;
	}

	public static TrackWrapper generateTrackFor(final BaseLayer layer)
	{
		TrackWrapper res = new TrackWrapper();
		res.setName("T_" + layer.getName());
		
		Color trackColor = null;
		
		// ok, step through the points
		final Enumeration<Editable> numer = layer.elements();
		
		// remember the last line viewed, since we want to add both of it's points
		ShapeWrapper lastLine = null;
		
		while (numer.hasMoreElements())
		{
			final Plottable pl = (Plottable) numer.nextElement();
			if(pl instanceof LabelWrapper)
			{
				final LabelWrapper label = (LabelWrapper) pl;

				// just check we know the track color
				if(trackColor == null)
					trackColor = label.getColor();

				HiResDate dtg = label.getStartDTG();
				if(dtg == null)
					dtg = new HiResDate(new Date());
				
				final WorldLocation loc = label.getBounds().getCentre();
				final Fix newFix = new Fix(dtg, loc, 0, 0);
				final FixWrapper fw = new FixWrapper(newFix);
				
				if(label.getColor() != trackColor)
					fw.setColor(label.getColor());

				res.add(fw);
				fw.setTrackWrapper(res);
				
				// forget the last-line, clearly we've moved on to other things
				lastLine = null;
				
			}
			else if(pl instanceof ShapeWrapper)
			{
				final ShapeWrapper sw = (ShapeWrapper) pl;
				final PlainShape shape = sw.getShape();
				if(shape instanceof LineShape)
				{
					final LineShape line = (LineShape) shape;
					// just check we know the track color
					if(trackColor == null)
						trackColor = line.getColor();
					
					final HiResDate dtg = sw.getStartDTG();
					final WorldLocation loc = line.getLine_Start();
					final Fix newFix = new Fix(dtg, loc, 0, 0);
					final FixWrapper fw = new FixWrapper(newFix);
					
					if(line.getColor() != trackColor)
						fw.setColor(line.getColor());
					fw.setTrackWrapper(res);
					res.add(fw);
					
					// and remember this line
					lastLine = sw;
					
					
				}
			}
		}
		
		// did we have a trailing line item?
		if(lastLine != null)
		{
			final HiResDate dtg = lastLine.getEndDTG();
			final LineShape line = (LineShape) lastLine.getShape();
			final WorldLocation loc = line.getLineEnd();
			final Fix newFix = new Fix(dtg, loc, 0, 0);
			final FixWrapper fw = new FixWrapper(newFix);
			fw.setTrackWrapper(res);
			res.add(fw);
		}

		// update the track color
		res.setColor(trackColor);
		
		// did we find any?
		if(res.numFixes() == 0)
			 res = null;
		
		return res;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
		}

		public final void testIWork()
		{
			final Layers theLayers = new Layers();
			final BaseLayer holder = new BaseLayer();
			holder.setName("Trk");
			theLayers.addThisLayer(holder);
			
			WorldLocation lastLoc = null;
			for(int i=0;i<4;i++)
			{
				final WorldLocation thisLoc = new WorldLocation(0,i,0,'N',0,0,0,'W', 0);
				if(lastLoc != null)
				{
					// ok, add the line
					final LineShape ls = new LineShape(lastLoc, thisLoc);
					
					final long theDate1 = 20000000 + i * 60000;
					final long theDate2 = 20000000 + i * 61000;
					
					final ShapeWrapper sw = new ShapeWrapper("shape:" + i, ls, Color.red, new HiResDate(theDate1));
					sw.setTime_Start(new HiResDate(theDate1));
					sw.setTimeEnd(new HiResDate(theDate2));
					holder.add(sw);
				}

				// and remember the last location
				lastLoc = thisLoc;
			}
			
			// ok, now do the interpolation
			final ConvertTrack ct = new ConvertTrack("convert it", theLayers, new Editable[]{holder});
			
			try
			{
				ct.execute(null, null);
			}
			catch (final ExecutionException e)
			{
				fail("Exception thrown");
			}
			
			// check the track got generated
			final TrackWrapper tw = (TrackWrapper) theLayers.findLayer("T_Trk");
			
			// did we find it?
			assertNotNull("track generated", tw);
			
			 // check we've got the right number of fixes
			assertEquals("right num of fixes generated", tw.numFixes(), 4);

		}
	}
}
