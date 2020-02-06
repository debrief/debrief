
package MWC.GUI.Shapes.Symbols.Geog;

import MWC.GUI.Shapes.Symbols.PlainSymbol;

public class FilledSquareSymbol extends SquareSymbol
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FilledSquareSymbol()
	{
		super();

		// and set it to be filled
		super.setFillSymbol(true);
	}
	
	 
  @Override
  public PlainSymbol create()
  {
    return new FilledSquareSymbol();
  }
	
	/**
	 * getType
	 * 
	 * @return the returned String
	 */
	public String getType()
	{
		return "FilledSquare";
	}
	
	
}
