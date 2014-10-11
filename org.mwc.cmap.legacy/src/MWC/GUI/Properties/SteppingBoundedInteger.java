/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
