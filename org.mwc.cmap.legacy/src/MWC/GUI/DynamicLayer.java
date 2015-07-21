package MWC.GUI;

import java.util.Enumeration;

/** specialised type of layer that is able to plot dynamic children,
 * that-is elements that are time-sensitive.
 * @author ian
 *
 */
public class DynamicLayer extends BaseLayer implements DynamicPlottable
{

	private static final long serialVersionUID = 1L;

	
	
	@Override
	public void add(Editable thePlottable)
	{
		// SPECIAL HANDLING.  We can't allow DynamicPlottables to be added to normal
		// layers, since normal layers don't provide the time integration.  
		// We only allow them to be pasted into a DynamicLayer like this.
		super.getData().add(thePlottable);
	}

	@Override
	public void paint(CanvasType dest)
	{
		// do nothing
	}

	@Override
	public void paint(CanvasType dest, long time)
	{
		Enumeration<Editable> elements = elements();
		while (elements.hasMoreElements())
		{
			Editable element = elements.nextElement();
			if (element instanceof DynamicPlottable)
			{
				((DynamicPlottable)element).paint(dest, time);
			}
		}
	}

}
