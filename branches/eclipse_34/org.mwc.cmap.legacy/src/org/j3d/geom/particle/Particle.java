/*****************************************************************************
 *                        Copyright (c) 2001 Daniel Selman
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.particle;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Color4f;

import javax.media.j3d.Bounds;
import javax.media.j3d.BoundingBox;


/**
 * An abstract Particle that defines some physical properties
 * and life-cycle properties. This class is subclassed for specific
 * types of particle that have a means of representing themselves.
 * This class contains some fields commonly used to implement physics
 * based particle systems, such as force, energy, surface area as well
 * as a total age and a cyclable age.
 *
 * @author Daniel Selman
 * @version $Revision: 1.1.1.1 $
 */
public abstract class Particle
{
   // total number of iterations this particles has been in existance
   long totalAge = 0;

   // a resettable cyclable age
   int cycleAge = 0;

   // square meters
   double surfaceArea = 0.004;

   // currently unused and undefined
   double energy = 0;

   // kilograms
   double mass = 0.0000001;

   // currently unused and undefined
   double electrostaticCharge = 0;

   // newtons
   Vector3d resultantForce = new Vector3d();

   // meters per second
   Vector3d velocity = new Vector3d();

   // current position of the particle
   Point3d position = new Point3d();

   // previous position of the particle
   Point3d previousPosition = new Point3d();

   // bounding box for the particle
   private BoundingBox boundingBox = new BoundingBox();

   // color of the particle
   protected Color4f color = new Color4f();

   public Particle()
   {
   }

   public void setPosition( double x, double y, double z )
   {
       previousPosition.set( position );
       position.set( x, y, z );
   }

   public void getPosition( Tuple3d newPosition )
   {
       newPosition.set( this.position );
   }

   public void getPreviousPosition( Tuple3d position )
   {
       position.set( previousPosition );
   }

   public double getPositionX()
   {
       return position.x;
   }

   public double getPositionY()
   {
       return position.y;
   }

   public double getPositionZ()
   {
       return position.z;
   }

   public void getColor( Color4f newColor )
   {
       newColor.set( this.color );
   }

   public float getColorRed()
   {
       return color.x;
   }

   public float getColorGreen()
   {
       return color.y;
   }

   public float getColorBlue()
   {
       return color.z;
   }

   public float getColorAlpha()
   {
       return color.w;
   }

   public void setColor( float r, float g, float b, float alpha )
   {
       color.set( r, g, b, alpha );
   }

   public void setAlpha( float alpha )
   {
       color.w = alpha;
   }

   public double getWidth()
   {
       return 0.2;
   }

   public double getHeight()
   {
      return 0.2;
   }

   public double getDepth()
   {
       return 0.2;
   }

   public Bounds getBounds()
   {
       double width = getWidth();
       double heigth = getHeight();
       double depth = getDepth();

       boundingBox.setLower( position.x - width, position.y - heigth, position.z - depth );
       boundingBox.setUpper( position.x + width, position.y + heigth, position.z + depth );

       return boundingBox;
   }

   // increments both the total and cycle ages
   public void incAge()
   {
       totalAge += 1;
       cycleAge += 1;
   }

   // return the total age
   public long getTotalAge()
   {
       return totalAge;
   }

   // return the cycle age
   public int getCycleAge()
   {
       return cycleAge;
   }

   // set the cycle age
   public void setCycleAge( int cycleAge )
   {
       this.cycleAge = cycleAge;
   }
}
