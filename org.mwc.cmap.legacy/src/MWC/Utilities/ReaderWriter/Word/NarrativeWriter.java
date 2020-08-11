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

import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import junit.framework.TestCase;

/**
 * @author Ayesha
 *
 */
public class NarrativeWriter {
	
	public void write(NarrativeWrapper narratives, boolean showSource, boolean showType, File targetDirectory) {
		NarrativeEntry currentEntry = null;
		Enumeration<Editable> editableItems = narratives.elements();
		if(editableItems.hasMoreElements()) {
			currentEntry = (NarrativeEntry)editableItems.nextElement();
		}
		if(currentEntry!=null) {
			final String fileName = targetDirectory+File.separator+currentEntry.getDTGString()+".docx";
			FileOutputStream outStream = null;
			XWPFDocument document = null;
			try {
				File file = new File(fileName);
				System.out.println("File exists:"+file.exists());
				document = new XWPFDocument();
				int rowCount = 0;
				int colCount=4;
				final XWPFTable narrativesTable = document.createTable(rowCount+1,colCount);
				final XWPFTableRow row = narrativesTable.getRow(rowCount);
				row.getCell(0).setText("DTG ");
				row.getCell(1).setText("Entry");
				row.getCell(2).setText("Track");
				row.getCell(3).setText("Type");
				
				NarrativeEntry nextEntry = currentEntry;
				
				document.insertTable(1, narrativesTable);
				
				while(nextEntry!=null) {
					final XWPFTableRow dataRow = narrativesTable.createRow();
					rowCount++;
					System.out.println("Rowcount:"+rowCount);
					currentEntry = nextEntry;
					XWPFTableCell cell1 = dataRow.getCell(0);
					String dtgString = currentEntry.getDTGString();
					cell1.setText(dtgString);
					System.out.println("DTGString:"+dtgString);
					XWPFTableCell cell2 = dataRow.getCell(1);
					cell2.setText(currentEntry.getEntry());
					
					if(!isNullOrBlank(currentEntry.getSource())) {
						XWPFTableCell sourceCell = dataRow.getCell(2);
						sourceCell.setText(currentEntry.getSource());
					}
					if(!isNullOrBlank(currentEntry.getType())) {
						XWPFTableCell typeCell = dataRow.getCell(3);
						typeCell.setText(currentEntry.getType());
					}
					nextEntry = editableItems.hasMoreElements()?(NarrativeEntry)editableItems.nextElement():null;
					//narrativesTable.addRow(dataRow);
				}
				
				outStream = new FileOutputStream(fileName);
				document.write(outStream);
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if(document!=null) {
						document.close();
					}
					if(outStream!=null) {
						outStream.close();
					}
				} catch (IOException e) {
					//ignore
				}
			}
		}
		else {
			//log empty file
		}
	}
	private boolean isNullOrBlank(String text) {
		if(text==null || text.isBlank()) {
			return true;
		}
		return false;
	}
	public static class TestMe extends TestCase {
		public void testWritingNarratives() throws Exception{
			final NarrativeWrapper narr = new NarrativeWrapper("Some title");
			assertEquals("empty", 0, narr.size());
			HiResDate date1 = new HiResDate(3000);
			final NarrativeEntry n1 = new NarrativeEntry("track", date1, "some entry");
			narr.add(n1);

			assertEquals("has one", 1, narr.size());

			final NarrativeEntry n2 = new NarrativeEntry("track", new HiResDate(3100), "some entry");
			narr.add(n2);

			assertEquals("has two", 2, narr.size());
			
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
				assertEquals("row count",3,tables.get(0).getNumberOfRows());
				XWPFTableRow row1 = tables.get(0).getRow(1);
				System.out.println("n1dtgstring:"+n1.getDTGString());
				assertEquals(n1.getDTGString(),row1.getCell(0).getText());
				assertEquals(n1.getEntry(),row1.getCell(1).getText());
				XWPFTableRow row2 = tables.get(0).getRow(2);
				assertEquals(n2.getDTGString(),row2.getCell(0).getText());
				assertEquals(n2.getEntry(),row2.getCell(1).getText());
				assertEquals(n2.getSource(),row2.getCell(2).getText());
				assertEquals(n2.getType(),row2.getCell(3).getText());
				
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
				}
			}
			outFile.delete();
			
			
		}
		public void testWritingEmptyList() {
			final NarrativeWrapper narr = new NarrativeWrapper("Some title");
			assertEquals("empty", 0, narr.size());
			NarrativeWriter nw = new NarrativeWriter();
			String currentDirectory = System.getProperty("user.dir");
			nw.write(narr, true,true, new File(currentDirectory));
			
			
			
		}
		public void testWritingBlankType() {
			final NarrativeWrapper narr = new NarrativeWrapper("Some title");
			assertEquals("empty", 0, narr.size());
			HiResDate date1 = new HiResDate(3000);
			final NarrativeEntry n1 = new NarrativeEntry("track", date1, "some entry");
			narr.add(n1);

			assertEquals("has one", 1, narr.size());

			final NarrativeEntry n2 = new NarrativeEntry("track", new HiResDate(3100), "some entry");
			narr.add(n2);

			assertEquals("has two", 2, narr.size());
			
			n2.setSource("src");
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
				assertEquals("row count",3,tables.get(0).getNumberOfRows());
				XWPFTableRow row1 = tables.get(0).getRow(1);
				System.out.println("n1dtgstring:"+n1.getDTGString());
				assertEquals(n1.getDTGString(),row1.getCell(0).getText());
				assertEquals(n1.getEntry(),row1.getCell(1).getText());
				XWPFTableRow row2 = tables.get(0).getRow(2);
				assertEquals(n2.getDTGString(),row2.getCell(0).getText());
				assertEquals(n2.getEntry(),row2.getCell(1).getText());
				assertEquals(n2.getSource(),row2.getCell(2).getText());
				
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
				}
			}
			outFile.delete();
			
			
		}
		
		
		public void testNarrativeBlankEntry() {
			final NarrativeWrapper narr = new NarrativeWrapper("Some title");
			assertEquals("empty", 0, narr.size());
			HiResDate date1 = new HiResDate(3000);
			final NarrativeEntry n1 = new NarrativeEntry("track", date1, "");
			narr.add(n1);

			assertEquals("has one", 1, narr.size());

			final NarrativeEntry n2 = new NarrativeEntry("track", new HiResDate(3100), "");
			narr.add(n2);

			assertEquals("has two", 2, narr.size());
			
			n2.setSource("src");
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
				assertEquals("row count",3,tables.get(0).getNumberOfRows());
				XWPFTableRow row1 = tables.get(0).getRow(1);
				System.out.println("n1dtgstring:"+n1.getDTGString());
				assertEquals(n1.getDTGString(),row1.getCell(0).getText());
				assertEquals(n1.getEntry(),row1.getCell(1).getText());
				XWPFTableRow row2 = tables.get(0).getRow(2);
				assertEquals(n2.getDTGString(),row2.getCell(0).getText());
				assertEquals(n2.getEntry(),row2.getCell(1).getText());
				assertEquals(n2.getSource(),row2.getCell(2).getText());
				
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
				}
			}
			outFile.delete();
			
		}
		public void testNarrativeBlankSource() {
			final NarrativeWrapper narr = new NarrativeWrapper("Some title");
			assertEquals("empty", 0, narr.size());
			HiResDate date1 = new HiResDate(3000);
			final NarrativeEntry n1 = new NarrativeEntry("track", date1, "some entry");
			narr.add(n1);

			assertEquals("has one", 1, narr.size());

			final NarrativeEntry n2 = new NarrativeEntry("track", new HiResDate(3100), "some entry");
			narr.add(n2);

			assertEquals("has two", 2, narr.size());
			
			n2.setSource("");
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
				assertEquals("row count",3,tables.get(0).getNumberOfRows());
				XWPFTableRow row1 = tables.get(0).getRow(1);
				System.out.println("n1dtgstring:"+n1.getDTGString());
				assertEquals(n1.getDTGString(),row1.getCell(0).getText());
				assertEquals(n1.getEntry(),row1.getCell(1).getText());
				XWPFTableRow row2 = tables.get(0).getRow(2);
				assertEquals(n2.getDTGString(),row2.getCell(0).getText());
				assertEquals(n2.getEntry(),row2.getCell(1).getText());
				assertEquals(n2.getSource(),row2.getCell(2).getText());
				assertEquals(n2.getType(),row2.getCell(3).getText());
				
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
				}
			}
			outFile.delete();
		}
		public void testNarrativeExtraFields() {
			final NarrativeWrapper narr = new NarrativeWrapper("Some title");
			assertEquals("empty", 0, narr.size());
			HiResDate date1 = new HiResDate(3000);
			final NarrativeEntry n1 = new NarrativeEntry("track", date1, "some entry");
			n1.setColor(Color.BLUE);
			n1.setSource("Src");
			n1.setType("Type");
			narr.add(n1);

			assertEquals("has one", 1, narr.size());

			final NarrativeEntry n2 = new NarrativeEntry("track", new HiResDate(3100), "some entry");
			narr.add(n2);

			assertEquals("has two", 2, narr.size());
			
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
				assertEquals("row count",3,tables.get(0).getNumberOfRows());
				XWPFTableRow row1 = tables.get(0).getRow(1);
				System.out.println("n1dtgstring:"+n1.getDTGString());
				assertEquals(n1.getDTGString(),row1.getCell(0).getText());
				assertEquals(n1.getEntry(),row1.getCell(1).getText());
				XWPFTableRow row2 = tables.get(0).getRow(2);
				assertEquals(n2.getDTGString(),row2.getCell(0).getText());
				assertEquals(n2.getEntry(),row2.getCell(1).getText());
				assertEquals(n2.getSource(),row2.getCell(2).getText());
				assertEquals(n2.getType(),row2.getCell(3).getText());
				
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
				}
			}
			outFile.delete();
		}
	}
	
}
