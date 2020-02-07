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

package org.mwc.debrief.satc_interface.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.mwc.cmap.core.wizards.CoreEditableWizardPage;

import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class CourseConstraintsWizardPage extends CoreEditableWizardPage {

	public static class CourseConstraintsObject implements Plottable {
		private final CourseForecastContribution course;

		private boolean hasEstimate;

		public CourseConstraintsObject(final CourseForecastContribution course) {
			this.course = course;
		}

		@Override
		public int compareTo(final Plottable arg0) {
			return 0;
		}

		@Override
		public WorldArea getBounds() {
			return null;
		}

		public CourseForecastContribution getContribution() {
			return course;
		}

		public double getEstimate() {
			final Double theEstimate = course.getEstimate();
			if (theEstimate != null)
				return Math.toDegrees(theEstimate);
			else
				return 0;
		}

		@Override
		public EditorType getInfo() {
			return null;
		}

		public double getMaxCourse() {
			return (int) Math.toDegrees(course.getMaxCourse());
		}

		public double getMinCourse() {
			return (int) Math.toDegrees(course.getMinCourse());
		}

		@Override
		public String getName() {
			return course.getName();
		}

		@Override
		public boolean getVisible() {
			return false;
		}

		@Override
		public boolean hasEditor() {
			return false;
		}

		public boolean isHasEstimate() {
			return hasEstimate;
		}

		@Override
		public void paint(final CanvasType dest) {

		}

		@Override
		public double rangeFrom(final WorldLocation other) {
			return 0;
		}

		public void setEstimate(final double estimate) {
			course.setEstimate(Math.toRadians(estimate));
		}

		public void setHasEstimate(final boolean hasEstimate) {
			this.hasEstimate = hasEstimate;
		}

		public void setMaxCourse(final double maxCourse) {
			course.setMaxCourse(Math.toRadians(maxCourse));
		}

		public void setMinCourse(final double minCourse) {
			course.setMinCourse(Math.toRadians(minCourse));
		}

		public void setName(final String name) {
			course.setName(name);
		}

		@Override
		public void setVisible(final boolean val) {

		}

	}

	private final CourseForecastContribution course;

	/**
	 * Constructor for SampleNewWizardPage.
	 *
	 * @param pageName
	 */
	public CourseConstraintsWizardPage(final ISelection selection, final CourseForecastContribution course) {
		super(selection, "coursePage", "Add Course Constraints",
				"If you wish to provide a course constraint for this straight leg, specify it below",
				"images/scale_wizard.gif", null);
		this.course = course;

	}

	@Override
	protected Editable createMe() {
		if (_editable == null) {
			final CourseConstraintsObject theCourse = new CourseConstraintsObject(course);
			theCourse.setName("Course Forecast");
			_editable = theCourse;
		}

		return _editable;
	}

	/**
	 * @return
	 */
	@Override
	protected PropertyDescriptor[] getPropertyDescriptors() {
		final PropertyDescriptor[] descriptors = { prop("MinCourse", "the minimum course", getEditable()),
				prop("MaxCourse", "the maximum course", getEditable()),
				prop("Name", "the name of this contribution", getEditable()),
				prop("HasEstimate", "whether to use an estimate", getEditable()),
				prop("Estimate", "the estimate", getEditable()) };
		return descriptors;
	}

}