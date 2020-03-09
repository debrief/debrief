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

package ASSET.GUI.MonteCarlo;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.xpath.XPathExpressionException;

import ASSET.Scenario.CoreScenario;
import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Properties.Swing.SwingCustomEditor;
import MWC.Utilities.TextFormatting.GMTDateFormat;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 07-Oct-2004 Time: 13:58:18 To
 * change this template use File | Settings | File Templates.
 */
abstract public class Loader extends SwingCustomEditor {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * label to use to prefix the scenario file name
	 */
	private static final String SCENARIO_LABEL = "<i>Scenario file:</i>";

	/**
	 * label to use to prefix the control file name
	 */
	private static final String CONTROL_LABEL = "<i>Control file:</i>";

	/**
	 * the loader to handle actually building the scenario
	 */
	LoaderCore _myLoader;

	/**
	 * the build scenario button
	 */
	JButton _buildButton;

	/**
	 * the label where we show the dropped control file
	 */
	private JLabel _controllerCatcher;

	/**
	 * the label where we show the dropped scenario file
	 */
	private JLabel _scenarioCatcher;

	/**
	 * window to track build progress
	 */
	JTextArea _progressWindow;

	// ////////////////////////////////////////////////
	//
	// ////////////////////////////////////////////////

	/**
	 * constructor - builds & connects the front-end
	 */
	public Loader(final CoreScenario theScenario, final PlainChart theChart, final ToolParent theParent,
			final PropertiesPanel theProperties) {

		// setup the loader
		_myLoader = new LoaderCore(theScenario) {
			DateFormat sdf = new GMTDateFormat("[hh:mm:ss]");

			@Override
			public void buildEnabled(final boolean enabled) {
				_buildButton.setEnabled(enabled);
			}

			/**
			 * write a message to a message tracking window
			 */
			@Override
			void writeMessage(String msg) {
				final Date now = new Date();
				final String dtg = sdf.format(now);
				msg = dtg + " " + msg;
				_progressWindow.setText(_progressWindow.getText() + "\n" + msg);
			}
		};

		// tell our parent object about the important stuff
		setObject(null, theParent, _theLayers, theProperties);

		// build the form
		initForm();

	}

	/**
	 * constructor - builds & connects the front-end, loads data-files
	 */
	public Loader(final CoreScenario myScenario, final String scenarioFile, final String controlFile,
			final PlainChart theChart, final ToolParent theParent, final PropertiesPanel theProperties) {
		this(myScenario, theChart, theParent, theProperties);

		// and configure the files
		setScenario(new File(scenarioFile));
		setController(new File(controlFile));

	}

	// ////////////////////////////////////////////////
	// Swing custom editor methods
	// ////////////////////////////////////////////////

	/**
	 * user has tried to close the loader panel = we'll just hide it
	 */
	@Override
	abstract public void doClose();

	// ////////////////////////////////////////////////
	//
	// ////////////////////////////////////////////////

