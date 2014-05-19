package MWC.GUI.Dialogs.AWT;

import java.awt.Dialog;

public interface DialogClient {
	abstract public void dialogDismissed(Dialog d); 
	abstract public void dialogCancelled(Dialog d); 
}
