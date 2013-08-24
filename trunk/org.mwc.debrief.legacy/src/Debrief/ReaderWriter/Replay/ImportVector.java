package Debrief.ReaderWriter.Replay;

import java.util.StringTokenizer;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.*;
import MWC.GenericData.*;
import MWC.Utilities.ReaderWriter.PlainLineImporter;

/** class to parse a vector from a line of text
 */
final class ImportVector implements PlainLineImporter
{
  /** the type for this string
   */
  private final String _myType = ";VECTOR:";
  
  /** read in this string and return a Label
   */
  public final Object readThisLine(final String theLine){
	// get a stream from the string
	    final StringTokenizer st = new StringTokenizer(theLine);
	    
	    // declare local variables
	    WorldLocation start;
	    String theSymbology;
	    
	    // skip the comment identifier
	    st.nextToken();
	    
	    // start with the symbology
	    theSymbology = st.nextToken();
	    
	    // now the start location
		start = ImportLine.extractStart(st);

	    final String range = st.nextToken();
	    final WorldDistance distance = new WorldDistance(new Double(range), WorldDistance.YARDS);
	    final String bearingString = st.nextToken();
			
		String theText="";
	    // see if there are any more tokens waiting,
	    if(st.hasMoreTokens())
	    {
	      // and lastly read in the message
	      theText = st.nextToken("\r").trim();
	    }
	    
	    
			
	    // create the Vector object
	    final VectorShape sp = new VectorShape(start, new Double(bearingString), distance);
	    sp.setColor(ImportReplay.replayColorFor(theSymbology));
	    
		final WorldArea tmp = new WorldArea(start, sp.getLineEnd());
		tmp.normalise();
			
	    // and put it into a shape
	    final ShapeWrapper sw = new ShapeWrapper(theText, 
	                                       sp, 
	                                       ImportReplay.replayColorFor(theSymbology),
																				 null);
	    
	    return sw;
  }
  
  /** determine the identifier returning this type of annotation
   */
  public final String getYourType(){
    return _myType;
  }

	/** export the specified shape as a string
	 * @return the shape in String form
	 * @param shape the Shape we are exporting
	 */	
	public final String exportThis(final MWC.GUI.Plottable theWrapper)
	{
		final ShapeWrapper theShape = (ShapeWrapper) theWrapper;
		
		final VectorShape vector = (VectorShape) theShape.getShape();
		
		// result value
		String line;
		
		line = _myType + " " + ImportReplay.replaySymbolFor(vector.getColor(), null) + "  ";
							
		line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(vector.getLine_Start());

		line = line + " " + vector.getDistance().getValueIn(WorldDistance.YARDS) + " " + vector.getBearing();

		return line;
		
	}
	
	/** indicate if you can export this type of object
	 * @param val the object to test
	 * @return boolean saying whether you can do it
	 */
	public final boolean canExportThis(final Object val)
	{
		boolean res = false;
		
		if(val instanceof ShapeWrapper)
		{
			final ShapeWrapper sw = (ShapeWrapper) val;
			final PlainShape ps = sw.getShape();
			res = (ps instanceof VectorShape);
		}
		
		return res;

	}
	
}
