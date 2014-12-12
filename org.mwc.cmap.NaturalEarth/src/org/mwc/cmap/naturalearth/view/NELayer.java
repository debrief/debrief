package org.mwc.cmap.naturalearth.view;

import java.util.HashMap;
import java.util.Iterator;

import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.model.NELibrary;
import org.mwc.cmap.naturalearth.model.NELibrary.NEFeature;
import org.mwc.cmap.naturalearth.model.NEResolutionGroup;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;

public class NELayer extends BaseLayer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private NELibrary _myLibrary = null;

	private HashMap<String, NEStyle> _styles = null;

	@Override
	public void paint(CanvasType dest)
	{
		// do we have data?
		if (_myLibrary == null)
		{
			_myLibrary = initLibrary();
		}

		// do we have styles?
		if (_styles == null)
		{
			_styles = Activator.getDefault().getStyleSet();
		}

		if ((_myLibrary != null) && (_styles != null))
		{
			// ok, we can paint

			// what's the scale
			double scale = dest.getProjection().getScreenArea().getWidth()
					/ dest.getProjection().getDataArea().getWidth();

			// get the relevant resolution
			NEResolutionGroup thisRes = resolutionFor(scale);

			if (thisRes != null)
			{
				// loop through the features
				Iterator<NEFeature> features = thisRes.iterator();

				while (features.hasNext())
				{
					NELibrary.NEFeature feature = (NELibrary.NEFeature) features.next();

					// get the style
					NEStyle thisStyle = _styles.get(feature.getName());

					// and paint it
					paintFeature(feature, thisStyle);
				}
			}
		}
	}

	private NEResolutionGroup resolutionFor(double scale)
	{
		NEResolutionGroup res = null;

		// loop through the resolutions
		Iterator<NEResolutionGroup> iter = _myLibrary.iterator();
		while (iter.hasNext())
		{
			NEResolutionGroup group = (NEResolutionGroup) iter.next();
			if (group.canHandle(scale))
			{
				res = group;
				break;
			}
		}

		return res;
	}

	private void paintFeature(NEFeature feature, NEStyle thisStyle)
	{
		// TODO Auto-generated method stub

	}

	private NELibrary initLibrary()
	{
		// collect a series of resolutions
		NELibrary lib = new NELibrary();

		// retrieve the path
		String prefPath = Activator.getDefault().getLibraryPath();

		// retrieve the set of styles

		// loop through our resolutions

		// loop through our "target" files

		// does this file exist in the folder?

		// do we have an array for this resolution

		// nope, create one

		// store it

		// yes, load it.

		//

		// TODO Auto-generated method stub
		if (lib.size() > 0)
			return lib;
		else
			return null;
	}

}
