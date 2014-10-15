/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.plotViewer.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class RTFWriter {
	
	private BufferedWriter writer;	
	
	public RTFWriter(OutputStream outputStream) {
		try {
			this.writer= new BufferedWriter(new OutputStreamWriter(outputStream, "US-ASCII"));
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
		public void writeMacPictPicture(byte[] data, double width, double height) throws IOException {
		writer.write("{{\\pict");
		
		int integerWidth= (int) Math.ceil(width);
		int integerHeight= (int) Math.ceil(height);
		
		writer.write("\\picw"+ integerWidth);
		writer.write("\\pich"+ integerHeight);	
		
		int widthInTwips= (int) Math.ceil(20*width);
		int heightInTwips= (int) Math.ceil(20*height);
		writer.write("\\picwgoal"+ widthInTwips);
		writer.write("\\pichgoal"+ heightInTwips);
		
		writer.write("\\macpict");
		
		writer.newLine();
		for (int i=0; i < data.length; i++) {
			if (i % 64 == 0)
				writer.newLine();
			int v= (data[i] + 0x100) % 0x100; 
			String hex= Integer.toHexString(v);
			if (hex.length() == 1)
				hex= "0"+hex;
			writer.write(hex);
		}
		writer.write("}}");		
		writer.newLine();
	}
	
	public void writeHeader() throws IOException {
		StringBuffer header= new StringBuffer();
		header.append("{\\rtf1\\ansi");
		header.append("{\\fonttbl");
		
		header.append("}");
		writer.write(header.toString());
		// Non Unicode reader will ignore Unicode chars.
		//writer.write("\\uc0");
	}
	
	public void writeTail() throws IOException {
		writer.write("}");
		//writer.write(0); // For mac?
		writer.close();
	}
	
	
	
	public void setItalic(boolean italic) throws IOException {
		writer.write("\\i");
		if (! italic)
			writer.write("0");
		writer.write(" ");		
	}
	
	public void setBold(boolean bold) throws IOException {
		writer.write("\\b");
		if (! bold)
			writer.write("0");
		writer.write(" ");		
	}
	
	public void writeString(String text) throws IOException {
		for (int i=0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c < 128) {
				writer.write(c);
			} else {
				writer.write("\\u");
				writer.write(Integer.toString(c));
				// "equivalent character in ascii... well, we only do unicode, so we publish a "?" here:
				writer.write(" ?");

			}
		}
	}
	
	public void newParagraph() throws IOException {
		writer.write("\\par ");
	}
	
	public void startBlock() throws IOException {
		writer.write("{");
	}

	public void endBlock() throws IOException {
		writer.write("}");
	}
	
	public void newPage() throws IOException {
		writer.write("");
	}
	
	public void writeEmfPicture(byte[] data, double width, double height) throws IOException {
		double scale = 100.0 /  (72/25.4); // Number of 1/100mm in one inch.
		
		int scaledWidth= (int) Math.ceil(width* scale);
		int scaledHeight= (int) Math.ceil(height*scale);
		
		writer.write("{{\\pict");
		writer.write("\\emfblip");
		
		writer.write("\\picw"+ scaledWidth);
		writer.write("\\pich"+ scaledHeight);
	
		writer.write("\\picscalex100");
		writer.write("\\picscaley100");
	
		for (int i=0; i < data.length; i++) {
			if (i % 20 == 0)
				writer.newLine();
			int v= (data[i] + 0x100) % 0x100; 
			String hex= Integer.toHexString(v);
			if (hex.length() == 1)
				hex= "0"+hex;
			writer.write(hex);
		}
				
		writer.write("}}");		
	}

	public void writeWmfPicture(byte[] data, double width, double height) throws IOException {
		double scale = 100.0 /  (72/25.4); // Number of 1/100mm in one inch.
		
		int scaledWidth= (int) Math.ceil(width* scale);
		int scaledHeight= (int) Math.ceil(height*scale);
		
		writer.write("{\\*\\shppict{\\pict");
		writer.write("\\wmetafile8");
		
		writer.write("\\picw"+ scaledWidth);
		writer.write("\\pich"+ scaledHeight);
	
		writer.write("\\picscalex100");
		writer.write("\\picscaley100");
	
		for (int i=0; i < data.length; i++) {
			if (i % 64 == 0)
				writer.newLine();
			int v= (data[i] + 0x100) % 0x100; 
			String hex= Integer.toHexString(v);
			if (hex.length() == 1)
				hex= "0"+hex;
			writer.write(hex);
		}
		
		writer.write("}}");
		
	}
}