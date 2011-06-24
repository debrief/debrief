/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.terrain;

// Standard imports
import java.util.Random;

import javax.vecmath.Vector3f;

import org.j3d.geom.GeometryData;
import org.j3d.geom.GeometryGenerator;
import org.j3d.geom.InvalidArraySizeException;
import org.j3d.geom.UnsupportedTypeException;

/**
 * A terrain generator using the standard "fractal" algorithm.
 * <p>
 *
 * The algorithm used is the standard "fractal" method of subdivision and
 * vertex perturbation. You can find a copy of this algorithm at
 * <a href="http://astronomy.swin.edu.au/pbourke/terrain/frachill/">
 *  http://astronomy.swin.edu.au/pbourke/terrain/frachill/</a>.
 * Roughness values are always greater than 1 to avoid problems with
 * the calculations going out of whack.
 * <p>
 *
 * Geometry is generated about the origin with to the half distance either
 * side of that along the X-Z plane. X dimension is the width and Z dimension
 * is the depth. Internally, this generator just creates the height points and
 * then uses the {@link ElevationGridGenerator} to create the geometry array
 * data.
 * <p>
 *
 * The generator may take a seed terrain to start with. A common example of
 * this is to use this generator to make a height map and then pass that
 * through again from a heightfield to image coverter to generate clouds.
 * If a seed terrain is given, then the number of coordinates along both sides
 * must conform to the iteration requirements. It is best to provide a basic
 * terrain site with either 3 or 5 points on a side although the generator will
 * accept a seed terrain, so long as it is square and contains more than 2
 * points on a side.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class FractalTerrainGenerator extends GeometryGenerator
{
    /** The default number of perturbation iterations */
    private static final int DEFAULT_ITERATIONS = 20;

    /** The default size of the terrain in X-Z directions */
    private static final float DEFAULT_SIZE = 100f;

    /** The default roughness */
    private static final float DEFAULT_ROUGHNESS = 2;

    /** The default sea level */
    private static final float DEFAULT_SEALEVEL = 0;

    /** The default initial height */
    private static final float DEFAULT_HEIGHT = 20;

    /** Flag indicating if we should use sea level or not */
    private boolean useSeaLevel;

    /** Width of the terrain to generate */
    private float terrainWidth;

    /** Depth of the terrain to generate */
    private float terrainDepth;

    /** height purtubance when generating */
    private float terrainHeight;

    /** Height of sea level, if used */
    private float seaLevelHeight;

    /** Roughness during calculation */
    private float roughness;

    /** The number of iterations to generate */
    private int iterations;

    /** Flag indicating terrain values have changed */
    private boolean terrainChanged;

    /** Random number generator for the heights */
    private Random randomiser;

    /** Generator for creating the actual grid points */
    private ElevationGridGenerator gridGenerator;

    /** The seed terrain, if provided */
    private float[][] seedTerrain;

    /** The last generated terrain heights */
    private float[][] terrainHeights;

    /**
     * Construct a default terrain with the following properties:<BR>
     * Size: 100x100
     * Height: 20
     * Sea Level: 0
     * Iterations: 20
     * Roughness: 2
     */
    public FractalTerrainGenerator()
    {
        this(DEFAULT_SIZE,
             DEFAULT_SIZE,
             DEFAULT_HEIGHT,
             true,
             DEFAULT_SEALEVEL,
             DEFAULT_ITERATIONS,
             DEFAULT_ROUGHNESS,
             0,
             null);
    }

    /**
     * Create a new fractal terrain that uses the given seed terrain
     *
     * @param terrain The spot heights to use
     * @throws IllegalArgumentException The provided matrix is not square
     */
    public FractalTerrainGenerator(float[][] terrain)
    {
        this(DEFAULT_SIZE,
             DEFAULT_SIZE,
             DEFAULT_HEIGHT,
             true,
             DEFAULT_SEALEVEL,
             DEFAULT_ITERATIONS,
             DEFAULT_ROUGHNESS,
             0,
             terrain);
    }

    /**
     * Create a new fractal terrain that can select whether the sea is in
     * use or not.
     *
     * @param useSea true if the sea level is to be used
     * @param seaLevel The height of the sea
     * @throws IllegalArgumentException seaLevel, if used is greater than the
     *    default height of the ground (20)
     */
    public FractalTerrainGenerator(boolean useSea, float seaLevel)
    {
        this(DEFAULT_SIZE,
             DEFAULT_SIZE,
             DEFAULT_HEIGHT,
             useSea,
             seaLevel,
             DEFAULT_ITERATIONS,
             DEFAULT_ROUGHNESS,
             0,
             null);
    }

    /**
     * Create a new fractal terrain that can select whether the sea is in
     * use or not and is based on the given seed terrain.
     *
     * @param useSea true if the sea level is to be used
     * @param seaLevel The height of the sea
     * @param terrain The spot heights to use
     * @throws IllegalArgumentException seaLevel, if used is greater than the
     *    default height of the ground (20) or the provided terrain matrix is
     *    not square
     */
    public FractalTerrainGenerator(float[][] terrain,
                                   boolean useSea,
                                   float seaLevel)
    {
        this(DEFAULT_SIZE,
             DEFAULT_SIZE,
             DEFAULT_HEIGHT,
             useSea,
             seaLevel,
             DEFAULT_ITERATIONS,
             DEFAULT_ROUGHNESS,
             0,
             terrain);
    }

    /**
     * Construct a new fractal terrain with a given width and depth. A sea level
     * is used and defaults to a height of zero.
     *
     * @param width The width of the terrain to generate
     * @param depth The depth of the terrain to generate
     * @throws IllegalArgumentException The width or height is non-positive
     */
    public FractalTerrainGenerator(float width, float depth)
    {
        this(width,
             depth,
             DEFAULT_HEIGHT,
             true,
             DEFAULT_SEALEVEL,
             DEFAULT_ITERATIONS,
             DEFAULT_ROUGHNESS,
             0,
             null);
    }

    /**
     * Construct a fractal terrain of the given width and depth that is based
     * on the given seed terrain. A sea level is used and defaults to a height
     * of zero.
     *
     * @param width The width of the terrain to generate
     * @param depth The depth of the terrain to generate
     * @throws IllegalArgumentException The width or height is non-positive or
     *    the terrain given is non-square.
     */
    public FractalTerrainGenerator(float[][] terrain, float width, float depth)
    {
        this(width,
             depth,
             DEFAULT_HEIGHT,
             true,
             DEFAULT_SEALEVEL,
             DEFAULT_ITERATIONS,
             DEFAULT_ROUGHNESS,
             0,
             terrain);
    }

    /**
     * Construct a new terrain that starts at the given maximum height and
     * is iterated through the given number of times. The seed is can be used
     * to control the seed value for the random number generator. A value of
     * zero says to use the default seed provided by the Java runtime.
     *
     * @param height The (approx) max height of the terrain to generate
     * @param iterations The number of subdivisions to calculate
     * @param roughness Division factor for each iteration of height
     * @param seed The seed for the random number generator (0 to be ignored)
     * @throws IllegalArgumentException Roughness < 1 or the height is lower
     *    than the default sea height (0).
     */
    public FractalTerrainGenerator(float height,
                                   int iterations,
                                   float roughness,
                                   long seed)
    {
        this(DEFAULT_SIZE,
             DEFAULT_SIZE,
             height,
             true,
             DEFAULT_SEALEVEL,
             iterations,
             roughness,
             seed,
             null);
    }

    /**
     * Construct a terrain generator with the given width and depth. Maximum
     * height can be set as well as the iterations to generate terrain.
     *
     * @param width The width of the terrain to generate
     * @param depth The depth of the terrain to generate
     * @param height The (approx) max height of the terrain to generate
     * @param iterations The number of subdivisions to calculate
     * @param roughness Division factor for each iteration of height
     * @param seed The seed for the random number generator (0 to be ignored)
     * @throws IllegalArgumentException Various reasons. See message or other
     *     constructors
     */
    public FractalTerrainGenerator(float width,
                                   float depth,
                                   float height,
                                   int iterations,
                                   float roughness,
                                   long seed)
    {
        this(width,
             depth,
             height,
             true,
             DEFAULT_SEALEVEL,
             iterations,
             roughness,
             seed,
             null);
    }

    /**
     * Construct a terrain generator with all items configurable.
     *
     * @param width The width of the terrain to generate
     * @param depth The depth of the terrain to generate
     * @param height The (approx) max height of the terrain to generate
     * @param useSea true if the sea level is to be used
     * @param seaLevel The height of the sea
     * @param iterations The number of subdivisions to calculate
     * @param roughness Division factor for each iteration of height
     * @param seed The seed for the random number generator (0 to be ignored)
     * @param terrain The spot heights to use
     * @throws IllegalArgumentException Various reasons. See message or other
     *     constructors
     */
    public FractalTerrainGenerator(float width,
                                   float depth,
                                   float height,
                                   boolean useSea,
                                   float seaLevel,
                                   int iterations,
                                   float roughness,
                                   long seed,
                                   float[][] terrain)
    {
        if((width <= 0) || (height <= 0))
            throw new IllegalArgumentException("Width or height <= 0");

        if(useSea) {
            if(seaLevel >= height)
                throw new IllegalArgumentException("Sea level >= height");

            seaLevelHeight = seaLevel;
        }

        if(roughness <= 1)
            throw new IllegalArgumentException("Roughness <= 1");

        useSeaLevel = useSea;
        terrainWidth = width;
        terrainHeight = height;
        terrainDepth = depth;
        this.iterations = iterations;
        this.roughness = roughness;

        randomiser = new Random();

        if(seed != 0)
            randomiser.setSeed(seed);

        new Vector3f();

        if(terrain == null)
            seedTerrain = new float[][] {{0, 0}, {0, 0}};
        else
        {
            // possibly should check all the items...
            if((terrain.length < 2) || (terrain.length != terrain[0].length))
                throw new IllegalArgumentException("Non-square terrain");

            seedTerrain = terrain;
        }

        int side_points = calcSidePoints(iterations);

        gridGenerator = new ElevationGridGenerator(terrainWidth,
                                                   terrainDepth,
                                                   side_points,
                                                   side_points);

        terrainChanged = true;
    }

    /**
     * Check to see that this cylinder has ends in use or not
     *
     * @return true if there is are end caps in use
     */
    public boolean hasSeaLevel()
    {
        return useSeaLevel;
    }

    /**
     * Get the dimensions of the terrain. These are returned as 2 values of
     * width and depth respectively for the array. A new array is
     * created each time so you can do what you like with it.
     *
     * @return The current size of the terrain
     */
    public float[] getDimensions()
    {
        return new float[] { terrainWidth, terrainDepth };
    }

    /**
     * Set the factors that effect the generation of the terrain - heights
     * randomness etc
     *
     * @param height The (approx) max height of the terrain to generate
     * @param iterations The number of subdivisions to calculate
     * @param roughness Division factor for each iteration of height
     * @param colorMap The colour ranges for height
     */
    public void setGenerationFactors(float height,
                                     int iterations,
                                     float roughness,
                                     long seed)
    {
        if(roughness <= 1)
            throw new IllegalArgumentException("Roughness <= 1");

        terrainHeight = height;
        this.iterations = iterations;
        this.roughness = roughness;

        terrainChanged = true;

        if(seed != 0)
            randomiser.setSeed(seed);

        int side_points = calcSidePoints(iterations);

        gridGenerator.setDimensions(terrainWidth,
                                    terrainDepth,
                                    side_points,
                                    side_points);
    }

    /**
     * Change the dimensions of the cone to be generated. Calling this will
     * make the points be re-calculated next time you ask for geometry or
     * normals.
     *
     * @param width The width of the terrain to generate
     * @param depth The depth of the terrain to generate
     */
    public void setDimensions(float width, float depth)
    {
        if((terrainWidth != width) || (terrainDepth != depth))
        {
            terrainChanged = true;
            terrainDepth = depth;
            terrainWidth = width;

            int side_points = calcSidePoints(iterations);

            gridGenerator.setDimensions(terrainWidth,
                                        terrainDepth,
                                        side_points,
                                        side_points);
        }
    }

    /**
     * Set all of the items related to the sea level information.
     *
     * @param useSea true if the sea level is to be used
     * @param seaLevel The height of the sea
     */
    public void setSeaData(boolean useSea, float seaLevel)
    {
        if(useSea) {
            if(seaLevel >= terrainHeight)
                throw new IllegalArgumentException("Sea level >= height");

            seaLevelHeight = seaLevel;
        }

        useSeaLevel = useSea;
        terrainChanged = true;
    }

    /**
     * Set the terrain that is used as a seed for the generator. This can be
     * used to set a basic shape of the terrain. Provided points must be a
     * square array. A null reference can be used to return the seed terrain
     * to the default.
     *
     * @param terrain The new seed terrain to use or null
     * @throws IllegalArgumentException The provided matrix is not square
     */
    public void setSeedTerrain(float[][] terrain)
    {
        if(terrain == null)
            seedTerrain = new float[][] {{0, 0}, {0, 0}};
        else
        {
            // possibly should check all the items...
            if((terrain.length < 2) || (terrain.length != terrain[0].length))
                throw new IllegalArgumentException("Non-square terrain");

            seedTerrain = terrain;
        }

        int side_points = calcSidePoints(iterations);

        gridGenerator = new ElevationGridGenerator(terrainWidth,
                                                   terrainDepth,
                                                   side_points,
                                                   side_points);

        terrainChanged = true;
    }

    /**
     * Force the generator to create a new set of points without having to
     * reset any other data.
     */
    public void forceRegenerate()
    {
        terrainChanged = true;
    }

    /**
     * Get the number of vertices that this generator will create for the
     * shape given in the definition.
     *
     * @param data The data to base the calculations on
     * @return The vertex count for the object
     * @throws UnsupportedTypeException The generator cannot handle the type
     *   of geometry you have requested.
     */
    public int getVertexCount(GeometryData data)
        throws UnsupportedTypeException
    {
        return gridGenerator.getVertexCount(data);
    }

    /**
     * Generate a new set of geometry items based on the passed data. If the
     * data does not contain the right minimum array lengths an exception will
     * be generated. If the array reference is null, this will create arrays
     * of the correct length and assign them to the return value.
     *
     * @param data The data to base the calculations on
     * @throws InvalidArraySizeException The array is not big enough to contain
     *   the requested geometry
     * @throws UnsupportedTypeException The generator cannot handle the type
     *   of geometry you have requested
     */
    public void generate(GeometryData data)
        throws UnsupportedTypeException, InvalidArraySizeException
    {
        regenerateTerrain();

        // set the height and size info here.
        gridGenerator.generate(data);
    }

    /**
     * Generate height values only based on the current configuration.
     *
     * @return The last generated height values
     */
    public float[][] generate()
    {
        regenerateTerrain();

        return terrainHeights;
    }

    /**
     * Regenerate the base coordinate points. These are the flat circle that
     * makes up the base of the code. The coordinates are generated based on
     * the 2 PI divided by the number of facets to generate.
     */
    private void regenerateTerrain()
    {
        if(!terrainChanged)
            return;

        terrainChanged = false;

        float[][] terrain = subdivideSurface();

        // now scour the heights and truncate the sea level if needed
        if(useSeaLevel)
        {
            for(int i = terrain.length; --i >= 0; )
            {
                for(int j = terrain.length; --j >= 0; )
                {
                    if(terrain[i][j] < seaLevelHeight)
                        terrain[i][j] = seaLevelHeight;
                }
            }

        }

        terrainHeights = terrain;

        // set the terrain into the elevation grid handler
        gridGenerator.setTerrainDetail(terrain, 0);
    }

    /**
     * Recursive method to subdivide the given surface and perturb the values
     * to create a new surface. Recursion stops when the iterator is zero.
     * The current surface consists of each box as four consecutive heights.
     * This means that height values do get doubled up for each box
     *
     * @return The height map for the final generation
     */
    private float[][] subdivideSurface()
    {
        int new_points = 0;
        int old_points = seedTerrain.length;
        int count;
        float height;
        int sign;
        int i, j, k;
        int old = 0;
        float delta = terrainHeight;

        float[][] new_surface = null;
        float[][] old_surface = seedTerrain;

        for(k = 1; k <= iterations; k++)
        {
            old = 0;
            new_points = old_points + (int)Math.pow(2, (k - 1));
            new_surface = new float[new_points][new_points];

            for(i = 0; i < new_points; i++)
            {
                count = 0;

                if((i % 2) != 1)
                {
                    // Copy the existing row and insert new points
                    for(j = 0; j < old_points - 1; j++)
                    {
                        sign = randomiser.nextBoolean() ? 1 : -1;
                        height = (old_surface[old][j] + old_surface[old][j + 1]) / 2;
                        height += delta * randomiser.nextFloat() * sign;

                        new_surface[i][count++] = old_surface[old][j];
                        new_surface[i][count++] = height;
                    }

                    new_surface[i][count++] = old_surface[old][j];
                    old++;
                }
                else
                {
                    // This is the new row being inserted halfway between the old
                    // and the new.
                    for(j = 0; j < old_points - 1; j++)
                    {
                        sign = randomiser.nextBoolean() ? 1 : -1;
                        height = (old_surface[old - 1][j] + old_surface[old][j]) / 2;
                        height += delta * randomiser.nextFloat() * sign;

                        new_surface[i][count++] = height;

                        sign = randomiser.nextBoolean() ? 1 : -1;
                        height = (old_surface[old - 1][j] + old_surface[old - 1][j + 1] +
                                  old_surface[old][j] + old_surface[old][j + 1]) / 4;
                        height += delta * randomiser.nextFloat() * sign;

                        new_surface[i][count++] = height;
                    }

                    sign = randomiser.nextBoolean() ? 1 : -1;
                    height = (old_surface[old - 1][j] + old_surface[old][j]) / 2;
                    height += delta * randomiser.nextFloat() * sign;

                    new_surface[i][count++] = height;
                }
            }

            delta /= roughness;
            old_surface = new_surface;
            old_points = new_points;
        }

        return new_surface;
    }

    /**
     * Convenience routine to calculate the number of points along a side of
     * the subdivided surface.
     *
     * @param itrs The number of iterations to perform
     * @return The number of points
     */
    private final int calcSidePoints(int itrs)
    {
        int points = seedTerrain.length;

        for(int i = 0; i < itrs; i++)
            points += (int)Math.pow(2, i);

        return points;
    }
}
