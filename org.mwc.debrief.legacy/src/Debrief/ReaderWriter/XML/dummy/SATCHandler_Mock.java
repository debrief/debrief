
package Debrief.ReaderWriter.XML.dummy;

import java.awt.Color;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Debrief.GUI.Frames.Application;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

/** mock handler that will read in an SATC_Solution object, but which 
 * won't actually load the data
 * @author ian
 *
 */
public class SATCHandler_Mock extends MWCXMLReader implements LayerHandlerExtension
{
	private static final String MY_TYPE = "satc_solution";

	private static final String NAME = "NAME";
	private static final String SHOW_BOUNDS = "ShowBounds";
	private static final String ONLY_ENDS = "OnlyPlotEnds";
	private static final String SHOW_SOLUTIONS = "ShowSolutions";
	private static final String SHOW_ALTERATIONS = "ShowAlterationBounds";
	private static final String LIVE_RUNNING = "LiveRunning";

	public SATCHandler_Mock()
	{
		this(MY_TYPE);
	}

	public SATCHandler_Mock(String theType)
	{
		// inform our parent what type of class we are
		super(theType);

		addAttributeHandler(new HandleAttribute(NAME)
		{
			public void setValue(String name, String val)
			{
			  // ignore
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_BOUNDS)
		{
			@Override
			public void setValue(String name, boolean value)
			{
        // ignore
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_ALTERATIONS)
		{
			@Override
			public void setValue(String name, boolean value)
			{
        // ignore
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(ONLY_ENDS)
		{
			@Override
			public void setValue(String name, boolean value)
			{
        // ignore
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(LIVE_RUNNING)
		{
			@Override
			public void setValue(String name, boolean value)
			{
        // ignore
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_SOLUTIONS)
		{
			@Override
			public void setValue(String name, boolean value)
			{
        // ignore
			}
		});
		addHandler(new ColourHandler()
		{
			@Override
			public void setColour(Color res)
			{
        // ignore
			}
		});
	}


  public void elementClosed()
  {
    Application.logError2(Application.WARNING,
        "SATC element has been dropped, this is a mock handler", null);
  }

	@Override
	public void setLayers(Layers theLayers)
	{
    // ignore
	}

	@Override
	public boolean canExportThis(Layer subject)
	{
		return false;
	}

	@Override
	public void exportThis(Layer theLayer, Element parent, Document doc)
	{
	  throw new IllegalArgumentException("This is a mock handler, it cannot be used for export");
	}

}