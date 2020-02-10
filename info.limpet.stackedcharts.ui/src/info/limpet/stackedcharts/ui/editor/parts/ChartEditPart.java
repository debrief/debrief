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
package info.limpet.stackedcharts.ui.editor.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.ui.editor.commands.DeleteChartCommand;
import info.limpet.stackedcharts.ui.editor.figures.ChartFigure;
import info.limpet.stackedcharts.ui.editor.policies.ChartContainerEditPolicy;

public class ChartEditPart extends AbstractGraphicalEditPart implements ActionListener {
	public class ChartAdapter extends EContentAdapter {

		@Override
		public void notifyChanged(final Notification notification) {
			final Object feature = notification.getFeature();

			// ok, now check if anything changed that causes a visual update
			for (final EAttribute thisA : _visualUpdates) {
				if (feature == thisA) {
					refreshVisuals();
					break;
				}
			}

			// ok, now check for a children update
			for (final EReference thisA : _childrenUpdates) {
				if (feature == thisA) {
					refreshChildren();
					break;
				}
			}
		}
	}

	public enum ChartPanePosition {
		MIN, MAX
	}

	/**
	 * Helper class to handle the container of {@link ScatterSet}s
	 */
	@SuppressWarnings("serial")
	public static class ScatterSetContainer extends ArrayList<ScatterSet> {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

	}

	/**
	 * Update scatter sets in the scatter set container when model changes. Use an
	 * {@link EContentAdapter}, since we'd like to be notified when multiple
	 * properties of different objects in the shared axis get changed.
	 */
	public class SharedAxisAdapter extends EContentAdapter {
		private IndependentAxis independentAxis;

		void attachTo(final IndependentAxis independentAxis) {
			if (this.independentAxis != null) {
				this.independentAxis.eAdapters().remove(this);
			}
			this.independentAxis = independentAxis;
			if (this.independentAxis != null) {
				this.independentAxis.eAdapters().add(this);
			}
		}

		@Override
		public void notifyChanged(final Notification notification) {
			super.notifyChanged(notification);
			final int featureId = notification.getFeatureID(StackedchartsPackage.class);
			switch (featureId) {
			case StackedchartsPackage.INDEPENDENT_AXIS__ANNOTATIONS:
			case StackedchartsPackage.SELECTIVE_ANNOTATION__APPEARS_IN:
				refreshChildren();
				break;
			}
		}
	}

	public static final Color BACKGROUND_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	final private ChartAdapter adapter = new ChartAdapter();
	final private SharedAxisAdapter sharedAxisAdapter = new SharedAxisAdapter();

	public final ArrayList<EAttribute> _visualUpdates;

	public final ArrayList<EReference> _childrenUpdates;

	public ChartEditPart() {

		// get our model definition
		final StackedchartsPackage pckg = StackedchartsPackage.eINSTANCE;

		// collate a list of what features trigger a visual update
		_visualUpdates = new ArrayList<EAttribute>();
		_visualUpdates.add(pckg.getChart_Name());
		_visualUpdates.add(pckg.getStyling_LineStyle());
		_visualUpdates.add(pckg.getStyling_LineThickness());
		_visualUpdates.add(pckg.getStyling_MarkerSize());
		_visualUpdates.add(pckg.getStyling_MarkerStyle());
		_visualUpdates.add(pckg.getPlainStyling_Color());

		// and now collate a list of which attributes trigger the
		// chidren to update
		_childrenUpdates = new ArrayList<EReference>();
		_childrenUpdates.add(pckg.getChart_MaxAxes());
		_childrenUpdates.add(pckg.getChart_MinAxes());
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		final Command deleteCommand = getCommand(new GroupRequest(REQ_DELETE));
		if (deleteCommand != null) {
			final CommandStack commandStack = getViewer().getEditDomain().getCommandStack();
			commandStack.execute(deleteCommand);
		}

	}

	@Override
	public void activate() {
		super.activate();
		getModel().eAdapters().add(adapter);
		sharedAxisAdapter.attachTo(getSharedAxis());
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ChartContainerEditPolicy());

		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			@Override
			protected Command createDeleteCommand(final GroupRequest deleteRequest) {
				final Chart chart = getModel();
				final ChartSet parent = chart.getParent();
				final DeleteChartCommand deleteChartCommand = new DeleteChartCommand(parent, chart);
				return deleteChartCommand;
			}
		});
	}

	@Override
	protected IFigure createFigure() {
		return new ChartFigure(getModel(), this);
	}

	@Override
	public void deactivate() {
		getModel().eAdapters().remove(adapter);
		// effectively detach the adapter/listener
		sharedAxisAdapter.attachTo(null);
		super.deactivate();
	}

	@Override
	public Chart getModel() {
		return (Chart) super.getModel();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List getModelChildren() {
		final List modelChildren = new ArrayList();
		modelChildren.addAll(Arrays.asList(ChartPanePosition.values()));
		final ScatterSetContainer scatterSets = new ScatterSetContainer();
		for (final SelectiveAnnotation annotation : getSharedAxis().getAnnotations()) {
			if (annotation.getAnnotation() instanceof ScatterSet && annotation.getAppearsIn().contains(getModel())) {
				scatterSets.add((ScatterSet) annotation.getAnnotation());
			}
		}
		modelChildren.add(scatterSets);
		return modelChildren;
	}

	private IndependentAxis getSharedAxis() {
		return getModel().getParent().getSharedAxis();
	}

	@Override
	protected void refreshChildren() {
		// remove all Childs
		@SuppressWarnings("unchecked")
		final List<EditPart> children = getChildren();
		for (final EditPart object : new ArrayList<EditPart>(children)) {
			removeChild(object);
		}
		// add back all model elements
		@SuppressWarnings("rawtypes")
		final List modelObjects = getModelChildren();
		for (int i = 0; i < modelObjects.size(); i++) {
			addChild(createChild(modelObjects.get(i)), i);

		}

		((ChartFigure) getFigure()).getLayoutManager().layout(getFigure());
	}

	@Override
	protected void refreshVisuals() {
		final String name = getModel().getName();
		final ChartFigure chartFigure = (ChartFigure) getFigure();
		chartFigure.setName(name);
		chartFigure.setVertical(getModel().getParent().getOrientation() == Orientation.VERTICAL);

		final GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;

		((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure, gridData);

	}
}
