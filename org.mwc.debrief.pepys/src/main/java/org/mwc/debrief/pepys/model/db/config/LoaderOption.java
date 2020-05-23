package org.mwc.debrief.pepys.model.db.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.mwc.debrief.model.utils.OSUtils;
import org.mwc.debrief.pepys.Activator;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;

public class LoaderOption {
	
	public static enum LoaderType {
		ENV_VARIABLE, DEFAULT_FILE, DRAG_AND_DROP_INI, DRAG_AND_DROP_SQLITE
	}
	
	private LoaderType _type;
	
	private String _path;

	public LoaderOption(LoaderType _type, String _path) {
		this._type = _type;
		this._path = _path;
	}

	public LoaderType getType() {
		return _type;
	}

	public String getPath() {
		return _path;
	}
	
	public InputStream getInputStream() throws IOException {
		switch (_type) {
		case ENV_VARIABLE:
			return new FileInputStream(new File(_path));
		case DEFAULT_FILE:
			return OSUtils.getInputStreamResource(DatabaseConnection.class, _path,
					Activator.PLUGIN_ID);
		case DRAG_AND_DROP_INI:
			return new FileInputStream(new File(_path));
		default:
			return null;
		}
	}
	
	public boolean isValid() {
		if (_type == LoaderType.ENV_VARIABLE) {
			return _path != null && new File(_path).isFile();
		}else if (_type == LoaderType.DEFAULT_FILE) {
			return _path != null;
		}else if (_type == LoaderType.DRAG_AND_DROP_INI){
			return _path != null;
		}else if (_type == LoaderType.DRAG_AND_DROP_SQLITE) {
			// Drag and Drop is always true
			return true;
		}
		
		// This will never happen
		return false; 
	}
}
