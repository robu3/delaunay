import processing.core.*;
import java.util.*;

// A triangle and associated methods for their construction
class Triangle {
	PVector[] points;

	// returns the circumcenter for the specified triangle
	// the circumcenter is the intersection of two perpendicular bisectors
	// for any given triangle
	public PVector GetCircumcenter(PVector a, PVector b, PVector c) {
		// determine midpoints (average of x & y coordinates)
		PVector midAB = Midpoint(a, b);
		PVector midBC = Midpoint(b, c);

		// determine slope
		// we need the negative reciprocal of the slope to get the slope of the perpendicular bisector
		float slopeAB = -1 / Slope(a, b);
		float slopeBC = -1 / Slope(b, c);

		// y = mx + b
		// solve for b
		float bAB = midAB.y - slopeAB * midAB.x;
		float bBC = midBC.y - slopeBC * midBC.x;

		// solve for x & y
		// x = (b1 - b2) / (m2 - m1)
		float x = (bAB - bBC) / (slopeBC - slopeAB);
		PVector circumcenter = new PVector(
			x,
			(slopeAB * x) + bAB
		);

		return circumcenter;
	}

	// Returns the circumcenter of this triangle
	public PVector GetCircumcenter() {
		return GetCircumcenter(points[0], points[1], points[2]);
	}

	// Returns true if p is in the circumcircle of this triangle
	public boolean CircumcircleContains(PVector p) {
		PVector center = GetCircumcenter();
		float rad = center.dist(points[0]);
		return center.dist(p) <= rad;
	}

	// Returns the points in points contained in the circumcircle
	public ArrayList<PVector> GetContainedPoints(PVector[] points) {
		ArrayList<PVector> contained = new ArrayList<PVector>();
		for (int i = 0; i < points.length; i++) {
			if (CircumcircleContains(points[i])) {
				contained.add(points[i]);
			}
		}

		return contained;
	}

	// returns the midpoint between two points
	public PVector Midpoint(PVector a, PVector b) {
		// midpoint is the average of x & y coordinates
		return new PVector(
			(a.x + b.x) / 2,
			(a.y + b.y) / 2
		);
	}

	// returns the slope of the line between two points
	public float Slope(PVector from, PVector to) {
		return (to.y - from.y) / (to.x - from.x);
	}

	// returns true if point is in the circle located at center with the specified radius
	public boolean IsInCircle(PVector point, PVector center, float radius) {
		// could also use the pythagorean theorem for this
		return point.dist(center) < radius;
	}

	// returns true if we share a vertex with another triangle
	public boolean SharesVertex(Triangle other) {
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < other.points.length; j++) {
				if (points[i] == other.points[j]) {
					return true;
				}
			}
		}

		return false;
	}

	// draw the triangle to the specified applet window
	public void Draw(PApplet app) {
		DrawEdges(app);
		DrawCircumcircle(app);
	}

	public void DrawEdges(PApplet app) {
		int next;
		app.stroke(0, 0, 0);
		for (int i = 0; i < points.length; i++) {
			next = i == points.length - 1 ? 0 : i + 1;
			app.line(points[i].x, points[i].y, points[next].x, points[next].y);
		}
	}	

	public void DrawCircumcircle(PApplet app) {
		// draw circumcircle
		PVector center = GetCircumcenter();
		float rad = center.dist(points[0]);
		app.noFill();
		app.stroke(192, 192, 192);
		app.ellipse(center.x, center.y, rad * 2, rad * 2);
	}

	public Triangle() {
		points = new PVector[3];
	}

	public Triangle(PVector[] pts) {
		points = pts;
	}

	// constructor using vectors
	public Triangle(PVector a, PVector b, PVector c) {
		points = new PVector[3];
		points[0] = a;
		points[1] = b;
		points[2] = c;
	}
}
