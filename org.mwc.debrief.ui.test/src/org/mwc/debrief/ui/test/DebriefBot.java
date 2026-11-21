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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.mwc.cmap.plotViewer.editors.CorePlotEditor;

import MWC.GUI.Layers;

/**
 * Shared steps for the Debrief NG UI tests: a clean workbench, a project with
 * the sample data, and opening a plot.
 */
final class DebriefBot {

	/** system property, set by the pom: the folder holding Debrief's sample data */
	static final String SAMPLE_DATA_PROPERTY = "debrief.sampleData";

	static final String PROJECT = "demo";

	static final SWTWorkbenchBot BOT = new SWTWorkbenchBot();

	/** the size of the chart canvas in a screenshot */
	static final Point CHART_SIZE = new Point(1400, 900);

	/**
	 * close every editor, unsaved, in every workbench window. Not SWTBot's
	 * closeAllEditors: that sees only the active window, and under xvfb (no
	 * window manager) no window may be active after a dialog closes. An editor
	 * left open makes the next test reuse it, and an unsaved one makes the
	 * workbench ask "Save changes?" at exit, which nobody answers.
	 */
	static void closeAllEditors() {
		restoreLayout();
		UIThreadRunnable.syncExec(() -> {
			for (final IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
				for (final IWorkbenchPage page : window.getPages()) {
					page.closeAllEditors(false);
				}
			}
		});
	}

	/** the workbench window: the active one, or the first if none is active */
	static IWorkbenchWindow window() {
		final IWorkbenchWindow active = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		return active != null ? active : PlatformUI.getWorkbench().getWorkbenchWindows()[0];
	}

	/** make the workbench window active again, as a dialog closing should */
	static void activateWorkbench() {
		UIThreadRunnable.syncExec(() -> {
			final Shell shell = window().getShell();
			shell.forceActive();
			shell.setFocus();
		});
	}

