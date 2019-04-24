package br.agr.terras.corelibrary.infraestructure.resources.geo;

import java.util.ArrayList;
import java.util.List;


/**
 * The 2D polygon. <br>
 * 
 * @see {@link Builder}
 * @author Roman Kushnarenko (sromku@gmail.com)
 */
public class PolygonFunctions
{
	private final BoundingBox _boundingBox;
	private final List<LineFunctions> _sides;

	private PolygonFunctions(List<LineFunctions> sides, BoundingBox boundingBox)
	{
		_sides = sides;
		_boundingBox = boundingBox;
	}

	/**
	 * Get the builder of the polygon
	 * 
	 * @return The builder
	 */
	public static Builder Builder()
	{
		return new Builder();
	}

	/**
	 * Builder of the polygon
	 * 
	 * @author Roman Kushnarenko (sromku@gmail.com)
	 */
	public static class Builder
	{
		private List<PointFunctions> _vertexes = new ArrayList<PointFunctions>();
		private List<LineFunctions> _sides = new ArrayList<LineFunctions>();
		private BoundingBox _boundingBox = null;

		private boolean _firstPoint = true;
		private boolean _isClosed = false;

		/**
		 * Add vertex points of the polygon.<br>
		 * It is very important to add the vertexes by order, like you were drawing them one by one.
		 * 
		 * @param point
		 *            The vertex point
		 * @return The builder
		 */
		public Builder addVertex(PointFunctions point)
		{
			if (_isClosed)
			{
				// each hole we start com the new array of vertex points
				_vertexes = new ArrayList<PointFunctions>();
				_isClosed = false;
			}

			updateBoundingBox(point);
			_vertexes.add(point);

			// add line (edge) to the polygon
			if (_vertexes.size() > 1)
			{
				LineFunctions Line = new LineFunctions(_vertexes.get(_vertexes.size() - 2), point);
				_sides.add(Line);
			}

			return this;
		}

		
		public Builder addVertexes(List<PointFunctions> points){
			for(PointFunctions point:points){
				if (_isClosed)
				{
					// each hole we start com the new array of vertex points
					_vertexes = new ArrayList<PointFunctions>();
					_isClosed = false;
				}
	
				updateBoundingBox(point);
				_vertexes.add(point);
	
				// add line (edge) to the polygon
				if (_vertexes.size() > 1)
				{
					LineFunctions Line = new LineFunctions(_vertexes.get(_vertexes.size() - 2), point);
					_sides.add(Line);
				}
			}
			return this;
		}
		
		
		/**
		 * Close the polygon shape. This will create a new side (edge) from the <b>last</b> vertex point to the <b>first</b> vertex point.
		 * 
		 * @return The builder
		 */
		public Builder close()
		{
			validate();

			// add last Line
			_sides.add(new LineFunctions(_vertexes.get(_vertexes.size() - 1), _vertexes.get(0)));
			_isClosed = true;

			return this;
		}

		/**
		 * Build the instance of the polygon shape.
		 * 
		 * @return The polygon
		 */
		public PolygonFunctions build()
		{
			validate();

			// in case you forgot to close
			if (!_isClosed)
			{
				// add last Line
				_sides.add(new LineFunctions(_vertexes.get(_vertexes.size() - 1), _vertexes.get(0)));
			}

			PolygonFunctions polygon = new PolygonFunctions(_sides, _boundingBox);
			return polygon;
		}

		/**
		 * Update bounding box com a new point.<br>
		 * 
		 * @param point
		 *            New point
		 */
		private void updateBoundingBox(PointFunctions point)
		{
			if (_firstPoint)
			{
				_boundingBox = new BoundingBox();
				_boundingBox.xMax = point.x;
				_boundingBox.xMin = point.x;
				_boundingBox.yMax = point.y;
				_boundingBox.yMin = point.y;

				_firstPoint = false;
			}
			else
			{
				// set bounding box
				if (point.x > _boundingBox.xMax)
				{
					_boundingBox.xMax = point.x;
				}
				else if (point.x < _boundingBox.xMin)
				{
					_boundingBox.xMin = point.x;
				}
				if (point.y > _boundingBox.yMax)
				{
					_boundingBox.yMax = point.y;
				}
				else if (point.y < _boundingBox.yMin)
				{
					_boundingBox.yMin = point.y;
				}
			}
		}

