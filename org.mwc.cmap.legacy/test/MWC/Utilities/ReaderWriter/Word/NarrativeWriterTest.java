/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */

package MWC.Utilities.ReaderWriter.Word;

import static MWC.Utilities.ReaderWriter.Word.NarrativeWriter.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.Utilities.ReaderWriter.Word.NarrativeWriter.*;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Test;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;

public class NarrativeWriterTest {
	@Test
	public void testWritingNarratives() throws Exception {
		final NarrativeWrapper narr = new NarrativeWrapper("Some title");
		assertEquals(0, narr.size(), "empty");
		HiResDate date1 = new HiResDate(3000);
		final NarrativeEntry n1 = new NarrativeEntry("track", date1, "some entry");
		narr.add(n1);

		assertEquals(1, narr.size(), "has one");

		final NarrativeEntry n2 = new NarrativeEntry("track", new HiResDate(3100), "some entry");
		narr.add(n2);

		assertEquals(2, narr.size(), "has two");
		
		n2.setSource("src");
		n2.setType("type");
		NarrativeWriter nw = new NarrativeWriter();
		String currentDirectory = System.getProperty("user.dir");
		nw.write(narr, true,true, new File(currentDirectory));
		String fileName = n1.getDTGString()+".docx";
		File outFile =new File(fileName);
		assertTrue(outFile.exists());
		XWPFDocument document = null;
		FileInputStream fileInputStream = null;
		try {
			File fileToBeRead = new File(fileName);
			fileInputStream = new FileInputStream(fileToBeRead);
			document = new XWPFDocument(fileInputStream);
			List<XWPFTable> tables = document.getTables();
			assertEquals(1,tables.size(),"one table");
			assertEquals(3,tables.get(0).getNumberOfRows(),"row count");
			XWPFTableRow header = tables.get(0).getRow(0);
			assertEquals(4,header.getTableCells().size(),"header cells");
			assertEquals("Time",header.getCell(0).getText());
			assertEquals("Source",header.getCell(1).getText());
			assertEquals("Type",header.getCell(2).getText());
			assertEquals("Entry",header.getCell(3).getText());
			XWPFTableRow row1 = tables.get(0).getRow(1);
			assertEquals(n1.getDTGString(),row1.getCell(0).getText());
			assertEquals(n1.getSource(),row1.getCell(1).getText());
			XWPFTableRow row2 = tables.get(0).getRow(2);
			assertEquals(n2.getDTGString(),row2.getCell(0).getText());
			assertEquals(n2.getSource(),row2.getCell(1).getText());
			assertEquals(n2.getType(),row2.getCell(2).getText());
			assertEquals(n2.getEntry(),row2.getCell(3).getText());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error reading the word doc");
		} finally {
			try {
				if (document != null) {
					document.close();
				}
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (Exception ex) {
				//ignore
				System.err.println("Error closing file");
			}
		}
		outFile.delete();
		
		
	}

	@Test
	public void testWritingEmptyList() throws IOException {
		final NarrativeWrapper narr = new NarrativeWrapper("Some title");
		assertEquals(0, narr.size(), "empty");
		final File dir = java.nio.file.Files.createTempDirectory("narrative").toFile();
		try {
			new NarrativeWriter().write(narr, true, true, dir);
			assertEquals(0, dir.list().length, "no document written");
		} finally {
			dir.delete();
		}
	}

	@Test
	public void testWritingNoType() {
		final NarrativeWrapper narr = new NarrativeWrapper("Some title");
		assertEquals(0, narr.size(), "empty");
		HiResDate date1 = new HiResDate(3000);
		final NarrativeEntry n1 = new NarrativeEntry("track", date1, "some entry");
		n1.setSource("src");
		n1.setType("Type");

		narr.add(n1);

		assertEquals(1, narr.size(), "has one");

		final NarrativeEntry n2 = new NarrativeEntry("track", new HiResDate(3100), "some entry");
		narr.add(n2);

		assertEquals(2, narr.size(), "has two");
		
		n2.setSource("src");
		n2.setType("Type");
		NarrativeWriter nw = new NarrativeWriter();
		String currentDirectory = System.getProperty("user.dir");
		nw.write(narr, true,false, new File(currentDirectory));
		String fileName = n1.getDTGString()+".docx";
		File outFile =new File(fileName);
		assertTrue(outFile.exists());
		XWPFDocument document = null;
		FileInputStream fileInputStream = null;
		try {
			File fileToBeRead = new File(fileName);
			System.out.println(fileToBeRead.getAbsolutePath());
			fileInputStream = new FileInputStream(fileToBeRead);
			document = new XWPFDocument(fileInputStream);
			List<XWPFTable> tables = document.getTables();
			assertEquals(3,tables.get(0).getNumberOfRows(),"row count");
			XWPFTableRow row1 = tables.get(0).getRow(1);
			System.out.println("n1dtgstring:"+n1.getDTGString());
			assertEquals(n1.getDTGString(),row1.getCell(0).getText());
			assertEquals(n1.getSource(),row1.getCell(1).getText());
			XWPFTableRow row2 = tables.get(0).getRow(2);
			assertEquals(n2.getDTGString(),row2.getCell(0).getText());
			assertEquals(n2.getSource(),row2.getCell(1).getText());
			assertEquals(n2.getEntry(),row2.getCell(2).getText());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error reading the word doc");
		} finally {
			try {
				if (document != null) {
					document.close();
				}
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (Exception ex) {
				System.err.println("error closing file");
			}
		}
		outFile.delete();
		
		
	}


	@Test
	public void testNarrativeNoSource() {
		final NarrativeWrapper narr = new NarrativeWrapper("Some title");
		assertEquals(0, narr.size(), "empty");
		HiResDate date1 = new HiResDate(3000);
		final NarrativeEntry n1 = new NarrativeEntry("track", date1, "some entry");
		n1.setSource("src");
		n1.setType("Type");

		narr.add(n1);

		assertEquals(1, narr.size(), "has one");

		final NarrativeEntry n2 = new NarrativeEntry("track", new HiResDate(3100), "some entry");
		narr.add(n2);

		assertEquals(2, narr.size(), "has two");
		
		n2.setSource("src");
		n2.setType("Type");
		NarrativeWriter nw = new NarrativeWriter();
		String currentDirectory = System.getProperty("user.dir");
		nw.write(narr, false,true, new File(currentDirectory));
		String fileName = n1.getDTGString()+".docx";
		File outFile =new File(fileName);
		assertTrue(outFile.exists());
		XWPFDocument document = null;
		FileInputStream fileInputStream = null;
		try {
			File fileToBeRead = new File(fileName);
			System.out.println(fileToBeRead.getAbsolutePath());
			fileInputStream = new FileInputStream(fileToBeRead);
			document = new XWPFDocument(fileInputStream);
			List<XWPFTable> tables = document.getTables();
			assertEquals(3,tables.get(0).getNumberOfRows(),"row count");
			XWPFTableRow row1 = tables.get(0).getRow(1);
			System.out.println("n1dtgstring:"+n1.getDTGString());
			assertEquals(n1.getDTGString(),row1.getCell(0).getText());
			assertEquals(n1.getType(),row1.getCell(1).getText());
			XWPFTableRow row2 = tables.get(0).getRow(2);
			assertEquals(n2.getDTGString(),row2.getCell(0).getText());
			assertEquals(n2.getType(),row2.getCell(1).getText());
			assertEquals(n2.getEntry(),row2.getCell(2).getText());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error reading the word doc");
		} finally {
			try {
				if (document != null) {
					document.close();
				}
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (Exception ex) {
				System.err.println("error closing file");
			}
		}
		outFile.delete();
	}

	@Test
	public void testNarrativeExtraFields() {
		final NarrativeWrapper narr = new NarrativeWrapper("Some title");
		assertEquals(0, narr.size(), "empty");
		HiResDate date1 = new HiResDate(3000);
		final NarrativeEntry n1 = new NarrativeEntry("track", date1, "some entry");
		n1.setColor(Color.BLUE);
		n1.setSource("Src");
		n1.setType("Type");
		narr.add(n1);

		assertEquals(1, narr.size(), "has one");

		final NarrativeEntry n2 = new NarrativeEntry("track", new HiResDate(3100), "some entry");
		narr.add(n2);

		assertEquals(2, narr.size(), "has two");
		
		n2.setSource("src");
		n2.setType("type");
		NarrativeWriter nw = new NarrativeWriter();
		String currentDirectory = System.getProperty("user.dir");
		nw.write(narr, true,true, new File(currentDirectory));
		String fileName = n1.getDTGString()+".docx";
		File outFile =new File(fileName);
		assertTrue(outFile.exists());
		XWPFDocument document = null;
		FileInputStream fileInputStream = null;
		try {
			File fileToBeRead = new File(fileName);
			System.out.println(fileToBeRead.getAbsolutePath());
			fileInputStream = new FileInputStream(fileToBeRead);
			document = new XWPFDocument(fileInputStream);
			List<XWPFTable> tables = document.getTables();
			assertEquals(3,tables.get(0).getNumberOfRows(),"row count");
			XWPFTableRow row1 = tables.get(0).getRow(1);
			System.out.println("n1dtgstring:"+n1.getDTGString());
			assertEquals(n1.getDTGString(),row1.getCell(0).getText());
			assertEquals(n1.getSource(),row1.getCell(1).getText());
			XWPFTableRow row2 = tables.get(0).getRow(2);
			assertEquals(n2.getDTGString(),row2.getCell(0).getText());
			assertEquals(n2.getSource(),row2.getCell(1).getText());
			assertEquals(n2.getType(),row2.getCell(2).getText());
			assertEquals(n2.getEntry(),row2.getCell(3).getText());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error reading the word doc");
		} finally {
			try {
				if (document != null) {
					document.close();
				}
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (Exception ex) {
				System.err.println("error closing file");
			}
		}
		outFile.delete();
	}
}
