package Debrief.Wrappers;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import Debrief.Wrappers.Track.PlanningSegment;
import Debrief.Wrappers.Track.PlanningSegment.ClosingSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PlanningLegCalcModelPropertyEditor;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * class that represents a series of track legs, but a single start
 * time/location
 * 
 * @author ian
 * 
 */
public class CompositeTrackWrapper extends TrackWrapper implements
		GriddableSeriesMarker, NeedsToKnowAboutLayers
{

	public static interface GiveMeALeg
	{
		public void createLegFor(Layer parent);
	}

	/**
	 * class containing editable details of a track
	 */
	public final class CompositeTrackInfo extends Editable.EditorType implements
			Editable.DynamicDescriptors
	{

		/**
		 * constructor for this editor, takes the actual track as a parameter
		 * 
		 * @param data
		 *          track being edited
		 */
		public CompositeTrackInfo(final CompositeTrackWrapper data)
		{
			super(data, data.getName(), "");
		}

		@Override
		public final MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			final Class<CompositeTrackWrapper> c = CompositeTrackWrapper.class;

			final MethodDescriptor[] mds =
			{
					method(c, "addLeg", null, "Add new leg"),
					method(c, "addClosingLeg", null, "Add closing leg"),
					method(c, "exportThis", null, "Export Shape"),
					method(c, "appendReverse", null, "Append reverse version of segments"), };

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
				PropertyDescriptor[] res =
				{
						expertProp("Origin", "where this track starts", FORMAT),
						expertProp("StartDate", "the time this track starts", FORMAT),

						expertLongProp("LabelFrequency", "the label frequency",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						expertLongProp("SymbolFrequency", "the symbol frequency",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						expertLongProp("ResampleDataAt", "the data sample rate",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						expertLongProp("ArrowFrequency", "the direction marker frequency",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						expertProp("SymbolColor", "the color of the symbol (when used)",
								FORMAT),
						expertProp("TrackFont", "the track label font", FORMAT),
						expertProp("NameVisible", "show the track label", VISIBILITY),
						expertProp("NameAtStart",
								"whether to show the track name at the start (or end)",
								VISIBILITY),
						expertProp("Name", "the track name", FORMAT),
						expertLongProp("SymbolType",
								"the type of symbol plotted for this label",
								MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor.class),

				};
				return res;
			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

	}

	private static GiveMeALeg _triggerNewLeg;

	private static ToolParent _toolParent;

	private HiResDate _startDate;
	private WorldLocation _origin;

	public CompositeTrackWrapper(HiResDate startDate, final WorldLocation centre)
	{
		super();

		_startDate = startDate;
		if (centre != null)
			_origin = new WorldLocation(centre);

		// we don't store a track-level color, just at leg level, so set it to null
		this.setColor(null);

		// give us a neater set of intervals
		this.setSymbolFrequency(new HiResDate(0,
				TimeFrequencyPropertyEditor._5_MINS));
		this.setLabelFrequency(new HiResDate(0,
				TimeFrequencyPropertyEditor._15_MINS));
	}

	
	
	@Override
	public Color getColor()
	{
		// TODO Auto-generated method stub
		return super.getColor();
	}



	@Override
	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			ComponentConstruct currentNearest, Layer parentLayer)
	{
		// initialise thisDist, since we're going to be over-writing it
		WorldDistance thisDist = new WorldDistance(0, WorldDistance.DEGS);

		Enumeration<Editable> numer = getSegments().elements();
		while (numer.hasMoreElements())
		{
			final PlanningSegment thisSeg = (PlanningSegment) numer.nextElement();
			if (thisSeg.getVisible())
			{
				// produce a location for the end
				FixWrapper endFix = (FixWrapper) thisSeg.last();
				if (endFix != null)
				{

					// how far away is it?
					thisDist = endFix.getLocation().rangeFrom(cursorLoc, thisDist);

					final WorldLocation fixLocation = new WorldLocation(
							endFix.getLocation())
					{
						private static final long serialVersionUID = 1L;

						@Override
						public void addToMe(WorldVector delta)
						{
							super.addToMe(delta);

							// so, what's the bearing back to the leg start?
							double newBearing = super.bearingFrom(thisSeg.first().getBounds()
									.getCentre());

							newBearing = MWC.Algorithms.Conversions.Rads2Degs(newBearing);

							// limit the bearing to the nearest 5 deg marker
							int m = ((int) newBearing / 10);
							newBearing = m * 10d;

							// trim it to being positive
							if (newBearing < 0)
								newBearing += 360;

							thisSeg.setCourse(newBearing);
						}
					};

					// try range
					currentNearest
							.checkMe(this, thisDist, null, parentLayer, fixLocation);
				}
			}
		}
	}

	@Override
	public void shift(WorldVector vector)
	{
		this.getOrigin().addToMe(vector);
		recalculate();
	}

	public HiResDate getStartDate()
	{
		return _startDate;
	}

	@FireExtended
	public void setStartDate(HiResDate startDate)
	{
		this._startDate = startDate;
		recalculate();
	}

	public WorldLocation getOrigin()
	{
		return _origin;
	}

	public void setOrigin(WorldLocation origin)
	{
		this._origin = origin;
		recalculate();
	}

	/**
	 * the editable details for this track
	 * 
	 * @return the details
	 */
	@Override
	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new CompositeTrackInfo(this);

		return _myEditor;
	}

	@Override
	protected void paintThisFix(CanvasType dest, WorldLocation lastLocation,
			FixWrapper fw)
	{
		// set the fix font-size to be my font-size
		fw.setFont(this.getTrackFont());

		// and now let it paint
		super.paintThisFix(dest, lastLocation, fw);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Enumeration<Editable> contiguousElements()
	{
		return this.getSegments().elements();
	}

	/**
	 * popup a dialog to add a new leg
	 * 
	 */
	public void addLeg()
	{
		if (_triggerNewLeg != null)
			_triggerNewLeg.createLegFor(this);
		else
		{
			if (_toolParent != null)
				_toolParent.logError(ToolParent.ERROR,
						"CompositeTrackWrapper does not have leg-trigger helper", null);
			else
				throw new RuntimeException(
						"CompositeTrackWrapper has not been configured in app start");
		}
	}

	/**
	 * popup a dialog to add a new leg
	 * 
	 */
	@FireExtended
	public void addClosingLeg()
	{
		this.add(new ClosingSegment("Closing segment", 45, new WorldSpeed(12,
				WorldSpeed.Kts), new WorldDistance(2, WorldDistance.NM), getColor()));
	}

	@FireExtended
	public void appendReverse()
	{
		// ok, get the legs
		SegmentList list = super.getSegments();
		ArrayList<PlanningSegment> holder = new ArrayList<PlanningSegment>();
		Enumeration<Editable> iterator = list.elements();
		while (iterator.hasMoreElements())
		{
			// put this element at the start
			holder.add(0, (PlanningSegment) iterator.nextElement());
		}

		// now run the legs back in reverse
		Iterator<PlanningSegment> iter2 = holder.iterator();
		while (iter2.hasNext())
		{
			PlanningSegment pl = iter2.next();

			try
			{
				PlanningSegment pl2 = (PlanningSegment) pl.clone();

				// now reverse it
				double newCourse = pl2.getCourse() + 180d;
				if (newCourse > 360)
					newCourse -= 360;
				pl2.setCourse(newCourse);

				// show the name as reversed
				pl2.setName(pl2.getName() + "(R)");

				// ok, now add it
				this.add(pl2);
			}
			catch (CloneNotSupportedException e)
			{
				e.printStackTrace();
			}

		}

		// ok, better throw in a recalculate
		this.recalculate();
	}

	@Override
	public void add(Editable point)
	{
		if (point instanceof PlanningSegment)
		{

			// hey, is this a closing segment?
			if (point instanceof ClosingSegment)
			{
				// do we already have one?
				if (this.getSegments().last() instanceof ClosingSegment)
				{
					// skip....
					_toolParent.logError(ToolParent.WARNING,
							"Already have closing segment", null);
				}
			}

			// take a copy of the name, to stop it getting manmgled
			String name = point.getName();

			super.add(point);

			if (point.getName() != name)
				((PlanningSegment) point).setName(name);
			
			// better do a recalc, aswell
			recalculate();
		}
		else
		{
			throw new RuntimeException(
					"can't add this type to a composite track wrapper");
		}
	}

	@Override
	public void addFix(FixWrapper theFix)
	{
		throw new RuntimeException("can't add a fix to this composite track");
	}

	@Override
	public void append(Layer other)
	{
		throw new RuntimeException(
				"can't add another layer to this composite track");
	}

	public void recalculate()
	{
		Enumeration<Editable> numer = getSegments().elements();
		WorldLocation thisOrigin = getOrigin();
		HiResDate thisDate = getStartDate();
		while (numer.hasMoreElements())
		{
			Editable editable = (Editable) numer.nextElement();
			PlanningSegment seg = (PlanningSegment) editable;

			PlanningCalc theCalc = null;
			int model = seg.getCalculation();
			switch (model)
			{
			case PlanningLegCalcModelPropertyEditor.RANGE_SPEED:
				theCalc = new FromRangeSpeed();
				break;
			case PlanningLegCalcModelPropertyEditor.RANGE_TIME:
				theCalc = new FromRangeTime();
				break;
			case PlanningLegCalcModelPropertyEditor.SPEED_TIME:
				theCalc = new FromSpeedTime();
				break;
			}

			// see if this is the closing segment
			if (seg instanceof ClosingSegment)
			{
				// what's the range and bearing back to the origin
				WorldVector offset = getOrigin().subtract(thisOrigin);

				// and store it.
				seg.setSpeedSilent(new WorldSpeed(12, WorldSpeed.Kts));
				seg.setDistanceSilent(new WorldDistance(offset.getRange(),
						WorldDistance.DEGS));
				seg.setCourseSilent(MWC.Algorithms.Conversions.Rads2Degs(offset
						.getBearing()));
				seg.setDepthSilent(new WorldDistance(offset.getDepth(),
						WorldDistance.METRES));

			}

			theCalc.construct(seg, thisOrigin, thisDate);

			// did we generate anything?
			if (seg.size() > 0)
			{
				// ok, now update the date/location
				thisOrigin = seg.last().getBounds().getCentre();
				thisDate = seg.endDTG();
			}
		}

		// ok, sort out the symbol & label freq
		HiResDate symFreq = this.getSymbolFrequency();
		HiResDate labelFreq = this.getLabelFrequency();

		this.setSymbolFrequency(new HiResDate(0));
		this.setLabelFrequency(new HiResDate(0));

		// and restore them
		setSymbolFrequency(symFreq);
		setLabelFrequency(labelFreq);

	}

	private abstract static class PlanningCalc
	{
		void construct(PlanningSegment seg, WorldLocation origin, HiResDate date)
		{
			// check we have some data
			if (date == null || origin == null)
				return;

			double distPerMinute = getMinuteDelta(seg);
			double timeTravelled = getSecsTravelled(seg);

			// ditch the existing items
			seg.removeAllElements();

			// ok build for this segment
			double courseDegs = seg.getCourse();
			double courseRads = MWC.Algorithms.Conversions.Degs2Rads(courseDegs);

			WorldVector vec = new WorldVector(courseRads, new WorldDistance(
					distPerMinute, WorldDistance.METRES), null);

			long timeMillis = date.getDate().getTime();
			for (long tNow = timeMillis; tNow <= timeMillis + timeTravelled * 1000; tNow += 60 * 1000)
			{
				HiResDate thisDtg = new HiResDate(tNow);

				// ok, do this fix
				Fix thisF = new Fix(thisDtg, origin, courseRads, seg.getSpeed()
						.getValueIn(WorldSpeed.ft_sec) / 3);

				// override the depth
				thisF.getLocation().setDepth(
						seg.getDepth().getValueIn(WorldDistance.METRES));

				FixWrapper fw = new FixWrapper(thisF);

				fw.setColor(seg.getColor());

				// and store it
				seg.add(fw);

				// reset the name, we're not going to use a human generated one
				fw.resetName();

				// produce a new position
				origin = origin.add(vec);
			}
		}

		protected abstract double getSecsTravelled(PlanningSegment seg);

		abstract double getMinuteDelta(PlanningSegment seg);
	}

	private static class FromRangeSpeed extends PlanningCalc
	{

		@Override
		double getMinuteDelta(PlanningSegment seg)
		{
			// find out how far it travels
			double distPerMinute = seg.getSpeed().getValueIn(WorldSpeed.M_sec) * 60d;
			return distPerMinute;
		}

		@Override
		protected double getSecsTravelled(PlanningSegment seg)
		{
			// how long does it take to travel this distance?
			double secsTaken = seg.getDistance().getValueIn(WorldDistance.METRES)
					/ seg.getSpeed().getValueIn(WorldSpeed.M_sec);

			// sort out the leg length
			seg.setDurationSilent(new Duration(secsTaken, Duration.SECONDS));

			return secsTaken;
		}
	}

	private static class FromRangeTime extends PlanningCalc
	{

		@Override
		double getMinuteDelta(PlanningSegment seg)
		{
			// home long to travel along it (secs)
			double travelSecs = seg.getDuration().getValueIn(Duration.SECONDS);
			double metresPerSec = seg.getDistance().getValueIn(WorldDistance.METRES)
					/ travelSecs;

			double metresPerMin = metresPerSec * 60d;

			// update the speed, so it makes sense in the fix
			WorldSpeed speedMtrs = new WorldSpeed(metresPerSec, WorldSpeed.M_sec);
			WorldSpeed speedKTs = new WorldSpeed(
					speedMtrs.getValueIn(WorldSpeed.Kts), WorldSpeed.Kts);
			seg.setSpeedSilent(speedKTs);

			return metresPerMin;
		}

		@Override
		protected double getSecsTravelled(PlanningSegment seg)
		{
			return seg.getDuration().getValueIn(Duration.SECONDS);
		}
	}

	private static class FromSpeedTime extends PlanningCalc
	{

		@Override
		protected double getSecsTravelled(PlanningSegment seg)
		{
			return seg.getDuration().getValueIn(Duration.SECONDS);
		}

		@Override
		double getMinuteDelta(PlanningSegment seg)
		{
			// how far will we travel in time?
			double metresPerSec = seg.getSpeed().getValueIn(WorldSpeed.M_sec);
			double metresPerMin = metresPerSec * 60d;

			double distanceM = metresPerSec * getSecsTravelled(seg);
			WorldDistance wd = new WorldDistance(distanceM, WorldDistance.METRES);
			seg.setDistanceSilent(new WorldDistance(wd.getValueIn(WorldDistance.NM),
					WorldDistance.NM));

			return metresPerMin;
		}
	}

	/**
	 * store helps that will aid us in creating a leg - it's an RCP thing, not a
	 * legacy thing
	 * 
	 * @param triggerNewLeg
	 */
	public static void setNewLegHelper(GiveMeALeg triggerNewLeg)
	{
		_triggerNewLeg = triggerNewLeg;
	}

	/**
	 * learn about the tool-parent that we're to use
	 * 
	 * @param toolParent
	 */
	public static void initialise(ToolParent toolParent)
	{
		_toolParent = toolParent;
	}

	/**
	 * indicate that planning segments have an order
	 * 
	 */
	@Override
	public boolean hasOrderedChildren()
	{
		return true;
	}

	@Override
	public Enumeration<Editable> elements()
	{
		/**
		 * just return the track segments, we don't contain any other data...
		 * 
		 */
		return _thePositions.elements();
	}

	@Override
	public Editable getSampleGriddable()
	{
		String name = "new leg";
		double courseDegs = 45d;
		WorldSpeed worldSpeed = new WorldSpeed(10, WorldSpeed.Kts);
		WorldDistance worldDistance = new WorldDistance(5, WorldDistance.MINUTES);
		return new PlanningSegment(name, courseDegs, worldSpeed, worldDistance,
				Color.RED);
	}

	@Override
	public TimeStampedDataItem makeCopy(TimeStampedDataItem item)
	{
		// make a copy
		PlanningSegment newSeg = new PlanningSegment((PlanningSegment) item);
		return newSeg;
	}

	@Override
	public boolean supportsAddRemove()
	{
		return false;
	}

	@Override
	public boolean requiresManualSave()
	{
		return false;
	}

	@Override
	public void doSave(String message)
	{
	}

	@Override
	public void setLayers(Layers parent)
	{
		// ok, we've been pasted. just double check that our children know who is
		// the boss
		Enumeration<Editable> numer = getSegments().elements();
		while (numer.hasMoreElements())
		{
			Editable editable = (Editable) numer.nextElement();
			PlanningSegment seg = (PlanningSegment) editable;
			seg.setWrapper(this);
		}
	}

}
