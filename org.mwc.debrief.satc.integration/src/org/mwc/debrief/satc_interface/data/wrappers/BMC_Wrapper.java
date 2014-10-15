/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables.IteratorWrapper;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class BMC_Wrapper extends ContributionWrapper implements Layer
{
	
	public class BMC_Info extends Editable.EditorType implements Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public BMC_Info(BMC_Wrapper data)
		{
			super(data, data.getName(), "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res =
				{ 
						prop("Error", "the size of bearing error to allow", EditorType.SPATIAL),
						prop("Name", "name of this contribution", EditorType.FORMAT)
						
				};

				return res;
			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}


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

	public class MeasurementEditable implements Plottable, ExcludeFromRightClickEdit
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
					{ prop("Active", "whether to use this bearing", EditorType.OPTIONAL) };

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
			return 0;
		}

		@Override
		public void paint(CanvasType dest)
		{

		}

		@Override
		public WorldArea getBounds()
		{
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
	public boolean hasEditor()
	{
		return true;
	}

	@Override
	public EditorType getInfo()
	{
		if(_myEditor == null)
			_myEditor = new BMC_Info(this);
		return _myEditor;
	}
	
	public BoundedInteger getError()
	{
		BearingMeasurementContribution bm = (BearingMeasurementContribution) super.getContribution();
		return  new BoundedInteger( (int) Math.toDegrees(bm.getBearingError()),1,20);
	}

	public void setError(BoundedInteger error)
	{
		BearingMeasurementContribution bm = (BearingMeasurementContribution) super.getContribution();
		bm.setBearingError(Math.toRadians(error.getCurrent()));
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
		super.getContribution().setName(val);
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

	}

}