package org.mwc.cmap.core.wizards;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ImportRepFreqDialog extends TitleAreaDialog
{

  private long sampleFreq;
  private final String _trackName;

  public ImportRepFreqDialog(final Shell parentShell, final String trackName)
  {
    super(parentShell);
    _trackName = trackName;
  }

  @Override
  public void create()
  {
    super.create();
    setTitle("Add REP data to existing track");
    getButton(IDialogConstants.OK_ID).setText("Import");
    setMessage("Please choose the frequency at which data will be imported (or 'none' to not import that type).");
  }

  @Override
  protected Control createDialogArea(final Composite parent)
  {
    final Composite base = (Composite) super.createDialogArea(parent);
    final Composite composite = new Composite(base, SWT.NONE);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH
        | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
    composite.setLayout(new GridLayout(2, false));
    {
      new Label(composite, SWT.NONE).setText("Sampling frequency for " + _trackName + ":");
      final ComboViewer comboViewer = new ComboViewer(composite);
      comboViewer.setContentProvider(new ArrayContentProvider());
      comboViewer.setLabelProvider(newLabelProvider());
      comboViewer.setInput(getDataSet());

      comboViewer.getCombo().setLayoutData(
          new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
      comboViewer.setSelection(new StructuredSelection(Long
          .valueOf(sampleFreq)));
      comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
      {
        @Override
        public void selectionChanged(final SelectionChangedEvent event)
        {
          final IStructuredSelection selection =
              (IStructuredSelection) event.getSelection();
          if (selection.getFirstElement() instanceof Long)
          {
            sampleFreq = (Long) selection.getFirstElement();
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

  public long getSampleFreq()
  {
    return sampleFreq;
  }

  private IBaseLabelProvider newLabelProvider()
  {
    return new ColumnLabelProvider()
    {
      @Override
      public String getText(final Object element)
      {
        if (element instanceof Long)
        {
          final long longValue = ((Long) element).longValue();
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

}
