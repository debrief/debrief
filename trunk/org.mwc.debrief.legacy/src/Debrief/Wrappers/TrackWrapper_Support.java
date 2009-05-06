package Debrief.Wrappers;

import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class TrackWrapper_Support
{


	/**
	 * interface defining a boolean operation which is applied to all fixes in a
	 * track
	 */
	protected interface FixSetter
	{
		/**
		 * operation to apply to a fix
		 * 
		 * @param fix
		 *          subject of operation
		 * @param val
		 *          yes/no value to apply
		 */
		public void execute(FixWrapper fix, boolean val);
	}

	/**
	 * embedded class to allow us to pass the local iterator (Iterator) used
	 * internally outside as an Enumeration
	 */
	public static final class IteratorWrapper implements
			java.util.Enumeration<Editable>
	{
		private final Iterator<Editable> _val;

		public IteratorWrapper(final Iterator<Editable> iterator)
		{
			_val = iterator;
		}

		public final boolean hasMoreElements()
		{
			return _val.hasNext();

		}

		public final Editable nextElement()
		{
			return _val.next();
		}
	}

	/**
	 * the collection of track segments
	 * 
	 * @author Administrator
	 * 
	 */
	final public static class SegmentList extends BaseItemLayer
	{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public SegmentList()
		{
			setName("Track segments");
		}

		
		@Override
		public void setWrapper(TrackWrapper wrapper)
		{
			// is it different?
			if(wrapper == _myTrack)
				return;
			
			// store the value
			super.setWrapper(wrapper);

			// update our segments
			Collection<Editable> items = getData();
			for (Iterator<Editable> iterator = items.iterator(); iterator.hasNext();)
			{
				TrackSegment seg = (TrackSegment) iterator.next();
				seg.setWrapper(_myTrack);
			}
		}



		public void addSegment(TrackSegment segment)
		{
			segment.setWrapper(_myTrack);
			
			if(this.size() == 1)
			{
				// aah, currently, it's name's probably wrong sort out it's date
				TrackSegment first = (TrackSegment) getData().iterator().next();
				first.sortOutDate();
			}
			
			super.add(segment);
			
			// if we've just got the one, set it's name to positions
			if(this.size() == 1)
			{
				TrackSegment first = (TrackSegment) getData().iterator().next();
				first.setName("Positions");
			}
		}
		
		public void add(Editable item)
		{
			System.err.println("SHOULD NOT BE ADDING NORMAL ITEM TO SEGMENT LIST");
		}

		@Override
		public void append(Layer other)
		{
			System.err.println("SHOULD NOT BE ADDING LAYER TO SEGMENTS LIST");
		}
		
		
		
		@Override
		public EditorType getInfo()
		{
			return new SegmentInfo(this);
		}

		@FireExtended
		public void mergeAllSegments()
		{
			Collection<Editable> segs = getData();
			TrackSegment first = null;
			for (Iterator<Editable> iterator = segs.iterator(); iterator.hasNext();)
			{
				TrackSegment segment = (TrackSegment) iterator.next();
				
				if(first == null)
					first = segment;
				else
				{
					first.append((Layer)segment);
				}				
			}
			
			// ditch the segments
			this.removeAllElements();
			
			// and put the first one back in
			this.addSegment(first);
			
			// and fire some kind of update...
		}
		
		/**
		 * class containing editable details of a track
		 */
		public final class SegmentInfo extends Editable.EditorType
		{

			/**
			 * constructor for this editor, takes the actual track as a parameter
			 * 
			 * @param data
			 *          track being edited
			 */
			public SegmentInfo(final SegmentList data)
			{
				super(data, data.getName(), "");
			}

			public final String getName()
			{
				return super.getName();
			}

			public final PropertyDescriptor[] getPropertyDescriptors()
			{
				try
				{
					final PropertyDescriptor[] res =
					{ expertProp("Visible", "whether this layer is visible", FORMAT), };
					return res;
				}
				catch (final IntrospectionException e)
				{
					e.printStackTrace();
					return super.getPropertyDescriptors();
				}
			}

			@Override
		    public final MethodDescriptor[] getMethodDescriptors()
		    {
		      // just add the reset color field first
		      final Class<SegmentList> c = SegmentList.class;
		      final MethodDescriptor[] mds = {
		        method(c, "mergeAllSegments", null, "Merge all track segments")
		      };
		      return mds;
			}
		}
		

	}
	
	/**
	 * a single collection of track points
	 * 
	 * @author Administrator
	 * 
	 */
	final public static class TrackSegment extends BaseItemLayer implements DraggableItem
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/** define the length of the stalk we plot when dragging
		 * 
		 */
		final int len = 400;
		
		@Override
		public void setWrapper(TrackWrapper wrapper)
		{
			// is it different?
			if(wrapper == _myTrack)
				return;
			
			// store the value
			super.setWrapper(wrapper);

			// update our segments
			Collection<Editable> items = getData();
			for (Iterator<Editable> iterator = items.iterator(); iterator.hasNext();)
			{
				FixWrapper fix =  (FixWrapper) iterator.next();				
				fix.setTrackWrapper(_myTrack);
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

		@Override
		public double rangeFrom(WorldLocation other)
		{
			FixWrapper first = (FixWrapper) this.first();
			FixWrapper last = (FixWrapper) this.last();
			WorldArea area = new WorldArea(first.getFixLocation(), last.getFixLocation());
			WorldLocation centrePt = area.getCentre();
			double centre = centrePt.rangeFrom(other);
			double oneEnd = first.rangeFrom(other);
			double otherEnd = last.rangeFrom(other);
			double res = Math.min(centre, oneEnd);
			res = Math.min(res, otherEnd);
			return res;
		}

		private boolean _plotDR = false;


		public void append(Layer other)
		{
			// ok, pass through and add the items
			final Enumeration<Editable> enumer = other.elements();
			while (enumer.hasMoreElements())
			{
				final FixWrapper pl =  (FixWrapper) enumer.nextElement();
				
				addFix(pl);
			}
		}
		
		/** create a segment based on the suppplied items
		 * 
		 * @param theItems
		 */
		public TrackSegment(SortedSet<Editable> theItems)
		{
			getData().addAll(theItems);
			
			// now sort out the name
			sortOutDate();
		}

		public void sortOutDate()
		{
			if(getData().size() > 0)
				setName(FormatRNDateTime.toString(startDTG().getDate().getTime()));
		}
		
		public TrackSegment()
		{
			// no-op constructor
		}

		/** sort the items in ascending order
		 * 
		 */
		public int compareTo(Plottable arg0)
		{
			int res = 0;
			if(arg0 instanceof TrackSegment)
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
			return  res;
		}		
		
		/** find the start time of each segment
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
		
		public boolean getPlotDR()
		{
			return _plotDR;
		}

		public void setPlotDR(boolean _plotdr)
		{
			_plotDR = _plotdr;
		}
		
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

		@Override
		public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
				LocationConstruct currentNearest, Layer parentLayer)
		{
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
				if(lastPoint != null)
				{
					// draw that line
					dest.drawLine(lastPoint.x, lastPoint.y, thisPoint.x, thisPoint.y);

					// are we at the start of the line?
					if(lastButOne == null)
					{
						drawMyStalk(dest, lastPoint, thisPoint, false);
					}					
				}				
				
				lastButOne = lastPoint;
				lastPoint = new Point(thisPoint);
				
				// also draw in a marker for this point
				dest.drawRect(lastPoint.x-1, lastPoint.y-1,3,3);
			}
			
			// lastly 'plot on' from the last points
			drawMyStalk(dest, lastPoint, lastButOne, true);
			
		}

		private void drawMyStalk(CanvasType dest, Point lastPoint, Point thisPoint, boolean forwards)
		{
			// yup, we've now got just two points. plot a 'back-trace'
			double xDelta = thisPoint.x - lastPoint.x;
			double yDelta = thisPoint.y - lastPoint.y;
			
			double gradient = xDelta / yDelta;

			int myLen = len;
			if(!forwards)
				myLen = -len;
			
			Point backPoint = new Point(lastPoint.x + (int)(myLen * gradient),lastPoint.y + myLen );
			dest.setLineStyle(2);
			dest.drawLine(lastPoint.x, lastPoint.y, backPoint.x, backPoint.y);
			
			// hey, chuck in a circle
			int radius = 10;
			dest.drawOval(lastPoint.x - radius - (int)xDelta, lastPoint.y -radius - (int)yDelta,radius*2, radius*2);
			
			dest.setLineStyle(CanvasType.SOLID);
		}

		@Override
		public void shift(WorldVector vector)
		{
			// add this vector to all my points.
			Collection<Editable> items = getData();
			for (Iterator<Editable> iterator = items.iterator(); iterator.hasNext();)
			{
				FixWrapper thisFix =  (FixWrapper) iterator.next();

				final WorldLocation copiedLoc = new WorldLocation(thisFix.getFix()
						.getLocation());
				copiedLoc.addToMe(vector);

				// and replace the location (this method updates all 3 location
				// contained
				// in the fix wrapper
				thisFix.setFixLocation(copiedLoc);
			}
		}

		public HiResDate endDTG()
		{
			Collection<Editable> items = getData();
			SortedSet<Editable> sortedItems = (SortedSet<Editable>) items;
			Editable last = sortedItems.last();
			FixWrapper fw = (FixWrapper) last;
			return fw.getDateTimeGroup();
		}

	}

	/**
	 * convenience class that makes our plottables look like a layer
	 * 
	 * @author ian.mayo
	 */
	abstract public static class BaseItemLayer extends Plottables implements Layer
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected TrackWrapper _myTrack;
		
		
		public void exportShape()
		{
			// ignore..
		}
		
		public void setWrapper(TrackWrapper wrapper)
		{
			_myTrack = wrapper;
		}

		/**
		 * get the editing information for this type
		 */
		public Editable.EditorType getInfo()
		{
			return new BaseLayerInfo(this);
		}

		public int getLineThickness()
		{
			// ignore..
			return 1;
		}

		public boolean hasOrderedChildren()
		{
			return true;
		}

		/**
		 * class containing editable details of a track
		 */
		public final class BaseLayerInfo extends Editable.EditorType
		{

			/**
			 * constructor for this editor, takes the actual track as a parameter
			 * 
			 * @param data
			 *          track being edited
			 */
			public BaseLayerInfo(final BaseItemLayer data)
			{
				super(data, data.getName(), "");
			}

			public final String getName()
			{
				return super.getName();
			}

			public final PropertyDescriptor[] getPropertyDescriptors()
			{
				try
				{
					final PropertyDescriptor[] res =
					{ expertProp("Visible", "whether this layer is visible", FORMAT), };
					return res;
				}
				catch (final IntrospectionException e)
				{
					e.printStackTrace();
					return super.getPropertyDescriptors();
				}
			}
		}

	}
	
}