	/**
	 * build the interface
	 */
	private void initForm() {

		// first the create button
		_buildButton = new JButton("Import");
		_buildButton.setEnabled(false);
		_buildButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				// ok, get the generation going.
				startGenerate();
			}
		});

		// and the close button
		final JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			/**
			 * Invoked when an action occurs.
			 */
			@Override
			public void actionPerformed(final ActionEvent e) {
				doClose();
			}
		});

		// now the scenario catcher
		_scenarioCatcher = new JLabel("<HTML>" + SCENARIO_LABEL + " [drop here]</html>");
		_scenarioCatcher.setBorder(BorderFactory.createLoweredBevelBorder());
		final DropTarget scenarioTarget = new DropTarget();
		scenarioTarget.setActive(true);
		try {
			scenarioTarget.addDropTargetListener(new DropTargetAdapter() {
				@Override
				@SuppressWarnings("rawtypes")
				public void drop(final DropTargetDropEvent dtde) {
					// see if it's an XML file
					if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						try {
							dtde.acceptDrop(DnDConstants.ACTION_COPY);

							final Transferable tr = dtde.getTransferable();
							final List list = (List) tr.getTransferData(DataFlavor.javaFileListFlavor);
							if (list.size() > 1) {
								MWC.Utilities.Errors.Trace.trace("One file at a time please", true);
							} else {
								final File thisFile = (File) list.iterator().next();
								setScenario(thisFile);
							}
							// ASSET.Util.XML.ASSETReaderWriter.importThis(_myList, s, new
							// java.io.FileInputStream(s));
							dtde.dropComplete(true);
						} catch (final UnsupportedFlavorException e) {
							e.printStackTrace(); // To change body of catch statement use File
													// | Settings | File Templates.
						} catch (final IOException e) {
							e.printStackTrace(); // To change body of catch statement use File
													// | Settings | File Templates.
						}
					}
				}
			});
		} catch (final TooManyListenersException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		_scenarioCatcher.setDropTarget(scenarioTarget);

		// and the controller catcher
		_controllerCatcher = new JLabel("<HTML>" + CONTROL_LABEL + " [drop here]</html>");
		_controllerCatcher.setBorder(BorderFactory.createLoweredBevelBorder());
		final DropTarget controllerTarget = new DropTarget();
		try {
			controllerTarget.addDropTargetListener(new DropTargetAdapter() {
				@Override
				@SuppressWarnings("rawtypes")
				public void drop(final DropTargetDropEvent dtde) {
					// see if it's an XML file
					if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						try {
							dtde.acceptDrop(DnDConstants.ACTION_COPY);

							final Transferable tr = dtde.getTransferable();
							final List list = (List) tr.getTransferData(DataFlavor.javaFileListFlavor);
							if (list.size() > 1) {
								MWC.Utilities.Errors.Trace.trace("One file at a time please", true);
							} else {
								final File thisFile = (File) list.iterator().next();
								setController(thisFile);
							}
							// ASSET.Util.XML.ASSETReaderWriter.importThis(_myList, s, new
							// java.io.FileInputStream(s));
							dtde.dropComplete(true);
						} catch (final UnsupportedFlavorException e) {
							e.printStackTrace(); // To change body of catch statement use File
													// | Settings | File Templates.
						} catch (final IOException e) {
							e.printStackTrace(); // To change body of catch statement use File
													// | Settings | File Templates.
						}
					}
				}
			});
		} catch (final TooManyListenersException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		_controllerCatcher.setDropTarget(controllerTarget);

		final JPanel fileHolder = new JPanel();
		fileHolder.setLayout(new GridLayout(2, 0));
		fileHolder.add(_scenarioCatcher);
		fileHolder.add(_controllerCatcher);

		_progressWindow = new JTextArea();
		_progressWindow.setBorder(BorderFactory.createLoweredBevelBorder());

		final JPanel holder = new JPanel();
		holder.setLayout(new BorderLayout());
		holder.add(fileHolder, BorderLayout.NORTH);
		holder.add(new JScrollPane(_progressWindow), BorderLayout.CENTER);

		final JPanel buttonHolder = new JPanel();
		buttonHolder.setLayout(new GridLayout(2, 0));
		buttonHolder.add(_buildButton);
		buttonHolder.add(closeButton);

		holder.add(buttonHolder, BorderLayout.SOUTH);

		this.setLayout(new BorderLayout());
		this.add(holder, BorderLayout.CENTER);

		this.setName("Loader");
	}

	/**
	 * store the controller filename
	 *
	 * @param thisFile
	 */
	void setController(final File thisFile) {
		_myLoader.setControllerFile(thisFile);
		_controllerCatcher.setText("<html>" + CONTROL_LABEL + thisFile.getName() + "</html>");
		_buildButton.setText("Generate");
	}

	/**
	 * update the editor with the supplied object
	 */
	@Override
	public void setObject(final Object data) {
		// hey, don't worry about this.
	}

	/**
	 * store the scenario filename
	 *
	 * @param thisFile
	 */
	void setScenario(final File thisFile) {
		_myLoader.setScenarioFile(thisFile);
		_scenarioCatcher.setText("<html>" + SCENARIO_LABEL + thisFile.getName() + "</html>");
	}

	/**
	 * get the loader to load itself. Note that we do it in a separate thread so
	 * that the GUI can still get updated.
	 */
	void startGenerate() {
		final Thread runner = new Thread() {
			@Override
			public void run() {
				super.run(); // To change body of overridden methods use File | Settings
								// | File Templates.

				// suspend updates
				suspendUpdates(true);

				try {
					_myLoader.buildScenario();
				} catch (final XPathExpressionException e) {
					e.printStackTrace();
				}

				// suspend updates
				suspendUpdates(false);

				// and rebuild the plot
				_theLayers.fireExtended();
			}
		};

		runner.start();
	}

	public void suspendUpdates(final boolean override) {
		_theLayers.suspendFiringExtended(override);
	}

}
