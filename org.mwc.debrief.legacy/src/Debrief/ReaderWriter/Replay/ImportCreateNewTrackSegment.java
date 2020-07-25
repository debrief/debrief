package Debrief.ReaderWriter.Replay;

import java.text.ParseException;

import MWC.GUI.Plottable;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;

public class ImportCreateNewTrackSegment extends AbstractPlainLineImporter {

	/**
	 * the type for this string
	 */
	private final String _myType = ";TRACKSPLIT";
	
	/**
	 * indicate if you can export this type of object
	 *
	 * @param val the object to test
	 * @return boolean saying whether you can do it
	 */
	@Override
	public boolean canExportThis(Object val) {
		return false;
	}

	@Override
	public String exportThis(Plottable theShape) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * determine the identifier returning this type of annotation
	 */
	@Override
	public String getYourType() {
		return _myType;
	}

	@Override
	public Object readThisLine(String theLine) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

}
