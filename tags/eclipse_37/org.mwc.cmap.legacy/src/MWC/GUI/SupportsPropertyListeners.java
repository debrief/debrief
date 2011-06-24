package MWC.GUI;

public interface SupportsPropertyListeners
{

	public void addPropertyChangeListener(String property,
			java.beans.PropertyChangeListener listener);

	/**
	 * add this listener which wants to hear about all changes
	 */
	public void addPropertyChangeListener(
			java.beans.PropertyChangeListener listener);

	/**
	 * remove this listener which wants to hear about all changes
	 */
	public void removePropertyChangeListener(
			java.beans.PropertyChangeListener listener);

	public void removePropertyChangeListener(String property,
			java.beans.PropertyChangeListener listener);

	/** something has changed - tell everybody
	 * 
	 * @param propertyChanged
	 * @param oldValue
	 * @param newValue
	 */
	public void firePropertyChange(String propertyChanged, Object oldValue,
			Object newValue);

}