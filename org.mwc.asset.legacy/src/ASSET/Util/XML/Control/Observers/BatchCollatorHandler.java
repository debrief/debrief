
package ASSET.Util.XML.Control.Observers;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

import ASSET.Scenario.Observers.Summary.BatchCollator;
import ASSET.Scenario.Observers.Summary.BatchCollatorHelper;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class BatchCollatorHandler extends MWCXMLReader {

	private static String type = "BatchCollator";

	private static final String PER_CASE = "PerCase";
	private static final String FILE_NAME = "file_name";
	private static final String COLLATION_METHOD = "CollationMethod";
	private static final String ACTIVE = "Active";
	private static final String ONLY_BATCH = "OnlyBatchReporting";

	public static void exportCollator(final BatchCollatorHelper collator, final boolean onlyBatchProcesing,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc) {
		exportCollator(collator.getPerCase(), collator.getActive(), onlyBatchProcesing, collator.getFilename(),
				collator.getCollationStrategy(), parent, doc);
	}

	public static void exportCollator(final boolean perCase, final boolean isActive, final boolean onlyBatch,
			final String fileName, final String collation, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final org.w3c.dom.Element batch = doc.createElement(type);

		// set the attributes
		batch.setAttribute(ACTIVE, writeThis(isActive));
		batch.setAttribute(PER_CASE, writeThis(perCase));
		batch.setAttribute(FILE_NAME, fileName);
		batch.setAttribute(COLLATION_METHOD, collation);
		batch.setAttribute(ONLY_BATCH, writeThis(onlyBatch));

		parent.appendChild(batch);
	}

	public boolean _perCase;
	public boolean _isActive = false;
	public boolean _onlyBatch = false;

	public String _fileName;

	public String _collation;

	public BatchCollatorHandler() {
		super(type);

		addAttributeHandler(new HandleAttribute(FILE_NAME) {
			@Override
			public void setValue(final String name, final String val) {
				_fileName = val;
			}
		});
		addAttributeHandler(new HandleAttribute(COLLATION_METHOD) {
			@Override
			public void setValue(final String name, final String val) {
				_collation = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(ACTIVE) {
			@Override
			public void setValue(final String name, final boolean value) {
				_isActive = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(ONLY_BATCH) {
			@Override
			public void setValue(final String name, final boolean value) {
				_onlyBatch = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(PER_CASE) {
			@Override
			public void setValue(final String name, final boolean value) {
				_perCase = value;
			}
		});

	}

	@Override
	public void elementClosed() {

	}

	// /** store how this batch collator works
	// *
	// * @param perCase
	// * @param isActive
	// * @param directory
	// * @param fileName
	// * @param collation
	// */
	// abstract public void setBatchCollation(boolean perCase,
	// boolean isActive,
	// String directory,
	// String fileName,
	// String collation);

	public void reset() {
	}

	/**
	 * store our data in the collator observer we received
	 *
	 * @param collator
	 */
	public void setData(final BatchCollator collator) {

		// did we find any data?
		if (_collation != null) {
			// we have a collation method, must be allright then.

			collator.setBatchCollationProcessing(_fileName, _collation, _perCase, _isActive);
			collator.setBatchOnly(_onlyBatch);

			// and clear our data
			_perCase = false;
			_isActive = false;
			_fileName = null;
			_collation = null;
			_onlyBatch = false;
		}
	}

}