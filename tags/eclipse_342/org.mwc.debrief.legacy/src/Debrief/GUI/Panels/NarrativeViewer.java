/**
 * Debrief.GUI.Panels.NarrativeViewer
 */
package Debrief.GUI.Panels;

// Copyright MWC 1999
// $RCSfile: NarrativeViewer.java,v $
// $Author: Ian.Mayo $
// $Log: NarrativeViewer.java,v $
// Revision 1.8  2006/08/08 12:55:28  Ian.Mayo
// Restructure loading narrative entries (so we can see it from CMAP)
//
// Revision 1.7  2005/12/13 09:04:22  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.6  2005/01/11 10:51:57  Ian.Mayo
// Correctly support hi res dates
//
// Revision 1.5  2005/01/11 10:51:11  Ian.Mayo
// Reflect us swithing to Hi Res dates
//
// Revision 1.4  2004/11/25 10:23:58  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.3  2004/11/22 13:40:48  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.2  2004/09/09 10:22:52  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.1.1.2  2003/07/21 14:47:11  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-04-30 16:05:24+01  ian_mayo
// tidy up date format management
//
// Revision 1.3  2003-03-19 15:38:05+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:27:54+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:19+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:30:06+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-01-17 15:03:37+00  administrator
// Reflect new interface to hide StepperListener class
//
// Revision 1.3  2001-10-08 17:12:54+01  administrator
// Rename track column to source
//
// Revision 1.2  2001-08-31 10:40:41+01  administrator
// No need for editor buttons
//
// Revision 1.1  2001-07-31 12:17:44+01  administrator
// Word-wrap on letter, not on word.
// Match up with narrative entries equal to the current DTG.
// Correct bug where dates not updated on second opening
//
// Revision 1.0  2001-07-17 08:41:35+01  administrator
// Initial revision
//
// Revision 1.4  2001-07-12 12:26:13+01  novatech
// General tidying (following Andy's review) and insertion of comments
//
// Revision 1.3  2001-07-09 14:14:46+01  novatech
// handle Form closing, during which we remove ourselves as a stepper listener
//
// Revision 1.2  2001-07-09 14:08:42+01  novatech
// Manage the stepper, listening to the stepper, updating the stepper when the user double-clicks on a narrative entry.
//
// Revision 1.1  2001-07-06 16:00:14+01  novatech
// Initial revision
//
// Revision 1.2  2001-01-05 09:13:19+00  novatech

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellRenderer;

import Debrief.Wrappers.*;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;

