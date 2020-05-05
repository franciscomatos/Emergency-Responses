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



	private static List<Station> stations;
	private static List<Hospital> hospitals;
	private static List<Ambulance> ambulances;

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
		stations.add(new Station(new Point(2, 2), Color.blue, 2, 2 ,2));
//		stations.add(new Station(new Point(1, 3), Color.blue, 2, 2 ,2));
//		stations.add(new Station(new Point(19, 4), Color.blue, 2, 2 ,2));
//		stations.add(new Station(new Point(28, 16), Color.blue, 2, 2 ,2));

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

		ambulances = new ArrayList<>();
		for(Station station : stations){
			ambulances.addAll(station.getAmbulances());
		}


		/** A: create board */
		board = new Block[nX][nY];
		for(int i=0; i<nX; i++)
			for(int j=0; j<nY; j++)
				board[i][j] = new Block(Shape.free, Color.lightGray);

		for (Station s : stations){
			s.setCentral(central);
			board[s.point.x][s.point.y] = new Block(Shape.station, s.color);
		}

		for (Hospital h : hospitals){
			h.setCentral(central);
			board[h.point.x][h.point.y] = new Block(Shape.hospital, h.color);
		}

		objects = new Entity[nX][nY];
		for(Ambulance ambulance : ambulances) objects[ambulance.point.x][ambulance.point.y]=ambulance;

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
	public static void removeBlock(Point point) {
		board[point.x][point.y] = new Block(Shape.free, Color.lightGray);
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

		for (Ambulance ambulance : ambulances) {
			ambulance.decide();
		}

		displayObjects();
		GUI.update();
		stepCounter++;
	}

	public static void stop() {
		runThread.interrupt();
		runThread.stop();
	}

	public static void displayObjects(){
		for (Ambulance ambulance : ambulances) GUI.displayObject(ambulance);
		for (Emergency emergency: emergencies) GUI.displayObject(emergency);
		//for(Agent agent : robots) GUI.displayObject(agent);
		//for(Box box : boxes) GUI.displayObject(box);
		if (stepCounter % 5 == 0){
			int x = rand.nextInt(nX);
			int y = rand.nextInt(nY);
			Emergency emergency = new Emergency(new Point(x, y), Color.ORANGE);
			emergencies.add(emergency);
			if (board[x][y].shape == Shape.free){
				board[x][y] = new Block(Shape.emergency, emergency.color);
				insertEntity(emergency, emergency.point);

				// central receives the emergency request and selects nearest station
				Station nearestStation = central.selectNearestStation(emergency);
				System.out.println("nearest station: (" + nearestStation.point.x + "," + nearestStation.point.y + ")");
				GUI.displayObject(emergency);
				GUI.displayBoard();
			}
		}

	}
	
	public static void removeObjects(){
//		for(Agent agent : robots) GUI.removeObject(agent);
//		for(Box box : boxes) GUI.removeObject(box);
//		for(Station s: stations) GUI.removeObject(s);
		for(Emergency emergency: emergencies) GUI.removeObject(emergency);
		for(Ambulance ambulance: ambulances) GUI.removeObject(ambulance);
	}
	
	public static void associateGUI(GUI graphicalInterface) {
		GUI = graphicalInterface;
	}
}
