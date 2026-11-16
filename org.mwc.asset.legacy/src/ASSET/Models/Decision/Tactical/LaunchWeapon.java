/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package ASSET.Models.Decision.Tactical;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

import ASSET.ParticipantType;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * behaviour relating to the launch of a weapon The weapon to be launched is
 * stored as a long text string. The text string may contain the following
 * keywords which will be replaced with the correct value when the weapon is
 * launched (if known)
 * <ul>
 * <li>$BRG$ current bearing to the target (in degrees)</li>
 * <li>$RNG$ current range to the target (in yards)</li>
 * <ul>
 */

public class LaunchWeapon extends CoreDecision implements java.io.Serializable {

	/**
	 * ************************************************* editor support
	 * *************************************************
	 */

	static public class LaunchInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public LaunchInfo(final LaunchWeapon data) {
			super(data, data.getName(), "LaunchWeapon");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = {
						prop("TargetType", "the type of vessel this model is evading"),
						prop("LaunchRange", "the range at which to launch"),
						prop("Name", "the name of this evasion model"),
						// prop("LaunchType", "the description of the weapon to be fired"),
						prop("CoolOffTime", "the period before we re-launch against another valid target (millis)"),
						prop("LaunchFilename", "the filename where the weapon is stored"),

				};
				// res[0].setPropertyEditorClass(ASSET.GUI.Editors.TargetTypeEditor.class);
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the activity to return when we launch
	 */
	static final String LAUNCH_MESSAGE = "Launching";

	/**
	 * get the participant
	 */
	protected static ParticipantType getWeapon(final DetectionEvent detection, final Status currentLocation,
			final String weapon, final TargetType target) {
		ParticipantType res = null;

		final String fName = null;

		// swap in the keywords
		final String working = swapKeywords(detection, currentLocation, weapon, target);

		// put the string into a stream
		// final StringBufferInputStream sr = new StringBufferInputStream(working);
		final InputStream sr = new ByteArrayInputStream(working.getBytes());

		// extract the participant
		res = ASSETReaderWriter.importParticipant(fName, sr);

		return res;
	}

	/**
	 * utility method to read the contents of a file, and return it as a string
	 *
	 * @param fileName
	 * @return
	 */
	public static String readWeaponFromThisFile(final String fileName) {
		final StringBuffer res = new StringBuffer();
		// we've received the weapon description as a filename, load it
		try {

			final java.io.FileReader fi = new java.io.FileReader(fileName);
			final java.io.BufferedReader bg = new java.io.BufferedReader(fi);
			String next = bg.readLine();
			while (next != null) {
				res.append(next);
				res.append("\n");
				next = bg.readLine();
			}
			bg.close();

		} catch (final java.io.IOException ee) {
			ee.printStackTrace();
		}
		return res.toString();
	}

	/**
	 * utility class provided to overcome String.replace only being present in
	 * JDK1.4
	 */
	protected static String replaceAll(final String original, final String toBeSwappedOut, final String toBeSwappedIn) {
		if (original == null)
			return null;

		String temp = original;
		int a = 0;

		for (int i = 0; i < original.length(); i++) {
			a = temp.indexOf(toBeSwappedOut);
			if (a == -1)
				break;
			temp = temp.substring(0, a) + toBeSwappedIn + temp.substring(a + toBeSwappedOut.length());
			int tmp = toBeSwappedOut.length() - toBeSwappedIn.length();
			if (tmp < 0)
				tmp = (tmp * -1) + 1;
			if (tmp == 0)
				tmp = toBeSwappedIn.length();
			a += tmp;
		}

		return temp;
	}

