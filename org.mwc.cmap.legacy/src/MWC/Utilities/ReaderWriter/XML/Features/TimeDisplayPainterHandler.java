package MWC.Utilities.ReaderWriter.XML.Features;

import java.awt.Color;
import java.awt.Font;

import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.TimeDisplayPainter;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;

public abstract class TimeDisplayPainterHandler extends MWCXMLReader implements PlottableExporter
{
	public static final String COLOR = "Color";
	public static final String BACKGROUND_COLOR = "BackgroundColor";
	public static final String FILL_BACKGROUND = "FillBackground";
	public static final String FORMAT_TIME = "FormatTime";
	public static final String PREFIX = "Prefix";
	public static final String SUFFIX = "Suffix";
	public static final String VISIBLE = "Visible";
	public static final String ABSOLUTE = "Absolute";
	public static final String ORIGIN = "Origin";
	public static final String NAME = "Name";
	public static final String LOCATION = "Location";
	public static final String TYPE = "timeDisplay";
	protected boolean _visible;
	protected boolean _absolute;
	protected HiResDate _origin;
	protected boolean _fillBackground;
	protected String _formatTime;
	protected String _name;
	protected String _suffix;
	protected String _prefix;
	protected Font _font;
	protected int _location;
	protected Color _color;
	protected Color _bgColor;

	public TimeDisplayPainterHandler()
	{
		super(TYPE);
		addAttributeHandler(new HandleBooleanAttribute(VISIBLE)
		{
			public void setValue(final String name, final boolean value)
			{
				_visible = value;
			}
		});
		addAttributeHandler(new HandleAttribute(ORIGIN)
		{
			public void setValue(final String name, final String value)
			{
				_origin = parseThisDate(value);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(ABSOLUTE)
		{
			public void setValue(final String name, final boolean value)
			{
				_absolute = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(FILL_BACKGROUND)
		{
			public void setValue(final String name, final boolean value)
			{
				_fillBackground = value;
			}
		});
		addAttributeHandler(new HandleAttribute(NAME)
		{
			public void setValue(final String name, final String value)
			{
				_name = value;
			}
		});
		addAttributeHandler(new HandleAttribute(FORMAT_TIME)
		{
			public void setValue(final String name, final String val)
			{
				_formatTime = val;
			}
		});
		addAttributeHandler(new HandleAttribute(PREFIX)
		{
			public void setValue(final String name, final String val)
			{
				_prefix = val;
			}
		});
		addAttributeHandler(new HandleAttribute(SUFFIX)
		{
			public void setValue(final String name, final String val)
			{
				_suffix = val;
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(LOCATION)
		{
			public void setValue(final String name, final int val)
			{
				_location = val;
			}
		});
		addHandler(new FontHandler()
    {
      public void setFont(final java.awt.Font font)
      {
        _font = font;
      }
    });
		addHandler(new ColourHandler(COLOR)
    {
			public void setColour(Color res)
			{
				_color = res;
			}
    });
		addHandler(new ColourHandler(BACKGROUND_COLOR)
    {
			public void setColour(Color res)
			{
				_bgColor = res;
			}
    });
    
	}
	
	@Override
	public final void elementClosed()
	{
			// set our specific attributes
			final TimeDisplayPainter wrapper = new TimeDisplayPainter();
			wrapper.setVisible(_visible);
			wrapper.setAbsolute(_absolute);
			wrapper.setOrigin(_origin);
			wrapper.setFillBackground(_fillBackground);
			wrapper.setName(_name);
			wrapper.setFormat(_formatTime);
			wrapper.setPrefix(_prefix);
			wrapper.setSuffix(_suffix);
			if (_font != null)
			{
				wrapper.setFont(_font);
			}
			if (_color != null)
			{
				wrapper.setColor(_color);
			}
			if (_bgColor != null)
			{
				wrapper.setBackground(_bgColor);
			}
			wrapper.setLocation(_location);
			addPlottable(wrapper);
	}
		
	abstract public void addPlottable(MWC.GUI.Plottable plottable);

	@Override
	public void exportThisPlottable(Plottable plottable, org.w3c.dom.Element parent,
			org.w3c.dom.Document doc)
	{
		TimeDisplayPainter tdp = (TimeDisplayPainter) plottable;
		if (tdp == null)
		{
			return;
		}
		final org.w3c.dom.Element timeDisplay = doc.createElement(TYPE);

		timeDisplay.setAttribute(NAME, tdp.getName());
		timeDisplay.setAttribute(VISIBLE, writeThis(tdp.getVisible()));
		timeDisplay.setAttribute(ABSOLUTE, writeThis(tdp.isAbsolute()));
		if (!tdp.isAbsolute())
		{
			timeDisplay.setAttribute(ORIGIN, writeThis(tdp.getOrigin()));
		}
		timeDisplay.setAttribute(FILL_BACKGROUND, writeThis(tdp.isFillBackground()));
		timeDisplay.setAttribute(LOCATION, writeThis(tdp.getLocation()));
		timeDisplay.setAttribute(FORMAT_TIME, tdp.getFormat());
		timeDisplay.setAttribute(PREFIX, tdp.getPrefix());
		timeDisplay.setAttribute(SUFFIX, tdp.getSuffix());
		FontHandler.exportFont(tdp.getFont(), timeDisplay, doc);
    ColourHandler.exportColour(tdp.getColor(), timeDisplay, doc, COLOR);
    ColourHandler.exportColour(tdp.getBackground(), timeDisplay, doc, BACKGROUND_COLOR);
    
		parent.appendChild(timeDisplay);
	}

}
