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
package org.mwc.debrief.multipath2;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;

/**

 */

public class MultiPathViewTest extends MultiPathView
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.mwc.debrief.MultiPath2Test";

	/**
	 * The constructor.
	 */
	public MultiPathViewTest()
	{
		// now sort out the presenter
		_presenter = new MultiPathPresenterTest(this);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent)
	{
		super.createPartControl(parent);
	}

	@Override
	public TrackDataProvider getDataProvider()
	{
		TrackDataProvider res = null;

		// ok, grab the current editor
		final IEditorPart editor = this.getSite().getPage().getActiveEditor();
		final Object provider = editor.getAdapter(TrackDataProvider.class);
		if (provider != null)
		{
			res = (TrackDataProvider) provider;
		}

		return res;
	}


}