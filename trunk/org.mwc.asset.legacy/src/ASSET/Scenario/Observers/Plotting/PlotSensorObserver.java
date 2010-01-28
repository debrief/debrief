/*
 * Desciption: Observer which stops the scenario once a
 * particular type of vessel has detected a particular type of target
 * User: administrator
 * Date: Nov 6, 2001
 * Time: 10:31:15 AM
 */
package ASSET.Scenario.Observers.Plotting;

import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.GUI.Workbench.Plotters.ScenarioParticipantWrapper;
import ASSET.Models.SensorType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Sensor.Cookie.TypedCookieSensor;
import ASSET.Models.Sensor.Cookie.TypedCookieSensor.TypedRangeDoublet;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Scenario.Observers.DetectionObserver;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class PlotSensorObserver extends DetectionObserver implements
		ScenarioSteppedListener
{
	/***************************************************************
	 * member variables
	 ***************************************************************/

	/***************************************************************
	 * constructor
	 ***************************************************************/

	/**
	 * the last time step
	 * 
	 */
	private long _tNow = -1;

	/**
	 * whether to display the target types
	 * 
	 */
	private boolean _showNames;

	private boolean _shadeCircle;

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
	 * @param showNames
	 * @param shadeCircle 
	 */

	public PlotSensorObserver(final TargetType watchVessel,
			final TargetType targetVessel, final String name,
			final Integer detectionLevel, final boolean isActive, boolean showNames, boolean shadeCircle)
	{
		super(watchVessel, targetVessel, name, detectionLevel, isActive);

		_showNames = showNames;
		_shadeCircle = shadeCircle;
	}

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

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
	}

	// ////////////////////////////////////////////////
	// scenario processing/listening
	// ////////////////////////////////////////////////

	/**
	 * add any applicable listeners
	 */
	protected void addListeners(ScenarioType scenario)
	{
		super.addListeners(scenario);

		scenario.addScenarioSteppedListener(this);
	}

	/**
	 * remove any listeners
	 */
	protected void removeListeners(ScenarioType scenario)
	{
		super.removeListeners(scenario);

		scenario.removeScenarioSteppedListener(this);
	}

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

	public boolean getShowNames()
	{
		return _showNames;
	}

	public void setShowNames(boolean showNames)
	{
		_showNames = showNames;
	}

	public boolean isShadeCircle()
	{
		return _shadeCircle;
	}

	public void setShadeCircle(boolean shadeCircle)
	{
		_shadeCircle = shadeCircle;
	}

	/**
	 * paint this object to the specified canvas
	 */
	public void paint(CanvasType dest)
	{
		if(!this.getVisible())
			return;
		
		// cool, here we go.

		// loop through our selected vessels
		Vector<ParticipantType> parts = this.getWatchedVessels();
		for (Iterator<ParticipantType> iterator = parts.iterator(); iterator
				.hasNext();)
		{
			ParticipantType thisP = iterator.next();

			// right, have a look at it's sensors
			SensorList sensors = thisP.getSensorFit();

			Collection<SensorType> sensorList = sensors.getSensors();
			Iterator<SensorType> looper = sensorList.iterator();
			while (looper.hasNext())
			{
				SensorType thisS = looper.next();

				// is it simple enought to plot?
				if (thisS instanceof TypedCookieSensor)
				{
					TypedCookieSensor pcs = (TypedCookieSensor) thisS;
					// get the detection ranges

					Vector<TypedRangeDoublet> ranges = pcs.getRanges();

					for (Iterator<TypedRangeDoublet> iterator2 = ranges.iterator(); iterator2
							.hasNext();)
					{
						TypedRangeDoublet thisRange = iterator2.next();

						WorldDistance range = thisRange.getRange();

						// converto to screen coords
						WorldLocation tlW = thisP.getStatus().getLocation().add(
								new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(315),
										range, null));
						WorldLocation brW = thisP.getStatus().getLocation().add(
								new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(135),
										range, null));

						Point tl = new Point(dest.toScreen(tlW));
						Point br = dest.toScreen(brW);

						boolean doSolid = false;

						// decide if this sensor is in contact
						DetectionList dList = pcs.getDetectionsFor(thisRange);
						if (dList != null)
						{
							if (dList.size() > 0)
							{
								DetectionEvent lastD = dList.lastElement();
								// is it at the current time?
								if (lastD.getTime() == _tNow)
								{
									doSolid = true;
								}
							}
						}

						// sort out the color
						dest.setColor(ScenarioParticipantWrapper.getColorFor(thisP.getCategory().getForce()));
						
						if (doSolid)
							dest.setLineStyle(CanvasType.SOLID);
						else
							dest.setLineStyle(CanvasType.DOTTED);

						if (_shadeCircle)
						{
							dest.fillOval(tl.x, tl.y, br.x - tl.x, br.y - tl.y);
						}
						dest.drawOval(tl.x, tl.y, br.x - tl.x, br.y - tl.y);

						dest.setLineStyle(CanvasType.SOLID);

						if (_showNames)
						{
							// collate a string of the types this ring represents
							StringBuilder sb = new StringBuilder();
							Vector<String> theItems = thisRange.getMyTypes();
							Iterator<String> iter = theItems.iterator();
							while (iter.hasNext())
							{
								String thisT = iter.next();
								sb.append(thisT + " ");
							}

							if (sb.length() > 0)
							{
								String theStr = sb.toString();
								int wit = dest.getStringWidth(null, theStr);
								dest.drawText(theStr, tl.x + (br.x - tl.x) / 2 - wit / 2, br.y
										+ dest.getStringHeight(null));
							}
						}
					}
				}
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

		public DetectionInfo(final PlotSensorObserver data, final String name)
		{
			super(data, name, "Plot sensor performance");
		}

		public String getName()
		{
			return PlotSensorObserver.this.getName();
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{ prop("Name", "the name of this observer"),
						prop("Active", "whether this listener is active"),
						prop("ShadeCircle", "whether to shade in the arc of coverage"),
						prop("ShowNames", "whether to plot text for target types"), };
				return res;
			}
			catch (IntrospectionException e)
			{
				System.out.println("::" + e.getMessage());
				return super.getPropertyDescriptors();
			}
		}
	}

	@Override
	protected void stopListeningTo(ParticipantType thisPart)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void step(ScenarioType scenario, long newTime)
	{
		_tNow = newTime;
	}
}
