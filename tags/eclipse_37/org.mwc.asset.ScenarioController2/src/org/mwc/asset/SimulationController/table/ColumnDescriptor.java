package org.mwc.asset.SimulationController.table;

public class ColumnDescriptor
{

	private final String myName;

	private final int myIndex;

	private boolean myIsVisible;

	public ColumnDescriptor(String name, int index, boolean visible)
	{
		myName = name;
		myIndex = index;
		myIsVisible = visible;
	}

	public int getIndex()
	{
		return myIndex;
	}

	public String getName()
	{
		return myName;
	}

	public boolean isVisible()
	{
		return myIsVisible;
	}

	public void setVisible(boolean visible)
	{
		myIsVisible = visible;
	}
}
