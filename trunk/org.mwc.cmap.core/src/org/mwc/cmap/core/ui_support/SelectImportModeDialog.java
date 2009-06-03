package org.mwc.cmap.core.ui_support;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.core.CorePlugin;

import Debrief.ReaderWriter.Replay.ImportReplay;

/**
 * This class demonstrates how to create your own dialog classes. It allows users
 * to input a String
 */
public class SelectImportModeDialog extends Dialog implements SelectionListener {
  private String message;
  private String input;
  
  private boolean _rememberIt;
  private String _mode = ImportReplay.IMPORT_AS_ATG;
  

  /**
   * InputDialog constructor
   * 
   * @param parent the parent
   */
  public SelectImportModeDialog(Shell parent, String trackName) {
    // Pass the default styles here
    this(parent, trackName, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
  }

  /**
   * InputDialog constructor
   * 
   * @param parent the parent
   * @param style the style
   */
  public SelectImportModeDialog(Shell parent, String trackName, int style) {
    // Let users override the default styles
    super(parent, style);
    setText("Select track mode");
    message = "Debrief can plot tracks using one of two modes." +
    		"\nUse this dialog to select how to import the track titled " + trackName + "." +
    		"\nYou can override your choice using the CMAP tab of Windows/Preferences";
  }


  /**
   * Gets the input
   * 
   * @return String
   */
  public String getInput() {
    return input;
  }

  /**
   * Sets the input
   * 
   * @param input the new input
   */
  public void setInput(String input) {
    this.input = input;
  }

  /**
   * Opens the dialog and returns the input
   * 
   * @return String
   */
  public String open() {
    // Create the dialog window
    Shell shell = new Shell(getParent(), getStyle());
    shell.setText(getText());
    createContents(shell);
    shell.pack();
    shell.open();
    Display display = getParent().getDisplay();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }

    // Return the entered value (will be null if user cancelled)
    return _mode;
  }

  private static void storeModePreference(String mode)
	{
  	CorePlugin.getToolParent().setProperty(ImportReplay.TRACK_IMPORT_MODE, mode);
	}

	/**
   * Creates the dialog's contents
   * 
   * @param shell the dialog window
   */
  /**
   * @param shell
   */
  private void createContents(final Shell shell) {
    shell.setLayout(new GridLayout(1, true));

    // Show the message
    Label label = new Label(shell, SWT.NONE);
    label.setText(message);
    GridData data = new GridData();
    data.horizontalSpan = 2;
    label.setLayoutData(data);

    // Display the radio button list

    Button[] radios = new Button[2];

    radios[0] = new Button(shell, SWT.RADIO);
    radios[0].setText("Dead Reckoning (DR) - positions are calculated using recorded course and speed");
    radios[0].setData(ImportReplay.IMPORT_AS_DR);
    radios[0].addSelectionListener(this);

    radios[1] = new Button(shell, SWT.RADIO);
    radios[1].setText("Over The Ground (OTG) - where positions are plotted according to the recorded location");
    radios[1].setData(ImportReplay.IMPORT_AS_ATG);
    radios[1].addSelectionListener(this);
    radios[1].setSelection(true);
    

    final Button rememberBtn = new Button(shell, SWT.CHECK);
    rememberBtn.setText("Automatically use this mode next time");
    rememberBtn.addSelectionListener(new SelectionListener(){
		
			public void widgetSelected(SelectionEvent e)
			{
				_rememberIt = rememberBtn.getSelection();
			}
		
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
    
    Composite holder = new Composite(shell, SWT.NONE);
    holder.setLayout(new GridLayout(2, true));
    

    // Create the OK button and add a handler
    // so that pressing it will set input
    // to the entered value
    Button ok = new Button(holder, SWT.PUSH);
    ok.setText("OK");
    data = new GridData(GridData.FILL_HORIZONTAL);
    ok.setLayoutData(data);
    ok.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        // so, do they want us to remember the value?
        if(_rememberIt)
        {
        	// put it into the prefs.
        	storeModePreference(_mode);
        }
        
        shell.close();
      }
    });

    // Create the cancel button and add a handler
    // so that pressing it will set input to null
    Button cancel = new Button(holder, SWT.PUSH);
    cancel.setText("Cancel");
    data = new GridData(GridData.FILL_HORIZONTAL);
    cancel.setLayoutData(data);
    cancel.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        // clear the selection
      	_mode = null;
        shell.close();
      }
    });

    // Set the OK button as the default, so
    // user can type input and press Enter
    // to dismiss
    shell.setDefaultButton(ok);
  }

	public void widgetDefaultSelected(SelectionEvent e)
	{

	}

	public void widgetSelected(SelectionEvent e)
	{
		Button btn = (Button) e.widget;
		_mode = (String) btn.getData();
	}
}
           
       