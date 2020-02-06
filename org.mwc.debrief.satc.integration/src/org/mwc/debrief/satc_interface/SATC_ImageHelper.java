
package org.mwc.debrief.satc_interface;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider.ViewLabelImageHelper;
import org.mwc.debrief.satc_interface.data.SATC_Solution;
import org.mwc.debrief.satc_interface.data.wrappers.BMC_Wrapper;
import org.mwc.debrief.satc_interface.data.wrappers.ContributionWrapper;
import org.mwc.debrief.satc_interface.data.wrappers.FMC_Wrapper;
import org.mwc.debrief.satc_interface.data.wrappers.StraightLegWrapper;

import MWC.GUI.Editable;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.Range1959ForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;

public class SATC_ImageHelper implements ViewLabelImageHelper
{

	public ImageDescriptor getImageFor(final Editable editable)
	{
		ImageDescriptor res = null;

		if (editable instanceof SATC_Solution)
			res = SATC_Interface_Activator.getImageDescriptor("icons/16/scenario.png");
		else if (editable instanceof StraightLegWrapper)
			res = SATC_Interface_Activator.getImageDescriptor("icons/16/leg.png");
		else if (editable instanceof BMC_Wrapper.BearingMeasurementWrapper)
			res = SATC_Interface_Activator.getImageDescriptor("icons/16/bearing.png");
		else if (editable instanceof FMC_Wrapper.FrequencyMeasurementEditable)
			res = SATC_Interface_Activator.getImageDescriptor("icons/16/frequency.png");
		else if (editable instanceof BMC_Wrapper)
			res = SATC_Interface_Activator.getImageDescriptor("icons/16/bearing.png");
		else if (editable instanceof FMC_Wrapper)
			res = SATC_Interface_Activator.getImageDescriptor("icons/16/frequency.png");
		else if (editable instanceof StraightLegWrapper)
			res = SATC_Interface_Activator.getImageDescriptor("icons/16/leg.png");
		else if (editable instanceof ContributionWrapper)
		{
			ContributionWrapper cw = (ContributionWrapper) editable;
			BaseContribution cont = cw.getContribution();
			if (cont instanceof CourseForecastContribution)
				res = SATC_Interface_Activator
						.getImageDescriptor("icons/16/direction.png");
			else if (cont instanceof SpeedForecastContribution)
				res = SATC_Interface_Activator.getImageDescriptor("icons/16/speed.png");
			else if (cont instanceof RangeForecastContribution)
				res = SATC_Interface_Activator.getImageDescriptor("icons/16/range.png");
			else if (cont instanceof Range1959ForecastContribution)
				res = SATC_Interface_Activator.getImageDescriptor("icons/16/range.png");
		}
		return res;
	}

}
