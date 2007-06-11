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

//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996, 1996 Borland International, Inc. All Rights Reserved.
//
//--------------------------------------------------------------------------------------------------

package MWC.GUI.SplitPanel;




import java.awt.event.*;
//import borland.jbcl.util.*;
import java.awt.*;

/**
 * A Pane Layout Manager
 *
 * Maintains a binary tree of child components.  First split divides
 * the whole space horizontally or vertically, next split divides these
 * splits further.
 *
 * Components can be added either in a default (and not very useful) way
 * by using add().  This method traverses the right hand nodes of the
 * layout tree until it encounters a leaf.  This leaf is then split 50:50
 * in the opposite way from its parent and the new node inserted.
 *
 * The other way to add components is to specifiy a PaneConstaints object
 * which specifies the component to split, whether to split horizontally and
 * the proportion of the space to take from the component that is being split
 *
 * When the window is resized, all the proportions are maintained as best
 * as possible.
 *
 * Programmatic modifictaion of the proportions at runtime are accomplished
 * via setConstraints(Component, PaneConstaint) .
 *
 * For a UI to modifify proportions, use borland.jbcl.control.SplitPanel.
 *
 * Further work:
 *   - implement docking of windows (at present they are just zero width)
 *   - implement undocking of windows into different orders
 *   - improve the message handling to get sharper performance
 *   - may ultimately need a more complex alogrithm - current binary tree
 *     may look a little wacky under some conditions
 *
 * @version 1.0, 25 Sept 1995
 * @author David Williams
 */
public class PaneLayout implements LayoutManager2
{
  PaneNode rootNode;             // Root node of the layout tree
  //Rectangle dividerRect;
  // Container c;
  PaneNode lastSelected;         // the last Selected node; used by Split
  private PaneNode lastDeletion; // the last removed component so we can repair
                                 // the tree if the user removes and then re-adds
                                 // instead of using setConstraints
  int gap = 0;                   // SplitPanel sets this to 2
  private String lastComponentAdded;
  private int addCount = 0;     // number of components added without a constraint object

  /**
   *  Constructor
   */
  public PaneLayout() {
  }

  /** The number of pixels that will surround each component
   *  the actual size of a divider will be twice this number
   *  SplitPanel uses this area to allow the user to change the
   *  relative sizes of the components by clicking and dragging
   *  on this area. Default is 0.
   */
  public void setGap(int gap) {
    this.gap = gap;
  }

  /**
   * returns the  gap size
   */
  public int getGap() {
    return gap;
  }

  /**
   *  set the PaneConstraints for the specified component
   *  paneConstraints.splitComponent is ignored
   *  the components are not redrawn until layoutContainer is called
   */
  public void setConstraints(Component child,PaneConstraints constraints) {
    //System.err.println("PaneLayout.setConstraints " + constraints);
    setComponentConstraints(child,constraints);
    lastDeletion = null;
  }

  /**
   * (Internal) implementation of setConstraints
   * returns false if component is not found
   */
  private boolean setComponentConstraints(Component child,PaneConstraints constraints) {
    if (rootNode == null) {
      //System.err.println("PaneLayout.setComponentConstraints: setting root node to " + constraints.name);
      rootNode = new PaneNode(constraints.name,child,PaneConstraints.ROOT);
    }
    else {
      PaneNode node = rootNode.getParentNode(child,null);
      if (node != null) {
        node.setConstraints(child,constraints);
        //System.err.println("PaneLayout.setComponentConstraints:");
        return true;
      }
      //System.err.println("PaneLayout.setComponentConstraints: did not find the component " + constraints.name);
      return false;
    }
    return true;
  }

