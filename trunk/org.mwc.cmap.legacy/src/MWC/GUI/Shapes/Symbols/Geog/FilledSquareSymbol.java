package MWC.GUI.Shapes.Symbols.Geog;

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
