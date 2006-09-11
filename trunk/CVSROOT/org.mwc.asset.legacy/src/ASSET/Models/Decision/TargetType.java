package ASSET.Models.Decision;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Participants.Category;

import java.util.Iterator;
import java.util.Vector;

public class TargetType implements java.io.Serializable
{

  //////////////////////////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private java.util.Vector _myTargets;

  //////////////////////////////////////////////////////////////////////
  // constructors
  //////////////////////////////////////////////////////////////////////
  public TargetType()
  {
  }

  public TargetType(final String target)
  {
    addTargetType(target);
  }

  public TargetType(final TargetType other)
  {
    _myTargets = new Vector(0, 1);
    _myTargets.addAll(other._myTargets);
  }

  //////////////////////////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////////////////////////
  /**
   * see if this target type matches the supplied category
   *
   * @param thisCategory the target type to match
   * @return yes/no
   * @see Category.Force.RED for example
   */
  public boolean matches(final Category thisCategory)
  {
    boolean res = false;

    int num_matches = 0;

    if (_myTargets != null)
    {

      final java.util.Enumeration enumer = _myTargets.elements();
      while (enumer.hasMoreElements())
      {
        final String thisType = (String) enumer.nextElement();
        if (thisCategory.isA(thisType))
        {
          num_matches++;
        }
      }

      // how many did we find?
      if (num_matches == _myTargets.size())
        res = true;
    }

    return res;
  }

  /**
   * add a target type to this behaviour
   *
   * @param type a ASSET.Particiants.Category
   */
  public void addTargetType(final String type)
  {
    if (type == null)
      MWC.Utilities.Errors.Trace.trace("Trying to add null type to target type");
    else
    {
      if (_myTargets == null)
        _myTargets = new java.util.Vector(1, 1);

      _myTargets.addElement(type);
    }
  }

  public void removeTargetType(final String val)
  {
    if (_myTargets == null)
      _myTargets = new java.util.Vector(1, 1);

    if (_myTargets != null)
      _myTargets.remove(val);
  }

  public java.util.Collection getTargets()
  {
    if (_myTargets == null)
      _myTargets = new java.util.Vector(1, 1);

    return _myTargets;
  }

  public String toString()
  {
    final StringBuffer res = new StringBuffer();
    final Iterator it = _myTargets.iterator();
    while (it.hasNext())
    {
      final String thisCat = (String) it.next();
      res.append(thisCat);
      res.append(", ");
    }

    return res.toString();
  }

}