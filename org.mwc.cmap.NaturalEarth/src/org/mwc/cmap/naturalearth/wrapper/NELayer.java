package org.mwc.cmap.naturalearth.wrapper;

import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.data.CachedNaturalEarthFile;
import org.mwc.cmap.naturalearth.model.NEFeature;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.mwc.cmap.naturalearth.view.NEResolution;
import org.mwc.cmap.naturalearth.view.NEStyle;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GenericData.NamedWorldLocation;
import MWC.GenericData.NamedWorldPath;
import MWC.GenericData.NamedWorldPathList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;

public class NELayer extends BaseLayer implements NeedsToKnowAboutLayers
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<NEResolution, Collection<Editable>> _myResolutions;
	private NEResolution _currentRes;
	
	private HashMap<String, Font> _fontCache = new HashMap<String, Font>();
	

	/** the safest we can get to the poles without GeoTools falling over.
	 * 
	 */
	final private double LAT_LIMIT = 89.0;
	private Layers _theLayers;

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
			double screenMM = dest.getProjection().getScreenArea().getWidth();
			double worldDegs = dest.getProjection().getDataArea().getWidth();
			double worldMM = worldDegs * 60 * 1852 * 1000;
			final double curScale = worldMM / screenMM;

			// find the style set for this scale
			final NEResolution thisR = getStyleSetFor(curScale);

			System.out.println("got: " + thisR.getName());
			
			if(thisR.getName().startsWith("50M"))
			{
				System.err.println("here");
			}

			// is this different?
			if (thisR != _currentRes)
			{
				// check we have the resolutions collection
				if (_myResolutions == null)
				{
					_myResolutions = new HashMap<NEResolution, Collection<Editable>>();
				}

				// ok, drop any existing layers
				super.removeAllElements();

				// ok, is this a new one?
				if (!_myResolutions.containsKey(thisR))
				{
					// ok, better load it.
					Iterator<NEFeatureStyle> iter = thisR.iterator();

					while (iter.hasNext())
					{
						NEFeatureStyle neFeatureStyle = (NEFeatureStyle) iter.next();
						NEFeature newF = new NEFeature(neFeatureStyle);
						super.add(newF);
					}
					_myResolutions.put(thisR, super.getData());
				}
				else
				{
					// already have it, load it.
					Iterator<Editable> iter = _myResolutions.get(thisR).iterator();
					while (iter.hasNext())
					{
						Editable editable = (Editable) iter.next();
						super.add(editable);
					}
				}
				
				// hmm, we also have to tell the layer manager that we have updated
				if(_theLayers != null)
					_theLayers.fireExtended(null,  this);

				_currentRes = thisR;
			}

			// start off by making sure data is loaded
			Enumeration<Editable> children = super.elements();
			while (children.hasMoreElements())
			{
				NEFeature feature = (NEFeature) children.nextElement();

				if (feature.getVisible())
				{
					if (feature.getData() == null)
					{
						String fName = feature.getStyle().getFileName();
						// get the datafile
						CachedNaturalEarthFile thisData = Activator.getDefault().loadData(
								fName);

						// did we find the shapefile?
						if (thisData != null)
						{
							feature.setDataSource(thisData);
						}

						// ok, and self-load
						thisData.init();
					}

				}
			}

			children = super.elements();
			while (children.hasMoreElements())
			{
				NEFeature feature = (NEFeature) children.nextElement();

				if (!feature.getVisible())
					continue;

				// draw the data from the bottom up
				drawPolygonPolygons(dest, feature.getData().getPolygons(),
						feature.getStyle());
			}

			children = super.elements();
			while (children.hasMoreElements())
			{
				NEFeature feature = (NEFeature) children.nextElement();

				if (!feature.getVisible())
					continue;

				// draw the data from the bottom up
				drawPolygonLines(dest, feature.getData().getPolygons(),
						feature.getStyle());
				drawLineLines(dest, feature.getData().getLines(), feature.getStyle());
			}

			children = super.elements();
			while (children.hasMoreElements())
			{
				NEFeature feature = (NEFeature) children.nextElement();

				if (!feature.getVisible())
					continue;

				// draw the data from the bottom up
				drawPolygonPoints(dest, feature.getData().getPolygons(),
						feature.getStyle());
				drawLinePoints(dest, feature.getData().getLines(), feature.getStyle());
				drawPointPoints(dest, feature.getData().getPoints(), feature.getStyle());
			}

		}
	}

	private void drawPolygonPolygons(CanvasType dest,
			ArrayList<NamedWorldPath> polygons, NEFeatureStyle style)
	{
		if (polygons == null)
			return;
		
		if(!style.isShowPolygons())
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
			
			// see if it's not our test one
			if(! namedWorldPath.getName().equals("81"))
			{
		//		continue;
			}

			dest.setColor(style.getPolygonColor());

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

				if (Math.abs(next.getLat()) < LAT_LIMIT)
				{
					// convert to screen
					Point thisP = null;
					try{
						thisP = dest.toScreen(next);
					}
					catch(Exception e)
					{
						System.err.println("failed with:" + next);
					}

					if (thisP == null)
					{
						System.out.println("NULL LOCATION:" + next + " lat:" + next.getLat());
					}
					else
					{
						if (locationOk(thisP))
						{
							// remember the coords
							xP[counter] = thisP.x;
							yP[counter] = thisP.y;

							// move the counter
							counter++;
						}
					}
				}
			}

			dest.fillPolygon(xP, yP, counter);
		}
	}

	private void drawPolygonLines(CanvasType dest,
			ArrayList<NamedWorldPath> polygons, NEFeatureStyle style)
	{
		if (polygons == null)
			return;
		
		if(style.getLineColor() == null)
			return;
		
		if(!style.isShowLines())
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

				if (Math.abs(next.getLat()) < LAT_LIMIT)
				{
					// convert to screen
					Point thisP = null;
					try{
						thisP = dest.toScreen(next);
					}
					catch(Exception e)
					{
						System.err.println("failed with:" + next);
					}

					if (thisP == null)
					{
						System.err.println("duff location");
					}
					else
					{

						if (locationOk(thisP))
						{
							// remember the coords
							xP[counter] = thisP.x;
							yP[counter] = thisP.y;

							// move the counter
							counter++;
						}
					}
				}
			}

			dest.drawPolygon(xP, yP, counter);
		}
	}
	
	private boolean locationOk(Point point)
	{
		return point.x > -10000 && point.y > -10000;
	}

	private void drawPolygonPoints(CanvasType dest,
			ArrayList<NamedWorldPath> polygons, NEFeatureStyle style)
	{
		if (polygons == null)
			return;
		
		if(style.getTextColor() == null)
			return;

		// this method handles labels and points, so drop out if neither are of interest
		if(!style.isShowLabels() && !style.isShowPoints())
			return;
		
		dest.setColor(style.getTextColor());		
		Font font = fontFor(style.getTextHeight(), style.getTextStyle(), style.getTextFont());
		dest.setFont(font);

		// store the screen size
		WorldArea visArea = dest.getProjection().getVisibleDataArea();

		// ok, loop through the polys
		Iterator<NamedWorldPath> iter = polygons.iterator();
		while (iter.hasNext())
		{
			NamedWorldPath namedWorldPath = (NamedWorldPath) iter.next();

			if (namedWorldPath.getName() == null)
				continue;

			if (!visArea.overlaps(namedWorldPath.getBounds()))
			{
				// ok, skip to the next one
				continue;
			}

			dest.setColor(style.getTextColor());

			WorldLocation centre = namedWorldPath.getBounds().getCentre();
			// convert to screen
			final Point thisP = dest.toScreen(centre);

			dest.drawText(namedWorldPath.getName(), thisP.x, thisP.y);
		}
	}

	private void drawLineLines(CanvasType dest,
			ArrayList<NamedWorldPathList> paths, NEFeatureStyle style)
	{
		if (paths == null)
			return;

		if(style.getLineColor() == null)
			return;
		
		if(!style.isShowLines())
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
					
					if (locationOk(thisP))
					{
						// remember the coords
						xPoints[counter++] = thisP.x;
						xPoints[counter++] = thisP.y;
					}
				}

				dest.drawPolyline(xPoints);
			}
		}
	}

	private void drawLinePoints(CanvasType dest,
			ArrayList<NamedWorldPathList> paths, NEFeatureStyle style)
	{
		if (paths == null)
			return;
		
		// this method handles labels and points, so drop out if neither are of interest
		if(!style.isShowLabels() && !style.isShowPoints())
			return;

		// store the screen size
		WorldArea visArea = dest.getProjection().getVisibleDataArea();

		// ok, loop through the polys
		Iterator<NamedWorldPathList> iter = paths.iterator();
		while (iter.hasNext())
		{
			NamedWorldPathList next2 = iter.next();
			if (next2.getName() == null)
				continue;

			if (next2.getBounds() == null)
				continue;

			if (!visArea.overlaps(next2.getBounds()))
			{
				// ok, skip to the next one
				continue;
			}

			dest.setColor(style.getTextColor());

			WorldLocation centre = next2.getBounds().getCentre();
			// convert to screen
			final Point thisP = dest.toScreen(centre);

			dest.drawText(next2.getName(), thisP.x, thisP.y);

		}
	}

	private Font fontFor(int height, int style, String family)
	{
		String descriptor = family + "_" + height + "_" + style;
		Font font = _fontCache.get(descriptor);
		if(font == null)
		{
			font = new Font(family, style, height);
			_fontCache.put(descriptor, font);
		}
		return font;
	}
	
	private void drawPointPoints(CanvasType dest,
			ArrayList<NamedWorldLocation> points, NEFeatureStyle style)
	{
		if (points == null)
			return;

		if(style.getTextColor() == null)
			return;
		
		// this method handles labels and points, so drop out if neither are of interest
		if(!style.isShowLabels() && !style.isShowPoints())
			return;
		

		dest.setColor(style.getTextColor());		
		Font font = fontFor(style.getTextHeight(), style.getTextStyle(), style.getTextFont());
		dest.setFont(font);

		// store the screen size
		WorldArea visArea = dest.getProjection().getVisibleDataArea();

		// ok, loop through the polys
		Iterator<NamedWorldLocation> iter = points.iterator();
		while (iter.hasNext())
		{
			NamedWorldLocation namedLoc = (NamedWorldLocation) iter.next();
			final WorldLocation next = namedLoc;

			// check if it's visible
			if (visArea.contains(next))
			{
				final Point thisP = dest.toScreen(next);
				dest.drawText(namedLoc.getName(), thisP.x, thisP.y);
			}
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

	@Override
	public void setLayers(Layers parent)
	{
		_theLayers = parent;
	}

}


