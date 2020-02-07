
package org.mwc.debrief.dis.editor;

import org.eclipse.ui.editors.text.TextEditor;

public class InputFileEditor extends TextEditor {

	public InputFileEditor() {
		setSourceViewerConfiguration(new InputFileViewerConfig());
	}
}