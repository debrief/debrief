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

package org.mwc.debrief.pepys.model.tree;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public class TreeNameLabelProvider implements ILabelProvider {

	@Override
	public void addListener(final ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(final Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(final Object element) {
		if (element instanceof TreeNode) {
			final TreeNode node = (TreeNode) element;
			String name = node.getName();
			if (node.getCurrentPeriod() instanceof BaseTimePeriod
					&& !((BaseTimePeriod) node.getCurrentPeriod()).isInvalid()) {
				name = name + "  " + DebriefFormatDateTime.toStringHiRes(node.getCurrentPeriod().getStartDTG()) + " - "
						+ DebriefFormatDateTime.toStringHiRes(node.getCurrentPeriod().getEndDTG());
			}
			return name;
		}
		return "";
	}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}
