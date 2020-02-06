/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.operations.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.admin.CopyCsvToClipboardAction.CopyCsvToClipboardCommand;

/**
 * Provides UI & processing to export a single collection to a CSV file
 *
 * @author ian
 *
 */
public class ExportCsvToFileAction implements IOperation {

	/**
	 * encapsulate command
	 *
	 * @author ian
	 *
	 */
	public static class ExportCsvToFileCommand extends AbstractCommand {
		private final List<IStoreItem> _selection;

		public ExportCsvToFileCommand(final String title, final List<IStoreItem> selection, final IStoreGroup store,
				final IContext context) {
			super(title, "Export selection to CSV file", store, false, false, null, context);
			_selection = selection;
		}

		@Override
		public void execute() {
			final String csv = CopyCsvToClipboardCommand.getCsvString(_selection);
			if (csv != null && !csv.isEmpty()) {
				final String result = getContext().getCsvFilename();
				if (result == null) {
					return;
				}
				final File file = new File(result);
				if (file.exists() && !getContext().openQuestion("Overwrite '" + result + "'?",
						"Are you sure you want to overwrite '" + result + "'?")) {
					return;
				}
				FileOutputStream fop = null;
				try {
					fop = new FileOutputStream(file);
					fop.write(csv.getBytes(Charset.forName("UTF-8")));
				} catch (final IOException e) {
					getContext().openError("Error", "Cannot write to '" + result + "'. See log for more details");
					getContext().log(e);
				} finally {
					if (fop != null) {
						try {
							fop.close();
						} catch (final IOException e) {
							getContext().logError(IContext.Status.ERROR,
									"Failed to close fop in DataManagerEditor export to CSV", e);
						}
					}
				}
			} else {
				getContext().openInformation("Data Manager Editor", "Cannot copy current selection");
			}
		}

		@Override
		protected void recalculate(final IStoreItem subject) {
		}

	}

	@Override
	public List<ICommand> actionsFor(final List<IStoreItem> selection, final IStoreGroup destination,
			final IContext context) {
		final List<ICommand> res = new ArrayList<ICommand>();
		if (appliesTo(selection)) {
			// hmm, see if we have a single collection selected
			ICommand newC = null;
			if (selection.size() == 1) {
				newC = new ExportCsvToFileCommand("Export to CSV file", selection, destination, context);
				res.add(newC);
			}
		}

		return res;
	}

	private boolean appliesTo(final List<IStoreItem> selection) {
		return selection.size() == 1 && selection.get(0) instanceof IDocument;
	}

}
