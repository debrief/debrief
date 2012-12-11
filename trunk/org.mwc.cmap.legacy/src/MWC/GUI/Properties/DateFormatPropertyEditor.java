// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DateFormatPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.6 $
// $Log: DateFormatPropertyEditor.java,v $
// Revision 1.6  2006/01/10 09:24:30  Ian.Mayo
// Use better default date format
//
// Revision 1.5  2005/09/29 14:34:01  Ian.Mayo
// Provide external, static function
//
// Revision 1.4  2005/09/20 15:37:51  Ian.Mayo
// Put in the date format andy's after
//
// Revision 1.3  2005/09/20 10:40:50  Ian.Mayo
// Make list of selectable date formats public
//
// Revision 1.2  2004/05/25 15:28:46  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:23  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-06-11 16:01:04+01  ian_mayo
// Tidy javadoc comments
//
// Revision 1.3  2003-01-14 14:16:42+00  ian_mayo
// General tidying
//
// Revision 1.2  2002-05-28 09:25:44+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:39+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:36+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:48+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-12 12:05:33+01  novatech
// white space only
//
// Revision 1.2  2001-07-09 13:58:26+01  novatech
// Add ymf format, and custom SwingControl for editing date format
//
// Revision 1.1  2001-01-03 13:42:46+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:10  ianmayo
// initial version
//
// Revision 1.1  2000-12-01 10:13:35+00  ian_mayo
// Initial revision
//
// Revision 1.1  2000-09-26 10:53:10+01  ian_mayo
// Initial revision
//


package MWC.GUI.Properties;

import java.beans.PropertyEditorSupport;

/** property editor which provides a set of date formats to be used in date presentation.
 * @see java.text.SimpleDateFormat
 */

public class DateFormatPropertyEditor extends PropertyEditorSupport
{

  public static final int INVALID_INDEX = 4;



	//////////////////////////////////////////////////
  // member objects
  //////////////////////////////////////////////////
  static private String _stringTags[];
  


  /** the currently selected format
   *
   */
  protected int _myFormat = 4;

  //////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////

  public String[] getTags()
  {
  	return getTagList();
  }
  
  /** provide easy access to the 'first cut of the day' format
   * 
   */
	public static final String DATE_FORMAT = "ddHHmm";

	/** provide easy access to the 'normal' time lable format
	 * 
	 */
	public static final String TIME_FORMAT = "HHmm";


  
  /** retrieve/initialise our list of date formats
   * 
   * @return
   */
  public static String[] getTagList()
  {
  	if(_stringTags == null)
  		_stringTags = new String[]{
          "mm:ss.SSS",
          "HHmm.ss",
          TIME_FORMAT,
          DATE_FORMAT,
          "ddHHmm:ss",
          "yy/MM/dd HH:mm"
  		};
    return _stringTags;
  }

  public Object getValue()
  {
    return getTags()[_myFormat];
  }



  public void setValue(Object p1)
  {
    if(p1 instanceof String)
    {
      String val = (String) p1;
      setAsText(val);
    }
  }

  public void setAsText(String val)
  {
    _myFormat = getIndexOf(val);
  }

  public static int getIndexOf(String val)
  {
  	int res = INVALID_INDEX;
  	
  	// cycle through the tags until we get a matching one
  	for (int i = 0; i < getTagList().length; i++)
		{
			String thisTag = getTagList()[i];
			if(thisTag.equals(val))
			{
				res = i;
				break;
			}
			
		}
  	return res;
  }
  
  public String getAsText()
  {
    return getTags()[_myFormat];
  }

  abstract static public class SwingDateFormatEditor extends javax.swing.JComboBox
  {

    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;


		public SwingDateFormatEditor()
    {
      // get our property editor data
      DateFormatPropertyEditor pe = new DateFormatPropertyEditor();

      // create and collate the data model for the combo box
      javax.swing.DefaultComboBoxModel dcm = new javax.swing.DefaultComboBoxModel(pe.getTags());

      // put the model into the combo box
      super.setModel(dcm);

      // assign the listener for the combobox
      super.addActionListener( new java.awt.event.ActionListener()
      {
        public void actionPerformed(java.awt.event.ActionEvent e)
        {
          String current = (String)getSelectedItem();
          if(current != null)
            newFormat(current);
        }
      });
    }


    abstract public void newFormat(String format);

  }
}

