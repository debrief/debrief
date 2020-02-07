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

package org.mwc.debrief.lite.custom;

import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentPresentationModel;
import org.pushingpixels.flamingo.api.ribbon.synapse.projection.ComponentProjection;

/**
 * @author Ayesha
 *
 */
public class RibbonLabelProjection extends ComponentProjection<JRibbonLabel, LabelComponentContentModel> {

	public RibbonLabelProjection(final LabelComponentContentModel contentModel,
			final ComponentPresentationModel presentationModel,
			final ComponentSupplier<JRibbonLabel, LabelComponentContentModel, ComponentPresentationModel> componentSupplier) {
		super(contentModel, presentationModel, componentSupplier);
	}

	@Override
	protected void configureComponent(final JRibbonLabel component) {
		component.setText(getContentModel().getText());
		if (getContentModel().getBorder() != null) {

			component.setBorder(getContentModel().getBorder());
		}
		if (getContentModel().getBackground() != null) {
			component.setBackground(getContentModel().getBackground());
		}
		if (getContentModel().getName() != null) {

			component.setName(getContentModel().getName());
		}
		if (component.getFont() != null) {
			component.setFont(getContentModel().getFont());

		}
		if (getContentModel().getForeground() != null) {
			component.setForeground(getContentModel().getForeground());
		}

	}

}
