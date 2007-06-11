package Debrief.Tools.Tote;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: WatchableList.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: WatchableList.java,v $
// Revision 1.2  2004/11/25 10:24:42  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:49:10  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.10  2003-05-08 16:00:57+01  ian_mayo
// remove unused imports
//
// Revision 1.9  2003-03-27 11:16:04+00  ian_mayo
// Update javadoc
//
// Revision 1.8  2003-03-19 15:37:21+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.7  2003-02-03 14:10:34+00  ian_mayo
// Add testing helper class
//
// Revision 1.6  2003-01-15 15:28:43+00  ian_mayo
// Improve comment
//
// Revision 1.5  2003-01-10 15:22:36+00  ian_mayo
// Add getVisible parameter
//
// Revision 1.4  2002-12-16 15:10:23+00  ian_mayo
// minor tidying
//
// Revision 1.3  2002-05-28 09:25:11+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:44+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-08 14:40:50+01  ian_mayo
// make final objects static
//
// Revision 1.1  2002-04-23 12:28:40+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-03-13 08:46:49+00  administrator
// more comment improvements
//
// Revision 1.3  2002-03-13 08:40:33+00  administrator
// improve comments
//
// Revision 1.2  2001-10-01 12:49:53+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.1  2001-08-06 16:57:43+01  administrator
// Add text field to use to categories the PropertyEvent when a watchable has been filtered
//
// Revision 1.0  2001-07-17 08:41:14+01  administrator
// Initial revision
//
// Revision 1.5  2001-01-15 11:19:01+00  novatech
// get the symbol to use for this list
//
// Revision 1.4  2001-01-11 15:37:11+00  novatech
// switch back to working with dates in long class format
//
// Revision 1.3  2001-01-11 11:53:02+00  novatech
// Before we switch from long dates to java.util.date dates.
//
// Revision 1.2  2001-01-09 10:28:40+00  novatech
// add extra parameters to allow WatchableLists to be used instead of TrackWrappers
//
// Revision 1.1  2001-01-03 13:40:27+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:04  ianmayo
// initial import of files
//
// Revision 1.5  2000-10-16 11:50:46+01  ian_mayo
// changed default Time threshold for stepper
//
// Revision 1.4  2000-04-19 11:24:16+01  ian_mayo
// add time threshold parameter
//
// Revision 1.3  2000-04-03 10:20:25+01  ian_mayo
// add time-filtering method
//
// Revision 1.2  1999-10-15 12:37:16+01  ian_mayo
// improved management of watchables which don't hve time periods
//
// Revision 1.1  1999-10-12 15:34:06+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:03:16+01  administrator
// Initial revision
//

import MWC.GenericData.HiResDate;

import java.util.Collection;

import junit.framework.TestCase;

public interface WatchableList
{
  /** the name of the property change event to fire should this object get filtered
   *
   */
  public static final String FILTERED_PROPERTY = "WATCHABLE_FILTERED";

	/** the time threshold around a Watchable to decide
	 * if it is visible or not (value in micros)
	 */
	public static final long TIME_THRESHOLD=120000000; // 2 minutes

  /** get the name of this list
   * @return the name of this list
   */
  public String getName();

  /** get the start DTG of this list
   * @return the start DTG, or -1 if not time-related
   */
  public HiResDate getStartDTG();

  /** get the end DTG of this list
   * @return the end DTG, or -1 if not time-related
   */
  public HiResDate getEndDTG();

  /** find out if this object is visible
   *
   * @return yes/no for visible
   */
  public boolean getVisible();


  /** get the watchable in this list nearest to the specified DTG.
   * If the watchable list has start/finish times, only return a fix if the DTG
   * supplied is during the "live" period of the watchables.
   * If not DTG is present, or an invalid DTG is requested ,return all valid points
   * @param DTG to search for
   * @return the nearest Watchable
   */
  public Watchable[] getNearestTo(HiResDate DTG);

	/** filter the list to the specified time period
	 */
	public void filterListTo(HiResDate start, HiResDate end);

  /** return the set of items which fall inside the indicated period.
   * If an items has an "alive" period which overlaps this period then it will
   * be returned.  If the item has no time set, then return it as being valid
   */
  public java.util.Collection getItemsBetween(HiResDate start, HiResDate end);

  /** find out the total area covered by this list
   */
  public MWC.GenericData.WorldArea getBounds();

  /** find out the default colour for this list
   */
  public java.awt.Color getColor();

  /** find out the symbol to use for plotting this list in Snail mode
   */
  public MWC.GUI.Shapes.Symbols.PlainSymbol getSnailShape();

  /**********************************************************************
   * embedded class for testing watchable lists.
   * Used in support of JUnit testing
   *********************************************************************/
  public abstract class TestWatchables
  {
    /** get an example of this kind of list with no dates set
     *
     * @return
     */
    public abstract WatchableList getNullDates();

    /** get an example of this kind of list with both dates set
     *
     * @return
     */
    public abstract WatchableList getBothDates(HiResDate startDate, HiResDate endDate);

