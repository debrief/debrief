package MWC.Algorithms.LiveData;

import java.beans.PropertyChangeListener;
import java.util.Vector;

/** a watchable attribute. Note, attributes are indexed - thus this class can store the temporal variation 
 * of a number of indexed attributes
 * 
 * @author ianmayo
 *
 */
public interface IAttribute
{
	
	/** representation of an attribute and index couplet
	 * 
	 * @author ianmayo
	 *
	 */
	public class IndexedAttribute
	{
		public final Object index;
		public final IAttribute attribute;
		public IndexedAttribute(final Object _index, final IAttribute _attribute)
		{
			index = _index;
			attribute = _attribute;
		}
	}
			
	/**
	 * name of this attribute
	 * 
	 * @return name
	 */
	public String getName();
	
	/** the units of this attribute, typically used for when plotting this attribute graphically.
	 * 
	 */
	public String getUnits();

	/**
	 * current value of this attribute
	 * @param index what we're after the current value of
	 * 
	 * @return value
	 */
	public DataDoublet getCurrent(Object index);
	
	/** whether this is a significant attribute, displayed by default
	 * @return yes/no
	 */
	public boolean isSignificant();

	/**
	 * start listening to this attribute
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * stop listening to this attribute
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * retrieve past values for this attribute type
	 * @param index the index of the list for which we want current values
	 * 
	 * @return collection of time-stamped observations
	 */
	public Vector<DataDoublet> getHistoricValues(Object index);

	/**
	 * record change in value, pass to listeners
	 * 
	 * @param time
	 * @param newValue
	 */
//	public void fireUpdate(long time, Object newValue);

}
