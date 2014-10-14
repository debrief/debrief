/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc_rcp.ui.widgets;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class ExpandButton
{
	private static final String EXPAND_ICON = "/icons/bullet-arrow-right-icon.png";
	private static final String COLLAPSE_ICON = "/icons/bullet-arrow-down-icon.png";

	private Image expandImage;
	private Image collapseImage;

	private ToolBar toolBar;
	private ToolItem toolItem;

	private Set<SelectionListener> listeners;

	private boolean selected = false;

	public ExpandButton(Composite parent)
	{
		expandImage = SATC_Activator.getImageDescriptor(EXPAND_ICON).createImage();
		collapseImage = SATC_Activator.getImageDescriptor(COLLAPSE_ICON)
				.createImage();

		toolBar = new ToolBar(parent, SWT.FLAT);
		toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(expandImage);
		toolItem.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
				//
			}

			@Override
			public void widgetSelected(SelectionEvent event)
			{
				setSelection(!selected);
				if (listeners != null)
				{
					for (SelectionListener listener : listeners)
					{
						listener.widgetSelected(event);
					}
				}
			}
		});
	}

	public void addSelectionListener(SelectionListener listener)
	{
		if (listeners == null)
		{
			listeners = new LinkedHashSet<SelectionListener>();
		}
		listeners.add(listener);
	}

	public Control getControl()
	{
		return toolBar;
	}

	public boolean getSelection()
	{
		return selected;
	}

	public void removeSelectionListener(SelectionListener listener)
	{
		listeners.remove(listener);
	}

	public void setSelection(boolean selected)
	{
		if (selected == this.selected)
		{
			return;
		}
		this.selected = selected;
		toolItem.setImage(selected ? collapseImage : expandImage);
	}
}
