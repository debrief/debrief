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
package info.limpet.stackedcharts.ui.editor.commands;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;

import info.limpet.stackedcharts.model.DependentAxis;

public class AddAxisToChartCommand extends Command {
	private final DependentAxis[] axes;
	private final EList<DependentAxis> destination;

	public AddAxisToChartCommand(final EList<DependentAxis> destination, final DependentAxis... axes) {
		this.axes = axes;
		this.destination = destination;
	}

	@Override
	public void execute() {

		for (final DependentAxis ds : axes) {

			destination.add(ds);
		}

	}

	@Override
	public void undo() {
		for (final DependentAxis ds : axes) {

			destination.remove(ds);
		}
	}
}
