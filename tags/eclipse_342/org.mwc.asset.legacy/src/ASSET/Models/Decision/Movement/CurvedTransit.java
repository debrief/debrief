package ASSET.Models.Decision.Movement;

/**
 * Created by IntelliJ IDEA.
 * User: Ian
 * Date: 28-Oct-2004
 * Time: 11:15:39
 * To change this template use File | Settings | File Templates.
 */
public class CurvedTransit
{


  /**
   * A spline factory instance.
   *
   * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
   */
  public static class SplineFactory
  {


    /**
     * Create a cubic spline based on the given control points.
     * The generated curve starts in the first control point and ends
     * in the last control point.
     *
     * @param controlPoints Control points of spline (x0,y0,z0,x1,y1,z1,...).
     * @param nParts        Number of parts to divide each leg into.
     * @return Spline (x0,y0,z0,x1,y1,z1,...).
     */
    public static double[] createCubic(double[] controlPoints, int nParts)
    {
      CubicSpline spline = new CubicSpline(controlPoints, nParts);
      return spline.generate();
    }


  }


  /**
   * A cubic spline object. Use the SplineFactory class to create
   * splines of this type.
   *
   * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
   */
  static class CubicSpline
  {

    protected double controlPoints_[];
    protected int nParts_;

    /**
     * Construct a cubic spline. Package local; Use the SplineFactory
     * to create splines of this type. The control points are used according
     * to the definition of cubic splines.
     *
     * @param controlPoints Control points of spline (x0,y0,z0,x1,y1,z1,...)
     * @param nParts        Number of parts in generated spline.
     */
    CubicSpline(double controlPoints[], int nParts)
    {
      initialize(controlPoints, nParts);
    }


    protected void initialize(double controlPoints[], int nParts)
    {
      nParts_ = nParts;

      // Endpoints are added three times to get them include in the
      // generated array
      controlPoints_ = new double[controlPoints.length + 12];
      System.arraycopy(controlPoints, 0, controlPoints_, 6,
        controlPoints.length);

      controlPoints_[0] = controlPoints_[6];
      controlPoints_[1] = controlPoints_[7];
      controlPoints_[2] = controlPoints_[8];

      controlPoints_[3] = controlPoints_[6];
      controlPoints_[4] = controlPoints_[7];
      controlPoints_[5] = controlPoints_[8];

      controlPoints_[controlPoints_.length - 3] = controlPoints_[controlPoints_.length - 9];
      controlPoints_[controlPoints_.length - 2] = controlPoints_[controlPoints_.length - 8];
      controlPoints_[controlPoints_.length - 1] = controlPoints_[controlPoints_.length - 7];

      controlPoints_[controlPoints_.length - 6] = controlPoints_[controlPoints_.length - 9];
      controlPoints_[controlPoints_.length - 5] = controlPoints_[controlPoints_.length - 8];
      controlPoints_[controlPoints_.length - 4] = controlPoints_[controlPoints_.length - 7];
    }


    /**
     * Generate this spline.
     *
     * @return Coordinates of the spline (x0,y0,z0,x1,y1,z1,...)
     */
    double[] generate()
    {
      int n = controlPoints_.length / 3;
      int length = (n - 3) * nParts_ + 1;
      double spline[] = new double[length * 3];

      p(2, 0, controlPoints_, spline, 0);

      int index = 3;
      for (int i = 2; i < n - 1; i++)
      {
        for (int j = 1; j <= nParts_; j++)
        {
          p(i, j / (double) nParts_, controlPoints_, spline, index);
          index += 3;
        }
      }

      return spline;
    }


    private void p(int i, double t, double cp[], double spline[], int index)
    {
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;

      int k = (i - 2) * 3;
      for (int j = -2; j <= 1; j++)
      {
        // TODO: Precompute blending matrix
        double b = blend(j, t);

        x += b * cp[k++];
        y += b * cp[k++];
        z += b * cp[k++];
      }

      spline[index + 0] = x;
      spline[index + 1] = y;
      spline[index + 2] = z;
    }


    protected double blend(int i, double t)
    {
      if (i == -2)
        return (((-t + 3) * t - 3) * t + 1) / 6;
      else if (i == -1)
        return (((3 * t - 6) * t) * t + 4) / 6;
      else if (i == 0)
        return (((-3 * t + 3) * t + 3) * t + 1) / 6;
      else
        return (t * t * t) / 6;
    }
  }

  /**
   * Testing the spline package.
   *
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    double[] c = new double[9];
    c[0] = 0.0;  // x0
    c[1] = 0.0;  // y0
    c[2] = 0.0;  // z0

    c[3] = 1.0;  // x1
    c[4] = 3.0;  // y1
    c[5] = 0.0;  // z1

    c[6] = 4.0;  // x2
    c[7] = 6.0;  // y2
    c[8] = 0.0;  // z2

    double[] spline2 = SplineFactory.createCubic(c, 5);

    for (int i = 0; i < spline2.length; i += 3)
      System.out.println(spline2[i] + "," + spline2[i + 1] + "," + spline2[i + 2]);
  }

}
