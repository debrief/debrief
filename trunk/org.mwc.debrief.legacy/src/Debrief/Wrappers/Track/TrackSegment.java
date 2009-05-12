package Debrief.Wrappers.Track;

import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.BaseItemLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

/**
	 * a single collection of track points
	 * 
	 * @author Administrator
	 * 
	 */
	public class TrackSegment extends BaseItemLayer implements
			DraggableItem
	{
		
		/**
		 * class containing editable details of a track
		 */
		public class TrackSegmentInfo extends Editable.EditorType
		{

			/**
			 * constructor for this editor, takes the actual track as a parameter
			 * 
			 * @param data
			 *          track being edited
			 */
			public TrackSegmentInfo(final TrackSegment data)
			{
				super(data, data.getName(), "");
			}

			@Override
			public  PropertyDescriptor[] getPropertyDescriptors()
			{
				try
				{
					final PropertyDescriptor[] res =
					{ expertProp("Visible", "whether this layer is visible", FORMAT),
};
					return res;
				}
				catch (final IntrospectionException e)
				{
					e.printStackTrace();
					return super.getPropertyDescriptors();
				}
			}
		}		

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * define the length of the stalk we plot when dragging
		 * 
		 */
		final int len = 200;
		
		/** whether to determine this track's positions using DR calculations
		 * 
		 */
		boolean _plotRelative;

		private WorldVector _vecTempLastVector = null;

		protected long _vecTempLastDTG = -2;

		public boolean getPlotRelative()
		{
			return _plotRelative;
		}

		public void setPlotRelative(boolean plotRelative)
		{
			_plotRelative = plotRelative;
		}

		public TrackSegment()
		{
			// no-op constructor
		}

		/**
		 * create a segment based on the suppplied items
		 * 
		 * @param theItems
		 */
		public TrackSegment(SortedSet<Editable> theItems)
		{
			getData().addAll(theItems);

			// now sort out the name
			sortOutDate();
		}

		@Override
		public void add(Editable item)
		{
			System.err.println("SHOULD NOT BE ADDING NORMAL ITEM TO TRACK SEGMENT");
		}

		public void addFix(FixWrapper fix)
		{
			super.add(fix);

			// override the name, just in case this point is earlier
			sortOutDate();

			// tell it about our daddy
			fix.setTrackWrapper(_myTrack);
		}

		public void append(Layer other)
		{
			// ok, pass through and add the items
			final Enumeration<Editable> enumer = other.elements();
			while (enumer.hasMoreElements())
			{
				final FixWrapper pl = (FixWrapper) enumer.nextElement();

				addFix(pl);
			}
		}
		
		/**
		 * get the start of this segment (it's the location of the first point).
		 * 
		 * @return
		 */
		public WorldLocation getOrigin()
		{
			FixWrapper firstW = (FixWrapper) getData().iterator().next();
			return firstW.getFixLocation();
		}

		/**
		 * sort the items in ascending order
		 * 
		 */
		@Override
		public int compareTo(Plottable arg0)
		{
			int res = 0;
			if (arg0 instanceof TrackSegment)
			{
				// sort them in dtg order
				TrackSegment other = (TrackSegment) arg0;
				res = startDTG().compareTo(other.startDTG());
			}
			else
			{
				// just use string comparison
				res = getName().compareTo(arg0.getName());
			}
			return res;
		}

		protected void drawMyStalk(CanvasType dest, Point lastPoint,
				Point thisPoint, boolean forwards)
		{
			// yup, we've now got just two points. plot a 'back-trace'
			double xDelta = thisPoint.x - lastPoint.x;
			double yDelta = thisPoint.y - lastPoint.y;

			double gradient = xDelta / yDelta;

			int myLen = len;
			if (!forwards)
				myLen = -len;

			Point backPoint = new Point(lastPoint.x + (int) (myLen * gradient),
					lastPoint.y + myLen);
			dest.setLineStyle(2);
			dest.drawLine(lastPoint.x, lastPoint.y, backPoint.x, backPoint.y);

			// hey, chuck in a circle
			int radius = 10;
			dest.drawOval(lastPoint.x - radius - (int) xDelta, lastPoint.y - radius
					- (int) yDelta, radius * 2, radius * 2);

			dest.setLineStyle(CanvasType.SOLID);
		}

		public HiResDate endDTG()
		{
			Collection<Editable> items = getData();
			SortedSet<Editable> sortedItems = (SortedSet<Editable>) items;
			Editable last = sortedItems.last();
			FixWrapper fw = (FixWrapper) last;
			return fw.getDateTimeGroup();
		}

		public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
				LocationConstruct currentNearest, Layer parentLayer)
		{
		}

		public Editable.EditorType getInfo()
		{
			return new TrackSegmentInfo(this);
		}

		@Override
		public void paint(CanvasType dest)
		{
			Collection<Editable> items = getData();

			// ok - draw that line!
			Point lastPoint = null;
			Point lastButOne = null;
			for (Iterator<Editable> iterator = items.iterator(); iterator.hasNext();)
			{
				FixWrapper thisF = (FixWrapper) iterator.next();

				Point thisPoint = dest.toScreen(thisF.getFixLocation());

				// do we have enough for a line?
				if (lastPoint != null)
				{
					// draw that line
					dest.drawLine(lastPoint.x, lastPoint.y, thisPoint.x, thisPoint.y);

					// are we at the start of the line?
					if (lastButOne == null)
					{
						drawMyStalk(dest, lastPoint, thisPoint, false);
					}
				}

				lastButOne = lastPoint;
				lastPoint = new Point(thisPoint);

				// also draw in a marker for this point
				dest.drawRect(lastPoint.x - 1, lastPoint.y - 1, 3, 3);
			}

			// lastly 'plot on' from the last points
			drawMyStalk(dest, lastPoint, lastButOne, true);

		}

		@Override
		public double rangeFrom(WorldLocation other)
		{
			FixWrapper first = (FixWrapper) this.first();
			FixWrapper last = (FixWrapper) this.last();
			WorldArea area = new WorldArea(first.getFixLocation(), last
					.getFixLocation());
			WorldLocation centrePt = area.getCentre();
			double centre = centrePt.rangeFrom(other);
			double oneEnd = first.rangeFrom(other);
			double otherEnd = last.rangeFrom(other);
			double res = Math.min(centre, oneEnd);
			res = Math.min(res, otherEnd);
			return res;
		}

		@Override
		public void setWrapper(TrackWrapper wrapper)
		{
			// is it different?
			if (wrapper == _myTrack)
				return;

			// store the value
			super.setWrapper(wrapper);

			// update our segments
			Collection<Editable> items = getData();
			for (Iterator<Editable> iterator = items.iterator(); iterator.hasNext();)
			{
				FixWrapper fix = (FixWrapper) iterator.next();
				fix.setTrackWrapper(_myTrack);
			}
		}

		public void shift(WorldVector vector)
		{
			// add this vector to all my points.
			Collection<Editable> items = getData();
			for (Iterator<Editable> iterator = items.iterator(); iterator.hasNext();)
			{
				FixWrapper thisFix = (FixWrapper) iterator.next();

				final WorldLocation copiedLoc = new WorldLocation(thisFix.getFix()
						.getLocation());
				copiedLoc.addToMe(vector);

				// and replace the location (this method updates all 3 location
				// contained
				// in the fix wrapper
				thisFix.setFixLocation(copiedLoc);
			}
		}

		/**
		 * move the whole of the track be the provided offset
		 */
		public final void shiftTrack(Enumeration<Editable> theEnum,
				final WorldVector offset)
		{
			if (theEnum == null)
				theEnum = elements();

			while (theEnum.hasMoreElements())
			{
				final Object thisO = theEnum.nextElement();
				if (thisO instanceof FixWrapper)
				{
					final FixWrapper fw = (FixWrapper) thisO;

					final WorldLocation copiedLoc = new WorldLocation(fw.getFix()
							.getLocation());
					copiedLoc.addToMe(offset);

					// and replace the location (this method updates all 3 location
					// contained
					// in the fix wrapper
					fw.setFixLocation(copiedLoc);

					// ok - job well done
				}
			}
		}

		public void sortOutDate()
		{
			if (getData().size() > 0)
				setName(FormatRNDateTime.toString(startDTG().getDate().getTime()));
		}

		/**
		 * find the start time of each segment
		 * 
		 * @return
		 */
		public HiResDate startDTG()
		{
			Collection<Editable> items = getData();
			SortedSet<Editable> sortedItems = (SortedSet<Editable>) items;
			Editable first = sortedItems.first();
			FixWrapper fw = (FixWrapper) first;
			return fw.getDateTimeGroup();
		}

		/**
		 * represent the named leg as a DR vector
		 * 
		 * @param fw
		 *          the leg we're looking at
		 * @param period
		 *          how long it's travelling for
		 * @return a vector representing the subject
		 */
		public WorldVector vectorFor(long period, double speedKts, double courseRads)
		{
			// have we already looked for this
			if (period != _vecTempLastDTG)
			{
				// nope better calc it
				WorldDistance dist = new WorldDistance(speedKts * period / (1000 * 60 * 60),
						WorldDistance.KM);
				_vecTempLastVector = new WorldVector(courseRads, dist, null);
			}
		
			return _vecTempLastVector;
		}

	}