package org.mwc.debrief.dis.diagnostics.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class CoreFileListener implements LoggingFileWriter {
	/**
	 *
	 */
	final private String _path;
	private FileWriter _outF;
	final private boolean _toFile;
	final private boolean _toScreen;
	final private String _dataType;
	final private String _header;
	final private LoggingFileWriter _writer;

	final String LINE_BREAK = System.getProperty("line.separator");
	final String SUFFIX = ".csv";

	/**
	 *
	 * @param root     the path for the output file
	 * @param toFile   whether to write to file
	 * @param toScreen whether to write to standard output
	 * @param dataType
	 * @param header
	 * @param writer   the object that will do the writing (or null to write to
	 *                 file)
	 */
	CoreFileListener(final String root, final boolean toFile, final boolean toScreen, final String dataType,
			final String header, final LoggingFileWriter writer) {
		_path = root;
		_dataType = dataType;
		_toFile = toFile;
		_toScreen = toScreen;
		_header = header;

		// has a writer been supplied?
		if (writer == null) {
			// nope, we'll do it ourselves
			_writer = this;
		} else {
			// yes, well let him do the work
			_writer = writer;
		}
	}

	protected void createOut(final String filename, final String header) throws IOException {
		// check the output folder exists
		final File folder = new File(_path);
		if (!folder.exists()) {
			// nope, create the necessary folders
			folder.mkdirs();
		}

		// ok, create it
		_outF = new FileWriter(new File(_path, filename));

		// and insert the header line
		_outF.write(header);
		_outF.write(LINE_BREAK);
	}

	public void write(final String output) {
		_writer.writeThis(_dataType, _header, output);
	}

	@Override
	public void writeThis(final String dType, final String header, final String output) {
		if (_toScreen) {
			// output the normal line
			System.out.print(_dataType + ":" + output);
		}

		if (_toFile) {
			// is our file created?
			try {
				if (_outF == null) {
					// write the header
					createOut(_dataType + SUFFIX, _header);
				}

				// and our output
				_outF.write(output);

				// flush - so we have as many lines in there as possible
				_outF.flush();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
}