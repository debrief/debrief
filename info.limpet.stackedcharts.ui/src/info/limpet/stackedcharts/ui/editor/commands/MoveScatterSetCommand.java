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

import org.eclipse.gef.commands.Command;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;

public class MoveScatterSetCommand extends Command {

	private final Chart from;
	private final Chart to;
	private final SelectiveAnnotation selectiveAnnotation;

	public MoveScatterSetCommand(final ScatterSet scatterSet, final Chart from, final Chart to) {
		this.from = from;
		this.to = to;
		this.selectiveAnnotation = AddScatterSetsToChartCommand.findAnnotationByName(scatterSet.getName(),
				from.getParent());
	}

	@Override
	public void execute() {
		selectiveAnnotation.getAppearsIn().remove(from);
		selectiveAnnotation.getAppearsIn().add(to);
	}

	@Override
	public void undo() {
		selectiveAnnotation.getAppearsIn().add(from);
		selectiveAnnotation.getAppearsIn().remove(to);
	}
}
