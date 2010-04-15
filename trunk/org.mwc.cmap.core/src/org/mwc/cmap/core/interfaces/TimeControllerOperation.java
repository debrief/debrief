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
	protected TimeControllerOperation(String operationName,
			boolean requireSensorData, boolean singleSensor, boolean  requiresSensorData)
	{
		_operationName = operationName;
		_requireSensor = requireSensorData;
		_singleSensor = singleSensor;
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

		TrackWrapper primaryTrack = (TrackWrapper) primary;

		if (_requireSensor)
		{

			Enumeration<SensorWrapper> enumer = primaryTrack.getSensors();
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
			SensorWrapper theSensor = FlatFileExporter.getSubjectSensor((TrackWrapper) primaryTrack);

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
			Enumeration<SensorWrapper> sensors = primaryTrack.getSensors();
			Collection <Editable> theCuts = new Vector<Editable>();
			while (sensors.hasMoreElements())
			{
				SensorWrapper thisS = sensors.nextElement();
				if (thisS.getVisible())
				{
					Collection<Editable> theseCuts = thisS.getItemsBetween(period.getStartDTG(), period.getEndDTG());
					if(theseCuts != null)
					theCuts.addAll(theseCuts);
				}
			}

			if(theCuts.size() == 0)
			{
				showMessage("Sorry, there must be at least some visible sensor data data");
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
