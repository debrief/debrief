package com.borlander.rac353542.bislider.cdata;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import com.borlander.rac353542.bislider.BiSliderUIModel;
import com.borlander.rac353542.bislider.DefaultBiSliderUIModel;

/**
 * Single pack of objects required to instantiate BiSlider working with custom
 * object data model of <code>java.util.Date</code> values, representing
 * midnights in the current time zone.
 */
public class CalendarDateSuite {
    private static final DateFormat ourDateFormat = DateFormat.getDateInstance();

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
            setTotalObjectRange(minimum, maximum);
        }
    }

    /**
     * Mapper for date's. Each data object is some <code>java.util.Date</code>
     * representing a midnight in current locale's time zone.
     */
    public static DataObjectMapper CALENDAR_DATE = new DataObjectMapper() {

        private final Calendar myCalendar = Calendar.getInstance();

        public Object double2object(double value) {
            myCalendar.setTimeInMillis(Math.round(value));
        //    setToMidnight();
            return myCalendar.getTime();
        }

        public double object2double(Object object) {
            myCalendar.setTime((Date) object);
         //   setToMidnight();
            return myCalendar.getTimeInMillis();
        }

        private void setToMidnight() {
            myCalendar.set(Calendar.HOUR, 0);
            myCalendar.set(Calendar.AM_PM, Calendar.AM);
            myCalendar.set(Calendar.MINUTE, 0);
            myCalendar.set(Calendar.SECOND, 0);
            myCalendar.set(Calendar.MILLISECOND, 0);
        }
    };
}
