package Debrief.Wrappers.Track;

import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Iterator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.Layer;
import MWC.GUI.Plottables;
import MWC.GUI.SupportsPropertyListeners;

public class TrackWrapper_Support
{

	/**
	 * convenience class that makes our plottables look like a layer
	 * 
	 * @author ian.mayo
	 */
	abstract public static class BaseItemLayer extends Plottables implements
			Layer, SupportsPropertyListeners
	{

		/**
		 * property support
		 * 
		 */
		private PropertyChangeSupport _pSupport = new PropertyChangeSupport(this);

		@Override
		public void addPropertyChangeListener(PropertyChangeListener listener)
		{
			_pSupport.addPropertyChangeListener(listener);
		}

		@Override
		public void addPropertyChangeListener(String property,
				PropertyChangeListener listener)
		{
			_pSupport.addPropertyChangeListener(property, listener);
		}

		@Override
		public void firePropertyChange(String propertyChanged, Object oldValue,
				Object newValue)
		{
			_pSupport.firePropertyChange(propertyChanged, oldValue, newValue);
		}

		@Override
		public void removePropertyChangeListener(PropertyChangeListener listener)
		{
			_pSupport.removePropertyChangeListener(listener);
		}

		@Override
		public void removePropertyChangeListener(String property,
				PropertyChangeListener listener)
		{
			_pSupport.removePropertyChangeListener(property, listener);
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

			@Override
			public final String getName()
			{
				return super.getName();
			}

			@Override
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

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected TrackWrapper _myTrack;

		public void exportShape()
		{
			// ignore..
		}

		/**
		 * get the editing information for this type
		 */
		@Override
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

		public void setWrapper(TrackWrapper wrapper)
		{
			_myTrack = wrapper;
		}

		public TrackWrapper getWrapper()
		{
			return _myTrack;
		}

	}

	/**
	 * interface defining a boolean operation which is applied to all fixes in a
	 * track
	 */
	public interface FixSetter
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

			@Override
			public final MethodDescriptor[] getMethodDescriptors()
			{
				// just add the reset color field first
				final Class<SegmentList> c = SegmentList.class;
				final MethodDescriptor[] mds =
				{ method(c, "mergeAllSegments", null, "Merge all track segments") };
				return mds;
			}

			@Override
			public final String getName()
			{
				return super.getName();
			}

			@Override
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

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SegmentList()
		{
			setName("Track segments");
		}

		@Override
		public void add(Editable item)
		{
			System.err.println("SHOULD NOT BE ADDING NORMAL ITEM TO SEGMENT LIST");
		}

		public void addSegment(TrackSegment segment)
		{
			segment.setWrapper(_myTrack);

			if (this.size() == 1)
			{
				// aah, currently, it's name's probably wrong sort out it's date
				TrackSegment first = (TrackSegment) getData().iterator().next();
				first.sortOutDate(null);
			}

			super.add(segment);

			// if we've just got the one, set it's name to positions
			if (this.size() == 1)
			{
				TrackSegment first = (TrackSegment) getData().iterator().next();
				first.setName("Positions");
			}
		}

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

				if (first == null)
				{
					// aaah, now, if this is a TMA segment we've got to replace it with
					// a normal track segment. You can't join new track sections onto the
					// end
					// of a tma segment
					if (segment instanceof CoreTMASegment)
					{
						CoreTMASegment tma = (CoreTMASegment) segment;
						first = new TrackSegment(tma);
					}
					else
					{
						// cool, just go ahead
						first = segment;
					}
				}
				else
				{
					first.append((Layer) segment);
				}
			}

			// ditch the segments
			this.removeAllElements();

			// and put the first one back in
			this.addSegment(first);

			// and fire some kind of update...
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
				TrackSegment seg = (TrackSegment) iterator.next();
				seg.setWrapper(_myTrack);
			}
		}

	}

}
