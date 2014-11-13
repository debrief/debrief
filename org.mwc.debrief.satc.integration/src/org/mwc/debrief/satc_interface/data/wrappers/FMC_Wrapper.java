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
package org.mwc.debrief.satc_interface.data.wrappers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import MWC.GUI.Editable;
import MWC.GUI.Plottables.IteratorWrapper;
import MWC.GUI.Properties.BoundedInteger;

import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc.model.contributions.CoreMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CoreMeasurementContribution.CoreMeasurement;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution.FMeasurement;

public class FMC_Wrapper extends CoreLayer_Wrapper<FrequencyMeasurementContribution>
{
	
	public class BMC_Info extends Editable.EditorType implements Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public BMC_Info(FMC_Wrapper data)
		{
			super(data, data.getName(), "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res =
				{ 
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

	public FMC_Wrapper(FrequencyMeasurementContribution contribution)
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

			FrequencyMeasurementContribution bmc = (FrequencyMeasurementContribution) getBMC();
			ArrayList<FMeasurement> meas = bmc.getMeasurements();
			Iterator<FMeasurement> iter = meas.iterator();
			while (iter.hasNext())
			{
				CoreMeasurement thisM = (CoreMeasurement) iter
						.next();
				FrequencyMeasurementEditable thisMe = new FrequencyMeasurementEditable(thisM);
				_myElements.add(thisMe);
			}
		}

		return new IteratorWrapper(_myElements.iterator());
	}

	@SuppressWarnings("rawtypes")
	public class FrequencyMeasurementEditable extends CoreLayer_Wrapper.CoreMeasurementEditable
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

			public Meas_Info(FrequencyMeasurementEditable data)
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


		@Override
		public EditorType getInfo()
		{
			if (_myEditor == null)
				_myEditor = new Meas_Info(this);
			return _myEditor;
		}
		
		private EditorType _myEditor;

		public FrequencyMeasurementEditable(CoreMeasurement measurement)
		{
			super(measurement);
		}
	}

	public int size()
	{
		return getBMC().getNumObservations();
	}

	@SuppressWarnings("rawtypes")
	private CoreMeasurementContribution getBMC()
	{
		return (CoreMeasurementContribution) super.getContribution();
	}

	@Override
	public EditorType getInfo()
	{
		if(_myEditor == null)
			_myEditor = new BMC_Info(this);
		return _myEditor;
	}
}