	/** open a file from the sample data in its editor, and wait for it */
	static SWTBotEditor openSample(final String name) throws Exception {
		final IFile file = sampleFile(name);
		final AtomicReference<Exception> failure = new AtomicReference<>();
		UIThreadRunnable.syncExec(() -> {
			try {
				final IWorkbenchPage page = window().getActivePage();
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

	/**
	 * start opening a file from the sample data without waiting: for files whose
	 * import asks a question (a REP file's track mode), so the test can answer it
	 */
	static void openSampleAsync(final String name) throws Exception {
		final IFile file = sampleFile(name);
		UIThreadRunnable.asyncExec(() -> {
			try {
				final IWorkbenchPage page = window().getActivePage();
				IDE.openEditor(page, file, true);
			} catch (final Exception e) {
				throw new IllegalStateException(e);
			}
		});
	}

	/**
	 * answer the "Select track mode" dialog that importing a REP file shows, once
	 * per track
	 */
	static void answerTrackMode(final boolean overTheGround, final int tracks) {
		for (int i = 0; i < tracks; i++) {
			final SWTBotShell dialog = BOT.shell("Select track mode");
			dialog.activate();
			dialog.bot().radio(overTheGround ? 1 : 0).click();
			dialog.bot().button("OK").click();
			BOT.waitUntil(Conditions.shellCloses(dialog));
		}
	}

	/** the plot editor behind an editor reference */
	static CorePlotEditor plotEditor(final SWTBotEditor editor) {
		return UIThreadRunnable.syncExec(() -> (CorePlotEditor) editor.getReference().getEditor(false));
	}

	/**
	 * maximise the editor, fit the plot to it, let it paint, and copy what its
	 * chart canvas shows
	 */
	static BufferedImage captureChart(final SWTBotEditor editor) {
		final CorePlotEditor plot = plotEditor(editor);
		UIThreadRunnable.syncExec(() -> {
			final IWorkbenchPage page = window().getActivePage();
			page.setPartState(page.getReference(plot), IWorkbenchPage.STATE_MAXIMIZED);
			// size the window so the canvas is exactly CHART_SIZE, whatever the
			// workbench's toolbars and borders take
			final Shell shell = window().getShell();
			final Control canvas = plot.getChart().getCanvasControl();
			for (int i = 0; i < 5; i++) {
				final Point now = canvas.getSize();
				if (now.equals(CHART_SIZE)) {
					break;
				}
				final Point outer = shell.getSize();
				shell.setSize(outer.x + CHART_SIZE.x - now.x, outer.y + CHART_SIZE.y - now.y);
				shell.layout(true, true);
				while (Display.getCurrent().readAndDispatch()) {
					// let the layout settle
				}
			}
		});
		settle();
		// fit to the window only now the canvas has its final size, and let it
		// repaint; twice, as a fit can move the plot by a few pixels when the
		// projection catches up with the new size
		for (int i = 0; i < 2; i++) {
			UIThreadRunnable.syncExec(() -> {
				plot.getChart().rescale();
				plot.getChart().getCanvasControl().redraw();
				plot.getChart().getCanvasControl().update();
			});
			settle();
		}
		return UIThreadRunnable.syncExec(() -> {
			final Control canvas = plot.getChart().getCanvasControl();
			final Point size = canvas.getSize();
			if (!size.equals(CHART_SIZE)) {
				throw new IllegalStateException("The chart canvas is " + size + ", not " + CHART_SIZE);
			}
			final Image image = new Image(canvas.getDisplay(), size.x, size.y);
			final GC gc = new GC(canvas);
			try {
				gc.copyArea(image, 0, 0);
				return toBufferedImage(image.getImageData());
			} finally {
				gc.dispose();
				image.dispose();
			}
		});
	}

	/** run the UI events that are waiting, a few times, with short pauses */
	static void settle() {
		for (int i = 0; i < 5; i++) {
			UIThreadRunnable.syncExec(() -> {
				while (Display.getCurrent().readAndDispatch()) {
					// drain
				}
			});
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
	}

	private static BufferedImage toBufferedImage(final ImageData data) {
		final BufferedImage res = new BufferedImage(data.width, data.height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < data.height; y++) {
			for (int x = 0; x < data.width; x++) {
				final RGB rgb = data.palette.getRGB(data.getPixel(x, y));
				res.setRGB(x, y, (rgb.red << 16) | (rgb.green << 8) | rgb.blue);
			}
		}
		return res;
	}

	/** restore any maximised part, so the next test sees the normal layout */
	static void restoreLayout() {
		UIThreadRunnable.syncExec(() -> {
			window().getShell().setSize(1600, 1200);
			final IWorkbenchPage page = window().getActivePage();
			if (page.getActivePartReference() != null) {
				page.setPartState(page.getActivePartReference(), IWorkbenchPage.STATE_RESTORED);
			}
		});
	}

	/** a view by id, shown and active */
	static SWTBotView showView(final String id) {
		UIThreadRunnable.syncExec(() -> {
			try {
				window().getActivePage().showView(id);
			} catch (final Exception e) {
				throw new IllegalStateException(e);
			}
		});
		final SWTBotView view = BOT.viewById(id);
		view.show();
		return view;
	}

	/**
	 * the labels of the Outline's top-level items, once they are filled in (the
	 * tree is populated before its labels are)
	 */
	static List<String> outlineLabels() {
		final SWTBotView outline = outline();
		BOT.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() {
				final SWTBotTreeItem[] items = outline.bot().tree().getAllItems();
				if (items.length == 0) {
					return false;
				}
				for (final SWTBotTreeItem item : items) {
					if (item.getText().isEmpty()) {
						return false;
					}
				}
				return true;
			}

			@Override
			public String getFailureMessage() {
				return "Outline labels were not filled in";
			}
		});
		final List<String> labels = new ArrayList<>();
		for (final SWTBotTreeItem item : outline.bot().tree().getAllItems()) {
			labels.add(item.getText());
		}
		return labels;
	}

	/**
	 * click a button on the Outline view's toolbar, once the current selection
	 * has enabled it
	 */
	static void clickOutlineTool(final String tooltip) {
		final SWTBotView outline = outline();
		final SWTBotToolbarButton button = outline.getToolbarButtons().stream()
				.filter(b -> tooltip.equals(b.getToolTipText())).findFirst()
				.orElseThrow(() -> new IllegalStateException("No Outline tool \"" + tooltip + "\""));
		waitFor(button::isEnabled, "Outline tool \"" + tooltip + "\" was not enabled");
		button.click();
	}

	/**
	 * the editor that {@link #openSampleAsync} opened, once its import finishes;
	 * if it doesn't appear, the failure lists the open shells, the open editors
	 * and the end of the error log, to show what held it up
	 */
	static SWTBotEditor editorAfterImport(final String name) {
		final AtomicReference<IEditorReference> found = new AtomicReference<>();
		BOT.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() {
				found.set(UIThreadRunnable.syncExec(() -> findEditor(name)));
				return found.get() != null;
			}

			@Override
			public String getFailureMessage() {
				return "no editor '" + name + "' after its import. " + describeWorkbench();
			}
		});
		// SWTBot finds editors only in the active window, and after the import's
		// dialogs close under xvfb there may be none: wrap the reference directly
		activateWorkbench();
		final SWTBotEditor editor = new SWTBotEditor(found.get(), BOT);
		editor.setFocus();
		return editor;
	}

	/**
	 * the editor with this title in any workbench window (not the active one:
	 * there is none while a dialog has focus), once its part exists; or null.
	 * The reference and its title appear before the import finishes creating
	 * the editor.
	 */
	private static IEditorReference findEditor(final String name) {
		for (final IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (final IWorkbenchPage page : window.getPages()) {
				for (final IEditorReference ref : page.getEditorReferences()) {
					if (name.equals(ref.getTitle()) && ref.getEditor(false) != null) {
						return ref;
					}
				}
			}
		}
		return null;
	}

	/** the open shells and editors, and the end of the error log */
	static String describeWorkbench() {
		// threads first, from this (non-UI) thread: if the UI thread is stuck, the
		// syncExec below never returns, and these show where it and the import are
		final StringBuilder res = new StringBuilder(threadDump()).append('\n');
		UIThreadRunnable.syncExec(() -> {
			res.append("Shells:");
			for (final Shell shell : Display.getDefault().getShells()) {
				res.append(" [").append(shell.getText()).append(shell.isVisible() ? "" : " (hidden)")
						.append(shell == Display.getDefault().getActiveShell() ? " (active)" : "").append(']');
			}
			res.append(". Editors:");
			for (final IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
				for (final IWorkbenchPage page : window.getPages()) {
					for (final IEditorReference ref : page.getEditorReferences()) {
						res.append(" [").append(ref.getTitle()).append(']');
					}
				}
			}
			res.append(". Active window: ").append(PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null);
		});
		res.append(". Error log ends:\n");
		try {
			final List<String> lines = Files.readAllLines(Platform.getLogFileLocation().toFile().toPath());
			res.append(String.join("\n", lines.subList(Math.max(0, lines.size() - 40), lines.size())));
		} catch (final Exception e) {
			res.append("(no log: ").append(e).append(')');
		}
		return res.toString();
	}

	/**
	 * the stacks of the threads running Debrief, SWT or JFace code (the UI
	 * thread, a progress-service worker such as ModalContext): enough to see
	 * where an import that never finishes is waiting
	 */
	static String threadDump() {
		final StringBuilder res = new StringBuilder("Threads:");
		for (final Map.Entry<Thread, StackTraceElement[]> e : Thread.getAllStackTraces().entrySet()) {
			final StackTraceElement[] stack = e.getValue();
			boolean ours = false;
			for (final StackTraceElement frame : stack) {
				final String c = frame.getClassName();
				if (c.startsWith("org.mwc.") || c.startsWith("Debrief.") || c.startsWith("MWC.")
						|| c.startsWith("org.eclipse.swt.") || c.startsWith("org.eclipse.jface.")) {
					ours = true;
					break;
				}
			}
			if (!ours) {
				continue;
			}
			final Thread th = e.getKey();
			res.append("\n\"").append(th.getName()).append("\" ").append(th.getState());
			for (int i = 0; i < Math.min(stack.length, 30); i++) {
				res.append("\n\tat ").append(stack[i]);
			}
			if (stack.length > 30) {
				res.append("\n\t... ").append(stack.length - 30).append(" more");
			}
		}
		return res.toString();
	}

	/** wait, up to SWTBot's timeout, until the check holds */
	static void waitFor(final BooleanSupplier check, final String failure) {
		BOT.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() {
				return check.getAsBoolean();
			}

			@Override
			public String getFailureMessage() {
				return failure;
			}
		});
	}

	/** the layers of the plot an editor shows */
	static Layers layers(final SWTBotEditor editor) {
		return UIThreadRunnable.syncExec(() -> (Layers) plotEditor(editor).getAdapter(Layers.class));
	}

	/**
	 * drop files on a plot, as dragging them from Navigator does, without
	 * waiting: the import may show a wizard for the test to drive
	 */
	static void dropOnPlot(final SWTBotEditor editor, final String... sampleNames) throws Exception {
		final String[] paths = new String[sampleNames.length];
		for (int i = 0; i < sampleNames.length; i++) {
			paths[i] = sampleFile(sampleNames[i]).getLocation().toOSString();
		}
		final CorePlotEditor plot = plotEditor(editor);
		final Method drop = findMethod(plot.getClass(), "filesDropped", String[].class);
		drop.setAccessible(true);
		UIThreadRunnable.asyncExec(() -> {
			try {
				drop.invoke(plot, (Object) paths);
			} catch (final Exception e) {
				throw new IllegalStateException(e);
			}
		});
	}

	private static Method findMethod(final Class<?> type, final String name, final Class<?>... params)
			throws NoSuchMethodException {
		for (Class<?> c = type; c != null; c = c.getSuperclass()) {
			try {
				return c.getDeclaredMethod(name, params);
			} catch (final NoSuchMethodException e) {
				// try the parent
			}
		}
		throw new NoSuchMethodException(name);
	}

	/** the Outline view, shown and active */
	static SWTBotView outline() {
		UIThreadRunnable.syncExec(() -> {
			try {
				window().getActivePage()
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
			window().getShell().setSize(1600, 1200);
		});

		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(PROJECT);
		if (!project.exists()) {
			project.create(null);
		}
		project.open(null);
	}

	static IFile sampleFile(final String name) throws Exception {
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
