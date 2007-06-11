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
  public SteppingBoundedInteger(int current,
                                int min,
                                int max,
                                int step)
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


	public void setCurrent(int val){ _current = val;}
	
	public boolean equals(Object o)
	{
		boolean res = false;
		if(o instanceof SteppingBoundedInteger)
		{
			SteppingBoundedInteger other = (SteppingBoundedInteger)o;
			res = (other.getCurrent() == this.getCurrent());
		}
		
		return res;
	}

}
