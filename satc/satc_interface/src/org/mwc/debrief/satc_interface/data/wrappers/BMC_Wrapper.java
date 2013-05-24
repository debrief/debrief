package org.mwc.debrief.satc_interface.data.wrappers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.core.runtime.Status;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables.IteratorWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class BMC_Wrapper extends ContributionWrapper implements Layer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Collection<Editable> _myElements;

	public BMC_Wrapper(BearingMeasurementContribution contribution)
	{
		super(contribution);
	}

	@Override
	public Enumeration<Editable> elements()
	{
		if (_myElements == null)
		{
			// wrap the measurements
			_myElements = new ArrayList<Editable>();

			BearingMeasurementContribution bmc = getBMC();
			ArrayList<BMeasurement> meas = bmc.getMeasurements();
			Iterator<BMeasurement> iter = meas.iterator();
			while (iter.hasNext())
			{
				BearingMeasurementContribution.BMeasurement thisM = (BearingMeasurementContribution.BMeasurement) iter
						.next();
				BMC_Wrapper.MeasurementEditable thisMe = new MeasurementEditable(thisM);
				_myElements.add(thisMe);
			}
		}

		return new IteratorWrapper(_myElements.iterator());
	}

	protected class MeasurementEditable implements Plottable
	{
		// ///////////////////////////////////////////////////////////
		// info class
		// //////////////////////////////////////////////////////////
		public class Meas_Info extends Editable.EditorType implements Serializable
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Meas_Info(MeasurementEditable data)
			{
				super(data, data.getName(), "");
			}

			public PropertyDescriptor[] getPropertyDescriptors()
			{
				try
				{
					PropertyDescriptor[] res =
					{ prop("Active", "whether to use this bearing", VISIBILITY) };

					return res;
				}
				catch (IntrospectionException e)
				{
					return super.getPropertyDescriptors();
				}
			}
		}

		private final BMeasurement _myMeas;
		private EditorType _myEditor;

		public MeasurementEditable(BMeasurement measurement)
		{
			_myMeas = measurement;
		}

		public Boolean getActive()
		{
			return _myMeas.isActive();
		}

		public void setActive(Boolean active)
		{
			_myMeas.setActive(active);

			// fire hard constraints changed
			getContribution().fireHardConstraintsChange();
		}

		@Override
		public String getName()
		{
			return FormatRNDateTime.toString(_myMeas.getDate().getTime());
		}

		@Override
		public String toString()
		{
			return getName();
		}

		@Override
		public boolean hasEditor()
		{
			return true;
		}

		@Override
		public EditorType getInfo()
		{
			if (_myEditor == null)
				_myEditor = new Meas_Info(this);
			return _myEditor;
		}

		@Override
		public int compareTo(Plottable arg0)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void paint(CanvasType dest)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public WorldArea getBounds()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean getVisible()
		{
			return getActive();
		}

		@Override
		public void setVisible(boolean val)
		{
			setActive(val);
		}

		@Override
		public double rangeFrom(WorldLocation other)
		{
			return INVALID_RANGE;
		}
	}

	public int size()
	{
		return getBMC().getNumObservations();
	}

	private BearingMeasurementContribution getBMC()
	{
		return (BearingMeasurementContribution) super.getContribution();
	}

	@Override
	public void exportShape()
	{
	}

	@Override
	public void append(Layer other)
	{
	}

	@Override
	public void setName(String val)
	{
	}

	@Override
	public boolean hasOrderedChildren()
	{
		return true;
	}

	@Override
	public int getLineThickness()
	{
		return 0;
	}

	@Override
	public void add(Editable point)
	{
		SATC_Activator.log(Status.ERROR,
				"Should not be adding items to this layer", null);
	}

	@Override
	public void removeElement(Editable point)
	{
		// TODO Auto-generated method stub

	}

}