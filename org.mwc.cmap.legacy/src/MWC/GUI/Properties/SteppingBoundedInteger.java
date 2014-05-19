/*
 * SteppingBoundedInteger.java
 *
 * Created on 29 September 2000, 14:01
 */

package MWC.GUI.Properties;

/**
 *
 * @author  IAN MAYO
 * @version 
 */
final public class SteppingBoundedInteger extends java.beans.PropertyEditorSupport 
{

  ///////////////////////////////////
  // member variables
  //////////////////////////////////
	protected int _current;
	protected int _min;
	protected int _max;	
  protected int _step;
  
  ///////////////////////////////////
  // constructor
  //////////////////////////////////
  /** Creates new SteppingBoundedInteger */
  public SteppingBoundedInteger(final int current,
                                final int min,
                                final int max,
                                final int step)
  {
		_current = current;
		_min = min;
		_max = max;
    _step = step;
  }
  
	
  ///////////////////////////////////
  // member functions
  //////////////////////////////////
	public int getCurrent(){ return _current; }
	public int getMin(){ return _min; }
	public int getMax(){ return _max; }
  public int getStep()
  {
    return _step;
  }


	public void setCurrent(final int val){ _current = val;}
	
	public boolean equals(final Object o)
	{
		boolean res = false;
		if(o instanceof SteppingBoundedInteger)
		{
			final SteppingBoundedInteger other = (SteppingBoundedInteger)o;
			res = (other.getCurrent() == this.getCurrent());
		}
		
		return res;
	}

}
