/**
 * 
 */
package org.mwc.debrief.core.editors.painters;

import java.beans.*;
import java.util.Vector;

import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;

/**
 * @author ian.mayo
 *
 */
public class LayerPainterManager extends PropertyChangeSupport
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** our list of painters
	 * 
	 */
	private Vector _myList;
	
	/** the current one
	 * 
	 * @param dataProvider
	 */
	private TemporalLayerPainter _current = null;

	/** constructor - to collate the list
	 * 
	 * @param dataProvider
	 */
	public LayerPainterManager(TrackDataProvider dataProvider)
	{
		super(dataProvider);
		
		// and now build the painters
		_myList = new Vector(0,1);
		_myList.add(new PlainHighlighter());
		_myList.add(new SnailHighlighter(dataProvider));
		
		setCurrent((TemporalLayerPainter) _myList.firstElement());
	}

	/** find out which is the currently selected painter
	 * 
	 * @return
	 */
	public TemporalLayerPainter getCurrent()
	{
		return _current;
	}

	/** get the list of painters
	 * 
	 */
	public TemporalLayerPainter[] getList()
	{
		TemporalLayerPainter[] res = new TemporalLayerPainter[]{null};
		return (TemporalLayerPainter[])_myList.toArray(res);
	}
	
	/** allow changing of the current painter
	 * 
	 * @param current the new one being selected
	 */	
	public void setCurrent(TemporalLayerPainter current)
	{
		// store the old one
		TemporalLayerPainter old = _current;
		
		// assign to the new one
		_current = current;
		
		// inform anybody who wants to know
		firePropertyChange("Changed", old, current);
	}
}
