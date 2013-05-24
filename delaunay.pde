ArrayList<PVector> points;
DelaunayTriangulator dt;

// canvas setup
void setup() {
	points = new ArrayList<PVector>();
	background(255);
	size(500, 500);
}

// draw a point on mouse click
// these points will be added to
// our vertex collection for triangulation
void mouseReleased() {
	PVector p = new PVector(mouseX, mouseY);
	points.add(p);
	println(p);

	ellipse(mouseX, mouseY, 5, 5);
}

// self-explanatory
// generate the triangles
void Triangulate() {
	dt = new DelaunayTriangulator();
	dt.points = points.toArray(new PVector[points.size()]);
	dt.triangles = dt.Calculate();
}

// key interactions:
// * ENTER: triangulate & take a screenshot
// * x: clear the screen and all points / vertices
void keyPressed() {
	if (keyCode == ENTER) {
		println("here");
		String name = "image_" + year() + month() + day() + "_" + minute() + second();
		save(name + "-a.jpg");

		Triangulate();
		draw();
		save(name + "-b.jpg");
	}

	if (key == 'x') {
		println("here");
		points = new ArrayList<PVector>();
		dt = null;
	}
}

// draw vertices, triangles, and circumcircles
void draw() {
	background(255);

	// draw points
	stroke(0);
	fill(0);
	for (PVector p : points) {
		color(0);
		fill(0);
		ellipse(p.x, p.y, 5, 5);
	}

	// draw triangles
	// draw edges on top of circumcircle
	if (dt != null) {
		for (Triangle t : dt.triangles) {
			t.DrawCircumcircle(this);
		}
		for (Triangle t : dt.triangles) {
			t.DrawEdges(this);
		}
	}
}
