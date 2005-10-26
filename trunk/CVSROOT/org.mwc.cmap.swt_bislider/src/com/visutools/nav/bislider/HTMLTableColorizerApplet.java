package com.visutools.nav.bislider;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JApplet;
import javax.swing.JPopupMenu;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
/**
 * HTMLTableColorizerApplet.java
 * 5 examples of BiSlider in a JFrame to see the widget at work.
 * <br><br>
 * <table border=1 width = "90%">
 *   <tr>
 *     <td>
 *       Copyright 1997-2005 Frederic Vernier. All Rights Reserved.<br>
 *       <br>
 *       Permission to use, copy, modify and distribute this software and its documentation for educational, research and
 *       non-profit purposes, without fee, and without a written agreement is hereby granted, provided that the above copyright
 *       notice and the following three paragraphs appear in all copies.<br>
 *       <br>
 *       To request Permission to incorporate this software into commercial products contact
 *       Frederic Vernier, 19 butte aux cailles street, Paris, 75013, France. Tel: (+33) 871 747 387.
 *       eMail: Frederic.Vernier@laposte.net / Web site: http://vernier.frederic.free.fr
 *       <br>
 *       IN NO EVENT SHALL FREDERIC VERNIER BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 *       DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF FREDERIC
 *       VERNIER HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.<br>
 *       <br>
 *       FREDERIC VERNIER SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *       MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HERE UNDER IS ON AN "AS IS" BASIS, AND
 *       FREDERIC VERNIER HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.<br>
 *     </td>
 *   </tr>
 * </table>
 * <br>
 * <b>Dates:</b><br>
 *   <li>Creation    : October 15, 2005<br>
 *   <li>Format      : 15/10/2005 <br>
 *   <li>Last Modif  : 19/10/2005 <br>
 *<br>
 * <b>Bugs:</b><br>
 * <li><br>
 *<br>
 * <b>To Do:</b><br>
 *  <li><br>
 *<br>
 * @author Frederic Vernier, Frederic.Vernier@laposte.net
 * @version 1.4.1
 **/

public class HTMLTableColorizerApplet extends JApplet {

  // The code will parse a HTML table we need to know the range and the name
  protected int MinColIndex = 0;
  protected int MaxColIndex = 0;
  protected int MinRowIndex = 0;
  protected int MaxRowIndex = 0;
  protected String LinkToTableString = "";
  
  // let's share the distribution array as well
  protected double[][] DistributionArray = null;
  protected double[][] ValueArray        = null;
  protected JSObject[][] CellArray       = null; 
  protected int BiggestClassIndex        = 0;
  protected DecimalFormat DecimalFormat1 = null;
  protected Locale Language              = null;
  protected boolean Normalize            = false;
  protected int ColNormalize             = -1;
  protected double[] NormalRows          = null;
  

