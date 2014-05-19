package org.mwc.debrief.satc_interface.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.mwc.cmap.core.wizards.CoreEditableWizardPage;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class CourseConstraintsWizardPage extends CoreEditableWizardPage
{

	public static class CourseConstraintsObject implements Plottable
	{
		private CourseForecastContribution course;

		private boolean hasEstimate;

		public CourseForecastContribution getContribution()
		{
			return course;
		}

		public CourseConstraintsObject(CourseForecastContribution course)
		{
			this.course = course;
		}

		public double getMinCourse()
		{
			return (int) Math.toDegrees(course.getMinCourse());
		}

		public void setMinCourse(double minCourse)
		{
			course.setMinCourse(Math.toRadians(minCourse));
		}

		public double getMaxCourse()
		{
			return (int) Math.toDegrees(course.getMaxCourse());
		}

		public void setMaxCourse(double maxCourse)
		{
			course.setMaxCourse(Math.toRadians(maxCourse));
		}

		public boolean isHasEstimate()
		{
			return hasEstimate;
		}

		public void setHasEstimate(boolean hasEstimate)
		{
			this.hasEstimate = hasEstimate;
		}

		public double getEstimate()
		{
			Double theEstimate = course.getEstimate();
			if (theEstimate != null)
				return Math.toDegrees(theEstimate);
			else
				return 0;
		}

		public void setEstimate(double estimate)
		{
			course.setEstimate(Math.toRadians(estimate));
		}

		@Override
		public String getName()
		{
			return course.getName();
		}

		public void setName(String name)
		{
			course.setName(name);
		}

		@Override
		public boolean hasEditor()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public EditorType getInfo()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int compareTo(Plottable arg0)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void paint(CanvasType dest)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public WorldArea getBounds()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean getVisible()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setVisible(boolean val)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public double rangeFrom(WorldLocation other)
		{
			// TODO Auto-generated method stub
			return 0;
		}

	}

	private final CourseForecastContribution course;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public CourseConstraintsWizardPage(final ISelection selection,
			CourseForecastContribution course)
	{
		super(
				selection,
				"coursePage",
				"Add Course Constraints",
				"If you wish to provide a course constraint for this straight leg, specify it below",
				"images/scale_wizard.gif", null);
		this.course = course;

	}

	@Override
	protected Editable createMe()
	{
		if (_editable == null)
		{
			CourseConstraintsObject theCourse = new CourseConstraintsObject(course);
			theCourse.setName("Course Forecast");
			_editable = theCourse;
		}

		return _editable;
	}

	/**
	 * @return
	 */
	@Override
	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		final PropertyDescriptor[] descriptors =
		{ prop("MinCourse", "the minimum course", getEditable()),
				prop("MaxCourse", "the maximum course", getEditable()),
				prop("Name", "the name of this contribution", getEditable()),
				prop("HasEstimate", "whether to use an estimate", getEditable()),
				prop("Estimate", "the estimate", getEditable()) };
		return descriptors;
	}

}