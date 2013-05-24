import processing.core.*;

// One edge of a triangle a.k.a line
class Edge {
	public PVector p1;	
	public PVector p2;	

	public boolean IsEqual(Edge other) {
		if (other == null) {
			return false;
		} else {
			return (p1 == other.p1 && p2 == other.p2) || (p2 == other.p1 && p1 == other.p2);
		}
	}

	public Edge(PVector a, PVector b) {
		p1 = a;
		p2 = b;
	}
}
