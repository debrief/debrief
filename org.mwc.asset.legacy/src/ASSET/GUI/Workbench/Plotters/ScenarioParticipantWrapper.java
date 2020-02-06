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

package ASSET.GUI.Workbench.Plotters;

import java.awt.Point;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import ASSET.ScenarioType;
import ASSET.Models.DecisionType;
import ASSET.Models.DecisionType.Paintable;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Vessels.SonarBuoyField;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import ASSET.Participants.Status;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * ******************************************************************** class to
 * handle listening to participants
 * *********************************************************************
 */
public class ScenarioParticipantWrapper
		implements ASSET.Participants.ParticipantMovedListener, ASSET.Participants.ParticipantDetectedListener, Layer

{
	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the projection
	// //////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public class ParticipantInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public ParticipantInfo(final ScenarioParticipantWrapper data) {
			super(data, data.getParticipantName(), "");
		}

		/**
		 * return a description of this bean, also specifies the custom editor we use
		 *
		 * @return the BeanDescriptor
		 */
		@Override
		public java.beans.BeanDescriptor getBeanDescriptor() {
			final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(ScenarioParticipantWrapper.class,
					ASSET.GUI.Editors.VesselPane.class);
			bp.setDisplayName(super.getData().toString());
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
				final java.beans.PropertyDescriptor[] res = {
						prop("ShowContacts", "show contacts for this participant"),
						prop("Visible", "whether to show this participant"),
						prop("ShowBehaviour", "whether to plot any applicable behaviours"),
						prop("Name", "name of this participant"), };
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

	public static java.awt.Color getColorFor(final String force) {
		if (force.equals(ASSET.Participants.Category.Force.BLUE))
			return DebriefColors.BLUE;
		else if (force.equals(ASSET.Participants.Category.Force.GREEN))
			return DebriefColors.GREEN;
		else
			return DebriefColors.RED;

	}

	/**
	 * the last location we received for this participant
	 */
	private MWC.GenericData.WorldLocation _curLocation = null;

	/**
	 * the participant we are watching
	 */
	private ASSET.ParticipantType _myPart = null;

	/**
	 * the editor we store
	 */
	transient private MWC.GUI.Editable.EditorType _myEditor = null;

	/**
	 * the scenario layer which contains us (we access the show symbol,name,activity
	 * from this layer)
	 */
	private ScenarioLayer _myParent = null;

	/**
	 * the most recent list of detections
	 */
	private DetectionList _myDetections;

	/**
	 * whether to plot detections or not
	 */
	private boolean _showDetections = false;

	/**
	 * whether to display any behaviours that have figurative view
	 *
	 */
	private boolean _showBehaviour = false;

	/**
	 * whether we're visible
	 */
	private boolean _visible = true;

	/**
	 * utility to represent us as a vector
	 */
	private Vector<Editable> _theElements;

	/**
	 * sort out if we have created sensor fit in our elements array
	 */
	private boolean _noSensorFit = true;

	/**
	 * sort out if we have created decision model in our elements array
	 */
	private boolean _noDecisionModel = true;

	/**
	 * sort out if we have created rad chars in our elements array
	 */
	private boolean _noRadChars = true;

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	public ScenarioParticipantWrapper(final ASSET.ParticipantType part, final ScenarioLayer parent) {
		// remember the participant
		_myPart = part;

		// take a local copy of whether decisions should be painted
		_showBehaviour = _myPart.getPaintDecisions();

		// try to get the initial location
		final ASSET.Participants.Status stat = part.getStatus();
		if (stat != null)
			_curLocation = stat.getLocation();

		_myParent = parent;
	}

	@Override
	public void add(final Editable point) {
	}

	@Override
	public void append(final Layer other) {
	}

	/**
	 * do the comparison
	 *
	 * @param arg0
	 * @return
	 */
	@Override
	public int compareTo(final Plottable arg0) {
		final ScenarioParticipantWrapper other = (ScenarioParticipantWrapper) arg0;
		final String otherName = other.getName();
		return getName().compareTo(otherName);
	}

	@Override
	public Enumeration<Editable> elements() {
		if (_theElements == null) {
			_theElements = new Vector<Editable>(3, 1);

			// ok add the movement chars
			final Editable moveChars = new MoveCharsPlottable(_myPart.getMovementChars(), _myParent);
			_theElements.add(moveChars);
		}

		// sensors
		if (_noSensorFit)
			if (_myPart.getSensorFit() != null) {
				final Editable sensors = new SensorsPlottable(_myPart.getSensorFit(), _myParent);
				_theElements.add(sensors);
				_noSensorFit = false;
			}

		// decision model
		if (_noDecisionModel)
			if (_myPart.getDecisionModel() != null) {
				final Editable behaviours = new BehavioursPlottable(_myPart.getDecisionModel(), _myParent);
				_theElements.add(behaviours);
				_noDecisionModel = false;
			}

		// rad noise model
		if (_noRadChars)
			if (_myPart.getRadiatedChars() != null) {
				final RadiatedCharacteristics chars = _myPart.getRadiatedChars();
				final Editable wrappedChart = new RadCharsPlottable(chars, _myParent);
				_theElements.add(wrappedChart);
				_noRadChars = false;
			}

		// ok, wrap our items
		return _theElements.elements();
	}

	@Override
	public void exportShape() {
	}

	/**
	 * find the data area occupied by this item
	 */
	@Override
	public MWC.GenericData.WorldArea getBounds() {
		MWC.GenericData.WorldArea res = null;
		if (_curLocation != null)
			res = new MWC.GenericData.WorldArea(_curLocation, _curLocation);

		return res;
	}

	public java.awt.Color getColor() {
		return getColorFor(_myPart.getCategory().getForce());
	}

	public int getId() {
		return _myPart.getId();
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new ParticipantInfo(this);

		return _myEditor;
	}

	// ////////////////////////////////////////////////
	// layer support
	// ////////////////////////////////////////////////

	// ////////////////////////////////////////////////
	// accessors
	// ////////////////////////////////////////////////

	@Override
	public int getLineThickness() {
		return 1;
	}

	public MWC.GenericData.WorldLocation getLocation() {
		return _myPart.getStatus().getLocation();
	}

	/**
	 * the name of this object
	 *
	 * @return the name of this editable object
	 */
	@Override
	public String getName() {
		return _myPart.getName();
		// + ":" + _myPart.getActivity() + " " + _myPart.getStatus().statusString();
	}

	/**
	 * accessor to get the participant
	 */
	public ASSET.ParticipantType getParticipant() {
		return _myPart;
	}

	public String getParticipantName() {
		return _myPart.getName();
	}

	/**
	 * whether to plot contacts
	 */
	public boolean getShowContacts() {
		return _showDetections;
	}

	/**
	 * it this item currently visible?
	 */
	@Override
	public boolean getVisible() {
		return (_myPart.isAlive() && _visible);
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

	@Override
	public boolean hasOrderedChildren() {
		return false;
	}

	public boolean isShowBehaviour() {
		return _showBehaviour;
	}

	/**
	 * this participant has moved
	 */
	@Override
	public void moved(final ASSET.Participants.Status newStatus) {
		_curLocation = newStatus.getLocation();
	}

	/**
	 * pass on the list of new detections
	 */
	@Override
	public void newDetections(final DetectionList detections) {
		_myDetections = detections;
	}

	/**
	 * paint this object to the specified canvas
	 */
	@Override
	public void paint(final MWC.GUI.CanvasType dest) {
		if (getVisible()) {
			final MWC.GenericData.WorldLocation loc = getLocation();
			if (loc != null) {
				final java.awt.Point pt = dest.toScreen(loc);

				if (_myParent.getShowSymbol()) {

					// see if we can remember this symbol
					PlainSymbol sym = _myParent.getSymbolRegister().get(_myPart.getCategory().getType());
					if (sym == null) {
						// bugger. we haven't had this one before. retrieve it the long way
						sym = MWC.GUI.Shapes.Symbols.SymbolFactory.createSymbol(_myPart.getCategory().getType());

						// ok, and remember it
						_myParent.getSymbolRegister().put(_myPart.getCategory().getType(), sym);
					}

					if (sym != null) {
						sym.setColor(getColor());
						sym.setScaleVal(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.LARGE);
						sym.paint(dest, loc, MWC.Algorithms.Conversions.Degs2Rads(_myPart.getStatus().getCourse()));
					} else {
						dest.setColor(getColor());
						dest.drawRect(pt.x, pt.y, 1, 1);
					}
				} else {
					dest.setColor(getColor());

					dest.drawRect(pt.x, pt.y, 1, 1);
				}

				// just throwin a special case - for buoyfields
				if (_myPart instanceof SonarBuoyField) {

					boolean amAlive = true;

					final SonarBuoyField field = (SonarBuoyField) _myPart;
					// is it alive?
					final long tNow = field.getStatus().getTime();
					if (!field.isActiveAt(tNow)) {
						amAlive = false;
					}

					if (!amAlive)
						dest.setLineStyle(CanvasType.DOTTED);
					else
						dest.setLineStyle(CanvasType.SOLID);

					final WorldArea area = field.getCoverage();
					final Point tl = new Point(dest.toScreen(area.getTopLeft()));
					final Point br = new Point(dest.toScreen(area.getBottomRight()));
					final int width = br.x - tl.x;
					final int height = br.y - tl.y;
					dest.drawRect(tl.x, tl.y, width, height);
				}

				// now for the activity
				if (_myParent.getShowActivity()) {
					final String act = _myPart.getActivity();
					final int ht = dest.getStringHeight(null);
					dest.drawText(act, pt.x + 16, pt.y - ht / 2);
				}

				// and the name
				if (_myParent.getShowName()) {
					final String nm = _myPart.getName();
					final int wid = dest.getStringWidth(null, nm);
					final int ht = dest.getStringHeight(null);
					dest.drawText(nm, pt.x - wid / 2, pt.y + ht - 2);
				}

				// and the name
				if (_myParent.getShowStatus()) {
					final Status theStat = _myPart.getStatus();
					final String statString = theStat.statusString();
					final int wid = dest.getStringWidth(null, statString);
					dest.drawText(statString, pt.x - wid / 2, pt.y - 22);
				}

				// lastly the detections
				if (_showDetections) {
					paintDetections(dest, pt);
				}

				// lastly lastly the behavious
				if (_showBehaviour) {
					paintBehaviours(dest);
				}
			}
		}
	}

	private void paintBehaviours(final CanvasType dest) {
		// ok, cycle through the behaviours
		final DecisionType dm = _myPart.getDecisionModel();

		// see how we get on
		paintThisBehaviour(dest, dm);
	}

	/**
	 * the detections part of the painting
	 */
	private void paintDetections(final MWC.GUI.CanvasType dest, Point pt) {

		if (_myDetections != null) {
			final int len = _myDetections.size();
			for (int i = 0; i < len; i++) {
				final DetectionEvent de = _myDetections.getDetection(i);

				// do we have bearing?
				final Float brg = de.getBearing();
				if (brg != null) {
					// do we have range?
					final WorldDistance rng = de.getRange();
					if (rng != null) {
						// hey, plot it!
						final WorldVector wv = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(brg.floatValue()),
								rng.getValueIn(WorldDistance.DEGS), 0);
						final WorldLocation other_end = _curLocation.add(wv);
						// take copy of original location, since we don't want to over-write
						// it
						pt = new Point(pt);
						final Point pt2 = dest.toScreen(other_end);
						dest.drawLine(pt.x, pt.y, pt2.x, pt2.y);
					}
				}

			}
		}
	}

	private void paintThisBehaviour(final CanvasType dest, final DecisionType dm) {
		if (dm instanceof Waterfall) {
			final Waterfall w = (Waterfall) dm;
			final Vector<DecisionType> decs = w.getModels();
			final Iterator<DecisionType> iter = decs.iterator();
			while (iter.hasNext()) {
				final DecisionType next = iter.next();
				paintThisBehaviour(dest, next);
			}
		} else {
			// ok, we've got an actual behaviour
			if (dm instanceof DecisionType.Paintable) {
				final DecisionType.Paintable pt = (Paintable) dm;
				pt.paint(dest);
			}
		}
	}

	/**
	 * how far away are we from this point. Return null if it can't be calculated
	 */
	@Override
	public double rangeFrom(final MWC.GenericData.WorldLocation other) {
		double res = INVALID_RANGE;
		if (_curLocation != null)
			res = _curLocation.rangeFrom(other);
		return res;
	}

	@Override
	public void removeElement(final Editable point) {
	}

	/**
	 * the scenario has restarted
	 */
	@Override
	public void restart(final ScenarioType scenario) {
		_curLocation = _myPart.getStatus().getLocation();
	}

	@Override
	public void setName(final String val) {
		_myPart.setName(val);
	}

	public void setShowBehaviour(final boolean showBehaviour) {
		_showBehaviour = showBehaviour;
	}

	/**
	 * whether to plot contacts
	 */
	public void setShowContacts(final boolean val) {
		_showDetections = val;
	}

	/**
	 * set the visibility of this item
	 */
	@Override
	public void setVisible(final boolean val) {
		_visible = val;
	}

	/**
	 * ************************************************************ methods
	 * *************************************************************
	 */
	public void startListen() {
		_myPart.addParticipantMovedListener(this);
		_myPart.addParticipantDetectedListener(this);
	}

	public void stopListen() {
		_myPart.removeParticipantMovedListener(this);
		_myPart.removeParticipantDetectedListener(this);
	}

	@Override
	public String toString() {
		return _myPart.getName();
	}

}
