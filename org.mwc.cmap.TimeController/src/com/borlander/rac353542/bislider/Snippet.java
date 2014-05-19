package com.borlander.rac353542.bislider;

import java.util.Calendar;
import java.util.Date;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import MWC.Utilities.TextFormatting.FormatRNDateTime;

import com.borlander.rac353542.bislider.cdata.*;
import com.borlander.rac353542.bislider.cdata.LongDataSuite.LongDataModel;

public class Snippet {

    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        decorateShell(shell);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    private static void decorateShell(Shell shell) {
        shell.setText("Bi-Slider demo (543)");
        shell.setLayout (new FillLayout (SWT.VERTICAL));

        Group simple = new Group(shell, SWT.NONE);
        simple.setText("All defaults");
        simple.setLayout(new FillLayout());
        BiSlider simpleSlider = BiSliderFactory.getInstance().createBiSlider(simple, null);
        
        /*
        simpleSlider.getDataModel().addListener(new BiSliderDataModel.Listener() {
            private boolean myInCompositeUpdate;
            
            public void dataModelChanged(BiSliderDataModel dataModel, boolean moreChangesExpectedInNearFuture) {
                if (moreChangesExpectedInNearFuture && myInCompositeUpdate){
                    return;
                }
                myInCompositeUpdate = moreChangesExpectedInNearFuture;
                System.err.println("dataModelChanges: isComposite: " + moreChangesExpectedInNearFuture);
            }
        });
        */
        
        simpleSlider.getWritableDataModel().setSegmentLength(30);
        
        DefaultBiSliderUIModel uiConfig = (DefaultBiSliderUIModel) simpleSlider.getUIModel();
        uiConfig.setHasLabelsAboveOrLeft(true);
        uiConfig.setHasLabelsBelowOrRight(true);
        
        Group longGroup = new Group(shell, SWT.NONE);
        longGroup.setText("Long values");
        longGroup.setLayout(new FillLayout());
        createLongBiSlider(longGroup);

        Group dateGroup = new Group(shell, SWT.NONE);
        dateGroup.setText("Date values");
        dateGroup.setLayout(new FillLayout());
        createDateBiSlider(dateGroup);

        shell.setSize (1000, 1000);
    }
    
    private static void createDateBiSlider(Composite parent) {
        final Calendar calendar = Calendar.getInstance();
        final long nowMillis = System.currentTimeMillis();
        calendar.setTimeInMillis(nowMillis);
        calendar.add(Calendar.YEAR, -1);
        Date yearAgo = calendar.getTime();
        
        calendar.setTimeInMillis(nowMillis);
        calendar.add(Calendar.MONTH, -3);
        Date threeMonthesAgo = calendar.getTime();
        
        calendar.setTimeInMillis(nowMillis);
        calendar.add(Calendar.MONTH, +4);
        Date fourMonthesFromNow = calendar.getTime();
        
        calendar.setTimeInMillis(nowMillis);
        calendar.add(Calendar.YEAR, +1);
        Date yearFromNow = calendar.getTime();
        
        BiSlider result = BiSliderFactory.getInstance().createCalendarDateBiSlider(parent, yearAgo, yearFromNow, null);
        
        CalendarDateSuite.CalendarDateModel model = (CalendarDateSuite.CalendarDateModel)result.getDataModel();
        model.setUserMinimum(threeMonthesAgo);
        model.setUserMaximum(fourMonthesFromNow);
        model.setSegmentCount(100);
        
        DefaultBiSliderUIModel uiConfig = (DefaultBiSliderUIModel)result.getUIModel();
        uiConfig.setBiSliderForegroundRGB(new RGB(255, 0, 255)); //ugly, I know
        uiConfig.setMaximumRGB(new RGB(255, 0, 0));
        uiConfig.setMinimumRGB(new RGB(255, 255, 0));
        uiConfig.setColorInterpolation(new ColorInterpolation.INTERPOLATE_CENTRAL(new RGB(0, 0, 127)));
        uiConfig.setContentsDataProvider(BiSliderContentsDataProvider.NORMAL_DISTRIBUTION);
        uiConfig.setHasLabelsAboveOrLeft(true);
        uiConfig.setHasLabelsBelowOrRight(false);
        uiConfig.setLabelInsets(65);
        
        DataObjectLabelProvider customLabelProvider = new DataObjectLabelProvider(model.getMapper()){
//            private final Date todayMidnight = (Date)CalendarDateSuite.CALENDAR_DATE.double2object(nowMillis);

       			public String getLabel(Object value)
      			{
      				// ok, convert to date
      				Date theDate = (Date) value;
      				
      				String res = FormatRNDateTime.toString(theDate.getTime());
      				return res;
      			}           
//       			
//            public String getLabel(Object dataObject) {
//                Date date = (Date)dataObject;
//                long deltaMillis = date.getTime() - todayMidnight.getTime();
//                long deltaInDays = deltaMillis / (1000L * 60 * 60 * 24);
//                if (deltaInDays == 0){
//                    return "Today";
//                }
//                if (deltaInDays > 0){
//                    return "+" + String.valueOf(deltaInDays) + " days" ;
//                } else {
//                    return String.valueOf(deltaInDays) + " days" ;    
//                }
//            }
        };
        
        uiConfig.setLabelProvider(customLabelProvider);
    }

    public static BiSlider createLongBiSlider(Composite parent){
        long base = 1000000000L;
        BiSlider result = BiSliderFactory.getInstance().createLongBiSlider(parent, base + 0, base + 100, null);
        //the safety of these casts is guarranteed -- see BiSliderFactory 
        LongDataModel dataModel = (LongDataModel)result.getDataModel(); 
        dataModel.setUserMaximum(new Long(base + 70));
        dataModel.setUserMinimum(new Long(base + 20));
        DefaultBiSliderUIModel uiConfig = (DefaultBiSliderUIModel)result.getUIModel();
        
        uiConfig.setLabelInsets(80);
        uiConfig.setVerticalLabels(true);
        uiConfig.setHasLabelsAboveOrLeft(false);
        uiConfig.setHasLabelsBelowOrRight(true);        
//        
//        uiConfig.setLabelInsets(SWT.DEFAULT);
//        uiConfig.setVerticalLabels(true);
//        uiConfig.setHasLabelsAboveOrLeft(true);
//        uiConfig.setHasLabelsBelowOrRight(true);

        return result; 
    }
    
}
