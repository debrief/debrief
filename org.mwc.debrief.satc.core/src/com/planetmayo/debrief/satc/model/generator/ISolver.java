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

package com.planetmayo.debrief.satc.model.generator;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.mwc.debrief.track_shift.zig_detector.Precision;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

/**
 *
 * The main interface to solve "Semi-Automatic Track Construction" problem.
 * Contains four parts: * IContributions - manages contributions which are used
 * in problem * IBoundsManager - builds and constraints problem space *
 * ISolutionGenerators - generates solutions based on problem space *
 * IJobsManager - technical stuff, allows to run computations in multiple
 * threads
 * 
 * Handles interaction between parts
 */
public interface ISolver {
	public interface Reader {
		List<BaseContribution> readContributions();

		Precision readPrecision();

		VehicleType readVehicleType();
	}

	public interface Writer {
		void writeContributions(List<BaseContribution> contributions);

		void writePrecision(Precision precision);

		void writeVehicleType(VehicleType vehicleType);
	}

	static final String LIVE_RUNNING = "liveRunning";
	static final String NAME = "name";
	static final String PRECISION = "precision";

	static final String AUTO_SUPPRESS = "suppress";

	static final String VEHICLE_TYPE = "vehicleType";

	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * cancels generation job
	 */
	void cancel();

	/**
	 * does full cleaning of parameters of the problem 1. removes all contributions
	 * 2. cleans problem space 3. restarts bounds manager and solution generator
	 */
	void clear();

	/**
	 * whether insignificant cuts should be suppressed (only in mid-low)
	 *
	 * @return yes/no
	 */
	boolean getAutoSuppress();

	/**
	 * returns bounds manager
	 */
	IBoundsManager getBoundsManager();

	/**
	 * returns contributions manager associated with solver
	 */
	IContributions getContributions();

	/**
	 * @returns solver name
	 */
	String getName();

	Precision getPrecision();

	/**
	 * get problem space
	 */
	SafeProblemSpace getProblemSpace();

	/**
	 * returns solution generator associated with solver
	 */
	ISolutionGenerator getSolutionGenerator();

	/**
	 * returns vehicle type which is used in computations
	 */
	VehicleType getVehicleType();

	/**
	 * indicate whether we do 'run' after each contribution change
	 * 
	 * @return
	 */
	boolean isLiveRunning();

	void load(Reader reader);

	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * solves the problem with specified parameters
	 */
	void run(boolean constraint, boolean generate);

	void save(Writer writer);

	/**
	 * whether insignificant cuts should be suppressed (only in mid-low)
	 * 
	 * @param autoSuppress yes/no
	 */
	void setAutoSuppress(boolean autoSuppress);

	/**
	 * specify whether we should do a 'run' after each contribution change
	 * 
	 * @param checked
	 */
	void setLiveRunning(boolean checked);

	/**
	 * 
	 * @param name the new solver name
	 */
	void setName(String name);

	void setPrecision(Precision precision);

	/**
	 * sets vehicle type which is used in computations
	 */
	void setVehicleType(VehicleType type);

}
