/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2017, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument.QuestionHelper;

/** utility to ask a question, in SWT
 * 
 * @author Ian
 *
 */
public class SWTEclipseHelper implements QuestionHelper
{
  
  @Override
  public boolean askYes(final String title, final String question)
  {
    // allow the answer to be shared across threads
    final AtomicBoolean answerVal  = new AtomicBoolean();

    // get a display to open on
    final Display targetDisplay;
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
        MessageDialog dialog =
            new MessageDialog(null, title, null, question, MessageDialog.QUESTION,
                new String[]
                {"Yes", "No"}, 0); // yes is the default
        answerVal.set(dialog.open() == 0);
      }});
    return answerVal.get();
  }

}
