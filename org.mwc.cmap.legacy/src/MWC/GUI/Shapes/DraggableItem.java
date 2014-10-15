/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Shapes;

import java.awt.Point;

import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * marker interface for objects that can be dragged on-screen
 * 
 * @author ian.mayo
 */
public interface DraggableItem
{

	/**
	 * paint the shape onto the destination. note that the subject knows <i> where
	 * </i> to plot itself to
	 * 
	 * @param dest -
	 *          the place to paint to
	 */
	public void paint(CanvasType dest);

	/**
	 * move the object by the supplied distance/direction
	 * 
	 * @param vector
	 */
	public void shift(WorldVector vector);

	/**
	 * ok - see how far we are from the cursor
	 * 
	 * @param cursorPos
	 *          the current mouse cursor position
	 * @param cursorLoc
	 *          that position in world coordinates
	 * @param currentNearest
	 *          the 'running total' of nearest points
	 * @param parentLayer
	 *          the layer to update when drag is complete
	 */
	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			DraggableItem.LocationConstruct currentNearest, Layer parentLayer, Layers theLayers);

	/**
	 * get the name of the item, so we can place a detailed description on the
	 * undo buffer
	 * 
	 * @return name of this item
	 */
	public String getName();

	/**
	 * composite object used for finding nearest dragable items
	 * 
	 * @author ian.mayo
	 */
	public static class LocationConstruct
	{
		/**
		 * the object we've found
		 */
		public DraggableItem _object = null;

		/**
		 * how far the object is from the current cursor point
		 */
		public WorldDistance _distance = null;

		/**
		 * the layer we have to update if this item is moved
		 */
		public Layer _topLayer;

		// private WorldLocation _hotSpot;

		public void setData(final DraggableItem p, final WorldDistance dist, final WorldLocation hotSpot,
				final Layer topLayer)
		{
			_object = p;
			_distance = new WorldDistance(dist);
			_topLayer = topLayer;
			// _hotSpot = hotSpot;
		}

		public void checkMe(final DraggableItem p, final WorldDistance dist, final WorldLocation hotSpot,
				final Layer topLayer)
		{
			if (!populated())
			{
				setData(p, dist, hotSpot, topLayer);
			}
			else
			{
				if (dist != null)
				{
					if (dist.lessThan(_distance))
						setData(p, dist, hotSpot, topLayer);
				}
			}
		}

		public boolean populated()
		{
			return _distance != null;
		}
	}

}