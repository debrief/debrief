package org.mwc.debrief.core.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.core.CorePlugin;

public class ImportNMEADialog extends TitleAreaDialog
{

  private long ownshipFreq;
  private long thirdPartyFreq;

  public ImportNMEADialog(Shell parentShell)
  {
    super(parentShell);
  }

  public ImportNMEADialog()
  {
    this(Display.getDefault().getActiveShell());
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite base = (Composite) super.createDialogArea(parent);
    Composite composite = new Composite(base, SWT.NONE);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH
        | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
    composite.setLayout(new GridLayout(2, false));
    {
      new Label(composite, SWT.NONE).setText("Ownship postion frequency:");
      ComboViewer comboViewer = new ComboViewer(composite);
      comboViewer.setContentProvider(new ArrayContentProvider());
      comboViewer.setLabelProvider(newLabelProvider());
      comboViewer.setInput(getDataSet());

      comboViewer.getCombo().setLayoutData(
          new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
      comboViewer.setSelection(new StructuredSelection(Long.valueOf(ownshipFreq)));
      comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
      {

        @Override
        public void selectionChanged(SelectionChangedEvent event)
        {
          IStructuredSelection selection =
              (IStructuredSelection) event.getSelection();
          if (selection.getFirstElement() instanceof Long)
          {
            ownshipFreq = (Long) selection.getFirstElement();
          }
        }
      });
    }
    {
      new Label(composite, SWT.NONE).setText("Third Party postion frequency:");
      ComboViewer comboViewer = new ComboViewer(composite);
      comboViewer.setContentProvider(new ArrayContentProvider());
      comboViewer.setLabelProvider(newLabelProvider());
      comboViewer.setInput(getDataSet());
      comboViewer.getCombo().setLayoutData(
          new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
      comboViewer.setSelection(new StructuredSelection(Long.valueOf(thirdPartyFreq)));
      comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
      {

        @Override
        public void selectionChanged(SelectionChangedEvent event)
        {
          IStructuredSelection selection =
              (IStructuredSelection) event.getSelection();
          if (selection.getFirstElement() instanceof Long)
          {
            thirdPartyFreq = (Long) selection.getFirstElement();
          }
        }
      });
    }

    return composite;
  }

  private Object[] getDataSet()
  {
    return new Long[]
    {0l, 5000l, 15000l, 60000l, 300000l, 600000l, 3600000l, Long.MAX_VALUE};
  }

  private IBaseLabelProvider newLabelProvider()
  {
    return new ColumnLabelProvider()
    {

      @Override
      public String getText(Object element)
      {
        if (element instanceof Long)
        {
          long longValue = ((Long) element).longValue();
          if (longValue == 0)
            return "All";

          if (longValue == Long.MAX_VALUE)
            return "None";
          if (longValue == 5000)
            return "5 Second";
          if (longValue == 15000)
            return "15 Second";
          if (longValue == 60000)
            return "1 Minute";
          if (longValue == 300000)
            return "5 Minute";
          if (longValue == 600000)
            return "10 Minute";
          if (longValue == 3600000)
            return "1 Hour";

        }
        return super.getText(element);
      }

    };
  }

  @Override
  public void create()
  {
    super.create();
    setTitle("Import NMEA Data");
    getButton(IDialogConstants.OK_ID).setText("Import");
    setTitleImage(CorePlugin.getImageFromRegistry("icons/48/NMEA.png"));
    setMessage("please choose the frequency at which data will be imported.");
  }

  public long getOwnshipFreq()
  {
    return ownshipFreq;
  }

  public long getThirdPartyFreq()
  {
    return thirdPartyFreq;
  }

  /**
   * @param args
   */
  public static void main(String[] args)
  {

    Display display = Display.getDefault();
    if (display.isDisposed())
      display = new Display();
    Shell shell = new Shell(display, SWT.NO_TRIM);
    ImportNMEADialog dialog = new ImportNMEADialog(shell);

    dialog.open();
  }

}