  /**
   * returns the PaneConstraints for the specified component
   */
  public PaneConstraints getConstraints(Component comp) {
    PaneConstraints constraints = null;
    if (rootNode != null) {
      PaneNode node = rootNode.getParentNode(comp,null);
      if (node != null) {
        if (node.childComponent == comp)
          constraints = new PaneConstraints(node.name,node.name,"Root",0.5f);  //NORES
         else {
           float proportion = node.heightDivide * node.widthDivide;
           String splitComponentName = node.childNodeA.getNodeAComponent();
           String name = node.childNodeB.getNodeAComponent();
           if (node.childNodeA.childComponent == comp)
             name = node.childNodeB.name;
           if (node.horizontal)
             if (node.reverse)
               constraints = new PaneConstraints(name,splitComponentName,PaneConstraints.TOP,1.0f - proportion);
             else
               constraints = new PaneConstraints(name,splitComponentName,PaneConstraints.BOTTOM,1.0f - proportion);
           else
             if (node.reverse)
               constraints = new PaneConstraints(name,splitComponentName,PaneConstraints.LEFT,1.0f - proportion);
             else
               constraints = new PaneConstraints(name,splitComponentName,PaneConstraints.RIGHT,1.0f -proportion);
           if (name.equals(splitComponentName))
             constraints.position = "Root";  //NORES
         }
      }
    }
    //System.err.println("PaneLayout.getConstraints for " + constraints);
    return constraints;
  }

  /**
   * Returns the String representation of this PaneLayout's values.
   */
  public String toString() {
    return "PaneLayout";  //NORES
  }

  //------------------------------------------------------------------------------------------------
  // LayoutManager Methods
  //------------------------------------------------------------------------------------------------

  /**
   * Removes the specified node from the layout - although not from the container itself
   * a LayoutManager Interface method
   */
  public void removeLayoutComponent(Component comp) {
    if (comp == null) {
      lastDeletion = null;
      return;
    }
    if (rootNode != null) {
      //System.err.println("removing " + getConstraints(comp));
      if (rootNode.childComponent == comp)
        rootNode = null;
      else {
        PaneNode parent = rootNode.getImmediateParent(comp);
        if (parent != null) {
          //System.err.println(parent.childNodeA.name + "-" +parent.childNodeB.name);
          lastDeletion = parent.removeChild(comp);
        }
      }
    }
  }

  /**
   * Returns the preferred dimensions for this layout given the components
   * in the specified Container.
   * a LayoutManager Interface method
   * @param parent the component which needs to be laid out
   * @see #getMinimumSize
   */
  public Dimension preferredLayoutSize(Container parent) {
    if (rootNode != null) {
      Dimension d = rootNode.getPreferredSize(gap);
      //System.err.println("PaneLayout.preferredLayoutSize returns:" + d);
      return  d;
    }
    //System.err.println("PaneLayout.preferredLayoutSize- no components");
    return new Dimension(10, 10);
  }

  /**
   * Returns the minimum dimensions needed to layout the components
   * contained in the specified panel.
   * a LayoutManager Interface method
   * @param parent the component which needs to be laid out
   * @see #getPreferredSize
   */
  public Dimension minimumLayoutSize(Container parent) {
    return preferredLayoutSize(parent);
  }

  /**
   * Lays out the container in the specified Container.
   * a LayoutManager Interface method
   * @param parent the specified component being laid out
   * @see Container
   */
  public void layoutContainer(Container parent) {
    //System.err.println("PaneLayout.layoutContainer");
    if (rootNode != null) {
      Dimension d = parent.getSize();
      Rectangle location = new Rectangle(0, 0, d.width, d.height);
      rootNode.assertLocation(location,gap);
    }
  }

  /**
   * Returns an array of the components in an order that will work
   * called by the UI Designer's PaneLayoutAssistant after components have been
   * moved or deleted
   */
  public String[] getAddOrder(Container parent) {
    Component[] componentArray = parent.getComponents();  //just to get the correct size array
    String[] componentNames = new String[componentArray.length];
    Point subscript = new Point(0,0);  // wrap int in an object whose value the called methods can tweak
    if (rootNode != null) {
      if (rootNode.childComponent == null)
        rootNode.getComponents(subscript,componentNames,true);
      else
        componentNames[0] = rootNode.name;
    }
    return componentNames;
  }

  /**
   * (Internal) Adds a child to the layout into a default position
   */
  void addChild(Component c, float proportion) {
    addCount++;
    String name = "component" + addCount;  //NORES
    //System.err.println("PanelLayout.addChild naming component " + name);
    if (rootNode == null)
      rootNode = new PaneNode(name, c, PaneConstraints.TOP);
    else
      rootNode.addChild(name, c, PaneConstraints.BOTTOM, proportion);  //NORES
    lastComponentAdded = name;
  }

