package org.mwc.cmap.core.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;
import org.eclipse.ui.IWorkbench;

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

public class VideoCapturePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public class PreferenceConstants {
		public static final String WHITE_CURSOR = "white cursor";

		public static final String BLACK_CURSOR = "black cursor";

		public static final String JPEG50 = "jpeg50";

		public static final String JPEG100 = "jpeg100";

		public static final String PNG = "png";

		public static final String NONE2 = "none";

		public static final String ANIMATION = "animation";

		public static final String SCREEN_CAPTURE = "screen capture";

		public static final String ENTIRE_SCREEN = "Entire Screen";

		public static final String QUICK_TIME = "QuickTime";

		public static final String AVI = "avi";

		public static final String NO_CURSOR = "no cursor";

		public static final String P_FORMAT = "formatPreference";

		public static final String P_ENCODING = "encodingPreference";

		public static final String P_COLORS = "colorsPreference";

		public static final String P_SCREEN_RATE = "screenRatePreference";

		public static final String P_MOUSE = "mouseCursorPreference";

		public static final String P_MOUSE_RATE = "mouseRatePreference";

		public static final String P_AUDIO = "audioPreference";

		public static final String P_AUDIO_RATE = "audioRatePrefence";

		public static final String P_SCREEN_RATE_AUTO = "screenRateAuto";

		public static final String P_SCREEN_AREA = "screenArea";

		public static final String P_ENABLE = "enableVideoRecording";

