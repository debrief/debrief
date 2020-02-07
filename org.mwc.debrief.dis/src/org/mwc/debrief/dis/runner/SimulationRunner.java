package org.mwc.debrief.dis.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.mwc.debrief.dis.DisActivator;
import org.mwc.debrief.dis.ui.preferences.DebriefDISSimulatorPrefs;

public class SimulationRunner {

	final private DebriefDISSimulatorPrefs _prefs;
	private Process _process;
	private Thread oThread;

	private MessageConsole disConsole;
	private MessageConsoleStream msgStream;

	public SimulationRunner(final DebriefDISSimulatorPrefs simPrefs) {
		_prefs = simPrefs;
	}

	public void run(final String inFile) {
		final File input = new File(inFile);
		final File parent = input.getParentFile();

		if (!input.exists()) {
			System.err.println("Input file not found");
		} else if (!parent.isDirectory()) {
			System.err.println("Unable to find parent directory");
		} else {
			final String[] exeCmd = new String[] { _prefs.getExePath(), inFile };

			// fire up the processs
			try {
				// get ready to write to the console
				if (disConsole == null) {
					try {
						disConsole = new MessageConsole("DIS Output", null);
						ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { disConsole });
						msgStream = disConsole.newMessageStream();
					} catch (final NoClassDefFoundError de) {
						DisActivator.log(IStatus.WARNING, "Could not find console UI", de);
					}
				} else {
					disConsole.clearConsole();
				}

				_process = Runtime.getRuntime().exec(exeCmd, null, parent);

				final BufferedReader iReader = new BufferedReader(new InputStreamReader(_process.getInputStream()));

				final Runnable outputter = new Runnable() {
					boolean _terminated = false;

					@Override
					public void run() {
						while (!_terminated) {
							String line;
							try {
								line = iReader.readLine();
								if (line != null) {
									if (msgStream != null) {
										msgStream.println(line);
									} else {
										System.err.println(line);
									}
								}

								// has it completed?
								try {
									_process.exitValue();
									_terminated = true;
									System.err.println("TERMINATING OUTPUTTER");
								} catch (final Exception e) {
								}

							} catch (final IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				};

				oThread = new Thread(outputter);
				oThread.start();

			} catch (final IOException e1) {
				final String msg;
				if (e1.getMessage().contains("Permission denied")) {
					msg = "Permission denied. If on Unix the script requires the executable bit to be set";
				} else {
					msg = "IO Exception whilst starting simulator";
				}
				DisActivator.log(IStatus.ERROR, msg, e1);
			}
		}
	}

	public void stop() {
		if (_process != null) {
			_process.destroy();
			_process = null;
		}
		if (oThread != null) {
			// thread.destroy() was never implemented:
			// https://docs.oracle.com/javase/8/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html#targetText=Thread.destroy%20was%20never%20implemented,a%20subsequent%20Thread.resume%20.)

			// oThread.destroy();
			oThread = null;
		}
	}

}
