/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Feb 24, 2003
 * Time: 9:44:48 AM
 * To change this template use Options | File Templates.
 */
package MWC.GUI.Java3d;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import org.j3d.geom.GeometryData;
import org.j3d.geom.GeometryGenerator;
import org.j3d.geom.terrain.ElevationGridGenerator;

import MWC.GUI.ETOPO.BathyProvider;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**********************************************************************
 * class providing support utilities for creating bathy data in 3-d
 *********************************************************************/
public class WorldBathySupport
{
  /** the color to paint the land bathy
   *
   */
  private final static Color SURFACE_COLOR = new Color(0,64,0);
  /** the colour to paint the subsurface bathy
   *
   */
  private final static Color UNDERWATER_COLOR = Color.gray;

  /** get and format the bathy data
   *
   * @param depthScale the scale/stretch to apply
   * @param projection the current world/screen projection
   * @param bathy the provider for the bathy data
   * @return the scaled, formatted geometry data
   */
  public static GeometryArray calculateAndCreateGeometryData(double depthScale,
                                                             DoubleProjection projection,
                                                             BathyProvider bathy)
  {
    // get the size of the grid
    double myWid = World.OCEAN_DIAMETER / 2;

    // produce corners to represent outer corners of the screen
    Point3d p1 = new Point3d(myWid, 0, myWid);
    Point3d p2 = new Point3d(-myWid, -0, -myWid);

    WorldLocation w1 = new WorldLocation(projection.toWorld3D(p1));
    WorldLocation w2 = new WorldLocation(projection.toWorld3D(p2));
    WorldArea _myArea = new WorldArea(w1, w2);

    // calculate the width of the area in degrees (at the equator, not at this lat)
    double width = _myArea.getBottomRight().getLong() - _myArea.getTopLeft().getLong();;
    double height = _myArea.getHeight();

    // determine the bathy delta
    double sep = bathy.getGridDelta();

    // sort out how many steps in each direction
    int width_steps = (int) (width / sep);
    int height_steps = (int) (height / sep);

    // check we have enough data to create a grid
    if((width_steps < 2) || (height_steps < 2))
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("View plot in 3d", "Sorry, the data doesn't cover sufficient area to create a 3-d bathy");
      System.out.println("width_steps:" + width_steps + " height_steps:" + height_steps +
            " width:" + width + " height:" + height + " sep:" + sep);
      return null;
    }

    // so, pass through the dataset, and created the formatted geometry data
    GeometryData bathyGeometry = createBathyGeometry(width_steps, height_steps, _myArea,
                                                     sep, depthScale, bathy, projection);

    // do the other stuff
    GeometryArray geom = null;
    int format = GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.COLOR_3;
    geom = new QuadArray(bathyGeometry.vertexCount, format);
    geom.setCoordinates(0, bathyGeometry.coordinates);
    geom.setNormals(0, bathyGeometry.normals);
    geom.setColors(0, bathyGeometry.colors);

