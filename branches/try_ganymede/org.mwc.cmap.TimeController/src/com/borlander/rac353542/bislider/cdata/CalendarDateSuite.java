package com.borlander.rac353542.bislider.cdata;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.*;

import com.borlander.rac353542.bislider.*;

/**
 * Single pack of objects required to instantiate BiSlider working with custom
 * object data model of <code>java.util.Date</code> values, representing
 * midnights in the current time zone.
 */
public class CalendarDateSuite {
    static final DateFormat ourDateFormat = DateFormat.getDateInstance();

    /** utility object used for converting between dates & millis
     * 
     */
    static final Calendar _myCalendar = Calendar.getInstance();

    
    public CalendarDateModel createDataModel(Date totalMin, Date totalMax, Date userMin, Date userMax) {
        CalendarDateModel result = new CalendarDateModel();
        result.setTotalRange(totalMin, totalMax);
        result.setUserMinimum(userMin);
        result.setUserMaximum(userMax);
        return result;
    }

    public BiSliderUIModel createUIModel() {
        DefaultBiSliderUIModel result = new DefaultBiSliderUIModel();
        result.setLabelProvider(createLabelProvider());
        // date values requires a lot of space
        result.setVerticalLabels(true);
        result.setLabelInsets(100);
        return result;
    }

    public DataObjectLabelProvider createLabelProvider() {
        return new DataObjectLabelProvider(CALENDAR_DATE) {
            public String getLabel(Object dataObject) {
                Date date = (Date) dataObject;
                return ourDateFormat.format(date);
            }
        };
    }

    public static class CalendarDateModel extends DataObjectDataModel {
        private Label_Limit[] _limits;

				public CalendarDateModel() {
            super(CALENDAR_DATE);
        }

        public Date getTotalMinimumDate() {
            return (Date) getTotalMinimumObject();
        }

        public Date getTotalMaximumDate() {
            return (Date) getTotalMaximumObject();
        }

        public Date getUserMinimumDate() {
            return (Date) getUserMinimumObject();
        }

        public Date getUserMaximumDate() {
            return (Date) getUserMaximumObject();
        }

        public void setUserMaximum(Date maximum) {
            setUserMaximumObject(maximum);
        }

        public void setUserMinimum(Date minimum) {
            setUserMinimumObject(minimum);
        }

        public void setTotalRange(Date minimum, Date maximum) {
        		// right, update the min & max values
            setTotalObjectRange(minimum, maximum);
            
            // ok, we now know the step size.  trim the start/end times to these values
            // ok, sort out the size of segments to use
            rescaleSegments(maximum.getTime() - minimum.getTime());
            long scaleStep = (long)getSegmentLength();
            
            // work out the start-time trimmed to our segment size (so the time-slider starts
            // on a whole figure
            _myCalendar.setTimeInMillis((minimum.getTime() / scaleStep) * scaleStep);
            
            // and send out another update...
            setTotalObjectRange(_myCalendar.getTime(), maximum);
            
        }
        

        // ok, sort out a nice, tidy segment size to use for our dates
        private void rescaleSegments(long timeRange)
        {
        	long scaleStep;

          // check we have our set of data
          if (_limits == null)
            initialiseLimits();

          // find the range we are working in
          int counter = 0;
          while ((counter < _limits.length - 1) &&
            (timeRange > _limits[counter].upper_limit))
          {
            counter++;
          }    	
          
          // cool, which is the respective step size
          scaleStep = _limits[counter].increment;
          
          // ok, update the GUI
          setSegmentLength(scaleStep);
        }
        
        /**
         * setup the list
         */
        private void initialiseLimits()
        {
        	long ONE_SECOND = 1000;
        	long ONE_MINUTE = ONE_SECOND * 60;
        	long ONE_HOUR = ONE_MINUTE * 60;
        	long ONE_DAY = ONE_HOUR * 24;    	
        	
          // create the array of limits values in a tmp parameter
        	// the structure of the data is as follows:
        	// for an overall time period of under 7 hours, 30 minute steps are used,
        	// for 7 or more hours, it switches to 1 hour time steps.  that's all.
          Label_Limit[] tmp = {
              new Label_Limit(ONE_SECOND * 7,            ONE_SECOND),         // 0
              new Label_Limit(ONE_SECOND * 35,            ONE_SECOND * 30),   // 1
              new Label_Limit(ONE_MINUTE * 12,            ONE_MINUTE),         // 2
              new Label_Limit(ONE_MINUTE * 120,            ONE_MINUTE * 15),   // 3
              new Label_Limit(ONE_HOUR * 3,            ONE_MINUTE * 30),   // 4
              new Label_Limit(ONE_HOUR * 8,            ONE_HOUR),         // 5
              new Label_Limit(ONE_HOUR * 13,            ONE_HOUR * 2),         // 6
              new Label_Limit(ONE_HOUR * 72,            ONE_HOUR * 12),       // 7
              new Label_Limit(ONE_DAY * 15,            ONE_DAY),               // 8
              new Label_Limit(ONE_DAY * 15,            ONE_DAY * 30),               // 8
              new Label_Limit(ONE_DAY * 32,            ONE_DAY * 60)               // 8
          		};

          // and now store the array in our local variable
          _limits = tmp;
        }   
        
        /////////////////////////////////////////////////////////////
        // scale limits and labels from a data range
        ////////////////////////////////////////////////////////////
        private static final class Label_Limit implements Serializable
        {
          /**
      		 * 
      		 */
      		private static final long serialVersionUID = 1L;
      		
      		long upper_limit;
          long increment;

          Label_Limit(long limit, long inc)
          {
            upper_limit = limit;
            increment = inc;
          }
        }            
        
    }

    /**
     * Mapper for date's. Each data object is some <code>java.util.Date</code>
     * representing a midnight in current locale's time zone.
     */
    public static DataObjectMapper CALENDAR_DATE = new DataObjectMapper() {

      public Object double2object(double value) {
          _myCalendar.setTimeInMillis(Math.round(value));
          return _myCalendar.getTime();
      }

      public double object2double(Object object) {
          _myCalendar.setTime((Date) object);
          return _myCalendar.getTimeInMillis();
      }

      public double getPrecision() {
          return 1000 * 30;
      }    	
    	
    };
    
}
