package org.mwc.cmap.tests.ui;

import junit.framework.*;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.TimeController.controls.DTGBiSlider;
import org.mwc.cmap.TimeController.views.TimeController;

public class TimeControllerTests extends TestCase
{

	private static final String VIEW_ID = "org.mwc.cmap.TimeController.views.TimeController";
	private TimeController _myController;
	
	public TimeControllerTests(String testName)
	{
		super(testName);
		// TODO Auto-generated constructor stub
	}
	


  /**
   * Perform pre-test initialization
   *
   * @throws Exception
   *
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
     super.setUp();

     // initialize the test fixture for each test that is run
     waitForJobs();
     _myController =
        (TimeController) PlatformUI
           .getWorkbench()
           .getActiveWorkbenchWindow()
           .getActivePage()
           .showView(VIEW_ID);

     // Delay for 3 seconds so that 
     // the favorites view can be seen
     waitForJobs();
     delay(3000);
  }

  /**
   * Perform post-test clean up
   *
   * @throws Exception
   *
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
     super.tearDown();

     // Dispose of the test fixture
     waitForJobs();
     PlatformUI
        .getWorkbench()
        .getActiveWorkbenchWindow()
        .getActivePage()
        .hideView(_myController);
  }

  /**
   * Run the view test
   */
  public void testView() {
  	
  	DTGBiSlider periodSlider = _myController.getPeriodSlider();
  	
  	// did we find it?
  	assertNotNull("haven't found the slider", periodSlider);
  	
  	// ok, what else can we do?
  	Scale timeSlider = _myController.getTimeSlider();

  	// did we find it?
  	assertNotNull("haven't found the time slider", timeSlider);
  	
  	// hey, are we looking at any data?
  	_myController.doTests();
  
  }

  /**
   * Process UI input but do not return for the specified time interval.
   * 
   * @param waitTimeMillis the number of milliseconds 
   */
  protected void delay(long waitTimeMillis) {
     Display display = Display.getCurrent();

     // If this is the user interface thread, then process input
     if (display != null) {
        long endTimeMillis =
           System.currentTimeMillis() + waitTimeMillis;
        while (System.currentTimeMillis()
           < endTimeMillis) {
           if (!display.readAndDispatch())
              display.sleep();
        }
        display.update();
     }

     // Otherwise perform a simple sleep
     else {
        try {
           Thread.sleep(waitTimeMillis);
        }
        catch (InterruptedException e) {
           // ignored
        }
     }
  }
	
  /**
   * Wait until all background tasks are complete
   */
  @SuppressWarnings("deprecation")
	public void waitForJobs() {
     while (Platform.getJobManager().currentJob() != null)
        delay(1000);
  }	
  
  

}
