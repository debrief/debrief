package MWC.GUI.Shapes.Symbols;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SymbolFactoryPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SymbolFactoryPropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:37:29  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:34  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:53+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:22+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:06+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:14+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-17 13:26:39+00  novatech
// All property editors which return a subset of the full range of symbols
//
// Revision 1.1  2001-01-03 13:42:13+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:10  ianmayo
// initial version
//
// Revision 1.3  2000-11-24 11:51:26+00  ian_mayo
// correct typo
//
// Revision 1.2  2000-11-22 10:37:12+00  ian_mayo
// allow use of strings without spaces (for XML files)
//
// Revision 1.1  2000-09-26 10:52:04+01  ian_mayo
// Initial revision
//

import java.beans.PropertyEditorSupport;

public class SymbolFactoryPropertyEditor extends PropertyEditorSupport
{

  protected String _mySymbolType;

  public String[] getTags()
  {
    // retrieve the set of symbols from the factory
    String[] _theSymbols = SymbolFactory.getSymbolList();

    return _theSymbols;
  }

  public Object getValue()
  {
    return _mySymbolType;
  }



  public void setValue(Object p1)
  {
    if(p1 instanceof String)
    {
      String val = (String) p1;
      setAsText(val);
    }else if(p1 instanceof Integer)
    {
    	Integer index = (Integer) p1;
			setAsText(getTags()[index ]);
    }
  }

  public void setAsText(String val)
  {
    _mySymbolType = val;
  }


  public String getAsText()
  {
    return _mySymbolType;
  }

  //////////////////////////////////////////////////////////////////
  // class which performs just as normal symbol property lister, but which only
  // returns the list of vessel types
  ///////////////////////////////////////////////////////////////////
  public static class SymbolFactoryVesselPropertyEditor extends SymbolFactoryPropertyEditor
  {
    public String[] getTags()
    {
      // retrieve the set of symbols from the factory
      String[] _theSymbols = SymbolFactory.getVesselSymbolList();

      return _theSymbols;
    }
  }

  //////////////////////////////////////////////////////////////////
  // class which performs just as normal symbol property lister, but which only
  // returns the list of buoy types
  ///////////////////////////////////////////////////////////////////
  public static class SymbolFactoryBuoyPropertyEditor extends SymbolFactoryPropertyEditor
  {
    public String[] getTags()
    {
      // retrieve the set of symbols from the factory
      String[] _theSymbols = SymbolFactory.getBuoySymbolList();

      return _theSymbols;
    }
  }

}

