package Debrief.Wrappers.Extensions;

import java.util.ArrayList;

/** store list of data items of unpredictable type (since they're provided by extension)
 * 
 * @author ian
 *
 */
public class AdditionalData extends ArrayList<Object>
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  /** get a child item of the provided type
   * 
   * @param clazz
   * @return
   */
  public Object getThisType(Class<?> clazz)
  {
    Object res = null;
    
    // loop through our data
    for(Object item: this)
    {
      if(item.getClass().equals(clazz))
      {
        res = item;
      }
    }
    
    return res;
  }

}
