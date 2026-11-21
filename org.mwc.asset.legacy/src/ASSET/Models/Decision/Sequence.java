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

package ASSET.Models.Decision;

import java.util.Vector;

import ASSET.Models.DecisionType;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GUI.Editable;

/**
 * Represents a sequence of ordered behaviours.
 * <p/>
 * When called, the sequence first examines its "Alive" status. If the sequence
 * is not "Alive", it returns a blank null.
 * <p/>
 * While a behaviour returns a non-null demanded status, that behaviour
 * continues to be active. Once a behaviour returns a null status, processing
 * switches to the next behaviour, and so on.
 * <p/>
 * Once all of the behaviours are complete, the "StayAlive" setting is examined.
 * If this behaviour should stay alive, then it remains "Alive" for the next
 * time it's required. If not, it dies and is not triggered again.
 * <p/>
 * Company:
 *
 * @author Ian Mayo
 * @version 1.0
 */

public class Sequence extends Waterfall {

	////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the object
	////////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public class SequenceInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public SequenceInfo(final Sequence data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * return a description of this bean, also specifies the custom editor we use
		 *
		 * @return the BeanDescriptor
		 */
		@Override
		public java.beans.BeanDescriptor getBeanDescriptor() {
			final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(Sequence.class,
					ASSET.GUI.Editors.Decisions.WaterfallEditor.class);
			bp.setDisplayName(MY_NAME);
			return bp;
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Name", "the name of this condition"),
						prop("IsAlive", "whether this behaviour is currently operating"),
						prop("StayAlive", "whether this behaviour should restart on completion"), };
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
	 * our behaviour name
	 */
	static final String MY_NAME = "Sequence";

	/**
	 * the index of the current behaviour
	 */
	protected int _currentDecision = 0;

	/**
	 * whether or not we should stay alive on completion
	 */
	private boolean _stayAlive;

	/**
	 * whether this status is currently alive
	 */
	private boolean _isAlive = true;

	/**
	 * the original is alive setting
	 */
	private final boolean _originalIsAlive;

	////////////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////////////

	public Sequence() {
		this(true, MY_NAME);
	}

	////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	public Sequence(final boolean isAlive, final String name) {
		super(name);
		_myDecisions = new Vector<ASSET.Models.DecisionType>(0, 1);

		_originalIsAlive = isAlive;
		_isAlive = isAlive;
	}

	/**
	 * performed the waterfalled decision, if a model does not return a demanded
	 * status, we move on to the next one
	 */
	@Override
	public ASSET.Participants.DemandedStatus decide(final ASSET.Participants.Status status,
			final ASSET.Models.Movement.MovementCharacteristics chars, final DemandedStatus demStatus,
			final ASSET.Models.Detection.DetectionList detections, final ASSET.Scenario.ScenarioActivityMonitor monitor,
			final long time) {
		ASSET.Participants.DemandedStatus res = null;

		String activity = null;

		// just check we're still alive
		if (this.getIsAlive()) {

			// step through our list
			int index;

			for (index = _currentDecision; index < _myDecisions.size(); index++) {
				final DecisionType dt = _myDecisions.elementAt(index);

				// make a decision
				res = dt.decide(status, chars, demStatus, detections, monitor, time);

				// did this work?
				if (res != null) {
					activity = dt.getActivity();

					fireUpdate(UPDATED, dt);

					// yes, drop out
					break;
				} // whether it worked

			} // looping through the behaviours

			// did we find a valid behaviour?
			if (res != null) {
				// yes, all is well - remember the current decision model
				_currentDecision = index;
			} else {
				// bugger, we've finished - reset the pointer to the first item
				_currentDecision = 0;

				// do we stay alive
				if (_stayAlive) {
					// yes, we do stay alive.
					// reset the behaviours (so they run again)
					this.restart();
				} else {
					// no, we don't want to, just lie down and die
					_isAlive = false;
				}
			}
		}

		super.setLastActivity(activity);

		return res;
	}

	@Override
	public String getBehaviourName() {
		return MY_NAME;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new SequenceInfo(this);

		return _myEditor;
	}

	/**
	 * find out if this behaviour is currently available (it's not alive if it has
	 * completed and is not set to stay alive)
	 *
	 * @return yes/no
	 */
	public boolean getIsAlive() {
		return _isAlive;
	}

	/**
	 * find out if this sequence of events should restart once complete. If not,
	 * IsAlive is set to false on completion, and it will not run again
	 *
	 * @return yes/no for whether to re-run
	 */
	public boolean getStayAlive() {
		return _stayAlive;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: Sequence.java,v $
	 * Revision 1.2  2006/11/02 10:33:19  Ian.Mayo
	 * Restart sequence properly
	 *
	 * Revision 1.1  2006/08/08 14:21:40  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:48  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.18  2004/10/28 14:52:30  ian
	 * Correct the restart implementation
	 *
	 * Revision 1.17  2004/08/31 09:36:33  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.16  2004/08/26 16:27:12  Ian.Mayo
	 * Implement editable properties
	 * <p/>
	 * Revision 1.15  2004/08/26 14:09:52  Ian.Mayo
	 * Start switching to automated property editor testing.  Correct property editor bugs where they arise.
	 * <p/>
	 * Revision 1.14  2004/08/25 11:20:52  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.13  2004/08/20 13:32:40  Ian.Mayo
	 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
	 * <p/>
	 * Revision 1.12  2004/08/17 14:22:16  Ian.Mayo
	 * Refactor to introduce parent class capable of storing name & isActive flag
	 * <p/>
	 * Revision 1.11  2004/08/06 12:55:41  Ian.Mayo
	 * Include current status when firing interruption
	 * <p/>
	 * Revision 1.10  2004/08/06 11:14:34  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.9  2004/05/24 15:59:42  Ian.Mayo
	 * Commit updates from home
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:51  ian
	 * no message
	 * <p/>
	 * Revision 1.8  2003/11/05 09:19:39  Ian.Mayo
	 * Include MWC Model support
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

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

	//////////////////////////////////////////////////
	// property editing
	//////////////////////////////////////////////////

	/**
	 * reset this decision model
	 */
	@Override
	public void restart() {
		super.restart();

		// go back to the first step in the sequence
		_currentDecision = 0;

		_isAlive = _originalIsAlive;
	}

	/**
	 * set if this behaviour is currently available (it's not alive if it has
	 * completed and is not set to stay alive)
	 *
	 * @param isAlive yes/no
	 */
	protected void setIsAlive(final boolean isAlive) {
		this._isAlive = isAlive;
	}

	/**
	 * set if this sequence of events should restart once complete. If not, IsAlive
	 * is set to false on completion, and it will not run again
	 *
	 * @param stayAlive yes/no for whether to re-run
	 */
	public void setStayAlive(final boolean stayAlive) {
		this._stayAlive = stayAlive;
	}

}