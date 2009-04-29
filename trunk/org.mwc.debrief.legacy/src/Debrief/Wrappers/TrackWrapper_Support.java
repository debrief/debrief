package Debrief.Wrappers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.Iterator;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;

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
	static final class IteratorWrapper implements
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
	final public class SegmentList extends BaseItemLayer
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void addSegment(TrackSegment segment)
		{
			super.add(segment);
		}

	}

	/**
	 * a single collection of track points
	 * 
	 * @author Administrator
	 * 
	 */
	final public static class TrackSegment extends BaseItemLayer
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private boolean _plotDR = false;

		public boolean getPlotDR()
		{
			return _plotDR;
		}

		public void setPlotDR(boolean _plotdr)
		{
			_plotDR = _plotdr;
		}

		public void addFix(FixWrapper fix)
		{
			super.add(fix);
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

		/**
		 * only allow fixes to be added...
		 * 
		 * @param thePlottable
		 */
		public void add(Editable thePlottable)
		{
			if (thePlottable instanceof FixWrapper)
			{
				super.add(thePlottable);
			}
			else
			{
				System.err.println("Trying to add wront");
			}
		}

		public void append(Layer other)
		{
			// ok, pass through and add the items
			final Enumeration<Editable> enumer = other.elements();
			while (enumer.hasMoreElements())
			{
				final Plottable pl = (Plottable) enumer.nextElement();
				add(pl);
			}
		}

		public void exportShape()
		{
			// ignore..
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
