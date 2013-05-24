import processing.core.*;
import java.util.*;

// Class for creating Delaunay triangles for a given set of points.
// References used: 
//		* http://wiki.processing.org/w/Triangulation (main algorithm)
//		* http://astronomy.swin.edu.au/~pbourke/modelling/triangulate/
public class DelaunayTriangulator {
	PVector[] points;
	ArrayList<Triangle> triangles;

	// sort points in clockwise order (in place)
	// insertion sort
	// NOTE: not currently in use
	private PVector[] SortClockwise(PVector[] pts, PVector center) {
		// sort in clockwise order
		// left -> right
		for (int i = 1; i < pts.length; i++) {
			PVector p = pts[i];
			int pos = i;

			while (pos > 0 && IsCcw(p, pts[pos - 1], center)) {
				// larger value shifts up
				pts[pos] = pts[pos - 1];
				// insert position moves down
				pos = pos - 1;
			}

			// correct position determined
			pts[pos] = p;
		}
		return pts;
	}

	// returns true if A is CCW in relation to B
	// reference: http://stackoverflow.com/questions/6989100/sort-points-in-clockwise-order
	// NOTE: not currently in use
	private boolean IsCcw(PVector a, PVector b, PVector center) {
		PVector diffA = PVector.sub(center, a);
		PVector diffB = PVector.sub(center, b);

		if (diffA.x >= 0 && diffB.x < 0) {
			return true;
		}
		if (diffA.x == 0 && diffB.x == 0) {
			return diffA.y > diffB.y;
		}
		
		// (0, 0, 1) is the perpendicular vector
		// it is the vector perpendicular to the xy plane
		float dot = PVector.dot(diffA.cross(diffB), new PVector(0, 0, 1));
		if (dot < 0) {
			return true;
		} else if (dot > 0) {
			return false;
		}

		// a & b are on the same line from the center point
		// use distance; further is CCW
		return diffA.mag() > diffB.mag();
	}	

	// find a triangle that contains all the points
	// this used as a starting reference for the algorithm
	public Triangle GetSuperTriangle() {
		// find min & max x and y values
		float xMin = points[0].x;
		float yMin = points[0].y;
		float xMax = xMin;
		float yMax = yMin;

		for (int i = 0; i < points.length; i++) {
			PVector p = points[i];
			if (p.x < xMin) {
				xMin = p.x;
			}
			if (p.x > xMax) {
				xMax = p.x;
			}
			if (p.y < xMin) {
				xMin = p.y;
			}
			if (p.y > xMax) {
				xMax = p.y;
			}
		}

		// build triangle that contains the min and max values
		float dx = xMax - xMin;
		float dy = yMax - yMin;
		float dMax = dx > dy ? dx : dy;
		float xMid = (xMin + xMax) / 2f;
		float yMid = (yMin + yMax) / 2f;

		Triangle superTri = new Triangle(
			new PVector(xMid - 2f * dMax, yMid - dMax),
			new PVector(xMid, yMid + 2f * dMax),
			new PVector(xMid + 2f * dMax, yMid - dMax)
		);

		return superTri;
	}

	// Calculates / creates delaunay triangles for the
	// current set of points
	public ArrayList<Triangle> Calculate()
	{
		// the buffer of current triangles
		ArrayList<Triangle> triangleBuffer = new ArrayList<Triangle>();

		// final collection of completed triangles
		ArrayList<Triangle> completed = new ArrayList<Triangle>();

		// add the super triangle
		Triangle superTriangle = GetSuperTriangle();
		triangleBuffer.add(superTriangle);

		// add each point
		PVector point;
		ArrayList<Edge> edgeBuffer = new ArrayList<Edge>();
		for (int i = 0; i < points.length; i++) {
			point = points[i];
			edgeBuffer.clear();

			// iterate over all current triangles (in reverse)
			// checking to see if the current point is included
			// in a triangles circumcircle
			for (int j = triangleBuffer.size() - 1; j >= 0; j--) {
				Triangle tri = triangleBuffer.get(j);

				PVector circumcenter = tri.GetCircumcenter();
				float rad = circumcenter.dist(tri.points[0]);

				if (circumcenter.x + rad < point.x) {
					// triangle is complete
					// TODO can we end evaluation for current point here?
					completed.add(tri);
				}

				if (circumcenter.dist(point) < rad) {
					// inside
					// add edges to buffer and remove the triangle
					edgeBuffer.add(new Edge(tri.points[0], tri.points[1]));
					edgeBuffer.add(new Edge(tri.points[1], tri.points[2]));
					edgeBuffer.add(new Edge(tri.points[2], tri.points[0]));
					triangleBuffer.remove(j);
				}
			}

			// edge buffer time
			// check for duplicate edges
			// if found, remove them
			for (int j = 0; j < edgeBuffer.size() - 1; j++) {
				Edge edgeA = edgeBuffer.get(j);
				if (edgeA != null) {
					for (int k = j + 1; k < edgeBuffer.size(); k++) {
						Edge edgeB = edgeBuffer.get(k);
						if (edgeA.IsEqual(edgeB)) {
							edgeBuffer.set(j, null);
							edgeBuffer.set(k, null);
						}
					}	
				}
			}

			// build new triangles from
			// the remaining edges
			for (int j = 0; j < edgeBuffer.size(); j++) {
				Edge edge = edgeBuffer.get(j);
				if (edge == null) {
					continue;
				}

				// make sure to order points in a clockwise fashion
				Triangle tri = new Triangle(edge.p1, edge.p2, point);
				triangleBuffer.add(tri);
			}
		}

		// remove triangles with
		// the super triangle vertices
		for (int i = triangleBuffer.size() - 1; i >= 0; i--) {
			if (triangleBuffer.get(i).SharesVertex(superTriangle)) {
				triangleBuffer.remove(i);
			}	
		}

		// set local triangles collection
		triangles = triangleBuffer;

		return triangleBuffer;
	}

	public DelaunayTriangulator() {
		triangles = new ArrayList<Triangle>();
	}
}
