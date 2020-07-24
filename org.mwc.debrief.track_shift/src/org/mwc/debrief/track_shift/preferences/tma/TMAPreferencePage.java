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
package org.mwc.debrief.track_shift.preferences.tma;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.ContextOperations.GenerateTMASegmentFromCuts;
import org.mwc.debrief.core.ContextOperations.MergeTracks;
import org.mwc.debrief.track_shift.TrackShiftActivator;
import org.mwc.debrief.track_shift.views.BaseStackedDotsView;

import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import MWC.Algorithms.FrequencyCalcs;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class TMAPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	/**
	 * Constant definitions for plug-in preferences
	 */
	public static class PreferenceConstants {
		public static final String MERGED_INFILL_COLOR = MergeTracks.MERGED_INFILL_COLOR;
		public static final String MERGED_TRACK_COLOR = MergeTracks.MERGED_TRACK_COLOR;
		public static final String CUT_OFF_VALUE_DEGS = RelativeTMASegment.CUT_OFF_VALUE_DEGS;
		public static final String CUT_OFF_VALUE_HZ = RelativeTMASegment.CUT_OFF_VALUE_HZ;
		public static final String USE_CUT_COLOR = GenerateTMASegmentFromCuts.USE_CUT_COLOR;
		public static final String INFILL_COLOR_STRATEGY = DynamicInfillSegment.INFILL_COLOR_STRATEGY;
		public static final String SPEED_OF_SOUND_KTS = FrequencyCalcs.SPEED_OF_SOUND_KTS_PROPERTY;
		public static final String HOLISTIC_LEG_SLICER = BaseStackedDotsView.USE_HOLISTIC_SLICER;
	}

	private SelectionListener freqListener;
	private ScaleFieldEditor freqEdit;

	public TMAPreferencePage() {
		super("Debrief Preferences", CorePlugin.getImageDescriptor("icons/24/MultiPath.png"), GRID);
		setPreferenceStore(TrackShiftActivator.getDefault().getPreferenceStore());
		setDescription("TMA-specific settings");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.USE_CUT_COLOR,
				"Use sensor cut colors for new TMA leg positions", getFieldEditorParent()));

		// insert a separator
		final Label label1 = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		label1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));

		addField(new IntegerFieldEditor(PreferenceConstants.SPEED_OF_SOUND_KTS,
				"Speed of sound for Doppler calcs (kts)", getFieldEditorParent()));

		// insert a separator
		final Label label3 = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		label3.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));

		addField(new IntegerFieldEditor(PreferenceConstants.CUT_OFF_VALUE_DEGS,
				"Cut-off value for acceptable bearing errors in stacked dots (degs)", getFieldEditorParent()));

		final String freqLabelStr = "Cut-off value for acceptable frequency errors in stacked dots";
		freqEdit = new ScaleFieldEditor(PreferenceConstants.CUT_OFF_VALUE_HZ, "Cut-off", getFieldEditorParent());
		freqEdit.setMinimum(0);
		freqEdit.setMaximum(100);
		freqEdit.setIncrement(5);
		freqEdit.setLabelText(freqLabelStr + " (x.xx Hz) ");
		freqListener = new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int curInt = freqEdit.getScaleControl().getSelection();
				final double curVal = curInt / 100d;
				freqEdit.setLabelText(freqLabelStr + " (" + curVal + " Hz)");
			}
		};
		freqEdit.getScaleControl().addSelectionListener(freqListener);
		addField(freqEdit);

		// insert a separator
		final Label label2 = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		label2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));

		// initialise the import choice tags, if we have to
		final String[][] _trackModeTags = new String[3][2];
		_trackModeTags[0][0] = "Darker version of previous leg color";
		_trackModeTags[0][1] = DynamicInfillSegment.DARKER_INFILL;
		_trackModeTags[1][0] = "Random color";
		_trackModeTags[1][1] = DynamicInfillSegment.RANDOM_INFILL;
		_trackModeTags[2][0] = "Single shade (green)";
		_trackModeTags[2][1] = DynamicInfillSegment.GREEN_INFILL;

		addField(new RadioGroupFieldEditor(PreferenceConstants.INFILL_COLOR_STRATEGY,
				"Policy for dynamic infill colors:", 1, _trackModeTags, getFieldEditorParent()));

		addField(new ColorFieldEditor(PreferenceConstants.MERGED_TRACK_COLOR, "Default color for merged track:",
				getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.MERGED_INFILL_COLOR,
				"Color for infill segments in merged track:", getFieldEditorParent()));

		// insert a separator
		final Label label4 = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		label4.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));

		addField(new BooleanFieldEditor(PreferenceConstants.HOLISTIC_LEG_SLICER, "Use holistic leg slicer",
				getFieldEditorParent()));

	}

	@Override
	public void dispose() {
		// drop the manually generated listener
		freqEdit.getScaleControl().removeSelectionListener(freqListener);

		// let the parent carry on ditching
		super.dispose();
	}

	@Override
	public void init(final IWorkbench workbench) {
		// default implementation
	}
}