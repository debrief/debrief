
package MWC.GUI.Shapes.Symbols.Geog;

import MWC.GUI.Shapes.Symbols.PlainSymbol;

public class FilledCircleSymbol extends CircleSymbol
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FilledCircleSymbol()
	{
		super();
		
		// indicate the parent should be filled
		super.setFillSymbol(true);
	}
	
	@Override
  public PlainSymbol create()
  {
    return new FilledCircleSymbol();
  }

  /**
	 * getType
	 * 
	 * @return the returned String
	 */
	public String getType()
	{
		return "FilledCircle";
	}
	

}
