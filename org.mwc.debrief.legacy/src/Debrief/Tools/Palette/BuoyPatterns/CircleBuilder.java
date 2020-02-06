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

// $RCSfile: CircleBuilder.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: CircleBuilder.java,v $
// Revision 1.2  2005/12/13 09:04:49  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:48:49  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-07-04 10:59:26+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.3  2003-03-19 15:37:20+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 09:25:10+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:47+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:43+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-01-17 20:21:29+00  administrator
// Reflect use of WorldDistance and duration objects
//
// Revision 1.1  2001-08-21 12:14:52+01  administrator
// Don't keep the local editor
//
// Revision 1.0  2001-07-17 08:41:15+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-22 12:30:03+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-17 13:21:02+00  novatech
// Initial revision
//
// Revision 1.4  2001-01-11 15:36:23+00  novatech
// time details have been passed to BuoyField
//
// Revision 1.3  2001-01-09 13:59:17+00  novatech
// add "direction" parameter which specifies clockwise/anti-clockwise
//
// Revision 1.2  2001-01-08 14:14:29+00  novatech
// better numbering of buoys, improved tooltips
//
// Revision 1.1  2001-01-08 11:40:45+00  novatech
// Initial revision
//

package Debrief.Tools.Palette.BuoyPatterns;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public final class CircleBuilder extends PatternBuilderType {

	//////////////////////////////////////////
	// Member variables
	//////////////////////////////////////////

	public final class CircleInfo extends MWC.GUI.Editable.EditorType {

		public CircleInfo(final CircleBuilder data, final String theName) {
			super(data, theName, "Circle:");
		}

		@Override
		public final PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] myRes = {
						displayProp("SymbolType", "Symbol type", "the type of symbol plotted for this label"),
						displayProp("SymbolSize", "Symbol size", "the scale of the symbol"),
						prop("Duration", "the lifetime of the buoy pattern"),
						displayProp("PatternName", "Pattern name", "the name of this circle"),
						displayProp("PatternOrientation", "Pattern orientation",
								"the orientation of the first point of this circle (degs)"),
						displayProp("PatternRadius", "Pattern radius", "the radius of this circular pattern"),
						displayProp("KingpinRange", "Kingpin range", "the range of the kingpin from the jig point"),
						displayProp("KingpinBearing", "Kingpin bearing",
								"the bearing of the kingpin from the jig point (degs)"),
						displayProp("JigPoint", "Jig point", "the jig point for the construction of this circle"),
						displayProp("NumberOfBuoys", "Number of buoys", "the number of buoys in this circle"),
						prop("Color", "the default colour for this circle"),
						displayProp("DateTimeGroup", "DateTime group", "the DTG this pattern starts (DD/MM/YY)"),
						displayProp("BuoyLabelVisible", "Buoy label visible", "whether the buoy labels are visible"),
						displayProp("PatternClockwise", "Pattern clockwise", "whether the buoys are laid clockwise") };
				myRes[0].setPropertyEditorClass(
						MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor.SymbolFactoryBuoyPropertyEditor.class);
				myRes[1].setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.class);

				return myRes;

			} catch (final IntrospectionException e) {
				// find out which property fell over
				MWC.Utilities.Errors.Trace.trace(e, "Creating editor for Circle Builder");

				return super.getPropertyDescriptors();
			}
		}

		/**
		 * method which gets called when all parameters have been updated
		 */
		@Override
		public final void updatesComplete() {
			// get the builder to build itself
			create();

			// inform the parent
			super.updatesComplete();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val) {
			super(val);
		}

		public final void testMyParams() {
			MWC.GUI.Editable ed = new CircleBuilder(null, null, null);
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

	/**
	 * radius of this barrier (nm)
	 */
	private double _radius;

	/**
	 * orientation of this barrier (degs)
	 */
	private double _orientation;

	/**
	 * direction to lay circle (yes/no)
	 */
	private boolean _clockwise = true;

	//////////////////////////////////////////
	// Member functions
	//////////////////////////////////////////

	/**
	 * our editor
	 */
	transient private MWC.GUI.Editable.EditorType _myEditor = null;

	//////////////////////////////////////////
	// editable accessor functions
	//////////////////////////////////////////

	//////////////////////////////////////////
	// Constructor
	//////////////////////////////////////////
	public CircleBuilder(final WorldLocation centre, final MWC.GUI.Properties.PropertiesPanel thePanel,
			final MWC.GUI.Layers theData) {
		super(centre, thePanel, theData);

		// initialise our variables
		_radius = 5.0;
		_orientation = 0.0;
		setPatternName("blank circle");

		// and the variables in our parent
		setKingpinBearing(0.0);
		setKingpinRange(new WorldDistance(0.0, WorldDistance.DEGS));
		setNumberOfBuoys(new Integer(12));

	}

	/**
	 * this method is called by the 'Create' function, and it fills in the buoys
	 * into the correct pattern
	 */
	@Override
	protected final void addBuoys(final Debrief.Wrappers.BuoyPatternWrapper pattern) {
		final WorldLocation centre = getKingpin();
		final double orient_rads = MWC.Algorithms.Conversions.Degs2Rads(_orientation);
		final double radius_degs = MWC.Algorithms.Conversions.Nm2Degs(_radius);

		// find out the angle between each buoy
		double theta = 360.0 / (getNumberOfBuoys().intValue());
		theta = MWC.Algorithms.Conversions.Degs2Rads(theta);

		if (_clockwise) {
			// do nothing, we're already dropping them in a clockwise direction
		} else {
			// reverse the direction we are dropping in
			theta = -1.0 * theta;
		}

		double currentAngle = orient_rads;

		for (int i = 0; i < getNumberOfBuoys().intValue(); i++) {

			// create the location for this buoy, starting with the correct orientation
			final WorldVector thisStep = new MWC.GenericData.WorldVector(currentAngle, radius_degs, 0.0);
			final WorldLocation thisLoc = centre.add(thisStep);

			// create the new symbol
			final Debrief.Wrappers.LabelWrapper lw = new Debrief.Wrappers.LabelWrapper("C" + (i + 1), thisLoc,
					MWC.GUI.Properties.DebriefColors.RED);

			this.formatSymbol(lw, pattern);

			currentAngle += theta;
		}
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public final MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new CircleInfo(this, this.getName());

		return _myEditor;
	}

	public final boolean getPatternClockwise() {
		return _clockwise;
	}

	public final double getPatternOrientation() {
		return _orientation;
	}

	public final WorldDistance getPatternRadius() {
		return new WorldDistance(_radius, WorldDistance.NM);
	}

	public final void setPatternClockwise(final boolean val) {
		_clockwise = val;
	}

	public final void setPatternOrientation(final double val) {
		_orientation = val;
	}

	//////////////////////////////////////////
	// editable details
	//////////////////////////////////////////

	public final void setPatternRadius(final WorldDistance val) {
		_radius = val.getValueIn(WorldDistance.NM);
	}

	@Override
	public final String toString() {
		return "Circle Builder";
	}

}