	/**
	 * put our keywords into the XML description
	 */
	protected static String swapKeywords(final DetectionEvent detection, final Status currentLocation,
			final String weapon, final TargetType theTarget) {
		// amend string template to include available parameters
		final Float brg_degs = detection.getBearing();
		final WorldDistance rng = detection.getRange();

		// take a copy of the string
		String working = new String(weapon);

		// swap the bearing
		if (brg_degs != null) {
			final String brg_val = "" + brg_degs.floatValue();
			working = replaceAll(working, "$BRG$", brg_val);
		}

		// swap the range
		if (rng != null) {
			final String rng_val = "" + rng.getValueIn(WorldDistance.YARDS);
			working = replaceAll(working, "$RNG$", rng_val);
		}

		// insert the location of the target
		if (brg_degs != null) {
			final float brg_val = brg_degs.floatValue();

			// do we know range?
			if (rng != null) {
				// yes, compute target location
				final WorldVector newVector = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(brg_val),
						rng.getValueIn(WorldDistance.DEGS), 0);
				final WorldLocation newLoc = currentLocation.getLocation().add(newVector);

				// produce strings from this location
				final String theDepth = "" + newLoc.getDepth();
				final String theLat = "" + newLoc.getLat();
				final String theLong = "" + newLoc.getLong();

				// put these strings into the new behaviour
				working = replaceAll(working, "$TGT_DEPTH$", theDepth);
				working = replaceAll(working, "$TGT_LAT$", theLat);
				working = replaceAll(working, "$TGT_LONG$", theLong);

			} else {
				// no, send the weapon down a bearing for XXXX yds
				// compute target location
				final double TGT_RANGE = 5000;
				final WorldVector newVector = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(brg_val),
						MWC.Algorithms.Conversions.Yds2Degs(TGT_RANGE), 0);
				final WorldLocation newLoc = currentLocation.getLocation().add(newVector);

				// produce strings from this location
				final String theDepth = "" + newLoc.getDepth();
				final String theLat = "" + newLoc.getLat();
				final String theLong = "" + newLoc.getLong();

				// put these strings into the new behaviour
				working = replaceAll(working, "$TGT_DEPTH$", theDepth);
				working = replaceAll(working, "$TGT_LAT$", theLat);
				working = replaceAll(working, "$TGT_LONG$", theLong);
			}
		}

		if (theTarget != null) {
			// output the XML header stuff
			// output the plot
			final java.io.StringWriter newString = new StringWriter();
			// final com.sun.xml.tree.XmlDocument doc = new com.sun.xml.tree.XmlDocument();
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document doc = null;
			try {
				final DocumentBuilder builder = factory.newDocumentBuilder();
				doc = builder.newDocument();
				final org.w3c.dom.Element type = ASSET.Util.XML.Decisions.Util.TargetTypeHandler.getElement(theTarget,
						doc);
				doc.appendChild(type);
				doc.setNodeValue(type.getTagName());
				// doc.changeNodeOwner(type);
				// doc.setSystemId("ASSET XML Version 1.0");

				// Use a Transformer for output
				final TransformerFactory tFactory = TransformerFactory.newInstance();
				final Transformer transformer = tFactory.newTransformer();

				final DOMSource source = new DOMSource(doc);
				final StreamResult result = new StreamResult(newString);
				transformer.transform(source, result);
			} catch (final ParserConfigurationException e) {
				e.printStackTrace(); // To change body of catch statement use Options | File Templates.
			} catch (final DOMException e) {
				e.printStackTrace(); // To change body of catch statement use Options | File Templates.
			} catch (final TransformerFactoryConfigurationError transformerFactoryConfigurationError) {
				transformerFactoryConfigurationError.printStackTrace(); // To change body of catch statement
																		// use Options | File Templates.
			} catch (final TransformerException e) {
				e.printStackTrace(); // To change body of catch statement use Options | File Templates.
			}

			// // ok, we should be done now
			// try
			// {
			// doc.write(newString);
			// }
			// catch(java.io.IOException e)
			// {
			// e.printStackTrace();
			// }

			// try to extract the <target type portion
			if (newString != null) {
				final String val = newString.toString();
				final String startIdentifier = "<TargetType";
				final String endIdentifier = "</TargetType";
				final int start = val.indexOf(startIdentifier);
				final int end = val.lastIndexOf(endIdentifier);
				final String detail = val.substring(start, end + endIdentifier.length() + 1);

				// lastly, replace the string
				working = replaceAll(working, "<TargetType/>", detail);
			}

		}

