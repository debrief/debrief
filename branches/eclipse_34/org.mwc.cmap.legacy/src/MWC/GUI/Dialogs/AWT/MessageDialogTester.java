package MWC.GUI.Dialogs.AWT;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageDialogTester extends Applet
{
  public void init()
  {
    setLayout(new BorderLayout());
    add(new MessageDialogLauncher(this), "Center");
  }
}

class MessageDialogLauncher extends Panel
  implements DialogClient,
  ActionListener
{
  private Applet applet;
  private Button messageDialogButton;
  private MessageDialog messageDialog;
  private Image image = null;
  private Checkbox modal = new Checkbox("modal");

  public MessageDialogLauncher(Applet applet)
  {
    this.applet = applet;

    add(modal);

    add(messageDialogButton =
        new Button("Launch Message Dialog"));

    messageDialogButton.addActionListener(this);
  }

  public void actionPerformed(ActionEvent event)
  {
    Image image = applet.getImage(applet.getCodeBase(),
                                  "gifs/information.gif");
    if (messageDialog == null)
    {
      messageDialog = new MessageDialog(Util.getFrame(this), this,
                                        "Example Message Dialog",
                                        "This is an example of a message dialog.",
                                        image, modal.getState());
    }
    else
    {
      if (modal.getState())
        messageDialog.setModal(true);
      else
        messageDialog.setModal(false);
    }
    messageDialog.setVisible(true);
  }

  public void dialogDismissed(Dialog d)
  {
    applet.showStatus("MessageDialog Dismissed");
  }

  public void dialogCancelled(Dialog d)
  {
    applet.showStatus("Message Dialog Cancelled");
  }
}
