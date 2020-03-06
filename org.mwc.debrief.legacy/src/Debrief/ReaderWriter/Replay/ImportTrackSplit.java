package Debrief.ReaderWriter.Replay;

import java.text.ParseException;

import MWC.GUI.Plottable;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;

class ImportTrackSplit extends AbstractPlainLineImporter {

	private final static String TYPE_STR = ";TRACKSPLIT:";

	/**
	 * the type for this importer
	 */
	private final String _type;
	
	public ImportTrackSplit()
	{
		this(TYPE_STR);
	}
	
	public ImportTrackSplit(final String type) {
		_type = type;
	}

	@Override
	public boolean canExportThis(Object val) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String exportThis(Plottable theShape) {
		return _type;
	}

	/**
	 * determine the identifier returning this type of annotation
	 */
	@Override
	public String getYourType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object readThisLine(String theLine) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

}
