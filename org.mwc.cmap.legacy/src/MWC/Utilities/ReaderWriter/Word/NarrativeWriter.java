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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import MWC.GUI.Editable;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;

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
				document = new XWPFDocument();
				int rows = 0;
				int cols=2;
				if(showSource) {
					cols++;
				}
				if(showType) {
					cols++;
				}
				final XWPFTable narrativesTable = document.createTable(rows+1,cols);
				final XWPFTableRow row = narrativesTable.getRow(rows);
				int colCount=0;
				row.getCell(colCount++).setText("Time");
				if(showSource) {
					row.getCell(colCount++).setText("Source");
				}
				if(showType) {
					row.getCell(colCount++).setText("Type");
				}
				row.getCell(colCount).setText("Entry");
				NarrativeEntry nextEntry = currentEntry;
				
				document.insertTable(1, narrativesTable);
				
				while(nextEntry!=null) {
					int cellCount=0;
					final XWPFTableRow dataRow = narrativesTable.createRow();
					currentEntry = nextEntry;
					XWPFTableCell cell1 = dataRow.getCell(cellCount++);
					String dtgString = currentEntry.getDTGString();
					cell1.setText(dtgString);
					if(showSource) {
						XWPFTableCell sourceCell = dataRow.getCell(cellCount++);
						sourceCell.setText(currentEntry.getSource());
					}
					if(showType) {
						XWPFTableCell typeCell;
						typeCell = dataRow.getCell(cellCount++);
						typeCell.setText(currentEntry.getType());	
					}
					XWPFTableCell cell2 = dataRow.getCell(cellCount++);
					cell2.setText(currentEntry.getEntry());
					nextEntry = editableItems.hasMoreElements()?(NarrativeEntry)editableItems.nextElement():null;
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
					System.err.println("error closing file");
				}
			}
		}
		else {
			//log empty file
		}
	}
	
}