  /** 
   * parse a html color name and return a java <code>Color</code>
   */
  private Color parseColorStr(String ColorName) {
    int r, g, b;
    // Convert a string in the standard HTML "#RRBBGG" format to a valid
    // color value, if possible.
    if (ColorName.length() == 7 && ColorName.charAt(0) == '#') {
      try {
        r = Integer.parseInt(ColorName.substring(1,3),16);
        g = Integer.parseInt(ColorName.substring(3,5),16);
        b = Integer.parseInt(ColorName.substring(5,7),16);
        return(new Color(r, g, b));
      }
      catch (Exception Exception_Arg) {
        Exception_Arg.printStackTrace();
      }
    }
    // Otherwise, default to black.
    return(Color.BLACK);
  }// parseColorStr()
 
 
  /** Creates a new instance of HTMLTableColorizerApplet */
  public HTMLTableColorizerApplet() {
  }
  
  
  /**
   * standard method to return info about the applet
   **/
  public String getAppletInfo(){
    return "BiSlider applet display a BiSlider bean to control an HTML table";
  }  
  
  
  /**
   * entry point fot the applet
   */
  public void init() {
    // get parameters from the applet param tags if exist
    String MinimumColorString        = getParameter("MinimumColor");
    String MaximumColorString        = getParameter("MaximumColor");
    String MinimumValueString        = getParameter("MinimumValue");    
    String MaximumValueString        = getParameter("MaximumValue");    
    String MinimumColoredValueString = getParameter("MinimumColoredValue");    
    String MaximumColoredValueString = getParameter("MaximumColoredValue");    
    
    String ArcSizeString             = getParameter("ArcSize");    
    String UniformSegmentString      = getParameter("UniformSegment");    
    String SegmentSizeString         = getParameter("SegmentSize");    
    String InterpolationModeString   = getParameter("InterpolationMode");    
    
    String CustomPaintString         = getParameter("CustomPaint");    
           LinkToTableString         = getParameter("LinkToTable");    
    String TableWidthString          = getParameter("TableWidth");    
    String TableHeightString         = getParameter("TableHeight");  
    
    String DecimalFormatString       = getParameter("DecimalFormat");  
    String LanguageString            = getParameter("Language");  
    if (LanguageString!=null)
      Language = new Locale(LanguageString);
    
    String MinColIndexString         = getParameter("MinColIndex");
    String MaxColIndexString         = getParameter("MaxColIndex");
    String MinRowIndexString         = getParameter("MinRowIndex");
    String MaxRowIndexString         = getParameter("MaxRowIndex");
    
    String PreciseString             = getParameter("Precise");    
    String UnitString                = getParameter("Unit");       
    
    String ColNormalizeString        = getParameter("ColNormalize");    
    if (ColNormalizeString!=null)
      try {
        ColNormalize = Integer.parseInt(ColNormalizeString);
        Normalize = true;
      } catch (NumberFormatException NumberFormatException_Arg) {
        NumberFormatException_Arg.printStackTrace();
      }      
      
    
    if (DecimalFormatString  !=null)
      DecimalFormat1 = new DecimalFormat(DecimalFormatString);
    
    final BiSlider BiSliderWeb = new BiSlider(BiSlider.CENTRAL_BLACK);
    if (InterpolationModeString!=null && InterpolationModeString.toLowerCase().equals("hsb"))
      BiSliderWeb.setInterpolationMode(BiSlider.HSB);
    else if (InterpolationModeString!=null && InterpolationModeString.toLowerCase().equals("rgb"))
      BiSliderWeb.setInterpolationMode(BiSlider.RGB);
    
    //-----------------------------------------------------------------------
    if (UniformSegmentString!=null)
      try {
        BiSliderWeb.setUniformSegment(Boolean.parseBoolean(UniformSegmentString));
      } catch (NumberFormatException ex) {
        ex.printStackTrace();
      }
    else    
      BiSliderWeb.setUniformSegment(true);
    //-----------------------------------------------------------------------
    BiSliderWeb.setVisible(true);
    //-----------------------------------------------------------------------
    if (MinimumValueString!=null)
      try {
        BiSliderWeb.setMinimumValue(Integer.parseInt(MinimumValueString));
      } catch (NumberFormatException NumberFormatException_Arg) {
        NumberFormatException_Arg.printStackTrace();
      }
    else
      BiSliderWeb.setMinimumValue(0);
    //-----------------------------------------------------------------------
    if (MaximumValueString!=null)
      try {
        BiSliderWeb.setMaximumValue(Integer.parseInt(MaximumValueString));
      } catch (NumberFormatException ex) {
        ex.printStackTrace();
      }
    else
      BiSliderWeb.setMaximumValue(100);
    //-----------------------------------------------------------------------
    if (SegmentSizeString!=null)
      try {
        BiSliderWeb.setSegmentSize(Integer.parseInt(SegmentSizeString));
      } catch (NumberFormatException ex) {
        ex.printStackTrace();
      }
    else
      BiSliderWeb.setSegmentSize(40);
    //-----------------------------------------------------------------------
    if (MinimumColorString!=null)
      BiSliderWeb.setMinimumColor(parseColorStr(MinimumColorString));
    else
      BiSliderWeb.setMinimumColor(Color.RED);
    //-----------------------------------------------------------------------
    if (MaximumColorString!=null)
      BiSliderWeb.setMaximumColor(parseColorStr(MaximumColorString));
    else
      BiSliderWeb.setMaximumColor(Color.BLUE);
    //-----------------------------------------------------------------------
    if (MinimumColoredValueString!=null)
      try {
        BiSliderWeb.setColoredValues(Integer.parseInt(MinimumColoredValueString), Integer.parseInt(MinimumColoredValueString));
      } catch (NumberFormatException ex) {
        ex.printStackTrace();
      }
    else
      BiSliderWeb.setColoredValues(0, 100);
    //-----------------------------------------------------------------------
    if (MaximumColoredValueString!=null)
      try {
        BiSliderWeb.setColoredValues(BiSliderWeb.getMinimumColoredValue(), Integer.parseInt(MaximumColoredValueString));
      } catch (NumberFormatException ex) {
        ex.printStackTrace();
      }
    else
      BiSliderWeb.setColoredValues(BiSliderWeb.getMinimumColoredValue(), BiSliderWeb.getMinimumColoredValue()+100);  

    //-----------------------------------------------------------------------    
    if (UnitString!=null)
      BiSliderWeb.setUnit(UnitString);
    else
      BiSliderWeb.setUnit("");
    BiSliderWeb.setBackground(Color.WHITE);

    //-----------------------------------------------------------------------    
    if (PreciseString!=null)
      try {
        BiSliderWeb.setPrecise(Boolean.parseBoolean(PreciseString));
      } catch (NumberFormatException ex) {
        ex.printStackTrace();
      }
    else    
      BiSliderWeb.setPrecise(true);
    
    //-----------------------------------------------------------------------
    if (ArcSizeString!=null)
      try {
        BiSliderWeb.setArcSize(Integer.parseInt(ArcSizeString));
      } catch (NumberFormatException ex) {
        ex.printStackTrace();
      }
    else
      BiSliderWeb.setArcSize(0);

    //-----------------------------------------------------------------------    
    try {
      if (CustomPaintString!=null && Boolean.parseBoolean(CustomPaintString)) {    
        if (LinkToTableString!=null) {
          MinColIndex = Integer.parseInt(MinColIndexString);
          MaxColIndex = Integer.parseInt(MaxColIndexString);
          MinRowIndex = Integer.parseInt(MinRowIndexString);
          MaxRowIndex = Integer.parseInt(MaxRowIndexString);
          
          BiSliderWeb.setToolTipText("The background is the histogram of the HTML cells");
          
          initHTMLLink();
          updateHTMLLink(BiSliderWeb);
        }
        else 
          BiSliderWeb.setToolTipText("The background is a customizable histogram");    

        BiSliderWeb.addContentPainterListener(new ContentPainterListener(){
          public void paint(ContentPainterEvent ContentPainterEvent_Arg){
            Graphics2D Graphics2 = (Graphics2D)ContentPainterEvent_Arg.getGraphics();

            int index = ContentPainterEvent_Arg.getSegmentIndex();
            double Rand = 1.0;
            Rectangle Rect1 = ContentPainterEvent_Arg.getRectangle();
            Rectangle Rect2 = ContentPainterEvent_Arg.getBoundingRectangle();

            // normal distribution
            if (DistributionArray!=null) {
              Rand = 1-(DistributionArray[index][2]/DistributionArray[BiggestClassIndex][2]);
            } else {
              float X = ((float)Rect2.x-BiSliderWeb.getWidth()/2)/BiSliderWeb.getWidth()*6;
              Rand = 1-Math.exp((-1*X*X)/2);
            }

            if (ContentPainterEvent_Arg.getColor()!=null) {
              Graphics2.setColor(BiSliderWeb.getSliderBackground().darker());
              Graphics2.fillRect(Rect2.x, Rect2.y, Rect2.width, (int)((Rand*Rect2.height)));
              Graphics2.setColor(ContentPainterEvent_Arg.getColor());
              Graphics2.fillRect(Rect2.x, Rect2.y+(int)((Rand*Rect2.height)), Rect2.width-1, (int)(((1-Rand)*Rect2.height)));
            }
            Graphics2.setColor(Color.BLACK);
            Graphics2.drawRect(Rect2.x, Rect2.y+(int)((Rand*Rect2.height)), Rect2.width-1, (int)(((1-Rand)*Rect2.height)));
          }
        });
      }
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
    }
    
    if (LinkToTableString!=null) {
      final JApplet TheApplet = this;
      BiSliderWeb.addColorisationListener(new ColorisationListener() {
        public void newColors(ColorisationEvent ColorisationEvent_Arg) {
          updateHTMLLink(BiSliderWeb);
        }
      });
    }
    
    final JPopupMenu JPopupMenu6 = BiSliderWeb.createPopupMenu();
    BiSliderWeb.addMouseListener(new MouseAdapter(){
      public void mousePressed(MouseEvent MouseEvent_Arg){
        if (MouseEvent_Arg.getButton()==MouseEvent.BUTTON3){
          JPopupMenu6.show(BiSliderWeb, MouseEvent_Arg.getX(), MouseEvent_Arg.getY());
        }
      }
    });
    
    setLayout(new BorderLayout());
    add(BiSliderWeb, BorderLayout.CENTER);
  }// init()
  

