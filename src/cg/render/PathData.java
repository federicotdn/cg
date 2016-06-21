package cg.render;

public class PathData {
	public Color color;
	public double distance;
	public PathData( Color color, double distance) {
		this.color = color;
		this.distance = distance;
	}

	public PathData(Color color) {
		this(color, 1);
	}
}



