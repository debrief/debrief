/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
/**
 *
 */
package org.mwc.debrief.core.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.preferences.PrefsPage.PreferenceConstants;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument.ImportNarrativeEnum;

/**
 * Dialog popped up from {@link ImportNarrativeHelper}
 *
 * @author Ayesha
 *
 */
public class ImportNarrativeDialog extends Dialog {
	private Button _btnLoadedTracks;
	private boolean preference;
	private final Map<String, Integer> types;
	private Button[] typesCheck;
	private Button selectAllCheck;
	private final List<String> selectedTypes = new ArrayList<>();

	public ImportNarrativeDialog(final Shell parentShell, final Map<String, Integer> narrativeTypes) {
		super(parentShell);
		this.types = narrativeTypes;
	}

	private int computePreferredHeight() {
		final int numberOfLines = typesCheck.length / 2 + 1;
		final int defaultHorizontalSpacing = 5;
		final Point preferredSize = typesCheck[0].computeSize(SWT.DEFAULT, SWT.DEFAULT);
		return numberOfLines * (preferredSize.y + defaultHorizontalSpacing);
	}

	@Override
	protected void configureShell(final Shell newShell) {
		newShell.setText("Import Narrative Entries");

		super.configureShell(newShell);
	}

	private Composite createCheckboxes(final Composite scrolledComposite) {
		final Composite component = new Composite(scrolledComposite, SWT.NONE);
		component.setLayout(new GridLayout());

		final Composite typesComposite = new Composite(component, SWT.NONE);
		typesComposite.setLayout(new GridLayout(2, true));
		typesCheck = new Button[types.size()];
		int i = 0;

		for (final String type : types.keySet()) {
			typesCheck[i] = new Button(typesComposite, SWT.CHECK);
			typesCheck[i].setText(type + "(" + types.get(type) + ")");
			typesCheck[i].setLayoutData(new GridData(SWT.FILL));
			i++;
		}
		return component;

	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite control = (Composite) super.createDialogArea(parent);
		final Composite composite = new Composite(control, SWT.BORDER);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Label title = new Label(composite, SWT.BOLD);
		FontDescriptor descriptor = FontDescriptor.createFrom(title.getFont());
		// setStyle method returns a new font descriptor for the given style
		descriptor = descriptor.setStyle(SWT.BOLD);
		title.setFont(descriptor.createFont(title.getDisplay()));
		title.setText("Loading narrative data.");

		_btnLoadedTracks = new Button(composite, SWT.CHECK);
		_btnLoadedTracks.setText("Trim Narrative Entries to the  period of currently loaded tracks");
		_btnLoadedTracks.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NONE).setLayoutData(new GridData(GridData.FILL));
		_btnLoadedTracks.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				preference = _btnLoadedTracks.getSelection();
				CorePlugin.getDefault().getPreferenceStore()
						.setValue(PreferenceConstants.REUSE_TRIM_NARRATIVES_DIALOG_CHOICE, preference);
			}
		});
		preference = CorePlugin.getDefault().getPreferenceStore()
				.getBoolean(PreferenceConstants.REUSE_TRIM_NARRATIVES_DIALOG_CHOICE);
		_btnLoadedTracks.setSelection(preference);
		final Composite headingComposite = new Composite(control, SWT.NONE);
		final Label lblHeading = new Label(headingComposite, SWT.NONE);
		headingComposite.setLayout(new GridLayout());
		lblHeading.setText("Select the narrative types to import:");
		lblHeading.setFont(descriptor.createFont(title.getDisplay()));
		lblHeading.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		final ScrolledComposite scrolledComposite = new ScrolledComposite(control, SWT.V_SCROLL | SWT.BORDER);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		final Composite component = createCheckboxes(scrolledComposite);
		scrolledComposite.setContent(component);
		scrolledComposite.addListener(SWT.Resize, event -> {
			final int width = scrolledComposite.getClientArea().width;
			scrolledComposite.setMinSize(width, computePreferredHeight());
		});
		final GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		final int prefHeight = computePreferredHeight();
		gridData.heightHint = prefHeight > 250 ? 250 : prefHeight + 10;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		scrolledComposite.setLayoutData(gridData);
		selectAllCheck = new Button(control, SWT.CHECK);
		selectAllCheck.setText("Select All/None");
		selectAllCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				for (final Button button : typesCheck) {
					button.setSelection(selectAllCheck.getSelection());
				}
			}
		});
		return control;
	}

	public boolean getPreference() {
		return preference;
	}

	public List<String> getSelectedNarrativeTypes() {
		return selectedTypes;
	}

	public ImportNarrativeEnum getUserChoice() {
		if (preference) {
			return ImportNarrativeEnum.TRIMMED_DATA;
		}
		return ImportNarrativeEnum.ALL_DATA;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		for (final Button button : typesCheck) {
			if (button.getSelection()) {
				final String text = button.getText();
				selectedTypes.add(text.substring(0, text.indexOf("(")));
			}
		}
		super.okPressed();
	}

}
