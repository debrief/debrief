package MWC.GUI.Dialogs.AWT;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Dialog;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class QuestionDialogLauncher extends Panel
  implements DialogClient,
  ActionListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Applet applet;
  private QuestionDialog questionDialog;
  private final Checkbox modal = new Checkbox("modal");

  private Button questionDialogButton;

  public QuestionDialogLauncher(final Applet applet)
  {
    this.applet = applet;

    add(modal);
    add(questionDialogButton =
        new Button("Launch Question Dialog"));

    questionDialogButton.addActionListener(this);
  }

  public void actionPerformed(final ActionEvent event)
  {
    final Image image = applet.getImage(applet.getCodeBase(),
                                  "gifs/book.gif");
    if (questionDialog == null)
    {
      questionDialog =
        new QuestionDialog(Util.getFrame(this), this,
                           "Example Question Dialog",
                           "Book Of The Month:  ",
                           "The Hobbit",
                           45, image, modal.getState());
    }
    if (modal.getState())
      questionDialog.setModal(true);
    else
      questionDialog.setModal(false);

    questionDialog.setVisible(true);
  }

  public void dialogDismissed(final Dialog d)
  {
    if (questionDialog.wasCancelled())
      applet.showStatus("CANCELLED");
    else
      applet.showStatus("Book Of The Month:  " +

                        questionDialog.getTextField().getText());
  }

  public void dialogCancelled(final Dialog d)
  {
    applet.showStatus("Dialog Cancelled");
  }
}