  /**
   * (SplitPanel specific) Move the divider the amount specified
   *  One paramter will be zero
   */
  public void dragDivider(int x, int y) {
    if (lastSelected != null) {
      //System.err.println("Drag" + lastSelected.childNodeA.location + lastSelected.childNodeB.location  + lastSelected.location);
      lastSelected.drag(x,y);
    }
  }

  /**
   * (SplitPanel specific) returns the rectangle that the divider can be moved in
   */
  public Rectangle getDividerBounds() {
    if (lastSelected != null) {
      // System.err.println("Bounds" + lastSelected.childNodeA.location + lastSelected.childNodeB.location  + lastSelected.location);
      return lastSelected.location;
    }
    return null;
  }

  /**
   * (SplitPanel specific) determines which divider contains the supplied point
   *  and returns that dividers rectangle
   */
  public Rectangle getDividerRect(int x, int y) {
    if (rootNode != null)
      lastSelected = rootNode.hitTest(x,y,gap*2);
    if (lastSelected != null) {
      //System.err.println("Divides" + lastSelected.childNodeA.location + lastSelected.childNodeB.location  + lastSelected.location);
      return lastSelected.getDividerRect(gap*2);
    }
    return null;
  }

  //never figured out what this was for, cause I don't use it anymore
  //public void deselectAll() {
  //   if (rootNode != null)
  //     rootNode.deselectAll();
  // }

  /**
   * (Internal) Adds a child into the layout by splitting the specified component (assumed to
   * be already present) in the manner specified (ie horizontal or vertical)
   */
  void addChild(String name,String splitComponentName, String position, Component newComponent, float proportion) {
    if (rootNode == null) {
      //System.err.println("adding " + name + " as the root node");
      rootNode = new PaneNode(name, newComponent, PaneConstraints.TOP);
    }
    else {
      boolean foundSplitComponent = true;
      if (splitComponentName == null || splitComponentName.length() == 0) {
        //System.err.println("split with " + lastComponentAdded + " instead");
        foundSplitComponent = rootNode.addChildSplit(name, lastComponentAdded, position, newComponent, proportion);
      }
      else
        if (!rootNode.addChildSplit(name,splitComponentName, position, newComponent, proportion))
          foundSplitComponent = rootNode.addChildSplit(name, lastComponentAdded, position, newComponent, proportion);
      if (!foundSplitComponent) {
        //System.err.println("addChild never found " + splitComponentName);
        rootNode.addChild(name, newComponent, position, proportion);
      }
    }
  }

  //mark the node that this divider divides as needing re-layout
  //(the layout remembers the lastSelected PaneNode instead)
  // public void select(boolean shift, int x, int y) {
  //   if (rootNode != null)
  //     rootNode.select(shift,x,y,gap);
  // }

  public void addLayoutComponent(String name, Component comp) {
    PaneConstraints pc = new PaneConstraints(name, "", "", 0.5f);
    addLayoutComponent(comp,pc);
  }

  //------------------------------------------------------------------------------------------------
  // LayoutManager2 methods
  //------------------------------------------------------------------------------------------------

  /**
   * add the specified component using the supplied PaneConstraints
   */
  public void addLayoutComponent(Component newComponent, Object constraints) {
    //try {
    //   String s=null;
    //   s.substring(0);
    //  }
    // catch (Exception e) {MWC.Utilities.Errors.Trace.trace(e);}
    //System.err.println("adding Component" + constraints);
    if (constraints instanceof PaneConstraints) {
      if (justDeleted(newComponent,(PaneConstraints)constraints));
      //System.err.println("ignored add for " + ((PaneConstraints)constraints).name);
      else if (setComponentConstraints(newComponent,(PaneConstraints)constraints)) { //make sure it has not already been added
        //System.err.println("this is ok; " + ((PaneConstraints)constraints).name + " already in containter");
        lastDeletion = null;
        return;
      }
      else {
        PaneConstraints paneConstraints = (PaneConstraints) constraints  ;
        //System.err.println("adding " + paneConstraints.name);
        addChild(paneConstraints.name,paneConstraints.splitComponentName,paneConstraints.position,newComponent,paneConstraints.proportion);
        lastComponentAdded = paneConstraints.name;
      }
    }
    else
      //if (!(newComponent instanceof PaneLayoutDivider))
      addChild(newComponent,0.5f);
    lastDeletion = null;
    //System.err.println("added" + getConstraints(newComponent));
  }

