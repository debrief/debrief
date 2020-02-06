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

public class DeleteScatterSetCommand extends Command {
	private final SelectiveAnnotation annotation;
	private final Chart chart;
	private boolean deleted;

	public DeleteScatterSetCommand(final ScatterSet scatterSet, final Chart chart) {
		this.annotation = AddScatterSetsToChartCommand.findAnnotationByName(scatterSet.getName(), chart.getParent());
		this.chart = chart;
	}

	@Override
	public void execute() {
		annotation.getAppearsIn().remove(chart);
		// delete the annotation as well
		if (annotation.getAppearsIn().isEmpty()) {
			chart.getParent().getSharedAxis().getAnnotations().remove(annotation);
			deleted = true;
		}
	}

	@Override
	public void undo() {
		annotation.getAppearsIn().add(chart);
		if (deleted) {
			chart.getParent().getSharedAxis().getAnnotations().add(annotation);
			deleted = false;
		}
	}
}
