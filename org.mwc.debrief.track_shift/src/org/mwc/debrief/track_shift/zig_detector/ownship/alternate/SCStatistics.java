package org.mwc.debrief.track_shift.zig_detector.ownship.alternate;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * SCStatistics - utility methods of mathematical statistics. Various tools for extracting data patterns 
 * and making predictions about the vessels positions.
 */
public class SCStatistics {
	
    // Variance: used for variance analysis of potentially steady-intervals
    public static class Variance{
        public Variance (int _index, int _f, double _m2) {
            index = _index;
            f = _f;
            m2 = _m2;
        }
        
        int index;
        int f;          // degrees of freedom
        double m2;      // variance (sigma0 * sigma0)
    }
    
    // Used for sorting variances in ascending order
    static class SortVariances implements Comparator<Variance>
    {
        @Override
        public int compare(Variance a, Variance b) {
            return Double.compare(a.m2, b.m2);
        }
    }    
    
    // Results from linear regression analysis
    public static class RegrResults {
        int istart;
        int iend;
        
        double taumax; // maximal normalized deviation
        
        double sigma0; // standard deviation
        
        // estimated parameters of linear model: Yi = a*Xi + b
        double a;
        double b;
        
        // ... and their estimated standard error (i.e reciprocal accuracy)
        double ma;
        double mb;

        // parameters needed for performance ehancement (i.e. to avoid recalculating of the same sums again)
        double N11;
        double N12;
        double n1;
        double n2;
        double det;

        // true/false if regression line is/isn't horizontal (default is false)
        boolean isHorizontal = false;
    }
	
    //-------------------------------------------------------------------------
    /**
     * Creates a double array of time values from an List of totes
     * @param  totes given List of Tote objects
     * @return double[] - array of absolute times extracted from totes
     */
    public static double[] getTimes(List<Tote> totes) {
        return getTimes(totes, 0, totes.size());
    }

    //-------------------------------------------------------------------------
    /**
     * Creates a double array of absolute time values from an List of totes within the given indexes
     * @param  totes given List of Tote objects
     * @param  istart (inclusive) lower index of the totes-array segment extracted for calculation
     * @param  iend (exclusive) upper index of the totes-array segment extracted for calculation
     * @return double[] - array of absolute times extracted from totes
     */
    public static double[] getTimes(List<Tote> totes, int istart, int iend) {

        if (0 > istart)
            throw new IndexOutOfBoundsException("Calling ScStatistics.getTimes - 'istart' index (" + istart + ") is less than 0.");

        if (totes.size() < iend)
            throw new IndexOutOfBoundsException("Calling ScStatistics.getTimes - 'iend' index (" + iend + ") is greater than array length.");
        
        if (istart > iend)
            throw new NegativeArraySizeException("Calling ScStatistics.getTimes - 'istart' (" + istart + ") is less than 'iend' (" + iend + ").");
        
        int index = 0;
        double[] da = new double[iend - istart];
        for (int ii=istart; ii<iend; ii++)
            da[index++] = totes.get(ii).dabsolute_time;

        return da;
    }

    //-------------------------------------------------------------------------
    /**
     * Creates a double array of relative time values from an List of totes within the given indexes. The values are relative to the first time in list i.e the elapsed times from totes[istart] are recorded.
     * @param  totes given List of Tote objects
     * @return double[] - array of relative times extracted from totes
     */
    public static double[] getRelativeTimes(List<Tote> totes) {
        return getRelativeTimes(totes, 0, totes.size());
    }

    //-------------------------------------------------------------------------
    /**
     * Creates a double array of relative time values from an List of totes within the given indexes. The values are relative to the first time in list i.e the elapsed times from totes[istart] are recorded.
     * @param  totes given List of Tote objects
     * @param  istart (inclusive) lower index of the totes-array segment extracted for calculation
     * @param  iend (exclusive) upper index of the totes-array segment extracted for calculation
     * @return double[] - array of relative times extracted from totes
     */
    public static double[] getRelativeTimes(List<Tote> totes, int istart, int iend) {

        if (0 > istart)
            throw new IndexOutOfBoundsException("Calling ScStatistics.getRelativeTimes - 'istart' index (" + istart + ") is less than 0.");

        if (totes.size() < iend)
            throw new IndexOutOfBoundsException("Calling ScStatistics.getRelativeTimes - 'iend' index (" + iend + ") is greater than array length.");
        
        if (istart > iend)
            throw new NegativeArraySizeException("Calling ScStatistics.getRelativeTimes - 'istart' (" + istart + ") is less than 'iend' (" + iend + ").");
        
        int index = 0;
        double dreduce = totes.get(istart).dabsolute_time;
        double[] da = new double[iend - istart];
        for (int ii=istart; ii<iend; ii++)
            da[index++] = totes.get(ii).dabsolute_time - dreduce;

        return da;
    }

    //-------------------------------------------------------------------------
    /**
     * Creates a double array of speed values from an List of totes
     * @param  totes given List of Tote objects
     * @return double[] - array of speeds extracted form totes
     */
    public static double[] getSpeeds(List<Tote> totes) {

        int index = 0;
        double[] da = new double[totes.size()];
        Iterator<Tote> iter = totes.iterator();
        while (iter.hasNext())
            da[index++] = iter.next().dspeed;

        return da;
    }

    //-------------------------------------------------------------------------
    /**
     * Creates a double array of speed values from an List of totes within the given indexes
     * @param  totes given List of Tote objects
     * @param  istart (inclusive) lower index of the totes-array segment extracted for calculation
     * @param  iend (exclusive) upper index of the totes-array segment extracted for calculation
     * @return double[] - array of speeds extracted form totes
     */
    public static double[] getSpeeds(List<Tote> totes, int istart, int iend) {

        if (0 > istart)
            throw new IndexOutOfBoundsException("Calling ScStatistics.getSpeeds - 'istart' index (" + istart + ") is less than 0.");

        if (totes.size() < iend)
            throw new IndexOutOfBoundsException("Calling ScStatistics.getSpeeds - 'iend' index (" + iend + ") is greater than array length.");
        
        if (istart > iend)
            throw new NegativeArraySizeException("Calling ScStatistics.getSpeeds - 'istart' (" + istart + ") is less than 'iend' (" + iend + ").");
        
        int index = 0;
        double[] da = new double[iend - istart];
        for (int ii=istart; ii<iend; ii++)
            da[index++] = totes.get(ii).dspeed;

        return da;
    }

    //-------------------------------------------------------------------------
    /**
     * Creates a double array of heading values from an List of totes
     * @param  totes given List of Tote objects
     * @return double[] - array of headings extracted form totes
     */
    public static double[] getHeadings(List<Tote> totes) {

        double[] dl = new double[totes.size()];
        int index = 0;
        Iterator<Tote> iter = totes.iterator();
        while (iter.hasNext())
            dl[index++] = iter.next().dheading;

        return dl;
    }
	
