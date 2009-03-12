package ASSET.Util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */


public class DebriefLogger implements java.beans.PropertyChangeListener {

  private static java.io.FileWriter os;

  private DebriefLogger(final String path) {
    try{
      os = new java.io.FileWriter(path);
      System.out.println("File opened");

      try
      {
        os.write(";; ASSET Output" + new java.util.Date());
        os.write("" + System.getProperty("line.separator"));
        os.flush();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public DebriefLogger() {
      this("c:\\res" + System.currentTimeMillis() + ".rep");
  }



  public void propertyChange(java.beans.PropertyChangeEvent pe)
  {
/*    String res = null;

    if(pe.getPropertyName() == ASSET.ServerType.DECISION)
    {
      ASSET.ParticipantType pt = (ASSET.ParticipantType)pe.getNewValue();
      ASSET.Participants.Status stat = pt.getStatus();
      MWC.GenericData.WorldLocation loc = stat.getLocation();

      String locStr = MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(loc);
      String dateStr = MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(stat.getTime());

      res = dateStr + " " + pt.getName() + " @C " + locStr + " " + df.format(stat.getCourse()) + df.format(stat.getSpeed()) + df.format(loc.getDepth());

    }
    else if(pe.getPropertyName() == ASSET.ServerType.DECISION)
    {
      ASSET.Participants.DemandedStatus ds = (ASSET.Participants.DemandedStatus)pe.getNewValue();
      double demCourse = ds.getCourse();
      double demSpd = ds.getSpeed();
      double demDepth = ds.getDepth();

      String dateStr = MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(ds.getTime());

//;NARRATIVE:  YYMMDD HHMMSS  TTT.TTT  XX..XX
//;; dtg, track name, narrative entry


      res = ";NARRATIVE: " + dateStr + " " + "track_name" + " " + "message";

    }



    if(res != null)
    {
      try
      {
        os.write(res);
        os.write("" + System.getProperty("line.separator"));
        os.flush();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }*/
  }

  public void setServer(ASSET.ServerType server)
  {
  //  server.addListener(ASSET.UPDATE, this);
  }

  public void removeServer(ASSET.ServerType server)
  {
//    server.removeListener(ASSET.UPDATE, this);
  }

  static public void trace(final String msg)
  {
    try
    {
      os.write(msg + System.getProperty("line.separator"));
      os.flush();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }


}