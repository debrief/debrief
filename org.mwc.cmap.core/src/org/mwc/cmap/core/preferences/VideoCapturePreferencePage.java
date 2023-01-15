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

		public static final String P_FORMAT = "formatPreference";

		public static final String P_ENCODING = "encodingPreference";

		public static final String P_COLORS = "colorsPreference";

		public static final String P_SCREEN_RATE = "screenRatePreference";

		public static final String P_MOUSE = "mouseCursorPreference";

		public static final String P_MOUSE_RATE = "mouseRatePreference";

		public static final String P_AUDIO = "audioPreference";

		public static final String P_AUDIO_RATE = "audioRatePrefence";

		public static final String P_SCREEN_RATE_AUTO = "screenRateAuto";
	}

	public VideoCapturePreferencePage() {
		super(GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
		setDescription("Video Capture Preferences.");
	}

	final String mouseFieldTextDesc = "&Mouse Rate";

	ScaleFieldEditor mouseRateEditor;

	BooleanFieldEditor adaptativeMouseRateEditor;

	BooleanFieldEditor addAudioEditor;

	RadioGroupFieldEditor audioInputEditor;

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new RadioGroupFieldEditor(PreferenceConstants.P_FORMAT, "&Format to generate the video", 3,
				new String[][] { { "&AVI", "avi" }, { "&QuickTime", "QuickTime" } }, getFieldEditorParent()));

		addField(new RadioGroupFieldEditor(PreferenceConstants.P_ENCODING, "&Encoding", 3,
				new String[][] { { "&Screen Capture", "screen capture" }, { "&Animation", "animation" },
						{ "&None", "none" }, { "&PNG", "png" }, { "&jpeg 100%", "jpeg100" },
						{ "j&peg 50%", "jpeg50" } },
				getFieldEditorParent()));

		addField(new RadioGroupFieldEditor(PreferenceConstants.P_COLORS, "&Colors", 3, new String[][] {
				{ "&Hundreds", "hundreds" }, { "&Thousands", "thousands" }, { "&Millions", "millions" } },
				getFieldEditorParent()));

		adaptativeMouseRateEditor = new BooleanFieldEditor(PreferenceConstants.P_SCREEN_RATE_AUTO,
				"Adaptative Screen Rate", getFieldEditorParent());
		addField(adaptativeMouseRateEditor);

		final String screenRateFieldTextDesc = "&Screen Rate";

		final ScaleFieldEditor screenRateEditor = new ScaleFieldEditor(PreferenceConstants.P_SCREEN_RATE,
				screenRateFieldTextDesc, getFieldEditorParent());

		screenRateEditor.setMinimum(1);
		screenRateEditor.setMaximum(30);
		screenRateEditor.setIncrement(1);
		screenRateEditor.setLabelText(screenRateFieldTextDesc + " (15 fps) ");
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

		addField(new RadioGroupFieldEditor(
				PreferenceConstants.P_MOUSE, "&Mouse", 3, new String[][] { { "&No Cursor", "no cursor" },
						{ "&Black Cursor", "black cursor" }, { "&White Cursor", "white cursor" } },
				getFieldEditorParent()));

		mouseRateEditor = new ScaleFieldEditor(PreferenceConstants.P_MOUSE_RATE, mouseFieldTextDesc,
				getFieldEditorParent());
		mouseRateEditor.setMinimum(1);
		mouseRateEditor.setMaximum(30);
		mouseRateEditor.setIncrement(1);
		mouseRateEditor.setLabelText(mouseFieldTextDesc + " (15 fps) ");
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
		addField(screenRateEditor);

		addAudioEditor = new BooleanFieldEditor(PreferenceConstants.P_AUDIO, "Add Audio", getFieldEditorParent());
		addField(addAudioEditor);

		audioInputEditor = new RadioGroupFieldEditor(PreferenceConstants.P_FORMAT, "&Format to generate the video", 3,
				new String[][] { { "&8.000 Hz", "8000" }, { "&11.025 Hz", "11025" }, { "&22.050 Hz", "22050" },
						{ "&44.100 Hz", "44100" } },
				getFieldEditorParent());

		addField(audioInputEditor);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource().equals(adaptativeMouseRateEditor)) {
			mouseRateEditor.setEnabled((boolean) event.getNewValue(), getFieldEditorParent());
		}
		if (event.getSource().equals(addAudioEditor)) {
			audioInputEditor.setEnabled((boolean) event.getNewValue(), getFieldEditorParent());
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