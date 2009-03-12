package org.mwc.cmap.plotViewer.editors.chart;

import java.util.HashMap;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.ui_support.LineItem;

public class StatusPanel // implements MouseMoveListener
{

	/**
	 * the label we're updating
	 */
	protected LineItem _label;

	/**
	 * the identifier for this type of tracker
	 * 
	 */
	private String _myId;

	/**
	 * keep track of how many people have a pointer to this tracker
	 * 
	 */
	private static HashMap<String, Integer> _lineUsers = new HashMap<String, Integer>();

	// ///////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////////
	/**
	 * create a panel in the status bar
	 * 
	 * @param editor
	 *          the editor to host the panel
	 * @param id
	 *          a unique id for the panel
	 * @param template
	 *          a string to use to set the correct width
	 * @param tooltip
	 *          hover-tooltip to show for this panel
	 */
	public StatusPanel(EditorPart editor, String id, String template,
			String tooltip, String prefId)
	{
		// store the id
		_myId = id;

		// remember the users
		if (!_lineUsers.containsKey(id))
			_lineUsers.put(id, new Integer(0));

		// ok, get a new status line
		_label = getStatusLine(editor, id, template, tooltip, prefId);
	}

	public void write(final String msg)
	{
		Display d = Display.getDefault();
		d.asyncExec(new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run()
			{

				if (_label != null)
				{
					if (!_label.isDisposed())
					{
						_label.setText(msg);
					}
					else
					{
						// don't worry. the label isn't available when no editor is selected
					}
				}
			}
		});
	}

	public void close()
	{
		if (_lineUsers.containsKey(_myId))
		{
			int curr = _lineUsers.get(_myId);
			curr--;
			_lineUsers.put(_myId, curr);

			// is there any left?
			if (curr == 0)
			{
				// nope, ditch them
				_lineUsers.remove(_myId);
			}
		}
	}

	private LineItem getStatusLine(EditorPart editor, String id, String template,
			String tooltip, String prefId)
	{
		LineItem thisLabel = null;

		// right, is anybody holding onto the last line item? If nobody is,
		// Eclipse will ditch it, and we have to create a new one.
		int curr = _lineUsers.get(_myId);
		if (curr == 0)
		{
			IStatusLineManager mgr = editor.getEditorSite().getActionBars()
					.getStatusLineManager();
			thisLabel = new LineItem(id, template, tooltip, prefId);
			mgr.add(thisLabel);
		}

		if (curr < 0)
		{
			System.err.println("CorePlugin: StatusPanel in unstable state");
		}

		// ok, increment the users
		curr++;

		// and remember it
		_lineUsers.put(_myId, curr);

		return thisLabel;
	}

}
