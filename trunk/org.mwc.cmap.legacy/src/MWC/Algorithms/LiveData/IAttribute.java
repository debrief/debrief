package MWC.Algorithms.LiveData;

import java.beans.PropertyChangeListener;
import java.util.Vector;

public interface IAttribute
{
	/**
	 * name of this attribute
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * current value of this attribute
	 * 
	 * @return value
	 */
	public DataDoublet getCurrent();
	
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
	 * 
	 * @return collection of time-stamped observations
	 */
	public Vector<DataDoublet> getHistoricValues();

	/**
	 * record change in value, pass to listeners
	 * 
	 * @param time
	 * @param newValue
	 */
//	public void fireUpdate(long time, Object newValue);

}
