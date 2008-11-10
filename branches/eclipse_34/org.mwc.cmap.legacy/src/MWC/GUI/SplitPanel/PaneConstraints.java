 /*
 * Copyright (c) 1997 Borland International, Inc. All Rights Reserved.
 * 
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.  
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS 
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY CLAIMS
 * OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR DISTRIBUTION
 * OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES ARISING OUT OF
 * OR RESULTING FROM THE USE, MODIFICATION, OR DISTRIBUTION OF PROGRAMS
 * OR FILES CREATED FROM, BASED ON, AND/OR DERIVED FROM THIS SOURCE
 * CODE FILE.
 * 
 */


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

  public PaneConstraints(String name,String splitComponentName, String position, float proportion) {
    this.name = name;
    this.splitComponentName = splitComponentName;
    this.position = position;
    this.proportion = proportion;
  }
  
  public String toString() {
    return name + ": " + splitComponentName + "," + position + " proportion:" + proportion;    //NORES
  }
}
