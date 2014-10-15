/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
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
public class Output8211
{

	private static FileWriter os;

	protected boolean bFSPTHack = false;

	protected String pszFilename = null;

	public Output8211(final String filename, final boolean fspt_repeating)
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
				dOut("Record " + (iRecord++) + "(" + poRecord.getDataSize() + " bytes)");

				/* ------------------------------------------------------------ */
				/* Loop over each field in this particular record. */
				/* ------------------------------------------------------------ */
				for (final Iterator<DDFField> it = poRecord.iterator(); it != null && it.hasNext();
				// dOut(((DDFField)it.next()).toString()));
				viewRecordField(((DDFField) it.next())))
				{
					
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

		// Report general information about the field.
		dOut("    Field " + poFieldDefn.getName() + ": "
				+ poFieldDefn.getDescription());

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
				dOut("Repeating (" + iRepeat + ")...");
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

	protected int viewSubfield(final DDFSubfieldDefinition poSFDefn, final byte[] pachFieldData,
			final int nBytesRemaining)
	{

		final MutableInt nBytesConsumed = new MutableInt();

		final DDFDataType ddfdt = poSFDefn.getType();

		if (ddfdt == DDFDataType.DDFInt)
		{
			dOut("        " + poSFDefn.getName() + " = "
					+ poSFDefn.extractIntData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFFloat)
		{
			dOut("        " + poSFDefn.getName() + " = "
					+ poSFDefn.extractFloatData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFString)
		{
			dOut("        " + poSFDefn.getName() + " = "
					+ poSFDefn.extractStringData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFBinaryString)
		{
			poSFDefn.extractStringData(pachFieldData, nBytesRemaining, nBytesConsumed); // pabyBString
      dOut("        " + poSFDefn.getName() + " = " + poSFDefn.extractStringData(pachFieldData,
          nBytesRemaining,
          nBytesConsumed));
		}

		return nBytesConsumed.value;
	}

	public static void main(final String[] argv)
	{
		String[] args = argv.clone();
		if (args.length == 0)
		{
			args = new String[] { "d://dev/AU411141.000" };
		}

		try
		{
			os = new FileWriter("d:\\\\dev\\s57_listing2.txt");
		}
		catch (final IOException e)
		{
			e.printStackTrace();
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
			dOut("Usage: View8211 filename\n");
			System.exit(1);
		}

		new Output8211(pszFilename, bFSPTHack);

	}
	
	private static void dOut(final String txt)
	{
		try
		{
			os.write(txt + "\r\n");
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

}
