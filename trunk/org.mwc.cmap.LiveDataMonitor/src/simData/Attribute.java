package simData;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;

public class Attribute implements IAttribute
{

	public static final String VALUE = null;

	/**
	 * name of this attribute
	 * 
	 */
	private String _name;

	/**
	 * support for property listeners
	 * 
	 */
	PropertyChangeSupport _pSupport;

	/** whether this attribute is worthy of display by default
	 * 
	 */
	private boolean _isSignificant;
	
	/** daddies little helper
	 * 
	 */
	private AttributeHelper _myHelper;

	/**
	 * constructor for an instance
	 * 
	 * @param name
	 */
	public Attribute(String name, boolean isSignificant)
	{
		_isSignificant = isSignificant;
		
		// remember the name
		_name = name;

		// get the support ready
		_pSupport = new PropertyChangeSupport(this);

		// get our helper ready.
		_myHelper = new AttributeHelper(_pSupport);

	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		_pSupport.addPropertyChangeListener(listener);
	}

	@Override
	public String getName()
	{
		return _name;
	}

	@Override
	public Vector<DataDoublet> getHistoricValues()
	{
		return _myHelper.getHistoricValues();
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		_pSupport.removePropertyChangeListener(listener);
	}

	/** this has changed, store it & tell the listeners
	 * 
	 * @param time
	 * @param newValue
	 */
	public void fireUpdate(long time, Object newValue)
	{
		_myHelper.newData(time, newValue);
	}

	@Override
	public DataDoublet getCurrent()
	{
		return _myHelper.getCurrent();
	}

	@Override
	public boolean isSignificant()
	{
		return _isSignificant;
	}

	@Override
	public String toString()
	{
		return getName() + ":" + getCurrent().getValue();
	}
	
	/** convenience class to help objects that can't inherit from Attribute class
	 * 
	 * @author ianmayo
	 *
	 */
	public static class AttributeHelper
	{
		/** the collection of data items
		 * 
		 */
    private	Vector<DataDoublet> _historicData;
    
    /** the property listeners for this object
     * 
     */
    private PropertyChangeSupport _pSupport;
		
    /** constructor, to get us moving
     * 
     * @param pSupport
     */
		public AttributeHelper(PropertyChangeSupport pSupport)
		{
			_pSupport = pSupport;
		}
		
		/** store the new data update
		 * 
		 * @param time
		 * @param value
		 */
		public void newData(long time, Object value)
		{		
			DataDoublet newD = new DataDoublet(time, value);
			DataDoublet oldD = getCurrent();

			// store the new value
			getHistoricValues().add(newD);
			
			// and tell the chaps
			_pSupport.firePropertyChange(VALUE, oldD, newD);
			
		}
		
		/** find the most recent data item
		 * 
		 * @return
		 */
		public DataDoublet getCurrent()
		{
			DataDoublet res = null;
			if(getHistoricValues().size() > 0)
				res = getHistoricValues().lastElement();
			
			return res;
		}
		
		/** get the past values
		 * 
		 * @return
		 */
		public Vector<DataDoublet> getHistoricValues()
		{
			if(_historicData == null)
				_historicData = new Vector<DataDoublet>();
			
			return _historicData;
		}

	}
	
	
}
