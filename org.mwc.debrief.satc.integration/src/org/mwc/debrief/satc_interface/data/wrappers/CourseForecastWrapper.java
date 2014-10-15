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

import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;

public class CourseForecastWrapper extends ContributionWrapper
{

	public static class CourseInfo extends EditorType
	{

		public CourseInfo(CourseForecastWrapper data)
		{
			super(data, "Course Forecast", "CourseForecast");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{ prop("Name", "the Name of this leg", FORMAT),
						prop("_Start", "the start date of this leg", FORMAT),
						prop("End", "the finish date of this leg", FORMAT),
						prop("MinCourse", "the minimum course", SPATIAL),
						prop("MaxCourse", "the maximum course", SPATIAL)
						
				};

				return res;
			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

	}

	private final CourseForecastContribution _courseF;

	public CourseForecastWrapper(CourseForecastContribution contribution)
	{
		super(contribution);
		_courseF = contribution;
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
			_myEditor = new CourseInfo(this);

		return _myEditor;
	}
	
	
	public double getMinCourse()
	{
		return (int) Math.toDegrees(_courseF.getMinCourse());
	}

	public void setMinCourse(double minCourse)
	{
		_courseF.setMinCourse(Math.toRadians(minCourse));
	}

	public double getMaxCourse()
	{
		return (int) Math.toDegrees(_courseF.getMaxCourse());
	}

	public void setMaxCourse(double maxCourse)
	{
		_courseF.setMaxCourse(Math.toRadians(maxCourse));
	}

}
