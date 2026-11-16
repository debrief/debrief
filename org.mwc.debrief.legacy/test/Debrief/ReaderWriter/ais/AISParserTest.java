package Debrief.ReaderWriter.ais;

import static Debrief.ReaderWriter.ais.AISParser.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.ais.AISParser.*;
import java.util.Optional;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class AISParserTest {
	private static String withCRC(final String body) {
		return "!" + body + "*" + calcCRC(body);
	}

	@Test
	public void testOrphanFragmentDiscarded() {
		final AISParser parser = new AISParser();
		// a second fragment, with no first fragment (e.g. log starts mid-message)
		final String orphan = withCRC("AIVDM,2,2,3,B,88888888880,2");
		assertTrue(isValidAIS(orphan), "fragment is otherwise valid");
		final Optional<IAISMessage> res = parser.parse(orphan, 1);
		assertNotNull(res, "never a raw null");
		assertFalse(res.isPresent());

		// and the parser recovers for the next, normal, message
		final Optional<IAISMessage> next = parser.parse("!AIVDM,1,1,,B,19NS7Sp02wo?HETKA2K6mUM20<L=,0*27", 2);
		assertNotNull(next);
	}
}
