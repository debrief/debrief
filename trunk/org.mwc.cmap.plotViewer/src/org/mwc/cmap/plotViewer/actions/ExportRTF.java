/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.plotViewer.PlotViewerPlugin;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;

import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Canvas.MetafileCanvas;
import MWC.GUI.Tools.Chart.WriteMetafile;

/**
 * @author ian.mayo
 */
public class ExportRTF extends CoreEditorAction
{
	public static ToolParent _theParent = null;

	/**
	 * whether to put the image on the clipboard
	 */
	private boolean _writeToClipboard = false;

	/**
	 * whether to put the image into the working export directory
	 */
	private boolean _writeToFile = false;

	/**
	 * @param toClipboard
	 * @param toFile
	 */
	public ExportRTF()
	{
		this(false, true);
	}
	
	/**
	 * @param toClipboard
	 * @param toFile
	 */
	public ExportRTF(final boolean toClipboard, final boolean toFile)
	{
		super();
		_writeToClipboard = toClipboard;
		_writeToFile = toFile;
	}

	/**
	 * ok, store who the parent is for the operation
	 * 
	 * @param theParent
	 */
	public static void init(final ToolParent theParent)
	{
		_theParent = theParent;
	}

	/**
	 * and execute..
	 */
	protected void execute()
	{
		final PlainChart theChart = getChart();

		if (_theParent == null)
		{
			CorePlugin.logError(Status.ERROR, "Tool parent missing for Write Metafile", null);
			return;
		}

		final WriteMetafile write = new WriteMetafile(_theParent, theChart, _writeToFile)
		{

			/**
			 * @param mf
			 */
			protected void paintToMetafile(final MetafileCanvas mf)
			{
				final SWTCanvas sc = (SWTCanvas) theChart.getCanvas();
				sc.paintPlot(mf);
			}

		};
		write.execute();

		// ok, do we want to write it to the clipboard?
		if (_writeToClipboard)
		{
			// try to get the filename
			final String fName = MetafileCanvas.getLastFileName();

			if (fName != null)
			{
				// create the clipboard

				// try to copy the wmf to the clipboard
				ByteArrayOutputStream os = null;
				DataInputStream dis = null;
				try
				{
					// get the dimensions
					final Dimension dim = MetafileCanvas.getLastScreenSize();

					os = new ByteArrayOutputStream();
					RTFWriter writer = new RTFWriter(os);
					File file = new File(fName);
			    byte[] data = new byte[(int) file.length()];
			    dis = new DataInputStream(new FileInputStream(file));
			    dis.readFully(data);
			    writer.writeHeader();
			    writer.writeEmfPicture(data, dim.getWidth(), dim.getHeight());
			    writer.writeTail();
					
			    RTFTransfer rtfTransfer = RTFTransfer.getInstance();
			    Clipboard clipboard = new Clipboard(Display.getDefault());
			    Object[] rtfData = new Object[] { os.toString() };
			    clipboard.setContents(rtfData, new Transfer[] {rtfTransfer});
				}
				catch (final Exception e)
				{
					IStatus status = new Status(IStatus.ERROR, PlotViewerPlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
					PlotViewerPlugin.getDefault().getLog().log(status);
				}
				finally {
					if (os != null) {
						try
						{
							os.close();
						} catch (IOException e)
						{
							// ignore
						}
					}
					if (dis != null) {
						try
						{
							dis.close();
						} catch (IOException e)
						{
							// ignore
						}
					}
				}
			}
			else {
				IStatus status = new Status(IStatus.ERROR, PlotViewerPlugin.PLUGIN_ID, "Target filename missing");
				PlotViewerPlugin.getDefault().getLog().log(status);
			}
		}
	}
	
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


}