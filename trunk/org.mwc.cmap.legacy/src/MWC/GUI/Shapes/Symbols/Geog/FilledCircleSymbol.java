package MWC.GUI.Shapes.Symbols.Geog;

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
