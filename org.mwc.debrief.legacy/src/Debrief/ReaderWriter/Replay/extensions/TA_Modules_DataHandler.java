package Debrief.ReaderWriter.Replay.extensions;


public class TA_Modules_DataHandler extends TA_ForeAft_DataHandler
{

  public TA_Modules_DataHandler()
  {
    super("TA_MODULES", "Acoustic Modules");
  }

  @Override
  protected String nameForRow(int ctr)
  {
    return "" + ctr;
  }
}
