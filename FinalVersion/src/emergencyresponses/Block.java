package emergencyresponses;

import java.awt.Color;

public class Block {

	public enum Shape { free , station, hospital, emergency, ocean}
	public Shape shape;
	public Color color;
	
	public Block(Shape shape, Color color) {
		this.shape = shape;
		this.color = color;
	}

}
