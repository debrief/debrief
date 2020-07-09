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
package org.mwc.cmap.xyplot.wizards;

import java.text.DecimalFormat;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.ui_support.wizards.SimplePageListWizard;
import org.mwc.cmap.core.wizards.EnterStringPage;

import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationWizard;
import Debrief.Tools.Tote.Calculations.dopplerCalc;
import MWC.Algorithms.Conversions;
import MWC.GUI.Editable;
import MWC.GenericData.WatchableList;
import MWC.Tools.Tote.toteCalculation;

public class DopplerPlotWizard implements CalculationWizard {

	@Override
	public int open(final toteCalculation pCalc, final WatchableList primary, final Editable[] subjects) {
		final dopplerCalc calc = (dopplerCalc) pCalc;

		// create the wizard to color/name this
		final SimplePageListWizard wizard = new SimplePageListWizard();

		final String imagePath = "images/UnderwaterPropagation.png";
		final String taskTitle = "Generate Doppler Plot";

		final DecimalFormat fFormat = new DecimalFormat("0.0000");
		final DecimalFormat cFormat = new DecimalFormat("0.0");

		final EnterStringPage getf0 = new EnterStringPage(null, " " + fFormat.format(calc.getFNought()) + " ",
				taskTitle, "Please enter a f-Nought (Hz) for [" + primary + "]", "(e.g. 150)", imagePath, null, false,
				null);
		final EnterStringPage getSoundSpeed = new EnterStringPage(null,
				" " + cFormat.format(Conversions.Kts2Mps(calc.getSpeedOfSound())) + " ", taskTitle,
				"Please enter the Speed of Sound in Water (m/sec)", "(e.g. 2000)", imagePath, null, false, null);
		wizard.addWizard(getf0);
		wizard.addWizard(getSoundSpeed);
		final WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
		dialog.create();
		dialog.setBlockOnOpen(true);
		dialog.open();
		// did it work?
		if (dialog.getReturnCode() == Window.OK) {
			calc.setFNought(Double.parseDouble(getf0.getString()));
			calc.setSpeedOfSound(Conversions.Mps2Kts(Double.parseDouble(getSoundSpeed.getString())));
		}

		// ok, return the dialog ok/cancel value
		return dialog.getReturnCode();
	}
}
