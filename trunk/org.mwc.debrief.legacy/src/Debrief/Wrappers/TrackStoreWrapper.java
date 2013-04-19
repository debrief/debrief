package Debrief.Wrappers;

import java.awt.Color;
import java.util.Collection;

import Debrief.GUI.Frames.Application;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;

public class TrackStoreWrapper extends BaseLayer  implements WatchableList
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String COUCHDB_LOCATION = "CouchDb_Loc";
	public static final String ES_LOCATION = "ES_Loc";
	
	public TrackStoreWrapper()
	{
		// ok, self-config
		String couchURL = Application.getThisProperty(COUCHDB_LOCATION);
		String esURL = Application.getThisProperty(ES_LOCATION);
		
		// find the database - is it there?
		System.out.println("Couch: " + couchURL);
		System.out.println("ES: " + esURL);
		
		// now find the search index - is it there?
	}


	@Override
	public void filterListTo(HiResDate start, HiResDate end)
	{
		// here's where we re-query
		System.err.println("Filtering my documents to:" + start.toString() + " to " + end);
		
		// do we have a search connection?
		
		
	}

	
	@Override
	public void paint(CanvasType dest)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasEditor()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public WorldArea getBounds()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exportShape()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void append(Layer other)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setName(String val)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasOrderedChildren()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLineThickness()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void add(Editable point)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeElement(Editable point)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return "Track Store";
	}


	@Override
	public HiResDate getStartDTG()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public HiResDate getEndDTG()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Watchable[] getNearestTo(HiResDate DTG)
	{
		return new Watchable[]{};
	}


	@Override
	public Collection<Editable> getItemsBetween(HiResDate start, HiResDate end)
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Color getColor()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public PlainSymbol getSnailShape()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
