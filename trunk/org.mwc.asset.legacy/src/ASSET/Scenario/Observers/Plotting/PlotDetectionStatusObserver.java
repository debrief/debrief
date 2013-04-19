/*
 * Desciption: Observer which stops the scenario once a
 * particular type of vessel has detected a particular type of target
 * User: administrator
 * Date: Nov 6, 2001
 * Time: 10:31:15 AM
 */
package ASSET.Scenario.Observers.Plotting;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.HashMap;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Category;
import ASSET.Scenario.Observers.DetectionObserver;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class PlotDetectionStatusObserver extends DetectionObserver
{
	/***************************************************************
	 * member variables
	 ***************************************************************/

	/***************************************************************
	 * constructor
	 ***************************************************************/

	/**
	 * the list of targets - and their detection status
	 * 
	 */
	private HashMap<Integer, Integer> _detectionStates;

	/**
	 * create a detection observer
	 * 
	 * @param watchVessel
	 *          the type of vessel we are monitoring
	 * @param targetVessel
	 *          the type of vessel the monitored vessel is looking for,
	 * @param name
	 *          the name of this observer
	 * @param detectionLevel
	 *          the (optional) detection level required
	 * @param isActive
	 *          whether this is observer is active
	 */

	public PlotDetectionStatusObserver(final TargetType watchVessel,
			final TargetType targetVessel, final String name,
			final Integer detectionLevel, final boolean isActive)
	{
		super(watchVessel, targetVessel, name, detectionLevel, isActive);

		// get ready...
		_detectionStates = new HashMap<Integer, Integer>();
	}

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

	@Override
	public void newDetections(DetectionList detections)
	{
		super.newDetections(detections);

		// right, see which have these have already been plotted
		Enumeration<DetectionEvent> numer = detections.elements();
		while (numer.hasMoreElements())
		{
			DetectionEvent de = numer.nextElement();

			// does this match our target category
			Category thisTargetType = de.getTargetType();
			TargetType myTargetType = this.getTargetType();
			if (myTargetType.matches(thisTargetType))
			{
				// have we already detected it?
				int tgtId = de.getHost();// de.getTarget();

				Integer oldVal = _detectionStates.get(tgtId);
				int detectionState = de.getDetectionState();

				if (oldVal != null)
				{
					// ditch any old state
					_detectionStates.remove(tgtId);

					detectionState = Math.max(oldVal, detectionState);
				}

				_detectionStates.put(tgtId, detectionState);

			}

		}
	}

	@Override
	protected void performCloseProcessing(ScenarioType scenario)
	{
		super.performCloseProcessing(scenario);
	}

	@Override
	protected void performSetupProcessing(ScenarioType scenario)
	{
		super.performSetupProcessing(scenario);
	}

	/**
	 * return the calculated result for the batch processing
	 * 
	 * @return string to be used in results collation
	 */
	protected Number getBatchResult()
	{
		return null;
	}

	/**
	 * ok, this vessel matches what we're looking for. start listening to it
	 * 
	 * @param newPart
	 */
	protected void listenTo(final ParticipantType newPart)
	{
		super.listenTo(newPart);
	}

	// ////////////////////////////////////////////////
	// scenario processing/listening
	// ////////////////////////////////////////////////

	/**
	 * get the scenario
	 */
	protected ScenarioType getScenario()
	{
		return _myScenario;
	}

	/***************************************************************
	 * listen for detections
	 ***************************************************************/

	/***************************************************************
	 * handle participants being added/removed
	 ***************************************************************/

	/***************************************************************
	 * plottable props
	 ***************************************************************/

	/**
	 * paint this object to the specified canvas
	 */
	public void paint(CanvasType dest)
	{
		if (!this.getVisible())
			return;

		if (_myScenario == null)
			return;

		// cool, here we go.

		// loop through our selected vessels
		Integer[] parts = _myScenario.getListOfParticipants();
		for (int i = 0; i < parts.length; i++)
		{
			Integer thisP = parts[i];

			// sort out where he is
			NetworkParticipant part = _myScenario.getThisParticipant(thisP);
			WorldLocation loc = part.getStatus().getLocation();
			Point pt = dest.toScreen(loc);

			// have we detected him?
			Color hisColor = null;// Color.red;
			if (_detectionStates.containsKey(thisP))
			{
				Integer theState = _detectionStates.get(thisP);

				switch (theState)
				{
				case (DetectionEvent.CLASSIFIED):
				{
					hisColor = Color.orange;
					break;
				}
				case (DetectionEvent.DETECTED):
				{
					hisColor = Color.yellow;
					break;
				}
				case (DetectionEvent.IDENTIFIED):
				{
					hisColor = Color.green;
					break;
				}
				default:
				{
					hisColor = null;
				}
				}
			}

			if (hisColor != null)
			{
				dest.setColor(hisColor);
				dest.fillOval(pt.x - 1, pt.y - 1, 3, 3);
			}
		}

	}

	/**
	 * find the data area occupied by this item
	 */
	public WorldArea getBounds()
	{
		return null;
	}

	/**
	 * Determine how far away we are from this point. or return null if it can't
	 * be calculated
	 */
	public double rangeFrom(WorldLocation other)
	{
		return -1;
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 * 
	 * @return yes/no
	 */
	public boolean hasEditor()
	{
		return true;
	}

	/**
	 * get the editor for this item
	 * 
	 * @return the BeanInfo data for this editable object
	 */
	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new DetectionInfo(this, getName());

		return _myEditor;
	}

	// ////////////////////////////////////////////////
	// accessors
	// ////////////////////////////////////////////////

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class DetectionInfo extends Editable.EditorType
	{

		public DetectionInfo(final PlotDetectionStatusObserver data,
				final String name)
		{
			super(data, name, "Plot sensor performance");
		}

		public String getName()
		{
			return PlotDetectionStatusObserver.this.getName();
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{ prop("Name", "the name of this observer"),
						prop("Active", "whether this listener is active") };
				return res;
			}
			catch (IntrospectionException e)
			{
				System.out.println("::" + e.getMessage());
				return super.getPropertyDescriptors();
			}
		}
	}

}
