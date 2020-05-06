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

package org.mwc.debrief.pepys.view.tree;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.pepys.model.tree.TreeNode;

import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public class TreeNameLabelProvider implements ILabelProvider {

	private final Map<String, Image> imageCache = new HashMap<>();

	@Override
	public void addListener(final ILabelProviderListener listener) {
		return;
	}

	@Override
	public void dispose() {
		return;
	}

	@Override
	public Image getImage(final Object element) {
		if (element instanceof TreeNode) {
			final TreeNode node = (TreeNode) element;
			String key = node.getType().name();
			if (node.getType().equals(TreeNode.NodeType.MEASURE)) {
				key += node.getName();
			}
			if (!imageCache.containsKey(key)) {
				if (node.getType().equals(TreeNode.NodeType.ROOT)) {
					imageCache.put(key, DebriefPlugin.getImageDescriptor("/icons/16/database.png").createImage());
				} else if (node.getType().equals(TreeNode.NodeType.PLATFORM)) {
					imageCache.put(key, DebriefPlugin.getImageDescriptor("/icons/16/leg.png").createImage());
				} else if (node.getType().equals(TreeNode.NodeType.MEASURE)) {
					if (node.getName().equals(TreeNode.STATE)) {
						imageCache.put(key, DebriefPlugin.getImageDescriptor("/icons/16/fix.png").createImage());
					} else if (node.getName().equals(TreeNode.COMMENT)) {
						imageCache.put(key, DebriefPlugin.getImageDescriptor("/icons/16/narrative.png").createImage());
					} else if (node.getName().equals(TreeNode.CONTACTS)) {
						imageCache.put(key, DebriefPlugin.getImageDescriptor("/icons/16/bearing.png").createImage());
					}
				}
				if (node.getType().equals(TreeNode.NodeType.DATAFILE)) {
					imageCache.put(key,
							DebriefPlugin.getImageDescriptor("/icons/16/narrative_viewer.png").createImage());
				}
				if (node.getType().equals(TreeNode.NodeType.SENSOR)) {
					imageCache.put(key, DebriefPlugin.getImageDescriptor("/icons/16/sensor.png").createImage());
				}
			}
			return imageCache.get(key);
		}
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
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {
		return;
	}

}
