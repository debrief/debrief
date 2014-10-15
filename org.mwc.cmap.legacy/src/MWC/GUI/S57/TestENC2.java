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
import java.util.Vector;

import MWC.GenericData.WorldLocation;

import com.bbn.openmap.layer.vpf.MutableInt;
import com.bbn.openmap.util.Debug;

/**
 * Class that uses the DDF* classes to read an 8211 file and print out the
 * contents.
 */
public class TestENC2
{

	protected boolean bFSPTHack = false;

	protected String pszFilename = null;

	public TestENC2(final String filename, final boolean fspt_repeating)
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
				iRecord++;

				// if (iRecord > 100)
				// break;
				doo("=== Record " + iRecord);

				/* ------------------------------------------------------------ */
				/* Loop over each field in this particular record. */
				/* ------------------------------------------------------------ */
				final Iterator<DDFField> it = poRecord.iterator();
				while (it.hasNext())
				{
					final DDFField field = (DDFField) it.next();
					viewRecordField(field);
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
	 * create a series of 3d positions
	 * 
	 * @param poField
	 * @param poFieldDefn
	 */
	private void extractPositionField(final DDFField poField, final DDFFieldDefinition poFieldDefn)
	{
		final Vector<WorldLocation> thePositions = new Vector<WorldLocation>(0, 1);

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
			final WorldLocation myVal = new WorldLocation(0, 0, 0);

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

			thePositions.add(myVal);
		}

		System.out.println("|| 3d field: found " + thePositions.size());
	}

	/**
	 * Dump the contents of a field instance in a record.
	 */
	protected void viewRecordField(final DDFField poField)
	{
		final DDFFieldDefinition poFieldDefn = poField.getFieldDefn();

		// Report general information about the field.
		doo(" Field " + poFieldDefn.getName() + ": "
				+ poFieldDefn.getDescription());

		if (poFieldDefn.getName().equals("G3D"))
		{
			extractPositionField(poField, poFieldDefn);
		}
		else
		{
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
					 doo("  Repeating (" + iRepeat + " of " +
					              poField.getRepeatCount() + ")...");
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
	}

	protected int viewSubfield(final DDFSubfieldDefinition poSFDefn, final byte[] pachFieldData,
			final int nBytesRemaining)
	{

		final MutableInt nBytesConsumed = new MutableInt();

		final DDFDataType ddfdt = poSFDefn.getType();

		if (ddfdt == DDFDataType.DDFInt)
		{
			doo("   " + poSFDefn.getName() + " = "
					+ poSFDefn.extractIntData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFFloat)
		{
			doo("   " + poSFDefn.getName() + " = "
					+ poSFDefn.extractFloatData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFString)
		{
			doo("   " + poSFDefn.getName() + " = "
					+ poSFDefn.extractStringData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}
		else if (ddfdt == DDFDataType.DDFBinaryString)
		{
			String str = poSFDefn.extractStringData(pachFieldData, nBytesRemaining, nBytesConsumed); // pabyBString
			if(poSFDefn.getName().equals("NAME"))
			{
				int _rcnm = str.charAt(0);

				// HACK! We should be reading chars as chars, we read them as
				// bytes. Values over 128 get made -ve, which doesn't work.
				if (_rcnm == 8218)
					_rcnm = 130;

				final int _rcid = str.charAt(1) + str.charAt(2) * 256 + str.charAt(3) * 256 * 256
						+ str.charAt(4) * 256 * 256 * 256;
				
				str += " nm:" + _rcnm + " id:" + _rcid;
			}
			doo(" " + poSFDefn.getName() + " = "
					+ str);
		}

		return nBytesConsumed.value;
	}


	public static void main(final String[] argv)
	{
		String[] args = argv.clone();

		Debug.init();

		String pszFilename = null;
		boolean bFSPTHack = false;

		if (args.length == 0)
		{
			args = new String[] { "E://dev/s57/AU411141.000" };
		}

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
			Debug.output("Usage: View8211 filename\n");
			System.exit(1);
		}

		new TestENC2(pszFilename, bFSPTHack);
		
		try
		{
			_so.close();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}

	}

	private static FileWriter _so = null;

	private static void doo(final String val)
	{
		try
		{
			if (_so == null)
				_so = new FileWriter("e:\\dev\\s57\\s57_listing3.txt");
			_so.write(val  +System.getProperty("line.separator") );
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}

	}

}
