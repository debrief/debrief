package org.mwc.debrief.core.operations;

import interfaces.TimeControllerOperation;

import java.util.Enumeration;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;

public class ExportToFlatFile implements TimeControllerOperation
{
	
	/** the operation we're conducting
	 * 
	 */
	private String _operationName;

	public ExportToFlatFile()
	{
		this("Export to flat file");
	}
	
	/** main constructor. we always want an operation name, so let the
	 * child classes call this with a name
	 * 
	 * @param operationName what we put on the menu and the dialog
	 */
	protected ExportToFlatFile(String operationName)
	{
		_operationName = operationName;
	}

	@Override
	public ImageDescriptor getDescriptor()
	{
		return DebriefPlugin.getImageDescriptor("icons/new.gif");
	}

	@Override
	public String getName()
	{
		return _operationName;
	}

	/** report a messge
	 * 
	 * @param msg the message
	 */
	protected void showMessage(final String msg)
	{
		CorePlugin.showMessage(_operationName, msg);
	}

	@Override
	public void run(final WatchableList primary, final WatchableList[] secondaries,
			final TimePeriod period)
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
		Enumeration<SensorWrapper> enumer = primaryTrack.getSensors();
		if(enumer == null)
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
		
		// well, here we are.  May as well go for it!
		executeExport(primaryTrack, (TrackWrapper) secondaries[0], period);

	}

	/** perform the actual export
	 * 
	 * @param primaryTrack
	 * @param trackWrapper
	 * @param period
	 */
	private void executeExport(TrackWrapper primaryTrack,
			TrackWrapper trackWrapper, TimePeriod period)
	{
		writeHeader();
		
		// now loop through the OS track
		System.err.println("DOING EXPORT!!");
	}

	private void writeHeader()
	{
		// TODO Auto-generated method stub
		
	}

}
