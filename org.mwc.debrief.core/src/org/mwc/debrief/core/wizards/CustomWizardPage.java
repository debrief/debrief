package org.mwc.debrief.core.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.DropdownProvider;

public abstract class CustomWizardPage extends WizardPage
{

  private Composite pageNameBody;
  private Composite pageNameSection;

  public CustomWizardPage(String pageName, String title,
      ImageDescriptor titleImage)
  {
    super(pageName, title, titleImage);
  }

  public CustomWizardPage(String pageName)
  {
    super(pageName);
  }
  

  final protected static String getPrefValue(String key, String currentVal)
  {
    IPreferenceStore preferenceStore = CorePlugin.getDefault()
        .getPreferenceStore();
    String newVal = preferenceStore.getString(key.toUpperCase());

    return (newVal == null || newVal.isEmpty()) ? currentVal : newVal;
  }

  final protected static String setPrefValue(String key, String currentVal)
  {
    IPreferenceStore preferenceStore = CorePlugin.getDefault()
        .getPreferenceStore();

    preferenceStore.setValue(key, currentVal);
    return currentVal;
  }

  @Override
  public void createControl(final Composite parent)
  {
    final Composite contents = new Composite(parent, SWT.NONE);
    contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    contents.setLayout(new GridLayout(3, false));

    pageNameSection = new Composite(contents, SWT.NONE);

    {
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, true);
      gridData.widthHint = 120;
      pageNameSection.setLayoutData(gridData);
      pageNameSection.setLayout(new FillLayout());
      updatePageNames();
    }
    {
      Label sep = new Label(contents, SWT.SEPARATOR | SWT.VERTICAL);
      sep.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
    }
    {
      Composite createDataSection = createDataSection(contents);
      createDataSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
          true));

    }

    setControl(contents);
  }

  protected void updatePageNames()
  {
    if (pageNameBody != null)
      pageNameBody.dispose();

    pageNameBody = new Composite(pageNameSection, SWT.NONE);

    FontData nomalFontData = pageNameBody.getFont().getFontData()[0];
    final Font nomalFont = new Font(pageNameBody.getDisplay(), new FontData(
        nomalFontData.getName(), nomalFontData.getHeight() + 1, SWT.NORMAL));
    pageNameBody.addDisposeListener(new DisposeListener()
    {

      @Override
      public void widgetDisposed(DisposeEvent e)
      {
        nomalFont.dispose();
      }
    });

    pageNameBody.setLayout(new RowLayout(SWT.VERTICAL));

    List<String> pageNames = getPageNames();
    for (String name : pageNames)
    {
      Label section = new Label(pageNameBody, SWT.NONE);
      section.setText(name);
      section.setFont(nomalFont);

      if (name.equals(getName()))
      {
        FontData fontData = section.getFont().getFontData()[0];
        final Font font = new Font(section.getDisplay(), new FontData(fontData
            .getName(), fontData.getHeight() + 1, SWT.BOLD));
        section.setFont(font);
        section.addDisposeListener(new DisposeListener()
        {

          @Override
          public void widgetDisposed(DisposeEvent e)
          {
            font.dispose();
          }
        });
      }
    }

  }

  protected abstract List<String> getPageNames();

  protected abstract Composite createDataSection(Composite parent);

  protected static ComboViewer addCmbField(final Composite contents, final String key,
      final String title, String tooltip, final boolean edit, final String val,
      final WizardPage page, final DropdownProvider provider)
  {

    final Label lbl = new Label(contents, SWT.NONE);
    lbl.setText(title);
    lbl.setToolTipText(tooltip);
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    final ComboViewer typeCmb = new ComboViewer(contents, (edit ? SWT.BORDER
        : SWT.READ_ONLY | SWT.BORDER));
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.widthHint = 120;
    typeCmb.setContentProvider(new ArrayContentProvider());
    List<String> values = provider.getValuesFor(key);
    if(values==null) {
      values = new ArrayList<String>();
      page.setErrorMessage("No value for "+ title +" in file, may be an invalid file");
      page.setPageComplete(false);
    }
    typeCmb.setInput(values.toArray());
    typeCmb.getCombo().setLayoutData(gridData);
    if (val != null)
      typeCmb.getCombo().setText(val);
    else if (typeCmb.getCombo().getItemCount() > 0)
      typeCmb.getCombo().setText(typeCmb.getCombo().getItem(0));// select default first item

    return typeCmb;
  }


  protected static String getTxtVal(final Text control, final String val)
  {
    if (control != null && !control.isDisposed())
    {
      return control.getText().trim();
    }
    else
    {
      return val;
    }
  }
  
  
  protected static String getCmbVal(final ComboViewer comboViewer, String val)
  {
    final String res;
    if (comboViewer != null && !comboViewer.getCombo().isDisposed())
    {
      final StructuredSelection selection = (StructuredSelection) comboViewer
          .getSelection();
      if (selection.isEmpty())
      {
        // ah, it's not one of the drop downs, so
        // get the value from the combo
        final String comboText = comboViewer.getCombo().getText();
        res = comboText == null ? val : comboText;
      }
      else
      {
        // just get the selected item
        res = (String) selection.getFirstElement();
      }
    }
    else
    {
      res = val;
    }

    return res;
  }


  protected Text addTxtField(final Composite contents, final String label,
      String tooltip, final String initialValue)
  {

    final Label lbl = new Label(contents, SWT.NONE);
    lbl.setText(label);
    lbl.setToolTipText(tooltip);
    lbl.setAlignment(SWT.RIGHT);
    lbl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    final Text textControl = new Text(contents, SWT.BORDER);
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.widthHint = 120;
    textControl.setLayoutData(gridData);
    if (initialValue != null)
      textControl.setText(initialValue);

    return textControl;

  }
}
