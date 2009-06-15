package MWC.GUI.Shapes;

import java.awt.Point;

import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * marker interface for objects that can be dragged on-screen
 * 
 * @author ian.mayo
 */
public interface HasDraggableComponents
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
	 * @param feature the component we're dragging 
	 * @param vector
	 */
	public void shift(WorldLocation feature, WorldVector vector);

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
			HasDraggableComponents.ComponentConstruct currentNearest, Layer parentLayer);

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
	public static class ComponentConstruct
	{
		/**
		 * the object we've found
		 */
		public HasDraggableComponents _object = null;
		
		/** the component we're dragging
		 * 
		 */
		public WorldLocation _draggableComponent = null;

		/**
		 * how far the object is from the current cursor point
		 */
		public WorldDistance _distance = null;

		/**
		 * the layer we have to update if this item is moved
		 */
		public Layer _topLayer;

		// private WorldLocation _hotSpot;

		public void setData(HasDraggableComponents p, WorldDistance dist, WorldLocation hotSpot,
				Layer topLayer, WorldLocation draggableComponent)
		{
			_object = p;
			_distance = new WorldDistance(dist);
			_topLayer = topLayer;
			_draggableComponent = draggableComponent;
			// _hotSpot = hotSpot;
		}

		public void checkMe(HasDraggableComponents p, WorldDistance dist, WorldLocation hotSpot,
				Layer topLayer, WorldLocation draggableComponent)
		{
			if (!populated())
			{
				setData(p, dist, hotSpot, topLayer, draggableComponent);
			}
			else
			{
				if (dist != null)
				{
					if (dist.lessThan(_distance))
						setData(p, dist, hotSpot, topLayer, draggableComponent);
				}
			}
		}

		public boolean populated()
		{
			return _distance != null;
		}
	}

}