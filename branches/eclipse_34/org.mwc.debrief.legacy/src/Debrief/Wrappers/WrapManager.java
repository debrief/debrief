package Debrief.Wrappers;


import MWC.TacticalData.*;

/**
 * factory class to produce a new wrapper for the item of
 * tactical data passed in
 */
public final class WrapManager
{
  /**
   * determine the type of the object,
   * and get the correct wrapper for it
   *
   * @param theData the Tactical Data item to be wrapped
   * @return a wrapper placed around the data item
   */
  static public MWC.GUI.PlainWrapper wrapThis(final Object theData){
    MWC.GUI.PlainWrapper res = null;
		if(theData instanceof Fix)
		{
      res = new FixWrapper((Fix) theData);
		}
		else if(theData instanceof Track)
		{
      res = new TrackWrapper();
		}

    return res;
  }
}


