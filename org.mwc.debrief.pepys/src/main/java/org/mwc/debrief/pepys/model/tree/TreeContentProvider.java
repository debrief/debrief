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

import org.eclipse.jface.viewers.ITreeContentProvider;

public class TreeContentProvider implements ITreeContentProvider {

	final Object[] EMPTY_ARRAY = new Object[] {};

	@Override
	public Object[] getChildren(final Object element) {
		if (element instanceof TreeNode) {
			return ((TreeNode) element).getChildren();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object[] getElements(final Object element) {
		return getChildren(element);
	}

	@Override
	public Object getParent(final Object element) {
		if (element instanceof TreeNode) {
			return ((TreeNode) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(final Object element) {
		if (element instanceof TreeNode) {
			return ((TreeNode) element).hasChildren();
		}
		return false;
	}

}
