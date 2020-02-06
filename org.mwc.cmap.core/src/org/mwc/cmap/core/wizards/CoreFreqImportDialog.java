
package org.mwc.cmap.core.wizards;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * utility class to encapsulate the frequency drop-down
 * 
 * @author ian
 * 
 */
public class CoreFreqImportDialog extends TitleAreaDialog
{

  public CoreFreqImportDialog(final Shell parentShell)
  {
    super(parentShell); 
  }
  
  @Override
	protected int getShellStyle() {
		return SWT.PRIMARY_MODAL|SWT.TITLE|SWT.SHELL_TRIM;
	}

  protected Object[] getDataSet()
  {
    return new Long[]
    {0l, 5000l, 15000l, 60000l, 300000l, 600000l, 3600000l, Long.MAX_VALUE};
  }

  protected IBaseLabelProvider newLabelProvider()
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