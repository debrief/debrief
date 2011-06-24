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
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
	}

	@Override
	public TrackDataProvider getDataProvider()
	{
		TrackDataProvider res = null;

		// ok, grab the current editor
		IEditorPart editor = this.getSite().getPage().getActiveEditor();
		Object provider = editor.getAdapter(TrackDataProvider.class);
		if (provider != null)
		{
			res = (TrackDataProvider) provider;
		}

		return res;
	}


}