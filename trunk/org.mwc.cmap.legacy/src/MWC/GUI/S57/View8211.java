/* ****************************************************************************
 * $Id: View8211.java,v 1.3 2007/05/04 08:30:16 ian.mayo Exp $
 *
 * Project:  SDTS Translator
 * Purpose:  Example program dumping data in 8211 data to stdout.
 * Author:   Frank Warmerdam, warmerda@home.com
 *
 ******************************************************************************
 * Copyright (c) 1999, Frank Warmerdam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ******************************************************************************
 */

package MWC.GUI.S57;

import com.bbn.openmap.layer.vpf.MutableInt;
import com.bbn.openmap.util.Debug;

import java.io.*;
import java.util.Iterator;

/**
 * Class that uses the DDF* classes to read an 8211 file and print out the
 * contents.
 */
public class View8211
{

	protected boolean bFSPTHack = false;

	protected String pszFilename = null;

	public View8211(String filename, boolean fspt_repeating)
	{
		pszFilename = filename;
		bFSPTHack = fspt_repeating;

		view();
	}

	protected void view()
	{
		DDFModule oModule;

		try
		{

			oModule = new DDFModule(pszFilename);

			if (bFSPTHack)
			{
				DDFFieldDefinition poFSPT = oModule.findFieldDefn("FSPT");

				if (poFSPT == null)
					Debug.error("View8211: unable to find FSPT field to set repeating flag.");
				else
					poFSPT.setRepeating(true);
			}

			/* -------------------------------------------------------------------- */
			/* Loop reading records till there are none left. */
			/* -------------------------------------------------------------------- */
			DDFRecord poRecord;
			int iRecord = 1;

			while ((poRecord = oModule.readRecord()) != null)
			{
				doo("<h2>Record " + (iRecord++) + "(" + poRecord.getDataSize() + " bytes)</h2>");

				if (iRecord == 10000)
				{
					break;
				}

				/* ------------------------------------------------------------ */
				/* Loop over each field in this particular record. */
				/* ------------------------------------------------------------ */
				Iterator iter = poRecord.iterator();
				while (iter.hasNext())
				{
					viewRecordField((DDFField) iter.next());
				}
			}

		}
		catch (IOException ioe)
		{
			Debug.error(ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	/**
	 * Dump the contents of a field instance in a record.
	 */
	protected void viewRecordField(DDFField poField)
	{
		DDFFieldDefinition poFieldDefn = poField.getFieldDefn();

		if (poFieldDefn.getName().equals("FSPT"))
		{
			// System.out.println("found one!");
		}

		// Report general information about the field.
		doo("<h3>    Field " + poFieldDefn.getName() + ": " + poFieldDefn.getDescription()
				+ "</h3>");

		// Get pointer to this fields raw data. We will move through
		// it consuming data as we report subfield values.

		byte[] pachFieldData = poField.getData();
		int nBytesRemaining = poField.getDataSize();

		/* -------------------------------------------------------- */
		/* Loop over the repeat count for this fields */
		/* subfields. The repeat count will almost */
		/* always be one. */
		/* -------------------------------------------------------- */
		for (int iRepeat = 0; iRepeat < poField.getRepeatCount(); iRepeat++)
		{
			if (iRepeat > 0)
			{
				doo("<h4>Repeating (" + iRepeat + ")...</h4>");
			}
			/* -------------------------------------------------------- */
			/* Loop over all the subfields of this field, advancing */
			/* the data pointer as we consume data. */
			/* -------------------------------------------------------- */
			for (int iSF = 0; iSF < poFieldDefn.getSubfieldCount(); iSF++)
			{

				DDFSubfieldDefinition poSFDefn = poFieldDefn.getSubfieldDefn(iSF);
				int nBytesConsumed = viewSubfield(poSFDefn, pachFieldData, nBytesRemaining);
				nBytesRemaining -= nBytesConsumed;
				byte[] tempData = new byte[pachFieldData.length - nBytesConsumed];
				System.arraycopy(pachFieldData, nBytesConsumed, tempData, 0, tempData.length);
				pachFieldData = tempData;
			}
		}
	}

	private static Integer _pendingName = null;

	protected int viewSubfield(DDFSubfieldDefinition poSFDefn, byte[] pachFieldData,
			int nBytesRemaining)
	{

		MutableInt nBytesConsumed = new MutableInt();

		DDFDataType ddfdt = poSFDefn.getType();

		if (ddfdt == DDFDataType.DDFInt)
		{
			final int fieldVal = poSFDefn.extractIntData(pachFieldData, nBytesRemaining,
					nBytesConsumed);
			String string = "&nbsp;&nbsp;&nbsp;" + poSFDefn.getName() + " = " + fieldVal;
			if (poSFDefn.getName().equals("RCNM"))
			{
				_pendingName = new Integer(fieldVal);
			}
			if (poSFDefn.getName().equals("RCID"))
			{
				doo("<a name='" + _pendingName + "_" + fieldVal + "'>" + string + "</a>");
				_pendingName = null;
			}
			else
			{
				doo(string);
			}
		}
		else if (ddfdt == DDFDataType.DDFFloat)
		{
			doo("&nbsp;&nbsp;&nbsp;" + poSFDefn.getName() + " = "
					+ poSFDefn.extractFloatData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFString)
		{
			doo("&nbsp;&nbsp;&nbsp;" + poSFDefn.getName() + " = "
					+ poSFDefn.extractStringData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFBinaryString)
		{
			if (poSFDefn.getName().equals("NAME"))
			{
				String name = poSFDefn.extractStringData(pachFieldData, nBytesRemaining,
						nBytesConsumed);
				int rcnm = name.charAt(0);
				if (rcnm == 8218)
					rcnm = 130;
				int rcid = name.charAt(1) + name.charAt(2) * 255 + name.charAt(3) * 255 * 255
						+ name.charAt(4) * 255 * 255 * 255;
				doo("&nbsp;&nbsp;&nbsp;" + poSFDefn.getName() + " = " + " rcnm:" + rcnm
						+ " rcid:" + rcid + "<a href='#" + rcnm + "_" + rcid + "'>here</a>");
			}
			else
			{
				doo("&nbsp;&nbsp;&nbsp;" + poSFDefn.getName() + " = "
						+ poSFDefn.extractStringData(pachFieldData, nBytesRemaining, nBytesConsumed));
			}
		}

		return nBytesConsumed.value;
	}

	public static void main(String[] argv)
	{

		if (argv.length == 0)
		{
			argv = new String[] { "e:\\dev\\s57\\AU411141.000" };
		}
		Debug.init();

		String pszFilename = null;
		boolean bFSPTHack = false;

		for (int iArg = 0; iArg < argv.length; iArg++)
		{
			if (argv[iArg].equals("-fspt_repeating"))
			{
				bFSPTHack = true;
			}
			else
			{
				pszFilename = argv[iArg];
			}
		}

		if (pszFilename == null)
		{
			doo("Usage: View8211 filename\n");
			System.exit(1);
		}

		new View8211(pszFilename, bFSPTHack);

	}

	private static FileWriter _so = null;

	private static void doo(String val)
	{

		try
		{
			if (_so == null)
			{
				_so = new FileWriter("e:\\dev\\s57\\s57_listing.html");
				_so.write("						<head>\n");
				_so.write("<style  type='text/css' media='screen'>\n");
				_so.write("h2,h3,h4 {\n");
				_so.write("font: bold 1.5em arial, verdana, sans-serif;\n");
				_so.write("padding: .1em;\n");
				_so.write("border: 2px solid #000000;\n");
				_so.write("color: #CC0000;\n");
				_so.write("background-color: #F5F5F5;\n");
				_so.write("margin: 0 0 2px 0; /* top right bottom left */\n");
				_so.write("}\n");
				_so.write("h3\n");
				_so.write("{\n");
				_so.write("font-size:1em;\n");
				_so.write("color:blue;\n");
				_so.write("}\n");
				_so.write("h4\n");
				_so.write("{\n");
				_so.write("font-size:0.7em;\n");
				_so.write("color:brown;\n");
				_so.write("}\n");
				_so.write("body{font: normal 1em arial, verdana, sans-serif;}\n");
				_so.write("</style>\n");
				_so.write("</head>\n");
				_so.write("<body>\n");
				_so.write("}\n");
			}
			_so.write(val + "<br>");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
