package org.mwc.cmap.naturalearth.wrapper;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.gt2plot.data.CachedNauticalEarthFile;
import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.model.NEFeature;
import org.mwc.cmap.naturalearth.model.NEPointLayer;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.mwc.cmap.naturalearth.view.NEResolution;
import org.mwc.cmap.naturalearth.view.NEStyle;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.NamedWorldLocation;
import MWC.GenericData.NamedWorldPath;
import MWC.GenericData.NamedWorldPathList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;

public class NELayer extends BaseLayer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NELayer()
	{
		setName("Natural Earth");
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
					NEFeature newF = new NEFeature(neFeatureStyle);
					super.add(newF);
				}
			}

			// ok, get painting - start off with the polygons
			Enumeration<Editable> children = super.elements();
			while (children.hasMoreElements())
			{
				NEFeature neFeature = (NEFeature) children.nextElement();
				paintThisFeatureType(dest, neFeature);
			}
		}
	}

	private boolean hasLayers()
	{
		return super.size() > 0;
	}

	private void paintThisFeatureType(CanvasType dest, NEFeature thisF)
	{
		// double check if this style should be visible
		if (!thisF.getStyle().isVisible())
			return;

		// ok, is it loaded?
		if (!thisF.isLoaded())
		{
			String fName = thisF.getStyle().getFileName();

			// get the datafile
			CachedNauticalEarthFile thisData = Activator.getDefault().loadData(fName);

			// did we find the shapefile?
			if (thisData != null)
			{
				thisF.setDataSource(thisData);
			}
		}

		// double-check it has some data
		if (thisF.getData() != null)
		{
			// ok, do the load
			thisF.getData().init();

			// draw the data from the bottom up
			drawPolygons(dest, thisF.getData().getPolygons(), thisF.getStyle());
			drawLines(dest, thisF.getData().getLines(), thisF.getStyle());
			drawPoints(dest, thisF.getData().getPoints(), thisF.getStyle());
		}
	}

	private void drawPolygons(CanvasType dest,
			ArrayList<NamedWorldPath> polygons, NEFeatureStyle style)
	{
		if(polygons == null)
			return;
		
		// store the screen size
		WorldArea visArea = dest.getProjection().getVisibleDataArea();

		// ok, loop through the polys
		Iterator<NamedWorldPath> iter = polygons.iterator();
		while (iter.hasNext())
		{
			NamedWorldPath namedWorldPath = (NamedWorldPath) iter.next();

			if (!visArea.overlaps(namedWorldPath.getBounds()))
			{
				// ok, skip to the next one
				continue;
			}

			dest.setColor(style.getLineColor());

			Collection<WorldLocation> _nodes = namedWorldPath.getPoints();

			// create our point lists
			final int[] xP = new int[_nodes.size()];
			final int[] yP = new int[_nodes.size()];

			// ok, step through the area
			final Iterator<WorldLocation> points = _nodes.iterator();

			int counter = 0;

			while (points.hasNext())
			{
				final WorldLocation next = points.next();

				// convert to screen
				final Point thisP = dest.toScreen(next);

				if ((thisP.x <= 0) || (thisP.y <= 0))
				{
					System.err.println("here");
				}
				else
				{
					// remember the coords
					xP[counter] = thisP.x;
					yP[counter] = thisP.y;

					// move the counter
					counter++;
				}
			}

			dest.fillPolygon(xP, yP, counter / 2);
		}
	}

	private void drawLines(CanvasType dest, ArrayList<NamedWorldPathList> paths,
			NEFeatureStyle style)
	{
		if(paths == null)
			return;
		
		dest.setColor(style.getLineColor());

		// ok, loop through the polys
		Iterator<NamedWorldPathList> iter = paths.iterator();
		while (iter.hasNext())
		{
			NamedWorldPathList next2 = iter.next();
			Iterator<WorldPath> iter2 = next2.iterator();

			while (iter2.hasNext())
			{
				WorldPath namedWorldPath = (WorldPath) iter2.next();

				Collection<WorldLocation> _nodes = namedWorldPath.getPoints();

				// create our point lists
				final int[] xPoints = new int[2 * _nodes.size()];

				// ok, step through the area
				final Iterator<WorldLocation> points = _nodes.iterator();

				int counter = 0;

				while (points.hasNext())
				{
					final WorldLocation next = points.next();

					// convert to screen
					final Point thisP = dest.toScreen(next);

					// remember the coords
					xPoints[counter++] = thisP.x;
					xPoints[counter++] = thisP.y;
				}

				dest.drawPolyline(xPoints);
			}
		}
	}

	private void drawPoints(CanvasType dest,
			ArrayList<NamedWorldLocation> points, NEFeatureStyle style)
	{
		if(points == null)
			return;
		
		dest.setColor(style.getTextColor());

		// ok, loop through the polys
		Iterator<NamedWorldLocation> iter = points.iterator();
		while (iter.hasNext())
		{
			NamedWorldLocation namedLoc = (NamedWorldLocation) iter.next();
			final WorldLocation next = namedLoc;
			final Point thisP = dest.toScreen(next);
			dest.drawText(namedLoc.getName(), thisP.x, thisP.y);
		}
	}

	private NEResolution getStyleSetFor(double curScale)
	{
		return Activator.getStyleFor(curScale);
	}

	public static boolean hasGoodPath()
	{
		final File dataPath = new File(Activator.getDefault().getLibraryPath());

		return dataPath.exists();
	}

}
