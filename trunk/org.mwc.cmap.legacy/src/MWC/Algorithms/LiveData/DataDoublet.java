package MWC.Algorithms.LiveData;

/**
 * object that stores time-stamped data observation
 * 
 * @author ianmayo
 * 
 */
public class DataDoublet
{
	/**
	 * time of this observation
	 * 
	 */
	private final long _time;

	/**
	 * value of this observation
	 * 
	 */
	private final Object _value;

	/**
	 * constructor for a data observation
	 * 
	 * @param time
	 * @param value
	 */
	public DataDoublet(final long time, final Object value)
	{
		_time = time;
		_value = value;
	}

	/**
	 * retrieve time of observation
	 * 
	 * @return
	 */
	public long getTime()
	{
		return _time;
	}

	/**
	 * retrieve observation
	 * 
	 * @return
	 */
	public Object getValue()
	{
		return _value;
	}

}
