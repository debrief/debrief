package ASSET.GUI.Editors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */
import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.Iterator;

import ASSET.Models.Decision.TargetType;

abstract public class TargetTypeEditor extends
           PropertyEditorSupport  {

  TargetType _myType;

  abstract protected void setText(String val);

  abstract public java.awt.Component getCustomEditor();

  public void setValue(final Object p1)
  {
    if(p1 instanceof TargetType)
    {
      _myType = (TargetType)p1;

      resetData();
    }
    else
      return;
  }

  public boolean supportsCustomEditor()
  {
    return true;
  }

  public Object getValue()
  {
    return _myType;
  }

  void resetData()
  {
    if(_myType == null)
      return;

    // get our categories
    final Collection<String> coll = _myType.getTargets();

    if(coll == null)
      return;

    final Iterator<String> it = coll.iterator();

    String types = "";
    while (it.hasNext())
    {
      final String thisType = (String) it.next();
      types += thisType + ", ";
    }

    setText(types);
  }

}