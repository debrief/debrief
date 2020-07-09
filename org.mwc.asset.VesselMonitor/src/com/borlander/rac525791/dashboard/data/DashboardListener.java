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

package com.borlander.rac525791.dashboard.data;

public interface DashboardListener {

	public static final DashboardListener NULL = new DashboardListener() {

		@Override
		public void actualDepthChanged() {
			//
		}

		@Override
		public void actualDirectionChanged() {
			//
		}

		@Override
		public void actualSpeedChanged() {
			//
		}

		@Override
		public void demandedDepthChanged() {
			//
		}

		@Override
		public void demandedDirectionChanged() {
			//
		}

		@Override
		public void demandedSpeedChanged() {
			//
		}

		@Override
		public void depthUnitsChanged() {
			//
		}

		@Override
		public void nameChanged() {
			//
		}

		@Override
		public void speedUnitsChanged() {
			//
		}

		@Override
		public void statusChanged() {
			//
		}

	};

	void actualDepthChanged();

	void actualDirectionChanged();

	void actualSpeedChanged();

	void demandedDepthChanged();

	void demandedDirectionChanged();

	void demandedSpeedChanged();

	void depthUnitsChanged();

	void nameChanged();

	void speedUnitsChanged();

	void statusChanged();

}
