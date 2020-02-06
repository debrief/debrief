 


package MWC.GUI.SplitPanel;



public class PaneConstraints {
  public float  proportion = 0.5f;             //NORES
  public String position = TOP;
  public String splitComponentName;
  public String name;
  public static final String TOP    = "Top";       //NORES
  public static final String BOTTOM = "Bottom";    //NORES
  public static final String LEFT   = "Left";      //NORES
  public static final String RIGHT  = "Right";     //NORES
  public static final String ROOT   = "Root";      //NORES

  public PaneConstraints() {
  }

  public PaneConstraints(final String name,final String splitComponentName, final String position, final float proportion) {
    this.name = name;
    this.splitComponentName = splitComponentName;
    this.position = position;
    this.proportion = proportion;
  }
  
  public String toString() {
    return name + ": " + splitComponentName + "," + position + " proportion:" + proportion;    //NORES
  }
}
