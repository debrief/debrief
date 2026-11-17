/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2026, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.ui.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * Shared steps for the Debrief NG UI tests: a clean workbench, a project with
 * the sample data, and opening a plot.
 */
final class DebriefBot {

	/** system property, set by the pom: the folder holding Debrief's sample data */
	static final String SAMPLE_DATA_PROPERTY = "debrief.sampleData";

	static final String PROJECT = "demo";

	static final SWTWorkbenchBot BOT = new SWTWorkbenchBot();

	static void closeAllEditors() {
		BOT.closeAllEditors();
	}

	/** open a file from the sample data in its editor, and wait for it */
	static SWTBotEditor openSample(final String name) throws Exception {
		final IFile file = sampleFile(name);
		final AtomicReference<Exception> failure = new AtomicReference<>();
		UIThreadRunnable.syncExec(() -> {
			try {
				final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IDE.openEditor(page, file, true);
			} catch (final Exception e) {
				failure.set(e);
			}
		});
		if (failure.get() != null) {
			throw failure.get();
		}
		final SWTBotEditor editor = BOT.editorByTitle(name);
		editor.setFocus();
		return editor;
	}

	/** the Outline view, shown and active */
	static SWTBotView outline() {
		UIThreadRunnable.syncExec(() -> {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView("org.eclipse.ui.views.ContentOutline");
			} catch (final Exception e) {
				throw new IllegalStateException(e);
			}
		});
		final SWTBotView view = BOT.viewByTitle("Outline");
		view.show();
		return view;
	}

	/**
	 * close the welcome page, maximise nothing, and make a workspace project
	 * holding the sample data
	 */
	static void prepareWorkbench() throws Exception {
		SWTBotPreferences.TIMEOUT = 20000;
		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
		UIThreadRunnable.syncExec(() -> {
			PlatformUI.getWorkbench().getIntroManager()
					.closeIntro(PlatformUI.getWorkbench().getIntroManager().getIntro());
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setSize(1600, 1200);
		});

		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(PROJECT);
		if (!project.exists()) {
			project.create(null);
		}
		project.open(null);
	}

	private static IFile sampleFile(final String name) throws Exception {
		final String dir = System.getProperty(SAMPLE_DATA_PROPERTY);
		if (dir == null) {
			throw new IllegalStateException("System property " + SAMPLE_DATA_PROPERTY + " is not set");
		}
		final File source = new File(dir, name);
		final IFile target = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT).getFile(name);
		if (!target.exists()) {
			try (InputStream in = new FileInputStream(source)) {
				target.create(in, true, null);
			}
		}
		return target;
	}

	private DebriefBot() {
	}
}