  public Dimension maximumLayoutSize(Container parm1) {
    return new Dimension(500,500);
  }

  public float getLayoutAlignmentX(Container parm1) {
     return 0.5f;
  }

  public float getLayoutAlignmentY(Container parm1) {
    return 0.5f;
  }

  public void invalidateLayout(Container parm1) {
  }

  /**
   * (Internal) helper function for addLayoutComponent
   * checks to see if the newComponent was just removed from the layout
   * and if it was, places it back were it once belonged.
   * The UI designer does this (removeLayoutComponent immediately followed by AddLayoutComponent)
   * because the preferred method, setConstraints, is not part of the LayoutManager2 interface
   */
  boolean justDeleted(Component newComponent, PaneConstraints constraints) {
    if (lastDeletion == null)
      return false;
    if (lastDeletion.childComponent == newComponent) {
      if (lastDeletion.childNodeA == null) {
        PaneNode node = lastDeletion.childNodeB;
        if (node == null) {
          //System.err.println("justDeleted: node is null");
          return false;
        }
        //System.err.println(constraints.splitComponentName +"," + node.getNodeAComponent());
        if (constraints.splitComponentName.equals(node.getNodeAComponent()) ||
             !lastDeletion.name.equals(constraints.name)) {
          //System.err.println("lastDeletion restored as nodeB " +constraints.name);
          PaneNode newNode = new PaneNode(node.childNodeA,node.childNodeB,"",0.5f);
          newNode.widthDivide = node.widthDivide;
          newNode.heightDivide = node.heightDivide;
          newNode.name = node.name;
          lastDeletion.name = constraints.name;
          newNode.childComponent  = node.childComponent;
          newNode.reverse = node.reverse;
          newNode.horizontal = node.horizontal;
          node.childNodeA = newNode;
          node.childNodeB = lastDeletion;
          node.name = null;
          node.childComponent = null;
          lastDeletion = null;
          setConstraints(newComponent,constraints);
          return true;
        }
      }
      else {
        //System.err.println("lastDeletion restored as nodeA " +constraints.name);
        PaneNode node = lastDeletion.childNodeA;
        //System.err.println(constraints.splitComponentName +"," + node.getNodeAComponent());
        // verify that split component constraint did not change
        //if (constraints.splitComponentName.equals(node.getNodeAComponent())) {
          PaneNode newNode = new PaneNode(node.childNodeA,node.childNodeB,"",0.5f);
          newNode.widthDivide = node.widthDivide;
          newNode.heightDivide = node.heightDivide;
          newNode.name = node.name;
          lastDeletion.name = constraints.name;
          newNode.childComponent  = node.childComponent;
          newNode.reverse = node.reverse;
          newNode.horizontal = node.horizontal;
          node.childNodeA = lastDeletion;
          node.childNodeB = newNode;
          node.name = null;
          node.childComponent = null;
          lastDeletion = null;
          setConstraints(newComponent,constraints);
          return true;
        //}
      }
      //System.err.println("justdeleted = false");
      lastDeletion = null;
    }
    return false;
  }
}

class PaneNode
{
  //public final static int Border = 3;

  // Contents: either one Component or two PaneNodes
  // Determined by the presence or absense of a value in
  // childComponent.
  Component childComponent;
  String name;
  // or
  PaneNode childNodeA;
  PaneNode childNodeB;

  // Location
  Rectangle location = new Rectangle();

  // When this object is a tree node, need to work out
  // how to split the space between the two children
  // either (x,1), or (1,y) where x, y may be one
  float widthDivide;
  float heightDivide;

  int xOffset;
  int yOffset;

