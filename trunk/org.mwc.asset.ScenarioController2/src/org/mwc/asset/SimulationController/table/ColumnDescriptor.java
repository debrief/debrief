package org.mwc.asset.SimulationController.table;

public class ColumnDescriptor
{

	private final String myName;

	private final int myIndex;

	private boolean myIsVisible;

	public ColumnDescriptor(final String name, final int index, final boolean visible)
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

	public void setVisible(final boolean visible)
	{
		myIsVisible = visible;
	}
}
