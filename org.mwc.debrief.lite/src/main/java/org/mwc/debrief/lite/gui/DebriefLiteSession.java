package org.mwc.debrief.lite.gui;

import java.awt.datatransfer.Clipboard;
import javax.swing.JScrollPane;

import Debrief.GUI.Frames.Session;
import MWC.GUI.ToolParent;

public final class DebriefLiteSession extends Session {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3661691617306670541L;

	/** an instance of DebriefLiteImportData **/
	private transient ToolParent theParent;

	private JScrollPane scrollPane;
	
	public DebriefLiteSession(Clipboard theClipboard) {
		super(theClipboard);
	}

	public DebriefLiteSession(final ToolParent parent, Clipboard clipBoard, final String newName) {
		super(clipBoard);
	}
	
	public void setToolParent(final ToolParent theParent) {
		this.theParent = theParent;
	}
	
	@Override
	protected boolean wantsToClose() {
		return false;
	}

	@Override
	public void closeGUI() {
	}

	@Override
	public void repaint() {
	}

	/**
	 * this method initializes the form, but do we need it now? we will see later
	 * 
	 */
	@Override
	public void initialiseForm(ToolParent theParent) {
	}

	/**
	 * there is dependency of parent view container. So sets the
	 * proper container here, In this case, it is going to be
	 * JScrollPane where we draw the tracks
	 * 
	 * @param pane
	 */
	public void resolveViewDependency(final JScrollPane pane) {
		this.scrollPane = pane;
	}
	
}