  protected JSObject JSTable    = null;
  protected JSObject JSWindow   = null;
  
  
  /**
   * initialize the Java-Javascript bridge
   */
  public void initHTMLLink(){
    JSObject JSDocument = null;
    String JSBrowerName = null;
    
    try {
      JSWindow     =            JSObject.getWindow(this);
      JSDocument   = (JSObject) JSWindow.getMember("document");    
      JSBrowerName = (String)   JSWindow.eval("window.navigator.appName.toLowerCase()");
    } catch (JSException JSException_Arg){
      JSException_Arg.printStackTrace();
      System.out.println("ex"+JSException_Arg.getWrappedExceptionType()+"="+JSException_Arg.getMessage());
    }

    String BrowserName  = System.getProperty("browser");
    //if (JSBrowerName.startsWith("netscape")) ...

    try {
      JSTable = (JSObject) JSWindow.eval("document.getElementById('"+LinkToTableString+"')");
    } catch (JSException JSException_Arg){
      JSException_Arg.printStackTrace();
      System.out.println("ex"+JSException_Arg.getWrappedExceptionType()+"="+JSException_Arg.getMessage());
    }       
  }// initHTMLLink()
  
  
  /**
   * update the distribution histogram and paint the HTML table
   */
  public void updateHTMLLink(BiSlider BiSlider_Arg){   
    Colorizer Colorizer1 = BiSlider_Arg.getColorizer();    
    
    if (Normalize)
      NormalRows      = new double[MaxRowIndex-MinRowIndex+1];
    
    if(ValueArray==null){
      ValueArray        = new double[MaxColIndex-MinColIndex+1][MaxRowIndex-MinRowIndex+1];
      CellArray         = new JSObject[MaxColIndex-MinColIndex+1][MaxRowIndex-MinRowIndex+1];

      // update the ValueArray going through the table
      try {
        // The user chose to indicate the full html table with <table id="MyTableName"...> 
        if (JSTable!=null)
          updateHTMLLinkFullTable(JSTable);
        // The user chose to indicate the html table cell by cell with <td id="MyTableName1-1"...> ...<td id="MyTableName1-2"...> 
        else 
          updateHTMLLinkCellByCell(JSWindow);

      } catch (JSException JSException_Arg){
        JSException_Arg.printStackTrace();
        System.out.println("ex"+JSException_Arg.getWrappedExceptionType()+"="+JSException_Arg.getMessage());
      } 
    
      // Normalize
      if (Normalize){
        for (int i=0; i<ValueArray.length; i++) {
          for (int j=0; j<ValueArray[0].length; j++) {
            ValueArray[i][j]= 100* ValueArray[i][j] / NormalRows[j];
          }
        }
      }
    }

    // let's prepare a distribution table with 0 in all classes and no biggest class
    DistributionArray = new double[BiSlider_Arg.getSegmentCount()][3];
    for (int k=0; k<BiSlider_Arg.getSegmentCount(); k++) {
      DistributionArray[k][0] = k*BiSlider_Arg.getSegmentSize();
      DistributionArray[k][1] = (k+1)*BiSlider_Arg.getSegmentSize();
      DistributionArray[k][2] = 0;
    }
    BiggestClassIndex = 0;     
    
    // update the distribution table 
    for (int i=0; i<ValueArray.length; i++) {
      for (int j=0; j<ValueArray[0].length; j++) {
        if (ValueArray[i][j]!=Double.NaN) {        
          for (int k=0; k<DistributionArray.length; k++){
            if (ValueArray[i][j]>=DistributionArray[k][0] && ValueArray[i][j]<DistributionArray[k][1]){
              DistributionArray[k][2] = DistributionArray[k][2]+1;
              break;
            }

            // update the biggest class index
            if (DistributionArray[k][2]>DistributionArray[BiggestClassIndex][2])
              BiggestClassIndex = k;
          }// look for the good class
        }// if a real number is in this cell
      }
    }// update the distribution table 

    // colorize the cells
    for (int i=0; i<ValueArray.length; i++) {
      for (int j=0; j<ValueArray[0].length; j++) {
        if (ValueArray[i][j]!=Double.NaN) {
          String ColorName = "#FFFFFF";
          // from Color to html format #FF0088
          Color col = Colorizer1.getColorForValue(ValueArray[i][j]);
          if (col!=null) {
            String RedString   = Integer.toHexString(col.getRed());
            String GreenString = Integer.toHexString(col.getGreen());
            String BlueString  = Integer.toHexString(col.getBlue());
            if (RedString.length()==1)   RedString   = "0"+RedString;
            if (GreenString.length()==1) GreenString = "0"+GreenString;
            if (BlueString.length()==1)  BlueString  = "0"+BlueString;
            ColorName = "#"+RedString+GreenString+BlueString;
          }
          // update the parameters of thwe html cell
          CellArray[i][j].setMember("bgColor", ColorName);
          //JSCell_Arg.setMember("align",   "right");   
        }// if a real number is in this cell
      }
    }// update the distribution table 
  }// updateHTMLLink()
  
  
  /**
   * update the table by going through the DOM table
   */
  protected void updateHTMLLinkFullTable(
    JSObject JSTable_Arg) {

    try {
      JSObject childNodes=(JSObject)JSTable_Arg.getMember("childNodes");
 
      // There are maybe whitespaces or other comments before we reach the tbody !
      // we'll use this technique for tr and td parsing as well
      int nodeindex = 0;
      for (; (nodeindex<10&&childNodes.getSlot(nodeindex) instanceof JSObject && 
              !childNodes.getSlot(nodeindex).toString().equals("[object HTMLTableSectionElement]")); nodeindex++);
      if (nodeindex==10){System.err.println("Error when looking for [object HTMLTableSectionElement] in the dom");return;}
 
      // So we get the JSObject where we found it
      JSObject JSTBODYObject = (JSObject)childNodes.getSlot(nodeindex);
 
      int rownodeindex = -1;
      for (int j=0; j<=MaxRowIndex; j++) {
        childNodes=(JSObject)JSTBODYObject.getMember("childNodes");  

        for (rownodeindex++; (rownodeindex<4*MaxRowIndex&&childNodes.getSlot(rownodeindex) instanceof JSObject && 
                !childNodes.getSlot(rownodeindex).toString().equals("[object HTMLTableRowElement]")); rownodeindex++);
        if (rownodeindex==4*MaxRowIndex){System.err.println("Error when looking for [object HTMLTableRowElement] in the dom: not enough columns?");return;}
        
        JSObject JSTRNode=(JSObject)childNodes.getSlot(rownodeindex);

        // if we are after the 1rst row to consider
        if (j>=MinRowIndex) {
          int colnodeindex = -1;
          for (int i=0; i<=MaxColIndex; i++) {
            childNodes=(JSObject)JSTRNode.getMember("childNodes");

            for (colnodeindex++; (colnodeindex<4*MaxColIndex&&childNodes.getSlot(colnodeindex) instanceof JSObject && 
                    !childNodes.getSlot(colnodeindex).toString().equals("[object HTMLTableCellElement]")); colnodeindex++);
            if (colnodeindex==4*MaxColIndex){System.err.println("Error when looking for [object HTMLTableCellElement] in the dom: not enough columns?");return;}

            // if we are after the 1rst column to consider and before the last
            if (i>=MinColIndex && i<=MaxColIndex) {
              JSObject JSTDNode=(JSObject)childNodes.getSlot(colnodeindex);
              ValueArray[i-MinColIndex][j-MinRowIndex] = updateCell(JSTDNode);
              CellArray[i-MinColIndex][j-MinRowIndex]  = JSTDNode;
            }// if we are after the 1rst column to consider and before the last
            else if (Normalize && i==ColNormalize){
              JSObject JSTDNode=(JSObject)childNodes.getSlot(colnodeindex);
              NormalRows[j-MinRowIndex] = updateCell(JSTDNode);            
            }
          }// for loop on columns
        }// if we are after the 1rst row to consider
      }// for loop on rows
 
    } catch (JSException JSException_Arg){
      JSException_Arg.printStackTrace();
      System.out.println("ex"+JSException_Arg.getWrappedExceptionType()+"="+JSException_Arg.getMessage());
    }
    catch(Exception Exception_Arg) {
      Exception_Arg.printStackTrace();
    }
  }// updateHTMLLinkFullTable()