		private void validate()
		{
			if (_vertexes.size() < 3)
			{
				throw new RuntimeException("Polygon must have at least 3 points");
			}
		}
	}

	/**
	 * Check if the the given point is inside of the polygon.<br>
	 * 
	 * @param point
	 *            The point to check
	 * @return <code>True</code> if the point is inside the polygon, otherwise return <code>False</code>
	 */
	public boolean contains(PointFunctions point)
	{
		if (inBoundingBox(point))
		{
			LineFunctions ray = createRay(point);
			int intersection = 0;
			for (LineFunctions side : _sides)
			{
				if (intersect(ray, side))
				{
					// System.out.println("intersection++");
					intersection++;
				}
			}

			/*
			 * If the number of intersections is odd, then the point is inside the polygon
			 */
			if (intersection % 2 == 1)
			{
				return true;
			}
		}
		return false;
	}

	public List<LineFunctions> getSides()
	{
		return _sides;
	}

	/**
	 * By given ray and one side of the polygon, check if both lines intersect.
	 * 
	 * @param ray
	 * @param side
	 * @return <code>True</code> if both lines intersect, otherwise return <code>False</code>
	 */
	private boolean intersect(LineFunctions ray, LineFunctions side)
	{
		PointFunctions intersectPoint = null;

		// if both vectors aren't from the kind of x=1 lines then go into
		if (!ray.isVertical() && !side.isVertical())
		{
			// check if both vectors are parallel. If they are parallel then no intersection point will exist
			if (ray.getA() - side.getA() == 0)
			{
				return false;
			}

			double x = ((side.getB() - ray.getB()) / (ray.getA() - side.getA())); // x = (b2-b1)/(a1-a2)
			double y = side.getA() * x + side.getB(); // y = a2*x+b2
			intersectPoint = new PointFunctions(x, y);
		}

		else if (ray.isVertical() && !side.isVertical())
		{
			double x = ray.getStart().x;
			double y = side.getA() * x + side.getB();
			intersectPoint = new PointFunctions(x, y);
		}

		else if (!ray.isVertical() && side.isVertical())
		{
			double x = side.getStart().x;
			double y = ray.getA() * x + ray.getB();
			intersectPoint = new PointFunctions(x, y);
		}

		else
		{
			return false;
		}

		// System.out.println("Ray: " + ray.toString() + " ,Side: " + side);
		// System.out.println("Intersect point: " + intersectPoint.toString());

		if (side.isInside(intersectPoint) && ray.isInside(intersectPoint))
		{
			return true;
		}

		return false;
	}

	/**
	 * Create a ray. The ray will be created by given point and on point outside of the polygon.<br>
	 * The outside point is calculated automatically.
	 * 
	 * @param point
	 * @return
	 */
	private LineFunctions createRay(PointFunctions point)
	{
		// create outside point
		double epsilon = (_boundingBox.xMax - _boundingBox.xMin) / 100f;
		PointFunctions outsidePoint = new PointFunctions(_boundingBox.xMin - epsilon, _boundingBox.yMin);

		LineFunctions vector = new LineFunctions(outsidePoint, point);
		return vector;
	}

	/**
	 * Check if the given point is in bounding box
	 * 
	 * @param point
	 * @return <code>True</code> if the point in bounding box, otherwise return <code>False</code>
	 */
	private boolean inBoundingBox(PointFunctions point)
	{
		if (point.x < _boundingBox.xMin || point.x > _boundingBox.xMax || point.y < _boundingBox.yMin || point.y > _boundingBox.yMax)
		{
			return false;
		}
		return true;
	}
	
    public PointFunctions getCentroid(){
    	double x;
    	double y;
    	x = (_boundingBox.xMin + _boundingBox.xMax)/2;
    	y = (_boundingBox.yMin + _boundingBox.yMax)/2;
    	
    	PointFunctions pointFunctions = new PointFunctions(x, y);
    	    	
    	return pointFunctions;
    }
    
    /*public double calculateMedian(double min, double max){
    	return min + ((max - min)/2);
    }*/

	private static class BoundingBox
	{
		public double xMax = Double.NEGATIVE_INFINITY;
		public double xMin = Double.NEGATIVE_INFINITY;
		public double yMax = Double.NEGATIVE_INFINITY;
		public double yMin = Double.NEGATIVE_INFINITY;
	}
}
