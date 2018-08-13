package org.mwc.debrief.core.ui;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument.ImportNarrativeEnum;
import Debrief.ReaderWriter.Word.ImportNarrativeDocument.TrimNarrativeHelper;

/**
 * Helper class to pop up dialog to offer choice to analyst
 * to import all data or loaded tracks 
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class ImportNarrativeHelper implements TrimNarrativeHelper
{

  private static final String PREF_DEF_NARRATIVE_CHOICE = "defaultNarrativeEntryChoice";
  @Override
  public ImportNarrativeEnum findWhatToImport()
  {
    final Display targetDisplay;
    String defaultChoice = CorePlugin.getDefault().getPreference(PREF_DEF_NARRATIVE_CHOICE);
    if(defaultChoice!=null && !defaultChoice.isEmpty()) {
      return ImportNarrativeEnum.getByName(defaultChoice);
    }
    else {
      final StringBuilder retVal = new StringBuilder();
      if(Display.getCurrent() == null)
      {
        targetDisplay = Display.getDefault();
      }
      else
      {
        targetDisplay = Display.getCurrent();
      }
      
      // ok, get the answer
      targetDisplay.syncExec(new Runnable(){
        @Override
        public void run()
        {
          ImportNarrativeDialog dialog =
              new ImportNarrativeDialog(targetDisplay.getActiveShell());
          if(dialog.open()==Window.OK) {
            ImportNarrativeEnum userChoice = dialog.getUserChoice();
            retVal.append(userChoice.getName());
            if(dialog.getPreference()) {
              CorePlugin.getDefault().getPreferenceStore().setValue(PREF_DEF_NARRATIVE_CHOICE,userChoice.getName());
            }  
          }else {
            ImportNarrativeEnum userChoice = dialog.getUserChoice();
            retVal.append(userChoice.getName());
          }
          
          
        }});
      return ImportNarrativeEnum.getByName(retVal.toString());
    }
  }
  

}
