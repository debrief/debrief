package org.mwc.debrief.core.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument.QuestionHelper;

public class SWTEclipseHelper implements QuestionHelper
{

  private boolean answer = false;
  
  @Override
  public boolean askYes(final String title, final String question)
  {

    final Display targetDisplay;
    if(Display.getCurrent() == null)
    {
      targetDisplay = Display.getDefault();
    }
    else
    {
      targetDisplay = Display.getCurrent();
    }
    
    targetDisplay.syncExec(new Runnable(){

      @Override
      public void run()
      {
        MessageDialog dialog =
            new MessageDialog(null, title, null, question, MessageDialog.QUESTION,
                new String[]
                {"Yes", "No"}, 0); // yes is the default
        answer = dialog.open() == 0;
      }});
    

    return answer;
  }

}