		return working;

	}

	/**
	 * the type of participant to launch
	 */
	protected String _launchThis = "blank";

	/**
	 * the filename where we read the launch description from
	 */
	protected String _fileName = null;

	/**
	 * the type of target to launch against
	 */
	private TargetType _myTarget;

	/**
	 * a local copy of our editable object
	 */
	protected MWC.GUI.Editable.EditorType _myEditor = null;

	/**
	 * the range at which we launch weapon (yds)
	 */
	private WorldDistance _launchRange;

	/**
	 * the amount of time to wait before launching another (millis)
	 */
	private Duration _coolOffTime;

	/**
	 * the time the last missile was fired
	 */
	private long _lastLaunch = -1;

	/**
	 * ************************************************* constructor
	 * *************************************************
	 */
	public LaunchWeapon() {
		super("Launch Weapon");
		_coolOffTime = new Duration(Duration.DAYS, 1);
	}

	/**
	 * decide the course of action to take, or return null to no be used
	 *
	 * @param status     the current status of the participant
	 * @param detections the current list of detections for this participant
	 * @param time       the time this decision was made
	 */
	@Override
	public DemandedStatus decide(final Status status, final ASSET.Models.Movement.MovementCharacteristics chars,
			final DemandedStatus demStatus, final DetectionList detections,
			final ASSET.Scenario.ScenarioActivityMonitor monitor, final long time) {
		// produce a steady-state demanded course - so we continue
		// what we're doing during the weapons launch
		DemandedStatus res = null;

		// clear the activity flag
		String activity = "Not in trail";

		// is it time to fire another yet?
		if ((_lastLaunch == -1) || (time > _lastLaunch + _coolOffTime.getValueIn(Duration.MILLISECONDS))) {

			// do we have any detections?
			if (detections != null) {
				// get bearing to first detection
				final int len = detections.size();
				if (len > 0) {
					for (int i = 0; i < len; i++) {

						final ASSET.Models.Detection.DetectionEvent de = detections.getDetection(i);
						final Float brg = de.getBearing();
						if (brg != null) {
							// do we have a target type?
							if (_myTarget != null) {
								// is this of our target type
								final ASSET.Participants.Category thisTarget = de.getTargetType();
								if (_myTarget.matches(thisTarget)) {
									// do we have range?
									if (de.getRange() != null) {

										// work out distance from us to the target, not from the sensor to the target
										final WorldLocation sensorLocation = de.getSensorLocation();

										// Work out the estimated target location
										final WorldVector sensorToTarget = new WorldVector(
												MWC.Algorithms.Conversions.Degs2Rads(de.getBearing().doubleValue()),
												de.getRange().getValueIn(WorldDistance.DEGS), 0);

										final WorldLocation targetLocation = sensorLocation.add(sensorToTarget);

										// how are are we from the target location
										final WorldVector meToTarget = status.getLocation().subtract(targetLocation);
										final double yds_to_target = MWC.Algorithms.Conversions
												.Degs2Yds(meToTarget.getRange());
										final double brg_to_target_degs = MWC.Algorithms.Conversions
												.Rads2Degs(meToTarget.getBearing());

										// is it within range?
										if (yds_to_target < _launchRange.getValueIn(WorldDistance.YARDS)) {
											// continue in steady state
											res = new SimpleDemandedStatus(time, status);

											// remember the launch time
											_lastLaunch = time;

											// start the launch steps
											launchWeapon(de, monitor, status, brg_to_target_degs, time);

											activity = LaunchWeapon.LAUNCH_MESSAGE;

											// ok, drop out, we don't need to launch any more weapons
											return res;
										}
									}
								}
							}
						} // if we know the bearing
					} // looping through the detections
				} // if we have any detections
			} // if the detections object was received
		} // whether it's time to launch another
		else {
			//
		}

		super.setLastActivity(activity);

		// always return null, since we continue in steady state
		return res;
	}

	/**
	 * get how long to wait before re-launch
	 */
	public Duration getCoolOffTime() {
		return _coolOffTime;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new LaunchInfo(this);

		return _myEditor;
	}

	/**
	 * get the filename for the weapon description
	 */
	public String getLaunchFilename() {
		return _fileName;
	}

	/**
	 * get the range at which we launch (yds)
	 */
	public WorldDistance getLaunchRange() {
		return _launchRange;
	}

	/**
	 * get the XML string describing what to launch
	 */
	public String getLaunchType() {
		return _launchThis;
	}

	/**
	 * ************************************************* member methods
	 * *************************************************
	 */
	public TargetType getTargetType() {
		return _myTarget;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: LaunchWeapon.java,v $
	 * Revision 1.1  2006/08/08 14:21:36  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:44  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.21  2004/09/02 13:17:36  Ian.Mayo
	 * Reflect CoreDecision handling the toString method
	 *
	 * Revision 1.20  2004/08/31 09:36:25  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.19  2004/08/26 16:27:06  Ian.Mayo
	 * Implement editable properties
	 * <p/>
	 * Revision 1.18  2004/08/25 11:20:40  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.17  2004/08/20 13:32:31  Ian.Mayo
	 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
	 * <p/>
	 * Revision 1.16  2004/08/17 14:22:08  Ian.Mayo
	 * Refactor to introduce parent class capable of storing name & isActive flag
	 * <p/>
	 * Revision 1.15  2004/08/12 11:09:24  Ian.Mayo
	 * Respect observer classes refactored into tidy directories
	 * <p/>
	 * Revision 1.14  2004/08/09 15:50:33  Ian.Mayo
	 * Refactor category types into Force, Environment, Type sub-classes
	 * <p/>
	 * Revision 1.13  2004/08/06 12:52:05  Ian.Mayo
	 * Include current status when firing interruption
	 * <p/>
	 * Revision 1.12  2004/08/06 11:14:27  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.11  2004/05/24 15:57:11  Ian.Mayo
	 * Commit updates from home
	 * <p/>
	 * Revision 1.2  2004/04/08 20:27:17  ian
	 * Restructured contructor for CoreObserver
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:52  ian
	 * no message
	 * <p/>
	 * Revision 1.10  2004/02/18 08:48:10  Ian.Mayo
	 * Sync from home
	 * <p/>
	 * Revision 1.7  2003/11/05 14:28:25  Ian.Mayo
	 * correct path for testing
	 * <p/>
	 * Revision 1.6  2003/11/05 09:19:55  Ian.Mayo
	 * Include MWC Model support
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	/**
	 * indicate to this model that its execution has been interrupted by another
	 * (prob higher priority) model
	 *
	 * @param currentStatus
	 */
	@Override
	public void interrupted(final Status currentStatus) {
		// ignore.
	}

	/**
	 * do the actual launch
	 */
	private void launchWeapon(final DetectionEvent detection, final ASSET.Scenario.ScenarioActivityMonitor monitor,
			final Status currentLocation, final double initial_course_degs, final long time) {

		// have we read in the weapon description yet?
		if ((_launchThis == null) || (_launchThis.length() == 0))
			_launchThis = readWeaponFromThisFile(this._fileName);

		final ParticipantType newPart = getWeapon(detection, currentLocation, _launchThis, _myTarget);

		// create the target
		monitor.createParticipant(newPart);

		// set it's status
		final Status newState = new Status(currentLocation);
		// update the status for the object's new id
		newState.setId(newPart.getId());

		// and put it in the participant
		newPart.setStatus(newState);

		// give it a faster speed though
		newState.setSpeed(new WorldSpeed(30, WorldSpeed.M_sec));

		// give the weapon a hint about direction to travel in
		final SimpleDemandedStatus ds = new SimpleDemandedStatus(time, newState);
		ds.setCourse(initial_course_degs);

		newPart.setDemandedStatus(ds);

	}

	/**
	 * reset this decision model
	 */
	@Override
	public void restart() {
		_lastLaunch = -1;
	}

	/**
	 * set how long to wait before re-launch
	 */
	public void setCoolOffTime(final Duration coolOffTimeMillis) {
		_coolOffTime = coolOffTimeMillis;
	}

	/**
	 * set the filename for the description
	 */
	public void setLaunchFilename(final String launchThis) {
		_fileName = launchThis;
	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	/**
	 * set the range at which we launch (yds)
	 */
	public void setLaunchRange(final WorldDistance launchRangeYds) {
		this._launchRange = launchRangeYds;
	}

	/**
	 * set the XML string describing what to launch
	 */
	public void setLaunchType(final String launchThis) {
		this._launchThis = launchThis;
	}

	public void setTargetType(final TargetType target) {
		this._myTarget = target;
	}
}
