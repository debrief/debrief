package org.mwc.cmap.naturalearth.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.view.NEFeatureStore;

/**
 * 
 * TODO: @Peco can you implement the prefs page so that it has a path variable
 * (with browse button to change it). TODO: @Peco = It should also have
 * "Import styles (XML)", "Export styles (XML)" and "reset styles" buttons.
 * 
 */

public class NaturalEarthPrefs extends PreferencePage implements
		IWorkbenchPreferencePage
{

	public static final String ID = "org.mwc.cmap.naturalearth.preferences.NaturalEarhPrefs";
	private Text dataFolderText;
	private ListViewer viewer;
	private Button importButton;
	private Button exportButton;
	private Button duplicateButton;
	private Button deleteButton;

	public NaturalEarthPrefs()
	{
		super("Natural Earth");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		composite.setLayoutData(gd);
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);

		Label dataFolderLabel = new Label(composite, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		dataFolderLabel.setLayoutData(gd);
		dataFolderLabel.setText("Data Folder:");

		dataFolderText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		dataFolderText.setLayoutData(gd);
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String dataFolderValue = store.getString(PreferenceConstants.DATA_FOLDER);
		dataFolderText.setText(dataFolderValue == null ? "" : dataFolderValue);

		final Button dataFolderBrowse = new Button(composite, SWT.PUSH);
		dataFolderBrowse.setText("Browse...");
		dataFolderBrowse.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SINGLE);
				String value = dataFolderText.getText();
				if (value.trim().length() == 0)
				{
					value = Platform.getLocation().toOSString();
				}
				dialog.setFilterPath(value);

				String result = dialog.open();
				if (result == null || result.trim().length() == 0)
				{
					return;
				}
				dataFolderText.setText(result);

			}

		});

		Group stylesGroup = new Group(composite, SWT.NONE);
		layout = new GridLayout(2, false);
		stylesGroup.setLayout(layout);
		stylesGroup.setText("Styles");
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 3;
		stylesGroup.setLayoutData(gd);

		viewer = new ListViewer(stylesGroup, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER);
		viewer.setContentProvider(new StylesContentProvider());
		viewer.setLabelProvider(new StylesLabelProvider());
		viewer.setInput(new Styles());
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite buttonComposite = new Composite(stylesGroup, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(1, false));
		buttonComposite
				.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		deleteButton = new Button(buttonComposite, SWT.PUSH);
		deleteButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		deleteButton.setText("Delete");
		deleteButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				// TODO
				viewer.refresh();
			}
		});

		duplicateButton = new Button(buttonComposite, SWT.PUSH);
		duplicateButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		duplicateButton.setText("Duplicate");
		duplicateButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				// TODO
				viewer.refresh();
			}

		});

		exportButton = new Button(buttonComposite, SWT.PUSH);
		exportButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		exportButton.setText("Export");

		exportButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				// TODO
				viewer.refresh();
			}

		});

		importButton = new Button(buttonComposite, SWT.PUSH);
		importButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		importButton.setText("Import");

		importButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				// TODO
				viewer.refresh();
			}

		});

		enableButtons(false);

		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				enableButtons(false);
				ISelection sel = event.getSelection();
				if (sel instanceof IStructuredSelection)
				{
					enableButtons(true);
					NEFeatureStore style = (NEFeatureStore) ((IStructuredSelection) sel)
							.getFirstElement();
					if (style != null && "default".equals(style.getName()))
					{
						deleteButton.setEnabled(false);
					}
				}
			}
		});

		return composite;
	}

	private void enableButtons(boolean enable)
	{
		deleteButton.setEnabled(enable);
		duplicateButton.setEnabled(enable);
		exportButton.setEnabled(enable);
		importButton.setEnabled(enable);
	}

	@Override
	protected void performDefaults()
	{
		dataFolderText.setText(""); //$NON-NLS-1$
		storePreferences();
		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		storePreferences();
		return super.performOk();
	}

	private void storePreferences()
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String value = dataFolderText.getText();
		if (!value.isEmpty())
		{
			store.setValue(PreferenceConstants.DATA_FOLDER, value);
		}
	}

	class StylesLabelProvider extends LabelProvider
	{

		@Override
		public Image getImage(Object element)
		{
			return null;
		}

		@Override
		public String getText(Object element)
		{
			if (element instanceof NEFeatureStore)
			{
				return ((NEFeatureStore) element).getName();
			}
			return super.getText(element);
		}

	}

	class StylesContentProvider implements IStructuredContentProvider
	{

		@Override
		public void dispose()
		{
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}

		@Override
		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof Styles)
			{
				return ((Styles) inputElement).getStyles().toArray(new NEFeatureStore[0]);
			}
			return null;
		}

	}

}