package org.mwc.debrief.lite.gui;

import java.util.Map;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.Replay.ImportReplay.ProvidesModeSelector;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;

public class DebriefLiteToolParent implements ToolParent, ProvidesModeSelector  {

	final ImportSettings settings;
    final Long freq;

	public DebriefLiteToolParent(final String mode, final Long freq)
    {

	  	
	  settings = new ImportSettings(mode, freq);
      this.freq = freq;
    }


	@Override
	public void logError(int status, String text, Exception e) {
		
	}

	@Override
	public void logError(int status, String text, Exception e, boolean revealLog) {
		
	}

	@Override
	public void logStack(int status, String text) {
		
	}

	@Override
	public Long getSelectedImportFrequency(String trackName) {
		return null;
	}

	@Override
	public ImportSettings getSelectedImportMode(String trackName) {
		return settings;
	}

	@Override
	public void setCursor(int theCursor) {
	}

	@Override
	public void restoreCursor() {
	}

	@Override
	public void addActionToBuffer(Action theAction) {
	}

	@Override
	public String getProperty(String name) {
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
	public Map<String, String> getPropertiesLike(String pattern) {
		return null;
	}

	@Override
	public void setProperty(String name, String value) {
		
	}

}