    return geom;
  }

  /** create the array of depths, the colours to use for the depths
   *
   * @param width_steps how wide is the data?
   * @param height_steps how high is the data?
   * @param myArea the area we cover
   * @param sep the separation between the depth samples
   * @param depthScale how far to stretch/shrink the data
   * @param bathy the provider for the bathy data
   * @param proj the world/screen projection we're using
   * @return the bathy data inserted into a geometry array
   */
  public static GeometryData createBathyGeometry(int width_steps, int height_steps, WorldArea myArea,
                                                  double sep, double depthScale, BathyProvider bathy,
                                                  DoubleProjection proj)
  {
    // create the array of depths from the bathy data
    float[] depths = createBathyDepthArray(width_steps, height_steps, myArea,
                                           sep, depthScale, bathy);

    // store the colors
    Color3f[] colors = new Color3f[width_steps * height_steps];

    // perform another pass to produce the colourings
    for (int thisIndex = 0; thisIndex < height_steps * width_steps; thisIndex++)
    {
      // what's this depth
      double thisDepth = depths[thisIndex];

      Color thisCol = Color.darkGray;

      // are we above water?
      if(thisDepth > 0)
      {
        thisCol = SURFACE_COLOR;
      }
      else
      {
        thisCol = UNDERWATER_COLOR;
      }
      Color3f thisC3 = new Color3f(thisCol);
      colors[thisIndex] = thisC3;
    }

    // scale the height and width to our screen coordinates
    Point3d tl = new Point3d(proj.toScreen3D(myArea.getTopLeft()));
    Point3d br = new Point3d(proj.toScreen3D(myArea.getBottomRight()));

    float w = (float) (br.x - tl.x);
    float h = (float) (br.z - tl.z);

    // create the generator
    GeometryGenerator eleGenny = new ElevationGridGenerator(w, h, width_steps, height_steps, depths, 0);

    org.j3d.geom.terrain.ColorRampGenerator colGenny = new org.j3d.geom.terrain.ColorRampGenerator(depths, colors);

    // create the geometry to put the data into
    GeometryData data = new GeometryData();
    data.geometryType = GeometryData.QUADS;
    data.geometryComponents = GeometryData.TEXTURE_2D_DATA | GeometryData.NORMAL_DATA;

    // fill the geometry
    eleGenny.generate(data);
    colGenny.generate(data);
    return data;
  }

  /** create an array of depths
   *
   * @param width_steps how wide is the data
   * @param height_steps how high is the data
   * @param myArea the area of world the data covers
   * @param sep the separation of the datapoints
   * @param depthScale the scale at which we shrink/stretch the data
   * @param bathy the provider for the bathy data
   * @return the array of depths (in screen units)
   */
  public static float[] createBathyDepthArray(int width_steps, int height_steps,
                                               WorldArea myArea, double sep, double depthScale,
                                               BathyProvider bathy)
  {
    // produce the array to store the depths
    float[] depths = new float[width_steps * height_steps];

    // produce a working value
    WorldLocation thisLocation = new WorldLocation(0, 0, 0);

    double maxElevation = 0;
    double minElevation = 0;

    // produce the BL corner of the data
    WorldLocation BLCorner = myArea.getBottomLeft();
    WorldLocation TLCorner = myArea.getTopLeft();

    // get the array of depths
    for (int i = 0; i < height_steps; i++)
    {
      for (int j = 0; j < width_steps; j++)
      {

        thisLocation.setLat(TLCorner.getLat() - sep * i);
        thisLocation.setLong(BLCorner.getLong() + sep * j);

        int thisIndex = i * width_steps + j;

        double thisDepth = bathy.getDepthAt(thisLocation);

        maxElevation = Math.max(thisDepth, maxElevation);
        minElevation = Math.min(thisDepth, minElevation);

        // convert the depth to degrees
        double convertedDepth = thisDepth / depthScale;

        // store this depth
        depths[thisIndex] = (float) convertedDepth;

      }
    }
    return depths;
  }

  /** take the geometry, and put it in to a shape
     *
     * @param geom the geometry for the shape object
     * @return the shape
     */
    public static Shape3D createBathyLines(GeometryArray geom,
                                            PolygonAttributes polyA)
    {
      // create the shape
      Shape3D newShape = new Shape3D();
      newShape.setGeometry(geom);
      newShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

      // colour the shape
      Color3f color = new Color3f(0.1f, 1.0f, 1.0f);
      Appearance app = new Appearance();
      app.setCapability(Appearance.ALLOW_MATERIAL_READ);
      app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
      app.setColoringAttributes(new ColoringAttributes(color, ColoringAttributes.FASTEST));
      newShape.setAppearance(app);
      app.setColoringAttributes(new ColoringAttributes(color, ColoringAttributes.FASTEST));

      // the poly attributes
      app.setPolygonAttributes(polyA);

      return newShape;
    }

  /** take the geometry, and put it in to a shape
   *
   * @param geom the geometry for the shape object
   * @return the shape
   */
  public static Shape3D createBathyShape(GeometryArray geom,
                                          PolygonAttributes polyA)
  {
    // create the shape
    Shape3D newShape = new Shape3D();
    newShape.setGeometry(geom);
    newShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

    // colour the shape
    Color3f color = new Color3f(0.1f, 1.0f, 1.0f);
    Appearance app = new Appearance();
    app.setCapability(Appearance.ALLOW_MATERIAL_READ);
    app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
    Material mat = new Material();
    mat.setAmbientColor(color.x * 0.4f, color.y * 0.4f, color.z * 0.4f);
    mat.setDiffuseColor(color.x * 0.6f, color.y * 0.6f, color.z * 0.6f);
    mat.setCapability(Material.ALLOW_COMPONENT_READ);
    mat.setCapability(Material.ALLOW_COMPONENT_WRITE);
    mat.setSpecularColor(new Color3f(0.7f, 0.7f, 0.7f));
    mat.setShininess(75.0f);
    mat.setLightingEnable(true);
    app.setMaterial(mat);
    newShape.setAppearance(app);
    app.setColoringAttributes(new ColoringAttributes(color, ColoringAttributes.FASTEST));

    // the poly attributes
    app.setPolygonAttributes(polyA);

    return newShape;
  }

  /** create the ocean itself (well the shaded surface, at least)
   *
   */
  protected static Switch addOcean()
  {
    // the switch to contain the ocean
    Switch theSurface = new Switch();
    theSurface.setCapability(Switch.ALLOW_SWITCH_WRITE);
    theSurface.setWhichChild(Switch.CHILD_ALL);

    // declare the array
    QuadArray box = new QuadArray(4, QuadArray.COORDINATES | QuadArray.COLOR_3);

    // set the vertices
    box.setCoordinate(0, new Point3f(World.OCEAN_DIAMETER / 2, 0f, World.OCEAN_DIAMETER / 2));
    box.setCoordinate(1, new Point3f(World.OCEAN_DIAMETER / 2, 0f, -World.OCEAN_DIAMETER / 2));
    box.setCoordinate(2, new Point3f(-World.OCEAN_DIAMETER / 2, 0f, -World.OCEAN_DIAMETER / 2));
    box.setCoordinate(3, new Point3f(-World.OCEAN_DIAMETER / 2, 0f, World.OCEAN_DIAMETER / 2));

    // set the colorus
    Color3f newCol = new Color3f(0.1f, 0.5f, 1f);
    box.setColor(0, newCol);
    box.setColor(1, newCol);
    box.setColor(2, newCol);
    box.setColor(3, newCol);

    // create the shape to hold the flat plane
    Shape3D shp = new Shape3D();
    shp.setPickable(false);
    // make the shape transparent
    Appearance app = new Appearance();
    app.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.FASTEST, 0.8f));
    shp.setAppearance(app);
    shp.setGeometry(box);

    // add to our surface
    theSurface.addChild(shp);

    return theSurface;
  }

  static public LineArray createGrid(WorldDistance gridDelta,
                                        DoubleProjection projection,
                                        double oceanDiameter)
  {

    // find out how wide our ocean is
    double surf_wid = oceanDiameter * projection.getScaleFactor();

    // calculate how many lines will fit in this area, at this grid separation
    int NUM_LINES = (int) (surf_wid / gridDelta.getValueIn(WorldDistance.DEGS));

    // find out what the grid separation will be
    final double demi_length = oceanDiameter / 2;
    final double delta = oceanDiameter / NUM_LINES;

    // create the geometry
    LineArray line = new LineArray(4 * (NUM_LINES + 1), LineArray.COORDINATES);

    // travel across first
    int coord_counter = 0;

    // create the lines
    for (int i = 0; i <= NUM_LINES; i++)
    {
      line.setCoordinate(coord_counter++, new Point3d(-demi_length, 0d, -demi_length + i * delta));
      line.setCoordinate(coord_counter++, new Point3d(+demi_length, 0d, -demi_length + i * delta));
      line.setCoordinate(coord_counter++, new Point3d(-demi_length + i * delta, 0d, -demi_length));
      line.setCoordinate(coord_counter++, new Point3d(-demi_length + i * delta, 0d, +demi_length));
    }

    return line;
  }

}
