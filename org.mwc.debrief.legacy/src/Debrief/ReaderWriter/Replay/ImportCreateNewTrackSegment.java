package Debrief.ReaderWriter.Replay;

import java.text.ParseException;
import java.util.StringTokenizer;

import org.apache.poi.ss.formula.eval.NotImplementedException;

import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * Class to parse the TRACKSPLIT annotation. Format:
 * 
 * ;TRACKSPLIT DTG NAME
 * 
 * Sample:
 * 
 * ;TRACKSPLIT 951212 073100.000 NELSON
 */
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
	public boolean canExportThis(final Object val) {
		return false;
	}

	@Override
	public String exportThis(final Plottable theShape) {
		throw new NotImplementedException("We don't export these to REP format");
	}

	/**
	 * determine the identifier returning this type of annotation
	 */
	@Override
	public String getYourType() {
		return _myType;
	}

	@Override
	public Object readThisLine(final String theLine) throws ParseException {
		final StringTokenizer stringTokenizer = new StringTokenizer(theLine);

		try {
			stringTokenizer.nextToken();
			// combine the date, a space, and the time
			final String dateToken = stringTokenizer.nextToken();
			final String timeToken = stringTokenizer.nextToken();

			// and extract the date

			final HiResDate theDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);

			final String trackName = stringTokenizer.nextToken();
			
			return new TrackSplitOrder(theDate, trackName);
		} catch (Exception e) {
			// oh oh, they lied to us. We didn't receive a correct line :(
			throw new ParseException("Error parsing line " + theLine + "\n. Correct format: ;TRACKSPLIT DTG NAME", 0);
		}
	}

}
