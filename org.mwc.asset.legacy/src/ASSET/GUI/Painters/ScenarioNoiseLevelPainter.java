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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Vector;

import ASSET.ScenarioType;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Sensor.Initial.InitialSensor;
import ASSET.Scenario.ScenarioSteppedListener;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * ************************************************************** painter
 * support **************************************************************
 */
public class ScenarioNoiseLevelPainter extends SpatialRasterPainter implements NoiseSource, ScenarioSteppedListener {

	/**
	 * class to store the combination of source level and location
	 */
	public static class ParticipantStatus {
		public double sourceLevel;
		public WorldLocation location;
	}

	/////////////////////////////////////////////////////////////
	// info class
	////////////////////////////////////////////////////////////
	public class ScenarioNoiseInfo extends MWC.GUI.Editable.EditorType implements java.io.Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public ScenarioNoiseInfo(final ScenarioNoiseLevelPainter data) {
			super(data, data.getName(), "Edit");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "name of this painter"),
						prop("Visible", "whether this layer is visible"),
						prop("BathyRes", "the resolution of the grid plotted"),
						prop("LiveUpdate", "update after each scenario change"),
						prop("BackgroundNoise", "the background noise level in this environemnt"), };
				return res;
			} catch (final IntrospectionException e) {
				System.err.println("Problem declaring editable properties:" + e.getMessage());
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * ************************************************* interface for classes which
	 * can provide the list of statuses we plot
	 * *************************************************
	 */
	public static interface StatusProvider {
		/**
		 * listen out for status changes
		 */
		public void addScenarioChangeListener(ASSET.Scenario.ScenarioSteppedListener listener);

		/**
		 * return a list of ParticipantStatus objects
		 */
		public Vector<ParticipantStatus> getStatuses(int theMedium);

		/**
		 * stop listening for status changes
		 */
		public void removeScenarioChangeListener(ASSET.Scenario.ScenarioSteppedListener listener);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the environment we are calculating for
	 */
	private EnvironmentType _myEnvironment;

	/**
	 * the medium we are painting
	 */
	private int _myMedium;

	/**
	 * the colour to paint the key
	 */
	protected final Color _myColor = Color.darkGray;

	/**
	 * somebody to tell us where the participants are
	 */
	private StatusProvider _provider = null;

	/**
	 * our editor
	 */
	private transient ScenarioNoiseInfo _myEditor = null;

	/**
	 * whether we should re-plot after each scenario movement
	 */
	private boolean _liveUpdate = false;

	/**
	 * the set of layers we are being painted into
	 */
	private Layers _theLayers = null;

	/**
	 * background noise level in this environment
	 */
	private double _BackgroundNoiseLevel = 65;

	/**
	 * ************************************************* * constructor
	 * **************************************************
	 */

	public ScenarioNoiseLevelPainter(final EnvironmentType theEnv, final StatusProvider provider, final int medium,
			final Layers theLayers) {
		super("Acoustic Painter");
		new PainterComponent() {

			@Override
			protected void assignPixel(final int width, final int thisValue, final int x_coord, final int y_coord) {

			}

			@Override
			protected void checkImageValid(final int width, final int height) {

			}

			@Override
			public int convertColor(final int red, final int green, final int blue) {
				return 0;
			}

			@Override
			protected void paintTheImage(final CanvasType dest, final int width, final int height, final int alpha) {

			}

			@Override
			protected void updatePixelColors(final SpatialRasterPainter parent, final int width, final int height,
					final int min_height, final int max_height, final CanvasType dest, final boolean useNE) {

			}
		};
		super.setBuffered(true);
		super.setVisible(false);
		_myEnvironment = theEnv;
		_provider = provider;
		_myMedium = medium;
		_theLayers = theLayers;

		// switch off contours
		super.setContoursVisible(false);
	}

	public BoundedInteger getBackgroundNoise() {
		return new BoundedInteger((int) _BackgroundNoiseLevel, 0, 150);
	}

	@Override
	public WorldArea getBounds() {
		final WorldArea res = null;
		return res;
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
			_myEditor = new ScenarioNoiseInfo(this);
		}
		return _myEditor;
	}

	public boolean getLiveUpdate() {
		return _liveUpdate;
	}

	@Override
	public int getValueAt(final WorldLocation location) {
		double res = 0;
		final Vector<ParticipantStatus> stats = _provider.getStatuses(_myMedium);

		if (stats != null) {
			final Iterator<ParticipantStatus> it = stats.iterator();
			while (it.hasNext()) {
				final ParticipantStatus ps = it.next();
				final WorldLocation origin = ps.location;
				final double sourceLevel = ps.sourceLevel;

				// find out the resultant noise at this point
				final double resultant = _myEnvironment.getResultantEnergyAt(_myMedium, origin, location, sourceLevel);

				// build a cumulative total
				res = InitialSensor.SensorUtils.powerSum(resultant, res);
			}
		}

		// finally add in the background noise level
		res = InitialSensor.SensorUtils.powerSum(_BackgroundNoiseLevel, res);

		return (int) res;
	}

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

	/**
	 * ************************************************* * member variables
	 * **************************************************
	 */

	@Override
	public void paint(final CanvasType dest) {
		final Vector<ParticipantStatus> statuses = _provider.getStatuses(_myMedium);

		// did we find any?
		if (statuses.size() > 0)
			super.paint(dest);
	}

	/**
	 * the scenario has restarted, reset
	 */
	@Override
	public void restart(final ScenarioType scenario) {
	}

	public void setBackgroundNoise(final BoundedInteger backgroundNoiseLevel) {
		this._BackgroundNoiseLevel = backgroundNoiseLevel.getCurrent();
	}

	public void setLiveUpdate(final boolean liveUpdate) {
		this._liveUpdate = liveUpdate;

		// listen to parent as necessary
		if (liveUpdate)
			_provider.addScenarioChangeListener(this);
		else
			_provider.removeScenarioChangeListener(this);
	}

	/**
	 * the scenario has stepped forward
	 */
	@Override
	public void step(final ScenarioType scenario, final long newTime) {
		// somehow, we have to update ourselves...
		_theLayers.fireModified(this);
	}

}