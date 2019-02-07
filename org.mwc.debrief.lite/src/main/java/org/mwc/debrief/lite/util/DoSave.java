package org.mwc.debrief.lite.util;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.AbstractAction;

import Debrief.GUI.Frames.Session;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;

public class DoSave extends AbstractAction
{

  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final Session _session;

  public DoSave(Session session)
  {
    _session = session;
  }
  
  @Override
  public void actionPerformed(ActionEvent e)
  {
    OutputStream stream;
    try
    {
      stream = new FileOutputStream("test_out.xml");
      DebriefXMLReaderWriter.exportThis(_session, stream);
    }
    catch (FileNotFoundException e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

}
