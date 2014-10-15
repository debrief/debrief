/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.interfaces;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.CorePlugin;

import Debrief.ReaderWriter.FlatFile.FlatFileExporter;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;

public abstract class TimeControllerOperation
{
	/**
	 * convenience class to store these items
	 * 
	 * @author ianmayo
	 * 
	 */
	public static class TimeControllerOperationStore extends
			Vector<TimeControllerOperation>
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}

	/**
	 * the operation we're conducting
	 * 
	 */
	private final String _operationName;

	/**
	 * whether we need sensor data
	 * 
	 */
	private final boolean _requireSensor;

	/**
	 * whether there should just be a single sensor on the primary track
	 * 
	 */
	private final boolean _singleSensor;

	/**
	 * whether there should just be two sensors on the primary track
	 * 
	 */
	@SuppressWarnings("unused")
	private final boolean _doubleSensor;

	
	/** 
	 * whether there must be visible sensor data visible in the specified time period
	 */
	private final boolean _requiresSensorData;

	/**
	 * main constructor. we always want an operation name, so let the child
	 * classes call this with a name
	 * 
	 * @param operationName
	 *          what we put on the menu and the dialog
	 */
	protected TimeControllerOperation(final String operationName,
			final boolean requireSensor, final boolean singleSensor, final boolean doubleSensor,  final boolean  requiresSensorData)
	{
		_operationName = operationName;
		_requireSensor = requireSensor;
		_singleSensor = singleSensor;
		_doubleSensor = doubleSensor;
		_requiresSensorData = requiresSensorData;
	}

	/**
	 * the image to show for this action
	 * 
	 * @return the descriptor to show
	 */
	abstract public ImageDescriptor getDescriptor();

	public String getName()
	{
		return _operationName;
	}

	/**
	 * report a messge
	 * 
	 * @param msg
	 *          the message
	 */
	protected void showMessage(final String msg)
	{
		CorePlugin.showMessage(_operationName, msg);
	}

	public void run(final WatchableList primary,
			final WatchableList[] secondaries, final TimePeriod period)
	{
		// check we have data
		if (primary == null)
		{
			showMessage("Sorry, a primary selection must be selected");
			return;
		}

		if (!(primary instanceof TrackWrapper))
		{
			showMessage("Sorry, primary selection must be a Track");
			return;
		}

		final TrackWrapper primaryTrack = (TrackWrapper) primary;

		if (_requireSensor)
		{

			final Enumeration<Editable> enumer = primaryTrack.getSensors().elements();
			if (enumer == null)
			{
				showMessage("Sorry, primary selection must be have sensor data");
				return;
			}

			int count = 0;
			while (enumer.hasMoreElements())
			{
				count++;
				enumer.nextElement();
			}

			if (count == 0)
			{
				showMessage("Sorry, primary selection must be have sensor data");
				return;
			}

		}

		if ((secondaries == null) || (secondaries.length == 0))
		{
			showMessage("Sorry, there must be a secondary selection");
			return;
		}

		if (secondaries.length > 1)
		{
			showMessage("Sorry, there must only be one secondary selection");
			return;
		}

		if (!(secondaries[0] instanceof TrackWrapper))
		{
			showMessage("Sorry, secondary selection must be a Track");
			return;
		}

		if (period == null)
		{
			showMessage("Sorry, there must be a time period selected");
			return;
		}

		if (_singleSensor)
		{
			// right, check there's one visible sensor
			final SensorWrapper theSensor = FlatFileExporter.getSubjectSensor((TrackWrapper) primaryTrack);

			if (theSensor == null)
			{
				showMessage("Sorry, there must be only one sensor visible on the primary track");
				return;
			}
		}
		
		if(_requiresSensorData)
		{
			// loop through the sensors
			// loop through collecting cuts from visible sensors
			final Enumeration<Editable> sensors = primaryTrack.getSensors().elements();
			final Collection <Editable> theCuts = new Vector<Editable>();
			while (sensors.hasMoreElements())
			{
				final SensorWrapper thisS = (SensorWrapper) sensors.nextElement();
				if (thisS.getVisible())
				{
					final Collection<Editable> theseCuts = thisS.getItemsBetween(period.getStartDTG(), period.getEndDTG());
					if(theseCuts != null)
					theCuts.addAll(theseCuts);
				}
			}

			if(theCuts.size() == 0)
			{
				showMessage("Sorry, there must be some visible sensor data in the specified time period");
				return;
			}
		}

		if (period.getEndDTG().getMicros() - period.getStartDTG().getMicros() < 1000000)
		{
			showMessage("Sorry, there must be at least a second of data");
			return;
		}

		// well, here we are. May as well go for it!
		executeExport(primaryTrack, secondaries, period);

	}



	/**
	 * perform the actual export
	 * 
	 * @param primaryTrack
	 * @param trackWrapper
	 * @param period
	 */
	abstract protected void executeExport(WatchableList primaryTrack,
			WatchableList[] secondaryTracks, TimePeriod period);

}
