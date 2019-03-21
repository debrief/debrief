package org.mwc.debrief.lite.outline;

import java.util.ArrayList;

import MWC.GUI.Plottable;

public class LogicHelpers
{
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
    boolean isEnabled(final Helper helper)
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

    abstract boolean isEnabled(final Helper helper);

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
    boolean isEnabled(final Helper helper)
    {
      for (final EnabledTest t : _tests)
      {
        if (t.isEnabled(helper))
          return true;
      }
      return false;
    }
  }
}
