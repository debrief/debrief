package org.mwc.cmap.core.interfaces;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.CorePlugin;

import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
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
	 * main constructor. we always want an operation name, so let the child
	 * classes call this with a name
	 * 
	 * @param operationName
	 *          what we put on the menu and the dialog
	 */
	protected TimeControllerOperation(String operationName,
			boolean requireSensorData)
	{
		_operationName = operationName;
		_requireSensor = requireSensorData;
	}

	/** the image to show for this action
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

		if (secondaries == null)
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
