package org.mwc.debrief.lite.util;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;

public class DoSave extends DoSaveAs
{

  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public DoSave(Session session)
  {
    super(session);
  }
  
  @Override
  public void actionPerformed(ActionEvent e)
  {
    // get the current file path,
    // check we have write permission to it before starting save
    try
    {
      OutputStream stream = new FileOutputStream("test_out.xml");
      performSave(stream);
    }
    catch (FileNotFoundException e1)
    {
      Application.logError2(Application.ERROR, "Can't find file", e1);
    }
  }

}
