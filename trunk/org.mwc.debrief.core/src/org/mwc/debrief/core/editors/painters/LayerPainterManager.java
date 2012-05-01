/**
 * 
 */
package org.mwc.debrief.core.editors.painters;

import java.beans.*;
import java.util.*;

import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.debrief.core.editors.painters.highlighters.*;

/**
 * @author ian.mayo
 */
public class LayerPainterManager extends PropertyChangeSupport
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * our list of painters
	 */
	private Vector<TemporalLayerPainter> _myPainterList;

	/**
	 * our list of highlighters
	 * 
	 */
	private Vector<SWTPlotHighlighter> _myHighlighterList;

	/**
	 * the current one
	 * 
	 * @param dataProvider
	 */
	private TemporalLayerPainter _currentPainter = null;

	/**
	 * and the highlighter
	 * 
	 */
	private SWTPlotHighlighter _currentHighlighter = null;

	/**
	 * constructor - to collate the list
	 * 
	 * @param dataProvider
	 */
	public LayerPainterManager(TrackDataProvider dataProvider)
	{
		super(dataProvider);

		// and now build the painters
		_myPainterList = new Vector<TemporalLayerPainter>(0, 1);
		_myPainterList.add(new PlainHighlighter());
		_myPainterList.add(new SnailHighlighter(dataProvider));

		setCurrentPainter((TemporalLayerPainter) _myPainterList.firstElement());

		// and the plot highlighters
		_myHighlighterList = new Vector<SWTPlotHighlighter>(0, 1);
		_myHighlighterList.add(new SWTPlotHighlighter.RectangleHighlight());
		_myHighlighterList.add(new SWTSymbolHighlighter());
		_myHighlighterList.add(new SWTRangeHighlighter());
		_myHighlighterList.add(new NullHighlighter());

		// and sort out the defaults
		_currentPainter = _myPainterList.firstElement();
		_currentHighlighter = _myHighlighterList.firstElement();
	}

	/**
	 * get the list of painters
	 */
	public TemporalLayerPainter[] getPainterList()
	{
		TemporalLayerPainter[] res = new TemporalLayerPainter[]
		{ null };
		return (TemporalLayerPainter[]) _myPainterList.toArray(res);
	}

	/**
	 * ditch ourselves
	 * 
	 */
	public void close()
	{
		_myHighlighterList.clear();
		_myPainterList.clear();
		_currentHighlighter = null;
		_currentPainter = null;
	}

	/**
	 * get the list of painters
	 */
	public SWTPlotHighlighter[] getHighlighterList()
	{
		SWTPlotHighlighter[] res = new SWTPlotHighlighter[]
		{ null };
		return (SWTPlotHighlighter[]) _myHighlighterList.toArray(res);
	}

	/**
	 * find out which is the currently selected painter
	 * 
	 * @return
	 */
	public TemporalLayerPainter getCurrentPainter()
	{
		return _currentPainter;
	}

	/**
	 * allow changing of the current painter
	 * 
	 * @param current
	 *          the new one being selected
	 */
	public void setCurrentPainter(TemporalLayerPainter current)
	{
		// store the old one
		TemporalLayerPainter old = _currentPainter;

		// assign to the new one
		_currentPainter = current;

		// inform anybody who wants to know
		firePropertyChange("Changed", old, current);
	}

	/**
	 * decide which cursor to use (based on text string)
	 * 
	 * @param cursorName
	 */
	public void setCurrentPainter(String cursorName)
	{
		TemporalLayerPainter newCursor = null;
		for (Iterator<TemporalLayerPainter> thisPainter = _myPainterList.iterator(); thisPainter
				.hasNext();)
		{
			TemporalLayerPainter thisP = (TemporalLayerPainter) thisPainter.next();
			if (thisP.getName().equals(cursorName))
			{
				newCursor = thisP;
				break;
			}
		}

		// cool. did we find one?
		if (newCursor != null)
			setCurrentPainter(newCursor);
	}

	/**
	 * find out which is the currently selected painter
	 * 
	 * @return
	 */
	public SWTPlotHighlighter getCurrentHighlighter()
	{
		return _currentHighlighter;
	}

	/**
	 * allow changing of the current painter
	 * 
	 * @param current
	 *          the new one being selected
	 */
	public void setCurrentHighlighter(SWTPlotHighlighter current)
	{
		// store the old one
		SWTPlotHighlighter old = _currentHighlighter;

		// assign to the new one
		_currentHighlighter = current;

		// inform anybody who wants to know
		firePropertyChange("Changed", old, current);
	}

	/**
	 * decide which cursor to use (based on text string)
	 * 
	 * @param highlighterName
	 */
	public void setCurrentHighlighter(String highlighterName)
	{
		SWTPlotHighlighter newCursor = null;
		for (Iterator<SWTPlotHighlighter> thisHighlighter = _myHighlighterList
				.iterator(); thisHighlighter.hasNext();)
		{
			SWTPlotHighlighter thisP = (SWTPlotHighlighter) thisHighlighter.next();
			if (thisP.getName().equals(highlighterName))
			{
				newCursor = thisP;
				break;
			}
		}

		// cool. did we find one?
		if (newCursor != null)
			setCurrentHighlighter(newCursor);
	}
}