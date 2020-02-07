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

package MWC.GUI.S57;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: S57Layer.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.6 $
// $Log: S57Layer.java,v $
// Revision 1.6  2007/05/11 10:52:10  ian.mayo
// Add rudimentary S57 import support
//
// Revision 1.5  2007/05/08 08:07:42  ian.mayo
// Lots of refactoring.  Plot lots of new feature types
//
// Revision 1.4  2007/05/04 08:30:16  ian.mayo
// Changes from home
//
// Revision 1.3  2007/05/01 15:17:11  ian.mayo
// Have series of points, called it a layer...
//
// Revision 1.2  2007/04/27 16:06:35  ian.mayo
// Getting closer
//
// Revision 1.1  2007/04/27 13:45:42  ian.mayo
// Getting closer.  Importing data
//
// Revision 1.1  2007/04/27 09:20:28  ian.mayo
// initial import

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.Serializable;
import java.util.Enumeration;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables.IteratorWrapper;
import MWC.GUI.S57.features.S57Feature;
import MWC.GenericData.WorldArea;

public class S57Layer implements Layer {
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class GridPainterTest extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public GridPainterTest(final String val) {
			super(val);
		}

		public void testMyParams() {
			MWC.GUI.Editable ed = new S57Layer();
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

	// ///////////////////////////////////////////////////////////
	// info class
	// //////////////////////////////////////////////////////////
	public class S57PainterInfo extends Editable.EditorType implements Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public S57PainterInfo(final S57Layer data) {
			super(data, data.getName(), "");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Visible", "whether S57 data is visible", VISIBILITY),
						displayProp("SourceFile", "Source file", "the S57 data-file", FORMAT), };
				return res;
			} catch (final IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		final S57Layer layer = new S57Layer();
		layer.load("d://dev/AU411141.000");
		System.out.println("loaded!");
	}

	/**
	 * whether this layer is visible
	 */
	protected boolean _isOn;

	/**
	 * our editor
	 */
	transient protected Editable.EditorType _myEditor;

	private String _myName = "S57";

	private S57Database _myDatabase;

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	private File _sourceFile = null;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public S57Layer() {
		// make it visible to start with
		setVisible(true);
	}

	@Override
	public void add(final Editable point) {
		System.err.println("you can't add items to an S57 layer");
	}

	@Override
	public void append(final Layer other) {
	}

	@Override
	public int compareTo(final Plottable arg0) {
		final Plottable other = arg0;
		final String myName = this.getName() + this.hashCode();
		final String hisName = other.getName() + arg0.hashCode();
		return myName.compareTo(hisName);
	}

	@Override
	public Enumeration<Editable> elements() {
		IteratorWrapper res = null;
		if (_myDatabase != null)
			res = new IteratorWrapper(_myDatabase.getFeatures().iterator());
		return res;
	}

	@Override
	public void exportShape() {
	}

	@Override
	public MWC.GenericData.WorldArea getBounds() {
		// doesn't return a sensible size
		return null;
	}

	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new S57PainterInfo(this);

		return _myEditor;
	}

	@Override
	public int getLineThickness() {
		return 0;
	}

	@Override
	public String getName() {
		return _myName;
	}

	/**
	 * @return the _sourceFile
	 */
	public final String getSourceFile() {
		String res = null;
		if (_sourceFile != null) {
			res = _sourceFile.getPath();
		}
		return res;
	}

	@Override
	public boolean getVisible() {
		return _isOn;
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	@Override
	public boolean hasOrderedChildren() {
		return false;
	}

	protected void load(final String name) {
		_myDatabase = new S57Database();
		_myDatabase.loadDatabase(name, false);
	}

	@Override
	public void paint(final CanvasType g) {

		// check we are visible
		if (!_isOn)
			return;

		if (_myDatabase == null) {
			// do we have a name yet
			if (_sourceFile != null) {
				// ok, does it exist?
				if (_sourceFile.exists()) {
					load(_sourceFile.getAbsolutePath());
					// _myDatabase = new S57Database();
					// _myDatabase.loadDatabase("d://dev/AU411141.000", true);
					// _myDatabase.loadDatabase(_sourceFile, true);
				}
			}
		}

		// just double-check things went ok
		if (_myDatabase == null) {
			System.err.println("Not painting S57, data not present");
			return;

		}

		g.setColor(Color.orange);
		// g.setColor(Color.cyan);
		// g.drawLine(222, 133, 244, 355);

		final Enumeration<Editable> theFeatures = elements();
		while (theFeatures.hasMoreElements()) {
			final S57Feature nextF = (S57Feature) theFeatures.nextElement();
			nextF.paint(g);
		}

		// give us the area aswell
		final WorldArea area = _myDatabase.getArea();
		final Point s1 = g.toScreen(area.getTopLeft());
		// handle unable to gen screen coords (if off visible area)
		if (s1 == null)
			return;

		final Point p1 = new Point(s1);
		final Point p2 = new Point(g.toScreen(area.getBottomRight()));
		g.setColor(Color.white);
		g.drawRect(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
	}

	@Override
	public double rangeFrom(final MWC.GenericData.WorldLocation other) {
		// doesn't return a sensible distance;
		return INVALID_RANGE;
	}

	@Override
	public void removeElement(final Editable point) {
		System.err.println("you can't remove items from an S57 layer");
	}

	@Override
	public void setName(final String val) {
		_myName = val;
	}

	/**
	 * @param file the _sourceFile to set
	 */
	public final void setSourceFile(final String file) {
		_sourceFile = new java.io.File(file);
	}

	@Override
	public void setVisible(final boolean val) {
		_isOn = val;
	}

	/**
	 * return this item as a string
	 */
	@Override
	public String toString() {
		return getName();
	}
}
