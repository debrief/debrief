package org.mwc.debrief.core.editors.painters.snail;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import org.mwc.debrief.core.editors.painters.SnailHighlighter;
import org.mwc.debrief.core.editors.painters.SnailHighlighter.drawSWTHighLight;

import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact.HostedList;
import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact.PlottableWrapperWithTimeAndOverrideableColor;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * Created by IntelliJ IDEA. User: ian.mayo Date: 22-Feb-2005 Time: 09:10:33 To
 * change this template use File | Settings | File Templates.
 */
public abstract class SnailDrawSWTTacticalContact implements drawSWTHighLight,
		MWC.GUI.Editable
{
	/**
	 * the snail plotter we are using = we look at this to determine plotting
	 * characteristics
	 */
	protected SnailDrawSWTFix _fixPlotter = null;

	/**
	 * do the plotting
	 */
	public final Rectangle drawMe(final MWC.Algorithms.PlainProjection proj,
			final CanvasType dest, final WatchableList list, final Watchable watch,
			final SnailHighlighter parent, final HiResDate dtg, final Color backColor)
	{

		Rectangle thisR = null;

		final boolean keepItSimple = true;

		// get a pointer to the fix
		final PlottableWrapperWithTimeAndOverrideableColor contact = (PlottableWrapperWithTimeAndOverrideableColor) watch;
		final HostedList wrapper = (HostedList) list;

		// how long? (convert to millis)
		final long trail_len = (long) _fixPlotter.getTrailLength().getValueIn(
				Duration.MICROSECONDS);

		// are we plotting a back-track?
		if (trail_len > 0)
		{
			// calculate the start time
			final HiResDate start_time = new HiResDate(0, dtg.getMicros() - trail_len);

			// get the list of contacts
			final java.util.Collection<Editable> contacts = list.getItemsBetween(
					start_time, dtg);

			// get the parent track - so we can plot relative to it..
			final TrackWrapper hostTrack = (TrackWrapper) wrapper.getHost();

			// how long is it?
			if (contacts != null)
			{
				final float len = contacts.size();

				if (len > 0)
				{
					// ok, work back from the last one
					final java.util.Iterator<Editable> cons = contacts.iterator();
					while (cons.hasNext())
					{
						// get the next contact
						final PlottableWrapperWithTimeAndOverrideableColor scw = (PlottableWrapperWithTimeAndOverrideableColor) cons
								.next();

						// ahhh, there's a problem with ellipses, they only normally
						// know their offset from the track when they've been painted
						// by a 'normal' painter. So, explicitly tell them where
						// they're centred from
						if (scw instanceof TMAContactWrapper)
						{
							TMAContactWrapper tma = (TMAContactWrapper) scw;
							tma.locateEllipseCentre(hostTrack);

						}

						// sort out the area for this tua
						final WorldArea wa = scw.getBounds();

						// did we find an area?
						// there's a chance that we have tua data which extends beyond the
						// time period of the track,
						// and for relative tua data we shouldn't even try to plot it
						if (wa != null)
						{
							// so, take a safe copy of the actual colour (which we will
							// restore later)
							final Color oldCol = scw.getActualColor();

							// now take the colour in use (which may in fact belong to the
							// parent class)
							Color thisCol = scw.getColor();

							// do we want to fade the cut data?
							boolean doFade = parent.getSnailProperties().getFadePoints();

							if (doFade)
							{
								thisCol = SnailDrawSWTTrack.getFadedColorFor(thisCol,
										backColor, trail_len, dtg, scw.getTime());
							}

							// and put this colour back into the data item
							scw.setColor(thisCol);

							// paint the object
							scw.paint(hostTrack, dest, keepItSimple);

							// restore the colour, if it's non-null
							scw.setColor(oldCol);

							// cool, we've found the location. sorted.
							final WorldLocation tl = wa.getTopLeft();
							final WorldLocation br = wa.getBottomRight();
							final Point pTL = new Point(proj.toScreen(tl));
							final Point pBR = new Point(proj.toScreen(br));
							final Rectangle thisArea = new Rectangle(pTL);
							thisArea.add(pBR);
							if (thisR == null)
								thisR = thisArea;
							else
								thisR.add(thisArea);
						}

					} // while there are still contacts
				} // if one or more contacts got returned
			} // if a list of contacts got returned
		} // if we are plotting a back-track at all
		else
		{
			// just plot the most recent one
			// work out the area covered
			final WorldArea wa = watch.getBounds();
			final WorldLocation tl = wa.getTopLeft();
			final WorldLocation br = wa.getBottomRight();
			final Point pTL = new Point(proj.toScreen(tl));
			final Point pBR = new Point(proj.toScreen(br));
			final Rectangle thisArea = new Rectangle(pTL);
			thisArea.add(pBR);
			thisR = thisArea;

			// and plot in the line
			contact.paint(null, dest, keepItSimple);

		}

		return thisR;
	}

	public abstract boolean canPlot(Watchable wt);

	public final String getName()
	{
		return "Snail";
	}

	public final String toString()
	{
		return getName();
	}

	public final boolean hasEditor()
	{
		return false;
	}

	public final EditorType getInfo()
	{
		return null;
	}

}
