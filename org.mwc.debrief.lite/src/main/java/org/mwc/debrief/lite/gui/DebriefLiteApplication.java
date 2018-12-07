package org.mwc.debrief.lite.gui;

import java.awt.MenuShortcut;

import javax.swing.JMenu;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import MWC.GUI.Tool;

public class DebriefLiteApplication extends Application {

	public DebriefLiteApplication() {
		super();
		
		final JMenu newMen = new JMenu("test");
		_mru = new MWC.GUI.Dialogs.MruMenuManager(newMen, 7, 10, _appProps, "MRU");

	}
	
	@Override
	public void logStack(int status, String text) {
	}

	@Override
	public Session createSession() {
		return new DebriefLiteSession(getClipboard());
	}

	@Override
	public Session getCurrentSession() {
		return null;
	}

	@Override
	protected void setTitleName(String theStr) {
	}

	@Override
	protected void addMenuItem(String theMenu, String theLabel, Tool theTool, MenuShortcut theShortCut) {
	}

	@Override
	protected void addMenuSeparator(String theMenu) {
	}

	@Override
	protected void showSession(Session theSession) {
	}

	@Override
	protected void closeSessionGUI(Session theSession) {
	}

	@Override
	public void setCursor(int theCursor) {
	}

	@Override
	public void restoreCursor() {
	}

	public final void newSession(final Session theSession){
	
		Session session = theSession;
	    if (session == null)
	    {
	      session = new DebriefLiteSession(this, getClipboard(), super.getNewSessionName());
	    }

	    // pass the session to the parent
	    super.newSession(session);

	}
	  
}
