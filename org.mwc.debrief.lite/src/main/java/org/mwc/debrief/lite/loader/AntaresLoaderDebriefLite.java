package org.mwc.debrief.lite.loader;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Debrief.ReaderWriter.Antares.ImportAntares;
import Debrief.ReaderWriter.Antares.ImportAntaresImpl.ImportAntaresException;
import MWC.GUI.Layers;

public class AntaresLoaderDebriefLite {

	public static void handleImportAntares(final File file, final ImportAntares antaresImporter, final Layers theLayers,
			final JFrame owner) {
		// We need to ask to the user the trackname, month and year.

		antaresImporter.setLayers(theLayers);

		final JPanel mainPanel = new JPanel(new BorderLayout());

		final JLabel nameOfTheTrackLabel = new JLabel("Please, choose the name of the track");
		nameOfTheTrackLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JLabel dateLabel = new JLabel("Please, choose the month and year");
		dateLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JTextField nameOfTheTrackTextField = new JTextField();
		nameOfTheTrackTextField.setToolTipText("Name of the track");

		final JComboBox<String> monthComboBox = new JComboBox<String>(
				IntStream.range(1, 13).mapToObj(i -> String.format("%01d", i)).toArray(String[]::new));
		monthComboBox.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
		final JTextField yearTextField = new JTextField();
		yearTextField.setText(Calendar.getInstance().get(Calendar.YEAR) + "");

		final JButton cancelButton = new JButton("Cancel");
		final JButton acceptButton = new JButton("Accept");

		final JPanel centerPanel = new JPanel();

		centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.add(nameOfTheTrackLabel);
		centerPanel.add(nameOfTheTrackTextField);
		centerPanel.add(dateLabel);

		final JPanel datePanel = new JPanel();
		datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));
		datePanel.add(monthComboBox);
		datePanel.add(yearTextField);

		centerPanel.add(datePanel);

		mainPanel.add(centerPanel, BorderLayout.CENTER);

		final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(acceptButton);

		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

		final JDialog frame = new JDialog(owner, "Antares Import Configuration", true) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4759126543816346899L;

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(300, 200);
			}

		};
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});

		acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				antaresImporter.setMonth(monthComboBox.getSelectedIndex());
				antaresImporter.setYear(Integer.parseInt(yearTextField.getText()));
				antaresImporter.setTrackName(nameOfTheTrackTextField.getText());
				try {
					antaresImporter.importThis(file.getName(), new FileInputStream(file));
					if (!antaresImporter.getErrors().isEmpty()) {
						final StringBuilder builder = new StringBuilder();
						for (ImportAntaresException exception : antaresImporter.getErrors()) {
							builder.append(exception.getMessage());
							builder.append("\n");
						}
						JOptionPane.showMessageDialog(owner,
								"Antares file was imported, but with errors: " + builder.toString().trim());
					}
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(owner, "Antares file was not found " + e1.getMessage());
				}
				frame.dispose();
			}
		});
		frame.setLocationRelativeTo(null);
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setVisible(true);
	}
}
