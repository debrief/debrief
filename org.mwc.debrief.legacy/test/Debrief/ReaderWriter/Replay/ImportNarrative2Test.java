package Debrief.ReaderWriter.Replay;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.ReaderWriter.Replay.ImportNarrative2.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.ReaderWriter.Replay.ImportNarrative2.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.junit.jupiter.api.Test;

// ///////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class ImportNarrative2Test {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testImportQuotedLine() throws ParseException {
		final String theLine = ";NARRATIVE2:	020421	121857	\"HMS TORBAY\" 	GenComment2	Mk Rge BAAA R121212";
		final ImportNarrative2 in = new ImportNarrative2();
		final Object res = in.readThisLine(theLine);
		final NarrativeEntry ne = (NarrativeEntry) res;

		// check it contains the right data
		final String theDate = ne.getDTGString();
		assertEquals("020421 121857", theDate);
		assertEquals("HMS TORBAY", ne.getTrackName(), "found track name");
		assertEquals("GenComment2", ne.getType(), "type matches");

	}

	@Test
	public void testImportSingleLine() throws ParseException {
		final String theLine = ";NARRATIVE2:	020421	121857	HMS_TORBAY 	GenComment	Mk Rge BAAA R121212";
		final ImportNarrative2 in = new ImportNarrative2();
		final Object res = in.readThisLine(theLine);
		final NarrativeEntry ne = (NarrativeEntry) res;

		// check it contains the right data
		final String theDate = ne.getDTGString();
		assertEquals(theDate, "020421 121857");
		assertEquals("HMS_TORBAY", ne.getTrackName(), "found track name");
		assertEquals("GenComment", ne.getType(), "type matches");
	}

}
