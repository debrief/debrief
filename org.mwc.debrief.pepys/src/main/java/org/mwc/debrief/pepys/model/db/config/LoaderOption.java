package org.mwc.debrief.pepys.model.db.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.mwc.debrief.model.utils.OSUtils;
import org.mwc.debrief.pepys.Activator;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;

/**
 * Class used to specify where we are reading the database from and what type of
 * configuration it is. For example, from drag and drop, from environment
 * variable, sample database etc
 *
 */
public class LoaderOption {

	/**
	 * Enum used to indicate the type of database source we are using
	 *
	 */
	public static enum LoaderType {
		ENV_VARIABLE, DEFAULT_FILE, DRAG_AND_DROP_INI, DRAG_AND_DROP_SQLITE
	}

	private final LoaderType _type;

	/**
	 * Path of the database source. It can be an ini or a sqlite
	 */
	private final String _path;

	public LoaderOption(final LoaderType _type, final String _path) {
		this._type = _type;
		this._path = _path;
	}

	/**
	 *
	 * @return An InputStream of the ini file contained (in case we have one), null
	 *         for sqlite files
	 * @throws IOException Exception when the file given in the constructor is
	 *                     invalid
	 */
	public InputStream getInputStream() throws IOException {
		switch (_type) {
		case ENV_VARIABLE:
			return new FileInputStream(new File(_path));
		case DEFAULT_FILE:
			return OSUtils.getInputStreamResource(DatabaseConnection.class, _path, Activator.PLUGIN_ID);
		case DRAG_AND_DROP_INI:
			return new FileInputStream(new File(_path));
		default:
			return null;
		}
	}

	/**
	 * Method that returns the path of the database source
	 *
	 * @return path of the database source
	 */
	public String getPath() {
		return _path;
	}

	/**
	 * Method that returns the type of database source we are using
	 *
	 * @return type of database source we are using
	 */
	public LoaderType getType() {
		return _type;
	}

	/**
	 *
	 * @return true if the configuration is valid. It checks basic things like if
	 *         the file exists, if it is available, etc
	 */
	public boolean isValid() {
		if (_type == LoaderType.ENV_VARIABLE) {
			return _path != null && new File(_path).isFile();
		} else if (_type == LoaderType.DEFAULT_FILE) {
			return _path != null;
		} else if (_type == LoaderType.DRAG_AND_DROP_INI) {
			return _path != null;
		} else if (_type == LoaderType.DRAG_AND_DROP_SQLITE) {
			// Drag and Drop is always true
			return true;
		}

		// This will never happen
		return false;
	}
}