public final class NarrativeViewer extends MWC.GUI.Properties.Swing.SwingCustomEditor implements MWC.GUI.StepperListener,
  MWC.GUI.Properties.NoEditorButtons
{

  /////////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the narrative we are presenting
   */
  private NarrativeWrapper _myData;

  /**
   * the model which stores the data for the table
   */
  private javax.swing.table.DefaultTableModel dm;

  /**
   * the date format to use
   */
  java.text.SimpleDateFormat df = null;

  /**
   * the table object containing our data
   */
  JTable _myTable = null;

  /**
   * whether we are listening to the step control
   */
  boolean _listenToStep = true;

  /**
   * whether we want to see just the first line
   */
  boolean _onlyFirstLine = false;

  /**
   * the step control we are listening to
   */
  private Debrief.GUI.Tote.StepControl _theStepper = null;

  /**
   * semaphore we use to indicate whether we are already trying to reformat the
   * table columns
   */
  boolean _changing = false;

  /**
   * fixed width font we use to provide correctly width'd columns
   */
  static Font _myFont = null;

  /**
   * the width of a text character using this component
   */
  static int _charWid = 0;

  /**
   * the list of renderers we use for dtgs
   */
  static java.util.Vector<MyDateComponent> _theDTGRenderers = new java.util.Vector<MyDateComponent>(10, 10);

  /**
   * the list of renderers we use for entries
   */
  static java.util.Vector<MyEntryComponent> _theEntryRenderers = new java.util.Vector<MyEntryComponent>(10, 10);



  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /**
   * handle the close event, so that we can remove ourselves from being a stepper listener
   */
  public final void doClose()
  {
    if (_theStepper != null)
    {
      _theStepper.removeStepperListener(this);
    }
  }

  /**
   * setObject
   *
   * @param data the NarrativeWrapper we are going to edit
   */
  public final void setObject(final Object data)
  {
    _myData = (NarrativeWrapper) data;

    initForm();

  }

  /**
   * initForm
   */
  private void initForm()
  {
    // initialise the date format
    newDateFormat("yy/MM/dd HH:mm");

    // we also want to reset our lists of renderers if we are re-creating this form
    _theDTGRenderers = null;
    _theDTGRenderers = new Vector<MyDateComponent>(10, 10);

    _theEntryRenderers = null;
    _theEntryRenderers = new Vector<MyEntryComponent>(10, 10);

    setLayout(new BorderLayout());
    add(getForm(), "Center");

    // try to get the stepper
    _theStepper = _myData.getStepper();

    // do we know the stepper?
    if (_theStepper != null)
    {
      _theStepper.addStepperListener(this);
    }
    else
    {
      MWC.Utilities.Errors.Trace.trace("Sorry, the narrative wasn't loaded properly. It won't be able to follow the time step");
    }
  }


  /**
   * getForm
   *
   * @return the returned JComponent
   */
  private JComponent getForm()
  {
    // create the table
    _myTable = new JTable()
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			// add listener which informs us when a column is being dragged
      public void columnMarginChanged(final ChangeEvent e)
      {
        if (!_changing)
        {
          _changing = true;
          super.columnMarginChanged(e);
          doResize();
          _changing = false;
        }
        else
        {
        }
      }

      // add listener which informs us when a column is being dragged
      public void valueChanged(final javax.swing.event.ListSelectionEvent e)
      {
        super.valueChanged(e);
        if (e.getValueIsAdjusting())
        {
        }
        else
        {
          final int index = e.getFirstIndex();
          if (index != 0)
          {
            itemSelected();
          }
        }
      }
    };

    // prevent the columns being draggable
    _myTable.getTableHeader().setReorderingAllowed(false);

    // we need to listen out for a double-click on a table entry, since
    // we then move the time stepper to that time
    _myTable.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(final java.awt.event.MouseEvent e)
      {
        // was this a double-click?
        if (e.getClickCount() == 2)
        {
          // get the currently selected row in the table
          final int index = _myTable.getSelectedRow();

          // retrieve the narrative entry from column 2 of this row
          final Object oj = _myTable.getValueAt(index, 2);

          if (oj instanceof MWC.TacticalData.NarrativeEntry)
          {
            final MWC.TacticalData.NarrativeEntry ne =
              (MWC.TacticalData.NarrativeEntry) oj;
            moveToEntry(ne.getDTG());
          }
        }
      }
    });


    // when we resize any columns, we want the last column (entries) to
    // take up the slack
    _myTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

    // create the data model, over-riding it slightly to ensure no elements are editable
    dm = new javax.swing.table.DefaultTableModel()
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(final int r, final int c)
      {
        return false;
      }
    };
    _myTable.setModel(dm);
    _myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // now put in the columns
    dm.addColumn("DTG");
    dm.addColumn("Source");
    dm.addColumn("Entry");

    // get the columns
    final javax.swing.table.TableColumn dtgs = _myTable.getColumn("DTG");
    final javax.swing.table.TableColumn entries = _myTable.getColumn("Entry");

    // and set the custom renderers
    dtgs.setCellRenderer(new myDTGRenderer());
    entries.setCellRenderer(new myEntryRenderer());

    // a holder for the table
    final JPanel holder = new JPanel();
    holder.setLayout(new BorderLayout());

    // put the table in a scroll pane, so it can scroll
    // if necessary
    final JScrollPane jsp = new JScrollPane(_myTable);
    holder.add("Center", jsp);

    // add the other components we manage
    final JPanel jp = new JPanel();
    jp.setLayout(new FlowLayout());
    // first line
    final JCheckBox flo = new JCheckBox("First line only");
    flo.setToolTipText("Only show the first line of each entry");
    flo.setSelected(_onlyFirstLine);
    flo.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent e)
      {
        _onlyFirstLine = flo.isSelected();
        doResize();
      }
    });
    // follow time
    final JCheckBox fts = new JCheckBox("Follow time");
    fts.setToolTipText("Highlight entry nearest to current time");
    fts.setSelected(_listenToStep);
    fts.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent e)
      {
        _listenToStep = fts.isSelected();
      }
    });
    // date format
    final MWC.GUI.Properties.DateFormatPropertyEditor.SwingDateFormatEditor sf
      = new MWC.GUI.Properties.DateFormatPropertyEditor.SwingDateFormatEditor()
      {
        /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void newFormat(final String format)
        {
          newDateFormat(format);
        }
      };
    sf.setSelectedItem("ddHHmm");
    sf.setToolTipText("Format of date column");

    jp.add(sf);
    jp.add(flo);
    jp.add(fts);

    holder.add("North", jp);

    // put in the data
    fillTable();

    // do a quick resize to check everything's ok
    doResize();

    return holder;

  }

  /**
   * fillTable
   */
  private void fillTable()
  {
    // get the data model
    final javax.swing.table.DefaultTableModel dm1 = (javax.swing.table.DefaultTableModel) _myTable.getModel();

    // find out how many rows there are
    final int rowCount = dm1.getRowCount();

    // delete the rows
    for (int i = 0; i < rowCount; i++)
    {
      dm1.removeRow(0);
    }

    // keep track of the longest track name
    int len = 0;

    // and fill them in
    final Enumeration<Editable> iter = _myData.elements();
    while (iter.hasMoreElements())
    {
      final NarrativeEntry ne = (NarrativeEntry) iter.nextElement();
      len = Math.max(len, ne.getTrackName().length());
      final Object[] newR = {ne.getDTG(), ne.getTrackName(), ne};
      dm1.addRow(newR);
    }


    // set the width of the tracks column to the minimum suitable size
    final javax.swing.table.TableColumn trks = _myTable.getColumn("Source");
    final int newWid = (len + 1) * 10;
    trks.setWidth(newWid);
  }

  /**
   * method to select the narrative entry nearest
   * to the current time in the time stepper
   *
   * @param time the new time
   */
  void newTime(final HiResDate time)
  {
    // check if we are following the stepper
    if (!this._listenToStep)
      return;

    // keep track of the row index we are looking at
    int rowIndex = -1;

    // find which row this is
    for (int i = 0; i < _myTable.getRowCount(); i++)
    {
      final Object oj = _myTable.getModel().getValueAt(i, 2);

      /*      // get the narrative entry component
            Component c = entries.getCellRenderer().getTableCellRendererComponent(_myTable,
                                                                              _myTable.getValueAt(i, 2),
                                                                              false,
                                                                              false,
                                                                              i, 1);
            // cast to the entry
            MyEntryComponent ja = (MyEntryComponent)c;

            long thisDTG = ja.getEntry().getDTG();
        */
      final NarrativeEntry ne = (NarrativeEntry) oj;

      final HiResDate thisDTG = ne.getDTG();

      // run through until we meet one equal to or greater than the indicated time
      if (thisDTG.lessThanOrEqualTo(time))
      {
        rowIndex = i;
      }
      else
        break;

    }

    // did we find one?
    if (rowIndex > -1)
    {
      _myTable.changeSelection(rowIndex, 0, false, false);
    }
  }

  /**
   * the user has double-clicked on a narrative entry, cause the time stepper
   * to move to that point
   *
   * @param dtg the time to step to
   */
  void moveToEntry(final HiResDate dtg)
  {
    // set the time in the stepper to this time
    if (_theStepper != null)
    {
      // temporarily remove us from the stepper, since we're not interested in
      // processing the event fired by the new time in the stepper
      _theStepper.removeStepperListener(this);

      // set the time in the stepper
      _theStepper.changeTime(dtg);

      // and replace us in the stepper
      _theStepper.addStepperListener(this);
    }
  }


  /**
   * the user has selected a new date format, update the date column
   *
   * @param format the new selection from the Combo box
   */
  void newDateFormat(final String format)
  {
    df = new java.text.SimpleDateFormat(format);

    // check the formats are in the correct time zone
    df.setTimeZone(TimeZone.getTimeZone("GMT"));

    // now resize, since the width of the first column may have changed, if we have data
    if (_myTable != null)
      doResize();
  }

  /**
   * the user has double-clicked on our table, process the selection
   */
  void itemSelected()
  {
  }

  /**
   * the column widths have changed, ensure that we can see all of each paragraph
   * if the user has elected to do so.
   */
  void doResize()
  {

    final javax.swing.table.TableColumn entries = _myTable.getColumn("Entry");
    final javax.swing.table.TableColumn dtgs = _myTable.getColumn("DTG");
    // set the correct width of the DTG column
    dtgs.setWidth((df.toPattern().length() + 1) * _charWid);

    // and make the entry column resize to suit this
    _myTable.sizeColumnsToFit(2);

    // the height of a single row
    final int h = 17;

    // we have created our editors, now set the best height
    for (int i = 0; i < _myTable.getRowCount(); i++)
    {
      // retrieve the entry components
      final Component c = entries.getCellRenderer().getTableCellRendererComponent(_myTable,
                                                                                  _myTable.getValueAt(i, 2),
                                                                                  false,
                                                                                  false,
                                                                                  i, 1);

      // does the user only want to see the first row?
      if (_onlyFirstLine)
      {
        _myTable.setRowHeight(i, h);
      }
      else
      {
        final MyEntryComponent ja = (MyEntryComponent) c;
        // how many times does this fit into object width
        final int colWid = entries.getWidth();
        final long totalWid = ja.getTotalWidth();
        final double lines = (double) totalWid / colWid;
        // now set the number of lines to this
        _myTable.setRowHeight(i, (int) (lines + 1) * h);

      } // first line or not

    } // for each row

  }


  /**
   * doReset
   */
  public final void doReset()
  {
  }

  ///////////////////////////////////////////////////
  // provide the methods needed by the stepper
  /**
   * //////////////////////////////////////////////////
   * ignore this really, we don't mind if the user
   * has switched to snail mode
   *
   * @param on the new value from the check box
   */

  public final void steppingModeChanged(final boolean on)
  {
  }

  /**
   * newTime
   *
   * @param oldDTG the old time in the stepper
   * @param newDTG the new time in the stepper
   * @param canvas   ignore this, since we don't plot to it
   */
  public final void newTime(final HiResDate oldDTG, final HiResDate newDTG, final CanvasType canvas)
  {
    newTime(newDTG);
  }

  /**
   * toString
   *
   * @return the returned String
   */
  public final String toString()
  {
    return "Narrative Viewer";
  };

  /**
   * and the method to set the stepper
   *
   * @param stepper the step control we will listen to
   */
  public final void setStepper(final Debrief.GUI.Tote.StepControl stepper)
  {
    _theStepper = stepper;
  }

  /////////////////////////////////////////////////////
  // produce a JLabel component, so that we can see labels
  /**
   * ///////////////////////////////////////////////////
   */

  final class myEntryRenderer implements TableCellRenderer
  {

    /**
     * getTableCellRendererComponent
     *
     * @param p1 parameter for getTableCellRendererComponent
     * @return the returned Component
     */
    public final Component getTableCellRendererComponent(final JTable p1,
                                                         final Object object,
                                                         final boolean isSelected,
                                                         final boolean p4,
                                                         final int row,
                                                         final int column)
    {

      MyEntryComponent theRenderer = null;

      // see if this row number is greater than the total length
      if (row >= _theEntryRenderers.size())
      {
        _theEntryRenderers.setSize(row + 5);
      }

      try
      {
        // now try to get the renderer
        theRenderer = _theEntryRenderers.elementAt(row);
      }
      finally
      {
        // was there one there?
        if (theRenderer == null)
        {
          final NarrativeEntry ne = (NarrativeEntry) object;
          theRenderer = new MyEntryComponent(ne);
          _theEntryRenderers.setElementAt(theRenderer, row);
        }
      }


      // set it selected, if we have to
      if (isSelected)
      {
        theRenderer.setBackground(_myTable.getSelectionBackground());
      }
      else
      {
        theRenderer.setBackground(_myTable.getBackground());
      }


      return (Component) theRenderer;

    }

  }

  static final class MyEntryComponent extends JTextArea
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
     * // the total width of this line of text
     */
    private long txtWidth = 0;

    /**
     * <init>
     *
     * @param entry parameter for <init>
     */
    public MyEntryComponent(final NarrativeEntry entry)
    {
      super(entry.getEntry());

      if (_myFont == null)
      {
        final Font curFont = super.getFont();
        _myFont = new Font("Courier", curFont.getStyle(), curFont.getSize());
      }

      if (_charWid == 0)
      {
        final FontMetrics fm = super.getFontMetrics(_myFont);
        _charWid = fm.charWidth(' ');
      }

      txtWidth = (long) _charWid * entry.getEntry().length();

      // now the remaining TextField initialisation
      setWrapStyleWord(false);
      setEnabled(true);
      setLineWrap(true);
      setFont(_myFont);

    }

    /**
     * getTotalWidth
     *
     * @return the returned long
     */
    public final long getTotalWidth()
    {
      return txtWidth;
    }
  }



  /////////////////////////////////////////////////////
  // viewer for the DTG's
  /**
   * ///////////////////////////////////////////////////
   */
  final class myDTGRenderer implements TableCellRenderer
  {

    /**
     * getTableCellRendererComponent
     *
     * @param p1  parameter for getTableCellRendererComponent
     * @param row parameter for getTableCellRendererComponent
     * @return the returned Component
     */
    public final Component getTableCellRendererComponent(final JTable p1,
                                                         final Object p2,
                                                         final boolean isSelected,
                                                         final boolean p4,
                                                         final int row,
                                                         final int column)
    {

      MyDateComponent theRenderer = null;

      // see if this row number is greater than the total length
      if (row >= _theDTGRenderers.size())
      {
        _theDTGRenderers.setSize(row + 5);
      }

      try
      {
        // now try to get the renderer
        theRenderer = _theDTGRenderers.elementAt(row);
      }
      finally
      {
        // was there one there?
        if (theRenderer == null)
        {
          theRenderer = new MyDateComponent((HiResDate) p2);
          _theDTGRenderers.setElementAt(theRenderer, row);
        }
      }

      // set it selected, if we have to
      if (isSelected)
      {
        theRenderer.setBackground(_myTable.getSelectionBackground());
      }
      else
      {
        theRenderer.setBackground(_myTable.getBackground());
      }

      return (Component) theRenderer;
    }
  }


  final class MyDateComponent extends javax.swing.table.DefaultTableCellRenderer
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
     * the date we are presenting
     */
    private final Date _theDate;

    /**
     * the last date format we were using
     */
    private java.text.SimpleDateFormat _lastFormat = null;

    /**
     * constructor
     *
     * @param theDate the date we want to plot
     */
    public MyDateComponent(final HiResDate theDate)
    {
      super();


      if (_myFont == null)
      {
        final Font curFont = super.getFont();
        _myFont = new Font("Courier", curFont.getStyle(), 10);
      }

      setFont(_myFont);

      _theDate = theDate.getDate();
    }

    /**
     * invalidate
     */
    public final void invalidate()
    {
      super.invalidate();
      if (_lastFormat != df)
      {
        setValue(df.format(_theDate));
        _lastFormat = df;
      }
    }

  }


  /**
   * @param args dummy
   */


  public static void main(final String[] args)
  {

    final NarrativeWrapper nw = new NarrativeWrapper("test", null);
    int ct = 1;
    for (int i = 0; i < 15; i++)
    {
      nw.add(new NarrativeEntry("TRK", new HiResDate(2134234234l + i * 1222567000), ":" + ct++ + " some text"));
      nw.add(new NarrativeEntry("TRK", new HiResDate(2534234234l + i * 1222054300), ":" + ct++ + " some text2 some text2 some text2 some text2 some text2 some text2 some text2 some text2 some text2 some text2 "));
      nw.add(new NarrativeEntry("TRK", new HiResDate(2734234234l + i), ":" + ct++ + " some text3"));
    }

    final NarrativeViewer nv = new NarrativeViewer();
    nv.setObject(nw);


    final JFrame jf = new JFrame("tester");
    jf.setSize(300, 600);
    jf.setVisible(true);
    jf.getContentPane().setLayout(new java.awt.BorderLayout());
    jf.getContentPane().add(nv, "Center");
    jf.doLayout();
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    final JButton doMe = new JButton("do me");
    jf.getContentPane().add(doMe, "South");
    doMe.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent e)
      {
        nv.newTime(new HiResDate(2734234234l));
      }
    });

  }

}
