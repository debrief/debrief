/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package Debrief.GUI.Views;

import java.util.ArrayList;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Plottable;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import junit.framework.TestCase;

public class LogicHelpers
{
  public static class TestLogic extends TestCase
  {
    public void testSelectionEmptyFail()
    {
      EnabledTest test = getSelectionEmptyTest();
      Helper helper = new Helper() {

        @Override
        public ArrayList<Plottable> getClipboardContents()
        {
          return null;
        }

        @Override
        public ArrayList<Plottable> getSelection()
        {
          ArrayList<Plottable> res= new ArrayList<Plottable>();
          return res;
        }};
      assertTrue(test.isEnabled(helper));
    }
    
    public void testSelectionEmptyPass()
    {
      EnabledTest test = getSelectionEmptyTest();
      Helper helper = new Helper() {

        @Override
        public ArrayList<Plottable> getClipboardContents()
        {
          return null;
        }

        @Override
        public ArrayList<Plottable> getSelection()
        {
          ArrayList<Plottable> res= new ArrayList<Plottable>();
          res.add(new BaseLayer());
          return res;
        }};
      assertTrue(!test.isEnabled(helper));
    }
    public void testClipboardNotEmptyFail()
    {
      EnabledTest test = getClipboardNotEmptyTest();
      Helper helper = new Helper() {

        @Override
        public ArrayList<Plottable> getClipboardContents()
        {
          ArrayList<Plottable> res= new ArrayList<Plottable>();
          return res;
        }

        @Override
        public ArrayList<Plottable> getSelection()
        {
          return null;
        }};
      assertTrue(!test.isEnabled(helper));
    }
    
    public void testClipboardNotEmptyPass()
    {
      EnabledTest test = getClipboardNotEmptyTest();
      Helper helper = new Helper() {

        @Override
        public ArrayList<Plottable> getClipboardContents()
        {
          ArrayList<Plottable> res= new ArrayList<Plottable>();
          res.add(new BaseLayer());
          return res;
        }

        @Override
        public ArrayList<Plottable> getSelection()
        {
          return null;
        }};
      assertTrue(test.isEnabled(helper));
    }
    
    
    
  }
  
  /** logical AND operation
   * 
   * @author ian
   *
   */
  public static class And extends EnabledTestGroup
  {

    public And(final EnabledTest... tests)
    {
      super("And", tests);
    }

    @Override
    public boolean isEnabled(final Helper helper)
    {
      for (final EnabledTest t : _tests)
      {
        if (!t.isEnabled(helper))
          return false;
      }
      return true;
    }
  }

  /** parent of all tests
   * 
   * @author ian
   *
   */
  public abstract static class EnabledTest
  {
    private final String _name;

    public EnabledTest(final String name)
    {
      _name = name;
    }

    abstract public boolean isEnabled(final Helper helper);

    @Override
    public String toString()
    {
      return _name;
    }
  }

  /** series of tests
   * 
   * @author ian
   *
   */
  public abstract static class EnabledTestGroup extends EnabledTest
  {
    protected EnabledTest[] _tests;

    public EnabledTestGroup(final String name, final EnabledTest... tests)
    {
      super(name);
      _tests = tests;
    }
  }

  /** helper that can provide selection and clipboard contents
   * 
   * @author ian
   *
   */
  public static interface Helper
  {
    /** what's on the clipboard?
     * 
     * @return
     */
    ArrayList<Plottable> getClipboardContents();

    /** what's currently selected?
     * 
     * @return
     */
    ArrayList<Plottable> getSelection();
  }

  /** logical OR method
   * 
   * @author ian
   *
   */
  public static class Or extends EnabledTestGroup
  {

    public Or(final EnabledTest... tests)
    {
      super("Or", tests);
    }

    @Override
    public boolean isEnabled(final Helper helper)
    {
      for (final EnabledTest t : _tests)
      {
        if (t.isEnabled(helper))
          return true;
      }
      return false;
    }
  }
  

  public static EnabledTest getNotNarrativeTest()
  {
    return new EnabledTest("Selection not narrative")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        ArrayList<Plottable> sel = helper.getSelection();
        for (Plottable t : sel)
        {
          if (t instanceof NarrativeEntry || t instanceof NarrativeWrapper)
          {
            return false;
          }
        }
        return true;
      }
    };
  }

  public static EnabledTest getNotEmptyTest()
  {
    return new EnabledTest("Not empty")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        return !helper.getSelection().isEmpty();
      }
    };
  }

  public static EnabledTest getOnlyOneTest()
  {
    return new EnabledTest("Only one")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        return helper.getSelection().size() == 1;
      }
    };
  }

  public static EnabledTest getClipboardNotEmptyTest()
  {
    return new EnabledTest("Clipboard not empty")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        return !helper.getClipboardContents().isEmpty();
      }
    };
  }

  public static EnabledTest getIsTrackTest()
  {
    return new EnabledTest("Selection is track")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        ArrayList<Plottable> sel = helper.getSelection();
        if(sel.size() == 1)
        {
          Plottable first = sel.get(0);
          if(first instanceof TrackWrapper)
          {
            return true;
          }
        }
        return false;
      }
    };
  }

  public static EnabledTest getIsLayerTest()
  {
    return new EnabledTest("Selection is layer")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        ArrayList<Plottable> sel = helper.getSelection();
        if(sel.size() == 1)
        {
          Plottable first = sel.get(0);
          if(first instanceof BaseLayer)
          {
            return true;
          }
        }
        return false;
      }
    };
  }
  public static EnabledTest getNotLayerTest()
  {
    return new EnabledTest("Selection is not layer")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        ArrayList<Plottable> sel = helper.getSelection();
        if(sel.size() == 1)
        {
          Plottable first = sel.get(0);
          if(first instanceof BaseLayer)
          {
            return false;
          }
        }
        return true;
      }
    };
  }
  
  
  public static EnabledTest getIsFixesTest()
  {
    return new EnabledTest("Clipboard is fixes")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        ArrayList<Plottable> sel = helper.getClipboardContents();
        for (Plottable t : sel)
        {
          if (!(t instanceof FixWrapper))
          {
            return false;
          }
        }
        return true;
      }
    };
  }

  public static EnabledTest getIsShapesTest()
  {
    return new EnabledTest("Clipboard is shapes or labels")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        ArrayList<Plottable> sel = helper.getClipboardContents();
        for (Plottable t : sel)
        {
          if (!(t instanceof ShapeWrapper) && !(t instanceof LabelWrapper))
          {
            return false;
          }
        }
        return true;
      }
    };
  }

  public static EnabledTest getSelectionEmptyTest()
  {
    return new EnabledTest("Is empty")
    {
      @Override
      public boolean isEnabled(final Helper helper)
      {
        return helper.getSelection().isEmpty();
      }
    };
  }

}
