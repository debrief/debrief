package org.mwc.debrief.track_shift.views;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Enumeration;

import org.mockito.Mockito;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;

import MWC.GUI.Editable;
import MWC.GUI.Layers;

public class OptimiseTest
{

	public static void main(String[] args) throws FileNotFoundException
	{
		// get some data
		Layers layers = getTheData();

		// create a mockup of the plot
		Enumeration<Editable> enumer = layers.elements();
		while (enumer.hasMoreElements())
		{
			Editable editable = (Editable) enumer.nextElement();
			System.out.println("this layer is:" + editable.getName());
		}

		// run setup optimise

		// and optimise it
	}

	private static Layers getTheData() throws FileNotFoundException
	{
		DebriefEclipseXMLReaderWriter reader = new DebriefEclipseXMLReaderWriter();
		Layers res = new Layers();
		String path = "src/org/mwc/debrief/track_shift/views/";
		String fName = "midflow2.xml";
		InputStream is = new FileInputStream(path + fName);
		IControllableViewport view = Mockito.mock(IControllableViewport.class);
		PlotEditor plot = Mockito.mock(PlotEditor.class);
		reader.importThis(fName, is, res, view, plot);
		return res;
	}

}
