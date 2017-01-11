package org.mwc.debrief.core.ui;

import org.eclipse.jface.dialogs.MessageDialog;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument.QuestionHelper;

public class SWTEclipseHelper implements QuestionHelper
{

  @Override
  public boolean askYes(String title, String question)
  {
    MessageDialog dialog =
        new MessageDialog(null, title, null, question, MessageDialog.QUESTION,
            new String[]
            {"Yes", "No"}, 0); // yes is the default
    int result = dialog.open();

    return result == 0;
  }

}
