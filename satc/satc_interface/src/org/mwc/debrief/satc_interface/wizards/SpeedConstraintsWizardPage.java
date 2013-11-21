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
import com.planetmayo.debrief.satc.util.GeoSupport;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class SpeedConstraintsWizardPage extends CoreEditableWizardPage
{

	public static class SpeedConstraintsObject implements Plottable
	{
		private final SpeedForecastContribution _speed;

		private boolean hasEstimate;

		public SpeedConstraintsObject(SpeedForecastContribution speed)
		{
			this._speed = speed;
		}

	  
		
		public WorldSpeed getMinSpeed()
		{
			return new WorldSpeed(GeoSupport.MSec2kts( _speed.getMinSpeed()), WorldSpeed.Kts);
		}

		public void setMinSpeed(WorldSpeed speed)
		{
			_speed.setMinSpeed(speed.getValueIn(WorldSpeed.M_sec));
		}

		public WorldSpeed getMaxSpeed()
		{
			return new WorldSpeed(GeoSupport.MSec2kts(_speed.getMaxSpeed()), WorldSpeed.Kts);
		}

		public void setMaxSpeed(WorldSpeed speed)
		{
			_speed.setMaxSpeed(speed.getValueIn(WorldSpeed.M_sec));
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
			Double theEstimate = _speed.getEstimate();
			if (theEstimate != null)
				return new WorldSpeed(GeoSupport.MSec2kts(theEstimate), WorldSpeed.Kts);
			else
				return new WorldSpeed(0, WorldSpeed.Kts);
		}

		public void setEstimate(WorldSpeed estimate)
		{
			_speed.setEstimate(estimate.getValueIn(WorldSpeed.M_sec));
		}

		@Override
		public String getName()
		{
			return _speed.getName();
		}

		public void setName(String name)
		{
			_speed.setName(name);
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
			return _speed;
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
		super(selection, "speedPage", "Add Speed Constraints",
				"If you wish to provide a speed constraint for this straight leg, specify it below",
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
			theSpeed.setMinSpeed(new WorldSpeed(2, WorldSpeed.Kts));
			theSpeed.setMaxSpeed(new WorldSpeed(20, WorldSpeed.Kts));
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
		{ prop("MinSpeed", "the minimum speed", getEditable()),
				prop("MaxSpeed", "the maximum speed", getEditable()),
				prop("Name", "the name for this contribution", getEditable()),
				prop("HasEstimate", "whether to use an estimate", getEditable()),
				prop("Estimate", "the estimate of speed", getEditable()) };
		return descriptors;
	}

}