  // Are children distributed horizontally or vertically?
  boolean horizontal;
  // Is NodeA on the top/left or bottom/right?
  boolean reverse = false;

  // Is the node selected?
  //boolean selected = false;

  public PaneNode(String childName,Component child, String position) {
    childComponent = child;
    name = childName;
    widthDivide = 1.0f;
    heightDivide = 0.5f;
    horizontal = position.equals(PaneConstraints.TOP) || position.equals(PaneConstraints.BOTTOM);  //NORES
    reverse = position.equals(PaneConstraints.TOP) || position.equals(PaneConstraints.LEFT);  //NORES
  }

  public PaneNode(PaneNode childA, PaneNode childB, String position, float proportion) {
    childNodeA = childA;
    childNodeB = childB;
    if (position.equals(PaneConstraints.TOP) || position.equals(PaneConstraints.BOTTOM)) {
      widthDivide = 1.0f;     //NORES
      heightDivide = proportion;
      horizontal = true;
    }
    else {
      widthDivide = proportion;
      heightDivide = 1.0f;      //NORES
      horizontal = false;
    }
    if (position.equals(PaneConstraints.TOP) || position.equals(PaneConstraints.LEFT))
      reverse = true;
    else
      reverse = false;
  }

  void dump() {
    //Diagnostic.println("childComponent:" + childComponent);   //NORES
    //Diagnostic.println("childNodeA:" + childNodeA);           //NORES
    //Diagnostic.println("childNodeB:" + childNodeB);           //NORES
  }

  public void setConstraints(Component child,PaneConstraints constraints) {
    if (childComponent != null)
      return;
    if (constraints.position.equals(PaneConstraints.TOP) ||
        constraints.position.equals(PaneConstraints.LEFT))
      reverse = true;
    else
      reverse = false;
    if (constraints.position.equals(PaneConstraints.BOTTOM)) {
      heightDivide =  1.0f - constraints.proportion;
      widthDivide = 1.0f;
      horizontal =  true;
    }
    else if (constraints.position.equals(PaneConstraints.TOP)) {
      heightDivide = 1.0f - constraints.proportion;
      widthDivide = 1.0f;
      horizontal =  true;
    }
    else if (constraints.position.equals(PaneConstraints.RIGHT)) {
      widthDivide = 1.0f - constraints.proportion;
      heightDivide = 1.0f;
      horizontal =  false;
    }
    else if (constraints.position.equals(PaneConstraints.LEFT)) {
      widthDivide = 1.0f - constraints.proportion;
      heightDivide = 1.0f;
      horizontal =  false;
    }
  }

  public void addChild(String childName, Component child, String position, float proportion) {
    // Only two cases: either
    // a) node contains a single component => split it
    // b) node contains two sub nodes => pass the component on
    if (childComponent != null) {
      // Option a).  Split the node
      // Steps:
      // i)   Create a new node to contain the component from this node
      //System.err.println("Splitting " + name + " with " + childName);
      childNodeA = new PaneNode(name,childComponent, PaneConstraints.ROOT);
      childComponent = null;
      name = null;
      // ii)  Put new node into the other child slot
      childNodeB = new PaneNode(childName,child, PaneConstraints.ROOT);
      // iii) Split as instructed
      proportion = 1.0f - proportion;
      if (position.equals(PaneConstraints.RIGHT) || position.equals(PaneConstraints.BOTTOM))
         reverse = false;
      else
        reverse = true;
      if (position.equals(PaneConstraints.LEFT) || position.equals(PaneConstraints.RIGHT)) {
        widthDivide = proportion;
        heightDivide = 1.0f;
        horizontal = false;
      }
      else {
        widthDivide = 1.0f;
        heightDivide = proportion;
        horizontal = true;
      }
      //Diagnostic.println("addChild "+splitHorizontal+"  widthD:"+widthDivide+" heightD:"+heightDivide);
    }
    else {
      // Option b). Pass the component in to the second node
      if (horizontal)
        position = PaneConstraints.RIGHT;
      else
        position = PaneConstraints.BOTTOM;
      childNodeB.addChild(childName,child, position, proportion);
    }
  }

