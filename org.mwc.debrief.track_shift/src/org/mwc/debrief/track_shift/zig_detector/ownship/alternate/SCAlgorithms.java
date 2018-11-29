package org.mwc.debrief.track_shift.zig_detector.ownship.alternate;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SCAlgorithms {

    // internal utility data structures
    public static class SpanPair {
        public SpanPair (int _first, int _second) {
            first = _first;
            second = _second;
        }

        public int first;
        public int second;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns true if we all the deviations from mean value are within the statistically allowed limits
     * (hypothesis |x(i)-x'| = 0 is tested; alternative hypothesis is |x(i)-x'| > 0
     * test value (x(i)-x')/sigma(x)*sqrt(n-1)/sqrt(n) has Student(0,1) distribution with n-1 degrees of freedom)
     * @param  times the array of times
     * @param  values the array of values
     * @param  istart (inclusive) lower index of the sub-array
     * @param  iend (exclusive) upper index of the sub-array
     * @param  steady_stdev it will be considered that deviations are
     * in allowed limits if standard deviation is less then this predefined argument
     * @return boolean - true if we can consider that the deviations are within the allowed limits
     */
    static boolean areDeviationsInAllowedLimits(double[] values, int istart, int iend, double steady_stdev) {

        final double dstdev = SCStatistics.stdev(values, istart, iend);
        if (dstdev < steady_stdev) // Preventing devzero errors
            return true;

        final double dmax = SCStatistics.max(values, istart, iend);
        final double dmin = SCStatistics.min(values, istart, iend);
        final int numelements = iend - istart;
        final double dmean = SCStatistics.mean(values, istart, iend);
        final double ddeviation = Double.max(dmax - dmean, dmean - dmin);
        final double dtest = ddeviation / dstdev * Math.sqrt(numelements / (numelements-1));
        final double dquantile = numelements > 2 ? SCStatistics.get99StudentQuantil(numelements - 2) : SCStatistics.get99StudentQuantil(numelements - 1);

        return dtest <= dquantile;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns true if we all the deviations from mean value are within the statistically allowed limits
     * (hypothesis |x(i)-x'| = 0 is tested; alternative hypothesis is |x(i)-x'| > 0
     * test value (x(i)-x')/sigma(x)*sqrt(n-1)/sqrt(n) has Student(0,1) distribution with n-2 degrees of freedom)
     * @param  numeelements number of elements in the interval
     * @param  dmax the maximal value inside the interval
     * @param  dmin the minimal element inside the interval
     * @param  dmean the average value of the elements inside the interval
     * @param  dstdev statistical standard deviation
     * @param  steady_stdev it will be considered that deviations are
     * in allowed limits if standard deviation is less then this predefined argument
     * @return boolean - true if we can consider that the deviations are within the allowed limits
     */
    static boolean areDeviationsInAllowedLimits(int numelements, double dmax, double dmin, double dmean, double dstdev, double steady_stdev) {

        if (dstdev < steady_stdev) // Preventing devzero errors
            return true;

        final double ddeviation = Double.max(dmax - dmean, dmean - dmin);
        final double dtest = ddeviation / dstdev * Math.sqrt(numelements / (numelements-1));
        final double dquantile = numelements > 2 ? SCStatistics.get99StudentQuantil(numelements - 2) : SCStatistics.get99StudentQuantil(numelements - 1);

        return dtest <= dquantile;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns true if the maximal range in an interval is within the statistically allowed limits.
     * Test value (max(x)-min(x))/sigma(range) should be less then corresponding quantile
     * @param  values the array of values
     * @param  istart (inclusive) lower index of the sub-array
     * @param  iend (exclusive) upper index of the sub-array
     * @param  steady_stdev it will be considered that deviations are
     * in allowed limits if standard deviation is less then this predefined argument
     * @return boolean - true if maximal range is in the allowed limits
     */
    static boolean isMaxRangeInAllowedLimits(double[] values, int istart, int iend, double steady_stdev) {

        final double dstdev = SCStatistics.stdev(values, istart, iend);
        if (dstdev < steady_stdev) // Preventing devzero errors
            return true;

        final int numelements = iend - istart;
        final double dmax = SCStatistics.max(values, istart, iend);
        final double dmin = SCStatistics.min(values, istart, iend);
        final double dtest = (dmax - dmin) / Math.sqrt(dstdev);
        final double dquantile = SCStatistics.get99RangeQuantile(numelements);

        return dtest <= dquantile;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns true if we can consider regression line horizontal
     * (regression analysis: hypothesis |a| > 0 is tested; alternative hypothesis is a = 0
     * test value (a-0)/sigma(a) has Student(0,1) distribution with n-2 degrees of freedom)
     * @param  times the array of times
     * @param  values the array of values
     * @param  istart (inclusive) lower index of the sub-array
     * @param  iend (exclusive) upper index of the sub-array
     * @param  steady_range the regression line will be considered horizontal if it cannot grow more than this predefined argument
     * @return boolean - true if we can consider regression line horizontal
     */
    static boolean isRegressionLineHorizontal(double[] times, double[] values, int istart, int iend, double steady_range) {
        SCStatistics.RegrResults res = SCStatistics.lregression(times, values, istart, iend);
        final int numelements = iend - istart;
        final double regression_grow = Math.abs(res.a * (times[iend-1] - times[istart]));
        boolean cond1 = regression_grow <= Double.max(steady_range, res.sigma0);

        // if regression line cannot increase significantly we can consider the line horizontal
        if (cond1) return true;

        // ... otherwise we have to compare test statistics
        boolean cond2 = true;
        if(res.ma > 1.e-8) { // Preventing devzero errors
            final double dtest = Math.abs(res.a / res.ma);
            final double dquantile = numelements > 2 ? SCStatistics.get99StudentQuantil(numelements - 2) : SCStatistics.get99StudentQuantil(1) ;
            cond2 = dtest <= dquantile;
        }

        return cond2;
    }

    //-------------------------------------------------------------------------
    /**
     * Creates an array of steady value (course or speed) intervals from an
     * array of input values. Based on statistics and calculated limits
     * for deviation from the mean value. The maximal deviation is compared to the test statistics.
     * Inclination of regression line is analyzed afterwards.
     * @param  times the array of times
     * @param  values the array of values
     * @param  mintimes the minimal elapsed time of the 'steady' interval. Intervals cannot be considered steady if mintimes has not elapsed.
     * @param bRegressionAnalysis perform regression analysis if true
     * @param  steady_range it will be considered that interval is steady if its max and min valueas are in this predefined range
     * @param  steady_stdev it will be considered that interval is steady if standard deviation is less then this predefined argument
     * @return List< SpanPair > - array of steady intervals extracted from totes
     * @param range_limit if range (in an interval) exceeds this value, we cannot consider it as a steady-interval regardless of what mathematical statistics says
     * todo min_elapsedtime instead of minelements
     * todo it should be optimized. It can run significantly faster.
     */
    public static List<SpanPair> fifo_maxdev(double[] times, double[] values, double mintimes, boolean bRegressionAnalysis, double steady_range, double steady_stdev, double range_limit) {
        if (times.length != values.length)
            throw new IndexOutOfBoundsException("Calling ScStatistics.fifo_maxdev - input arrays of different length");

        List<SpanPair> periods = new ArrayList<>();

        int istart=0;
        int iend = istart + 1;
        boolean bfirstpass = true;

        while ((istart < values.length - 1) && (iend <= values.length)) {
            while((iend <= values.length) && (times[iend - 1] - times[istart] < mintimes))
                iend++;
            
            // if the end of arrays is reached and the is still not long enough, break the loop
            if(iend > values.length)
                break;

            boolean bcondition;
            final double dmax = SCStatistics.max(values, istart, iend);
            final double dmin = SCStatistics.min(values, istart, iend);
            final double dstdev = SCStatistics.stdev(values, istart, iend);

            // If range exceeds predefined limits, it cannot be steady-interval.
            // If all the values are in the predefined small range or
            // if they deviate within the numbers precision (2 decimals, here)
            // we can drop out calculations and consider it to be steady course/speed interval.
            if(dmax - dmin >= range_limit)
                bcondition = false;
            else if (steady_range >= (dmax - dmin) || steady_stdev >= dstdev)
                bcondition = true;
            else
                bcondition = areDeviationsInAllowedLimits(values, istart, iend, steady_stdev);

            if(!bcondition || values.length == iend) {

                if(bfirstpass) // if no success; if no steady interval at the very beginning - then, iterate forward
                    iend = ++istart + 1;
                else {
                    SCStatistics.RegrResults regr = new SCStatistics.RegrResults();

                    // Is horizontal line - linear regression analysis. If not, iterate backward until finding horizontal line.
                    if(bRegressionAnalysis) {
                        do {
                            SCStatistics.lregressionEx(times, values, regr, istart, iend-1, steady_range);
                        } while(!regr.isHorizontal && times[--iend - 1] - times[istart] >= mintimes);
                    }

                    if(!bRegressionAnalysis || regr.isHorizontal ) {
                        SpanPair sp = new SpanPair(istart, (iend != values.length ? --iend : iend));
                        periods.add(sp);

                        istart = iend++;
                    }
                    else // if no interval passed regression test - iterate forward
                        iend = ++istart + 1;
                }

                bfirstpass = true;
            }

            else {
                iend++;
                bfirstpass = false;
            }
        }

        return periods;
    }

    //-------------------------------------------------------------------------
    /**
     * Creates an array of steady value (course or speed) intervals from an
     * array of input values. Based on statistics and calculated allowed ranges. The maximal range is compared to the test statistics.
     * Optionally - the inclination of regression line is analyzed afterwards.
     * @param  times the array of times
     * @param  values the array of values
     * @param minelements the minimal number of elements in the 'steady' interval
     * @param bRegressionAnalysis perform regression analysis if true
     * @param  steady_range it will be considered that interval is steady if its max and min valueas are in this predefined range
     * @param  steady_stdev it will be considered that interval is steady if standard deviation is less then this predefined argument
     * @return List< SpanPair > - array of steady intervals extracted from totes
     * todo min_elapsedtime instead of minelements
     * todo it should be optimized. It can run significantly faster.
     */
    public static List<SpanPair> fifo_maxrange(double[] times, double[] values, int minelements, boolean bRegressionAnalysis, double steady_range, double steady_stdev) {
        if (times.length != values.length)
            throw new IndexOutOfBoundsException("Calling ScStatistics.fifo_maxrange - input arrays of different length");

        List<SpanPair> periods = new ArrayList<>();

        int istart=0;
        int iend = istart + minelements;

        while ((istart <= values.length - minelements) && (iend <= values.length)) {
            boolean bcondition;
            final double dmax = SCStatistics.max(values, istart, iend);
            final double dmin = SCStatistics.min(values, istart, iend);
            final double dstdev = SCStatistics.stdev(values, istart, iend);

            // if all the values are in the predefined small range or
            // if they deviate within the numbers precision (2 decimals, here)
            // we can drop out calculations and consider it to be steady course/speed interval
            if (steady_range >= (dmax - dmin) || steady_stdev >= dstdev)
                bcondition = true;
            else {
                bcondition = isMaxRangeInAllowedLimits(values, istart, iend, steady_stdev);
            }

            if(!bcondition || values.length == iend) {

                if(iend - istart == minelements) // if no success; if no steady interval at the very beginning - then, iterate forward
                    iend = ++istart + minelements;

                else {
                    boolean cond_regression = bRegressionAnalysis ? true : false;

                    // Is horizontal line - linear regression analysis. If not, iterate backward until finding horizontal line.
                    if(bRegressionAnalysis) {
                        do {
                            cond_regression = isRegressionLineHorizontal(times, values, istart, iend, steady_range);
                           } while (!cond_regression && --iend > istart + minelements);
                        }

                    if(!bRegressionAnalysis || cond_regression) {
                        boolean bshift = shiftRangeIntervalRight(times, values, istart, iend, bRegressionAnalysis, steady_stdev);
                        if(bshift) {
                            istart++;
                            continue;
                        }

                        SpanPair sp = new SpanPair(istart, (iend != values.length ? --iend : iend));
                        periods.add(sp);

                        istart = iend;
                        iend += minelements;
                    }
                    else // if no interval passed regression test - iterate forward
                        iend = ++istart + minelements;
                }

            }
            else
                iend++;
        }

        return periods;
    }

    //-------------------------------------------------------------------------
    /**
     * Creates an array of steady value (course or speed) intervals from an
     * array of input values. The function produce optimal intervals: 
     * a) the sum of lengths is maximal b) the sum of squares of residuals is minimal.
     * @param spans list of steady intervals (it is being filled by this function)
     * @param times the array of times
     * @param values the array of values
     * @param  istart (inclusive) lower index of the sub-array
     * @param  iend (exclusive) upper index of the sub-array
     * @param numelems number of elements for 'sieving' (it can be less than iend-istart)
     * @param minseconds the resulting interval have to be greater than minseconds
     * @param bRegressionAnalysis perform regression analysis if true
     * @param steady_range it will be considered that interval is steady if its max and min valueas are in this predefined range
     * @param steady_stdev it will be considered that interval is steady if standard deviation is less then this predefined argument
     * @param ff referent degrees of freedom for the F-test used in sieve-algorithm
     * @param mm2 referent variance for the F-test used in sieve-algorithm
     * @param range_limit if range (in an interval) exceeds this value, we cannot consider it as a steady-interval regardless of what mathematical statistics says
     * todo it should be optimized. It can run significantly faster.
     */
    public static void sieve_maxdev(List<SpanPair> spans, 
                                    double[] times, double[] values, 
                                    int istart, int iend, int numelems,
                                    double minseconds,
                                    boolean bRegressionAnalysis, 
                                    double steady_range, double steady_stdev,
                                    int ff, double mm2,
                                    double range_limit) {

        int minelements = 2; // min-time period cannot be less than two elements
        boolean btimespan = true;

        int numelements = Integer.min(iend - istart, numelems);
        if(numelements < minelements) 
            return;

        int end_while = istart + numelements; 
        double min_while = Double.MAX_VALUE;
        double max_while = Double.MIN_VALUE;
        double sum_while = 0.0;
        double sumqv_while = 0.0;
        for(int kk = istart; kk < end_while; kk++) {
            double dval = values[kk];
            min_while = Double.min(min_while, dval);
            max_while = Double.max(max_while, dval);
            sum_while += dval;
            sumqv_while += dval*dval;
        }
        double mean_while = sum_while / numelements;
        double stdev_while = Math.sqrt((sumqv_while - mean_while*mean_while * numelements) / (numelements - 1));

        while(numelements >= minelements) {

            if(!btimespan)
                break;
            else
                btimespan = false;

            SpanPair nsp = new SpanPair(0,0);
            double stdevmin = Double.MAX_VALUE;

            double min_for = min_while;
            double max_for = max_while;
            double sum_for = sum_while;
            double sumqv_for = sumqv_while;
            double mean_for = mean_while;
            double stdev_for = stdev_while;

            SCStatistics.RegrResults regr = new SCStatistics.RegrResults();

            for(int ii = istart, jj = istart + numelements; jj<=iend; ii++, jj++) {
                double dtime = times[jj-1] - times[ii];
                if(dtime < minseconds) 
                    continue;

                btimespan = true;
                
                boolean bcond = (max_for - min_for < range_limit);

                // testing whether deviations are in Allowed limits
                if (bcond)
                    bcond = areDeviationsInAllowedLimits(numelements, max_for, min_for, mean_for, stdev_for, steady_stdev);

                // testing whether the variances are compatible (F-test)
                if (bcond) {
                    double test = stdev_for*stdev_for / mm2;
                    double quantile = SCStatistics.get95FQuantile((jj - ii - 1), ff);
                    bcond = (test <= quantile);
                }

                // testing whether the regression line is horizontal
                if (bcond) {
                    if(ii <= (regr.iend + regr.istart)/2)
                        SCStatistics.lregressionEx(times, values, regr, ii, jj, steady_range);
                    else {
                        regr = SCStatistics.lregression(times, values, ii, jj);
                        double regression_grow = Math.abs(regr.a * (times[jj-1] - times[ii]));
                        regr.isHorizontal = regression_grow <= Double.max(steady_range, regr.sigma0);
                        if(!regr.isHorizontal && regr.ma > 1.e-8) {
                            double dtest = Math.abs(regr.a / regr.ma);
                            double dquantile = numelements > 2 ? SCStatistics.get99StudentQuantil(numelements - 2) : SCStatistics.get99StudentQuantil(1) ;
                            regr.isHorizontal= dtest <= dquantile;
                        }
                    }

                    bcond = regr.isHorizontal;
                }

                // If an interval has just been found and if it is the best one, so far - remember it 
                // (the optimization - the best interval (i.e. stdev = min) is being searched)
                if(bcond) {
                    if (stdev_for < stdevmin) {
                        stdevmin = stdev_for;
                        nsp.first = ii;
                        nsp.second = jj;
                    }                    
                }

                // update sum_for, sumqv_for, mean_for, stdev_for, min_for, max_for for the next for-loop
                if(jj < iend) {
                    double dleft  = values[ii];
                    double dright = values[jj];

                    sum_for -= dleft;
                    sum_for += dright;

                    sumqv_for -= dleft*dleft;
                    sumqv_for += dright*dright;

                    mean_for = sum_for / numelements;
                    stdev_for = Math.sqrt((sumqv_for - mean_for*mean_for * numelements) / (numelements - 1));

                    if(dright <= min_for)
                        min_for = dright;
                    else if(dleft == min_for)
                        min_for = SCStatistics.min(values, ii+1, jj+1);

                    if(dright >= max_for)
                        max_for = dright;
                    else if(dleft == max_for)
                        max_for = SCStatistics.max(values, ii+1, jj+1);
                }
            }

            // If there is an (optimal) interval satisfying the conditions,
            // add it and make recursive calls for right and left subintervals
            if(nsp.second != 0) {
                sieve_maxdev(spans, times, values, 
                             istart, nsp.first, numelements,
                             minseconds, 
                             bRegressionAnalysis, 
                             steady_range, steady_stdev,
                             ff, mm2,
                             range_limit);

                spans.add(new SpanPair(nsp.first, nsp.second));

                sieve_maxdev(spans, times, values, 
                             nsp.second, iend, numelements,
                             minseconds, 
                             bRegressionAnalysis, 
                             steady_range, steady_stdev,
                             ff, mm2,
                             range_limit);

                return;
            }

            // if we came here, it means that no good interval (with numelements) has been found

            // update sum_while, sumqv_while, mean_while, stdev_while, min_while, max_while for the next while-loop
            double dval = values[--end_while];
            sum_while -= dval;
            sumqv_while -= dval*dval;

            mean_while = sum_while / --numelements;
            stdev_while = Math.sqrt((sumqv_while - mean_while*mean_while * numelements) / (numelements - 1));

            if(min_while == dval)
                min_while = SCStatistics.min(values, istart, end_while);
            if(max_while == dval)
                max_while = SCStatistics.max(values, istart, end_while);
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Returns a number of possible steady-interval shifting (to the right) so that the new interval should be statistically better (satisfying the allowed-deviations condition).
     * (Example: the initial interval [5,15) is statistically good; [5,16) is bad; [6,16) is good and even better
     * than [5,15) - it is recommendable to shift the initial steady interval to the right for 1)
     * @param  times the array of times
     * @param  values the array of values
     * @param  istart (inclusive) lower index of the sub-array
     * @param  iend (exclusive) upper index of the sub-array
     * @param bRegressionAnalysis perform regression analysis if true
     * @param  steady_stdev it will be considered that deviations are
     * in allowed limits if standard deviation is less then this predefined argument
     * @return boolean - true if (deviations) interval should be shifted
     */
    public static boolean shiftDevIntervalRight(double[] times, double[] values, int istart, int iend, boolean bRegressionAnalysis, double steady_stdev) {
        if (0 > istart)
            throw new IndexOutOfBoundsException("Calling ScAlgorithms.shiftDevIntervalRight - 'istart' index (" + istart + ") is less than 0.");

        if (istart > iend)
            throw new NegativeArraySizeException("Calling ScAlgorithms.shiftDevIntervalRight - 'istart' (" + istart + ") is bigger than 'iend' (" + iend + ").");

        if (times.length != values.length)
            throw new IndexOutOfBoundsException("Calling ScAlgorithms.shifDevtIntervalRight - input arrays of different length");

        if (iend >= values.length - 1)
            return false;

        // start,end of right shifted interval
        int istartnew = istart + 1;
        int iendnew = iend + 1;

        // next interval is not proper if it doesn't fulfill the basic condition
        if(!areDeviationsInAllowedLimits(values, istartnew, iendnew, steady_stdev))
            return false;

        int numelements = iend - istart;
        double stdev_old = SCStatistics.stdev(values, istart, iend);

        // shifted aray
        double stdev_new = SCStatistics.stdev(values, istartnew, iendnew);
        if(stdev_new >= stdev_old)
            return false;

        if(bRegressionAnalysis) {
            double dmax = SCStatistics.max(values, istartnew, iendnew);
            double dmin = SCStatistics.min(values, istartnew, iendnew);
            if (dmax - dmin > 0.1) { // no need to analyze regression of the next interval if all the values in array are within 0.01 range
                SCStatistics.RegrResults res = SCStatistics.lregression(times, values, istartnew, iendnew);
                double dtest = Math.abs(res.a / res.ma);
                double dquantile = numelements > 2 ? SCStatistics.get99StudentQuantil(numelements - 2) : SCStatistics.get99StudentQuantil(1) ;
                double regression_grow = Math.abs(res.a * (times[iend] - times[istart+1])); // iend (not iendnew). The end of intervals are always exclusive.
                boolean bcond = (regression_grow <= stdev_new || dtest <= dquantile);
                if(bcond) { // no shift if the next interval regression line is more inclined
                    SCStatistics.RegrResults res0 = SCStatistics.lregression(times, values, istart, iend);
                    if(Math.abs(res0.a) < Math.abs(res.a))
                        return false;
                }
            }
        }

        // If we came here, the interval should be shifted
        return true;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns a number of possible steady-interval shifting (to the right) so that the new interval should be statistically better (satisfying maximal range condition).
     * (Example: the initial interval [5,15) is statistically good; [5,16) is bad; [6,16) is good and even better
     * than [5,15) - it is recommendable to shift the initial steady interval to the right for 1)
     * @param  times the array of times
     * @param  values the array of values
     * @param  istart (inclusive) lower index of the sub-array
     * @param  iend (exclusive) upper index of the sub-array
     * @param bRegressionAnalysis perform regression analysis if true
     * @param  steady_stdev it will be considered that deviations are
     * in allowed limits if standard deviation is less then this predefined argument
     * @return int - Return a number of possible (and recommended) shifting to the right
     */
    public static boolean shiftRangeIntervalRight(double[] times, double[] values, int istart, int iend, boolean bRegressionAnalysis, double steady_stdev) {
        if (0 > istart)
            throw new IndexOutOfBoundsException("Calling ScAlgorithms.shiftRangeIntervalRight - 'istart' index (" + istart + ") is less than 0.");

        if (istart > iend)
            throw new NegativeArraySizeException("Calling ScAlgorithms.shiftRangeIntervalRight - 'istart' (" + istart + ") is bigger than 'iend' (" + iend + ").");

        if (times.length != values.length)
            throw new IndexOutOfBoundsException("Calling ScAlgorithms.shifRangetIntervalRight - input arrays of different length");

        if (iend > values.length - 1)
            return false;

        // start,end of right shifted interval
        int istartnew = istart + 1;
        int iendnew = iend + 1;

        // next interval is not proper if it doesn't fulfill the basic condition
        if(!isMaxRangeInAllowedLimits(values, istartnew, iendnew, steady_stdev))
            return false;

        int numelements = iend - istart;
        double stdev_old = SCStatistics.stdev(values, istart, iend);

        // shifted aray
        double stdev_new = SCStatistics.stdev(values, istartnew, iendnew);
        if(stdev_new >= stdev_old)
            return false;

        if(bRegressionAnalysis) {
            double dmax = SCStatistics.max(values, istartnew, iendnew);
            double dmin = SCStatistics.min(values, istartnew, iendnew);
            if (dmax - dmin > 0.1) { // no need to analyze regression of the next interval if all the values in array are within 0.01 range
                SCStatistics.RegrResults res = SCStatistics.lregression(times, values, istartnew, iendnew);
                double dtest = Math.abs(res.a / res.ma);
                double dquantile = numelements > 2 ? SCStatistics.get99StudentQuantil(numelements - 2) : SCStatistics.get99StudentQuantil(1) ;
                double regression_grow = Math.abs(res.a * (times[iendnew] - times[istartnew]));
                boolean bcond = (regression_grow <= stdev_new || dtest <= dquantile);
                if(bcond) { // no shift if the next interval regression line is more inclined
                    SCStatistics.RegrResults res0 = SCStatistics.lregression(times, values, istart, iend);
                    if(Math.abs(res0.a) < Math.abs(res.a))
                        return false;
                }
            }
        }

        // If we came here, the interval should be shifted
        return true;
    }

    //-------------------------------------------------------------------------
    /**
     * Merge intervals which can be considered same (the same mean, in statistical sense)
     * Criterion that has to be fulfilled - all the deviations (in the merged interval) have to be within allowed limits
     * @param  values the array of values
     * @param  periods_in the array of steady course/speed intervals
     * todo it should be optimized. It can run significantly faster
     */
    public static List<SpanPair> mergeDevIntervals(double[] times, double[] values, List<SpanPair> periods_in) {

        // todo: throw here... if empty list etc.
        List<SpanPair> periods_out = new ArrayList<>();

        Iterator<SpanPair> iter = periods_in.iterator();
        SpanPair out = iter.next();           
        while(iter.hasNext()) {
            SpanPair merging = iter.next();
            int num1 = out.second - out.first;
            double mean1 = SCStatistics.mean(values, out.first, out.second);
            double stdev1 = SCStatistics.stdev(values, out.first, out.second);

            int num2 = merging.second - merging.first;
            double mean2 = SCStatistics.mean(values, merging.first, merging.second);
            double stdev2 = SCStatistics.stdev(values, merging.first, merging.second);

            // big-interval standard deviation
            double stdev_big = SCStatistics.stdev(values, out.first, merging.second);

            // standard deviation of the difference of two mean values
            double stedev_diff = Math.sqrt(stdev1*stdev1 + stdev2*stdev2);

            // testing of equality of mean (average) values
            double dtest1 = Math.abs(mean1 - mean2) / stedev_diff;
            double dquantile_diff = SCStatistics.get99StudentQuantil(num1 + num2 - 2);

            boolean cond = dtest1 <= dquantile_diff;

            // testing of equality of variances
            if(cond) {
                int num_merged = merging.second - out.first;
                double m2f = (stdev1*stdev1*(num1 - 1) + stdev2*stdev2*(num2 - 1)) / (num1 + num2 - 2);
                double dtest2 = stdev_big*stdev_big / m2f;
                double dquantileF = SCStatistics.get99FQuantile(num_merged - 1, num1 + num2 - 2);

                cond = dtest2 <= dquantileF;
            }

            // testing whether deviations (residuals) are in allowed limits
            if(cond) { // merge intervals
                cond = areDeviationsInAllowedLimits(values, out.first, merging.second, SCConstants.SPEED_STEADY_STDEV);
            }

            // testing whether regression line is horizontal // todo - if bRegression
            if(cond) {                
                cond = isRegressionLineHorizontal(times, values, out.first, merging.second, SCConstants.SPEED_STEADY_RANGE);
            }

            if(cond) {
                out.second = merging.second;
            } else {
                periods_out.add(out);
                out = new SpanPair(merging.first, merging.second);                
            }
        }

        // last steady interval in the input List
        periods_out.add(out);

        return periods_out;
    }

    //-------------------------------------------------------------------------
    /**
     * Merge intervals which can be considered same (the same mean, in statistical sense)
     * Criterion that has to be fulfilled - the maximal range (in merged interval) has to be within allowed limits
     * @param  values the array of values
     * @param  periods_in the array of steady course/speed intervals
     * @return List<SpanPair> - new list of merged intervals.
     * todo it should be optimized. It can run significantly faster
     */
    public static List<SpanPair> mergeRangeIntervals(double[] values, List<SpanPair> periods_in) {
        // todo: throw here...
        List<SpanPair> periods_out = new ArrayList<>();

        SpanPair in = periods_in.get(0);
        SpanPair out = new SpanPair(in.first, in.second);

        for(int jj=1; jj<periods_in.size(); jj++) {
            SpanPair merging = periods_in.get(jj);

            // todo: Fischer's test of dispersion equality

            // big-interval standard deviation
            final double stdev_big = SCStatistics.stdev(values, out.first, merging.second);
            final double max_big = SCStatistics.max(values, out.first, merging.second);
            final double min_big = SCStatistics.min(values, out.first, merging.second);

            final double dtest = (max_big - min_big) / Math.sqrt(stdev_big);
            final double dquantile = SCStatistics.get99RangeQuantile(merging.second - merging.first);

            boolean cond = (dtest <= dquantile);

            if(cond) {
                out.second = merging.second;
                if(periods_in.size()-1 == jj) // last steady interval in the input List
                    periods_out.add(out);

                continue;
            }

            periods_out.add(out);
            out = new SpanPair(merging.first, merging.second);
            if(periods_in.size()-1 == jj) // last steady interval in the input List
                periods_out.add(out);
        }

        return periods_out;
    }

    //-------------------------------------------------------------------------
    /**
     * Intersects two lists of intervals (e.g. steady course and steady speed intervals).
     * @param  first the first list of intervals (the list has to be ordered)
     * @param  second the second list of intervals (the list has to be ordered)
     * @return List<SpanPair> - Returns new list of a combined/intersected intervals.
     */
    public static List<SpanPair> intersectLists(List<SpanPair> first, List<SpanPair> second) {
        List<SpanPair> intersection = new ArrayList<>();

        // examine and treat properly all the topological possibilities

        int index_second = 0; // index of a SpanPair element from the second list
        Iterator<SpanPair> iter = first.iterator();
        while (iter.hasNext()) { // iterate through the first list
            SpanPair pair1 = iter.next();
            for(int ii=index_second; ii<second.size(); ii++) { // iterate through the second list, starting at index_second
                SpanPair pair2 = second.get(ii);
                if(pair1.first >= pair2.first && pair1.first < pair2.second) { // the beginning of pair1 falls between pair2 interval
                    intersection.add( new SpanPair(pair1.first, Integer.min(pair1.second, pair2.second)) );
                    index_second = pair1.second < pair2.second ? ii : ii+1;
                    if(pair1.second <= pair2.second)
                    break;
                }
                else if(pair1.second > pair2.first && pair1.second <= pair2.second) {// the end of pair1 falls between pair2 interval
                    intersection.add( new SpanPair(Integer.max(pair1.first, pair2.first), pair1.second) );
                    index_second = ii;
                    break;
                }
                else if(pair1.first <= pair2.first && pair1.second >= pair2.second) // if pair1 envelopes pair2
                    intersection.add( new SpanPair(pair2.first, pair2.second) );
                else if(pair1.second <= pair2.first) // pair1 and pair2 are disjunct intervals (the first pair precedes the seconds)
                    break;
               // pair1 and pair2 are disjunct intervals (the second pair precedes the first) - nothing to be done
            }
        }

        return intersection;
    }

    //-------------------------------------------------------------------------
    /**
     * Adjusts touching intervals so that the sum of squares of deviations should have a minimal possible value.
     * (Optionally, it can be checked whether the regression line is horizontal)
     * Criteria that must be fulfilled - the deviations have to be within allowed limits
     * @param times the times in the examined period
     * @param values the values being examined (headings or speeds)
     * @param periods the array of steady course/speed intervals
     * @param mintimes the minimal elapsed time of the 'steady' interval. Intervals cannot be considered steady if mintimes has not elapsed.
     * @param bRegressionAnalysis perform regression analysis if true
     * @param  steady_range it will be considered that interval is steady if its max and min values are in this predefined range
     * @param  steady_stdev it will be considered that deviations are
     * in allowed limits if standard deviation is less then this predefined argument
     */
    public static void adjustDevTouchingIntervals(double[] times, double[] values, List<SpanPair> periods, double mintimes, boolean bRegressionAnalysis, double steady_range, double steady_stdev) {

        // Iterate through the list, having in focus only neighboring intervals
        for(int ii=0, jj=1; jj<periods.size(); ii++, jj++) {
            SpanPair left = periods.get(ii);
            SpanPair right = periods.get(jj);

            // Treat only touching intervals
            if(left.second != right.first)
                continue;

            int touching_final = left.second;
            double minsumdev2 = Double.MAX_VALUE; // sum of squares of deviations - the crucial parameter

            // left interval has to be greater (in timeunits) than mintimes
            int leftmovingedge = left.first + 1;
            while(times[leftmovingedge-1] - times[left.first] < mintimes)
                leftmovingedge++;

            // right interval has to be greater (in timeunits) than mintimes
            int rightmovingedge = right.second - 1;
            while(times[right.second-1] - times[rightmovingedge] < mintimes)
                rightmovingedge--;

            // Test all possible touching points between...
            for(int touching = leftmovingedge; touching <= rightmovingedge; touching++) {

                // First, check whether deviations are in allowed limits
                boolean cond_first_ok = areDeviationsInAllowedLimits(values, left.first, touching, steady_stdev);
                if(!cond_first_ok) continue;
                boolean cond_second_ok = areDeviationsInAllowedLimits(values, touching, right.second, steady_stdev);
                if(!cond_second_ok) continue;

                // Optionally, verify that the regression lines are horizontal
                if(bRegressionAnalysis) {
                    cond_first_ok &= isRegressionLineHorizontal(times, values, left.first, touching, steady_range);
                    if(!cond_first_ok) continue;
                    cond_second_ok &= isRegressionLineHorizontal(times, values, touching, right.second, steady_range);
                    if(!cond_second_ok) continue;
                }

                // Is this is the optimal case, so far - remember it
                double stdev_left = SCStatistics.stdev(values, left.first, touching);
                double stdev_right = SCStatistics.stdev(values, touching, right.second);
                double sumdev2 = stdev_left*stdev_left*(touching - left.first - 1) + stdev_right*stdev_right*(right.second - touching - 1);

                if(sumdev2 < minsumdev2) {
                    minsumdev2 = sumdev2;
                    touching_final = touching;
                }
            }

            left.second = right.first = touching_final;
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Adjusts touching intervals so that the sum of squares of deviations should have a minimal possible value.
     * (Optionally, it can be checked whether the regression line is horizontal)
     * Criteria that must be fulfilled - the max range has to be within allowed limits
     * @param times the times in the examined period
     * @param values the values being examined (headings or speeds)
     * @param periods the array of steady course/speed intervals
     * @param minelements the minimal number of elements in a 'steady' interval
     * @param bRegressionAnalysis perform regression analysis if true
     * @param  steady_range it will be considered that interval is steady if its max and min values are in this predefined range
     * @param  steady_stdev it will be considered that deviations are
     * in allowed limits if standard deviation is less then this predefined argument
     */
    //public static List<SpanPair> merge_intervals(double[] values, List<SpanPair> periods_in) {
    public static void adjustRangeTouchingIntervals(double[] times, double[] values, List<SpanPair> periods, int minelements, boolean bRegressionAnalysis, double steady_range, double steady_stdev) {

        // Iterate through the list, having in focus only neighboring intervals
        for(int ii=0, jj=1; jj<periods.size(); ii++, jj++) {
            SpanPair left = periods.get(ii);
            SpanPair right = periods.get(jj);

            // Treat only touching intervals
            if(left.second != right.first)
                continue;

            int touching_final = left.second;
            double minsumdev2 = Double.MAX_VALUE; // sum of squares of deviations - the crucial parameter

            // Test all possible touching points between...
            for(int touching = left.first + minelements; touching <= right.second - minelements; touching++) {

                // First, check whether deviations are in allowed limits
                boolean cond_first_ok = isMaxRangeInAllowedLimits(values, left.first, touching, steady_stdev);
                if(!cond_first_ok) continue;
                boolean cond_second_ok = isMaxRangeInAllowedLimits(values, touching, right.second, steady_stdev);
                if(!cond_second_ok) continue;

                // Optionally, verify that the regression lines are horizontal
                if(bRegressionAnalysis) {
                    cond_first_ok &= isRegressionLineHorizontal(times, values, left.first, touching, steady_range);
                    if(!cond_first_ok) continue;
                    cond_second_ok &= isRegressionLineHorizontal(times, values, touching, right.second, steady_range);
                    if(!cond_second_ok) continue;
                }

                // Is this is the optimal case, so far - remember it
                double stdev_left = SCStatistics.stdev(values, left.first, touching);
                double stdev_right = SCStatistics.stdev(values, touching, right.second);
                double sumdev2 = stdev_left*(touching - left.first - 1) + stdev_right*(right.second - touching - 1);

                if(sumdev2 < minsumdev2) {
                    minsumdev2 = sumdev2;
                    touching_final = touching;
                }
            }

            left.second = right.first = touching_final;
        }
    }

    //-------------------------------------------------------------------------
    // the difference between connectNeighboring and mergeDevIntervals is that the second function (mergeDevIntervals)
    // is based on the tests of mathematical statistics. The first one is simply merging (without any statistics) 
    // if mean values are close enough.
    public static void mergeSimpleIntervals(double[] values, List<SpanPair> intervals, double tolerable_diff) {
        // find neighboring intervals with minumal mean-value difference
        int index = -1;
        double mindiff = tolerable_diff;
        for(int ii=0, jj=1; jj < intervals.size(); ii++, jj++) {
            SpanPair interval1 = intervals.get(ii);
            SpanPair interval2 = intervals.get(jj);

            // connect only neighboring/touching intervals
            if(interval1.second != interval2.first)
                continue;

            // examine this interval-pair
            double mean1 = SCStatistics.mean(values, interval1.first, interval1.second);
            double mean2 = SCStatistics.mean(values, interval2.first, interval2.second);
            double diff = mean1 - mean2;
            if(diff < mindiff) {
                index = ii;
                mindiff = diff;
            }
        }

        // if there are suitable intervals for connection - connect them
        if( (index != -1) && (mindiff < tolerable_diff) ) {
            SpanPair interval1 = intervals.get(index);
            SpanPair interval2 = intervals.get(index+1);
            System.out.println("merged simple: (" + interval1.first + "," + interval1.second + ") - (" + interval2.first + "," + interval2.second + ")");
            // enhance the first interval
            interval1.second = interval2.second;
            // remove the second interval
            intervals.remove(index+1);

            // recursive call
            mergeSimpleIntervals(values, intervals, tolerable_diff);
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Creates an array of steady-speed intervals from an input array of totes.
     * @param  totes given List of Tote objects
     * @param mintime elapsed interval time in seconds. Interval cannot be considered as steady-speed interval if the elapsed time is less than mintime (usually 300sec)
     * @return List< SpanPair > - array of steady-speed intervals extracted from totes
     */
    public static List<SpanPair> extractSteadySpeeds(List<Tote> totes, double mintime) {

        double[] times = SCStatistics.getRelativeTimes(totes);
        double[] values = SCStatistics.getSpeeds(totes);

        double range_limit = 0.75; // range cannot exceed this limit in a steady-speed interval

        // Apply max-deviation + regression check algorithm
        List<SCAlgorithms.SpanPair> speed_intervals0 = fifo_maxdev(times, values, mintime, true, SCConstants.SPEED_STEADY_RANGE, SCConstants.SPEED_STEADY_STDEV, range_limit);

        // Remove redundant intervals (peaks, holes). They all have non-homogeneous variance. The return value is considered as average variance for this dataset.
        SCStatistics.Variance var = SCStatistics.isolateNonHomogeneous(speed_intervals0, values, SCConstants.SPEED_STEADY_STDEV);

        // sieving
    //    System.out.println("\nPlease, wait. Sieve algorithm is working... \n");
        int ff = var.f;
        double mm2 = var.m2;

        mm2 = Double.max(mm2, SCConstants.SPEED_STEADY_STDEV); // prevent F-test with zero-variations (rare but possible situations)

        speed_intervals0.clear();
        sieve_maxdev(speed_intervals0, times, values,
                     0, values.length, values.length,
                     mintime,
                     true,
                     SCConstants.SPEED_STEADY_RANGE, SCConstants.SPEED_STEADY_STDEV,
                     ff, mm2,
                     range_limit);
                    
        // Adjust touching steady-course intervals (criteria: sum of squares of deviations = min)
        adjustDevTouchingIntervals(times, values, speed_intervals0, mintime, true, SCConstants.SPEED_STEADY_RANGE, SCConstants.SPEED_STEADY_STDEV);

        // Merge neighboring steady-speed intervals (those that pass statistical equal-means test)
        // (practically redundant and useless after using sieve algorithm. It can be tested but there should be no interavls for merging)
        // List<SCAlgorithms.SpanPair> speed_intervals2 = mergeDevIntervals(times, values, speed_intervals0);

        // eventually, this function can be called just to smooth the results (no statistics inside)
        //mergeSimpleIntervals(values, speed_intervals0, 1.0);

        return speed_intervals0;
    }

    //-------------------------------------------------------------------------
    /**
     * Creates an array of steady-course intervals from an input array of totes.
     * @param  totes given List of Tote objects
     * @param mintime elapsed interval time in seconds. Interval cannot be considered as steady-speed interval if the elapsed time is less than mintime (usually 300sec)
     * @return List< SpanPair > - array of steady-course intervals extracted from totes
     */
    public static List<SpanPair> extractSteadyHeadings(List<Tote> totes, double mintime) {

        double[] times = SCStatistics.getRelativeTimes(totes);
        double[] values = SCStatistics.getHeadings(totes);

        double range_limit = 20.0; // range cannot exceed this limit in a steady-speed interval

        // Apply max-deviation + regression check algorithm
        List<SCAlgorithms.SpanPair> course_intervals0 = fifo_maxdev(times, values, mintime, true, SCConstants.COURSE_STEADY_RANGE, SCConstants.COURSE_STEADY_STDEV, range_limit);

        // sieving
    //    System.out.println("\nPlease, wait. Sieve algorithm is working... \n");
        // find out referent variance for sieving
        int ff = 0;
        double mm2 = Double.MIN_VALUE;
        for(int ii=0; ii<course_intervals0.size(); ii++) {
            int index_start = course_intervals0.get(ii).first;
            int index_end = course_intervals0.get(ii).second;
            int numelements = index_end - index_start;
            double dstdev = SCStatistics.stdev(values, index_start, index_end);
            double m2 = dstdev*dstdev;
            if(m2 > mm2) {
                ff = numelements - 1;
                mm2 = m2;
            }
        }

        mm2 = Double.max(mm2, SCConstants.COURSE_STEADY_STDEV); // prevent F-test with zero-variations (rare but possible situations)

        course_intervals0.clear();
        sieve_maxdev(course_intervals0, times, values,
                     0, values.length, values.length,
                     mintime,
                     true,
                     SCConstants.COURSE_STEADY_RANGE, SCConstants.COURSE_STEADY_STDEV,
                     ff, mm2,
                     range_limit);
                    
        // Adjust touching steady-course intervals (criteria: sum of squares of deviations = min)
        adjustDevTouchingIntervals(times, values, course_intervals0, mintime, true, SCConstants.COURSE_STEADY_RANGE, SCConstants.COURSE_STEADY_STDEV);

        // Merge neighboring steady-course intervals (those that pass statistical equal-means test)
        // (practically redundant and useless after using sieve algorithm. It can be tested but there should be no interavls for merging)
        // List<SCAlgorithms.SpanPair> course_intervals1 = mergeDevIntervals(times, values, course_intervals0);

        // eventually, this function can be called just to smooth the results (no statistics inside)
        //mergeSimpleIntervals(values, course_intervals0, 5.0);

        return course_intervals0;
    }

}

