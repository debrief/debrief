package MWC.GUI;

import java.util.Enumeration;

public class DynamicLayer extends BaseLayer implements MovingPlottable
{

	private static final long serialVersionUID = 1L;

	@Override
	public void paint(CanvasType dest, long time)
	{
		Enumeration<Editable> elements = elements();
		while (elements.hasMoreElements())
		{
			Editable element = elements.nextElement();
			if (element instanceof MovingPlottable)
			{
				((MovingPlottable)element).paint(dest, time);
			}
		}
	}

}
