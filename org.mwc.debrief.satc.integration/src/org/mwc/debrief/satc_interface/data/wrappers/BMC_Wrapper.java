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

import MWC.GUI.Editable;
import MWC.GUI.Properties.BoundedInteger;

import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc.model.contributions.CoreMeasurementContribution.CoreMeasurement;

public class BMC_Wrapper extends CoreLayer_Wrapper<BearingMeasurementContribution, 
   BMeasurement, BMC_Wrapper.BearingMeasurementEditable>
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
	public BMC_Wrapper(BearingMeasurementContribution contribution)
	{
		super(contribution);
	}


	@SuppressWarnings("rawtypes")
	public class BearingMeasurementEditable extends CoreLayer_Wrapper.CoreMeasurementEditable
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

			public Meas_Info(BearingMeasurementEditable data)
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

		public BearingMeasurementEditable(CoreMeasurement measurement)
		{
			super(measurement);
		}
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


}