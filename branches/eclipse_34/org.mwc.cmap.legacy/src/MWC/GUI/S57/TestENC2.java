/* ****************************************************************************
 * $Id: TestENC2.java,v 1.2 2007/05/04 08:30:16 ian.mayo Exp $
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

import MWC.GenericData.WorldLocation;

import com.bbn.openmap.layer.vpf.MutableInt;
import com.bbn.openmap.util.Debug;

import java.io.*;
import java.util.*;

/**
 * Class that uses the DDF* classes to read an 8211 file and print out the
 * contents.
 */
public class TestENC2
{

	protected boolean bFSPTHack = false;

	protected String pszFilename = null;

	public TestENC2(String filename, boolean fspt_repeating)
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
				iRecord++;

				// if (iRecord > 100)
				// break;
				doo("=== Record " + iRecord);

				/* ------------------------------------------------------------ */
				/* Loop over each field in this particular record. */
				/* ------------------------------------------------------------ */
				Iterator it = poRecord.iterator();
				while (it.hasNext())
				{
					DDFField field = (DDFField) it.next();
					viewRecordField(field);
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
	 * create a series of 3d positions
	 * 
	 * @param poField
	 * @param poFieldDefn
	 */
	private void extractPositionField(DDFField poField, DDFFieldDefinition poFieldDefn)
	{
		Vector<WorldLocation> thePositions = new Vector<WorldLocation>(0, 1);

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
			WorldLocation myVal = new WorldLocation(0, 0, 0);

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

			thePositions.add(myVal);
		}

		System.out.println("|| 3d field: found " + thePositions.size());
	}

	/**
	 * Dump the contents of a field instance in a record.
	 */
	protected void viewRecordField(DDFField poField)
	{
		DDFFieldDefinition poFieldDefn = poField.getFieldDefn();

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
					DDFSubfieldDefinition poSFDefn = poFieldDefn.getSubfieldDefn(iSF);
					int nBytesConsumed = viewSubfield(poSFDefn, pachFieldData, nBytesRemaining);
					nBytesRemaining -= nBytesConsumed;
					byte[] tempData = new byte[pachFieldData.length - nBytesConsumed];
					System.arraycopy(pachFieldData, nBytesConsumed, tempData, 0, tempData.length);
					pachFieldData = tempData;
				}
			}
		}
	}

	protected int viewSubfield(DDFSubfieldDefinition poSFDefn, byte[] pachFieldData,
			int nBytesRemaining)
	{

		MutableInt nBytesConsumed = new MutableInt();

		DDFDataType ddfdt = poSFDefn.getType();

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

				int _rcid = str.charAt(1) + str.charAt(2) * 256 + str.charAt(3) * 256 * 256
						+ str.charAt(4) * 256 * 256 * 256;
				
				str += " nm:" + _rcnm + " id:" + _rcid;
			}
			doo(" " + poSFDefn.getName() + " = "
					+ str);
		}

		return nBytesConsumed.value;
	}


	public static void main(String[] argv)
	{

		Debug.init();

		String pszFilename = null;
		boolean bFSPTHack = false;

		if (argv.length == 0)
		{
			argv = new String[] { "E://dev/s57/AU411141.000" };
		}

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
			Debug.output("Usage: View8211 filename\n");
			System.exit(1);
		}

		new TestENC2(pszFilename, bFSPTHack);
		
		try
		{
			_so.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private static FileWriter _so = null;

	private static void doo(String val)
	{
		try
		{
			if (_so == null)
				_so = new FileWriter("e:\\dev\\s57\\s57_listing3.txt");
			_so.write(val  +System.getProperty("line.separator") );
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

}
