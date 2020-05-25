package emergencyresponses;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

public class Entity extends Thread {

	public Point point;
	public Color color;
	protected Random random;

	public Entity(Point point, Color color){
		this.point = point;
		this.color = color;
		this.random = new Random();
	} 
}
