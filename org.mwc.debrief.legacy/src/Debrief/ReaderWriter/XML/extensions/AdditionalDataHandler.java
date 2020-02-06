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
package Debrief.ReaderWriter.XML.extensions;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Debrief.Wrappers.Extensions.AdditionalData;
import Debrief.Wrappers.Extensions.AdditionalProvider;
import MWC.Utilities.ReaderWriter.XML.IDOMExporter;
import MWC.Utilities.ReaderWriter.XML.ISAXImporter;
import MWC.Utilities.ReaderWriter.XML.ISAXImporter.DataCatcher;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class AdditionalDataHandler extends MWCXMLReader {
	public static interface ExportProvider {
		public List<IDOMExporter> getExporters();

		public List<ISAXImporter> getImporters();
	}

	private static final String MY_TYPE = "AdditionalData";

	private static ExportProvider _eHelper;

	public static void appendChild(final AdditionalProvider holder, final Element parent, final Document doc) {
		// ok, is there any extra data?
		final AdditionalData additional = holder.getAdditionalData();
		if (additional != null) {
			// do we have any exporters?
			if (_eHelper != null) {
				final List<IDOMExporter> exporters = _eHelper.getExporters();

				// ok, any children?
				if (additional.size() > 0) {
					boolean doneOne = false;

					// ok, we need the placeholder
					final Element aData = doc.createElement(MY_TYPE);

					for (final Object item : additional) {
						for (final IDOMExporter t : exporters) {
							if (t.canExportThis(item)) {
								t.export(item, aData, doc);

								doneOne = true;
							}
						}
					}

					// add to parent, if we have anything
					if (doneOne) {
						parent.appendChild(aData);
					}
				}
			}
		}
	}

	public static void setExportHelper(final ExportProvider helper) {
		_eHelper = helper;
	}

	private AdditionalData aData = new AdditionalData();

	public AdditionalDataHandler() {
		super(MY_TYPE);

		// ok, now for the child data. See if we have other handlers to declare
		if (_eHelper != null) {
			// create helper to store dta
			final DataCatcher storeMe = new DataCatcher() {

				@Override
				public void storeThis(final Object data) {
					aData.add(data);
				}
			};

			final List<ISAXImporter> helpers = _eHelper.getImporters();
			for (final ISAXImporter t : helpers) {
				addHandler(t.getHandler(storeMe));
			}
		}
	}

	@Override
	public void elementClosed() {
		super.elementClosed();

		// ok, store the data
		storeData(aData);

		// now clear it
		aData = new AdditionalData();
	}

	/**
	 * implementing classes must override this, to store the data
	 *
	 * @param data
	 */
	abstract public void storeData(AdditionalData data);

}