  public boolean addChildSplit(String childName,String splitComponent, String position, Component newComponent, float proportion) {
    // Split this one?
    if (childComponent != null) {
      if (name != null)
        if (name.equals(splitComponent)) {
          // cheat by using the other code
          addChild(childName,newComponent, position, proportion);
          return true;
        }
        else
          return false;
    }
    else {
      // try the children
      if (!childNodeA.addChildSplit(childName, splitComponent, position, newComponent, proportion))
        return childNodeB.addChildSplit(childName, splitComponent, position, newComponent, proportion);
      return true;
    }
    return false;
  }

  public Dimension getPreferredSize(int gap) {
    // is This one split?
    Dimension d = null;
    try {
      if (childComponent != null) {
        d = childComponent.getPreferredSize();
        //System.err.println("preferred size of:" + name + "="+ d);
        return d;
      }

      Dimension a = childNodeA.getPreferredSize(gap);
      Dimension b = childNodeB.getPreferredSize(gap);

      float divisor = widthDivide;
      if (!horizontal) {
        if (divisor >= 0.999)
          d = a;
        if (divisor == 0.0)
          d = b;
        else
          d =  new Dimension((int)Math.max(((float)a.width)/divisor,((float)b.width)/(1.- divisor)),Math.max(a.height,b.height));
        d.width += gap + gap + 1;
      }
      else {
        divisor = heightDivide;

        if (divisor >= 0.999)       //NORES
          d = a;
        else
          if (divisor == 0.0)        //NORES
            d =  b;
          else
            d = new Dimension(Math.max(a.width,b.width),(int)Math.max(((float)a.height)/divisor,((float)b.height)/(1.- divisor)));
        d.height += gap + gap + 1;
      }
      //System.err.println("PaneNode  has preferred size of:" + d + " divisor=" + divisor + " " +heightDivide +" " + widthDivide);
    }
    catch(Exception e) {
      MWC.Utilities.Errors.Trace.trace(e);
    }
    return d;
  }

  /**
   * return the first NodeA that is a childComponent
   */
  public String getNodeAComponent() {
    //System.err.println("getNodeAcomponent");
    if (childComponent != null)  {
      //System.err.println("found " + name);
      return name;
    }
    //if (childNodeA.childComponent != null)
    //  return childNodeA.name;
    else
      return childNodeA.getNodeAComponent();
  }

  /**
   * returns the node that points to the child leaf
   */
  public PaneNode getImmediateParent(Component child) {
    if (childComponent != null) {
      if (childComponent == child) {
        return this;
      }
      else
        return null;
    }
    if (childNodeA.childComponent == child)
      return this;
    else
      if (childNodeB.childComponent == child)
        return this;
    // Check ChildNodeA
    PaneNode  rc = childNodeA.getImmediateParent(child);
    if (rc != null)
      return rc;
    else
      return childNodeB.getImmediateParent(child);
  }
  void getComponents(Point subscript,String[] componentArray,boolean parentIsNodeB){
    if (childComponent == null) {
      if (parentIsNodeB){
        componentArray[subscript.x] = childNodeA.getNodeAComponent();
        //System.err.println("addA at " + subscript.x + " - " + componentArray[subscript.x]);
        subscript.x++;
      }
      componentArray[subscript.x] = childNodeB.getNodeAComponent();

      //System.err.println("addB at " + subscript.x +parentIsNodeB + " - " + componentArray[subscript.x]);
      subscript.x++;
      childNodeA.getComponents(subscript,componentArray,false);

      childNodeB.getComponents(subscript,componentArray,false);

    }

  }
   /**
   * returns the lowest node that points to both the component and the component
   * that it is split from
   */
  public PaneNode getParentNode(Component child,PaneNode lastBNode) {
    // End the recursion if we have a leaf (ie childComponent != null)
    if (childComponent != null) {
      if (childComponent == child) {
        //System.err.println("not split with anyone??");
        return this;
      }
      return null;
    }
    else
      if (childNodeA.childComponent == child)
        if (lastBNode != null)
          return lastBNode;
        else
          return childNodeA; //the root component
      else
        if (childNodeB.childComponent == child)
          return this;
    // Check ChildNodeA
    PaneNode  resultA = childNodeA.getParentNode(child,lastBNode);
    if (resultA != null)
      return resultA;
    else
      return childNodeB.getParentNode(child,this);
  }

