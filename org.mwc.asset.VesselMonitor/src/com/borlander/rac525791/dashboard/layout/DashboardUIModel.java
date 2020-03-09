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

package com.borlander.rac525791.dashboard.layout;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.borlander.rac525791.dashboard.layout.data.SuiteImpl;

public class DashboardUIModel {
	private final List<ControlUISuite> mySuites;

	public DashboardUIModel() {
		this(SuiteImpl.create280x160(), SuiteImpl.create320x183(), SuiteImpl.create360x206(),
				SuiteImpl.create400x229());
	}

	public DashboardUIModel(final ControlUISuite... possibleSizes) {
		mySuites = new LinkedList<ControlUISuite>();
		for (final ControlUISuite next : possibleSizes) {
			mySuites.add(next);
		}
		Collections.sort(mySuites, new Comparator<ControlUISuite>() {
			@Override
			public int compare(final ControlUISuite s1, final ControlUISuite s2) {
				return s1.getPreferredSizeRO().width - s2.getPreferredSizeRO().width;
			}
		});
	}

	public void dispose() {
		for (final ControlUISuite next : mySuites) {
			next.dispose();
		}
		// next invocation of any method will fail.
		mySuites.clear();

	}

	public ControlUISuite getUISuite(final int actualWidth, final int actualHeight) {
		ControlUISuite bestSuite = null;
		for (final ControlUISuite next : mySuites) {
			if (bestSuite == null) {
				bestSuite = next;
			}
			final int nextWidth = next.getPreferredSizeRO().width;
			final int nextHeight = next.getPreferredSizeRO().height;
			if (nextWidth > actualWidth || nextHeight > actualHeight) {
				break;
			}
			bestSuite = next;
		}
		return bestSuite;
	}

}
