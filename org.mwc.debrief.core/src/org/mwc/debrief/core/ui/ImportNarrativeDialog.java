/**
 * 
 */
package org.mwc.debrief.core.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument.ImportNarrativeEnum;

/**
 * Dialog popped up from {@link ImportNarrativeHelper}
 * @author Ayesha
 *
 */
public class ImportNarrativeDialog extends Dialog
{
  private Button _btnLoadedTracks;
  private ImportNarrativeEnum userChoice;
  private boolean preference;
  
  private SelectionListener selectionListener = new SelectionAdapter()
  {
    @Override
    public void widgetSelected(SelectionEvent e)
    {
      if(_btnLoadedTracks.getSelection()) {
        userChoice = ImportNarrativeEnum.TRIMMED_DATA;
      }
      else{
        userChoice = ImportNarrativeEnum.ALL_DATA;
      }
    }
  };

  public ImportNarrativeDialog(Shell parentShell)
  {
    super(parentShell);
  }
 
  @Override
  protected void configureShell(Shell newShell)
  {
    newShell.setText("Import Narrative Entries");
    
    super.configureShell(newShell);
  }
  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite control = (Composite)super.createDialogArea(parent);
    Composite composite = new Composite(control,SWT.NONE);
    composite.setLayout(new GridLayout(1,false));
    composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    _btnLoadedTracks = new Button(composite,SWT.RADIO);
    _btnLoadedTracks.setText("Trim to loaded tracks");
    _btnLoadedTracks.addSelectionListener(selectionListener);
    _btnLoadedTracks.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
     Button _btnAllData = new Button(composite,SWT.RADIO);
    _btnAllData.setText("Load all data");
    _btnAllData.addSelectionListener(selectionListener);
    _btnAllData.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    
    new Label(composite,SWT.NONE).setLayoutData(new GridData(GridData.FILL));
    new Label(composite,SWT.NONE).setLayoutData(new GridData(GridData.FILL));
    final Button dontAskAgain = new Button(composite,SWT.CHECK);
    dontAskAgain.setText("Use this mode next time");
    dontAskAgain.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        preference = dontAskAgain.getSelection();
      }
    });
    return control;
  }
  
  public boolean getPreference()
  {
    return preference;
  }

  public ImportNarrativeEnum getUserChoice()
  {
    return userChoice;
  }
  
  @Override
  protected void cancelPressed()
  {
    userChoice = ImportNarrativeEnum.CANCEL;
    super.cancelPressed();
  }
  
 
  

}
