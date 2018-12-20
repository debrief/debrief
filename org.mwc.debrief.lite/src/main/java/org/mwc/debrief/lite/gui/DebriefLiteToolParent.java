package org.mwc.debrief.lite.gui;

import java.util.Map;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.Replay.ImportReplay.ProvidesModeSelector;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;

public class DebriefLiteToolParent implements ToolParent, ProvidesModeSelector
{

  private final ImportSettings settings;
  
  @SuppressWarnings("unused")
  private final Long freq;

  public DebriefLiteToolParent(final String mode, final long freq)
  {
    settings = new ImportSettings(mode, freq);
    this.freq = freq;
  }

  @Override
  public void addActionToBuffer(final Action theAction)
  {
  }

  @Override
  public Map<String, String> getPropertiesLike(final String pattern)
  {
    return null;
  }

  @Override
  public String getProperty(final String name)
  {
    if (name.equals(ImportReplay.TRACK_IMPORT_MODE))
    {
      return settings.importMode;
    }
    else if (name.equals(ImportReplay.RESAMPLE_FREQUENCY))
    {
      return "" + settings.sampleFrequency;
    }
    else
    {
      return null;
    }
  }

  @Override
  public Long getSelectedImportFrequency(final String trackName)
  {
    return null;
  }

  @Override
  public ImportSettings getSelectedImportMode(final String trackName)
  {
    return settings;
  }

  @Override
  public void logError(final int status, final String text, final Exception e)
  {

  }

  @Override
  public void logError(final int status, final String text, final Exception e,
      final boolean revealLog)
  {

  }

  @Override
  public void logStack(final int status, final String text)
  {

  }

  @Override
  public void restoreCursor()
  {
  }

  @Override
  public void setCursor(final int theCursor)
  {
  }

  @Override
  public void setProperty(final String name, final String value)
  {

  }

}
