package loadingdocks;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import loadingdocks.Block.Shape;

/**
 * Environment
 * @author Rui Henriques
 */
public class Board {

	/** The environment */

	public enum Turtle { agent, box }
	
	public static int nX = 30, nY = 30;
	private static Block[][] board;
	private static Entity[][] objects;
	private static List<Agent> robots;
	private static List<Box> boxes;



	private static List<Station> stations;
	private static List<Hospital> hospitals;
	private static List<Emergency> emergencies;
	private static Central central;

	private static Random rand = new Random();

	private static int stepCounter = 0;
	
	
	/****************************
	 ***** A: SETTING BOARD *****
	 ****************************/
	
	public static void initialize() {

		/**
		* Create stations
		* */
		stations = new ArrayList<>();
		stations.add(new Station(new Point(2, 2), Color.blue));
		stations.add(new Station(new Point(1, 3), Color.blue));
		stations.add(new Station(new Point(19, 4), Color.blue));
		stations.add(new Station(new Point(28, 16), Color.blue));

		/**
		 * Create Hospitals
		 * */
		hospitals = new ArrayList<>();
		hospitals.add(new Hospital(new Point(9, 1), Color.red));
		hospitals.add(new Hospital(new Point(20, 7), Color.red));
		hospitals.add(new Hospital(new Point(21, 9), Color.red));
		hospitals.add(new Hospital(new Point(19, 10), Color.red));

		/**
		 * Initialize Emergencies
		 * */
		emergencies = new ArrayList<>();

		/**
		 * Create Central
		 * */
		central = new Central(stations, hospitals);


		/** A: create board */
		board = new Block[nX][nY];
		for(int i=0; i<nX; i++) 
			for(int j=0; j<nY; j++) 
				board[i][j] = new Block(Shape.free, Color.lightGray);

//
//		/** B: create ramp, boxes and shelves */
//		int rampX = 4, rampY = 3;
//		Color[] colors = new Color[] {Color.red, Color.blue, Color.green, Color.yellow};
//		boxes = new ArrayList<Box>();
//		for(int i=rampX, k=0; i<2*rampX; i++) {
//			for(int j=0; j<rampY; j++) {
//				board[i][j] = new Block(Shape.ramp, Color.gray);
//				if((j==0||j==1) && (i==(rampX+1)||i==(rampX+2))) continue;
//				else boxes.add(new Box(new Point(i,j), colors[k++%4]));
//			}
//		}
//		Point[] pshelves = new Point[] {new Point(0,6), new Point(0,8), new Point(8,6), new Point(8,8)};
//		for(int k=0; k<pshelves.length; k++)
//			for(int i=0; i<2; i++)
//				board[pshelves[k].x+i][pshelves[k].y] = new Block(Shape.shelf, colors[k]);
//
//		/** C: create agents */
//		int nrobots = 3;
//		robots = new ArrayList<Agent>();
//		for(int j=0; j<nrobots; j++) robots.add(new Agent(new Point(0,j), Color.pink));
//
//		objects = new Entity[nX][nY];
//		for(Box box : boxes) objects[box.point.x][box.point.y]=box;
//		for(Agent agent : robots) objects[agent.point.x][agent.point.y]=agent;

		for (Station s : stations){
			s.setCentral(central);
			board[s.point.x][s.point.y] = new Block(Shape.station, s.color);
		}

		for (Hospital h : hospitals){
			board[h.point.x][h.point.y] = new Block(Shape.hospital, h.color);
		}



		objects = new Entity[nX][nY];
		for(Hospital hospital : hospitals) objects[hospital.point.x][hospital.point.y]=hospital;
		for(Station station : stations) objects[station.point.x][station.point.y]=station;
	}
	
	/****************************
	 ***** B: BOARD METHODS *****
	 ****************************/
	
	public static Entity getEntity(Point point) {
		return objects[point.x][point.y];
	}
	public static Block getBlock(Point point) {
		return board[point.x][point.y];
	}
	public static void updateEntityPosition(Point point, Point newpoint) {
		objects[newpoint.x][newpoint.y] = objects[point.x][point.y];
		objects[point.x][point.y] = null;
	}	
	public static void removeEntity(Point point) {
		objects[point.x][point.y] = null;
	}
	public static void insertEntity(Entity entity, Point point) {
		objects[point.x][point.y] = entity;
	}

	/***********************************
	 ***** C: ELICIT AGENT ACTIONS *****
	 ***********************************/
	
	private static RunThread runThread;
	private static GUI GUI;

	public static class RunThread extends Thread {
		
		int time;
		
		public RunThread(int time){
			this.time = time*time;
		}
		
	    public void run() {
	    	while(true){
	    		step();
				try {
					sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}
	
	public static void run(int time) {
		Board.runThread = new RunThread(time);
		Board.runThread.start();
	}

	public static void reset() {
		removeObjects();
		initialize();
		GUI.displayBoard();
		displayObjects();	
		GUI.update();
	}

	public static void step() {
		removeObjects();
		//for(Agent a : robots) a.agentDecision();
		displayObjects();
		GUI.update();
		stepCounter++;
	}

	public static void stop() {
		runThread.interrupt();
		runThread.stop();
	}

	public static void displayObjects(){
		//for(Agent agent : robots) GUI.displayObject(agent);
		//for(Box box : boxes) GUI.displayObject(box);
		if (stepCounter % 5 == 0){
			int x = rand.nextInt(nX);
			int y = rand.nextInt(nY);
			Emergency emergency = new Emergency(new Point(x, y), Color.yellow);
			emergencies.add(emergency);
			board[x][y] = new Block(Shape.Emergency, emergency.color);
			insertEntity(emergency, emergency.point);

			// central receives the emergency request and selects nearest station
			Station nearestStation = central.selectNearestStation(emergency);
			System.out.println("nearest station: (" + nearestStation.point.x + "," + nearestStation.point.y + ")");
			GUI.displayObject(emergency);
			GUI.displayBoard();
		}
	}
	
	public static void removeObjects(){
//		for(Agent agent : robots) GUI.removeObject(agent);
//		for(Box box : boxes) GUI.removeObject(box);
		for(Station s: stations) GUI.removeObject(s);
	}
	
	public static void associateGUI(GUI graphicalInterface) {
		GUI = graphicalInterface;
	}
}
