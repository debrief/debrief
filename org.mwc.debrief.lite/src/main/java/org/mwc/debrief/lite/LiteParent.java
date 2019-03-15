package org.mwc.debrief.lite;

import java.util.Map;

import MWC.GUI.Tools.Action;

public class LiteParent implements MWC.GUI.ToolParent
{

  @Override
  public void logError(int status, String text, Exception e)
  {
    System.err.println(text);
    if(e != null)
      e.printStackTrace();
  }

  @Override
  public void logError(int status, String text, Exception e, boolean revealLog)
  {
    System.err.println(text);
    if(e != null)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void logStack(int status, String text)
  {
    throw new IllegalArgumentException("not implemented (yet)");
  }

  @Override
  public void setCursor(int theCursor)
  {
    throw new IllegalArgumentException("not implemented (yet)");
  }

  @Override
  public void restoreCursor()
  {
    throw new IllegalArgumentException("not implemented (yet)");
  }

  @Override
  public void addActionToBuffer(Action theAction)
  {
    throw new IllegalArgumentException("not implemented (yet)");
  }

  @Override
  public String getProperty(String name)
  {
    System.err.println("Failed to return property:" + name
        + " (LiteParent not yet implemented)");
    return null;
  }

  @Override
  public Map<String, String> getPropertiesLike(String pattern)
  {
    throw new IllegalArgumentException("not implemented (yet)");
  }

  @Override
  public void setProperty(String name, String value)
  {
    throw new IllegalArgumentException("not implemented (yet)");
  }
}