  /**
   * This method removes a given node.
   * must already be positioned on the parent of the node to be removed
   */
  public PaneNode removeChild(Component child) {
    PaneNode rc = null;
    // Check ChildNodeA
    if (childNodeA.childComponent == child) {
      //System.err.println("removing A"+childNodeA.name);
      rc = childNodeA;
      absorbChildNode(childNodeB);
      //remember enough stuff so we can reconstruct the removed node
      rc.childNodeA = this;
      rc.childNodeB = null;
    }
    else if (childNodeB.childComponent == child) {
      //System.err.println("removing B"+childNodeB.name);
      rc = childNodeB;
      absorbChildNode(childNodeA);
      rc.childNodeA = null;
      rc.childNodeB = this;
    }
    //else
    //  System.err.println("did not find the component ");
    return rc;
  }

  Rectangle getDividerRect(int gap) {
    if (childComponent == null) {
      if (heightDivide == 1.0f) {
        if (reverse)
          return new Rectangle(childNodeB.location.x + childNodeB.location.width,
                               childNodeB.location.y, gap , childNodeB.location.height);
        else
          return new Rectangle(childNodeA.location.x + childNodeA.location.width,
                               childNodeA.location.y, gap , childNodeA.location.height);
      }
      else {
        if (reverse)
          return new Rectangle(childNodeB.location.x,
                               childNodeB.location.y + childNodeB.location.height, childNodeB.location.width, gap);
        else
          return new Rectangle(childNodeA.location.x,
                               childNodeA.location.y + childNodeA.location.height, childNodeA.location.width, gap);
      }
    }
    return new Rectangle(location.x - gap, yOffset + location.y - gap, location.width + gap, gap);
  }

  //void deselectAll() {
  //  if (childComponent == null) {
  //    selected = false;
  //    childNodeA.deselectAll();
  //    childNodeB.deselectAll();
  //  }
  //}

  /*
  boolean select(boolean shift, int x, int y, int gap) {
    // Make sure this is a node, not a leaf
    if (childComponent == null) {
      int offset;
      if (heightDivide == 1.0f)
        offset = x - (location.x + xOffset);
      else
        offset = y - (location.y + yOffset);

      if (Math.abs(offset) <= gap) {
        if (shift)
          selected = !selected;
        else
          selected = true;

        return true;
      }
      else {
        if (offset < 0 ^ reverse)
          return childNodeA.select(shift, x, y, gap);
        else
          return childNodeB.select(shift, x, y, gap);
      }
    }
    return false;
  }
   */

  void drag(int x, int y) {
     boolean goodDrag = true;

    if (childComponent == null) {
      //if (selected) {
        if (heightDivide == 1.0f) {
          // if (reverse)
          //   widthDivide = 1.0f - widthDivide ;
          //widthDivide += (float)dx / (float)location.width;
          widthDivide = (float)(x - location.x) / (float)location.width;
           if (reverse)
             widthDivide = 1.0f - widthDivide ;
          if (widthDivide < (float)0){
           widthDivide = (float)0;
            goodDrag = false;
          }
          else
          if (widthDivide >= 1.0f) {
             widthDivide = 0.999f;
             goodDrag = false;
          }
        }
        else {
           if (reverse)
             heightDivide = 1.0f - heightDivide ;
         //heightDivide += (float)dy / (float)location.height;
          heightDivide = (float) (y - location.y) /(float) location.height;
          if (reverse)
            heightDivide = 1.0f - heightDivide ;
          if (heightDivide < (float)0){
            goodDrag = false;
            heightDivide = (float)0;
          }
          else

          if (heightDivide >= 1.0f){
            goodDrag = false;
            heightDivide = 0.999f;
          }
        }
      //}
    }
  }

