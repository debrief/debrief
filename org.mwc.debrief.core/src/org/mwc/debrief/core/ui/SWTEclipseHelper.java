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
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument.QuestionHelper;

/**
 * utility to ask a question, in SWT
 *
 * @author Ian
 *
 */
public class SWTEclipseHelper implements QuestionHelper
{

  /**
   * This class validates a String. It makes sure that the String is between 5 and 8 characters
   */
  class LengthValidator implements IInputValidator
  {
    /**
     * Validates the String. Returns null for no error, or an error message
     *
     * @param newText
     *          the String to validate
     * @return String
     */
    @Override
    public String isValid(final String newText)
    {
      final int len = newText.length();

      // Determine if input is too short or too long
      if (len < 2)
        return "Too short";

      // Input must be OK
      return null;
    }
  }

  @Override
  public String askQuestion(final String title, final String question,
      final String defaultStr)
  {
    // allow the answer to be shared across threads
    final AtomicReference<String> answerVal = new AtomicReference<String>();

    // get a display to open on
    final Display targetDisplay;
    if (Display.getCurrent() == null)
    {
      targetDisplay = Display.getDefault();
    }
    else
    {
      targetDisplay = Display.getCurrent();
    }

    // ok, get the answer
    targetDisplay.syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        final InputDialog input = new InputDialog(null, title, question,
            defaultStr, new LengthValidator());
        if (input.open() == Window.OK)
        {
          // User clicked OK; update the label with the input
          answerVal.set(input.getValue());
        }
      }
    });
    return answerVal.get();
  }

  @Override
  public boolean askYes(final String title, final String question)
  {
    // allow the answer to be shared across threads
    final AtomicBoolean answerVal = new AtomicBoolean();

    // get a display to open on
    final Display targetDisplay;
    if (Display.getCurrent() == null)
    {
      targetDisplay = Display.getDefault();
    }
    else
    {
      targetDisplay = Display.getCurrent();
    }

    // ok, get the answer
    targetDisplay.syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        final MessageDialog dialog = new MessageDialog(null, title, null,
            question, MessageDialog.QUESTION, new String[]
        {"Yes", "No"}, 0); // yes is the default
        answerVal.set(dialog.open() == 0);
      }
    });
    return answerVal.get();
  }

  @Override
  public void showMessage(final String title, final String message)
  {
    // get a display to open on
    final Display targetDisplay;
    if (Display.getCurrent() == null)
    {
      targetDisplay = Display.getDefault();
    }
    else
    {
      targetDisplay = Display.getCurrent();
    }

    // ok, get the answer
    targetDisplay.syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        MessageDialog.openError(null, title, message);
      }
    });
  }
  
  public void showErrorLog()
  {
    try
    {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.pde.runtime.LogView");
    }
    catch (PartInitException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  

}
