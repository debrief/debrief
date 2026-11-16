package MWC.Utilities.ReaderWriter.XML.Util;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.Utilities.ReaderWriter.XML.Util.ShortLocationHandler.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import MWC.Utilities.ReaderWriter.XML.Util.ShortLocationHandler.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReaderWriter;

public class ShortLocationHandlerTest {
	private static List<WorldLocation> read(final String element, final MWCXMLReaderWriter rw) {
		DialogFactory.setRunHeadless(true);
		final List<WorldLocation> res = new ArrayList<WorldLocation>();
		final ShortLocationHandler handler = new ShortLocationHandler() {
			@Override
			public void setLocation(final WorldLocation loc) {
				res.add(loc);
			}
		};
		final String doc = "<?xml version=\"1.0\"?>\n" + element;
		rw.importThis("test.dpf", new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8)), handler);
		return res;
	}

	@Test
	public void testBadLatitudeSkipped() {
		final MWCXMLReaderWriter rw = new MWCXMLReaderWriter();
		final List<WorldLocation> locs = read("<shortLocation Lat=\"N50.5\" Long=\"-1.2\" Depth=\"0\"/>", rw);
		assertEquals(0, locs.size(), "no location at 0,0");
		assertEquals(1, rw.getImportProblems().size(), "problem reported");
	}

	@Test
	public void testGoodLocation() {
		final MWCXMLReaderWriter rw = new MWCXMLReaderWriter();
		final List<WorldLocation> locs = read("<shortLocation Lat=\"5.0E1\" Long=\"-1,5\" Depth=\"12\"/>", rw);
		assertEquals(1, locs.size());
		assertEquals(50, locs.get(0).getLat(), 0);
		assertEquals(-1.5, locs.get(0).getLong(), 0);
		assertEquals(12, locs.get(0).getDepth(), 0);
		assertTrue(rw.getImportProblems().isEmpty());
	}
}
