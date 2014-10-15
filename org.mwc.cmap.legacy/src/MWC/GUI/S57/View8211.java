/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package MWC.GUI.S57;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import com.bbn.openmap.layer.vpf.MutableInt;
import com.bbn.openmap.util.Debug;

/**
 * Class that uses the DDF* classes to read an 8211 file and print out the
 * contents.
 */
public class View8211
{

	protected boolean bFSPTHack = false;

	protected String pszFilename = null;

	public View8211(final String filename, final boolean fspt_repeating)
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
				final DDFFieldDefinition poFSPT = oModule.findFieldDefn("FSPT");

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
				final Iterator<DDFField> iter = poRecord.iterator();
				while (iter.hasNext())
				{
					viewRecordField((DDFField) iter.next());
				}
			}

		}
		catch (final IOException ioe)
		{
			Debug.error(ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	/**
	 * Dump the contents of a field instance in a record.
	 */
	protected void viewRecordField(final DDFField poField)
	{
		final DDFFieldDefinition poFieldDefn = poField.getFieldDefn();

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

				final DDFSubfieldDefinition poSFDefn = poFieldDefn.getSubfieldDefn(iSF);
				final int nBytesConsumed = viewSubfield(poSFDefn, pachFieldData, nBytesRemaining);
				nBytesRemaining -= nBytesConsumed;
				final byte[] tempData = new byte[pachFieldData.length - nBytesConsumed];
				System.arraycopy(pachFieldData, nBytesConsumed, tempData, 0, tempData.length);
				pachFieldData = tempData;
			}
		}
	}

	private static Integer _pendingName = null;

	protected int viewSubfield(final DDFSubfieldDefinition poSFDefn, final byte[] pachFieldData,
			final int nBytesRemaining)
	{

		final MutableInt nBytesConsumed = new MutableInt();

		final DDFDataType ddfdt = poSFDefn.getType();

		if (ddfdt == DDFDataType.DDFInt)
		{
			final int fieldVal = poSFDefn.extractIntData(pachFieldData, nBytesRemaining,
					nBytesConsumed);
			final String string = "&nbsp;&nbsp;&nbsp;" + poSFDefn.getName() + " = " + fieldVal;
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
				final String name = poSFDefn.extractStringData(pachFieldData, nBytesRemaining,
						nBytesConsumed);
				int rcnm = name.charAt(0);
				if (rcnm == 8218)
					rcnm = 130;
				final int rcid = name.charAt(1) + name.charAt(2) * 255 + name.charAt(3) * 255 * 255
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

	public static void main(final String[] argv)
	{
		String[] args = argv.clone();

		if (args.length == 0)
		{
			args = new String[] { "e:\\dev\\s57\\AU411141.000" };
		}
		Debug.init();

		String pszFilename = null;
		boolean bFSPTHack = false;

		for (int iArg = 0; iArg < args.length; iArg++)
		{
			if (args[iArg].equals("-fspt_repeating"))
			{
				bFSPTHack = true;
			}
			else
			{
				pszFilename = args[iArg];
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

	private static void doo(final String val)
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
		catch (final IOException e)
		{
			e.printStackTrace();
		}

	}

}