  PaneNode hitTest(int x, int y, int gap) {
    // Make sure this is a node, not a leaf
    if (location != null && childComponent == null) {
      int offset;
      if (heightDivide == 1.0f)
        offset = x - (location.x + xOffset);
      else
        offset = y - (location.y + yOffset);

      if (Math.abs(offset) <= gap) {
        return this;
      }
      else {
        //if (heightDivide == 1.0f)
        //  System.err.println("not on X " + (location.x + xOffset));
        // else
        //  System.err.println("not on Y " + (location.y + yOffset));
        if (offset < 0 ^ reverse)
          return childNodeA.hitTest(x, y, gap);
        else
          return childNodeB.hitTest(x, y, gap);
      }
    }
    return null;
  }

  void assertLocation(Rectangle locationInit,int gap) {
    location.x = locationInit.x;
    location.y = locationInit.y;
    location.width = locationInit.width;
    location.height = locationInit.height;
    //Diagnostic.println("    Node: " + location.toString() + " child:" + childComponent);
    if (childComponent != null) {
      if (!childComponent.getBounds().equals(location)) {
        //System.err.println("reshape " +  name + location);
        childComponent.setBounds(location.x, location.y, location.width, location.height);
        if (childComponent instanceof Container)
          ((Container)childComponent).doLayout();
      }
    }
    else {
      Rectangle childALocation = new Rectangle();
      Rectangle childBLocation = new Rectangle();

      calculateLocations(location, childALocation, childBLocation, gap);

      childNodeA.assertLocation(childALocation, gap);
      childNodeB.assertLocation(childBLocation, gap);
    }
  }

  void absorbChildNode(PaneNode child) {
    //child is going away - immediate parent is adjusted so it looks just like child
    if (child.childComponent != null) {
      childComponent = child.childComponent;
      name = child.name;
    }
    else {
      childComponent = null;
      childNodeA = child.childNodeA;
      childNodeB = child.childNodeB;
      widthDivide = child.widthDivide;
      heightDivide = child.heightDivide;
      reverse = child.reverse;
      horizontal = child.horizontal;
    }
  }

  void calculateLocations(Rectangle location,
                          Rectangle childALocation,
                          Rectangle childBLocation,
                          int Border) {
    //Diagnostic.println("CALC horizontal:"+horizontal+" widthD:"+widthDivide+" heightD:"+heightDivide+" loc:"+location);
    //Diagnostic.println("  xOffset:"+xOffset+"  yOffset:"+yOffset);
    Border = Border + Border;

    if (heightDivide == 1.0f) {
      float proportion = widthDivide;
      if (reverse)
        proportion = 1.0f - proportion;

      // Work out the location of the two children
      xOffset = (int)((float)(location.width -Border)* proportion);

      childALocation.x = location.x;
      childALocation.y = location.y;
      childALocation.width = xOffset ;
      childALocation.height = location.height;

      childBLocation.x = location.x + xOffset + Border ;
      childBLocation.y = location.y;
      childBLocation.width = location.width - xOffset - Border  ;
      childBLocation.height = location.height;

    }
    else {
      float proportion = heightDivide;
      if (reverse)
        proportion = 1.0f - proportion;
      // Work out the location of the two children
      yOffset = (int)((float)(location.height -Border) * proportion);
      childALocation.x = location.x;
      childALocation.y = location.y;
      childALocation.width = location.width;
      childALocation.height = yOffset ;

      childBLocation.x = location.x ;
      childBLocation.y = location.y + yOffset + Border;
      childBLocation.width = location.width;
      childBLocation.height =  location.height - yOffset - Border;

    }
    if (reverse) {
      //System.err.println("reverse");
      Rectangle temp =  new Rectangle(childALocation.x,childALocation.y,
                                      childALocation.width,childALocation.height);
      childALocation.x = childBLocation.x;
      childALocation.y = childBLocation.y;
      childALocation.width = childBLocation.width;
      childALocation.height = childBLocation.height ;

      childBLocation.x = temp.x ;
      childBLocation.y = temp.y ;
      childBLocation.width = temp.width;
      childBLocation.height =  temp.height ;
    }
    //System.err.println("CHILDA:"+childALocation+"  CHILDB:"+childBLocation);
  }
}
