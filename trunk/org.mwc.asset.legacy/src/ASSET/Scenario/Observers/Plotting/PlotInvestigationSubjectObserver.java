/*
 * Desciption: Observer which stops the scenario once a
 * particular type of vessel has detected a particular type of target
 * User: administrator
 * Date: Nov 6, 2001
 * Time: 10:31:15 AM
 */
package ASSET.Scenario.Observers.Plotting;

import java.awt.Point;
import java.util.Iterator;
import java.util.Vector;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.GUI.Workbench.Plotters.ScenarioParticipantWrapper;
import ASSET.Models.DecisionType;
import ASSET.Models.Decision.BehaviourList;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Tactical.Investigate;
import ASSET.Participants.Status;
import ASSET.Scenario.Observers.CoreObserver;
import MWC.GUI.CanvasType;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class PlotInvestigationSubjectObserver extends CoreObserver
{
	private TargetType _watchType;

	/***************************************************************
	 * member variables
	 ***************************************************************/

	/***************************************************************
	 * constructor
	 ***************************************************************/

	/**
	 * create a detection observer
	 * 
	 * @param watchVessel
	 *          the type of vessel we are monitoring
	 * @param name
	 *          the name of this observer
	 * @param isActive
	 *          whether this is observer is active
	 */

	public PlotInvestigationSubjectObserver(final TargetType watchVessel,
			final String name, final boolean isActive)
	{
		super(name, isActive);

		_watchType = watchVessel;
	}

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

	/**
	 * whether this is a significant attribute, displayed by default
	 * 
	 * @return yes/no
	 */
	public boolean isSignificant()
	{
		return false;
	}

	protected void doPlot(CanvasType dest, DecisionType decision, Integer myId)
	{
		if (decision instanceof BehaviourList)
		{
			BehaviourList list = (BehaviourList) decision;
			Vector<DecisionType> models = list.getModels();
			Iterator<DecisionType> dec = models.iterator();
			while (dec.hasNext())
			{
				DecisionType thisD = dec.next();
				doPlot(dest, thisD, myId);
			}
		}
		else
		{
			if (decision instanceof Investigate)
			{
				Investigate inv = (Investigate) decision;

				// get my location
				
				ParticipantType me = _myScenario.getThisParticipant(myId);
				Status myStat = me.getStatus();
				WorldLocation loc = myStat.getLocation();

				Integer tgtId = inv.getCurrentTarget();
				if (tgtId != null)
				{
					ParticipantType theTarget = _myScenario.getThisParticipant(tgtId);
					if (theTarget != null)
					{
						Status hisStat = theTarget.getStatus();
						WorldLocation hisLoc = hisStat.getLocation();

						Point pt1 = new Point(dest.toScreen(loc));
						Point pt2 = new Point(dest.toScreen(hisLoc));
						
						// get my color
						String force = me.getCategory().getForce();
						dest.setColor(ScenarioParticipantWrapper.getColorFor(force));
						dest.setLineStyle(CanvasType.DOTTED);

						dest.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
					}
				}

			}
		}
	}

	/**
	 * paint this object to the specified canvas
	 */
	public void paint(CanvasType dest)
	{
		if (!this.getVisible())
			return;

		if (_myScenario == null)
			return;

		Integer[] parts = _myScenario.getListOfParticipants();
		for (int i = 0; i < parts.length; i++)
		{
			Integer theId = parts[i];
			ParticipantType thisP = _myScenario.getThisParticipant(theId);
			DecisionType dm = thisP.getDecisionModel();
			doPlot(dest, dm, theId);
		}

		// // loop
		//		
		// // loop through our selected vessels
		// Vector<ParticipantType> parts = this.getWatchedVessels();
		// for (Iterator<ParticipantType> iterator = parts.iterator(); iterator
		// .hasNext();)
		// {
		// ParticipantType thisP = iterator.next();
		//
		// // right, have a look at it's sensors
		// SensorList sensors = thisP.getSensorFit();
		//
		// Collection<SensorType> sensorList = sensors.getSensors();
		// Iterator<SensorType> looper = sensorList.iterator();
		// while (looper.hasNext())
		// {
		// SensorType thisS = looper.next();
		//
		// // is it simple enought to plot?
		// if (thisS instanceof TypedCookieSensor)
		// {
		// TypedCookieSensor pcs = (TypedCookieSensor) thisS;
		// // get the detection ranges
		//
		// Vector<TypedRangeDoublet> ranges = pcs.getRanges();
		//
		// for (Iterator<TypedRangeDoublet> iterator2 = ranges.iterator(); iterator2
		// .hasNext();)
		// {
		// TypedRangeDoublet thisRange = iterator2.next();
		//
		// WorldDistance range = thisRange.getRange();
		//
		// // converto to screen coords
		// WorldLocation tl1 = thisP.getStatus().getLocation().add(
		// new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270),
		// range, null));
		// WorldLocation tl2 = thisP.getStatus().getLocation().add(
		// new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(0),
		// range, null));
		// WorldLocation br1 = thisP.getStatus().getLocation().add(
		// new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
		// range, null));
		// WorldLocation br2 = thisP.getStatus().getLocation().add(
		// new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(180),
		// range, null));
		//						
		// WorldLocation tlW = new WorldLocation(tl2.getLat(), tl1.getLong(), 0);
		// WorldLocation brW = new WorldLocation(br2.getLat(), br1.getLong(), 0);
		//
		// Point tl = new Point(dest.toScreen(tlW));
		// Point br = dest.toScreen(brW);
		//
		// boolean doSolid = false;
		//
		// // decide if this sensor is in contact
		// DetectionList dList = pcs.getDetectionsFor(thisRange);
		// if (dList != null)
		// {
		// if (dList.size() > 0)
		// {
		// DetectionEvent lastD = dList.lastElement();
		// // is it at the current time?
		// if (lastD.getTime() == _tNow)
		// {
		// doSolid = true;
		// }
		// }
		// }
		//
		// // sort out the color
		// dest.setColor(ScenarioParticipantWrapper.getColorFor(thisP
		// .getCategory().getForce()));
		//
		// if (doSolid)
		// dest.setLineStyle(CanvasType.SOLID);
		// else
		// dest.setLineStyle(CanvasType.DOTTED);
		//
		// if (_shadeCircle)
		// {
		// if (dest instanceof ExtendedCanvasType)
		// {
		// ExtendedCanvasType et = (ExtendedCanvasType) dest;
		// et.semiFillOval(tl.x, tl.y, br.x - tl.x, br.y - tl.y);
		// }
		// else
		// dest.fillOval(tl.x, tl.y, br.x - tl.x, br.y - tl.y);
		// }
		// dest.drawOval(tl.x, tl.y, br.x - tl.x, br.y - tl.y);
		//
		// dest.setLineStyle(CanvasType.SOLID);
		//
		// if (_showNames)
		// {
		// // collate a string of the types this ring represents
		// StringBuilder sb = new StringBuilder();
		// Vector<String> theItems = thisRange.getMyTypes();
		// if (theItems != null)
		// {
		// Iterator<String> iter = theItems.iterator();
		// while (iter.hasNext())
		// {
		// String thisT = iter.next();
		// sb.append(thisT + " ");
		// }
		//
		// if (sb.length() > 0)
		// {
		// String theStr = sb.toString();
		// int wit = dest.getStringWidth(null, theStr);
		// dest.drawText(theStr, tl.x + (br.x - tl.x) / 2 - wit / 2,
		// br.y + dest.getStringHeight(null));
		// }
		// }
		// }
		// }
		// }
		// }
		// }
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

	@Override
	protected void addListeners(ScenarioType scenario)
	{
	}

	@Override
	protected void performCloseProcessing(ScenarioType scenario)
	{
	}

	@Override
	protected void performSetupProcessing(ScenarioType scenario)
	{
	}

	@Override
	protected void removeListeners(ScenarioType scenario)
	{
	}

	@Override
	public EditorType getInfo()
	{
		return null;
	}

	public TargetType getWatchType()
	{
		return _watchType;
	}

	// ////////////////////////////////////////////////
	// accessors
	// ////////////////////////////////////////////////

}
