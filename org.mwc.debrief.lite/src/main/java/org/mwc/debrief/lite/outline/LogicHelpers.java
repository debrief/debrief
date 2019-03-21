package org.mwc.debrief.lite.outline;

import java.util.ArrayList;

import MWC.GUI.Plottable;

public class LogicHelpers
{
  public static interface Helper
  {
    ArrayList<Plottable> getSelection();
    ArrayList<Plottable> getClipboardContents();
  }
  
  public abstract static class EnabledTest
  {
    private final String _name;
    public EnabledTest(final String name)
    {
      _name = name;
    }
    @Override
    public String toString()
    {
      return _name;
    }
    abstract boolean isEnabled(final Helper helper);
  }

  public abstract static class EnabledTestGroup extends EnabledTest
  {
    protected EnabledTest[] _tests;

    public EnabledTestGroup(final String name, EnabledTest... tests)
    {
      super(name);
      _tests = tests;
    }
  }
  
  public static class Or extends EnabledTestGroup
  {

    public Or(EnabledTest... tests)
    {
      super("Or", tests);
    }

    @Override
    boolean isEnabled(final Helper helper)
    {
      for (EnabledTest t : _tests)
      {
        if (t.isEnabled(helper))
          return true;
      }
      return false;
    }
  }
  
  
  public static class And extends EnabledTestGroup
  {

    public And(EnabledTest... tests)
    {
      super("And", tests);
    }

    @Override
    boolean isEnabled(final Helper helper)
    {
      for (EnabledTest t : _tests)
      {
        if (!t.isEnabled(helper))
          return false;
      }
      return true;
    }
  }
}
