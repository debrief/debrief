//JStacker.java

package MWC.GUI.ptplot;


import java.awt.BorderLayout;
import java.awt.Panel;

/** act as a wrapper to this implementation of a plot - 
 * separate the interface from the plot object
 */
abstract public class JStacker extends Panel {

  ////////////////////////////////////////////////
  // member variables
  ///////////////////////////////////////////////
  
	// the plot object
	private Plot sPlot;

  ////////////////////////////////////////////////
  // constructor
  ///////////////////////////////////////////////
  
  /** constructor - overrides the Panel constructor, takes
   * the title to give the plot
   */
	public JStacker(String name){   
		initForm(name);
		
    // re-initialise the data
		calcData();
		
	}
  ////////////////////////////////////////////////
  // member functions
  ///////////////////////////////////////////////
  
	
  /** draw the bare content for the plot
   */
	@SuppressWarnings("deprecation")
	protected void initForm(String name){
    
    setName(name);
    
		sPlot = new Plot();
		sPlot.setVisible(true);

		sPlot.setTitle("name");

		sPlot.setNumSets(1);
		sPlot.setMarksStyle("points");
    
		this.setLayout(new BorderLayout());
		add("Center", sPlot);
    doLayout();
    


    sPlot.init();
	}
	
  /** abstract function, to be overriden by concrete classes.
   * The method should clear the plot and redraw the points
   */
	abstract protected void calcData();
	
  /** redraw the plot, using a calcDAta
   */
	public void update(){
		this.calcData();
		sPlot.repaint();
		}

  ////////////////////////////////////////////////
  // plot accessor methods
  ///////////////////////////////////////////////
  protected void setTitle(String theName){
    sPlot.setTitle(theName);
  }
  
  @SuppressWarnings("deprecation")
	protected void setNumSets(int numSets){
    sPlot.setNumSets(numSets);
  }
  
  protected void setMarksStyle(String theStyle){
    sPlot.setMarksStyle(theStyle);
  }
  
  protected void setXRange(double lower, double upper){
    sPlot.setXRange(lower, upper);
  }
  
  protected void setYRange(double lower, double upper){
    sPlot.setYRange(lower, upper);
  }
  
  protected void addPoint(int theSeries, double xVal, double yVal, boolean connected){
    sPlot.addPoint(theSeries, xVal, yVal, connected);
  }
  
  protected void setBackgroundColor(java.awt.Color theCol){
    sPlot.setBackground(theCol);
  }

  protected void setForegroundColor(java.awt.Color theCol){
    sPlot.setForeground(theCol);
  }
  
  protected void addYTick(String theStr,
                           double thePoint){
    sPlot.addYTick(theStr, thePoint);
  }
                           
  
}