		public static final String DEBRIEF_S_WINDOW = "Debrief's Window";
	}

	public VideoCapturePreferencePage() {
		super(GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
		setDescription("Video Capture Preferences.");
	}

	final String mouseFieldTextDesc = "&Mouse Rate";

	ScaleFieldEditor mouseRateEditor;

	BooleanFieldEditor adaptativeMouseRateEditor;

	BooleanFieldEditor enableVideoRecording;

	RadioGroupFieldEditor cursorRadioEditor;

	ScaleFieldEditor screenRateEditor;

	RadioGroupFieldEditor fileFormatEditor;

	RadioGroupFieldEditor encodingEditor;

	RadioGroupFieldEditor colourRadioEditor;

	RadioGroupFieldEditor screenAreaRadioEditor;

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		enableVideoRecording = new BooleanFieldEditor(PreferenceConstants.P_ENABLE, "Enable Video Recording",
				getFieldEditorParent());

		addField(enableVideoRecording);
		final IPreferenceStore preferenceStore = CorePlugin.getDefault().getPreferenceStore();
		final boolean recordingEnabledPref = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE);

		screenAreaRadioEditor = new RadioGroupFieldEditor(PreferenceConstants.P_SCREEN_AREA, "&Screen Area", 3,
				new String[][] { { "&Entire Screen", PreferenceConstants.ENTIRE_SCREEN },
						{ "&Debrief's Window", PreferenceConstants.DEBRIEF_S_WINDOW } },
				getFieldEditorParent());
		addField(screenAreaRadioEditor);
		screenAreaRadioEditor.setEnabled(recordingEnabledPref, getFieldEditorParent());

		fileFormatEditor = new RadioGroupFieldEditor(
				PreferenceConstants.P_FORMAT, "&Format to generate the video", 3, new String[][] {
						{ "&AVI", PreferenceConstants.AVI }, { "&QuickTime", PreferenceConstants.QUICK_TIME } },
				getFieldEditorParent());
		addField(fileFormatEditor);
		fileFormatEditor.setEnabled(recordingEnabledPref, getFieldEditorParent());

		encodingEditor = new RadioGroupFieldEditor(PreferenceConstants.P_ENCODING, "&Encoding", 3,
				new String[][] { { "&Screen Capture", PreferenceConstants.SCREEN_CAPTURE },
						{ "&Animation", PreferenceConstants.ANIMATION }, { "&None", PreferenceConstants.NONE2 },
						{ "&PNG", PreferenceConstants.PNG }, { "&jpeg 100%", PreferenceConstants.JPEG100 },
						{ "j&peg 50%", PreferenceConstants.JPEG50 } },
				getFieldEditorParent());
		addField(encodingEditor);
		encodingEditor.setEnabled(recordingEnabledPref, getFieldEditorParent());

		colourRadioEditor = new RadioGroupFieldEditor(PreferenceConstants.P_COLORS, "&Colors", 3,
				new String[][] { { "&Hundreds", "8" }, { "&Thousands", "16" }, { "&Millions", "24" } },
				getFieldEditorParent());
		addField(colourRadioEditor);
		colourRadioEditor.setEnabled(recordingEnabledPref, getFieldEditorParent());

		adaptativeMouseRateEditor = new BooleanFieldEditor(PreferenceConstants.P_SCREEN_RATE_AUTO,
				"Adaptative Screen Rate", getFieldEditorParent());
		addField(adaptativeMouseRateEditor);
		adaptativeMouseRateEditor.setEnabled(recordingEnabledPref, getFieldEditorParent());

		final String screenRateFieldTextDesc = "&Screen Rate";

		screenRateEditor = new ScaleFieldEditor(PreferenceConstants.P_SCREEN_RATE, screenRateFieldTextDesc,
				getFieldEditorParent());

		screenRateEditor.setMinimum(1);
		screenRateEditor.setMaximum(30);
		screenRateEditor.setIncrement(1);
		screenRateEditor.setLabelText(screenRateFieldTextDesc + " (15 fps) ");
		screenRateEditor.setEnabled(
				recordingEnabledPref && !preferenceStore.getBoolean(PreferenceConstants.P_SCREEN_RATE_AUTO),
				getFieldEditorParent());
		SelectionListener screenRateListener = new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int screenRate = screenRateEditor.getScaleControl().getSelection();
				screenRateEditor.setLabelText(screenRateFieldTextDesc + " (" + screenRate + " fps)");
			}
		};
		screenRateEditor.getScaleControl().addSelectionListener(screenRateListener);
		addField(screenRateEditor);

		cursorRadioEditor = new RadioGroupFieldEditor(PreferenceConstants.P_MOUSE, "&Mouse", 3,
				new String[][] { { "&No Cursor", PreferenceConstants.NO_CURSOR },
						{ "&Black Cursor", PreferenceConstants.BLACK_CURSOR },
						{ "&White Cursor", PreferenceConstants.WHITE_CURSOR } },
				getFieldEditorParent());
		cursorRadioEditor.setEnabled(recordingEnabledPref, getFieldEditorParent());
		addField(cursorRadioEditor);

		mouseRateEditor = new ScaleFieldEditor(PreferenceConstants.P_MOUSE_RATE, mouseFieldTextDesc,
				getFieldEditorParent());
		mouseRateEditor.setMinimum(1);
		mouseRateEditor.setMaximum(30);
		mouseRateEditor.setIncrement(1);
		mouseRateEditor.setLabelText(mouseFieldTextDesc + " (15 fps) ");
		mouseRateEditor.setEnabled(recordingEnabledPref, getFieldEditorParent());
		SelectionListener mouseRateListener = new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int screenRate = mouseRateEditor.getScaleControl().getSelection();
				mouseRateEditor.setLabelText(mouseFieldTextDesc + " (" + screenRate + " fps)");
			}
		};
		mouseRateEditor.getScaleControl().addSelectionListener(mouseRateListener);
		mouseRateEditor.setEnabled(
				!PreferenceConstants.NO_CURSOR.equals(preferenceStore.getString(PreferenceConstants.P_MOUSE)),
				getFieldEditorParent());
		addField(mouseRateEditor);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		final IPreferenceStore preferenceStore = CorePlugin.getDefault().getPreferenceStore();

		final boolean prefEnabled = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE);
		if (event.getSource().equals(adaptativeMouseRateEditor)) {
			screenRateEditor.setEnabled((!((boolean) event.getNewValue())) && prefEnabled, getFieldEditorParent());
		}
		if (event.getSource().equals(cursorRadioEditor)) {
			mouseRateEditor.setEnabled(
					!PreferenceConstants.NO_CURSOR.equals((String) event.getNewValue()) && prefEnabled,
					getFieldEditorParent());
		}
		if (event.getSource().equals(enableVideoRecording)) {

			screenAreaRadioEditor.setEnabled((boolean) event.getNewValue(), getFieldEditorParent());

			mouseRateEditor.setEnabled((boolean) event.getNewValue(), getFieldEditorParent());

			adaptativeMouseRateEditor.setEnabled((boolean) event.getNewValue(), getFieldEditorParent());

			cursorRadioEditor.setEnabled((boolean) event.getNewValue(), getFieldEditorParent());

			screenRateEditor.setEnabled(
					(boolean) event.getNewValue()
							&& !preferenceStore.getBoolean(PreferenceConstants.P_SCREEN_RATE_AUTO),
					getFieldEditorParent());

			fileFormatEditor.setEnabled((boolean) event.getNewValue(), getFieldEditorParent());

			encodingEditor.setEnabled((boolean) event.getNewValue(), getFieldEditorParent());

			colourRadioEditor.setEnabled((boolean) event.getNewValue(), getFieldEditorParent());

		}

		super.propertyChange(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}