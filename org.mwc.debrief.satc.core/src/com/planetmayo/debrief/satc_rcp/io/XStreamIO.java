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
package com.planetmayo.debrief.satc_rcp.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.ATBForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.HostState;
import com.planetmayo.debrief.satc.model.contributions.CompositeStraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.CoreMeasurementContribution.CoreMeasurement;
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurement;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution.FMeasurement;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.Range1959ForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution.ROrigin;
import com.planetmayo.debrief.satc.model.contributions.SpeedAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;

public class XStreamIO
{
	public static final int CURRENT_VERSION = 1;

	private static final XStream xstream;
	static
	{
		xstream = new XStream();
		xstream.registerConverter(new DebriefDateConverter(), XStream.PRIORITY_VERY_HIGH);
		xstream.processAnnotations(SolutionDescription.class);

		aliasFor(xstream, ATBForecastContribution.class);
		aliasFor(xstream, BearingMeasurementContribution.class);
		aliasFor(xstream, CourseAnalysisContribution.class);
		aliasFor(xstream, CourseForecastContribution.class);
		aliasFor(xstream, FrequencyMeasurementContribution.class);
		aliasFor(xstream, LocationAnalysisContribution.class);
		aliasFor(xstream, LocationForecastContribution.class);
		aliasFor(xstream, RangeForecastContribution.class);
		aliasFor(xstream, SpeedAnalysisContribution.class);
		aliasFor(xstream, SpeedForecastContribution.class);
		aliasFor(xstream, CompositeStraightLegForecastContribution.class);
		aliasFor(xstream, Range1959ForecastContribution.class);
		aliasFor(xstream, StraightLegForecastContribution.class);

		xstream.alias("bmeasurement", BMeasurement.class);
		xstream.alias("rorigin", ROrigin.class);
		xstream.alias("fmeasurement", FMeasurement.class);
		xstream.alias("freqMeasurement", FrequencyMeasurement.class);
		xstream.alias("hostState", HostState.class);

		xstream.useAttributeFor(FrequencyMeasurement.class, "freq");
		xstream.useAttributeFor(FrequencyMeasurement.class, "time");
		xstream.useAttributeFor(FrequencyMeasurement.class, "isActive");
		
		xstream.useAttributeFor(FrequencyMeasurementContribution.class, "baseFrequency");
		xstream.useAttributeFor(FrequencyMeasurementContribution.class, "soundSpeed");
		
		xstream.useAttributeFor(Range1959ForecastContribution.class, "fNought");
		xstream.useAttributeFor(Range1959ForecastContribution.class, "speedSound");
		xstream.useAttributeFor(Range1959ForecastContribution.class, "calculatedRange");
		xstream.useAttributeFor(Range1959ForecastContribution.class, "minRangeM");
		xstream.useAttributeFor(Range1959ForecastContribution.class, "maxRangeM");
		
		xstream.useAttributeFor(CoreMeasurement.class, "isActive");
		xstream.useAttributeFor(CoreMeasurement.class, "time");

		xstream.useAttributeFor(ROrigin.class, "origin");
		xstream.useAttributeFor(ROrigin.class, "time");

		xstream.useAttributeFor(BMeasurement.class, "origin");
		xstream.useAttributeFor(BMeasurement.class, "bearingAngle");
		xstream.useAttributeFor(BMeasurement.class, "range");

		xstream.useAttributeFor(HostState.class, "time");
		xstream.useAttributeFor(HostState.class, "courseDegs");
		xstream.useAttributeFor(HostState.class, "speedKts");

		xstream.useAttributeFor(FMeasurement.class, "frequency");
		xstream.useAttributeFor(FMeasurement.class, "osCourse");
		xstream.useAttributeFor(FMeasurement.class, "osSpeed");

		xstream.useAttributeFor(GeoPoint.class, "lat");
		xstream.useAttributeFor(GeoPoint.class, "lon");		
	}

	private static void aliasFor(XStream xstream, Class<?> klass)
	{
		String simpleName = klass.getSimpleName();
		xstream.alias(
				Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1),
				klass);
	}

	public static XStreamWriter newWriter()
	{
		return new XStreamWriter();
	}

	public static XStreamReader newReader(InputStream inputStream, String filename)
	{
		return new XStreamReader(inputStream, filename);
	}

	public static class XStreamWriter implements ISolver.Writer
	{

		private final SolutionDescription description = new SolutionDescription();

		@Override
		public void writeContributions(List<BaseContribution> contributions)
		{
			description.setContributions(contributions);
		}

		@Override
		public void writeVehicleType(VehicleType vehicleType)
		{
			description.setVehicleType(vehicleType);
		}

		@Override
		public void writePrecision(Precision precision)
		{
			description.setPrecision(precision);
		}

		public void process(OutputStream outputStream)
		{
			description.setVersion(CURRENT_VERSION);
			try
			{
				xstream.toXML(description,
						new OutputStreamWriter(outputStream, "utf-8"));
			}
			catch (IOException ex)
			{
				LogFactory.getLog().error("Can't save file", ex);
			}
			finally
			{
				IOUtils.closeQuietly(outputStream);
			}
		}
	}

	public static class XStreamReader implements ISolver.Reader
	{
		private SolutionDescription description;
		private boolean loaded;

		public XStreamReader(InputStream inputStream, String fileName)
		{
			try
			{
				Object object = xstream.fromXML(new InputStreamReader(inputStream,
						"utf-8"));
				if (object instanceof SolutionDescription)
				{
					description = (SolutionDescription) object;
					if (description.getVersion() != CURRENT_VERSION)
					{
						LogFactory.getLog().warn(
								"Version of " + fileName + " is " + description.getVersion()
										+ ", but current version is " + CURRENT_VERSION);
					}
					loaded = true;
				}
				else
				{
					throw new IOException();
				}

			}
			catch (IOException ex)
			{
				description = null;
				LogFactory.getLog().error("Can't load SATC Solution from xml", ex);
			}
			finally
			{
				IOUtils.closeQuietly(inputStream);
			}
		}

		public boolean isLoaded()
		{
			return loaded;
		}

		@Override
		public List<BaseContribution> readContributions()
		{
			return description.getContributions();
		}

		@Override
		public VehicleType readVehicleType()
		{
			return description.getVehicleType();
		}

		@Override
		public Precision readPrecision()
		{
			return description.getPrecision();
		}

	}

	private static class DebriefDateConverter extends DateConverter {

		@Override
		public Object fromString(String str)
		{
			Date date = (Date) super.fromString(str);
			return new Date(date.getTime() - TimeZone.getDefault().getRawOffset());
		}

		@Override
		public String toString(Object obj)
		{
			Date date = new Date(((Date)obj).getTime() + TimeZone.getDefault().getRawOffset());
			return super.toString(date);
		}
		
		
	}
}
