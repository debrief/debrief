package MWC.GUI.Properties;

/** class to represent a bounded integer value
 *
 */
public class BoundedInteger
{
	
  ///////////////////////////////////
  // member variables
  //////////////////////////////////
  /** the current value
   *
   */
	protected int _current;

  /** the minimum value which may be reached
   *
   */
	protected int _min;

  /** the maximum value which may be reached
   *
   */
	protected int _max;
	

  ///////////////////////////////////
  // constructor
  //////////////////////////////////
	public BoundedInteger(int current,
												int min,
												int max)
	{
		_current = current;
		_min = min;
		_max = max;
	}

	
  ///////////////////////////////////
  // member functions
  //////////////////////////////////
  /** get the current value
   *
   */
	public int getCurrent(){ return _current; }
  /** get the minimum allowable value
   *
   */
	public int getMin(){ return _min; }
	public int getMax(){ return _max; }

	public void setCurrent(int val){ _current = val;}
	
	public boolean equals(Object o)
	{
		boolean res = false;
		if(o instanceof BoundedInteger)
		{
			BoundedInteger other = (BoundedInteger)o;
			res = (other.getCurrent() == this.getCurrent());
		}
		
		return res;
	}
  ///////////////////////////////////
  // nested classes
  //////////////////////////////////
}
