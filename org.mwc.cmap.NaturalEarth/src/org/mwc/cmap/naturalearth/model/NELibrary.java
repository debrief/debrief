package org.mwc.cmap.naturalearth.model;

import java.util.ArrayList;

import MWC.GUI.Editable;

public class NELibrary extends ArrayList<NEResolutionGroup>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class NEFeature implements Editable
	{
		private String name;

		public NEFeature(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		@Override
		public boolean hasEditor()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public EditorType getInfo()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}
}