    /** get an example of this kind of list with only start date set
     *
     * @return
     */
    public abstract WatchableList getStartDateOnly(HiResDate startDate);

    /** test an instance of this class
     *
     * @param tester
     */
    public final void doTest(junit.framework.TestCase tester)
    {
      // test without dates
      WatchableList noDates = getNullDates();

      // test filter list to
      noDates.filterListTo(new HiResDate(2000), new HiResDate(3000));
      TestCase.assertTrue("item wasn't filtered", noDates.getVisible());

      // test get items between
      Collection coll = noDates.getItemsBetween(new HiResDate(2000), new HiResDate(3000));
      TestCase.assertTrue("all items were returned", coll.size() == 1);

      // test get nearest to
      Debrief.Tools.Tote.Watchable[] nearest = noDates.getNearestTo(new HiResDate(3000, 0));
      TestCase.assertEquals("return itself when no DTG present", 1, nearest.length);

      // test get start DTG
      TestCase.assertEquals("no start DTG", null, noDates.getStartDTG());

      // test get end DTG
      TestCase.assertEquals("no end DTG", null, noDates.getEndDTG());

      // test start date only
      WatchableList startOnly = getStartDateOnly(new HiResDate(2500));

      // test filter list to
      startOnly.filterListTo(new HiResDate(2000), new HiResDate(3000));
      TestCase.assertTrue("item was filtered", startOnly.getVisible());
      startOnly.filterListTo(new HiResDate(4000), new HiResDate(5000));
      TestCase.assertTrue("item wasn't filtered", !startOnly.getVisible());
      startOnly.filterListTo(new HiResDate(1000), new HiResDate(2000));
      TestCase.assertTrue("item wasn't filtered", !startOnly.getVisible());
      startOnly.filterListTo(new HiResDate(2000), new HiResDate(3000));
      TestCase.assertTrue("item was filtered", startOnly.getVisible());

      // test get items between
      coll = startOnly.getItemsBetween(new HiResDate(2000), new HiResDate(3000));
      TestCase.assertTrue("items were returned", coll != null);
      TestCase.assertTrue("items were returned", coll.size() == 1);
      coll = startOnly.getItemsBetween(new HiResDate(4000), new HiResDate(6000));
      TestCase.assertTrue("no items were returned", coll == null);
      coll = startOnly.getItemsBetween(new HiResDate(1000), new HiResDate(2000));
      TestCase.assertTrue("no items were returned", coll == null);

      // test get nearest to
      nearest = startOnly.getNearestTo(new HiResDate(3000));
      TestCase.assertEquals("nearest item found", nearest[0], startOnly);
      TestCase.assertEquals("nearest item found", nearest.length, 1);

      // test get start DTG
      TestCase.assertEquals("no start DTG", 2500, startOnly.getStartDTG().getDate().getTime());

      // test get end DTG
      TestCase.assertEquals("no end DTG", null, startOnly.getEndDTG());


      // test with dates
      WatchableList withDates = getBothDates(new HiResDate(2500),new HiResDate(2700));

      // test filter list to
       withDates.filterListTo(new HiResDate(2000), new HiResDate(3000));
       TestCase.assertTrue("item was filtered", withDates.getVisible());
       withDates.filterListTo(new HiResDate(4000), new HiResDate(5000));
       TestCase.assertTrue("item wasn't filtered", !withDates.getVisible());
       withDates.filterListTo(new HiResDate(1000), new HiResDate(2000));
       TestCase.assertTrue("item wasn't filtered", !withDates.getVisible());
       withDates.filterListTo(new HiResDate(2000), new HiResDate(3000));
       TestCase.assertTrue("item was filtered", withDates.getVisible());

       // test get items between
       coll = withDates.getItemsBetween(new HiResDate(2000), new HiResDate(3000));
       TestCase.assertTrue("items were returned", coll != null);
       TestCase.assertTrue("items were returned", coll.size() == 1);
       coll = withDates.getItemsBetween(new HiResDate(4000), new HiResDate(6000));
       TestCase.assertTrue("no items were returned", coll == null);
       coll = withDates.getItemsBetween(new HiResDate(1000), new HiResDate(2000));
       TestCase.assertTrue("no items were returned", coll == null);

       // test get nearest to
       nearest = withDates.getNearestTo(new HiResDate(2600));
       TestCase.assertEquals("nearest item found", nearest.length, 1);
       TestCase.assertEquals("nearest item found", nearest[0], withDates);
       nearest = withDates.getNearestTo(new HiResDate(1100));
       TestCase.assertEquals("nearest item found", nearest.length, 0);
       nearest = withDates.getNearestTo(new HiResDate(5100));
       TestCase.assertEquals("nearest item found", nearest.length, 0);

       // test get start DTG
       TestCase.assertEquals("start DTG", 2500, withDates.getStartDTG().getDate().getTime());

       // test get end DTG
       TestCase.assertEquals("end DTG", 2700, withDates.getEndDTG().getDate().getTime());
    }
  }

}