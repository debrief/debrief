package org.mwc.debrief.lite.util;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.AbstractAction;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;

public class DoSaveAs extends AbstractAction
{

  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final Session _session;

  public DoSaveAs(Session session)
  {
    _session = session;
  }
  
  protected void performSave(final OutputStream fos)
  {
    DebriefXMLReaderWriter.exportThis(_session, fos);
  }
  
  @Override
  public void actionPerformed(ActionEvent e)
  {
    // get a new file path to use.
    
    // if it already exists, check with rename/cancel
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