   /**
   * update the table by going through the DOM table
   */
  protected void updateHTMLLinkCellByCell( 
    JSObject JSWindow) {  
    
    try {
      for (int i=MinColIndex; i<=MaxColIndex; i++)
        for (int j=MinRowIndex; j<=MaxRowIndex; j++) {
          // get the value in the html table
          JSObject JSCell = (JSObject) JSWindow.eval("document.getElementById('"+LinkToTableString+i+"-"+j+"')");
          ValueArray[i-MinColIndex][j-MinRowIndex] = updateCell(JSCell);
          CellArray[i-MinColIndex][j-MinRowIndex]  = JSCell;
        }
      for (int j=MinRowIndex; j<=MaxRowIndex; j++) {
        JSObject JSCell = (JSObject) JSWindow.eval("document.getElementById('"+LinkToTableString+ColNormalize+"-"+j+"')");
        NormalRows[j] = updateCell(JSCell);
      }
    } catch (Exception Exception_Arg) {
      Exception_Arg.printStackTrace();
    } 
  }// updateHTMLLinkCellByCell();


   /**
   * update the table by going through the DOM table
   */
  protected double updateCell(
    JSObject JSCell_Arg) {  
    
    double val = Double.NaN;    
    
    if (JSCell_Arg!=null) {
      String Value     = (String)JSCell_Arg.getMember("innerHTML");

      while (Value.indexOf("<")>=0 &&Value.indexOf(">")>=0 && Value.indexOf("<")<Value.indexOf(">"))
        Value= Value.substring(0, Value.indexOf("<"))+Value.substring(Value.indexOf(">")+1);
      Value = Value.replaceAll("\n", "");
      Value = Value.replaceAll("\r", "");
      Value = Value.trim();

      if (Value!=null) {
        // convert the value 
        try {
          if (DecimalFormat1!=null)                      
            val = DecimalFormat1.parse(Value).doubleValue();      
          else if (Language!=null){
            NumberFormat NumberFormat1 = NumberFormat.getInstance(Language);
            // in some languages like french F#@$%ing spaces are used ... but not real spaces ! 
            val = NumberFormat1.parse(Value.replace(' ', '\u00A0')).doubleValue();   
          } else 
            val = Integer.parseInt(Value);                    
        } catch (NumberFormatException NumberFormatException_Arg) {
          NumberFormatException_Arg.printStackTrace();
          return Double.NaN;
        } catch (ParseException ParseException_Arg) {
          ParseException_Arg.printStackTrace();
          return Double.NaN;
        }
      }// if Value!=null
    }
    return val;
  }// updateCell()
  
}// class HTMLTableColorizerApplet
