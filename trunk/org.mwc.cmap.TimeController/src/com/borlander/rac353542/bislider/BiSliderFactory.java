package com.borlander.rac353542.bislider;

import java.util.Date;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.TimeController.controls.DTGBiSlider.DoFineControl;

import com.borlander.rac353542.bislider.cdata.*;
import com.borlander.rac353542.bislider.impl.BiSliderFactoryImpl;

/**
 * Singleton access point to BiSlider implementation. This class is the only
 * place in this "api" package which depends on implementation package.
 */
public abstract class BiSliderFactory {

    private static BiSliderFactory ourInstance;

    public static BiSliderFactory getInstance() {
        if (ourInstance == null) {
            ourInstance = new BiSliderFactoryImpl();
        }
        return ourInstance;
    }

    /**
     * Creates BiSlider with specified data model and UI configuration.
     * @param fc TODO
     */
    public abstract BiSlider createBiSlider(Composite parent, BiSliderDataModel.Writable dataModel, BiSliderUIModel uiConfig, DoFineControl fc);

    /**
     * Creates BiSlider with double data model and default configuration
     * parameters.
     * <p>
     * Both data model and UI-configuration may be adjusted later using
     * <code>BiSlider#getDataModel()</code> and
     * <code>BiSlider#getUIModel()</code> methods.
     */
    public BiSlider createBiSlider(Composite parent, DoFineControl mHandler) {
        return createBiSlider(parent, new DefaultBiSliderDataModel(), new DefaultBiSliderUIModel(), mHandler);
    }

    /**
     * Convenient way to create BiSlider with custom objects data model
     * representing a set long values and default configuration parameters.
     * <p>
     * It is guarranteed that invocation of <code>BiSlider#getDataModel()</code>
     * method for result control will return an instance of
     * <code>LongDataSuite.LongDataModel</code>.
     * <p>
     * Result BiSlider will have appropriate default Label provider installed.
     * However, it (as well as any of other configuration options) may be adjusted
     * via <code>BiSlider#getDataModel()</code> and
     * <code>BiSlider#getUIModel()</code> methods.
     * <p>
     * This method may be considered as an example for construction of the
     * BiSlider with any other custom data object model.
     */
    public BiSlider createLongBiSlider(Composite parent, long totalMin, long totalMax, DoFineControl mHandler) {
        LongDataSuite suite = new LongDataSuite();
        LongDataSuite.LongDataModel dataModel = suite.createDataModel(totalMin, totalMax, totalMin, totalMax);
        BiSliderUIModel uiModel = suite.createUIModel();
        return createBiSlider(parent, dataModel, uiModel, mHandler);
    }

    /**
     * Convenient way to create BiSlider with custom objects data model
     * representing a set of <code>java.util.Date</code> values and default
     * configuration parameters.
     * <p>
     * It is guarranteed that invocation of <code>BiSlider#getDataModel()</code>
     * method for result control will return an instance of
     * <code>CalendarDateSuite.CalendarDataModel</code>.
     * <p>
     * Result BiSlider will have appropriate default Label provider installed.
     * However, it (as well as any of other configuration options) may be adjusted
     * via <code>BiSlider#getDataModel()</code> and
     * <code>BiSlider#getUIModel()</code> methods.
     * <p>
     * This method may be considered as an example for construction of the
     * BiSlider with any other custom data object model.
     */
    public BiSlider createCalendarDateBiSlider(Composite parent, Date totalMin, Date totalMax, DoFineControl mHandler) {
        CalendarDateSuite suite = new CalendarDateSuite();
        CalendarDateSuite.CalendarDateModel dataModel = suite.createDataModel(totalMin, totalMax, totalMin, totalMax);
        BiSliderUIModel uiModel = suite.createUIModel();
        return createBiSlider(parent, dataModel, uiModel, mHandler);
    }
}
