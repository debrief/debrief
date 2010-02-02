package interfaces;

import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;

import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;

/** description of an operation that can be run from the time controller
 * 
 * @author ianmayo
 *
 */
public interface TimeControllerOperation
{
	
	public static class TimeControllerOperationStore extends Vector<TimeControllerOperation>
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	/** execute this operation
	 * 
	 * @param primary the primary track
	 * @param secondaries the secondary track
	 * @param period the period for which to apply the operation
	 */
	public void run(final WatchableList primary, final WatchableList[] secondaries, final TimePeriod period);

	/** how to label this operation
	 * 
	 * @return a label to display
	 */
	public String getName();
	
	/** the image to display for this operation
	 * 
	 * @return a descriptor to display
	 */
	public ImageDescriptor getDescriptor();
	
}
