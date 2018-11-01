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
package Debrief.Wrappers.Track;

import java.awt.Color;
import java.awt.Font;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.CreateEditorForParent;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.Griddable;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Canvas.CanvasTypeUtilities;
import MWC.GUI.Properties.CardinalPointsPropertyEditor;
import MWC.GUI.Properties.PlanningLegCalcModelPropertyEditor;
import MWC.GUI.Shapes.TextLabel;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.TextFormatting.GeneralFormat;

public class PlanningSegment extends TrackSegment implements Cloneable,
		CreateEditorForParent, TimeStampedDataItem, ICompositeTrackSegment
{

	/**
	 * special case that gives us a leg that goes back to the start
	 * 
	 * @author ian
	 * 
	 */
	public static class ClosingSegment extends PlanningSegment implements ICompositeTrackSegment
	{

		public ClosingSegment(final String name, final double courseDegs,
				final WorldSpeed worldSpeed, final WorldDistance worldDistance)
		{
			super(name, courseDegs, worldSpeed, worldDistance);
			this.setCalculation(PlanningLegCalcModelPropertyEditor.RANGE_SPEED);
		}

		@Override
		public int getLineStyle()
		{
			return CanvasType.DOTTED;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}

	/**
	 * class containing editable details of a track
	 */
	public class PlanningSegmentInfo extends Griddable
	{

		/**
		 * constructor for this editor, takes the actual track as a parameter
		 * 
		 * @param data
		 *          track being edited
		 */
		public PlanningSegmentInfo(final TrackSegment data)
		{
			super(data, data.getName(), "");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						expertProp("Calculation", "How to calculate the leg length",
								SPATIAL),
						expertProp("Visible", "whether this layer is visible", FORMAT),
						displayExpertProp("VectorLabelVisible", "Vector label visible",
								"whether this vector label is visible", FORMAT),
						expertProp("Depth", "The depth for this leg", SPATIAL),
						expertProp("Course", "The course for this leg", SPATIAL),
						expertProp("Distance", "The distance travelled along this leg",
								SPATIAL),
						expertProp("Speed", "The speed travelled along this leg", SPATIAL),
						expertProp("Duration", "The duration of travel along this leg",
								SPATIAL),
						expertProp("Name", "Name of this track segment", FORMAT), };

				res[0].setPropertyEditorClass(PlanningLegCalcModelPropertyEditor.class);
				res[4].setPropertyEditorClass(CardinalPointsPropertyEditor.class);

				return res;
			}
			catch (final IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}

		@Override
		public PropertyDescriptor[] getGriddablePropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						prop("Name", "the name for this leg", FORMAT),
						prop("Course", "the course for this leg", SPATIAL),
						prop("Speed", "the speed at which to travel on this leg", SPATIAL),
						prop("Distance", "how long this leg is", SPATIAL),
						prop("Duration", "how long the vessel travels on this leg",
								TEMPORAL),
						prop("Depth", "depth to travel at on this leg", SPATIAL),

				};
				return res;

			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

		@Override
		public NonBeanPropertyDescriptor[] getNonBeanGriddableDescriptors()
		{
			return null;
		}
	}

	private transient CompositeTrackWrapper _parent;
	
	private List<FixWrapper> _cachedFixes = new ArrayList<FixWrapper>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * how far we represent (optional)
	 * 
	 */
	WorldDistance _myLength;

	/**
	 * the course to follow
	 * 
	 */
	double _myCourseDegs;

	/**
	 * the speed to transit at (optional)
	 * 
	 */
	WorldSpeed _mySpeed;

	/**
	 * the date this segment was created - used to force sort order by the order
	 * they were read in
	 * 
	 */
	private long _created = System.nanoTime();

	/**
	 * how far to travel for (optional)
	 * 
	 */
	private Duration _myPeriod = new Duration(12, Duration.MINUTES);

	/**
	 * which calculation model to use
	 * 
	 */
	int _calcModel;

	/**
	 * the depth for this leg
	 * 
	 */
	private WorldDistance _myDepth = new WorldDistance(0, WorldDistance.METRES);

	/**
	 * whether this vector label is visible default: true
	 * 
	 */
	private boolean _myVectorLabelVisible = true;

	/**
	 * copy constructor
	 * 
	 * @param other
	 */
	public PlanningSegment(final PlanningSegment other)
	{
	  // make it absolute, which planning semgntns are
	  super(TrackSegment.ABSOLUTE);
	  
		_calcModel = other._calcModel;
		_created = System.nanoTime();
		_myCourseDegs = other._myCourseDegs;
		_myDepth = new WorldDistance(other._myDepth);
		_myLength = new WorldDistance(other._myLength);
		_myPeriod = new Duration(other._myPeriod);
		_mySpeed = new WorldSpeed(other._mySpeed);
		_parent = other._parent;
		this.setName(other.getName());
	}

	public PlanningSegment(final String name, final double courseDegs,
			final WorldSpeed worldSpeed, final WorldDistance worldDistance)
	{
	   // make it absolute, which planning semgntns are
    super(TrackSegment.ABSOLUTE);
	  
		this.setName(name);
		this.setCourse(courseDegs);
		this.setSpeedSilent(worldSpeed);
		this.setDistanceSilent(worldDistance);
		this.recalc();
	}

	public WorldDistance getDepth()
	{
		return _myDepth;
	}

	public void setDepth(final WorldDistance depth)
	{
		_myDepth = depth;
		recalc();
	}

	public void setDepthSilent(final WorldDistance depth)
	{
		_myDepth = depth;
	}

	/**
	 * special add-fix, so we don't bother with rename
	 * 
	 */
	public void addFix(final FixWrapper fix)
	{
	  FixWrapper matched = null;
	  
	  // loop through the cached fixes
	  for(final FixWrapper cachedFix: _cachedFixes)
	  {
      if(cachedFix.getTime().equals(fix.getTime()))
      {
        matched = cachedFix;
        fix.setLabelLocation(matched.getLabelLocation());
        fix.setLabelShowing(matched.getLabelShowing());
        fix.setArrowShowing(matched.getArrowShowing());
        fix.setLabel(matched.getLabel());
        fix.setSymbolShowing(matched.getSymbolShowing());
        fix.setFont(matched.getFont());
        fix.setVisible(matched.getVisible());
    
        // only set the color if this specific fix has one
        Color color = matched.getActualColor();
        if(color != null)
        {
          fix.setColor(color);
        }
        
        break;
      }
    }
	  
	  if(matched != null)
	  {
	    _cachedFixes.remove(matched);
	  }
	  else
	  {
	    // ok, do some default formatting
	    fix.setSymbolShowing(true);
	    fix.resetLabelLocation();
	  }
	  
		// remember the fix
		this.addFixSilent(fix);
	}

	@Override
	public int compareTo(final Plottable arg0)
	{
		int res = 1;
		if (arg0 instanceof ClosingSegment)
		{
			// the closing semgent will always come after
			res = -1;
		}
		else if (arg0 instanceof PlanningSegment)
		{
			final PlanningSegment other = (PlanningSegment) arg0;
			final Long myTime = _created;
			final Long hisTime = other._created;
			res = myTime.compareTo(hisTime);
		}
		return res;
	}

	@Override
	public double rangeFrom(final WorldLocation other)
	{
		double firstRange = Plottable.INVALID_RANGE;

		final Enumeration<Editable> numer = this.elements();
		while (numer.hasMoreElements())
		{
			final Editable editable = (Editable) numer.nextElement();
			final FixWrapper fw = (FixWrapper) editable;
			final double thisR = fw.rangeFrom(other);
			if (firstRange == Plottable.INVALID_RANGE)
				firstRange = thisR;
			else
				firstRange = Math.min(firstRange, thisR);
		}
		return firstRange;
	}

	public int getCalculation()
	{
		return _calcModel;
	}

	@FireExtended
	public void setCalculation(final int calculation)
	{
		_calcModel = calculation;
	}

	@FireExtended
	public void setCalculation(final Integer calculation)
	{
		_calcModel = calculation;
	}

	public WorldDistance getDistance()
	{
		return _myLength;
	}

	@FireExtended
	public void setDistance(final WorldDistance length)
	{
		this._myLength = length;
		recalc();
	}

	public double getCourse()
	{
		// trim it to +ve domain
		double res = _myCourseDegs;
		if(res < 0)
			res += 360;
		
		return res;
	}

	@FireExtended
	public void setCourse(final double courseDegs)
	{
		this._myCourseDegs = courseDegs;
		recalc();
	}

	public void setCourseSilent(final double courseDegs)
	{
		this._myCourseDegs = courseDegs;
	}

	public WorldSpeed getSpeed()
	{
		return _mySpeed;
	}

	@FireExtended
	public void setSpeed(final WorldSpeed speed)
	{
		this._mySpeed = speed;
		recalc();
	}

	public Duration getDuration()
	{
		return _myPeriod;
	}

	@FireExtended
	public void setDuration(final Duration period)
	{
		this._myPeriod = period;
		recalc();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		final PlanningSegment res = new PlanningSegment(this);

		return res;
	}

	@Override
	public Editable.EditorType getInfo()
	{
		return new PlanningSegmentInfo(this);
	}

	/**
	 * does this item have an editor?
	 */
	public boolean hasEditor()
	{
		return true;
	}

  private void recalc()
  {
    if (_parent != null)
    {
      _parent.recalculate();

      // also, reset all of the label locations for this leg, since we want
      // them to indicate direction of travel
      Enumeration<Editable> numer = this.elements();
      while (numer.hasMoreElements())
      {
        FixWrapper thisF = (FixWrapper) numer.nextElement();
        thisF.resetLabelLocation();
      }
    }
  }

	@Override
	public void sortOutDateLabel(final HiResDate startDTG)
	{
		// ignore - we want to keep the layer name
	}

	@Override
	public void setWrapper(final TrackWrapper wrapper)
	{
		// store the parent
		super.setWrapper(wrapper);

		// and store the helper-outer
		_parent = (CompositeTrackWrapper) wrapper;

	}

	public void setSpeedSilent(final WorldSpeed worldSpeed)
	{
		_mySpeed = worldSpeed;
		// don't bother triggering recalc
	}

	public void setDistanceSilent(final WorldDistance worldDistance)
	{
		_myLength = worldDistance;
		// don't bother triggering recalc
	}

	public void setDurationSilent(final Duration duration)
	{
		_myPeriod = duration;
		// don't bother triggering recalc
	}

	public PlanningSegment createCopy()
	{
		final PlanningSegment res = new PlanningSegment(this);
		res._calcModel = _calcModel;
		res._myCourseDegs = _myCourseDegs;
		res._myDepth = _myDepth;
		res._myLength = _myLength;
		res._myPeriod = _myPeriod;
		res._mySpeed = _mySpeed;
		res._parent = _parent;

		return res;
	}

	@Override
	public Editable getParent()
	{
		return getWrapper();
	}

	@Override
	public HiResDate getDTG()
	{
		return this.startDTG();
	}

	@Override
	public void setDTG(final HiResDate date)
	{
		// ingore, we don't set the DTG for a planning segment
		System.err.println("Should not set DTG for planning segment");
	}

	public boolean getVectorLabelVisible()
	{
		return _myVectorLabelVisible;
	}

	public void setVectorLabelVisible(boolean vectorLabelVisible)
	{
		this._myVectorLabelVisible = vectorLabelVisible;
	}

	public String getVectorLabel()
	{

		StringBuilder builder = new StringBuilder();
		if (getDistance() != null)
		{
			builder.append(GeneralFormat.formatOneDecimalPlace(getDistance()
					.getValue()));
			builder.append(getDistance().getUnitsLabel());
			builder.append(" ");
		}
		builder.append((int) getCourse());
		builder.append("\u00b0");
		return builder.toString();
	}

	public void paintLabel(final CanvasType dest)
	{
		if (getVectorLabelVisible())
		{
			String textLabel = getVectorLabel();
			if (first() instanceof FixWrapper && last() instanceof FixWrapper)
			{
				FixWrapper first = (FixWrapper) first();
				FixWrapper last = (FixWrapper) last();
				Font f = first.getFont();
				Color c = first.getColor();
				WorldLocation firstLoc = first.getLocation();
				WorldLocation lastLoc = last.getLocation();

				// ok, now plot it
				CanvasTypeUtilities.drawLabelOnLine(dest, textLabel, f, c, firstLoc,
						lastLoc, 2.0, true);
				textLabel = getName().replace(TextLabel.NEWLINE_MARKER, " ");
				CanvasTypeUtilities.drawLabelOnLine(dest, textLabel, f, c, firstLoc,
						lastLoc, 2.0, false);
				
			}
		}
	}

  public void cacheExistingElements()
  {
    Enumeration<Editable> iter = super.elements();
    while (iter.hasMoreElements())
    {
      FixWrapper thisF = (FixWrapper) iter.nextElement();
      _cachedFixes.add(thisF);
    }
  }

  public void clearCachedFixes()
  {
    _cachedFixes.clear();
  }

  @Override
  public Color getColor()
  {
    return _parent.getTrackColor();
  }

}
