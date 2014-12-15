package org.mwc.cmap.naturalearth.wrapper;

import java.util.ArrayList;
import java.util.Iterator;

import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.model.NEFeature;
import org.mwc.cmap.naturalearth.model.NEPointLayer;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.mwc.cmap.naturalearth.view.NEResolution;
import org.mwc.cmap.naturalearth.view.NEStyle;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;

public class NELayer extends BaseLayer
{

	private ArrayList<NEFeature> _features = new ArrayList<NEFeature>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NELayer(NEStyle styles)
	{

	}

	/**
	 * switch to the supplied style set
	 * 
	 * @param styles
	 */
	public void applyLayer(NEStyle newStyle)
	{

	}

	@Override
	public void paint(CanvasType dest)
	{
		if (getVisible())
		{
			double curScale = dest.getProjection().getScreenArea().getWidth()
					/ dest.getProjection().getDataArea().getWidth();

			// have we loaded our layers?
			if (!hasLayers())
			{
				// find the style set for this scale
				NEResolution thisR = getStyleSetFor(curScale);

				// cool, loop through the features
				Iterator<NEFeatureStyle> iter = thisR.iterator();

				while (iter.hasNext())
				{
					NEFeatureStyle neFeatureStyle = (NEFeatureStyle) iter.next();
					NEFeature newF = featureFor(neFeatureStyle);

					_features.add(newF);
				}
			}

			// ok, get painting
			Iterator<NEFeature> iter = _features.iterator();
			while (iter.hasNext())
			{
				NEFeature neFeature = (NEFeature) iter.next();
				paintThisFeatureType(dest, neFeature);
			}
		}
	}

	/**
	 * create the correct feature type for this style
	 * 
	 * @param neFeatureStyle
	 * @return
	 */
	private NEFeature featureFor(NEFeatureStyle neFeatureStyle)
	{
		return new NEPointLayer(neFeatureStyle);
	}

	private boolean hasLayers()
	{
		return _features.size() > 0;
	}

	private void paintThisFeatureType(CanvasType dest, NEFeature thisF)
	{
		// ok, is it loaded?
		if (!thisF.isLoaded())
		{
			String fName = thisF.getStyle().getFileName();

			// get the datafile
			CachedShapefile thisData = Activator.getDefault().loadData(fName);

			// did we find the shapefile?
			if (thisData != null)
			{
				thisF.setFeature(thisData);
			}
		}

		// just check we have our data
		if (thisF.isLoaded())
		{
			Iterator<CachedFeature> iter = thisF.iterator();
			while (iter.hasNext())
			{
				CachedFeature cachedFeature = (CachedFeature) iter.next();
				paintThisFeature(dest, cachedFeature, thisF.getStyle());
			}
		}

	}

	private void paintThisFeature(CanvasType dest, CachedFeature cachedFeature,
			NEFeatureStyle style)
	{
		// ok, this paint is dependent on the feature type, and the styling.
	}

	private NEResolution getStyleSetFor(double curScale)
	{
		return Activator.getStyleFor(curScale);
	}

}
