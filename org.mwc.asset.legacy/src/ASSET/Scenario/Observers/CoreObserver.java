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

package ASSET.Scenario.Observers;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

import ASSET.ScenarioType;
import MWC.Algorithms.LiveData.Attribute;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.GeneralFormat;

abstract public class CoreObserver implements ScenarioObserver, Editable {
	/**
	 * get a string recording the current date and version of ASSET
	 *
	 * @return string
	 */
	public static String getHeaderInfo() {
		String res = "ASSET Version:" + ASSET.GUI.VersionInfo.getVersion() + GeneralFormat.LINE_SEPARATOR;
		res += "File saved:" + MWC.Utilities.TextFormatting.FullFormatDateTime.toString(new Date().getTime())
				+ GeneralFormat.LINE_SEPARATOR;
		return res;
	}

	/***************************************************************
	 * member variables
	 ***************************************************************/
	/**
	 * the name of this observer
	 */
	private String _myName = "Un-named observer";

	/**
	 * whether this observer is currently active
	 */
	private boolean _isActive = true;

	/**
	 * remember the scenario
	 */
	protected ScenarioType _myScenario;

	/**
	 * the editor for this data type
	 */
	protected Editable.EditorType _myEditor = null;

	/**
	 * our property support
	 *
	 */
	protected PropertyChangeSupport _pSupport;

	/**
	 * attribute helper support, just in case we want it
	 *
	 */
	private Attribute.AttributeHelper _myAttributeHelper;

	/**
	 * our multi scenario observer helper
	 *
	 */
	private BatchListenerHelper _batchListener = null;

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */

	public CoreObserver(final String myName, final boolean isActive) {
		_myName = myName;
		_isActive = isActive;

		// create the prop support, mostly used for the IAttribute listen-able stats
		// hierarchy
		_pSupport = new PropertyChangeSupport(this);
	}

	/**
	 * add any applicable listeners
	 *
	 * @param scenario the scenario to listen to
	 */
	abstract protected void addListeners(ScenarioType scenario);

	/**
	 * somebody cares about us, aaah
	 *
	 * @param listener that loving soul
	 */
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		_pSupport.addPropertyChangeListener(listener);
	}

	/**
	 * do the comparison
	 *
	 * @param arg0
	 * @return
	 */
	@Override
	public int compareTo(final Plottable arg0) {
		final CoreObserver other = (CoreObserver) arg0;
		return getName().compareTo(other.getName());
	}

	/**
	 * convenience class, largely helping with attribute watchers
	 *
	 */
	protected Attribute.AttributeHelper getAttributeHelper() {
		if (_myAttributeHelper == null)
			_myAttributeHelper = new Attribute.AttributeHelper(_pSupport);

		return _myAttributeHelper;
	}

	/**
	 * find the data area occupied by this item
	 */
	@Override
	public WorldArea getBounds() {
		return null;
	}

	protected BatchListenerHelper getListenerHelper() {
		if (_batchListener == null)
			_batchListener = new BatchListenerHelper();

		return _batchListener;
	}

	/**
	 * get the name of this observer
	 */
	@Override
	public String getName() {
		return _myName;
	}

	/**
	 * it this item currently visible?
	 */
	@Override
	public boolean getVisible() {
		return isActive();
	}

	/**
	 * whether this observer is currently active
	 */
	@Override
	public boolean isActive() {
		return _isActive;
	}

	/**
	 * paint this object to the specified canvas
	 */
	@Override
	public void paint(final CanvasType dest) {
	}

	/**
	 * right, the scenario is about to close. We haven't removed the listeners or
	 * forgotten the scenario (yet).
	 *
	 * @param scenario the scenario we're closing from
	 */
	abstract protected void performCloseProcessing(ScenarioType scenario);

	/**
	 * we're getting up and running. The observers have been created and we've
	 * remembered the scenario
	 *
	 * @param scenario the new scenario we're looking at
	 */
	abstract protected void performSetupProcessing(ScenarioType scenario);

	/**
	 * Determine how far away we are from this point. or return INVALID_RANGE if it
	 * can't be calculated
	 */
	@Override
	public double rangeFrom(final WorldLocation other) {
		return 0;
	}

	/**
	 * remove any listeners
	 *
	 * @param scenario the scenario to stop listening to
	 */
	abstract protected void removeListeners(ScenarioType scenario);

	/**
	 * somebody doesn't care about us
	 *
	 * @param listener not worth mentioning
	 */
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		_pSupport.removePropertyChangeListener(listener);
	}

	public void restart(final ScenarioType scenario) {
		// remember the scenario - since we will probably forget it when we teardown
		final ScenarioType tmpScen = _myScenario;

		// mark end of processing
		tearDown(tmpScen);

		// set ourselves up again
		setup(tmpScen);
	}

	/**
	 * whether this observer is currently active
	 */
	@Override
	public void setActive(final boolean active) {
		this._isActive = active;
	}

	/**********************************************************************
	 * editable parameters
	 *********************************************************************/

	/**
	 * set the name of this observer
	 */
	@Override
	public void setName(final String val) {
		_myName = val;
	}

	/***************************************************************
	 * scenario parameters
	 ***************************************************************/
	/**
	 * configure observer to listen to this scenario
	 */
	@Override
	final public void setup(final ScenarioType scenario) {
		if (isActive()) {
			_myScenario = scenario;

			// also add any listeners we're interested in
			addListeners(scenario);

			// and the specific processing for this type
			performSetupProcessing(scenario);
		}
	}

	/**
	 * set the visibility of this item
	 */
	@Override
	public void setVisible(final boolean val) {
		setActive(val);
	}

	/**
	 * inform observer that scenario is complete, to remove listeners, etc It's ok
	 * to clear the score (if applicable) since it will have been retrieved before
	 * this point
	 */
	@Override
	final public void tearDown(final ScenarioType scenario) {
		if (isActive()) {
			performCloseProcessing(scenario);

			// ok, remove any listeners
			removeListeners(scenario);
		}
	}

	/**
	 * get the string
	 */
	@Override
	public String toString() {
		return getName();
	}
}
