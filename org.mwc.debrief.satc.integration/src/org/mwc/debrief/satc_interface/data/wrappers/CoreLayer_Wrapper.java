package org.mwc.debrief.satc_interface.data.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.core.runtime.Status;
import org.mwc.debrief.satc_interface.data.wrappers.BMC_Wrapper.BearingMeasurementEditable;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables.IteratorWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CoreMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc.model.contributions.CoreMeasurementContribution.CoreMeasurement;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

abstract public class CoreLayer_Wrapper<Contribution extends CoreMeasurementContribution<?>, 
  Measurement extends CoreMeasurementContribution.CoreMeasurement,
  Wrapper extends CoreLayer_Wrapper.CoreMeasurementEditable> extends ContributionWrapper implements Layer
{
	protected Collection<Editable> _myElements;


	@SuppressWarnings("rawtypes")
	private CoreMeasurementContribution getBMC()
	{
		return (CoreMeasurementContribution) super.getContribution();
	}

	@Override
	public Enumeration<Editable> elements()
	{
		if (_myElements == null)
		{
			// wrap the measurements
			_myElements = new ArrayList<Editable>();

			CoreMeasurementContribution bmc = (Contribution) getBMC();
			ArrayList<Measurement> meas = bmc.getMeasurements();
			Iterator<Measurement> iter = meas.iterator();
			while (iter.hasNext())
			{
				CoreMeasurement thisM = (CoreMeasurement) iter
						.next();
				Wrapper thisMe = new Wrapper(thisM);
				_myElements.add(thisMe);
			}
		}

		return new IteratorWrapper(_myElements.iterator());
	}

	public int size()
	{
		return getBMC().getNumObservations();
	}

	abstract public class CoreMeasurementEditable implements Plottable, ExcludeFromRightClickEdit
	{

		protected final CoreMeasurement _myMeas;

		public CoreMeasurementEditable(CoreMeasurement measurement)
		{
			_myMeas = measurement;
		}

		@Override
		public int compareTo(Plottable arg0)
		{
			return 0;
		}

		public Boolean getActive()
		{
			return _myMeas.isActive();
		}

		@Override
		public WorldArea getBounds()
		{
			return null;
		}

		@Override
		public String getName()
		{
			return FormatRNDateTime.toString(_myMeas.getDate().getTime());
		}

		@Override
		public boolean getVisible()
		{
			return getActive();
		}

		@Override
		public boolean hasEditor()
		{
			return true;
		}

		@Override
		public void paint(CanvasType dest)
		{
		
		}

		@Override
		public double rangeFrom(WorldLocation other)
		{
			return INVALID_RANGE;
		}

		public void setActive(Boolean active)
		{
			_myMeas.setActive(active);
		
			// fire hard constraints changed
			getContribution().fireHardConstraintsChange();
		}

		@Override
		public void setVisible(boolean val)
		{
			setActive(val);
		}

		@Override
		public String toString()
		{
			return getName();
		}
		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CoreLayer_Wrapper(BaseContribution contribution)
	{
		super(contribution);
	}


	@Override
	final public void exportShape()
	{
	}

	@Override
	final public void append(Layer other)
	{
	}

	@Override
	final public void setName(String val)
	{
		super.getContribution().setName(val);
	}
	

	@Override
	final public boolean hasEditor()
	{
		return true;
	}

	@Override
	final public boolean hasOrderedChildren()
	{
		return true;
	}

	@Override
	final public int getLineThickness()
	{
		return 0;
	}

	@Override
	final public void add(Editable point)
	{
		SATC_Activator.log(Status.ERROR,
				"Should not be adding items to this layer", null);
	}

	@Override
	final public void removeElement(Editable point)
	{

	}
}
