package com.planetmayo.debrief.satc_rcp.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.ATBForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution.FMeasurement;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution.ROrigin;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.support.SupportServices;
import com.thoughtworks.xstream.XStream;

public class XStreamIO
{
	public static final int CURRENT_VERSION = 1; 
	
	private static final XStream xstream;
	static 
	{
		xstream = new XStream();
		xstream.processAnnotations(TaskDescription.class);

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
		aliasFor(xstream, StraightLegForecastContribution.class);
		
		xstream.alias("bmeasurement", BMeasurement.class);
		xstream.alias("rorigin", ROrigin.class);
		xstream.alias("fmeasurement", FMeasurement.class);
		
		xstream.useAttributeFor(BMeasurement.class, "origin");
		xstream.useAttributeFor(BMeasurement.class, "bearingAngle");
		xstream.useAttributeFor(BMeasurement.class, "time");
		xstream.useAttributeFor(BMeasurement.class, "theRange");

		xstream.useAttributeFor(ROrigin.class, "origin");
		xstream.useAttributeFor(ROrigin.class, "time");

		xstream.useAttributeFor(FMeasurement.class, "origin");
		xstream.useAttributeFor(FMeasurement.class, "bearingAngle");
		xstream.useAttributeFor(FMeasurement.class, "origin");
		xstream.useAttributeFor(FMeasurement.class, "theRange");

		xstream.useAttributeFor(GeoPoint.class, "lat");
		xstream.useAttributeFor(GeoPoint.class, "lon");
	}	
	
	private static void aliasFor(XStream xstream, Class<?> klass) 
	{
		String simpleName = klass.getSimpleName();
		xstream.alias(Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1), klass);
	}
	
	public static XStreamWriter newWriter() 
	{
		return new XStreamWriter();
	}
	
	public static XStreamReader newReader(String fileName) 
	{
		return new XStreamReader(fileName);
	}	
	
	public static class XStreamWriter implements ISolver.Writer 
	{

		private final TaskDescription description = new TaskDescription(); 
		
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
		
		public void process(String fileName)
		{
			description.setVersion(CURRENT_VERSION);
			FileOutputStream outputStream = null;
			try
			{
				outputStream = new FileOutputStream(fileName);				
				xstream.toXML(description, new OutputStreamWriter(outputStream, "utf-8"));
			}
			catch (IOException ex) 
			{
				SupportServices.INSTANCE.getLog().error("Can't save file", ex);
			}
			finally 
			{
				IOUtils.closeQuietly(outputStream);
			}
		}
	}
	
	public static class XStreamReader implements ISolver.Reader 
	{
		private TaskDescription description;
		private boolean loaded;

		public XStreamReader(String fileName) 
		{
			FileInputStream inputStream = null;
			try 
			{
				inputStream = new FileInputStream(fileName);
				Object object = xstream.fromXML(new InputStreamReader(inputStream, "utf-8"));
				if (object instanceof TaskDescription) 
				{
					description = (TaskDescription) object;
					if (description.getVersion() != CURRENT_VERSION) 
					{
						SupportServices.INSTANCE.getLog().warn("Version of " + fileName + 
								" is " + description.getVersion() + ", but current version is " + CURRENT_VERSION);
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
				SupportServices.INSTANCE.getLog().error("Can't load task from xml", ex);
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
	
}
