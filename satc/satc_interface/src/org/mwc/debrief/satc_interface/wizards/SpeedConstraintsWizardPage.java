package org.mwc.debrief.satc_interface.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.mwc.cmap.core.wizards.CoreEditableWizardPage;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class SpeedConstraintsWizardPage extends CoreEditableWizardPage
{

	public static class SpeedConstraintsObject implements Plottable
	{
		private SpeedForecastContribution course;

		private boolean hasEstimate;

		public SpeedConstraintsObject(SpeedForecastContribution course)
		{
			this.course = course;
		}

		public WorldSpeed getMinSpeed()
		{
			return new WorldSpeed(course.getMinSpeed(), WorldSpeed.M_sec);
		}

		public void setMinSpeed(WorldSpeed speed)
		{
			course.setMinSpeed(speed.getValueIn(WorldSpeed.M_sec));
		}

		public WorldSpeed getMaxSpeed()
		{
			return new WorldSpeed(course.getMaxSpeed(), WorldSpeed.M_sec);
		}

		public void setMaxSpeed(WorldSpeed speed)
		{
			course.setMinSpeed(speed.getValueIn(WorldSpeed.M_sec));
		}

		public boolean isHasEstimate()
		{
			return hasEstimate;
		}

		public void setHasEstimate(boolean hasEstimate)
		{
			this.hasEstimate = hasEstimate;
		}

		public WorldSpeed getEstimate()
		{
			Double theEstimate = course.getEstimate();
			if (theEstimate != null)
				return new WorldSpeed(theEstimate, WorldSpeed.M_sec);
			else
				return new WorldSpeed(0, WorldSpeed.M_sec);
		}

		public void setEstimate(WorldSpeed estimate)
		{
			course.setEstimate(estimate.getValueIn(WorldSpeed.M_sec));
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
			return false;
		}

		@Override
		public EditorType getInfo()
		{
			return null;
		}

		@Override
		public int compareTo(Plottable arg0)
		{
			return 0;
		}

		@Override
		public void paint(CanvasType dest)
		{
		}

		@Override
		public WorldArea getBounds()
		{
			return null;
		}

		@Override
		public boolean getVisible()
		{
			return false;
		}

		@Override
		public void setVisible(boolean val)
		{
		}

		@Override
		public double rangeFrom(WorldLocation other)
		{
			return 0;
		}

		public SpeedForecastContribution getContribution()
		{
			return course;
		}

	}

	private final SpeedForecastContribution speed;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public SpeedConstraintsWizardPage(final ISelection selection,
			SpeedForecastContribution speed)
	{
		super(selection, "coursePage", "Add Course Constraints",
				"Use this page to specify course constraints",
				"images/scale_wizard.gif", null);
		this.speed = speed;
	}

	@Override
	protected Editable createMe()
	{
		if (_editable == null)
		{
			SpeedConstraintsObject theSpeed = new SpeedConstraintsObject(speed);
			theSpeed.setName("Speed estimate");
			theSpeed.setMaxSpeed(new WorldSpeed(2, WorldSpeed.Kts));
			theSpeed.setMinSpeed(new WorldSpeed(20, WorldSpeed.Kts));
			theSpeed.setEstimate(new WorldSpeed(10, WorldSpeed.Kts));
			_editable = theSpeed;
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
		{ prop("MinSpeed", "the minimum course", getEditable()),
				prop("MaxSpeed", "the maximum course", getEditable()),
				prop("Name", "the name for this contribution", getEditable()),
				prop("HasEstimate", "whether to use an estimate", getEditable()),
				prop("Estimate", "the estimate", getEditable()) };
		return descriptors;
	}

}