    //-------------------------------------------------------------------------
    /**
     * Creates a double array of sheading values from an List of totes within the given indexes
     * @param  totes given List of Tote objects
     * @param  istart (inclusive) lower index of the totes-array segment extracted for calculation
     * @param  iend (exclusive) upper index of the totes-array segment extracted for calculation
     * @return double[] - array of headings extracted form totes
     */
    public static double[] getHeadings(List<Tote> totes, int istart, int iend) {

        if (0 > istart)
            throw new IndexOutOfBoundsException("Calling ScStatistics.getHeadings - 'istart' index (" + istart + ") is less than 0.");

        if (totes.size() < iend)
            throw new IndexOutOfBoundsException("Calling ScStatistics.getHeadings - 'iend' index (" + iend + ") is greater than array length.");
        
        if (istart > iend)
            throw new NegativeArraySizeException("Calling ScStatistics.getHeadings - 'istart' (" + istart + ") is less than 'iend' (" + iend + ").");
        
        int index = 0;
        double[] da = new double[iend - istart];
        for (int ii=istart; ii<iend; ii++)
            da[index++] = totes.get(ii).dheading;

        return da;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns maximal value from an array of doubles
     * @param  ardoubles given array
     * @return double - max value within the array
     */
    public static double max(double[] ardoubles) {

        double dmax = -Double.MAX_VALUE;
        for (int ii=0; ii<ardoubles.length; ii++)
            dmax = Double.max(dmax, ardoubles[ii]);

        return dmax;
    }
	
    //-------------------------------------------------------------------------
    /**
     * Returns minimal value from an array of doubles
     * @param  ardoubles given array
     * @return double - min value within the array
     */
    public static double min(double[] ardoubles) {

        double dmin = Double.MAX_VALUE;
        for (int ii=0; ii<ardoubles.length; ii++)
                dmin = Double.min(dmin, ardoubles[ii]);

        return dmin;
    }
	
    //-------------------------------------------------------------------------
    /**
     * Returns maximal value from a sub-array within an array of doubles
     * @param  ardoubles given array
     * @param  istart (inclusive) lower index of the sub-array
     * @param  iend (exclusive) upper index of the sub-array
     * @return double - max value within the array
     */
    public static double max(double[] ardoubles, int istart, int iend) {

        if (0 > istart)
            throw new IndexOutOfBoundsException("Calling ScStatistics.max - 'istart' index (" + istart + ") is less than 0.");

        if (ardoubles.length < iend)
            throw new IndexOutOfBoundsException("Calling ScStatistics.max - 'iend' index (" + iend + ") is greater than array length.");

        double dmax = -Double.MAX_VALUE;
        for (int ii=istart; ii<iend; ii++)
            dmax = Double.max(dmax, ardoubles[ii]);

        return dmax;
    }
	
    //-------------------------------------------------------------------------
    /**
     * Returns minimal value from a sub-array within an array of doubles
     * @param  ardoubles given array
     * @param  istart (inclusive) lower index of the sub-array
     * @param  iend (exclusive) upper index of the sub-array
     * @return double - min value within the array
     */
    public static double min(double[] ardoubles, int istart, int iend) {

        if (0 > istart)
            throw new IndexOutOfBoundsException("Calling ScStatistics.min - 'istart' index (" + istart + ") is less than 0.");

        if (ardoubles.length < iend)
            throw new IndexOutOfBoundsException("Calling ScStatistics.min - 'iend' index (" + iend + ") is greater than array length.");

        double dmin = Double.MAX_VALUE;
        for (int ii=istart; ii<iend; ii++)
            dmin = Double.min(dmin, ardoubles[ii]);

        return dmin;
    }
	
    //-------------------------------------------------------------------------
    /**
     * Returns mean from an array of normally distributed values
     * @param  ardoubles given array
     * @return double - mean value (calculated as simple arithmetic mean)
     */
    public static double mean(double[] ardoubles) {

        double dsum = 0.0;
        for (int ii=0; ii<ardoubles.length; ii++)
            dsum += ardoubles[ii];

        return dsum / ardoubles.length;
    }
	
    //-------------------------------------------------------------------------
    /**
     * Returns mean of an sub-array from an array of normally distributed values
     * @param  ardoubles given array
     * @param  istart (inclusive) lower index of the sub-array
     * @param  iend (exclusive) upper index of the sub-array
     * @return double - mean value (calculated as simple arithmetic mean)
     */
    public static double mean(double[] ardoubles, int istart, int iend) {

        if (0 > istart)
            throw new IndexOutOfBoundsException("Calling ScStatistics.mean - 'istart' index (" + istart + ") is less than 0.");

        if (ardoubles.length < iend)
            throw new IndexOutOfBoundsException("Calling ScStatistics.mean - 'iend' index (" + iend + ") is greater than array length.");

        double dsum = 0.0;
        for (int ii=istart; ii<iend; ii++)
            dsum += ardoubles[ii];

        return dsum / (iend - istart);
    }
	
    //-------------------------------------------------------------------------
    /**
     * Returns mean from an array of values with different 'weight' (e.g. probability)
     * @param  ardoubles given array of values
     * @param  arweights given array of weights
     * @return double - mean value (calculated as weighted arithmetic mean)
     */
    public static double weighted_mean(double[] ardoubles, double[] arweights){

        if (ardoubles.length != arweights.length)
            throw new IndexOutOfBoundsException("Calling ScStatistics.weighted_mean - arrays of different length");
        
        double dsum = 0.0;
        double dsumweights = 0.0;
        for (int ii=0; ii<ardoubles.length; ii++) {
            dsum += ardoubles[ii] * arweights[ii];
            dsumweights += arweights[ii];
        }

        return dsum / dsumweights;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Returns standard deviation an array of normally distributed values (expected mean value is unknown)
     * @param  ardoubles given array
     * @return double - standard deviation (calculated as simple arithmetic mean)
     */
    public static double stdev(double[] ardoubles) {

        double dsum = 0.0;
        double dsum_squares = 0.0;
        for (int ii=0; ii<ardoubles.length; ii++) {
            dsum += ardoubles[ii];
            dsum_squares += ardoubles[ii]*ardoubles[ii];
        }

        double dmean = dsum / ardoubles.length;
        double d1 = dsum_squares - dmean*dmean * ardoubles.length;
        if(d1 < 1e-9) return 0.0;
        double d2 = d1 / (ardoubles.length - 1);
        double d3 = Math.sqrt(d2);
    
        return d3;
}

    //-------------------------------------------------------------------------
    /**
     * Returns standard deviation from a sub-array of normally distributed values (expected mean value is unknown)
     * @param  ardoubles given array
     * @param  istart (inclusive) lower index of the sub-array
     * @param  iend (exclusive) upper index of the sub-array
     * @return double - standard deviation (calculated as simple arithmetic mean)
     */
    public static double stdev(double[] ardoubles, int istart, int iend) {

        if (0 > istart)
            throw new IndexOutOfBoundsException("Calling ScStatistics.stdev - 'istart' index (" + istart + ") is less than 0.");

        if (ardoubles.length < iend)
            throw new IndexOutOfBoundsException("Calling ScStatistics.stdev - 'iend' index (" + iend + ") is greater than array length.");

        int numelemets = iend - istart;
        double dsum = 0.0;
        double dsum_squares = 0.0;
        for (int ii=istart; ii<iend; ii++) {
            dsum += ardoubles[ii];
            dsum_squares += ardoubles[ii]*ardoubles[ii];
        }

        double dmean = dsum / numelemets;
        double d1 = dsum_squares - dmean*dmean * numelemets;
        if(d1 < 1e-9) return 0.0;
        double d2 = d1 / (numelemets - 1);
        double d3 = Math.sqrt(d2);

        return d3;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns standard deviation an array of normally distributed values (please note: expected 'mean' value is known)
     * @param  ardoubles given array
     * @param  dmean the expected value of stochastic variable which is represented by array values
     * @return double - standard deviation (calculated as simple arithmetic mean)
     */
    public static double stdev_mn(double[] ardoubles, double dmean) {

        double dsum_squares = 0.0;
        for (int ii=0; ii<ardoubles.length; ii++)
            dsum_squares += ardoubles[ii]*ardoubles[ii];

        double d1 = dsum_squares - dmean*dmean * ardoubles.length;
        if(d1 < 1e-9) return 0.0; 
        double d2 = d1 / ardoubles.length;
        double d3 = Math.sqrt(d2);

        return d3;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns standard deviation from an array of values with different 'weight' (e.g. probability)
     * @param  ardoubles given array
     * @param  arweights given array of weights
     * @return double - standard deviation (calculated as simple arithmetic mean)
     */
    public static double weighted_stdev(double[] ardoubles, double[] arweights) {

        if (ardoubles.length != arweights.length)
            throw new IndexOutOfBoundsException("Calling ScStatistics.weighted_mean - arrays of different length");

        double dsum = 0.0;
        double dsum_squares = 0.0;
        double dsumweights = 0.0;
        for (int ii=0; ii<ardoubles.length; ii++) {
            double dtemp = ardoubles[ii] * arweights[ii];
            dsum += dtemp;
            dtemp *= ardoubles[ii];
            dsum_squares += dtemp;
            dsumweights += arweights[ii];
        }

        double dmean = dsum / dsumweights;
        double d1 = dsum_squares - dmean*dmean * dsumweights;
        if(d1 < 1e-9) return 0.0;
        double d2 = d1 / dsumweights;
        double d3 = Math.sqrt(d2);

        return d3;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns two sided quantile of Student's distribution with the 95% confidence interval
     * @param  inum number of observed/measured data in dataset
     * @return double - quantile of Student's distribution
     * @see https://en.wikipedia.org/wiki/Student%27s_t-distribution
     */
    static double get95StudentQuantil(int inum) {

        if (inum < 1) 
            throw new IllegalArgumentException("Calling ScStatistics.get95StudentQuantil - number of elements leass than 1");
        
        final double[] quantiles_1_30 = {
        12.71, 4.303, 3.182, 2.776, 2.571, 2.447, 2.365, 2.306, 2.262, 2.228, 
        2.201, 2.179, 2.160, 2.145, 2.131, 2.120, 2.110, 2.101, 2.093, 2.086,
        2.080, 2.074, 2.069, 2.064, 2.060, 2.056, 2.052, 2.048, 2.045, 2.042
        }; 
        
        final double[] quantiles_32_50 = { // increment = 2
        2.037, 2.032, 2.028, 2.024, 2.021, 2.018, 2.015, 2.013, 2.011, 2.009
        };

        final double[] quantiles_55_70 = { // increment = 5
        2.004, 2.000, 1.997, 1.994 
        };
        
        final double[] quantiles_80_100 = { // increment = 10
        1.990, 1.987, 1.984 
        };
        
        final double quantil_120 = 1.980;
        
        final double[] quantiles_150_300 = { // increment = 50
        1.976, 1.972, 1.970, 1.968 
        };
        
        final double[] quantiles_400_500 = { // increment = 100
        1.966, 1.965 
        };
        
        final double quantil_infinity = 1.960;
        
        if (inum < 30) return quantiles_1_30[inum-1];
        else if (inum < 32) return quantiles_1_30[29]; // 2.042
        else if (inum <= 50) return quantiles_32_50[(inum-32) / 2];
        else if (inum < 55) return quantiles_32_50[9]; // 2.009
        else if (inum <= 70) return quantiles_55_70[(inum-55) / 5];
        else if (inum < 80) return quantiles_55_70[3]; // 1.994
        else if (inum <= 100) return quantiles_80_100[(inum-80) / 10];
        else if (inum < 120) return quantiles_80_100[2]; // 1.984
        else if (inum < 150) return quantil_120; // 1.980
        else if (inum <= 300) return quantiles_150_300[(inum-150) / 50];
        else if (inum < 400) return quantiles_150_300[3]; // 1.968
        else if (inum < 500) return quantiles_400_500[0]; // 1.966
        else if (inum < 700) return quantiles_400_500[1]; // 1.965
        else
            return quantil_infinity; // 1.96
    }
    
    //-------------------------------------------------------------------------
    /**
     * Returns two sided quantile of Student's distribution with the 99% confidence interval
     * @param  inum number of observed/measured data in dataset
     * @return double - quantile of Student's distribution
     */
    static double get99StudentQuantil(int inum) {

        if (inum < 1) 
            throw new IllegalArgumentException("Calling ScStatistics.get99StudentQuantil - number of elements leass than 1");
        
        final double[] quantiles_1_30 = {
        63.66, 9.925, 5.841, 4.604, 4.032, 3.707, 3.499, 3.355, 3.250, 3.169,
        3.106, 3.055, 3.012, 2.977, 2.947, 2.921, 2.898, 2.878, 2.861, 2.845,
        2.831, 2.819, 2.807, 2.797, 2.787, 2.779, 2.771, 2.763, 2.756, 2.750
        }; 
        
        final double[] quantiles_32_50 = { // increment = 2
        2.739, 2.728, 2.720, 2.712, 2.705, 2.698, 2.692, 2.687, 2.682, 2.678
        };

        final double[] quantiles_55_70 = { // increment = 5
        2.668, 2.660, 2.654, 2.648
        };
        
        final double[] quantiles_80_100 = { // increment = 10
        2.639, 2.632, 2.626
        };
        
        final double quantil_120 = 2.617;
        
        final double[] quantiles_150_300 = { // increment = 50 
        2.609, 2.601, 2.596, 2.592
        };
        
        final double[] quantiles_400_500 = { // increment = 100
        2.588, 2.586 
        };
        
        final double quantil_infinity = 2.576;
        
        if (inum < 30) return quantiles_1_30[inum-1];
        else if (inum < 32) return quantiles_1_30[29]; // 2.042
        else if (inum <= 50) return quantiles_32_50[(inum-32) / 2];
        else if (inum < 55) return quantiles_32_50[9]; // 2.009
        else if (inum <= 70) return quantiles_55_70[(inum-55) / 5];
        else if (inum < 80) return quantiles_55_70[3]; // 1.994
        else if (inum <= 100) return quantiles_80_100[(inum-80) / 10];
        else if (inum < 120) return quantiles_80_100[2]; // 1.984
        else if (inum < 150) return quantil_120; // 1.980
        else if (inum <= 300) return quantiles_150_300[(inum-150) / 50];
        else if (inum < 400) return quantiles_150_300[3]; // 1.968
        else if (inum < 500) return quantiles_400_500[0]; // 1.966
        else if (inum < 700) return quantiles_400_500[1]; // 1.965
        else
            return quantil_infinity; // 1.96
    }
    
    //-------------------------------------------------------------------------
    /**
     * Returns two sided quantile of Student's distribution with the 99.9% confidence interval
     * @param  inum number of observed/measured data in dataset
     * @return double - quantile of Student's distribution
     */
    static double get999StudentQuantil(int inum) {

        if (inum < 1) 
            throw new IllegalArgumentException("Calling ScStatistics.get999StudentQuantil - number of elements leass than 1");
        
        final double[] quantiles_1_30 = {
        636.6, 31.96, 12.92, 8.610, 6.869, 5.959, 5.408, 5.041, 4.718, 4.587,    
        4.437, 4.318, 4.221, 4.140, 4.073, 4.015, 3.965, 3.922, 3.883, 3.850,
        3.819, 3.792, 3.767, 3.745, 3.725, 3.707, 3.690, 3.674, 3.659, 3.646
        }; 
        
        final double[] quantiles_32_50 = { // increment = 2
        3.622, 3.601, 3.582, 3.566, 3.551, 3.538, 3.526, 3.515, 3.505, 3.496
        };

        final double[] quantiles_55_70 = { // increment = 5
        3.476, 3.460, 3.447, 3.435
        };
        
        final double[] quantiles_80_100 = { // increment = 10
        3.416, 3.402, 3.390
        };
        
        final double quantil_120 = 3.374;
        
        final double[] quantiles_150_300 = { // increment = 50 
        3.357, 3.340, 3.330, 3.323
        };
        
        final double[] quantiles_400_500 = { // increment = 100
        3.315, 3.310 
        };
        
        final double quantil_infinity = 3.291;
        
        if (inum < 30) return quantiles_1_30[inum-1];
        else if (inum < 32) return quantiles_1_30[29]; // 2.042
        else if (inum <= 50) return quantiles_32_50[(inum-32) / 2];
        else if (inum < 55) return quantiles_32_50[9]; // 2.009
        else if (inum <= 70) return quantiles_55_70[(inum-55) / 5];
        else if (inum < 80) return quantiles_55_70[3]; // 1.994
        else if (inum <= 100) return quantiles_80_100[(inum-80) / 10];
        else if (inum < 120) return quantiles_80_100[2]; // 1.984
        else if (inum < 150) return quantil_120; // 1.980
        else if (inum <= 300) return quantiles_150_300[(inum-150) / 50];
        else if (inum < 400) return quantiles_150_300[3]; // 1.968
        else if (inum < 500) return quantiles_400_500[0]; // 1.966
        else if (inum < 700) return quantiles_400_500[1]; // 1.965
        else
            return quantil_infinity; // 1.96
    }
    
    //-------------------------------------------------------------------------
    /**
     * This function filters out an array of steady-values intervals (filtered out because of non-homogeneous variance). Equal range variance statistical test.  
     * @param intervals intervals of steady values (heading or speeds) for analyzing. 
     * @param values the array of input values (headings or speeds)
     * @param  steady_stdev interval will not be considered redundant if its standard deviation is within the limits defined 
     * by this parameter (this parameter is usually the decimal precision of written values)
     * @return referent variance and degrees of freedom. (needed in sieve algorithms)
     */
    public static Variance isolateNonHomogeneous(List<SCAlgorithms.SpanPair> intervals, double[] values, double steady_stdev) {

        Variance retval = new Variance(0, 1, 1.0);

        List<Variance> _variances = new ArrayList<>();
        for(int ii=0; ii<intervals.size(); ii++) {
            int index_start = intervals.get(ii).first;
            int index_end = intervals.get(ii).second;
            int numelements = index_end - index_start;
            double dstdev = SCStatistics.stdev(values, index_start, index_end);
            _variances.add(new SCStatistics.Variance(ii, numelements-1,  dstdev*dstdev));
        }

        Collections.sort(_variances, new SortVariances());
        
        // Remove wrong intervals (peaks, holes). They all have non-homogeneous variance.

        // Find them...
        int f0 = _variances.get(0).f;
        double m20 = _variances.get(0).m2;
        Variance disp = new Variance(_variances.size(), f0, m20);
        List<Integer> for_remove = new ArrayList<>();
        for(int ii = 1; ii < _variances.size(); ii++  ) {
            Variance disp_i = _variances.get(ii);
            double d1 = Math.sqrt(disp_i.m2);
            double d2 = 3.0 * steady_stdev; //todo: 0.5 * SCConstants.*_STEADY_RANGE;
            if(d1 < d2) {
                disp.f = disp_i.f;
                disp.m2 = disp_i.m2;                
                continue;
            }

            if(disp.m2 < 1e-6) disp.m2 = 3.0 * steady_stdev; // prevent F-test with zero-value and devzero error

            double test = disp_i.m2 / disp.m2;
            double quantile = get99FQuantile(disp_i.f, disp.f);
            if(test <= quantile) {
                disp.f = disp_i.f;
                disp.m2 = disp_i.m2;
                retval.index = disp_i.index;
                retval.f = disp_i.f;
                retval.m2 = disp_i.m2;
            }
            else {
                // Take the last good variance (the biggist one) as reference variance for sieve algorithm. 
                Variance ref = _variances.get(ii-1);
                retval.index = ref.index;
                retval.f = ref.f;
                retval.m2 = ref.m2;
                for(int jj=ii; jj<_variances.size(); jj++)
                    for_remove.add(_variances.get(jj).index);

                break;
            }
        }

        // ... and remove them
        Collections.sort(for_remove);
        for(int jj = for_remove.size()-1; jj >= 0; jj--) {
            Integer myint = for_remove.get(jj);
            intervals.remove(myint.intValue());
        }

        return retval;
    }

    //-------------------------------------------------------------------------
    /**
     * Equal mean values statistical test. Returns true if two mean values 
     * can be consider stochastically equal (confidence interval - 95%).
     * The assumption is that the both datasets are homogenous in the sense of 
     * the measurement accuracy. (e.g. measured with the same sensor)
     * @param  dmean1 first mean
     * @param  dstdev1 standard deviation of measurements (values) in the first dataset
     * @param  inum1 number of elements in the first dataset
     * @param  dmean2 second  mean
     * @param  dstdev2 standard deviation of measurements (values) in the second dataset
     * @param  inum2 number of elements in the second dataset
     * @return  boolean - true if means can be considered equal (in the stochastic sense)
     */
    public static boolean areMeansEqual(double dmean1, double dstdev1, int inum1, 
                                        double dmean2, double dstdev2, int inum2) {
        
        final double quantil95 = get95StudentQuantil(Integer.min(inum1, inum2));
        
        double dstdev =  Math.sqrt(dstdev1*dstdev1 / inum1 + dstdev2*dstdev2 / inum2);
        double ddiff = Math.abs(dmean1 - dmean2);
        double dtest_value = ddiff / dstdev;
        
        return dtest_value <= quantil95;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Returns quantile of ranges (normal Gaussian distribution) with the 99% confidence interval
     * @param num number of elements
     * @return double - the resulting quantile
     */
    public static double get99RangeQuantile(int num) {
    	
        if (num < 2) 
            throw new IllegalArgumentException("Calling ScStatistics.get99FQuantil - degrees of freedoms less than 1");

        // The table has been created applying the Monte-Carlo method with 1e6 repeats. (The code can be exposed, if needed)
        double[] rangeTable = {
// num =    2       3       4       5       6       7       8       9       10      11      12      13      14      15
           3.886,  4.346,  4.621,  4.815,  4.965,  5.087,  5.188,  5.277,  5.354,  5.422,  5.484,  5.540,  5.591,  5.638,
           
// num =    16      17      18      19      20      60      100     200     350     500
           5.682,  5.723,  5.761,  5.797,  5.831,  6.509,  6.801,  7.178,  7.470,  7.650
        };

        // taking from the table directly
        if(num <= 20)
            return rangeTable[num-2];
        
        // linear interpolation
	else if(num <= 60)
            return linterpolation(rangeTable[18], rangeTable[19], (num-20), (60-num));

        else if(num <= 100)
            return linterpolation(rangeTable[19], rangeTable[20], (num-60), (100-num));

        else if(num <= 200)
            return linterpolation(rangeTable[20], rangeTable[21], (num-100), (200-num));

        else if(num <= 350)
            return linterpolation(rangeTable[21], rangeTable[22], (num-200), (350-num));

        else
            return linterpolation(rangeTable[22], rangeTable[23], (num-350), (500-num));

    }

    //-------------------------------------------------------------------------
    /**
     * Returns quantile of F-distribution with the 95% confidence interval
     * @param  f1 numerator degrees of freedom
     * @param  f2 denominator degrees of freedom
     * @return double - quantile of F-distribution
     * @see https://en.wikipedia.org/wiki/F-distribution
     * @see http://www.socr.ucla.edu/Applets.dir/F_Table.html#FTable0.05
     * @todo There are more precise and bigger tables. Implement one of them.
     */
    public static double get95FQuantile(int f1, int f2) {

        if (f1 < 1 || f2 < 1) 
            throw new IllegalArgumentException("Calling ScStatistics.get95F_DistributionQuantil - degrees of freedoms less than 1");

        double[][] F95table = {
//        f1 = 1         2         3         4         5         6         7         8         9         10        12        15        20        24        30        40        60       120        ∞
/*f2=1 */  {161.4476, 199.5000, 215.7073, 224.5832, 230.1619, 233.9860, 236.7684, 238.8827, 240.5433, 241.8817, 243.9060, 245.9499, 248.0131, 249.0518, 250.0951, 251.1432, 252.1957, 253.2529, 254.3144},
/*   2 */  { 18.5128,  19.0000,  19.1643,  19.2468,  19.2964,  19.3295,  19.3532,  19.3710,  19.3848,  19.3959,  19.4125,  19.4291,  19.4458,  19.4541,  19.4624,  19.4707,  19.4791,  19.4874,  19.4957},
/*   3 */  { 10.1280,   9.5521,   9.2766,   9.1172,   9.0135,   8.9406,   8.8867,   8.8452,   8.8123,   8.7855,   8.7446,   8.7029,   8.6602,   8.6385,   8.6166,   8.5944,   8.5720,   8.5494,   8.5264},
/*   4 */  {  7.7086,   6.9443,   6.5914,   6.3882,   6.2561,   6.1631,   6.0942,   6.0410,   5.9988,   5.9644,   5.9117,   5.8578,   5.8025,   5.7744,   5.7459,   5.7170,   5.6877,   5.6581,   5.6281},
/*   5 */  {  6.6079,   5.7861,   5.4095,   5.1922,   5.0503,   4.9503,   4.8759,   4.8183,   4.7725,   4.7351,   4.6777,   4.6188,   4.5581,   4.5272,   4.4957,   4.4638,   4.4314,   4.3985,   4.3650},

/*   6 */  {  5.9874,   5.1433,   4.7571,   4.5337,   4.3874,   4.2839,   4.2067,   4.1468,   4.0990,   4.0600,   3.9999,   3.9381,   3.8742,   3.8415,   3.8082,   3.7743,   3.7398,   3.7047,   3.6689},
/*   7 */  {  5.5914,   4.7374,   4.3468,   4.1203,   3.9715,   3.8660,   3.7870,   3.7257,   3.6767,   3.6365,   3.5747,   3.5107,   3.4445,   3.4105,   3.3758,   3.3404,   3.3043,   3.2674,   3.2298},
/*   8 */  {  5.3177,   4.4590,   4.0662,   3.8379,   3.6875,   3.5806,   3.5005,   3.4381,   3.3881,   3.3472,   3.2839,   3.2184,   3.1503,   3.1152,   3.0794,   3.0428,   3.0053,   2.9669,   2.9276},
/*   9 */  {  5.1174,   4.2565,   3.8625,   3.6331,   3.4817,   3.3738,   3.2927,   3.2296,   3.1789,   3.1373,   3.0729,   3.0061,   2.9365,   2.9005,   2.8637,   2.8259,   2.7872,   2.7475,   2.7067},
/*  10 */  {  4.9646,   4.1028,   3.7083,   3.4780,   3.3258,   3.2172,   3.1355,   3.0717,   3.0204,   2.9782,   2.9130,   2.8450,   2.7740,   2.7372,   2.6996,   2.6609,   2.6211,   2.5801,   2.5379},

/*  11 */  {  4.8443,   3.9823,   3.5874,   3.3567,   3.2039,   3.0946,   3.0123,   2.9480,   2.8962,   2.8536,   2.7876,   2.7186,   2.6464,   2.6090,   2.5705,   2.5309,   2.4901,   2.4480,   2.4045},
/*  12 */  {  4.7472,   3.8853,   3.4903,   3.2592,   3.1059,   2.9961,   2.9134,   2.8486,   2.7964,   2.7534,   2.6866,   2.6169,   2.5436,   2.5055,   2.4663,   2.4259,   2.3842,   2.3410,   2.2962},
/*  13 */  {  4.6672,   3.8056,   3.4105,   3.1791,   3.0254,   2.9153,   2.8321,   2.7669,   2.7144,   2.6710,   2.6037,   2.5331,   2.4589,   2.4202,   2.3803,   2.3392,   2.2966,   2.2524,   2.2064},
/*  14 */  {  4.6001,   3.7389,   3.3439,   3.1122,   2.9582,   2.8477,   2.7642,   2.6987,   2.6458,   2.6022,   2.5342,   2.4630,   2.3879,   2.3487,   2.3082,   2.2664,   2.2229,   2.1778,   2.1307},
/*  15 */  {  4.5431,   3.6823,   3.2874,   3.0556,   2.9013,   2.7905,   2.7066,   2.6408,   2.5876,   2.5437,   2.4753,   2.4034,   2.3275,   2.2878,   2.2468,   2.2043,   2.1601,   2.1141,   2.0658},

/*  16 */  {  4.4940,   3.6337,   3.2389,   3.0069,   2.8524,   2.7413,   2.6572,   2.5911,   2.5377,   2.4935,   2.4247,   2.3522,   2.2756,   2.2354,   2.1938,   2.1507,   2.1058,   2.0589,   2.0096},
/*  17 */  {  4.4513,   3.5915,   3.1968,   2.9647,   2.8100,   2.6987,   2.6143,   2.5480,   2.4943,   2.4499,   2.3807,   2.3077,   2.2304,   2.1898,   2.1477,   2.1040,   2.0584,   2.0107,   1.9604},
/*  18 */  {  4.4139,   3.5546,   3.1599,   2.9277,   2.7729,   2.6613,   2.5767,   2.5102,   2.4563,   2.4117,   2.3421,   2.2686,   2.1906,   2.1497,   2.1071,   2.0629,   2.0166,   1.9681,   1.9168},
/*  19 */  {  4.3807,   3.5219,   3.1274,   2.8951,   2.7401,   2.6283,   2.5435,   2.4768,   2.4227,   2.3779,   2.3080,   2.2341,   2.1555,   2.1141,   2.0712,   2.0264,   1.9795,   1.9302,   1.8780},
/*  20 */  {  4.3512,   3.4928,   3.0984,   2.8661,   2.7109,   2.5990,   2.5140,   2.4471,   2.3928,   2.3479,   2.2776,   2.2033,   2.1242,   2.0825,   2.0391,   1.9938,   1.9464,   1.8963,   1.8432},

/*  21 */  {  4.3248,   3.4668,   3.0725,   2.8401,   2.6848,   2.5727,   2.4876,   2.4205,   2.3660,   2.3210,   2.2504,   2.1757,   2.0960,   2.0540,   2.0102,   1.9645,   1.9165,   1.8657,   1.8117},
/*  22 */  {  4.3009,   3.4434,   3.0491,   2.8167,   2.6613,   2.5491,   2.4638,   2.3965,   2.3419,   2.2967,   2.2258,   2.1508,   2.0707,   2.0283,   1.9842,   1.9380,   1.8894,   1.8380,   1.7831},
/*  23 */  {  4.2793,   3.4221,   3.0280,   2.7955,   2.6400,   2.5277,   2.4422,   2.3748,   2.3201,   2.2747,   2.2036,   2.1282,   2.0476,   2.0050,   1.9605,   1.9139,   1.8648,   1.8128,   1.7570},
/*  24 */  {  4.2597,   3.4028,   3.0088,   2.7763,   2.6207,   2.5082,   2.4226,   2.3551,   2.3002,   2.2547,   2.1834,   2.1077,   2.0267,   1.9838,   1.9390,   1.8920,   1.8424,   1.7896,   1.7330},
/*  25 */  {  4.2417,   3.3852,   2.9912,   2.7587,   2.6030,   2.4904,   2.4047,   2.3371,   2.2821,   2.2365,   2.1649,   2.0889,   2.0075,   1.9643,   1.9192,   1.8718,   1.8217,   1.7684,   1.7110},

/*  26 */  {  4.2252,   3.3690,   2.9752,   2.7426,   2.5868,   2.4741,   2.3883,   2.3205,   2.2655,   2.2197,   2.1479,   2.0716,   1.9898,   1.9464,   1.9010,   1.8533,   1.8027,   1.7488,   1.6906},
/*  27 */  {  4.2100,   3.3541,   2.9604,   2.7278,   2.5719,   2.4591,   2.3732,   2.3053,   2.2501,   2.2043,   2.1323,   2.0558,   1.9736,   1.9299,   1.8842,   1.8361,   1.7851,   1.7306,   1.6717},
/*  28 */  {  4.1960,   3.3404,   2.9467,   2.7141,   2.5581,   2.4453,   2.3593,   2.2913,   2.2360,   2.1900,   2.1179,   2.0411,   1.9586,   1.9147,   1.8687,   1.8203,   1.7689,   1.7138,   1.6541},
/*  29 */  {  4.1830,   3.3277,   2.9340,   2.7014,   2.5454,   2.4324,   2.3463,   2.2783,   2.2229,   2.1768,   2.1045,   2.0275,   1.9446,   1.9005,   1.8543,   1.8055,   1.7537,   1.6981,   1.6376},
/*  30 */  {  4.1709,   3.3158,   2.9223,   2.6896,   2.5336,   2.4205,   2.3343,   2.2662,   2.2107,   2.1646,   2.0921,   2.0148,   1.9317,   1.8874,   1.8409,   1.7918,   1.7396,   1.6835,   1.6223},

/*  40 */  {  4.0847,   3.2317,   2.8387,   2.6060,   2.4495,   2.3359,   2.2490,   2.1802,   2.1240,   2.0772,   2.0035,   1.9245,   1.8389,   1.7929,   1.7444,   1.6928,   1.6373,   1.5766,   1.5089},
/*  60 */  {  4.0012,   3.1504,   2.7581,   2.5252,   2.3683,   2.2541,   2.1665,   2.0970,   2.0401,   1.9926,   1.9174,   1.8364,   1.7480,   1.7001,   1.6491,   1.5943,   1.5343,   1.4673,   1.3893},
/* 120 */  {  3.9201,   3.0718,   2.6802,   2.4472,   2.2899,   2.1750,   2.0868,   2.0164,   1.9588,   1.9105,   1.8337,   1.7505,   1.6587,   1.6084,   1.5543,   1.4952,   1.4290,   1.3519,   1.2539},
/* Inf.*/  {  3.8415,   2.9957,   2.6049,   2.3719,   2.2141,   2.0986,   2.0096,   1.9384,   1.8799,   1.8307,   1.7522,   1.6664,   1.5705,   1.5173,   1.4591,   1.3940,   1.3180,   1.2214,   1.0000}        };

        // taking from the table directly
        if(f2 <= 30 && f1 <= 10) return F95table[f2-1][f1-1];

        // linear interpolation (of the table values)
        else if(f2 <= 30) {
            int f1index = f1 <= 12 ? 10 : f1 <= 15 ? 11 : f1 <= 20 ? 12 : f1 <= 24 ? 13 : f1 <= 30 ? 14 : f1 <= 40 ? 15 : f1 <= 60 ? 16 : f1 <= 120 ? 17 : 18;
            double dx1 = f1 <= 12 ? (f1-10) : f1 <= 15 ? (f1-12) : f1 <= 20 ? (f1-15) : f1 <= 24 ? (f1-20) : f1 <= 30 ? (f1-24) : f1 <= 40 ? (f1-30) : f1 <= 60 ? (f1-40) : f1 <= 120 ? (f1-60) : (f1-120);
            double dx2 = f1 <= 12 ? (12-f1) : f1 <= 15 ? (15-f1) : f1 <= 20 ? (20-f1) : f1 <= 24 ? (24-f1) : f1 <= 30 ? (30-f1) : f1 <= 40 ? (40-f1) : f1 <= 60 ? (60-f1) : f1 <= 120 ? (120-f1) : (Integer.MAX_VALUE-f1);
            double z1 = F95table[f2-1][f1index-1];
            double z2 = F95table[f2-1][f1index];
            return linterpolation(z1, z2, dx1, dx2);
        }

        else if(f1 <= 10) {
            int f2index = f2 <= 40 ? 30 : f2 <= 60 ? 31 : f2 <= 120 ? 32 : 33;
            double dx1 = f2 <= 40 ? (f2-30) : f2 <= 60 ? (f2-40) : f2 <= 120 ? (f2-60)  : (f2-120);
            double dx2 = f2 <= 40 ? (40-f2) : f2 <= 60 ? (60-f2) : f2 <= 120 ? (120-f2) : (Integer.MAX_VALUE-f2);
            double z1 = F95table[f2index-1][f1-1];
            double z2 = F95table[f2index][f1-1];
            return linterpolation(z1, z2, dx1, dx2);
        }

        // bilinear interpolation (of the table values)
        else {
            int f1index = f1 <= 12 ? 10 : f1 <= 15 ? 11 : f1 <= 20 ? 12 : f1 <= 24 ? 13 : f1 <= 30 ? 14 : f1 <= 40 ? 15 : f1 <= 60 ? 16 : f1 <= 120 ? 17 : 18;
            int f2index = f2 <= 40 ? 30 : f2 <= 60 ? 31 : f2 <= 120 ? 32 : 33;
            double dx1 = f1 <= 12 ? (f1-10) : f1 <= 15 ? (f1-12) : f1 <= 20 ? (f1-15) : f1 <= 24 ? (f1-20) : f1 <= 30 ? (f1-24) : f1 <= 40 ? (f1-30) : f1 <= 60 ? (f1-40) : f1 <= 120 ? (f1-60) : (f1-120);
            double dx2 = f1 <= 12 ? (12-f1) : f1 <= 15 ? (15-f1) : f1 <= 20 ? (20-f1) : f1 <= 24 ? (24-f1) : f1 <= 30 ? (30-f1) : f1 <= 40 ? (40-f1) : f1 <= 60 ? (60-f1) : f1 <= 120 ? (120-f1) : (Integer.MAX_VALUE-f1);
            double dy1 = f2 <= 40 ? (f2-30) : f2 <= 60 ? (f2-40) : f2 <= 120 ? (f2-60)  : (f2-120);
            double dy2 = f2 <= 40 ? (40-f2) : f2 <= 60 ? (60-f2) : f2 <= 120 ? (120-f2) : (Integer.MAX_VALUE-f2);
            double z11 = F95table[f2index-1][f1index-1];
            double z12 = F95table[f2index-1][f1index];
            double z21 = F95table[f2index][f1index-1];
            double z22 = F95table[f2index][f1index];
            return bilinterpolation(z11, z12, z21, z22, dx1, dx2, dy1, dy2);
        }

    }

    //-------------------------------------------------------------------------
    /**
     * Returns quantile of F-distribution with the 99% confidence interval
     * @param  f1 numerator degrees of freedom
     * @param  f2 denominator degrees of freedom
     * @return double - quantile of F-distribution
     * @see https://en.wikipedia.org/wiki/F-distribution
     * @see http://www.socr.ucla.edu/Applets.dir/F_Table.html#FTable0.01
     * @todo There are more precise and bigger tables. Implement one of them.
     */
    public static double get99FQuantile(int f1, int f2) {

        if (f1 < 1 || f2 < 1) 
            throw new IllegalArgumentException("Calling ScStatistics.get99F_DistributionQuantil - degrees of freedoms less than 1");

        double[][] F99table = {
//         f1 = 1          2          3          4          5          6          7          8          9         10          12        15         20         24         30         40         60       120           ∞
/*f2=1 */  {4052.181,  4999.500,  5403.352,  5624.583,  5763.650,  5858.986,  5928.356,  5981.070,  6022.473,  6055.847,  6106.321,  6157.285,  6208.730,  6234.631,  6260.649,  6286.782,  6313.030,  6339.391,  6365.864 },
/*   2 */  {  98.503,    99.000,    99.166,    99.249,    99.299,    99.333,    99.356,    99.374,    99.388,    99.399,    99.416,    99.433,    99.449,    99.458,    99.466,    99.474,    99.482,    99.491,    99.499 },
/*   3 */  {  34.116,    30.817,    29.457,    28.710,    28.237,    27.911,    27.672,    27.489,    27.345,    27.229,    27.052,    26.872,    26.690,    26.598,    26.505,    26.411,    26.316,    26.221,    26.125 },
/*   4 */  {  21.198,    18.000,    16.694,    15.977,    15.522,    15.207,    14.976,    14.799,    14.659,    14.546,    14.374,    14.198,    14.020,    13.929,    13.838,    13.745,    13.652,    13.558,    13.463 },
/*   5 */  {  16.258,    13.274,    12.060,    11.392,    10.967,    10.672,    10.456,    10.289,    10.158,    10.051,     9.888,     9.722,     9.553,     9.466,     9.379,     9.291,     9.202,     9.112,     9.020 },

/*   6 */  {  13.745,    10.925,     9.780,     9.148,     8.746,     8.466,     8.260,     8.102,     7.976,     7.874,     7.718,     7.559,     7.396,     7.313,     7.229,     7.143,     7.057,     6.969,     6.880 },
/*   7 */  {  12.246,     9.547,     8.451,     7.847,     7.460,     7.191,     6.993,     6.840,     6.719,     6.620,     6.469,     6.314,     6.155,     6.074,     5.992,     5.908,     5.824,     5.737,     5.650 },
/*   8 */  {  11.259,     8.649,     7.591,     7.006,     6.632,     6.371,     6.178,     6.029,     5.911,     5.814,     5.667,     5.515,     5.359,     5.279,     5.198,     5.116,     5.032,     4.946,     4.859 },
/*   9 */  {  10.561,     8.022,     6.992,     6.422,     6.057,     5.802,     5.613,     5.467,     5.351,     5.257,     5.111,     4.962,     4.808,     4.729,     4.649,     4.567,     4.483,     4.398,     4.311 },
/*   10*/  {  10.044,     7.559,     6.552,     5.994,     5.636,     5.386,     5.200,     5.057,     4.942,     4.849,     4.706,     4.558,     4.405,     4.327,     4.247,     4.165,     4.082,     3.996,     3.909 },

/*   11 */ {   9.646,     7.206,     6.217,     5.668,     5.316,     5.069,     4.886,     4.744,     4.632,     4.539,     4.397,     4.251,     4.099,     4.021,     3.941,     3.860,     3.776,     3.690,     3.602 },
/*   12 */ {   9.330,     6.927,     5.953,     5.412,     5.064,     4.821,     4.640,     4.499,     4.388,     4.296,     4.155,     4.010,     3.858,     3.780,     3.701,     3.619,     3.535,     3.449,     3.361 },
/*   13 */ {   9.074,     6.701,     5.739,     5.205,     4.862,     4.620,     4.441,     4.302,     4.191,     4.100,     3.960,     3.815,     3.665,     3.587,     3.507,     3.425,     3.341,     3.255,     3.165 },
/*   14 */ {   8.862,     6.515,     5.564,     5.035,     4.695,     4.456,     4.278,     4.140,     4.030,     3.939,     3.800,     3.656,     3.505,     3.427,     3.348,     3.266,     3.181,     3.094,     3.004 },
/*   15 */ {   8.683,     6.359,     5.417,     4.893,     4.556,     4.318,     4.142,     4.004,     3.895,     3.805,     3.666,     3.522,     3.372,     3.294,     3.214,     3.132,     3.047,     2.959,     2.868 },
//
/*   16 */ {   8.531,     6.226,     5.292,     4.773,     4.437,     4.202,     4.026,     3.890,     3.780,     3.691,     3.553,     3.409,     3.259,     3.181,     3.101,     3.018,     2.933,     2.845,     2.753 },
/*   17 */ {   8.400,     6.112,     5.185,     4.669,     4.336,     4.102,     3.927,     3.791,     3.682,     3.593,     3.455,     3.312,     3.162,     3.084,     3.003,     2.920,     2.835,     2.746,     2.653 },
/*   18 */ {   8.285,     6.013,     5.092,     4.579,     4.248,     4.015,     3.841,     3.705,     3.597,     3.508,     3.371,     3.227,     3.077,     2.999,     2.919,     2.835,     2.749,     2.660,     2.566 },
/*   19 */ {   8.185,     5.926,     5.010,     4.500,     4.171,     3.939,     3.765,     3.631,     3.523,     3.434,     3.297,     3.153,     3.003,     2.925,     2.844,     2.761,     2.674,     2.584,     2.489 },
/*   20 */ {   8.096,     5.849,     4.938,     4.431,     4.103,     3.871,     3.699,     3.564,     3.457,     3.368,     3.231,     3.088,     2.938,     2.859,     2.778,     2.695,     2.608,     2.517,     2.421 },

/*   21 */ {   8.017,     5.780,     4.874,     4.369,     4.042,     3.812,     3.640,     3.506,     3.398,     3.310,     3.173,     3.030,     2.880,     2.801,     2.720,     2.636,     2.548,     2.457,     2.360 },
/*   22 */ {   7.945,     5.719,     4.817,     4.313,     3.988,     3.758,     3.587,     3.453,     3.346,     3.258,     3.121,     2.978,     2.827,     2.749,     2.667,     2.583,     2.495,     2.403,     2.305 },
/*   23 */ {   7.881,     5.664,     4.765,     4.264,     3.939,     3.710,     3.539,     3.406,     3.299,     3.211,     3.074,     2.931,     2.781,     2.702,     2.620,     2.535,     2.447,     2.354,     2.256 },
/*   24 */ {   7.823,     5.614,     4.718,     4.218,     3.895,     3.667,     3.496,     3.363,     3.256,     3.168,     3.032,     2.889,     2.738,     2.659,     2.577,     2.492,     2.403,     2.310,     2.211 },
/*   25 */ {   7.770,     5.568,     4.675,     4.177,     3.855,     3.627,     3.457,     3.324,     3.217,     3.129,     2.993,     2.850,     2.699,     2.620,     2.538,     2.453,     2.364,     2.270,     2.169 },

/*   26 */ {   7.721,     5.526,     4.637,     4.140,     3.818,     3.591,     3.421,     3.288,     3.182,     3.094,     2.958,     2.815,     2.664,     2.585,     2.503,     2.417,     2.327,     2.233,     2.131 },
/*   27 */ {   7.677,     5.488,     4.601,     4.106,     3.785,     3.558,     3.388,     3.256,     3.149,     3.062,     2.926,     2.783,     2.632,     2.552,     2.470,     2.384,     2.294,     2.198,     2.097 },
/*   28 */ {   7.636,     5.453,     4.568,     4.074,     3.754,     3.528,     3.358,     3.226,     3.120,     3.032,     2.896,     2.753,     2.602,     2.522,     2.440,     2.354,     2.263,     2.167,     2.064 },
/*   29 */ {   7.598,     5.420,     4.538,     4.045,     3.725,     3.499,     3.330,     3.198,     3.092,     3.005,     2.868,     2.726,     2.574,     2.495,     2.412,     2.325,     2.234,     2.138,     2.034 },
/*   30 */ {   7.562,     5.390,     4.510,     4.018,     3.699,     3.473,     3.304,     3.173,     3.067,     2.979,     2.843,     2.700,     2.549,     2.469,     2.386,     2.299,     2.208,     2.111,     2.006 },

/*   40 */ {   7.314,     5.179,     4.313,     3.828,     3.514,     3.291,     3.124,     2.993,     2.888,     2.801,     2.665,     2.522,     2.369,     2.288,     2.203,     2.114,     2.019,     1.917,     1.805 },
/*   60 */ {   7.077,     4.977,     4.126,     3.649,     3.339,     3.119,     2.953,     2.823,     2.718,     2.632,     2.496,     2.352,     2.198,     2.115,     2.028,     1.936,     1.836,     1.726,     1.601 },
/*  120 */ {   6.851,     4.787,     3.949,     3.480,     3.174,     2.956,     2.792,     2.663,     2.559,     2.472,     2.336,     2.192,     2.035,     1.950,     1.860,     1.763,     1.656,     1.533,     1.381 },
/*  Inf.*/ {   6.635,     4.605,     3.782,     3.319,     3.017,     2.802,     2.639,     2.511,     2.407,     2.321,     2.185,     2.039,     1.878,     1.791,     1.696,     1.592,     1.473,     1.325,     1.000 }
        };

        // taking from the table directly
        if(f2 <= 30 && f1 <= 10) return F99table[f2-1][f1-1];

        // linear interpolation (of the table values)
        else if(f2 <= 30) {
            int f1index = f1 <= 12 ? 10 : f1 <= 15 ? 11 : f1 <= 20 ? 12 : f1 <= 24 ? 13 : f1 <= 30 ? 14 : f1 <= 40 ? 15 : f1 <= 60 ? 16 : f1 <= 120 ? 17 : 18;
            double dx1 = f1 <= 12 ? (f1-10) : f1 <= 15 ? (f1-12) : f1 <= 20 ? (f1-15) : f1 <= 24 ? (f1-20) : f1 <= 30 ? (f1-24) : f1 <= 40 ? (f1-30) : f1 <= 60 ? (f1-40) : f1 <= 120 ? (f1-60) : (f1-120);
            double dx2 = f1 <= 12 ? (12-f1) : f1 <= 15 ? (15-f1) : f1 <= 20 ? (20-f1) : f1 <= 24 ? (24-f1) : f1 <= 30 ? (30-f1) : f1 <= 40 ? (40-f1) : f1 <= 60 ? (60-f1) : f1 <= 120 ? (120-f1) : (Integer.MAX_VALUE-f1);
            double z1 = F99table[f2-1][f1index-1];
            double z2 = F99table[f2-1][f1index];
            return linterpolation(z1, z2, dx1, dx2);
        }

        else if(f1 <= 10) {
            int f2index = f2 <= 40 ? 30 : f2 <= 60 ? 31 : f2 <= 120 ? 32 : 33;
            double dx1 = f2 <= 40 ? (f2-30) : f2 <= 60 ? (f2-40) : f2 <= 120 ? (f2-60)  : (f2-120);
            double dx2 = f2 <= 40 ? (40-f2) : f2 <= 60 ? (60-f2) : f2 <= 120 ? (120-f2) : (Integer.MAX_VALUE-f2);
            double z1 = F99table[f2index-1][f1-1];
            double z2 = F99table[f2index][f1-1];
            return linterpolation(z1, z2, dx1, dx2);
        }

        // bilinear interpolation (of the table values)
        else {
            int f1index = f1 <= 12 ? 10 : f1 <= 15 ? 11 : f1 <= 20 ? 12 : f1 <= 24 ? 13 : f1 <= 30 ? 14 : f1 <= 40 ? 15 : f1 <= 60 ? 16 : f1 <= 120 ? 17 : 18;
            int f2index = f2 <= 40 ? 30 : f2 <= 60 ? 31 : f2 <= 120 ? 32 : 33;
            double dx1 = f1 <= 12 ? (f1-10) : f1 <= 15 ? (f1-12) : f1 <= 20 ? (f1-15) : f1 <= 24 ? (f1-20) : f1 <= 30 ? (f1-24) : f1 <= 40 ? (f1-30) : f1 <= 60 ? (f1-40) : f1 <= 120 ? (f1-60) : (f1-120);
            double dx2 = f1 <= 12 ? (12-f1) : f1 <= 15 ? (15-f1) : f1 <= 20 ? (20-f1) : f1 <= 24 ? (24-f1) : f1 <= 30 ? (30-f1) : f1 <= 40 ? (40-f1) : f1 <= 60 ? (60-f1) : f1 <= 120 ? (120-f1) : (Integer.MAX_VALUE-f1);
            double dy1 = f2 <= 40 ? (f2-30) : f2 <= 60 ? (f2-40) : f2 <= 120 ? (f2-60)  : (f2-120);
            double dy2 = f2 <= 40 ? (40-f2) : f2 <= 60 ? (60-f2) : f2 <= 120 ? (120-f2) : (Integer.MAX_VALUE-f2);
            double z11 = F99table[f2index-1][f1index-1];
            double z12 = F99table[f2index-1][f1index];
            double z21 = F99table[f2index][f1index-1];
            double z22 = F99table[f2index][f1index];
            return bilinterpolation(z11, z12, z21, z22, dx1, dx2, dy1, dy2);
        }


    }

    //-------------------------------------------------------------------------
    /**
     * Returns quantile of F-distribution with the 99.9% confidence interval
     * @param  f1 numerator degrees of freedom
     * @param  f2 denominator degrees of freedom
     * @return double - quantile of F-distribution
     * @see https://en.wikipedia.org/wiki/F-distribution
     * @see http://www.socr.ucla.edu/Applets.dir/F_Table.html#FTable0.01
     * @todo There are more precise and bigger tables. Implement one of them.
     */
    public static double get999FQuantile(int f1, int f2) {

        if (f1 < 1 || f2 < 1) 
            throw new IllegalArgumentException("Calling ScStatistics.get99F_DistributionQuantil - degrees of freedoms less than 1");

        double[][] F999table = 
         {
//               1            2            3            4            5            6            7            8            9           10           12           15           20           24           30           40           60           120          Inf
/* 1  */ { 405284.068,  499999.500,  540379.202,  562499.583,  576404.556,  585937.111,  592873.288,  598144.156,  602283.992,  605620.971,  610667.821,  615763.662,  620907.673,  623497.465,  626098.959,  628712.031,  631336.556,  633972.403,  636619.439 },
/* 2  */ {    998.500,     999.000,     999.167,     999.250,     999.300,     999.333,     999.357,     999.375,     999.389,     999.400,     999.417,     999.433,     999.450,     999.458,     999.467,     999.475,     999.483,     999.492,     999.500 },
/* 3  */ {    167.029,     148.500,     141.108,     137.100,     134.580,     132.847,     131.583,     130.619,     129.860,     129.247,     128.316,     127.374,     126.418,     125.935,     125.449,     124.959,     124.466,     123.969,     123.469 },
/* 4  */ {     74.137,       61.246,     56.177,      53.436,      51.712,      50.525,      49.658,      48.996,      48.475,      48.053,      47.412,      46.761,      46.100,      45.766,       45.429,      45.089,     44.746,      44.400,      44.051 },
/* 5  */ {     47.181,       37.122,     33.202,      31.085,      29.752,      28.834,      28.163,      27.649,      27.244,      26.917,      26.418,      25.911,      25.395,      25.133,       24.869,      24.602,     24.333,      24.060,      23.785 },
/* 6  */ {     35.507,       27.000,     23.703,      21.924,      20.803,      20.030,      19.463,      19.030,      18.688,      18.411,      17.989,      17.559,      17.120,      16.897,       16.672,      16.445,     16.214,      15.981,      15.745 },
/* 7  */ {     29.245,       21.689,     18.772,      17.198,      16.206,      15.521,      15.019,      14.634,      14.330,      14.083,      13.707,      13.324,      12.932,      12.732,       12.530,      12.326,     12.119,      11.909,      11.696 },
/* 8  */ {     25.415,       18.494,     15.829,      14.392,      13.485,      12.858,      12.398,      12.046,      11.767,      11.540,      11.194,      10.841,      10.480,      10.295,       10.109,       9.919,      9.727,       9.532,       9.334 },
/* 9  */ {     22.857,       16.387,     13.902,      12.560,      11.714,      11.128,      10.698,      10.368,      10.107,       9.894,       9.570,       9.238,       8.898,       8.724,        8.548,       8.369,      8.187,       8.001,       7.813 },
/* 10 */ {     21.040,       14.905,     12.553,      11.283,      10.481,       9.926,       9.517,       9.204,       8.956,       8.754,       8.445,       8.129,       7.804,       7.638,        7.469,       7.297,      7.122,       6.944,       6.762 },
/* 11 */ {     19.687,       13.812,     11.561,      10.346,       9.578,       9.047,       8.655,       8.355,       8.116,       7.922,       7.626,       7.321,       7.008,       6.847,        6.684,       6.518,      6.348,       6.175,       5.998 },
/* 12 */ {     18.643,       12.974,     10.804,       9.633,       8.892,       8.379,       8.001,       7.710,       7.480,       7.292,       7.005,       6.709,       6.405,       6.249,        6.090,       5.928,      5.762,       5.593,       5.420 },
/* 13 */ {     17.815,       12.313,     10.209,       9.073,       8.354,       7.856,       7.489,       7.206,       6.982,       6.799,       6.519,       6.231,       5.934,       5.781,        5.626,       5.467,      5.305,       5.138,       4.967 },
/* 14 */ {     17.143,       11.779,      9.729,       8.622,       7.922,       7.436,       7.077,       6.802,       6.583,       6.404,       6.130,       5.848,       5.557,       5.407,        5.254,       5.098,      4.938,       4.773,       4.604 },
/* 15 */ {     16.587,       11.339,      9.335,       8.253,       7.567,       7.092,       6.741,       6.471,       6.256,       6.081,       5.812,       5.535,       5.248,       5.101,        4.950,       4.796,      4.638,       4.475,       4.307 },
/* 16 */ {     16.120,       10.971,      9.006,       7.944,       7.272,       6.805,       6.460,       6.195,       5.984,       5.812,       5.547,       5.274,       4.992,       4.846,        4.697,       4.545,      4.388,       4.226,       4.059 },
/* 17 */ {     15.722,       10.658,      8.727,       7.683,       7.022,       6.562,       6.223,       5.962,       5.754,       5.584,       5.324,       5.054,       4.775,       4.631,        4.484,       4.332,      4.177,       4.016,       3.850 },
/* 18 */ {     15.379,       10.390,      8.487,       7.459,       6.808,       6.355,       6.021,       5.763,       5.558,       5.390,       5.132,       4.866,       4.590,       4.447,        4.301,       4.151,      3.996,       3.836,       3.670 },
/* 19 */ {     15.081,       10.157,      8.280,       7.265,       6.622,       6.175,       5.845,       5.590,       5.388,       5.222,       4.967,       4.704,       4.430,       4.288,        4.143,       3.994,      3.840,       3.680,       3.514 },
/* 20 */ {     14.819,        9.953,      8.098,       7.096,       6.461,       6.019,       5.692,       5.440,       5.239,       5.075,       4.823,       4.562,       4.290,       4.149,        4.005,       3.856,      3.703,       3.544,       3.378 },
/* 21 */ {     14.587,        9.772,      7.938,       6.947,       6.318,       5.881,       5.557,       5.308,       5.109,       4.946,       4.696,       4.437,       4.167,       4.027,        3.884,       3.736,      3.583,       3.424,       3.257 },
/* 22 */ {     14.380,        9.612,      7.796,       6.814,       6.191,       5.758,       5.438,       5.190,       4.993,       4.832,       4.583,       4.326,       4.058,       3.919,        3.776,       3.629,      3.476,       3.317,       3.151 },
/* 23 */ {     14.195,        9.469,      7.669,       6.696,       6.078,       5.649,       5.331,       5.085,       4.890,       4.730,       4.483,       4.227,       3.961,       3.822,        3.680,       3.533,      3.380,       3.222,       3.055 },
/* 24 */ {     14.028,        9.339,      7.554,       6.589,       5.977,       5.550,       5.235,       4.991,       4.797,       4.638,       4.393,       4.139,       3.873,       3.735,        3.593,       3.447,      3.295,       3.136,       2.969 },
/* 25 */ {     13.877,        9.223,      7.451,       6.493,       5.885,       5.462,       5.148,       4.906,       4.713,       4.555,       4.312,       4.059,       3.794,       3.657,        3.515,       3.369,      3.217,       3.058,       2.890 },
/* 26 */ {     13.739,        9.116,      7.357,       6.406,       5.802,       5.381,       5.070,       4.829,       4.637,       4.480,       4.238,       3.986,       3.723,       3.586,        3.445,       3.299,      3.147,       2.988,       2.819 },
/* 27 */ {     13.613,        9.019,      7.272,       6.326,       5.726,       5.308,       4.998,       4.759,       4.568,       4.412,       4.171,       3.920,       3.658,       3.521,        3.380,       3.234,      3.082,       2.923,       2.754 },
/* 28 */ {     13.498,        8.931,      7.193,       6.253,       5.656,       5.241,       4.933,       4.695,       4.505,       4.349,       4.109,       3.859,       3.598,       3.462,        3.321,       3.176,      3.024,       2.864,       2.695 },
/* 29 */ {     13.391,        8.849,      7.121,       6.186,       5.593,       5.179,       4.873,       4.636,       4.447,       4.292,       4.053,       3.804,       3.543,       3.407,        3.267,       3.121,      2.970,       2.810,       2.640 },
/* 30 */ {     13.293,        8.773,      7.054,       6.125,       5.534,       5.122,       4.817,       4.581,       4.393,       4.239,       4.001,       3.753,       3.493,       3.357,        3.217,       3.072,      2.920,       2.760,       2.589 },
/* 40 */ {     12.609,        8.251,      6.595,       5.698,       5.128,       4.731,       4.436,       4.207,       4.024,       3.874,       3.642,       3.400,       3.145,       3.011,        2.872,       2.727,      2.574,       2.410,       2.233 },
/* 60 */ {     11.973,        7.768,      6.171,       5.307,       4.757,       4.372,       4.086,       3.865,       3.687,       3.541,       3.315,       3.078,       2.827,       2.694,        2.555,       2.409,      2.252,       2.082,       1.890 },
/* 120*/ {     11.380,        7.321,      5.781,       4.947,       4.416,       4.044,       3.767,       3.552,       3.379,       3.237,       3.016,       2.783,       2.534,       2.402,        2.262,       2.113,      1.950,       1.767,       1.543 },
/* Inf*/ {     10.828,        6.908,      5.422,       4.617,       4.103,       3.743,       3.475,       3.266,       3.097,       2.959,       2.742,       2.513,       2.266,       2.132,        1.990,       1.835,      1.660,       1.447,       1.000 }
         };

        // taking from the table directly
        if(f2 <= 30 && f1 <= 10) return F999table[f2-1][f1-1];

        // linear interpolation (of the table values)
        else if(f2 <= 30) {
            int f1index = f1 <= 12 ? 10 : f1 <= 15 ? 11 : f1 <= 20 ? 12 : f1 <= 24 ? 13 : f1 <= 30 ? 14 : f1 <= 40 ? 15 : f1 <= 60 ? 16 : f1 <= 120 ? 17 : 18;
            double dx1 = f1 <= 12 ? (f1-10) : f1 <= 15 ? (f1-12) : f1 <= 20 ? (f1-15) : f1 <= 24 ? (f1-20) : f1 <= 30 ? (f1-24) : f1 <= 40 ? (f1-30) : f1 <= 60 ? (f1-40) : f1 <= 120 ? (f1-60) : (f1-120);
            double dx2 = f1 <= 12 ? (12-f1) : f1 <= 15 ? (15-f1) : f1 <= 20 ? (20-f1) : f1 <= 24 ? (24-f1) : f1 <= 30 ? (30-f1) : f1 <= 40 ? (40-f1) : f1 <= 60 ? (60-f1) : f1 <= 120 ? (120-f1) : (Integer.MAX_VALUE-f1);
            double z1 = F999table[f2-1][f1index-1];
            double z2 = F999table[f2-1][f1index];
            return linterpolation(z1, z2, dx1, dx2);
        }

        else if(f1 <= 10) {
            int f2index = f2 <= 40 ? 30 : f2 <= 60 ? 31 : f2 <= 120 ? 32 : 33;
            double dx1 = f2 <= 40 ? (f2-30) : f2 <= 60 ? (f2-40) : f2 <= 120 ? (f2-60)  : (f2-120);
            double dx2 = f2 <= 40 ? (40-f2) : f2 <= 60 ? (60-f2) : f2 <= 120 ? (120-f2) : (Integer.MAX_VALUE-f2);
            double z1 = F999table[f2index-1][f1-1];
            double z2 = F999table[f2index][f1-1];
            return linterpolation(z1, z2, dx1, dx2);
        }

        // bilinear interpolation (of the table values)
        else {
            int f1index = f1 <= 12 ? 10 : f1 <= 15 ? 11 : f1 <= 20 ? 12 : f1 <= 24 ? 13 : f1 <= 30 ? 14 : f1 <= 40 ? 15 : f1 <= 60 ? 16 : f1 <= 120 ? 17 : 18;
            int f2index = f2 <= 40 ? 30 : f2 <= 60 ? 31 : f2 <= 120 ? 32 : 33;
            double dx1 = f1 <= 12 ? (f1-10) : f1 <= 15 ? (f1-12) : f1 <= 20 ? (f1-15) : f1 <= 24 ? (f1-20) : f1 <= 30 ? (f1-24) : f1 <= 40 ? (f1-30) : f1 <= 60 ? (f1-40) : f1 <= 120 ? (f1-60) : (f1-120);
            double dx2 = f1 <= 12 ? (12-f1) : f1 <= 15 ? (15-f1) : f1 <= 20 ? (20-f1) : f1 <= 24 ? (24-f1) : f1 <= 30 ? (30-f1) : f1 <= 40 ? (40-f1) : f1 <= 60 ? (60-f1) : f1 <= 120 ? (120-f1) : (Integer.MAX_VALUE-f1);
            double dy1 = f2 <= 40 ? (f2-30) : f2 <= 60 ? (f2-40) : f2 <= 120 ? (f2-60)  : (f2-120);
            double dy2 = f2 <= 40 ? (40-f2) : f2 <= 60 ? (60-f2) : f2 <= 120 ? (120-f2) : (Integer.MAX_VALUE-f2);
            double z11 = F999table[f2index-1][f1index-1];
            double z12 = F999table[f2index-1][f1index];
            double z21 = F999table[f2index][f1index-1];
            double z22 = F999table[f2index][f1index];
            return bilinterpolation(z11, z12, z21, z22, dx1, dx2, dy1, dy2);
        }


    }
    
    //-------------------------------------------------------------------------
    /*  Linear interpolation scheme

       dx1               dx2
    o--------X-------------------------o
    z11                                z12

    */
    //-------------------------------------------------------------------------
    /**
     * Linear interpolation. Estimates (or predict) the value of an argument if two argument-value pairs are already known.
     * @param  dmean1 first mean
     * @param  z1 value of the first argument
     * @param  z2 value of the second argument
     * @param  dx1 distance from the first point - |arg - arg1|
     * @param  dx2 distance from the second point- |arg - arg2|
     * @return  double - the interpolated (i.e. the estimated) value
     */
    static double linterpolation(double z1, double z2, double dx1, double dx2) {

        return (dx1*z2 + dx2*z1) / (dx1 + dx2);
    }

    //-------------------------------------------------------------------------
    /*  Bilinear interpolation scheme

    z21                                z22
    o----------------------------------o
    |      |                           |
    |      |                           |
    |      |                           |
    |      |                           |
    |      |                           |
    |      | dy2                       |
    |      |                           |
    |      |                           |
    |      |                           |
    |      |                           |
    |      |                           |
    | dx1  |            dx2            |
    |------X---------------------------|
    |      |                           |
    |      | dy1                       |
    |      |                           |
    o----------------------------------o
    z11                                z12

    */
    //-------------------------------------------------------------------------
    /**
     * Bilinear interpolation. Estimates (or predict) the value of an point in 2D space if four (surrounding) point-value pairs are already known.
     * @param  dmean1 first mean
     * @param  z11 value of the first point in (surrounding) square
     * @param  z12 value of the second point in square
     * @param  z21 value of the third point in square
     * @param  z22 value of the fourth point in square
     * @param  dx1 distance from the first (or third) point along x-axis - |X - x11| or |X - x21|
     * @param  dx2 distance from the second (or fourth) point along x-axis - |X - x12| or |X - x22|
     * @param  dy1 distance from the first (or second) point along y-axis - |Y - y11| or |Y - y12|
     * @param  dy2 distance from the third (or fourth) point along y-axis - |Y - y21| or |Y - y22|
     * @return  double - the interpolated (i.e. the estimated) value of the (X,Y) point
     */
    static double bilinterpolation(double z11, double z12, double z21, double z22, double dx1, double dx2, double dy1, double dy2) {

        double fx1 = dx2;
        double fx2 = dx1;
        double fy1 = dy2;
        double fy2 = dy1;

        double Zy1 = (fx1*z11 + fx2*z12) / (fx1+fx2);
        double Zy2 = (fx1*z21 + fx2*z22) / (fx1+fx2);

        double Z = (fy1*Zy1 + fy2*Zy2) / (fy1+fy2);

        return Z;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns the set of resulting parameters to analyze linear regression
     * @param  x given domain array
     * @param  y given value array
     * @param  istart (inclusive) lower index of the sub-array
     * @param  iend (exclusive) upper index of the sub-array
     * @return double - max value within the array
     */
    public static RegrResults lregression(double[] x, double[] y, int istart, int iend) {

        if (0 > istart)
            throw new IndexOutOfBoundsException("Calling ScStatistics.lregression - 'istart' index (" + istart + ") is less than 0.");

        if (x.length < iend - istart)
            throw new IndexOutOfBoundsException("Calling ScStatistics.lregression - 'iend' index (" + iend + ") is greater than array length.");

        if (x.length != y.length)
            throw new IndexOutOfBoundsException("Calling ScStatistics.lregression - input arrays of different size.");

        RegrResults result = new RegrResults();
        
        result.istart = istart;
        result.iend = iend;
        
        // find out the estimated values of basic linear regression parameters (a, b)
        int num = iend - istart;    // number of elements in the arrays
        result.N11 = 0.0;           // sum(x*x)
        result.N12 = 0.0;           // sum(x)
        double N22 = num;
        
        result.n1 = 0.0;            // -sum(x*y)
        result.n2 = 0.0;            // -sum(y)
        
        for(int ii=istart; ii<iend; ii++) {
            result.N11 += x[ii] * x[ii];
            result.N12 += x[ii];
            result.n1 -= x[ii] * y[ii];
            result.n2 -= y[ii];
        }
        
        result.det = result.N11 * N22 - result.N12 * result.N12; 
        if (result.det < 0) // N is positive definite matrix and 'det' should be always positive
            throw new IllegalStateException("Calling ScStatistics.lregression - negative determinant of positive definite matrix. It should not be possible.");
        
        double Qx11 =         N22 / result.det;
        double Qx12 = -result.N12 / result.det;
        double Qx22 =  result.N11 / result.det;
        		
        result.a = -(Qx11*result.n1 + Qx12*result.n2);        // -( sumxy * num   + sumx  * sumy ) / det;
        result.b = -(Qx12*result.n1 + Qx22*result.n2);        // -( sumx  * sumxy - sumxx * sumy ) / det;
        
        // accuracy, standard deviations, allowed errors...
        double sumvv = 0.0;
        double vmax = Double.MIN_VALUE; // redundant; just for tests
        for(int jj=istart; jj<iend; jj++) {
        	double yest = x[jj]*result.a + result.b;	// estimated (expected) value
        	double v = y[jj] - yest;			// residuals - measured value minus expected value
        	vmax = Double.max(vmax, Math.abs(v));
        	sumvv += v*v;
        }
        
        result.sigma0 = Math.sqrt(sumvv / (num - 2));
        
        result.ma = result.sigma0 * Math.sqrt(Qx11);
        result.mb = result.sigma0 * Math.sqrt(Qx22);
        
        result.taumax = Double.MIN_VALUE;
        for(int ii=istart; ii<iend; ii++) {
        	double v = y[ii] - (x[ii]*result.a + result.b);
        	double Qv = 1. - (Qx11*x[ii]*x[ii] + 2*Qx12*x[ii] + Qx22);
        	double tau = Math.abs(v) / (result.sigma0 * Math.sqrt(Qv));
        	result.taumax = Double.max(result.taumax, tau);
        }

        return result;
}

    //-------------------------------------------------------------------------
    /**
     * Returns the set of resulting parameters to analyze linear regression
     * @param  x given domain array
     * @param  y given value array
     * @param  res0 old regression parameters (they are going to be altered in this function)
     * @param  istartnew new lower index of the sub-array (inclusive)
     * @param  iendnew new upper index of the sub-array (exclusive)
     * @param  steady_range it will be considered that the regression line is horizontal if it grows within the these limits
     */
    public static void lregressionEx(double[] x, double[] y, RegrResults res0, int istartnew, int iendnew, double steady_range) {

        if (0 > istartnew)
            throw new IndexOutOfBoundsException("Calling ScStatistics.lregressionEx - 'istart' index (" + istartnew + ") is less than 0.");

        if (x.length < iendnew - istartnew)
            throw new IndexOutOfBoundsException("Calling ScStatistics.lregressionEx - 'iend' index (" + iendnew + ") is greater than array length.");

        if (x.length != y.length)
            throw new IndexOutOfBoundsException("Calling ScStatistics.lregressionEx - input arrays of different size.");

//        if(istartnew == res0.istart && iendnew == res0.iend)  
//            return;

        // find out the estimated values of basic linear regression parameters (a, b)
        int num = iendnew - istartnew;    // number of elements in the arrays
        double N22 = num;

        // include new elements or exclude the old ones (left edge)
        if(istartnew < res0.istart) {
            for(int ii=istartnew; ii<res0.istart; ii++) {
                res0.N11 += x[ii] * x[ii];
                res0.N12 += x[ii];
                res0.n1 -= x[ii] * y[ii];
                res0.n2 -= y[ii];
            }
        }
        else if(istartnew > res0.istart) {
            for(int ii=res0.istart; ii<istartnew; ii++) {
                res0.N11 -= x[ii] * x[ii];
                res0.N12 -= x[ii];
                res0.n1 += x[ii] * y[ii];
                res0.n2 += y[ii];
            }
        }

        // include new elements or exclude the old ones (right edge)
        if(iendnew > res0.iend) {
            for(int ii=res0.iend; ii<iendnew; ii++) {
                res0.N11 += x[ii] * x[ii];
                res0.N12 += x[ii];
                res0.n1 -= x[ii] * y[ii];
                res0.n2 -= y[ii];
            }
        }
        else if(iendnew < res0.iend) {
            for(int ii=iendnew; ii<res0.iend; ii++) {
                res0.N11 -= x[ii] * x[ii];
                res0.N12 -= x[ii];
                res0.n1 += x[ii] * y[ii];
                res0.n2 += y[ii];
            }
        }

        res0.det = res0.N11 * N22 - res0.N12 * res0.N12; 
        if (res0.det < 0) // N is positive definite matrix and 'det' should be always positive
            throw new IllegalStateException("Calling ScStatistics.lregression - negative determinant of positive definite matrix. It should not be possible.");
        
        double Qx11 =       N22 / res0.det;
        double Qx12 = -res0.N12 / res0.det;
        double Qx22 =  res0.N11 / res0.det;

        res0.a = -(Qx11*res0.n1 + Qx12*res0.n2);        // -( sumxy * num   + sumx  * sumy ) / det;
        res0.b = -(Qx12*res0.n1 + Qx22*res0.n2);        // -( sumx  * sumxy - sumxx * sumy ) / det;

        // accuracy, standard deviations, allowed errors...
        double sumvv = 0.0;
        res0.taumax = Double.MIN_VALUE;
        for(int jj=istartnew; jj<iendnew; jj++) {
            double yest = x[jj]*res0.a + res0.b;        // estimated (expected) value
            double v = Math.abs(y[jj] - yest);          // residuals - measured value minus expected value
            double Qv = 1. - (Qx11*x[jj]*x[jj] + 2*Qx12*x[jj] + Qx22);
            res0.taumax = Double.max(res0.taumax, v/Math.sqrt(Qv));
            sumvv += v*v;
        }

        res0.sigma0 = Math.sqrt(sumvv / (num - 2));

        res0.taumax /= res0.sigma0;

        res0.ma = res0.sigma0 * Math.sqrt(Qx11);
        res0.mb = res0.sigma0 * Math.sqrt(Qx22);

        double regression_grow = Math.abs(res0.a * (x[iendnew-1] - x[istartnew]));
        res0.isHorizontal = regression_grow <= Double.max(steady_range, res0.sigma0);
        if(!res0.isHorizontal && res0.ma > 1.e-8) {
            double dtest = Math.abs(res0.a / res0.ma);
            double dquantile = num > 2 ? SCStatistics.get99StudentQuantil(num - 2) : SCStatistics.get99StudentQuantil(1) ;
            res0.isHorizontal= dtest <= dquantile;
        }

        res0.istart = istartnew;
        res0.iend = iendnew;
    }
}

/*
 * Linear regression in matrix notation (method of least squares):
 * 
 * Mathematical model: Y = a*X + b
 * Stochastical model: The accuracy of all measured Y is the same
 * 
 * 
 * Matrix of coefficients (Jacobian - Ai1 = ∂Yi/∂a, Ai2 = ∂Yi/∂b) and vector of residuals:
 *
 *     | X1    1  |        | -Y1   |
 *     | X2    1  |        | -Y2   |
 * A = | ..    .. |    f = |  ...  |
 *     | ..    .. |        |  ...  |
 *     | Xnum  1  |        | -Ynum |
 * 
 *
 * Matrix of normal equations:
 * 
 *                  | sumxx  sumx |
 * N = trans(A)*A = |             |
 *                  | sumx   num  |
 *
 *                                    
 * detN = num*sumxx - sumx*sumx
 * 
 *
 *          |  num   -sumx  |
 * inv(N) = |               | / detN
 *          | -sumx   sumxx |
 * 
 *
 *
 * Vector of cofactors:
 * 
 *                  | -sumxy |
 * n = trans(A)*f = |        |
 *                  | -sumy  |
 *
 *
 *
 * The estimation of the linear regression parameters:
 * 
 *      | a |               | sumxy*num  - sumx*sumy  |
 * x' = |   | = -inv(N)*n = |                         | / detN
 *      | b |               | sumxx*sumy - sumx*sumxy |
 *
 *
 *
 * The covariance matrix:
 * 
 *               |  num  -sumx  |
 * Qx = inv(N) = |              | / detN
 *               | -sumx  sumxx |
 *
 *
 *
 * Corrections:
 * 
 * v = A*x' + f
 *
 * Standard deviation of unit weight
 *
 * sigma0 = trans(v)* v / (n - 2)
 * 
 * Standard deviation of estimated parameters:
 * 
 * ma = sigma0 * sqrt(Qx[11])
 * mb = sigma0 * sqrt(Qx[22])
 *
 * The covariance matrix of corrections:
 * 
 * Qv = E - A*Qx*trans(A)
 * 
 * Diagonal members of Qv:
 * 
 * Qv[ii] = 1 - ( X[i]^2 * Qx[11]  +  2*X[i]*Qx[12] + Qx[22] )
 * 
 * mv[i] = sigma0 * sqrt(Qv[ii])
 * 
 * tau[i] = v[i] / mv[i]  (should be less then corresponding quantile)
 * 
 */


