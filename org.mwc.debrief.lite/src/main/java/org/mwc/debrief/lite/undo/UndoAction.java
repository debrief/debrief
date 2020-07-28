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

package org.mwc.debrief.lite.undo;

import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;

import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.model.Command;

import MWC.GUI.Tools.Action;
import MWC.GUI.Undo.UndoBuffer;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
@SuppressWarnings("deprecation")
public class UndoAction extends AbstractAction implements Action, Observer, CommandAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Command actionCommand;

	private final UndoBuffer _buffer;

	public UndoAction(final UndoBuffer undoBuffer) {
		_buffer = undoBuffer;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		execute();

	}

	@Override
	public void commandActivated(final CommandActionEvent e) {
		actionPerformed(e);

	}

	@Override
	public void execute() {
		if (_buffer != null) {
			_buffer.undo();
		}

	}

	@Override
	public boolean isRedoable() {
		return false;
	}

	@Override
	public boolean isUndoable() {
		return false;
	}

	public void setActionCommand(final Command command) {
		this.actionCommand = command;
	}

	@Override
	public void undo() {

	}

	@Override
	public void update(final Observable o, final Object arg) {
		if (o instanceof UndoBuffer && actionCommand != null) {
			final UndoBuffer undoBuff = (UndoBuffer) o;
			if (undoBuff.canUndo()) {
				actionCommand.setActionEnabled(true);
				actionCommand
						.setActionRichTooltip(RichTooltip.builder().setTitle("Undo " + undoBuff.undoLabel()).build());
			} else {
				actionCommand.setActionEnabled(false);
			}
		}

	}

}
