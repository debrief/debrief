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

package ASSET.Models.Decision.Movement;

import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.Status;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 04-Mar-2004 Time: 21:02:00 To
 * change this template use File | Settings | File Templates.
 */
public class RectangleWander extends Wander {
	////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	static public final class RectWanderInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public RectWanderInfo(final Wander data) {
			super(data, data.getName(), "RectangleWander");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public final java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = {
						prop("TopLeft", "the top left corner of the area we wander around"),
						prop("BottomRight", "the bottom right corner of the area we wander around"),
						prop("Name", "the name of this wandering model"), prop("Speed", "the speed we wander at (kts)"),
						prop("Height", "the Height we wander at (m)"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////////////

	/**
	 * the area we wander around
	 */
	WorldArea _ourArea;

	/**
	 * create a wander behaviour contained in a rectangle
	 *
	 * @param area
	 * @param name
	 */
	public RectangleWander(final WorldArea area, final String name) {
		super(name);
		this._ourArea = area;
		super.setOrigin(area.getCentre());
	}

	//////////////////////////////////////////////////
	// accessors (largely to support editing)
	//////////////////////////////////////////////////

	/**
	 * accessor function to return the area we are going to search
	 *
	 * @return our area.
	 */
	public WorldArea getArea() {
		return _ourArea;
	}

	public WorldLocation getBottomRight() {
		return _ourArea.getBottomRight();
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public final MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new RectWanderInfo(this);

		return _myEditor;
	}

	public WorldLocation getTopLeft() {
		return _ourArea.getTopLeft();
	}

	//////////////////////////////////////////////////
	// editor support
	//////////////////////////////////////////////////

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public final boolean hasEditor() {
		return true;
	}

	/**
	 * function to determine if it's time to turn to the new course
	 *
	 * @param current the current status
	 * @return
	 */
	@Override
	protected boolean isBeyondLimit(final Status current) {
		return !_ourArea.contains(current.getLocation());
	}

	public void setBottomRight(final WorldLocation val) {
		_ourArea.setBottomRight(val);
		_ourArea.normalise();
	}

	//////////////////////////////////////////////////
	// testing support
	//////////////////////////////////////////////////

	/**
	 * ok, plot a new course because we're at the edge
	 *
	 * @param status       our current status
	 * @param oldDemCourse the old demanded course
	 * @param res          the demanded status object (to place our data into)
	 * @return the new activity
	 */
	@Override
	protected String setNewCourse(final Status status, final Double oldDemCourse, final SimpleDemandedStatus res) {
		return super.setNewCourse(status, oldDemCourse, res, _ourArea.getCentre());
	}

	public void setTopLeft(final WorldLocation val) {
		_ourArea.setTopLeft(val);
		_ourArea.normalise();
	}

}
