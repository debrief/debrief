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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsFactory;

public class AddScatterSetsToChartCommand extends Command {
	public static SelectiveAnnotation findAnnotationByName(final String annotationName, final ChartSet charts) {
		SelectiveAnnotation host = null;
		for (final SelectiveAnnotation annot : charts.getSharedAxis().getAnnotations()) {
			if (annot.getAnnotation().getName() != null && annot.getAnnotation().getName().equals(annotationName)) {
				host = annot;
				break;
			}
		}
		return host;
	}

	private final ScatterSet[] scatterSets;

	private final Chart parent;

	/**
	 * Contains all newly created annotations during {@link #execute()} that need to
	 * be removed during {@link #undo()}
	 */
	private List<SelectiveAnnotation> createdAnnotations;

	/**
	 * Contains annotations which have been added to appear in the parent. Again the
	 * parent needs to be removed from those during {@link #undo()}.
	 */
	private List<SelectiveAnnotation> appearInParent;

	public AddScatterSetsToChartCommand(final Chart parent, final ScatterSet... scatterSets) {
		this.scatterSets = scatterSets;
		this.parent = parent;
	}

	@Override
	public void execute() {

		createdAnnotations = new ArrayList<SelectiveAnnotation>();
		appearInParent = new ArrayList<SelectiveAnnotation>();

		for (final ScatterSet ds : scatterSets) {
			// ok, we may have to add it to the chartset first
			final ChartSet charts = parent.getParent();
			final EList<SelectiveAnnotation> annots = charts.getSharedAxis().getAnnotations();
			SelectiveAnnotation host = findAnnotationByName(ds.getName(), charts);

			if (host == null) {
				host = StackedchartsFactory.eINSTANCE.createSelectiveAnnotation();
				host.setAnnotation(ds);
				annots.add(host);
				createdAnnotations.add(host);
			}

			// check we're not already in that chart
			final EList<Chart> appearsIn = host.getAppearsIn();
			if (!appearsIn.contains(parent)) {
				appearsIn.add(parent);
				appearInParent.add(host);
			}
		}
	}

	@Override
	public void undo() {
		for (final SelectiveAnnotation annotation : appearInParent) {
			annotation.getAppearsIn().remove(parent);
		}

		final ChartSet charts = parent.getParent();
		final EList<SelectiveAnnotation> annotations = charts.getSharedAxis().getAnnotations();
		for (final SelectiveAnnotation annotation : createdAnnotations) {
			annotations.remove(annotation);
		}
	}
}