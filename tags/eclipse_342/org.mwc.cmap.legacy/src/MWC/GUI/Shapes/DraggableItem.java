package MWC.GUI.Shapes;

import java.awt.Point;

import MWC.GUI.*;
import MWC.GenericData.*;

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
			DraggableItem.LocationConstruct currentNearest, Layer parentLayer);

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

		public void setData(DraggableItem p, WorldDistance dist, WorldLocation hotSpot,
				Layer topLayer)
		{
			_object = p;
			_distance = new WorldDistance(dist);
			_topLayer = topLayer;
			// _hotSpot = hotSpot;
		}

		public void checkMe(DraggableItem p, WorldDistance dist, WorldLocation hotSpot,
				Layer topLayer)
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