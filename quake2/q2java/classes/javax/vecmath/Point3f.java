
package javax.vecmath;

/**
 * A 3 element point that is represented by single precision floating point
 * x,y,z coordinates. 
 * 
 */
public class Point3f extends Tuple3f 
	{
	
/**
 * Constructs and initializes a Point3f to (0,0,0). 
 */
public Point3f() 
	{
	super();
	}
/**
 * Constructs and initializes a Point3f from the specified xyz
 *   coordinates. 
 * @param nx float
 * @param ny float
 * @param nz float
 */
public Point3f(float nx, float ny, float nz) 
	{
	super(nx, ny, nz);
	}
/**
 * Constructs and initializes a Point3f from the specified Point3f. 
 * @param p javax.vecmath.Point3f
 */
public Point3f (Point3f p) 
	{
	super(p);
	}
/**
 * Constructs and initializes a Point3f from the specified Tuple3f. 
 * @param t1 javax.vecmath.Tuple3f
 */
public Point3f(Tuple3f t1) 
	{
	super(t1);
	}
/**
 * Computes the distance between this point and point p. 
 * @return float
 * @param p javax.vecmath.Point3f
 */
public float distance(Point3f p) 
	{
	float dx = x - p.x;
	float dy = y - p.y;
	float dz = z - p.z;

	return (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}
/**
 * Computes the square of the distance between this point and point p. 
 * @return float
 * @param p javax.vecmath.Point3f
 */
public float distanceSquared(Point3f p) 
	{
	float dx = x - p.x;
	float dy = y - p.y;
	float dz = z - p.z;

	return (float)((dx * dx) + (dy * dy) + (dz * dz));
	}
}