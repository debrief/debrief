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

package ASSET.GUI.Painters;

import java.awt.Color;
import java.awt.Point;

import ASSET.Models.Environment.EnvironmentType;
import ASSET.Util.SupportTesting;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldLocation;

public class NoiseSourcePainter extends SpatialRasterPainter implements NoiseSource {
	/////////////////////////////////////////////////////////////
	// info class
	////////////////////////////////////////////////////////////
	public class NoiseInfo extends Editable.EditorType implements java.io.Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public NoiseInfo(final NoiseSourcePainter data) {
			super(data, data.getName(), "Edit");
		}

		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Visible", "whether this layer is visible"),
						prop("Origin", "the origin of the noise source"), prop("Name", "the name of this noise source"),
						prop("SourceLevel", "the source level of the noise source"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	//////////////////////////////////////////////////
	// add testing code
	//////////////////////////////////////////////////
	public static class PainterTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new NoiseSourcePainter(null, EnvironmentType.BROADBAND_PASSIVE);
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	///////////////////////////////////////////////////
	// member variables
	///////////////////////////////////////////////////
	/**
	 * origin for this noise source
	 */
	private WorldLocation _myOrigin = new WorldLocation(0, 0, 0);

	/**
	 * noise level for this noise source
	 */
	private double _sourceLevel = 130;

	/**
	 * our editor
	 */
	private transient NoiseInfo _myEditor = null;

	/**
	 * our environment
	 */
	private EnvironmentType _myEnvironment = null;

	/**
	 * the medium we plot
	 */
	private int _myMedium = 1;

	///////////////////////////////////////////////////
	// member methods
	///////////////////////////////////////////////////

	///////////////////////////////////////////////////
	// constructor
	///////////////////////////////////////////////////
	NoiseSourcePainter(final EnvironmentType theEnv, final int medium) {
		super("Noise Source Painter");
		_myEnvironment = theEnv;
		_myMedium = medium;
	}

	public NoiseSourcePainter(final WorldLocation origin, final double sourceLevel, final EnvironmentType theEnv,
			final int medium) {
		this(theEnv, medium);
		_myOrigin = origin;
		_sourceLevel = sourceLevel;
	}

	/**
	 * provide the delta for the data (in degrees)
	 */
	@Override
	public double getGridDelta() {
		return MWC.Algorithms.Conversions.Nm2Degs(5);
	}

	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null) {
			_myEditor = new NoiseInfo(this);
		}
		return _myEditor;
	}

	public WorldLocation getOrigin() {
		return _myOrigin;
	}

	public BoundedInteger getSourceLevel() {
		return new BoundedInteger((int) _sourceLevel, 0, 250);
	}

	@Override
	public int getValueAt(final WorldLocation location) {
		// work out the noise dissipation from this origin
		int res = 0;
		res = (int) _myEnvironment.getResultantEnergyAt(_myMedium, _myOrigin, location, _sourceLevel);
		return res;
	}

	///////////////////////////////////////////////////
	// editor support
	///////////////////////////////////////////////////
	@Override
	public boolean hasEditor() {
		return true;
	}

	/**
	 * whether the data has been loaded yet
	 */
	@Override
	public boolean isDataLoaded() {
		return true;
	}

	@Override
	public void paint(final CanvasType dest) {
		super.paint(dest);

		// just put a cross at the origin
		final Point pt = dest.toScreen(_myOrigin);
		dest.setColor(Color.white);
		dest.drawLine(pt.x, pt.y - 1, pt.x, pt.y + 1);
		dest.drawLine(pt.x - 1, pt.y, pt.x + 1, pt.y);
	}

	public void setOrigin(final WorldLocation origin) {
		this._myOrigin = origin;
	}

	public void setSourceLevel(final BoundedInteger sourceLevel) {
		this._sourceLevel = sourceLevel.getCurrent();
	}

}
